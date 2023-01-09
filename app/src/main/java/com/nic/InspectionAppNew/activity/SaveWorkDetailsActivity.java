package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nic.InspectionAppNew.Interface.AdapterCameraIntent;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.SaveImageAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.SaveWorkDetailsActivityBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.MyLocationListener;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.CameraUtils;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class SaveWorkDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, RecognitionListener {
    private SaveWorkDetailsActivityBinding saveWorkDetailsActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> status_list = new ArrayList<>();
    private List<ModelClass> stage_list = new ArrayList<>();
    private List<ModelClass> selected_image_list = new ArrayList<>();

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

    String work_stage;
    String work_stage_id;

    String hab_code;
    String scheme_group_id;
    String work_group_id;
    String work_type_id;
    String other_work_detail="";
    String other_work_category_id="";
    String other_work_inspection_id="";
    String inspection_id="";
    String flag="";
    int min_img_count=0;
    int max_img_count=0;
    int work_status_id=0;
    String onOffType;
    String type;
    String  activityImage="";
    private static final int SPEECH_REQUEST_CODE = 103;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1213;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_CAMERA = 400;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    AdapterCameraIntent adapterCameraIntent;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private int maxLinesInput = 10;
    private TextView returnedText;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    boolean listening = false;
    SaveImageAdapter adapter;
    ArrayList<ModelClass> savedImage = new ArrayList<>();

    boolean true_flag = false;
    JSONObject maindataset = new JSONObject();
    AlertDialog add_cpts_search_alert;

    ////Location and Camera
    Double wayLatitude = 0.0, wayLongitude = 0.0;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

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


        statusFilterSpinner();
        stageFilterSpinner();

        getIntentData();

       /* RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        saveWorkDetailsActivityBinding.recycler.setLayoutManager(mLayoutManager);*/

        saveWorkDetailsActivityBinding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        saveWorkDetailsActivityBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        saveWorkDetailsActivityBinding.recycler.setHasFixedSize(true);
        saveWorkDetailsActivityBinding.recycler.setNestedScrollingEnabled(false);
        saveWorkDetailsActivityBinding.recycler.setFocusable(false);

        saveWorkDetailsActivityBinding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setWorkStatusId(String.valueOf(status_list.get(position).getWork_status_id()));
                    work_status = status_list.get(position).getWork_status();
                    work_status_id = status_list.get(position).getWork_status_id();

                }else {
                    prefManager.setWorkStatusId("");
                    work_status = "";
                    work_status_id=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        saveWorkDetailsActivityBinding.stageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    work_stage = stage_list.get(position).getWork_stage_name();
                    work_stage_id = stage_list.get(position).getWork_stage_code();

                }else {
                    work_stage = "";
                    work_stage_id="";
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
        saveWorkDetailsActivityBinding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOf();
            }
        });
        saveWorkDetailsActivityBinding.englishMic.setEnabled(true);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(true);
        saveWorkDetailsActivityBinding.clearText.setEnabled(true);
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.VISIBLE);
        Glide.with(this).asGif().load(R.raw.mic3).into(saveWorkDetailsActivityBinding.progressBarimg);

    }

    private void loadImageList(ArrayList<ModelClass> list,String f,String s) {
        savedImage = new ArrayList<>();
        min_img_count=1/*Integer.parseInt(prefManager.getPhotoCount())*/;
        max_img_count=Integer.parseInt(prefManager.getPhotoCount());
        if(list.size()>0){
            for (int j = 0; j < list.size(); j++) {
                ModelClass value = new ModelClass();
                value.setWork_id(list.get(j).getWork_id());
                value.setDistrictCode(list.get(j).getDistrictCode());
                value.setBlockCode(list.get(j).getBlockCode());
                value.setPvCode(list.get(j).getPvCode());
                value.setImage(list.get(j).getImage());
                value.setImage_path(list.get(j).getImage_path());
                value.setDescription(list.get(j).getDescription());
                value.setLatitude(list.get(j).getLatitude());
                value.setLongtitude(list.get(j).getLongtitude());
                value.setImage_serial_number(list.get(j).getImage_serial_number());
                savedImage.add(value);
            }

        }

        for (int i = savedImage.size(); i < max_img_count; i++) {
                ModelClass value = new ModelClass();
                value.setWork_id(work_id);
                value.setDistrictCode(dcode);
                value.setBlockCode(bcode);
                value.setPvCode(pvcode);
                value.setImage(null);
                value.setImage_path("");
                value.setDescription("");
                value.setLatitude("");
                value.setLongtitude("");
                value.setImage_serial_number(0);
                savedImage.add(value);
        }
        adapter = new SaveImageAdapter(SaveWorkDetailsActivity.this, savedImage,f,s);
        adapterCameraIntent=adapter;
        saveWorkDetailsActivityBinding.recycler.setAdapter(adapter);
    }
/*
    private void loadImageList(ArrayList<ModelClass> list,String f,String s) {
        boolean flag=false;
        savedImage = new ArrayList<>();
        min_img_count=1*/
/*Integer.parseInt(prefManager.getPhotoCount())*//*
;
        max_img_count=Integer.parseInt(prefManager.getPhotoCount());
        for (int i = 0; i < max_img_count; i++) {
            flag=false;
            for (int j = 0; j < list.size(); j++) {
                if(i==j){
                    flag=true;
                    ModelClass value = new ModelClass();
                    value.setWork_id(list.get(j).getWork_id());
                    value.setDistrictCode(list.get(j).getDistrictCode());
                    value.setBlockCode(list.get(j).getBlockCode());
                    value.setPvCode(list.get(j).getPvCode());
                    value.setImage(list.get(j).getImage());
                    value.setImage_path(list.get(j).getImage_path());
                    value.setDescription(list.get(j).getDescription());
                    value.setLatitude(list.get(j).getLatitude());
                    value.setLongtitude(list.get(j).getLongtitude());
                    value.setImage_serial_number(list.get(j).getImage_serial_number());
                    savedImage.add(value);
                }

            }
            if(!flag){
                ModelClass value = new ModelClass();
                value.setWork_id(work_id);
                value.setDistrictCode(dcode);
                value.setBlockCode(bcode);
                value.setPvCode(pvcode);
                value.setImage(null);
                value.setImage_path("");
                value.setDescription("");
                value.setLatitude("");
                value.setLongtitude("");
                value.setImage_serial_number(0);
                savedImage.add(value);
            }

        }
        adapter = new SaveImageAdapter(SaveWorkDetailsActivity.this, savedImage,f,s);
        adapterCameraIntent=adapter;
        saveWorkDetailsActivityBinding.recycler.setAdapter(adapter);
    }
*/

    private void getIntentData(){
        type= getIntent().getStringExtra("type");
        onOffType= getIntent().getStringExtra("onOffType");
        work_id= getIntent().getIntExtra("work_id",0);
        dcode = getIntent().getStringExtra("dcode");
        bcode = getIntent().getStringExtra("bcode");
        pvcode = getIntent().getStringExtra("pvcode");
        scheme_id = getIntent().getStringExtra("scheme_id");
        fin_year = getIntent().getStringExtra("fin_year");
        work_name = getIntent().getStringExtra("work_name");
        as_value = getIntent().getStringExtra("as_value");
        ts_value = getIntent().getStringExtra("ts_value");
        current_stage_of_work = getIntent().getStringExtra("current_stage_of_work");
        is_high_value = getIntent().getStringExtra("is_high_value");
        hab_code = getIntent().getStringExtra("hab_code");
        scheme_group_id = getIntent().getStringExtra("scheme_group_id");
        work_group_id = getIntent().getStringExtra("work_group_id");
        work_type_id = getIntent().getStringExtra("work_type_id");
        other_work_category_id = getIntent().getStringExtra("other_work_category_id");
        flag = getIntent().getStringExtra("flag");
        saveWorkDetailsActivityBinding.notEditable.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.stageNotEditable.setVisibility(View.GONE);
        if(type.equalsIgnoreCase("rdpr")){
            saveWorkDetailsActivityBinding.otherWorksLayout.setVisibility(View.GONE);
            saveWorkDetailsActivityBinding.stageLayout.setVisibility(View.VISIBLE);
        }else {
            saveWorkDetailsActivityBinding.otherWorksLayout.setVisibility(View.VISIBLE);
            saveWorkDetailsActivityBinding.stageLayout.setVisibility(View.GONE);
        }
        dbData.open();
        savedImage = new ArrayList<>();
        loadImageList(savedImage,flag,"");
        if(onOffType.equals("online")){
            if(flag.equalsIgnoreCase("edit")){
                saveWorkDetailsActivityBinding.submit.setText("Update");
            }else {
                saveWorkDetailsActivityBinding.submit.setText("Submit");
            }

        }

        if(flag.equalsIgnoreCase("edit")){
            if(type.equalsIgnoreCase("rdpr")){
                work_status_id = getIntent().getIntExtra("status_id",0);
                String  status = getIntent().getStringExtra("status");
                String  work_name = getIntent().getStringExtra("work_name");
                String  description = getIntent().getStringExtra("description");
                inspection_id = getIntent().getStringExtra("inspection_id");
                activityImage = getIntent().getStringExtra("activityImage");
                saveWorkDetailsActivityBinding.description.setText(description);
                saveWorkDetailsActivityBinding.stageLayout.setVisibility(View.GONE);
                saveWorkDetailsActivityBinding.statusLayout.setVisibility(View.GONE);
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == work_status_id){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                    }
                }
/*
                for(int i=0;i<stage_list.size();i++){
                    if(stage_list.get(i).getWork_stage_code() .equalsIgnoreCase(current_stage_of_work) ){
                        saveWorkDetailsActivityBinding.stageSpinner.setSelection(i);
                    }
                }
*/
                saveWorkDetailsActivityBinding.stageSpinner.setEnabled(false);
                saveWorkDetailsActivityBinding.stageNotEditable.setVisibility(View.VISIBLE);
                saveWorkDetailsActivityBinding.statusSpinner.setEnabled(false);
                saveWorkDetailsActivityBinding.notEditable.setVisibility(View.VISIBLE);
            }else {
                other_work_inspection_id = getIntent().getStringExtra("other_work_inspection_id");
                String  other_work_category_name = getIntent().getStringExtra("other_work_category_name");
                work_status_id = getIntent().getIntExtra("status_id",0);
                String  status = getIntent().getStringExtra("status");
                String  other_work_detail = getIntent().getStringExtra("other_work_detail");
                String  description = getIntent().getStringExtra("description");
                activityImage = getIntent().getStringExtra("activityImage");
                saveWorkDetailsActivityBinding.description.setText(description);
                saveWorkDetailsActivityBinding.otherWorkDetail.setText(other_work_detail);
                saveWorkDetailsActivityBinding.stageLayout.setVisibility(View.GONE);
                saveWorkDetailsActivityBinding.statusLayout.setVisibility(View.GONE);
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == work_status_id){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                    }
                }
            }
            try {
                savedImage=new ArrayList<>();
                JSONArray imgarray=prefManager.getImageJson();
                if(imgarray.length() > 0){

                    for(int j = 0; j < imgarray.length(); j++ ) {
                        try {
                            ModelClass imageOnline = new ModelClass();
                            imageOnline.setDescription(imgarray.getJSONObject(j).getString("image_description"));
                            if (!(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase("null") ||
                                    imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase(""))) {
                                byte[] decodedString = Base64.decode(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imageOnline.setImage(decodedByte);
                                savedImage.add(imageOnline);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    loadImageList(savedImage,flag,"");
                    /*adapter = new SaveImageAdapter(SaveWorkDetailsActivity.this, savedImage,flag,"");
                    adapterCameraIntent=adapter;
                    saveWorkDetailsActivityBinding.recycler.setAdapter(adapter);*/
                }


            } catch (ArrayIndexOutOfBoundsException j) {
                j.printStackTrace();
            }


        }else {
            dbData.open();
            ArrayList<ModelClass> savedCount = new ArrayList<>();
            savedCount=dbData.getSavedWorkList("",String.valueOf(work_id),dcode,bcode,pvcode,"");

            if(savedCount.size()>0){
                saveWorkDetailsActivityBinding.description.setText(savedCount.get(0).getWork_description());
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == savedCount.get(0).getWork_status_id()){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                    }
                }
                for(int i=0;i<stage_list.size();i++){
                    if(stage_list.get(i).getWork_stage_code() .equalsIgnoreCase(savedCount.get(0).getWork_stage_code()) ){
                        saveWorkDetailsActivityBinding.stageSpinner.setSelection(i);
                    }
                }

                try {
                    savedImage=new ArrayList<>();
                    savedImage=dbData.getParticularSavedImagebycode("all",dcode,bcode,pvcode,String.valueOf(work_id),"","");

                    if(savedImage.size() > 0){
                        loadImageList(savedImage,flag,"local");
                        /*adapter = new SaveImageAdapter(SaveWorkDetailsActivity.this, savedImage,"","local");
                        adapterCameraIntent=adapter;
                        saveWorkDetailsActivityBinding.recycler.setAdapter(adapter);*/
                    }


                } catch (ArrayIndexOutOfBoundsException j) {
                    j.printStackTrace();
                }
                saveWorkDetailsActivityBinding.submit.setText("Update");

            }
            else {
                saveWorkDetailsActivityBinding.description.setText("");
                saveWorkDetailsActivityBinding.statusSpinner.setSelection(0);
                saveWorkDetailsActivityBinding.stageSpinner.setSelection(0);
                savedImage=new ArrayList<>();
                loadImageList(savedImage,flag,"");
                saveWorkDetailsActivityBinding.submit.setText("Submit");
            }
        }


    }
    public void gotoSubmit()
    {
        if(flag.equals("edit")){
            gotoUpdate();
        }
        else {
            gotoSave();
        }
    }
    public void gotoSave()
    {
        ArrayList<ModelClass> list=adapter.finalImageList();
        if(type.equalsIgnoreCase("rdpr")){
            if(checkImageList(list)) {
                if (!work_stage_id.isEmpty()) {
//                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                    if(work_status_id != 0){
                        if(onOffType.equals("online")){
//                        onLineUploadData();
                            new uploadTask().execute();
                        }
                        else {
                            saveImageButtonClick();
                        }

                    } else {
                        Utils.showAlert(SaveWorkDetailsActivity.this,"Please Select Status");

                    }

               /* }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                }*/
                } else {
                    Utils.showAlert(SaveWorkDetailsActivity.this, "Please Select Stage");
                }
            }else {
                Utils.showAlert(SaveWorkDetailsActivity.this, "At least Capture one Photo");
            }
        }
        else {
            if(checkImageList(list)){
                if(work_status_id != 0){
                    if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){

                        if(!saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString().equals("")){
                            if(onOffType.equals("online")){
//                        onLineUploadData();
                                new uploadTask().execute();
                            }
                            else {
                                saveImageButtonClick();
                            }

                        } else {
                            Utils.showAlert(SaveWorkDetailsActivity.this,"Enter Other Inspection Detail");
                        }
                    }
                    else {
                        Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                    }

                }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please select status");
                }
            }
            else {

                Utils.showAlert(SaveWorkDetailsActivity.this, "At least Capture one Photo");
            }
        }

    }

    public void gotoUpdate()
    {
        ArrayList<ModelClass> list=adapter.finalImageList();
        if(type.equalsIgnoreCase("rdpr")){
            if(checkImageList(list)) {
                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                        if(onOffType.equals("online")){
//                        onLineUploadData();
                            new uploadTask().execute();
                        }
                        else {
                            saveImageButtonClick();
                        }


                }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                }

            }else {
                Utils.showAlert(SaveWorkDetailsActivity.this, "At least Capture one Photo");
            }
        }
        else {
            if(checkImageList(list)){
                    if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){

                        if(!saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString().equals("")){
                            if(onOffType.equals("online")){
//                        onLineUploadData();
                                new uploadTask().execute();
                            }
                            else {
                                saveImageButtonClick();
                            }

                        } else {
                            Utils.showAlert(SaveWorkDetailsActivity.this,"Enter Other Inspection Detail");
                        }
                    }
                    else {
                        Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                    }

            }
            else {

                Utils.showAlert(SaveWorkDetailsActivity.this, "At least Capture one Photo");
            }
        }

    }
    public class uploadTask extends AsyncTask<JSONObject, Void, Boolean> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(CameraScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(SaveWorkDetailsActivity.this, "Loading...", true, false, null);
        }

        @Override
        protected void onPostExecute(Boolean true_flag) {
            super.onPostExecute(true_flag);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(true_flag){
                if (Utils.isOnline()) {
                    uploadDialog(maindataset);
                    //saveImagesJsonParams(maindataset);
                    Log.d("saveImages", "" + maindataset);
                } else {

                    Utils.showAlert(SaveWorkDetailsActivity.this, "Turn On Mobile Data To Upload");
                }
            }

        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            if(prefManager.getWorkType().equalsIgnoreCase("rdpr")){

                if(flag.equalsIgnoreCase("edit")){
                    true_flag=onLineRDPREditUploadData();
                }else {
                    true_flag=onLineUploadData();
                }
            }else {
                if(flag.equalsIgnoreCase("edit")){
                    true_flag=onlineOtherWorkEditUploadData();
                }else {
                    true_flag=onlineOtherWorkUploadData();
                }

            }

            return true_flag;
        }

    }
    public boolean onLineUploadData() {
        maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray inspection_work_details = new JSONArray();
        true_flag = false;
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"work_inspection_details_save");
            dataset.put("dcode",dcode);
            dataset.put("bcode", bcode);
            dataset.put("pvcode",pvcode);
            dataset.put("hab_code",hab_code);
            dataset.put("work_id", work_id);
            dataset.put("status_id", work_status_id);
            dataset.put("description", saveWorkDetailsActivityBinding.description.getText().toString());
            dataset.put("work_group_id", work_group_id);
            dataset.put("work_type_id", work_type_id);
            dataset.put("work_stage_code", work_stage_id);

            int childCount = selected_image_list.size();
            int count = 0;
            JSONArray imageArray = new JSONArray();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {

                    if (selected_image_list.get(i).getImage() != null) {
                        //if(!myEditTextView.getText().toString().equals("")){
                        count = count + 1;
                        String image_str = "";
                        try {
                            image_str = BitMapToString(selected_image_list.get(i).getImage());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("latitude",selected_image_list.get(i).getLatitude());
                            jsonObject.put("longitude",selected_image_list.get(i).getLongtitude());
                            jsonObject.put("serial_no",count);
                            jsonObject.put("image_description",selected_image_list.get(i).getDescription());
                            jsonObject.put("image",image_str);
                            imageArray.put(jsonObject);

                            if(count==childCount)
                            { true_flag = true; }
                            else { true_flag = false; }
                        } catch (Exception e) {
                            this.runOnUiThread(new Runnable() {public void run() { Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.at_least_capture_one_photo)); }});
                        }
                    }
                    else {
                        this.runOnUiThread(new Runnable() {public void run() { Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.please_capture_image)); }});
                    }
                }
                dataset.put("image_details",imageArray);
                inspection_work_details.put(dataset);
                maindataset.put("inspection_work_details",inspection_work_details);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return true_flag;
    }
    public boolean onLineRDPREditUploadData() {
        maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray inspection_work_details = new JSONArray();
        true_flag = false;
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"work_inspection_details_save");
            dataset.put("dcode",dcode);
            dataset.put("bcode", bcode);
            dataset.put("pvcode",pvcode);
            dataset.put("hab_code",hab_code);
            dataset.put("work_id", work_id);
//            dataset.put("status_id", work_status_id);
            dataset.put("description", saveWorkDetailsActivityBinding.description.getText().toString());
            dataset.put("inspection_id", inspection_id);
            inspection_work_details.put(dataset);
            maindataset.put("inspection_work_details",inspection_work_details);
            true_flag = true;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return true_flag;
    }
    public boolean onlineOtherWorkUploadData() {
        maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray inspection_work_details = new JSONArray();
        true_flag = false;
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"other_work_inspection_details_save");
            dataset.put("dcode",dcode);
            dataset.put("bcode", bcode);
            dataset.put("pvcode",pvcode);
            dataset.put("hab_code",hab_code);
            dataset.put("fin_year", fin_year);
            dataset.put("status_id", work_status_id);
            dataset.put("other_work_category_id", other_work_category_id);
            dataset.put("other_work_detail", saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString());
            dataset.put("description", saveWorkDetailsActivityBinding.description.getText().toString());

            int childCount = selected_image_list.size();
            int count = 0;
            JSONArray imageArray = new JSONArray();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {

                    if (selected_image_list.get(i).getImage() != null) {
                        //if(!myEditTextView.getText().toString().equals("")){
                        count = count + 1;
                        String image_str = "";
                        try {
                            image_str = BitMapToString(selected_image_list.get(i).getImage());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("latitude",selected_image_list.get(i).getLatitude());
                            jsonObject.put("longitude",selected_image_list.get(i).getLongtitude());
                            jsonObject.put("serial_no",count);
                            jsonObject.put("image_description",selected_image_list.get(i).getDescription());
                            jsonObject.put("image",image_str);
                            imageArray.put(jsonObject);

                            if(count==childCount)
                            { true_flag = true; }
                            else { true_flag = false; }
                        } catch (Exception e) {
                            this.runOnUiThread(new Runnable() {public void run() { Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.at_least_capture_one_photo)); }});
                        }
                    }
                    else {
                        this.runOnUiThread(new Runnable() {public void run() { Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.please_capture_image)); }});
                    }
                }
                dataset.put("image_details",imageArray);
                inspection_work_details.put(dataset);
                maindataset.put("other_inspection_work_details",inspection_work_details);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return true_flag;
    }
    public boolean onlineOtherWorkEditUploadData() {
        maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray inspection_work_details = new JSONArray();
        true_flag = false;
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"other_work_inspection_details_update");
            dataset.put("dcode",dcode);
            dataset.put("bcode", bcode);
            dataset.put("pvcode",pvcode);
            dataset.put("hab_code",hab_code);
            dataset.put("fin_year", fin_year);
            dataset.put("status_id", work_status_id);
            dataset.put("other_work_category_id", other_work_category_id);
            dataset.put("other_work_detail", saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString());
            dataset.put("description", saveWorkDetailsActivityBinding.description.getText().toString());
            dataset.put("other_work_inspection_id", other_work_inspection_id);
            inspection_work_details.put(dataset);
            maindataset.put("other_inspection_work_details",inspection_work_details);
            true_flag = true;

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return true_flag;
    }
    private void uploadDialog(JSONObject jsonObject){
        try {
            final Dialog dialog = new Dialog(SaveWorkDetailsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            text.setText("Are you sure to upload data into server?");

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
//                    saveImagesJsonParams(jsonObject);
                    new saveTask().execute(jsonObject);
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class saveTask extends AsyncTask<JSONObject, Void, JSONObject> {
        private ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(SaveWorkDetailsActivity.this, "Loading...", true, false, null);
//            Utils.showProgress(CameraScreen.this,progressHUD);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }


            new ApiService(SaveWorkDetailsActivity.this).makeJSONObjectRequest("saveImage", Api.Method.POST, UrlGenerator.getMainService(), jsonObject, "not cache", SaveWorkDetailsActivity.this
            );

        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
//            saveImagesJsonParams(params[0]);
            String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), params[0].toString());
            JSONObject dataSet = new JSONObject();
            try {
                dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
                dataSet.put(AppConstant.DATA_CONTENT, authKey);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("saveImage", "" +  params[0].toString());
            Log.d("saveImage", "" + dataSet);
            return dataSet;
        }

    }
    public void saveImageButtonClick() {
        long work_insert_primary_id = 0;
        String whereCl = "";String[] whereAr = null;
        String[] selectionArgs;
        String selection;
        long rowInsert = 0;
        long rowUpdat = 0;
        try {
            ContentValues values = new ContentValues();

            values.put("dcode",dcode);
            values.put("bcode",bcode);
            values.put("pvcode",pvcode);
            values.put("scheme_id",scheme_id);
            values.put("fin_year",fin_year);
            values.put("work_id",work_id);
            values.put("work_name",work_name);
            values.put("as_value",as_value);
            values.put("ts_value",ts_value);
            values.put("current_stage_of_work",current_stage_of_work);
            values.put("is_high_value",is_high_value);
            values.put("work_status_id",work_status_id);
            values.put("work_status",work_status);
            values.put("work_stage_id",work_stage_id);
            values.put("work_stage",work_stage);
            values.put("work_description",saveWorkDetailsActivityBinding.description.getText().toString());
            values.put("hab_code",hab_code);
            values.put("scheme_group_id",scheme_group_id);
            values.put("work_group_id",work_group_id);
            values.put("work_type_id",work_type_id);
            values.put("flag","1");
            selection = "work_id = ?";
            selectionArgs = new String[]{String.valueOf(work_id)};
            dbData.open();
            ArrayList<ModelClass> saveCount = new ArrayList<>();
            saveCount=dbData.getSavedWorkList("",String.valueOf(work_id),dcode,bcode,pvcode,"");
            if(saveCount.size()>0){
                rowInsert = db.update(DBHelper.SAVE_WORK_DETAILS,values,selection,selectionArgs);
            }
            else {
                rowInsert = db.insert(DBHelper.SAVE_WORK_DETAILS,null,values);
            }


        } catch (Exception e) {

        }
        if (rowInsert>0){

            long id = 0; String whereClause = "";String[] whereArgs = null;

            JSONArray imageJson = new JSONArray();
            long rowInserted = 0;
            long rowUpdated = 0;
            int childCount = selected_image_list.size();
            int count = 0;
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    if (selected_image_list.get(i).getImage()  != null) {
                        // if(!myEditTextView.getText().toString().equals("")){
                        count = count + 1;
                        byte[] imageInByte = new byte[0];
                        String image_str = "";
                        String description = "";
                        String image_path = "";
                        Bitmap bitmap = null;
                        try {
                            bitmap = selected_image_list.get(i).getImage() ;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            imageInByte = baos.toByteArray();
                            image_path = fileDirectory(bitmap,"work_list",String.valueOf(i));




                        } catch (Exception e) {
                            Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.at_least_capture_one_photo));
                        }


                        ContentValues imageValue = new ContentValues();
                        imageValue.put("save_work_details_primary_id", rowInsert);
                        imageValue.put("work_id", selected_image_list.get(i).getWork_id() );
                        imageValue.put("image_description", selected_image_list.get(i).getDescription() );
                        imageValue.put("image_path", selected_image_list.get(i).getImage_path() );
                        imageValue.put("image", BitMapToString(selected_image_list.get(i).getImage()));
                        imageValue.put("latitude", selected_image_list.get(i).getLatitude());
                        imageValue.put("longitude", selected_image_list.get(i).getLongtitude());
                        imageValue.put("dcode", selected_image_list.get(i).getDistrictCode());
                        imageValue.put("bcode", selected_image_list.get(i).getBlockCode());
                        imageValue.put("pvcode", selected_image_list.get(i).getPvCode());
                        imageValue.put("serial_no", count);
                        imageValue.put("flag","1");

                        selection = "dcode = ? and bcode = ? and pvcode = ? and work_id = ? and serial_no = ?";
                        selectionArgs = new String[]{String.valueOf(selected_image_list.get(i).getDistrictCode()),String.valueOf(selected_image_list.get(i).getBlockCode()),
                                String.valueOf(selected_image_list.get(i).getPvCode()), String.valueOf(work_id), String.valueOf(count)};
                        ArrayList<ModelClass> imageCount = new ArrayList<>();
                        dbData.open();
                        imageCount = dbData.getParticularSavedImagebycode("",String.valueOf(selected_image_list.get(i).getDistrictCode()),String.valueOf(selected_image_list.get(i).getBlockCode()),
                                String.valueOf(selected_image_list.get(i).getPvCode()), String.valueOf(work_id),String.valueOf(count),"");

                        if(imageCount.size()>0){
                            for(int j=0;  j<imageCount.size() ;j++){
                                String filepath=imageCount.get(j).getImage_path();
                                Utils.deleteFileDirectory(filepath);
                            }

                            rowUpdated =db.update(DBHelper.SAVE_IMAGES,imageValue,selection,selectionArgs);
                        }
                        else {
                            rowInserted =db.insert(DBHelper.SAVE_IMAGES,null,imageValue);
                        }

                        if (count == childCount) {
                            if (rowInserted > 0) {

                                showToast(getResources().getString(R.string.success));
                            }else if(rowUpdated > 0){
                                showToast(getResources().getString(R.string.success));
                            }

                        }


                   /* }
                        else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.enter_description));
                    }*/
                    } else {
                        Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.please_capture_image));
                    }
                }
            }
        }
    }
    public String fileDirectory(Bitmap bitmap,String type,String count){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(type, Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String child_path = Utils.getCurrentDateTime()+"_"+count+".png";
        File mypath = new File(directory, child_path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return mypath.toString();
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private boolean checkImageList(ArrayList<ModelClass> list) {
        boolean flag=false;
        selected_image_list=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if(list.get(i).getImage()!=null){
                flag=true;
                selected_image_list.add(list.get(i));
            }
        }
        return flag;
    }

    public void gotoCameraScreen()
    {
        if(type.equalsIgnoreCase("rdpr")){
            if(work_status_id != 0){
//                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
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
                    intent.putExtra("work_status_id", work_status_id);
                    intent.putExtra("work_status", work_status);
                    intent.putExtra("work_stage_id", work_stage_id);
                    intent.putExtra("work_stage", work_stage);
                    intent.putExtra("onOffType", onOffType);
                    intent.putExtra("work_description", saveWorkDetailsActivityBinding.description.getText().toString());
                    intent.putExtra("other_work_category_id", other_work_category_id);
                    intent.putExtra("other_work_detail", other_work_detail);
                    intent.putExtra("activityImage",activityImage);
                    intent.putExtra("type","rdpr");
                    intent.putExtra("flag",flag);
                    if(flag.equalsIgnoreCase("edit")){
                        intent.putExtra("inspection_id",inspection_id);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
               /* }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                }*/
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Please Select Status");
            }
        }
        else {
            if(!saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString().equals("")){
            if(work_status_id != 0){
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
                    intent.putExtra("work_status_id", work_status_id);
                    intent.putExtra("work_status", work_status);
                    intent.putExtra("onOffType", onOffType);
                    intent.putExtra("work_description", saveWorkDetailsActivityBinding.description.getText().toString());
                    intent.putExtra("other_work_category_id", other_work_category_id);
                    intent.putExtra("other_work_detail", saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString());
                    intent.putExtra("activityImage",activityImage);
                    intent.putExtra("type","other");
                    intent.putExtra("flag",flag);
                    if(flag.equalsIgnoreCase("edit")){
                        intent.putExtra("other_work_inspection_id",other_work_inspection_id);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please enter description");
                }
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Please select status");
            }
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Enter Other Inspection Detail");
            }
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
    public void stageFilterSpinner() {
        String work_group_id=getIntent().getStringExtra("work_group_id");
        String work_type_id=getIntent().getStringExtra("work_type_id");
        String current_stage_of_work=getIntent().getStringExtra("current_stage_of_work");
        stage_list = new ArrayList<>();
        stage_list.clear();
        ModelClass list = new ModelClass();
        list.setWork_group_id("0");
        list.setWork_type_id("0");
        list.setWork_stage_code("0");
        list.setWork_stage_name("Current stage observed by inspecting officer");
        stage_list.add(list);
        dbData.open();
        stage_list.addAll(dbData.getAll_Stage("",work_group_id,work_type_id,current_stage_of_work));
        saveWorkDetailsActivityBinding.stageSpinner.setAdapter(new CommonAdapter(this, stage_list, "stage_list"));
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void homePage() {
        Intent intent = new Intent(this, MainHomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        finish();
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
            if ("saveImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    if(flag.equalsIgnoreCase("edit")){
                        showAlert(this, "Your Data Updated Successfully!");
                        JSONArray j=new JSONArray();
                        prefManager.setImageJson(j);
                    }else {
                        showAlert(this, "Your Data is Synchronized to the server!");
                        removeSavedItem(work_id,dcode,bcode,pvcode);
                    }
/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            homePage();
                        }
                    }, 500);
*/


                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    Utils.showAlert(this, jsonObject.getString("MESSAGE"));
                }
                Log.d("savedImage", "" + responseObj.toString());
                Log.d("savedImage", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void removeSavedItem(int work_id, String dcode, String bcode, String pvcode) {
        dbData.open();
        db.delete(DBHelper.SAVE_WORK_DETAILS, "work_id = ?", new String[]{String.valueOf(work_id)});
        db.delete(DBHelper.SAVE_IMAGES, "dcode = ? and bcode = ? and pvcode = ? and work_id = ?",
                new String[]{String.valueOf(dcode),String.valueOf(bcode),String.valueOf(pvcode),String.valueOf(work_id)});
        deleteSavedImage(String.valueOf(dcode),String.valueOf(bcode),String.valueOf(pvcode),String.valueOf(work_id));
    }
    private void deleteSavedImage(String dcode,String bcode,String pvcode,String work_id) {
        ArrayList<ModelClass> activityImage = new ArrayList<>();
        activityImage = dbData.getParticularSavedImagebycode("all",dcode,bcode, pvcode,work_id,"","");
        for (int i=0; i < activityImage.size();i++){
            String file_path= activityImage.get(i).getImage_path();
            deleteFileDirectory(file_path);
        }

    }
    private void deleteFileDirectory(String file_path){
        File file = new File(file_path);
        // call deleteDirectory method to delete directory
        // recursively
        file.delete();

    }
    public  void showAlert(Activity activity, String msg){
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(type.equalsIgnoreCase("rdpr")){
                        onBackPress();
                    }else {
                        if(flag.equalsIgnoreCase("edit")){
                            onBackPress();
                        }else {
                            prefManager.setAppBack("");
                            homePage();
                        }
                    }
//                    homePage();


                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPress() {
        prefManager.setAppBack("");
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

/*
    public void speechToText(String language) {
        listening = true;
        start(language);

        ActivityCompat.requestPermissions
                (SaveWorkDetailsActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }
*/

    public void start(String language){
        saveWorkDetailsActivityBinding.englishMic.setEnabled(false);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(false);
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.VISIBLE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.clearText.setEnabled(false);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if(language.equalsIgnoreCase("en")){
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "en-US");

        }
        else {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "ta-IND");
        }
/*
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
*/
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxLinesInput);
    }

    public void turnOf(){
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.VISIBLE);
        saveWorkDetailsActivityBinding.englishMic.setEnabled(true);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(true);
        saveWorkDetailsActivityBinding.clearText.setEnabled(true);

        speech.stopListening();
        speech.destroy();
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
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, Long.valueOf(10000));//5sec
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, Long.valueOf(20000));//5sec
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, Long.valueOf(5000));//5sec
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, Long.valueOf(5000));//5sec

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(SaveWorkDetailsActivity.this, " " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public  Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,"IMG_" + Calendar.getInstance().getTime(),null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                       /* Bitmap rotatedBitmap = BitmapFactory.decodeFile(filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);*/
                        Bitmap i = (Bitmap) data.getExtras().get("data");
                        String filePath = fileDirectory(i,"work_list",String.valueOf(i));
                        if(adapterCameraIntent!=null){
                            adapterCameraIntent.OnIntentListener(i,filePath,wayLatitude,wayLongitude);
                        }

                    }
                    else {
                        // Refreshing the gallery
                        CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                        // successfully captured the image
                        // display it in image view
                        previewCapturedImage();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }



            }else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(this,
                        this.getResources().getString(R.string.user_cancelled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(this,
                        this.getResources().getString(R.string.sorry_failed_to_capture_image), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(this, imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            }
            else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(this,
                        this.getResources().getString(R.string.user_cancelled_video_recording), Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                // failed to record video
                Toast.makeText(this,
                        this.getResources().getString(R.string.sorry_faild_to_record_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if(requestCode == SPEECH_REQUEST_CODE){
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                    saveWorkDetailsActivityBinding.description.setText(saveWorkDetailsActivityBinding.description.getText().toString()+" "+
                            Objects.requireNonNull(result).get(0));
                }else {
                    saveWorkDetailsActivityBinding.description.setText(Objects.requireNonNull(result).get(0));

                }
            }

        }
    }
    public void showToast(String s){
        Toasty.success(SaveWorkDetailsActivity.this,s,Toast.LENGTH_SHORT,true).show();
       /* super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);*/
        if(type.equalsIgnoreCase("rdpr")){
            onBackPressed();
        }else {
            prefManager.setAppBack("");
            homePage();
        }

    }

    public void captureImage() {
       /* Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CROP, false);//default is false
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(SaveWorkDetailsActivity.this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
//            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            Bitmap bitmap = Utils.resizedBitmap(imageStoragePath,SaveWorkDetailsActivity.this);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            if(adapterCameraIntent!=null){
                adapterCameraIntent.OnIntentListener(rotatedBitmap, imageStoragePath, wayLatitude, wayLongitude);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(prefManager.getAppBack() != null && prefManager.getAppBack().equalsIgnoreCase("back")){
            prefManager.setAppBack("");
            onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (speech != null) {
//            speech.destroy();
//            Log.i(LOG_TAG, "destroy");
//        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        /*if(!listening){
            turnOf();
            saveWorkDetailsActivityBinding.progressBar.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
//        returnedText.setText(errorMessage);
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onResults="+text);

        if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
            saveWorkDetailsActivityBinding.description.setText(saveWorkDetailsActivityBinding.description.getText().toString()+" "+
                    matches.get(0));
        }else {
            saveWorkDetailsActivityBinding.description.setText(matches.get(0));

        }

        speech.startListening(recognizerIntent);

    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onPartialResults="+text);

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
//                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    public void callTextAnswer(final int position) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View edit_cpt_list_layout = inflater.inflate(R.layout.pop_up_text_answer, null);

            EditText type_answer = (EditText) edit_cpt_list_layout.findViewById(R.id.type_answer);
            final TextView pop_msg = (TextView) edit_cpt_list_layout.findViewById(R.id.pop_msg);
            ImageView close_pop = (ImageView) edit_cpt_list_layout.findViewById(R.id.close_pop_up);
            TextView submit_text=(TextView)edit_cpt_list_layout.findViewById(R.id.submit_test);
            close_pop.setVisibility(View.VISIBLE);
            type_answer.setSelection(0);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setView(edit_cpt_list_layout);
            dialogBuilder.setCancelable(true);
            add_cpts_search_alert = dialogBuilder.create();
           /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(add_cpts_search_alert.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            lp.windowAnimations = R.style.popUp_animation;
            add_cpts_search_alert.getWindow().setAttributes(lp);*/
            add_cpts_search_alert.show();
            add_cpts_search_alert.setCanceledOnTouchOutside(false);
            add_cpts_search_alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            add_cpts_search_alert.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            type_answer.setFocusable(true);
            if(savedImage.get(position).getDescription() !=null && !savedImage.get(position).getDescription().equals(""))
            {
                type_answer.setText(savedImage.get(position).getDescription());
            }
            submit_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String answer = type_answer.getText().toString().trim();

                    if(answer.isEmpty() || answer.length() == 0 || answer.equals("") || answer == null)
                    {//EditText is empty
                        Toast.makeText(SaveWorkDetailsActivity.this, getApplicationContext().getResources().getString(R.string.enter_description), Toast.LENGTH_LONG).show();
                    } else
                    {//EditText is not empty
                        // Toast.makeText(mContext, mContext.getResources().getString(R.string.answer_successfully), Toast.LENGTH_LONG).show();
                        add_cpts_search_alert.dismiss();
                        savedImage.get(position).setDescription(type_answer.getText().toString());
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            close_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add_cpts_search_alert.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////getlOCATION AND Camera

    public void getExactLocation() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(SaveWorkDetailsActivity.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // check permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
                // reuqest for permission

            }
            else {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //locationRequest.setInterval(0);

                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                         wayLatitude= location.getLatitude();
                        wayLongitude = location.getLongitude();
                        Log.d("LocationAccuracy", "" + location.getAccuracy());
                        Log.d("Locations", "" + wayLatitude + "," + wayLongitude);

                        if (ContextCompat.checkSelfPermission(SaveWorkDetailsActivity.this,
                                CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            captureImage();

                        }else {
                            ActivityCompat.requestPermissions(this, new String[]{CAMERA},
                                    PERMISSION_CAMERA);

                        }

                    } else {
                        Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.satellite_communication_not_available));

                    }
                });
            }
        }
        else {
            Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.gps_is_not_turned_on));
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    captureImage();
                    Log.i( "LOG_TAG","Permission granted");

                }
                // Cancelled or denied.
                else {
                    Log.i("LOG_TAG","Permission denied");
//                    Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{CAMERA},
                            PERMISSION_CAMERA);

                }

            }
            break;
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SaveWorkDetailsActivity.this, "start talk...", Toast
                            .LENGTH_SHORT).show();
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(SaveWorkDetailsActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
                break;
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setInterval(0);

                    mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    mlocListener = new MyLocationListener();


                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(SaveWorkDetailsActivity.this,
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

                    }

                    if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                Log.d("LocationAccuracy", "" + location.getAccuracy());
                                Log.d("Locations", "" + wayLatitude + "," + wayLongitude);
                                if (ContextCompat.checkSelfPermission(SaveWorkDetailsActivity.this,
                                        CAMERA)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    captureImage();

                                }else {
                                    ActivityCompat.requestPermissions(this, new String[]{CAMERA},
                                            PERMISSION_CAMERA);

                                }



                            } else {
                                Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.satellite_communication_not_available));
                            }
                        });
                    }
                    else {
                        Utils.showAlert(SaveWorkDetailsActivity.this, getResources().getString(R.string.gps_is_not_turned_on));
                    }
                } else {
//                    Toast.makeText(this, "Permission got", Toast.LENGTH_SHORT).show();
                    showPermissionsAlert();
                }


                break;
            }
        }
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permissions_required))
                .setMessage(getResources().getString(R.string.allow_camera_location_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(SaveWorkDetailsActivity.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

}
