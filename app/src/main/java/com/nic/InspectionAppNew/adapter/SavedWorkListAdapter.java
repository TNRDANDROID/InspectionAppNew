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
import com.nic.InspectionAppNew.activity.ViewSavedWorkList;
import com.nic.InspectionAppNew.databinding.SavedWorkListItemViewBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.Utils;

import java.util.ArrayList;

public class SavedWorkListAdapter extends RecyclerView.Adapter<SavedWorkListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;

    public SavedWorkListAdapter(Activity context, ArrayList<ModelClass> listValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
    }

    @Override
    public SavedWorkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SavedWorkListItemViewBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.saved_work_list_item_view, viewGroup, false);
        return new MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SavedWorkListItemViewBinding binding;

        public MyViewHolder(SavedWorkListItemViewBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final SavedWorkListAdapter.MyViewHolder holder, final int position) {


        holder.binding.workId.setText(String.valueOf(list.get(position).getWork_id()));
        holder.binding.workName.setText(String.valueOf(list.get(position).getWork_name()));
        holder.binding.inspectedDate.setText(String.valueOf(list.get(position).getInspectedDate()));
        holder.binding.status.setText(String.valueOf(list.get(position).getWork_status()));


        if(String.valueOf(list.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(list.get(position).getWork_name()), holder.binding.workName, 2);
        }

        holder.binding.viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Utils.isOnline()){
                    ((ViewSavedWorkList)context).getWorkReportDetails(String.valueOf( list.get(position).getWork_id()),list.get(position).getInspection_id());
                   /* Intent intent = new Intent(context, ViewReport.class);
                    intent.putExtra("work_id",String.valueOf( list.get(position).getWork_id()));
                    intent.putExtra("inspection_id", list.get(position).getInspection_id());
                    context.startActivity(intent);*/

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
