package com.game.model;

import java.util.List;

public class GameState {
	private List<Player> players;//list holding both players
	private int currentPlayerIndex;
	private int diceValue;
	private GameStatus status;//stores current status of game using GameStatus enum
	private String winner;
	private String message;
	private int landingPosition;
	
	public int getLandingPosition() { return landingPosition; }
	public void setLandingPosition(int landingPosition) {
		this.landingPosition = landingPosition;
	}
	//empty ctor allows creating a blank GameState object and fill it using setters
	public GameState() {}
	//returns list of both players
	public List<Player> getPlayers() { return players;}
	public void setPlayers(List<Player> players) { this.players = players; }
	
	public int getCurrentPlayerIndex() { return currentPlayerIndex; }
	public void setCurrentPlayerIndex(int i) {this.currentPlayerIndex = i;}
	
	public int getDiceValue() { return diceValue; }
	public void setDiceValue(int diceValue) { this.diceValue = diceValue; }
	
	public GameStatus getStatus() { return status; }
	public void setStatus(GameStatus status) { this.status = status; }
	
	public String getWinner() { return winner; }
	public void setWinner(String winner) { this.winner = winner; }
	
	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
}
/*complete GameState looks at runtime
 * At game start:
players        = [Player1(pos=0), Player2(pos=0)]
currentPlayer  = 0  (Player 1's turn)
diceValue      = 0  (not rolled yet)
status         = PLAYING
winner         = null
message        = "Player 1's turn"

After Player 1 rolls 6:
players        = [Player1(pos=6), Player2(pos=0)]
currentPlayer  = 0  (extra turn because rolled 6)
diceValue      = 6
status         = PLAYING
winner         = null
message        = "Player 1 rolled 6! Extra turn!"

When Player 1 reaches 100:
players        = [Player1(pos=100), Player2(pos=45)]
currentPlayer  = 0
diceValue      = 3
status         = FINISHED
winner         = "Player 1"
message        = "Player 1 wins the game!"
 */
