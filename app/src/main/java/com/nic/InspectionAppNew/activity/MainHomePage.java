package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ActivityMainHomePageBinding;
import com.nic.InspectionAppNew.dialog.MyDialog;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainHomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private ActivityMainHomePageBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_home_page);
        homeScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            dbData.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }

        if (prefManager.getProfileImage() != null && !prefManager.getProfileImage().equals("")) {
            homeScreenBinding.userImg.setImageBitmap(Utils.StringToBitMap(prefManager.getProfileImage()));
        }else {
            homeScreenBinding.userImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_user));
        }




        if (Utils.isOnline()) {
            if(!isHome.equals("Home")){
                getPhotoCount();
                getFinYearList();
                getInspection_statusList();
                getCategoryList();
            }

        }
        syncButtonVisibility();
        homeScreenBinding.userName.setText(prefManager.getName());
        homeScreenBinding.designation.setText(prefManager.getDesignation());
        homeScreenBinding.navigationLayout.name.setText(prefManager.getName());
        if(prefManager.getLevels().equals("S")){
            homeScreenBinding.userLevel.setText("State : "+prefManager.getStateName());
            homeScreenBinding.navigationLayout.level.setText("State : "+prefManager.getStateName());
        }else if(prefManager.getLevels().equals("D")){
            homeScreenBinding.userLevel.setText("District : "+prefManager.getDistrictName());
            homeScreenBinding.navigationLayout.level.setText("District : "+prefManager.getDistrictName());
        }else if(prefManager.getLevels().equals("B")){
            homeScreenBinding.userLevel.setText("Block : "+prefManager.getBlockName());
            homeScreenBinding.navigationLayout.level.setText("Block : "+prefManager.getBlockName());

        }


        homeScreenBinding.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPendingScreen();
            }
        });
        homeScreenBinding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuDrawer();
            }
        });
        homeScreenBinding.navigationLayout.viewInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                gotoViewSavedWorkScreen();
            }
        });
        homeScreenBinding.navigationLayout.viewInspectedOtherWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                gotoViewSavedOtherWorkScreen();
            }
        });
        homeScreenBinding.goOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("rdpr");
                prefManager.setOnOffType("online");
                openOnlineWorkListScreen();
            }
        });
        homeScreenBinding.goOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("rdpr");
                prefManager.setOnOffType("offline");
                openDownloadScreen();
            }
        });
        homeScreenBinding.goOnlineOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("other");
                prefManager.setOnOffType("online");
                openWorkFilterScreen();
            }
        });
        homeScreenBinding.goOfflineOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("other");
                prefManager.setOnOffType("offline");
                openOnlineWorkListScreen();
            }
        });
    }

    private void openWorkFilterScreen() {
        Intent intent = new Intent(this, OnlineWorkFilterScreen.class);
        intent.putExtra("type","work");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void getInspection_statusList() {
        try {
            new ApiService(this).makeJSONObjectRequest("inspection_status", Api.Method.POST, UrlGenerator.getServicesListUrl(), inspection_statusListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getCategoryList() {
        try {
            new ApiService(this).makeJSONObjectRequest("CategoryList", Api.Method.POST, UrlGenerator.getServicesListUrl(), CategoryListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getPhotoCount() {
        try {
            new ApiService(this).makeJSONObjectRequest("PhotoCount", Api.Method.POST, UrlGenerator.getMainService(), PhotoCountJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getFinYearList() {
        try {
            new ApiService(this).makeJSONObjectRequest("FinYearList", Api.Method.POST, UrlGenerator.getServicesListUrl(), finyearListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject finyearListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeFinyearListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("finYearList", "" + dataSet);
        return dataSet;
    }
    public JSONObject inspection_statusListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.inspection_statusListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("inspection_statusList", "" + dataSet);
        return dataSet;
    }
    public JSONObject CategoryListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.CategoryListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("CategoryList", "" + dataSet);
        return dataSet;
    }
    public JSONObject PhotoCountJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.PhotoCountParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("PhotoCount", "" + dataSet);
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
        ArrayList<ModelClass> ImageCount = dbData.getSavedImage();
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

    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;
            if ("inspection_status".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_Inspection_status_Task().execute(jsonObject);
                }
                Log.d("inspection_status", "" + responseObj.toString());
                Log.d("inspection_status", "" + responseDecryptedBlockKey);
            }
            if ("CategoryList".equals(urlType) /*&& responseObj != null*/) {
//                String key = responseObj.getString(AppConstant.ENCODE_DATA);
//                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                String responseDecryptedBlockKey = "{\"STATUS\":\"OK\",\"RESPONSE\":\"OK\",\"JSON_DATA\":[{\"other_work_category_id\":\"1\",\"other_work_category_name\":\"PMAY\"},{\"other_work_category_id\":\"2\",\"other_work_category_name\":\"PMGSY\"},{\"other_work_category_id\":\"3\",\"other_work_category_name\":\"SBM\"},{\"other_work_category_id\":\"4\",\"other_work_category_name\":\"MGNREGS\"},{\"other_work_category_id\":\"5\",\"other_work_category_name\":\"Others\"}]}";
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_CategoryList_Task().execute(jsonObject);
                }
                Log.d("inspection_status", "" + responseObj.toString());
                Log.d("inspection_status", "" + responseDecryptedBlockKey);
            }
            if ("FinYearList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_FinYear_Task().execute(jsonObject);
                }
                Log.d("FinYearList", "" + responseObj.toString());
                Log.d("FinYearList", "" + responseDecryptedBlockKey);
            }

            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_Scheme_Task().execute(jsonObject);
                }
                Log.d("SchemeList", "" + responseObj.toString());
                Log.d("SchemeList", "" + responseDecryptedBlockKey);
            }
            if ("PhotoCount".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    prefManager.setPhotoCount(jsonObject.getString("COUNT"));
                }
                Log.d("PhotoCount", "" + responseObj.toString());
                Log.d("PhotoCount", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class Insert_Inspection_status_Task extends AsyncTask<JSONObject ,Void ,Void> {
        private ProgressHUD progressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(MainHomePage.this, "Loading...", true, false, null);
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteinspection_statusTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String status_id = jsonArray.getJSONObject(i).getString(AppConstant.STATUS_ID);
                        String status = jsonArray.getJSONObject(i).getString(AppConstant.STATUS);
                        ModelClass modelClass = new ModelClass();
                        modelClass.setWork_status_id(Integer.parseInt(status_id));
                        modelClass.setWork_status(status);
                        dbData.insertStatus(modelClass);
                        /*ContentValues status_items = new ContentValues();
                        status_items.put(AppConstant.STATUS_ID, status_id);
                        status_items.put(AppConstant.STATUS, status);*/
                        //db.insert(DBHelper.STATUS_TABLE, null, status_items);
                        //Log.d("LocalDBstatusList", "" + status_items);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

    }
    public class Insert_CategoryList_Task extends AsyncTask<JSONObject ,Void ,Void> {
        private ProgressHUD progressHUD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(MainHomePage.this, "Loading...", true, false, null);
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteOTHER_CATEGORY_TABLETable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String other_work_category_id = jsonArray.getJSONObject(i).getString("other_work_category_id");
                        String other_work_category_name = jsonArray.getJSONObject(i).getString("other_work_category_name");
                        ModelClass modelClass = new ModelClass();
                        modelClass.setOther_work_category_id(Integer.parseInt(other_work_category_id));
                        modelClass.setOther_work_category_name(other_work_category_name);
                        dbData.insertCategoryList(modelClass);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

    }
    public class Insert_Scheme_Task extends AsyncTask<JSONObject ,Void ,Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(MainHomePage.this, "Loading...", true, false, null);
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteSchemeTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String schemeSequentialID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID);
                        String schemeName = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME);
                        String fin_year = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);

                        ContentValues schemeListLocalDbValues = new ContentValues();
                        schemeListLocalDbValues.put(AppConstant.SCHEME_SEQUENTIAL_ID, schemeSequentialID);
                        schemeListLocalDbValues.put(AppConstant.SCHEME_NAME, schemeName);
                        schemeListLocalDbValues.put(AppConstant.FINANCIAL_YEAR, fin_year);

                        db.insert(DBHelper.SCHEME_TABLE_NAME, null, schemeListLocalDbValues);
                        Log.d("LocalDBSchemeList", "" + schemeListLocalDbValues);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


    }
    public class Insert_FinYear_Task extends AsyncTask<JSONObject ,Void ,Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(MainHomePage.this, "Loading...", true, false, null);
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteFinYearTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String financialYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);

                        ModelClass modelClass = new ModelClass();
                        modelClass.setFinancialYear(financialYear);
                        dbData.insertFinYear(modelClass);
                        //db.insert(DBHelper.FINANCIAL_YEAR_TABLE_NAME, null, FinYearListLocalDbValues);
                        //Log.d("LocalDBFinyearList", "" + FinYearListLocalDbValues);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
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
        ArrayList<ModelClass> imageCount = dbData.getSavedImage();
        if(imageCount.size()>0){
            homeScreenBinding.relativeLayout.setVisibility(View.VISIBLE);
        }
        else {
            homeScreenBinding.relativeLayout.setVisibility(View.GONE);
        }
    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, PendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void openOnlineWorkListScreen() {
        Intent intent = new Intent(this, GetWorkListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void openDownloadScreen() {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void openMenuDrawer(){
        if(homeScreenBinding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
            homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            homeScreenBinding.drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    private void gotoViewSavedWorkScreen(){
        Intent intent = new Intent(MainHomePage.this,ViewSavedWorkList.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    private void gotoViewSavedOtherWorkScreen(){
        Intent intent = new Intent(MainHomePage.this, ViewSavedOtherWorkList.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
