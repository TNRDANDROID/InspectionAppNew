package com.nic.InspectionAppNew.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InspectionNew";
    private static final int DATABASE_VERSION = 2;

    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String SAVE_IMAGES = " save_images";
    public static  final String WORK_LIST ="work_list";
    public static  final String SAVE_WORK_DETAILS ="save_work_details";
    public static final String DISTRICT_TABLE_NAME = "DistrictList";
    public static final String BLOCK_TABLE_NAME = "BlockList";
    public static final String SCHEME_TABLE_NAME = "SchemeList";
    public static final String FINANCIAL_YEAR_TABLE_NAME = "FinancialYear";
    public static final String STATUS_TABLE = "status";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DISTRICT_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "dname TEXT)");

        db.execSQL("CREATE TABLE " + BLOCK_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "bname varchar(32))");
        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname TEXT)");

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name TEXT)");
        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "scheme_name varchar(32)," +
                "fin_year  varchar(32),"+
                "scheme_seq_id INTEGER)");

        db.execSQL("CREATE TABLE " + STATUS_TABLE + " ("
                + "status_id INTEGER," +
                "status TEXT)");

        db.execSQL("CREATE TABLE " + FINANCIAL_YEAR_TABLE_NAME + " ("
                + "fin_year  varchar(32))");

        db.execSQL("CREATE TABLE " + SAVE_IMAGES + " ("
                + "img_id INTEGER,"+
                "image TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "type_of_photo TEXT)");

        db.execSQL("CREATE TABLE " + WORK_LIST + " ("
                + "work_id INTEGER," +
                "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "dname TEXT," +
                "bname TEXT," +
                "pvname TEXT," +
                "habitation_name TEXT," +
                "work_status TEXT," +
                "work_name TEXT)");
        db.execSQL("CREATE TABLE " + SAVE_WORK_DETAILS + " ("
                + "work_id INTEGER," +
                "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "dname TEXT," +
                "bname TEXT," +
                "pvname TEXT," +
                "habitation_name TEXT," +
                "work_status TEXT," +
                "work_name TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_IMAGES);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FINANCIAL_YEAR_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_WORK_DETAILS);
            onCreate(db);
        }
    }


}
