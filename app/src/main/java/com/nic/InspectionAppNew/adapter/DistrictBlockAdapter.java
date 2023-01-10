package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.graphics.Typeface;
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
import com.nic.InspectionAppNew.utils.Utils;

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
        holder.binding.satisfiedCount.setText(String.valueOf(list.get(position).getSatisfied_count()));
        holder.binding.unSatisfiedCount.setText(String.valueOf(list.get(position).getUnsatisfied_count()));
        holder.binding.needImprovementCount.setText(String.valueOf(list.get(position).getNeedimprovement_count()));

        if(list.get(position).getTotal_cout()>0){
            holder.binding.totalCount.setTextColor(context.getResources().getColor(R.color.primary_text_color2));
            holder.binding.totalCount.setTypeface(holder.binding.totalCount.getTypeface(), Typeface.BOLD);
        }else {
            holder.binding.totalCount.setTextColor(context.getResources().getColor(R.color.grey_7));
            holder.binding.totalCount.setTypeface(holder.binding.totalCount.getTypeface(), Typeface.NORMAL);
        }
        if(list.get(position).getSatisfied_count()>0){
            holder.binding.satisfiedCount.setTextColor(context.getResources().getColor(R.color.primary_text_color2));
            holder.binding.satisfiedCount.setTypeface(holder.binding.satisfiedCount.getTypeface(), Typeface.BOLD);
        }else {
            holder.binding.satisfiedCount.setTextColor(context.getResources().getColor(R.color.grey_7));
            holder.binding.satisfiedCount.setTypeface(holder.binding.satisfiedCount.getTypeface(), Typeface.NORMAL);
        }
        if(list.get(position).getUnsatisfied_count()>0){
            holder.binding.unSatisfiedCount.setTextColor(context.getResources().getColor(R.color.primary_text_color2));
            holder.binding.unSatisfiedCount.setTypeface(holder.binding.unSatisfiedCount.getTypeface(), Typeface.BOLD);
        }else {
            holder.binding.unSatisfiedCount.setTextColor(context.getResources().getColor(R.color.grey_7));
            holder.binding.unSatisfiedCount.setTypeface(holder.binding.unSatisfiedCount.getTypeface(), Typeface.NORMAL);
        }
        if(list.get(position).getNeedimprovement_count()>0){
            holder.binding.needImprovementCount.setTextColor(context.getResources().getColor(R.color.primary_text_color2));
            holder.binding.needImprovementCount.setTypeface(holder.binding.needImprovementCount.getTypeface(), Typeface.BOLD);
        }else {
            holder.binding.needImprovementCount.setTextColor(context.getResources().getColor(R.color.grey_7));
            holder.binding.needImprovementCount.setTypeface(holder.binding.needImprovementCount.getTypeface(), Typeface.NORMAL);
        }
        if(position%2==0){
            holder.binding.item.setBackgroundColor(context.getResources().getColor(R.color.full_transparent));
        }else {
            holder.binding.item.setBackgroundColor(context.getResources().getColor(R.color.ca1));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(level.equals("D")){
                    if(list.get(position).getTotal_cout()>0){
                        ((OverAllInspectionReport)context).getBlockList(list.get(position).getDistrictCode(),list.get(position).getDistrictName(),"D");
                    }else {
                        Utils.showAlert(context,context.getResources().getString(R.string.no_data_found));
                    }
                }else if(level.equals("B")){
                    if(list.get(position).getTotal_cout()>0){
                        ((OverAllInspectionReport)context).getVillageListReport(list.get(position).getDistrictCode(),list.get(position).getBlockCode(),list.get(position).getBlockName());
                    }else {
                        Utils.showAlert(context,context.getResources().getString(R.string.no_data_found));
                    }
                }

            }
        });

    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
