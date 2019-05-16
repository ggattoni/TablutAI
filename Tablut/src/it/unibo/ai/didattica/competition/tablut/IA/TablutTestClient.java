package it.unibo.ai.didattica.competition.tablut.IA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameModernTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateBrandub;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutTestClient extends TablutClient {
	private int game;
	private static final int timeOut = 10000;
	private static final int threads = 4;
	private List<State> drawConditions;

	public TablutTestClient(String player, String name, int gameChosen) throws IOException {
		super(player, name);
		game = gameChosen;
		this.drawConditions = new ArrayList<>();
	}

	public TablutTestClient(String player) throws IOException {
		this(player, "TeletubBIT", 4);
	}

	public TablutTestClient(String player, String name) throws IOException {
		this(player, name, 4);
	}

	public TablutTestClient(String player, int gameChosen) throws IOException {
		this(player, "TeletubBIT", gameChosen);
	}

	public static void main(String[] args) throws IOException {
		int gametype = 4;
		String role = "";
		String name = "TeletubBIT";
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			gametype = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			name = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutTestClient client = new TablutTestClient(role, name, gametype);
		client.run();
	}

	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		State state;

		Game rules = null;
		switch (this.game) {
		case 1:
			state = new StateTablut();
			rules = new GameTablut();
			break;
		case 2:
			state = new StateTablut();
			rules = new GameModernTablut();
			break;
		case 3:
			state = new StateBrandub();
			rules = new GameTablut();
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
			System.out.println("Ashton Tablut game");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		System.out.println("You are player " + this.getPlayer().toString() + "!");
		int turn = 0;
//		int rowLastPawnMoved = -1;
//		int colLastPawnMoved = -1;
//		boolean repeated = false;

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = this.getCurrentState();
			updateDrawConditions(state);
			System.out.println(state.toString());

			if (this.getPlayer().equals(Turn.WHITE)) {
				// è il mio turno (BIANCO)
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					turn++;
					
					// Trovo le mosse possibili
					List<Action> actions = Successors.getActions(state);
//					System.out.println("Total size: " + actions.size());
					int actionsPerThread = actions.size() / threads;
					int actionsLeft = actions.size() % threads;
					
					// Divido le azioni tra i thread
					List<List<Action>> threadActions = new ArrayList<>();
					for (int i = 0; i < threads; i++) {
						if (actionsLeft > 0) {
							threadActions.add(actions.subList(i * actionsPerThread, i * actionsPerThread + actionsPerThread + 1));
							actionsLeft--;
						} else {
							threadActions.add(actions.subList(i * actionsPerThread, i * actionsPerThread + actionsPerThread));
						}
					}
					
					int threadUsed = 0;
					for (List<Action> l : threadActions) {
//						System.out.println("Size: " + l.size());
						if (!l.isEmpty()) {
							threadUsed++;
						}
					}
					
					List<BasicAI> ai = new ArrayList<>();
					for (int i = 0; i < threadUsed; i++) {
						ai.add(new BasicAI(state, -Double.MAX_VALUE, Double.MAX_VALUE, (timeOut / 5) * 4, turn, drawConditions, threadActions.get(i)));
						ai.get(i).setName("THREAD " + i);
						ai.get(i).start();
					}
					
					try {
						Thread.sleep(timeOut);
					} catch (InterruptedException e1) {
						System.out.println("Non sono riuscito ad aspettare l'ia");
					}
					
					// Now choose best action
//					double bestValue = -Double.MAX_VALUE;
					List<Action> bestChoices = new ArrayList<>();
					for (int i = 0; i < threadUsed; i++) {
//						System.out.println("THREAD " + i);
//						System.out.println(ai.get(i).getBestAction() != null ? ai.get(i).getBestAction() : "Action: null");
//						System.out.println("Value: " + (!Double.isNaN(ai.get(i).getBestActionValue()) ? ai.get(i).getBestActionValue() : "undefined"));
						
						if (ai.get(i).hasEnded() /*&& ai.get(i).getBestActionValue() >= bestValue*/) {
							System.out.println("THREAD " + i + ": Trovata una mossa di valore " + ai.get(i).getBestActionValue());
							System.out.println(ai.get(i).getBestAction());
//							bestValue = ai.get(i).getBestActionValue();
							bestChoices.add(ai.get(i).getBestAction());
						} else if (!ai.get(i).hasEnded()) {
							i--;
						}
					}
//					Action a = bestChoices.get(0);
					
					BasicAI finalAi = new BasicAI(state, -Double.MAX_VALUE, Double.MAX_VALUE, timeOut / 5, turn, drawConditions, bestChoices);
					finalAi.setName("FINAL THREAD");
					Action a = finalAi.makeDecision(/*this.getCurrentState(),*/ /*rowLastPawnMoved, colLastPawnMoved*/);
					
//					Action a2 = ai.getBestAction();
//					double value = ai.getBestActionValue();
//					System.out.println("I found this move with value " + value);
//					System.out.println(a2);
					
					// Se ho rimosso la stessa pedina
//					repeated = rowLastPawnMoved == a.getRowFrom() && colLastPawnMoved == a.getColumnFrom();

					// Save new position last moved pawn
//					rowLastPawnMoved = a.getRowTo();
//					colLastPawnMoved = a.getColumnTo();

					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pawns.clear();
					empty.clear();

				}
				// � il turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			} else {

				// � il mio turno (NERO)
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					turn++;

					// Trovo le mosse possibili
					List<Action> actions = Successors.getActions(state);
//					System.out.println("Total size: " + actions.size());
					int actionsPerThread = actions.size() / threads;
					int actionsLeft = actions.size() % threads;
					
					// Divido le azioni tra i thread
					List<List<Action>> threadActions = new ArrayList<>();
					for (int i = 0; i < threads; i++) {
						if (actionsLeft > 0) {
							threadActions.add(actions.subList(i * actionsPerThread, i * actionsPerThread + actionsPerThread + 1));
							actionsLeft--;
						} else {
							threadActions.add(actions.subList(i * actionsPerThread, i * actionsPerThread + actionsPerThread));
						}
					}
					
					int threadUsed = 0;
					for (List<Action> l : threadActions) {
//						System.out.println("Size: " + l.size());
						if (!l.isEmpty()) {
							threadUsed++;
						}
					}
					
					List<BasicAI> ai = new ArrayList<>();
					for (int i = 0; i < threadUsed; i++) {
						ai.add(new BasicAI(state, -Double.MAX_VALUE, Double.MAX_VALUE, (timeOut / 4) * 3, turn, drawConditions, threadActions.get(i)));
						ai.get(i).setName("THREAD " + i);
						ai.get(i).start();
					}
					
					try {
						Thread.sleep(timeOut);
					} catch (InterruptedException e1) {
						System.out.println("Non sono riuscito ad aspettare l'ia");
					}
					
					// Now choose best action
//					double bestValue = -Double.MAX_VALUE;
					List<Action> bestChoices = new ArrayList<>();
					for (int i = 0; i < threadUsed; i++) {
//						System.out.println("THREAD " + i);
//						System.out.println(ai.get(i).getBestAction() != null ? ai.get(i).getBestAction() : "Action: null");
//						System.out.println("Value: " + (!Double.isNaN(ai.get(i).getBestActionValue()) ? ai.get(i).getBestActionValue() : "undefined"));
						
						if (ai.get(i).hasEnded() /*&& ai.get(i).getBestActionValue() >= bestValue*/) {
							System.out.println("THREAD " + i + ": Trovata una mossa di valore " + ai.get(i).getBestActionValue());
							System.out.println(ai.get(i).getBestAction());
//							bestValue = ai.get(i).getBestActionValue();
							bestChoices.add(ai.get(i).getBestAction());
						} else if (!ai.get(i).hasEnded()) {
							i--;
						}
					}
//					Action a = bestChoices.get(0);
					
					BasicAI finalAi = new BasicAI(state, -Double.MAX_VALUE, Double.MAX_VALUE, timeOut / 4, turn, drawConditions, bestChoices);
					finalAi.setName("FINAL THREAD");
					Action a = finalAi.makeDecision(/*this.getCurrentState(),*/ /*rowLastPawnMoved, colLastPawnMoved*/);
					
//					Action a2 = ai.getBestAction();
//					double value = ai.getBestActionValue();
//					System.out.println("I found this move with value " + value);
//					System.out.println(a2);
					
					// Se ho rimosso la stessa pedina
//					repeated = rowLastPawnMoved == a.getRowFrom() && colLastPawnMoved == a.getColumnFrom();

					// Save new position last moved pawn
//					rowLastPawnMoved = a.getRowTo();
//					colLastPawnMoved = a.getColumnTo();
					
					System.out.println("Mossa scelta: " + a.toString());
					try {
						this.write(a);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pawns.clear();
					empty.clear();

				}

				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
		}

	}

	private void updateDrawConditions(State state) {
		if (this.drawConditions.isEmpty()) {
			this.drawConditions.add(state);
		} else {
			int newPawnNum = 0;
			int oldPawnNum = 0;
			Pawn[][] newBoard = state.getBoard();
			Pawn[][] oldBoard = drawConditions.get(0).getBoard();
			for (int row = 0; row < 9; row++) {
				for (int col = 0; col < 9; col++) {
					if (!newBoard[row][col].equals(Pawn.EMPTY) && !newBoard[row][col].equals(Pawn.THRONE)) {
						newPawnNum++;
					}
					if (!oldBoard[row][col].equals(Pawn.EMPTY) && !oldBoard[row][col].equals(Pawn.THRONE)) {
						oldPawnNum++;
					}
				}
			}
			if (newPawnNum == oldPawnNum) {
				this.drawConditions.add(state);
			} else {
				this.drawConditions.clear();
				this.drawConditions.add(state);
			}
		}

	}

}
