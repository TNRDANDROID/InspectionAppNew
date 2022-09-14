package com.nic.samathuvapuram.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.samathuvapuram.Fragment.SlideshowDialogFragment;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.adapter.FullImageAdapter;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ApiService;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.FullImageRecyclerBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.utils.UrlGenerator;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private FullImageRecyclerBinding fullImageRecyclerBinding;
    public String OnOffType,samathuvapuram_id,house_serial_number,repairInfraEstimateId,Type
            ,scheme_group_id,scheme_id,work_group_id,work_type_id;
    private FullImageAdapter fullImageAdapter;
    private PrefManager prefManager;
    private static  ArrayList<ModelClass> activityImage = new ArrayList<>();
    private com.nic.samathuvapuram.dataBase.dbData dbData = new dbData(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullImageRecyclerBinding = DataBindingUtil.setContentView(this, R.layout.full_image_recycler);
        fullImageRecyclerBinding.setActivity(this);
        prefManager = new PrefManager(this);
        OnOffType = getIntent().getStringExtra("OnOffType");
        samathuvapuram_id = getIntent().getStringExtra("samathuvapuram_id");

        Type = getIntent().getStringExtra("Type");

        if(Type.equals("INFRA")){
            if(OnOffType.equals("Online")){
                repairInfraEstimateId = getIntent().getStringExtra("repairInfraEstimateId");
            }else {
                scheme_group_id = getIntent().getStringExtra("scheme_group_id");
                scheme_id = getIntent().getStringExtra("scheme_id");
                work_group_id = getIntent().getStringExtra("work_group_id");
                work_type_id = getIntent().getStringExtra("work_type_id");
            }
        }else {
            house_serial_number = getIntent().getStringExtra("house_serial_number");
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setLayoutManager(mLayoutManager);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setItemAnimator(new DefaultItemAnimator());
        fullImageRecyclerBinding.imagePreviewRecyclerview.setHasFixedSize(true);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setNestedScrollingEnabled(false);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setFocusable(false);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setAdapter(fullImageAdapter);

        if(OnOffType.equalsIgnoreCase("Online")) {
            if (Utils.isOnline()) {
                getOnlineImage();
            }
        }
        else {
            new fetchImagetask().execute();
        }

    }
    public class fetchImagetask extends AsyncTask<Void, Void,
            ArrayList<ModelClass>> {
        @Override
        protected ArrayList<ModelClass> doInBackground(Void... params) {

            final String dcode = prefManager.getDistrictCode();
            final String bcode = prefManager.getBlockCode();
            final String pvcode = prefManager.getPvCode();
            if(OnOffType.equalsIgnoreCase("Offline")){
                dbData.open();
                activityImage = new ArrayList<>();
                if(Type.equals("INFRA")){
                    activityImage = dbData.getParticularSavedInfraImage(samathuvapuram_id,scheme_group_id,scheme_id,work_group_id,work_type_id);
                }else {
                activityImage = dbData.getParticularSavedHouseImage(samathuvapuram_id,house_serial_number);
            }
            }

            Log.d("IMAGE_COUNT", String.valueOf(activityImage.size()));
            return activityImage;
        }

        @Override
        protected void onPostExecute(final ArrayList<ModelClass> imageList) {
            super.onPostExecute(imageList);
            setAdapter();
        }
    }
    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onClick(View view) {

    }

    public void getOnlineImage() {
        try {
            new ApiService(this).makeJSONObjectRequest("OnlineImage", Api.Method.POST, UrlGenerator.getMainService(), ImagesJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject ImagesJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), ImagesListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("utils_ImageEncrydataSet", "" + authKey);
        return dataSet;
    }

    public JSONObject ImagesListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();

        if(Type.equals("INFRA")){
            dataSet.put(AppConstant.KEY_SERVICE_ID, "blk_ae_samathuvapuram_list_of_infra_photos");
            dataSet.put("samathuvapuram_id", getIntent().getStringExtra("samathuvapuram_id"));
            dataSet.put("repair_infra_estimate_id", getIntent().getStringExtra("repair_infra_estimate_id"));
        }else {
            if(prefManager.getUsertype().equals("bdo")){
                dataSet.put(AppConstant.KEY_SERVICE_ID, "blk_bdo_samathuvapuram_list_of_house_photos");
                dataSet.put("samathuvapuram_id", getIntent().getStringExtra("samathuvapuram_id"));
                dataSet.put("house_serial_number", getIntent().getStringExtra("house_serial_number"));
            }else if(prefManager.getUsertype().equals("ae")){
                dataSet.put(AppConstant.KEY_SERVICE_ID, "blk_ae_samathuvapuram_list_of_house_photos");
                dataSet.put("samathuvapuram_id", getIntent().getStringExtra("samathuvapuram_id"));
                dataSet.put("house_serial_number", getIntent().getStringExtra("house_serial_number"));
            }
        }

        Log.d("utils_imageDataset", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("OnlineImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    generateImageArrayList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    Log.d("Length", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA).length());
                }
                Log.d("resp_OnlineImage", "" + responseDecryptedBlockKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void generateImageArrayList(JSONArray jsonArray){
        if(jsonArray.length() > 0){
            activityImage = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++ ) {
                try {
                    ModelClass imageOnline = new ModelClass();

                      if (!(jsonArray.getJSONObject(i).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase("null") || jsonArray.getJSONObject(i).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase(""))) {
                        byte[] decodedString = Base64.decode(jsonArray.getJSONObject(i).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageOnline.setImage(decodedByte);
                        activityImage.add(imageOnline);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            setAdapter();
        }
    }
    public void setAdapter(){
        fullImageAdapter = new FullImageAdapter(FullImageActivity.this,
                activityImage, dbData,OnOffType);
        fullImageRecyclerBinding.imagePreviewRecyclerview.addOnItemTouchListener(new FullImageAdapter.RecyclerTouchListener(getApplicationContext(), fullImageRecyclerBinding.imagePreviewRecyclerview, new FullImageAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", activityImage);
                bundle.putInt("position", position);
                bundle.putString("OnOffType", OnOffType);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        fullImageRecyclerBinding.imagePreviewRecyclerview.setAdapter(fullImageAdapter);
    }
    @Override
    public void OnError(VolleyError volleyError) {

    }
}
