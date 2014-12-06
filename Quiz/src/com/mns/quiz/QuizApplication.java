package com.mns.quiz;

import android.app.Application;
import android.content.Context;

import com.mns.quiz.api.ParseApi;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parse.module.Puzzle;
import com.mns.quiz.parser.preference.QuizPreference;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.PushService;

public class QuizApplication extends Application {
	private static ParseUser mUser;

	@Override
	public void onCreate() {
		super.onCreate();
		Debug.debug=false;
		// ParseAnalytics.trackAppOpened(getIntent());
		Parse.initialize(this, "Un9nHtNCfVphHof5RMSQlZZDUiclohoLGamaRb5s","sulTyAyuzDEH6MQV9ACIgD000Z4cubXtSEdpyJQ4");
		Parse.enableLocalDatastore(this);

		/*
		 * In this tutorial, we'll subclass ParseObject for convenience to
		 * create and modify Meal objects
		 */
		ParseObject.registerSubclass(Clue.class);
		ParseObject.registerSubclass(Puzzle.class);
		Parse.setLogLevel(TRIM_MEMORY_BACKGROUND);

		QuizPreference.init(this);
		ParseApi.init(this);
		ParseApi.setContext(this);

		ParseACL roleACL = new ParseACL();
		roleACL.setPublicReadAccess(true);
		roleACL.setPublicWriteAccess(true);
		ParseRole role = new ParseRole("Administrator", roleACL);
		role.saveInBackground();

		ParseACL userRoleACL = new ParseACL();
		userRoleACL.setPublicReadAccess(true);
		userRoleACL.setPublicWriteAccess(false);
		ParseRole userRole = new ParseRole("User", userRoleACL);
		userRole.saveInBackground();

	}

	public static ParseUser getUser() {
		return mUser;
	}

	public static void setUser(ParseUser user) {
		mUser = user;
	}
}
