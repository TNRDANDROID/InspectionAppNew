package com.nic.InspectionAppNew.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.api.Api;
import com.nic.InspectionAppNew.api.ApiService;
import com.nic.InspectionAppNew.api.ServerResponse;
import com.nic.InspectionAppNew.constant.AppConstant;
import com.nic.InspectionAppNew.databinding.ActivityOtpVerficationBinding;
import com.nic.InspectionAppNew.session.PrefManager;
import com.nic.InspectionAppNew.utils.UrlGenerator;
import com.nic.InspectionAppNew.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.nic.InspectionAppNew.utils.Utils.CategoryListJsonParams;
import static com.nic.InspectionAppNew.utils.Utils.showAlert;

public class OtpVerfication extends AppCompatActivity implements Api.ServerResponseListener{

    ActivityOtpVerficationBinding otpVerficationBinding;
    private PrefManager prefManager;
    String  mobile_number="";
    String  flag="";
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_otp_verfication);

        otpVerficationBinding = DataBindingUtil.setContentView(OtpVerfication.this,R.layout.activity_otp_verfication);
        otpVerficationBinding.setActivity(this);

        prefManager = new PrefManager(this);
        flag=getIntent().getStringExtra("flag");

        if(flag.equalsIgnoreCase("login")){
            otpVerficationBinding.sendOtpLayout.setVisibility(View.VISIBLE);
            otpVerficationBinding.otpVerificationLayout.setVisibility(View.GONE);
            otpVerficationBinding.changePasswordLayout.setVisibility(View.GONE);
        }else if(flag.equalsIgnoreCase("forgot_password")){
            otpVerficationBinding.sendOtpLayout.setVisibility(View.VISIBLE);
            otpVerficationBinding.otpVerificationLayout.setVisibility(View.GONE);
            otpVerficationBinding.changePasswordLayout.setVisibility(View.GONE);
        }else {
            mobile_number=getIntent().getStringExtra("mobile_number");
            otpVerficationBinding.sendOtpLayout.setVisibility(View.GONE);
            otpVerficationBinding.otpVerificationLayout.setVisibility(View.VISIBLE);
            otpVerficationBinding.changePasswordLayout.setVisibility(View.GONE);
        }
        otpVerficationBinding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!otpVerficationBinding.otp.getText().toString().equalsIgnoreCase("")){
                    if (Utils.isOnline()) {
                        try {
                            if(flag.equalsIgnoreCase("forgot_password")){
                                new ApiService(OtpVerfication.this).makeJSONObjectRequest("FORGOT_PASSWORD_OTP", Api.Method.POST, UrlGenerator.getOpenUrl(), FORGOT_PASSWORD_OTP_Params(), "not cache", OtpVerfication.this);

                            }else {
                                new ApiService(OtpVerfication.this).makeJSONObjectRequest("OTP", Api.Method.POST, UrlGenerator.getOpenUrl(), otpParams(), "not cache", OtpVerfication.this);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        showAlert(OtpVerfication.this,"No Internet Connection!");
                    }
                }else {
                    showAlert(OtpVerfication.this,"Please enter otp");
                }
            }
        });
        otpVerficationBinding.resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (Utils.isOnline()) {
                        try {
                            new ApiService(OtpVerfication.this).makeJSONObjectRequest("OTP_RESEND", Api.Method.POST, UrlGenerator.getOpenUrl(),  resend_otpParams(), "not cache", OtpVerfication.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        showAlert(OtpVerfication.this,"No Internet Connection!");
                    }

            }
        });
        otpVerficationBinding.sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (!otpVerficationBinding.mobileNo.getText().toString().isEmpty()&&Utils.isValidMobile1(otpVerficationBinding.mobileNo.getText().toString())) {
                        if (Utils.isOnline()) {
                        try {
                            if(flag.equalsIgnoreCase("forgot_password")){
                                new ApiService(OtpVerfication.this).makeJSONObjectRequest("FORGOT_PASSWORD_OTP_SEND", Api.Method.POST, UrlGenerator.getOpenUrl(),  FORGOT_PASSWORD_send_otpParams(), "not cache", OtpVerfication.this);

                            }else {
                                new ApiService(OtpVerfication.this).makeJSONObjectRequest("OTP_SEND", Api.Method.POST, UrlGenerator.getOpenUrl(),  send_otpParams(), "not cache", OtpVerfication.this);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        showAlert(OtpVerfication.this,"No Internet Connection!");
                    }
                    }else {
                        showAlert(OtpVerfication.this,"Enter Valid mobile number!");
                    }

            }
        });
        otpVerficationBinding.passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePassword();
            }
        });

        if(!mobile_number.isEmpty()){
            String mask = mobile_number.replaceAll("\\w(?=\\w{4})", "*");
            otpVerficationBinding.mobileNumberTxt.setText("( "+mask+" )");
            otpVerficationBinding.mobileNumberTxt1.setText("( "+mask+" )");
        }
        else {
            otpVerficationBinding.mobileNumberTxt.setText(mobile_number);
            otpVerficationBinding.mobileNumberTxt1.setText(mobile_number);
        }
    }

    private void validatePassword() {
        if(!otpVerficationBinding.password.getText().toString().isEmpty()){
            if(Utils.isValidPassword(otpVerficationBinding.password.getText().toString())&&Utils.isValidPasswordLength(otpVerficationBinding.password.getText().toString())){
                if(!otpVerficationBinding.password.getText().toString().isEmpty()){
                    if(otpVerficationBinding.password.getText().toString().equals(otpVerficationBinding.confirmPassword.getText().toString()) ){
                        if (Utils.isOnline()) {
                            try {
                                new ApiService(OtpVerfication.this).makeJSONObjectRequest("Change_Password", Api.Method.POST, UrlGenerator.getOpenUrl(),  change_password_Params(), "not cache", OtpVerfication.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            showAlert(OtpVerfication.this,"No Internet Connection!");
                        }
                    }else {
                        showAlert(OtpVerfication.this,"Your Password and Confirm Password does not match");
                    }
                }
                else {
                    showAlert(OtpVerfication.this,"Enter Confirm Password");
                }
            }
            else {
                otpVerficationBinding.password.requestFocus();
                showAlert(OtpVerfication.this,"Password Must Contain One capital letter,One Number,One Special Character and length should be above 8 character");
            }
        }
        else {
            showAlert(OtpVerfication.this,"Enter New Password");
        }
    }

    public  JSONObject otpParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "VerifyOtp");
        dataSet.put("mobile_otp", otpVerficationBinding.otp.getText().toString());
        dataSet.put("mobile_number",mobile_number);
        Log.d("otp", "" + dataSet);
        return dataSet;
    }
    public  JSONObject FORGOT_PASSWORD_OTP_Params() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "ChangePasswordVerifyOtp");
        dataSet.put("mobile_otp", otpVerficationBinding.otp.getText().toString());
        dataSet.put("mobile_number",mobile_number);
        Log.d("otp", "" + dataSet);
        return dataSet;
    }
    public  JSONObject  resend_otpParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "ResendOtp");
        dataSet.put("mobile_number",mobile_number);
        Log.d("resend_otp", "" + dataSet);
        return dataSet;
    }
    public  JSONObject  change_password_Params() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "change_password");
        dataSet.put("mobile_number",mobile_number);
        dataSet.put("password",otpVerficationBinding.password.getText().toString());
        dataSet.put("confirm_password",otpVerficationBinding.confirmPassword.getText().toString());
        Log.d("change_password", "" + dataSet);
        return dataSet;
    }
    public  JSONObject  send_otpParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "ResendOtp");
        dataSet.put("mobile_number",otpVerficationBinding.mobileNo.getText().toString());
        Log.d("send_otp", "" + dataSet);
        return dataSet;
    }
    public  JSONObject  FORGOT_PASSWORD_send_otpParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "sendOTP_for_change_password");
        dataSet.put("mobile_number",otpVerficationBinding.mobileNo.getText().toString());
        Log.d("send_otp", "" + dataSet);
        return dataSet;
    }
    private void showSignInScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(OtpVerfication.this, LoginScreen.class);
                startActivity(i);

                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;
            if ("OTP".equals(urlType) && responseObj != null) {
                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert1(responseObj.getString("MESSAGE"));
                    //showSignInScreen();

                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("OTP", "" + responseObj.toString());

            }
            if ("FORGOT_PASSWORD_OTP".equals(urlType) && responseObj != null) {
                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, responseObj.getString(AppConstant.KEY_MESSAGE));
                    mobile_number=otpVerficationBinding.mobileNo.getText().toString();
                    otpVerficationBinding.sendOtpLayout.setVisibility(View.GONE);
                    otpVerficationBinding.changePasswordLayout.setVisibility(View.VISIBLE);
                    otpVerficationBinding.otpVerificationLayout.setVisibility(View.GONE);

                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("OTP", "" + responseObj.toString());

            }
            if ("OTP_RESEND".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, responseObj.getString("MESSAGE"));
                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("OTP_RESEND", "" + responseObj.toString());

            }
            if ("OTP_SEND".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, responseObj.getString("MESSAGE"));
                    mobile_number=otpVerficationBinding.mobileNo.getText().toString();
                    String mask = mobile_number.replaceAll("\\w(?=\\w{4})", "*");
                    otpVerficationBinding.mobileNumberTxt1.setText("( "+mask+" )");
                    otpVerficationBinding.sendOtpLayout.setVisibility(View.GONE);
                    otpVerficationBinding.changePasswordLayout.setVisibility(View.GONE);
                    otpVerficationBinding.otpVerificationLayout.setVisibility(View.VISIBLE);

                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("OTP_RESEND", "" + responseObj.toString());

            }
            if ("FORGOT_PASSWORD_OTP_SEND".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert(this, responseObj.getString("MESSAGE"));
                    mobile_number=otpVerficationBinding.mobileNo.getText().toString();
                    String mask = mobile_number.replaceAll("\\w(?=\\w{4})", "*");
                    otpVerficationBinding.mobileNumberTxt1.setText("( "+mask+" )");
                    otpVerficationBinding.sendOtpLayout.setVisibility(View.GONE);
                    otpVerficationBinding.changePasswordLayout.setVisibility(View.GONE);
                    otpVerficationBinding.otpVerificationLayout.setVisibility(View.VISIBLE);

                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("FORGOT_PW_OTP_SEND", "" + responseObj.toString());

            }
            if ("Change_Password".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    showAlert1(responseObj.getString("MESSAGE"));

                }else {
                    showAlert(this, responseObj.getString(AppConstant.KEY_RESPONSE));
                }
                Log.d("Change_Password", "" + responseObj.toString());

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    public  void showAlert1( String msg){
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignInScreen();
                    dialog.dismiss();

                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
