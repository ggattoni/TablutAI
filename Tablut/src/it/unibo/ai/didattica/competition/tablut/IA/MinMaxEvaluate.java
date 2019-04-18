package it.unibo.ai.didattica.competition.tablut.IA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class MinMaxEvaluate {

	protected State state;
	protected double utilMax;
	protected double utilMin;
	protected int currDepthLimit;
	private boolean heuristicEvaluationUsed; // indicates that non-terminal nodes have been evaluated.
	private Timer timer;
	private Turn goalState;

	public MinMaxEvaluate(State state, double utilMin, double utilMax, int time) {
		this.state = state;
		this.utilMin = utilMin;
		this.utilMax = utilMax;
		this.timer = new Timer(time);
	}

	public Action makeDecision(State state) {
		Turn player = state.getTurn();
		this.goalState = player.equalsTurn("W") ? Turn.WHITEWIN : Turn.BLACKWIN;
		List<Action> results = Successors.getActions(state);
		timer.start();
		currDepthLimit = 0;
		do {
			currDepthLimit++;
			heuristicEvaluationUsed = false;
			ActionStore<Action> newResults = new ActionStore<>();
			for (Action action : results) {
				double value = minValue(Successors.movePawn(state, action), Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY, 1);
				if (timer.timeOutOccurred())
					break; // exit from action loop
				newResults.add(action, value);
			}
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
		return results.get(0);
	}

	// returns an utility value
	public double maxValue(State state, double alpha, double beta, int depth) {
		if (state.getTurn().equals(goalState) || depth >= currDepthLimit || timer.timeOutOccurred()) {
			return eval(state);
		} else {
			double value = Double.NEGATIVE_INFINITY;
			for (Action action : Successors.getActions(state)) {
				value = Math.max(value, minValue(Successors.movePawn(state, action), alpha, beta, depth + 1));
				if (value >= beta)
					return value;
				alpha = Math.max(alpha, value);
			}
			return value;
		}
	}

	// returns an utility value
	public double minValue(State state, double alpha, double beta, int depth) {
		if (state.getTurn().equals(goalState) || depth >= currDepthLimit || timer.timeOutOccurred()) {
			return eval(state);
		} else {
			double value = Double.POSITIVE_INFINITY;
			for (Action action : Successors.getActions(state)) {
				value = Math.min(value, maxValue(Successors.movePawn(state, action), alpha, beta, depth + 1));
				if (value <= alpha)
					return value;
				beta = Math.min(beta, value);
			}
			return value;
		}
	}

	protected boolean isSignificantlyBetter(double newUtility, double utility) {
		return false;
	}

	protected boolean hasSafeWinner(double resultUtility) {
		return resultUtility <= utilMin || resultUtility >= utilMax;
	}

	private double eval(State state) {
		int whiteNum = 1;
		int blackNum = 0;
		for (Pawn[] pArray : state.getBoard()) {
			for (Pawn p : pArray) {
				if (p.equals(Pawn.WHITE)) {
					whiteNum++;
				} else if (p.equals(Pawn.BLACK)) {
					blackNum++;
				}
			}
		}
		
		return whiteNum / 9 - blackNum / 16;
	}

	private static class Timer {
		private long duration;
		private long startTime;

		Timer(int maxSeconds) {
			this.duration = 1000 * maxSeconds;
		}

		void start() {
			startTime = System.currentTimeMillis();
		}

		boolean timeOutOccurred() {
			return System.currentTimeMillis() > startTime + duration;
		}
	}

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

}
