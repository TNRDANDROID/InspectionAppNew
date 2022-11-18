package com.nic.InspectionAppNew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.model.ModelClass;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<ModelClass> inspectionList;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<ModelClass> pmgsySurvey, String type) {
        this.inspectionList = pmgsySurvey;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return inspectionList.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.spinner_drop_down_item, parent, false);
//        TextView tv_type = (TextView) view.findViewById(R.id.tv_spinner_item);
        View view = inflater.inflate(R.layout.spinner_value, parent, false);
        TextView tv_type = (TextView) view.findViewById(R.id.spinner_list_value);
        ModelClass inspectionItems = inspectionList.get(position);

        if (type.equalsIgnoreCase("Village")) {
            tv_type.setText(inspectionItems.getPvName());
        } else if (type.equalsIgnoreCase("District")) {
            tv_type.setText(inspectionItems.getDistrictName());
        } else if (type.equalsIgnoreCase("Block")) {
            tv_type.setText(inspectionItems.getBlockName());
        } else if (type.equalsIgnoreCase("Scheme")) {
            tv_type.setText(inspectionItems.getSchemeName());
        } else if (type.equalsIgnoreCase("Phototype")) {
            tv_type.setText(inspectionItems.getPhoto_type_name());
        }else if (type.equalsIgnoreCase("FinYear")) {
            tv_type.setText(inspectionItems.getFinancialYear());
        }else if (type.equalsIgnoreCase("work_type_list")) {
            tv_type.setText(inspectionItems.getWork_name());
        }else if (type.equalsIgnoreCase("status")) {
            tv_type.setText(inspectionItems.getWork_status());
        }else if (type.equalsIgnoreCase("other_category_list")) {
            tv_type.setText(inspectionItems.getOther_work_category_name());
        }
        else if (type.equalsIgnoreCase("GenderList")) {
            tv_type.setText(inspectionItems.getGender_name_en());
        }
        else if (type.equalsIgnoreCase("LevelList")) {
            tv_type.setText(inspectionItems.getLocalbody_name());
        }
        else if (type.equalsIgnoreCase("DesignationList")) {
            tv_type.setText(inspectionItems.getDesig_name());
        }
        else {
            tv_type.setText("");
        }
        return view;
    }

}