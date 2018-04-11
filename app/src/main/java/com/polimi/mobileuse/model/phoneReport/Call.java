package com.polimi.mobileuse.model.phoneReport;


public class Call extends LogItemAbstract{

    private int duration;

    public Call(int id, long date, int duration, int type, int number, int smartphoneSession){
        super(id, date, type, number, smartphoneSession);
        setDuration(duration);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString(){

        return "Call Details: " +
                "\n ID: "       + id            +
                "\n Data: "     + getDateString() +
                "\n Ora: "      + getTimeString() +
                "\n Numero: "   + number        +
                "\n Durata: "   + duration      +
                "\n Tipo: "     + typeEnum      ;
    }

}
