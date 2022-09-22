package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.WorkListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.WorkListBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.InspectionAppNew.dataBase.DBHelper.FINANCIAL_YEAR_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.SCHEME_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.VILLAGE_TABLE_NAME;

public class WorkList extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private WorkListBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<ModelClass> Village = new ArrayList<>();
    private List<ModelClass> Scheme = new ArrayList<>();
    private List<ModelClass> FinYear = new ArrayList<>();
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ProgressHUD progressHUD;
    WorkListAdapter workListAdapter;


    String pref_Village;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        workListBinding = DataBindingUtil.setContentView(this, R.layout.work_list);
        workListBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            isHome = bundle.getString("Home");
        }

        workListBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        workListBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        workListBinding.recycler.setHasFixedSize(true);
        workListBinding.recycler.setNestedScrollingEnabled(false);
        workListBinding.recycler.setFocusable(false);

        workListBinding.recycler.setVisibility(View.GONE);
        workListBinding.notFoundTv.setVisibility(View.VISIBLE);
        villageFilterSpinner();
        schemeFilterSpinner();
        finyearFilterSpinner();


       /* if(prefManager.getOnOffType().equals("online")){
            if (Utils.isOnline()) {
                getWorkListOptional();
            }else {
                //Utils.showAlert(this, getResources().getString(R.string.no_internet));
                AlertDialog.Builder ab = new AlertDialog.Builder(
                        WorkList.this);
                ab.setMessage(getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                ab.setPositiveButton(getResources().getString(R.string.settings),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent I = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(I);
                            }
                        });
                ab.setNegativeButton(getResources().getString(R.string.continue_with_offline),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                new fetchWorkList().execute();
                            }
                        });
                ab.show();
            }
        }else {
            new fetchWorkList().execute();
        }*/

        workListBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setPvCode(Village.get(position).getPvCode());
                    workListBinding.schemeSpinner.setSelection(0);

                }else {
                    prefManager.setPvCode("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.schemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    if(prefManager.getPvCode()!=null && !prefManager.getPvCode().equals("")){
                        if(prefManager.getFinancialyearName()!=null && !prefManager.getFinancialyearName().equals("")){
                            prefManager.setSchemeSeqId(Scheme.get(position).getSchemeSequentialID());
                            new fetchWorkList().execute();
                        }else {
                            Utils.showAlert(WorkList.this,"Please Select Financial Year");
                        }
                    }else {
                        Utils.showAlert(WorkList.this,"Please Select Village");
                    }


                }else {
                    prefManager.setSchemeSeqId("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.finYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setFinancialyearName(FinYear.get(position).getFinancialYear());
                    workListBinding.schemeSpinner.setSelection(0);

                }else {
                    prefManager.setFinancialyearName("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }
    public void schemeFilterSpinner() {
       /* Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.SCHEME_TABLE_NAME , null);*/
        String sql = null;
        JSONArray filter = prefManager.getSchemeSeqIdJson();
        JSONArray filter2 = prefManager.getFinYearJson();
        sql = "SELECT distinct scheme_seq_id,scheme_name FROM " + SCHEME_TABLE_NAME + " WHERE scheme_seq_id in" + filter.toString().replace("[", "(").replace("]", ")") +
                " and fin_year in" + filter2.toString().replace("[", "(").replace("]", ")") + " order by scheme_name";

        Log.d("Scheme",""+sql);

        Cursor cursor = getRawEvents(sql, null);

        Scheme.clear();
        ModelClass list = new ModelClass();
        list.setSchemeName("Select Scheme");
        Scheme.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int scheme_seq_id = cursor.getInt(cursor.getColumnIndexOrThrow("scheme_seq_id"));
                    String scheme_name = cursor.getString(cursor.getColumnIndexOrThrow("scheme_name"));

                    modelClass.setSchemeSequentialID(String.valueOf(scheme_seq_id));
                    modelClass.setSchemeName(scheme_name);

                    Scheme.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Schemespinnersize", "" + Scheme.size());

        }
        workListBinding.schemeSpinner.setAdapter(new CommonAdapter(this, Scheme, "Scheme"));
    }
    public void finyearFilterSpinner() {
        /*Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME , null);*/
        String sql = null;
        JSONArray filter = prefManager.getFinYearJson();
        sql = "SELECT * FROM " + FINANCIAL_YEAR_TABLE_NAME + " WHERE fin_year in" + filter.toString().replace("[", "(").replace("]", ")") + " order by fin_year";

        Log.d("fin_year",""+sql);

        Cursor cursor = getRawEvents(sql, null);

        FinYear.clear();
        ModelClass list = new ModelClass();
        list.setFinancialYear("Select Financial Year");
        FinYear.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    String fin_year = cursor.getString(cursor.getColumnIndexOrThrow("fin_year"));

                    modelClass.setFinancialYear(fin_year);

                    FinYear.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("FinYearspinnersize", "" + FinYear.size());

        }
        workListBinding.finYearSpinner.setAdapter(new CommonAdapter(this, FinYear, "FinYear"));
    }

    public void villageFilterSpinner() {

        String sql = null;
        JSONArray filter = prefManager.getVillagePvCodeJson();
        sql = "SELECT * FROM " + VILLAGE_TABLE_NAME + " WHERE dcode = "+prefManager.getDistrictCode()+" and bcode = "+prefManager.getBlockCode()+" and pvcode in" + filter.toString().replace("[", "(").replace("]", ")") + " order by pvname";

        Log.d("village",""+sql);

        Cursor cursor = getRawEvents(sql, null);

//        cursor = db.rawQuery("select * from "+ VILLAGE_TABLE_NAME+" where bcode = "+prefManager.getBlockCode()+" order by pvname asc",null);
        Village.clear();
        ModelClass list = new ModelClass();
        list.setPvName("Select Village");
        Village.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int pvcode = cursor.getInt(cursor.getColumnIndexOrThrow("pvcode"));
                    String pvname = cursor.getString(cursor.getColumnIndexOrThrow("pvname"));
                    modelClass.setPvCode(String.valueOf(pvcode));
                    modelClass.setPvName(pvname);

                    Village.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Villagespinnersize", "" + Village.size());

        }
        workListBinding.villageSpinner.setAdapter(new CommonAdapter(this, Village, "Village"));
    }


    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public class fetchWorkList extends AsyncTask<Void, Void,ArrayList<ModelClass>> {
        @Override
        protected ArrayList<ModelClass> doInBackground(Void... params) {
            dbData.open();
            workList = new ArrayList<>();
            workList = dbData.getAllWorkList("",prefManager.getDistrictCode(),prefManager.getBlockCode(),prefManager.getPvCode(),prefManager.getFinancialyearName(),prefManager.getSchemeSeqId());
            Log.d("Wlist_COUNT", String.valueOf(workList.size()));
            return workList;
        }

        @Override
        protected void onPostExecute(ArrayList<ModelClass> worklist) {
            super.onPostExecute(worklist);
            if(!Utils.isOnline()) {
                if (worklist.size() == 0) {
                    Utils.showAlert(WorkList.this, "No Data Available in Local Database. Please, Turn On mobile data");
                }
            }
            if (worklist.size() > 0) {
                workListBinding.recycler.setVisibility(View.VISIBLE);
                workListBinding.notFoundTv.setVisibility(View.GONE);
                workListAdapter = new WorkListAdapter(WorkList.this, worklist,dbData);
                workListBinding.recycler.setAdapter(workListAdapter);
            }else {
                workListBinding.recycler.setVisibility(View.GONE);
                workListBinding.notFoundTv.setVisibility(View.VISIBLE);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }





}
