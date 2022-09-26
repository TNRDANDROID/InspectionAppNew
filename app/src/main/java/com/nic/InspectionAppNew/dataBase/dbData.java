package com.nic.InspectionAppNew.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;
    private PrefManager prefManager;
    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;

    }

    public void open() {
        db = dbHelper.getWritableDatabase();
        prefManager = new PrefManager(context);
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public ModelClass insertVillage(ModelClass pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return pmgsySurvey;
    }
    public ModelClass insertScheme(ModelClass pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put(AppConstant.SCHEME_SEQUENTIAL_ID, pmgsySurvey.getSchemeSequentialID());
        values.put(AppConstant.SCHEME_NAME, pmgsySurvey.getSchemeName());
        values.put(AppConstant.FINANCIAL_YEAR, pmgsySurvey.getFinancialYear());

        long id = db.insert(DBHelper.SCHEME_TABLE_NAME,null,values);
        Log.d("Inserted_id_scheme", String.valueOf(id));

        return pmgsySurvey;
    }
    public void insertFinYear(ModelClass pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.FINANCIAL_YEAR, pmgsySurvey.getFinancialYear());

        long id = db.insert(DBHelper.FINANCIAL_YEAR_TABLE_NAME,null,values);
        Log.d("Inserted_id_fin", String.valueOf(id));

    }
    public void insertStatus(ModelClass pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.STATUS_ID, pmgsySurvey.getWork_status_id());
        values.put(AppConstant.WORK_STATUS, pmgsySurvey.getWork_status());

        long id = db.insert(DBHelper.STATUS_TABLE,null,values);
        Log.d("Inserted_id_status", String.valueOf(id));

    }
    public ArrayList<ModelClass> getAll_Village(String dcode, String bcode) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<ModelClass> getAll_Fin_Year() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.FINANCIAL_YEAR_TABLE_NAME,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setFinancialYear(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.FINANCIAL_YEAR)));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<ModelClass> getAll_Work_Status() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.STATUS_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setWork_status_id(cursor.getInt(cursor.getColumnIndexOrThrow(AppConstant.STATUS_ID)));
                    card.setWork_status(cursor.getString(cursor.getColumnIndexOrThrow(AppConstant.WORK_STATUS)));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ModelClass insertHabitation(ModelClass pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HABITATION_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return pmgsySurvey;
    }
    public void Insert_workList(String type,ModelClass modelClass) {
        long id;
        ContentValues values = new ContentValues();
        values.put("dcode", modelClass.getDistictCode());
        values.put("bcode", modelClass.getBlockCode());
        values.put("pvcode", modelClass.getPvCode());
        values.put("hab_code", modelClass.getHabCode());
        values.put("scheme_group_id", modelClass.getScheme_group_id());
        values.put("scheme_id", modelClass.getSchemeSequentialID());
        values.put("work_group_id", modelClass.getWork_group_id());
        values.put("work_type_id", modelClass.getWork_type_id());
        values.put("fin_year", modelClass.getFinancialYear());
        values.put("work_id", modelClass.getWork_id());
        values.put("work_name", modelClass.getWork_name());
        values.put("as_value", modelClass.getAs_value());
        values.put("ts_value", modelClass.getTs_value());
        values.put("current_stage_of_work", modelClass.getCurrent_stage_of_work());
        values.put("is_high_value", modelClass.getIs_high_value());
        if(type.equals("offline")){
             id = db.insert(DBHelper.WORK_LIST,null,values);
        }else {
             id = db.insert(DBHelper.ONLINE_WORK_LIST,null,values);
        }

        Log.d("Insert_id_work", String.valueOf(id));

    }
    public ArrayList<ModelClass> getAll_Habitation(String dcode, String bcode) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<ModelClass> getSavedImage() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_IMAGES,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setSave_work_details_primary_id(cursor.getInt(cursor.getColumnIndexOrThrow("save_work_details_primary_id")));
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("image_description")));
                    card.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                    card.setLongtitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                    card.setImage_path(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("serial_no")));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<ModelClass> getParticularSavedImage(String type,String save_work_details_primary_id,String work_id,String serial_number) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(type.equals("work_id")){
                selection = "save_work_details_primary_id = ?";
                selectionArgs = new String[]{save_work_details_primary_id};
            }
            else {
                selection = "save_work_details_primary_id = ? and work_id = ? and serial_no = ?";
                selectionArgs = new String[]{save_work_details_primary_id,work_id,serial_number};
            }


            cursor = db.query(DBHelper.SAVE_IMAGES,new String[]{"*"},
                    selection, selectionArgs, null, null, "work_id");
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setSave_work_details_primary_id(cursor.getInt(cursor.getColumnIndexOrThrow("save_work_details_primary_id")));
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("image_description")));
                    card.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                    card.setLongtitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                    card.setImage_path(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("serial_no")));

                    cards.add(card);
                }
            }
        } catch (Exception e){
               Log.d("DEBUG_TAG", "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<ModelClass> getAllWorkList(String onOffType,String type,String dcode,String bcode,String pvcode,String fin_year,String scheme_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(onOffType.equals("offline")){
                if(type.equals("all")){
                    cursor = db.rawQuery("select * from "+DBHelper.WORK_LIST,null);

                }else {
                    selection = "dcode = ? and bcode = ? and pvcode = ? and fin_year = ? and scheme_id = ? ";
                    selectionArgs = new String[]{ dcode, bcode, pvcode, fin_year,scheme_id};
                    cursor = db.query(DBHelper.WORK_LIST, new String[]{"*"},
                            selection, selectionArgs, null, null, "work_id");
                }
            }else {
                selection = "dcode = ? and bcode = ? and pvcode = ? and fin_year = ? and scheme_id = ? ";
                selectionArgs = new String[]{ dcode, bcode, pvcode, fin_year,scheme_id};
                cursor = db.query(DBHelper.ONLINE_WORK_LIST, new String[]{"*"},
                        selection, selectionArgs, null, null, "work_id");
            }



            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setDistictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
                    card.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow("bcode")));
                    card.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow("pvcode")));
                    card.setHabCode(cursor.getString(cursor.getColumnIndexOrThrow("hab_code")));
                    card.setScheme_group_id(cursor.getString(cursor.getColumnIndexOrThrow("scheme_group_id")));
                    card.setSchemeSequentialID(cursor.getString(cursor.getColumnIndexOrThrow("scheme_id")));
                    card.setWork_group_id(cursor.getString(cursor.getColumnIndexOrThrow("work_group_id")));
                    card.setWork_type_id(cursor.getString(cursor.getColumnIndexOrThrow("work_type_id")));
                    card.setFinancialYear(cursor.getString(cursor.getColumnIndexOrThrow("fin_year")));
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setWork_name(cursor.getString(cursor.getColumnIndexOrThrow("work_name")));
                    card.setAs_value(cursor.getString(cursor.getColumnIndexOrThrow("as_value")));
                    card.setTs_value(cursor.getString(cursor.getColumnIndexOrThrow("ts_value")));
                    card.setCurrent_stage_of_work(cursor.getString(cursor.getColumnIndexOrThrow("current_stage_of_work")));
                    card.setIs_high_value(cursor.getString(cursor.getColumnIndexOrThrow("is_high_value")));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<ModelClass> getSavedWorkList(String type,String work_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String[] selectionArgs;
        String selection;
        try {
            if(type.equals("all")){
                cursor = db.rawQuery("select * from "+DBHelper.SAVE_WORK_DETAILS,null);
            }
            else {
                selection = "work_id = ?";
                selectionArgs = new String[]{work_id};


                cursor = db.query(DBHelper.SAVE_WORK_DETAILS,new String[]{"*"},
                        selection, selectionArgs, null, null, "work_id");
            }

            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setSave_work_details_primary_id(cursor.getInt(cursor.getColumnIndexOrThrow("save_work_details_primary_id")));
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setDistictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
                    card.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow("bcode")));
                    card.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow("pvcode")));
                    card.setHabCode(cursor.getString(cursor.getColumnIndexOrThrow("hab_code")));
                    card.setScheme_group_id(cursor.getString(cursor.getColumnIndexOrThrow("scheme_group_id")));
                    card.setWork_group_id(cursor.getString(cursor.getColumnIndexOrThrow("work_group_id")));
                    card.setWork_type_id(cursor.getString(cursor.getColumnIndexOrThrow("work_type_id")));
                    card.setSchemeSequentialID(cursor.getString(cursor.getColumnIndexOrThrow("scheme_id")));
                    card.setFinancialYear(cursor.getString(cursor.getColumnIndexOrThrow("fin_year")));
                    card.setWork_name(cursor.getString(cursor.getColumnIndexOrThrow("work_name")));
                    card.setAs_value(cursor.getString(cursor.getColumnIndexOrThrow("as_value")));
                    card.setTs_value(cursor.getString(cursor.getColumnIndexOrThrow("ts_value")));
                    card.setCurrent_stage_of_work(cursor.getString(cursor.getColumnIndexOrThrow("current_stage_of_work")));
                    card.setIs_high_value(cursor.getString(cursor.getColumnIndexOrThrow("is_high_value")));
                    card.setWork_description(cursor.getString(cursor.getColumnIndexOrThrow("work_description")));
                    card.setWork_status(cursor.getString(cursor.getColumnIndexOrThrow("work_status")));
                    card.setWork_status_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_status_id")));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
            e.printStackTrace();
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public Bitmap bytearrtoBitmap(byte[] photo){
        byte[] imgbytes = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgbytes, 0,
                imgbytes.length);
        return bitmap;
    }



    //////////////////////*****************/////////////


    public void deleteDistrictTable() {
        db.execSQL("delete from " + DBHelper.DISTRICT_TABLE_NAME);
    }
    public void deleteBlockTable() {
        db.execSQL("delete from " + DBHelper.BLOCK_TABLE_NAME);
    }
    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }
    public void deleteHabitationTable() {
        db.execSQL("delete from " + DBHelper.HABITATION_TABLE_NAME);
    }
    public void deleteSAVE_IMAGESTable() {
        db.execSQL("delete from " + DBHelper.SAVE_IMAGES);
    }

    public void deleteWorkListTable() {
        db.execSQL("delete from " + DBHelper.WORK_LIST);
    }
    public void deleteOnlineWorkListTable() {
        db.execSQL("delete from " + DBHelper.ONLINE_WORK_LIST);
    }
    public void deleteSchemeTable() {
        db.execSQL("delete from " + DBHelper.SCHEME_TABLE_NAME);
    }
    public void deleteinspection_statusTable() {
        db.execSQL("delete from " + DBHelper.STATUS_TABLE);
    }
    public void deleteFinYearTable() {
        db.execSQL("delete from " + DBHelper.FINANCIAL_YEAR_TABLE_NAME);
    }
    public void deleteSaveWorkDetailsTable() {
        db.execSQL("delete from " + DBHelper.SAVE_WORK_DETAILS);
    }





    public void deleteAll() {

        deleteDistrictTable();
        deleteBlockTable();
        deleteVillageTable();
        deleteHabitationTable();
        deleteSAVE_IMAGESTable();
        deleteWorkListTable();
        deleteSchemeTable();
        deleteinspection_statusTable();
        deleteFinYearTable();
        deleteSaveWorkDetailsTable();
        deleteOnlineWorkListTable();

    }



}
