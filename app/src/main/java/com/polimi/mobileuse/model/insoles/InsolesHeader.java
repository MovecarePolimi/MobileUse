package com.polimi.mobileuse.model.insoles;

public class InsolesHeader {

    private double sampleRate;

    private int rightID;
    private int rightSensor;

    private int leftID;
    private int leftSensor;

    public InsolesHeader(Double sampleRate, Integer leftID, Integer leftSensor, Integer rightID, Integer rightSensor) {
        if(sampleRate != null){
            this.sampleRate = sampleRate;
        } else{
            this.sampleRate = -1;
        }

        if(rightID != null){
            this.rightID = rightID;
        } else{
            this.rightID = -1;
        }

        if(rightSensor != null){
            this.rightSensor= rightSensor;
        } else{
            this.rightSensor = -1;
        }

        if(leftID != null){
            this.leftID= leftID;
        } else{
            this.leftID = -1;
        }

        if(leftSensor != null){
            this.leftSensor= leftSensor;
        } else{
            this.leftSensor = -1;
        }
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
