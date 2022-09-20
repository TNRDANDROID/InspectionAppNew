package com.nic.InspectionAppNew.activity;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.SaveWorkDetailsActivityBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SaveWorkDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private SaveWorkDetailsActivityBinding saveWorkDetailsActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> status_list = new ArrayList<>();
    int work_id=0;
    int min_img_count=0;
    int max_img_count=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saveWorkDetailsActivityBinding = DataBindingUtil.setContentView(this, R.layout.save_work_details_activity);
        saveWorkDetailsActivityBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        work_id= getIntent().getIntExtra("work_id",0);
        statusFilterSpinner();
        saveWorkDetailsActivityBinding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setWorkStatusId(String.valueOf(status_list.get(position).getWork_status_id()));

                }else {
                    prefManager.setWorkStatusId("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void gotoCameraScreen()
    {
        Intent intent = new Intent(this, CameraScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void statusFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.STATUS_TABLE, null);

        status_list.clear();
        ModelClass list = new ModelClass();
        list.setSchemeName("Select status");
        status_list.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int status_id = cursor.getInt(cursor.getColumnIndexOrThrow("status_id"));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                    modelClass.setWork_status_id(status_id);
                    modelClass.setWork_status(status);

                    status_list.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("statuspinnersize", "" + status_list.size());

        }
        saveWorkDetailsActivityBinding.statusSpinner.setAdapter(new CommonAdapter(this, status_list, "status"));
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
