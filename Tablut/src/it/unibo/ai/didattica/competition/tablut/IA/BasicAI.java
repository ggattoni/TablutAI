package it.unibo.ai.didattica.competition.tablut.IA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

/**
 * Implements an iterative deepening Minimax search with alpha-beta pruning and
 * action ordering. Maximal computation time is specified in seconds. The
 * algorithm is implemented as template method and can be configured and tuned
 * by subclassing.
 *
 * @param <S>
 *            Type which is used for states in the game.
 * @param <A>
 *            Type which is used for actions in the game.
 * @param <P>
 *            Type which is used for players in the game.
 * @author Ruediger Lunde
 */
public class BasicAI {

	public final static String METRICS_NODES_EXPANDED = "nodesExpanded";
	public final static String METRICS_MAX_DEPTH = "maxDepth";

	protected State game;
	protected Turn max;
	protected int blackCount;
	protected int whiteCount;
	protected int blackPawnsAroundKing;
	protected int turn;
	protected double utilMax;
	protected double utilMin;
	protected int currDepthLimit;
	private boolean heuristicEvaluationUsed; // indicates that non-terminal
	// nodes
	// have been evaluated.
	private Timer timer;
	private boolean logEnabled;

	private List<State> drawConditions;

	private Metrics metrics = new Metrics();

	/**
	 * Creates a new search object for a given game.
	 *
	 * @param game
	 *            The game.
	 * @param utilMin
	 *            Utility value of worst state for this player. Supports evaluation
	 *            of non-terminal states and early termination in situations with a
	 *            safe winner.
	 * @param utilMax
	 *            Utility value of best state for this player. Supports evaluation
	 *            of non-terminal states and early termination in situations with a
	 *            safe winner.
	 * @param time
	 *            Maximal computation time in seconds.
	 */
	public BasicAI(State game, double utilMin, double utilMax, long millis, int turn, List<State> draw) {
		this.game = game;
		this.utilMin = utilMin;
		this.utilMax = utilMax;
		this.turn = turn;
		this.timer = new Timer(millis);
		this.max = game.getTurn();
		this.drawConditions = draw;
		System.out.println("Draw conditions: " + this.drawConditions.size());

		this.whiteCount = 0;
		this.blackCount = 0;
		for (Pawn[] pArray : game.getBoard()) {
			for (Pawn p : pArray) {
				if (p.equals(Pawn.WHITE)) {
					this.whiteCount++;
				} else if (p.equals(Pawn.BLACK)) {
					this.blackCount++;
				}
			}
		}
		this.blackPawnsAroundKing = kingSurroundedInit(this.game);

	}

	public void setLogEnabled(boolean b) {
		logEnabled = b;
	}

	/**
	 * Template method controlling the search. It is based on iterative deepening
	 * and tries to make to a good decision in limited time. Credit goes to Behi
	 * Monsio who had the idea of ordering actions by utility in subsequent
	 * depth-limited search runs.
	 */

	public Action makeDecision(State state, int lastRow, int lastCol) {
		// Nel primo turno restituisci una mossa standard
		if (this.turn == 1 && this.max.equals(Turn.WHITE)) {
			try {
				return new Action("e3", "f3", Turn.WHITE);
			} catch (IOException e) {
				System.out.println("Non riesco a restituire la prima mossa, provo con la ricerca normale");
			}
		}

		metrics = new Metrics();
		StringBuffer logText = null;
		Turn player = state.getTurn();
		List<Action> results = null;
		// TODO Magari con poche pedine si può muovere anche l'ultima pedina mossa?
		if (lastRow < 0 || lastCol < 0 || state.getBoard()[lastRow][lastCol].equals(Pawn.KING)) {
			results = Successors.getActions(state);
		} else {
			results = Successors.getActionsWithout(state, lastRow, lastCol);
		}
		timer.start();
		currDepthLimit = 0;
		do {
			incrementDepthLimit();
			if (logEnabled)
				logText = new StringBuffer("depth " + currDepthLimit + ": ");
			heuristicEvaluationUsed = false;
			ActionStore<Action> newResults = new ActionStore<>();
			for (Action action : results) {
				double value = minValue(Successors.movePawn(state, action), player, -Double.MAX_VALUE, Double.MAX_VALUE,
						1);
				if (timer.timeOutOccurred()) {
					if (newResults.size() > 0)
						System.out.println("Eval: " + newResults.utilValues.get(0));
					break; // exit from action loop
				}
				newResults.add(action, value);
				if (logEnabled)
					logText.append(action).append("->").append(value).append(" ");
			}
			if (logEnabled)
				System.out.println(logText);
			if (newResults.size() > 0) {
				results = newResults.actions;
				if (!timer.timeOutOccurred()) {
					if (hasSafeWinner(newResults.utilValues.get(0))) {
						System.out.println("Eval: " + newResults.utilValues.get(0));
						break; // exit from iterative deepening loop
					} else if (newResults.size() > 1
							&& isSignificantlyBetter(newResults.utilValues.get(0), newResults.utilValues.get(1))) {
						System.out.println("Eval: " + newResults.utilValues.get(0));
						break; // exit from iterative deepening loop
					}
				}
			}
		} while (!timer.timeOutOccurred() && heuristicEvaluationUsed);
		System.out.println("Depth reached = " + currDepthLimit);
		return results.get(0);
	}

	// returns an utility value
	public double maxValue(State state, Turn player, double alpha, double beta, int depth) {
		updateMetrics(depth);
		if (gameEnded(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
			return eval(state, player);
		} else {
			double value = -Double.MAX_VALUE;
			for (Action action : orderActions(state, Successors.getActions(state), player, depth)) {
				value = Math.max(value, minValue(Successors.movePawn(state, action), //
						player, alpha, beta, depth + 1));
				if (value >= beta)
					return value;
				alpha = Math.max(alpha, value);
			}
			return value;
		}
	}

	// returns an utility value
	public double minValue(State state, Turn player, double alpha, double beta, int depth) {
		updateMetrics(depth);
		if (gameEnded(state) || depth >= currDepthLimit || timer.timeOutOccurred()) {
			return eval(state, player);
		} else {
			double value = Double.MAX_VALUE;
			for (Action action : orderActions(state, Successors.getActions(state), player, depth)) {
				value = Math.min(value, maxValue(Successors.movePawn(state, action), //
						player, alpha, beta, depth + 1));
				if (value <= alpha)
					return value;
				beta = Math.min(beta, value);
			}
			return value;
		}
	}

	private void updateMetrics(int depth) {
		metrics.incrementInt(METRICS_NODES_EXPANDED);
		metrics.set(METRICS_MAX_DEPTH, Math.max(metrics.getInt(METRICS_MAX_DEPTH), depth));
	}

	/**
	 * Returns some statistic data from the last search.
	 */
	public Metrics getMetrics() {
		return metrics;
	}

	/**
	 * Primitive operation which is called at the beginning of one depth limited
	 * search step. This implementation increments the current depth limit by one.
	 */
	protected void incrementDepthLimit() {
		currDepthLimit++;
	}

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a clear best action exists. This implementation returns
	 * always false.
	 */
	protected boolean isSignificantlyBetter(double newUtility, double utility) {
		return false;
	}

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a safe winner has been identified. This implementation
	 * returns true if the given value (for the currently preferred action result)
	 * is the highest or lowest utility value possible.
	 */
	protected boolean hasSafeWinner(double resultUtility) {
		return resultUtility <= utilMin || resultUtility >= utilMax;
	}

	private boolean gameEnded(State state) {
		return !state.getTurn().equals(Turn.BLACK) && !state.getTurn().equals(Turn.WHITE);
	}

	/**
	 * Primitive operation, which estimates the value for (not necessarily terminal)
	 * states. This implementation returns the utility value for terminal states and
	 * <code>(utilMin + utilMax) / 2</code> for non-terminal states. When
	 * overriding, first call the super implementation!
	 */
	protected double eval(State state, Turn player) {
		if (gameEnded(state)) {
			if (state.getTurn().equals(Turn.BLACKWIN)) {
				return this.max.equals(Turn.BLACK) ? utilMax : utilMin;
			} else if (state.getTurn().equals(Turn.WHITEWIN)) {
				return this.max.equals(Turn.WHITE) ? utilMax : utilMin;
			} else if (drawConditions.contains(state)) {
				// Evito il pareggio a meno che non siano passati un sacco di turni e sono il bianco
				if (turn > 35 && player.equals(Turn.WHITE)) {
					return this.max.equals(Turn.WHITE) ? (utilMin + 1) : (utilMax - 1);
				} else {
					if (player.equals(Turn.WHITE)) {
						return this.max.equals(Turn.WHITE) ? (utilMax / 2) : (utilMin / 2);
					} else if (player.equals(Turn.BLACK)) {
						return this.max.equals(Turn.BLACK) ? (utilMax / 2) : (utilMin / 2);
					} else {
						return 0.0;
					}
				}
			} else {
				return 0.0;
			}
		} else {
			heuristicEvaluationUsed = true;
			// return (utilMin + utilMax) / 2;
			if (state.getTurn().equals(Turn.BLACK)) {
				return this.max.equals(Turn.BLACK) ? evalBlack(state) : -evalBlack(state);
			} else {
				return this.max.equals(Turn.WHITE) ? evalWhite(state) : -evalWhite(state);
			}
		}
	}

	private double evalWhite(State state) {
		// int freeEscapes = checkMate(state);
		// if (freeEscapes >= 2) {
		// return this.utilMax;
		// } else if (freeEscapes == 1) {
		// return this.utilMax / 2;
		// }
		// int whiteNum = 1;
		// int blackNum = 0;
		// for (Pawn[] pArray : state.getBoard()) {
		// for (Pawn p : pArray) {
		// if (p.equals(Pawn.WHITE) || p.equals(Pawn.KING)) {
		// whiteNum++;
		// } else if (p.equals(Pawn.BLACK)) {
		// blackNum++;
		// }
		// }
		// }
		// return this.blackCount - blackNum - (this.whiteCount - whiteNum);
		// return whiteNum / this.whiteCount - blackNum / this.blackCount;

		return pawnNumsValue(state, Turn.WHITE) + 2.0 * checkMate(state);
	}

	private int pawnNumsValue(State state, Turn turn) {
		int whiteNum = 1;
		int blackNum = 0;
		for (Pawn[] pArray : state.getBoard()) {
			for (Pawn p : pArray) {
				if (p.equals(Pawn.WHITE) || p.equals(Pawn.KING)) {
					whiteNum++;
				} else if (p.equals(Pawn.BLACK)) {
					blackNum++;
				}
			}
		}
		return turn.equals(Turn.WHITE) ? this.blackCount - blackNum - (this.whiteCount - whiteNum)
				: this.whiteCount - whiteNum - (this.blackCount - blackNum);
	}

	private int checkMate(State state) {
		int kingRow = -1;
		int kingCol = -1;
		boolean found = false;
		for (int i = 0; i < state.getBoard().length && !found; i++) {
			for (int j = 0; j < state.getBoard()[i].length && !found; j++) {
				if (state.getBoard()[i][j].equals(Pawn.KING)) {
					kingRow = i;
					kingCol = j;
					found = true;
				}
			}
		}

		/**
		 * Se il re si trova nella parte centrale della board la fuga è bloccata dagli
		 * accampamenti, quindi non è possibile avere vie di fuga libere
		 */
		if (kingRow >= 3 && kingRow <= 5 && kingCol >= 3 && kingCol <= 5) {
			return 0;
		}

		int freeEscapes = 0;
		List<Action> kingActions = Successors.getKingActions(state, kingRow, kingCol);

		for (Action a : kingActions) {
			int toRow = a.getRowTo();
			int toCol = a.getColumnTo();

			if ((toRow == 0 && (toCol == 1 || toCol == 2 || toCol == 6 || toCol == 7))
					|| (toRow == 1 && (toCol == 0 || toCol == 8)) || (toRow == 2 && (toCol == 0 || toCol == 8))
					|| (toRow == 6 && (toCol == 0 || toCol == 8)) || (toRow == 7 && (toCol == 0 || toCol == 8))
					|| (toRow == 8 && (toCol == 1 || toCol == 2 || toCol == 6 || toCol == 7))) {
				freeEscapes++;
			}
		}

		return freeEscapes;
	}

	private double evalBlack(State state) {
		// int whiteNum = 0;
		// int blackNum = 0;
		// for (Pawn[] pArray : state.getBoard()) {
		// for (Pawn p : pArray) {
		// if (p.equals(Pawn.WHITE) || p.equals(Pawn.KING)) {
		// whiteNum++;
		// } else if (p.equals(Pawn.BLACK)) {
		// blackNum++;
		// }
		// }
		// }
		// return this.whiteCount - whiteNum - (this.blackCount - blackNum) +
		// kingSurrounded(state);
		// return - whiteNum / this.whiteCount + blackNum / this.blackCount;

		return pawnNumsValue(state, Turn.BLACK) + 2.0 * kingSurrounded(state);
	}

	private int kingSurrounded(State state) {
		return kingSurroundedInit(state) - this.blackPawnsAroundKing;
	}

	private int kingSurroundedInit(State state) {
		int result = 0;
		Pawn[][] board = state.getBoard();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (board[row][col].equals(Pawn.KING)) {
					// Controllo sopra
					if (row - 1 >= 0 && board[row - 1][col].equals(Pawn.BLACK))
						result++;
					// Controllo sotto
					if (row + 1 < 9 && board[row + 1][col].equals(Pawn.BLACK))
						result++;
					// Controllo a sx
					if (col - 1 >= 0 && board[row][col - 1].equals(Pawn.BLACK))
						result++;
					// Controllo a dx
					if (col + 1 < 9 && board[row][col + 1].equals(Pawn.BLACK))
						result++;
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * Primitive operation for action ordering. This implementation preserves the
	 * original order (provided by the game).
	 */
	public List<Action> orderActions(State state, List<Action> actions, Turn player, int depth) {
		return actions;
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	// nested helper classes

	private static class Timer {
		private long duration;
		private long startTime;

		Timer(long milliSeconds) {
			this.duration = milliSeconds;
		}

		void start() {
			startTime = System.currentTimeMillis();
		}

		boolean timeOutOccurred() {
			return System.currentTimeMillis() > startTime + duration;
		}
	}

	/**
	 * Orders actions by utility.
	 */
	private static class ActionStore<A> {
		private List<A> actions = new ArrayList<>();
		private List<Double> utilValues = new ArrayList<>();

		void add(A action, double utilValue) {
			int idx = 0;
			while (idx < actions.size() && utilValue <= utilValues.get(idx))
				idx++;
			actions.add(idx, action);
			utilValues.add(idx, utilValue);
		}

		int size() {
			return actions.size();
		}
	}

	/**
	 * Stores key-value pairs for efficiency analysis.
	 * 
	 * @author Ravi Mohan
	 * @author Ruediger Lunde
	 */
	public class Metrics {
		private Hashtable<String, String> hash;

		public Metrics() {
			this.hash = new Hashtable<String, String>();
		}

		public void set(String name, int i) {
			hash.put(name, Integer.toString(i));
		}

		public void set(String name, double d) {
			hash.put(name, Double.toString(d));
		}

		public void incrementInt(String name) {
			set(name, getInt(name) + 1);
		}

		public void set(String name, long l) {
			hash.put(name, Long.toString(l));
		}

		public int getInt(String name) {
			String value = hash.get(name);
			return value != null ? Integer.parseInt(value) : 0;
		}

		public double getDouble(String name) {
			String value = hash.get(name);
			return value != null ? Double.parseDouble(value) : Double.NaN;
		}

		public long getLong(String name) {
			String value = hash.get(name);
			return value != null ? Long.parseLong(value) : 0l;
		}

		public String get(String name) {
			return hash.get(name);
		}

		public Set<String> keySet() {
			return hash.keySet();
		}

		/** Sorts the key-value pairs by key names and formats them as equations. */
		public String toString() {
			TreeMap<String, String> map = new TreeMap<String, String>(hash);
			return map.toString();
		}
	}

	public static void main(String[] args) {
		State s = new StateTablut();
		BasicAI ai = new BasicAI(s, -Double.MAX_VALUE, Double.MAX_VALUE, 1000 * 60 * 10, 3, new ArrayList<>());
		Action a = ai.makeDecision(s, -1, -1);
		System.out.println(a);
	}
}
