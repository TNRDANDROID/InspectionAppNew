package com.nic.samathuvapuram.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.activity.FullImageActivity;
import com.nic.samathuvapuram.activity.InfraListActivity;
import com.nic.samathuvapuram.activity.PendingScreen;
import com.nic.samathuvapuram.activity.SaveAeHouseDetailsActivity;
import com.nic.samathuvapuram.activity.SaveAeInfraDetailsActivity;
import com.nic.samathuvapuram.activity.SaveBlkHouseDetailsActivity;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.InfraListAdapterBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class InfraListAdapter extends RecyclerView.Adapter<InfraListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    static JSONObject dataset = new JSONObject();
    private final com.nic.samathuvapuram.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;




    public InfraListAdapter(Activity context, ArrayList<ModelClass> pendingListValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.list = pendingListValues;
    }

    @Override
    public InfraListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        InfraListAdapterBinding infraListAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.infra_list_adapter, viewGroup, false);
        return new InfraListAdapter.MyViewHolder(infraListAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private InfraListAdapterBinding infraListAdapterBinding;

        public MyViewHolder(InfraListAdapterBinding Binding) {
            super(Binding.getRoot());
            infraListAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.infraListAdapterBinding.repairInfraEstimateId.setText(String.valueOf(list.get(position).getRepair_infra_estimate_id()));
        holder.infraListAdapterBinding.conditionOfInfra.setText(list.get(position).getCondition_of_infra());
        holder.infraListAdapterBinding.schemeName.setText(list.get(position).getScheme_name());
        holder.infraListAdapterBinding.workName.setText(list.get(position).getWork_name());


/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, SaveAeInfraDetailsActivity.class);
                    intent.putExtra("samathuvapuram_id",list.get(position).getSamathuvapuram_id());
                    intent.putExtra("repair_infra_estimate_id",list.get(position).getRepair_infra_estimate_id());
                intent.putExtra("condition_of_infra_id", list.get(position).getCondition_of_infra_id());
                intent.putExtra("scheme_group_id", list.get(position).getScheme_group_id());
                intent.putExtra("scheme_id", list.get(position).getScheme_id());
                intent.putExtra("work_group_id", list.get(position).getWork_group_id());
                intent.putExtra("work_type_id", list.get(position).getWork_type_id());
                intent.putExtra("estimate_cost_required", list.get(position).getEstimate_cost_required());
                intent.putExtra("condition_of_infra", list.get(position).getCondition_of_infra());
                    context.startActivity(intent);

            }
        });
*/

        holder.infraListAdapterBinding.viewOnlineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOnlineImages(String.valueOf(list.get(position).getSamathuvapuram_id()),String.valueOf(list.get(position).getRepair_infra_estimate_id()));

            }
        });
        holder.infraListAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isOnline()) {
                    ((InfraListActivity)context).delete(list.get(position).getSamathuvapuram_id(),list.get(position).getRepair_infra_estimate_id());
                } else {
                    Activity activity = (Activity) context;
                    Utils.showAlert(activity, "Turn On Mobile Data To Utpload");
                }
            }
        });



    }

    public void viewOnlineImages(String samathuvapuram_id,String repairInfraEstimateId) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("samathuvapuram_id",samathuvapuram_id);
        intent.putExtra("repair_infra_estimate_id",repairInfraEstimateId);
        intent.putExtra("Type","INFRA");
        intent.putExtra("OnOffType","Online");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

}
