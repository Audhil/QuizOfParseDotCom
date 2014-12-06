package com.mns.quiz.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.mns.quiz.Debug;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parse.module.Puzzle;
import com.mns.quiz.parser.preference.DatabaseQuery;
import com.mns.quiz.uitity.Constants;
import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseApi {
	private static ParseApi instance = null;
	public static Context context = null;
	  private ParseConfig config;
	  private long configLastFetchedTime;

	public synchronized static void setContext(Context value) {
		ParseApi.context = value;
	}

	static public void init(Context ctx) {
		if (null == instance) {
			instance = new ParseApi(ctx);
		}
	}

	public ParseApi(Context context) {
	}

	public synchronized static ParseApi getInstance() {
		if (null == instance) {
			instance = new ParseApi(ParseApi.context);
		}

		return instance;
	}
	
	 public void getUsers(ParseUser user
			 ,final String role){
		 Debug.print("Get Users--------------->"+user.getUsername());
			 ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
			 query.whereEqualTo(Constants.PARSE_USER_NAME,user.getUsername() );
			 query.whereEqualTo(Constants.PARSE_EMAIL,user.getEmail() );
			query.findInBackground(new FindCallback<ParseUser>() {

				@Override
				public void done(List<ParseUser> objects, ParseException e) {
					 if(e==null){
						 Debug.print("user:"+objects.get(0).getUsername());
						 setRoleForUser(objects.get(0), role);
					 }else{
						 Debug.print(e.getMessage());
					 }
					
				}
			});
	 }
	 
	 public void setRoleForUser(final ParseUser user, String roleName){
		ParseQuery<ParseRole> query = ParseQuery.getQuery(ParseRole.class);
		query.whereEqualTo(Constants.PARSE_NAME,roleName ); 
		query.getFirstInBackground(new GetCallback<ParseRole>() {
			
			@Override
			public void done(ParseRole object, ParseException e) {
				Debug.print("Role Object==>"+object.getName());
				object.getUsers().add(user);
				object.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						Debug.print("Saved ACL==>"+e);
						
					}
				});
			}
		});
     		  
	 }
	 
	
	public void fetchConfigIfNeeded() {
		final long configRefreshInterval = 60 * 60; // 1 hour

		if (config == null|| System.currentTimeMillis() - configLastFetchedTime > configRefreshInterval) {
			 
			config = ParseConfig.getCurrentConfig();

			 
			ParseConfig.getInBackground(new ConfigCallback() {
				@Override
				public void done(ParseConfig parseConfig, ParseException e) {
					if (e == null) {
						// Yay, retrieved successfully
						config = parseConfig;
						configLastFetchedTime = System.currentTimeMillis();
					} else {
						// Fetch failed, reset the time
						configLastFetchedTime = 0;
					}
				}
			});
		}
	}

	public List<Float> getSearchDistanceAvailableOptions() {
		final List<Float> defaultOptions = Arrays.asList(250.0f, 1000.0f,
				2000.0f, 5000.0f);

		List<Number> options = config.getList("availableFilterDistances");
		if (options == null) {
			return defaultOptions;
		}

		List<Float> typedOptions = new ArrayList<Float>();
		for (Number option : options) {
			typedOptions.add(option.floatValue());
		}

		return typedOptions;
	}

	public int getPostMaxCharacterCount() {
		int value = config.getInt("postMaxCharacterCount", 140);
		return value;
	}
	ArrayList<Puzzle> puzzleList=null;
	public void addPuzzle(ArrayList<Puzzle> puzzles,
			final ApiCallback<String> apiCallback) {
		puzzleList= new ArrayList<Puzzle>();
		for (Puzzle puzzle : puzzles) {
			Puzzle puzzle_ = new Puzzle();
//			Puzzle puzzle_ = ParseObject.createWithoutData(Puzzle.class,puzzle.getName());
			puzzle_.setId(puzzle.getId());
			Debug.print("---->"+puzzle_.getObjectId());
				
			puzzle_.setName(puzzle.getName());
			puzzle_.setTitle(puzzle.getTitle());
			puzzle_.setNoOfClues(puzzle.getNoOfClues());
//			puzzle_.setACL(puzzle.getACL());
			// Save the post and return
			puzzleList.add(puzzle_);
			puzzle_=null;
			Debug.print("Add Puzzle" + puzzleList.size());
		}
		if(puzzleList!=null){
		Puzzle.saveAllInBackground(puzzleList, new SaveCallback() {

			@Override
			public void done(ParseException e) {
				puzzleList=null;
				if (e == null) {
					Debug.print("ListRow inserted Sucessfully");
					// setResult(RESULT_OK);
					// finish();
					apiCallback.onCompleted("ListRow inserted Sucessfully");

				} else {
					Debug.print(e.getMessage());
					apiCallback.onException(e);

				}
			}
		});
		}
	}
	
	ArrayList<Clue> clueList =null;
	public void addClues(ArrayList<Clue> clues,
			final ApiCallback<String> apiCallback) {
		Debug.print("AddClues");
		clueList = new ArrayList<Clue>();
		for (Clue clue : clues) {
			Clue clue_ = new Clue();
			clue_.setId(clue.getId());
			clue_.setAnswer(clue.getAnswer());
			clue_.setClue(clue.getClue());
			clue_.setClueNumber(clue.getClueNumber());
			clue_.setUserSolution(clue.getUserSolution());
			clue_.setRefId(clue.getRefId());
			clue.setTitle(clue.getTitle());
//			clue_.setACL(clue.getACL());
			Debug.print("clue:" + clue.getRefId());
			clueList.add(clue_);
			clue_=null;
		}
		if(clueList!=null){
		Debug.print("listSize:" + clueList.size());
		 Clue.saveAllInBackground(clueList, new SaveCallback() {
			@Override
			public void done(ParseException e) {
				clueList=null;
				if (e == null) {
					Debug.print("okkk");
					Debug.print("ListRow clues inserted Sucessfully");
					apiCallback.onCompleted("ListRow clues inserted Sucessfully");

				} else {
					Debug.print("Row clues inserted failed"+e);
					apiCallback.onCompleted("Row clues inserted failed"
							+ e.getMessage());
					apiCallback.onException(e);

				}
			}
		}); 
		}
	}

	// getRow
	public void getPuzzleObjectId(String name, String title,
			final ApiCallback<String> apiCallback) {
		final String id = null;
		ParseQuery<Puzzle	> query_ = ParseQuery.getQuery(Puzzle.class);
		// query_.whereEqualTo(DatabaseQuery.ID, id_);
		query_.whereEqualTo(DatabaseQuery.NAME, name);
		query_.whereEqualTo(DatabaseQuery.TITLE, name);
		query_.getFirstInBackground(new GetCallback<Puzzle>() {

			@Override
			public void done(Puzzle object, ParseException e) {
			 
				if (object == null) {
					apiCallback.onException(e);
				} else {
					Log.d("score",
							"Retrieved the object." + object.getObjectId());
					apiCallback.onCompleted(object.getObjectId());
				}

			}
		});
	}

	public void getPuzzles(final DefaultApiCallback<List<Puzzle>> apiCallback) {
		Debug.print("GetPuzzles");
	 
		ParseQuery<Puzzle> query = ParseQuery.getQuery(Puzzle.class);
		 
		query.findInBackground(new FindCallback<Puzzle>() {

			@Override
			public void done(List<Puzzle> puzzles, ParseException e) {
				if (e == null) {
					if (puzzles != null && puzzles.size() > 0) {
					 Puzzle.pinAllInBackground(puzzles);
					}					
					
					
					apiCallback.onCompleted(puzzles);
					
				 
					Log.d(ParseApi.class.getName(),"Retrieved " + puzzles.size() + " puzzles");
				} else {
					Log.d(ParseApi.class.getName(), "Error: " + e.getMessage());
					apiCallback.onException(e);
				}
			}
		});
	 
	}
	
	public void getParseClues(final DefaultApiCallback<List<Clue>> apiCallback) {

				ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class);
				try {
					List<Clue> puzzleList = query.find();
					apiCallback.onCompleted(puzzleList);
					Debug.print("Parse Clues:" + puzzleList.size());
				} catch (ParseException e) {
					apiCallback.onException(e);
				}
		 
	}
	

	

	public void getClueObjectId(String clue, String clueNo, String refId,
			final ApiCallback<Clue> apiCallback) {
		// getRow

		final String id = null;
		ParseQuery<Clue> query_ = ParseQuery.getQuery(Clue.class);
		query_.whereEqualTo(DatabaseQuery.PUZZLE_REF_ID, refId);
		query_.whereEqualTo(DatabaseQuery.CLUE, clue);
		query_.whereEqualTo(DatabaseQuery.CLUE_NUMBER, clueNo);
		query_.getFirstInBackground(new GetCallback<Clue>() {

			@Override
			public void done(Clue object, ParseException e) {
				if (object == null) {
					apiCallback.onException(e);
				} else {
					Log.d("score",
							"Retrieved the object." + object.getObjectId());
					apiCallback.onCompleted( object);
				}

			}
		});
	}

	public void getClueObjectId(String clueId,
			final ApiCallback<Clue> apiCallback) {
		// getRow

		final String id = null;
		ParseQuery<Clue> query_ = ParseQuery.getQuery(Clue.class);
		query_.whereEqualTo(DatabaseQuery.OBJECT_ID, clueId);
		query_.getFirstInBackground(new GetCallback<Clue>() {

			@Override
			public void done(Clue object, ParseException e) {
				if (object == null) {
					apiCallback.onException(e);
				} else {
					Log.d("score",
							"Retrieved the object." + object.getObjectId());
					apiCallback.onCompleted(  object);
				}

			}
		});
	}
	
	public void getClues(String refId, final DefaultApiCallback<List<Clue>> apiCallback) {
		Debug.print("GetClues"+refId);
		ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class);
		if(refId!=null)
		 query.whereEqualTo(DatabaseQuery.PUZZLE_REF_ID, refId);
		query.findInBackground(new FindCallback<Clue>() {

			@Override
			public void done(List<Clue> clueList, ParseException e) {
				if (e == null) {
					if (clueList != null && clueList.size() > 0) {
					 Clue.pinAllInBackground(clueList);
					}					
					
					
					apiCallback.onCompleted(clueList);
					Log.d(ParseApi.class.getName(),"Retrieved " + clueList.size() + " Clues");
				} else {
					Log.d(ParseApi.class.getName(), "Error: " + e.getMessage());
					apiCallback.onException(e);
				}
			}
		});

	}
	

	/*
	  * 
	  * 
	  */

	public void  getLocalPuzzles(final DefaultApiCallback<List<Puzzle>> apiCallback) {

		// Pin ParseQuery results
		// List<ParseObject> objects = query.find(); // Online ParseQuery
		// results
		// ParseObject.pinAllInBackground(objects);

		// Query the Local Datastore
Debug.print("Fetch local Pzzles=-->");
		ParseQuery<Puzzle> query = ParseQuery.getQuery(Puzzle.class).fromPin();
		 
		 query.findInBackground(new FindCallback<Puzzle>() {
			
			@Override
			public void done(List<Puzzle> objects, ParseException e) {
		 Debug.print("Get Local puuuzzles--->"+objects);
			 if(objects!=null){
				apiCallback.onCompleted(objects);
			}else{
				apiCallback.onException(e);
			}
			}
		});

	}
	
	public void  getLocalClues(final DefaultApiCallback<List<Clue>> apiCallback) {

	 
		ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class);
		 query.fromLocalDatastore();
		 query.findInBackground(new FindCallback<Clue>() {
			
			@Override
			public void done(List<Clue> objects, ParseException e) {
		 
			 if(objects!=null){
				apiCallback.onCompleted(objects);
			}else{
				apiCallback.onException(e);
			}
			}
		});
		
	 

	}
	
	public void  getLocalCluesByRefId(String refId,final DefaultApiCallback<List<Clue>> apiCallback) {

		 
		ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class);
		 query.fromLocalDatastore();
		 query.whereEqualTo(DatabaseQuery.PUZZLE_REF_ID, refId);
		 query.findInBackground(new FindCallback<Clue>() {
			
			@Override
			public void done(List<Clue> objects, ParseException e) {
//			 Debug.print(objects);
			 if(objects!=null){
				apiCallback.onCompleted(objects);
			}else{
				apiCallback.onException(e);
			}
			}
		});
		
	 

	}
	

	public static class ResponseStatus {
		public boolean Success;
		public List<String> StatusCodes;
		public int MessageId;
		public String MessageText;

		public String getMessage() {
			String message = "";
			return message;
		}
	}

	public static class ApiResponse<T> {
		public ResponseStatus Status;
		public T Value;
	}

	public static abstract class DefaultApiCallback<T> implements ApiCallback<T> {
		@Override
		public void onCompleted(T result) {
			onCompleted(result);
		}

		@Override
		public void onException(ParseException ex) {
			onException(ex);
		}
	}

	public void puzzleSaveInLocal(ArrayList<Puzzle> puzzles ) {
		ArrayList<Puzzle> list = new ArrayList<Puzzle>();
		for (Puzzle puzzle : puzzles) {
			Puzzle newFile = new Puzzle();
			newFile.setId(puzzle.getId());
			newFile.setName(puzzle.getName());
			newFile.setTitle(puzzle.getTitle());
			newFile.setNoOfClues(puzzle.getNoOfClues());
			newFile.saveEventually();
			list.add(newFile);
			
		}
		 
	}

	public Clue getClue(String id) {
		ParseQuery<Clue> query = ParseQuery.getQuery(Clue.class).fromPin();
		query.whereEqualTo(DatabaseQuery.OBJECT_ID,id);
		Clue clue=null;
		 try {
			clue = query.getFirst();
		} catch (ParseException e) {
			return null;
		}
		return clue;
	}

	 
}
