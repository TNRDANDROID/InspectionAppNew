package com.nic.samathuvapuram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.model.ModelClass;

import java.util.List;

/**
 * Created by shanmugapriyan on 25/05/16.
 */
public class CommonAdapter extends BaseAdapter {
    private List<ModelClass> pmgsySurveys;
    private Context mContext;
    private String type;


    public CommonAdapter(Context mContext, List<ModelClass> pmgsySurvey, String type) {
        this.pmgsySurveys = pmgsySurvey;
        this.mContext = mContext;
        this.type = type;
    }


    public int getCount() {
        return pmgsySurveys.size();
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
        ModelClass pmgsySurvey = pmgsySurveys.get(position);

        if (type.equalsIgnoreCase("CommunityList")) {
            tv_type.setText(pmgsySurvey.getCommunity_category());
        } else if (type.equalsIgnoreCase("Current_house_usage")) {
            tv_type.setText(pmgsySurvey.getCurrent_usage());
        } else if (type.equalsIgnoreCase("Phototype")) {
            tv_type.setText(pmgsySurvey.getPhoto_type_name());
        }else if (type.equalsIgnoreCase("estimate_type_list")) {
            tv_type.setText(pmgsySurvey.getEstimate_type_name());
        }else if (type.equalsIgnoreCase("condition_of_house_list")) {
            tv_type.setText(pmgsySurvey.getCondition_of_house());
        }else if (type.equalsIgnoreCase("condition_of_infra_list")) {
            tv_type.setText(pmgsySurvey.getCondition_of_infra());
        }else if (type.equalsIgnoreCase("work_scheme_list")) {
            tv_type.setText(pmgsySurvey.getScheme_name());
        }else if (type.equalsIgnoreCase("work_type_list")) {
            tv_type.setText(pmgsySurvey.getWork_name());
        }
        else {
            tv_type.setText("");
        }
        return view;
    }

}