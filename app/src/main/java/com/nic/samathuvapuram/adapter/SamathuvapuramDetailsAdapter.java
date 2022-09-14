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
import com.nic.samathuvapuram.activity.AEScreen;
import com.nic.samathuvapuram.activity.HouseListActivity;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.SamathuvapuramListAdapterBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SamathuvapuramDetailsAdapter extends RecyclerView.Adapter<SamathuvapuramDetailsAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    static JSONObject dataset = new JSONObject();
    private final com.nic.samathuvapuram.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;

    private int samathuvapuram_id;
    private String pvcode;
    private String hab_code;
    private int localbody_area_type;
    private int localbody_type_id;
    private int dcode;
    private int bcode;
    private int tpcode;
    private int no_of_houses_constructed;
    private String muncode;
    private String corpcode;
    private String year_of_construction;
    private String dname;
    private String bname;
    private String pvname;
    private String habitation_name;
    private String townpanchayat_name;
    private String municipality_name;
    private String corporation_name;

    public SamathuvapuramDetailsAdapter(Activity context, ArrayList<ModelClass> listValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.dbData = dbData;
        this.list = listValues;
    }

    @Override
    public SamathuvapuramDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SamathuvapuramListAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.samathuvapuram_list_adapter, viewGroup, false);
        return new SamathuvapuramDetailsAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SamathuvapuramListAdapterBinding binding;

        public MyViewHolder(SamathuvapuramListAdapterBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
         samathuvapuram_id = list.get(position).getSamathuvapuram_id();
         pvcode = list.get(position).getPvCode();
         hab_code = list.get(position).getHabCode();
         localbody_area_type = list.get(position).getLocalbody_area_type();
         localbody_type_id = list.get(position).getLocalbody_type_id();
         dcode = list.get(position).getDcode();
         bcode = list.get(position).getBcode();
         tpcode = list.get(position).getTpcode();
         no_of_houses_constructed = list.get(position).getNo_of_houses_constructed();
        muncode = list.get(position).getMuncode();
        corpcode = list.get(position).getCorpcode();
        year_of_construction = list.get(position).getYear_of_construction();
        dname = list.get(position).getDname();
        bname = list.get(position).getBname();
        pvname = list.get(position).getPvName();
        habitation_name = list.get(position).getHabitationName();
        townpanchayat_name = list.get(position).getTownpanchayat_name();
        municipality_name = list.get(position).getMunicipality_name();
        corporation_name = list.get(position).getCorporation_name();

        holder.binding.samathuvapuramId.setText(String.valueOf(list.get(position).getSamathuvapuram_id()));
        if(dname != null && !dname.equals("") ){
            holder.binding.dnameLayout.setVisibility(View.VISIBLE);
            holder.binding.dname.setText(dname);
        }else {
            holder.binding.dnameLayout.setVisibility(View.GONE);
            holder.binding.dname.setText("");
        }

        if(bname != null && !bname.equals("") ){
            holder.binding.bnameLayout.setVisibility(View.VISIBLE);
            holder.binding.bname.setText(bname);
        }else {
            holder.binding.bnameLayout.setVisibility(View.GONE);
            holder.binding.bname.setText("");
        }

        if(pvname != null && !pvname.equals("") ){
            holder.binding.pvnameLayout.setVisibility(View.VISIBLE);
            holder.binding.pvname.setText(pvname);
        }else {
            holder.binding.pvnameLayout.setVisibility(View.GONE);
            holder.binding.pvname.setText("");
        }

        if(habitation_name != null && !habitation_name.equals("") ){
            holder.binding.habitationNameLayout.setVisibility(View.VISIBLE);
            holder.binding.habitationName.setText(habitation_name);
        }else {
            holder.binding.habitationNameLayout.setVisibility(View.GONE);
            holder.binding.habitationName.setText("");
        }


        if(townpanchayat_name != null && !townpanchayat_name.equals("") ){
            holder.binding.townpanchayatNameLayout.setVisibility(View.VISIBLE);
            holder.binding.townpanchayatName.setText(townpanchayat_name);
        }else {
            holder.binding.townpanchayatNameLayout.setVisibility(View.GONE);
            holder.binding.townpanchayatName.setText("");
        }

        if(municipality_name != null && !municipality_name.equals("") ){
            holder.binding.municipalityNameLayout.setVisibility(View.VISIBLE);
            holder.binding.municipalityName.setText(municipality_name);
        }else {
            holder.binding.municipalityNameLayout.setVisibility(View.GONE);
            holder.binding.municipalityName.setText("");
        }

        if(corporation_name != null && !corporation_name.equals("") ){
            holder.binding.corporationNameLayout.setVisibility(View.VISIBLE);
            holder.binding.corporationName.setText(corporation_name);
        }else {
            holder.binding.corporationNameLayout.setVisibility(View.GONE);
            holder.binding.corporationName.setText("");
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefManager.getUsertype().equals("bdo")){
                    Intent intent = new Intent(context, HouseListActivity.class);
                    intent.putExtra("samathuvapuram_id", list.get(position).getSamathuvapuram_id());
                    context.startActivity(intent);
                }else  if(prefManager.getUsertype().equals("ae")){
                    Intent intent = new Intent(context, AEScreen.class);
                    intent.putExtra("samathuvapuram_id", list.get(position).getSamathuvapuram_id());
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
