package com.nic.InspectionAppNew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.CheckBoxValueBinding;
import com.nic.InspectionAppNew.databinding.ImagesAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.util.ArrayList;
import java.util.List;



    public class CheckBoxAdapter extends RecyclerView.Adapter<com.nic.InspectionAppNew.adapter.CheckBoxAdapter.MyViewHolder> implements Filterable {

        private static Activity context;
        private PrefManager prefManager;
        private ArrayList<ModelClass> list;
        private ArrayList<ModelClass> listFiltered;
        private ArrayList<ModelClass> selectedList =new ArrayList<>();
        private LayoutInflater layoutInflater;
        Api.schemeListener listener;


        public CheckBoxAdapter(Activity context, ArrayList<ModelClass> listValues, Api.schemeListener listener) {

            this.context = context;
            prefManager = new PrefManager(context);
            this.list = listValues;
            this.listFiltered = listValues;
            this.listener = listener;

        }

        @Override
        public com.nic.InspectionAppNew.adapter.CheckBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (layoutInflater == null) {
                layoutInflater = LayoutInflater.from(viewGroup.getContext());
            }
            CheckBoxValueBinding binding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.check_box_value, viewGroup, false);
            return new com.nic.InspectionAppNew.adapter.CheckBoxAdapter.MyViewHolder(binding);

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            private CheckBoxValueBinding binding;

            public MyViewHolder(CheckBoxValueBinding Binding) {
                super(Binding.getRoot());
                binding = Binding;
            }

        }

        @Override
        public void onBindViewHolder(@NonNull final com.nic.InspectionAppNew.adapter.CheckBoxAdapter.MyViewHolder holder, final int position) {

           if(listFiltered.get(position).getSchemeCheck()){
               holder.binding.checkbox.setChecked(true);
           }else {
               holder.binding.checkbox.setChecked(false);
           }
            holder.binding.value.setText(listFiltered.get(position).getSchemeName());
            holder.binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked){
                      for(int i=0;i<list.size();i++){
                          if(list.get(i).getSchemeSequentialID().equals(listFiltered.get(position).getSchemeSequentialID())){
                              //ModelClass modelClass1 = list.get(i);
                              list.get(i).setSchemeCheck(true);
                              listFiltered.get(position).setSchemeCheck(true);
                             // modelClass1.setSchemeCheck(true);
                              //list.set(i, modelClass1);
//                              listFiltered.get(i).setSchemeCheck(true);
                          }
                      }

                        listener.OnMyScheme(list);
                    }else {
                        for(int i=0;i<list.size();i++){
                            if(list.get(i).getSchemeSequentialID().equals(listFiltered.get(position).getSchemeSequentialID())){
                                list.get(i).setSchemeCheck(false);
                                listFiltered.get(position).setSchemeCheck(false);
                                //ModelClass modelClass1 = list.get(i);
                                //modelClass1.setSchemeCheck(false);
                                //list.set(i, modelClass1);
//                                listFiltered.get(i).setSchemeCheck(false);
                            }
                        }


                        listener.OnMyScheme(list);
                    }

                }
            });


        }



        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return listFiltered.size();
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFiltered = list;
                    } else {
                        ArrayList<ModelClass> filteredList = new ArrayList<>();
                        for (ModelClass row : list) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (
                                    row.getSchemeName().toLowerCase().contains(charString.toLowerCase()) ||
                                            row.getSchemeName().toLowerCase().contains(charString.toUpperCase())||
                                            String.valueOf(row.getSchemeSequentialID()).toLowerCase().contains(charString.toUpperCase())
                            ) {
                                filteredList.add(row);
                            }
                        }

                        listFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = listFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    listFiltered = (ArrayList<ModelClass>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }
