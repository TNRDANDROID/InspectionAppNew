package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.ATRWorkListAdapter;
import com.nic.InspectionAppNew.adapter.WorkListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.AtrWorkListBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nic.InspectionAppNew.utils.Utils.showAlert;

public class ATRWorkList extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private AtrWorkListBinding binding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ArrayList<ModelClass> need_improvement_workList = new ArrayList<>();
    private ArrayList<ModelClass> unsatisfied_workList = new ArrayList<>();
    ATRWorkListAdapter workListAdapter;
    private SearchView searchView;
    String onOffType="";
    private String WorkType="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.atr_work_list);
        binding.setActivity(this);
        setSupportActionBar(binding.toolbar);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
       
        dbData.open();
        onOffType=prefManager.getOnOffType();
        binding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setNestedScrollingEnabled(false);
        binding.recycler.setFocusable(false);

        binding.recycler.setVisibility(View.GONE);
        binding.notFoundTv.setVisibility(View.GONE);

        WorkType="need_improvement";
        workList = new ArrayList<>();
        need_improvement_workList = new ArrayList<>();
        unsatisfied_workList = new ArrayList<>();
        if(onOffType.equals("online")) {
            if (Utils.isOnline()) {
                getWorkListOptional();

            } else {
                showAlert(ATRWorkList.this, "No Internet Connection!");
            }
        }else {
            new  fetchWorkList().execute();
        }
        binding.needImprovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recycler.setVisibility(View.GONE);
                binding.notFoundTv.setVisibility(View.GONE);
                binding.needImprovement.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                binding.needImprovement.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.left_button_color));
                binding.unsatisfied.setTextColor(getApplicationContext().getResources().getColor(R.color.grey_8));
                binding.unsatisfied.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.right_button));
                WorkType="need_improvement";
                System.out.println("need_improvement_workList >>"+need_improvement_workList.size());
                if(onOffType.equals("online")) {
//                    getWorkListOptional();
                    getNeedImprovementWorkList(need_improvement_workList);

                }else {
                    getOfflineWorkList();
                }


            }
        });
        binding.unsatisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.recycler.setVisibility(View.GONE);
                binding.notFoundTv.setVisibility(View.GONE);
                binding.needImprovement.setTextColor(getApplicationContext().getResources().getColor(R.color.grey_8));
                binding.needImprovement.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.left_button));
                binding.unsatisfied.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                binding.unsatisfied.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.right_button_color));
                WorkType="unsatisfied";
                System.out.println("unsatisfied_workList >>"+unsatisfied_workList.size());
                if(onOffType.equals("online")) {
//                    getWorkListOptional();
                    getUnSatisfiedWorkList(unsatisfied_workList);

                }else {
                   getOfflineWorkList();
                }

            }
        });



    }

    public void getOfflineWorkList() {
            if(WorkType.equalsIgnoreCase("need_improvement")){
                getNeedImprovementWorkList(need_improvement_workList);
            }else {
                getUnSatisfiedWorkList(unsatisfied_workList);
            }
    }
    public void getWorkListOptional() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListOptional", Api.Method.POST, UrlGenerator.getMainService(), workListOptionalJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workListOptionalJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workListOptional(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListOptional", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workListOptional(Activity activity) throws JSONException {
        JSONObject dataSet = new JSONObject();
        JSONObject dataSet1 = new JSONObject();
        JSONArray villageCodeJsonArray=new JSONArray();
        JSONArray schemeJsonArray=new JSONArray();
        JSONArray finyearJsonArray=new JSONArray();
        Bundle b = getIntent().getExtras();
            String response=b.getString("jsonObject");
            try {

                JSONObject obj = new JSONObject(response);
                villageCodeJsonArray=obj.getJSONArray("villageCodeJsonArray");
                schemeJsonArray=obj.getJSONArray("schemeJsonArray");
                finyearJsonArray=obj.getJSONArray("finyearJsonArray");
                Log.d("My App", obj.toString());

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
            }

        try{
            if (prefManager.getLevels().equalsIgnoreCase("S")){
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, getIntent().getStringExtra("SelectedDistrict"));
                dataSet.put(AppConstant.BLOCK_CODE,getIntent().getStringExtra("SelectedBlock") );
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
            else if (prefManager.getLevels().equalsIgnoreCase("D")) {
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
                dataSet.put(AppConstant.BLOCK_CODE, getIntent().getStringExtra("SelectedBlock"));
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
            else if (prefManager.getLevels().equalsIgnoreCase("B")) {
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
                dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("workListOptional", "" + dataSet1);
        return dataSet1;
    }
    
    public class fetchWorkList extends AsyncTask<Void, Void,ArrayList<ModelClass>> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(ATRWorkList.this, "Loading...", true, false, null);
//            Utils.showProgress(WorkList.this,progressHUD);
        }

        @Override
        protected ArrayList<ModelClass> doInBackground(Void... params) {
            dbData.open();
            workList = new ArrayList<>();
            need_improvement_workList = new ArrayList<>();
            unsatisfied_workList = new ArrayList<>();
            workList = dbData.getAllATRWorkList("offline");

            Log.d("Wlist_COUNT", String.valueOf(workList.size()));
            return workList;
        }

        @Override
        protected void onPostExecute(ArrayList<ModelClass> worklist) {
            super.onPostExecute(worklist);
//            Utils.hideProgress(progressHUD);

            if(!Utils.isOnline()) {
                if (worklist.size() == 0) {
                    Utils.showAlert(ATRWorkList.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if (worklist.size() > 0) {
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.recycler.setVisibility(View.VISIBLE);
                binding.notFoundTv.setVisibility(View.GONE);
                for(int i=0;i<worklist.size();i++){
                    if(worklist.get(i).getCurrent_stage_of_work().equalsIgnoreCase("11")){
                        need_improvement_workList.add(worklist.get(i));
                    }else {
                        unsatisfied_workList.add(worklist.get(i));
                    }
                }
                if(WorkType.equalsIgnoreCase("need_improvement")){
                    System.out.println("need_improvement_workList >>"+need_improvement_workList.size());
                    getNeedImprovementWorkList(need_improvement_workList);
                }else {
                    System.out.println("unsatisfied_workList >>"+unsatisfied_workList.size());
                    getUnSatisfiedWorkList(unsatisfied_workList);
                }

                /*workListAdapter = new WorkListAdapter(WorkList.this, worklist,dbData,"online");
                workListBinding.recycler.setAdapter(workListAdapter);*/

            }else {
                need_improvement_workList =new ArrayList<>();
                unsatisfied_workList =new ArrayList<>();
                binding.recycler.setVisibility(View.GONE);
                binding.notFoundTv.setVisibility(View.VISIBLE);
            }
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        }

    public class GetWorkListTask extends AsyncTask<JSONObject, Void, ArrayList<ModelClass>> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(WorkList.this,progressHUD);
            progressHUD = ProgressHUD.show(ATRWorkList.this, "Loading...", true, false, null);
        }

        @Override
        protected ArrayList<ModelClass> doInBackground(JSONObject... params) {
            workList = new ArrayList<>();
            need_improvement_workList = new ArrayList<>();
            unsatisfied_workList = new ArrayList<>();
            dbData.open();
            dbData.deleteWorkListTable();

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    if(jsonArray.length() >0){

                        for (int i = 0; i < jsonArray.length(); i++) {
                            String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                            String SelectedBlockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                            String hab_code = jsonArray.getJSONObject(i).getString("hab_code");
                            String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                            String schemeID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_ID);
                            String scheme_group_id = jsonArray.getJSONObject(i).getString("scheme_group_id");
                            String work_group_id = jsonArray.getJSONObject(i).getString("work_group_id");
                            String work_type_id = jsonArray.getJSONObject(i).getString("work_type_id");
                            String finYear = jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR);
                            int workID = jsonArray.getJSONObject(i).getInt(AppConstant.WORK_ID);
                            String workName = jsonArray.getJSONObject(i).getString(AppConstant.WORK_NAME);
                            String as_value = jsonArray.getJSONObject(i).getString("as_value");
                            String ts_value = jsonArray.getJSONObject(i).getString("ts_value");
                            String current_stage_of_work = jsonArray.getJSONObject(i).getString("current_stage_of_work");
                            String stage_name = jsonArray.getJSONObject(i).getString("stage_name");
                            String is_high_value = jsonArray.getJSONObject(i).getString("is_high_value");
                            String as_date = jsonArray.getJSONObject(i).getString("as_date");
                            String ts_date = jsonArray.getJSONObject(i).getString("ts_date");
                            String work_order_date = jsonArray.getJSONObject(i).getString("work_order_date");
                            String work_type_name = jsonArray.getJSONObject(i).getString("work_type_name");

                            ModelClass modelClass = new ModelClass();
                            modelClass.setDistrictCode(dcode);
                            modelClass.setBlockCode(SelectedBlockCode);
                            modelClass.setHabCode(hab_code);
                            modelClass.setPvCode(pvcode);
                            modelClass.setSchemeSequentialID(schemeID);
                            modelClass.setScheme_group_id(scheme_group_id);
                            modelClass.setWork_group_id(work_group_id);
                            modelClass.setWork_type_id(work_type_id);
                            modelClass.setFinancialYear(finYear);
                            modelClass.setWork_id(workID);
                            modelClass.setWork_name(workName);
                            modelClass.setAs_value(as_value);
                            modelClass.setTs_value(ts_value);
                            modelClass.setCurrent_stage_of_work(current_stage_of_work);
                            modelClass.setStage_name(stage_name);
                            modelClass.setIs_high_value(is_high_value);
                            modelClass.setAs_date(as_date);
                            modelClass.setTs_date(ts_date);
                            modelClass.setWork_order_date(work_order_date);
                            modelClass.setWork_type_name(work_type_name);
                            
                            if(onOffType.equals("offline")){
                                dbData.Insert_atr_workList("offline",modelClass);
                            }else {
                                workList.add(modelClass);
                            }
                            

                        }

                    } else {
                        Utils.showAlert(ATRWorkList.this, "No Record Found for Corresponding Financial Year");
                    }

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
                binding.recycler.setVisibility(View.VISIBLE);
                binding.notFoundTv.setVisibility(View.GONE);
                for(int i=0;i<worklist.size();i++){
                    if(worklist.get(i).getCurrent_stage_of_work().equalsIgnoreCase("11")){
                        need_improvement_workList.add(worklist.get(i));
                    }else {
                        unsatisfied_workList.add(worklist.get(i));
                    }
                }
                if(WorkType.equalsIgnoreCase("need_improvement")){
                    System.out.println("need_improvement_workList >>"+need_improvement_workList.size());
                    getNeedImprovementWorkList(need_improvement_workList);
                }else {
                    System.out.println("unsatisfied_workList >>"+unsatisfied_workList.size());
                    getUnSatisfiedWorkList(unsatisfied_workList);
                }

                /*workListAdapter = new WorkListAdapter(WorkList.this, worklist,dbData,"online");
                workListBinding.recycler.setAdapter(workListAdapter);*/

            }else {
                need_improvement_workList =new ArrayList<>();
                unsatisfied_workList =new ArrayList<>();
                binding.recycler.setVisibility(View.GONE);
                binding.notFoundTv.setVisibility(View.VISIBLE);
            }

            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new GetWorkListTask().execute(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + responseObj.toString());
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

// listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
// filter recycler view when query submitted
                workListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
// filter recycler view when text is changed
                workListAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    public void getNeedImprovementWorkList(ArrayList<ModelClass> worklist){
        if (need_improvement_workList.size() > 0) {
            binding.recycler.setVisibility(View.VISIBLE);
            binding.notFoundTv.setVisibility(View.GONE);
            workListAdapter = new ATRWorkListAdapter(ATRWorkList.this, need_improvement_workList,dbData,onOffType);
            binding.recycler.setAdapter(workListAdapter);
        }else {
            binding.recycler.setVisibility(View.GONE);
            binding.notFoundTv.setVisibility(View.VISIBLE);
        }

    }
    public void getUnSatisfiedWorkList(ArrayList<ModelClass> worklist){
        if (unsatisfied_workList.size() > 0) {
            binding.recycler.setVisibility(View.VISIBLE);
            binding.notFoundTv.setVisibility(View.GONE);
            workListAdapter = new ATRWorkListAdapter(ATRWorkList.this, unsatisfied_workList,dbData,onOffType);
            binding.recycler.setAdapter(workListAdapter);
        }else {
            binding.recycler.setVisibility(View.GONE);
            binding.notFoundTv.setVisibility(View.VISIBLE);
        }

    }


}
