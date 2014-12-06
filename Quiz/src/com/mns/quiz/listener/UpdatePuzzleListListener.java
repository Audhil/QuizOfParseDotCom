package com.mns.quiz.listener;

import java.util.HashMap;

import com.mns.quiz.parse.module.PuzzleClues;

public interface UpdatePuzzleListListener {
	
	public void updateListAdapter(HashMap<String,PuzzleClues> puzzleClues);

}
