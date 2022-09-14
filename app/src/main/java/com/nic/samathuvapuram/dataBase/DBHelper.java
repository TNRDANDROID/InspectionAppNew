package com.nic.samathuvapuram.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Samathuvapuram";
    private static final int DATABASE_VERSION = 2;

    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String SAVE_IMAGES = " save_images";
    public static final String CURRENT_HOUSE_USAGE = " current_house_usage";
    public static final String COMMUNITY = " community";
    public static final String PHOTOS_TYPE = " photos_type";
    public static final String CONDITION_OF_HOUSE = " condition_of_house";
    public static final String CONDITION_OF_INFRA = " condition_of_infra";
    public static final String ESTIMATE_TYPE = " estimate_type";
    public static final String WORK_SCHEME = " work_scheme";
    public static final String WORK_TYPE = " work_type";
    public static  final String MENU_ACCESS_CONTROL="menu_access_control";
    public static  final String SAMATHUVAPURAM_DETAILS="samathuvapuram_details";
    public static  final String HOUSE_LIST="house_list";
    public static  final String INFRA_LIST="infra_list";
    public static  final String HOUSE_DETAILS="house_details";
    public static  final String HOUSE_IMAGES_DETAILS="house_image_details";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

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

        db.execSQL("CREATE TABLE " + SAVE_IMAGES + " ("
                + "img_id INTEGER,"+
                "image TEXT," +
                "latitude TEXT," +
                "longitude TEXT," +
                "type_of_photo TEXT)");

        db.execSQL("CREATE TABLE " + MENU_ACCESS_CONTROL + " ("
                + "menu_control_primary_key INTEGER PRIMARY KEY AUTOINCREMENT," +
                "menu_id INTEGER," +
                "menu_name TEXT," +
                "menu_access_control TEXT)");

        db.execSQL("CREATE TABLE " + SAMATHUVAPURAM_DETAILS + " ("
                + "samathuvapuram_id INTEGER," +
                "localbody_area_type INTEGER," +
                "localbody_type_id INTEGER," +
                "dcode INTEGER," +
                "bcode INTEGER," +
                "tpcode INTEGER," +
                "no_of_houses_constructed INTEGER," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "muncode TEXT," +
                "corpcode TEXT," +
                "year_of_construction TEXT," +
                "dname TEXT," +
                "bname TEXT," +
                "pvname TEXT," +
                "habitation_name TEXT," +
                "townpanchayat_name TEXT," +
                "municipality_name TEXT," +
                "corporation_name TEXT)");


        db.execSQL("CREATE TABLE " + HOUSE_LIST + " ("
                + "current_beneficiary_id INTEGER," +
                "samathuvapuram_id INTEGER," +
                "house_serial_number INTEGER," +
                "is_house_owned_by_sanctioned_beneficiary TEXT," +
                "current_house_usage TEXT," +
                "current_name_of_the_beneficiary TEXT," +
                "current_gender TEXT," +
                "current_community_category_id TEXT," +
                "current_usage TEXT," +
                "is_beneficiary_detail_required TEXT," +

                "name_of_the_beneficiary TEXT," +
                "gender TEXT," +
                "community_category_id INTEGER," +
                "house_sanctioned_order_no TEXT," +
                "condition_of_house_id INTEGER," +
                "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "work_group_id INTEGER," +
                "work_type_id INTEGER," +
                "estimate_cost_required TEXT," +
                "condition_of_house TEXT)");

        db.execSQL("CREATE TABLE " + INFRA_LIST + " ("
                + "repair_infra_estimate_id INTEGER," +
                "samathuvapuram_id INTEGER," +
                "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "work_group_id INTEGER," +
                "work_type_id INTEGER," +
                "condition_of_infra_id INTEGER," +
                "condition_of_infra TEXT," +
                "estimate_cost_required TEXT," +
                "scheme_name TEXT," +
                "work_name TEXT)");


        db.execSQL("CREATE TABLE " + HOUSE_DETAILS + " ("
                + "current_house_usage_id INTEGER," +
                "samathuvapuram_id INTEGER," +
                "house_serial_number INTEGER," +
                "current_house_usage TEXT," +
                "Type TEXT," +
                "is_house_owned_by_sanctioned_beneficiary TEXT," +
                "current_name_of_the_beneficiary TEXT," +
                "current_gender TEXT," +
                "current_community_category_id TEXT," +
                "condition_of_house TEXT," +
                "condition_of_house_id INTEGER," +
                "condition_of_infra TEXT," +
                "condition_of_infra_id INTEGER," +
                "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "scheme TEXT," +
                "work TEXT," +
                "work_group_id INTEGER," +
                "work_type_id INTEGER," +
                "estimate_cost_required TEXT)");

        db.execSQL("CREATE TABLE " + HOUSE_IMAGES_DETAILS + " ("
                + "image_serial_number INTEGER," +
                "photo_type_id INTEGER," +
                "house_serial_number INTEGER," +
                "samathuvapuram_id INTEGER," +
                "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "work_group_id INTEGER," +
                "work_type_id INTEGER," +
                "latitude TEXT," +
                "longitude TEXT," +
                "image_path TEXT)");

        db.execSQL("CREATE TABLE " + CURRENT_HOUSE_USAGE + " ("
                + "current_usage_id INTEGER," +
                "current_usage TEXT," +
                "is_beneficiary_detail_required TEXT)");

        db.execSQL("CREATE TABLE " + COMMUNITY + " ("
                + "community_category_id INTEGER," +
                "community_category TEXT)");

        db.execSQL("CREATE TABLE " + PHOTOS_TYPE + " ("
                + "photo_type_id INTEGER," +
                 "form_id INTEGER," +
                 "min_no_of_photos INTEGER," +
                 "max_no_of_photos INTEGER," +
                "photo_type_name TEXT)");

        db.execSQL("CREATE TABLE " + CONDITION_OF_HOUSE + " ("
                + "condition_of_house_id INTEGER," +
                "condition_of_house TEXT)");

        db.execSQL("CREATE TABLE " + CONDITION_OF_INFRA + " ("
                + "condition_of_infra_id INTEGER," +
                "condition_of_infra TEXT)");

        db.execSQL("CREATE TABLE " + ESTIMATE_TYPE + " ("
                + "estimate_type_id INTEGER," +
                "estimate_type_name TEXT)");

        db.execSQL("CREATE TABLE " + WORK_SCHEME + " ("
                + "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "scheme_disp_order INTEGER," +
                "category TEXT," +
                "has_sub_type TEXT," +
                "existing_condition_required TEXT," +
                "photos_required TEXT," +
                "scheme_name TEXT)");


        db.execSQL("CREATE TABLE " + WORK_TYPE + " ("
                + "scheme_group_id INTEGER," +
                "scheme_id INTEGER," +
                "work_group_id INTEGER," +
                "work_type_id INTEGER," +
                "category TEXT," +
                "existing_condition_required TEXT," +
                "photos_required TEXT," +
                "work_name TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_IMAGES);
            db.execSQL("DROP TABLE IF EXISTS " + MENU_ACCESS_CONTROL);
            db.execSQL("DROP TABLE IF EXISTS " + CURRENT_HOUSE_USAGE);
            db.execSQL("DROP TABLE IF EXISTS " + COMMUNITY);
            db.execSQL("DROP TABLE IF EXISTS " + PHOTOS_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + CONDITION_OF_HOUSE);
            db.execSQL("DROP TABLE IF EXISTS " + CONDITION_OF_INFRA);
            db.execSQL("DROP TABLE IF EXISTS " + ESTIMATE_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_SCHEME);
            db.execSQL("DROP TABLE IF EXISTS " + WORK_TYPE);
            db.execSQL("DROP TABLE IF EXISTS " + SAMATHUVAPURAM_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + HOUSE_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + HOUSE_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + HOUSE_IMAGES_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + INFRA_LIST);

            onCreate(db);
        }
    }


}
