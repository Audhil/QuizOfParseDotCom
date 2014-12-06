package com.mns.quiz.view;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SoftKeyBoard {
	
	private KeyboardView mKeyboardView;
	private Activity mActivity;
	
	private OnKeyboardActionListener onKeyboardActionListener = new OnKeyboardActionListener() {
		
		public final static int BackSpaceCodeDelete   = -5;
		public final static int KeyBoardCodeCancel   = -3;
        public final static int CodeSpace    = 55004;
        public final static int CodeDelete   = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel   = -3; // Keyboard.KEYCODE_CANCEL
        public final static int CodePrev     = 55000;
        public final static int CodeAllLeft  = 55001;
        public final static int CodeLeft     = 55002;
        public final static int CodeRight    = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext     = 55005;
        public final static int CodeClear    = 55006;
        
		@Override
		public void swipeUp() {
			
		}
		
		@Override
		public void swipeRight() {
			
		}
		
		@Override
		public void swipeLeft() {
			
		}
		
		@Override
		public void swipeDown() {
			
		}
		
		@Override
		public void onText(CharSequence text) {
			
		}
		
		@Override
		public void onRelease(int primaryCode) {
			
		}
		
		@Override
		public void onPress(int primaryCode) {
			
		}
		
		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
		/*	View focuscurrent = mActivity.getWindow().getCurrentFocus();
			
			if( focuscurrent==null || focuscurrent.getClass()!= EditText.class ) 
				return;
			
			EditText edittext = (EditText) focuscurrent;
			Editable editable = edittext.getText();
			int start = edittext.getSelectionStart();
			
			if(primaryCode == BackSpaceCodeDelete){
				if( editable!=null && start>0 ) 
					editable.delete(start - 1, start);
			}else if(primaryCode == KeyBoardCodeCancel){
				hideCustomKeyboard();
			}else {
				editable.insert(start, Character.toString((char) primaryCode));
			}*/
			
			   // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable
            View focusCurrent = mActivity.getWindow().getCurrentFocus();
            if( focusCurrent==null || focusCurrent.getClass()!=EditText.class ) return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            // Apply the key to the edittext
        	if(primaryCode == BackSpaceCodeDelete){
				if( editable!=null && start>0 ) 
					editable.delete(start - 1, start);
			}else  if( primaryCode==CodeCancel ) {
                hideCustomKeyboard();
            } else if( primaryCode==CodeDelete ) {
                if( editable!=null && start>0 ) editable.delete(start - 1, start);
            } else if( primaryCode==CodeClear ) {
                if( editable!=null ) editable.clear();
            } else if( primaryCode==CodeLeft ) {
                if( start>0 ) edittext.setSelection(start - 1);
            } else if( primaryCode==CodeRight ) {
                if (start < edittext.length()) edittext.setSelection(start + 1);
            } else if( primaryCode==CodeAllLeft ) {
                edittext.setSelection(0);
            } else if( primaryCode==CodeAllRight ) {
                edittext.setSelection(edittext.length());
            } else if( primaryCode==CodePrev ) {
                View focusNew= edittext.focusSearch(View.FOCUS_BACKWARD);
                if( focusNew!=null ) focusNew.requestFocus();
            } else if( primaryCode==CodeNext ) {
                View focusNew= edittext.focusSearch(View.FOCUS_FORWARD);
                if( focusNew!=null ) focusNew.requestFocus();
            } else { // insert character
            	
                editable.insert(start, Character.toString((char) primaryCode).toUpperCase());
            }
		}

	};
	
	public SoftKeyBoard(Activity host, int viewid, int layoutid){
		mActivity = host;
		mKeyboardView = (KeyboardView) mActivity.findViewById(viewid);
		mKeyboardView.setKeyboard(new Keyboard(mActivity, layoutid));
		mKeyboardView.setPreviewEnabled(false);
		mKeyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public SoftKeyBoard(Activity host,KeyboardView keyboardView, int layoutid){
		mActivity = host;
		mKeyboardView =keyboardView;
		mKeyboardView.setKeyboard(new Keyboard(mActivity, layoutid));
		mKeyboardView.setPreviewEnabled(false);
		mKeyboardView.setOnKeyboardActionListener(onKeyboardActionListener);
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public void hideCustomKeyboard() {
		mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
        
	}
	
	public boolean isCustomKeyboardVisible(){
		return mKeyboardView.getVisibility() == View.VISIBLE;
	}
	
	public void showCustomKeyBoard(View v){
		mKeyboardView.setVisibility(View.VISIBLE);
		mKeyboardView.setEnabled(true);
		if( v!=null ) 
			((InputMethodManager)mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	public void registerEditText(EditText edittext){
		/*EditText edittext= (EditText)mActivity.findViewById(resid);
		
		edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if( hasFocus ) 
					showCustomKeyBoard(v); 
				else 
					hideCustomKeyboard();
			}
		});
		
		edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v) {
            	showCustomKeyBoard(v);
            }
        });
		
		edittext.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.setInputType(inType);              // Restore input type
                return edittext.onTouchEvent(event); // Consume touch event
            }
        });
		
		edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);*/
		
		
		 // Find the EditText 'resid'
//        EditText edittext= (EditText)mActivity.findViewById(resid);
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if( hasFocus ) showCustomKeyBoard(v); else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override public void onClick(View v) {
                showCustomKeyBoard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}
	
	
}
