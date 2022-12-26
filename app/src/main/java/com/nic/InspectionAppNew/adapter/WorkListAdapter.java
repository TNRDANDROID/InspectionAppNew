package com.nic.InspectionAppNew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.WorkList;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.WorkListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.MyViewHolder> implements Filterable {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> listFilteredValue;
    private ArrayList<ModelClass> list;
    private final com.nic.InspectionAppNew.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    String onOffType;
    ProgressHUD progressHUD;
    private static DiffUtil.ItemCallback<ModelClass> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ModelClass>() {
                @Override
                public boolean areItemsTheSame(ModelClass oldItem, ModelClass newItem) {
                    return oldItem.getWork_id() == newItem.getWork_id();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(ModelClass oldItem, ModelClass newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public WorkListAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData,String onOffType) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.onOffType = onOffType;
        this.listFilteredValue = listValues;
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


        holder.binding.workId.setText(String.valueOf(listFilteredValue.get(position).getWork_id()));
        holder.binding.workName.setText(String.valueOf(listFilteredValue.get(position).getWork_name()));
//        holder.binding.workStatus.setText(String.valueOf(list.get(position).getWork_status()));
        holder.binding.finYear.setText(String.valueOf(listFilteredValue.get(position).getFinancialYear()));
        holder.binding.asValue.setText(String.valueOf(listFilteredValue.get(position).getAs_value()));
        holder.binding.tsValue.setText(String.valueOf(listFilteredValue.get(position).getTs_value()));
        holder.binding.stageName.setText(String.valueOf(listFilteredValue.get(position).getStage_name()));
        holder.binding.asDate.setText(String.valueOf(listFilteredValue.get(position).getAs_date()));
        holder.binding.tsDate.setText(String.valueOf(listFilteredValue.get(position).getTs_date()));
        holder.binding.workOrderDate.setText(String.valueOf(listFilteredValue.get(position).getWork_order_date()));
        holder.binding.workTypeName.setText(String.valueOf(listFilteredValue.get(position).getWork_type_name()));

        if(String.valueOf(listFilteredValue.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(listFilteredValue.get(position).getWork_name()), holder.binding.workName, 0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                intent.putExtra("dcode", listFilteredValue.get(position).getDistrictCode());
                intent.putExtra("bcode", listFilteredValue.get(position).getBlockCode());
                intent.putExtra("pvcode", listFilteredValue.get(position).getPvCode());
                intent.putExtra("hab_code", listFilteredValue.get(position).getHabCode());
                intent.putExtra("scheme_group_id", listFilteredValue.get(position).getScheme_group_id());
                intent.putExtra("work_group_id", listFilteredValue.get(position).getWork_group_id());
                intent.putExtra("work_type_id", listFilteredValue.get(position).getWork_type_id());
                intent.putExtra("scheme_id", listFilteredValue.get(position).getSchemeSequentialID());
                intent.putExtra("fin_year", listFilteredValue.get(position).getFinancialYear());
                intent.putExtra("work_id", listFilteredValue.get(position).getWork_id());
                intent.putExtra("work_name", listFilteredValue.get(position).getWork_name());
                intent.putExtra("as_value", listFilteredValue.get(position).getAs_value());
                intent.putExtra("ts_value", listFilteredValue.get(position).getTs_value());
                intent.putExtra("current_stage_of_work", listFilteredValue.get(position).getCurrent_stage_of_work());
                intent.putExtra("is_high_value", listFilteredValue.get(position).getIs_high_value());
                intent.putExtra("onOffType",onOffType);
                intent.putExtra("other_work_detail","");
                intent.putExtra("flag","");
                intent.putExtra("type","rdpr");
                context.startActivity(intent);


            }
        });
        holder.binding.takeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                intent.putExtra("dcode", listFilteredValue.get(position).getDistrictCode());
                intent.putExtra("bcode", listFilteredValue.get(position).getBlockCode());
                intent.putExtra("pvcode", listFilteredValue.get(position).getPvCode());
                intent.putExtra("hab_code", listFilteredValue.get(position).getHabCode());
                intent.putExtra("scheme_group_id", listFilteredValue.get(position).getScheme_group_id());
                intent.putExtra("work_group_id", listFilteredValue.get(position).getWork_group_id());
                intent.putExtra("work_type_id", listFilteredValue.get(position).getWork_type_id());
                intent.putExtra("scheme_id", listFilteredValue.get(position).getSchemeSequentialID());
                intent.putExtra("fin_year", listFilteredValue.get(position).getFinancialYear());
                intent.putExtra("work_id", listFilteredValue.get(position).getWork_id());
                intent.putExtra("work_name", listFilteredValue.get(position).getWork_name());
                intent.putExtra("as_value", listFilteredValue.get(position).getAs_value());
                intent.putExtra("ts_value", listFilteredValue.get(position).getTs_value());
                intent.putExtra("current_stage_of_work", listFilteredValue.get(position).getCurrent_stage_of_work());
                intent.putExtra("is_high_value", listFilteredValue.get(position).getIs_high_value());
                intent.putExtra("onOffType",onOffType);
                intent.putExtra("other_work_detail","");
                intent.putExtra("flag","");
                intent.putExtra("type","rdpr");
                context.startActivity(intent);

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
                                row.getWork_name().toLowerCase().contains(charString.toLowerCase()) ||
                                        row.getWork_name().toLowerCase().contains(charString.toUpperCase())||
                                        String.valueOf(row.getWork_id()).toLowerCase().contains(charString.toUpperCase())
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
