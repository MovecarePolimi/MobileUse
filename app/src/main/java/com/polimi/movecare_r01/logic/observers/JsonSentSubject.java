package com.polimi.movecare_r01.logic.observers;


public class JsonSentSubject implements JsonSentSubjectInterface {

    private boolean state;
    private JsonSentObserver observer;

    public boolean getState() {
        return state;
    }

    public void setState(boolean state, String timeMillis) {
        this.state = state;
        if(timeMillis == null){
            notifyAllObservers();
        } else{
            notifyAgainAllObservers(timeMillis);
        }

    }

    @Override
    public void attach(JsonSentObserver jsonSentObserver) {
        observer = jsonSentObserver;
    }

    @Override
    public void detach(JsonSentObserver jsonSentObserver) {

    }

    @Override
    public void notifyAllObservers() {
        observer.update();
    }

    @Override
    public void notifyAgainAllObservers(String timeMillis) {
        observer.sendAgainUpdate(timeMillis);
    }

    public JsonSentObserver getObserver() {
        return observer;
    }

    public void setObserver(JsonSentObserver observer) {
        this.observer = observer;
    }
}
