package com.polimi.movecare_r01.model.insoles;


import java.util.List;

public class InsolesRawData {

    private Long timestamp;
    private Integer msgDefinitionLeft;
    private Integer msgDefinitionRight;
    private String dataLeft;
    private String dataRight;

    public InsolesRawData(Long ts, Integer msgDefeft, Integer msgDefRight, String dataLeft, String dataRight){
        this.timestamp = ts;
        this.msgDefinitionLeft = msgDefeft;
        this.msgDefinitionRight = msgDefRight;
        this.dataLeft = dataLeft;
        this.dataRight = dataRight;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getMsgDefinitionLeft() {
        return msgDefinitionLeft;
    }

    public String getDataLeft() {
        return dataLeft;
    }

    public Integer getMsgDefinitionRight() {
        return msgDefinitionRight;
    }

    public String getDataRight() {
        return dataRight;
    }
}
