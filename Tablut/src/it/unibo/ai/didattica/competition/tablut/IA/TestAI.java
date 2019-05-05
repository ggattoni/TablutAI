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
public class TestAI {

	public final static String METRICS_NODES_EXPANDED = "nodesExpanded";
	public final static String METRICS_MAX_DEPTH = "maxDepth";

	protected State game;
	protected Turn max;
	private int rigaLastMove = 0;
	private int colonnaLastMove = 0;
	protected int blackCount;
	protected int whiteCount;
	protected int turn;
	protected double utilMax;
	protected double utilMin;
	protected int currDepthLimit;
	private boolean heuristicEvaluationUsed; // indicates that non-terminal
	// nodes
	// have been evaluated.
	private Timer timer;
	private boolean logEnabled;

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
	public TestAI(State game, double utilMin, double utilMax, long millis, int turn) {
		this.game = game;
		this.utilMin = utilMin;
		this.utilMax = utilMax;
		this.timer = new Timer(millis);
		this.max = game.getTurn();
		this.turn = turn;

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
	}

	public void setLogEnabled(boolean b) {
		logEnabled = b;
	}

	// public Action checkBlackOpeningMoves(Pawn [][] board, State state) {
	// try {
	// /***********CONTROMOSSA 1*************/
	// //Bianco muove da 2,4 in 2,8 => nero contromuove 0,3 -> 2,3
	// if(board[2][8].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(2,3), Turn.BLACK);
	// //Bianco muove da 2,4 in 2,0 => nero contromuove 0,5 -> 2,5
	// if(board[2][0].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,5),
	// Successors.convertToString(2,5), Turn.BLACK);
	// //Bianco muove da 4,2 in 0,2 => nero contromuove 5,0 -> 5,2
	// if(board[0][2].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(5,0),
	// Successors.convertToString(5,2), Turn.BLACK);
	// //Bianco muove da 4,2 in 8,2 => nero contromuove 3,0 -> 3,2
	// if(board[2][8].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,0),
	// Successors.convertToString(3,2), Turn.BLACK);
	// //Bianco muove da 4,6 in 0,6 => nero contromuove 5,8 -> 5,6
	// if(board[0][6].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(5,8),
	// Successors.convertToString(5,6), Turn.BLACK);
	// //Bianco muove da 4,6 in 8,6 => nero contromuove 3,8 -> 3,6
	// if(board[8][6].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,8),
	// Successors.convertToString(3,6), Turn.BLACK);
	// //Bianco muove da 6,4 in 6,8 => nero contromuove 8,3 -> 8,6
	// if(board[6][8].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,3),
	// Successors.convertToString(8,6), Turn.BLACK);
	// //Bianco muove da 6,4 in 6,0 => nero contromuove 8,5 -> 6,5
	// if(board[6][0].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,5),
	// Successors.convertToString(6,5), Turn.BLACK);
	// /************CONTROMOSSA 2*************/
	// //Bianco muove da 6,4 -> 6,7 => nero contromuove 8,3 -> 6,3
	// if(board[6][7].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,3),
	// Successors.convertToString(6,3), Turn.BLACK);
	// //Bianco muove da 6,4 -> 6,1 => nero contromuove 8,5 -> 6,5
	// if(board[6][1].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,5),
	// Successors.convertToString(6,5), Turn.BLACK);
	// //Bianco muove da 3,4 -> 3,7 => nero contromuove 0,3 -> 3,3
	// if(board[3][7].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(3,3), Turn.BLACK);
	// //Bianco muove da 3,4 -> 3,1 => nero contromuove 0,5 -> 3,5
	// if(board[3][1].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,5),
	// Successors.convertToString(3,5), Turn.BLACK);
	// //Bianco muove da 4,6 -> 7,6 => nero contromuove 3,8 -> 3,6
	// if(board[7][6].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,8),
	// Successors.convertToString(3,6), Turn.BLACK);
	// //Bianco muove da 4,6 -> 1,6 => nero contromuove 5,8 -> 5,6
	// if(board[1][6].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(5,8),
	// Successors.convertToString(5,6), Turn.BLACK);
	// //Bianco muove da 4,2 -> 1,2 => nero contromuove 3,0-> 3,2
	// if(board[1][2].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,0),
	// Successors.convertToString(3,2), Turn.BLACK);
	// //Bianco muove da 4,2 -> 7,2 => nero contromuove 3,0 -> 3,2
	// if(board[7][2].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,0),
	// Successors.convertToString(3,2), Turn.BLACK);
	// /************CONTROMOSSA 3*************/
	// //Bianco muove da 5,4 a 5,7 => nero mangia la pedina che resta ferma 5,0 in
	// 5,4
	// if(board[5][7].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(5,0),
	// Successors.convertToString(5,4), Turn.BLACK);
	// //Bianco muove da 5,4 a 5,1 => nero mangia la pedina che resta ferma 5,8 in
	// 5,4
	// if(board[5][1].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(5,8),
	// Successors.convertToString(5,4), Turn.BLACK);
	// //Bianco muove da 4,5 a 1,5 => nero mangia la pedina che resta ferma 8,5 a
	// 4,5
	// if(board[1][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,5),
	// Successors.convertToString(4,5), Turn.BLACK);
	// //Bianco muove da 4,5 a 7,5 => nero mangia la pedina che resta ferma 0,5 a
	// 4,5
	// if(board[7][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,5),
	// Successors.convertToString(4,5), Turn.BLACK);
	// //Bianco muove da 4,3 a 1,3 => nero mangia la pedina che resta ferma 8,3 a
	// 4,3
	// if(board[1][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,3),
	// Successors.convertToString(4,3), Turn.BLACK);
	// //Bianco muove da 4,3 a 7,3 => nero mangia la pedina che resta ferma 0,3 in
	// 4,3
	// if(board[7][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(4,3), Turn.BLACK);
	// //Bianco muove da 3,4 a 3,7 => nero mangia la pedina che resta ferma 3,0 in
	// 3,4
	// if(board[3][7].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,0),
	// Successors.convertToString(3,4), Turn.BLACK);
	// //Bianco muove da 3,4 a 3,1 => nero mangia la pedina che resta ferma 3,8 in
	// 3,4
	// if(board[3][1].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,8),
	// Successors.convertToString(3,4), Turn.BLACK);
	// /************CONTROMOSSA 4*************/
	// //Bianco muove da 5,4 a 5,5 => nero blocca path del re con 0,5 a 1,5
	// if(board[5][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,5),
	// Successors.convertToString(1,5), Turn.BLACK);
	// //Bianco muove da 5,4 a 5,3 => nero blocca path del re con 0,3 a 1,3
	// if(board[5][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(1,3), Turn.BLACK);
	// //Bianco muove da 4,3 a 3,3 => nero blocca path del re con 8,3 a 7,3
	// if(board[3][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,3),
	// Successors.convertToString(7,3), Turn.BLACK);
	// //Bianco muove da 4,3 a 5,3 => nero blocca path del re con 0,3 a 1,3
	// if(board[5][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(1,3), Turn.BLACK);
	// //Bianco muove da 3,4 a 3,5 => nero blocca path del re con 3,0 a 3,1
	// if(board[3][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,0),
	// Successors.convertToString(3,1), Turn.BLACK);
	// //Bianco muove da 3,4 a 3,3 => nero blocca path del re con 3,8 a 3,7
	// if(board[3][3].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(3,8),
	// Successors.convertToString(3,7), Turn.BLACK);
	// //Bianco muove da 4,5 a 5,5 => nero blocca path del re con 0,3 in 1,3
	// if(board[5][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(0,3),
	// Successors.convertToString(1,3), Turn.BLACK);
	// //Bianco muove da 4,5 a 3,5 => nero blocca path del re con 8,5 in 7,5
	// if(board[3][5].equals(Pawn.WHITE))
	// return new Action(Successors.convertToString(8,5),
	// Successors.convertToString(7,5), Turn.BLACK);
	// } catch (IOException e) {
	// System.out.println("Non riesco a creare l'azione, lunghezza from-to errata");
	// }
	// return makeStandardEvaluation(board,state); //Se non trovo niente, in base
	// allo stato del gioco decidi cosa fare (come sempre)
	// }
	//
	public Action makeStandardEvaluation(Pawn[][] board, State state) {
		metrics = new Metrics();
		StringBuffer logText = null;
		Turn player = state.getTurn();
		List<Action> results = null;
		results = Successors.getActions(state);
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
						++turn);
				if (timer.timeOutOccurred())
					break; // exit from action loop
				newResults.add(action, value);
				if (logEnabled)
					logText.append(action).append("->").append(value).append(" ");
			}
			if (logEnabled)
				System.out.println(logText);
			if (newResults.size() > 0) {
				results = newResults.actions;
				if (!timer.timeOutOccurred()) {
					if (hasSafeWinner(newResults.utilValues.get(0)))
						break; // exit from iterative deepening loop
					else if (newResults.size() > 1
							&& isSignificantlyBetter(newResults.utilValues.get(0), newResults.utilValues.get(1)))
						break; // exit from iterative deepening loop
				}
			}
		} while (!timer.timeOutOccurred() && heuristicEvaluationUsed);
		System.out.println("Depth reached = " + currDepthLimit);
		this.rigaLastMove = results.get(0).getRowTo();
		this.colonnaLastMove = results.get(0).getColumnTo();
		return results.get(0);
	}

	/**
	 * Template method controlling the search. It is based on iterative deepening
	 * and tries to make to a good decision in limited time. Credit goes to Behi
	 * Monsio who had the idea of ordering actions by utility in subsequent
	 * depth-limited search runs.
	 */

	public Action makeDecision(State state) {
		Pawn[][] board = state.getBoard();
		// Prima di creare l'albero controllo le migliori aperture del bianco e le
		// migliori contromosse del nero
		/** Apertura BIANCO **/
		if (this.turn == 1 && this.max.equals(Turn.WHITE)) {
			try {
				return new Action("e7", "h7", Turn.WHITE);
			} catch (IOException e) {
				System.out.println("Non riesco a creare l'azione, lunghezza from-to errata");
			}
		}

		// /**Contromosse NERO **/
		// if (this.turn == 1 && this.max.equals(Turn.BLACK))
		// return checkBlackOpeningMoves(board, state);

		/** EVALUATE STANDARD **/
		return makeStandardEvaluation(board, state);
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
		if (this.turn > 1 && this.turn < 5)
			return 0.4 * checkMateValue(state) + 0.2 * pawnNumsValue(state) + 0.1 * checkWhiteDiagonalMove(state)
					+ 0.05 * externMove(state) - 0.1 * canBeKilled(state) - 0.2 * samePawnMove(state)
					+ 0.3 * oneRowOneBlack(state) + 0.1 * kingNotInCitadelRow(state);
		else
			return 0.5 * checkMateValue(state) + 0.4 * pawnNumsValue(state) - 1 * samePawnMove(state)
					+ 0.05 * oneRowOneBlack(state) + 0.05 * kingNotInCitadelRow(state);
	}

	// 1)Capire quale pedina ha fatto lo spostamento
	// 2)A partire da dove mi sposterei controllo le caselle attorno alla pedina
	// (sopra, sotto, sinistra,destra)
	// 3)Verifico che :
	// Nelle caselle ci sia un avversario o un muro => In caso affermativo controllo
	// che da tale lato OPPOSTO ci sia una casella vuota
	// Se una pedina avversaria si puo' spostare in quella casella vuota d� il voto
	// negativo in quanto potrebbe mangiarmi alla mossa successiva
	private double canBeKilled(State state) {
		// TODO
		return 0;
	}

	// Se dopo aver spostato la pedina NON ho nessuno in diagonale del mio team
	// nessun bonus, in caso contrario
	// Controllo che la pedina spostata abbia in quello stato altre pedine alleate
	// in DIAGONALE (mossa vantaggiosa)
	// Piu pedine in diagonale alleate ci sono meglio �.
	private double checkWhiteDiagonalMove(State state) {
		double stateValue = 0;
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].equals(Pawn.WHITE) || board[i][j].equals(Pawn.KING)) {
					// Controllo in basso a destra
					if (isInBoard(i + 1, j + 1)) {
						if (board[i + 1][j + 1].equals(Pawn.WHITE) || board[i + 1][j + 1].equals(Pawn.KING)) {
							stateValue += 1;
						}
					}
					// Controllo in basso a sinistra
					if (isInBoard(i + 1, j - 1)) {
						if (board[i + 1][j - 1].equals(Pawn.WHITE) || board[i + 1][j - 1].equals(Pawn.KING)) {
							stateValue += 1;
						}
					}
					// Controllo in alto a destra
					if (isInBoard(i - 1, j - 1)) {
						if (board[i - 1][j - 1].equals(Pawn.WHITE) || board[i - 1][j - 1].equals(Pawn.KING)) {
							stateValue += 1;
						}
					}
					// Controllo in alto a sinistra
					if (isInBoard(i - 1, j + 1)) {
						if (board[i - 1][j + 1].equals(Pawn.WHITE) || board[i - 1][j + 1].equals(Pawn.KING)) {
							stateValue += 1;
						}
					}
				}

			}
		}
		return normalize(stateValue, 0, 4);
	}

	private double checkBlackDiagonalMove(State state) {
		double stateValue = 0;
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].equals(Pawn.BLACK)) {
					// Controllo in basso a destra
					if (isInBoard(i + 1, j + 1)) {
						if (board[i + 1][j + 1].equals(Pawn.BLACK)) {
							stateValue += 1;
						}
					}
					// Controllo in basso a sinistra
					if (isInBoard(i + 1, j - 1)) {
						if (board[i + 1][j - 1].equals(Pawn.BLACK)) {
							stateValue += 1;
						}
					}
					// Controllo in alto a destra
					if (isInBoard(i - 1, j - 1)) {
						if (board[i - 1][j - 1].equals(Pawn.BLACK)) {
							stateValue += 1;
						}
					}
					// Controllo in alto a sinistra
					if (isInBoard(i - 1, j + 1)) {
						if (board[i - 1][j + 1].equals(Pawn.BLACK)) {
							stateValue += 1;
						}
					}
				}

			}
		}
		return stateValue;
	}

	private boolean isInBoard(int i, int j) {
		return (i < 9 && i >= 0 && j < 9 && j >= 0) ? true : false;
	}

	// Bisogna cercare di muovere pedine diverse in turni consecutivi, valore
	// negativo se cerco di muovere la stessa pedina del turno prima
	private double samePawnMove(State state) {
		Pawn[][] board = state.getBoard();
		if (state.getTurn().equals(Turn.WHITE)) {
			if (board[this.rigaLastMove][this.colonnaLastMove].equals(Pawn.WHITE)) { // spero che gli indici siano
																						// giusti :)
				// Se la mia IA sceglie questa configurazione ho ancora un bianco nello stesso
				// posto, do quindi dei punti
				return 10;
			}
		} else {
			if (board[this.rigaLastMove][this.colonnaLastMove].equals(Pawn.BLACK)) { // spero che gli indici siano
																						// giusti :)
				// Se la mia IA sceglie questa configurazione ho ancora un nero nello stesso
				// posto, do quindi dei punti
				return 10;
			}
		}
		return 0; // fk ur points
	}

	// Muovere le pedine pi� esterne per i bianchi � pi� vantaggioso
	// Muovere le pedine pi� interne per i neri � pi� vantaggioso
	private double externMove(State state) {
		// if turnoDelBianco => Ritorna punti se sto muovendo pedina esterna
		// else if turnoDelNero => Ritorna punti se sto muovendo pedina interna
		// TODO
		return 0;
	}

	// Se una riga ha poche pedine nere, avere una pedina bianca che minaccia/mangia
	// una pedina nera � vantaggioso
	// 1)Controllare se una riga ha SOLO una pedina nera che pu� essere mangiata
	// 2)Controllare se posso mettere a destra/sinstra/sopra/sotto una pedina bianca
	// e ci� � vantaggioso
	// NB => Bisogna concatenare la "SE NON VENGO MANGIATO" in questa funzione per
	// evitare mosse inconsistenti
	private double oneRowOneBlack(State state) {
		// MaxPoints viene usato nei casi in cui la configurazione che fornisce 10 punti
		// sia SURCLASSATA da eventuali future
		// configurazioni migliori da 30 punti (quella dove mangi la pedina che stai
		// puntando invece di minacciarla e basta)
		// 30 punti => MANGIO
		// 10 punti => MINACCIO

		double maxPoints = 0;
		Pawn[][] board = state.getBoard();
		int blackNum = 0;
		int whereRiga = 0;
		int whereColonna = 0;
		int i = 2;
		int j = 2;
		/***** RIGA ORIZZONTALE ALTA (2) ******/
		for (j = 2; j < 7; j++) {
			if (board[i][j].equals(Pawn.BLACK)) {
				blackNum++;
				whereRiga = i;
				whereColonna = j;
			}
		}
		// Controllo che nelle 4 posizioni sopra sotto sinistra destra ci sia un bianco
		// (ci� � vantaggioso)
		// Piu punteggio per pedine su due lati opposti
		if (blackNum == 1) {
			// Controllo solo sopra o sopra-sotto [whereColonna == 4 controlla il capezzolo
			// della citadel, vale meno punti]
			if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points!
				}
				// Se sono sul capezzolo della cittadella e dall'altra parte ho una bianca, la
				// mangio ma prendo meno punti
				if ((whereColonna == 4) && board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points!
				}
				maxPoints = normalize(10, 0, 30); // Ne ho solo una, ok points not perfect but fine!
			}

			// Controllo solo sotto o sotto-sopra
			if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE) || whereColonna == 4) {
					return Double.MAX_VALUE; // Sotto ho bianco, sopra ho bianca oppure un muro, mi va bene uguale perch�
								// mangio!
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo destra o sinistra-destra
			if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo sinistra o destra-sinistra
			if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
		}
		/***** RIGA ORIZZONTALE BASSA (6) ******/
		i = 6;
		j = 2;
		for (j = 2; j < 7; j++) {
			if (board[i][j].equals(Pawn.BLACK)) {
				blackNum++;
				whereRiga = i;
				whereColonna = j;
			}
		}
		// Controllo che nelle 4 posizioni sopra sotto sinistra destra ci sia un bianco
		// (ci� � vantaggioso)
		// Piu punteggio per pedine su due lati opposti
		if (blackNum == 1) {
			// Controllo solo sopra o sopra-sotto
			if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE) || whereColonna == 4) {
					return Double.MAX_VALUE; // Top points!
				}
				maxPoints = normalize(10, 0, 30); // Ne ho solo una, ok points not perfect but fine!
			}
			// Controllo solo sotto o sotto-sopra
			if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;// Top poins! Mangio
				}
				if ((whereColonna == 4) && board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points! Mangio
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo destra o sinistra-destra
			if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo sinistra o destra-sinistra
			if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
		}

		/***** COLONNA VERTICALE SINISTRA (2) ****/
		i = 2;
		j = 2;
		for (i = 2; i < 7; i++) {
			if (board[i][j].equals(Pawn.BLACK)) {
				blackNum++;
				whereRiga = i;
				whereColonna = j;
			}
		}
		// Controllo che nelle 4 posizioni sopra sotto sinistra destra ci sia un bianco
		// (ci� � vantaggioso)
		// Piu punteggio per pedine su due lati opposti
		if (blackNum == 1) {
			// Controllo solo sopra o sopra-sotto
			if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points!
				}
				maxPoints = normalize(10, 0, 30); // Ne ho solo una, ok points not perfect but fine!
			}
			// Controllo solo sotto o sotto-sopra
			if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo destra o sinistra-destra
			if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE) || whereRiga == 4) {
					return Double.MAX_VALUE; // Top points mangio!
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo sinistra o destra-sinistra
			if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				if ((whereRiga == 4) && board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points mangio!
				}
				maxPoints = normalize(10, 0, 30);
			}
		}

		/***** COLONNA VERTICALE DESTRA (6) ******/
		i = 2;
		j = 6;
		for (i = 2; i < 7; i++) {
			if (board[i][j].equals(Pawn.BLACK)) {
				blackNum++;
				whereRiga = i;
				whereColonna = j;
			}
		}
		// Controllo che nelle 4 posizioni sopra sotto sinistra destra ci sia un bianco
		// (ci� � vantaggioso)
		// Piu punteggio per pedine su due lati opposti
		if (blackNum == 1) {
			// Controllo solo sopra o sopra-sotto
			if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points!
				}
				maxPoints = normalize(10, 0, 30); // Ne ho solo una, ok points not perfect but fine!
			}
			// Controllo solo sotto o sotto-sopra
			if (board[whereRiga + 1][whereColonna].equals(Pawn.WHITE)) {
				if (board[whereRiga - 1][whereColonna].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo destra o sinistra-destra
			if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points mangio!
				}
				if ((whereRiga == 4) && board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
					return Double.MAX_VALUE; // Top points mangio!
				}
				maxPoints = normalize(10, 0, 30);
			}
			// Controllo sinistra o destra-sinistra
			if (board[whereRiga][whereColonna - 1].equals(Pawn.WHITE)) {
				if (board[whereRiga][whereColonna + 1].equals(Pawn.WHITE) || whereRiga == 4) {
					return Double.MAX_VALUE;
				}
				maxPoints = normalize(10, 0, 30);
			}
		}

		return maxPoints;
	}

	// Poter posizionare il re su una riga che NON ha accampamenti � una mossa
	// vantaggiosa
	private double kingNotInCitadelRow(State state) {
		double maxPoint = 0;
		Pawn[][] board = state.getBoard();
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
				if (board[i][j].equals(Pawn.KING)) {
					if (i == 2 || i == 6)
						maxPoint += 20;
					if (j == 2 || j == 6)
						maxPoint += 20;
				}
			}
		}
//		return maxPoint;
		return normalize(maxPoint, 0, 40);
	}
	// Dovrebbe variare rispetto allo stato del gioco, se siamo in VANTAGGIO DI
	// PEDINE ha meno priorit� mangiare
	// Se una pedina blocca una strada di vittoria ha pi� priorit� mangiarla
	// TODO

	private double pawnNumsValue(State state) {
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

		// return (whiteNum / blackNum - this.whiteCount / this.blackCount)
		return normalize((whiteNum / blackNum - this.whiteCount / this.blackCount), -247 / 144, 65 / 9)
				* (this.max.equals(Turn.WHITE) ? this.utilMax : this.utilMin);
	}

	private double checkMateValue(State state) {
		int freeEscapes = checkMate(state);
		double result;
		if (freeEscapes >= 2) {
			result = this.utilMax;
		} else if (freeEscapes == 1) {
			result = this.utilMax / 2;
		} else {
			result = (this.utilMax + this.utilMin) / 2;
		}
		return result * (this.max.equals(Turn.WHITE) ? this.utilMax : this.utilMin);
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
		 * accampamenti, quindi non è possibile avere due vie di fuga libere
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
		int whiteNum = 0;
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

		return -whiteNum / this.whiteCount + blackNum / this.blackCount + checkBlackDiagonalMove(state)
				+ externMove(state) - canBeKilled(state) - samePawnMove(state) + coverRow(state) + canIEatTheKing(state)
				+ canIBlockTheKing(state);
	}

	// Se una riga NON contiene pedine nere e ne ho una che ne contiene PIU' di una
	// che si puo' spostare in quella riga, la sposto
	// Attribuire un valore diverso in base al numero di righe coperte => Se tu hai
	// 11 righe coperte do +11.000 se ho 10 righe do + 10.000 per esempio
	private double coverRow(State state) {
		// TODO
		return 0;
	}

	// Controllo se posso mangiare il re, mossa piu' prioritaria di tutte per il
	// nero
//	private double canIEatTheKing(State state) {
//		// TODO
//		return 0;
//	}

	// Se il re ha una possibilit� di vittoria, posiziono una pedina nera per
	// bloccarlo
	// 1)Se il re pu� raggiungere una via di uscita dallo stato in cui si trova
	// 2)Controllo tutte le pedine e verifico se ne ho una che si pu� posizionare
	// lungo la via d'uscita e lo faccio
	// 3)Per fare tale mossa, dare priorit� alle pedine che spostandosi vanno pi�
	// vicine al re senza essere mangiate

	// Si controlla che si sia SEMPRE una nera o UN MURO nelle 4 direzioni del re
	private double canIBlockTheKing(State state) {
		int maxPoints = 0;
		Pawn[][] board = state.getBoard();
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
				if (board[i][j].equals(Pawn.KING)) {
					// Controllo le 4 direzioni del re

					// SINISTRA (NB j COLONNA dove � il RE)
					for (int k = 0; k < j; k++) {
						if (board[i][k].equals(Pawn.BLACK) || Successors.isWall(i, k)) {
							maxPoints += 25;
						}
					}
					// DESTRA (NB j COLONNA dove � il RE)
					for (int k = j + 1; k < 9; k++) {
						if (board[i][k].equals(Pawn.BLACK) || Successors.isWall(i, k)) {
							maxPoints += 25;
						}
					}
					// SOPRA (NB i RIGA dove � il RE)
					for (int k = 0; k < i; k++) {
						if (board[k][j].equals(Pawn.BLACK) || Successors.isWall(k, j)) {
							maxPoints += 25;
						}
					}
					// SOTTO (NB i RIGA dove � il RE)
					for (int k = i + 1; k < 9; k++) {
						if (board[k][j].equals(Pawn.BLACK) || Successors.isWall(k, j)) {
							maxPoints += 25;
						}
					}
				}
			}
		}
		return maxPoints;
	}

	/**
	 * Primitive operation for action ordering. This implementation preserves the
	 * original order (provided by the game).
	 */
	public List<Action> orderActions(State state, List<Action> actions, Turn player, int depth) {
		return actions;
	}

	public double normalize(double x, double min, double max) {
		return (((x - min) / (max - min)) - 0.5) * 2 * Double.MAX_VALUE;
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
		TestAI ai = new TestAI(s, -Double.MAX_VALUE, Double.MAX_VALUE, 1000 * 60 * 10, 1);
		Action a = ai.makeDecision(s);
		System.out.println(a);
	}
}
