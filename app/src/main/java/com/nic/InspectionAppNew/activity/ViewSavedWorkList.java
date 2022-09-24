package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
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
import com.nic.InspectionAppNew.Interface.DateInterface;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.ImageAdapter;
import com.nic.InspectionAppNew.adapter.SavedWorkListAdapter;
import com.nic.InspectionAppNew.adapter.WorkListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ViewSavedWorkListBinding;
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

import static com.nic.InspectionAppNew.dataBase.DBHelper.FINANCIAL_YEAR_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.SCHEME_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.VILLAGE_TABLE_NAME;

public class ViewSavedWorkList extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, DateInterface {
    private ViewSavedWorkListBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ProgressHUD progressHUD;
    SavedWorkListAdapter savedWorkListAdapter;


    String onOffType;
    String fromDate;
    String toDate;

    ArrayList<ModelClass> savedWorkList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        workListBinding = DataBindingUtil.setContentView(this, R.layout.view_saved_work_list);
        workListBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        workListBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        workListBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        workListBinding.recycler.setHasFixedSize(true);
        workListBinding.recycler.setNestedScrollingEnabled(false);
        workListBinding.recycler.setFocusable(false);

        workListBinding.recycler.setVisibility(View.GONE);
        workListBinding.notFoundTv.setVisibility(View.VISIBLE);



    }

    public void showDatePickerDialog(){
        Utils.showDatePickerDialog(this);

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

            if ("WorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListData(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }


    @Override
    public void getDate(String date) {
        String[] separated = date.split(":");
        fromDate = separated[0]; // this will contain "Fruit"
        toDate = separated[1];
        workListBinding.date.setText(fromDate+" to "+toDate);

        if(Utils.isOnline()){
            getWorkDetails();
        }
        else {
            Utils.showAlert(ViewSavedWorkList.this,"No Internet");
        }

    }
    public void getWorkDetails() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkDetails", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workDetailsJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workDetailsParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkDetails", "" + authKey);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "date_wise_inspection_details_view");
        dataSet.put("from_date", fromDate);
        dataSet.put("to_date", toDate);

        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }

    private void workListData(JSONArray jsonArray) {
        try {

            if (jsonArray.length() > 0) {
                savedWorkList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String bcode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String inspection_id = jsonArray.getJSONObject(i).getString("inspection_id");
                    String inspection_date = jsonArray.getJSONObject(i).getString("inspection_date");
                    String status_id = jsonArray.getJSONObject(i).getString("status_id");
                    String status = jsonArray.getJSONObject(i).getString("status");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String work_name = jsonArray.getJSONObject(i).getString("work_name");
                    String work_id = jsonArray.getJSONObject(i).getString("work_id");

                    ModelClass modelClass = new ModelClass();
                    modelClass.setDistictCode(dcode);
                    modelClass.setBlockCode(bcode);
                    modelClass.setPvCode(pvcode);
                    modelClass.setInspection_id(inspection_id);
                    modelClass.setInspectedDate(inspection_date);
                    modelClass.setWork_status_id(Integer.parseInt(status_id));
                    modelClass.setWork_status(status);
                    modelClass.setDescription(description);
                    modelClass.setWork_name(work_name);
                    modelClass.setWork_id(Integer.parseInt(work_id));
                    savedWorkList.add(modelClass);

                }

                if (savedWorkList.size()>0){
                    savedWorkListAdapter = new SavedWorkListAdapter(ViewSavedWorkList.this,savedWorkList);
                    workListBinding.recycler.setVisibility(View.VISIBLE);
                    workListBinding.notFoundTv.setVisibility(View.GONE);
                    workListBinding.recycler.setAdapter(savedWorkListAdapter);
                }
                else {
                    workListBinding.recycler.setVisibility(View.GONE);
                    workListBinding.notFoundTv.setVisibility(View.VISIBLE);
                    workListBinding.recycler.setAdapter(null);
                }

            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Work");
                workListBinding.recycler.setVisibility(View.GONE);
                workListBinding.notFoundTv.setVisibility(View.VISIBLE);
                workListBinding.recycler.setAdapter(null);
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }
}
