package com.mns.quiz.parser.preference;

public class DatabaseQuery {

	
	
	
	public static final String OBJECT_ID = "objectId";
	public static final String ID = "ID";
	
	/*
	 *  Puzzle Master Table
	 */
	public static final String PUZZLE_MASTER_TABLE = "Puzzle_Master";
	public static final String NAME="NAME";
	public static final String TITLE="TITLE";
	public static final String NO_OF_CLUES="NO_OF_CLUES";

	static final String CREATE_PUZZLE_MASTER_TABLE = "CREATE TABLE " + PUZZLE_MASTER_TABLE
			+ "("
			+ ID + " INTEGER PRIMARY KEY,"
			+ OBJECT_ID + " TEXT," 
			+ NAME + " TEXT," 
			+ TITLE + " TEXT," 
			+ NO_OF_CLUES + " INTEGER DEFAULT(0)"
			+ ")";

	/*
	 * CROSSWORD_DETAILS_TABLE
	 */
	
		public static final String CLUES_TABLE = "CLUE";
//		static final String ID="CLUE_ID";
		public static final String PUZZLE_REF_ID="PUZZLE_REF_ID";
		public static final String CLUE="CLUE";
		public static final String ANSWER="ANSWER";
		public static final String USER_SOLUTION="USER_SOLUTION";
		public static final String CLUE_NUMBER="CLUE_NUMBER";
		public static final String PUZZLE_TITLE = "PUZZLE_TITLE";
		 

		static final String CREATE_PUZZLE_CLUES_TABLE = "CREATE TABLE " + CLUES_TABLE
				+ "("
				+ ID	+ " INTEGER PRIMARY KEY," // NOTNULL
				+ OBJECT_ID + " TEXT," 
				+ PUZZLE_REF_ID	+ " TEXT,"// NOTNULL
				+ CLUE	+ " TEXT,"// NOTNULL
				+ ANSWER	+ " TEXT,"// NOTNULL
				+ USER_SOLUTION	+ " TEXT,"// NOTNULL
				+ CLUE_NUMBER + " INTEGER " 
				+ ")";
	
}
