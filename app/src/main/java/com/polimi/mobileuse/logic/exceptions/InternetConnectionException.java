package com.polimi.mobileuse.logic.exceptions;

public class InternetConnectionException extends Exception {

    public InternetConnectionException(){
        super("Internet not available");
    }

}
