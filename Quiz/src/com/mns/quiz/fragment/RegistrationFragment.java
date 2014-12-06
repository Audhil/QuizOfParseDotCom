package com.mns.quiz.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.InputType;
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
import com.mns.quiz.R;
import com.mns.quiz.api.ParseApi;
import com.mns.quiz.uitity.Constants;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class RegistrationFragment extends Fragment implements OnClickListener{

    private MainActivity mContext;
	private LayoutInflater mInflater;
 
	private String LOG_TAG=RegistrationFragment.class.getName();
	private EditText usernameField;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private EditText emailField;
	private EditText nameField;
	private Button createAccountButton;
  
 // newInstance constructor for creating fragment with arguments
    public static RegistrationFragment newInstance() {
    	RegistrationFragment fragmentFirst = new RegistrationFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signup_form, container, false);

		usernameField = (EditText) view.findViewById(R.id.signup_username_input);
		passwordField = (EditText) view.findViewById(R.id.signup_password_input);
		confirmPasswordField = (EditText) view.findViewById(R.id.signup_confirm_password_input);
		emailField = (EditText) view.findViewById(R.id.signup_email_input);
		nameField = (EditText) view.findViewById(R.id.signup_name_input);
		createAccountButton = (Button) view.findViewById(R.id.create_account);

		usernameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		createAccountButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.create_account:
			
			String username = usernameField.getText().toString();
			String password = passwordField.getText().toString();
			String passwordAgain = confirmPasswordField.getText().toString();
			String email = emailField.getText().toString();
			String name = nameField.getText().toString();

			boolean error = false;
			clearError();
			if (username != null && TextUtils.isEmpty(username)) {
				usernameField.setError("");
			} else if (password != null && TextUtils.isEmpty(password)) {
				passwordField.setError("");
			} else if (passwordAgain != null
					&& TextUtils.isEmpty(passwordAgain)) {
				confirmPasswordField.setError("");
			} else if (!password.equals(passwordAgain)) {
				confirmPasswordField.setError(getActivity().getResources().getString(R.string.password_not_equal));
			} else if (email != null
					&& (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS
							.matcher(email).matches())) {
				emailField.setError("");
			} else if (name != null && TextUtils.isEmpty(name)) {
				nameField.setError("");
			} else {
				final ParseUser user = new ParseUser();

				// Set standard fields
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				ParseACL roleACL = new ParseACL();
				roleACL.setPublicReadAccess(true);
				mContext.onLoadingStart(null);
				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {

						if (e == null) {
							mContext.onLoadingFinish();
							onSucess();
							onSetRole(user,Constants.USER);
							Debug.print("Success");
						} else {
							mContext.onLoadingFinish();
							Debug.print("error");
							if (e != null) {

								switch (e.getCode()) {
								case ParseException.INVALID_EMAIL_ADDRESS:
									Toast.makeText(getActivity(),
											getResources().getString(R.string.invalid_email_address),
											Toast.LENGTH_SHORT).show();
									break;
								case ParseException.USERNAME_TAKEN:
									Toast.makeText(getActivity(),
											getResources().getString(R.string.username_taken),
											Toast.LENGTH_SHORT).show();
									break;
								case ParseException.EMAIL_TAKEN:
									Toast.makeText(getActivity(),
											getResources().getString(R.string.email_taken), Toast.LENGTH_SHORT)
											.show();
									break;
								default:
									Toast.makeText(getActivity(),
											e.getMessage(), Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					}
				});
			}
			
			break;

		default:
			break;
		}
	}

	protected void onSetRole(ParseUser user, String role) {
		 
		ParseApi.getInstance().getUsers(user,role);
		
	}

	protected void onSucess() {
		Toast.makeText(getActivity(), getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
		 ((MainActivity)getActivity()).onBackPressed();
	}
    
 
	
	public void clearError(){
		 
		 usernameField.setError(null,null);
		 passwordField.setError(null,null);
		 confirmPasswordField.setError(null,null);
		 emailField.setError(null,null);
		 nameField.setError(null,null);
	}
	public void updateActionBar() {
		 
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
 
		mContext.mMenuPlay.setVisible(false);
		mContext.mMenuLogout.setVisible(false);
		getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.register));
	
	}
	

 
}
