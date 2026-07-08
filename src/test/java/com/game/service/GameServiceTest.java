package com.game.service;

import com.game.model.GameState;
import com.game.model.GameStatus;
import com.game.model.Player;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

	// Random subclass that returns a scripted sequence of dice values (1-6) instead
	// of real randomness, so tests can force a player onto a specific square.
	private static class FixedDice extends Random {
		private final Queue<Integer> rolls;

		FixedDice(Integer... rollsInOrder) {
			this.rolls = new LinkedList<>(java.util.Arrays.asList(rollsInOrder));
		}

		@Override
		public int nextInt(int bound) {
			Integer next = rolls.poll();
			if (next == null) {
				throw new IllegalStateException("No more scripted dice rolls configured");
			}
			return next - 1; // GameService does nextInt(6) + 1
		}
	}

	@Test
	void initGame_setsUpTwoPlayersAtStartingPositions() {
		GameService service = new GameService(new FixedDice());

		GameState state = service.initGame();

		assertEquals(2, state.getPlayers().size());
		assertEquals(0, state.getPlayers().get(0).getPosition());
		assertEquals(0, state.getPlayers().get(1).getPosition());
		assertEquals(0, state.getCurrentPlayerIndex());
		assertEquals(GameStatus.PLAYING, state.getStatus());
		assertNull(state.getWinner());
	}

	@Test
	void rollDice_beforeInit_returnsNullWithoutThrowing() {
		GameService service = new GameService(new FixedDice());

		assertNull(service.rollDice());
	}

	@Test
	void rollDice_exactRollTo100_winsGame() {
		GameService service = new GameService(new FixedDice(6));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(94);

		GameState state = service.rollDice();

		assertEquals(GameStatus.FINISHED, state.getStatus());
		assertEquals("Player 1", state.getWinner());
		assertEquals(100, state.getPlayers().get(0).getPosition());
	}

	@Test
	void rollDice_overshootPast100_staysPutAndPassesTurn() {
		GameService service = new GameService(new FixedDice(5));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(97);

		GameState state = service.rollDice();

		assertEquals(97, state.getPlayers().get(0).getPosition());
		assertEquals(GameStatus.PLAYING, state.getStatus());
		assertEquals(1, state.getCurrentPlayerIndex());
	}

	@Test
	void rollDice_landingOnSnakeHead_movesToTail() {
		// snake 43 -> 17 (see GameBoard); player at 40 rolling 3 lands on 43
		GameService service = new GameService(new FixedDice(3));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(40);

		GameState state = service.rollDice();

		assertEquals(17, state.getPlayers().get(0).getPosition());
	}

	@Test
	void rollDice_landingOnLadderBottom_movesToTop() {
		// ladder 6 -> 45; player at 0 rolling 6 would grant extra turn instead,
		// so approach via 4 -> roll 2 -> lands on 6
		GameService service = new GameService(new FixedDice(2));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(4);

		GameState state = service.rollDice();

		assertEquals(45, state.getPlayers().get(0).getPosition());
	}

	@Test
	void rollDice_rollingSix_grantsExtraTurn() {
		GameService service = new GameService(new FixedDice(6));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(10);

		GameState state = service.rollDice();

		assertEquals(0, state.getCurrentPlayerIndex());
		assertEquals(16, state.getPlayers().get(0).getPosition());
	}

	@Test
	void rollDice_normalMove_switchesTurn() {
		GameService service = new GameService(new FixedDice(3));
		service.initGame();

		GameState state = service.rollDice();

		assertEquals(3, state.getPlayers().get(0).getPosition());
		assertEquals(1, state.getCurrentPlayerIndex());
	}

	@Test
	void rollDice_afterGameFinished_doesNotMovePlayers() {
		GameService service = new GameService(new FixedDice(6, 3));
		service.initGame();
		service.getGameState().getPlayers().get(0).setPosition(94);
		service.rollDice(); // wins the game

		GameState state = service.rollDice(); // should no-op

		assertEquals(GameStatus.FINISHED, state.getStatus());
		assertEquals(100, state.getPlayers().get(0).getPosition());
	}

	@Test
	void resetGame_reinitializesToStartingState() {
		GameService service = new GameService(new FixedDice(3));
		service.initGame();
		service.rollDice();

		GameState state = service.resetGame();

		assertEquals(0, state.getPlayers().get(0).getPosition());
		assertEquals(GameStatus.PLAYING, state.getStatus());
	}
}
