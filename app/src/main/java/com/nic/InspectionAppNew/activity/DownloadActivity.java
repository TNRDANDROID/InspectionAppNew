package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.MyCustomTextView;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.nic.InspectionAppNew.dataBase.DBHelper.BLOCK_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.DISTRICT_TABLE_NAME;
import static com.nic.InspectionAppNew.dataBase.DBHelper.SCHEME_TABLE_NAME;


public class DownloadActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener {
    private Button download, btn_view_finyear,btn_view_district, btn_view_block, btn_view_village, btn_view_scheme;

    public MyCustomTextView title_tv, selected_finyear_tv, selected_district_tv, selected_block_tv, selected_village_tv, selected_scheme_tv;
    private  PrefManager prefManager;

    private ImageView back_img,homeimg;
    private List<ModelClass> District = new ArrayList<>();
    private List<ModelClass> Block = new ArrayList<>();
    private List<ModelClass> Village = new ArrayList<>();
    private List<ModelClass> Scheme = new ArrayList<>();
    private List<ModelClass> FinYearList = new ArrayList<>();
    public LinearLayout select_fin_year_layout,select_district_layout, select_block_layout, select_village_layout, select_scheme_layout, block_hide_layout,district_hide_layout ;
    public LinearLayout district_layout,block_layout;
    private View view;
    final ArrayList<Integer> mDistrictItems = new ArrayList<>();
    final ArrayList<Integer> mVillageItems = new ArrayList<>();
    final ArrayList<Integer> mFinYearItems = new ArrayList<>();
    final ArrayList<Integer> mSchemeItems = new ArrayList<>();
    final ArrayList<Integer> mBlockItems = new ArrayList<>();
    /*It is Temporarly hide scheme is empty in the multiple choice dialog to unhide */
    String[] districtStrings;
    String[] districtCodeStrings;
    String[] blockStrings;
    String[] blockCodeStrings;
    String[] villageStrings;
    String[] villageCodeStrings;
    String[] schemeStrings;
    String[] schemeCodeStrings;
    String[] finyearStrings;
    boolean[] districtcheckedItems;
    boolean[] blockcheckedItems;
    boolean[] FinYearcheckedItems;
    boolean[] villageCheckedItems;
    boolean[] schemeCheckedItems;
    String pref_Block, pref_Village, pref_Scheme, pref_finYear;
    private ProgressHUD progressHUD;
    private JSONArray updatedJsonArray;
    boolean clicked = false;
    ArrayList<JSONArray> myVillageCodelist;
    ArrayList<JSONArray> myBlockCodelist;
    public com.nic.InspectionAppNew.dataBase.dbData dbData = new dbData(this);
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    boolean workListInsert = false;
    TextView skip;
    String Bcode="";
    String Bname="";
    String Dcode="";
    String Dname="";
    String Vcode="";
    String Vname="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_parent_layout);
        intializeUI();
    }

    public void intializeUI() {
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager = new PrefManager(this);
        homeimg = (ImageView) findViewById(R.id.homeimg);
        skip = (TextView) findViewById(R.id.skip);
        district_layout = (LinearLayout) findViewById(R.id.district_layout);
        block_layout = (LinearLayout) findViewById(R.id.block_layout);
        select_fin_year_layout = (LinearLayout) findViewById(R.id.select_fin_year_layout);
        select_block_layout = (LinearLayout) findViewById(R.id.select_block_layout);
        select_district_layout = (LinearLayout) findViewById(R.id.select_district_layout);
        select_village_layout = (LinearLayout) findViewById(R.id.select_village_layout);
        select_scheme_layout = (LinearLayout) findViewById(R.id.select_scheme_layout);
        block_hide_layout = (LinearLayout) findViewById(R.id.block_hide_layout);
        district_hide_layout = (LinearLayout) findViewById(R.id.district_hide_layout);
        download = (Button) findViewById(R.id.btn_download);
        btn_view_finyear = (Button) findViewById(R.id.btn_view_finyear);
        btn_view_district = (Button) findViewById(R.id.btn_view_district);
        btn_view_block = (Button) findViewById(R.id.btn_view_block);
        btn_view_village = (Button) findViewById(R.id.btn_view_village);
        btn_view_scheme = (Button) findViewById(R.id.btn_view_scheme);
        back_img = (ImageView) findViewById(R.id.backimg);
        title_tv = (MyCustomTextView) findViewById(R.id.title_tv);
        selected_finyear_tv = (MyCustomTextView) findViewById(R.id.selected_finyear_tv);
        selected_district_tv = (MyCustomTextView) findViewById(R.id.selected_district_tv);
        selected_block_tv = (MyCustomTextView) findViewById(R.id.selected_block_tv);
        selected_village_tv = (MyCustomTextView) findViewById(R.id.selected_village_tv);
        selected_scheme_tv = (MyCustomTextView) findViewById(R.id.selected_scheme_tv);
        view = (View) findViewById(R.id.scheme_view);
        back_img.setOnClickListener(this);
        homeimg.setOnClickListener(this);
        download.setOnClickListener(this);


        btn_view_finyear.setOnClickListener(this);
        btn_view_district.setOnClickListener(this);
        btn_view_block.setOnClickListener(this);
        btn_view_village.setOnClickListener(this);
        btn_view_scheme.setOnClickListener(this);
        download.setOnClickListener(this);
        skip.setOnClickListener(this);

        loadOfflineFinYearListDBValues();


        if (prefManager.getLevels().equalsIgnoreCase("B")) {
//            loadOfflineVillgeListDBValues();
            getVillageList();
            district_layout.setVisibility(View.GONE);
            block_layout.setVisibility(View.GONE);
        }
        if(prefManager.getLevels().equalsIgnoreCase("S")){
            loadOfflineDistrictListDBValues();
        }
        if (prefManager.getLevels().equalsIgnoreCase("D")) {
            loadOfflineBlockListDBValues();
            district_layout.setVisibility(View.GONE);
        }

    }
    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + authKey);
        return dataSet;
    }


    public void loadOfflineFinYearListDBValues() {
        dbData.open();
        FinYearList  = new ArrayList<>();
        //Cursor FinYear = getRawEvents("SELECT fin_year FROM " + DBHelper.FINANCIAL_YEAR_TABLE_NAME, null);
        FinYearList.clear();
        FinYearList.addAll(dbData.getAll_Fin_Year());
        final ArrayList<String> myFinYearlist = new ArrayList<String>();

        /*if (FinYear.getCount() > 0) {
            if (FinYear.moveToFirst()) {
                do {
                    ModelClass finyearList = new ModelClass();
                    String financialYear = FinYear.getString(FinYear.getColumnIndexOrThrow(AppConstant.FIN_YEAR));
                    finyearList.setFinancialYear(financialYear);
                    FinYearList.add(finyearList);
                    //   Log.d("finyeardb", "" + finyearList);
                } while (FinYear.moveToNext());
            }

        }*/

        for (int i = 0; i < FinYearList.size(); i++) {
            myFinYearlist.add(FinYearList.get(i).getFinancialYear());

        }
        String[] mStringArray = new String[myFinYearlist.size()];
        finyearStrings = myFinYearlist.toArray(mStringArray);
        FinYearcheckedItems = new boolean[finyearStrings.length];
    }
    public void finYearCheckbox(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.finyear_dialog_title);
        mBuilder.setMultiChoiceItems(finyearStrings, FinYearcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {

                    mSchemeItems.clear();
                    select_scheme_layout.setVisibility(View.GONE);
                    mDistrictItems.clear();
                    select_district_layout.setVisibility(View.GONE);

                    if (!mFinYearItems.contains(position)) {
                        mFinYearItems.add(position);

                    }
                } else if (mFinYearItems.contains(position)) {
                    mFinYearItems.remove(Integer.valueOf(position));
                }
                JSONArray finyearJsonArray = new JSONArray();

                for (int i = 0; i < mFinYearItems.size(); i++) {
                    finyearJsonArray.put(finyearStrings[mFinYearItems.get(i)]);
                    prefManager.setFinYearJson(finyearJsonArray);
                    Log.d("FinYearArray", "" + finyearJsonArray);
                }
            }
        });

        mBuilder.setCancelable(false);
        //   final String[] finalFinYearStrings = finyearStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";

                for (int i = 0; i < mFinYearItems.size(); i++) {
                    item = item + finyearStrings[mFinYearItems.get(i)];
                    if (i != mFinYearItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                if(mFinYearItems.size() > 0){
                    select_fin_year_layout.setVisibility(View.VISIBLE);
                }else {
                    select_fin_year_layout.setVisibility(View.GONE);
                }
                selected_finyear_tv.setText(item);
                selected_scheme_tv.setText("");
                select_scheme_layout.setVisibility(View.GONE);
                if(!prefManager.getLevels().equalsIgnoreCase("S")) {
                    if (Utils.isOnline()) {
                        try {
                            db.delete(SCHEME_TABLE_NAME, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }if(mFinYearItems.size() > 0) {
                            getSchemeList();
                        }
                    } else {
                        loadOfflineSchemeListDBValues();
                    }
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
                    selected_finyear_tv.setText("");
                    select_fin_year_layout.setVisibility(View.GONE);

                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
    public void loadOfflineDistrictListDBValues() {

        Cursor DistrictList = getRawEvents("SELECT * FROM " + DISTRICT_TABLE_NAME, null);
        District.clear();
        final ArrayList<String> myDistrictList = new ArrayList<String>();
        final ArrayList<String> myDistrictCodeList = new ArrayList<String>();
        if (DistrictList.getCount() > 0) {
            if (DistrictList.moveToFirst()) {
                do {
                    ModelClass districtList = new ModelClass();
                    String districtCode = DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String districtName= DistrictList.getString(DistrictList.getColumnIndexOrThrow(AppConstant.DISTRICT_NAME));
                    districtList.setDistictCode(districtCode);
                    districtList.setDistrictName(districtName);
                    District.add(districtList);
                } while (DistrictList.moveToNext());
            }
        }
        for (int i = 0; i < District.size(); i++) {
            myDistrictList.add(District.get(i).getDistrictName());
            myDistrictCodeList.add(District.get(i).getDistictCode());
        }

        districtStrings = myDistrictList.toArray(new String[myDistrictList.size()]);
        districtCodeStrings = myDistrictCodeList.toArray(new String[myDistrictCodeList.size()]);
        districtcheckedItems= new boolean[districtStrings.length];

    }
    public void districtCheckbox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.district_dialog_title);
        mBuilder.setSingleChoiceItems(districtStrings, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                JSONArray districtCodeJsonArray = new JSONArray();
                prefManager.setDistrictCode(District.get(position).getDistictCode());
                districtCodeJsonArray.put(District.get(position).getDistictCode());
                prefManager.setDistrictCodeJson(districtCodeJsonArray);
                Dcode=District.get(position).getDistictCode();
                Dname=District.get(position).getDistrictName();
                Log.d("districtcode", "" + districtCodeJsonArray);

            }
        });

        mBuilder.setCancelable(true);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                if (Dcode != null && !Dcode.equals("")) {
                    select_district_layout.setVisibility(View.VISIBLE);
                    selected_district_tv.setVisibility(View.VISIBLE);
                    selected_district_tv.setText(Dname);
                    selected_scheme_tv.setText("");
                    select_scheme_layout.setVisibility(View.GONE);
                    selected_block_tv.setText("");
                    select_block_layout.setVisibility(View.GONE);
                    selected_village_tv.setText("");
                    select_village_layout.setVisibility(View.GONE);
                    loadOfflineBlockListDBValues();
                    if(prefManager.getLevels().equalsIgnoreCase("S")) {
                        if (Utils.isOnline()) {
                            try {
                                db.delete(SCHEME_TABLE_NAME, null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }if(mFinYearItems.size() > 0 && !Dcode.equals("")) {
                                getSchemeList();
                            }
                        } else {
                            loadOfflineSchemeListDBValues();
                        }
                    }
                }else {
                    select_district_layout.setVisibility(View.GONE);
                    selected_district_tv.setVisibility(View.GONE);
                    selected_district_tv.setText("");
                }
                dialogInterface.dismiss();
            }
        });
        if(districtStrings.length>0){
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }else {
            Utils.showAlert(this,"No Record Found!");
        }



    }

    public void  districtCheckboxMultipleSelection() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.district_dialog_title);
        mBuilder.setMultiChoiceItems(districtStrings, districtcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    mSchemeItems.clear();
                    select_scheme_layout.setVisibility(View.GONE);
                    mBlockItems.clear();
                    select_block_layout.setVisibility(View.GONE);
                    mVillageItems.clear();
                    select_village_layout.setVisibility(View.GONE);

                    /*if (!mDistrictItems.contains(position)) {
                        mDistrictItems.add(position);
                    }*/
                    for (int i = 0; i < districtcheckedItems.length; i++) {
                        if (i == position) {
                            districtcheckedItems[i]=true;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, true);
                            mDistrictItems.add(i);
                        }
                        else {
                            districtcheckedItems[i]=false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                            mDistrictItems.remove(Integer.valueOf(i));

                        }
                    }

                } else if (mDistrictItems.contains(position)) {
                    mDistrictItems.remove(Integer.valueOf(position));
                }
                JSONArray districtCodeJsonArray = new JSONArray();

                for (int i = 0; i < mDistrictItems.size(); i++) {
                    prefManager.setDistrictCode(districtCodeStrings[mDistrictItems.get(i)]);
                    districtCodeJsonArray.put(districtCodeStrings[mDistrictItems.get(i)]);
                }
                prefManager.setDistrictCodeJson(districtCodeJsonArray);
                Log.d("districtcode", "" + districtCodeJsonArray);



            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mDistrictItems.size(); i++) {
                    item = item + districtStrings[mDistrictItems.get(i)];
                    if (i != mDistrictItems.size() - 1) {
                        item = item + ", ";


                    }
                }
                if(mDistrictItems.size() > 0) {
                    select_district_layout.setVisibility(View.VISIBLE);
                }else {
                    select_district_layout.setVisibility(View.GONE);
                }
                selected_district_tv.setText(item);
                selected_scheme_tv.setText("");
                select_scheme_layout.setVisibility(View.GONE);
                selected_block_tv.setText("");
                select_block_layout.setVisibility(View.GONE);
                selected_village_tv.setText("");
                select_village_layout.setVisibility(View.GONE);
                loadOfflineBlockListDBValues();
                if(prefManager.getLevels().equalsIgnoreCase("S")) {
                    if (Utils.isOnline()) {
                        try {
                            db.delete(SCHEME_TABLE_NAME, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }if(mFinYearItems.size() > 0 && mDistrictItems.size() > 0) {
                            getSchemeList();
                        }
                    } else {
                        loadOfflineSchemeListDBValues();
                    }
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
                for (int i = 0; i < districtcheckedItems.length; i++) {
                    districtcheckedItems[i] = false;
                    mDistrictItems.clear();
                    selected_district_tv.setText("");
                    select_district_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void loadOfflineBlockListDBValues() {

        String blockSql = null;
        if (prefManager.getLevels().equalsIgnoreCase("S")){
            JSONArray filterBlock = prefManager.getDistrictCodeJson();
            blockSql = "SELECT * FROM " + BLOCK_TABLE_NAME + " WHERE dcode in" + filterBlock.toString().replace("[", "(").replace("]", ")") + " order by bname";
        }
        else if (prefManager.getLevels().equalsIgnoreCase("D")){
            String filterBlock = prefManager.getDistrictCode();
            blockSql = "SELECT * FROM "+BLOCK_TABLE_NAME+" where dcode = "+filterBlock+" order by bname";
        }
        Log.d("District",""+blockSql);

        Cursor BlockList = getRawEvents(blockSql, null);
        Block.clear();
        final ArrayList<String> myBlockList = new ArrayList<String>();
        final ArrayList<String> block_myBlockCodeList = new ArrayList<String>();
        myBlockCodelist = new ArrayList<JSONArray>();
        if (BlockList.getCount() > 0) {
            if (BlockList.moveToFirst()) {
                do {
                    ModelClass blockList = new ModelClass();
                    String districtCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String blockName = BlockList.getString(BlockList.getColumnIndexOrThrow(AppConstant.BLOCK_NAME));
                    blockList.setDistictCode(districtCode);
                    blockList.setBlockCode(blockCode);
                    blockList.setBlockName(blockName);
                    Block.add(blockList);
                } while (BlockList.moveToNext());
            }
        }
        for (int i = 0; i < Block.size(); i++) {
            myBlockList.add(Block.get(i).getBlockName());

            if (prefManager.getLevels().equalsIgnoreCase("S")) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(Block.get(i).getDistictCode());
                jsonArray.put(Block.get(i).getBlockCode());
                myBlockCodelist.add(jsonArray);
            }
            block_myBlockCodeList.add(Block.get(i).getBlockCode());

        }

        blockStrings = myBlockList.toArray(new String[myBlockList.size()]);
        blockCodeStrings = block_myBlockCodeList.toArray(new String[block_myBlockCodeList.size()]);
        blockcheckedItems= new boolean[blockStrings.length];

    }

    // Single selection Block
    public void blockCheckbox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.block_dialog_title);
        mBuilder.setSingleChoiceItems(blockStrings, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                    JSONArray blockCodeJsonArray = new JSONArray();
                    prefManager.setBlockCode(Block.get(position).getBlockCode());
                    blockCodeJsonArray.put(Block.get(position).getBlockCode());
                    prefManager.setBlockCodeJson(blockCodeJsonArray);
                    Bcode=Block.get(position).getBlockCode();
                    Bname=Block.get(position).getBlockName();
                    Log.d("blockcode", "" + blockCodeJsonArray);

            }
        });

        mBuilder.setCancelable(true);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                if (Bcode != null && !Bcode.equals("")) {
                    getVillageList();
                    select_block_layout.setVisibility(View.VISIBLE);
                    selected_block_tv.setVisibility(View.VISIBLE);
                    selected_block_tv.setText(Bname);
                    selected_village_tv.setText("");
                    select_village_layout.setVisibility(View.GONE);

                }else {
                    select_block_layout.setVisibility(View.GONE);
                    selected_block_tv.setVisibility(View.GONE);
                    selected_block_tv.setText("");
                }
                dialogInterface.dismiss();
            }
        });

        if(prefManager.getLevels().equals("S")){
            if(Dcode != null && !Dcode.equals("") ) {
                if(blockStrings.length>0){
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }else {
                    Utils.showAlert(this,"No Record Found!");
                }
            }
            else {
                Utils.showAlert(this,"Please Select District!");
                select_village_layout.setVisibility(View.GONE);
            }
        }else {
            if(blockStrings.length>0){
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }else {
                Utils.showAlert(this,"No Record Found!");
            }
        }

    }

    public void blockCheckboxMultipleSelection() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.block_dialog_title);
        mBuilder.setMultiChoiceItems(blockStrings, blockcheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {

                    mVillageItems.clear();
                    select_village_layout.setVisibility(View.GONE);

                    /*if (!mBlockItems.contains(position)) {
                        mBlockItems.add(position);
                    }*/
                    for (int i = 0; i < blockcheckedItems.length; i++) {
                        if (i == position) {
                            blockcheckedItems[i]=true;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, true);
                            mBlockItems.add(i);
                        }
                        else {
                            blockcheckedItems[i]=false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                            mBlockItems.remove(Integer.valueOf(i));

                        }
                    }

                } else if (mBlockItems.contains(position)) {
                    mBlockItems.remove(Integer.valueOf(position));
                }
                JSONArray blockCodeJsonArray = new JSONArray();

                for (int i = 0; i < mBlockItems.size(); i++) {
                    prefManager.setBlockCode(blockCodeStrings[mBlockItems.get(i)]);
                    if(prefManager.getLevels().equalsIgnoreCase("S")) {
                        blockCodeJsonArray.put(myBlockCodelist.get(mBlockItems.get(i)));
                    }
                    if (prefManager.getLevels().equalsIgnoreCase("D")) {

                        blockCodeJsonArray.put(blockCodeStrings[mBlockItems.get(i)]);
                    }

                }
                prefManager.setBlockCodeJson(blockCodeJsonArray);
                Log.d("blockcode", "" + blockCodeJsonArray);

              // loadOfflineVillgeListDBValues();

            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mBlockItems.size(); i++) {
                    item = item + blockStrings[mBlockItems.get(i)];
                    if (i != mBlockItems.size() - 1) {
                        item = item + ", ";


                    }
                }
                if(mBlockItems.size() > 0) {
                    select_block_layout.setVisibility(View.VISIBLE);
                }else {
                    select_block_layout.setVisibility(View.GONE);
                }
                selected_block_tv.setText(item);
                selected_village_tv.setText("");
                select_village_layout.setVisibility(View.GONE);
//                loadOfflineVillgeListDBValues();
                getVillageList();
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
                for (int i = 0; i < blockcheckedItems.length; i++) {
                    blockcheckedItems[i] = false;
                    mBlockItems.clear();
                    selected_block_tv.setText("");
                    select_block_layout.setVisibility(View.GONE);
                }
            }
        });

        if(prefManager.getLevels().equals("S")){
            AlertDialog mDialog = mBuilder.create();
            if(mDistrictItems.size() > 0 ) {
                mDialog.show();
            }
            else {
                Utils.showAlert(this,"Please Select District!");
                select_village_layout.setVisibility(View.GONE);
            }
        }else {
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    }


    public void loadOfflineVillgeListDBValues() {

        String villageSql = null;
        villageSql = "SELECT * FROM " + DBHelper.VILLAGE_TABLE_NAME + " WHERE bcode ="+prefManager.getBlockCode()+" and dcode ="+prefManager.getDistrictCode()+ " order by pvname asc";
        Log.d("villageSql", "" + villageSql);
        Cursor VillageList = getRawEvents(villageSql, null);
        Village.clear();
        final ArrayList<String> myVillageList = new ArrayList<String>();
         myVillageCodelist = new ArrayList<JSONArray>();

        if (VillageList.getCount() > 0) {
            if (VillageList.moveToFirst()) {
                do {
                    ModelClass villageList = new ModelClass();
                    String districtCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.DISTRICT_CODE));
                    String blockCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.BLOCK_CODE));
                    String pvCode = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_CODE));
                    String pvname = VillageList.getString(VillageList.getColumnIndexOrThrow(AppConstant.PV_NAME));

                    villageList.setVillageListDistrictCode(districtCode);
                    villageList.setVillageListBlockCode(blockCode);
                    villageList.setVillageListPvCode(pvCode);
                    villageList.setVillageListPvName(pvname);

                    Village.add(villageList);
                } while (VillageList.moveToNext());
            }
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < Village.size(); i++) {
            myVillageList.add(Village.get(i).getVillageListPvName());
            jsonArray.put(Village.get(i).getVillageListPvCode());

            myVillageCodelist.add(jsonArray);
        }

        villageStrings = myVillageList.toArray(new String[myVillageList.size()]);
        villageCheckedItems = new boolean[villageStrings.length];
    }

    // Single Selection Village
    public void villageCheckbox() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.village_dialog_title);
        mBuilder.setSingleChoiceItems(villageStrings, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                    JSONArray villageCodeJsonArray = new JSONArray();
                    villageCodeJsonArray.put(Village.get(position).getVillageListPvCode());
                    prefManager.setVillagePvCodeJson(villageCodeJsonArray);
                    Vcode=Village.get(position).getVillageListPvCode();
                    Vname=Village.get(position).getVillageListPvName();
                    Log.d("villagecode", "" + villageCodeJsonArray);
            }
        });

        mBuilder.setCancelable(true);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                if (Vcode != null && !Vcode.equals("")) {
                    selected_village_tv.setText(Vname);
                    select_village_layout.setVisibility(View.VISIBLE);
                    selected_village_tv.setVisibility(View.VISIBLE);

                }else {
                    selected_village_tv.setText("");
                    select_village_layout.setVisibility(View.GONE);
                    selected_village_tv.setVisibility(View.GONE);
                }
                    dialogInterface.dismiss();

            }
        });



        if(Bcode != null && !Bcode.equals("") || prefManager.getLevels().equalsIgnoreCase("B")) {/*Used for Block level Login*/
            if(villageStrings.length>0){
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }else {
                Utils.showAlert(this,"No Record Found!");
            }
        }
        else {
            Utils.showAlert(this,"Please Select Block!");
            select_village_layout.setVisibility(View.GONE);
        }

    }


    // Multiple Selection Village
    public void villageCheckboxMultipleSelection() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.village_dialog_title);
        mBuilder.setMultiChoiceItems(villageStrings, villageCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
/* if (!mVillageItems.contains(position)) {
                        mVillageItems.add(position);
                    }*/

                    for (int i = 0; i < villageCheckedItems.length; i++) {
                        if (i == position) {
                            villageCheckedItems[i]=true;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, true);
                            mVillageItems.add(i);
                        }
                        else {
                            villageCheckedItems[i]=false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                            mVillageItems.remove(Integer.valueOf(i));

                        }
                    }
                } else if (mVillageItems.contains(position)) {
                    mVillageItems.remove((Integer.valueOf(position)));
                }
                JSONArray villageCodeJsonArray = new JSONArray();

                for (int i = 0; i < mVillageItems.size(); i++) {
                    villageCodeJsonArray.put(Village.get(i).getVillageListPvCode());
                }
                prefManager.setVillagePvCodeJson(villageCodeJsonArray);
                Log.d("villagecode", "" + villageCodeJsonArray);
//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mVillageItems.size(); i++) {
                    item = item + villageStrings[mVillageItems.get(i)];
                    if (i != mVillageItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                if(mVillageItems.size() > 0) {
                    select_village_layout.setVisibility(View.VISIBLE);
                }else {
                    select_village_layout.setVisibility(View.GONE);
                }
                selected_village_tv.setText(item);
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
                for (int i = 0; i < villageCheckedItems.length; i++) {
                    villageCheckedItems[i] = false;
                    mVillageItems.clear();
                    selected_village_tv.setText("");
                    select_village_layout.setVisibility(View.GONE);
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        if(mBlockItems.size() > 0 || prefManager.getLevels().equalsIgnoreCase("B")) {
/*Used for Block level Login*/

            mDialog.show();
        }
        else {
            Utils.showAlert(this,"Please Select Block!");
            select_village_layout.setVisibility(View.GONE);
        }

    }

    public void selectFinancialYear() {
        String fin_Year = "SELECT distinct(fin_year) FROM " + SCHEME_TABLE_NAME;
        String Fin_Year_DB = null;
        Cursor selectFinYearInDB = getRawEvents(fin_Year, null);
        if (selectFinYearInDB.moveToFirst()) {
            do {
                Fin_Year_DB = selectFinYearInDB.getString(0);
                Log.d("inspectionID", "" + Fin_Year_DB);
            } while (selectFinYearInDB.moveToNext());
        }
        if (!Fin_Year_DB.equalsIgnoreCase(String.valueOf(prefManager.getFinYearJson()))) {
            Utils.showAlert(this, "Data Not Available for this Financial Year! please turn on your Mobile Network");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_view_finyear:
                finYearCheckbox();
                break;
            case R.id.btn_view_district:
                districtCheckbox();
                break;
            case R.id.btn_view_block:
                blockCheckbox();
                break;
            case R.id.btn_view_village:
                villageCheckbox();
                break;
            case R.id.btn_view_scheme:
                schemeCheckbox();
                break;
            case R.id.btn_download:
                download();
                break;
            case R.id.backimg:
                onBackPress();
                break;
            case R.id.skip:
                dbData.open();
                ArrayList<ModelClass> workList = new ArrayList<>();
                workList = new ArrayList<>();
                workList = dbData.getAllWorkList("offline","all","","","","","");
                if(workList.size() > 0){
                    openWorkListScreen();
                }else {
                    Utils.showAlert(this,"Please download your work list!");
                }

                break;
            case R.id.homeimg:
                dashboard();
                break;
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

    public void projectListScreenStateUser() {
        if (!selected_finyear_tv.getText().equals("")) {
            if(!selected_district_tv.getText().equals("")) {
                if (!selected_block_tv.getText().equals("")) {
                    if (!selected_village_tv.getText().equals("")) {
                        if (!selected_scheme_tv.getText().equals("")) {
                            getWorkListOptional();
                        } else {
                            Utils.showAlert(this, "Select Scheme");
                        }
                    } else {
                        Utils.showAlert(this, "Select Village");
                    }
                } else {
                    Utils.showAlert(this, "Select Block");
                }
            }else{
                Utils.showAlert(this, "Select District");
            }
        } else {
            Utils.showAlert(this, "Select Financial year");
        }
    }

    public void projectListScreenDistrictUser() {
        if (!selected_finyear_tv.getText().equals("")) {
            if (!selected_block_tv.getText().equals("")) {
                if (!selected_village_tv.getText().equals("")) {
                    if (!selected_scheme_tv.getText().equals("")) {
                        getWorkListOptional();
                    } else {
                        Utils.showAlert(this, "Select Scheme");
                    }
                } else {
                    Utils.showAlert(this, "Select Village");
                }
            } else {
                Utils.showAlert(this, "Select Block");
            }
        } else {
            Utils.showAlert(this, "Select Financial year");
        }
    }

    public void projectListScreenBlockUser() {
        if (!selected_finyear_tv.getText().equals("")) {
                if (!selected_village_tv.getText().equals("")) {
                    if (!selected_scheme_tv.getText().equals("")) {
                        getWorkListOptional();
                    } else {
                        Utils.showAlert(this, "Select Scheme");
                    }
                } else {
                    Utils.showAlert(this, "Select Village");
                }
        } else {
            Utils.showAlert(this, "Select Financial year");
        }
    }



    public void getSchemeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("SchemeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), schemeListJsonParams(), "not cache", this);
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



    public JSONObject schemeListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.schemeListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("schemeList", "" + authKey);
        return dataSet;
    }

    public JSONObject workListOptionalJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.workListOptional(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkListOptional", "" + dataSet);
        return dataSet;
    }





    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {

            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("SchemeList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedSchemeKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedSchemeKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    loadSchemeList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
               Log.d("schemeAll", "" + responseDecryptedSchemeKey);
//                int maxLogSize = 1000;
//                        for(int i = 0; i <= responseDecryptedSchemeKey.length() / maxLogSize; i++) {
//                            int start = i * maxLogSize;
//                            int end = (i+1) * maxLogSize;
//                            end = end > responseDecryptedSchemeKey.length() ? responseDecryptedSchemeKey.length() : end;
//                            Log.v("schemeAll", responseDecryptedSchemeKey.substring(start, end));
//                     }
            }
            if ("WorkListOptional".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListOptionalS(jsonObject.getJSONArray(AppConstant.JSON_DATA));
//                    Utils.showAlert(this, "Your Data will be Downloaded");
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    workListInsert = false;
                    Utils.showAlert(this, "No Projects Found! for your selected items");
                }
                Log.d("responseWorkList", "" + responseDecryptedKey);

            }
            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {

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
                            villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
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
            loadOfflineVillgeListDBValues();
        }
    }

    private void loadSchemeList(JSONArray jsonArray) {
        try {
            db.delete(SCHEME_TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressHUD = ProgressHUD.show(this, "Downloading...", true, false, null);

        try {


            for (int i = 0; i < jsonArray.length(); i++) {
                String schemeSequentialID = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_SEQUENTIAL_ID);
                String schemeName = jsonArray.getJSONObject(i).getString(AppConstant.SCHEME_NAME);
                String fin_year = jsonArray.getJSONObject(i).getString("fin_year");

                ContentValues schemeListLocalDbValues = new ContentValues();
                schemeListLocalDbValues.put(AppConstant.SCHEME_SEQUENTIAL_ID, schemeSequentialID);
                schemeListLocalDbValues.put(AppConstant.SCHEME_NAME, schemeName);
                schemeListLocalDbValues.put(AppConstant.FINANCIAL_YEAR, fin_year);

                db.insert(SCHEME_TABLE_NAME, null, schemeListLocalDbValues);
                Log.d("LocalDBSchemeList", "" + schemeListLocalDbValues);

            }
            loadOfflineSchemeListDBValues();
        } catch (JSONException j) {
            j.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
        }
        if (progressHUD != null) {
            progressHUD.cancel();
        }
    }


    public void loadOfflineSchemeListDBValues() {
        String query = "SELECT distinct scheme_name,scheme_seq_id FROM " + SCHEME_TABLE_NAME + " Where fin_year in " + prefManager.getFinYearJson().toString().replace("[", "(").replace("]", ")") + " order by LTRIM(scheme_name) asc";
        Cursor SchemeList = getRawEvents(query, null);
        Log.d("SchemeQuery", "" + query);

        Scheme.clear();
        final ArrayList<String> mySchemelist = new ArrayList<>();
        final ArrayList<String> mySchemeCodelist = new ArrayList<>();


        if (SchemeList.getCount() > 0) {
            if (SchemeList.moveToFirst()) {
                do {
                    ModelClass schemeList = new ModelClass();
                    String schemeSequentialID = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_SEQUENTIAL_ID));
                    String schemeName = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.SCHEME_NAME));
                  //  String fin_year = SchemeList.getString(SchemeList.getColumnIndexOrThrow(AppConstant.FINANCIAL_YEAR));
                    schemeList.setSchemeSequentialID(schemeSequentialID);
                    schemeList.setSchemeName(schemeName);
                   // schemeList.setFinancialYear(fin_year);
                    Scheme.add(schemeList);

                } while (SchemeList.moveToNext());
            }
        }
        for (int i = 0; i < Scheme.size(); i++) {
            mySchemelist.add(Scheme.get(i).getSchemeName());
            mySchemeCodelist.add(Scheme.get(i).getSchemeSequentialID());
        }
        schemeStrings = mySchemelist.toArray(new String[mySchemelist.size()]);
        schemeCodeStrings = mySchemeCodelist.toArray(new String[mySchemeCodelist.size()]);
        schemeCheckedItems = new boolean[schemeStrings.length];
    }

    public void schemeCheckbox() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DownloadActivity.this);
        mBuilder.setTitle(R.string.scheme_dialog_title);
        mBuilder.setMultiChoiceItems(schemeStrings, schemeCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!mSchemeItems.contains(position)) {
                        mSchemeItems.add(position);
                    }
                } else if (mSchemeItems.contains(position)) {
                    mSchemeItems.remove((Integer.valueOf(position)));
                }
                JSONArray SchemeSeqIdJsonArray = new JSONArray();

                for (int i = 0; i < mSchemeItems.size(); i++) {
                    SchemeSeqIdJsonArray.put(schemeCodeStrings[mSchemeItems.get(i)]);
                }
                prefManager.setSchemeSeqIdJson(SchemeSeqIdJsonArray);
                Log.d("schemeSeqId", "" + SchemeSeqIdJsonArray);

//                if (isChecked) {
//                    mVillageItems.add(position);
//                } else {
//                    mVillageItems.remove((Integer.valueOf(position)));
//                }
            }
        });

        mBuilder.setCancelable(false);
        final String[] finalschemeStrings = schemeStrings;
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mSchemeItems.size(); i++) {
                    item = item + finalschemeStrings[mSchemeItems.get(i)];
                    if (i != mSchemeItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                if(mSchemeItems.size() > 0) {
                    select_scheme_layout.setVisibility(View.VISIBLE);
                }else {
                    select_scheme_layout.setVisibility(View.GONE);
                }
                view.setVisibility(View.GONE);
                selected_scheme_tv.setText(item);
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
                for (int i = 0; i < schemeCheckedItems.length; i++) {
                    schemeCheckedItems[i] = false;
                    mSchemeItems.clear();
                    selected_scheme_tv.setText("");
                    select_scheme_layout.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        if(mFinYearItems.size() > 0) {
            mDialog.show();
        }
        else {
            Utils.showAlert(this,"Please Select Financial Year!");
            select_scheme_layout.setVisibility(View.GONE);
        }

    }

    private void workListOptionalS(JSONArray jsonArray) {
        try {
            dbData.open();
            dbData.deleteWorkListTable();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {

            if (jsonArray.length() > 0) {
                workListInsert = true;
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
                    String is_high_value = jsonArray.getJSONObject(i).getString("is_high_value");

                    ModelClass modelClass = new ModelClass();
                    modelClass.setDistictCode(dcode);
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
                    modelClass.setIs_high_value(is_high_value);

                    dbData.Insert_workList("offline",modelClass);

                }
                callAlert();

            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Financial Year");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }


    public void callAlert() {
        if (workListInsert){
            Utils.showAlert(this, "Your Data Downloaded Successfully!");
            workListInsert = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openWorkListScreen();
                }
            }, 1000);

        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    public void openWorkListScreen() {
        Intent intent = new Intent(this, WorkList.class);
        intent.putExtra("OnOffType","offline");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public Cursor getRawEvents(String sql, String string) {
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public void dashboard() {
        Intent intent = new Intent(this, MainHomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }




}
