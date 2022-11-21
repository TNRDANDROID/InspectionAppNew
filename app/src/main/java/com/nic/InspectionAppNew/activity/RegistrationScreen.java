package com.nic.InspectionAppNew.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.util.Util;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.databinding.ActivityRegistrationScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.dmoral.toasty.Toasty;

import static com.nic.InspectionAppNew.utils.Utils.showAlert;

public class RegistrationScreen extends AppCompatActivity implements Api.ServerResponseListener{
    private PrefManager prefManager;
    ActivityRegistrationScreenBinding registrationScreenBinding;
    ProgressHUD progressHUD;


    ////Array List
    ArrayList<ModelClass> genderList= new ArrayList<>();
    ArrayList<ModelClass> districtList=new ArrayList<>();
    ArrayList<ModelClass> blockList=new ArrayList<>();
    ArrayList<ModelClass> levelList=new ArrayList<>();
    ArrayList<ModelClass> designationList=new ArrayList<>();

    String gender_code="";
    String dcode="";
    String dcodeSelected="0";
    String bcode="";
    String bcodeSelected="0";
    String level_id="";
    String designation_id="";
    String designation_idSelected="0";
    String key="";
    String profile_data="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registrationScreenBinding =DataBindingUtil.setContentView(this, R.layout.activity_registration_screen);
        registrationScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);

        key=getIntent().getStringExtra("key");
        fetchResponce();
        registrationScreenBinding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){

                    gender_code = genderList.get(position).getGender_code();
                }
                else {
                    gender_code ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    dcode = "";
                    bcode = "";
                    designation_id ="";
                    registrationScreenBinding.block.setAdapter(null);

                    level_id = levelList.get(position).getLocalbody_code();
                    if(level_id.equalsIgnoreCase("S")){
                        registrationScreenBinding.districtLayout.setVisibility(View.GONE);
                        registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
                        registrationScreenBinding.blockLayout.setVisibility(View.GONE);

                    }
                    else if(level_id.equalsIgnoreCase("D")){
                        getDistrictList();
                        registrationScreenBinding.districtLayout.setVisibility(View.VISIBLE);
                        registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
                        registrationScreenBinding.blockLayout.setVisibility(View.GONE);
                    }
                    else {
                        getDistrictList();
                        registrationScreenBinding.districtLayout.setVisibility(View.VISIBLE);
                        registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
                        registrationScreenBinding.blockLayout.setVisibility(View.VISIBLE);
                    }

                    getDesignationList();
                }
                else {
                    level_id ="";
                    dcode = "";
                    bcode = "";
                    designation_id ="";
                    registrationScreenBinding.districtLayout.setVisibility(View.GONE);
                    registrationScreenBinding.blockLayout.setVisibility(View.GONE);
                    registrationScreenBinding.designationLayout.setVisibility(View.GONE);

                    registrationScreenBinding.district.setAdapter(null);
                    registrationScreenBinding.block.setAdapter(null);
                    registrationScreenBinding.designation.setAdapter(null);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registrationScreenBinding.district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    dcode = districtList.get(position).getDistrictCode();
                    getBlockList();
                }
                else {
                    dcode ="";
                    bcode = "";
                    registrationScreenBinding.block.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    bcode = blockList.get(position).getBlockCode();
                }
                else {
                    bcode ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    designation_id = designationList.get(position).getDesig_code();
                }
                else {
                    designation_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        registrationScreenBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldValidation();
            }
        });
        registrationScreenBinding.tick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!registrationScreenBinding.mobileNo.getText().toString().isEmpty()&&Utils.isValidMobile1(registrationScreenBinding.mobileNo.getText().toString())){
                        validate(registrationScreenBinding.mobileNo.getText().toString());
                       /* registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_check_sign_icon));
                        registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);*/
                    }
                    else {
                        Utils.showAlert(RegistrationScreen.this,"Enter Valid Mobile Number!");
                    }
            }
        });
        registrationScreenBinding.mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
                registrationScreenBinding.detailsLayout.setVisibility(View.GONE);
                registrationScreenBinding.officeAddress.setText("");
                registrationScreenBinding.emailId.setText("");
                registrationScreenBinding.level.setSelection(0);
                registrationScreenBinding.designation.setSelection(0);
                registrationScreenBinding.genderSpinner.setSelection(0);
                registrationScreenBinding.district.setSelection(0);
                registrationScreenBinding.block.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(key.equalsIgnoreCase("login")){
            registrationScreenBinding.detailsLayout.setVisibility(View.GONE);
            registrationScreenBinding.mobileNo.setEnabled(true);
            registrationScreenBinding.tick1.setVisibility(View.VISIBLE);
            registrationScreenBinding.btnRegister.setText("Register");
        }
        else {
            registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.mobileNo.setEnabled(false);
            registrationScreenBinding.tick1.setVisibility(View.GONE);
            registrationScreenBinding.btnRegister.setText("Update");
            profile_data=getIntent().getStringExtra("profile_data");
            try
            {
                JSONObject jsonObject = new JSONObject(profile_data);
                System.out.println("JSON Object: "+jsonObject);
                setProfileData(jsonObject.getJSONArray(AppConstant.JSON_DATA));
            }
            catch (JSONException e)
            {
                System.out.println("Error "+e.toString());
            }


        }
    }

    private void validate(String mobile) {
        if (Utils.isOnline()) {
            try {
                new ApiService(RegistrationScreen.this).makeJSONObjectRequest("MobileVerify", Api.Method.POST, UrlGenerator.getOpenUrl(),  jsonParams(), "not cache", RegistrationScreen.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(RegistrationScreen.this,"No Internet Connection!");
        }


    }
    public  JSONObject  jsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "Verify_mobile_number");
        dataSet.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
        Log.d("mobile_number", "" + dataSet);
        return dataSet;
    }

    private void fetchResponce() {
        if(Utils.isOnline()){
            getStageLevelList();
            getDesignationList();
            getGenderList();

        }
        else {
            Utils.showAlert(RegistrationScreen.this,"No Internet");
        }
    }

    public void getGenderList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Gender", Api.Method.POST, UrlGenerator.getOpenUrl(), genderParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getStageLevelList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Level", Api.Method.POST, UrlGenerator.getOpenUrl(), stageLevelParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getDesignationList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Designation", Api.Method.POST, UrlGenerator.getOpenUrl(), designationParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getOpenUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getOpenUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject districtListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID,AppConstant.KEY_DISTRICT_LIST_ALL);
        Log.d("object", "" + dataSet);
        return dataSet;
    }

    public JSONObject blockListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BLOCK_LIST_DISTRICT_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        Log.d("object", "" + dataSet);
        return dataSet;
    }
    public JSONObject genderParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_profile_gender");
        return dataSet;
    }
    public JSONObject stageLevelParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_profile_level");
        return dataSet;
    }
    public JSONObject designationParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_mobile_designation");
        dataSet.put("level_id", level_id);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status ;
            String response ;
            if ("MobileVerify".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, "Mobile number verified Successfully!");
                    registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_check_sign_icon));
                    registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                Log.d("OTP_RESEND", "" + responseObj.toString());

            }

            if ("Gender".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                   try {
                       JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                       //prefManager.setGenderList(jsonarray.toString());
                       loadGenderList(jsonarray);
                       Log.d("Gender", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }
                }

            }
            if ("Level".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    try {
                        JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                        loadLevelList(jsonarray);
                        Log.d("Level", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
            if ("Designation".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    try {
                        JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                        loadDesignationList(jsonarray);
                        Log.d("Designation", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
            if ("DistrictList".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new  InsertDistrictTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                //Log.d("DistrictList", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
            }
            if ("BlockList".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new  InsertBlockTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.getString(AppConstant.KEY_MESSAGE));
                    registrationScreenBinding.block.setAdapter(null);
                }
                //Log.d("BlockList", "" + responseObj.getJSONArray(AppConstant.JSON_DATA));
            }

            if ("registration".equals(urlType) && responseObj != null) {
                Log.d("registration", "" + responseObj.toString());
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    Toasty.success(RegistrationScreen.this,responseObj.getString("MESSAGE"), Toast.LENGTH_SHORT).show();
                    gotoOtpVerification();
                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_MESSAGE));
                }

            }
            if ("update".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, "User data updated successfully!");
                    getProfileData();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           onBackPress();
                        }
                    }, 1000);

                }
//                Log.d("update", "" + responseDecryptedBlockKey);
            }
            if ("getProfileData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);

                Log.d("registration", "" + jsonObject.toString());
                status  = jsonObject.getString(AppConstant.KEY_STATUS);
                response = jsonObject.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    if (jsonObject.getJSONArray(AppConstant.JSON_DATA).length() > 0) {
                        JSONArray jsonArray=jsonObject.getJSONArray(AppConstant.JSON_DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String name=jsonArray.getJSONObject(i).getString("name");
                            String mobile_number=jsonArray.getJSONObject(i).getString("mobile");
                            String gender=jsonArray.getJSONObject(i).getString("gender");
                            String level=jsonArray.getJSONObject(i).getString("level");
                            String designation_code=jsonArray.getJSONObject(i).getString("desig_code");
                            String designation=jsonArray.getJSONObject(i).getString("desig_name");
                            String dcode=jsonArray.getJSONObject(i).getString("dcode");
                            String bcode=jsonArray.getJSONObject(i).getString("bcode");
                            String office_address=jsonArray.getJSONObject(i).getString("office_address");
                            String email=jsonArray.getJSONObject(i).getString("email");
                            prefManager.setDesignation(designation);
                            prefManager.setName(String.valueOf(name));
                        }
                    }

                }else {
                    showAlert(this, jsonObject.getString(AppConstant.KEY_MESSAGE));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getProfileData() {
        if (Utils.isOnline()) {
            try {
                new ApiService(RegistrationScreen.this).makeJSONObjectRequest("getProfileData", Api.Method.POST, UrlGenerator.getMainService(),  getProfileJsonParams(), "not cache", RegistrationScreen.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(RegistrationScreen.this,"No Internet Connection!");
        }
    }
    public JSONObject getProfileJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), getProfileParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("getProfile", "" + dataSet);
        return dataSet;
    }
    public  JSONObject getProfileParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "work_inspection_profile_list");
        Log.d("getProfile", "" + dataSet);
        return dataSet;
    }
    private void setProfileData(JSONArray jsonArray) {
        if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String name=jsonArray.getJSONObject(i).getString("name");
                    String mobile_number=jsonArray.getJSONObject(i).getString("mobile");
                    String gender=jsonArray.getJSONObject(i).getString("gender");
                    String level=jsonArray.getJSONObject(i).getString("level");
                    String designation=jsonArray.getJSONObject(i).getString("desig_code");
                    String dcode=jsonArray.getJSONObject(i).getString("dcode");
                    String bcode=jsonArray.getJSONObject(i).getString("bcode");
                    String office_address=jsonArray.getJSONObject(i).getString("office_address");
                    String email=jsonArray.getJSONObject(i).getString("email");
                    dcodeSelected=dcode;
                    bcodeSelected=bcode;
                    designation_idSelected=designation;

                    registrationScreenBinding.name.setText(name);
                    registrationScreenBinding.mobileNo.setText(mobile_number);
                    registrationScreenBinding.mobileNo.setFocusable(false);
                    registrationScreenBinding.tick1.setClickable(false);
                    registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
                    registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
                    registrationScreenBinding.officeAddress.setText(office_address);
                    registrationScreenBinding.emailId.setText(email);
                   /* for(int j=0;j<genderList.size();j++){
                        if(genderList.get(j).getGender_code() .equalsIgnoreCase(gender) ){
                            registrationScreenBinding.genderSpinner.setSelection(j);
                        }
                    }
                    for(int m=0;m<levelList.size();m++){
                        if(levelList.get(m).getLocalbody_code() .equalsIgnoreCase(level) ){
                            registrationScreenBinding.level.setSelection(m);
                        }
                    }
                    for(int k=0;k<designationList.size();k++){
                        if(designationList.get(k).getDesig_code() .equalsIgnoreCase(designation) ){
                            registrationScreenBinding.designation.setSelection(k);
                        }
                    }*/


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            registrationScreenBinding.genderSpinner.setSelection(getSpinnerIndex("Gender",genderList,gender));
                            registrationScreenBinding.level.setSelection(getSpinnerIndex("Level",levelList,level));
                            }
                    }, 500);
/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(level.equalsIgnoreCase("D")){
                                registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcode));
                            }
                            else if(level.equalsIgnoreCase("B")){
                                registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcode));
                                registrationScreenBinding.block.setSelection(getSpinnerIndex("Block",blockList,bcode));
                            }
                            registrationScreenBinding.designation.setSelection(getSpinnerIndex("Designation",designationList,designation));
                        }
                    }, 500);
*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private void gotoOtpVerification() {
        Intent intent = new Intent(RegistrationScreen.this,OtpVerfication.class);
        intent.putExtra("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
        intent.putExtra("flag","register");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @SuppressLint("StaticFieldLeak")
    public class InsertDistrictTask extends AsyncTask<JSONArray ,Void ,Void> {
        private  ProgressHUD progressHUD;
        ArrayList<ModelClass> districtList1=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(RegistrationScreen.this, "Loading...", true, false, null);
        }
        @Override
        protected Void doInBackground(JSONArray... params) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = params[0];
                    districtList.clear();

                    ModelClass districtListValue1 = new ModelClass();
                    districtListValue1.setDistrictCode("0");
                    districtListValue1.setDistrictName("Select District");
                    districtList.add(districtListValue1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass districtListValue = new ModelClass();
                        try {
                            districtListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            districtListValue.setDistrictName(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME));

                            districtList.add(districtListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*districtList1.sort((o1, o2)
                    -> o1.getDistrictName().compareTo(
                    o2.getDistrictName()));
            districtList.addAll(districtList1);*/
            registrationScreenBinding.district.setAdapter(new CommonAdapter(RegistrationScreen.this,districtList,"District"));
            if(key.equalsIgnoreCase("home")){
                if(!dcodeSelected.equalsIgnoreCase("0")&& !dcodeSelected.equalsIgnoreCase("")){
                    registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcodeSelected));
                }

            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class InsertBlockTask extends AsyncTask<JSONArray ,Void ,Void> {
        private  ProgressHUD progressHUD;
        ArrayList<ModelClass> blockList1 = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(RegistrationScreen.this, "Loading...", true, false, null);
        }

        @Override
        protected Void doInBackground(JSONArray... params) {

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = params[0];

                    blockList.clear();
                    ModelClass modelClass = new ModelClass();
                    modelClass.setDistrictCode("0");
                    modelClass.setBlockCode("0");
                    modelClass.setBlockName("Select Block");
                    blockList.add(modelClass);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass blocktListValue = new ModelClass();
                        try {
                            blocktListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            blocktListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            blocktListValue.setBlockName(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME));

                            blockList.add(blocktListValue);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* blockList1.sort((o1, o2)
                    -> o1.getDistrictName().compareTo(
                    o2.getDistrictName()));
            blockList.addAll(blockList1);*/
            registrationScreenBinding.block.setAdapter(new CommonAdapter(RegistrationScreen.this,blockList,"Block"));

            if(key.equalsIgnoreCase("home")){
                if(!bcodeSelected.equalsIgnoreCase("0")&& !bcodeSelected.equalsIgnoreCase("")){
                    registrationScreenBinding.block.setSelection(getSpinnerIndex("Block",blockList,bcodeSelected));
                }

            }
        }
    }

    private void loadLevelList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                levelList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setLocalbody_name("Select Level");
                modelClass.setLocalbody_code("0");

                levelList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String localbody_name = jsonobject.getString("localbody_name");
                    String localbody_code = (jsonobject.getString("localbody_code"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setLocalbody_name(localbody_name);
                    roadListValue.setLocalbody_code(localbody_code);

                    levelList.add(roadListValue);
                }
                registrationScreenBinding.level.setAdapter(new CommonAdapter(this, levelList, "LevelList"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void loadGenderList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                genderList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setGender_code("0");
                modelClass.setGender_name_en("Select Gender");

                genderList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String gender_code = jsonobject.getString("gender_code");
                    String gender_name_en = (jsonobject.getString("gender_name_en"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setGender_code(gender_code);
                    roadListValue.setGender_name_en(gender_name_en);

                    genderList.add(roadListValue);
                }
                registrationScreenBinding.genderSpinner.setAdapter(new CommonAdapter(this, genderList, "GenderList"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void loadDesignationList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                designationList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setDesig_code("0");
                modelClass.setDesig_name("Select Designation");

                designationList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String desig_code = jsonobject.getString("desig_code");
                    String desig_name = (jsonobject.getString("desig_name"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setDesig_code(desig_code);
                    roadListValue.setDesig_name(desig_name);

                    designationList.add(roadListValue);
                }
                registrationScreenBinding.designation.setAdapter(new CommonAdapter(this, designationList, "DesignationList"));

                if(key.equalsIgnoreCase("home")){
                    if(!designation_idSelected.equalsIgnoreCase("0")&& !designation_idSelected.equalsIgnoreCase("")){
                        registrationScreenBinding.designation.setSelection(getSpinnerIndex("Designation",designationList,designation_idSelected));
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fieldValidation(){
        if(!registrationScreenBinding.name.getText().toString().isEmpty()){
            if(!registrationScreenBinding.mobileNo.getText().toString().isEmpty()&&Utils.isValidMobile1(registrationScreenBinding.mobileNo.getText().toString())){
                if(!gender_code.isEmpty()){
                    if(!level_id.isEmpty()){
                        if(!designation_id.isEmpty()){
                            if(level_id.equalsIgnoreCase("S")){
                                if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                    if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid1(registrationScreenBinding.emailId.getText().toString())){
                                        saveDataAlert();
                                    }
                                    else {
                                        registrationScreenBinding.emailId.setError("Enter Valid Email");
                                        registrationScreenBinding.emailId.requestFocus();
                                    }
                                }
                                else {
                                    registrationScreenBinding.officeAddress.setError("Enter Address");
                                    registrationScreenBinding.officeAddress.requestFocus();
                                }
                            }
                            else if(level_id.equalsIgnoreCase("D")){
                                if(!dcode.isEmpty()){
                                    if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                        if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid(registrationScreenBinding.emailId.getText().toString())){
                                            saveDataAlert();
                                        }
                                        else {
                                            registrationScreenBinding.emailId.setError("Enter Valid Email");
                                            registrationScreenBinding.emailId.requestFocus();
                                        }
                                    }
                                    else {
                                        registrationScreenBinding.officeAddress.setError("Enter Address");
                                        registrationScreenBinding.officeAddress.requestFocus();
                                    }
                                }
                                else {
                                    Utils.showAlert(RegistrationScreen.this,"Please Select District");
                                }
                            }
                            else {
                                if(!bcode.isEmpty()){
                                    if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                        if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid(registrationScreenBinding.emailId.getText().toString())){
                                            saveDataAlert();
                                        }
                                        else {
                                            registrationScreenBinding.emailId.setError("Enter Email");
                                            registrationScreenBinding.emailId.requestFocus();
                                        }
                                    }
                                    else {
                                        registrationScreenBinding.officeAddress.setError("Enter Address");
                                        registrationScreenBinding.officeAddress.requestFocus();
                                    }
                                }
                                else {
                                    Utils.showAlert(RegistrationScreen.this,"Please Select Block");
                                }
                            }
                        }
                        else {
                            Utils.showAlert(RegistrationScreen.this,"Please Select Designation");
                        }
                    }
                    else {
                        Utils.showAlert(RegistrationScreen.this,"Please Select Level");
                    }
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"Please Select Gender");
                }
            }
            else {
                registrationScreenBinding.mobileNo.setError("Please Enter Valid Mobile Number");
                registrationScreenBinding.mobileNo.requestFocus();
            }
        }
        else {
            registrationScreenBinding.name.setError("Please Enter Name");
            registrationScreenBinding.name.requestFocus();
        }
    }

    private void saveDataAlert(){
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(key.equalsIgnoreCase("login")){
                text.setText("Are you sure you want to register?");
            }else {
                text.setText("Are you sure you want update data to server?");
            }


            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDetails();
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void saveDetails(){
        if(key.equalsIgnoreCase("login")){
            try {
                JSONObject data_set = new JSONObject();
                data_set.put(AppConstant.KEY_SERVICE_ID,"register");
                data_set.put("name",registrationScreenBinding.name.getText().toString());
                data_set.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
                data_set.put("gender",gender_code);
                data_set.put("level",level_id);
                data_set.put("designation",designation_id);
                data_set.put("dcode",dcode);
                if(level_id.equalsIgnoreCase("B")){
                    data_set.put("bcode",bcode);
                }
                data_set.put("office_address",registrationScreenBinding.officeAddress.getText().toString());
                data_set.put("email",registrationScreenBinding.emailId.getText().toString());
                Log.d("register_json",data_set.toString());

                if(Utils.isOnline()){
                    registration(data_set);
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"No Internet");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }else {
            try {
                JSONObject data_set = new JSONObject();
                data_set.put(AppConstant.KEY_SERVICE_ID,"Update_work_inspection_profile");
                data_set.put("name",registrationScreenBinding.name.getText().toString());
                data_set.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
                data_set.put("gender",gender_code);
                data_set.put("level",level_id);
                data_set.put("designation",designation_id);
                data_set.put("dcode",dcode);
                if(level_id.equalsIgnoreCase("B")){
                    data_set.put("bcode",bcode);
                }
                data_set.put("office_address",registrationScreenBinding.officeAddress.getText().toString());
                data_set.put("email",registrationScreenBinding.emailId.getText().toString());

                String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), data_set.toString());
                JSONObject dataSet = new JSONObject();
                dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
                dataSet.put(AppConstant.DATA_CONTENT, authKey);
                Log.d("update_param", "" + dataSet.toString());

                if(Utils.isOnline()){
                    update(dataSet);
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"No Internet");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
    public void registration(JSONObject jsonObject) {
        try {
            new ApiService(this).makeJSONObjectRequest("registration", Api.Method.POST, UrlGenerator.getOpenUrl(), jsonObject, "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update(JSONObject jsonObject) {
        try {
            new ApiService(this).makeJSONObjectRequest("update", Api.Method.POST, UrlGenerator.getMainService(), jsonObject, "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSpinnerIndex(String type,ArrayList<ModelClass> list,String myString){
        int index = 0;
        try {
            if(type.equals("Gender")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getGender_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("Level")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getLocalbody_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("District")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getDistrictCode().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("Designation")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getDesig_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else {
                for (int i=0;i<blockList.size();i++){
                    if (blockList.get(i).getBlockCode().equals(myString)){
                        index = i;
                    }
                }
            }
        }
        catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
