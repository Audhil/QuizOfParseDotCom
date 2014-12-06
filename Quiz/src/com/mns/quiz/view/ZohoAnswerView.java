package com.mns.quiz.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class ZohoAnswerView extends EditText implements TextWatcher {

	private String answer;
	private String userAnswer;

	private ZohoAnswerView(Context context) {
		this(context,null);

	}
	 
	public ZohoAnswerView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public ZohoAnswerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setAnswer(String answer){
		this.answer=answer;
	}
	
	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public String getAnswer() {
		return answer;
	}


	@Override
	public void afterTextChanged(Editable s) {

	}

	public interface PinEntryDelegate {
		// callbacks for creation
		public void didCreatePin(String createdPin);

		public void didFailToCreatePin();

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
