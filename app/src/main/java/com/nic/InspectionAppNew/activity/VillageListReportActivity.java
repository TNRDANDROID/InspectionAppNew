package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.DashboardVillageListAdapter;
import com.nic.InspectionAppNew.adapter.DistrictBlockAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.VillageListReportBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VillageListReportActivity extends AppCompatActivity implements Api.ServerResponseListener {
    private VillageListReportBinding binding;
    private ShimmerRecyclerView recyclerView;
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public  DBHelper dbHelper;
    public com.nic.InspectionAppNew.dataBase.dbData dbData = new dbData(this);
    private ArrayList<ModelClass> villageList = new ArrayList<>();
    private DashboardVillageListAdapter adapter;
    String dcode="";
    String bcode="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.village_list_report);
        binding.setActivity(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefManager = new PrefManager(this);

        villageList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView = binding.recycler;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        dcode=getIntent().getStringExtra("dcode");
        bcode=getIntent().getStringExtra("bcode");
        fetData();
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.searchTv.getText().toString().isEmpty()){
                    adapter.getFilter().filter(binding.searchTv.getText().toString());
                }
            }
        });
        binding.searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!binding.searchTv.getText().toString().isEmpty()){
                    adapter.getFilter().filter(binding.searchTv.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void fetData() {
        if (Utils.isOnline()) {
            getVillageDashboardData();
        }else {
            showAlert(VillageListReportActivity.this,"No Internet Connection!");
        }
    }

    public void getVillageDashboardData() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), villageListDistrictBlockWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }
    public  JSONObject villageListDistrictBlockWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_BLOCK_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        dataSet.put(AppConstant.BLOCK_CODE, bcode);
        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }


    private void loadCards() {

        recyclerView.hideShimmerAdapter();

    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseObj.toString());
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(DownloadActivity.this,progressHUD);
//            progressHUD = ProgressHUD.show(VillageListReportActivity.this, "Loading...", true, false, null);
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            villageList.clear();
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
                            modelClass.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            modelClass.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            modelClass.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            modelClass.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));
                             /* String satisfied_count = jsonArray.getJSONObject(i).getString("satisfied");
                        String un_satisfied_count = jsonArray.getJSONObject(i).getString("unsatisfied");
                        String need_improvement_count = jsonArray.getJSONObject(i).getString("need_improvement");
                        if(satisfied_count.equals("")){
                            satisfied_count="0";
                        } if(un_satisfied_count.equals("")){
                            un_satisfied_count="0";
                        } if(need_improvement_count.equals("")){
                            need_improvement_count="0";
                        }*/
                            modelClass.setTotal_cout(6);
                            modelClass.setSatisfied_cout(1);
                            modelClass.setUnsatisfied_cout(2);
                            modelClass.setNeedimprovement_cout(3);
                            villageList.add(modelClass);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    Collections.sort(villageList, comparatorvillage);
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
/*
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
*/

            adapter = new DashboardVillageListAdapter(VillageListReportActivity.this, villageList);
            recyclerView.setAdapter(adapter);
            recyclerView.showShimmerAdapter();
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCards();
                }
            }, 1000);
        }
    }

    Comparator<ModelClass> comparatorvillage = new Comparator<ModelClass>() {
        @Override
        public int compare(ModelClass movie, ModelClass t1) {
            return movie.getPvName().compareTo(t1.getPvName());
        }
    };

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
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        setResult(Activity.RESULT_CANCELED);
        Intent intent = new Intent();
        intent.putExtra("flag", "B");
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

    }
    @Override
    public void OnError(VolleyError volleyError) {

    }

    @Override
    public void onResume() {
        super.onResume();
        fetData();
    }

}
