package com.polimi.movecare_r01.logic.log;


import android.content.Context;
import android.util.Log;

import com.polimi.movecare_r01.dao.DaoLogs;
import com.polimi.movecare_r01.model.phoneReport.Call;
import com.polimi.movecare_r01.model.phoneReport.Message;
import com.polimi.movecare_r01.model.phoneReport.PeopleUseCall;
import com.polimi.movecare_r01.model.phoneReport.PeopleUseMessage;
import com.polimi.movecare_r01.model.phoneReport.SmartphoneUse;

import java.util.List;


public class LogsManager {
    private static final String TAG = LogsManager.class.getSimpleName();
    private DaoLogs daoLogs;

    public LogsManager(Context context){
        daoLogs = new DaoLogs(context);
    }


    /***** CALLS *****/
    public void saveCallList(List<Call> callList, long dateMillis){
        Log.v(TAG, "Method saveCallList: start");

        if(callList == null || callList.isEmpty()){
            Log.w(TAG, "Method saveCallList: received null or empty List");
            return;
        }

        List<Call> storedList = daoLogs.retrieveCallsByDay(dateMillis);

        if (storedList == null || storedList.isEmpty()){
            Log.i(TAG, "Method saveCallList: nothing stored yet");
            try {
                daoLogs.storeCallList(callList);
            } catch (Exception e) {
                Log.e(TAG, "Method saveCallList: DuplicatedID Exception");
                e.printStackTrace();
            }
        }
        else{
            Log.i(this.getClass().getSimpleName(), "Method saveCallList: something found, checking ID uniqueness");

            for(Call newCall : callList){
                boolean isDuplicated = false;
                for(Call storedCall : storedList){
                    if(newCall.getId() == storedCall.getId()){
                        isDuplicated = true;
                        Log.i(TAG, "Method saveCallList: ID NOT unique");
                        break;
                    }
                }
                if(!isDuplicated){
                    Log.i(TAG, "Method saveCallList: ID unique");
                    try {
                        daoLogs.storeCall(newCall);
                    } catch (Exception e) {
                        Log.e(TAG, "Method saveCallList: Exception Thrown");
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.v(TAG, "Method saveCallList: end");
    }

    public List<Call> getAllCalls(){
        return daoLogs.retrieveAllCalls();
    }

    public List<Call> getCallsByDate(long dateMillis){
        return daoLogs.retrieveCallsByDay(dateMillis);
    }


    /***** MESSAGES *****/
    public void saveMessageList(List<Message> messageList, long dateMillis){
        Log.v(TAG, "Method saveMessageList: start");

        if(messageList == null || messageList.isEmpty()){
            Log.w(TAG, "Method saveMessageList: received null or empty List");
            return;
        }

        List<Message> storedList = daoLogs.retrieveMessagesByDay(dateMillis);

        if (storedList == null || storedList.isEmpty()){
            Log.i(TAG, "Method saveMessageList: nothing stored yet");
            try {
                daoLogs.storeMessageList(messageList);
            } catch (Exception e) {
                Log.e(TAG, "Method saveMessageList: DuplicatedID Exception");
                e.printStackTrace();
            }
        }
        else{
            Log.i(TAG, "Method saveMessageList: something found, checking ID uniqueness");

            for(Message newMessage : messageList){
                boolean isDuplicated = false;
                for(Message storedMessage : storedList){
                    if(newMessage.getId() == storedMessage.getId()){
                        isDuplicated = true;
                        Log.i(TAG, "Method saveMessageList: ID NOT unique");
                        break;
                    }
                }
                if(!isDuplicated){
                    Log.i(TAG, "Method saveMessageList: ID unique");
                    try {
                        daoLogs.storeMessage(newMessage);
                    } catch (Exception e) {
                        Log.e(TAG, "Method saveMessageList: Exception Thrown");
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.v(TAG, "Method saveCallList: end");
    }

    public List<Message> getAllMessages(){
        return daoLogs.retrieveAllMessages();
    }

    public List<Message> getMessagesByDate(long dateMillis){
        return daoLogs.retrieveMessagesByDay(dateMillis);
    }

    /***** PEOPLE USE *****/
    public List<PeopleUseCall> getPeopleUseCall(long dateMillis){
        return daoLogs.retrievePeopleUseCall(dateMillis);
    }

    public List<PeopleUseMessage> getPeopleUseMessage(long dateMillis){
        return daoLogs.retrievePeopleUseMessage(dateMillis);
    }

    /***** SMARTPHONE USE *****/
    public SmartphoneUse getSmartphoneUse(long dateMillis){
        return daoLogs.retrieveSmartphoneUse(dateMillis);
    }



    public void closeDatabaseConnection(){
        daoLogs.closeConnection();
    }
}
