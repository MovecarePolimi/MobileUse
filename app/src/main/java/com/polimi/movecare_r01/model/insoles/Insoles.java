package com.polimi.movecare_r01.model.insoles;

import java.util.List;

public class Insoles {

    private long timestamp;
    private List dataLeft;
    private List dataRight;

    public Insoles(long timestamp, List dataLeft, List dataRight){
        this.timestamp = timestamp;
        this.dataLeft = dataLeft;
        this.dataRight = dataRight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List getDataLeft() {
        return dataLeft;
    }

    public List getDataRight() {
        return dataRight;
    }

    @Override
    public String toString(){
        return "ts: " + timestamp + " ," +
                ((dataLeft==null) ? "LEFT all null" : dataLeft.toString()) + " ," +
                ((dataRight==null)? "RIGHT all null" : dataRight.toString());
    }
}
