package it.unibo.ai.didattica.competition.tablut.IA;

import java.util.LinkedList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class Successors {

	private static GameAshtonTablut game = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
	private static List<String> c;

	public static List<State> getSuccessors(State state) {
		Turn turn = state.getTurn();

		LinkedList<State> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].equalsPawn(turn.toString())) {
					result.addAll(getPawnSuccessors(state, i, j, board));
				}
			}
		}

		// if (turn.equalsTurn("W")) {
		// // Turno del bianco
		// return getWhiteSuccessors(state);
		// } else if (turn.equalsTurn("B")) {
		// // Turno del nero
		// return getBlackSuccessors(state);
		// }

		return result;
	}

//	private static List<State> getWhiteSuccessors(State state) {
//		LinkedList<State> result = new LinkedList<>();
//		Pawn[][] board = state.getBoard();
//		for (int i = 0; i < board.length; i++) {
//			for (int j = 0; j < board[i].length; j++) {
//				if (board[i][j].equalsPawn("W")) {
//					result.addAll(getPawnSuccessors(state, i, j, board));
//				}
//			}
//		}
//
//		return result;
//	}
//
//	private static List<State> getBlackSuccessors(State state) {
//		LinkedList<State> result = new LinkedList<>();
//		Pawn[][] board = state.getBoard();
//		for (int i = 0; i < board.length; i++) {
//			for (int j = 0; j < board[i].length; j++) {
//				if (board[i][j].equalsPawn("B")) {
//					result.addAll(getPawnSuccessors(state, i, j, board));
//				}
//			}
//		}
//
//		return result;
//	}

	private static List<State> getPawnSuccessors(State state, int row, int col, Pawn[][] board) {
		LinkedList<State> result = new LinkedList<>();
		// Mosse a destra
		for (int j = col + 1; j < board[row].length; j++) {
			try {
				result.add(game.checkMove(state.clone(),
						new Action(convertToString(row, col), convertToString(row, j), state.getTurn())));
			} catch (Exception e) {
				break;
			}
		}

		// Mosse sotto
		for (int i = row + 1; i < board.length; i++) {
			try {
				result.add(game.checkMove(state.clone(),
						new Action(convertToString(row, col), convertToString(i, col), state.getTurn())));
			} catch (Exception e) {
				break;
			}
		}

		// Mosse a sinistra
		for (int j = col - 1; j >= 0; j--) {
			try {
				result.add(game.checkMove(state.clone(),
						new Action(convertToString(row, col), convertToString(row, j), state.getTurn())));
			} catch (Exception e) {
				break;
			}
		}

		// Mosse sopra
		for (int i = row - 1; i >= 0; i--) {
			try {
				result.add(game.checkMove(state.clone(),
						new Action(convertToString(row, col), convertToString(i, col), state.getTurn())));
			} catch (Exception e) {
				break;
			}
		}

		return result;
	}

	private static String convertToString(int row, int col) {
		return "" + (char) (col + 97) + (row + 1);
	}

	public static void main(String[] args) {
		StateTablut s = new StateTablut();
		System.out.println(getSuccessors(s).size());
	}

	// Dato uno stato restituisce le possibili azioni per quello stato
	public static List<Action> getActions(State state) {
		Turn turn = state.getTurn();
		LinkedList<Action> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].equalsPawn(turn.toString())) {
					result.addAll(getAllPossibleActions(state, i, j, board));
				} else if (board[i][j].equals(Pawn.KING) && turn.equals(Turn.WHITE)) {
					result.addAll(getAllPossibleActions(state, i, j, board));
				}
			}
		}
		return result;
	}
	
	public static List<Action> getActions(State state, Turn turn) {
		LinkedList<Action> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].equalsPawn(turn.toString())) {
					result.addAll(getAllPossibleActions(state, i, j, board));
				} else if (board[i][j].equals(Pawn.KING) && turn.equals(Turn.WHITE)) {
					result.addAll(getAllPossibleActions(state, i, j, board));
				}
			}
		}
		return result;
	}

	public static List<Action> getActionsWithout(State state, int row, int col) {
		Turn turn = state.getTurn();
		LinkedList<Action> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (row != i || col != j) {
					if (board[i][j].equalsPawn(turn.toString())) {
						result.addAll(getAllPossibleActions(state, i, j, board));
					} else if (board[i][j].equals(Pawn.KING) && turn.equals(Turn.WHITE)) {
						result.addAll(getAllPossibleActions(state, i, j, board));
					}
				}
			}
		}
		return result;

	}

	public static List<Action> getKingActions(State state, int kingRow, int kingCol) {
		return getAllPossibleActions(state.clone(), kingRow, kingCol, state.clone().getBoard());

	}

	private static List<Action> getAllPossibleActions(State state, int row, int col, Pawn[][] board) {
		List<Action> result = new LinkedList<>();
		// Mosse a destra
		for (int j = col + 1; j < board[row].length; j++) {
			try {
				Action a = new Action(convertToString(row, col), convertToString(row, j), state.getTurn());
				if (checkAction(state, a))
					result.add(a);
			} catch (Exception e) {
				break;
			}
		}

		// Mosse sotto
		for (int i = row + 1; i < board.length; i++) {
			try {
				Action a = new Action(convertToString(row, col), convertToString(i, col), state.getTurn());
				if (checkAction(state, a))
					result.add(a);
			} catch (Exception e) {
				break;
			}
		}

		// Mosse a sinistra
		for (int j = col - 1; j >= 0; j--) {
			try {
				Action a = new Action(convertToString(row, col), convertToString(row, j), state.getTurn());
				if (checkAction(state, a))
					result.add(a);
			} catch (Exception e) {
				break;
			}
		}

		// Mosse sopra
		for (int i = row - 1; i >= 0; i--) {
			try {
				Action a = new Action(convertToString(row, col), convertToString(i, col), state.getTurn());
				if (checkAction(state, a))
					result.add(a);
			} catch (Exception e) {
				break;
			}
		}

		return result;

	}

	private static boolean checkAction(State state, Action a) {
		List<String> citadels = getCitadels();
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > state.getBoard().length - 1 || rowFrom > state.getBoard().length - 1
				|| rowTo > state.getBoard().length - 1 || columnTo > state.getBoard().length - 1 || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			return false;
		}

		// controllo che non vada sul trono
		if (state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.THRONE.toString())) {
			return false;
		}

		// controllo la casella di arrivo
		if (!state.getPawn(rowTo, columnTo).equalsPawn(State.Pawn.EMPTY.toString())) {
			return false;
		}
		if (citadels.contains(state.getBox(rowTo, columnTo)) && !citadels.contains(state.getBox(rowFrom, columnFrom))) {
			return false;
		}
		if (citadels.contains(state.getBox(rowTo, columnTo)) && citadels.contains(state.getBox(rowFrom, columnFrom))) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					return false;
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					return false;
				}
			}

		}

		// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			return false;
		}

		// controllo se sto muovendo una pedina giusta
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !state.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				return false;
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!state.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				return false;
			}
		}

		// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			return false;
		}

		// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (citadels.contains(state.getBox(rowFrom, i))
							&& !citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						return false;
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!state.getPawn(rowFrom, i).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(rowFrom, i).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (citadels.contains(state.getBox(rowFrom, i))
							&& !citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						return false;
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (citadels.contains(state.getBox(i, columnFrom))
							&& !citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						return false;
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!state.getPawn(i, columnFrom).equalsPawn(State.Pawn.EMPTY.toString())) {
						if (state.getPawn(i, columnFrom).equalsPawn(State.Pawn.THRONE.toString())) {
							return false;
						} else {
							return false;
						}
					}
					if (citadels.contains(state.getBox(i, columnFrom))
							&& !citadels.contains(state.getBox(a.getRowFrom(), a.getColumnFrom()))) {
						return false;
					}
				}
			}
		}

		// Se sono arrivato qui l'azione è consentita
		return true;
	}

	public static State movePawn(State state, Action a) {
		State newState = state.clone();
		State.Pawn pawn = newState.getPawn(a.getRowFrom(), a.getColumnFrom());
		State.Pawn[][] newBoard = newState.getBoard();

		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
		} else {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		newState.setBoard(newBoard);
		// cambio il turno
		if (newState.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			newState.setTurn(State.Turn.BLACK);
		} else {
			newState.setTurn(State.Turn.WHITE);
		}

		// a questo punto controllo lo stato per eventuali catture
		if (newState.getTurn().equalsTurn("W")) {
			newState = checkCaptureBlack(newState, a);
		} else if (newState.getTurn().equalsTurn("B")) {
			newState = checkCaptureWhite(newState, a);
		}

		return newState;
	}

	private static State checkCaptureWhite(State state, Action a) {
		List<String> citadels = getCitadels();

		// controllo se mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))
								&& !(a.getColumnTo() + 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() + 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
		}
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
								&& !(a.getColumnTo() - 2 == 8 && a.getRowTo() == 4)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 0)
								&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 8)
								&& !(a.getColumnTo() - 2 == 0 && a.getRowTo() == 4)))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		}
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() - 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() - 2 == 4)))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
		}
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
						|| (citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
								&& !(a.getColumnTo() == 8 && a.getRowTo() + 2 == 4)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 0)
								&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 8)
								&& !(a.getColumnTo() == 0 && a.getRowTo() + 2 == 4)))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
		}
		// controllo se ho vinto
		if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
				|| a.getColumnTo() == state.getBoard().length - 1) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
				state.setTurn(State.Turn.WHITEWIN);
			}
		}

		return state;
	}

	private static State checkCaptureBlackKingLeft(State state, Action a) {
		List<String> citadels = getCitadels();

		// ho il re sulla sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")) {
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")) {
				if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(3, 5).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingRight(State state, Action a) {
		List<String> citadels = getCitadels();
		// ho il re sulla destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))) {
			// re sul trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")) {
				if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")) {
				if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
				if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingDown(State state, Action a) {
		List<String> citadels = getCitadels();

		// ho il re sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")) {
			// re sul trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")) {
				if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackKingUp(State state, Action a) {
		List<String> citadels = getCitadels();

		// ho il re sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")) {
			// re sul trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// re adiacente al trono
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")) {
				if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")) {
				if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")) {
				if (state.getPawn(4, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
			// sono fuori dalle zone del trono
			if (!state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
				if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))) {
					state.setTurn(State.Turn.BLACKWIN);
				}
			}
		}
		return state;
	}

	private static State checkCaptureBlackPawnRight(State state, Action a) {
		List<String> citadels = getCitadels();

		// mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			}
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			}
			if (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			}

		}

		return state;
	}

	private static State checkCaptureBlackPawnLeft(State state, Action a) {
		List<String> citadels = getCitadels();

		// mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
						|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		}
		return state;
	}

	private static State checkCaptureBlackPawnUp(State state, Action a) {
		List<String> citadels = getCitadels();

		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
		}
		return state;
	}

	private static State checkCaptureBlackPawnDown(State state, Action a) {
		List<String> citadels = getCitadels();

		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
		}
		return state;
	}

	private static State checkCaptureBlack(State state, Action a) {

		checkCaptureBlackPawnRight(state, a);
		checkCaptureBlackPawnLeft(state, a);
		checkCaptureBlackPawnUp(state, a);
		checkCaptureBlackPawnDown(state, a);
		checkCaptureBlackKingRight(state, a);
		checkCaptureBlackKingLeft(state, a);
		checkCaptureBlackKingDown(state, a);
		checkCaptureBlackKingUp(state, a);

		return state;
	}

	private static List<String> getCitadels() {
		if (c == null) {
			c = new LinkedList<String>();
			c.add("a4");
			c.add("a5");
			c.add("a6");
			c.add("b5");
			c.add("d1");
			c.add("e1");
			c.add("f1");
			c.add("e2");
			c.add("i4");
			c.add("i5");
			c.add("i6");
			c.add("h5");
			c.add("d9");
			c.add("e9");
			c.add("f9");
			c.add("e8");
		}
		return c;
	}

}
