package com.mns.quiz.parse.module;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.mns.quiz.parser.preference.DatabaseQuery;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PUZZLE")
public class Puzzle extends ParseObject  implements Serializable,Parcelable{

	/*
	 * 
	 */
	
	private static final long serialVersionUID = 2893454830608888104L;
	private Puzzle obj;

	public Puzzle(Parcel in) {
		readFromParcel(in);
	}
	public Puzzle() {
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

	public String getName() {
		return getString(DatabaseQuery.NAME);
	}

	public void setName(String name) {
		put(DatabaseQuery.NAME, name);
	}

	public String getTitle() {
		return getString(DatabaseQuery.TITLE);
	}

	public void setTitle(String title) {
		put(DatabaseQuery.TITLE, title);
	}

	public int getNoOfClues() {
		return getInt(DatabaseQuery.NO_OF_CLUES);
	}

	public void setNoOfClues(int noOfClues) {
		put(DatabaseQuery.NO_OF_CLUES, noOfClues);
	}

	@Override
	public int describeContents() {
		return 0;
	}
 
	private void readFromParcel(Parcel in) {

		
		obj = in.readParcelable(Puzzle.class.getClassLoader());
		setId(Integer.getInteger(in.readString()));
//		setObjectId(in.readString());
		setName(in.readString());
		setTitle(in.readString());
		setNoOfClues(in.readInt());
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
 
		dest.writeParcelable(obj, flags);
		dest.writeInt(getId());
//		dest.writeString(getObjectId());
		dest.writeString(getName());
		dest.writeString(getTitle());
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new Parcelable.Creator() {
	            public Puzzle createFromParcel(Parcel in) {
	                return new Puzzle(in);
	            }
	 
	            public Puzzle[] newArray(int size) {
	                return new Puzzle[size];
	            }
	        };


}
