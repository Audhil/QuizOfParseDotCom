package com.mns.quiz.parser.preference;


import java.io.File;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

@SuppressLint("CommitPrefEdits")
public class QuizPreference {
	
	static private QuizPreference instance;
	private Context mContext;
	private static final String MASTER_PREF_NAME = "Master";
	public static final String PLATFORM = "android";
	private static final String LOG_TAG = QuizPreference.class.getName();
	
	private static SharedPreferences prefs;
	private String deviceId;

	public QuizPreference(Context ctx) {
		mContext=ctx;
//		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefs = mContext.getSharedPreferences(MASTER_PREF_NAME, Context.MODE_PRIVATE);
		this.deviceId = prefs.getString("deviceId", this.getDeviceId());
	}

	static public void init(Context ctx) {
		if (null==instance) {
			instance = new QuizPreference(ctx);
		}
	}

	static public QuizPreference getInstance() {
		return instance;
	}
/*
 * Device Configuration
 */
	
	public String getDeviceId() {
		if (null == this.deviceId) {
			PackageManager pm = mContext.getPackageManager();
			if (!pm.hasSystemFeature("com.google.android.tv")) {
				try {
					TelephonyManager mgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
					this.deviceId = mgr.getDeviceId();
				} catch (Exception e) {
//					Logger.Log.d(LOG_TAG, "Faield to find device ID:%s %s",e.toString(),e.toString() );
				}
			}

			if (isNullOrEmpty(deviceId)) {
				this.deviceId = UUID.randomUUID().toString();
			}

			this.deviceId = UUID.randomUUID().toString();
		}
		return this.deviceId;
	}
	
	public static boolean isNullOrEmpty(CharSequence s) {
        return (s == null || s.equals("") || s.equals("null") || s.equals("NULL"));
    }
	
	 

	public void setDeviceId(String deviceId) {
		 this.deviceId=deviceId;
		
	}
	

	
	public int getDeviceSdkInt() {
		return Build.VERSION.SDK_INT;
	}
	

	
	public void clearUserPrefernce(){
		prefs.edit().clear().commit();
	}


	public void setUserSessionPrefence(String sessionToken) {
		Editor editor = prefs.edit();
		editor.putString("session",sessionToken);
		editor.commit();
	}

	public String getUserSessionPrefence() {
		Editor editor = prefs.edit();
		String session = prefs.getString("session",null);
		editor.commit();
		return session;
		
	}

	public void setSwipePositionPrefence(int position) {
		Editor editor = prefs.edit();
		editor.putInt("swipe",position);
		editor.commit();
	}

	public int getSwipePositionPrefence() {
		Editor editor = prefs.edit();
		int swipe = prefs.getInt("swipe", -1);
		editor.commit();
		return swipe;
		
	}
	
	/*
	 * File Browser Preference
	 * 
	 */
	
	public static final String PREF_HOME_DIR = "homeDir";
	public static final String PREF_SHOW_HIDDEN = "showHidden";
	public static final String PREF_SHOW_SYSFILES = "showSysFiles";
	public static final String PREF_SHOW_DIRS_FIRST = "showDirsFirst";
	public static final String PREF_SORT_DIR = "sort.dir";
	public static final String PREF_SORT_FIELD = "sort.field";
	public static final String VALUE_SORT_DIR_ASC = "asc";
	public static final String VALUE_SORT_DIR_DESC = "desc";
	public static final String VALUE_SORT_FIELD_M_TIME = "mtime";
	public static final String VALUE_SORT_FIELD_NAME = "name";
	public static final String VALUE_SORT_FIELD_SIZE = "size";
	private static final String PREF_NAVIGATE_FOCUS_ON_PARENT = "focusOnParent";
	public static final String EULA_ACCEPTED = "eula_accepted_v2.5";
	public static final String EULA_MARKER = "eula_marker_file_v2.5";
	public static final String PREF_USE_BACK_BUTTON = "useBackButton";

	public enum SortField {
		NAME, MTIME, SIZE
	}
	
	public File getStartDir() {
		String dirPath = PreferenceManager
				.getDefaultSharedPreferences(mContext).getString(PREF_HOME_DIR,
						"/");
		File homeDir = new File(dirPath);

		if (homeDir.exists() && homeDir.isDirectory()) {
			return homeDir;
		} else {
			return new File("/");
		}
	}

	public boolean isShowHidden() {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(PREF_SHOW_HIDDEN, false);
	}

	public boolean isShowSystemFiles() {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(PREF_SHOW_SYSFILES, true);
	}
	public boolean isShowDirsOnTop() {
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(PREF_SHOW_DIRS_FIRST, true);
	}
	public SortField getSortField() {
		String field = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString(PREF_SORT_FIELD, VALUE_SORT_FIELD_NAME);
		if (VALUE_SORT_FIELD_NAME.equalsIgnoreCase(field)) {
			return SortField.NAME;
		} else if (VALUE_SORT_FIELD_M_TIME.equalsIgnoreCase(field)) {
			return SortField.MTIME;
		} else if (VALUE_SORT_FIELD_SIZE.equalsIgnoreCase(field)) {
			return SortField.SIZE;
		} else {
			return SortField.NAME;
		}
	}

	 
	public int getSortDir() {
		String field = PreferenceManager.getDefaultSharedPreferences(mContext)
				.getString(PREF_SORT_DIR, "asc");
		if (VALUE_SORT_DIR_ASC.equalsIgnoreCase(field)) {
			return 1;
		} else {
			return -1;
		}
	}

public boolean focusOnParent() {
		
		return PreferenceManager.getDefaultSharedPreferences(mContext)
		.getBoolean(PREF_NAVIGATE_FOCUS_ON_PARENT, true);
	}

 

public boolean useBackNavigation() {
	Editor editor = prefs.edit();
	boolean back = prefs.getBoolean(PREF_USE_BACK_BUTTON, false);
	editor.commit();
	return  back;
}

	
}