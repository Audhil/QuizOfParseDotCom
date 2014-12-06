package com.mns.quiz;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.receiver.NetworkUtil;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SplashScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }
		Thread splashTread = new Thread() {
			private Intent intent;
		 

			@Override
			public void run() {

				intent=new Intent(SplashScreen.this,  MainActivity.class);
				try {
					String sessionToken = QuizPreference.getInstance().getUserSessionPrefence();
					Debug.print("sessionToken:"+sessionToken);
					if(NetworkUtil.getConnectivityStatusString(getApplicationContext())){
					Debug.print("Network Connect");
						if (sessionToken != null) {
							try {
								ParseUser user = ParseUser.become(sessionToken);
								 QuizApplication.setUser(user);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}
					if (QuizApplication.getUser() == null) {
						Debug.print("Non Network Connect");
						@SuppressWarnings("deprecation")
						ParseQuery<ParseUser> query = ParseQuery.getUserQuery().fromPin();
						query.whereEqualTo("sessionToken", sessionToken);
						try {
							List<ParseUser> objects = query.find();
							if (objects != null && objects.size() > 0) {
								for (ParseUser user_ : objects) {
									Debug.print(user_.getUsername());
									QuizApplication.setUser(user_);								}
							}

						} catch (ParseException e) {

						}

					}	
					sleep(1000);
					startActivity(intent);
					finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		splashTread.start();
    }

    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
		this.registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	unregisterReceiver(mConnReceiver);
    	super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

            return rootView;
        }
    }
    
	private boolean mNetworkConnected;
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Debug.print("onReceiver");
			NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			mNetworkConnected = ni != null && ni.isConnected();
			boolean newWifiConnected = mNetworkConnected && ni.getTypeName().equalsIgnoreCase("wifi");
 
			 
		}
		 
	};

}
