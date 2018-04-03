package com.polimi.movecare_r01.dao;

import android.provider.BaseColumns;

class DatabaseContract {
    static final  int    DATABASE_VERSION          = 2;
    static final  String DATABASE_NAME             = "MOVECARE_DB";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String INT_TYPE           = " INTEGER";
    private static final String REAL_TYPE          = " REAL";
    private static final String PRIMARY_KEY        = " PRIMARY KEY";
    private static final String AUTO_INCREMENT     = " AUTOINCREMENT";
    private static final String NULL               = " NULL";
    private static final String NOT_NULL           = " NOT NULL";
    private static final String DEFAULT            = " DEFAULT";
    private static final String COMMA              = ", ";
    private static final String LEFT_PAR           = " ( ";
    private static final String RIGHT_PAR          = " )";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {}


    /* ********** SMARTPHONE SESSION ********** */

    static abstract class SmartphoneSessionTable implements BaseColumns {
        static final String TABLE_NAME           = "SMARTPHONE_SESSION";
        static final String DATE_COL1            = "date";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                      + LEFT_PAR +
                _ID                 + INT_TYPE  + PRIMARY_KEY + AUTO_INCREMENT  + COMMA    +
                DATE_COL1           + TEXT_TYPE                                 + RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class CallTable implements BaseColumns {
        static final String TABLE_NAME                = "CALL";
        static final String DATE_COL1                 = "date";
        static final String DURATION_COL2             = "duration";
        static final String TYPE_COL3                 = "type";      //incoming VS missed VS outcoming
        static final String NUMBER_COL4               = "number";
        static final String SMARTPHONE_SESSION_COL5   = "smartphone_session";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                             + LEFT_PAR +
                _ID                         + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT  + COMMA    +
                DATE_COL1                   + INT_TYPE                                 + COMMA    +
                DURATION_COL2               + INT_TYPE                                 + COMMA    +
                TYPE_COL3                   + INT_TYPE                                 + COMMA    +
                NUMBER_COL4                 + INT_TYPE                                 + COMMA    +
                SMARTPHONE_SESSION_COL5     + INT_TYPE                                 +

                RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class MessageTable implements BaseColumns {
        static final String TABLE_NAME                = "MESSAGE";
        static final String DATE_COL1                 = "date";
        static final String TYPE_COL2                 = "type";       //incoming VS missed VS outcoming
        static final String NUMBER_COL3               = "number";
        static final String SMARTPHONE_SESSION_COL4   = "smartphone_session";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                              + LEFT_PAR +
                _ID                         + INT_TYPE  + PRIMARY_KEY + AUTO_INCREMENT  + COMMA    +
                DATE_COL1                   + INT_TYPE                                  + COMMA    +
                TYPE_COL2                   + INT_TYPE                                  + COMMA    +
                NUMBER_COL3                 + INT_TYPE                                  + COMMA    +
                SMARTPHONE_SESSION_COL4     + INT_TYPE                +
                //SMARTPHONE_SESSION_COL4     + INT_TYPE  + NOT_NULL    + COMMA    +
                //PRIMARY_KEY + LEFT_PAR + _ID + COMMA + SMARTPHONE_SESSION_COL4 +RIGHT_PAR +
                RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }



    /* ********** GAIT SESSION ********** */

    static abstract class GaitSessionTable implements BaseColumns {
        static final String TABLE_NAME                = "GAIT_SESSION";
        static final String DATE_COL1                 = "date";
        static final String SAMPLE_RATE_COL2          = "sample_rate";
        static final String INS_LEFT_ID_COL3          = "ins_left_id";
        static final String INS_LEFT_SENS_COL4        = "ins_left_sens";
        static final String INS_RIGHT_ID_COL5         = "ins_right_id";
        static final String INS_RIGHT_SENS_COL6       = "ins_right_sens";

        /*static final String DURATION_COL3      = "duration";
        static final String CADENCE_COL4       = "cadence";
        static final String FLAG1_COL5         = "insoles_status";           // enum
        static final String FLAG2_COL6         = "insoles_data_downloaded";  // boolean
        static final String FLAG3_COL7         = "insoles_battery_low";      // boolean
        static final String FLAG4_COL8         = "insoles_cleared";          // boolean
        static final String TS1_COL9           = "ts_out_home";
        static final String TS2_COL10          = "ts_place_reached";
        static final String TS3_COL11          = "ts_place_left";
        static final String TS4_COL12          = "ts_back_home";
        static final String TS5_COL13          = "ts_smartphone_battery_low";*/

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                          + LEFT_PAR +
                _ID                   + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT     + COMMA    +
                DATE_COL1             + INT_TYPE                                    + COMMA    +
                SAMPLE_RATE_COL2      + REAL_TYPE                                   + COMMA    +
                INS_LEFT_ID_COL3      + INT_TYPE                                    + COMMA    +
                INS_LEFT_SENS_COL4    + INT_TYPE                                    + COMMA    +
                INS_RIGHT_ID_COL5     + INT_TYPE                                    + COMMA    +
                INS_RIGHT_SENS_COL6   + REAL_TYPE
                /*DURATION_COL3       + INT_TYPE                + COMMA    +
                CADENCE_COL4        + INT_TYPE                + COMMA    +
                FLAG1_COL5          + INT_TYPE                + COMMA    + // questo è un enum
                FLAG2_COL6          + INT_TYPE                + COMMA    +
                FLAG3_COL7          + INT_TYPE                + COMMA    +
                FLAG4_COL8          + INT_TYPE                + COMMA    +
                TS1_COL9            + TEXT_TYPE               + COMMA    +
                TS2_COL10           + TEXT_TYPE               + COMMA    +
                TS3_COL11           + TEXT_TYPE               + COMMA    +
                TS4_COL12           + TEXT_TYPE               + COMMA    +
                TS5_COL13           + TEXT_TYPE              */ + RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class InsolesRawHeaderTable implements BaseColumns {
        static final String TABLE_NAME                = "INSOLES_RAW_HEADER";
        static final String TIMESTAMP_COL1            = "ts";
        static final String SAMPLE_RATE_COL2          = "sample_rate";
        static final String INS_LEFT_ID_COL3          = "ins_left_id";
        static final String INS_LEFT_SENS_COL4        = "ins_left_sens";
        static final String INS_RIGHT_ID_COL5         = "ins_right_id";
        static final String INS_RIGHT_SENS_COL6       = "ins_right_sens";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                          + LEFT_PAR +
                _ID                   + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT     + COMMA    +
                TIMESTAMP_COL1        + INT_TYPE                                    + COMMA    +
                SAMPLE_RATE_COL2      + REAL_TYPE                                   + COMMA    +
                INS_LEFT_ID_COL3      + INT_TYPE                                    + COMMA    +
                INS_LEFT_SENS_COL4    + INT_TYPE                                    + COMMA    +
                INS_RIGHT_ID_COL5     + INT_TYPE                                    + COMMA    +
                INS_RIGHT_SENS_COL6   + REAL_TYPE                                   + RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class InsolesTable implements BaseColumns {
        static final String TABLE_NAME               = "INSOLES";
        static final String DATE_COL1                = "date";

        static final String ACCEL_X_L_COL2           = "accel_x_l";
        static final String ACCEL_Y_L_COL3           = "accel_y_l";
        static final String ACCEL_Z_L_COL4           = "accel_z_l";
        static final String PRESS_0_L_COL5           = "press_0_l";
        static final String PRESS_1_L_COL6           = "press_1_l";
        static final String PRESS_2_L_COL7           = "press_2_l";
        static final String PRESS_3_L_COL8           = "press_3_l";
        static final String PRESS_4_L_COL9           = "press_4_l";
        static final String PRESS_5_L_COL10          = "press_5_l";
        static final String PRESS_6_L_COL11          = "press_6_l";
        static final String PRESS_7_L_COL12          = "press_7_l";
        static final String PRESS_8_L_COL13          = "press_8_l";
        static final String PRESS_9_L_COL14          = "press_9_l";
        static final String PRESS_10_L_COL15         = "press_10_l";
        static final String PRESS_11_L_COL16         = "press_11_l";
        static final String PRESS_12_L_COL17         = "press_12_l";
        static final String TOT_FORCE_L_COL18        = "tot_force_l";
        static final String COP_X_L_COL19            = "cop_x_l";
        static final String COP_Y_L_COL20            = "cop_y_l";

        static final String ACCEL_X_R_COL21          = "accel_x_r";
        static final String ACCEL_Y_R_COL22          = "accel_y_r";
        static final String ACCEL_Z_R_COL23          = "accel_z_r";
        static final String PRESS_0_R_COL24          = "press_0_r";
        static final String PRESS_1_R_COL25          = "press_1_r";
        static final String PRESS_2_R_COL26          = "press_2_r";
        static final String PRESS_3_R_COL27          = "press_3_r";
        static final String PRESS_4_R_COL28          = "press_4_r";
        static final String PRESS_5_R_COL29          = "press_5_r";
        static final String PRESS_6_R_COL30          = "press_6_r";
        static final String PRESS_7_R_COL31          = "press_7_r";
        static final String PRESS_8_R_COL32          = "press_8_r";
        static final String PRESS_9_R_COL33          = "press_9_r";
        static final String PRESS_10_R_COL34         = "press_10_r";
        static final String PRESS_11_R_COL35         = "press_11_r";
        static final String PRESS_12_R_COL36         = "press_12_r";
        static final String TOT_FORCE_R_COL37        = "tot_force_r";
        static final String COP_X_R_COL38            = "cop_x_r";
        static final String COP_Y_R_COL39            = "cop_y_r";


        static final String GAIT_SESSION_COL40       = "gait_session";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                          +  LEFT_PAR +
                _ID                     + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT   +  COMMA    +
                DATE_COL1               + INT_TYPE + NOT_NULL                      +  COMMA    +

                ACCEL_X_L_COL2          + REAL_TYPE                                 +  COMMA    +
                ACCEL_Y_L_COL3          + REAL_TYPE                                 +  COMMA    +
                ACCEL_Z_L_COL4          + REAL_TYPE                                 +  COMMA    +
                PRESS_0_L_COL5          + REAL_TYPE                                 +  COMMA    +
                PRESS_1_L_COL6          + REAL_TYPE                                 +  COMMA    +
                PRESS_2_L_COL7          + REAL_TYPE                                 +  COMMA    +
                PRESS_3_L_COL8          + REAL_TYPE                                 +  COMMA    +
                PRESS_4_L_COL9          + REAL_TYPE                                 +  COMMA    +
                PRESS_5_L_COL10         + REAL_TYPE                                 +  COMMA    +
                PRESS_6_L_COL11         + REAL_TYPE                                 +  COMMA    +
                PRESS_7_L_COL12         + REAL_TYPE                                 +  COMMA    +
                PRESS_8_L_COL13         + REAL_TYPE                                 +  COMMA    +
                PRESS_9_L_COL14         + REAL_TYPE                                 +  COMMA    +
                PRESS_10_L_COL15        + REAL_TYPE                                 +  COMMA    +
                PRESS_11_L_COL16        + REAL_TYPE                                 +  COMMA    +
                PRESS_12_L_COL17        + REAL_TYPE                                 +  COMMA    +
                TOT_FORCE_L_COL18       + REAL_TYPE                                 +  COMMA    +
                COP_X_L_COL19           + REAL_TYPE                                 +  COMMA    +
                COP_Y_L_COL20           + REAL_TYPE                                 +  COMMA    +

                ACCEL_X_R_COL21         + REAL_TYPE                                 +  COMMA    +
                ACCEL_Y_R_COL22         + REAL_TYPE                                 +  COMMA    +
                ACCEL_Z_R_COL23         + REAL_TYPE                                 +  COMMA    +
                PRESS_0_R_COL24         + REAL_TYPE                                 +  COMMA    +
                PRESS_1_R_COL25         + REAL_TYPE                                 +  COMMA    +
                PRESS_2_R_COL26         + REAL_TYPE                                 +  COMMA    +
                PRESS_3_R_COL27         + REAL_TYPE                                 +  COMMA    +
                PRESS_4_R_COL28         + REAL_TYPE                                 +  COMMA    +
                PRESS_5_R_COL29         + REAL_TYPE                                 +  COMMA    +
                PRESS_6_R_COL30         + REAL_TYPE                                 +  COMMA    +
                PRESS_7_R_COL31         + REAL_TYPE                                 +  COMMA    +
                PRESS_8_R_COL32         + REAL_TYPE                                 +  COMMA    +
                PRESS_9_R_COL33         + REAL_TYPE                                 +  COMMA    +
                PRESS_10_R_COL34        + REAL_TYPE                                 +  COMMA    +
                PRESS_11_R_COL35        + REAL_TYPE                                 +  COMMA    +
                PRESS_12_R_COL36        + REAL_TYPE                                 +  COMMA    +
                TOT_FORCE_R_COL37       + REAL_TYPE                                 +  COMMA    +
                COP_X_R_COL38           + REAL_TYPE                                 +  COMMA    +
                COP_Y_R_COL39           + REAL_TYPE                                 +

                /*GAIT_SESSION_COL40      + INT_TYPE  + NOT_NULL    +  COMMA    +
                PRIMARY_KEY + LEFT_PAR + _ID + COMMA + GAIT_SESSION_COL40 + RIGHT_PAR +*/
                RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class InsolesRawDataTable implements BaseColumns {
        static final String TABLE_NAME              = "INSOLES_RAW_DATA";
        static final String TIMESTAMP_COL1          = "ts";

        static final String MSG_DEF_L_COL2          = "msg_def_l";
        static final String MSG_DEF_R_COL3          = "msg_def_r";
        static final String DATA_L_COL4             = "data_l";
        static final String DATA_R_COL5             = "data_r";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME                                                          +  LEFT_PAR +
                _ID                     + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT   +  COMMA    +
                TIMESTAMP_COL1          + INT_TYPE                                  +  COMMA    +
                MSG_DEF_L_COL2          + INT_TYPE                                  +  COMMA    +
                MSG_DEF_R_COL3          + INT_TYPE                                  +  COMMA    +
                DATA_L_COL4             + TEXT_TYPE                                 +  COMMA    +
                DATA_R_COL5             + TEXT_TYPE                                 +  RIGHT_PAR;

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
