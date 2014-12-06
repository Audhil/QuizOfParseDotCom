package com.mns.quiz.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mns.quiz.Debug;
import com.mns.quiz.MainActivity;
import com.mns.quiz.QuizApplication;
import com.mns.quiz.R;
import com.mns.quiz.parser.preference.QuizPreference;
import com.mns.quiz.uitity.Constants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;



@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class LoginFragment extends Fragment implements OnClickListener{

   
	private LayoutInflater mInflater;
 
	private String LOG_TAG=LoginFragment.class.getName();
	private EditText usernameField;
	private EditText passwordField;
	private Button parseLoginButton;
	private Button parseSignupButton;

	private MainActivity mContext;
  
 // newInstance constructor for creating fragment with arguments
    public static LoginFragment newInstance() {
    	LoginFragment fragmentFirst = new LoginFragment();
        Bundle args = new Bundle();
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
    
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        mContext=(MainActivity) getActivity();
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.login_form, container,false);
		 usernameField = (EditText)view. findViewById(R.id.login_username_input);
//		 usernameField.setText("surendran");
		 passwordField = (EditText) view. findViewById(R.id.login_password_input);
//		 passwordField.setText("surendran");
		 parseLoginButton = (Button) view. findViewById(R.id.parse_login_button);
		 parseSignupButton = (Button) view. findViewById(R.id.parse_signup_button);
		 parseLoginButton.setOnClickListener(this);
		 parseSignupButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.parse_login_button:
			
			String username = usernameField.getText().toString();
			String password = passwordField.getText().toString();
			if (username != null && TextUtils.isEmpty(username)) { 
				Debug.print("user Error");
				clearError();
				usernameField.setError(username.length()>0?getActivity().getResources().getString(R.string.invalid_username):getActivity().getResources().getString(R.string.enter_username));
			}else if (password != null && TextUtils.isEmpty(password)) { 
				Debug.print("password  Error");
				clearError();
				passwordField.setError(getActivity().getResources().getString(R.string.invalid_password));
			}else{
				Debug.print("Login");
				clearError();
				mContext.onLoadingStart(null);
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {
							@Override
							public void done(ParseUser user,ParseException e) {
								if (user != null) {
//									Toast.makeText(getActivity(), "LoginSuccess", Toast.LENGTH_SHORT).show();
									mContext.onLoadingFinish();
									usernameField.setText("");
									passwordField.setText("");
									onLoginSuccess();
									try {
										user.saveEventually();
										user.pin();
									} catch (ParseException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
					 
									QuizApplication.setUser(user);
									String token = user.getSessionToken();
									QuizPreference.getInstance().setUserSessionPrefence(token);
									user.pinInBackground();
								
								} else {
									mContext.onLoadingFinish();
									passwordField.selectAll();
									passwordField.requestFocus();
									switch(e.getCode()){
									case ParseException.USERNAME_TAKEN:
										Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.exist_username),Toast.LENGTH_SHORT).show();
									  break;
									case ParseException.USERNAME_MISSING:
										Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.exception_user_to_register),Toast.LENGTH_SHORT).show();
									  break;
									case ParseException.PASSWORD_MISSING:
										Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.exception_password_to_register),Toast.LENGTH_SHORT).show();
									  break;
									case ParseException.OBJECT_NOT_FOUND:
										Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
									default:
										Toast.makeText(getActivity(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
									}
									
								}
							}
						});
			}
			break;
		case R.id.parse_signup_button:
			mContext.switchToRegistrationViewFragment();
			break;

		default:
			break;
		}
	}
	
	public void onLoginSuccess() {
		((MainActivity)getActivity()).switchToMainViewFragment();
	}
	
	private void clearError(){
		usernameField.setError(null);
		passwordField.setError(null);
	}
	
	public void updateActionBar() {
	 
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		mContext.mMenuPlay.setVisible(false);
		mContext.mMenuLogout.setVisible(false);
		getActivity().getActionBar().setTitle("Login");
	
	}
}
