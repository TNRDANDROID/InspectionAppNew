package com.nic.InspectionAppNew.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nic.InspectionAppNew.Interface.AdapterCameraIntent;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveATRWorkDetailsActivity;
import com.nic.InspectionAppNew.databinding.SaveAtrImagesAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SaveATRImageAdapter extends RecyclerView.Adapter<SaveATRImageAdapter.MyViewHolder> implements AdapterCameraIntent {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;
    String flag="";

    int clicked_position=0;
    String data="";
    AdapterCameraIntent adapterCameraIntent;


    public SaveATRImageAdapter(Activity context, ArrayList<ModelClass> listValues, String flag, String data) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
        this.flag = flag;
        this.data = data;
        adapterCameraIntent=this;
    }

    @Override
    public SaveATRImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SaveAtrImagesAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.save_atr_images_adapter, viewGroup, false);
        return new SaveATRImageAdapter.MyViewHolder(binding);

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SaveAtrImagesAdapterBinding binding;

        public MyViewHolder(SaveAtrImagesAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if(data.equalsIgnoreCase("local")){
            if(list.get(position).getImage()!=null) {
                holder.binding.image.setVisibility(View.VISIBLE);
                holder.binding.imagePreview.setVisibility(View.GONE);
                Glide.with(context).load(list.get(position).getImage())
                        .thumbnail(0.5f)
                        .into(holder.binding.image);
            }else {
                holder.binding.image.setVisibility(View.GONE);
                holder.binding.imagePreview.setVisibility(View.VISIBLE);
            }
        }else {
            if(list.get(position).getImage()!=null) {
                holder.binding.image.setVisibility(View.VISIBLE);
                holder.binding.imagePreview.setVisibility(View.GONE);
                Glide.with(context).load(list.get(position).getImage())
                        .thumbnail(0.5f)
                        .into(holder.binding.image);
            }else {
                holder.binding.image.setVisibility(View.GONE);
                holder.binding.imagePreview.setVisibility(View.VISIBLE);
            }

        }

        if(!list.get(position).getDescription().equalsIgnoreCase("")){
            holder.binding.description.setText(list.get(position).getDescription());
        }else {
            holder.binding.description.setText("");
        }
        if(flag.equalsIgnoreCase("edit")){
            holder.binding.description.setEnabled(false);
            holder.binding.image.setEnabled(false);
            holder.binding.imagePreview.setEnabled(false);
        }else {
            holder.binding.description.setEnabled(true);
            holder.binding.image.setEnabled(true);
            holder.binding.imagePreview.setEnabled(true);
        }
        holder.binding.imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked_position=position;
                if(flag.equalsIgnoreCase("edit")){
                    Utils.showAlert(context, "You can't edit inspected photo");
                }else {
                    ((SaveATRWorkDetailsActivity)context).getExactLocation();
                }

            }
        });
        holder.binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked_position=position;
                if(flag.equalsIgnoreCase("edit")){
                    Utils.showAlert(context, "You can't edit inspected photo");
                }else {
                    ((SaveATRWorkDetailsActivity)context).getExactLocation();
                }

            }
        });
        holder.binding.cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked_position=position;
                if(flag.equalsIgnoreCase("edit")){
                    Utils.showAlert(context, "You can't edit inspected photo");
                }else {
                    ((SaveATRWorkDetailsActivity)context).getExactLocation();
                }

            }
        });
        holder.binding.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SaveATRWorkDetailsActivity)context).callTextAnswer(position);

            }
        });


    }

    @Override
    public void OnIntentListener(Bitmap bitmap, String filePath, Double wayLatitude, Double wayLongitude) {
          /*  myholder.binding.image.setImageBitmap(bitmap);
            myholder.binding.imagePreview.setVisibility(View.GONE);
            myholder.binding.image.setVisibility(View.VISIBLE);*/
            list.get(clicked_position).setImage(bitmap);
            list.get(clicked_position).setImage_path(filePath);
            list.get(clicked_position).setImage_serial_number(clicked_position);
            list.get(clicked_position).setLatitude(String.valueOf(wayLatitude));
            list.get(clicked_position).setLongtitude(String.valueOf(wayLongitude));
            notifyItemChanged(clicked_position);

    }
    public String fileDirectory(Bitmap bitmap,String type,String count){
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(type, Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String child_path = Utils.getCurrentDateTime()+"_"+count+".png";
        File mypath = new File(directory, child_path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return mypath.toString();
    }

    public ArrayList<ModelClass> finalImageList() {
        return list;
    }


    @Override
    public int getItemCount() {

        return Integer.parseInt(prefManager.getPhotoCount());
//        return list.size();
    }

}
