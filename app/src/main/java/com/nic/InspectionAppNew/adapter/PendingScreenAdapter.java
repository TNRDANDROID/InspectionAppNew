package com.nic.InspectionAppNew.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.FullImageActivity;
import com.nic.InspectionAppNew.activity.PendingScreen;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.NewPendingAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PendingScreenAdapter extends PagedListAdapter<ModelClass,PendingScreenAdapter.MyViewHolder> implements Filterable {
    private List<ModelClass> pendingListValues;
    private List<ModelClass> pendingListFiltered;
    private String letter;
    private Context context;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private com.nic.InspectionAppNew.dataBase.dbData dbData;
    private PrefManager prefManager;
    ArrayList<ModelClass> imageCount;
    private LayoutInflater layoutInflater;
    String image_sql="";
    public DBHelper dbHelper;
    public SQLiteDatabase db;

    private static DiffUtil.ItemCallback<ModelClass> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ModelClass>() {
                @Override
                public boolean areItemsTheSame(ModelClass oldItem, ModelClass newItem) {
                    return oldItem.getWork_id() == newItem.getWork_id();
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
        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        holder.pendingScreenAdapterBinding.workId.setText(String.valueOf(pendingListFiltered.get(position).getWork_id()));
        holder.pendingScreenAdapterBinding.workName.setText(pendingListFiltered.get(position).getWork_name());
        holder.pendingScreenAdapterBinding.workStatus.setText(pendingListFiltered.get(position).getWork_status());
        holder.pendingScreenAdapterBinding.finYear.setText(pendingListFiltered.get(position).getFinancialYear());
        holder.pendingScreenAdapterBinding.asValue.setText(pendingListFiltered.get(position).getAs_value());
        holder.pendingScreenAdapterBinding.tsValue.setText(pendingListFiltered.get(position).getTs_value());

        if(String.valueOf(pendingListFiltered.get(position).getWork_name()).length() > 5) {
            Utils.addReadMore(context, "Activity : "+String.valueOf(pendingListFiltered.get(position).getWork_name()), holder.pendingScreenAdapterBinding.workName, 0);
        }
        dbData.open();
        imageCount = dbData.getParticularSavedImagebycode("all",String.valueOf(pendingListFiltered.get(position).getDistrictCode()),String.valueOf(pendingListFiltered.get(position).getBlockCode()),
                String.valueOf(pendingListFiltered.get(position).getPvCode()),String.valueOf(pendingListFiltered.get(position).getWork_id()),"");

        if(imageCount.size() > 0) {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }

        holder.pendingScreenAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOfflineImages(String.valueOf(pendingListFiltered.get(position).getSave_work_details_primary_id()),position);
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

        holder.pendingScreenAdapterBinding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SaveWorkDetailsActivity.class);
                intent.putExtra("dcode", pendingListFiltered.get(position).getDistrictCode());
                intent.putExtra("bcode", pendingListFiltered.get(position).getBlockCode());
                intent.putExtra("pvcode", pendingListFiltered.get(position).getPvCode());
                intent.putExtra("hab_code", pendingListFiltered.get(position).getHabCode());
                intent.putExtra("scheme_group_id", pendingListFiltered.get(position).getScheme_group_id());
                intent.putExtra("work_group_id", pendingListFiltered.get(position).getWork_group_id());
                intent.putExtra("work_type_id", pendingListFiltered.get(position).getWork_type_id());
                intent.putExtra("scheme_id", pendingListFiltered.get(position).getSchemeSequentialID());
                intent.putExtra("fin_year", pendingListFiltered.get(position).getFinancialYear());
                intent.putExtra("work_id", pendingListFiltered.get(position).getWork_id());
                intent.putExtra("work_name", pendingListFiltered.get(position).getWork_name());
                intent.putExtra("as_value", pendingListFiltered.get(position).getAs_value());
                intent.putExtra("ts_value", pendingListFiltered.get(position).getTs_value());
                intent.putExtra("current_stage_of_work", pendingListFiltered.get(position).getCurrent_stage_of_work());
                intent.putExtra("is_high_value", pendingListFiltered.get(position).getIs_high_value());
                intent.putExtra("onOffType","offline");
                intent.putExtra("other_work_detail",pendingListFiltered.get(position).getOther_work_detail());
                intent.putExtra("flag","");
                intent.putExtra("type","rdpr");
                context.startActivity(intent);
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
        JSONArray inspection_work_details = new JSONArray();
        String work_id = String.valueOf(pendingListFiltered.get(position).getWork_id());
        String status_id = String.valueOf(pendingListFiltered.get(position).getWork_status_id());
        String description = String.valueOf(pendingListFiltered.get(position).getWork_description());
        String work_group_id = String.valueOf(pendingListFiltered.get(position).getWork_group_id());
        String work_type_id = String.valueOf(pendingListFiltered.get(position).getWork_type_id());
        String work_stage_code = String.valueOf(pendingListFiltered.get(position).getWork_stage_code());
        String save_work_details_primary_id = String.valueOf(pendingListFiltered.get(position).getSave_work_details_primary_id());

        prefManager.setWorkId(work_id);
        prefManager.setDeleteAdapterPosition(position);
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"work_inspection_details_save");
            dataset.put("dcode", pendingListFiltered.get(position).getDistrictCode());
            dataset.put("bcode", pendingListFiltered.get(position).getBlockCode());
            dataset.put("pvcode", pendingListFiltered.get(position).getPvCode());
            dataset.put("hab_code", pendingListFiltered.get(position).getHabCode());
            dataset.put("work_id", work_id);
            dataset.put("status_id", status_id);
            dataset.put("description", description);
            dataset.put("work_group_id", work_group_id);
            dataset.put("work_type_id", work_type_id);
            dataset.put("work_stage_code", work_stage_code);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageArray = new JSONArray();
        dbData.open();
        ArrayList<ModelClass> imageList = new ArrayList<>();
        imageList.addAll(dbData.getParticularSavedImagebycode("all",String.valueOf(pendingListFiltered.get(position).getDistrictCode()),String.valueOf(pendingListFiltered.get(position).getBlockCode()),
                String.valueOf(pendingListFiltered.get(position).getPvCode()),String.valueOf(pendingListFiltered.get(position).getWork_id()),""));
        try {
            for (int i=0;i<imageList.size();i++){

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("latitude",imageList.get(i).getLatitude());
                jsonObject.put("longitude",imageList.get(i).getLongtitude());
                jsonObject.put("serial_no",imageList.get(i).getImage_serial_number());
                jsonObject.put("image_description",imageList.get(i).getDescription());
                jsonObject.put("image",BitMapToString(imageList.get(i).getImage()));
                imageArray.put(jsonObject);
            }
            dataset.put("image_details",imageArray);
            inspection_work_details.put(dataset);
            maindataset.put("inspection_work_details",inspection_work_details);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            ((PendingScreen)context).saveImagesJsonParams(maindataset,String.valueOf(pendingListFiltered.get(position).getDistrictCode()),String.valueOf(pendingListFiltered.get(position).getBlockCode()),
                    String.valueOf(pendingListFiltered.get(position).getPvCode()),String.valueOf(pendingListFiltered.get(position).getWork_id()));
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
        String save_work_details_primary_id = String.valueOf(pendingListFiltered.get(position).getSave_work_details_primary_id());
        dbData.open();
        db.delete(DBHelper.SAVE_WORK_DETAILS, "work_id = ?", new String[]{String.valueOf(pendingListFiltered.get(position).getWork_id())});
        db.delete(DBHelper.SAVE_IMAGES, "dcode = ? and bcode = ? and pvcode = ? and work_id = ?",
                new String[]{String.valueOf(pendingListFiltered.get(position).getDistrictCode()),String.valueOf(pendingListFiltered.get(position).getBlockCode()),String.valueOf(pendingListFiltered.get(position).getPvCode()),String.valueOf(pendingListFiltered.get(position).getWork_id())});
        deleteSavedImage(String.valueOf(pendingListFiltered.get(position).getDistrictCode()),String.valueOf(pendingListFiltered.get(position).getBlockCode()),String.valueOf(pendingListFiltered.get(position).getPvCode()),String.valueOf(pendingListFiltered.get(position).getWork_id()));
        pendingListFiltered.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListFiltered.size());
    }

    public void viewOfflineImages(String save_work_details_primary_id,int position) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("save_work_details_primary_id",save_work_details_primary_id);
        intent.putExtra("work_id",String.valueOf(pendingListFiltered.get(position).getWork_id()));
        intent.putExtra("dcode",String.valueOf(pendingListFiltered.get(position).getDistrictCode()));
        intent.putExtra("bcode",String.valueOf(pendingListFiltered.get(position).getBlockCode()));
        intent.putExtra("pvcode",String.valueOf(pendingListFiltered.get(position).getPvCode()));
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
                        if (
                                row.getWork_name().toLowerCase().contains(charString.toLowerCase()) ||
                                        row.getWork_name().toLowerCase().contains(charString.toUpperCase())||
                                        String.valueOf(row.getWork_id()).toLowerCase().contains(charString.toUpperCase())
                        ) {
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

    private void deleteSavedImage(String dcode,String bcode,String pvcode,String work_id) {
        ArrayList<ModelClass> activityImage = new ArrayList<>();
        activityImage = dbData.getParticularSavedImagebycode("all",dcode,bcode, pvcode,work_id,"");
        for (int i=0; i < activityImage.size();i++){
            String file_path= activityImage.get(i).getImage_path();
            deleteFileDirectory(file_path);
        }

    }
    private void deleteFileDirectory(String file_path){
        File file = new File(file_path);
        // call deleteDirectory method to delete directory
        // recursively
        file.delete();

    }

}

