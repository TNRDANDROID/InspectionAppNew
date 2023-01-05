package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.OverAllInspectionReport;
import com.nic.InspectionAppNew.databinding.DistrictAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;

public class DistrictBlockAdapter extends RecyclerView.Adapter<DistrictBlockAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;
    private String level;

    public DistrictBlockAdapter(Activity context, ArrayList<ModelClass> listValues, String level) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
        this.level = level;
    }

    @Override
    public DistrictBlockAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        DistrictAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.district_adapter, viewGroup, false);
        return new DistrictBlockAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private DistrictAdapterBinding binding;

        public MyViewHolder(DistrictAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if(level.equals("D")){
            holder.binding.headertxt.setText(list.get(position).getDistrictName());
        }else if(level.equals("B")){
            holder.binding.headertxt.setText(list.get(position).getBlockName());
        }

        holder.binding.totalCount.setText("("+String.valueOf(list.get(position).getTotal_cout())+")");
        holder.binding.satisfiedCount.setText(String.valueOf(list.get(position).getSatisfied_cout()));
        holder.binding.unSatisfiedCount.setText(String.valueOf(list.get(position).getUnsatisfied_cout()));
        holder.binding.needImprovementCount.setText(String.valueOf(list.get(position).getNeedimprovement_cout()));

        if(position%2==0){
            holder.binding.item.setBackgroundColor(context.getResources().getColor(R.color.full_transparent));
        }else {
            holder.binding.item.setBackgroundColor(context.getResources().getColor(R.color.ca1));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(level.equals("D")){
                    ((OverAllInspectionReport)context).getBlockDashboardData(list.get(position).getDistrictCode());
                }else if(level.equals("B")){
                    ((OverAllInspectionReport)context).getVillageListReport(list.get(position).getDistrictCode(),list.get(position).getBlockCode());
                }

            }
        });

    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
