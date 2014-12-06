package com.mns.quiz.parse.module;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.mns.quiz.parser.preference.DatabaseQuery;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CLUE")
public class Clue extends ParseObject  implements Serializable,Parcelable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2893454830608888104L;
	private Clue obj;

	public Clue(Parcel in) {
		readFromParcel(in);
	}
	public Clue() {
	}

	public int getId() {
		return getInt(DatabaseQuery.ID);
	}

	public void setId(int id) {
		put(DatabaseQuery.ID,id);
	}
	
/*	public String getObjectId() {
		return getString(DatabaseQuery.OBJECT_ID);
	}

	public void setObjectId(String objectId) {
		put(DatabaseQuery.OBJECT_ID, objectId!=null?objectId:"");
	}*/
	public String getClue() {
		return getString(DatabaseQuery.CLUE);
	}
	 
	public void setClue(String clue) {
		put(DatabaseQuery.CLUE,clue);
	}
	public String getAnswer() {
		return getString(DatabaseQuery.ANSWER);
	}
	public void setAnswer(String answer) {
		put(DatabaseQuery.ANSWER,answer);
	}
	public String getRefId() {
		return getString(DatabaseQuery.PUZZLE_REF_ID);
	}
	public void setRefId(String refId) {
		put(DatabaseQuery.PUZZLE_REF_ID,refId);
	}
	public String getUserSolution() {
		return getString(DatabaseQuery.USER_SOLUTION);
	}
	public void setUserSolution(String userSolution) {
		put(DatabaseQuery.USER_SOLUTION,userSolution);
	}
	public int getClueNumber() {
		return getInt(DatabaseQuery.CLUE_NUMBER);
	}
	public void setClueNumber(int clueNumber) {
		put(DatabaseQuery.CLUE_NUMBER,clueNumber);
	}
	
	public String getTitle() {
		return getString(DatabaseQuery.PUZZLE_TITLE);
	}
	public void setTitle(String puzzleTitle) {
		put(DatabaseQuery.PUZZLE_TITLE,puzzleTitle);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
 
	private void readFromParcel(Parcel in) {

		
		obj = in.readParcelable(Clue.class.getClassLoader());
		setId(in.readInt());
		setObjectId(in.readString());
		setClue(in.readString());
		setAnswer(in.readString());
		setRefId(in.readString());
		setUserSolution(in.readString());
		setClueNumber(in.readInt());
		setTitle(in.readString());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
 
		dest.writeParcelable(obj, flags);
		dest.writeInt(getId());
		dest.writeString(getObjectId());
		dest.writeString(getClue());
		dest.writeString(getAnswer());
		dest.writeString(getRefId());
		dest.writeString(getUserSolution());
		dest.writeInt(getClueNumber());
		dest.writeString(getTitle());
		 
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new Parcelable.Creator() {
	            public Clue createFromParcel(Parcel in) {
	                return new Clue(in);
	            }
	 
	            public Clue[] newArray(int size) {
	                return new Clue[size];
	            }
	        };


}
