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
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ViewSavedWorkListBinding;
import com.nic.InspectionAppNew.databinding.WorkListBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.InspectionAppNew.dataBase.DBHelper.FINANCIAL_YEAR_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.SCHEME_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.VILLAGE_TABLE_NAME;

public class ViewSavedWorkList extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private ViewSavedWorkListBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ProgressHUD progressHUD;
    WorkListAdapter workListAdapter;


    String onOffType;

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }





}
