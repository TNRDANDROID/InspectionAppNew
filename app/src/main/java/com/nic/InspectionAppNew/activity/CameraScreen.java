package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.CameraScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.MyLocationListener;
import com.nic.InspectionAppNew.utils.CameraUtils;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private com.nic.InspectionAppNew.dataBase.dbData dbData = new dbData(this);

    ///Image With Description
    ImageView imageView, image_view_preview;
    TextView latitude_text, longtitude_text;
    EditText myEditTextView;
    private List<View> viewArrayList = new ArrayList<>();
    private List<ModelClass> viewList = new ArrayList<>();

    int work_id;
    int min_img_count=0;
    int max_img_count =4;
    int clicked_position;

    String dcode;
    String bcode;
    String pvcode;
    String scheme_id;
    String fin_year;
    String work_name;
    String as_value;
    String ts_value;
    String current_stage_of_work;
    String is_high_value;
    String work_description;
    String work_status_id;
    String work_status;
    String hab_code;
    String scheme_group_id;
    String work_group_id;
    String work_type_id;
    String onOffType;

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

        intializeUI();

        cameraScreenBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageButtonClick();
            }
        });
    }

    public void intializeUI() {
        getIntentData();
        //work_id= getIntent().getIntExtra("work_id",0);

        viewArrayList.clear();


        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        cameraScreenBinding.btnSaveLocal.setOnClickListener(this::onClick);
        cameraScreenBinding.imageCountTv.setText("You Can Capture up to "+max_img_count+" photos");
        updateView(CameraScreen.this, cameraScreenBinding.cameraLayout, "", "", "", "");

    }
    private void getIntentData(){
        work_id= getIntent().getIntExtra("work_id",0);
        dcode = getIntent().getStringExtra("dcode");
        bcode = getIntent().getStringExtra("bcode");
        pvcode = getIntent().getStringExtra("pvcode");
        scheme_id = getIntent().getStringExtra("scheme_id");
        fin_year = getIntent().getStringExtra("fin_year");
        work_name = getIntent().getStringExtra("work_name");
        dcode = getIntent().getStringExtra("dcode");
        as_value = getIntent().getStringExtra("as_value");
        ts_value = getIntent().getStringExtra("ts_value");
        current_stage_of_work = getIntent().getStringExtra("current_stage_of_work");
        is_high_value = getIntent().getStringExtra("is_high_value");
        work_status_id = getIntent().getStringExtra("work_status_id");
        work_description = getIntent().getStringExtra("work_description");
        work_status = getIntent().getStringExtra("work_status");
        hab_code = getIntent().getStringExtra("hab_code");
        scheme_group_id = getIntent().getStringExtra("scheme_group_id");
        work_group_id = getIntent().getStringExtra("work_group_id");
        work_type_id = getIntent().getStringExtra("work_type_id");
        onOffType = getIntent().getStringExtra("onOffType");

        if(onOffType.equals("online")){
            cameraScreenBinding.btnSave.setText("Sync Data");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_local:
                int childCount = cameraScreenBinding.cameraLayout.getChildCount();
                if(childCount >= min_img_count){
                    if(onOffType.equals("online")){
                        onLineUploadData();
                    }
                    else {
                        saveImageButtonClick();
                    }

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

            /*int childCount = cameraScreenBinding.cameraLayout.getChildCount();
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
*/
            imageView.setImageBitmap(rotatedBitmap);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);

            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
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
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Your Data is Synchronized to the server!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            homePage();
                        }
                    }, 500);


                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    Utils.showAlert(this, jsonObject.getString("MESSAGE"));
                }
                Log.d("savedImage", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, MainHomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        finish();
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
        long work_insert_primary_id = 0;
        String whereCl = "";String[] whereAr = null;
        String[] selectionArgs;
        String selection;
        long rowInsert = 0;
        long rowUpdat = 0;
        try {
            ContentValues values = new ContentValues();

            values.put("dcode",dcode);
            values.put("bcode",bcode);
            values.put("pvcode",pvcode);
            values.put("scheme_id",scheme_id);
            values.put("fin_year",fin_year);
            values.put("work_id",work_id);
            values.put("work_name",work_name);
            values.put("as_value",as_value);
            values.put("ts_value",ts_value);
            values.put("current_stage_of_work",current_stage_of_work);
            values.put("is_high_value",is_high_value);
            values.put("work_status_id",work_status_id);
            values.put("work_status",work_status);
            values.put("work_description",work_description);
            values.put("hab_code",hab_code);
            values.put("scheme_group_id",scheme_group_id);
            values.put("work_group_id",work_group_id);
            values.put("work_type_id",work_type_id);
            selection = "work_id = ?";
            selectionArgs = new String[]{String.valueOf(work_id)};
            dbData.open();
            if(dbData.getSavedWorkList("",String.valueOf(work_id)).size()>0){
                rowInsert = db.update(DBHelper.SAVE_WORK_DETAILS,values,selection,selectionArgs);
            }
            else {
                rowInsert = db.insert(DBHelper.SAVE_WORK_DETAILS,null,values);
            }


        } catch (Exception e) {

        }
        if (rowInsert>0){
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
                            image_path = fileDirectory(bitmap,"work_list",String.valueOf(i));


                        } catch (Exception e) {
                            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.at_least_capture_one_photo));
                        }


                        ContentValues imageValue = new ContentValues();
                        imageValue.put("save_work_details_primary_id", rowInsert);
                        imageValue.put("work_id", work_id);
                        imageValue.put("image_description", description);
                        imageValue.put("image_path", image_path);
                        imageValue.put("latitude", latitude_text.getText().toString());
                        imageValue.put("latitude", latitude_text.getText().toString());
                        imageValue.put("longitude", longtitude_text.getText().toString());
                        imageValue.put("serial_no", count);

                        selection = "save_work_details_primary_id = ? and work_id = ? and serial_no = ?";
                        selectionArgs = new String[]{String.valueOf(rowInsert),String.valueOf(work_id),String.valueOf(count)};
                        ArrayList<ModelClass> imageCount = new ArrayList<>();
                        dbData.open();
                        imageCount = dbData.getParticularSavedImage("",String.valueOf(rowInsert),String.valueOf(work_id),String.valueOf(count));

                        if(imageCount.size()>0){
                            for(int j=0;  j<imageCount.size() ;j++){
                                String filepath=imageCount.get(j).getImage_path();
                                Utils.deleteFileDirectory(filepath);
                            }

                            rowUpdated =db.update(DBHelper.SAVE_IMAGES,imageValue,selection,selectionArgs);
                        }
                        else {
                            rowInserted =db.insert(DBHelper.SAVE_IMAGES,null,imageValue);
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
        description_layout.setVisibility(View.VISIBLE);

//        imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_phone_camera));
      /*  if(values!=null && !values.equals("") && !values.isEmpty()){

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
        }*/
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

                getLatLong();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    public void onLineUploadData() {
        JSONObject maindataset = new JSONObject();
        JSONObject dataset = new JSONObject();
        JSONArray inspection_work_details = new JSONArray();
        boolean true_flag = false;
        try {
            maindataset.put(AppConstant.KEY_SERVICE_ID,"work_inspection_details_save");
            dataset.put("dcode",dcode);
            dataset.put("bcode", bcode);
            dataset.put("pvcode",pvcode);
            dataset.put("hab_code",hab_code);
            dataset.put("work_id", work_id);
            dataset.put("status_id", work_status_id);
            dataset.put("description", work_description);

            int childCount = cameraScreenBinding.cameraLayout.getChildCount();
            int count = 0;
            JSONArray imageArray = new JSONArray();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View vv = cameraScreenBinding.cameraLayout.getChildAt(i);
                    imageView = vv.findViewById(R.id.image_view);
                    myEditTextView = vv.findViewById(R.id.description);
                    latitude_text = vv.findViewById(R.id.latitude);
                    longtitude_text = vv.findViewById(R.id.longtitude);


                    if (imageView.getDrawable() != null) {
                        if(!myEditTextView.getText().toString().equals("")){
                            count = count + 1;
                            String image_str = "";
                            String description = "";
                            try {
                                description = myEditTextView.getText().toString();
                                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                image_str = BitMapToString(bitmap);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("latitude",latitude_text.getText().toString());
                                jsonObject.put("longitude",longtitude_text.getText().toString());
                                jsonObject.put("serial_no",count);
                                jsonObject.put("image_description",description);
                                jsonObject.put("image",image_str);
                                imageArray.put(jsonObject);

                                if(count==childCount){
                                    true_flag = true;
                                }
                                else {
                                    true_flag = false;
                                }
                            } catch (Exception e) {
                                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.at_least_capture_one_photo));
                            }
                        }
                         else{
                            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.enter_description));
                         }
                    }
                    else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.please_capture_image));
                    }
                }
                dataset.put("image_details",imageArray);
                inspection_work_details.put(dataset);
                maindataset.put("inspection_work_details",inspection_work_details);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (Utils.isOnline()) {
            uploadDialog(maindataset);
            //saveImagesJsonParams(maindataset);
            //Log.d("saveImages", "" + maindataset);
        } else {

            Utils.showAlert(CameraScreen.this, "Turn On Mobile Data To Upload");
        }

    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public JSONObject saveImagesJsonParams(JSONObject savePMAYDataSet) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), savePMAYDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveImage", Api.Method.POST, UrlGenerator.getMainService(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("saveImages", "" + dataSet);
        return dataSet;
    }

    private void uploadDialog(JSONObject jsonObject){
        try {
            final Dialog dialog = new Dialog(CameraScreen.this);
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
                    saveImagesJsonParams(jsonObject);
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
