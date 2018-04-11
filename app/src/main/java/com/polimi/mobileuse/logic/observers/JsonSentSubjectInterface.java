package com.polimi.mobileuse.logic.observers;


public interface JsonSentSubjectInterface {

    void attach(JsonSentObserver jsonSentObserver);
    void detach(JsonSentObserver jsonSentObserver);
    void notifyAllObservers();
    void notifyAgainAllObservers(String timeMillis);

}
