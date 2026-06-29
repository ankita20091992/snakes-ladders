package com.game.service;
import com.game.model.GameBoard;
import com.game.model.GameState;
import com.game.model.GameStatus;
import com.game.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Service tells spring to manage this class as a service bean, allowing it to be injected into other components.
//creates one instance and reuses it everywhere
//gameState holds the current state of the game, including player positions, dice value, and game status.
//gameBoard represents the game board, including the positions of snakes and ladders.
//random is used to simulate dice rolls.
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
		//creates two players both starting at position 0
		players.add(new Player(1, "Player 1"));
		players.add(new Player(2, "Player 2"));
		//creates a fresh gameState and fill every fields with starting values
		gameState = new GameState();
		gameState.setPlayers(players);
		//currentPlayerIndex is set to 0, Player 1 will go first.
		gameState.setCurrentPlayerIndex(0);
		gameState.setDiceValue(0);
		gameState.setLandingPosition(0);
		gameState.setStatus(GameStatus.PLAYING);
		gameState.setWinner(null);
		gameState.setMessage("Player 1's turn. Roll the dice!");
		//return initial game state to the controller, which will send it to the frontend.
		return gameState;
	}

//full game logic roll dice,move players,check snake/ladder/wins	
	public GameState rollDice() {
		//(Guard-check) if gameState is null or game is finished, return current state without changes
		if(gameState == null || gameState.getStatus() == GameStatus.FINISHED) {
			return gameState;
		}
		//who is playing?-gets index 0 or 1 and fetch that player object from list.
		int currentIndex = gameState.getCurrentPlayerIndex();
		Player currentPlayer = gameState.getPlayers().get(currentIndex);
		//roll dice - nextInt give 0-5, +1 gives 1-6
		//save the rolled value into gameState so frontend can display it
		int diceValue = random.nextInt(6) + 1;
		gameState.setDiceValue(diceValue);
		//calculate new position - where are you + what you rolled=where you'd land
		int currentPosition = currentPlayer.getPosition();
		int newPosition = currentPosition + diceValue;
		
		StringBuilder message = new StringBuilder();
		message.append(currentPlayer.getName())
				.append("rolled").append(diceValue).append(".");
		
		//overshoot check - in S & L you need exact roll to land on 100.
		if(newPosition > 100) {
			gameState.setLandingPosition(currentPosition);
			message.append("needs exact roll to win! Turn passes");
			//for switching turns
			int nextIndex = (currentIndex == 0) ? 1 : 0;
			gameState.setCurrentPlayerIndex(nextIndex);
			gameState.setMessage(message.toString());
			return gameState;
		}
		
		//set landing position before snake/ladder check
		gameState.setLandingPosition(newPosition);
		currentPlayer.setPosition(newPosition);
		
		//check win - exact 100 is game over. 
		// set status to Finished, record the winner, return immediately
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
		//snake check - if landed square has a snake head. If yes, move 
		//player to the snake's tail.
		if(gameBoard.getSnakes().containsKey(newPosition)) {
			int tail = gameBoard.getSnakes().get(newPosition);
			message.append("Oops! Hit a snake...");
			currentPlayer.setPosition(tail);
			hitSnake = true;//prevent the ladder check from running on same square
		}
		//ladder check - 
		if(!hitSnake && gameBoard.getLadders().containsKey(newPosition)) {
			int top = gameBoard.getLadders().get(newPosition);
			message.append("Ladder! Climbs up from ").append(newPosition)
					.append("to ").append(top).append(". ");
			currentPlayer.setPosition(top);
			hitLadder = true;
		}
		
		//extra turn on rolling 6
		if (diceValue == 6) { //don't switch turn , return
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
