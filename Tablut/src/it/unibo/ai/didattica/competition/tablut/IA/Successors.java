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
		
//		if (turn.equalsTurn("W")) {
//			// Turno del bianco
//			return getWhiteSuccessors(state);
//		} else if (turn.equalsTurn("B")) {
//			// Turno del nero
//			 return getBlackSuccessors(state);
//		}

		return result;
	}

	private static List<State> getWhiteSuccessors(State state) {
		LinkedList<State> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].equalsPawn("W")) {
					result.addAll(getPawnSuccessors(state, i, j, board));
				}
			}
		}

		return result;
	}
	
	private static List<State> getBlackSuccessors(State state) {
		LinkedList<State> result = new LinkedList<>();
		Pawn[][] board = state.getBoard();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j].equalsPawn("B")) {
					result.addAll(getPawnSuccessors(state, i, j, board));
				}
			}
		}

		return result;
	}

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

}
