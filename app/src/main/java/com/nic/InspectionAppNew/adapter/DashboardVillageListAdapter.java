package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.VillageListReportActivity;
import com.nic.InspectionAppNew.databinding.DashboardVillageListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.Utils;

import java.util.ArrayList;

public class DashboardVillageListAdapter extends RecyclerView.Adapter<DashboardVillageListAdapter.MyViewHolder> implements Filterable {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> listFilteredValue;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;

    public DashboardVillageListAdapter(Activity context, ArrayList<ModelClass> listValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.listFilteredValue = listValues;
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


        holder.binding.villageName.setText(String.valueOf(listFilteredValue.get(position).getPvName()));
        holder.binding.totalTv.setText("Total Inspected Works ("+String.valueOf(listFilteredValue.get(position).getTotal_cout())+")");
        holder.binding.satisfiedCount.setText(String.valueOf(listFilteredValue.get(position).getSatisfied_count()));
        holder.binding.unSatisfiedCount.setText(String.valueOf(listFilteredValue.get(position).getUnsatisfied_count()));
        holder.binding.improvementCount.setText(String.valueOf(listFilteredValue.get(position).getNeedimprovement_count()));


        holder.binding.satisfiedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFilteredValue.get(position).getSatisfied_count()>0){
                    ((VillageListReportActivity)context).gotoATR(1,listFilteredValue.get(position).getDistrictCode(),listFilteredValue.get(position).getBlockCode()
                            ,listFilteredValue.get(position).getPvCode(),listFilteredValue.get(position).getPvName());

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.no_data_found));
                }
            }
        });
        holder.binding.unSatisfiedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFilteredValue.get(position).getUnsatisfied_count()>0){
                    ((VillageListReportActivity)context).gotoATR(2,listFilteredValue.get(position).getDistrictCode(),listFilteredValue.get(position).getBlockCode()
                            ,listFilteredValue.get(position).getPvCode(),listFilteredValue.get(position).getPvName());

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.no_data_found));
                }
            }
        });
        holder.binding.needImprovementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFilteredValue.get(position).getNeedimprovement_count()>0){
                    ((VillageListReportActivity)context).gotoATR(3,listFilteredValue.get(position).getDistrictCode(),listFilteredValue.get(position).getBlockCode()
                            ,listFilteredValue.get(position).getPvCode(),listFilteredValue.get(position).getPvName());

                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.no_data_found));
                }
            }
        });



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
                        if (
                                row.getPvName().toLowerCase().contains(charString.toLowerCase()) ||
                                        row.getPvName().toLowerCase().contains(charString.toUpperCase())
                        ) {
                            filteredList.add(row);
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
