package com.nic.InspectionAppNew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.DownloadActivity;
import com.nic.InspectionAppNew.activity.OnlineWorkFilterScreen;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.databinding.CheckBoxValueBinding;
import com.nic.InspectionAppNew.databinding.SchemeValueBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.MyViewHolder> implements Filterable  {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private ArrayList<ModelClass> listFiltered;
    private LayoutInflater layoutInflater;

    public SchemeAdapter(Activity context, ArrayList<ModelClass> listValues) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
        this.listFiltered = listValues;
    }

    @Override
    public SchemeAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SchemeValueBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.scheme_value, viewGroup, false);
        return new SchemeAdapter.MyViewHolder(binding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SchemeValueBinding binding;

        public MyViewHolder(SchemeValueBinding Binding) {
            super(Binding.getRoot());
            binding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final SchemeAdapter.MyViewHolder holder, final int position) {

        holder.binding.value.setText(listFiltered.get(position).getSchemeName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((OnlineWorkFilterScreen)context).getScheme(listFiltered.get(position).getSchemeName(),listFiltered.get(position).getSchemeSequentialID());;
            }
        });

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
