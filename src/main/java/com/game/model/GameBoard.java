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
			snakes.put(99, 54);
			snakes.put(70, 55);
			snakes.put(52, 42);
			snakes.put(25, 5);
			snakes.put(95, 75);
			snakes.put(61, 19);
			snakes.put(87, 24);
			snakes.put(17, 7);
			//creating empty hashmap for ladders
			ladders = new HashMap<>();
			ladders.put(4, 14);
			ladders.put(9, 31);
			ladders.put(20, 38);
			ladders.put(28, 84);
			ladders.put(40, 59);
			ladders.put(51, 67);
			ladders.put(63, 81);
			ladders.put(71, 91);
		}
		//getter methods allow other classes to access these maps
		public Map<Integer, Integer> getSnakes() { return snakes;}
		public Map<Integer, Integer> getLadders() { return ladders; }
	}
