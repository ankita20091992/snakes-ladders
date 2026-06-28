package com.game.model;

public class Player {
	private int id;
	private String name;
	private int position;

	public Player(int id, String name) {		super();
		this.id = id;
		this.name = name;
		this.position = 0;
	}
	public int getId() { return id; }
	public String getName() {return name; }
	public int getPosition() { return position; }
	public void setPosition(int position) {
		this.position = position;
	}
	
}
