package com.mns.quiz.browser;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mns.quiz.Debug;
import com.mns.quiz.R;

@SuppressLint("NewApi")
public abstract class BrowserActionsCallback implements Callback {

	private FileBrowseManager activity;
	private HashSet<FileListEntry> file;
	private ActionMode mActionMode;
	static int[] allOptions = { R.id.menu_cancel, R.id.menu_done };

	public BrowserActionsCallback(FileBrowseManager activity) {

		this.activity = activity;

	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {

	};

	@Override
	public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
		Debug.print("Action Item Clicked");
		switch (item.getItemId()) {

		case R.id.menu_cancel:
			activity.updateFileListAdapter();
			// mode.finish();
			break;
		case R.id.menu_done:
			activity.callBackActivity();
			break;
		}

		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

		Debug.print("onCreateActionMode");
		MenuInflater inflater = activity.getMenuInflater();
		mActionMode = actionMode;
		inflater.inflate(R.menu.context_menu, menu);

		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	public void updateActionMode(HashSet<FileListEntry> selectedFiles) {

		if (selectedFiles.size() == 0) {
			Debug.print("menu Null");
			mActionMode.finish();
			onDestroyActionMode(null);

		} else {
			mActionMode.setTitle("Selected " + selectedFiles.size());
		}
	}

}
