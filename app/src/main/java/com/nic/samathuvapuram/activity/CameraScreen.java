package com.nic.samathuvapuram.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.samathuvapuram.R;
import com.nic.samathuvapuram.api.Api;
import com.nic.samathuvapuram.api.ServerResponse;
import com.nic.samathuvapuram.constant.AppConstant;
import com.nic.samathuvapuram.dataBase.DBHelper;
import com.nic.samathuvapuram.dataBase.dbData;
import com.nic.samathuvapuram.databinding.CameraScreenBinding;
import com.nic.samathuvapuram.model.ModelClass;
import com.nic.samathuvapuram.session.PrefManager;
import com.nic.samathuvapuram.support.MyLocationListener;
import com.nic.samathuvapuram.utils.CameraUtils;
import com.nic.samathuvapuram.utils.Utils;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class CameraScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    private PrefManager prefManager;
    private CameraScreenBinding cameraScreenBinding;





    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private com.nic.samathuvapuram.dataBase.dbData dbData = new dbData(this);


    ///Image With Description
    ImageView imageView, image_view_preview;
    TextView latitude_text, longtitude_text;
    EditText myEditTextView;
    private List<View> viewArrayList = new ArrayList<>();
    private List<ModelClass> viewList = new ArrayList<>();

    int samathuvapuram_id;
    int house_serial_number;
    String current_house_usage;
    int current_house_usage_id;
    String condition_of_infra;
    int condition_of_infra_id;
    String is_house_owned_by_sanctioned_beneficiary="";
    String current_name_of_the_beneficiary="";
    String current_gender="";
    String current_community_category_id="";
    int photo_type_id;
    int min_img_count;
    int max_img_count;

    int condition_of_house_id;
    int scheme_group_id;
    int scheme_id;
    int work_group_id;
    int work_type_id;
    String estimate_cost_required;
    String condition_of_house;
    String Type="";
    String scheme="";
    String work="";
    ArrayList<ModelClass> imageCount;
    int clicked_position =0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraScreenBinding = DataBindingUtil.setContentView(this, R.layout.camera_screen);
        cameraScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageCount=new ArrayList<>();
        intializeUI();

        cameraScreenBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageButtonClick();
            }
        });
    }

    public void intializeUI() {
        Type= getIntent().getStringExtra("Type");

        if(Type.equals("INFRA")){
            samathuvapuram_id= getIntent().getIntExtra("samathuvapuram_id",0);
            scheme_group_id= getIntent().getIntExtra("scheme_group_id",0);
            scheme_id= getIntent().getIntExtra("scheme_id",0);
            scheme= getIntent().getStringExtra("scheme");
            work= getIntent().getStringExtra("work");
            work_group_id= getIntent().getIntExtra("work_group_id",0);
            work_type_id= getIntent().getIntExtra("work_type_id",0);
            condition_of_infra= getIntent().getStringExtra("condition_of_infra");
            condition_of_infra_id= getIntent().getIntExtra("condition_of_infra_id",0);
            estimate_cost_required= getIntent().getStringExtra("estimate_cost_required");
            photo_type_id= getIntent().getIntExtra("photo_type_id",0);
            min_img_count= getIntent().getIntExtra("min_no_of_photos",0);
            max_img_count= getIntent().getIntExtra("max_no_of_photos",0);
        }else {
            if(prefManager.getUsertype().equals("bdo")){
                samathuvapuram_id= getIntent().getIntExtra("samathuvapuram_id",0);
                house_serial_number= getIntent().getIntExtra("house_serial_number",0);
                current_house_usage_id= getIntent().getIntExtra("current_house_usage_id",0);
                current_house_usage= getIntent().getStringExtra("current_house_usage");
                is_house_owned_by_sanctioned_beneficiary= getIntent().getStringExtra("is_house_owned_by_sanctioned_beneficiary");
                current_name_of_the_beneficiary= getIntent().getStringExtra("current_name_of_the_beneficiary");
                current_gender= getIntent().getStringExtra("current_gender");
                current_community_category_id= getIntent().getStringExtra("current_community_category_id");
                photo_type_id= getIntent().getIntExtra("photo_type_id",0);
                min_img_count= getIntent().getIntExtra("min_no_of_photos",0);
                max_img_count= getIntent().getIntExtra("max_no_of_photos",0);
            }else  if(prefManager.getUsertype().equals("ae")){
                condition_of_house= getIntent().getStringExtra("condition_of_house");
                samathuvapuram_id= getIntent().getIntExtra("samathuvapuram_id",0);
                house_serial_number= getIntent().getIntExtra("house_serial_number",0);
                scheme_group_id= getIntent().getIntExtra("scheme_group_id",0);
                scheme_id= getIntent().getIntExtra("scheme_id",0);
                scheme= getIntent().getStringExtra("scheme");
                work= getIntent().getStringExtra("work");
                work_group_id= getIntent().getIntExtra("work_group_id",0);
                work_type_id= getIntent().getIntExtra("work_type_id",0);
                condition_of_house_id= getIntent().getIntExtra("condition_of_house_id",0);
                estimate_cost_required= getIntent().getStringExtra("estimate_cost_required");
                photo_type_id= getIntent().getIntExtra("photo_type_id",0);
                min_img_count= getIntent().getIntExtra("min_no_of_photos",0);
                max_img_count= getIntent().getIntExtra("max_no_of_photos",0);
            }
        }


        viewArrayList.clear();


        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        cameraScreenBinding.btnSaveLocal.setOnClickListener(this::onClick);
        cameraScreenBinding.imageCountTv.setText("You Can Capture up to "+max_img_count+" photos");

        dbData.open();
        if(Type.equals("INFRA")){
            imageCount = dbData.getParticularSavedInfraImage(String.valueOf(samathuvapuram_id)
                    ,String.valueOf(scheme_group_id) ,
                    String.valueOf(scheme_id) ,
                    String.valueOf(work_group_id) ,
                    String.valueOf(work_type_id));

        }else {
            imageCount = dbData.getParticularSavedHouseImage(String.valueOf(samathuvapuram_id),
                    String.valueOf(house_serial_number));
        }
        if(imageCount.size() > 0) {
            for(int i=0;i<imageCount.size();i++){
                updateView(CameraScreen.this,cameraScreenBinding.cameraLayout,imageCount.get(i).getImage_path(),"",imageCount.get(i).getLatitude(),imageCount.get(i).getLongtitude());
            }
        }
        else {
            updateView(CameraScreen.this,cameraScreenBinding.cameraLayout,"","","","");

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_local:
                int childCount = cameraScreenBinding.cameraLayout.getChildCount();
                if(childCount >= min_img_count){
                    saveImageButtonClick();
                }else {
                    Utils.showAlert(CameraScreen.this, getResources().getString(R.string.minimum_three_photos));

                }

                break;

        }
    }

    public static float distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        Log.d("DistMeter", "" + dist);
        return dist;
    }
    private void captureImage() {
        /*if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }*/
        Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CROP, false);//default is false
        startActivityForResult(intent, 1213);
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        if (ContextCompat.checkSelfPermission(CameraScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(CameraScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.satellite_communication_not_available));
            }
        } else {
            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.gps_not_turned_on));
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permissions_required))
                .setMessage(getResources().getString(R.string.camera_needs_few_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(CameraScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
            imageView.setImageBitmap(rotatedBitmap);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    int childCount = cameraScreenBinding.cameraLayout.getChildCount();
                    if (childCount > 0) {

                            View vv = cameraScreenBinding.cameraLayout.getChildAt(clicked_position);
                            imageView = vv.findViewById(R.id.image_view);
                            myEditTextView = vv.findViewById(R.id.description);
                            latitude_text = vv.findViewById(R.id.latitude);
                            longtitude_text = vv.findViewById(R.id.longtitude);
                        imageView.setImageBitmap(photo);
                        image_view_preview.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        latitude_text.setText("" + offlatTextValue);
                        longtitude_text.setText("" + offlongTextValue);

                        cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                        cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                        cameraScreenBinding.imageView.setImageBitmap(photo);
                    }
                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_cancelled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture_image), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if(requestCode == 1213){
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap rotatedBitmap = BitmapFactory.decodeFile(filePath);

            int childCount = cameraScreenBinding.cameraLayout.getChildCount();
            if (childCount > 0) {

                View vv = cameraScreenBinding.cameraLayout.getChildAt(clicked_position);
                imageView = vv.findViewById(R.id.image_view);
                myEditTextView = vv.findViewById(R.id.description);
                latitude_text = vv.findViewById(R.id.latitude);
                longtitude_text = vv.findViewById(R.id.longtitude);
                imageView.setImageBitmap(rotatedBitmap);
                image_view_preview.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                latitude_text.setText("" + offlatTextValue);
                longtitude_text.setText("" + offlongTextValue);

                cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
            }

           /* imageView.setImageBitmap(rotatedBitmap);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);

            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);*/
        }
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            }
            else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_cancelled_video_recording), Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_faild_to_record_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void saveImageButtonClick() {
        long house_primary_id = 0;
        String whereCl = "";String[] whereAr = null;

        long rowInsert = 0;
        long rowUpdat = 0;
        try {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String currentDateTimeString = df.format(c);
//            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());

            ContentValues values = new ContentValues();

            if(Type.equals("INFRA")){
                values.put("samathuvapuram_id", samathuvapuram_id);
                values.put("Type", "INFRA");
                values.put("condition_of_infra_id", condition_of_infra_id);
                values.put("condition_of_infra", condition_of_infra);
                values.put("scheme_group_id", scheme_group_id);
                values.put("scheme_id", scheme_id);
                values.put("scheme", scheme);
                values.put("work", work);
                values.put("work_group_id", work_group_id);
                values.put("work_type_id", work_type_id);
                values.put("estimate_cost_required", estimate_cost_required);
            }else {
                if(prefManager.getUsertype().equals("bdo")){
                    values.put("Type", "HOUSE");
                    values.put("is_house_owned_by_sanctioned_beneficiary", is_house_owned_by_sanctioned_beneficiary);
                    values.put("current_house_usage",current_house_usage);
                    values.put("current_house_usage_id",current_house_usage_id);
                    values.put("current_name_of_the_beneficiary", current_name_of_the_beneficiary);
                    values.put("current_gender", current_gender);
                    values.put("current_community_category_id", current_community_category_id);
                    values.put("samathuvapuram_id", samathuvapuram_id);
                    values.put("house_serial_number", house_serial_number);
                }else if(prefManager.getUsertype().equals("ae")){
                    values.put("Type", "HOUSE");
                    values.put("samathuvapuram_id", samathuvapuram_id);
                    values.put("house_serial_number", house_serial_number);
                    values.put("condition_of_house_id", condition_of_house_id);
                    values.put("condition_of_house", condition_of_house);
                    values.put("scheme", scheme);
                    values.put("work", work);
                    values.put("scheme_group_id", scheme_group_id);
                    values.put("scheme_id", scheme_id);
                    values.put("work_group_id", work_group_id);
                    values.put("work_type_id", work_type_id);
                    values.put("estimate_cost_required", estimate_cost_required);
                }
            }

            if(Type.equals("INFRA")){
                house_primary_id = db.insert(DBHelper.HOUSE_DETAILS, null, values);
            }else {
                dbData.open();
                whereCl = "samathuvapuram_id = ? and house_serial_number = ?";
                whereAr = new String[]{String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number)};

                ArrayList<ModelClass> count = dbData.getAll_Particular_houseDetails(String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number));

                if (count.size() >0) {
                    house_primary_id = db.update(DBHelper.HOUSE_DETAILS , values, whereCl, whereAr);

                }else {
                    house_primary_id = db.insert(DBHelper.HOUSE_DETAILS, null, values);
                }
            }


        } catch (Exception e) {

        }
        if (house_primary_id>0){
            long id = 0; String whereClause = "";String[] whereArgs = null;

            JSONArray imageJson = new JSONArray();
            long rowInserted = 0;
            long rowUpdated = 0;
            int childCount = cameraScreenBinding.cameraLayout.getChildCount();
            int count = 0;
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    JSONArray imageArray = new JSONArray();

                    View vv = cameraScreenBinding.cameraLayout.getChildAt(i);
                    imageView = vv.findViewById(R.id.image_view);
                    myEditTextView = vv.findViewById(R.id.description);
                    latitude_text = vv.findViewById(R.id.latitude);
                    longtitude_text = vv.findViewById(R.id.longtitude);


                    if (imageView.getDrawable() != null) {
                        //if(!myEditTextView.getText().toString().equals("")){
                        count = count + 1;
                        byte[] imageInByte = new byte[0];
                        String image_str = "";
                        String description = "";
                        String image_path = "";
                        try {
                            description = myEditTextView.getText().toString();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            imageInByte = baos.toByteArray();
                            image_path = fileDirectory(bitmap,"batch",String.valueOf(i));


                        } catch (Exception e) {
                            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.at_least_capture_one_photo));
                        }


                        ContentValues imageValue = new ContentValues();
                        imageValue.put("image_serial_number", count);
                        if(Type.equals("INFRA")){
                            imageValue.put("scheme_group_id", scheme_group_id);
                            imageValue.put("scheme_id", scheme_id);
                            imageValue.put("work_group_id", work_group_id);
                            imageValue.put("work_type_id", work_type_id);
                        }else {
                            imageValue.put("house_serial_number", house_serial_number);
                        }
                        imageValue.put("samathuvapuram_id", samathuvapuram_id);
                        imageValue.put("latitude", latitude_text.getText().toString());
                        imageValue.put("longitude", longtitude_text.getText().toString());
                        imageValue.put("image_path", image_path);
                        imageValue.put("photo_type_id", photo_type_id);
                        if(Type.equals("INFRA")){
                           /* dbData.open();
                            whereClause = "samathuvapuram_id = ? and scheme_group_id = ? and scheme_id = ? and work_group_id = ? and work_type_id = ?";
                            whereArgs = new String[]{String.valueOf(samathuvapuram_id),String.valueOf(scheme_group_id),String.valueOf(scheme_id),String.valueOf(work_group_id),String.valueOf(work_type_id)};

                            ArrayList<ModelClass> ImageCount = dbData.getParticularSavedInfraImage(String.valueOf(samathuvapuram_id),String.valueOf(scheme_group_id)
                                    ,String.valueOf(scheme_id),String.valueOf(work_group_id),String.valueOf(work_type_id));

                            if (ImageCount.size() >0) {
                                rowInserted = db.insert(DBHelper.HOUSE_IMAGES_DETAILS, null, imageValue);

                            }else {
                                rowUpdated = db.update(DBHelper.HOUSE_IMAGES_DETAILS , imageValue, whereClause, whereArgs);
                            }*/
                            rowInserted = db.insert(DBHelper.HOUSE_IMAGES_DETAILS, null, imageValue);
                        }else {
                            dbData.open();
                            whereClause = "samathuvapuram_id = ? and house_serial_number = ? and image_serial_number = ?";
                            whereArgs = new String[]{String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number),String.valueOf(count)};

                            ArrayList<ModelClass> ImageCount = dbData.getParticularSavedHouseImageLocal(String.valueOf(samathuvapuram_id),String.valueOf(house_serial_number),String.valueOf(count));

                            if (ImageCount.size() >0) {

                                rowUpdated = db.update(DBHelper.HOUSE_IMAGES_DETAILS , imageValue, whereClause, whereArgs);
                            }else {
                                rowInserted = db.insert(DBHelper.HOUSE_IMAGES_DETAILS, null, imageValue);

                            }
                        }



                        if (count == childCount) {
                            if (rowInserted > 0) {

                                showToast(getResources().getString(R.string.inserted_success));
                            }else if(rowUpdated > 0){
                                showToast(getResources().getString(R.string.updated_success));
                            }

                        }


                    } else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.please_capture_image));
                    }
                }
            }
        }
        focusOnView(cameraScreenBinding.scrollView);
    }
    public void addImageButtonClick(){
        if(viewArrayList.size() < max_img_count) {
            if (imageView.getDrawable() != null && viewArrayList.size() > 0) {
                updateView(CameraScreen.this, cameraScreenBinding.cameraLayout, "", "", "", "");
            } else {
                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.please_capture_image));
            }
        }
        else {
            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.maximum_three_photos));

        }
    }
    private final void focusOnView(final ScrollView your_scrollview) {
        your_scrollview.post(new Runnable() {
            @Override
            public void run() {
                your_scrollview.fullScroll(View.FOCUS_DOWN);

            }
        });
    }

    //Method for update single view based on email or mobile type
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout, final String values, final String type, final String latitude, final String longitude) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.image_with_description, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        final LinearLayout description_layout = (LinearLayout) hiddenInfo.findViewById(R.id.description_layout);
        imageView = (ImageView) hiddenInfo.findViewById(R.id.image_view);
        image_view_preview = (ImageView) hiddenInfo.findViewById(R.id.image_view_preview);
        myEditTextView = (EditText) hiddenInfo.findViewById(R.id.description);
        latitude_text = hiddenInfo.findViewById(R.id.latitude);
        longtitude_text = hiddenInfo.findViewById(R.id.longtitude);
        description_layout.setVisibility(View.GONE);

//        imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_phone_camera));
        if(values!=null && !values.equals("") && !values.isEmpty()){

            offlatTextValue= Double.valueOf(latitude);
            offlongTextValue= Double.valueOf(longitude);
            File imgFile = new File(values);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                image_view_preview.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                latitude_text.setText(""+offlatTextValue);
                longtitude_text.setText(""+offlongTextValue);

                cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                cameraScreenBinding.imageView.setImageBitmap(myBitmap);
            }
        }
        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    imageView.setVisibility(View.VISIBLE);
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_position =emailOrMobileLayout.indexOfChild(hiddenInfo);
                getLatLong();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_position =emailOrMobileLayout.indexOfChild(hiddenInfo);
                getLatLong();
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        EditText myEditTextView1 = (EditText) vv.findViewById(R.id.description);
        //myEditTextView1.setSelection(myEditTextView1.length());
        myEditTextView1.requestFocus();
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    @SuppressLint("CheckResult")
    public void showToast(String s){
        Toasty.success(CameraScreen.this,s,Toast.LENGTH_SHORT,true).show();
       /* super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);*/
        homePage();
    }

    public String fileDirectory(Bitmap bitmap,String type,String count){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
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

}
