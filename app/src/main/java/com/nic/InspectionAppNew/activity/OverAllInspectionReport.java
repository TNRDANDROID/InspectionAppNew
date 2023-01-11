package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.nic.InspectionAppNew.Interface.DateInterface;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.DistrictBlockAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.OverAllInspectionReportBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class OverAllInspectionReport extends AppCompatActivity implements Api.ServerResponseListener, DateInterface {
    private OverAllInspectionReportBinding binding;
    private ShimmerRecyclerView recyclerView;
    private ShimmerRecyclerView recyclerView_block;
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public  DBHelper dbHelper;
    public com.nic.InspectionAppNew.dataBase.dbData dbData = new dbData(this);
    private ArrayList<ModelClass> districtList = new ArrayList<>();
    private ArrayList<ModelClass> districtListDashboardData = new ArrayList<>();
    private ArrayList<ModelClass> blockListDashboardData = new ArrayList<>();
    private ArrayList<ModelClass> blockList = new ArrayList<>();
    private DistrictBlockAdapter districtAdapter;
    private DistrictBlockAdapter blockAdapter;
    String level="";
    String fromDate="";
    String toDate="";
    private ArrayList<ModelClass> workList = new ArrayList<>();

    String total="";
    String flag="";
    String dname="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.over_all_inspection_report);
        binding.setActivity(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefManager = new PrefManager(this);
        level=prefManager.getLevels();
        districtList = new ArrayList<>();
        districtAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, districtList,"D");
        blockList = new ArrayList<>();
        blockAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, blockList,"B");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView = binding.recycler;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_block = binding.blockRecycler;
        recyclerView_block.setLayoutManager(layoutManager);
        recyclerView_block.setItemAnimator(new DefaultItemAnimator());

        fetData();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getDistrictsReport(workList);

            }
        });
        binding.blockHeadtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.back.getVisibility() == View.VISIBLE){
                binding.districtLayout.setVisibility(View.VISIBLE);
                binding.blockLayout.setVisibility(View.GONE);
                recyclerView.showShimmerAdapter();
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadCards();
                    }
                }, 1000);

            }
            }
        });
        binding.dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline()) {
                    showDatePickerDialog();

                } else {
                    showAlert(OverAllInspectionReport.this, "No Internet Connection!");
                }
            }
        });

    }
    public void showDatePickerDialog(){
        Utils.showDatePickerDialog(this);

    }
    @Override
    public void getDate(String date) {
        String[] separated = date.split(":");
        fromDate = separated[0]; // this will contain "Fruit"
        toDate = separated[1];
      /*  binding.date.setText(fromDate+" to "+toDate);
        binding.dateSelected.setText(fromDate+" to "+toDate);*/

        if(Utils.isOnline()){
            getWorkDetails();
        }
        else {
            Utils.showAlert(OverAllInspectionReport.this,"No Internet");
        }

    }
    private void fetData() {
        if (Utils.isOnline()) {

            if(level.equals("S")){
                getDistrictList();
                binding.districtLayout.setVisibility(View.VISIBLE);
                binding.blockLayout.setVisibility(View.GONE);
            }else if(level.equals("D")){
                getBlockList(prefManager.getDistrictCode(),prefManager.getDistrictName(),"");
                binding.back.setVisibility(View.GONE);
                binding.districtLayout.setVisibility(View.GONE);
                binding.blockLayout.setVisibility(View.VISIBLE);
            }

            Date startDate = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            toDate = df.format(startDate);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);
            c.add(Calendar.DATE, -60);
            Date expDate = c.getTime();
            fromDate= df.format(expDate);

            if(Utils.isOnline()){
                getWorkDetails();
            }
            else {
                Utils.showAlert(OverAllInspectionReport.this,"No Internet Connection!");
            }


        }else {
            showAlert(OverAllInspectionReport.this,"No Internet Connection!");
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

    public void getDistrictList() {
        if(Utils.isOnline()){
            try {
                new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getServicesListUrl(), districtListJsonParams(), "not cache", this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            Utils.showAlert(OverAllInspectionReport.this,"No Internet");
        }
    }
    public JSONObject districtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), districtListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("districtList", "" + dataSet);
        return dataSet;
    }
    public  JSONObject districtListJsonParams(Activity activity) throws JSONException {
        JSONObject dataSet = new JSONObject();
        prefManager = new PrefManager(activity);
        dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_DISTRICT_LIST_ALL);

        Log.d("districtList", "" + dataSet);
        return dataSet;
    }

    public void getBlockList(String dcode,String dname_tv, String flag_tv) {
        flag=flag_tv;
        dname=dname_tv;
        if(Utils.isOnline()){
            try {
                new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(dcode), "not cache", this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Utils.showAlert(OverAllInspectionReport.this,"No Internet");
        }
    }
    public JSONObject blockListJsonParams(String dcode) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), blockListDistrictWiseJsonParams(this,dcode).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("blockListDistrictWise", "" + dataSet);
        return dataSet;
    }
    public  JSONObject blockListDistrictWiseJsonParams(Activity activity,String dcode) throws JSONException {
        JSONObject dataSet = new JSONObject();
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BLOCK_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
            dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        Log.d("blockListDistrictWise", "" + dataSet);
        return dataSet;
    }

    private void loadCards() {
        recyclerView.hideShimmerAdapter();
    }
    private void loadCardsBlock() {
        recyclerView_block.hideShimmerAdapter();
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("DistrictList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                Log.d("DistrictList", "" + prefManager.getUserPassKey());
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertDistrictTask().execute(jsonObject);
                }
                Log.d("DistrictList", "" + responseObj.toString());
                Log.d("DistrictList", "" + responseDecryptedBlockKey);
            }
            if ("BlockList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertBlockTask().execute(jsonObject);
                }
                Log.d("BlockList", "" + responseObj.toString());
                Log.d("BlockList", "" + responseDecryptedBlockKey);
            }
            if ("WorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new GetWorkListTask().execute(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
                   /* if(onOffType.equals("online")){
                        recyclerView.setVisibility(View.GONE);
                        binding.notFoundTv.setVisibility(View.VISIBLE);
                        binding.graphLayout.setVisibility(View.GONE);
                        binding.tabLayout.setVisibility(View.GONE);
                    }else {
                    }
*/
                }
                Log.d("responseWorkList", "" + responseObj.toString());
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public class GetWorkListTask extends AsyncTask<JSONObject, Void, ArrayList<ModelClass>> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(WorkList.this,progressHUD);
            progressHUD = ProgressHUD.show(OverAllInspectionReport.this, "Loading...", true, false, null);
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
                            String action_taken_id = jsonArray.getJSONObject(i).getString("action_taken_id");
                            String action_status = jsonArray.getJSONObject(i).getString("action_status");
                            String reported_by = jsonArray.getJSONObject(i).getString("reported_by");


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
                            modelClass.setAction_taken_id(action_taken_id);
                            modelClass.setAction_status(action_status);
                            modelClass.setReported_by(reported_by);


                            workList.add(modelClass);

                        }

                    } else {
                        Utils.showAlert(OverAllInspectionReport.this, "No Record Found for Corresponding Financial Year");
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
//                                total=String.valueOf(total_inspection_count);
//                                showpieChart(Integer.parseInt(satisfied_count),Integer.parseInt(un_satisfied_count),Integer.parseInt(need_improvement_count),total_inspection_count);
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
                    if(prefManager.getLevels().equals("S")){
                        getDistrictsReport(worklist);
                    }else if(prefManager.getLevels().equals("D")){
                        getBlocksReport(worklist);
                    }else if(prefManager.getLevels().equals("B")){

                    }

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

    private void getDistrictsReport(ArrayList<ModelClass> worklist) {
        districtListDashboardData.clear();
        int s=0;
        int us=0;
        int nm=0;
        for(int i=0;i<districtList.size();i++){
            for(int j=0;j<worklist.size();j++){
                if(worklist.get(j).getDistrictCode().equals(districtList.get(i).getDistrictCode())){
                    if(worklist.get(j).getWork_status_id()==1){
                        s=s+1;
                    }else if(worklist.get(j).getWork_status_id()==2){
                        us=us+1;
                    }else if(worklist.get(j).getWork_status_id()==3){
                        nm=nm+1;
                    }
                }

            }
            int totalCount=s+us+nm;
            ModelClass districtListValues = new ModelClass();
            districtListValues.setDistrictCode(districtList.get(i).getDistrictCode());
            districtListValues.setDistrictName(districtList.get(i).getDistrictName());
            districtListValues.setTotal_cout(totalCount);
            districtListValues.setSatisfied_count(s);
            districtListValues.setUnsatisfied_count(us);
            districtListValues.setNeedimprovement_count(nm);
            if(totalCount>0){
                districtListDashboardData.add(districtListValues);
            }

            s=0;
            us=0;
            nm=0;
        }

        binding.blockLayout.setVisibility(View.GONE);
        binding.districtLayout.setVisibility(View.VISIBLE);
        districtAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, districtListDashboardData,"D");
        recyclerView.setAdapter(districtAdapter);
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
        for(int i=0;i<districtListDashboardData.size();i++){
            if(districtListDashboardData.get(i).getSatisfied_count()>0){
                sat=sat+districtListDashboardData.get(i).getSatisfied_count();
            }
            if(districtListDashboardData.get(i).getUnsatisfied_count()>0){
                usat=usat+districtListDashboardData.get(i).getUnsatisfied_count();
            }
            if(districtListDashboardData.get(i).getNeedimprovement_count()>0){
                nimp=nimp+districtListDashboardData.get(i).getNeedimprovement_count();
            }
        }

        tot=sat+usat+nimp;
        total=String.valueOf(tot);
        binding.totalCountGraph.setText(total);
        binding.headerTv.setText("State - ");
        binding.headerTxt.setText("Tamil Nadu");
        binding.totalTv.setText("Total Inspected Works - ");
        showpieChart(sat,usat,nimp,tot);
    }
    public void getBlocksReport(ArrayList<ModelClass> worklist) {
        blockListDashboardData.clear();
        int s=0;
        int us=0;
        int nm=0;
        for(int i=0;i<blockList.size();i++){
            for(int j=0;j<worklist.size();j++){
                if(worklist.get(j).getDistrictCode().equals(blockList.get(i).getDistrictCode()) && worklist.get(j).getBlockCode().equals(blockList.get(i).getBlockCode())){
                    if(worklist.get(j).getWork_status_id()==1){
                        s=s+1;
                    }else if(worklist.get(j).getWork_status_id()==2){
                        us=us+1;
                    }else if(worklist.get(j).getWork_status_id()==3){
                        nm=nm+1;
                    }
                }

            }
            int totalCount=s+us+nm;
            ModelClass modelClass = new ModelClass();
            modelClass.setDistrictCode(blockList.get(i).getDistrictCode());
            modelClass.setBlockCode(blockList.get(i).getBlockCode());
            modelClass.setBlockName(blockList.get(i).getBlockName());
            modelClass.setTotal_cout(totalCount);
            modelClass.setSatisfied_count(s);
            modelClass.setUnsatisfied_count(us);
            modelClass.setNeedimprovement_count(nm);
            if(totalCount>0){
                blockListDashboardData.add(modelClass);
            }
            s=0;
            us=0;
            nm=0;
        }
        if(level.equals("S")){
            binding.back.setVisibility(View.VISIBLE);
        }else {
            binding.back.setVisibility(View.GONE);
        }
        binding.blockLayout.setVisibility(View.VISIBLE);
        binding.districtLayout.setVisibility(View.GONE);
        blockAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, blockListDashboardData,"B");
        recyclerView_block.setAdapter(blockAdapter);
        recyclerView_block.showShimmerAdapter();
        recyclerView_block.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCardsBlock();
            }
        }, 1000);

        int tot=0;
        int sat=0;
        int usat=0;
        int nimp=0;
        for(int i=0;i<blockListDashboardData.size();i++){
            if(blockListDashboardData.get(i).getSatisfied_count()>0){
                sat=sat+blockListDashboardData.get(i).getSatisfied_count();
            }
            if(blockListDashboardData.get(i).getUnsatisfied_count()>0){
                usat=usat+blockListDashboardData.get(i).getUnsatisfied_count();
            }
            if(blockListDashboardData.get(i).getNeedimprovement_count()>0){
                nimp=nimp+blockListDashboardData.get(i).getNeedimprovement_count();
            }
        }

        tot=sat+usat+nimp;
        total=String.valueOf(tot);
        binding.totalCountGraph.setText(total);
        binding.headerTv.setText("District - ");
        binding.headerTxt.setText(dname);
        binding.totalTv.setText("Total Inspected Works - ");
        showpieChart(sat,usat,nimp,tot);


    }

    public class InsertDistrictTask extends AsyncTask<JSONObject ,Void ,Void> {

        private  ProgressHUD progressHUD;
        @Override
        protected Void doInBackground(JSONObject... params) {
            districtList.clear();
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String districtName = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME);
                        ModelClass districtListValues = new ModelClass();
                        districtListValues.setDistrictCode(districtCode);
                        districtListValues.setDistrictName(districtName);
                        districtList.add(districtListValues);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(districtList, comparatordistrict);

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* if(!((Activity) OverAllInspectionReport.this).isFinishing())
            {
                progressHUD = ProgressHUD.show(OverAllInspectionReport.this, "Loading...", true, false, null);
            }*/
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
           /* binding.blockLayout.setVisibility(View.GONE);
            binding.districtLayout.setVisibility(View.VISIBLE);
            districtAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, districtList,"D");
            recyclerView.setAdapter(districtAdapter);
            recyclerView.showShimmerAdapter();
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCards();
                }
            }, 1000);*/
           /* try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }

    public class InsertBlockTask extends AsyncTask<JSONObject ,Void ,Void> {

        private  ProgressHUD progressHUD;
        @Override
        protected Void doInBackground(JSONObject... params) {
            blockList.clear();
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                        String blockName = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME);
                        ModelClass modelClass = new ModelClass();
                        modelClass.setDistrictCode(districtCode);
                        modelClass.setBlockCode(blockCode);
                        modelClass.setBlockName(blockName);
                        blockList.add(modelClass);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Collections.sort(blockList, comparatorblock);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* if(!((Activity) OverAllInspectionReport.this).isFinishing())
            {
                progressHUD = ProgressHUD.show(OverAllInspectionReport.this, "Loading...", true, false, null);
            }*/
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
          /*  if(level.equals("S")){
                binding.back.setVisibility(View.VISIBLE);
            }else {
                binding.back.setVisibility(View.GONE);
            }
            binding.blockLayout.setVisibility(View.VISIBLE);
            binding.districtLayout.setVisibility(View.GONE);
            blockAdapter = new DistrictBlockAdapter(OverAllInspectionReport.this, blockList,"B");
            recyclerView_block.setAdapter(blockAdapter);
            recyclerView_block.showShimmerAdapter();
            recyclerView_block.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadCardsBlock();
                }
            }, 1000);*/
           /* try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            if(flag.equals("D")){
                getBlocksReport(workList);
            }
        }
    }

    private void showpieChart(int satisfied,int unsatisfied,int need_improvement,int total_inspection_count){

        // Pie Chart Event

        ArrayList<PieEntry> Count = new ArrayList<>();
        ArrayList<Integer> Colors = new ArrayList<>();

        //Set Diffrent Colorss For the Values
        int need_improvement_color = 0xFF1E90FF;
        int unsatisfied_color = 0xFFFFA500;
        int satisfied_color = 0xFF00FA9A;

        PieDataSet pieDataSet = new PieDataSet( Count, "");

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

        if(satisfied!=0)
        {
            Count.add(new PieEntry(satisfied,"Satisfied",1));
            Colors.add(satisfied_color);
        }
        if(unsatisfied!=0)
        {
            Count.add(new PieEntry(unsatisfied,"UnSatisfied" ,2));
            Colors.add(unsatisfied_color);

        }
        if(need_improvement!=0)
        {
            Count.add(new PieEntry(need_improvement,"Need Improvement", 3));
            Colors.add(need_improvement_color);

        }

        pieDataSet.setColors(Colors);
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
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    @Override
    public void OnError(VolleyError volleyError) {

    }

    @Override
    public void onResume() {
        super.onResume();
       /* level=prefManager.getLevels();
       fetData();*/
    }
    public void getVillageListReport(String districtCode, String blockCode, String bname) {
        Intent intent = new Intent(this, VillageListReportActivity.class);
        intent.putExtra("dcode",districtCode);
        intent.putExtra("bcode",blockCode);
        intent.putExtra("bname",bname);
        intent.putExtra("fromDate",fromDate);
        intent.putExtra("toDate",toDate);
        intent.putExtra("flag","");
        startActivityForResult(intent,1);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String flag = data.getStringExtra("flag");
                if(flag.equals("B")){
                    binding.blockLayout.setVisibility(View.VISIBLE);
                    binding.districtLayout.setVisibility(View.GONE);
                }
            }
        }
    }
    Comparator<ModelClass> comparatordistrict = new Comparator<ModelClass>() {
        @Override
        public int compare(ModelClass movie, ModelClass t1) {
            return movie.getDistrictName().compareTo(t1.getDistrictName());
        }
    };
    Comparator<ModelClass> comparatorblock = new Comparator<ModelClass>() {
        @Override
        public int compare(ModelClass movie, ModelClass t1) {
            return movie.getBlockName().compareTo(t1.getBlockName());
        }
    };

}
