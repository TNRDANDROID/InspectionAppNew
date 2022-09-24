package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.WorkListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.OnlineWorkFilterScreenBinding;
import com.nic.InspectionAppNew.databinding.WorkListBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineWorkFilterScreen extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private OnlineWorkFilterScreenBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<ModelClass> District = new ArrayList<>();
    private List<ModelClass> Block = new ArrayList<>();
    private List<ModelClass> Village = new ArrayList<>();
    private List<ModelClass> Scheme = new ArrayList<>();
    private List<ModelClass> FinYear = new ArrayList<>();
    private ProgressHUD progressHUD;
    boolean workListInsert = false;

    String SelectedDistrict = "",SelectedBlock ="",SelectedVillage ="",SelectedFinYear ="",SelectedScheme ="";
    JSONArray districtCodeJsonArray = new JSONArray();
    JSONArray villageCodeJsonArray = new JSONArray();
    JSONArray schemeJsonArray = new JSONArray();
    JSONArray finyearJsonArray = new JSONArray();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        workListBinding = DataBindingUtil.setContentView(this, R.layout.online_work_filter_screen);
        workListBinding.setActivity(this);
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

        if(prefManager.getLevels().equals("S")){
            workListBinding.districtTv.setVisibility(View.VISIBLE);
            workListBinding.districtLayout.setVisibility(View.VISIBLE);
            workListBinding.blockTv.setVisibility(View.VISIBLE);
            workListBinding.blockLayout.setVisibility(View.VISIBLE);

        }else if(prefManager.getLevels().equals("D")){
            workListBinding.districtTv.setVisibility(View.GONE);
            workListBinding.districtLayout.setVisibility(View.GONE);
            workListBinding.blockTv.setVisibility(View.VISIBLE);
            workListBinding.blockLayout.setVisibility(View.VISIBLE);
        }else if(prefManager.getLevels().equals("B")){
            workListBinding.districtTv.setVisibility(View.GONE);
            workListBinding.districtLayout.setVisibility(View.GONE);
            workListBinding.blockTv.setVisibility(View.GONE);
            workListBinding.blockLayout.setVisibility(View.GONE);
        }

        loadDistrictList();


//        schemeFilterSpinner();
        finyearFilterSpinner();


        workListBinding.districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedDistrict=District.get(position).getDistictCode();
                    districtCodeJsonArray.put(District.get(position).getDistictCode());
                    loadBlockList();
                    getSchemeList();
                }else {
                    SelectedDistrict="";
                    workListBinding.blockSpinner.setSelection(0);
                    workListBinding.villageSpinner.setSelection(0);
                    workListBinding.schemeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedBlock=Block.get(position).getBlockCode();
                    getVillageList();
                }else {
                    SelectedBlock="";
                    workListBinding.villageSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedVillage=Village.get(position).getPvCode();
                    villageCodeJsonArray.put(Village.get(position).getPvCode());

                }else {
                    SelectedVillage="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.schemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedScheme=Scheme.get(position).getSchemeSequentialID();
                    schemeJsonArray.put(Scheme.get(position).getSchemeSequentialID());

                }else {
                    SelectedScheme="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.finYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedFinYear=FinYear.get(position).getFinancialYear();
                    finyearJsonArray.put(FinYear.get(position).getFinancialYear());
                    workListBinding.districtSpinner.setSelection(0);
                }else {
                    SelectedFinYear="";
                    workListBinding.districtSpinner.setSelection(0);
                    workListBinding.schemeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //fetAllApi();
        //Sample
       /* JSONObject jsonObject = new JSONObject();
        String json = "{\"STATUS\":\"OK\",\"RESPONSE\":\"OK\",\"JSON_DATA\":[{\"work_id\":1,\"work_name\":\"Property tax\"},{\"work_id\":2,\"work_name\":\"Water Charges\"},{\"work_id\":3,\"work_name\":\"Professional Tax\"},{\"work_id\":4,\"work_name\":\"Non Tax\"},{\"work_id\":5,\"work_name\":\"Trade License \"}]}";
        try {  jsonObject = new JSONObject(json); } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""); }
        try {
            if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                new Insert_workList().execute(jsonObject);
            }
        } catch (JSONException e) { e.printStackTrace(); }
*/
    }

    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
        return dataSet;
    }
    public  JSONObject villageListDistrictWiseJsonParams(Activity activity) throws JSONException {

        JSONObject dataSet = new JSONObject();

        if(prefManager.getLevels().equals("S")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, SelectedDistrict);
            dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
        }else if(prefManager.getLevels().equals("D")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
            dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
        }else if(prefManager.getLevels().equals("B")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
            dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        }        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector),schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + authKey);
        return dataSet;
    }
    public  JSONObject schemeListDistrictWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_SCHEME_LIST_DISTRICT_FINYEAR_WISE);
        if(prefManager.getLevels().equalsIgnoreCase("S")){
            dataSet.put(AppConstant.DISTRICT_CODE, districtCodeJsonArray);
        }
        else{
            dataSet.put(AppConstant.DISTRICT_CODE, SelectedDistrict);
        }

        if(prefManager.getLevels().equalsIgnoreCase("D") || prefManager.getLevels().equalsIgnoreCase("S")){
            dataSet.put(AppConstant.FINANCIAL_YEAR,finyearJsonArray);
        }
        Log.d("schemeListDistrictWise", "" + dataSet);
        return dataSet;
    }

    public void schemeFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT distinct scheme_name,scheme_seq_id FROM " + DBHelper.SCHEME_TABLE_NAME , null);

        Scheme.clear();
        ModelClass list = new ModelClass();
        list.setSchemeName("Select Scheme");
        Scheme.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int scheme_seq_id = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_seq_id"));
                    String scheme_name = cursor.getString(cursor.getColumnIndexOrThrow("scheme_name"));

                    modelClass.setSchemeSequentialID(String.valueOf(scheme_seq_id));
                    modelClass.setSchemeName(scheme_name);

                    Scheme.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Schemespinnersize", "" + Scheme.size());

        }
        workListBinding.schemeSpinner.setAdapter(new CommonAdapter(this, Scheme, "Scheme"));
    }
    public void finyearFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME , null);

        FinYear.clear();
        ModelClass list = new ModelClass();
        list.setFinancialYear("Select Financial Year");
        FinYear.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    String fin_year = cursor.getString(cursor.getColumnIndexOrThrow("fin_year"));

                    modelClass.setFinancialYear(fin_year);

                    FinYear.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("FinYearspinnersize", "" + FinYear.size());

        }
        workListBinding.finYearSpinner.setAdapter(new CommonAdapter(this, FinYear, "FinYear"));
    }

    public void villageFilterSpinner() {
        Cursor cursor = null;
        if(prefManager.getLevels().equals("S")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+SelectedDistrict+" and bcode = "+SelectedBlock+" order by pvname asc",null);

        }else if(prefManager.getLevels().equals("D")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" and bcode = "+SelectedBlock+" order by pvname asc",null);

        }else if(prefManager.getLevels().equals("B")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" and bcode = "+prefManager.getBlockCode()+" order by pvname asc",null);

        }
        Village.clear();
        ModelClass list = new ModelClass();
        list.setPvName("Select Village");
        list.setPvCode("0");
        Village.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int pvcode = cursor.getInt(cursor.getColumnIndexOrThrow("pvcode"));
                    String pvname = cursor.getString(cursor.getColumnIndexOrThrow("pvname"));

                    modelClass.setPvCode(String.valueOf(pvcode));
                    modelClass.setPvName(pvname);

                    Village.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Villagespinnersize", "" + Village.size());

        }
        workListBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "Village"));
    }
    public void loadBlockList() {
        Cursor cursor = null;
        if(prefManager.getLevels().equals("S")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+SelectedDistrict+" order by bname asc",null);

        }else if(prefManager.getLevels().equals("D")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+prefManager.getDistrictCode()+" order by bname asc",null);

        }else if(prefManager.getLevels().equals("B")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+prefManager.getDistrictCode()+" order by bname asc",null);

        }
        Block.clear();
        ModelClass list = new ModelClass();
        list.setBlockName("Select Block");
        list.setBlockCode("0");
        Block.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int bcode = cursor.getInt(cursor.getColumnIndexOrThrow("bcode"));
                    String bname = cursor.getString(cursor.getColumnIndexOrThrow("bname"));

                    modelClass.setBlockCode(String.valueOf(bcode));
                    modelClass.setBlockName(bname);

                    Block.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Blockspinnersize", "" + Block.size());

        }
        workListBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "Block"));
    }
    public void loadDistrictList() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.DISTRICT_TABLE_NAME , null);
        District.clear();
        ModelClass list = new ModelClass();
        list.setDistrictName("Select District");
        list.setDistictCode("0");
        District.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int dcode = cursor.getInt(cursor.getColumnIndexOrThrow("dcode"));
                    String dname = cursor.getString(cursor.getColumnIndexOrThrow("dname"));

                    modelClass.setDistictCode(String.valueOf(dcode));
                    modelClass.setDistrictName(dname);

                    District.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Districtspinnersize", "" + District.size());

        }
        workListBinding.districtSpinner.setAdapter(new CommonAdapter(this, District, "District"));
    }
    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }


    public void projectListScreenStateUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){
            if(SelectedDistrict!= null && !SelectedDistrict.equals("")){
                if(SelectedBlock!= null && !SelectedBlock.equals("")){
                    if(SelectedVillage!= null && !SelectedVillage.equals("")){
                        if(SelectedScheme!= null && !SelectedScheme.equals("")){

                            getWorkListOptional();
                        }else {
                            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Scheme");
                        }
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }
                }else {
                    Utils.showAlert(OnlineWorkFilterScreen.this,"Select Block");
                }
            }else {
                Utils.showAlert(OnlineWorkFilterScreen.this,"Select District");
            }
        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void projectListScreenDistrictUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){
            if(SelectedBlock!= null && !SelectedBlock.equals("")){
                if(SelectedVillage!= null && !SelectedVillage.equals("")){
                    if(SelectedScheme!= null && !SelectedScheme.equals("")){

                            getWorkListOptional();
                        }else {
                            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Scheme");
                        }
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }
                }else {
                    Utils.showAlert(OnlineWorkFilterScreen.this,"Select Block");
                }

        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void projectListScreenBlockUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){

            if(SelectedVillage!= null && !SelectedVillage.equals("")){
                if(SelectedScheme!= null && !SelectedScheme.equals("")){

                            getWorkListOptional();
                        }else {
                            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Scheme");
                        }
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }

        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void download() {
        if (Utils.isOnline()) {
            if(prefManager.getLevels().equalsIgnoreCase("S")) {
                projectListScreenStateUser();
            }
            else if (!prefManager.getLevels().equalsIgnoreCase("D")) {
                projectListScreenDistrictUser();
            } else {
                projectListScreenBlockUser();
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }
    public void getWorkListOptional() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListOptional", Api.Method.POST, UrlGenerator.getMainService(), workListOptionalJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workListOptionalJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workListOptional(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListOptional", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workListOptional(Activity activity) throws JSONException {
        JSONObject dataSet = new JSONObject();
        JSONObject dataSet1 = new JSONObject();
        try{
            if (prefManager.getLevels().equalsIgnoreCase("S")){
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, SelectedDistrict);
                dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
            else if (prefManager.getLevels().equalsIgnoreCase("D")) {
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
                dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
            else if (prefManager.getLevels().equalsIgnoreCase("B")) {
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
                dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("workListOptional", "" + dataSet1);
        return dataSet1;
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
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
//                    Utils.showAlert(this, "Your Data will be Downloaded");
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    workListInsert = false;
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }
            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSchemeTask().execute(jsonObject);
                }
                Log.d("schemeAll", "" + responseDecryptedSchemeKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }
    private void workListOptionalS(JSONArray jsonArray) {
        try {
            dbData.open();
            dbData.deleteWorkListTable();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String SelectedBlockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String hab_code = jsonArray.getJSONObject(i).getString("hab_code");
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String schemeID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_ID);
                    String scheme_group_id = jsonArray.getJSONObject(i).getString("scheme_group_id");
                    String work_group_id = jsonArray.getJSONObject(i).getString("work_group_id");
                    String work_type_id = jsonArray.getJSONObject(i).getString("work_type_id");
                    String finYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);
                    int workID = jsonArray.getJSONObject(i).getInt(AppConstant.WORK_ID);
                    String workName = jsonArray.getJSONObject(i).getString(AppConstant.WORK_NAME);
                    String as_value = jsonArray.getJSONObject(i).getString("as_value");
                    String ts_value = jsonArray.getJSONObject(i).getString("ts_value");
                    String current_stage_of_work = jsonArray.getJSONObject(i).getString("current_stage_of_work");
                    String is_high_value = jsonArray.getJSONObject(i).getString("is_high_value");

                    ModelClass modelClass = new ModelClass();
                    modelClass.setDistictCode(dcode);
                    modelClass.setBlockCode(SelectedBlockCode);
                    modelClass.setHabCode(hab_code);
                    modelClass.setPvCode(pvcode);
                    modelClass.setSchemeSequentialID(schemeID);
                    modelClass.setScheme_group_id(scheme_group_id);
                    modelClass.setWork_group_id(work_group_id);
                    modelClass.setWork_type_id(work_type_id);
                    modelClass.setFinancialYear(finYear);
                    modelClass.setWork_id(workID);
                    modelClass.setWork_name(workName);
                    modelClass.setAs_value(as_value);
                    modelClass.setTs_value(ts_value);
                    modelClass.setCurrent_stage_of_work(current_stage_of_work);
                    modelClass.setIs_high_value(is_high_value);

                    dbData.Insert_workList(modelClass);

                }
                workListInsert = true;
                callAlert();

            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Financial Year");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }
    public void callAlert() {
        if (workListInsert){
//            Utils.showAlert(this, "Your Data Downloaded Successfully!");
            workListInsert = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openWorkListScreen();
                }
            }, 1000);

        }
    }
    public void openWorkListScreen() {
        Intent intent = new Intent(this, WorkList.class);
        intent.putExtra("OnOffType","online");
        if(prefManager.getLevels().equalsIgnoreCase("S")) {
            intent.putExtra("dcode",SelectedDistrict);
            intent.putExtra("bcode",SelectedBlock);
            intent.putExtra("pvcode",SelectedVillage);
            intent.putExtra("scheme",SelectedScheme);
            intent.putExtra("fin_year",SelectedFinYear);
        }
        else if (!prefManager.getLevels().equalsIgnoreCase("D")) {
            intent.putExtra("dcode",prefManager.getDistrictCode());
            intent.putExtra("bcode",SelectedBlock);
            intent.putExtra("pvcode",SelectedVillage);
            intent.putExtra("scheme",SelectedScheme);
            intent.putExtra("fin_year",SelectedFinYear);
        } else {
            intent.putExtra("dcode",prefManager.getDistrictCode());
            intent.putExtra("bcode",prefManager.getBlockCode());
            intent.putExtra("pvcode",SelectedVillage);
            intent.putExtra("scheme",SelectedScheme);
            intent.putExtra("fin_year",SelectedFinYear);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteVillageTable();
            ArrayList<ModelClass> villagelist_count = dbData.getAll_Village(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass villageListValue = new ModelClass();
                        try {
                            villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            villageFilterSpinner();
        }
    }
    public class InsertSchemeTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteSchemeTable();

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass modelClass = new ModelClass();
                        try {
                            modelClass.setSchemeSequentialID(jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID));
                            modelClass.setSchemeName(jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME));
                            modelClass.setFinancialYear(jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR));

                            dbData.insertScheme(modelClass);
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
            schemeFilterSpinner();
        }
    }


}
