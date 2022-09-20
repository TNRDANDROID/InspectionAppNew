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
                    card.setImg_id(cursor.getInt(cursor.getColumnIndexOrThrow("img_id")));
                    card.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("image_serial_number")));
                    card.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                    card.setLongtitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                    card.setImage_path(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                    card.setPhoto_type_id(cursor.getInt(cursor.getColumnIndexOrThrow("photo_type_id")));

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
    public ArrayList<ModelClass> getParticularSavedImage(String work_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            selection = "work_id = ?";
            selectionArgs = new String[]{work_id};


            cursor = db.query(DBHelper.SAVE_IMAGES,new String[]{"*"},
                    selection, selectionArgs, null, null, "work_id");
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setImg_id(cursor.getInt(cursor.getColumnIndexOrThrow("img_id")));
                    card.setImage(cursor.getString(cursor.getColumnIndexOrThrow("image")));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("image_serial_number")));
                    card.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow("latitude")));
                    card.setLongtitude(cursor.getString(cursor.getColumnIndexOrThrow("longitude")));
                    card.setImage_path(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
                    card.setPhoto_type_id(cursor.getInt(cursor.getColumnIndexOrThrow("photo_type_id")));

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
    public ArrayList<ModelClass> getAllWorkList() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.WORK_LIST,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setWork_name(cursor.getString(cursor.getColumnIndexOrThrow("work_name")));
                    card.setWork_status(cursor.getString(cursor.getColumnIndexOrThrow("work_status")));

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
    public ArrayList<ModelClass> getSavedWorkList() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_WORK_DETAILS,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setWork_name(cursor.getString(cursor.getColumnIndexOrThrow("work_name")));
                    card.setWork_status(cursor.getString(cursor.getColumnIndexOrThrow("work_status")));

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
    public void Insert_workList(ModelClass modelClass) {
        ContentValues values = new ContentValues();
        values.put("work_id", modelClass.getWork_id());
        values.put("work_name", modelClass.getWork_name());
        values.put("work_status", modelClass.getWork_status());
        long id = db.insert(DBHelper.WORK_LIST,null,values);
        Log.d("Insert_id_work", String.valueOf(id));

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
    public void deleteSchemeTable() {
        db.execSQL("delete from " + DBHelper.SCHEME_TABLE_NAME);
    }
    public void deleteObservationTable() {
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
        deleteObservationTable();
        deleteFinYearTable();
        deleteSaveWorkDetailsTable();

    }



}
