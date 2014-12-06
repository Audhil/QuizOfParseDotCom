package com.mns.quiz.uitity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class EditTextWatcher implements TextWatcher {

	private View view;

	public EditTextWatcher(View view) {
		this.view = view;
	}

	public void beforeTextChanged(CharSequence charSequence, int i, int i1,
			int i2) {
	}

	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
	}

	public void afterTextChanged(Editable editable) {
		String text = editable.toString();
		CharSequence error = ((EditText) view).getError();
		if (error != null) {
			((EditText) view).setError(null);
		}
	}
}