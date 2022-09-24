package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.activity.ViewActionScreen;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ImagesAdapterBinding;
import com.nic.InspectionAppNew.databinding.WorkListAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private final com.nic.InspectionAppNew.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;

    public ImageAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.list = listValues;
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        ImagesAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.images_adapter, viewGroup, false);
        return new ImageAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImagesAdapterBinding binding;

        public MyViewHolder(ImagesAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Glide.with(context).load(list.get(position).getImage())
                .thumbnail(0.5f)
                .into(holder.binding.thumbnail);

        holder.binding.imageDescription.setText("Description : "+list.get(position).getDescription());

    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
