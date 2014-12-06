package com.mns.quiz.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mns.quiz.R;
import com.mns.quiz.listener.ConfigPagerListener;
import com.mns.quiz.parse.module.Clue;
import com.mns.quiz.uitity.Constants;
import com.mns.quiz.uitity.GenericTextWatcher;
import com.mns.quiz.uitity.GenericTextWatcher.AnswerStatusListener;
import com.mns.quiz.view.SoftKeyBoard;

public class ClueFragment extends Fragment implements AnswerStatusListener{

	private static Activity mContext;
	private static int mPosition;
	

	public ClueFragment() {
		
	}

	 

	private Clue mClue;
	private SoftKeyBoard mCustomKeyboard;
	private ConfigPagerListener mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
		mContext = getActivity();
		mClue = getArguments().getParcelable(Constants.CLUE);
		mPosition = getArguments().getInt(Constants.POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.clue_view, container, false);

		int pos = this.getArguments().getInt(Constants.POSITION);

		mClue = (Clue) this.getArguments().getSerializable(Constants.CLUE);

		TextView answer = (TextView) view.findViewById(R.id.answer_id);
		answer.setText(mClue.getAnswer());

		TextView clue = (TextView) view.findViewById(R.id.clue);
		clue.setText(mClue.getClue());
		EditText answerOfClue = (EditText) view.findViewById(R.id.answer_of_clue);
		if(mCustomKeyboard!=null)
		mCustomKeyboard.registerEditText(answerOfClue);
		answerOfClue.setSingleLine();
		answerOfClue.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mClue.getAnswer().length()) });
		answerOfClue.addTextChangedListener(new GenericTextWatcher(answerOfClue, mClue,this));
		answerOfClue.requestFocus();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		 outState.putSerializable(Constants.CLUE, mClue);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setKeyboard(SoftKeyBoard customKeyboard) {
		mCustomKeyboard = customKeyboard;
	}

	public void setListener(ConfigPagerListener listener) {
		mListener=listener;
		
	}

	@Override
	public void answerUpdate(Clue clue) {
		mListener.swipePagermode(this.getArguments().getInt(Constants.POSITION),clue);
	}

}
