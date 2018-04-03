package com.polimi.movecare_r01.logic.observers;

public interface JsonSentObserverInterface {

    void update();

    void sendAgainUpdate(String timeMillis);
}
