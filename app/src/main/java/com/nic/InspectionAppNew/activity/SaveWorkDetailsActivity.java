package com.nic.InspectionAppNew.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.adapter.CommonAdapter;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;
import com.nic.InspectionAppNew.databinding.SaveWorkDetailsActivityBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveWorkDetailsActivity extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, RecognitionListener {
    private SaveWorkDetailsActivityBinding saveWorkDetailsActivityBinding;
    private PrefManager prefManager;
    public dbData dbData = new dbData(this);
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    Handler myHandler = new Handler();

    private ProgressHUD progressHUD;
    private List<ModelClass> status_list = new ArrayList<>();

    int work_id=0;
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
    String work_status;

    String hab_code;
    String scheme_group_id;
    String work_group_id;
    String work_type_id;
    String other_work_detail="";
    String other_work_category_id="";
    String other_work_inspection_id="";
    String inspection_id="";
    String flag="";
    int min_img_count=0;
    int max_img_count=0;
    int work_status_id=0;
    String onOffType;
    String type;
    String  activityImage="";
    private static final int SPEECH_REQUEST_CODE = 103;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private int maxLinesInput = 10;
    private TextView returnedText;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    boolean listening = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        saveWorkDetailsActivityBinding = DataBindingUtil.setContentView(this, R.layout.save_work_details_activity);
        saveWorkDetailsActivityBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        statusFilterSpinner();

        getIntentData();

        saveWorkDetailsActivityBinding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prefManager.setWorkStatusId(String.valueOf(status_list.get(position).getWork_status_id()));
                    work_status = status_list.get(position).getWork_status();
                    work_status_id = status_list.get(position).getWork_status_id();

                }else {
                    prefManager.setWorkStatusId("");
                    work_status = "";
                    work_status_id=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveWorkDetailsActivityBinding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkDetailsActivityBinding.description.setText("");
            }
        });
        saveWorkDetailsActivityBinding.tamilMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("ta");
            }
        });
        saveWorkDetailsActivityBinding.englishMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechToText("en");
            }
        });
        saveWorkDetailsActivityBinding.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOf();
            }
        });
        saveWorkDetailsActivityBinding.englishMic.setEnabled(true);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(true);
        saveWorkDetailsActivityBinding.clearText.setEnabled(true);
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.VISIBLE);
        Glide.with(this).asGif().load(R.raw.mic3).into(saveWorkDetailsActivityBinding.progressBarimg);

    }

    private void getIntentData(){
        type= getIntent().getStringExtra("type");
        onOffType= getIntent().getStringExtra("onOffType");
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
        hab_code = getIntent().getStringExtra("hab_code");
        scheme_group_id = getIntent().getStringExtra("scheme_group_id");
        work_group_id = getIntent().getStringExtra("work_group_id");
        work_type_id = getIntent().getStringExtra("work_type_id");
        other_work_category_id = getIntent().getStringExtra("other_work_category_id");
        flag = getIntent().getStringExtra("flag");
        saveWorkDetailsActivityBinding.notEditable.setVisibility(View.GONE);
        if(type.equalsIgnoreCase("rdpr")){
            saveWorkDetailsActivityBinding.otherWorksLayout.setVisibility(View.GONE);
        }else {
            saveWorkDetailsActivityBinding.otherWorksLayout.setVisibility(View.VISIBLE);
        }
        dbData.open();

        if(flag.equalsIgnoreCase("edit")){
            if(type.equalsIgnoreCase("rdpr")){
                work_status_id = getIntent().getIntExtra("status_id",0);
                String  status = getIntent().getStringExtra("status");
                String  work_name = getIntent().getStringExtra("work_name");
                String  description = getIntent().getStringExtra("description");
                inspection_id = getIntent().getStringExtra("inspection_id");
                activityImage = getIntent().getStringExtra("activityImage");
                saveWorkDetailsActivityBinding.description.setText(description);
                saveWorkDetailsActivityBinding.takePhoto.setText("View Photo");
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == work_status_id){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                        saveWorkDetailsActivityBinding.statusSpinner.setEnabled(false);
                        saveWorkDetailsActivityBinding.notEditable.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                other_work_inspection_id = getIntent().getStringExtra("other_work_inspection_id");
                String  other_work_category_name = getIntent().getStringExtra("other_work_category_name");
                work_status_id = getIntent().getIntExtra("status_id",0);
                String  status = getIntent().getStringExtra("status");
                String  other_work_detail = getIntent().getStringExtra("other_work_detail");
                String  description = getIntent().getStringExtra("description");
                activityImage = getIntent().getStringExtra("activityImage");
                saveWorkDetailsActivityBinding.description.setText(description);
                saveWorkDetailsActivityBinding.otherWorkDetail.setText(other_work_detail);
                saveWorkDetailsActivityBinding.takePhoto.setText("View Photo");
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == work_status_id){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                    }
                }
            }


        }else {
            dbData.open();
            ArrayList<ModelClass> savedCount = new ArrayList<>();
            savedCount=dbData.getSavedWorkList("",String.valueOf(work_id),dcode,bcode,pvcode);

            if(savedCount.size()>0){
                saveWorkDetailsActivityBinding.description.setText(savedCount.get(0).getWork_description());
                for(int i=0;i<status_list.size();i++){
                    if(status_list.get(i).getWork_status_id() == savedCount.get(0).getWork_status_id()){
                        saveWorkDetailsActivityBinding.statusSpinner.setSelection(i);
                    }
                }

            }
            else {
                saveWorkDetailsActivityBinding.description.setText("");
                saveWorkDetailsActivityBinding.statusSpinner.setSelection(0);
            }
        }
    }

    public void gotoCameraScreen()
    {
        if(type.equalsIgnoreCase("rdpr")){
            if(work_status_id != 0){
                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                    Intent intent = new Intent(this, CameraScreen.class);
                    intent.putExtra("dcode", dcode);
                    intent.putExtra("bcode", bcode);
                    intent.putExtra("pvcode",pvcode);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("scheme_group_id",scheme_group_id);
                    intent.putExtra("work_group_id",work_group_id);
                    intent.putExtra("work_type_id",work_type_id);
                    intent.putExtra("is_high_value",is_high_value);
                    intent.putExtra("scheme_id",scheme_id);
                    intent.putExtra("fin_year", fin_year);
                    intent.putExtra("work_id", work_id);
                    intent.putExtra("work_name", work_name);
                    intent.putExtra("as_value", as_value);
                    intent.putExtra("ts_value", ts_value);
                    intent.putExtra("current_stage_of_work", current_stage_of_work);
                    intent.putExtra("work_status_id", work_status_id);
                    intent.putExtra("work_status", work_status);
                    intent.putExtra("onOffType", onOffType);
                    intent.putExtra("work_description", saveWorkDetailsActivityBinding.description.getText().toString());
                    intent.putExtra("other_work_category_id", other_work_category_id);
                    intent.putExtra("other_work_detail", other_work_detail);
                    intent.putExtra("activityImage",activityImage);
                    intent.putExtra("type","rdpr");
                    intent.putExtra("flag",flag);
                    if(flag.equalsIgnoreCase("edit")){
                        intent.putExtra("inspection_id",inspection_id);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please Enter Description");
                }
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Please Select Status");
            }
        }
        else {
            if(!saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString().equals("")){
            if(work_status_id != 0){
                if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                    Intent intent = new Intent(this, CameraScreen.class);
                    intent.putExtra("dcode", dcode);
                    intent.putExtra("bcode", bcode);
                    intent.putExtra("pvcode",pvcode);
                    intent.putExtra("hab_code",hab_code);
                    intent.putExtra("scheme_group_id",scheme_group_id);
                    intent.putExtra("work_group_id",work_group_id);
                    intent.putExtra("work_type_id",work_type_id);
                    intent.putExtra("is_high_value",is_high_value);
                    intent.putExtra("scheme_id",scheme_id);
                    intent.putExtra("fin_year", fin_year);
                    intent.putExtra("work_id", work_id);
                    intent.putExtra("work_name", work_name);
                    intent.putExtra("as_value", as_value);
                    intent.putExtra("ts_value", ts_value);
                    intent.putExtra("current_stage_of_work", current_stage_of_work);
                    intent.putExtra("work_status_id", work_status_id);
                    intent.putExtra("work_status", work_status);
                    intent.putExtra("onOffType", onOffType);
                    intent.putExtra("work_description", saveWorkDetailsActivityBinding.description.getText().toString());
                    intent.putExtra("other_work_category_id", other_work_category_id);
                    intent.putExtra("other_work_detail", saveWorkDetailsActivityBinding.otherWorkDetail.getText().toString());
                    intent.putExtra("activityImage",activityImage);
                    intent.putExtra("type","other");
                    intent.putExtra("flag",flag);
                    if(flag.equalsIgnoreCase("edit")){
                        intent.putExtra("other_work_inspection_id",other_work_inspection_id);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                else {
                    Utils.showAlert(SaveWorkDetailsActivity.this,"Please enter description");
                }
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Please select status");
            }
            }
            else {
                Utils.showAlert(SaveWorkDetailsActivity.this,"Enter Other Inspection Detail");
            }
        }

    }
    public void statusFilterSpinner() {
        status_list = new ArrayList<>();
        status_list.clear();
        ModelClass list = new ModelClass();
        list.setWork_status_id(0);
        list.setWork_status("Select Status");
        status_list.add(list);
        dbData.open();
        status_list.addAll(dbData.getAll_Work_Status());
        saveWorkDetailsActivityBinding.statusSpinner.setAdapter(new CommonAdapter(this, status_list, "status"));
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
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
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
    

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void speechToText(String language) {
        listening = true;
        start(language);

        ActivityCompat.requestPermissions
                (SaveWorkDetailsActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }

    public void start(String language){
        saveWorkDetailsActivityBinding.englishMic.setEnabled(false);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(false);
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.VISIBLE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.clearText.setEnabled(false);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if(language.equalsIgnoreCase("en")){
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "en-US");

        }
        else {
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "ta-IND");
        }
/*
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
*/
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxLinesInput);
    }

    public void turnOf(){
        saveWorkDetailsActivityBinding.progressBar.setVisibility(View.GONE);
        saveWorkDetailsActivityBinding.downloadLayout.setVisibility(View.VISIBLE);
        saveWorkDetailsActivityBinding.englishMic.setEnabled(true);
        saveWorkDetailsActivityBinding.tamilMic.setEnabled(true);
        saveWorkDetailsActivityBinding.clearText.setEnabled(true);

        speech.stopListening();
        speech.destroy();
    }




/*
    public void speechToText(String language) {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if(language.equalsIgnoreCase("en")){
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "en-IND");
        }
        else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    "ta-IND");
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);

        } catch (Exception e) {
            Toast.makeText(SaveWorkDetailsActivity.this, " " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case SPEECH_REQUEST_CODE:

                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
                        saveWorkDetailsActivityBinding.description.setText(saveWorkDetailsActivityBinding.description.getText().toString()+" "+
                                Objects.requireNonNull(result).get(0));
                    }else {
                        saveWorkDetailsActivityBinding.description.setText(Objects.requireNonNull(result).get(0));

                    }
                }

                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SaveWorkDetailsActivity.this, "start talk...", Toast
                            .LENGTH_SHORT).show();
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(SaveWorkDetailsActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(prefManager.getAppBack() != null && prefManager.getAppBack().equalsIgnoreCase("back")){
            prefManager.setAppBack("");
            onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (speech != null) {
//            speech.destroy();
//            Log.i(LOG_TAG, "destroy");
//        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        /*if(!listening){
            turnOf();
            saveWorkDetailsActivityBinding.progressBar.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
//        returnedText.setText(errorMessage);
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onResults="+text);

        if(!saveWorkDetailsActivityBinding.description.getText().toString().equals("")){
            saveWorkDetailsActivityBinding.description.setText(saveWorkDetailsActivityBinding.description.getText().toString()+" "+
                    matches.get(0));
        }else {
            saveWorkDetailsActivityBinding.description.setText(matches.get(0));

        }

        speech.startListening(recognizerIntent);

    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        Log.i(LOG_TAG, "onPartialResults="+text);

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
//                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}
