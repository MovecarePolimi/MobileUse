package com.polimi.mobileuse.logic.insoles;

import android.content.Context;

import com.polimi.mobileuse.dao.DaoGaitSession;
import com.polimi.mobileuse.dao.DaoInsoles;
import com.polimi.mobileuse.model.insoles.Insoles;
import com.polimi.mobileuse.model.insoles.InsolesHeader;
import com.polimi.mobileuse.model.insoles.InsolesRawData;
import com.polimi.mobileuse.model.insoles.InsolesRawHeader;

import java.util.List;

/* Thread Safe Class */
public class InsolesManager {
    // Multiple threads access to the class variables
    private static final String TAG = InsolesManager.class.getSimpleName();


    /* ****** GAIT SESSION ****** */
    public void saveGaitSession(Context context, InsolesHeader data){
        DaoGaitSession daoGaitSession = new DaoGaitSession(context);
        try {
            daoGaitSession.storeGaitSession(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<InsolesHeader> getGaitSessionByDay(Context context, long dateMillis){
        DaoGaitSession daoGaitSession = new DaoGaitSession(context);
        return daoGaitSession.retrieveGaitSessionByDay(dateMillis);
    }

    /* ****** RAW DATA ****** */
    public void saveRawData(Context context, InsolesRawData insolesRawData){
        DaoInsoles daoInsoles = new DaoInsoles(context);
        try {
            daoInsoles.storeRawData(insolesRawData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRawHeader(Context context, InsolesRawHeader rawHeader){
        DaoInsoles daoInsoles = new DaoInsoles(context);
        try {
            daoInsoles.storeRawHeader(rawHeader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ****** INSOLES ****** */
    public void saveInsoles(Context context, Insoles insoles){
        DaoInsoles daoInsoles = new DaoInsoles(context);
        try {
            daoInsoles.storeInsolesData(insoles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Insoles> getInsolesDataByDay(Context context, long dateMillis){
        DaoInsoles daoInsoles = new DaoInsoles(context);
        return daoInsoles.retrieveInsolesDataByDay(dateMillis);
    }

    public void closeDatabase(Context context){
        DaoInsoles daoInsoles = new DaoInsoles(context);
        daoInsoles.closeConnection();
    }
}
