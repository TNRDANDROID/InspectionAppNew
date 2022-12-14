package com.nic.InspectionAppNew.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.dataBase.DBHelper;
import com.nic.InspectionAppNew.dataBase.dbData;

import com.nic.InspectionAppNew.databinding.LoginScreenBinding;
import com.nic.InspectionAppNew.model.ModelClass;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.support.ProgressHUD;
import com.nic.InspectionAppNew.utils.FontCache;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.nic.InspectionAppNew.utils.Utils.showAlert;


/**
 * Created by Dileep on 2022.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private String randString;

    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    JSONObject jsonObject;

    private PrefManager prefManager;
    private ProgressHUD progressHUD;
    private int setPType;

    public LoginScreenBinding loginScreenBinding;
    public dbData dbData = new dbData(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        prefManager = new PrefManager(this);
        loginScreenBinding = DataBindingUtil.setContentView(this, R.layout.login_screen);
        loginScreenBinding.setActivity(this);
        try {
            dbData.open();
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginScreenBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    loginScreenBinding.passwordImg.setVisibility(View.VISIBLE);
                }
                else {
                    loginScreenBinding.passwordImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        loginScreenBinding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this,OtpVerfication.class);
                intent.putExtra("mobile_number","");
                intent.putExtra("flag","forgot_password");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        intializeUI();

    }

    public void intializeUI() {

       // Utils.setLocale(prefManager.getKEY_Language(),this);
        loginScreenBinding.btnSignin.setOnClickListener(this);

        loginScreenBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        loginScreenBinding.inputLayoutEmail.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        loginScreenBinding.inputLayoutPassword.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        //loginScreenBinding.btnSignin.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        loginScreenBinding.inputLayoutEmail.setHintTextAppearance(R.style.InActive);
        loginScreenBinding.inputLayoutPassword.setHintTextAppearance(R.style.InActive);

        loginScreenBinding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkLoginScreen();
                }
                return false;
            }
        });
        loginScreenBinding.password.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.ttf"));
        randString = Utils.randomChar();


        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            loginScreenBinding.tvVersion.setText(getResources().getString(R.string.version) + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setPType = 1;
        //loginScreenBinding.redEye.setOnClickListener(this);
        loginScreenBinding.versionHint.setVisibility(View.GONE);
        if(Build.VERSION.SDK_INT >= 28) {
            //only api 28 above
            loginScreenBinding.versionHint.setVisibility(View.GONE);
        }else{
            //only api 28 down
            loginScreenBinding.versionHint.setVisibility(View.VISIBLE);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(300); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            loginScreenBinding.versionHint.startAnimation(anim);
        }

        loginScreenBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoRegisterScreen = new Intent(LoginScreen.this,RegistrationScreen.class);
                gotoRegisterScreen.putExtra("key","login");
                startActivity(gotoRegisterScreen);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        loginScreenBinding.verifyOtpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginScreen.this,OtpVerfication.class);
                intent.putExtra("mobile_number","");
                intent.putExtra("flag","login");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
    }

    public void showPassword() {
        if (setPType == 1) {
            setPType = 0;
            loginScreenBinding.password.setTransformationMethod(null);
            if (loginScreenBinding.password.getText().length() > 0) {
                loginScreenBinding.password.setSelection(loginScreenBinding.password.getText().length());
                //loginScreenBinding.redEye.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24px);
            }
        } else {
            setPType = 1;
            loginScreenBinding.password.setTransformationMethod(new PasswordTransformationMethod());
            if (loginScreenBinding.password.getText().length() > 0) {
                loginScreenBinding.password.setSelection(loginScreenBinding.password.getText().length());
                //loginScreenBinding.redEye.setBackgroundResource(R.drawable.ic_baseline_visibility_24px);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    public boolean validate() {
        boolean valid = true;
        String username = loginScreenBinding.userName.getText().toString().trim();
        //prefManager.setUserName(username);
        String password = loginScreenBinding.password.getText().toString().trim();




        if (username.isEmpty()) {
            valid = false;
            Utils.showAlert(this, getResources().getString(R.string.please_enter_the_user_name));
        } else if (password.isEmpty()) {
            valid = false;
            Utils.showAlert(this, getResources().getString(R.string.please_enter_the_password));
        }
        else if(prefManager.getUserName()!=null&&prefManager.getUserPassword()!=null){
            if((!username.equals(prefManager.getUserName()))){
                valid = false;
                Utils.showAlert(this, "Previous login was successfully not logged out.So login again with old user name and password and click logout icon in home page to successfully logout the previous session");
            }
        }
        return valid;
    }

    public void checkLoginScreen() {
        if(loginScreenBinding.versionHint.getVisibility() == View.GONE){
        //local
            // Testing
           /* loginScreenBinding.userName.setText("7448944000");
            loginScreenBinding.password.setText("Ccc111#$");*/

         //local
       loginScreenBinding.userName.setText("9080873403");
        loginScreenBinding.password.setText("crd45#$");// local
        /*loginScreenBinding.userName.setText("7877979787");
        loginScreenBinding.password.setText("test123#$");// local*/

     /* loginScreenBinding.userName.setText("9751337424");
        loginScreenBinding.password.setText("Test88#$");// local*/

      //prod
     /*   loginScreenBinding.userName.setText("9750895078");
        loginScreenBinding.password.setText("Test123#$");//block prod*/
       /* loginScreenBinding.userName.setText("7878534575");
        loginScreenBinding.password.setText("test123#$");//state prod*/
        /*loginScreenBinding.userName.setText("9638527415");
        loginScreenBinding.password.setText("test123#$");//Dist prod*/

        /*loginScreenBinding.userName.setText("9310633090");
        loginScreenBinding.password.setText("test123#$");//Block prod*/

       /* loginScreenBinding.userName.setText("7373704589");
        loginScreenBinding.password.setText("crd33#$");//Block prod
*/



        final String username = loginScreenBinding.userName.getText().toString().trim();
        final String password = loginScreenBinding.password.getText().toString().trim();
        //prefManager.setUserPassword(password);

        if (Utils.isOnline()) {
            if (!validate())
                return;
            else if (username.length() > 0 && password.length() > 0) {
                new ApiService(this).makeRequest("LoginScreen", Api.Method.POST, UrlGenerator.getLoginUrl(), loginParams(), "not cache", this);
            } else {
                Utils.showAlert(this, getResources().getString(R.string.please_enter_user_name_and_password));
            }
        }
        else {
            //Utils.showAlert(this, getResources().getString(R.string.no_internet));
            AlertDialog.Builder ab = new AlertDialog.Builder(
                    LoginScreen.this);
            ab.setMessage(getResources().getString(R.string.internet_connection_not_available_please_turn_on_or_offline));
            ab.setPositiveButton(getResources().getString(R.string.settings),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            Intent I = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(I);
                        }
                    });
            ab.setNegativeButton(getResources().getString(R.string.continue_with_offline),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            offline_mode(username, password);
                        }
                    });
            ab.show();
        }
        }
        else {
            Utils.showAlert(this, "Please update your android version to login!");
        }

    }


    public Map<String, String> loginParams() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstant.KEY_SERVICE_ID, "login");


        String random = Utils.randomChar();

        params.put(AppConstant.USER_LOGIN_KEY, random);
        Log.d("randchar", "" + random);

        params.put(AppConstant.KEY_USER_NAME, loginScreenBinding.userName.getText().toString().trim());
        Log.d("user", "" + loginScreenBinding.userName.getText().toString().trim());

        String encryptUserPass = Utils.md5(loginScreenBinding.password.getText().toString().trim());
        prefManager.setEncryptPass(encryptUserPass);
        Log.d("md5", "" + encryptUserPass);

        String userPass = encryptUserPass.concat(random);
        Log.d("userpass", "" + userPass);
        String sha256 = Utils.getSHA(userPass);
        Log.d("sha", "" + sha256);

        params.put(AppConstant.KEY_USER_PASSWORD, sha256);


        Log.d("user", "" + loginScreenBinding.userName.getText().toString().trim());

        Log.d("params", "" + params);
        return params;
    }

    //The method for opening the registration page and another processes or checks for registering
    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getServicesListUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getHabList() {
        try {
            new ApiService(this).makeJSONObjectRequest("HabitationList", Api.Method.POST, UrlGenerator.getServicesListUrl(), habitationListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public JSONObject districtListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.districtListJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("districtList", "" + dataSet);
        return dataSet;
    }
    public JSONObject blockListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.blockListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("blockListDistrictWise", "" + dataSet);
        return dataSet;
    }

    public JSONObject villageListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.villageListDistrictWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("villageListDistrictWise", "" + dataSet);
        return dataSet;
    }


    public JSONObject habitationListJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.HabitationListDistrictBlockVillageWiseJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("HabitationList", "" + dataSet);
        return dataSet;
    }
    private void getProfileData() {
        if (Utils.isOnline()) {
            try {
                new ApiService(LoginScreen.this).makeJSONObjectRequest("getProfileData", Api.Method.POST, UrlGenerator.getMainService(),  getProfileJsonParams(), "not cache", LoginScreen.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            showAlert(LoginScreen.this,"No Internet Connection!");
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

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("LoginScreen".equals(urlType)) {
                status = responseObj.getString(AppConstant.KEY_STATUS);
                response = responseObj.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")) {
                    if (response.equals("LOGIN_SUCCESS")) {
                        String key = responseObj.getString(AppConstant.KEY_USER);
                        String user_data = responseObj.getString(AppConstant.USER_DATA);
                        String decryptedKey = Utils.decrypt(prefManager.getEncryptPass(), key);
                        String userDataDecrypt = Utils.decrypt(prefManager.getEncryptPass(), user_data);
                        Log.d("userdatadecry", "" + responseObj.toString());
                        Log.d("userdatadecry", "" + userDataDecrypt);
                        jsonObject = new JSONObject(userDataDecrypt);

//                        prefManager.setPvCode(jsonObject.get(AppConstant.PV_CODE));
                        if(jsonObject.get("profile_image_found").equals("Y")){
                            if (!(jsonObject.get("profile_image").toString().equalsIgnoreCase("null") ||
                                    jsonObject.get("profile_image").toString().equalsIgnoreCase(""))) {
                                byte[] decodedString = Base64.decode(jsonObject.get("profile_image").toString(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                prefManager.setProfileImage(Utils.BitMapToString(decodedByte));
                            }

                        }else {
                            prefManager.setProfileImage("");
                        }
                        prefManager.setDesignation(jsonObject.get(AppConstant.DESIG_NAME));
                        String designation_code=jsonObject.getString("desig_code");
                        prefManager.setDesignationCode(designation_code);
                        prefManager.setName(String.valueOf(jsonObject.get(AppConstant.KEY_NAME)));
                        prefManager.setLevels(jsonObject.get(AppConstant.LEVELS));
                        Log.d("userdata", "" + prefManager.getDistrictCode() + prefManager.getBlockCode() + prefManager.getPvCode() + prefManager.getDistrictName() + prefManager.getBlockName() + prefManager.getName());
                        prefManager.setUserPassKey(decryptedKey);
                        prefManager.setUserName(loginScreenBinding.userName.getText().toString());
                        getProfileData();

                        if(jsonObject.get(AppConstant.LEVELS).equals("S")){
                            prefManager.setStateCode(jsonObject.get("statecode"));
//                            prefManager.setStateName(jsonObject.get(AppConstant.STATE_NAME));
                            prefManager.setStateName("Tamil Nadu");
                            prefManager.setDistrictCode("");
                            prefManager.setBlockCode("");
                            getDistrictList();
                            getBlockList();
                        }
                        else if(jsonObject.get(AppConstant.LEVELS).equals("D")){
                            prefManager.setStateCode(jsonObject.get("statecode"));
                            prefManager.setDistrictCode(jsonObject.get(AppConstant.DISTRICT_CODE));
                            prefManager.setDistrictName(jsonObject.get(AppConstant.DISTRICT_NAME));
                            prefManager.setBlockCode("");
                            getBlockList();
                        }
                        else if(jsonObject.get(AppConstant.LEVELS).equals("B")){
                            prefManager.setStateCode(jsonObject.get("statecode"));
                            prefManager.setDistrictCode(jsonObject.get(AppConstant.DISTRICT_CODE));
                            prefManager.setBlockCode(jsonObject.get(AppConstant.BLOCK_CODE));
                            prefManager.setBlockName(jsonObject.get(AppConstant.BLOCK_NAME));

                        }
                        prefManager.setUserName(loginScreenBinding.userName.getText().toString());
                        prefManager.setUserPassword(loginScreenBinding.password.getText().toString());

//                        getVillageList();
                        //getHabList();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showHomeScreen();
                            }
                        }, 1000);

                    } else {
                        if (response.equals("LOGIN_FAILED")) {
                            Utils.showAlert(this, getResources().getString(R.string.invalid_user_name_or_password));
                        }
                    }
                }

            }
            if ("getProfileData".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);

                Log.d("registration", "" + jsonObject.toString());
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
                    }

                }else {
                    showAlert(this, jsonObject.getString(AppConstant.KEY_MESSAGE));
                }

            }

            if ("DistrictList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                Log.d("DistrictList", "" + prefManager.getUserPassKey());
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertDistrictTask().execute(jsonObject);
                }
                Log.d("DistrictList", "" + responseObj.toString());
                Log.d("DistrictList", "" + responseDecryptedBlockKey);
            }
            if ("BlockList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertBlockTask().execute(jsonObject);
                }
                Log.d("BlockList", "" + responseObj.toString());
                Log.d("BlockList", "" + responseDecryptedBlockKey);
            }

            if ("VillageList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertVillageTask().execute(jsonObject);
                }
                Log.d("VillageList", "" + responseObj.toString());
                Log.d("VillageList", "" + responseDecryptedBlockKey);
            }
            if ("HabitationList".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertHabTask().execute(jsonObject);
                }
                Log.d("HabitationList", "" + responseObj.toString());
                Log.d("HabitationList", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class InsertDistrictTask extends AsyncTask<JSONObject ,Void ,Void> {

        private  ProgressHUD progressHUD;
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteDistrictTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String districtName = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME);

                        ContentValues districtListValues = new ContentValues();
                        districtListValues.put(AppConstant.DISTRICT_CODE, districtCode);
                        districtListValues.put(AppConstant.DISTRICT_NAME, districtName);

                        db.insert(DBHelper.DISTRICT_TABLE_NAME, null, districtListValues);
                        Log.d("LocalDBdistrictList", "" + districtListValues);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!((Activity) LoginScreen.this).isFinishing())
            {
                progressHUD = ProgressHUD.show(LoginScreen.this, "Loading...", true, false, null);
            }
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public class InsertBlockTask extends AsyncTask<JSONObject ,Void ,Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                dbData.open();
                dbData.deleteBlockTable();
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                        String blockName = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME);

                        ContentValues blockListValues = new ContentValues();
                        blockListValues.put(AppConstant.DISTRICT_CODE, districtCode);
                        blockListValues.put(AppConstant.BLOCK_CODE, blockCode);
                        blockListValues.put(AppConstant.BLOCK_NAME, blockName);

                        db.insert(DBHelper.BLOCK_TABLE_NAME, null, blockListValues);
                        Log.d("LocalDBblockList", "" + blockListValues);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!((Activity) LoginScreen.this).isFinishing())
            {
                progressHUD = ProgressHUD.show(LoginScreen.this, "Loading...", true, false, null);
            }
//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {
        private  ProgressHUD progressHUD;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!((Activity) LoginScreen.this).isFinishing())
            {
                progressHUD = ProgressHUD.show(LoginScreen.this, "Loading...", true, false, null);
            }//            Utils.showProgress(MainHomePage.this,progressHUD);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Utils.hideProgress(progressHUD);
            try {
                if (progressHUD != null)
                    progressHUD.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deleteVillageTable();
            ArrayList<ModelClass> villagelist_count = dbData.getAll_Village(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (villagelist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass villageListValue = new ModelClass();
                        try {
                            villageListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                            dbData.insertVillage(villageListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;
        }

    }

    public class InsertHabTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            ArrayList<ModelClass> hablist_count = dbData.getAll_Habitation(prefManager.getDistrictCode(),prefManager.getBlockCode());
            if (hablist_count.size() <= 0) {
                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ModelClass habListValue = new ModelClass();
                        try {
                            habListValue.setDistrictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                            habListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                            habListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                            habListValue.setHabCode(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_CODE));
                            habListValue.setHabitationName(jsonArray.getJSONObject(i).getString(AppConstant.HABITATION_NAME));

                            dbData.insertHabitation(habListValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
            return null;


        }
    }





    @Override
    public void OnError(VolleyError volleyError) {
        Utils.showAlert(this, getResources().getString(R.string.log_in_again));
    }



    private void showHomeScreen() {
        Intent intent = new Intent(LoginScreen.this, MainHomePage.class);
        intent.putExtra("Home", "Login");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void offline_mode(String name, String pass) {
        String userName = prefManager.getUserName();
        String password = prefManager.getUserPassword();
        if (name.equals(userName) && pass.equals(password)) {
            showHomeScreen();
        } else {
            Utils.showAlert(this, getResources().getString(R.string.no_data_available_for_offline_please_turn_on_network));
        }
    }

/*
    void hideProgress() {
        try {
            if (progressHUD != null)
                progressHUD.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

}
