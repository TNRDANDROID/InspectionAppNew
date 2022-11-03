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
import com.nic.InspectionAppNew.databinding.VillageListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;

public class VillageListAdapter extends RecyclerView.Adapter<VillageListAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private final com.nic.InspectionAppNew.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;
    String onOffType;

    public VillageListAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData, String onOffType) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.onOffType = onOffType;
        this.list = listValues;
    }

    @Override
    public VillageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        VillageListAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.village_list_adapter, viewGroup, false);
        return new VillageListAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private VillageListAdapterBinding binding;

        public MyViewHolder(VillageListAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        holder.binding.villageTv.setText(String.valueOf(list.get(position).getPvName()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnlineWorkFilterScreen)context).getWorkListByVillage(list.get(position).getDistrictCode(),list.get(position).getBlockCode(),list.get(position).getPvCode());
            }
        });



    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
