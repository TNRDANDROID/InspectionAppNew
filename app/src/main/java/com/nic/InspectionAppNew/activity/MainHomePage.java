package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.ghanshyam.graphlibs.GraphData;
import com.nic.InspectionAppNew.ImageZoom.ImageMatrixTouchHandler;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ActivityMainHomePageNewBinding;
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
import java.util.Collection;

import static com.nic.InspectionAppNew.utils.Utils.showAlert;

public class MainHomePage extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private ActivityMainHomePageNewBinding homeScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        homeScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_home_page_new);
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
            homeScreenBinding.userImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_user_icon));
        }
        homeScreenBinding.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefManager.getProfileImage() != null && !prefManager.getProfileImage().equals("")) {
                    Utils.ExpandedImage(prefManager.getProfileImage(),MainHomePage.this);
            }
            }
        });
        homeScreenBinding.navigationLayout.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefManager.getProfileImage() != null && !prefManager.getProfileImage().equals("")) {
                    Utils.ExpandedImage(prefManager.getProfileImage(),MainHomePage.this);
                }
            }
        });
        homeScreenBinding.navigationLayout.refreshStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                getStageList();
            }
        });

        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            homeScreenBinding.navigationLayout.tvVersion.setText(getResources().getString(R.string.version) + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        if (Utils.isOnline()) {
            if(!isHome.equals("Home")){
                getDashboardData();
                getPhotoCount();
                getFinYearList();
                getInspection_statusList();
                getCategoryList();
                if(dbData.getAll_Stage("all","","","").size()==0){
                    getStageList();
                }
            }

        }else {
            showAlert(MainHomePage.this,"No Internet Connection!");
        }
        syncButtonVisibility();
        setProfile();

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
                prefManager.setWorkType("rdpr");
                prefManager.setOnOffType("online");
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                gotoViewSavedWorkScreen();
            }
        });
        homeScreenBinding.navigationLayout.viewInspectedOtherWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("other");
                prefManager.setOnOffType("online");
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                gotoViewSavedOtherWorkScreen();
            }
        });
        homeScreenBinding.navigationLayout.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                ArrayList<ModelClass> ImageCount = dbData.getSavedImage();
                if ((ImageCount.size() > 0)) {
                    Utils.showAlert(MainHomePage.this, "Sync all the data before edit your profile!");
                } else {
                    getProfileData();
                }

            }
        });
        homeScreenBinding.navigationLayout.logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeScreenBinding.drawerLayout.closeDrawer(Gravity.LEFT);
                logout();

            }
        });
        homeScreenBinding.navigationLayout.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainHomePage.this,OtpVerfication.class);
                intent.putExtra("mobile_number","");
                intent.putExtra("flag","change_password");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
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
        homeScreenBinding.goOnlineAtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("atr");
                prefManager.setOnOffType("online");
                openATRWorkListScreen();
            }
        });
        homeScreenBinding.goOfflineAtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefManager.setWorkType("atr");
                prefManager.setOnOffType("offline");
                openATRWorkListScreen();
            }
        });
    }

    private void getDashboardData() {
        try {
            new ApiService(this).makeJSONObjectRequest("DashboardData", Api.Method.POST, UrlGenerator.getMainService(), Params(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject Params() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), jsonparam(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("DashboardData", "" + dataSet);
        return dataSet;
    }
    public  JSONObject jsonparam(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "current_finyear_wise_status_count");

        Log.d("DashboardData", "" + dataSet);
        return dataSet;
    }
    private void setProfile() {

        homeScreenBinding.userName.setText(prefManager.getName());
        homeScreenBinding.designation.setText(prefManager.getDesignation());
        homeScreenBinding.navigationLayout.designation.setText(prefManager.getDesignation());
        homeScreenBinding.navigationLayout.name.setText(prefManager.getName());
        if(prefManager.getLevels().equals("S")){
            if(prefManager.getStateName()!=null && !prefManager.getStateName().isEmpty()){
                homeScreenBinding.userLevel.setText("State : "+prefManager.getStateName());
                homeScreenBinding.navigationLayout.level.setText("State : "+prefManager.getStateName());
            }else {
                homeScreenBinding.userLevel.setText("State");
                homeScreenBinding.navigationLayout.level.setText("State");
            }

        }else if(prefManager.getLevels().equals("D")){
            if(prefManager.getDistrictName()!=null && !prefManager.getDistrictName().isEmpty()){
                homeScreenBinding.userLevel.setText("District : "+prefManager.getDistrictName());
                homeScreenBinding.navigationLayout.level.setText("District : "+prefManager.getDistrictName());
            }else {
                homeScreenBinding.userLevel.setText("District");
                homeScreenBinding.navigationLayout.level.setText("District");
            }

        }else if(prefManager.getLevels().equals("B")){
            if(prefManager.getBlockName()!=null && !prefManager.getBlockName().isEmpty()){
                homeScreenBinding.userLevel.setText("Block : "+prefManager.getBlockName());
                homeScreenBinding.navigationLayout.level.setText("Block : "+prefManager.getBlockName());
            }else {
                homeScreenBinding.userLevel.setText("Block");
                homeScreenBinding.navigationLayout.level.setText("Block");
            }


        }
        if (prefManager.getProfileImage() != null && !prefManager.getProfileImage().equals("")) {
            homeScreenBinding.navigationLayout.userImg.setImageBitmap(Utils.StringToBitMap(prefManager.getProfileImage()));
        }else {
            homeScreenBinding.navigationLayout.userImg.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_user_icon));
        }
    }

    private void getProfileData() {
        if (Utils.isOnline()) {
            try {
                new ApiService(MainHomePage.this).makeJSONObjectRequest("getProfileData", Api.Method.POST, UrlGenerator.getMainService(),  getProfileJsonParams(), "not cache", MainHomePage.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(MainHomePage.this,"No Internet Connection!");
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

    public void getStageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("StageList", Api.Method.POST, UrlGenerator.getMainService(), stageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject stageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.stageListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("StageList", "" + authKey);
        return dataSet;
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
            new ApiService(this).makeJSONObjectRequest("CategoryList", Api.Method.POST, UrlGenerator.getMainService(), CategoryListJsonParams(), "not cache", this);
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
            new ApiService(this).makeJSONObjectRequest("FinYearList", Api.Method.POST, UrlGenerator.getMainService(), finyearListJsonParams(), "not cache", this);
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
        setProfile();
        if (Utils.isOnline()) {
            getDashboardData();
        }else {
            showAlert(MainHomePage.this,"No Internet Connection!");
        }


    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;
            if ("getProfileData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);

                Log.d("getProfileData", "" + jsonObject.toString());
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
                            String profile_image=jsonArray.getJSONObject(i).getString("profile_image");
                            if(profile_image != null &&!profile_image.isEmpty()){
                                byte[] decodedString = Base64.decode(profile_image, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                prefManager.setProfileImage(Utils.BitMapToString(decodedByte));


                            }else {
                                prefManager.setProfileImage("");
                            }
                            prefManager.setDesignation(designation);
                            prefManager.setName(String.valueOf(name));
                            prefManager.setLevels(String.valueOf(level));
                            prefManager.setDistrictCode(dcode);
                            prefManager.setBlockCode(bcode);

                        }
                        setProfile();
                    }

                    Intent gotoRegisterScreen = new Intent(MainHomePage.this,RegistrationScreen.class);
                    gotoRegisterScreen.putExtra("key","home");
                    gotoRegisterScreen.putExtra("profile_data",jsonObject.toString());
                    startActivity(gotoRegisterScreen);
                    overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                }else {
                    showAlert(this, jsonObject.getString(AppConstant.KEY_MESSAGE));
                }

            }

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
            if ("CategoryList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
//                String responseDecryptedBlockKey = "{\"STATUS\":\"OK\",\"RESPONSE\":\"OK\",\"JSON_DATA\":[{\"other_work_category_id\":\"1\",\"other_work_category_name\":\"PMAY\"},{\"other_work_category_id\":\"2\",\"other_work_category_name\":\"PMGSY\"},{\"other_work_category_id\":\"3\",\"other_work_category_name\":\"SBM\"},{\"other_work_category_id\":\"4\",\"other_work_category_name\":\"MGNREGS\"},{\"other_work_category_id\":\"5\",\"other_work_category_name\":\"Others\"}]}";
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_CategoryList_Task().execute(jsonObject);
                }
                Log.d("CategoryList_status", "" + responseObj.toString());
                Log.d("CategoryList_status", "" + responseDecryptedBlockKey);
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
            if ("StageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertStageTask().execute(jsonObject);
                }
                Log.d("StageList", "" + responseDecryptedKey);
            }
            if ("DashboardData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    dashboardData(jsonObject);
                }
                Log.d("DashboardData", "" + responseDecryptedKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }
    private void dashboardData(JSONObject jsonObject) {
        try {

            if (jsonObject.length() > 0) {
                JSONArray status_wise_count = new JSONArray();
                status_wise_count = jsonObject.getJSONArray("JSON_DATA");
                if(status_wise_count.length()>0){
                    for(int j=0;j<status_wise_count.length();j++){
                        try {
                            String satisfied_count = status_wise_count.getJSONObject(j).getString("satisfied");
                            String un_satisfied_count = status_wise_count.getJSONObject(j).getString("unsatisfied");
                            String need_improvement_count = status_wise_count.getJSONObject(j).getString("need_improvement");
                            String fin_year = status_wise_count.getJSONObject(j).getString("fin_year");
                            if(satisfied_count.equals("")){
                                satisfied_count="0";
                            } if(un_satisfied_count.equals("")){
                                un_satisfied_count="0";
                            } if(need_improvement_count.equals("")){
                                need_improvement_count="0";
                            }
                            int total_inspection_count = Integer.parseInt(satisfied_count)+Integer.parseInt(un_satisfied_count)+Integer.parseInt(need_improvement_count);
                            homeScreenBinding.satisfiedCountGraph.setText(String.valueOf(satisfied_count));
                            homeScreenBinding.unSatisfiedCountGraph.setText(String.valueOf(un_satisfied_count));
                            homeScreenBinding.needImprovementCountGraph.setText(String.valueOf(need_improvement_count));
                            homeScreenBinding.totalCountGraph.setText(String.valueOf(total_inspection_count));
                            homeScreenBinding.totalCount1.setText(String.valueOf(total_inspection_count));
                            homeScreenBinding.totalTv.setText("Total ("+fin_year+")");
                            showPieChart(Integer.parseInt(satisfied_count),Integer.parseInt(un_satisfied_count),Integer.parseInt(need_improvement_count),total_inspection_count);
                        } catch (JSONException e){

                        }

                    }
                }
                else {

                }
            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Work");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }
    private void showPieChart(int satisfied,int unsatisfied,int need_improvement,int total_inspection_count){
        homeScreenBinding.graph.setMinValue(0f);
        homeScreenBinding.graph.setMaxValue(total_inspection_count);
        homeScreenBinding.graph.setDevideSize(0.0f);
        homeScreenBinding.graph.setBackgroundShapeWidthInDp(10);
        homeScreenBinding.graph.setShapeForegroundColor(getResources().getColor(R.color.colorPrimaryDark));
        homeScreenBinding.graph.setShapeBackgroundColor(getResources().getColor(R.color.colorAccent));
        homeScreenBinding.totalCountGraph.setText(String.valueOf(total_inspection_count));
        homeScreenBinding.satisfiedCountGraph.setText(String.valueOf(satisfied));
        homeScreenBinding.unSatisfiedCountGraph.setText(String.valueOf(unsatisfied));
        homeScreenBinding.needImprovementCountGraph.setText(String.valueOf(need_improvement));
        homeScreenBinding.totalCount1.setText(String.valueOf(total_inspection_count));
        Resources resources = getResources();
        Collection<GraphData> data = new ArrayList<>();
        data.add(new GraphData(Float.valueOf(satisfied), resources.getColor(R.color.account_status_green_color)));
        data.add(new GraphData(Float.valueOf(unsatisfied), resources.getColor(R.color.red)));
        data.add(new GraphData(Float.valueOf(need_improvement), resources.getColor(R.color.primary_text_color)));
        homeScreenBinding.graph.setData(data);
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
    public class InsertStageTask extends AsyncTask<JSONObject, Void, Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteWorkStageTable();
                if (params.length > 0) {
                    //dbData.deleteWorkStageTable();
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass stage = new ModelClass();
                        try {
                            stage.setWork_group_id(jsonArray.getJSONObject(i).getString(AppConstant.WORK_GROUP_ID));
                            stage.setWork_type_id(jsonArray.getJSONObject(i).getString(AppConstant.WORK_TYPE_ID));
                            stage.setWork_stage_order(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_ORDER));
                            stage.setWork_stage_code(jsonArray.getJSONObject(i).getString(AppConstant.WORK_STAGE_CODE));
                            stage.setWork_stage_name(jsonArray.getJSONObject(i).getString(AppConstant.WORK_SATGE_NAME));

                            dbData.insertStage(stage);
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
            if (progressHUD != null) {
                progressHUD.cancel();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(MainHomePage.this, "Downloading", true, false, null);
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
    public void openATRWorkListScreen() {
        Intent intent = new Intent(this, ATRWorkList.class);
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
