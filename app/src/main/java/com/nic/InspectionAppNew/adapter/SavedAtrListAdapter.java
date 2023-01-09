package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.nic.InspectionAppNew.R;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.activity.SaveATRWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.ViewActionScreen;
import com.nic.InspectionAppNew.activity.ViewSavedAtrList;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import com.nic.InspectionAppNew.databinding.AtrWorkListItemViewBinding;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SavedAtrListAdapter extends  RecyclerView.Adapter<SavedAtrListAdapter.MyViewHolder> implements Api.ServerResponseListener, Filterable {

    private Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> listFilteredValue;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;
    String type;

    public SavedAtrListAdapter(Activity context, ArrayList<ModelClass> listValues,String type) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.listFilteredValue = listValues;
        this.list = listValues;
        this.type = type;
    }

    @Override
    public SavedAtrListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AtrWorkListItemViewBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.atr_work_list_item_view, viewGroup, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("ATRWorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    atrWorkListOptionalS(jsonObject);
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
        private AtrWorkListItemViewBinding binding;

        public MyViewHolder(AtrWorkListItemViewBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final SavedAtrListAdapter.MyViewHolder holder, final int position) {

        holder.binding.workId.setText(String.valueOf(listFilteredValue.get(position).getWork_id()));
        holder.binding.workName.setText(String.valueOf(listFilteredValue.get(position).getWork_name()));
        holder.binding.actionTakenDate.setText(String.valueOf(listFilteredValue.get(position).getAction_taken_date()));
        if(Utils.compareDate(String.valueOf(listFilteredValue.get(position).getAction_taken_date()))){
            holder.binding.edit.setVisibility(View.VISIBLE);
        }else {
            holder.binding.edit.setVisibility(View.GONE);
        }

        if(String.valueOf(listFilteredValue.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(listFilteredValue.get(position).getWork_name()), holder.binding.workName, 2);
        }

        holder.binding.viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    ((ViewSavedAtrList)context).getWorkReportDetails(String.valueOf( listFilteredValue.get(position).getWork_id()), listFilteredValue.get(position).getInspection_id(), listFilteredValue.get(position).getAction_taken_id());

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    Intent intent = new Intent(context, ViewActionScreen.class);
                    intent.putExtra("work_id", listFilteredValue.get(position).getWork_id());
                    intent.putExtra("inspection_id", listFilteredValue.get(position).getInspection_id());
                    intent.putExtra("action_taken_id", listFilteredValue.get(position).getAction_taken_id());
                    intent.putExtra("type", "atr");
                    context.startActivity(intent);

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }

            }
        });

        holder.binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isOnline()){
                    getATRWorkDetails(listFilteredValue.get(position).getWork_id(), listFilteredValue.get(position).getInspection_id(), listFilteredValue.get(position).getAction_taken_id());

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }

            }
        });



    }

    private void getATRWorkDetails(int work_id, String inspection_id, String action_taken_id) {
        try {
            new ApiService(context).makeJSONObjectRequest("ATRWorkDetails", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(work_id,inspection_id,action_taken_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workDetailsJsonParams(int work_id, String inspection_id, String action_taken_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), context.getResources().getString(R.string.init_vector), workDetailsParams(context,work_id,inspection_id,action_taken_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("ATRWorkDetails", "" + dataSet);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity,int work_id, String inspection_id, String action_taken_id) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "work_id_wise_inspection_action_taken_details_view");
        dataSet.put("action_taken_id", action_taken_id);
        dataSet.put("work_id", work_id);
        dataSet.put("inspection_id", inspection_id);

        Log.d("ATRWorkDetails", "" + dataSet);
        return dataSet;
    }


    private void atrWorkListOptionalS(JSONObject object) {
        try {
            JSONArray jsonArray=object.getJSONArray(AppConstant.JSON_DATA);
            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String bCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String work_id = jsonArray.getJSONObject(i).getString("workid");
                    String inspection_id = jsonArray.getJSONObject(i).getString("inspection_id");
                    String action_taken_date = jsonArray.getJSONObject(i).getString("action_taken_date");
                    String action_taken_id = jsonArray.getJSONObject(i).getString("action_taken_id");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String work_name = jsonArray.getJSONObject(i).getString("work_name");

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
                    Intent intent = new Intent(context, SaveATRWorkDetailsActivity.class);
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

                    intent.putExtra("type","atr");
                    intent.putExtra("flag","edit");
                    intent.putExtra("inspection_id",inspection_id);
                    intent.putExtra("action_taken_id",action_taken_id);
                    intent.putExtra("status_id",0);
                    intent.putExtra("status","");
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFilteredValue = list;
                } else {
                    ArrayList<ModelClass> filteredList = new ArrayList<>();
                    for (ModelClass row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if(type.equalsIgnoreCase("rdpr")){
                            if (
                                    row.getWork_name().toLowerCase().contains(charString.toLowerCase()) ||
                                            row.getWork_name().toLowerCase().contains(charString.toUpperCase())||
                                            String.valueOf(row.getWork_id()).toLowerCase().contains(charString.toUpperCase())
                            ) {
                                filteredList.add(row);
                            }

                        }else {
                            if (
                                    row.getOther_work_detail().toLowerCase().contains(charString.toLowerCase()) ||
                                            row.getOther_work_detail().toLowerCase().contains(charString.toUpperCase())||
                                            String.valueOf(row.getOther_work_inspection_id()).toLowerCase().contains(charString.toUpperCase())
                            ) {
                                filteredList.add(row);
                            }

                        }
                    }

                    listFilteredValue = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFilteredValue;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFilteredValue = (ArrayList<ModelClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return listFilteredValue.size();
    }
}
