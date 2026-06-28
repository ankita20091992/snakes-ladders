package com.game.service;
import com.game.model.GameBoard;
import com.game.model.GameState;
import com.game.model.GameStatus;
import com.game.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameService {
	private GameState gameState;
	private GameBoard gameBoard;
	private Random random;
	//GameService() ctor create dice and board
	public GameService() {
		this.random = new Random();
		this.gameBoard = new GameBoard();
	}
//initGame() creates player 1,2 set game to playing return starting state
	public GameState initGame() {
		List<Player> players = new ArrayList<>();
		players.add(new Player(1, "Player 1"));
		players.add(new Player(2, "Player 2"));
		
		gameState = new GameState();
		gameState.setPlayers(players);
		gameState.setCurrentPlayerIndex(0);
		gameState.setDiceValue(0);
		gameState.setLandingPosition(0);
		gameState.setStatus(GameStatus.PLAYING);
		gameState.setWinner(null);
		gameState.setMessage("Player 1's turn. Roll the dice!");
		
		return gameState;
	}
//full game logic roll dice,move players,check snake/ladder/wins	
	public GameState rollDice() {
		if(gameState == null || gameState.getStatus() == GameStatus.FINISHED) {
			return gameState;
		}
		
		int currentIndex = gameState.getCurrentPlayerIndex();
		Player currentPlayer = gameState.getPlayers().get(currentIndex);
		
		int diceValue = random.nextInt(6) + 1;
		gameState.setDiceValue(diceValue);
		
		int currentPosition = currentPlayer.getPosition();
		int newPosition = currentPosition + diceValue;
		
		StringBuilder message = new StringBuilder();
		message.append(currentPlayer.getName())
				.append("rolled").append(diceValue).append(".");
		
		//overshoot check
		if(newPosition > 100) {
			gameState.setLandingPosition(currentPosition);
			message.append("needs exact roll to win! Turn passes");
			int nextIndex = (currentIndex == 0) ? 1 : 0;
			gameState.setCurrentPlayerIndex(nextIndex);
			gameState.setMessage(message.toString());
			return gameState;
		}
		
		//set landing position before snake/ladder check
		gameState.setLandingPosition(newPosition);
		currentPlayer.setPosition(newPosition);
		
		//check win
		if(newPosition == 100) {
			gameState.setStatus(GameStatus.FINISHED);
			gameState.setWinner(currentPlayer.getName());
			message.append(currentPlayer.getName()).append("wins the game!");
			gameState.setMessage(message.toString());
			return gameState;
		}
		
		//check snake or ladder
		boolean hitSnake = false;
		boolean hitLadder = false;
		
		if(gameBoard.getSnakes().containsKey(newPosition)) {
			int tail = gameBoard.getSnakes().get(newPosition);
			message.append("Oops! Hit a snake at ").append(newPosition)
					.append("! Slides down to ").append(tail).append(". ");
			currentPlayer.setPosition(tail);
			hitLadder = true;
		}
		
		if(!hitSnake && !hitLadder) {
			message.append("Moved to ").append(newPosition).append(". ");
		}
		
		//extra turn on rolling 6
		if (diceValue == 6) {
			message.append(currentPlayer.getName()).append("gets an extra turn!");
			gameState.setMessage(message.toString());
			return gameState;
		}
		//Switch turn
		int nextIndex = (currentIndex == 0) ? 1 : 0;
		gameState.setCurrentPlayerIndex(nextIndex);
		message.append(gameState.getPlayers().get(nextIndex).getName()).append(" 's turn!");
		gameState.setMessage(message.toString());
		
		return gameState;
	}
	//returns current game state
	public GameState getGameState() {
		return gameState;
	}
	//restart the game from scratch
	public GameState resetGame() {
		return initGame();
	}
	
}
/*Player clicks Roll
       ↓
Roll random dice (1-6)
       ↓
newPosition = currentPosition + diceValue
       ↓
newPosition > 100? → Stay, switch turn (wasted turn)
       ↓
Set landingPosition = newPosition  ← frontend uses this for 2 sec delay
       ↓
newPosition == 100? → Game over, set winner
       ↓
Snake at newPosition? → Slide down to tail, switch turn
       ↓
Ladder at newPosition? → Climb up to top, switch turn
       ↓
Rolled 6? → Extra turn (no switch)
       ↓
Normal move → Switch turn
 */
