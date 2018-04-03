package com.polimi.movecare_r01.model.phoneReport;

public abstract class PeopleUse {

    private String log;      // enum: C o M
    private int number;    // last 4 digits
    private int total;
    private String type;   // enum: IN o OUT

    public PeopleUse(String log, int number, int total, String type){
        this.log = log;
        this.number = number;
        this.total = total;
        this.type = type;
    }

    public PeopleUse(){};

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
