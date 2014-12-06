package com.mns.quiz.api;

import com.parse.ParseException;

public interface ApiCallback<T> {

    void onCompleted(T result);
    void onException(ParseException ex);
}
