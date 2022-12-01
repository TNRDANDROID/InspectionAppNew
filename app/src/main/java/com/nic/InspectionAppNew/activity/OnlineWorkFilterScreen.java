package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.adapter.SchemeAdapter;
import com.nic.InspectionAppNew.adapter.VillageListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.OnlineWorkFilterScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.MyLocationListener;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static com.nic.InspectionAppNew.dataBase.DBHelper.SCHEME_TABLE_NAME;

public class OnlineWorkFilterScreen extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private OnlineWorkFilterScreenBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private List<ModelClass> District = new ArrayList<>();
    private List<ModelClass> Block = new ArrayList<>();
    private List<ModelClass> Village = new ArrayList<>();
    private List<ModelClass> Scheme = new ArrayList<>();
    private List<ModelClass> FinYear = new ArrayList<>();
    boolean workListInsert = false;

    String SelectedDistrict = "",SelectedBlock ="",SelectedVillage ="",SelectedFinYear ="",SelectedScheme ="";
    JSONArray districtCodeJsonArray = new JSONArray();
    JSONArray villageCodeJsonArray = new JSONArray();
    JSONArray schemeJsonArray = new JSONArray();
    JSONArray finyearJsonArray = new JSONArray();

    VillageListAdapter adapter;
    private List<ModelClass> FinYearList = new ArrayList<>();
    String[] finyearStrings;
    boolean[] FinYearcheckedItems;
    final ArrayList<Integer> mFinYearItems = new ArrayList<>();
    ArrayList<JSONArray> myVillageCodelist;
    String type="";
    Dialog dialog;
    SchemeAdapter schemeAdapter;
    String other_work_category_id="";
    String other_work_category_name="";
    private List<ModelClass> other_category_list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        workListBinding = DataBindingUtil.setContentView(this, R.layout.online_work_filter_screen);
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
        type= getIntent().getStringExtra("type");

        workListBinding.villageSelectionLayout.setVisibility(View.GONE);
        workListBinding.filterLayout.setVisibility(View.GONE);
        workListBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        workListBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        workListBinding.recycler.setHasFixedSize(true);
        workListBinding.recycler.setNestedScrollingEnabled(false);
        workListBinding.recycler.setFocusable(false);

        workListBinding.recycler.setVisibility(View.GONE);
        workListBinding.notFoundTv.setVisibility(View.GONE);

        if(prefManager.getWorkType().equalsIgnoreCase("rdpr")){
            workListBinding.schemeLayout.setVisibility(View.VISIBLE);
            workListBinding.otherWorkLayout.setVisibility(View.GONE);
            workListBinding.finYearSpinner.setVisibility(View.GONE);
            workListBinding.selectedFinyearTv.setVisibility(View.VISIBLE);
        }else {
            workListBinding.otherWorkLayout.setVisibility(View.VISIBLE);
            workListBinding.finYearSpinner.setVisibility(View.VISIBLE);
            workListBinding.schemeLayout.setVisibility(View.GONE);
            workListBinding.selectedFinyearTv.setVisibility(View.GONE);
        }

        if(prefManager.getLevels().equals("S")){
            workListBinding.districtTv.setVisibility(View.VISIBLE);
            workListBinding.districtLayout.setVisibility(View.VISIBLE);
            workListBinding.blockTv.setVisibility(View.VISIBLE);
            workListBinding.blockLayout.setVisibility(View.VISIBLE);

        }else if(prefManager.getLevels().equals("D")){
            workListBinding.districtTv.setVisibility(View.GONE);
            workListBinding.districtLayout.setVisibility(View.GONE);
            workListBinding.blockTv.setVisibility(View.VISIBLE);
            workListBinding.blockLayout.setVisibility(View.VISIBLE);
        }else if(prefManager.getLevels().equals("B")){
            workListBinding.districtTv.setVisibility(View.GONE);
            workListBinding.districtLayout.setVisibility(View.GONE);
            workListBinding.blockTv.setVisibility(View.GONE);
            workListBinding.blockLayout.setVisibility(View.GONE);
        }


        if(prefManager.getLevels().equalsIgnoreCase("S")){
            loadDistrictList();
        }else if(prefManager.getLevels().equalsIgnoreCase("D")){
            loadBlockList();
        }else if(prefManager.getLevels().equalsIgnoreCase("B")){
            getVillageList();
        }

        if(type.equals("village")){
            workListBinding.villageSelectionLayout.setVisibility(View.VISIBLE);
            workListBinding.filterLayout.setVisibility(View.GONE);
            Bundle b = getIntent().getExtras();
            String response=b.getString("jsonObject");
            try {

                JSONObject obj = new JSONObject(response);
                new InsertVillageOfLocationTask().execute(obj);
                Log.d("My App", obj.toString());

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
            }

        }else {
            workListBinding.villageSelectionLayout.setVisibility(View.GONE);
            workListBinding.filterLayout.setVisibility(View.VISIBLE);
        }

        otherWorkFilterSpinner();
//        schemeFilterSpinner();
        finyearFilterSpinner();
        loadOfflineFinYearListDBValues();
        workListBinding.selectedFinyearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finYearCheckbox();
            }
        });
        workListBinding.schemeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SelectedFinYear!= null && !SelectedFinYear.equals("")){
                    if(prefManager.getLevels().equalsIgnoreCase("S")) {
                        if(SelectedDistrict!= null && !SelectedDistrict.equals("")){
                            if(Scheme.size()>0){
                                callDialog();
                            }else {
                                Utils.showAlert(OnlineWorkFilterScreen.this,"No Record Found");
                            }

                        }else {
                            Utils.showAlert(OnlineWorkFilterScreen.this,"Select District");
                        }

                }else {
                        if(Scheme.size()>0){
                            callDialog();
                        }else {
                            Utils.showAlert(OnlineWorkFilterScreen.this,"No Record Found");
                        }
                    }
            }else {
                Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
            }

            }
        });
        workListBinding.districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedDistrict=District.get(position).getDistrictCode();
                    districtCodeJsonArray.put(District.get(position).getDistrictCode());
                    loadBlockList();
                    getSchemeList();
                }else {
                    SelectedDistrict="";
                    SelectedBlock="";
                    SelectedVillage="";
                    SelectedScheme="";
                    workListBinding.blockSpinner.setAdapter(null);
                    workListBinding.schemeSpinner.setAdapter(null);
                    workListBinding.scheme.setText("");
                    workListBinding.villageSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    SelectedBlock=Block.get(position).getBlockCode();
                    getVillageList();
                }else {
                    SelectedBlock="";
                    SelectedVillage="";
                    workListBinding.villageSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    villageCodeJsonArray = new JSONArray();
                    SelectedVillage=Village.get(position).getPvCode();
                    villageCodeJsonArray.put(Village.get(position).getPvCode());

                }else {
                    villageCodeJsonArray = new JSONArray();
                    SelectedVillage="";
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
                    schemeJsonArray = new JSONArray();
                    SelectedScheme=Scheme.get(position).getSchemeSequentialID();
                    schemeJsonArray.put(Scheme.get(position).getSchemeSequentialID());

                }else {
                    schemeJsonArray = new JSONArray();
                    SelectedScheme="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.finYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(prefManager.getWorkType().equalsIgnoreCase("rdpr")){
                    if (position > 0) {
                        finyearJsonArray = new JSONArray();
                        SelectedFinYear=FinYear.get(position).getFinancialYear();
                        finyearJsonArray.put(FinYear.get(position).getFinancialYear());
                        workListBinding.districtSpinner.setSelection(0);
                        if(!prefManager.getLevels().equals("S")){
                            getSchemeList();
                        }
                    }else
                        {
                        finyearJsonArray = new JSONArray();
                        SelectedFinYear="";
                        SelectedScheme="";
                        workListBinding.schemeSpinner.setAdapter(null);
                        workListBinding.scheme.setText("");
                        if(prefManager.getLevels().equals("S")){
                            workListBinding.districtSpinner.setSelection(0);
                            SelectedDistrict="";
                        }

                    }
                }else {
                    if (position > 0) {
                        finyearJsonArray = new JSONArray();
                        SelectedFinYear=FinYear.get(position).getFinancialYear();
                        finyearJsonArray.put(FinYear.get(position).getFinancialYear());
                    }else {
                        finyearJsonArray = new JSONArray();
                        SelectedFinYear="";

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        workListBinding.otherWorkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    other_work_category_id=(String.valueOf(other_category_list.get(position).getOther_work_category_id()));
                    other_work_category_name = other_category_list.get(position).getOther_work_category_name();

                }else {

                    other_work_category_id = "";
                    other_work_category_name = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //fetAllApi();
        //Sample
       /* JSONObject jsonObject = new JSONObject();
        String json = "{\"STATUS\":\"OK\",\"RESPONSE\":\"OK\",\"JSON_DATA\":[{\"work_id\":1,\"work_name\":\"Property tax\"},{\"work_id\":2,\"work_name\":\"Water Charges\"},{\"work_id\":3,\"work_name\":\"Professional Tax\"},{\"work_id\":4,\"work_name\":\"Non Tax\"},{\"work_id\":5,\"work_name\":\"Trade License \"}]}";
        try {  jsonObject = new JSONObject(json); } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""); }
        try {
            if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                new Insert_workList().execute(jsonObject);
            }
        } catch (JSONException e) { e.printStackTrace(); }
*/
    }

    private void callDialog() {

        // Initialize dialog
        dialog=new Dialog(OnlineWorkFilterScreen.this);

        // set custom dialog
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        // set custom height and width
//        dialog.getWindow().setLayout(650,800);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        // set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // show dialog
        dialog.show();

        // Initialize and assign variable
        EditText editText=dialog.findViewById(R.id.edit_text);
        RecyclerView recycler=dialog.findViewById(R.id.list_view);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setNestedScrollingEnabled(false);
        recycler.setFocusable(false);
        // Initialize array adapter
        schemeAdapter=new SchemeAdapter(OnlineWorkFilterScreen.this, (ArrayList<ModelClass>) Scheme);
        // set adapter
        recycler.setAdapter(schemeAdapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schemeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void getScheme(String scheme,String schemeId){
        schemeJsonArray = new JSONArray();
        SelectedScheme=schemeId;
        schemeJsonArray.put(schemeId);
        workListBinding.scheme.setText(scheme);
        dialog.dismiss();
    }
    public void otherWorkFilterSpinner() {
        other_category_list = new ArrayList<>();
        other_category_list.clear();
        ModelClass list = new ModelClass();
        list.setOther_work_category_id(0);
        list.setOther_work_category_name("Select Category");
        other_category_list.add(list);
        dbData.open();
        other_category_list.addAll(dbData.getAll_Other_work_category());
        workListBinding.otherWorkSpinner.setAdapter(new CommonAdapter(this, other_category_list, "other_category_list"));
    }

    public void loadOfflineFinYearListDBValues() {
        dbData.open();
        FinYearList  = new ArrayList<>();
        //Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
        FinYearList.clear();
        FinYearList.addAll(dbData.getAll_Fin_Year());
        final ArrayList<String> myFinYearlist = new ArrayList<String>();


        for (int i = 0; i < FinYearList.size(); i++) {
            myFinYearlist.add(FinYearList.get(i).getFinancialYear());

        }
        String[] mStringArray = new String[myFinYearlist.size()];
        finyearStrings = myFinYearlist.toArray(mStringArray);
        FinYearcheckedItems = new boolean[finyearStrings.length];
    }

    public void finYearCheckbox(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(OnlineWorkFilterScreen.this);
        mBuilder.setTitle(R.string.finyear_dialog_title);
        mBuilder.setMultiChoiceItems(finyearStrings, FinYearcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {

                    if (!mFinYearItems.contains(position)) {
                        mFinYearItems.add(position);

                    }
                } else if (mFinYearItems.contains(position)) {
                    mFinYearItems.remove(Integer.valueOf(position));
                }


            }
        });

        mBuilder.setCancelable(false);
        //   final String[] finalFinYearStrings = finyearStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";

                if(mFinYearItems.size() > 0){
                    for (int i = 0; i < mFinYearItems.size(); i++) {
                        item = item + finyearStrings[mFinYearItems.get(i)];
                        if (i != mFinYearItems.size() - 1) {
                            item = item + ", ";
                        }
                    }

                    finyearJsonArray = new JSONArray();
                    for (int i = 0; i < mFinYearItems.size(); i++) {
                        finyearJsonArray.put(finyearStrings[mFinYearItems.get(i)]);
                        prefManager.setFinYearJson(finyearJsonArray);
                        Log.d("FinYearArray", "" + finyearJsonArray);
                    }
                    workListBinding.selectedFinyearTv.setText(item);
                    SelectedFinYear=item;
                    workListBinding.districtSpinner.setSelection(0);
                    if(!prefManager.getLevels().equals("S")){
                        getSchemeList();
                    }
                }else {
                    workListBinding.selectedFinyearTv.setText("");
                    SelectedFinYear="";
                }


            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < FinYearcheckedItems.length; i++) {
                    FinYearcheckedItems[i] = false;
                    mFinYearItems.clear();
                    workListBinding.selectedFinyearTv.setText("");
                }
                finyearJsonArray = new JSONArray();
                prefManager.setFinYearJson(finyearJsonArray);
                SelectedFinYear="";
                SelectedScheme="";
                workListBinding.schemeSpinner.setAdapter(null);
                workListBinding.scheme.setText("");
                if(prefManager.getLevels().equals("S")){
                    workListBinding.districtSpinner.setSelection(0);
                    SelectedDistrict="";
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }


    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }
    public  JSONObject villageListDistrictWiseJsonParams(Activity activity) throws JSONException {

        JSONObject dataSet = new JSONObject();

        if(prefManager.getLevels().equals("S")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, SelectedDistrict);
            dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
        }else if(prefManager.getLevels().equals("D")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
            dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
        }else if(prefManager.getLevels().equals("B")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_DISTRICT_WISE);
            dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
            dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        }        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }

    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector),schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + dataSet);
        return dataSet;
    }
    public  JSONObject schemeListDistrictWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_SCHEME_LIST_DISTRICT_FINYEAR_WISE);
        if(prefManager.getLevels().equalsIgnoreCase("S")){
            dataSet.put(AppConstant.DISTRICT_CODE, districtCodeJsonArray);
        }
        else{
            dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        }

        if(prefManager.getLevels().equalsIgnoreCase("D") || prefManager.getLevels().equalsIgnoreCase("S")){
            dataSet.put(AppConstant.FINANCIAL_YEAR,finyearJsonArray);
        }
        Log.d("schemeListDistrictWise", "" + dataSet);
        return dataSet;
    }

    public void schemeFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT distinct scheme_name,scheme_seq_id FROM " + DBHelper.SCHEME_TABLE_NAME + " order by scheme_name asc", null);

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
        workListBinding.scheme.setText("");
    }
    public void finyearFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME , null);

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
        Cursor cursor = null;
        if(prefManager.getLevels().equals("S")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+SelectedDistrict+" and bcode = "+SelectedBlock+" order by pvname asc",null);

        }else if(prefManager.getLevels().equals("D")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" and bcode = "+SelectedBlock+" order by pvname asc",null);

        }else if(prefManager.getLevels().equals("B")){
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+prefManager.getDistrictCode()+" and bcode = "+prefManager.getBlockCode()+" order by pvname asc",null);

        }
        Village.clear();
        ModelClass list = new ModelClass();
        list.setPvName("Select Village");
        list.setPvCode("0");
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
    public void loadBlockList() {
        Cursor cursor = null;
        if(prefManager.getLevels().equals("S")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+SelectedDistrict+" order by bname asc",null);

        }else if(prefManager.getLevels().equals("D")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+prefManager.getDistrictCode()+" order by bname asc",null);

        }else if(prefManager.getLevels().equals("B")){
            cursor = db.rawQuery("select * from "+DBHelper.BLOCK_TABLE_NAME+" where dcode ="+prefManager.getDistrictCode()+" order by bname asc",null);

        }
        Block.clear();
        ModelClass list = new ModelClass();
        list.setBlockName("Select Block");
        list.setBlockCode("0");
        Block.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int bcode = cursor.getInt(cursor.getColumnIndexOrThrow("bcode"));
                    String bname = cursor.getString(cursor.getColumnIndexOrThrow("bname"));

                    modelClass.setBlockCode(String.valueOf(bcode));
                    modelClass.setBlockName(bname);

                    Block.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Blockspinnersize", "" + Block.size());

        }
        workListBinding.blockSpinner.setAdapter(new CommonAdapter(this, Block, "Block"));
    }
    public void loadDistrictList() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.DISTRICT_TABLE_NAME+" order by dname" , null);
        District.clear();
        ModelClass list = new ModelClass();
        list.setDistrictName("Select District");
        list.setDistrictCode("0");
        District.add(list);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int dcode = cursor.getInt(cursor.getColumnIndexOrThrow("dcode"));
                    String dname = cursor.getString(cursor.getColumnIndexOrThrow("dname"));

                    modelClass.setDistrictCode(String.valueOf(dcode));
                    modelClass.setDistrictName(dname);

                    District.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("Districtspinnersize", "" + District.size());

        }
        workListBinding.districtSpinner.setAdapter(new CommonAdapter(this, District, "District"));
    }
    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }


    public void projectListScreenStateUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){
            if(SelectedDistrict!= null && !SelectedDistrict.equals("")){
                if(SelectedBlock!= null && !SelectedBlock.equals("")){
                    if(SelectedVillage!= null && !SelectedVillage.equals("")){
                        checkWorkType();
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }
                }else {
                    Utils.showAlert(OnlineWorkFilterScreen.this,"Select Block");
                }
            }else {
                Utils.showAlert(OnlineWorkFilterScreen.this,"Select District");
            }
        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void checkWorkType(){
        if(prefManager.getWorkType().equalsIgnoreCase("rdpr")){
            if(SelectedScheme!= null && !SelectedScheme.equals("")&& !SelectedScheme.equals("Select Scheme")){

                getWorkListOptional();
            }else {
                Utils.showAlert(OnlineWorkFilterScreen.this,"Select Scheme");
            }
        }else {
            if(other_work_category_id!= null && !other_work_category_id.equals("")){

                Intent intent = new Intent(this, SaveWorkDetailsActivity.class);
                if (prefManager.getLevels().equalsIgnoreCase("S")){
                    intent.putExtra("scode", prefManager.getStateCode());
                    intent.putExtra("dcode", SelectedDistrict);
                    intent.putExtra("bcode", SelectedBlock);
                    intent.putExtra("pvcode", SelectedVillage);
                    intent.putExtra("fin_year", SelectedFinYear);
                }
                else if (prefManager.getLevels().equalsIgnoreCase("D")) {
                    intent.putExtra("scode", prefManager.getStateCode());
                    intent.putExtra("dcode", prefManager.getDistrictCode());
                    intent.putExtra("bcode", SelectedBlock);
                    intent.putExtra("pvcode", SelectedVillage);
                    intent.putExtra("fin_year", SelectedFinYear);
                }
                else if (prefManager.getLevels().equalsIgnoreCase("B")) {
                    intent.putExtra("scode", prefManager.getStateCode());
                    intent.putExtra("dcode", prefManager.getDistrictCode());
                    intent.putExtra("bcode", prefManager.getBlockCode());
                    intent.putExtra("pvcode", SelectedVillage);
                    intent.putExtra("fin_year", SelectedFinYear);
                }

                intent.putExtra("hab_code", "");
                intent.putExtra("scheme_group_id", "");
                intent.putExtra("work_group_id", "");
                intent.putExtra("work_type_id", "");
                intent.putExtra("scheme_id", "");
                intent.putExtra("work_id", 0);
                intent.putExtra("work_name", "");
                intent.putExtra("as_value", "");
                intent.putExtra("ts_value", "");
                intent.putExtra("current_stage_of_work", "");
                intent.putExtra("is_high_value", "");
                intent.putExtra("onOffType",prefManager.getOnOffType());
                intent.putExtra("other_work_category_id",other_work_category_id);
                intent.putExtra("flag","");
                intent.putExtra("type","other");
                prefManager.setWorkType("other");
                startActivity(intent);
            }else {
                Utils.showAlert(OnlineWorkFilterScreen.this,"Select other work category");
            }
        }

    }
    public void projectListScreenDistrictUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){
            if(SelectedBlock!= null && !SelectedBlock.equals("")){
                if(SelectedVillage!= null && !SelectedVillage.equals("")){
                    checkWorkType();
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }
                }else {
                    Utils.showAlert(OnlineWorkFilterScreen.this,"Select Block");
                }

        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void projectListScreenBlockUser(){
        if(SelectedFinYear!= null && !SelectedFinYear.equals("")){

            if(SelectedVillage!= null && !SelectedVillage.equals("")){
                checkWorkType();
                    }else {
                        Utils.showAlert(OnlineWorkFilterScreen.this,"Select Village");
                    }

        }else {
            Utils.showAlert(OnlineWorkFilterScreen.this,"Select Financial Year");
        }

    }
    public void download() {
        if (Utils.isOnline()) {
            if(prefManager.getLevels().equalsIgnoreCase("S")) {
                projectListScreenStateUser();
            }
            else if (prefManager.getLevels().equalsIgnoreCase("D")) {
                projectListScreenDistrictUser();
            } else {
                projectListScreenBlockUser();
            }
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_internet));
        }
    }
    public void getWorkListByVillage(String districtCode, String blockCode, String pvCode) {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkListByVillage", Api.Method.POST, UrlGenerator.getMainService(), workListByVillageJsonParams(districtCode,blockCode,pvCode), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
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
        try{
            if (prefManager.getLevels().equalsIgnoreCase("S")){
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, SelectedDistrict);
                dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
                dataSet.put(AppConstant.PV_CODE, villageCodeJsonArray);
                dataSet.put(AppConstant.SCHEME_ID, schemeJsonArray);
                dataSet.put(AppConstant.FINANCIAL_YEAR, finyearJsonArray);
                dataSet1.put("inspection_work_details", dataSet);
            }
            else if (prefManager.getLevels().equalsIgnoreCase("D")) {
                dataSet1.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS);
                dataSet.put(AppConstant.STATE_CODE, prefManager.getStateCode());
                dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
                dataSet.put(AppConstant.BLOCK_CODE, SelectedBlock);
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
    public JSONObject workListByVillageJsonParams(String districtCode, String blockCode, String pvCode) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workListByVillage(this,districtCode,blockCode,pvCode).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListByVillage", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workListByVillage(Activity activity,String districtCode, String blockCode, String pvCode) throws JSONException {
        JSONObject dataSet = new JSONObject();
        JSONObject obj = new JSONObject();
        JSONArray jsonArray=new JSONArray();
        jsonArray.put(pvCode);
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_INSPECTION_WORK_DETAILS_OF_LOCATION);
        dataSet.put("inspection_work_details",obj);
        obj.put(AppConstant.DISTRICT_CODE, districtCode);
        obj.put(AppConstant.BLOCK_CODE, blockCode);
        obj.put(AppConstant.PV_CODE, jsonArray);
        Log.d("workListByVillage", "" + dataSet);
        return dataSet;
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
                    openWorkListScreenOfVillage(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    workListInsert = false;
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + responseObj.toString());
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }
            if ("WorkListByVillage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    openWorkListScreenOfVillage(jsonObject);
//                    Utils.showAlert(this, "Your Data will be Downloaded");
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    workListInsert = false;
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + responseObj.toString());
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }
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
            if ("VillageListOfLocation".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageOfLocationTask().execute(jsonObject);
                }else {
                    workListBinding.recycler.setVisibility(View.GONE);
                    workListBinding.notFoundTv.setVisibility(View.VISIBLE);
                }
                Log.d("VillageListOfLocation", "" + responseObj.toString());
                Log.d("VillageListOfLocation", "" + responseDecryptedBlockKey);
            }
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertSchemeTask().execute(jsonObject);
                }
                Log.d("schemeAll", "" + responseObj.toString());
                Log.d("schemeAll", "" + responseDecryptedSchemeKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void openWorkListScreenOfVillage(JSONObject jsonObject) {
        Intent intent = new Intent(this, WorkList.class);
        intent.putExtra("OnOffType","online");

        Bundle b = new Bundle();
        b.putString("jsonObject",jsonObject.toString());
        intent.putExtras(b);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(OnlineWorkFilterScreen.this, "Loading...", true, false, null);
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteVillageTable();
            ArrayList<ModelClass> villagelist_count = dbData.getAll_Village(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass villageListValue = new ModelClass();
                        try {
                            villageListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            villageFilterSpinner();
        }
    }
    public class InsertVillageOfLocationTask extends AsyncTask<JSONObject, Void, List<ModelClass>> {
        private ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Utils.showProgress(OnlineWorkFilterScreen.this);
            progressHUD = ProgressHUD.show(OnlineWorkFilterScreen.this, "Loading...", true, false, null);

        }

        @Override
        protected List<ModelClass> doInBackground(JSONObject... params) {
            Village.clear();
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass villageListValue = new ModelClass();
                        try {
                            villageListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            Village.add(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            return Village;
        }

        @Override
        protected void onPostExecute(List<ModelClass> list) {
            super.onPostExecute(list);
            //Utils.hideProgress();
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(list.size()>0){
                adapter = new VillageListAdapter(OnlineWorkFilterScreen.this, (ArrayList<ModelClass>) list,dbData,"online");
                workListBinding.recycler.setAdapter(adapter);
                workListBinding.villageSelectionLayout.setVisibility(View.VISIBLE);
                workListBinding.recycler.setVisibility(View.VISIBLE);
                workListBinding.notFoundTv.setVisibility(View.GONE);
            }else {
                workListBinding.villageSelectionLayout.setVisibility(View.VISIBLE);
                workListBinding.recycler.setVisibility(View.GONE);
                workListBinding.notFoundTv.setVisibility(View.VISIBLE);
            }

        }
    }
    public class InsertSchemeTask extends AsyncTask<JSONObject, Void, Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(OnlineWorkFilterScreen.this, "Loading...", true, false, null);
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);

        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteSchemeTable();

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
                            modelClass.setSchemeSequentialID(jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID));
                            modelClass.setSchemeName(jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME));
                            modelClass.setFinancialYear(jsonArray.getJSONObject(i).getString(AppConstant.FINANCIAL_YEAR));

                            dbData.insertScheme(modelClass);
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
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            schemeFilterSpinner();
        }
    }


}
