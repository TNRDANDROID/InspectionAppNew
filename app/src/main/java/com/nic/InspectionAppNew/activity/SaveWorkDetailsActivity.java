package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

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
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.os.Build.VERSION_CODES.N;

public class SaveWorkDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private SaveWorkDetailsActivityBinding saveWorkDetailsActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> status_list = new ArrayList<>();
    int work_id=0;
    String dcode;
    String bcode;
    String pvcode;
    String scheme_id;
    String fin_year;
    String work_name;
    String as_value;
    String ts_value;
    String current_stage_of_work;
    String is_high_value;
    String work_status;
    String hab_code;
    String scheme_group_id;
    String work_group_id;
    String work_type_id;
    int min_img_count=0;
    int max_img_count=0;
    private static final int SPEECH_REQUEST_CODE = 103;

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
        getIntentData();
        statusFilterSpinner();
        saveWorkDetailsActivityBinding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setWorkStatusId(String.valueOf(status_list.get(position).getWork_status_id()));
                    work_status = status_list.get(position).getWork_status();

                }else {
                    prefManager.setWorkStatusId("");
                    work_status = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveWorkDetailsActivityBinding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkDetailsActivityBinding.description.setText("");
            }
        });
        saveWorkDetailsActivityBinding.tamilMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("ta");
            }
        });
        saveWorkDetailsActivityBinding.englishMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("en");
            }
        });

    }

    private void getIntentData(){
        work_id= getIntent().getIntExtra("work_id",0);
        dcode = getIntent().getStringExtra("dcode");
        bcode = getIntent().getStringExtra("bcode");
        pvcode = getIntent().getStringExtra("pvcode");
        scheme_id = getIntent().getStringExtra("scheme_id");
        fin_year = getIntent().getStringExtra("fin_year");
        work_name = getIntent().getStringExtra("work_name");
        dcode = getIntent().getStringExtra("dcode");
        as_value = getIntent().getStringExtra("as_value");
        ts_value = getIntent().getStringExtra("ts_value");
        current_stage_of_work = getIntent().getStringExtra("current_stage_of_work");
        is_high_value = getIntent().getStringExtra("is_high_value");
        hab_code = getIntent().getStringExtra("hab_code");
        scheme_group_id = getIntent().getStringExtra("scheme_group_id");
        work_group_id = getIntent().getStringExtra("work_group_id");
        work_type_id = getIntent().getStringExtra("work_type_id");
    }

    public void gotoCameraScreen()
    {
        if(!prefManager.getWorkStatusId().equals("")){
            if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                Intent intent = new Intent(this, CameraScreen.class);
                intent.putExtra("dcode", dcode);
                intent.putExtra("bcode", bcode);
                intent.putExtra("pvcode",pvcode);
                intent.putExtra("hab_code",hab_code);
                intent.putExtra("scheme_group_id",scheme_group_id);
                intent.putExtra("work_group_id",work_group_id);
                intent.putExtra("work_type_id",work_type_id);
                intent.putExtra("is_high_value",is_high_value);
                intent.putExtra("scheme_id",scheme_id);
                intent.putExtra("fin_year", fin_year);
                intent.putExtra("work_id", work_id);
                intent.putExtra("work_name", work_name);
                intent.putExtra("as_value", as_value);
                intent.putExtra("ts_value", ts_value);
                intent.putExtra("current_stage_of_work", current_stage_of_work);
                intent.putExtra("current_stage_of_work", current_stage_of_work);
                intent.putExtra("work_status_id", prefManager.getWorkStatusId());
                intent.putExtra("work_status", work_status);
                intent.putExtra("work_description", saveWorkDetailsActivityBinding.description.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
            }
        }
        else {
            Utils.showAlert(SaveWorkDetailsActivity.this,"Please Select Status");
        }
    }
    public void statusFilterSpinner() {
        status_list = new ArrayList<>();
        status_list.clear();
        ModelClass list = new ModelClass();
        list.setWork_status_id(0);
        list.setWork_status("Select Status");
        status_list.add(list);
        dbData.open();
        status_list.addAll(dbData.getAll_Work_Status());
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

    public void speechToText(String language) {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if(language.equalsIgnoreCase("en")){
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "en-IND");
        }
        else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "ta-IND");
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(SaveWorkDetailsActivity.this, " " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case SPEECH_REQUEST_CODE:

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    saveWorkDetailsActivityBinding.description.setText(
                            Objects.requireNonNull(result).get(0));
                }

                break;
            default:
                break;
        }
    }
}
