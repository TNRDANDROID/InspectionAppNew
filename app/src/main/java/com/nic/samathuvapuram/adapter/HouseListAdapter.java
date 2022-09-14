package com.nic.samathuvapuram.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.activity.FullImageActivity;
import com.nic.samathuvapuram.activity.HouseListActivity;
import com.nic.samathuvapuram.activity.SaveAeHouseDetailsActivity;
import com.nic.samathuvapuram.activity.SaveBlkHouseDetailsActivity;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.HouseListAdapterBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HouseListAdapter extends RecyclerView.Adapter<HouseListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    static JSONObject dataset = new JSONObject();
    private final com.nic.samathuvapuram.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;



    public HouseListAdapter(Activity context, ArrayList<ModelClass> pendingListValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.list = pendingListValues;
    }

    @Override
    public HouseListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        HouseListAdapterBinding houseListAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.house_list_adapter, viewGroup, false);
        return new HouseListAdapter.MyViewHolder(houseListAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private HouseListAdapterBinding houseListAdapterBinding;

        public MyViewHolder(HouseListAdapterBinding Binding) {
            super(Binding.getRoot());
            houseListAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        if(prefManager.getUsertype().equals("bdo")){
            holder.houseListAdapterBinding.houseSerialNumber.setText(String.valueOf(list.get(position).getHouse_serial_number()));
            holder.houseListAdapterBinding.currentHouseUsage.setText(list.get(position).getCurrent_usage());
            holder.houseListAdapterBinding.currentNameOfTheBeneficiary.setText(list.get(position).getCurrent_name_of_the_beneficiary());

            holder.houseListAdapterBinding.currentHouseUsageLayout.setVisibility(View.VISIBLE);
            holder.houseListAdapterBinding.estimateCostRequiredLayout.setVisibility(View.GONE);
            holder.houseListAdapterBinding.conditionOfHouseLayout.setVisibility(View.GONE);

        }else if(prefManager.getUsertype().equals("ae")){
            holder.houseListAdapterBinding.houseSerialNumber.setText(String.valueOf(list.get(position).getHouse_serial_number()));
            holder.houseListAdapterBinding.estimateCostRequired.setText(list.get(position).getEstimate_cost_required());
            holder.houseListAdapterBinding.conditionOfHouse.setText(list.get(position).getCondition_of_house());
            holder.houseListAdapterBinding.currentNameOfTheBeneficiary.setText(list.get(position).getName_of_the_beneficiary());

            holder.houseListAdapterBinding.currentHouseUsageLayout.setVisibility(View.GONE);
            holder.houseListAdapterBinding.estimateCostRequiredLayout.setVisibility(View.VISIBLE);
            holder.houseListAdapterBinding.conditionOfHouseLayout.setVisibility(View.VISIBLE);

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (prefManager.getUsertype().equals("bdo")) {
                        Intent intent = new Intent(context, SaveBlkHouseDetailsActivity.class);
                        intent.putExtra("samathuvapuram_id", list.get(position).getSamathuvapuram_id());
                        intent.putExtra("house_serial_number", list.get(position).getHouse_serial_number());
                        intent.putExtra("current_name_of_the_beneficiary", Utils.notNullString(list.get(position).getCurrent_name_of_the_beneficiary()));
                        intent.putExtra("is_house_owned_by_sanctioned_beneficiary", Utils.notNullString(list.get(position).getIs_house_owned_by_sanctioned_beneficiary()));
                        try {
                            intent.putExtra("current_community_category_id", Utils.notNullinteger(Integer.parseInt(Utils.notNullString(list.get(position).getCurrent_community_category_id()))));
                        }catch (NumberFormatException e){
                            intent.putExtra("current_community_category_id", 0);
                            e.printStackTrace();
                        }
                        try {
                            intent.putExtra("current_house_usage",Utils.notNullinteger( Integer.parseInt(Utils.notNullString(list.get(position).getCurrent_house_usage()))));
                        }catch (NumberFormatException e){
                            intent.putExtra("current_house_usage", 0);
                            e.printStackTrace();
                        }
                        intent.putExtra("current_gender", Utils.notNullString(list.get(position).getCurrent_gender()));
                        intent.putExtra("current_usage", Utils.notNullString(list.get(position).getCurrent_usage()));
                        context.startActivity(intent);
                    } else if (prefManager.getUsertype().equals("ae")) {
                        Intent intent = new Intent(context, SaveAeHouseDetailsActivity.class);
                        intent.putExtra("samathuvapuram_id", list.get(position).getSamathuvapuram_id());
                        intent.putExtra("house_serial_number", list.get(position).getHouse_serial_number());
                        intent.putExtra("condition_of_house_id", list.get(position).getCondition_of_house_id());
                        intent.putExtra("scheme_group_id", list.get(position).getScheme_group_id());
                        intent.putExtra("scheme_id", list.get(position).getScheme_id());
                        intent.putExtra("work_group_id", list.get(position).getWork_group_id());
                        intent.putExtra("work_type_id", list.get(position).getWork_type_id());
                        intent.putExtra("estimate_cost_required", list.get(position).getEstimate_cost_required());
                        intent.putExtra("condition_of_house", list.get(position).getCondition_of_house());
                        context.startActivity(intent);
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }

            }
        });

        holder.houseListAdapterBinding.viewOnlineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOnlineImages(String.valueOf(list.get(position).getSamathuvapuram_id()),String.valueOf(holder.houseListAdapterBinding.houseSerialNumber.getText().toString()));

            }
        });



    }

    public void viewOnlineImages(String samathuvapuram_id,String house_serial_number) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("samathuvapuram_id",samathuvapuram_id);
        intent.putExtra("house_serial_number",house_serial_number);
        intent.putExtra("Type","HOUSE");
        intent.putExtra("OnOffType","Online");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

}
