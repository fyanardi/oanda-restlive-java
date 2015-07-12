package com.yanardi.oanda.rest;

import com.yanardi.oanda.data.RestError;

public class OANDARestException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RestError restError;

    public OANDARestException(RestError restError) {
        this.restError = restError;
    }

    public RestError getRestError() {
        return restError;
    }

    @Override
    public String getMessage() {
        return "Error " + restError.getCode() + ": " + restError.getMessage();
        
    }
}
