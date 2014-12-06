package com.mns.quiz.parser;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mns.quiz.Debug;
import com.mns.quiz.QuizApplication;
import com.mns.quiz.listener.UpdatePuzzleListListener;
import com.mns.quiz.parse.module.Cell;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parse.module.Puzzle;
import com.mns.quiz.parse.module.PuzzleClues;
import com.mns.quiz.uitity.Constants;
import com.parse.ParseACL;

public class PuzParserTask extends AsyncTask<String, Void, Boolean> {
	private static PuzParserTask instance;
	ProgressDialog pDialog;
	private static Context context;
	private UpdatePuzzleListListener mListener;
	HashMap<String, PuzzleClues> puzzleclues;
	public PuzParserTask(Context activity) {
		context = activity;

	}
	
	public void setUpdateListener(UpdatePuzzleListListener listener) {
		mListener=listener;
	}

	@Override
	protected void onPreExecute() {
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(false);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(String... params) {

		// https://code.google.com/p/puz/wiki/FileFormat
		puzzleclues=new HashMap<String, PuzzleClues>();
		
		for (String path : params) {
			ArrayList<Clue> clues = new ArrayList<Clue>();
			try {
				Debug.print("file--->"+path);
				File metaFile = new File(path);
				FileInputStream fis = new FileInputStream(path);
				DataInputStream input = new DataInputStream(fis);

				ArrayList<Cell> cells = new ArrayList<Cell>();
				input.skipBytes(0x18);

				byte[] version = new byte[3];

				for (int i = 0; i < version.length; i++) {
					version[i] = input.readByte();
				}
				// Reserved1C(?) 0x1C 0x1D 0x2 ? In many files, this is
				// uninitialized memory

				input.skipBytes(0x3);
				Debug.print("Solution CheckSum:"
						+ Short.reverseBytes(input.readShort()));
				//
				input.skipBytes(0xC);
				int width = (0xFFFF & input.readByte());
				Debug.print("width:" + width);
				int height = (0xFFFF & input.readByte());
				Debug.print("height:" + height);
				short numberOfClues = Short.reverseBytes(input.readShort());
				Debug.print("No of clues:" + numberOfClues);
				// Scrambled Tag 0x32 0x33 0x2 short 0 for unscrambled puzzles.
				// Nonzero (often 4) for scrambled puzzles.
				// input.skipBytes(0x32);
				input.skipBytes(0x2);
				boolean scrambled = (input.readShort() != 0);
				Debug.print("Scrambled:" + scrambled);

				byte[] answerByte = new byte[1];

				for (int x = 0; x < height; x++) {
					for (int y = 0; y < width; y++) {
						Cell cell = new Cell();
						cell.setRow(x);
						cell.setColumn(y);
						cells.add(cell);
					}
				}

				for (Cell cell : cells) {
					answerByte[0] = input.readByte();
					char answer = new String(answerByte, Constants.CHARSET.name()).charAt(0);
					cell.setAnswer(String.valueOf(answer));
					// Debug.print("Block Solution[" + cell.getRow() + "] ["+
					// cell.getColumn() + "] " + answer);
				}
				for (Cell cell : cells) {
					answerByte[0] = input.readByte();
					char answer = new String(answerByte,Constants.CHARSET.name()).charAt(0);
					if (answer == '.') {
						continue;
					} else if (answer == '-') {
						cell.setUserAnser(String.valueOf(' '));
					} else if (cell != null) {
						cell.setUserAnser(String.valueOf(answer));
					} else {
						Debug.print("Unexpected answer: " + cell.getRow() + ","
								+ cell.getColumn() + " " + answer);
					}
				}

				int clueCount = 1;
				for (Cell cell : cells) {
					boolean tickedClue = false;
					if (cell.getAnswer() == '.') {
						continue;
					}

					Cell downRowFirstCont = getCell(cell.getRow() - 1,
							cell.getColumn(), cells);
					Cell downRowSecondCont = getCell(cell.getRow() + 1,
							cell.getColumn(), cells);

					if (((cell.getRow() == 0) || (downRowFirstCont == null || (downRowFirstCont != null && downRowFirstCont
							.getAnswer() == '.')))
							&& (((cell.getRow() + 1) < height) && ((downRowSecondCont != null && downRowSecondCont
									.getAnswer() != '.')))) {
						cell.setDown(true);

						if ((cell.getRow() == 0)
								|| (downRowFirstCont == null || (downRowFirstCont != null && downRowFirstCont
										.getAnswer() == '.'))) {
							cell.setClueNumber(clueCount);
							tickedClue = true;
						}
					}

					Cell acrossColumnFirstCont = getCell(cell.getRow(),
							cell.getColumn() - 1, cells);
					Cell acrossColumnSecondCont = getCell(cell.getRow(),
							cell.getColumn() + 1, cells);

					if (((cell.getColumn() == 0) || (acrossColumnFirstCont == null || (acrossColumnFirstCont != null && acrossColumnFirstCont
							.getAnswer() == '.')))
							&& (((cell.getColumn() + 1) < width) && ((acrossColumnSecondCont != null && acrossColumnSecondCont
									.getAnswer() != '.')))) {
						cell.setAcross(true);

						if ((cell.getColumn() == 0)
								|| (acrossColumnFirstCont == null || (acrossColumnFirstCont != null && acrossColumnFirstCont
										.getAnswer() == '.'))) {
							cell.setClueNumber(clueCount);
							tickedClue = true;
						}
					}

					if (tickedClue) {
						clueCount++;
						tickedClue = false;
					}

				}

				String title = readNullTerminatedString(input);

				readNullTerminatedString(input);
				readNullTerminatedString(input);

				Puzzle puzzle = Puzzle.createWithoutData(Puzzle.class,title);
				puzzle.setName(new File(path).getName());
//				String puzzleId=puzzle.getObjectId();
				puzzle.setId(-1);
				puzzle.setNoOfClues(numberOfClues);
				puzzle.setTitle(title);
				puzzle.setACL(new ParseACL( QuizApplication.getUser()));
//				puzzle.saveEventually();
				Debug.print("puzzle:--------------------------->"+puzzle.getObjectId());
				ArrayList<Cell> clueAcross = new ArrayList<Cell>();
				ArrayList<Cell> clueDown = new ArrayList<Cell>();
				int i = 0;
				for (Cell cell : cells) {
					if (cell.getAnswer() == '.') {
						continue;
					}

					if (cell.isAcross() && (cell.getClueNumber() != 0)) {
						String value = readNullTerminatedString(input);
						Debug.print(i + "==>Across--->[" + cell.getRow() + "] ["+ cell.getColumn() + "]====>" + value);
						cell.setAcross(value);
						clueAcross.add(cell);
						StringBuilder answer = new StringBuilder();
						for (int across = cell.getColumn(); across < width; across++) {

							char val = getCell(cell.getRow(), across, cells)
									.getAnswer();
							if (val == '.')
								break;
							else
								answer.append(val);
						}
						Clue clue = Clue.createWithoutData(Clue.class,title+"-"+cell.getClueNumber());
						clue.setId(-1);
						clue.setClue(value);
						clue.setAnswer(answer.toString());
						clue.setClueNumber(cell.getClueNumber());
						clue.setUserSolution("");
						clue.setACL(new ParseACL(  QuizApplication.getUser()));
//						clue.saveEventually();
						clues.add(clue);
						clue=null;
						i++;
					}

					if (cell.isDown() && (cell.getClueNumber() != 0)) {
						String value = readNullTerminatedString(input);
						Debug.print(i + "==>Down--->[" + cell.getRow() + "] ["+ cell.getColumn() + "]====>" + value);
						cell.setDown(value);
						clueDown.add(cell);
						StringBuilder answer = new StringBuilder();
						for (int down = cell.getRow(); down < height; down++) {
							char val = getCell(down, cell.getColumn(), cells)
									.getAnswer();
							if (val == '.')
								break;
							else
								answer.append(val);
						}

						Clue clue = Clue.createWithoutData(Clue.class,title+"-"+cell.getClueNumber());
						clue.setId(-1);
						clue.setClue(value);
						clue.setAnswer(answer.toString());
						clue.setClueNumber(cell.getClueNumber());
						clue.setUserSolution("");
						clue.setACL(new ParseACL(  QuizApplication.getUser()));
//						clue.saveEventually();
						clues.add(clue);
						clue=null;
						i++;
					}

				}
				
				PuzzleClues pc = new PuzzleClues();
				pc.setPuzzle(puzzle);
				pc.setClues(clues);
				puzzleclues.put(puzzle.getTitle(), pc);
				
				fis.close();

				if (metaFile.exists()) {
					fis = new FileInputStream(metaFile);
					fis.close();
					puzzle=null;
					clues=null;
				}
			} catch (IOException e) {
			 
				e.printStackTrace();
			}finally{
				
			}
		}
		
		return true;
	}
 

	
	
	

	@Override
	protected void onPostExecute(Boolean result) {
		if (null != pDialog && pDialog.isShowing()) {
			pDialog.dismiss();
		}
		Debug.print("puzzleClues-->"+puzzleclues.size());
		mListener.updateListAdapter(puzzleclues);
		puzzleclues=null;
		super.onPostExecute(result);
	}

	 
	private static Cell getCell(int row, int column, ArrayList<Cell> cells) {

		for (Cell cell : cells) {
			if (cell.getRow() == row && cell.getColumn() == column) {
				return cell;
			}
		}
		return null;
	}

	public static String readNullTerminatedString(InputStream is)
			throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream(128);

		for (byte nextByte = (byte) is.read(); nextByte != 0x0; nextByte = (byte) is
				.read()) {
			if (nextByte != 0x0) {
				baos.write(nextByte);
			}

			if (baos.size() > 4096) {
				throw new IOException("Run on string!");
			}
		}

		return (baos.size() == 0) ? null : new String(baos.toByteArray(),
				Constants.CHARSET.name());
	}
	

}