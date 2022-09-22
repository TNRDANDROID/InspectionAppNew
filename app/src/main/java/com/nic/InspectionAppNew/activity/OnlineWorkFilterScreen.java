package com.nic.InspectionAppNew.activity;

import android.app.Activity;
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


    String pref_Village;

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


        loadDistrictList();


//        schemeFilterSpinner();
        finyearFilterSpinner();


        workListBinding.districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setDistrictCode(District.get(position).getDistictCode());
                    JSONArray districtCodeJsonArray = new JSONArray();
                    districtCodeJsonArray.put(District.get(position).getDistictCode());
                    prefManager.setDistrictCodeJson(districtCodeJsonArray);
                    loadBlockList();
                    getSchemeList();
                }else {
                    prefManager.setDistrictCode("");
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
                    prefManager.setBlockCode(Block.get(position).getBlockCode());
                    getVillageList();
                }else {
                    prefManager.setBlockCode("");
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
                    prefManager.setPvCode(Village.get(position).getPvCode());

                }else {
                    prefManager.setPvCode("");
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
                    prefManager.setSchemeSeqId(Scheme.get(position).getSchemeSequentialID());

                }else {
                    prefManager.setSchemeSeqId("");
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
                    prefManager.setFinancialyearName(FinYear.get(position).getFinancialYear());
                    JSONArray finyearJsonArray = new JSONArray();
                    finyearJsonArray.put(FinYear.get(position).getFinancialYear());
                    prefManager.setFinYearJson(finyearJsonArray);
                    workListBinding.districtSpinner.setSelection(0);
                }else {
                    prefManager.setFinancialyearName("");
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
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
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
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + authKey);
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
        cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" and bcode = "+prefManager.getBlockCode()+" order by pvname asc",null);
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
        cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" order by bname asc",null);
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
//                    new  Insert_workList().execute(jsonObject);
//                    Utils.showAlert(this, "Your Data will be Downloaded");
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

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
