package com.nic.samathuvapuram.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.adapter.SamathuvapuramDetailsAdapter;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ApiService;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.HomeScreenBinding;
import com.nic.samathuvapuram.dialog.MyDialog;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.ProgressHUD;
import com.nic.samathuvapuram.utils.UrlGenerator;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private HomeScreenBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<ModelClass> Village = new ArrayList<>();
    private List<ModelClass> Habitation = new ArrayList<>();
    private ArrayList<ModelClass> samathuvapuramlist = new ArrayList<>();
    private ProgressHUD progressHUD;
    SamathuvapuramDetailsAdapter samathuvapuramDetailsAdapter;


    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.home_screen);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }

        homeScreenBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        homeScreenBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        homeScreenBinding.recycler.setHasFixedSize(true);
        homeScreenBinding.recycler.setNestedScrollingEnabled(false);
        homeScreenBinding.recycler.setFocusable(false);

        homeScreenBinding.synData.setVisibility(View.GONE);
        homeScreenBinding.recycler.setVisibility(View.GONE);
        homeScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
        //fetAllApi();
        /*//Sample
        JSONObject jsonObject = new JSONObject();
        String json = "{\"STATUS\":\"OK\",\"RESPONSE\":\"OK\",\"JSON_DATA\":[{\"id\":1,\"tax\":\"Property tax\"},{\"id\":2,\"tax\":\"Water Charges\"},{\"id\":3,\"tax\":\"Professional Tax\"},{\"id\":4,\"tax\":\"Non Tax\"},{\"id\":5,\"tax\":\"Trade License \"}]}";
        try {  jsonObject = new JSONObject(json); } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""); }
        try {
            if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                new Insert_samathuvapuram_details().execute(jsonObject);
            }
        } catch (JSONException e) { e.printStackTrace(); }*/


        if(Utils.isOnline()){
            fetAllApi();
        }
        else {
            accessController();

        }

        syncButtonVisibility();
        homeScreenBinding.synData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPendingScreen();
            }
        });
    }

    private void fetAllApi() {
        get_menu_access_control();
        current_house_usage();
        community();
        photos_type();
        condition_of_house();
        condition_of_infra();
        estimate_type();
        work_scheme();
        work_type();

    }

    public class fetchSamathuvapuram_details extends AsyncTask<Void, Void,ArrayList<ModelClass>> {
        @Override
        protected ArrayList<ModelClass> doInBackground(Void... params) {
            dbData.open();
            samathuvapuramlist = new ArrayList<>();
            samathuvapuramlist = dbData.getAllsamathuvapuramlist();
            Log.d("samlist_COUNT", String.valueOf(samathuvapuramlist.size()));
            return samathuvapuramlist;
        }

        @Override
        protected void onPostExecute(ArrayList<ModelClass> samathuvapuramlist) {
            super.onPostExecute(samathuvapuramlist);
            if(!Utils.isOnline()) {
                if (samathuvapuramlist.size() == 0) {
                    Utils.showAlert(HomePage.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if (samathuvapuramlist.size() > 0) {
                homeScreenBinding.recycler.setVisibility(View.VISIBLE);
                homeScreenBinding.notFoundTv.setVisibility(View.GONE);
                samathuvapuramDetailsAdapter = new SamathuvapuramDetailsAdapter(HomePage.this, samathuvapuramlist,dbData);
                homeScreenBinding.recycler.setAdapter(samathuvapuramDetailsAdapter);
            }else {
                homeScreenBinding.recycler.setVisibility(View.GONE);
                homeScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }


        }
        }



    private void accessController(){
        dbData.open();
        ArrayList<ModelClass> accessList = new ArrayList<>();
        accessList.addAll(dbData.getAll_Menu_Access_Control());
        if(accessList.size()>0){
            for (int i=0;i<accessList.size();i++){
                if(accessList.get(i).getMenu_name().equals("ae_samathuvapuram_entry")){
                    if(accessList.get(i).getMenu_access_control().equals("Y")){
                        prefManager.setUsertype("ae");
                        if(Utils.isOnline()){
                            blk_ae_samathuvapuram_details();
                        }else {
                            new fetchSamathuvapuram_details().execute();
                        }
                         }
                }
                else if(accessList.get(i).getMenu_name().equals("bdo_samathuvapuram_entry")){
                    if(accessList.get(i).getMenu_access_control().equals("Y")){
                        prefManager.setUsertype("bdo");
                        if(Utils.isOnline()){
                            blk_bdo_samathuvapuram_details();
                        }else {
                            new fetchSamathuvapuram_details().execute();
                        }

                        }
                }
                else {

                }
            }
        }
        else {

        }
    }

    public void blk_bdo_samathuvapuram_details() {
        try {
            new ApiService(this).makeJSONObjectRequest("blk_bdo_samathuvapuram_details", Api.Method.POST, UrlGenerator.getMainService(), blk_bdo_samathuvapuram_details_JSONParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void blk_ae_samathuvapuram_details() {
        try {
            new ApiService(this).makeJSONObjectRequest("blk_ae_samathuvapuram_details", Api.Method.POST, UrlGenerator.getMainService(), blk_ae_samathuvapuram_details_JSONParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void get_menu_access_control() {
        try {
            new ApiService(this).makeJSONObjectRequest("menu_access_control", Api.Method.POST, UrlGenerator.getMainService(), get_menu_access_control_JSONParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject blk_bdo_samathuvapuram_details_JSONParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blk_bdo_samathuvapuram_details_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("samathuvapuram_details", "" + dataSet);
        return dataSet;
    }
    public JSONObject blk_ae_samathuvapuram_details_JSONParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blk_ae_samathuvapuram_details_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("samathuvapuram_details", "" + dataSet);
        return dataSet;
    }
    public JSONObject get_menu_access_control_JSONParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.menu_access_control_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("menu_access_control", "" + dataSet);
        return dataSet;
    }


    public void current_house_usage() {
        try {
            new ApiService(this).makeJSONObjectRequest("current_house_usage", Api.Method.POST, UrlGenerator.getMainService(), current_house_usage_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void community() {
        try {
            new ApiService(this).makeJSONObjectRequest("community", Api.Method.POST, UrlGenerator.getMainService(), community_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void photos_type() {
        try {
            new ApiService(this).makeJSONObjectRequest("photos_type", Api.Method.POST, UrlGenerator.getMainService(), photos_type_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void condition_of_house() {
        try {
            new ApiService(this).makeJSONObjectRequest("condition_of_house", Api.Method.POST, UrlGenerator.getMainService(),condition_of_house_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void condition_of_infra() {
        try {
            new ApiService(this).makeJSONObjectRequest("condition_of_infra", Api.Method.POST, UrlGenerator.getMainService(), condition_of_infra_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void estimate_type() {
        try {
            new ApiService(this).makeJSONObjectRequest("estimate_type", Api.Method.POST, UrlGenerator.getMainService(), estimate_type_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void work_scheme() {
        try {
            new ApiService(this).makeJSONObjectRequest("work_scheme", Api.Method.POST, UrlGenerator.getMainService(), work_scheme_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void work_type() {
        try {
            new ApiService(this).makeJSONObjectRequest("work_type", Api.Method.POST, UrlGenerator.getMainService(), work_type_JsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject current_house_usage_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.current_house_usage_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("current_house_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject community_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.community_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("community_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject photos_type_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.photos_type_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("photos_type_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject condition_of_house_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.condition_of_house_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("condition_of_house_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject condition_of_infra_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.condition_of_infra_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("condition_of_infra_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject estimate_type_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.estimate_type_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("estimate_type_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject work_scheme_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.work_scheme_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("work_scheme_Json", "" + authKey);
        return dataSet;
    }
    public JSONObject work_type_JsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.work_type_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("work_type_Json", "" + authKey);
        return dataSet;
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

    public void logout() {
        dbData.open();
        ArrayList<ModelClass> ImageCount = dbData.getSavedHouseImage();
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(ImageCount.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
        accessController();
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("menu_access_control".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_menu_access_control().execute(jsonObject);
                }
                Log.d("menu_access_control", "" + jsonObject);
            }
            if ("blk_bdo_samathuvapuram_details".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_samathuvapuram_details().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,"No Record Found!");
                }
                Log.d("samathuvapuram_details", "" + jsonObject);
            }
            if ("blk_ae_samathuvapuram_details".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_samathuvapuram_details().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,"No Record Found!");
                }
                Log.d("samathuvapuram_details", "" + jsonObject);
            }
            if ("current_house_usage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_current_house_usage().execute(jsonObject);
                }
                Log.d("current_house_usage", "" + jsonObject);
            }
            if ("community".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_community().execute(jsonObject);
                }
                Log.d("community", "" + jsonObject);
            }
            if ("photos_type".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_photos_type().execute(jsonObject);
                }
                Log.d("photos_type", "" + jsonObject);

            }
            if ("condition_of_house".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_condition_of_house().execute(jsonObject);
                }
                Log.d("condition_of_house", "" + jsonObject);

            }
            if ("condition_of_infra".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_condition_of_infra().execute(jsonObject);
                }
                Log.d("condition_of_infra", "" + jsonObject);

            }
            if ("estimate_type".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_estimate_type().execute(jsonObject);
                }
                Log.d("estimate_type", "" + jsonObject);

            }
            if ("work_scheme".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_work_scheme().execute(jsonObject);
                }
                Log.d("work_scheme", "" + jsonObject);
            }
            if ("work_type".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_work_type().execute(jsonObject);
                }
                Log.d("work_type", "" + jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class Insert_menu_access_control extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteMENU_ACCESS_CONTROLTable();
                JSONArray menu_access_data_array = new JSONArray();
                try {
                    menu_access_data_array = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < menu_access_data_array.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setMenu_id(menu_access_data_array.getJSONObject(i).getInt("menu_id"));
                            ListValue.setMenu_name(menu_access_data_array.getJSONObject(i).getString("menu_name"));
                            ListValue.setMenu_access_control(menu_access_data_array.getJSONObject(i).getString("access_control"));
                            dbData.Insert_menu_access_control(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            accessController();

        }
    }
    public class Insert_current_house_usage extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteCURRENT_HOUSE_USAGETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setCurrent_usage_id(jsonArray.getJSONObject(i).getInt("current_usage_id"));
                            ListValue.setCurrent_usage(jsonArray.getJSONObject(i).getString("current_usage"));
                            ListValue.setIs_beneficiary_detail_required(jsonArray.getJSONObject(i).getString("is_beneficiary_detail_required"));
                            dbData.Insert_current_house_usage(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_samathuvapuram_details extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteSAMATHUVAPURAM_DETAILSTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setSamathuvapuram_id(jsonArray.getJSONObject(i).getInt("samathuvapuram_id"));
                            ListValue.setLocalbody_area_type(jsonArray.getJSONObject(i).getInt("localbody_area_type"));
                            ListValue.setLocalbody_type_id(jsonArray.getJSONObject(i).getInt("localbody_type_id"));
                            ListValue.setDcode(jsonArray.getJSONObject(i).getInt("dcode"));
                            ListValue.setBcode(jsonArray.getJSONObject(i).getInt("bcode"));
                            //ListValue.setTpcode(jsonArray.getJSONObject(i).getInt("tpcode"));
                            if(jsonArray.getJSONObject(i).getString("tpcode").equals("")){
                                ListValue.setTpcode(0);
                            }
                            else {
                                ListValue.setTpcode(jsonArray.getJSONObject(i).getInt("tpcode"));
                            }
                            ListValue.setNo_of_houses_constructed(jsonArray.getJSONObject(i).getInt("no_of_houses_constructed"));
                            ListValue.setPvCode(jsonArray.getJSONObject(i).getString("pvcode"));
                            ListValue.setHabCode(jsonArray.getJSONObject(i).getString("hab_code"));
                            ListValue.setMuncode(jsonArray.getJSONObject(i).getString("muncode"));
                            ListValue.setCorpcode(jsonArray.getJSONObject(i).getString("corpcode"));
                            ListValue.setYear_of_construction(jsonArray.getJSONObject(i).getString("year_of_construction"));
                            ListValue.setDname(jsonArray.getJSONObject(i).getString("dname"));
                            ListValue.setBname(jsonArray.getJSONObject(i).getString("bname"));
                            ListValue.setPvName(jsonArray.getJSONObject(i).getString("pvname"));
                            ListValue.setHabitationName(jsonArray.getJSONObject(i).getString("habitation_name"));
                            ListValue.setTownpanchayat_name(jsonArray.getJSONObject(i).getString("townpanchayat_name"));
                            ListValue.setMunicipality_name(jsonArray.getJSONObject(i).getString("municipality_name"));
                            ListValue.setCorporation_name(jsonArray.getJSONObject(i).getString("corporation_name"));
                            dbData.Insert_samathuvapuram_details(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new  fetchSamathuvapuram_details().execute();

        }
    }
    public class Insert_community extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteCOMMUNITYTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setCommunity_category_id(jsonArray.getJSONObject(i).getInt("community_category_id"));
                            ListValue.setCommunity_category(jsonArray.getJSONObject(i).getString("community_category"));
                            dbData.Insert_community(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_photos_type extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deletePHOTOS_TYPETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setPhoto_type_id(jsonArray.getJSONObject(i).getInt("photo_type_id"));
                            ListValue.setForm_id(jsonArray.getJSONObject(i).getInt("form_id"));
                            ListValue.setMin_no_of_photos(jsonArray.getJSONObject(i).getInt("min_no_of_photos"));
                            ListValue.setMax_no_of_photos(jsonArray.getJSONObject(i).getInt("max_no_of_photos"));
                            ListValue.setPhoto_type_name(jsonArray.getJSONObject(i).getString("photo_type_name"));
                            dbData.Insert_photos_type(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_condition_of_house extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteCONDITION_OF_HOUSETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setCondition_of_house_id(jsonArray.getJSONObject(i).getInt("condition_of_house_id"));
                            ListValue.setCondition_of_house(jsonArray.getJSONObject(i).getString("condition_of_house"));
                            dbData.Insert_condition_of_house(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_condition_of_infra extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteCONDITION_OF_INFRATable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setCondition_of_infra_id(jsonArray.getJSONObject(i).getInt("condition_of_infra_id"));
                            ListValue.setCondition_of_infra(jsonArray.getJSONObject(i).getString("condition_of_infra"));
                            dbData.Insert_condition_of_infra(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_estimate_type extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteESTIMATE_TYPETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setEstimate_type_id(jsonArray.getJSONObject(i).getInt("estimate_type_id"));
                            ListValue.setEstimate_type_name(jsonArray.getJSONObject(i).getString("estimate_type_name"));
                            dbData.Insert_estimate_type(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_work_scheme extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteWORK_SCHEMETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            int scheme_disp_order=0;
                            ListValue.setScheme_group_id(jsonArray.getJSONObject(i).getInt("scheme_group_id"));
                            ListValue.setScheme_id(jsonArray.getJSONObject(i).getInt("scheme_id"));
                            if(jsonArray.getJSONObject(i).getString("scheme_disp_order").equals("")){
                                scheme_disp_order=0;
                            }else {
                                scheme_disp_order=Integer.parseInt(jsonArray.getJSONObject(i).getString("scheme_disp_order"));
                            }
                            ListValue.setScheme_disp_order(scheme_disp_order);
                            ListValue.setCategory(jsonArray.getJSONObject(i).getString("category"));
                            ListValue.setHas_sub_type(jsonArray.getJSONObject(i).getString("has_sub_type"));
                            ListValue.setExisting_condition_required(jsonArray.getJSONObject(i).getString("existing_condition_required"));
                            ListValue.setPhotos_required(jsonArray.getJSONObject(i).getString("photos_required"));
                            ListValue.setScheme_name(jsonArray.getJSONObject(i).getString("scheme_name"));
                            dbData.Insert_work_scheme(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    public class Insert_work_type extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteWORK_TYPETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                            ListValue.setScheme_group_id(jsonArray.getJSONObject(i).getInt("scheme_group_id"));
                            ListValue.setScheme_id(jsonArray.getJSONObject(i).getInt("scheme_id"));
                            ListValue.setWork_group_id(jsonArray.getJSONObject(i).getInt("work_group_id"));
                            ListValue.setCategory(jsonArray.getJSONObject(i).getString("category"));
                            ListValue.setWork_type_id(jsonArray.getJSONObject(i).getInt("work_type_id"));
                            ListValue.setExisting_condition_required(jsonArray.getJSONObject(i).getString("existing_condition_required"));
                            ListValue.setPhotos_required(jsonArray.getJSONObject(i).getString("photos_required"));
                            ListValue.setWork_name(jsonArray.getJSONObject(i).getString("work_name"));
                            dbData.Insert_work_type(ListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }


    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<ModelClass> ImageCount = dbData.getSavedHouseImage();

        if (ImageCount.size() > 0) {
            homeScreenBinding.synData.setVisibility(View.VISIBLE);
        }else {
            homeScreenBinding.synData.setVisibility(View.GONE);
        }
    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


}
