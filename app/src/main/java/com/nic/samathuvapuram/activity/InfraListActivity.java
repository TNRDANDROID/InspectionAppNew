package com.nic.samathuvapuram.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.adapter.InfraListAdapter;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ApiService;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.InfraListScreenBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.ProgressHUD;
import com.nic.samathuvapuram.utils.UrlGenerator;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfraListActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private InfraListScreenBinding infraListScreenBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    Handler myHandler = new Handler();
    private ArrayList<ModelClass> infralist = new ArrayList<>();
    private ProgressHUD progressHUD;
    InfraListAdapter infraListAdapter;
    int samathuvapuram_id;
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        infraListScreenBinding = DataBindingUtil.setContentView(this, R.layout.infra_list_screen);
        infraListScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        samathuvapuram_id = getIntent().getIntExtra("samathuvapuram_id",0);
        infraListScreenBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        infraListScreenBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        infraListScreenBinding.recycler.setHasFixedSize(true);
        infraListScreenBinding.recycler.setNestedScrollingEnabled(false);
        infraListScreenBinding.recycler.setFocusable(false);

        infraListScreenBinding.addInfra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfraListActivity.this, SaveAeInfraDetailsActivity.class);
                intent.putExtra("samathuvapuram_id",samathuvapuram_id);
                startActivity(intent);
            }
        });

       if(Utils.isOnline()){
           infra_list();
       }
       else {
           new fetch_infra_list().execute();
       }

    }

    public void delete(int samathuvapuram_id, int repairInfraEstimateId) {
        try {
            final Dialog dialog = new Dialog(InfraListActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            text.setText("Are you sure to delete this data?");

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
                    try {
                        new ApiService(InfraListActivity.this).makeJSONObjectRequest("delete", Api.Method.POST, UrlGenerator.getMainService(), delete_JSONParams(samathuvapuram_id,repairInfraEstimateId), "not cache", InfraListActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JSONObject delete_JSONParams(int samathuvapuram_id, int repairInfraEstimateId) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), delete_JsonParams(samathuvapuram_id,repairInfraEstimateId).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("infra_delete", "" + dataSet);
        return dataSet;
    }
    public  JSONObject delete_JsonParams(int samathuvapuram_id, int repairInfraEstimateId) throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "blk_ae_samathuvapuram_infra_delete");
        dataSet.put("samathuvapuram_id", samathuvapuram_id);
        dataSet.put("repair_infra_estimate_id", repairInfraEstimateId);
        Log.d("infra_delete", "" + dataSet);
        return dataSet;
    }
    public class fetch_infra_list extends AsyncTask<Void, Void,
            ArrayList<ModelClass>> {
        @Override
        protected ArrayList<ModelClass> doInBackground(Void... params) {
            dbData.open();
            infralist = new ArrayList<>();
            infralist = dbData.getAll_Particular_samathuvapuram_infralist(String.valueOf(samathuvapuram_id));
            Log.d("infralist_COUNT", String.valueOf(infralist.size()));
            return infralist;
        }

        @Override
        protected void onPostExecute(ArrayList<ModelClass> infralist) {
            super.onPostExecute(infralist);
            if(!Utils.isOnline()) {
                if (infralist.size() == 0) {
                    Utils.showAlert(InfraListActivity.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if (infralist.size() > 0) {
                infraListScreenBinding.recycler.setVisibility(View.VISIBLE);
                infraListScreenBinding.notFoundTv.setVisibility(View.GONE);
                infraListAdapter = new InfraListAdapter(InfraListActivity.this, infralist,dbData);
                infraListScreenBinding.recycler.setAdapter(infraListAdapter);
            }else {
                infraListScreenBinding.recycler.setVisibility(View.GONE);
                infraListScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
            }


        }
        }


    public void infra_list() {
        try {
            new ApiService(this).makeJSONObjectRequest("infra_list", Api.Method.POST, UrlGenerator.getMainService(), infra_list_JSONParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject infra_list_JSONParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), infra_list_JsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("infra_list", "" + dataSet);
        return dataSet;
    }
    public  JSONObject infra_list_JsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "blk_ae_samathuvapuram_list_of_infra");
        dataSet.put("samathuvapuram_id", samathuvapuram_id);
        Log.d("infra_list", "" + dataSet);
        return dataSet;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Utils.isOnline()){
            infra_list();
        }
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

            if ("infra_list".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insert_infra_list().execute(jsonObject);
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
//                    Utils.showAlert(this,"No Record Found!");
                    infraListScreenBinding.recycler.setVisibility(View.GONE);
                    infraListScreenBinding.notFoundTv.setVisibility(View.VISIBLE);
                }
                Log.d("Insert_infra_list", "" + jsonObject);
            }
            if ("delete".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                    onResume();
                }else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD") && jsonObject.getString("MESSAGE").equalsIgnoreCase("NO_RECORD")){
                    Utils.showAlert(this,jsonObject.getString("MESSAGE"));
                }
                Log.d("Delete_infra_list", "" + jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class Insert_infra_list extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                dbData.open();
                dbData.deleteINFRA_LISTTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                        ModelClass ListValue = new ModelClass();
                        try {
                               ListValue.setSamathuvapuram_id(jsonArray.getJSONObject(i).getInt("samathuvapuram_id"));
                               ListValue.setRepair_infra_estimate_id(jsonArray.getJSONObject(i).getInt("repair_infra_estimate_id"));
                               ListValue.setScheme_group_id(jsonArray.getJSONObject(i).getInt("scheme_group_id"));
                               ListValue.setScheme_id(jsonArray.getJSONObject(i).getInt("scheme_id"));
                               ListValue.setWork_group_id(jsonArray.getJSONObject(i).getInt("work_group_id"));
                               ListValue.setWork_type_id(jsonArray.getJSONObject(i).getInt("work_type_id"));
                               ListValue.setCondition_of_infra_id(jsonArray.getJSONObject(i).getInt("condition_of_infra_id"));
                               ListValue.setEstimate_cost_required(jsonArray.getJSONObject(i).getString("estimate_cost_required"));
                               ListValue.setCondition_of_infra(jsonArray.getJSONObject(i).getString("condition_of_infra"));
                               ListValue.setScheme_name(jsonArray.getJSONObject(i).getString("scheme_name"));
                               ListValue.setWork_name(jsonArray.getJSONObject(i).getString("work_type_name"));

                            dbData.Insert_infra_list(ListValue);
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
            new  fetch_infra_list().execute();
        }
    }
    


}
