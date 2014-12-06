package com.mns.quiz.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mns.quiz.Debug;
import com.mns.quiz.R;
import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.uitity.Util;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class FileBrowseManager extends ActionBarActivity{
	private static final String CURRENT_DIR_DIR = "current-dir";
	private File currentDir;
	private ListView mListView;
	private List<com.mns.quiz.browser.FileListEntry> files;
	private com.mns.quiz.browser.BrowserFileListAdapter adapter;
	private boolean isPicker;
	private File previousOpenDirChild;
	private boolean excludeFromMedia;
	private String[] gotoLocations;
	private ArrayAdapter<CharSequence> mSpinnerAdapter;
	private boolean focusOnParent;
	private ActionMode mCurrentActionMode;
	private BrowserActionsCallback mFileActionCallBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initGotoLocations();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_browser_layout);
		mListView=(ListView)findViewById(R.id.file_browser_listview);
		prepareActionBar();
		initRootDir(savedInstanceState);
		files = new ArrayList<FileListEntry>();

		initFileListView();
		focusOnParent = QuizPreference.getInstance().focusOnParent();
		listContents(currentDir);
		mFileActionCallBack=new BrowserActionsCallback(FileBrowseManager.this) {
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				Debug.print("onDistroy");
				mCurrentActionMode = null;
				mListView.setEnabled(true);
			}
		};
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		 
			inflater.inflate(R.menu.file_browser_main, menu);
		 
		return true;
		
	}

 @Override
public void onOptionsMenuClosed(Menu menu) {
	super.onOptionsMenuClosed(menu);
}
 
 
 @Override
protected void onActivityResult(int arg0, int arg1, Intent arg2) {
	// TODO Auto-generated method stub
	super.onActivityResult(arg0, arg1, arg2);
}
 
	
 
	private void initRootDir(Bundle savedInstanceState) {
		// If app was restarted programmatically, find where the user last left
		// it
		String restartDirPath = getIntent().getStringExtra("FOLDER");
		
		if (restartDirPath != null) 
		{
			File restartDir = new File(restartDirPath);
			if (restartDir.exists() && restartDir.isDirectory()) {
				currentDir = restartDir;
				getIntent().removeExtra("FOLDER");
			}
		}
		else if (savedInstanceState!=null && savedInstanceState.getSerializable(CURRENT_DIR_DIR) != null) {
			
			currentDir = new File(savedInstanceState
					.getSerializable(CURRENT_DIR_DIR).toString());
		} 
		else 
		{ 
			currentDir = QuizPreference.getInstance().getStartDir();
		}		
		
		Debug.print("currentDir-->"+currentDir);
	}
	
	private void initFileListView() {
		 
		adapter = new BrowserFileListAdapter(this, files);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mListView.isClickable()) {
					FileListEntry file = (FileListEntry) mListView
							.getAdapter().getItem(position);
					select(file.getPath());
				}
			}

		});

		registerForContextMenu(mListView);		
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putSerializable(CURRENT_DIR_DIR, currentDir.getAbsolutePath());

	}
	
	
	void select(File file) {
		if (Util.isProtected(file)){
			new Builder(this).setTitle(getString(R.string.access_denied)).setMessage(
							getString(R.string.cant_open_dir, file.getName()))
					.show();
		} else if (file.isDirectory()) {
			
			listContents(file);
			
		} else {
			doFileAction(file);
		}
	}

	private void doFileAction(File file) {
		if (Util.isProtected(file) || file.isDirectory()) {
			return;
		}
		
		if(isPicker)
		{
			pickFile(file);
			return;
		}
		else
		{
//			openFile(file);
			return;
		}
	}
	
	private void pickFile(File file) {
//		setResult(Activity.RESULT_OK, fileAttachIntent);
		finish();
		return;
	}
	
	public void listContents(File dir)
	{
		listContents(dir, null);
	}
	public void listContents(File dir, File previousOpenDirChild) {
		Debug.print("listContents");
		if (!dir.isDirectory() || Util.isProtected(dir)) {
			return;
		}
		if(previousOpenDirChild!=null)
		{
			this.previousOpenDirChild = new File(previousOpenDirChild.getAbsolutePath());
		}
		else
		{
			this.previousOpenDirChild = null;
		}
		new Finder(this).execute(dir);
	}

	private void gotoParent() {

		if (Util.isRoot(currentDir)) {
			// Do nothing finish();
			finish();
		} else {
			listContents(currentDir.getParentFile(), currentDir);
		}
	}


	 
	
private void initGotoLocations() {
		
		gotoLocations = getResources().getStringArray(R.array.goto_locations);
	}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
private void prepareActionBar() {
	final ActionBar actionBar = getActionBar();
	actionBar.setDisplayHomeAsUpEnabled(true);
	actionBar.setDisplayShowTitleEnabled(true);
	
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
private OnNavigationListener getActionbarListener(final ActionBar actionBar) {
	return new OnNavigationListener() {
		
		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {
			
			int selectedIndex = actionBar.getSelectedNavigationIndex();
			
			if(selectedIndex == 0)
			{
				return false;
			}
			 
			
			return true;
		}

	};
}


	public synchronized void setCurrentDirAndChilren(File dir, FileListing folderListing) {
		currentDir = dir;
Debug.print("-----setCurrentDirAndChilren------------------------->");
		List<FileListEntry> children = folderListing.getChildren();
		excludeFromMedia   = folderListing.isExcludeFromMedia();
//		TextView emptyText = (TextView) findViewById(android.R.id.empty);
//		if (emptyText != null) {
//			emptyText.setText(R.string.empty_folder);
//		}
		files.clear();
		Debug.print("-----child:"+children.size());
		files.addAll(children);
	    adapter.updateList();
		adapter.notifyDataSetChanged();
//		getActionBar().setSelectedNavigationItem(0);
		
		if(Util.isRoot(currentDir))
		{
			gotoLocations[0] = getString(R.string.filesystem);	
		}
		else
		{
			gotoLocations[0] = currentDir.getName();
		}
		
		if(previousOpenDirChild!=null && focusOnParent)
		{
			int position = files.indexOf(new FileListEntry(previousOpenDirChild.getAbsolutePath()));
			if(position>=0)
			mListView.setSelection(position);
		}
		else
		{
			mListView.setSelection(0);
		}
		
		ActionBar ab = getActionBar();
		
		if(Util.isRoot(currentDir) || currentDir.getParentFile()==null)
    	{
			ab.setDisplayHomeAsUpEnabled(false);
			ab.setTitle(getString(R.string.filesystem));
    	}
    	else
    	{
    		ab.setTitle(currentDir.getName());
    		ab.setDisplayHomeAsUpEnabled(true);
    	}
		ab.setTitle(currentDir.getAbsolutePath());
	}
	
	
	@Override
	public void onBackPressed() {
		 
			gotoParent();

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case android.R.id.home:
			Debug.print("onBackpressed");
			gotoParent();
			return true;

		case R.id.action_close:
			finish();
			
		default:
			super.onOptionsItemSelected(item);
			break;
		}

		return true;
	}


	

	public void updateActionBar(HashSet<FileListEntry> selectedFiles) {
//		   android:uiOptions="splitActionBarWhenNarrow"
		getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		if(mCurrentActionMode==null){
			mCurrentActionMode=startActionMode(mFileActionCallBack);
			mFileActionCallBack.updateActionMode(selectedFiles);
		}else{
			if (selectedFiles.size() > 0) {
				mFileActionCallBack.updateActionMode(selectedFiles);

			} else {
				mCurrentActionMode.finish();
				mFileActionCallBack.onDestroyActionMode(null);
			}
		}
		 
	}




	public void callBackActivity() {
		mCurrentActionMode.finish();
		ArrayList<String> fileList = new ArrayList<String>();
		for(FileListEntry entry:adapter.selectedFiles){
			fileList.add(entry.getPath().getAbsolutePath());
		}
	 
		Intent returnIntent = new Intent();
		returnIntent.putStringArrayListExtra("Files",fileList);
		setResult(RESULT_OK,returnIntent);
		finish();
		
		
		
	}




	public void updateFileListAdapter() {
		Debug.print("update File List Adapter");
		
		mCurrentActionMode.finish();
		mFileActionCallBack.onDestroyActionMode(null);
		
					adapter.selectedFiles=new HashSet<FileListEntry>();
					adapter.notifyDataSetChanged();
					  
		
	}

	
}
