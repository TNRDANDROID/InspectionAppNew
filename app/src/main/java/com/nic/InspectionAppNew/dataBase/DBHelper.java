package com.nic.InspectionAppNew.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InspectionNew";
    private static final int DATABASE_VERSION = 10;

    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String SAVE_IMAGES = " save_images";
    public static final String SAVE_ATR_IMAGES = " save_atr_images";
    public static  final String WORK_LIST ="work_list";
    public static  final String ONLINE_WORK_LIST ="online_work_list";
    public static  final String ATR_WORK_LIST ="atr_work_list";
    public static  final String SAVE_WORK_DETAILS ="save_work_details";
    public static  final String SAVE_ATR_WORK_DETAILS ="save_atr_work_details";
    public static final String DISTRICT_TABLE_NAME = "DistrictList";
    public static final String BLOCK_TABLE_NAME = "BlockList";
    public static final String SCHEME_TABLE_NAME = "SchemeList";
    public static final String FINANCIAL_YEAR_TABLE_NAME = "FinancialYear";
    public static final String STATUS_TABLE = "status";
    public static final String OTHER_CATEGORY_TABLE = "other_category";
    public static final String WORK_STAGE_TABLE = "work_type_stage_link";

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
                "work_status TEXT)");
        db.execSQL("CREATE TABLE " + OTHER_CATEGORY_TABLE + " ("
                + "other_work_category_id INTEGER," +
                "other_work_category_name TEXT)");

        db.execSQL("CREATE TABLE " + FINANCIAL_YEAR_TABLE_NAME + " ("
                + "fin_year  varchar(32))");

        db.execSQL("CREATE TABLE " + SAVE_IMAGES + " ("
                + "save_work_details_image_primary_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "save_work_details_primary_id INTEGER," +
                "dcode INTEGER," +
                "image TEXT," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "work_id INTEGER," +
                "image_description TEXT," +
                "image_path TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "flag TEXT," +
                "inspection_id TEXT," +
                "serial_no INTEGER)");
        db.execSQL("CREATE TABLE " + SAVE_ATR_IMAGES + " ("
                + "save_work_details_image_primary_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "save_work_details_primary_id INTEGER," +
                "dcode INTEGER," +
                "image TEXT," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "work_id INTEGER," +
                "image_description TEXT," +
                "image_path TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "serial_no INTEGER)");

        db.execSQL("CREATE TABLE " + WORK_LIST + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "hab_code TEXT," +
                "scheme_group_id TEXT," +
                "scheme_id TEXT," +
                "work_group_id TEXT," +
                "work_type_id TEXT," +
                "fin_year TEXT," +
                "work_id INTEGER," +
                "work_name TEXT," +
                "as_value TEXT," +
                "ts_value TEXT," +
                "current_stage_of_work TEXT," +
                "stage_name TEXT," +
                "as_date TEXT," +
                "ts_date TEXT," +
                "work_order_date TEXT," +
                "work_type_name TEXT," +
                "is_high_value TEXT)");
        db.execSQL("CREATE TABLE " + ATR_WORK_LIST + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "work_id INTEGER," +
                "dname TEXT," +
                "bname TEXT," +
                "pvname TEXT," +
                "work_name TEXT," +
                "work_type_name TEXT," +
                "inspection_id TEXT," +
                "inspection_date TEXT," +
                "status_id TEXT," +
                "status TEXT," +
                "description TEXT," +
                "inspection_by_officer_designation TEXT," +
                "action_taken_id TEXT," +
                "action_status TEXT," +
                "reported_by TEXT," +
                "inspection_by_officer TEXT)");
        db.execSQL("CREATE TABLE " + ONLINE_WORK_LIST + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "hab_code TEXT," +
                "scheme_group_id TEXT," +
                "scheme_id TEXT," +
                "work_group_id TEXT," +
                "work_type_id TEXT," +
                "fin_year TEXT," +
                "work_id INTEGER," +
                "work_name TEXT," +
                "as_value TEXT," +
                "ts_value TEXT," +
                "current_stage_of_work TEXT," +
                "stage_name TEXT," +
                "as_date TEXT," +
                "ts_date TEXT," +
                "work_order_date TEXT," +
                "work_type_name TEXT," +
                "is_high_value TEXT)");
        db.execSQL("CREATE TABLE " + SAVE_WORK_DETAILS + " ("
                + "save_work_details_primary_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                 "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "hab_code TEXT," +
                "scheme_group_id TEXT," +
                "work_group_id TEXT," +
                "work_type_id TEXT," +
                "scheme_id TEXT," +
                "fin_year TEXT," +
                "work_id INTEGER," +
                "work_status_id INTEGER," +
                "work_name TEXT," +
                "as_value TEXT," +
                "ts_value TEXT," +
                "work_status TEXT," +
                "work_stage TEXT," +
                "work_stage_id TEXT," +
                "work_description TEXT," +
                "current_stage_of_work TEXT," +
                "flag TEXT," +
                "inspection_id TEXT," +
                "is_high_value TEXT)");
        db.execSQL("CREATE TABLE " + SAVE_ATR_WORK_DETAILS + " ("
                + "save_work_details_primary_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                 "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "hab_code TEXT," +
                "scheme_group_id TEXT," +
                "work_group_id TEXT," +
                "work_type_id TEXT," +
                "scheme_id TEXT," +
                "fin_year TEXT," +
                "work_id INTEGER," +
                "work_status_id INTEGER," +
                "work_name TEXT," +
                "as_value TEXT," +
                "ts_value TEXT," +
                "work_status TEXT," +
                "work_stage TEXT," +
                "work_stage_id TEXT," +
                "work_description TEXT," +
                "current_stage_of_work TEXT," +
                "is_high_value TEXT)");

        db.execSQL("CREATE TABLE " + WORK_STAGE_TABLE + " ("
                + "work_group_id  INTEGER," +
                "work_type_id  INTEGER," +
                "work_stage_order  INTEGER," +
                "work_stage_code  INTEGER," +
                "work_stage_name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BLOCK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_IMAGES);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + ATR_WORK_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + ONLINE_WORK_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_STAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + FINANCIAL_YEAR_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_WORK_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_ATR_IMAGES);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_ATR_WORK_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + OTHER_CATEGORY_TABLE);
            onCreate(db);
        }
    }


}
