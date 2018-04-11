package com.polimi.mobileuse.model.phoneReport;

public class PeopleUseCall extends PeopleUse {

    private int duration;

    public PeopleUseCall(String log, int number, int total, String type, int duration){
        super(log, number, total, type);
        this.duration = duration;
    }

    public PeopleUseCall(){};

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
