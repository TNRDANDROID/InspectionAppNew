package com.nic.InspectionAppNew.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.RegistrationScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.CameraUtils;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission.CAMERA;
import static android.os.Build.VERSION_CODES.M;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;
import static com.nic.InspectionAppNew.utils.Utils.showAlert;

public class RegistrationScreen extends AppCompatActivity implements Api.ServerResponseListener{
    private PrefManager prefManager;
    RegistrationScreenBinding registrationScreenBinding;
    ProgressHUD progressHUD;


    ////Array List
    ArrayList<ModelClass> genderList= new ArrayList<>();
    ArrayList<ModelClass> districtList=new ArrayList<>();
    ArrayList<ModelClass> blockList=new ArrayList<>();
    ArrayList<ModelClass> levelList=new ArrayList<>();
    ArrayList<ModelClass> designationList=new ArrayList<>();

    String gender_code="";
    String dcode="";
    String dcodeSelected="0";
    String gender_selected="",level_selected="", designation_selected="";
    String bcode="";
    String bcodeSelected="0";
    String level_id="";
    String designation_id="";
    String designation_idSelected="0";
    String key="";
    String profile_data="";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    String UserProfile ="";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 2;
    private Uri mCropImageUri;
    public com.nic.InspectionAppNew.dataBase.dbData dbData = new dbData(this);
    public DBHelper dbHelper;
    public SQLiteDatabase db;
    private static final int PERMISSION_CAMERA = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registrationScreenBinding =DataBindingUtil.setContentView(this, R.layout.registration_screen);
        registrationScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
            dbData.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        key=getIntent().getStringExtra("key");

        if(key.equalsIgnoreCase("login")){
            registrationScreenBinding.detailsLayout.setVisibility(View.GONE);
            registrationScreenBinding.mobileNo.setEnabled(true);
            registrationScreenBinding.tick1.setVisibility(View.VISIBLE);
            registrationScreenBinding.btnRegister.setText("Register");
            registrationScreenBinding.titleTv.setText("Registration");
        }
        else {
            registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.mobileNo.setEnabled(false);
            registrationScreenBinding.tick1.setVisibility(View.GONE);
            registrationScreenBinding.btnRegister.setText("Update");
            registrationScreenBinding.titleTv.setText("Edit Profile");

        }
        fetchResponce();
        registrationScreenBinding.genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){

                    gender_code = genderList.get(position).getGender_code();
                }
                else {
                    gender_code ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    getLevelSelection(position);
                }
                else {
                    level_id ="";
                    dcode = "";
                    bcode = "";
                    designation_id ="";
                    registrationScreenBinding.districtLayout.setVisibility(View.GONE);
                    registrationScreenBinding.blockLayout.setVisibility(View.GONE);
                    registrationScreenBinding.designationLayout.setVisibility(View.GONE);

                    registrationScreenBinding.district.setAdapter(null);
                    registrationScreenBinding.block.setAdapter(null);
                    registrationScreenBinding.designation.setAdapter(null);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registrationScreenBinding.district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    dcode = districtList.get(position).getDistrictCode();
                    bcode="0";
                    getBlockList();
                }
                else {
                    dcode ="";
                    bcode = "";
                    registrationScreenBinding.block.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    bcode = blockList.get(position).getBlockCode();
                }
                else {
                    bcode ="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        registrationScreenBinding.designation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    designation_id = designationList.get(position).getDesig_code();
                }
                else {
                    designation_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        registrationScreenBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldValidation();
            }
        });
        registrationScreenBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserProfile.isEmpty()){
                    Utils.ExpandedImage(UserProfile,RegistrationScreen.this);
                }

            }
        });
        registrationScreenBinding.tick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!registrationScreenBinding.mobileNo.getText().toString().isEmpty()&&Utils.isValidMobile1(registrationScreenBinding.mobileNo.getText().toString())){
                    validate(registrationScreenBinding.mobileNo.getText().toString());
                       /* registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_check_sign_icon));
                        registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);*/
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"Enter Valid Mobile Number!");
                }
            }
        });
        registrationScreenBinding.mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
                registrationScreenBinding.detailsLayout.setVisibility(View.GONE);
                registrationScreenBinding.officeAddress.setText("");
                registrationScreenBinding.emailId.setText("");
                registrationScreenBinding.level.setSelection(0);
                registrationScreenBinding.designation.setSelection(0);
                registrationScreenBinding.genderSpinner.setSelection(0);
                registrationScreenBinding.district.setSelection(0);
                registrationScreenBinding.block.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void getLevelSelection(int position){

        dcode = "";
        bcode = "";
        designation_id ="";
        registrationScreenBinding.block.setAdapter(null);

        level_id = levelList.get(position).getLocalbody_code();
        if(level_id.equalsIgnoreCase("S")){
            registrationScreenBinding.districtLayout.setVisibility(View.GONE);
            registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.blockLayout.setVisibility(View.GONE);

        }
        else if(level_id.equalsIgnoreCase("D")){
            getDistrictList();
            registrationScreenBinding.districtLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.blockLayout.setVisibility(View.GONE);
        }
        else {
            getDistrictList();
            registrationScreenBinding.districtLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.designationLayout.setVisibility(View.VISIBLE);
            registrationScreenBinding.blockLayout.setVisibility(View.VISIBLE);
        }

        getDesignationList();

    }

    public void getPerMissionCapture(){
       /* if (Build.VERSION.SDK_INT >= M) {
            if (CameraUtils.checkPermissions(RegistrationScreen.this)) {
                selectImage();

            } else {
                requestCameraPermission(MEDIA_TYPE_IMAGE);
            }
//                            checkPermissionForCamera();
        } else {
            selectImage();

        }
*/

        if (ContextCompat.checkSelfPermission(RegistrationScreen.this,
                CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            selectImage();

        }else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA},
                    PERMISSION_CAMERA);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    selectImage();
                    Log.i( "LOG_TAG","Permission granted");

                }
                // Cancelled or denied.
                else {
                    Log.i("LOG_TAG","Permission denied");
//                    Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{CAMERA},
                            PERMISSION_CAMERA);

                }

            }
            break;
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                selectImage();
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permissions_required))
                .setMessage(getResources().getString(R.string.allow_camera_location_permission))
                .setPositiveButton(getResources().getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(RegistrationScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    public void selectImage() {
        final CharSequence[] options = { getResources().getString(R.string.take_photo),getResources().getString(R.string.choose_from_gallery),getResources().getString(R.string.cancel) };
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationScreen.this);
        builder.setTitle(getResources().getString(R.string.add_photo));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getResources().getString(R.string.take_photo)))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (file != null) {
                            imageStoragePath = file.getAbsolutePath();
                        }

                        Uri fileUri = CameraUtils.getOutputMediaFileUri(RegistrationScreen.this, file);

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                        // start the image capture Intent
                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    }
                   /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);*/
                }
                else if (options[item].equals(getResources().getString(R.string.choose_from_gallery)))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
                }
                else if (options[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    //Uses https://github.com/zetbaitsu/Compressor library to compress selected image
//                    File file = new Compressor(this).compressToFile(new File(uri.getPath()));
                    File file = new File(uri.getPath());
                   /* File compressedFile  = new Compressor.Builder(this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .build()
                            .compressToFile(file);*/
                    Bitmap compBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    UserProfile=Utils.BitMapToString(compBitmap);
                    Picasso.get().load(file).into(registrationScreenBinding.profileImage);
//                    Toast.makeText(this, "Compressed", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
//                    Toast.makeText(this, "Failed Compress", Toast.LENGTH_SHORT).show();
                    Picasso.get().load(uri).into(registrationScreenBinding.profileImage);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //TODO handle cropping error
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap i = (Bitmap) data.getExtras().get("data");
                 /*   imageStoragePath=getRealPathFromURI(getImageUri(getApplicationContext(),i));
                    previewCapturedImage(i);*/
                    startCropImageActivity(getImageUri(getApplicationContext(),i));

                }else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                    // successfully captured the image
                    // display it in image view
                    /*Bitmap i=null;
                    previewCapturedImage(i);*/
                    startCropImageActivity(Uri.fromFile(new File(imageStoragePath)));
                    //
                }
            }
        }
        if (requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
               /* String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                Bitmap compBitmap = Utils.resizedBitmap(picturePath,RegistrationScreen.this);


                Log.w("path of img gallery", picturePath+"");
                UserProfile=BitMapToString(compBitmap);

                registrationScreenBinding.profileImage.setImageBitmap(compBitmap);
                uploadProfile();*/
                startCropImageActivity(selectedImage);

            }
        }
    }
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAllowFlipping(false)
                .setActivityTitle("Crop Image")
                .setCropMenuCropButtonIcon(R.drawable.ic_check)
                .setAllowRotation(true)
                .setInitialCropWindowPaddingRatio(0)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(80)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public  Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,"IMG_" + Calendar.getInstance().getTime(),null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public Bitmap previewCapturedImage(Bitmap i) {
        Bitmap rotatedBitmap = null;
        Bitmap bitmap = null;
        try {
            // hide video preview
           /* if(i != null){
                bitmap=i;
            }else {
                bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            }*/
            bitmap = Utils.resizedBitmap(imageStoragePath,RegistrationScreen.this);

            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

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
            registrationScreenBinding.profileImage.setImageBitmap(rotatedBitmap);

            UserProfile=BitMapToString(rotatedBitmap);
//            cameraScreenBinding.imageView.showImage((getImageUri(rotatedBitmap)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return  rotatedBitmap;
    }
    public String BitMapToString(Bitmap bitmap){
        String temp="";
        try {
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b=baos.toByteArray();
            temp= Base64.encodeToString(b, Base64.DEFAULT);
        }
        catch (Exception e){
        }
        return temp;
    }

    private void validate(String mobile) {
        if (Utils.isOnline()) {
            try {
                new ApiService(RegistrationScreen.this).makeJSONObjectRequest("MobileVerify", Api.Method.POST, UrlGenerator.getOpenUrl(),  jsonParams(), "not cache", RegistrationScreen.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(RegistrationScreen.this,"No Internet Connection!");
        }


    }
    public  JSONObject  jsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "Verify_mobile_number");
        dataSet.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
        Log.d("mobile_number", "" + dataSet);
        return dataSet;
    }

    private void fetchResponce() {
        if(Utils.isOnline()){
/*
            if(key.equalsIgnoreCase("home")){
                profile_data=getIntent().getStringExtra("profile_data");
                try
                {
                    JSONObject jsonObject = new JSONObject(profile_data);
                    System.out.println("JSON Object: "+jsonObject);
*/
/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                setProfileData(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                            }
                            catch (JSONException err) {

                            }
                        }
                    }, 2000);
*//*


                    setProfileData(jsonObject.getJSONArray(AppConstant.JSON_DATA));

                }
                catch (JSONException e)
                {
                    System.out.println("Error "+e.toString());
                }

            }
*/
            if(key.equalsIgnoreCase("home")){
                getProfileData();
            }else {
                getGenderList();
                getStageLevelList();
                getDesignationList();
            }

        }
        else {
            Utils.showAlert(RegistrationScreen.this,"No Internet");
        }
    }

    public void getGenderList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Gender", Api.Method.POST, UrlGenerator.getOpenUrl(), genderParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getStageLevelList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Level", Api.Method.POST, UrlGenerator.getOpenUrl(), stageLevelParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getDesignationList() {
        try {
            new ApiService(this).makeJSONObjectRequest("Designation", Api.Method.POST, UrlGenerator.getOpenUrl(), designationParams(), "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getOpenUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getOpenUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject districtListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID,AppConstant.KEY_DISTRICT_LIST_ALL);
        Log.d("object", "" + dataSet);
        return dataSet;
    }

    public JSONObject blockListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BLOCK_LIST_DISTRICT_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        Log.d("object", "" + dataSet);
        return dataSet;
    }
    public JSONObject genderParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_profile_gender");
        Log.d("object", "" + dataSet);
        return dataSet;
    }
    public JSONObject stageLevelParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_profile_level");
        Log.d("object", "" + dataSet);
        return dataSet;
    }
    public JSONObject designationParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "get_mobile_designation");
        dataSet.put("level_id", level_id);
        Log.d("object", "" + dataSet);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status ;
            String response ;
            if ("MobileVerify".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, "Mobile number verified Successfully!");
                    registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_check_sign_icon));
                    registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_MESSAGE));
                }
                Log.d("MobileVerify", "" + responseObj.toString());

            }

            if ("Gender".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    try {
                        JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                        //prefManager.setGenderList(jsonarray.toString());
                        loadGenderList(jsonarray);
                        Log.d("Gender", "" + responseObj.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
            if ("Level".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    try {
                        JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                        loadLevelList(jsonarray);
                        Log.d("Level", "" + responseObj.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
            if ("Designation".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    try {
                        JSONArray jsonarray = responseObj.getJSONArray(AppConstant.JSON_DATA);
                        loadDesignationList(jsonarray);
                        Log.d("Designation", "" + responseObj.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }
            if ("DistrictList".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new  InsertDistrictTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.toString());
                }
                Log.d("DistrictList", "" + responseObj.toString());
            }
            if ("BlockList".equals(urlType) && responseObj != null) {
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("OK")) {
                    new  InsertBlockTask().execute(responseObj.getJSONArray(AppConstant.JSON_DATA));
                } else if (status.equalsIgnoreCase("OK") && response.equalsIgnoreCase("NO_RECORD")) {
                    Log.d("Record", responseObj.toString());
                    registrationScreenBinding.block.setAdapter(null);
                }
                Log.d("BlockList", "" + responseObj.toString());
            }

            if ("registration".equals(urlType) && responseObj != null) {
                Log.d("registration", "" + responseObj.toString());
                status  = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    UserProfile="";
                    Toasty.success(RegistrationScreen.this,responseObj.getString("MESSAGE"), Toast.LENGTH_SHORT).show();
                    gotoOtpVerification();
                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_MESSAGE));
                }

            }
            if ("update".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
//                    showAlert(this, "User data updated successfully!");
                    Toasty.success(this,jsonObject.getString(AppConstant.KEY_MESSAGE),Toast.LENGTH_SHORT,true).show();
                    UserProfile="";
                   /* getProfileData();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPress();
                        }
                    }, 1000);*/
                   showSignInScreen();

                }
                else {
                    Utils.showAlert(RegistrationScreen.this,jsonObject.getString("MESSAGE"));
                }
                Log.d("update", "" + responseDecryptedBlockKey);
            }
            if ("getProfileData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);

                Log.d("getProfileData", "" + jsonObject.toString());
                status  = jsonObject.getString(AppConstant.KEY_STATUS);
                response = jsonObject.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")&& response.equalsIgnoreCase("OK")){
                    if (jsonObject.getJSONArray(AppConstant.JSON_DATA).length() > 0) {
                        JSONArray jsonArray=jsonObject.getJSONArray(AppConstant.JSON_DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String name=jsonArray.getJSONObject(i).getString("name");
                            String mobile_number=jsonArray.getJSONObject(i).getString("mobile");
                            String gender=jsonArray.getJSONObject(i).getString("gender");
                            String level=jsonArray.getJSONObject(i).getString("level");
                            String designation_code=jsonArray.getJSONObject(i).getString("desig_code");
                            String designation=jsonArray.getJSONObject(i).getString("desig_name");
                            String dcode=jsonArray.getJSONObject(i).getString("dcode");
                            String bcode=jsonArray.getJSONObject(i).getString("bcode");
                            String office_address=jsonArray.getJSONObject(i).getString("office_address");
                            String email=jsonArray.getJSONObject(i).getString("email");
                            String profile_image=jsonArray.getJSONObject(i).getString("profile_image");
                            if(profile_image != null &&!profile_image.isEmpty()){
                                byte[] decodedString = Base64.decode(profile_image, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                prefManager.setProfileImage(Utils.BitMapToString(decodedByte));


                            }else {
                                prefManager.setProfileImage("");
                            }
                            prefManager.setDesignation(designation);
                            prefManager.setDesignationCode(designation_code);
                            prefManager.setName(String.valueOf(name));
                            prefManager.setLevels(String.valueOf(level));
                            prefManager.setDistrictCode(dcode);
                            prefManager.setBlockCode(bcode);

                        }
                        setProfileData(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                    }

                }else {
                    showAlert(this, jsonObject.getString(AppConstant.KEY_MESSAGE));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getProfileData() {
        if (Utils.isOnline()) {
            try {
                new ApiService(RegistrationScreen.this).makeJSONObjectRequest("getProfileData", Api.Method.POST, UrlGenerator.getMainService(),  getProfileJsonParams(), "not cache", RegistrationScreen.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(RegistrationScreen.this,"No Internet Connection!");
        }
    }
    public JSONObject getProfileJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), getProfileParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("getProfile", "" + dataSet);
        return dataSet;
    }
    public  JSONObject getProfileParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "work_inspection_profile_list");
        Log.d("getProfile", "" + dataSet);
        return dataSet;
    }
    private void setProfileData(JSONArray jsonArray) {
        if (jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String name=jsonArray.getJSONObject(i).getString("name");
                    String mobile_number=jsonArray.getJSONObject(i).getString("mobile");
                    gender_selected=jsonArray.getJSONObject(i).getString("gender");
                    level_selected=jsonArray.getJSONObject(i).getString("level");
                    designation_selected=jsonArray.getJSONObject(i).getString("desig_code");
                    String dcode=jsonArray.getJSONObject(i).getString("dcode");
                    String bcode=jsonArray.getJSONObject(i).getString("bcode");
                    String office_address=jsonArray.getJSONObject(i).getString("office_address");
                    String email=jsonArray.getJSONObject(i).getString("email");
                    UserProfile=jsonArray.getJSONObject(i).getString("profile_image");
                    dcodeSelected=dcode;
                    bcodeSelected=bcode;
                    designation_idSelected=designation_selected;

                    registrationScreenBinding.name.setText(name);
                    registrationScreenBinding.mobileNo.setText(mobile_number);
                    registrationScreenBinding.mobileNo.setFocusable(false);
                    registrationScreenBinding.tick1.setClickable(false);
                    registrationScreenBinding.tick1.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
                    registrationScreenBinding.detailsLayout.setVisibility(View.VISIBLE);
                    registrationScreenBinding.officeAddress.setText(office_address);
                    registrationScreenBinding.emailId.setText(email);
                    if (UserProfile != null && !UserProfile.equals("")) {
                        registrationScreenBinding.profileImage.setImageBitmap(Utils.StringToBitMap(UserProfile));
                    }else {
                        registrationScreenBinding.profileImage.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_user_icon));
                    }
                    getGenderList();
                    getStageLevelList();
                    getDesignationList();

                   /* for(int j=0;j<genderList.size();j++){
                        if(genderList.get(j).getGender_code() .equalsIgnoreCase(gender) ){
                            registrationScreenBinding.genderSpinner.setSelection(j);
                        }
                    }
                    for(int m=0;m<levelList.size();m++){
                        if(levelList.get(m).getLocalbody_code() .equalsIgnoreCase(level) ){
                            registrationScreenBinding.level.setSelection(m);
                        }
                    }
                    for(int k=0;k<designationList.size();k++){
                        if(designationList.get(k).getDesig_code() .equalsIgnoreCase(designation) ){
                            registrationScreenBinding.designation.setSelection(k);
                        }
                    }*/


//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            registrationScreenBinding.genderSpinner.setSelection(getSpinnerIndex("Gender",genderList,gender));
//                            registrationScreenBinding.level.setSelection(getSpinnerIndex("Level",levelList,level));
//                            }
//                    }, 500);
/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(level.equalsIgnoreCase("D")){
                                registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcode));
                            }
                            else if(level.equalsIgnoreCase("B")){
                                registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcode));
                                registrationScreenBinding.block.setSelection(getSpinnerIndex("Block",blockList,bcode));
                            }
                            registrationScreenBinding.designation.setSelection(getSpinnerIndex("Designation",designationList,designation));
                        }
                    }, 500);
*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private void gotoOtpVerification() {
        Intent intent = new Intent(RegistrationScreen.this,OtpVerfication.class);
        intent.putExtra("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
        intent.putExtra("flag","register");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @SuppressLint("StaticFieldLeak")
    public class InsertDistrictTask extends AsyncTask<JSONArray ,Void ,Void> {
        private  ProgressHUD progressHUD;
        ArrayList<ModelClass> districtList1=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(RegistrationScreen.this, "Loading...", true, false, null);
        }
        @Override
        protected Void doInBackground(JSONArray... params) {
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                jsonArray = params[0];
                districtList.clear();

                ModelClass districtListValue1 = new ModelClass();
                districtListValue1.setDistrictCode("0");
                districtListValue1.setDistrictName("Select District");
                districtList.add(districtListValue1);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelClass districtListValue = new ModelClass();
                    try {
                        districtListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                        districtListValue.setDistrictName(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME));

                        districtList.add(districtListValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*districtList1.sort((o1, o2)
                    -> o1.getDistrictName().compareTo(
                    o2.getDistrictName()));
            districtList.addAll(districtList1);*/
            registrationScreenBinding.district.setAdapter(new CommonAdapter(RegistrationScreen.this,districtList,"District"));
            if(key.equalsIgnoreCase("home")){
                if(!dcodeSelected.equalsIgnoreCase("0")&& !dcodeSelected.equalsIgnoreCase("")){
                    registrationScreenBinding.district.setSelection(getSpinnerIndex("District",districtList,dcodeSelected));
                }

            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class InsertBlockTask extends AsyncTask<JSONArray ,Void ,Void> {
        private  ProgressHUD progressHUD;
        ArrayList<ModelClass> blockList1 = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Utils.showProgress(OnlineWorkFilterScreen.this,progressHUD);
            progressHUD = ProgressHUD.show(RegistrationScreen.this, "Loading...", true, false, null);
        }

        @Override
        protected Void doInBackground(JSONArray... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                jsonArray = params[0];

                blockList.clear();
                ModelClass modelClass = new ModelClass();
                modelClass.setDistrictCode("0");
                modelClass.setBlockCode("0");
                modelClass.setBlockName("Select Block");
                blockList.add(modelClass);

                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelClass blocktListValue = new ModelClass();
                    try {
                        blocktListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                        blocktListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                        blocktListValue.setBlockName(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME));

                        blockList.add(blocktListValue);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* blockList1.sort((o1, o2)
                    -> o1.getDistrictName().compareTo(
                    o2.getDistrictName()));
            blockList.addAll(blockList1);*/
            registrationScreenBinding.block.setAdapter(new CommonAdapter(RegistrationScreen.this,blockList,"Block"));

            if(key.equalsIgnoreCase("home")){
                if(!bcodeSelected.equalsIgnoreCase("0")&& !bcodeSelected.equalsIgnoreCase("")){
                    registrationScreenBinding.block.setSelection(getSpinnerIndex("Block",blockList,bcodeSelected));
                }

            }
        }
    }

    private void loadLevelList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                levelList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setLocalbody_name("Select Level");
                modelClass.setLocalbody_code("0");

                levelList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String localbody_name = jsonobject.getString("localbody_name");
                    String localbody_code = (jsonobject.getString("localbody_code"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setLocalbody_name(localbody_name);
                    roadListValue.setLocalbody_code(localbody_code);

                    levelList.add(roadListValue);
                }
                registrationScreenBinding.level.setAdapter(new CommonAdapter(this, levelList, "LevelList"));
            }
            if(levelList.size()>0){

                if(key.equalsIgnoreCase("home")){
                    if(!level_selected.equalsIgnoreCase("0")&& !level_selected.equalsIgnoreCase("")){
                        registrationScreenBinding.level.setSelection(getSpinnerIndex("Level",levelList,level_selected));
                    }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadGenderList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                genderList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setGender_code("0");
                modelClass.setGender_name_en("Select Gender");

                genderList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String gender_code = jsonobject.getString("gender_code");
                    String gender_name_en = (jsonobject.getString("gender_name_en"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setGender_code(gender_code);
                    roadListValue.setGender_name_en(gender_name_en);

                    genderList.add(roadListValue);
                }
                registrationScreenBinding.genderSpinner.setAdapter(new CommonAdapter(this, genderList, "GenderList"));

            }
            if(genderList.size()>0){
                if(key.equalsIgnoreCase("home")){
                    if(!gender_selected.equalsIgnoreCase("0")&& !gender_selected.equalsIgnoreCase("")){
                        registrationScreenBinding.genderSpinner.setSelection(getSpinnerIndex("Gender",genderList,gender_selected));
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadDesignationList(JSONArray jsonarray) {
        try {
            //JSONArray jsonarray = new JSONArray(prefManager.getGenderList());
            if (jsonarray != null && jsonarray.length() > 0) {
                designationList = new ArrayList<>();
                ModelClass modelClass = new ModelClass();
                modelClass.setDesig_code("0");
                modelClass.setDesig_name("Select Designation");

                designationList.add(modelClass);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String desig_code = jsonobject.getString("desig_code");
                    String desig_name = (jsonobject.getString("desig_name"));

                    ModelClass roadListValue = new ModelClass();
                    roadListValue.setDesig_code(desig_code);
                    roadListValue.setDesig_name(desig_name);

                    designationList.add(roadListValue);
                }
                registrationScreenBinding.designation.setAdapter(new CommonAdapter(this, designationList, "DesignationList"));

            }
            if(designationList.size()>0){

                if(key.equalsIgnoreCase("home")){
                    if(!designation_idSelected.equalsIgnoreCase("0")&& !designation_idSelected.equalsIgnoreCase("")){
                        registrationScreenBinding.designation.setSelection(getSpinnerIndex("Designation",designationList,designation_idSelected));
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fieldValidation(){
        if(!registrationScreenBinding.name.getText().toString().isEmpty()){
            if(!registrationScreenBinding.mobileNo.getText().toString().isEmpty()&&Utils.isValidMobile1(registrationScreenBinding.mobileNo.getText().toString())){
                if(!gender_code.isEmpty()){
                    if(!level_id.isEmpty()){
                        if(!designation_id.isEmpty()){
                            if(level_id.equalsIgnoreCase("S")){
                                if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                    if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid1(registrationScreenBinding.emailId.getText().toString())){
                                        if(!UserProfile.equalsIgnoreCase("")){
                                            saveDataAlert();
                                        }else {
                                            Utils.showAlert(this,"Please select profile image");
                                        }

                                    }
                                    else {
                                        registrationScreenBinding.emailId.setError("Enter Valid Email");
                                        registrationScreenBinding.emailId.requestFocus();
                                    }
                                }
                                else {
                                    registrationScreenBinding.officeAddress.setError("Enter Address");
                                    registrationScreenBinding.officeAddress.requestFocus();
                                }
                            }
                            else if(level_id.equalsIgnoreCase("D")){
                                if(!dcode.isEmpty()){
                                    if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                        if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid(registrationScreenBinding.emailId.getText().toString())){
                                            if(!UserProfile.equalsIgnoreCase("")){
                                                saveDataAlert();
                                            }else {
                                                Utils.showAlert(this,"Please select profile image");
                                            }
                                        }
                                        else {
                                            registrationScreenBinding.emailId.setError("Enter Valid Email");
                                            registrationScreenBinding.emailId.requestFocus();
                                        }
                                    }
                                    else {
                                        registrationScreenBinding.officeAddress.setError("Enter Address");
                                        registrationScreenBinding.officeAddress.requestFocus();
                                    }
                                }
                                else {
                                    Utils.showAlert(RegistrationScreen.this,"Please Select District");
                                }
                            }
                            else {
                                if(!dcode.isEmpty()){
                                    if(!bcode.isEmpty()){
                                        if(!registrationScreenBinding.officeAddress.getText().toString().isEmpty()){
                                            if(!registrationScreenBinding.emailId.getText().toString().isEmpty()&&Utils.isEmailValid(registrationScreenBinding.emailId.getText().toString())){
                                                if(!UserProfile.equalsIgnoreCase("")){
                                                    saveDataAlert();
                                                }else {
                                                    Utils.showAlert(this,"Please select profile image");
                                                }
                                            }
                                            else {
                                                registrationScreenBinding.emailId.setError("Enter Email");
                                                registrationScreenBinding.emailId.requestFocus();
                                            }
                                        }
                                        else {
                                            registrationScreenBinding.officeAddress.setError("Enter Address");
                                            registrationScreenBinding.officeAddress.requestFocus();
                                        }
                                    }
                                    else {
                                        Utils.showAlert(RegistrationScreen.this,"Please Select Block");
                                    }
                                }
                                else {
                                    Utils.showAlert(RegistrationScreen.this,"Please Select District");
                                }

                            }
                        }
                        else {
                            Utils.showAlert(RegistrationScreen.this,"Please Select Designation");
                        }
                    }
                    else {
                        Utils.showAlert(RegistrationScreen.this,"Please Select Level");
                    }
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"Please Select Gender");
                }
            }
            else {
                registrationScreenBinding.mobileNo.setError("Please Enter Valid Mobile Number");
                registrationScreenBinding.mobileNo.requestFocus();
            }
        }
        else {
            registrationScreenBinding.name.setError("Please Enter Name");
            registrationScreenBinding.name.requestFocus();
        }
    }

    private void saveDataAlert(){
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(key.equalsIgnoreCase("login")){
                text.setText("Are you sure you want to register?");
            }else {
                text.setText("Are you sure you want update data to server?");
            }


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
                    saveDetails();
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void saveDetails(){
        if(key.equalsIgnoreCase("login")){
            try {
                JSONObject data_set = new JSONObject();
                data_set.put(AppConstant.KEY_SERVICE_ID,"register");
                data_set.put("name",registrationScreenBinding.name.getText().toString());
                data_set.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
                data_set.put("gender",gender_code);
                data_set.put("level",level_id);
                data_set.put("designation",designation_id);
                data_set.put("dcode",dcode);
                if(level_id.equalsIgnoreCase("B")){
                    data_set.put("bcode",bcode);
                }
                data_set.put("office_address",registrationScreenBinding.officeAddress.getText().toString());
                data_set.put("email",registrationScreenBinding.emailId.getText().toString());
                data_set.put("profile_image",UserProfile);
                Log.d("register_json",data_set.toString());

                if(Utils.isOnline()){
                    registration(data_set);
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"No Internet");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }else {
            try {
                JSONObject data_set = new JSONObject();
                data_set.put(AppConstant.KEY_SERVICE_ID,"Update_work_inspection_profile");
                data_set.put("name",registrationScreenBinding.name.getText().toString());
                data_set.put("mobile_number",registrationScreenBinding.mobileNo.getText().toString());
                data_set.put("gender",gender_code);
                data_set.put("level",level_id);
                data_set.put("designation",designation_id);
                data_set.put("dcode",dcode);
                if(level_id.equalsIgnoreCase("B")){
                    data_set.put("bcode",bcode);
                }
                data_set.put("office_address",registrationScreenBinding.officeAddress.getText().toString());
                data_set.put("email",registrationScreenBinding.emailId.getText().toString());
                data_set.put("profile_image",UserProfile);
                String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), data_set.toString());
                JSONObject dataSet = new JSONObject();
                dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
                dataSet.put(AppConstant.DATA_CONTENT, authKey);
                Log.d("update_param", "" + data_set.toString());
                Log.d("update_param", "" + dataSet.toString());

                if(Utils.isOnline()){
                    update(dataSet);
                }
                else {
                    Utils.showAlert(RegistrationScreen.this,"No Internet");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
    }
    public void registration(JSONObject jsonObject) {
        try {
            new ApiService(this).makeJSONObjectRequest("registration", Api.Method.POST, UrlGenerator.getOpenUrl(), jsonObject, "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update(JSONObject jsonObject) {
        try {
            new ApiService(this).makeJSONObjectRequest("update", Api.Method.POST, UrlGenerator.getMainService(), jsonObject, "not cache", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSpinnerIndex(String type,ArrayList<ModelClass> list,String myString){
        int index = 0;
        try {
            if(type.equals("Gender")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getGender_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("Level")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getLocalbody_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("District")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getDistrictCode().equals(myString)){
                        index = i;
                    }
                }
            }
            else if(type.equals("Designation")){
                for (int i=0;i<list.size();i++){
                    if (list.get(i).getDesig_code().equals(myString)){
                        index = i;
                    }
                }
            }
            else {
                for (int i=0;i<blockList.size();i++){
                    if (blockList.get(i).getBlockCode().equals(myString)){
                        index = i;
                    }
                }
            }
        }
        catch (NumberFormatException e){ e.printStackTrace(); }
        return index;
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
    private void showSignInScreen() {
        dbData.open();
        dbData.deleteAll();
        prefManager.clearSession();
        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXIT", false);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


}
