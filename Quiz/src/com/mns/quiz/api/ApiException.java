package com.mns.quiz.api;

import java.net.URI;

import com.mns.quiz.api.ParseApi.ResponseStatus;


@SuppressWarnings("serial")
public class ApiException extends Exception {
    private ResponseStatus status;
    private URI uri;

    public ApiException(ResponseStatus status, URI uri) {
        this.uri = uri;
        this.status = status;
    }
    
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("Failure. Error from server: %s in call to %s", status.MessageText, uri.toString());
    }
}
