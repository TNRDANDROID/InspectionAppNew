package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ImagesAdapterBinding;
import com.nic.InspectionAppNew.databinding.SaveImagesAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;

public class SaveImageAdapter extends RecyclerView.Adapter<SaveImageAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;

    public SaveImageAdapter(Activity context, ArrayList<ModelClass> listValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
    }

    @Override
    public SaveImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SaveImagesAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.images_adapter, viewGroup, false);
        return new SaveImageAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SaveImagesAdapterBinding binding;

        public MyViewHolder(SaveImagesAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        Glide.with(context).load(list.get(position).getImage())
                .thumbnail(0.5f)
                .into(holder.binding.image);

        holder.binding.description.setText("Description : "+list.get(position).getDescription());

    }





    @Override
    public int getItemCount() {
        return list.size();
    }

}
