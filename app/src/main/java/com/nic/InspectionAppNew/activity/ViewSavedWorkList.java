package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.nic.InspectionAppNew.Interface.DateInterface;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.SavedWorkListAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.ViewSavedWorkListBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewSavedWorkList extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, DateInterface {
    private ViewSavedWorkListBinding workListBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    private String isHome;
    Handler myHandler = new Handler();
    private ArrayList<ModelClass> workList = new ArrayList<>();
    private ProgressHUD progressHUD;
    SavedWorkListAdapter savedWorkListAdapter;


    String onOffType;
    String fromDate;
    String toDate;
    String work_id="";
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    ProgressDialog progressBar;
    Dialog dialog;
    int pageNumber;
    String WorkId="";
    String inspectionID="";

    ArrayList<ModelClass> savedWorkList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        workListBinding = DataBindingUtil.setContentView(this, R.layout.view_saved_work_list);
        workListBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        workListBinding.recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        workListBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        workListBinding.recycler.setHasFixedSize(true);
        workListBinding.recycler.setNestedScrollingEnabled(false);
        workListBinding.recycler.setFocusable(false);

        workListBinding.recycler.setVisibility(View.GONE);
        workListBinding.notFoundTv.setVisibility(View.VISIBLE);

        workListBinding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!workListBinding.workId.getText().toString().isEmpty()){
                    work_id = workListBinding.workId.getText().toString();
                    workListBinding.workId.setText("");
                    workListBinding.date.setText("Select Date");
                    fromDate = "";
                    toDate = "";
                    workListBinding.recycler.setAdapter(null);
                    getWorkDetails();
                }
                else {
                    Utils.showAlert(ViewSavedWorkList.this,"Please Enter Work ID");
                }
            }
        });


    }

    public void showDatePickerDialog(){
        Utils.showDatePickerDialog(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public void getWorkReportDetails(String work_id, String inspection_id) {
        WorkId=work_id;
        inspectionID=inspection_id;
        try {
            new ApiService(this).makeJSONObjectRequest("WorkReport", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(work_id,inspection_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject workDetailsJsonParams(String work_id, String inspection_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workDetailsParams(this,work_id,inspection_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkDetails", "" + authKey);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity,String work_id, String inspection_id) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "pdf_download");
        dataSet.put("work_id", work_id);
        dataSet.put("inspection_id", inspection_id);

        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("WorkDetails".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    workListData(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                } else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
                }
                Log.d("responseWorkList", "" + jsonObject.getJSONArray(AppConstant.JSON_DATA));

            }
            if ("WorkReport".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1 = jsonObject.getJSONObject("JSON_DATA");
                    String pdf_string ="";
                    pdf_string = jsonObject1.getString("pdf_string");
                    if(checkPermissions()){
                        viewPdf1(pdf_string);
                    }
                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Utils.showAlert(this, jsonObject.getString("RESPONSE"));
                }
                Log.d("responseWorkList", "" + jsonObject);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }
    private boolean checkPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE

        };
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(ViewSavedWorkList.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_REQUEST_CODE_PERMISSION);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( "LOG_TAG","Permission granted");
                    Toast.makeText(this.getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();


//                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i("LOG_TAG","Permission denied");
                    Toast.makeText(this.getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    public void viewPdf1(final String DocumentString) {
        dialog = new Dialog(this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pdf_view_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        final PDFView pdfView = (PDFView) dialog.findViewById(R.id.documentViewer);
        final TextView pageNum = (TextView) dialog.findViewById(R.id.pageNum);
        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final ImageView down_load_icon = (ImageView) dialog.findViewById(R.id.down_load_icon);

        pageNumber = 0;
        if (DocumentString != null && !DocumentString.equals("")) {
            byte[] decodedString = new byte[0];
            try {
                //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                decodedString = Base64.decode(DocumentString/*traders.get(position).getDocument().toString()*/, Base64.DEFAULT);
                System.out.println(new String(decodedString));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            pdfView.fromBytes(decodedString).
                    onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            pageNumber = page;
//                            setTitle(String.format("%s %s / %s", "PDF", page + 1, pageCount));
                            pageNum.setText(pageNumber + 1 + "/" + pageCount);
                        }
                    }).defaultPage(pageNumber).swipeHorizontal(true).enableDoubletap(true).load();

        }else {
            Utils.showAlert(this,"No Record Found!");
        }
        down_load_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDownloadMethod(DocumentString,"");
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public void callDownloadMethod(String document,String type){
        new downloadPDFTask().execute(document);
    }

    @SuppressLint("StaticFieldLeak")
    public class downloadPDFTask extends AsyncTask<String,String,String> {

        String path;
        String DocumentString;
        File dwldsPath;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(String... strings) {
            DocumentString=strings[0];
            String  success="";
            String title="Inspection";
            String work_id =WorkId;
            dwldsPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+title+inspectionID+"_"+work_id + ".pdf");
            path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+title+inspectionID+"_"+work_id + ".pdf";
            if (DocumentString != null && !DocumentString.equals("")) {
                byte[] decodedString = new byte[0];
                try {
                    //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                    decodedString = Base64.decode(DocumentString/*traders.get(position).getDocument().toString()*/, Base64.DEFAULT);
                    //System.out.println(new String(decodedString));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    success="Failure";
                }


                FileOutputStream os = null;

                try {
                    os = new FileOutputStream(dwldsPath, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    success="Failure";
                }
                try {
                    os.write(decodedString);
                    os.flush();
                    os.close();
                    success="Success";
                    //System.out.println("Created");


                } catch (IOException e) {
                    success="Failure";
                    e.printStackTrace();

                }

            }
            return success;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgress();
            if(s.equals("Success")){
                addNotification(dwldsPath);
                //Utils.showAlert(ViewSavedWorkList.this,"Download Successfully File Path:"+path);


            }
            else {
                Utils.showAlert(ViewSavedWorkList.this,"Download Fail");
            }

        }
    }

    private void addNotification(File filePath) {
        //Log.d("filePath >> ", "" + filePath);
        Uri uriPdfPath = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", filePath);
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenIntent.setClipData(ClipData.newRawUri("", uriPdfPath));
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf");
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, pdfOpenIntent, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(null);
        bigText.setSummaryText("");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_message_black_24dp);
        mBuilder.setContentTitle("Inspection Report Downloaded Successfully!");
        mBuilder.setContentText("File Path : "+filePath);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    /*
    private void addNotification(String path) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp) //set icon for notification
                        .setContentTitle("PDF Path") //set title of notification
                        .setContentText(path)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, SaveWorkDetailsActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("PDF Path", path);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }
*/
    void showProgress(){
        // creating progress bar dialog
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("File downloading ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(100);
        progressBar.setMax(100);
        progressBar.show();
        //reset progress bar and filesize status

    }
    void hideProgress(){
        progressBar.hide();
    }


    @Override
    public void getDate(String date) {
        String[] separated = date.split(":");
        fromDate = separated[0]; // this will contain "Fruit"
        toDate = separated[1];
        workListBinding.date.setText(fromDate+" to "+toDate);

        if(Utils.isOnline()){
            work_id = "";
            workListBinding.workId.setText("");
            workListBinding.recycler.setAdapter(null);
            getWorkDetails();
        }
        else {
            Utils.showAlert(ViewSavedWorkList.this,"No Internet");
        }

    }
    public void getWorkDetails() {
        try {
            new ApiService(this).makeJSONObjectRequest("WorkDetails", Api.Method.POST, UrlGenerator.getMainService(), workDetailsJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject workDetailsJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), workDetailsParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("WorkDetails", "" + authKey);
        return dataSet;
    }
    public  JSONObject workDetailsParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "date_wise_inspection_details_view");
        if(!work_id.isEmpty()){
            dataSet.put("work_id", work_id);
            dataSet.put("type", 1);
        }
        else {
            dataSet.put("type", 2);
            dataSet.put("from_date", fromDate);
            dataSet.put("to_date", toDate);
        }
        Log.d("WorkDetails", "" + dataSet);
        return dataSet;
    }

    private void workListData(JSONArray jsonArray) {
        try {

            if (jsonArray.length() > 0) {
                savedWorkList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String dcode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                    String bcode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                    String pvcode = jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE);
                    String inspection_id = jsonArray.getJSONObject(i).getString("inspection_id");
                    String inspection_date = jsonArray.getJSONObject(i).getString("inspection_date");
                    String status_id = jsonArray.getJSONObject(i).getString("status_id");
                    String status = jsonArray.getJSONObject(i).getString("status");
                    String description = jsonArray.getJSONObject(i).getString("description");
                    String work_name = jsonArray.getJSONObject(i).getString("work_name");
                    String work_id = jsonArray.getJSONObject(i).getString("work_id");

                    ModelClass modelClass = new ModelClass();
                    modelClass.setDistictCode(dcode);
                    modelClass.setBlockCode(bcode);
                    modelClass.setPvCode(pvcode);
                    modelClass.setInspection_id(inspection_id);
                    modelClass.setInspectedDate(inspection_date);
                    modelClass.setWork_status_id(Integer.parseInt(status_id));
                    modelClass.setWork_status(status);
                    modelClass.setDescription(description);
                    modelClass.setWork_name(work_name);
                    modelClass.setWork_id(Integer.parseInt(work_id));
                    savedWorkList.add(modelClass);

                }

                if (savedWorkList.size()>0){
                    savedWorkListAdapter = new SavedWorkListAdapter(ViewSavedWorkList.this,savedWorkList);
                    workListBinding.recycler.setVisibility(View.VISIBLE);
                    workListBinding.notFoundTv.setVisibility(View.GONE);
                    workListBinding.recycler.setAdapter(savedWorkListAdapter);
                }
                else {
                    workListBinding.recycler.setVisibility(View.GONE);
                    workListBinding.notFoundTv.setVisibility(View.VISIBLE);
                    workListBinding.recycler.setAdapter(null);
                }

            } else {
                Utils.showAlert(this, "No Record Found for Corresponding Work");
                workListBinding.recycler.setVisibility(View.GONE);
                workListBinding.notFoundTv.setVisibility(View.VISIBLE);
                workListBinding.recycler.setAdapter(null);
            }

        } catch (JSONException | ArrayIndexOutOfBoundsException j) {
            j.printStackTrace();
        }

    }
}
