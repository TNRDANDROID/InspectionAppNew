package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    int pageNumber;
    String WorkId="";
    String inspectionID="";
    String pdf_string_actual ="";
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    ProgressDialog progressBar;
    Dialog dialog;


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
            binding.download.setVisibility(View.GONE);
            if (Utils.isOnline()) {
                getWorkListOptional();

            } else {
                showAlert(ATRWorkList.this, "No Internet Connection!");
            }
        }else {
            binding.download.setVisibility(View.VISIBLE);
            new  fetchWorkList().execute();
        }
        binding.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline()) {
                    getWorkListOptional();

                } else {
                    showAlert(ATRWorkList.this, "No Internet Connection!");
                }
            }
        });
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

    private JSONObject workListOptionalJsonParams() throws JSONException {

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

        villageCodeJsonArray.put("3");
        schemeJsonArray.put("395");
        finyearJsonArray.put("2018-2019");
        try {

            dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
            dataSet.put(AppConstant.STATE_CODE, "29");
            dataSet.put(AppConstant.DISTRICT_CODE, "31");
            dataSet.put(AppConstant.BLOCK_CODE,"8");
            dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
            dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
            dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
            dataSet1.put("inspection_work_details", dataSet);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("workListOptionals", "" + dataSet1);
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
            if(onOffType.equals("offline")){
                showAlertdialog(ATRWorkList.this, "Your Data Downloaded Successfully!");
            }else {
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
            }


            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public  void showAlertdialog(Activity activity, String msg){
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
                    new  fetchWorkList().execute();
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
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getWorkReportDetails(String work_id, String inspection_id) {
        WorkId=work_id;
        inspectionID=inspection_id;
        try {
            new ApiService(this).makeJSONObjectRequest("WorkReport", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(work_id,inspection_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject workDetailsJsonParams(String work_id, String inspection_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workDetailsParams(this,work_id,inspection_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity,String work_id, String inspection_id) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_pdf");
        dataSet.put("work_id", work_id);
        dataSet.put("inspection_id", inspection_id);

        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
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
            if ("WorkReport".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1 = jsonObject.getJSONObject("JSON_DATA");
                    String pdf_string ="";
                    pdf_string = jsonObject1.getString("pdf_string");
                    pdf_string_actual=pdf_string;
                    if(checkPermissions()){
                        viewPdf1(pdf_string_actual);
                    }
                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
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

    private boolean checkPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE

        };
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(ATRWorkList.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_REQUEST_CODE_PERMISSION);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( "LOG_TAG","Permission granted");
//                    Toast.makeText(this.getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    viewPdf1(pdf_string_actual);

//                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i("LOG_TAG","Permission denied");
                    Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    public void viewPdf1(final String DocumentString) {
        dialog = new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pdf_view_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        final PDFView pdfView = (PDFView) dialog.findViewById(R.id.documentViewer);
        final TextView pageNum = (TextView) dialog.findViewById(R.id.pageNum);
        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final ImageView down_load_icon = (ImageView) dialog.findViewById(R.id.down_load_icon);

        pageNumber = 0;
        if (DocumentString != null && !DocumentString.equals("")) {
            byte[] decodedString = new byte[0];
            try {
                //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                decodedString = Base64.decode(DocumentString/*traders.get(position).getDocument().toString()*/, Base64.DEFAULT);
                System.out.println(new String(decodedString));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pdfView.fromBytes(decodedString).
                    onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
//                            setTitle(String.format("%s %s / %s", "PDF", page + 1, pageCount));
                            pageNum.setText(pageNumber + 1 + "/" + pageCount);
                        }
                    }).defaultPage(pageNumber).swipeHorizontal(true).enableDoubletap(true).load();

        }else {
            Utils.showAlert(this,"No Record Found!");
        }
        down_load_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDownloadMethod(DocumentString,"");
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void callDownloadMethod(String document,String type){
        new downloadPDFTask().execute(document);
    }

    @SuppressLint("StaticFieldLeak")
    public class downloadPDFTask extends AsyncTask<String,String,String> {

        String path;
        String DocumentString;
        File dwldsPath;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(String... strings) {
            DocumentString=strings[0];
            String  success="";
            String title="Inspection";
            String work_id =WorkId;
            dwldsPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+title+inspectionID+"_"+work_id + ".pdf");
            path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+title+inspectionID+"_"+work_id + ".pdf";
            if (DocumentString != null && !DocumentString.equals("")) {
                byte[] decodedString = new byte[0];
                try {
                    //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                    decodedString = Base64.decode(DocumentString/*traders.get(position).getDocument().toString()*/, Base64.DEFAULT);
                    //System.out.println(new String(decodedString));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    success="Failure";
                }


                FileOutputStream os = null;

                try {
                    os = new FileOutputStream(dwldsPath, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    success="Failure";
                }
                try {
                    os.write(decodedString);
                    os.flush();
                    os.close();
                    success="Success";
                    //System.out.println("Created");


                } catch (IOException e) {
                    success="Failure";
                    e.printStackTrace();

                }

            }
            return success;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgress();
            if(s.equals("Success")){
                addNotification(dwldsPath);
                //Utils.showAlert(ViewSavedWorkList.this,"Download Successfully File Path:"+path);


            }
            else {
                Utils.showAlert(ATRWorkList.this,"Download Fail");
            }

        }
    }

    private void addNotification(File filePath) {
        //Log.d("filePath >> ", "" + filePath);
        Uri uriPdfPath = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", filePath);
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenIntent.setClipData(ClipData.newRawUri("", uriPdfPath));
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf");
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, pdfOpenIntent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(null);
        bigText.setSummaryText("");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_message_black_24dp);
        mBuilder.setContentTitle("Inspection Report Downloaded Successfully!");
        mBuilder.setContentText("File Path : "+filePath);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    /*
    private void addNotification(String path) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp) //set icon for notification
                        .setContentTitle("PDF Path") //set title of notification
                        .setContentText(path)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, SaveWorkDetailsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("PDF Path", path);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }
*/
    void showProgress(){
        // creating progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("File downloading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(100);
        progressBar.setMax(100);
        progressBar.show();
        //reset progress bar and filesize status

    }
    void hideProgress(){
        progressBar.hide();
    }

}
