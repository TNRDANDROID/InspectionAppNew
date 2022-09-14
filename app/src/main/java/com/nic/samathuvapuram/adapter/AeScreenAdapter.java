package com.nic.samathuvapuram.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.activity.FullImageActivity;
import com.nic.samathuvapuram.activity.HouseListActivity;
import com.nic.samathuvapuram.activity.InfraListActivity;
import com.nic.samathuvapuram.activity.SaveAeHouseDetailsActivity;
import com.nic.samathuvapuram.activity.SaveBlkHouseDetailsActivity;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.AeAdapterBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;

public class AeScreenAdapter extends RecyclerView.Adapter<AeScreenAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    static JSONObject dataset = new JSONObject();

    private LayoutInflater layoutInflater;


    private int estimate_type_id;
    private String estimate_type_name;


    public AeScreenAdapter(Activity context, ArrayList<ModelClass> list) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.list = list;
    }

    @Override
    public AeScreenAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        AeAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.ae_adapter, viewGroup, false);
        return new AeScreenAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private AeAdapterBinding adapterBinding;

        public MyViewHolder(AeAdapterBinding Binding) {
            super(Binding.getRoot());
            adapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        estimate_type_id = list.get(position).getEstimate_type_id();
        estimate_type_name = list.get(position).getEstimate_type_name();
        holder.adapterBinding.name.setText(estimate_type_name);
        if(list.get(position).getEstimate_type_id() ==1){
            holder.adapterBinding.img.setImageDrawable(context.getResources().getDrawable(R.drawable.home_outline));
        }else {
            holder.adapterBinding.img.setImageDrawable(context.getResources().getDrawable(R.drawable.infrastructure_outline));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).getEstimate_type_id() ==1){
                    Intent intent = new Intent(context, HouseListActivity.class);
                    intent.putExtra("samathuvapuram_id",list.get(position).getSamathuvapuram_id());
                    context.startActivity(intent);

                }else {
                    Intent intent = new Intent(context, InfraListActivity.class);
                    intent.putExtra("samathuvapuram_id",list.get(position).getSamathuvapuram_id());
                    context.startActivity(intent);

                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
