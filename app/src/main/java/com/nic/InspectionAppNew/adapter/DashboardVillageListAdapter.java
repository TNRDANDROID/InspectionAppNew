package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.OnlineWorkFilterScreen;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.DashboardVillageListAdapterBinding;
import com.nic.InspectionAppNew.databinding.VillageListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;

public class DashboardVillageListAdapter extends RecyclerView.Adapter<DashboardVillageListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;

    public DashboardVillageListAdapter(Activity context, ArrayList<ModelClass> listValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
    }

    @Override
    public DashboardVillageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        DashboardVillageListAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.dashboard_village_list_adapter, viewGroup, false);
        return new DashboardVillageListAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DashboardVillageListAdapterBinding binding;

        public MyViewHolder(DashboardVillageListAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.binding.villageName.setText(String.valueOf(list.get(position).getPvName()));
        holder.binding.totalTv.setText("Total Count Of Inspection (\n"+String.valueOf(list.get(position).getTotal_cout())+")");
        holder.binding.satisfiedCount.setText(String.valueOf(list.get(position).getSatisfied_cout()));
        holder.binding.unSatisfiedCount.setText(String.valueOf(list.get(position).getUnsatisfied_cout()));
        holder.binding.improvementCount.setText(String.valueOf(list.get(position).getNeedimprovement_cout()));


        holder.binding.satisfiedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
