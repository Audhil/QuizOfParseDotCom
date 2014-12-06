package com.mns.quiz;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.mns.quiz.api.ParseApi;
import com.mns.quiz.browser.FileBrowseManager;
import com.mns.quiz.fragment.LoginFragment;
import com.mns.quiz.fragment.MainViewFragment;
import com.mns.quiz.fragment.PlayFragment;
import com.mns.quiz.fragment.RegistrationFragment;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.parse.module.Puzzle;
import com.mns.quiz.parse.module.PuzzleClues;
import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.uitity.Constants;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "NewApi", "Recycle" })
public class MainActivity extends ActionBarActivity  /*implements SwipeRefreshLayout.OnRefreshListener*/ {

	private FragmentManager mFragmentManager;
	private ProgressDialog progressDialog;
	private ParseUser mUser;
	public MenuItem mMenuPlay;
	public MenuItem mMenuLogout;
	private FragmentTransaction mTransaction;
	private InputMethodManager inputManager;
 
	static String MODULES_KEY = "modules";
	private static final int REQUEST_CODE = 0;
	private static final String LOGIN_FRAGMENT = "LoginFragment";
	private static final String SIGNUP_FRAGMENT = "SignUpFragment";
	private static final String MAIN_FRAGMENT = "MainFragment";
	private static final String PLAY_FRAGMENT = "PlayFragment";
	public static HashMap<String, PuzzleClues> puzzleClues;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.ACTIONBAR_BG)));
		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		    inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mFragmentManager = this.getSupportFragmentManager();
		mFragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						try {
							if (mFragmentManager.getBackStackEntryCount() == 0) {
								finish();
							} else {
								 updateActionBar();
							}
						} catch (Exception ex) {

						}
					}
				});
		

		if(mMenuPlay!=null)
		mMenuPlay.setVisible(false);
		if(mMenuLogout!=null)
		mMenuLogout.setVisible(false);
		mUser= QuizApplication.getUser();
		updateScreen();
		 
	 
	}
	
	public void updateScreen(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (mUser!= null) {
					switchToMainViewFragment();
					if(mMenuPlay!=null)
						mMenuPlay.setVisible(true);
						if(mMenuLogout!=null)
						mMenuLogout.setVisible(true);
				} else {
					Debug.print(":)");
					switchToLoginViewFragment();
					
				}
				
			}
		});
		 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		Debug.print("on Main Menu ");
		  mMenuPlay= menu.findItem(R.id.action_play);
		   mMenuLogout=menu.findItem(R.id.action_logout);
		   updateActionBar();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Debug.print("onActivity Result"+requestCode);
		Debug.print("Intent data"+data);
		ArrayList<String> list=null;
		switch (requestCode) {
		case 1:
			if(data!=null){
				 list = data.getStringArrayListExtra("Files");
				 if(list!=null){
					 if (mFragmentManager.getBackStackEntryCount() > 0) {
							BackStackEntry backStackEntry = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1);

							if (backStackEntry != null) {
								String previousFragmentName = backStackEntry.getName();
								Fragment previousFragment = mFragmentManager.findFragmentByTag(previousFragmentName);
								if (previousFragment instanceof MainViewFragment) {
									((MainViewFragment)previousFragment).parseFile(list);
								}
							}
						}
				 }
			}
			 
			Debug.print("list--->"+list);
			
			break;

		default:
			break;
		}

		 
		
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		this.registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		ParseApi.getInstance().fetchConfigIfNeeded();
		super.onResume();
 
	}

	   protected void onPause() {
	      unregisterReceiver(mConnReceiver);
	      super.onPause();
	   }

	 
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_logout:
			Debug.print("-->logout");
			ParseUser.logOut();
			QuizApplication.setUser(null);
			QuizPreference.getInstance().clearUserPrefernce();
				Puzzle.unpinAllInBackground(new DeleteCallback() {
					
					@Override
					public void done(ParseException e) {
						Debug.print("Puzzle unpin:"+e);
					}
				});
				Clue.unpinAllInBackground(new DeleteCallback() {
					
					@Override
					public void done(ParseException e) {
						Debug.print("Clue unpin:"+e);						
					}
				});
				ParseUser.unpinAllInBackground(new DeleteCallback() {
					
					@Override
					public void done(ParseException e) {
						Debug.print("Parse User unpin:"+e);
					}
				});
			Debug.print("--->"+mFragmentManager.getBackStackEntryCount());
			if(mFragmentManager.getBackStackEntryCount()>0){
			
				for(int i=0;i<mFragmentManager.getBackStackEntryCount();i++){
					 
					BackStackEntry entry=mFragmentManager.getBackStackEntryAt(i);
					if(entry!=null){
						Fragment previousFragment = mFragmentManager.findFragmentByTag(entry.getName());
						if (previousFragment != null &&  !(previousFragment instanceof LoginFragment)) {
							
							mTransaction = mFragmentManager.beginTransaction();
							mTransaction.remove(previousFragment);
							mTransaction.commit();
							mFragmentManager.executePendingTransactions();
							if(mFragmentManager.getBackStackEntryCount()==1){
								switchToLoginViewFragment();
							}else{
								mFragmentManager.popBackStack();
							}
							Debug.print("Remove   Fragment"+mFragmentManager.getBackStackEntryCount());
						}
					}
					 
				} 
				
			}else{
				switchToLoginViewFragment();
			}
		
			return true;
		case R.id.action_play:
 
			switchToPlayFragment(null);
			
			 
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void switchToFragment(String fragmentName, Fragment fragment) {
		mTransaction = mFragmentManager.beginTransaction();

		if (mFragmentManager.getBackStackEntryCount() > 0) {
			BackStackEntry backStackEntry = mFragmentManager
					.getBackStackEntryAt(mFragmentManager
							.getBackStackEntryCount() - 1);
			if (backStackEntry != null) {
				String previousFragmentName = backStackEntry.getName();
				Fragment previousFragment = mFragmentManager
						.findFragmentByTag(previousFragmentName);
				if (previousFragment != null) {
					mTransaction.hide(previousFragment);
				}
			}
		}
		Debug.print("SwichtoMain"+fragment.getClass().getCanonicalName());
		mTransaction.replace(R.id.container, fragment, fragmentName);

		mTransaction.addToBackStack(fragmentName);

//		getActionBar().setDisplayHomeAsUpEnabled(fragmentName.equals(MAIN_FRAGMENT) ? false : true);
//		setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		try {
			mTransaction.commit();
			mFragmentManager.executePendingTransactions();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	public void switchToMainViewFragment() {
		Debug.print("switch to MainViewFra"+mFragmentManager.getBackStackEntryCount());
		hideKeyboard();
		if(mFragmentManager.getBackStackEntryCount()>1){
			mFragmentManager.popBackStack();
			
		}
		String fragmentName = String.format(MAIN_FRAGMENT);
		Fragment mainViewFragment = mFragmentManager.findFragmentByTag(fragmentName);
	 
		if (mainViewFragment == null) {
			mainViewFragment = new MainViewFragment();
			if (mainViewFragment != null&& mainViewFragment instanceof MainViewFragment) {
			}
		}
		
		switchToFragment(fragmentName, mainViewFragment);
	}
	
	public void switchToPlayFragment(String puzzleId) {
		
		String fragmentName = String.format(PLAY_FRAGMENT);
		Fragment playFragment = mFragmentManager
				.findFragmentByTag(fragmentName);
	 
		if (playFragment == null) {
			playFragment = new PlayFragment();
		}
		if (playFragment != null&& playFragment instanceof PlayFragment) {
			Bundle args = new Bundle();
			if(puzzleId!=null)
			args.putString(getResources().getString(R.string.puzzle_id), puzzleId);
			((PlayFragment) playFragment).setArguments(args);
		}
		switchToFragment(fragmentName, playFragment);
	}
	
	public void switchToLoginViewFragment() {
		String fragmentName = String.format(LOGIN_FRAGMENT);
		Fragment fragment = mFragmentManager
				.findFragmentByTag(fragmentName);
	 
		if (fragment == null) {
			fragment = LoginFragment.newInstance();
		}
		if (fragment != null&& fragment instanceof LoginFragment) {
			Debug.print("Login Fragment");
		}
		switchToFragment(fragmentName, fragment);
	}
	public void switchToRegistrationViewFragment() {
		String fragmentName = String.format(SIGNUP_FRAGMENT);
		Fragment fragment = mFragmentManager
				.findFragmentByTag(fragmentName);
	 
		if (fragment == null) {
			fragment = new RegistrationFragment();
			if (fragment != null
					&& fragment instanceof RegistrationFragment) {
			}
		}
		switchToFragment(fragmentName, fragment);
	}
	
	public void onLoadingStart(String msg) {
		 
			Debug.print("on load start");
			progressDialog = ProgressDialog.show(this, null, msg!=null?msg:"Loading", true,false);
		 
	}

	public void onLoadingFinish() {
		if (progressDialog != null) {
			Debug.print("on load Finish");
			progressDialog.dismiss();
		}
	}

	/**
	 * @see android.app.Activity#isDestroyed()
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public boolean isDestroyed() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return super.isDestroyed();
		}
		return false;
	}
	
public void getPuzFiles() {
	 
	Intent intent = new Intent(this,FileBrowseManager.class);
	startActivityForResult(intent,PICKFILE_RESULT_CODE);
	
	}
public static HashMap<String, PuzzleClues> getPuzzleClues() {
	return MainActivity.puzzleClues;
}



public static void setPuzzleClues(HashMap<String, PuzzleClues> puzzleClues) {
	MainActivity.puzzleClues = new HashMap<String, PuzzleClues>(puzzleClues);
}


public static final int PICKFILE_RESULT_CODE = 1;
protected static final int REQUEST_CODE_PICK_FILE_TO_OPEN = 1;
protected static final int REQUEST_CODE_PICK_FILE_TO_SAVE = 2;
protected static final int REQUEST_CODE_PICK_DIRECTORY = 3;

	
 

	
	
	
	
    
	 private void logout(){
		 // Call the Parse log out method
	        ParseUser.logOut();
	        // Start and intent for the dispatch activity
//	        Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
//	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//	        startActivity(intent);
	 }
 

	
	private boolean mNetworkConnected;
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			mNetworkConnected = ni != null && ni.isConnected();
			boolean newWifiConnected = mNetworkConnected && ni.getTypeName().equalsIgnoreCase("wifi");
		}
		 
	};
	
	
	public void onBackPressed() {
		Debug.print("mFragmentManager.getBackStackEntryCount() ==>"+mFragmentManager.getBackStackEntryCount() );
		if (mFragmentManager.getBackStackEntryCount() > 0) {
			BackStackEntry backStackEntry = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1);

			if (backStackEntry != null) {
				String previousFragmentName = backStackEntry.getName();
				Fragment previousFragment = mFragmentManager.findFragmentByTag(previousFragmentName);
				if (previousFragment instanceof MainViewFragment) {
					if(QuizApplication.getUser()!=null){
					
						mFragmentManager.popBackStack();
					}else{
					  switchToLoginViewFragment();	
					}
					
				}
				if (previousFragment instanceof LoginFragment) {
				finish();
				}
			}
		}
		
		super.onBackPressed();
		
	};
 
	private void updateActionBar(){
		if (mFragmentManager.getBackStackEntryCount() > 0) {
			BackStackEntry backStackEntry = mFragmentManager
					.getBackStackEntryAt(mFragmentManager
							.getBackStackEntryCount() - 1);
			if (backStackEntry != null) {
				String previousFragmentName = backStackEntry.getName();
				Fragment previousFragment = mFragmentManager
						.findFragmentByTag(previousFragmentName);
				if (previousFragment instanceof MainViewFragment) {
					((MainViewFragment) previousFragment).updateActionBar();
				} else if (previousFragment instanceof LoginFragment) {
					((LoginFragment) previousFragment).updateActionBar();

				} else if (previousFragment instanceof RegistrationFragment) {
					((RegistrationFragment) previousFragment).updateActionBar();
				}
				else if (previousFragment instanceof PlayFragment) {
					((PlayFragment) previousFragment).updateActionBar();
				}


			}
		}
	}
	public   void hideKeyboard()
    {
        try
        {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
         }
        catch (Exception e)
        {
        }
    }

}
