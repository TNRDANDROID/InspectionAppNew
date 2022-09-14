package com.nic.samathuvapuram.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.activity.FullImageActivity;
import com.nic.samathuvapuram.activity.PendingScreen;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.NewPendingAdapterBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.nic.samathuvapuram.activity.HomePage.db;


public class PendingScreenAdapter extends PagedListAdapter<ModelClass,PendingScreenAdapter.MyViewHolder> implements Filterable {
    private List<ModelClass> pendingListValues;
    private List<ModelClass> pendingListFiltered;
    private String letter;
    private Context context;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private com.nic.samathuvapuram.dataBase.dbData dbData;
    private PrefManager prefManager;
    ArrayList<ModelClass> imageCount;
    private LayoutInflater layoutInflater;
    String image_sql="";

    private static DiffUtil.ItemCallback<ModelClass> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ModelClass>() {
                @Override
                public boolean areItemsTheSame(ModelClass oldItem, ModelClass newItem) {
                    return oldItem.getSamathuvapuram_id() == newItem.getSamathuvapuram_id();
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(ModelClass oldItem, ModelClass newItem) {
                    return oldItem.equals(newItem);
                }
            };
    public PendingScreenAdapter(Context context, List<ModelClass> pendingListValues) {
        super(DIFF_CALLBACK);
        this.context = context;
        prefManager = new PrefManager(context);
        dbData = new dbData(context);
        this.pendingListValues = pendingListValues;
        this.pendingListFiltered = pendingListValues;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private NewPendingAdapterBinding pendingScreenAdapterBinding;

        public MyViewHolder(NewPendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingScreenAdapterBinding = Binding;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        NewPendingAdapterBinding pendingScreenAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.new_pending_adapter, viewGroup, false);
        return new MyViewHolder(pendingScreenAdapterBinding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.pendingScreenAdapterBinding.samathuvapuramId.setText(String.valueOf(pendingListFiltered.get(position).getSamathuvapuram_id()));

        if(prefManager.getUsertype().equals("bdo")){
            holder.pendingScreenAdapterBinding.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_house));

            holder.pendingScreenAdapterBinding.houseNoLayout.setVisibility(View.VISIBLE);
            holder.pendingScreenAdapterBinding.beneficiaryLayout.setVisibility(View.VISIBLE);
            holder.pendingScreenAdapterBinding.currentHouseUsageLayout.setVisibility(View.VISIBLE);
            holder.pendingScreenAdapterBinding.conditionOfHouseLayout.setVisibility(View.GONE);
            holder.pendingScreenAdapterBinding.conditionOfInfraLayout.setVisibility(View.GONE);
            holder.pendingScreenAdapterBinding.schemeLayout.setVisibility(View.GONE);
            holder.pendingScreenAdapterBinding.workLayout.setVisibility(View.GONE);
            holder.pendingScreenAdapterBinding.currentNameOfTheBeneficiary.setText(String.valueOf(pendingListFiltered.get(position).getCurrent_name_of_the_beneficiary()));
            holder.pendingScreenAdapterBinding.currentHouseUsage.setText(String.valueOf(pendingListFiltered.get(position).getCurrent_house_usage()));
            holder.pendingScreenAdapterBinding.houseSerialNumber.setText(String.valueOf(pendingListFiltered.get(position).getHouse_serial_number()));

        }else if(prefManager.getUsertype().equals("ae")){
            if(pendingListFiltered.get(position).getType().equals("INFRA")){
                holder.pendingScreenAdapterBinding.img.setImageDrawable(context.getResources().getDrawable(R.drawable.infrastructure));
                holder.pendingScreenAdapterBinding.houseNoLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.beneficiaryLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.currentHouseUsageLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.conditionOfHouseLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.conditionOfInfraLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.schemeLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.workLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.conditionOfInfra.setText(String.valueOf(pendingListFiltered.get(position).getCondition_of_infra()));
                holder.pendingScreenAdapterBinding.scheme.setText(String.valueOf(pendingListFiltered.get(position).getScheme_name()));
                holder.pendingScreenAdapterBinding.work.setText(String.valueOf(pendingListFiltered.get(position).getWork_name()));

            }else {
                holder.pendingScreenAdapterBinding.img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_house));

                holder.pendingScreenAdapterBinding.beneficiaryLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.currentHouseUsageLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.conditionOfHouseLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.conditionOfInfraLayout.setVisibility(View.GONE);
                holder.pendingScreenAdapterBinding.schemeLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.workLayout.setVisibility(View.VISIBLE);
                holder.pendingScreenAdapterBinding.scheme.setText(String.valueOf(pendingListFiltered.get(position).getScheme_name()));
                holder.pendingScreenAdapterBinding.work.setText(String.valueOf(pendingListFiltered.get(position).getWork_name()));
                holder.pendingScreenAdapterBinding.conditionOfHouse.setText(String.valueOf(pendingListFiltered.get(position).getCondition_of_house()));
                holder.pendingScreenAdapterBinding.houseSerialNumber.setText(String.valueOf(pendingListFiltered.get(position).getHouse_serial_number()));

            }

        }

        dbData.open();
        if(pendingListFiltered.get(position).getType().equals("INFRA")){
            imageCount = dbData.getParticularSavedInfraImage(String.valueOf(pendingListFiltered.get(position).getSamathuvapuram_id())
                    ,String.valueOf(pendingListFiltered.get(position).getScheme_group_id()) ,
                    String.valueOf(pendingListFiltered.get(position).getScheme_id()) ,
                    String.valueOf(pendingListFiltered.get(position).getWork_group_id()) ,
                    String.valueOf(pendingListFiltered.get(position).getWork_type_id()));

        }else {
            imageCount = dbData.getParticularSavedHouseImage(String.valueOf(pendingListFiltered.get(position).getSamathuvapuram_id()),String.valueOf(pendingListFiltered.get(position).getHouse_serial_number()));
        }
        if(imageCount.size() > 0) {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }

        holder.pendingScreenAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pendingListFiltered.get(position).getType().equals("INFRA")){
                    viewOfflineInfraImages(String.valueOf(pendingListFiltered.get(position).getSamathuvapuram_id())
                            ,String.valueOf(pendingListFiltered.get(position).getScheme_group_id()) ,
                            String.valueOf(pendingListFiltered.get(position).getScheme_id()) ,
                            String.valueOf(pendingListFiltered.get(position).getWork_group_id()) ,
                            String.valueOf(pendingListFiltered.get(position).getWork_type_id()));

                }else {
                    viewOfflineHouseImages(String.valueOf(pendingListFiltered.get(position).getSamathuvapuram_id()),String.valueOf(pendingListFiltered.get(position).getHouse_serial_number()));

                }

            }
        });

        holder.pendingScreenAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                if (Utils.isOnline()) {
                    try {
                        final Dialog dialog = new Dialog(activity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.alert_dialog);

                        TextView text = (TextView) dialog.findViewById(R.id.tv_message);
                        text.setText("Are you sure to upload data into server?");

                        Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                        cancel.setVisibility(View.VISIBLE);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadPending(position);
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Utils.showAlert(activity, "Turn On Mobile Data To Synchronize!");
                }
            }
        });
        holder.pendingScreenAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                try {
                    final Dialog dialog = new Dialog(activity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.alert_dialog);

                    TextView text = (TextView) dialog.findViewById(R.id.tv_message);
                    text.setText("Are you sure to delete data from local?");

                    Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
                    Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                    cancel.setVisibility(View.VISIBLE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeSavedItem(position);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void uploadPending(int position) {
        String key="";
        JSONObject maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String samathuvapuram_id = String.valueOf(pendingListValues.get(position).getSamathuvapuram_id());
        String house_serial_number = String.valueOf(pendingListValues.get(position).getHouse_serial_number());
        String scheme_group_id = String.valueOf(pendingListValues.get(position).getScheme_group_id());
        String scheme_id = String.valueOf(pendingListValues.get(position).getScheme_id());
        String work_group_id = String.valueOf(pendingListValues.get(position).getWork_group_id());
        String work_type_id = String.valueOf(pendingListValues.get(position).getWork_type_id());


        prefManager.setSamathuvapuramId(Integer.valueOf(samathuvapuram_id));
        prefManager.setHouse_serial_number(Integer.valueOf(house_serial_number));
        prefManager.setscheme_group_id(Integer.valueOf(scheme_group_id));
        prefManager.setscheme_id(Integer.valueOf(scheme_id));
        prefManager.setwork_group_id(Integer.valueOf(work_group_id));
        prefManager.setwork_type_id(Integer.valueOf(work_type_id));
        prefManager.setDeleteAdapterPosition(position);
        try {
            if(prefManager.getUsertype().equals("bdo")){
                key="house_details";
                maindataset.put(AppConstant.KEY_SERVICE_ID,"blk_bdo_samathuvapuram_current_beneficiary_house_update");
                dataset.put("samathuvapuram_id", samathuvapuram_id);
                dataset.put("house_serial_number", house_serial_number);
                dataset.put("is_house_owned_by_sanctioned_beneficiary", pendingListValues.get(position).getIs_house_owned_by_sanctioned_beneficiary());
                dataset.put("current_house_usage", pendingListValues.get(position).getCurrent_usage_id());
                dataset.put("current_name_of_the_beneficiary", pendingListValues.get(position).getCurrent_name_of_the_beneficiary());
                dataset.put("current_gender", pendingListValues.get(position).getCurrent_gender());
                dataset.put("current_community_category_id", pendingListValues.get(position).getCurrent_community_category_id());
            }else if(prefManager.getUsertype().equals("ae")){
                if(pendingListFiltered.get(position).getType().equals("INFRA")){
                    key="infra_details";
                    maindataset.put(AppConstant.KEY_SERVICE_ID,"blk_ae_samathuvapuram_infra_update");
                    dataset.put("samathuvapuram_id", samathuvapuram_id);
                    dataset.put("condition_of_infra_id", pendingListValues.get(position).getCondition_of_infra_id());
                    dataset.put("scheme_group_id", pendingListValues.get(position).getScheme_group_id());
                    dataset.put("scheme_id", pendingListValues.get(position).getScheme_id());
                    dataset.put("work_group_id", pendingListValues.get(position).getWork_group_id());
                    dataset.put("work_type_id", pendingListValues.get(position).getWork_type_id());
                    dataset.put("estimate_cost_required", pendingListValues.get(position).getEstimate_cost_required());
                }else {
                    key="house_details";
                    maindataset.put(AppConstant.KEY_SERVICE_ID,"blk_ae_samathuvapuram_repair_house_update");
                    dataset.put("samathuvapuram_id", samathuvapuram_id);
                    dataset.put("house_serial_number", house_serial_number);
                    dataset.put("condition_of_house_id", pendingListValues.get(position).getCondition_of_house_id());
                    dataset.put("scheme_group_id", pendingListValues.get(position).getScheme_group_id());
                    dataset.put("scheme_id", pendingListValues.get(position).getScheme_id());
                    dataset.put("work_group_id", pendingListValues.get(position).getWork_group_id());
                    dataset.put("work_type_id", pendingListValues.get(position).getWork_type_id());
                    dataset.put("estimate_cost_required", pendingListValues.get(position).getEstimate_cost_required());
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageArray = new JSONArray();
        dbData.open();
        if(pendingListFiltered.get(position).getType().equals("INFRA")){
             image_sql = "Select * from " + DBHelper.HOUSE_IMAGES_DETAILS + " where samathuvapuram_id =" + samathuvapuram_id
                     +" and scheme_group_id = "+scheme_group_id
                     +" and scheme_id = "+scheme_id
                     +" and work_group_id = "+work_group_id
                     +" and work_type_id = "+work_type_id ;

        }else {
             image_sql = "Select * from " + DBHelper.HOUSE_IMAGES_DETAILS + " where samathuvapuram_id =" + samathuvapuram_id+" and house_serial_number = "+house_serial_number ;

        }
            Log.d("sql", image_sql);
            Cursor image = db.rawQuery(image_sql, null);

            if (image.getCount() > 0) {
                if (image.moveToFirst()) {
                    do {
                        String latitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LATITUDE));
                        String longitude = image.getString(image.getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE));
                        String image_path = image.getString(image.getColumnIndexOrThrow("image_path"));
                        String image_string = imageString(image_path);
                        int photo_type_id = image.getInt(image.getColumnIndexOrThrow("photo_type_id"));
                        int image_serial_number = image.getInt(image.getColumnIndexOrThrow("image_serial_number"));

                        JSONObject imageJson = new JSONObject();

                        try {
                            imageJson.put("photo_serial_no",image_serial_number);
                            imageJson.put("photo_type_id",photo_type_id);
                            imageJson.put(AppConstant.KEY_LATITUDE,latitude);
                            imageJson.put(AppConstant.KEY_LONGITUDE,longitude);
                            imageJson.put(AppConstant.KEY_IMAGE,image_string);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        imageArray.put(imageJson);

                    } while (image.moveToNext());
                }
            }


        try {
            dataset.put("house_images", imageArray);
            dataArray.put( dataset);
            maindataset.put(key, dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            ((PendingScreen)context).savePMAYImagesJsonParams(maindataset,pendingListFiltered.get(position).getType());
            Log.d("saveImages", "" + maindataset);
        } else {
            Activity activity = (Activity) context;
            Utils.showAlert(activity, "Turn On Mobile Data To Utpload");
        }

    }
    private String imageString(String file_path){
        String image_string = "";
        File imgFile = new File(file_path);

        if(imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image_string = BitMapToString(myBitmap);

        }
        return image_string;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void removeSavedItem(int position) {
        dbData.open();
        if(pendingListFiltered.get(position).getType().equals("INFRA")){
            db.delete(DBHelper.HOUSE_IMAGES_DETAILS, "samathuvapuram_id = ? and scheme_group_id = ? and scheme_id = ? and work_group_id = ? and work_type_id = ?", new String[]{String.valueOf(pendingListValues.get(position).getSamathuvapuram_id()), String.valueOf(pendingListValues.get(position).getScheme_group_id()),
                    String.valueOf(pendingListValues.get(position).getScheme_id()),
                    String.valueOf(pendingListValues.get(position).getWork_group_id()),
                    String.valueOf(pendingListValues.get(position).getWork_type_id())});
            db.delete(DBHelper.HOUSE_DETAILS, "samathuvapuram_id = ? and scheme_group_id = ? and scheme_id = ? and work_group_id = ? and work_type_id = ?", new String[]{String.valueOf(pendingListValues.get(position).getSamathuvapuram_id()), String.valueOf(pendingListValues.get(position).getScheme_group_id()),
                    String.valueOf(pendingListValues.get(position).getScheme_id()),
                    String.valueOf(pendingListValues.get(position).getWork_group_id()),
                    String.valueOf(pendingListValues.get(position).getWork_type_id())});
        }else {
            db.delete(DBHelper.HOUSE_IMAGES_DETAILS, "samathuvapuram_id = ? and house_serial_number = ?", new String[]{String.valueOf(pendingListValues.get(position).getSamathuvapuram_id()), String.valueOf(pendingListValues.get(position).getHouse_serial_number())});
            db.delete(DBHelper.HOUSE_DETAILS, "samathuvapuram_id = ? and house_serial_number = ?", new String[]{String.valueOf(pendingListValues.get(position).getSamathuvapuram_id()), String.valueOf(pendingListValues.get(position).getHouse_serial_number())});

        }

        pendingListFiltered.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
    }


    public void viewOfflineInfraImages(String samathuvapuram_id,String Scheme_group_id,String Scheme_id,String Work_group_id,String Work_type_id) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("samathuvapuram_id",samathuvapuram_id);
        intent.putExtra("Type","INFRA");
        intent.putExtra("scheme_group_id",Scheme_group_id);
        intent.putExtra("scheme_id",Scheme_id);
        intent.putExtra("work_group_id",Work_group_id);
        intent.putExtra("work_type_id",Work_type_id);
        intent.putExtra("OnOffType","Offline");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    public void viewOfflineHouseImages(String samathuvapuram_id,String house_serial_number) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("samathuvapuram_id",samathuvapuram_id);
        intent.putExtra("Type","HOUSE");
        intent.putExtra("house_serial_number",house_serial_number);
        intent.putExtra("OnOffType","Offline");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    pendingListFiltered = pendingListValues;
                } else {
                    List<ModelClass> filteredList = new ArrayList<>();
                    for (ModelClass row : pendingListValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getPvName().toLowerCase().contains(charString.toLowerCase()) || row.getPvName().toLowerCase().contains(charString.toUpperCase())) {
                            filteredList.add(row);
                        }
                    }

                    pendingListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = pendingListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pendingListFiltered = (ArrayList<ModelClass>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return pendingListFiltered == null ? 0 : pendingListFiltered.size();
    }
}

