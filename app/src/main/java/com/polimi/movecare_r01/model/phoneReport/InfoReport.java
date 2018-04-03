package com.polimi.movecare_r01.model.phoneReport;

import java.util.List;

public class InfoReport {

    private List<Call> callList;
    private List<Message> messageList;
    private SmartphoneUse smartphoneUse;
    private List<PeopleUseCall> peopleUseCall;
    private List<PeopleUseMessage> peopleUseMessage;

    public InfoReport(List<Call> callList,
                      List<Message> messageList,
                      SmartphoneUse smartphoneUse,
                      List<PeopleUseCall> peopleUseCall,
                      List<PeopleUseMessage> peopleUseMessage){

        this.callList = callList;
        this.messageList = messageList;
        this.smartphoneUse = smartphoneUse;
        this.peopleUseCall = peopleUseCall;
        this.peopleUseMessage = peopleUseMessage;

    }

    public List<Call> getCallList() {
        return callList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public SmartphoneUse getSmartphoneUse() {
        return smartphoneUse;
    }

    public List<PeopleUseCall> getPeopleUseCall() {
        return peopleUseCall;
    }

    public List<PeopleUseMessage> getPeopleUseMessage() {
        return peopleUseMessage;
    }
}
