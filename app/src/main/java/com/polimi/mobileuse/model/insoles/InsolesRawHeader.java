package com.polimi.mobileuse.model.insoles;


public class InsolesRawHeader {

    private Double sampleRate;

    private Integer rightID;
    private Integer rightSensor;

    private Integer leftID;
    private Integer leftSensor;

    public InsolesRawHeader(Double sampleRate, Integer leftID, Integer leftSensor, Integer rightID, Integer rightSensor) {
        this.sampleRate = sampleRate;
        this.leftID = leftID;
        this.leftSensor = leftSensor;
        this.rightID = rightID;
        this.rightSensor = rightSensor;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public int getRightID() {
        return rightID;
    }

    public int getRightSensor() {
        return rightSensor;
    }

    public int getLeftID() {
        return leftID;
    }

    public int getLeftSensor() {
        return leftSensor;
    }

    @Override
    public String toString(){
        return "Sample Rate: "      + sampleRate    +
                ", Left ID: "       + leftID        +
                ", Left Sensor: "   + leftSensor    +
                ", Right ID: "      + rightID       +
                ", Right Sensor: "  + rightSensor   ;
    }
}
