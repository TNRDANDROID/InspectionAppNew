package com.nic.InspectionAppNew.utils;


import com.nic.InspectionAppNew.R;
import com.nic.InspectionAppNew.application.NICApplication;

/**
 * Created by Kavitha M on Oct 2022.
 */
public class UrlGenerator {



    public static String getLoginUrl() {
        return NICApplication.getAppString(R.string.LOGIN_URL);
    }

    public static String getServicesListUrl() {
        return NICApplication.getAppString(R.string.BASE_SERVICES_URL);
    }

    public static String getMainService() {
        return NICApplication.getAppString(R.string.APP_MAIN_SERVICES_URL);
    }

    public static String getTnrdHostName() {
        return NICApplication.getAppString(R.string.TNRD_HOST_NAME);
    }
    public static String getOpenUrl() {
        return NICApplication.getAppString(R.string.OPEN_SERVICES_URL);
    }


}
