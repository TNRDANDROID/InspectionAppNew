package com.nic.samathuvapuram.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.adapter.CommonAdapter;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.SaveAeHouseDetailsActivityBinding;
import com.nic.samathuvapuram.databinding.SaveBlkHouseDetailsActivityBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.ProgressHUD;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SaveAeHouseDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private SaveAeHouseDetailsActivityBinding saveHouseDetailosActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> condition_of_house_list = new ArrayList<>();
    private List<ModelClass> work_scheme_list = new ArrayList<>();
    private List<ModelClass> work_type_list = new ArrayList<>();
    private List<ModelClass> Phototype = new ArrayList<>();
    int samathuvapuram_id=0;
    int house_serial_number=0;
    int condition_of_house_id=0;
    String condition_of_house="0";
    String scheme="";
    String work="";
    int scheme_group_id=0;
    int scheme_id=0;
    int work_group_id=0;
    int work_type_id=0;
    int photo_type_id=0;
    int min_img_count=0;
    int max_img_count=0;
    int flag=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saveHouseDetailosActivityBinding = DataBindingUtil.setContentView(this, R.layout.save_ae_house_details_activity);
        saveHouseDetailosActivityBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        samathuvapuram_id= getIntent().getIntExtra("samathuvapuram_id",0);
        house_serial_number= getIntent().getIntExtra("house_serial_number",0);
        int condition_of_house_id_val= getIntent().getIntExtra("condition_of_house_id",0);
        int scheme_group_id_val= getIntent().getIntExtra("scheme_group_id",0);
        int scheme_id_val= getIntent().getIntExtra("scheme_id",0);
        int work_group_id_val= getIntent().getIntExtra("work_group_id",0);
        int work_type_id_val= getIntent().getIntExtra("work_type_id",0);
        String estimate_cost_required= getIntent().getStringExtra("estimate_cost_required");
        String condition_of_house_value= getIntent().getStringExtra("condition_of_house");


        condition_of_houseSpinner();
        work_schemeSpinner();
//        work_typeSpinner();
        phototypeFilterSpinner();

        saveHouseDetailosActivityBinding.photosTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setPhototype(Phototype.get(position).getPhoto_type_name());
                    min_img_count=Phototype.get(position).getMin_no_of_photos();
                    max_img_count=Phototype.get(position).getMax_no_of_photos();
                    photo_type_id=Phototype.get(position).getPhoto_type_id();
                }else {
                    prefManager.setPhototype("");
                    min_img_count=0;
                    max_img_count=0;
                    photo_type_id=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveHouseDetailosActivityBinding.conditionOfHouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    condition_of_house_id=condition_of_house_list.get(position).getCondition_of_house_id();
                    condition_of_house=condition_of_house_list.get(position).getCondition_of_house();

                }else {
                    condition_of_house_id=0;
                    condition_of_house="0";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }});
        saveHouseDetailosActivityBinding.workSchemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    flag=flag+1;
                    scheme_group_id=work_scheme_list.get(position).getScheme_group_id();
                    scheme_id=work_scheme_list.get(position).getScheme_id();
                    scheme=work_scheme_list.get(position).getScheme_name();
                    work_typeSpinner();
                }else {
                    saveHouseDetailosActivityBinding.workTypeSpinner.setAdapter(null);
                    scheme_group_id=0;
                    scheme_id=0;
                    scheme="=";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }});
        saveHouseDetailosActivityBinding.workTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position > 0) {
                        work_group_id=work_type_list.get(position).getWork_group_id();
                        work_type_id=work_type_list.get(position).getWork_type_id();
                        work=work_type_list.get(position).getWork_name();
                    }else {
                        work_group_id=0;
                        work_type_id=0;
                        work="";
                    }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }});


        if(condition_of_house_id > 0){
            condition_of_house_id=condition_of_house_id_val;
            condition_of_house=condition_of_house_value;
        }else {
            condition_of_house_id=0;
            condition_of_house="0";
        }
        if(estimate_cost_required != null && !estimate_cost_required .isEmpty() && !estimate_cost_required.equals("")){
            saveHouseDetailosActivityBinding.estimateCostRequired.setText(estimate_cost_required);
        }else {
            saveHouseDetailosActivityBinding.estimateCostRequired.setText("");
        }

          if(scheme_group_id_val > 0 && scheme_id_val > 0){
              scheme_group_id=scheme_group_id_val;
              scheme_id=scheme_id_val;
              scheme=getSchemeSpinnerName(scheme_group_id_val,scheme_id_val);
              work_typeSpinner();
        }else {
              saveHouseDetailosActivityBinding.workTypeSpinner.setAdapter(null);
              scheme_group_id=0;
              scheme_id=0;
              scheme="=";
        }


        saveHouseDetailosActivityBinding.conditionOfHouseSpinner.setSelection(getSpinnerIndex(condition_of_house_id_val));
        saveHouseDetailosActivityBinding.workSchemeSpinner.setSelection(getSchemeSpinnerIndex(scheme_group_id_val,scheme_id_val));


        if(scheme_group_id_val > 0 && scheme_id_val > 0 && work_group_id_val > 0 && work_type_id_val > 0){
            work_group_id=work_group_id_val;
            work_type_id=work_type_id_val;
            work=getWorkTypeSpinnerName(scheme_group_id_val,scheme_id_val,work_group_id_val,work_type_id_val);
        }else {
            work_group_id=0;
            work_type_id=0;
            work="";
        }


    }

    private int getSpinnerIndex(int myString){
        int index = 0;
        try {
            for (int i=0;i<condition_of_house_list.size();i++){
                if (condition_of_house_list.get(i).getCondition_of_house_id()== (myString)){
                    index = i;
                }
            }
        }catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }
    private int getSchemeSpinnerIndex(int val1,int val2){
        int index = 0;
        try {
            for (int i=0;i<work_scheme_list.size();i++){
                if (work_scheme_list.get(i).getScheme_group_id()== (val1) && work_scheme_list.get(i).getScheme_id()== (val2)){
                    index = i;
                }
            }
        }catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }
    private int getWorkTypeSpinnerIndex(int val1,int val2,int val3,int val4){
        int index = 0;
        try {
            for (int i=0;i<work_type_list.size();i++){
                if (work_type_list.get(i).getScheme_group_id()== (val1) && work_type_list.get(i).getScheme_id()== (val2)
                && work_type_list.get(i).getWork_group_id()== (val3) && work_type_list.get(i).getWork_type_id()== (val4)){
                    index = i;
                }
            }
        }catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }
    private String  getSchemeSpinnerName(int val1,int val2){
        String index = "";
        try {
            for (int i=0;i<work_scheme_list.size();i++){
                if (work_scheme_list.get(i).getScheme_group_id()== (val1) && work_scheme_list.get(i).getScheme_id()== (val2)){
                    index = work_scheme_list.get(i).getScheme_name();
                }
            }
        }catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }
    private String  getWorkTypeSpinnerName(int val1,int val2,int val3,int val4){
        String index = "";
        try {
            for (int i=0;i<work_type_list.size();i++){
                if (work_type_list.get(i).getScheme_group_id()== (val1) && work_type_list.get(i).getScheme_id()== (val2)
                        && work_type_list.get(i).getWork_group_id()== (val3) && work_type_list.get(i).getWork_type_id()== (val4)){
                    index = work_type_list.get(i).getWork_name();
                }
            }
        }catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }


    public void phototypeFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.PHOTOS_TYPE , null);

        Phototype.clear();
        ModelClass list = new ModelClass();
        list.setPhoto_type_name("Select photo type");
        Phototype.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int photo_type_id = cursor.getInt(cursor.getColumnIndexOrThrow("photo_type_id"));
                    int form_id = cursor.getInt(cursor.getColumnIndexOrThrow("form_id"));
                    String photo_type_name = cursor.getString(cursor.getColumnIndexOrThrow("photo_type_name"));
                    int min_no_of_photos = cursor.getInt(cursor.getColumnIndexOrThrow("min_no_of_photos"));
                    int max_no_of_photos = cursor.getInt(cursor.getColumnIndexOrThrow("max_no_of_photos"));

                    modelClass.setPhoto_type_id(photo_type_id);
                    modelClass.setForm_id(form_id);
                    modelClass.setPhoto_type_name(photo_type_name);
                    modelClass.setMin_no_of_photos(min_no_of_photos);
                    modelClass.setMax_no_of_photos(max_no_of_photos);

                    Phototype.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Phototypespinnersize", "" + Phototype.size());

        }
        prefManager.setPhototype(Phototype.get(2).getPhoto_type_name());
        min_img_count=Phototype.get(2).getMin_no_of_photos();
        max_img_count=Phototype.get(2).getMax_no_of_photos();
        photo_type_id=Phototype.get(2).getPhoto_type_id();
        saveHouseDetailosActivityBinding.photosTypeSpinner.setAdapter(new CommonAdapter(this, Phototype, "Phototype"));
    }


    public void condition_of_houseSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.CONDITION_OF_HOUSE , null);

        condition_of_house_list.clear();
        ModelClass list = new ModelClass();
        list.setCondition_of_house("Select condition of house");
        condition_of_house_list.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int condition_of_house_id = cursor.getInt(cursor.getColumnIndexOrThrow("condition_of_house_id"));
                    String condition_of_house = cursor.getString(cursor.getColumnIndexOrThrow("condition_of_house"));

                    modelClass.setCondition_of_house_id(condition_of_house_id);
                    modelClass.setCondition_of_house(condition_of_house);

                    condition_of_house_list.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("con_of_house_listsize", "" + condition_of_house_list.size());

        }
        saveHouseDetailosActivityBinding.conditionOfHouseSpinner.setAdapter(new CommonAdapter(this, condition_of_house_list, "condition_of_house_list"));
    }
    public void work_schemeSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.WORK_SCHEME , null);

        work_scheme_list.clear();
        ModelClass list = new ModelClass();
        list.setScheme_name("Select work scheme");
        work_scheme_list.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int scheme_group_id = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_group_id"));
                    int scheme_id = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_id"));
                    int scheme_disp_order = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_disp_order"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String has_sub_type = cursor.getString(cursor.getColumnIndexOrThrow("has_sub_type"));
                    String existing_condition_required = cursor.getString(cursor.getColumnIndexOrThrow("existing_condition_required"));
                    String photos_required = cursor.getString(cursor.getColumnIndexOrThrow("photos_required"));
                    String scheme_name = cursor.getString(cursor.getColumnIndexOrThrow("scheme_name"));

                    if(category.equals("HOUSE")){
                        modelClass.setScheme_group_id(scheme_group_id);
                        modelClass.setScheme_id(scheme_id);
                        modelClass.setScheme_disp_order(scheme_disp_order);
                        modelClass.setCategory(category);
                        modelClass.setHas_sub_type(has_sub_type);
                        modelClass.setExisting_condition_required(existing_condition_required);
                        modelClass.setPhotos_required(photos_required);
                        modelClass.setScheme_name(scheme_name);
                        work_scheme_list.add(modelClass);
                    }

                } while (cursor.moveToNext());
            }
            Log.d("work_scheme_size", "" + work_scheme_list.size());

        }
        saveHouseDetailosActivityBinding.workSchemeSpinner.setAdapter(new CommonAdapter(this, work_scheme_list, "work_scheme_list"));
    }
    public void work_typeSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.WORK_TYPE , null);

        work_type_list.clear();
        ModelClass list = new ModelClass();
        list.setWork_name("Select work type");
        work_type_list.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int schemegroup_id = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_group_id"));
                    int schemeid = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_id"));
                    int work_group_id = cursor.getInt(cursor.getColumnIndexOrThrow("work_group_id"));
                    int work_type_id = cursor.getInt(cursor.getColumnIndexOrThrow("work_type_id"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String existing_condition_required = cursor.getString(cursor.getColumnIndexOrThrow("existing_condition_required"));
                    String photos_required = cursor.getString(cursor.getColumnIndexOrThrow("photos_required"));
                    String work_name = cursor.getString(cursor.getColumnIndexOrThrow("work_name"));

                    if(scheme_group_id == schemegroup_id && scheme_id == schemeid && category.equals("HOUSE")) {
                        modelClass.setScheme_group_id(schemegroup_id);
                        modelClass.setScheme_id(schemeid);
                        modelClass.setWork_group_id(work_group_id);
                        modelClass.setWork_type_id(work_type_id);
                        modelClass.setCategory(category);
                        modelClass.setExisting_condition_required(existing_condition_required);
                        modelClass.setPhotos_required(photos_required);
                        modelClass.setWork_name(work_name);
                        work_type_list.add(modelClass);
                    }
                } while (cursor.moveToNext());
            }
            Log.d("work_type_list_size", "" + work_type_list.size());

        }
        saveHouseDetailosActivityBinding.workTypeSpinner.setAdapter(new CommonAdapter(this, work_type_list, "work_type_list"));

      if(flag ==1){
          saveHouseDetailosActivityBinding.workTypeSpinner.setSelection(getWorkTypeSpinnerIndex(scheme_group_id,scheme_id,work_group_id,work_type_id));

      }else {
          saveHouseDetailosActivityBinding.workTypeSpinner.setSelection(0);
      }

    }

    public  void gotoCameraScreen(){
        dbData.open();
        ArrayList<ModelClass> ImageCount = dbData.getParticularSavedHouseImage(String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number));

        /*if (ImageCount.size() < 1) {
            validate();
        }else {
            Utils.showAlert(this,"Already photo saved for this house");
        }*/
        validate();


    }

    private void validate() {
        if(condition_of_house_id >0){
            if(scheme_group_id >0 && scheme_id >0){
                if(work_group_id >0 && work_type_id >0){
                    if(saveHouseDetailosActivityBinding.estimateCostRequired.getText().toString() != null && !saveHouseDetailosActivityBinding.estimateCostRequired.getText().toString().equals("")){
                        Intent camera_screen = new Intent(SaveAeHouseDetailsActivity.this, CameraScreen.class);
                        camera_screen.putExtra("samathuvapuram_id",samathuvapuram_id);
                        camera_screen.putExtra("Type","HOUSE");
                        camera_screen.putExtra("house_serial_number",house_serial_number);
                        camera_screen.putExtra("condition_of_house_id",condition_of_house_id);
                        camera_screen.putExtra("condition_of_house",condition_of_house);
                        camera_screen.putExtra("scheme_group_id",scheme_group_id);
                        camera_screen.putExtra("scheme_id",scheme_id);
                        camera_screen.putExtra("scheme",scheme);
                        camera_screen.putExtra("work",work);
                        camera_screen.putExtra("work_group_id",work_group_id);
                        camera_screen.putExtra("work_type_id",work_type_id);
                        camera_screen.putExtra("estimate_cost_required",saveHouseDetailosActivityBinding.estimateCostRequired.getText().toString());
                        camera_screen.putExtra("photo_type_id",photo_type_id);
                        camera_screen.putExtra("min_no_of_photos",min_img_count);
                        camera_screen.putExtra("max_no_of_photos",max_img_count);
                        startActivity(camera_screen);
                    }else {
                        Utils.showAlert(this,"Enter estimate cost required");
                    }
                }else {
                    Utils.showAlert(this,"Select work type!");
                }
            }else {
                Utils.showAlert(this,"Select work scheme!");
            }
        }else {
            Utils.showAlert(this,"Select condition of house!");
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
    

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }




}
