package com.mns.quiz.parse.module;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.mns.quiz.parser.preference.DatabaseQuery;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject  implements Serializable,Parcelable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2893454830608888104L;
	private User obj;

	public User(Parcel in) {
		readFromParcel(in);
	}
	public User() {
	}
 
 
	@Override
	public int describeContents() {
		return 0;
	}
 
	private void readFromParcel(Parcel in) {

		
		obj = in.readParcelable(User.class.getClassLoader());
	 
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
 
		// The writeParcel method needs the flag
		// as well - but thats easy.
		dest.writeParcelable(obj, flags);
	 
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
	    	new Parcelable.Creator() {
	            public User createFromParcel(Parcel in) {
	                return new User(in);
	            }
	 
	            public User[] newArray(int size) {
	                return new User[size];
	            }
	        };


}
