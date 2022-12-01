package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.ViewActionScreen;
import com.nic.InspectionAppNew.activity.ViewSavedOtherWorkList;
import com.nic.InspectionAppNew.activity.ViewSavedWorkList;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.databinding.SavedWorkListItemViewBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SavedWorkListAdapter extends RecyclerView.Adapter<SavedWorkListAdapter.MyViewHolder> implements Api.ServerResponseListener{

    private  Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;
    String type;


    public SavedWorkListAdapter(Activity context, ArrayList<ModelClass> listValues,String type) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
        this.type = type;
    }

    @Override
    public SavedWorkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SavedWorkListItemViewBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.saved_work_list_item_view, viewGroup, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("OtherWorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    otherWorkListOptionalS(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(context, jsonObject.getString("RESPONSE"));
                }
                Log.d("responseOtherWorkList", "" + responseObj.toString());
                Log.d("responseOtherWorkList", "" + responseDecryptedKey);

            }
            if ("WorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    rdprWorkListOptionalS(jsonObject);
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(context, jsonObject.getString("RESPONSE"));
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


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SavedWorkListItemViewBinding binding;

        public MyViewHolder(SavedWorkListItemViewBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final SavedWorkListAdapter.MyViewHolder holder, final int position) {


        if(type.equalsIgnoreCase("rdpr")){
            holder.binding.otherWorkNameLayout.setVisibility(View.GONE);
            holder.binding.otherWorkCategoryNameLayout.setVisibility(View.GONE);
            holder.binding.workNameLayout.setVisibility(View.VISIBLE);
            holder.binding.workH.setText(context.getResources().getString(R.string.work_id));
            holder.binding.workId.setText(String.valueOf(list.get(position).getWork_id()));
            holder.binding.workName.setText(String.valueOf(list.get(position).getWork_name()));
            holder.binding.inspectedDate.setText(String.valueOf(list.get(position).getInspectedDate()));
            holder.binding.status.setText(String.valueOf(list.get(position).getWork_status()));
            if(Utils.compareDate(String.valueOf(list.get(position).getInspectedDate()))){
                holder.binding.edit.setVisibility(View.VISIBLE);
            }else {
                holder.binding.edit.setVisibility(View.GONE);
            }

        }
        else {
            holder.binding.otherWorkNameLayout.setVisibility(View.VISIBLE);
            holder.binding.otherWorkCategoryNameLayout.setVisibility(View.VISIBLE);
            holder.binding.edit.setVisibility(View.VISIBLE);
            holder.binding.workNameLayout.setVisibility(View.GONE);
            holder.binding.workH.setText(context.getResources().getString(R.string.other_work_id));
            holder.binding.workId.setText(String.valueOf(list.get(position).getOther_work_inspection_id()));
            holder.binding.otherWorkName.setText(String.valueOf(list.get(position).getOther_work_detail()));
            holder.binding.otherWorkCategoryName.setText(String.valueOf(list.get(position).getOther_work_category_name()));
            holder.binding.inspectedDate.setText(String.valueOf(list.get(position).getInspectedDate()));
            holder.binding.status.setText(String.valueOf(list.get(position).getWork_status()));
        }


        if(String.valueOf(list.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(list.get(position).getWork_name()), holder.binding.workName, 2);
        }

        holder.binding.viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    if(type.equalsIgnoreCase("rdpr")){
                        ((ViewSavedWorkList)context).getWorkReportDetails(String.valueOf( list.get(position).getWork_id()),list.get(position).getInspection_id());

                    }else {
                        ((ViewSavedOtherWorkList)context).getWorkReportDetails(String.valueOf( ""),list.get(position).getOther_work_inspection_id());

                    }

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    if(type.equalsIgnoreCase("rdpr")){
                        Intent intent = new Intent(context, ViewActionScreen.class);
                        intent.putExtra("work_id", list.get(position).getWork_id());
                        intent.putExtra("inspection_id", list.get(position).getInspection_id());
                        intent.putExtra("type", "rdpr");
                        context.startActivity(intent);

                    }else {
                        Intent intent = new Intent(context, ViewActionScreen.class);
                        intent.putExtra("type", "other_work");
                        intent.putExtra("other_work_inspection_id", list.get(position).getOther_work_inspection_id());
                        context.startActivity(intent);

                    }

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }

            }
        });

        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline()){
                    if(type.equalsIgnoreCase("rdpr")){
                        getRdprWorkDetails(list.get(position).getWork_id(),list.get(position).getInspection_id());
                    }else {
                        getOtherWorkDetails(list.get(position).getOther_work_inspection_id());
                    }
                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }

            }
        });



    }

    private void getRdprWorkDetails(int work_id, String inspection_id) {
        try {
            new ApiService(context).makeJSONObjectRequest("WorkDetails", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(work_id,inspection_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workDetailsJsonParams(int work_id, String inspection_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), context.getResources().getString(R.string.init_vector), workDetailsParams(context,work_id,inspection_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity,int work_id, String inspection_id) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "work_id_wise_inspection_details_view");
        dataSet.put("work_id", work_id);
        dataSet.put("inspection_id", inspection_id);

        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }

    public void getOtherWorkDetails(String other_work_inspection_id) {
        try {
            new ApiService(context).makeJSONObjectRequest("OtherWorkDetails", Api.Method.POST, UrlGenerator.getMainService(), otherworkDetailsJsonParams(other_work_inspection_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject otherworkDetailsJsonParams(String other_work_inspection_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), context.getResources().getString(R.string.init_vector), otherworkDetailsParams(context,other_work_inspection_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("OtherWorkDetails", "" + dataSet);
        return dataSet;
    }
    public  JSONObject otherworkDetailsParams(Activity activity,String other_work_inspection_id) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "other_inspection_details_view");
        dataSet.put("other_work_inspection_id", other_work_inspection_id);

        Log.d("OtherWorkDetails", "" + dataSet);
        return dataSet;
    }

    private void otherWorkListOptionalS(JSONObject object) {
        try {
            JSONArray jsonArray=object.getJSONArray(AppConstant.JSON_DATA);
            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    String statecode = jsonArray.getJSONObject(i).getString(AppConstant.STATE_CODE_);
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String bCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String other_work_inspection_id = jsonArray.getJSONObject(i).getString("other_work_inspection_id");
                    String inspection_date = jsonArray.getJSONObject(i).getString("inspection_date");
                    String status_id = jsonArray.getJSONObject(i).getString("status_id");
                    String status = jsonArray.getJSONObject(i).getString("status");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String other_work_category_id = jsonArray.getJSONObject(i).getString("other_work_category_id");
                    String other_work_category_name = jsonArray.getJSONObject(i).getString("other_work_category_name");
                    String other_work_detail = jsonArray.getJSONObject(i).getString("other_work_detail");
                    String fin_year = jsonArray.getJSONObject(i).getString("fin_year");
                    ArrayList<ModelClass> activityImage = new ArrayList<>();
                    JSONArray imgarray=new JSONArray();
                    imgarray=jsonArray.getJSONObject(i).getJSONArray("inspection_image");
                    if(imgarray.length() > 0){

                        for(int j = 0; j < imgarray.length(); j++ ) {
                            try {
                                ModelClass imageOnline = new ModelClass();
                                imageOnline.setDescription(imgarray.getJSONObject(j).getString("image_description"));
                                if (!(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase("null") ||
                                        imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase(""))) {
                                    byte[] decodedString = Base64.decode(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageOnline.setImage(decodedByte);
                                    activityImage.add(imageOnline);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    prefManager.setImageJson(imgarray);
                    Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                    intent.putExtra("scode", statecode);
                    intent.putExtra("dcode", dcode);
                    intent.putExtra("bcode", bCode);
                    intent.putExtra("pvcode", pvcode);
                    intent.putExtra("fin_year", fin_year);
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

                    intent.putExtra("type","other");
                    intent.putExtra("flag","edit");
                    intent.putExtra("other_work_inspection_id",other_work_inspection_id);
                    intent.putExtra("other_work_category_name",other_work_category_name);
                    intent.putExtra("status_id",Integer.parseInt(status_id));
                    intent.putExtra("status",status);
                    intent.putExtra("other_work_detail",other_work_detail);
                    intent.putExtra("description",description);
                   /* intent.putExtra("activityImage",object.toString());
                    Bundle b = new Bundle();
                    b.putString("activityImage",object.toString());
                    intent.putExtras(b);*/
                    context.startActivity(intent);

                }


            } else {
                Utils.showAlert(context, "No Record Found for Corresponding Work");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }
    private void rdprWorkListOptionalS(JSONObject object) {
        try {
            JSONArray jsonArray=object.getJSONArray(AppConstant.JSON_DATA);
            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String bCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String inspection_id = jsonArray.getJSONObject(i).getString("inspection_id");
                    String inspection_date = jsonArray.getJSONObject(i).getString("inspection_date");
                    String status_id = jsonArray.getJSONObject(i).getString("status_id");
                    String status = jsonArray.getJSONObject(i).getString("status");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String work_name = jsonArray.getJSONObject(i).getString("work_name");
                    String work_id = jsonArray.getJSONObject(i).getString("work_id");

                    ArrayList<ModelClass> activityImage = new ArrayList<>();
                    JSONArray imgarray=new JSONArray();
                    imgarray=jsonArray.getJSONObject(i).getJSONArray("inspection_image");
                    if(imgarray.length() > 0){

                        for(int j = 0; j < imgarray.length(); j++ ) {
                            try {
                                ModelClass imageOnline = new ModelClass();
                                imageOnline.setDescription(imgarray.getJSONObject(j).getString("image_description"));
                                if (!(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase("null") ||
                                        imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE).equalsIgnoreCase(""))) {
                                    byte[] decodedString = Base64.decode(imgarray.getJSONObject(j).getString(AppConstant.KEY_IMAGE), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageOnline.setImage(decodedByte);

                                    activityImage.add(imageOnline);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                    prefManager.setImageJson(imgarray);
                    Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                    intent.putExtra("scode", "");
                    intent.putExtra("dcode", dcode);
                    intent.putExtra("bcode", bCode);
                    intent.putExtra("pvcode", pvcode);
                    intent.putExtra("fin_year", "");
                    intent.putExtra("hab_code", "");
                    intent.putExtra("scheme_group_id", "");
                    intent.putExtra("work_group_id", "");
                    intent.putExtra("work_type_id", "");
                    intent.putExtra("scheme_id", "");
                    intent.putExtra("work_id", Integer.valueOf(work_id));
                    intent.putExtra("work_name", "");
                    intent.putExtra("as_value", "");
                    intent.putExtra("ts_value", "");
                    intent.putExtra("current_stage_of_work", "");
                    intent.putExtra("is_high_value", "");
                    intent.putExtra("onOffType",prefManager.getOnOffType());
                    intent.putExtra("other_work_category_id","");

                    intent.putExtra("type","rdpr");
                    intent.putExtra("flag","edit");
                    intent.putExtra("inspection_id",inspection_id);
                    intent.putExtra("status_id",Integer.parseInt(status_id));
                    intent.putExtra("status",status);
                    intent.putExtra("work_name",work_name);
                    intent.putExtra("description",description);
                   /* intent.putExtra("activityImage",object.toString());
                    Bundle b = new Bundle();
                    b.putString("activityImage",object.toString());
                    intent.putExtras(b);*/
                    context.startActivity(intent);

                }


            } else {
                Utils.showAlert(context, "No Record Found for Corresponding Work");
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
