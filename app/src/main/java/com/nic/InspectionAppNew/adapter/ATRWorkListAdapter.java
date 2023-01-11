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
import com.nic.InspectionAppNew.activity.ATRWorkList;
import com.nic.InspectionAppNew.activity.SaveATRWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.AtrWorkListAdapterBinding;
import com.nic.InspectionAppNew.databinding.WorkListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.Utils;

import java.util.ArrayList;

public class ATRWorkListAdapter extends RecyclerView.Adapter<ATRWorkListAdapter.MyViewHolder> implements Filterable {

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

    public ATRWorkListAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData, String onOffType) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.onOffType = onOffType;
        this.listFilteredValue = listValues;
        this.list = listValues;
    }

    @Override
    public ATRWorkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AtrWorkListAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.atr_work_list_adapter, viewGroup, false);
        return new ATRWorkListAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AtrWorkListAdapterBinding binding;

        public MyViewHolder(AtrWorkListAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.binding.workId.setText(String.valueOf(listFilteredValue.get(position).getWork_id()));
        holder.binding.workName.setText(String.valueOf(listFilteredValue.get(position).getWork_name()));
        holder.binding.inspectedDate.setText(String.valueOf(listFilteredValue.get(position).getInspectedDate()));
        holder.binding.status.setText(String.valueOf(listFilteredValue.get(position).getWork_status()));
        holder.binding.inspectionByOfficer.setText(String.valueOf(listFilteredValue.get(position).getInspection_by_officer()));
        holder.binding.inspectionByOfficerDesig.setText("("+String.valueOf(listFilteredValue.get(position).getInspection_by_officer_designation())+") ");
        holder.binding.workTypeName.setText(String.valueOf(listFilteredValue.get(position).getWork_type_name()));


        if(listFilteredValue.get(position).getWork_status_id() == 1) {
            holder.binding.atrStatusLayout.setVisibility(View.GONE);
            holder.binding.takeAction.setVisibility(View.GONE);
            holder.binding.check.setVisibility(View.VISIBLE);
            holder.binding.reportedByLayout.setVisibility(View.GONE);
            holder.binding.reportedBy.setText("");
        }else {
            if(listFilteredValue.get(position).getAction_status() != null && listFilteredValue.get(position).getAction_status().equals("Y")) {
                holder.binding.atrStatus.setText("Completed");
                holder.binding.atrStatus.setTextColor(context.getResources().getColor(R.color.account_status_green_color));
                holder.binding.takeAction.setVisibility(View.GONE);
                holder.binding.check.setVisibility(View.VISIBLE);
                holder.binding.reportedByLayout.setVisibility(View.VISIBLE);
                holder.binding.reportedBy.setText(listFilteredValue.get(position).getReported_by());
            }else {
                holder.binding.atrStatus.setText("Pending");
                holder.binding.atrStatus.setTextColor(context.getResources().getColor(R.color.grey_8));
                holder.binding.check.setVisibility(View.GONE);
                holder.binding.reportedByLayout.setVisibility(View.GONE);
                holder.binding.reportedBy.setText("");
                if(prefManager.getDesignationCode().equals("153")) {
                    holder.binding.takeAction.setVisibility(View.VISIBLE);
                }else {
                    holder.binding.takeAction.setVisibility(View.GONE);
                }
            }
        }

        if(String.valueOf(listFilteredValue.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(listFilteredValue.get(position).getWork_name()), holder.binding.workName, 0);
        }

        holder.binding.takeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaveATRWorkDetailsActivity.class);
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
                intent.putExtra("inspection_id", listFilteredValue.get(position).getInspection_id());
                intent.putExtra("work_name", listFilteredValue.get(position).getWork_name());
                intent.putExtra("as_value", listFilteredValue.get(position).getAs_value());
                intent.putExtra("ts_value", listFilteredValue.get(position).getTs_value());
                intent.putExtra("current_stage_of_work", listFilteredValue.get(position).getCurrent_stage_of_work());
                intent.putExtra("is_high_value", listFilteredValue.get(position).getIs_high_value());
                intent.putExtra("onOffType",onOffType);
                intent.putExtra("type","atr");
                intent.putExtra("flag","");
                context.startActivity(intent);

            }
        });

        holder.binding.viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utils.isOnline()){
                    ((ATRWorkList)context).getWorkReportDetails(listFilteredValue.get(position).getAction_status(),String.valueOf( listFilteredValue.get(position).getWork_id()), listFilteredValue.get(position).getInspection_id(), listFilteredValue.get(position).getAction_taken_id());
                }else {
                    Utils.showAlert(context,context.getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
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
