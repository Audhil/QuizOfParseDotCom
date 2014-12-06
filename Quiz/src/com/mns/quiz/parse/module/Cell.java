package com.mns.quiz.parse.module;

public class Cell {
	int crossId;
	String refPuzzleId;
	int row;
	int column;
	char answer;
	char userAnser;
	boolean isAcross;
	boolean isDown;
	String across;
	String down;
	int clueNumber;
	
	
	public int getCrossId() {
		return crossId;
	}
	public void setCrossId(int crossId) {
		
		this.crossId = crossId;
	}
	public String getRefPuzzleId() {
		return refPuzzleId;
	}
	public void setRefPuzzleId(String refPuzzleId) {
		this.refPuzzleId = refPuzzleId;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public char getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer =answer.charAt(0);
	}
	public char getUserAnser() {
		return userAnser;
	}
	public void setUserAnser(String userAnser) {
		this.userAnser = userAnser.charAt(0);;
	}
	public boolean isAcross() {
		return isAcross;
	}
	public void setAcross(boolean isAcross) {
		this.isAcross = isAcross;
	}
	public boolean isDown() {
		return isDown;
	}
	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
	public String getAcross() {
		return across;
	}
	public void setAcross(String across) {
		this.across = across;
	}
	public String getDown() {
		return down;
	}
	public void setDown(String down) {
		this.down = down;
	}
	public int getClueNumber() {
		return clueNumber;
	}
	public void setClueNumber(int clueNumber) {
		this.clueNumber = clueNumber;
	}
	 
}
