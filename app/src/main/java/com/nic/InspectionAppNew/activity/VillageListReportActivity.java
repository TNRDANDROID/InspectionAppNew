package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.DashboardVillageListAdapter;
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
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ArrayList<ModelClass> villageListDashboardData = new ArrayList<>();
    private DashboardVillageListAdapter adapter;
    String dcode="";
    String bcode="";
    String fromDate="";
    String toDate="";
    String total="";
    String flag="";
    String bname="";

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
        fromDate=getIntent().getStringExtra("fromDate");
        toDate=getIntent().getStringExtra("toDate");
        flag=getIntent().getStringExtra("flag");
        bname=getIntent().getStringExtra("bname");

        binding.date.setText(fromDate+" to "+toDate);
        binding.totalTv.setText("Total Inspections of "+bname+" - ");
        if(flag.equals("B")){
            binding.dateLayout.setVisibility(View.VISIBLE);
            binding.graphDetails.setVisibility(View.VISIBLE);
        }else {
            binding.dateLayout.setVisibility(View.GONE);
            binding.graphDetails.setVisibility(View.GONE);
        }
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
            getVillageList();
        }else {
            showAlert(VillageListReportActivity.this,"No Internet Connection!");
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
        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "overall_inspection_details_for_atr");
        dataSet.put("from_date", fromDate);
        dataSet.put("to_date", toDate);
        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }

    public void getVillageList() {
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

            if ("WorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new GetWorkListTask().execute(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
                }
                Log.d("responseWorkList", "" + responseObj.toString());
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void gotoATR(int status, String districtCode, String blockCode, String pvCode, String pvname) {
        ArrayList<ModelClass> selectedList = new ArrayList<>();
        if(workList.size()>0){
            for (int i = 0; i < workList.size(); i++) {
                if(workList.get(i).getDistrictCode().equals(districtCode) && workList.get(i).getBlockCode().equals(blockCode)
                        && workList.get(i).getPvCode().equals(pvCode) && workList.get(i).getWork_status_id()==status){
                    selectedList.add(workList.get(i));
                }
            }
            Intent intent = new Intent(this, ATRWorkList.class);
            intent.putExtra("flag","V");
            intent.putExtra("list",selectedList);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    public class GetWorkListTask extends AsyncTask<JSONObject, Void, ArrayList<ModelClass>> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(WorkList.this,progressHUD);
            progressHUD = ProgressHUD.show(VillageListReportActivity.this, "Loading...", true, false, null);
        }

        @Override
        protected ArrayList<ModelClass> doInBackground(JSONObject... params) {
            workList = new ArrayList<>();
            if (params.length > 0) {
                JSONObject jsonObject=new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONArray status_wise_count = new JSONArray();
                try {
                    jsonObject=params[0].getJSONObject(AppConstant.JSON_DATA);
                    jsonArray = jsonObject.getJSONArray("inspection_details");
                    status_wise_count = jsonObject.getJSONArray( "status_wise_count");

                    if(jsonArray.length() >0){

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
                            String inspection_by_officer = jsonArray.getJSONObject(i).getString("name");
                            String work_type_name = jsonArray.getJSONObject(i).getString("work_type_name");
                            String inspection_by_officer_designation = jsonArray.getJSONObject(i).getString("desig_name");
                            String dname = jsonArray.getJSONObject(i).getString("dname");
                            String bname = jsonArray.getJSONObject(i).getString("bname");
                            String pvname = jsonArray.getJSONObject(i).getString("pvname");


                            ModelClass modelClass = new ModelClass();
                            modelClass.setDistrictCode(dcode);
                            modelClass.setBlockCode(bcode);
                            modelClass.setPvCode(pvcode);
                            modelClass.setInspection_id(inspection_id);
                            modelClass.setInspectedDate(inspection_date);
                            modelClass.setWork_status_id(Integer.parseInt(status_id));
                            modelClass.setWork_status(status);
                            modelClass.setDescription(description);
                            modelClass.setWork_name(work_name);
                            modelClass.setWork_id(Integer.parseInt(work_id));
                            modelClass.setInspection_by_officer(inspection_by_officer);
                            modelClass.setWork_type_name(work_type_name);
                            modelClass.setInspection_by_officer_designation(inspection_by_officer_designation);
                            modelClass.setDistrictName(dname);
                            modelClass.setBlockName(bname);
                            modelClass.setPvName(pvname);


                            workList.add(modelClass);

                        }

                    } else {
                        Utils.showAlert(VillageListReportActivity.this, "No Record Found for Corresponding Financial Year");
                    }

                   /* if(status_wise_count.length()>0){

                        for(int j=0;j<status_wise_count.length();j++){
                            try {
                                String satisfied_count = status_wise_count.getJSONObject(j).getString("satisfied");
                                String un_satisfied_count = status_wise_count.getJSONObject(j).getString("unsatisfied");
                                String need_improvement_count = status_wise_count.getJSONObject(j).getString("need_improvement");

                                if(satisfied_count.equals("")){
                                    satisfied_count="0";
                                } if(un_satisfied_count.equals("")){
                                    un_satisfied_count="0";
                                } if(need_improvement_count.equals("")){
                                    need_improvement_count="0";
                                }
                                int total_inspection_count = Integer.parseInt(satisfied_count)+Integer.parseInt(un_satisfied_count)+Integer.parseInt(need_improvement_count);
                                 } catch (JSONException e){

                            }

                        }
                    }
                    else {

                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("Wlist_COUNT", String.valueOf(workList.size()));
            return workList;
        }

        @Override
        protected void onPostExecute(ArrayList<ModelClass> worklist) {
            super.onPostExecute(worklist);
//            Utils.hideProgress(progressHUD);
            if (worklist.size() > 0) {
                binding.date.setText(fromDate+" to "+toDate);
                getVillageReport(worklist);

            }else {
            }



            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void getVillageReport(ArrayList<ModelClass> worklist) {
        villageListDashboardData.clear();
        int s=0;
        int us=0;
        int nm=0;
        for(int i=0;i<villageList.size();i++){
            for(int j=0;j<worklist.size();j++){
                if(worklist.get(j).getDistrictCode().equals(villageList.get(i).getDistrictCode())
                        && worklist.get(j).getBlockCode().equals(villageList.get(i).getBlockCode())
                        && worklist.get(j).getPvCode().equals(villageList.get(i).getPvCode())){
                    if(worklist.get(j).getWork_status_id()==1){
                        s=s+1;
                    }else if(worklist.get(j).getWork_status_id()==2){
                        us=us+1;
                    }else if(worklist.get(j).getWork_status_id()==3){
                        nm=nm+1;
                    }
                }

            }
            ModelClass modelClass = new ModelClass();
            modelClass.setDistrictCode(villageList.get(i).getDistrictCode());
            modelClass.setBlockCode(villageList.get(i).getBlockCode());
            modelClass.setBlockName(villageList.get(i).getBlockName());
            modelClass.setPvCode(villageList.get(i).getPvCode());
            modelClass.setPvName(villageList.get(i).getPvName());
            modelClass.setTotal_cout(s+us+nm);
            modelClass.setSatisfied_count(s);
            modelClass.setUnsatisfied_count(us);
            modelClass.setNeedimprovement_count(nm);
            villageListDashboardData.add(modelClass);
            s=0;
            us=0;
            nm=0;
        }
        adapter = new DashboardVillageListAdapter(VillageListReportActivity.this, villageListDashboardData);
        recyclerView.setAdapter(adapter);
        recyclerView.showShimmerAdapter();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCards();
            }
        }, 1000);

        int tot=0;
        int sat=0;
        int usat=0;
        int nimp=0;
        for(int i=0;i<villageListDashboardData.size();i++){
            if(villageListDashboardData.get(i).getSatisfied_count()>0){
                sat=sat+villageListDashboardData.get(i).getSatisfied_count();
            }
            if(villageListDashboardData.get(i).getUnsatisfied_count()>0){
                usat=usat+villageListDashboardData.get(i).getUnsatisfied_count();
            }
            if(villageListDashboardData.get(i).getNeedimprovement_count()>0){
                nimp=nimp+villageListDashboardData.get(i).getNeedimprovement_count();
            }
        }

        tot=sat+usat+nimp;
        total=String.valueOf(tot);
        binding.totalCountGraph.setText(total);
        showpieChart(sat,usat,nimp,tot);


    }

    private void showpieChart(int satisfied,int unsatisfied,int need_improvement,int total_inspection_count){

        ArrayList<PieEntry> Count = new ArrayList<>();
        //Add the Values in the Array list
        Count.add(new PieEntry(satisfied,"Satisfied"));
        Count.add(new PieEntry(unsatisfied,"UnSatisfied"));
        Count.add(new PieEntry(need_improvement,"Need Improvement"));

        PieDataSet pieDataSet = new PieDataSet( Count, "");

        //Set Diffrent Colorss For the Values
        int c = 0xFF1E90FF;
        int b = 0xFFFFA500;
        int a = 0xFF00FA9A;
        pieDataSet.setColors(a,b,c);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setDrawIcons(false);

        //value format here, here is the overridden method
        ValueFormatter vf = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        pieDataSet.setValueFormatter(vf);

        // LEGEND SETTINGS
        Legend l = binding.pieChart.getLegend();
        l.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
        l.setTextSize(13f);
        l.setTextColor(Color.BLACK);
        l.setFormToTextSpace(5f); // LegForm to LegText
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        l.setEnabled(false);

        //Setup pisDataset into binding.pieChart
        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        //To hide Labels
        binding.pieChart.setDrawSliceText(false);
        // Postioning CENTER TExt
//        binding.pieChart.setCenterTextOffset(0, -20);
        binding.pieChart.setCenterText(String.valueOf(total_inspection_count));
        binding.pieChart.setCenterTextSize(15f);
        binding.pieChart.setCenterTextSizePixels(35);
        binding.pieChart.animate();
        binding.pieChart.setTouchEnabled(true);
        binding.pieChart.invalidate();

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
            if (Utils.isOnline()) {
                getWorkDetails();
            }else {
                showAlert(VillageListReportActivity.this,"No Internet Connection!");
            }

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
