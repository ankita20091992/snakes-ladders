package com.game.model;
//implementation of map we will use
import java.util.HashMap;
//interface stores data as key->value pairs
import java.util.Map;
public class GameBoard {
	//declaring two maps
	private Map<Integer, Integer> snakes;
	private Map<Integer, Integer> ladders;
		//constructor : it runs auto when you create GameBoard object
		public GameBoard() {
			//creating empty hashmap for snakes
			snakes = new HashMap<>();
			snakes.put(98, 40);
			snakes.put(84, 63);
			snakes.put(87, 49);
			snakes.put(75, 15);
			snakes.put(56, 8);
			snakes.put(50, 5);
			snakes.put(43, 17);
			//creating empty hashmap for ladders
			ladders = new HashMap<>();
			ladders.put(2, 23);
			ladders.put(6, 45);
			ladders.put(20, 59);
			ladders.put(52, 72);
			ladders.put(57, 96);
			ladders.put(71, 92);
		}
		//getter methods allow other classes to access these maps
		public Map<Integer, Integer> getSnakes() { return snakes;}
		public Map<Integer, Integer> getLadders() { return ladders; }
	}
