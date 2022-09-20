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
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.WorkListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

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
        holder.binding.workStatus.setText(String.valueOf(list.get(position).getWork_status()));



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                intent.putExtra("work_id", list.get(position).getWork_id());
                context.startActivity(intent);

            }
        });



    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
