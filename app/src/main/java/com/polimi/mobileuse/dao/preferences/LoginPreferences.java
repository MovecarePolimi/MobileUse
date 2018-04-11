package com.polimi.mobileuse.dao.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by steto on 28/03/2018.
 */

public class LoginPreferences {

    private final String TAG = this.getClass().getSimpleName();

    private static final String LOGIN_SETTINGS_FILE         = "LoginSettings";

    private static final String ACCESS_TOKEN_STRING         = "access_token";
    private static final String TOKEN_TYPE_STRING           = "token_type";
    private static final String REFRESH_TOKEN_STRING        = "refresh_token";
    private static final String EXPIRES_IN_STRING           = "expires_in";
    private static final String JTI_STRING                  = "jti";

    private static final String USERNAME_STRING             = "username";
    private static final String UUID_STRING                 = "uuid";
    private static final String EMAIL_STRING                = "email";

    public enum Variable{
        ACCESS_TOKEN,
        TOKEN_TYPE,
        REFRESH_TOKEN,
        EXPIRES_IN,
        JTI,
        USERNAME,
        UUID,
        EMAIL,
        TEST_TOKEN
    }

    public void storeVariable(Context context, Variable var, String value) {
        if(value == null || value.isEmpty() || value.equals("")){
            Log.e(TAG, "SetValue: received null or empty value");
            return;
        }

        String name = detectNameFromVariable(var);

        SharedPreferences settings = context.getSharedPreferences(LOGIN_SETTINGS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(name, value);
        editor.apply();

        Log.v(TAG, "Set variable ");
    }


    public String getVariable(Context context, Variable var) {
        SharedPreferences settings = context.getSharedPreferences(LOGIN_SETTINGS_FILE, Context.MODE_PRIVATE);

        String name = detectNameFromVariable(var);

        String x = settings.getString(name, null);

        return x;
    }

    private String detectNameFromVariable(Variable var){
        switch (var){
            case ACCESS_TOKEN:
                return ACCESS_TOKEN_STRING;
            case TOKEN_TYPE:
                return TOKEN_TYPE_STRING;
            case REFRESH_TOKEN:
                return REFRESH_TOKEN_STRING;
            case EXPIRES_IN:
                return EXPIRES_IN_STRING;
            case JTI:
                return JTI_STRING;
            case USERNAME:
                return USERNAME_STRING;
            case UUID:
                return UUID_STRING;
            case EMAIL:
                return EMAIL_STRING;
            case TEST_TOKEN:
                return "test_token";
            default:
                throw new AssertionError("Unknown variable "+this);
        }
    }
}
