package com.polimi.mobileuse.model.phoneReport;

public class Message extends LogItemAbstract{

    public Message(int id, long date, int type, int number, int smartphoneSession){
        super(id, date, type, number, smartphoneSession);
    }

    public String toString(){

        return "Message Details: " +
                "\n ID: "          + id              +
                "\n Data: "        + getDateString() +
                "\n Ora: "         + getTimeString() +
                "\n Numero: "      + number          +
                "\n Tipo: "        + typeEnum        ;


    }
}
