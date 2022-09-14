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
import com.nic.samathuvapuram.databinding.SaveBlkHouseDetailsActivityBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.ProgressHUD;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SaveBlkHouseDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private com.nic.samathuvapuram.databinding.SaveBlkHouseDetailsActivityBinding saveHouseDetailosActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> Community = new ArrayList<>();
    private List<ModelClass> Phototype = new ArrayList<>();
    private List<ModelClass> Current_house_usage = new ArrayList<>();

    String isGender="";
    String isBeneficiary="";
    int samathuvapuram_id;
    int house_serial_number;
    int current_house_usage_id;
    String current_house_usage;
    int photo_type_id;
    int min_img_count;
    int max_img_count;
    int selectCommunity=0;
    int current_community_category_id =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saveHouseDetailosActivityBinding = DataBindingUtil.setContentView(this, R.layout.save_blk_house_details_activity);
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
        String current_name_of_the_beneficiary= getIntent().getStringExtra("current_name_of_the_beneficiary");
        String is_house_owned_by_sanctioned_beneficiary= getIntent().getStringExtra("is_house_owned_by_sanctioned_beneficiary");
        current_community_category_id= getIntent().getIntExtra("current_community_category_id",0);
        int current_house_usage_id_value= getIntent().getIntExtra("current_house_usage",0);
        String current_gender= getIntent().getStringExtra("current_gender");
        String current_usage= getIntent().getStringExtra("current_usage");


        communityFilterSpinner();
        phototypeFilterSpinner();
        current_house_usageFilterSpinner();
        saveHouseDetailosActivityBinding.currentHouseUsageTv.setVisibility(View.GONE);
        saveHouseDetailosActivityBinding.currentHouseUsageLayout.setVisibility(View.GONE);

        if(current_name_of_the_beneficiary!=null && !current_name_of_the_beneficiary.equals("")){
            saveHouseDetailosActivityBinding.benificiaryName.setText(current_name_of_the_beneficiary);
        }else {
            saveHouseDetailosActivityBinding.benificiaryName.setText("");
        }


        if(is_house_owned_by_sanctioned_beneficiary!=null && !is_house_owned_by_sanctioned_beneficiary.equals("") && is_house_owned_by_sanctioned_beneficiary.equals("Y")){
            isBeneficiary = "Y";
            saveHouseDetailosActivityBinding.beneficiaryYes.setChecked(true);
            saveHouseDetailosActivityBinding.beneficiaryNo.setChecked(false);
            saveHouseDetailosActivityBinding.currentHouseUsageTv.setVisibility(View.GONE);
            saveHouseDetailosActivityBinding.currentHouseUsageLayout.setVisibility(View.GONE);
            saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setSelection(0);
        } else if(is_house_owned_by_sanctioned_beneficiary!=null && !is_house_owned_by_sanctioned_beneficiary.equals("") && is_house_owned_by_sanctioned_beneficiary.equals("N")){

            isBeneficiary = "N";
            saveHouseDetailosActivityBinding.beneficiaryYes.setChecked(false);
            saveHouseDetailosActivityBinding.beneficiaryNo.setChecked(true);
            saveHouseDetailosActivityBinding.currentHouseUsageTv.setVisibility(View.VISIBLE);
            saveHouseDetailosActivityBinding.currentHouseUsageLayout.setVisibility(View.VISIBLE);
            saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setSelection(0);
        }

        saveHouseDetailosActivityBinding.communitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setCommunityCode(Community.get(position).getCommunity_category_id());

                }else {
                    prefManager.setCommunityCode("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setCurrentUsageID(Current_house_usage.get(position).getCurrent_usage_id());
                    current_house_usage_id=Current_house_usage.get(position).getCurrent_usage_id();
                    current_house_usage=Current_house_usage.get(position).getCurrent_usage();
                }else {
                    prefManager.setCurrentUsageID("");
                    current_house_usage="";
                    current_house_usage_id=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        saveHouseDetailosActivityBinding.radioM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isGender="M";
                    saveHouseDetailosActivityBinding.radioGroup.clearCheck();
                    saveHouseDetailosActivityBinding.radioM.setEnabled(true);
                    saveHouseDetailosActivityBinding.radioM.setChecked(true);
                    saveHouseDetailosActivityBinding.radioF.setChecked(false);
                    saveHouseDetailosActivityBinding.radioT.setChecked(false);
                    Log.d("isGender",isGender);
                }
            }
        });
        saveHouseDetailosActivityBinding.radioF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isGender="F";
                    saveHouseDetailosActivityBinding.radioGroup.clearCheck();
                    saveHouseDetailosActivityBinding.radioF.setEnabled(true);
                    saveHouseDetailosActivityBinding.radioF.setChecked(true);
                    saveHouseDetailosActivityBinding.radioM.setChecked(false);
                    saveHouseDetailosActivityBinding.radioT.setChecked(false);
                    Log.d("isGender",isGender);
                }
            }
        });
        saveHouseDetailosActivityBinding.radioT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isGender="T";
                    saveHouseDetailosActivityBinding.radioGroup.clearCheck();
                    saveHouseDetailosActivityBinding.radioT.setEnabled(true);
                    saveHouseDetailosActivityBinding.radioT.setChecked(true);
                    saveHouseDetailosActivityBinding.radioM.setChecked(false);
                    saveHouseDetailosActivityBinding.radioF.setChecked(false);
                    Log.d("isGender",isGender);
                }
            }
        });

        saveHouseDetailosActivityBinding.beneficiaryYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isBeneficiary = "Y";
                    saveHouseDetailosActivityBinding.beneficiaryNo.setChecked(false);
                    saveHouseDetailosActivityBinding.currentHouseUsageTv.setVisibility(View.GONE);
                    saveHouseDetailosActivityBinding.currentHouseUsageLayout.setVisibility(View.GONE);
                    saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setSelection(0);
                }else {
                }
            }
        });
        saveHouseDetailosActivityBinding.beneficiaryNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isBeneficiary = "N";
                    saveHouseDetailosActivityBinding.beneficiaryYes.setChecked(false);
                    saveHouseDetailosActivityBinding.currentHouseUsageTv.setVisibility(View.VISIBLE);
                    saveHouseDetailosActivityBinding.currentHouseUsageLayout.setVisibility(View.VISIBLE);
                    saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setSelection(0);
                }else {
                }
            }
        });

        if(current_gender.equals("F")){
            isGender="F";
            saveHouseDetailosActivityBinding.radioGroup.clearCheck();
            saveHouseDetailosActivityBinding.radioF.setEnabled(true);
            saveHouseDetailosActivityBinding.radioF.setChecked(true);
            saveHouseDetailosActivityBinding.radioM.setChecked(false);
            saveHouseDetailosActivityBinding.radioT.setChecked(false);
        }else if(current_gender.equals("M")){
            isGender="M";
            saveHouseDetailosActivityBinding.radioGroup.clearCheck();
            saveHouseDetailosActivityBinding.radioM.setEnabled(true);
            saveHouseDetailosActivityBinding.radioM.setChecked(true);
            saveHouseDetailosActivityBinding.radioF.setChecked(false);
            saveHouseDetailosActivityBinding.radioT.setChecked(false);
        }else if(current_gender.equals("T")){
            isGender="T";
            saveHouseDetailosActivityBinding.radioGroup.clearCheck();
            saveHouseDetailosActivityBinding.radioT.setEnabled(true);
            saveHouseDetailosActivityBinding.radioT.setChecked(true);
            saveHouseDetailosActivityBinding.radioM.setChecked(false);
            saveHouseDetailosActivityBinding.radioF.setChecked(false);
        }

        if(current_house_usage_id_value > 0){
            prefManager.setCurrentUsageID(current_house_usage_id_value);
            current_house_usage_id=current_house_usage_id_value;
            current_house_usage=current_usage;
        }else {
            prefManager.setCurrentUsageID("");
            current_house_usage="";
            current_house_usage_id=0;
        }

        if(current_community_category_id > 0){
            prefManager.setCommunityCode(current_community_category_id);
        }else {

        }prefManager.setCommunityCode("");

        saveHouseDetailosActivityBinding.communitySpinner.setSelection(getSpinnerIndex(current_community_category_id));
        saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setSelection(gethouse_usageSpinnerIndex(current_house_usage_id_value));
    }
    private int getSpinnerIndex(int myString){
        int index = 0;
        try {

            for (int i=0;i<Community.size();i++){
                if (Community.get(i).getCommunity_category_id()== (myString)){
                    index = i;
                }
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return index;

    }
    private int gethouse_usageSpinnerIndex(int myString){
        int index = 0;
        try {

            for (int i=0;i<Current_house_usage.size();i++){
                if (Current_house_usage.get(i).getCurrent_usage_id()== (myString)){
                    index = i;
                }
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return index;

    }

    public void communityFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.COMMUNITY , null);

        Community.clear();
        ModelClass list = new ModelClass();
        list.setCommunity_category("Select Community");
        Community.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int community_id = cursor.getInt(cursor.getColumnIndexOrThrow("community_category_id"));
                    String community_name = cursor.getString(cursor.getColumnIndexOrThrow("community_category"));

                    modelClass.setCommunity_category_id(community_id);
                    modelClass.setCommunity_category(community_name);

                    Community.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Communityspinnersize", "" + Community.size());

        }
        saveHouseDetailosActivityBinding.communitySpinner.setAdapter(new CommonAdapter(this, Community, "CommunityList"));
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
        prefManager.setPhototype(Phototype.get(1).getPhoto_type_name());
        min_img_count=Phototype.get(1).getMin_no_of_photos();
        max_img_count=Phototype.get(1).getMax_no_of_photos();
        photo_type_id=Phototype.get(1).getPhoto_type_id();
        saveHouseDetailosActivityBinding.photosTypeSpinner.setAdapter(new CommonAdapter(this, Phototype, "Phototype"));
    }
    public void current_house_usageFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.CURRENT_HOUSE_USAGE , null);

        Current_house_usage.clear();
        ModelClass list = new ModelClass();
        list.setCurrent_usage("Select Current house usage");
        Current_house_usage.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int current_usage_id = cursor.getInt(cursor.getColumnIndexOrThrow("current_usage_id"));
                    String current_usage = cursor.getString(cursor.getColumnIndexOrThrow("current_usage"));
                    String is_beneficiary_detail_required = cursor.getString(cursor.getColumnIndexOrThrow("is_beneficiary_detail_required"));

                    modelClass.setCurrent_usage_id(current_usage_id);
                    modelClass.setCurrent_usage(current_usage);
                    modelClass.setIs_beneficiary_detail_required(is_beneficiary_detail_required);

                    Current_house_usage.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("house_usagespinnersize", "" + Current_house_usage.size());

        }
        saveHouseDetailosActivityBinding.currentHouseUsageSpinner.setAdapter(new CommonAdapter(this, Current_house_usage, "Current_house_usage"));
    }

    public  void gotoCameraScreen(){
        dbData.open();
        ArrayList<ModelClass> ImageCount = dbData.getParticularSavedHouseImage(String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number));

       /* if (ImageCount.size() < 1) {
            validate();
        }else {
            Utils.showAlert(this,"Already photo saved for this house");
        }*/
        validate();


    }

    private void validate() {
        if(saveHouseDetailosActivityBinding.benificiaryName.getText().toString()!=null && !saveHouseDetailosActivityBinding.benificiaryName.getText().toString().equals("")){
            if(isBeneficiary != null && !isBeneficiary.equals("") ){
                if( isBeneficiary.equals("Y")){
                    if(isGender != null && !isGender.equals("")){
                        if(prefManager.getCommunityCode() != null && !prefManager.getCommunityCode().equals("")){
                            Intent camera_screen = new Intent(SaveBlkHouseDetailsActivity.this, CameraScreen.class);
                            camera_screen.putExtra("samathuvapuram_id",samathuvapuram_id);
                            camera_screen.putExtra("Type","HOUSE");
                            camera_screen.putExtra("house_serial_number",house_serial_number);
                            camera_screen.putExtra("is_house_owned_by_sanctioned_beneficiary",isBeneficiary);
                            camera_screen.putExtra("current_house_usage","");
                            camera_screen.putExtra("current_house_usage_id",0);
                            camera_screen.putExtra("current_name_of_the_beneficiary",saveHouseDetailosActivityBinding.benificiaryName.getText().toString());
                            camera_screen.putExtra("current_gender",isGender);
                            camera_screen.putExtra("current_community_category_id",prefManager.getCommunityCode());
                            camera_screen.putExtra("photo_type_id",photo_type_id);
                            camera_screen.putExtra("min_no_of_photos",min_img_count);
                            camera_screen.putExtra("max_no_of_photos",max_img_count);
                            startActivity(camera_screen);
                        }else {
                            Utils.showAlert(this,"Select community!");
                        }
                    }else {
                        Utils.showAlert(this,"Select gender!");
                    }
                }else if( isBeneficiary.equals("N")){
                    if(prefManager.getCurrentUsageID() != null && !prefManager.getCurrentUsageID().equals("")){
                        if(isGender != null && !isGender.equals("")){
                            if(prefManager.getCommunityCode() != null && !prefManager.getCommunityCode().equals("")){
                                Intent camera_screen = new Intent(SaveBlkHouseDetailsActivity.this, CameraScreen.class);
                                camera_screen.putExtra("samathuvapuram_id",samathuvapuram_id);
                                camera_screen.putExtra("Type","HOUSE");
                                camera_screen.putExtra("house_serial_number",house_serial_number);
                                camera_screen.putExtra("is_house_owned_by_sanctioned_beneficiary",isBeneficiary);
                                camera_screen.putExtra("current_house_usage",current_house_usage);
                                camera_screen.putExtra("current_house_usage_id",current_house_usage_id);
                                camera_screen.putExtra("current_name_of_the_beneficiary",saveHouseDetailosActivityBinding.benificiaryName.getText().toString());
                                camera_screen.putExtra("current_gender",isGender);
                                camera_screen.putExtra("current_community_category_id",prefManager.getCommunityCode());
                                camera_screen.putExtra("photo_type_id",photo_type_id);
                                camera_screen.putExtra("min_no_of_photos",min_img_count);
                                camera_screen.putExtra("max_no_of_photos",max_img_count);
                                startActivity(camera_screen);
                            }else {
                                Utils.showAlert(this,"Select community!");
                            }
                        }else {
                            Utils.showAlert(this,"Select gender!");
                        }
                    }else {
                        Utils.showAlert(this,"Select current house usage!");
                    }
                }
            }else {
                Utils.showAlert(this,"Select house owned by sanctioned beneficiary or not!");
            }

        }else {
            Utils.showAlert(this,"Enter Beneficiary Name!");
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
