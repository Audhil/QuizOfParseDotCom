package com.mns.quiz.uitity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.mns.quiz.R;
import com.mns.quiz.api.ParseApi;
import com.mns.quiz.parse.module.Clue;
import com.parse.ParseException;

public class GenericTextWatcher implements TextWatcher {

	private View view;
	private Clue clue;
	private GradientDrawable drawable;
	private AnswerStatusListener listerner;
	private boolean correct;

	public GenericTextWatcher(View view, Clue clue,AnswerStatusListener listener) {
		
		this.view = view;
		Clue clue_ = ParseApi.getInstance().getClue(clue.getObjectId());//DatabaseManager.getInstance().getClue(clue.getId());
		// ParseQuery.getQuery(Clue.class).get(clue.getObjectId());
		 
		this.clue = clue_ != null ? clue_ : clue;
		clue_=null;
		((EditText) this.view).setText(this.clue.getUserSolution());
		((EditText) this.view).setSelection(((EditText) this.view).getText().length());
		this.view.setBackgroundResource(R.drawable.round_corner_edit_text);
		drawable = (GradientDrawable) this.view.getBackground();
		drawable.setStroke(2, Color.BLACK);
		correct = false;
		updateChanges(((EditText) this.view).getText().toString());
		this.listerner = listener;
	}

	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {
	}

	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	}

	public void afterTextChanged(final Editable editable) {

		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
					
				updateChanges(editable.toString());
			}
		}, 100);
	}

	private void updateChanges(String text) {
		if(clue!=null){
		clue.setUserSolution(text.toString());
		
		try {
			clue.pin();
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		DatabaseManager.getInstance().addClueDetails(clue);
		
		if (clue.getAnswer().equals(text)) {
			drawable.setStroke(2, Color.BLUE);
			if (correct)
				listerner.answerUpdate(this.clue);
		} else if (text.length() > 0) {
			drawable.setStroke(2, Color.RED);
		} else {
			drawable.setStroke(2, Color.BLACK);
		}
		correct = true;
		}
	}

	public interface AnswerStatusListener {

		public void answerUpdate(Clue clue);

	}
}