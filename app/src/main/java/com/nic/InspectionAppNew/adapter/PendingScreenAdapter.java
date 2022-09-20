package com.nic.InspectionAppNew.adapter;

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
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.FullImageActivity;
import com.nic.InspectionAppNew.activity.PendingScreen;
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

import static com.nic.InspectionAppNew.activity.WorkList.db;


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


        dbData.open();
        imageCount = dbData.getParticularSavedImage(String.valueOf(pendingListFiltered.get(position).getWork_id()));

        if(imageCount.size() > 0) {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.VISIBLE);
        }
        else {
            holder.pendingScreenAdapterBinding.viewOfflineImages.setVisibility(View.GONE);
        }

        holder.pendingScreenAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOfflineImages(String.valueOf(pendingListFiltered.get(position).getWork_id()));


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
        String work_id = String.valueOf(pendingListValues.get(position).getWork_id());

        prefManager.setWorkId(work_id);
        prefManager.setDeleteAdapterPosition(position);
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"work_update");
            dataset.put("work_id", work_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray imageArray = new JSONArray();
        dbData.open();
        image_sql = "Select * from " + DBHelper.SAVE_IMAGES + " where work_id =" + work_id;

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
            dataset.put("work_images", imageArray);
            dataArray.put( dataset);
            maindataset.put(key, dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            ((PendingScreen)context).saveImagesJsonParams(maindataset);
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
        db.delete(DBHelper.SAVE_WORK_DETAILS, "work_id = ?", new String[]{String.valueOf(pendingListValues.get(position).getWork_id())});
        db.delete(DBHelper.SAVE_IMAGES, "work_id = ?", new String[]{String.valueOf(pendingListValues.get(position).getWork_id())});

        pendingListFiltered.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
    }

    public void viewOfflineImages(String work_id) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("work_id",work_id);
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

