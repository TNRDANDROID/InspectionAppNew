package com.nic.samathuvapuram.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.adapter.AeScreenAdapter;
import com.nic.samathuvapuram.adapter.CommonAdapter;
import com.nic.samathuvapuram.adapter.HouseListAdapter;
import com.nic.samathuvapuram.adapter.PendingScreenAdapter;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ApiService;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.AeScreenBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.ProgressHUD;
import com.nic.samathuvapuram.utils.UrlGenerator;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AEScreen extends AppCompatActivity implements Api.ServerResponseListener {
    private AeScreenBinding binding;
    private RecyclerView recyclerView;
    private PrefManager prefManager;
    private SQLiteDatabase db;
    public static DBHelper dbHelper;
    public com.nic.samathuvapuram.dataBase.dbData dbData = new dbData(this);
    private AeScreenAdapter adapter;
    private List<ModelClass> estimate_type_list = new ArrayList<>();
    int samathuvapuram_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.ae_screen);
        binding.setActivity(this);
        setSupportActionBar(binding.toolbar);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        prefManager = new PrefManager(this);
        samathuvapuram_id = getIntent().getIntExtra("samathuvapuram_id",0);
        estimate_type_list = new ArrayList<>();
        adapter = new AeScreenAdapter(AEScreen.this, (ArrayList<ModelClass>) estimate_type_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView = binding.recycler;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        estimateTypeFilterSpinner();

    }


    public void estimateTypeFilterSpinner() {
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.ESTIMATE_TYPE , null);

        estimate_type_list.clear();

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ModelClass modelClass = new ModelClass();
                    int estimate_type_id = cursor.getInt(cursor.getColumnIndexOrThrow("estimate_type_id"));
                    String estimate_type_name = cursor.getString(cursor.getColumnIndexOrThrow("estimate_type_name"));

                    modelClass.setEstimate_type_id(estimate_type_id);
                    modelClass.setEstimate_type_name(estimate_type_name);
                    modelClass.setSamathuvapuram_id(samathuvapuram_id);

                    estimate_type_list.add(modelClass);
                } while (cursor.moveToNext());
            }
            Log.d("estimate_type_listsize", "" + estimate_type_list.size());

        }
        if (estimate_type_list.size() > 0) {
            binding.recycler.setVisibility(View.VISIBLE);
            binding.notFoundTv.setVisibility(View.GONE);
            adapter = new AeScreenAdapter(AEScreen.this, (ArrayList<ModelClass>) estimate_type_list);
            binding.recycler.setAdapter(adapter);
        }else {
            binding.recycler.setVisibility(View.GONE);
            binding.notFoundTv.setVisibility(View.VISIBLE);
        }

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
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    @Override
    public void OnError(VolleyError volleyError) {

    }
}
