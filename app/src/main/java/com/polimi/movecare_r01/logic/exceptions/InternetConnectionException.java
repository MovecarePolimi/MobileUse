package com.polimi.movecare_r01.logic.exceptions;

public class InternetConnectionException extends Exception {

    public InternetConnectionException(){
        super("Internet not available");
    }

}
