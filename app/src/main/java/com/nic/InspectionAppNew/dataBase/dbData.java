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
import com.nic.InspectionAppNew.utils.Utils;

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
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistrictCode());
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
        //values.put(AppConstant.FINANCIAL_YEAR, pmgsySurvey.getFinancialYear());

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
    public void insertCategoryList(ModelClass pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("other_work_category_id", pmgsySurvey.getOther_work_category_id());
        values.put("other_work_category_name", pmgsySurvey.getOther_work_category_name());

        long id = db.insert(DBHelper.OTHER_CATEGORY_TABLE,null,values);
        Log.d("insertCategoryList", String.valueOf(id));

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
                    card.setDistrictCode(cursor.getString(cursor
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
    public ArrayList<ModelClass> getAll_Other_work_category() {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.OTHER_CATEGORY_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setOther_work_category_id(cursor.getInt(cursor.getColumnIndexOrThrow("other_work_category_id")));
                    card.setOther_work_category_name(cursor.getString(cursor.getColumnIndexOrThrow("other_work_category_name")));
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
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistrictCode());
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
        values.put("dcode", modelClass.getDistrictCode());
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
        values.put("stage_name", modelClass.getStage_name());
        values.put("is_high_value", modelClass.getIs_high_value());
        values.put("as_date", modelClass.getAs_date());
        values.put("ts_date", modelClass.getTs_date());
        values.put("work_order_date", modelClass.getWork_order_date());
        values.put("work_type_name", modelClass.getWork_type_name());
        if(type.equals("offline")){
             id = db.insert(DBHelper.WORK_LIST,null,values);
        }else {
             id = db.insert(DBHelper.ONLINE_WORK_LIST,null,values);
        }

        Log.d("Insert_id_work", String.valueOf(id));

    }
    public void Insert_atr_workList(String type,ModelClass modelClass) {
        long id;
        ContentValues values = new ContentValues();
        values.put("dcode", modelClass.getDistrictCode());
        values.put("bcode", modelClass.getBlockCode());
        values.put("pvcode", modelClass.getPvCode());
        values.put("work_id", modelClass.getWork_id());
        values.put("work_name", modelClass.getWork_name());
        values.put("inspection_id", modelClass.getInspection_id());
        values.put("inspection_date", modelClass.getInspectedDate());
        values.put("status_id", modelClass.getWork_status_id());
        values.put("status", modelClass.getWork_status());
        values.put("description", modelClass.getDescription());
        values.put("inspection_by_officer", modelClass.getInspection_by_officer());
        values.put("inspection_by_officer_designation", modelClass.getInspection_by_officer_designation());
        values.put("satisfied_count", modelClass.getSatisfied_cout());
        values.put("unsatisfied_count", modelClass.getUnsatisfied_cout());
        values.put("need_improvement_count", modelClass.getNeedimprovement_cout());
        values.put("total_count", modelClass.getTotal_cout());
        values.put("inspection_by_officer_designation", modelClass.getInspection_by_officer_designation());
        id = db.insert(DBHelper.ATR_WORK_LIST,null,values);

        Log.d("Insert_id_atr_work", String.valueOf(id));

    }
    public void insertStage(ModelClass realTimeMonitoringSystem) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.WORK_GROUP_ID, realTimeMonitoringSystem.getWork_group_id());
        values.put(AppConstant.WORK_TYPE_ID, realTimeMonitoringSystem.getWork_type_id());
        values.put(AppConstant.WORK_STAGE_ORDER, realTimeMonitoringSystem.getWork_stage_order());
        values.put(AppConstant.WORK_STAGE_CODE, realTimeMonitoringSystem.getWork_stage_code());
        values.put(AppConstant.WORK_SATGE_NAME, realTimeMonitoringSystem.getWork_stage_name());

        long id = db.insert(DBHelper.WORK_STAGE_TABLE, null, values);
        Log.d("Inserted_id_Stage", String.valueOf(id));

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
                    card.setDistrictCode(cursor.getString(cursor
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
                    card.setInspection_id(cursor.getString(cursor.getColumnIndexOrThrow("inspection_id")));

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
            else if(type.equals("exist")){
                selection = "work_id = ?";
                selectionArgs = new String[]{work_id};
            }else {
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
                    card.setImage(Utils.StringToBitMap(cursor.getString(cursor.getColumnIndexOrThrow("image"))));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("serial_no")));
                    card.setInspection_id(cursor.getString(cursor.getColumnIndexOrThrow("inspection_id")));

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
    public ArrayList<ModelClass> getParticularSavedImagebycode(String type,String dcode, String bcode, String pvcode, String work_id, String serial_number, String inspection_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(type.equalsIgnoreCase("all")){
                selection = "dcode = ? and bcode = ? and pvcode = ? and work_id = ?";
                selectionArgs = new String[]{dcode,bcode,pvcode,work_id};
            }else if(type.equalsIgnoreCase("atr")){
                selection = "dcode = ? and bcode = ? and pvcode = ? and work_id = ? and inspection_id = ?";
                selectionArgs = new String[]{dcode,bcode,pvcode,work_id,inspection_id};
            }else {
                selection = "dcode = ? and bcode = ? and pvcode = ? and work_id = ? and serial_no = ?";
                selectionArgs = new String[]{dcode,bcode,pvcode,work_id,serial_number};
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
                    card.setImage(Utils.StringToBitMap(cursor.getString(cursor.getColumnIndexOrThrow("image"))));
                    card.setImage_serial_number(cursor.getInt(cursor.getColumnIndexOrThrow("serial_no")));
                    card.setDistrictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
                    card.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow("bcode")));
                    card.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow("pvcode")));
                    card.setInspection_id(cursor.getString(cursor.getColumnIndexOrThrow("inspection_id")));

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
    public ArrayList<ModelClass> getAllWorkList(String onOffType,String type,String dcode,String bcode,String pvcode,String scheme_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(onOffType.equals("offline")){
                if(type.equals("all")){
                    cursor = db.rawQuery("select * from "+DBHelper.WORK_LIST,null);

                }else {
                    selection = "dcode = ? and bcode = ? and pvcode = ? and scheme_id = ? ";
                    selectionArgs = new String[]{ dcode, bcode, pvcode, scheme_id};
                    cursor = db.query(DBHelper.WORK_LIST, new String[]{"*"},
                            selection, selectionArgs, null, null, "work_id");
                }
            }else {
                selection = "dcode = ? and bcode = ? and pvcode = ? and scheme_id = ? ";
                selectionArgs = new String[]{ dcode, bcode, pvcode, scheme_id};
                cursor = db.query(DBHelper.ONLINE_WORK_LIST, new String[]{"*"},
                        selection, selectionArgs, null, null, "work_id");
            }



            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setDistrictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
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
                    card.setStage_name(cursor.getString(cursor.getColumnIndexOrThrow("stage_name")));
                    card.setIs_high_value(cursor.getString(cursor.getColumnIndexOrThrow("is_high_value")));
                    card.setAs_date(cursor.getString(cursor.getColumnIndexOrThrow("as_date")));
                    card.setTs_date(cursor.getString(cursor.getColumnIndexOrThrow("ts_date")));
                    card.setWork_order_date(cursor.getString(cursor.getColumnIndexOrThrow("work_order_date")));
                    card.setWork_type_name(cursor.getString(cursor.getColumnIndexOrThrow("work_type_name")));

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
    public ArrayList<ModelClass> getAllATRWorkList(String onOffType) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(onOffType.equals("offline")){
                cursor = db.rawQuery("select * from "+DBHelper.ATR_WORK_LIST,null);
            }else {
            }



            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setDistrictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
                    card.setBlockCode(cursor.getString(cursor.getColumnIndexOrThrow("bcode")));
                    card.setPvCode(cursor.getString(cursor.getColumnIndexOrThrow("pvcode")));
                    card.setWork_id(cursor.getInt(cursor.getColumnIndexOrThrow("work_id")));
                    card.setWork_name(cursor.getString(cursor.getColumnIndexOrThrow("work_name")));
                    card.setInspection_id(cursor.getString(cursor.getColumnIndexOrThrow("inspection_id")));
                    card.setInspectedDate(cursor.getString(cursor.getColumnIndexOrThrow("inspection_date")));
                    card.setWork_status_id(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("status_id"))));
                    card.setWork_status(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    card.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    card.setInspection_by_officer(cursor.getString(cursor.getColumnIndexOrThrow("inspection_by_officer")));
                    card.setWork_type_name(cursor.getString(cursor.getColumnIndexOrThrow("work_type_name")));
                    card.setTotal_cout(cursor.getInt(cursor.getColumnIndexOrThrow("total_count")));
                    card.setSatisfied_cout(cursor.getInt(cursor.getColumnIndexOrThrow("satisfied_count")));
                    card.setUnsatisfied_cout(cursor.getInt(cursor.getColumnIndexOrThrow("unsatisfied_count")));
                    card.setNeedimprovement_cout(cursor.getInt(cursor.getColumnIndexOrThrow("need_improvement_count")));
                    card.setInspection_by_officer_designation(cursor.getString(cursor.getColumnIndexOrThrow("inspection_by_officer_designation")));

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
    public ArrayList<ModelClass> getSavedWorkList(String type,String work_id,String dcode,String bcode,String pvcode,String inspection_id) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;
        String[] selectionArgs;
        String selection;
        try {
            if(type.equals("all")){
                cursor = db.rawQuery("select * from "+DBHelper.SAVE_WORK_DETAILS,null);
            }
            else {
                selection = "work_id = ? and dcode = ? and bcode = ? and pvcode = ?";
                selectionArgs = new String[]{work_id,dcode, bcode, pvcode};


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
                    card.setDistrictCode(cursor.getString(cursor.getColumnIndexOrThrow("dcode")));
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
                    card.setWork_stage_name(cursor.getString(cursor.getColumnIndexOrThrow("work_stage")));
                    card.setWork_stage_code(cursor.getString(cursor.getColumnIndexOrThrow("work_stage_id")));
                    card.setFlag(cursor.getString(cursor.getColumnIndexOrThrow("flag")));
                    card.setInspection_id(cursor.getString(cursor.getColumnIndexOrThrow("inspection_id")));

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
    public ArrayList<ModelClass> getAll_Stage(String type,String work_group_id,String work_type_id,String work_stage_code) {

        ArrayList<ModelClass> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            if(type.equals("all")){
                cursor = db.rawQuery("select * from " + DBHelper.WORK_STAGE_TABLE, null);
            }else {
//                String Que = "select * from "+DBHelper.WORK_STAGE_TABLE+" where work_stage_order >(select work_stage_order from "+DBHelper.WORK_STAGE_TABLE+" where work_stage_code='"+work_stage_code+"' and work_group_id=" + work_group_id + "  and work_type_id=" + work_type_id + ")  and work_group_id=" + work_group_id + "  and work_type_id=" + work_type_id + " and work_stage_code != 11 order by work_stage_order";
                String Que = "select * from "+DBHelper.WORK_STAGE_TABLE+" where work_group_id=" + work_group_id + "  and work_type_id=" + work_type_id + " order by work_stage_order";

                cursor = db.rawQuery(Que,null);
            }

            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ModelClass card = new ModelClass();
                    card.setWork_group_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.WORK_GROUP_ID)));
                    card.setWork_type_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.WORK_TYPE_ID)));
                    card.setWork_stage_order(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.WORK_STAGE_ORDER)));
                    card.setWork_stage_code(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.WORK_STAGE_CODE)));
                    card.setWork_stage_name(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.WORK_SATGE_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
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
    public void deleteOTHER_CATEGORY_TABLETable() {
        db.execSQL("delete from " + DBHelper.OTHER_CATEGORY_TABLE);
    }
    public void deleteFinYearTable() {
        db.execSQL("delete from " + DBHelper.FINANCIAL_YEAR_TABLE_NAME);
    }
    public void deleteSaveWorkDetailsTable() {
        db.execSQL("delete from " + DBHelper.SAVE_WORK_DETAILS);
    }



    public void deleteWorkStageTable() {
        db.execSQL("delete from " + DBHelper.WORK_STAGE_TABLE);
    }
    public void deleteSAVE_ATR_IMAGESTable() {
        db.execSQL("delete from " + DBHelper.SAVE_ATR_IMAGES);
    }
    public void deleteATR_WORK_LISTTable() {
        db.execSQL("delete from " + DBHelper.ATR_WORK_LIST);
    }
    public void deleteSAVE_ATR_WORK_DETAILSTable() {
        db.execSQL("delete from " + DBHelper.SAVE_ATR_WORK_DETAILS);
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
        deleteOTHER_CATEGORY_TABLETable();
        deleteWorkStageTable();
        deleteSAVE_ATR_IMAGESTable();
        deleteATR_WORK_LISTTable();
        deleteSAVE_ATR_WORK_DETAILSTable();
    }



}
