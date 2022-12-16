package com.nic.InspectionAppNew.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.InspectionAppNew.Interface.AdapterCameraIntent;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.activity.SaveWorkDetailsActivity;
import com.nic.InspectionAppNew.databinding.SaveImagesAdapterBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.MyLocationListener;
import com.nic.InspectionAppNew.utils.CameraUtils;
import com.nic.InspectionAppNew.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SaveImageAdapter extends RecyclerView.Adapter<SaveImageAdapter.MyViewHolder> implements AdapterCameraIntent {

    private static Activity context;
    private PrefManager prefManager;
    private ArrayList<ModelClass> list;
    private LayoutInflater layoutInflater;
    String flag="";
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_FINE_LOCATION = 300;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    int i=0;
    int clicked_position=0;
    int clicked_position_des=0;
    MyViewHolder myholder;
    String data="";
    AdapterCameraIntent adapterCameraIntent;


    public SaveImageAdapter(Activity context, ArrayList<ModelClass> listValues,String flag,String data) {

        this.context = context;
        prefManager = new PrefManager(context);
        this.list = listValues;
        this.flag = flag;
        this.data = data;
        mlocManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        adapterCameraIntent=this;
    }

    @Override
    public SaveImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        SaveImagesAdapterBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.save_images_adapter, viewGroup, false);
        return new SaveImageAdapter.MyViewHolder(binding);

    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SaveImagesAdapterBinding binding;

        public MyViewHolder(SaveImagesAdapterBinding Binding) {
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
                    getLatLong(holder);
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
                    getLatLong(holder);
                }

            }
        });
        holder.binding.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SaveWorkDetailsActivity)context).callTextAnswer(position);

            }
        });


    }

    public void getLatLong(MyViewHolder holder) {
        myholder=holder;
            mlocManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            mlocListener = new MyLocationListener();
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(true);
            criteria.setBearingRequired(false);

            //API level 9 and up
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            Integer gpsFreqInMillis = 1000;
            Integer gpsFreqInDistance = 1;

            // permission was granted, yay! Do the
            // location-related task you need to do.
            if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Request location updates:
                //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

            }

            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ((SaveWorkDetailsActivity)context).getpermission(1);
                    }else {
                        getLocation(myholder);
                    }

                } else {
                    if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ((SaveWorkDetailsActivity)context).getpermission(2);

                    }else {
                        getLocation(myholder);
                    }
                }

            } else {
                Utils.showAlert(context, context.getResources().getString(R.string.gps_not_turned_on));
            }

    }


    public void getLocation(MyViewHolder holder) {
        int count=5;
        mlocManager = (LocationManager)context. getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;

        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }
        if (MyLocationListener.latitude > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              /*  if (CameraUtils.checkPermissions(context)) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }*/
              captureImage();
//                            checkPermissionForCamera();
            } else {
                captureImage();
            }
        } else {
            if(i<count){
                i++;
                getLocation(holder);
            }else {
                Utils.showAlert(context, context.getResources().getString(R.string.satellite_communication_not_available));

            }
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(context)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.permissions_required))
                .setMessage(context.getResources().getString(R.string.camera_needs_few_permission))
                .setPositiveButton(context.getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(context);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    private void captureImage() {
        ((SaveWorkDetailsActivity)context).getExactLocation();

     /*   if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }*/
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
    public void OnIntentListenerPermission(boolean flag) {
        if(flag){
            getLocation(myholder);
        }
    }

    @Override
    public void OnIntentListenerDescription(String s) {
//        list.get(clicked_position_des).setDescription(s);
    }


    @Override
    public int getItemCount() {

        return Integer.parseInt(prefManager.getPhotoCount());
//        return list.size();
    }

}
