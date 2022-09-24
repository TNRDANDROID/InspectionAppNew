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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.ImageAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ViewActionScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewActionScreen extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private ViewActionScreenBinding binding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;

    Handler myHandler = new Handler();
    private ProgressHUD progressHUD;
    int work_id;
    ImageAdapter imageAdapter;
    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.view_action_screen);
        binding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        work_id=getIntent().getIntExtra("work_id",0);

        binding.workId.setText(""+work_id);

        if (Utils.isOnline()) {
            getWorkDetails();
        }else {
            Utils.showAlert(this, getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
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
        dataSet.put(AppConstant.KEY_SERVICE_ID, "work_id_wise_inspection_details_view");
        dataSet.put("work_id", work_id);

        Log.d("WorkDetails", "" + dataSet);
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
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
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

    private void workListOptionalS(JSONArray jsonArray) {
        try {

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String SelectedBlockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String inspection_id = jsonArray.getJSONObject(i).getString("inspection_id");
                    String inspection_date = jsonArray.getJSONObject(i).getString("inspection_date");
                    String status_id = jsonArray.getJSONObject(i).getString("status_id");
                    String status = jsonArray.getJSONObject(i).getString("status");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String work_name = jsonArray.getJSONObject(i).getString("work_name");

                    binding.status.setText(Utils.notNullString(status));
                    binding.description.setText(Utils.notNullString(description));
                    binding.workName.setText(Utils.notNullString(work_name));

                    JSONArray imgarray=new JSONArray();
                    imgarray=jsonArray.getJSONObject(i).getJSONArray("inspection_image");
                    if(imgarray.length() > 0){
                        ArrayList<ModelClass> activityImage = new ArrayList<>();
                        for(int j = 0; j < imgarray.length(); j++ ) {
                            try {
                                ModelClass imageOnline = new ModelClass();
                                imageOnline.setDescription(imgarray.getJSONObject(j).getString("image_description"));
                                if (!(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase("null") ||
                                        imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase(""))) {
                                    byte[] decodedString = Base64.decode(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageOnline.setImage(decodedByte);
                                    activityImage.add(imageOnline);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        if (activityImage.size() > 0) {
                            binding.recycler.setVisibility(View.VISIBLE);
                            binding.notFoundTv.setVisibility(View.GONE);
                            binding.recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            imageAdapter = new ImageAdapter(ViewActionScreen.this, activityImage,dbData);
                            binding.recycler.setAdapter(imageAdapter);
                        }else {
                            binding.recycler.setVisibility(View.GONE);
                            binding.notFoundTv.setVisibility(View.VISIBLE);
                        }
                    }

                }

            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Work");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }

}