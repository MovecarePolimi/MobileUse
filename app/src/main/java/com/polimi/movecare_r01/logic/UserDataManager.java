package com.polimi.movecare_r01.logic;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.polimi.movecare_r01.model.user.User;

public class UserDataManager {
    private static final String TAG = UserDataManager.class.getSimpleName();

    private Context context;

    public UserDataManager(Context context){
        this.context = context;
    }
    public void storeUserData(User user){
        Log.i(TAG, "Method storeUserData: start");

        if(user == null){
            Log.w(TAG, "Method storeUserData: received null Object");
            return;
        }
        //User u = (User) intent.getExtras().getSerializable("utente");

        Log.i(TAG,
                user.getName() +" - "+user.getSurname()+" - "+user.getPhoneNumber());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        Editor editor = settings.edit();
        editor.putString("name", user.getName());
        editor.putString("surname", user.getSurname());
        editor.putString("phone", user.getPhoneNumber());

        editor.apply();

        //sendInitializationIntent(RECEIVER_INDEX);


        Log.i(TAG, "Method storeUserData: end");
    }

    public void getUserData(){
        Log.i(TAG, "Method getUserData: start");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String nome = settings.getString("name","Nome NOT FOUND");
        String cognome = settings.getString("surname","Cognome NOT FOUND");
        String tel = settings.getString("phone","Tel NOT FOUND");

        Log.e(TAG, nome+" - "+cognome+" - "+tel);

        Log.i(TAG, "Method getUserData: end");
    }
}
