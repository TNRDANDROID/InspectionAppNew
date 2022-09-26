package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.ViewActionScreen;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.WorkListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private final com.nic.InspectionAppNew.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;

    public WorkListAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.list = listValues;
    }

    @Override
    public WorkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        WorkListAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.work_list_adapter, viewGroup, false);
        return new WorkListAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private WorkListAdapterBinding binding;

        public MyViewHolder(WorkListAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.binding.workId.setText(String.valueOf(list.get(position).getWork_id()));
        holder.binding.workName.setText(String.valueOf(list.get(position).getWork_name()));
//        holder.binding.workStatus.setText(String.valueOf(list.get(position).getWork_status()));
        holder.binding.finYear.setText(String.valueOf(list.get(position).getFinancialYear()));
        holder.binding.asValue.setText(String.valueOf(list.get(position).getAs_value()));
        holder.binding.tsValue.setText(String.valueOf(list.get(position).getTs_value()));

        if(String.valueOf(list.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(list.get(position).getWork_name()), holder.binding.workName, 0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                intent.putExtra("dcode", list.get(position).getDistictCode());
                intent.putExtra("bcode", list.get(position).getBlockCode());
                intent.putExtra("pvcode", list.get(position).getPvCode());
                intent.putExtra("hab_code", list.get(position).getHabCode());
                intent.putExtra("scheme_group_id", list.get(position).getScheme_group_id());
                intent.putExtra("work_group_id", list.get(position).getWork_group_id());
                intent.putExtra("work_type_id", list.get(position).getWork_type_id());
                intent.putExtra("scheme_id", list.get(position).getSchemeSequentialID());
                intent.putExtra("fin_year", list.get(position).getFinancialYear());
                intent.putExtra("work_id", list.get(position).getWork_id());
                intent.putExtra("work_name", list.get(position).getWork_name());
                intent.putExtra("as_value", list.get(position).getAs_value());
                intent.putExtra("ts_value", list.get(position).getTs_value());
                intent.putExtra("current_stage_of_work", list.get(position).getCurrent_stage_of_work());
                intent.putExtra("is_high_value", list.get(position).getIs_high_value());
                context.startActivity(intent);


            }
        });
        holder.binding.viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline()){
                    Intent intent = new Intent(context, ViewActionScreen.class);
                    intent.putExtra("work_id", list.get(position).getWork_id());
                    intent.putExtra("inspection_id", list.get(position).getInspection_id());
                    context.startActivity(intent);

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
                }

            }
        });



    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
