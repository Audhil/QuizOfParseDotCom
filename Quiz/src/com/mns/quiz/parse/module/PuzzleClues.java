package com.mns.quiz.parse.module;

import java.util.ArrayList;

public class PuzzleClues {
	
	Puzzle puzzle;
	ArrayList<Clue> clues;
	public Puzzle getPuzzle() {
		return puzzle;
	}
	public void setPuzzle(Puzzle puzzle) {
		this.puzzle = puzzle;
	}
	public ArrayList<Clue> getClues() {
		return clues;
	}
	public void setClues(ArrayList<Clue> clues) {
		this.clues = clues;
	}

}
