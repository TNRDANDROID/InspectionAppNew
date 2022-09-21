package com.nic.InspectionAppNew.constant;

/**
 * Created by User on 24/05/16.
 */
public class AppConstant {
    public static final String PREF_NAME = "NIC";
    public static String KEY_SERVICE_ID = "service_id";
    public static String KEY_APP_CODE = "appcode";
    public static String JSON_DATA = "JSON_DATA";
    public static String KEY_VERSION_CHECK= "version_check";
    public static String DATA_CONTENT = "data_content";
    public static String ENCODE_DATA = "enc_data";


    public static String USER_LOGIN_KEY = "user_login_key";
    public static String KEY_USER_NAME = "user_name";
    public static String KEY_USER_PASSWORD = "user_pwd";
    public static String KEY_RESPONSE = "RESPONSE";
    public static String KEY_STATUS = "STATUS";
    public static String KEY_MESSAGE = "MESSAGE";
    public static String KEY_USER = "KEY";
    public static String USER_DATA = "user_data";
    public static String KEY_NAME = "name";

    public static String STATE_CODE = "scode";
    public static String STATE_NAME = "sname";
    public static String DISTRICT_CODE = "dcode";
    public static String DISTRICT_NAME = "dname";

    public static String BLOCK_CODE = "bcode";
    public static String PV_CODE = "pvcode";
    public static String HAB_CODE = "habcode";
    public static String HABITATION_CODE = "habitation_code";
    public static String BLOCK_NAME = "bname";
    public static String PV_NAME = "pvname";
    public static String DESIG_NAME = "desig_name";
    public static String LEVELS = "levels";
    public static String KEY_DISTRICT_LIST_ALL = "district_list_all";
    public static String KEY_BLOCK_LIST_ALL = "block_list_all";
    public static String KEY_BLOCK_LIST_DISTRICT_WISE = "block_list_district_wise";

    public static String KEY_VILLAGE_LIST_ALL = "village_list_all";
    public static String KEY_VILLAGE_LIST_DISTRICT_WISE = "village_list_district_wise";
    public static String KEY_VILLAGE_LIST_DISTRICT_BLOCK_WISE = "village_list_district_block_wise";
    public static String KEY_HABITATION_LIST_DISTRICT_BLOCK_WISE = "habitation_list_district_block_wise";
    public static String KEY_SCHEME_LIST_ALL = "scheme_list_all";
    public static String KEY_SCHEME_LIST_DISTRICT_FINYEAR_WISE = "scheme_list_district_finyear_wise";
    public static String KEY_SCHEME_FINYEAR_LIST_LAST_NYEARS = "scheme_finyear_list_last_nyears";
    public static String KEY_OBSERVATION = "master_high_value_project_observation";
    public static String N_YEAR = "nyear";
    public static String SCHEME_SEQUENTIAL_ID = "scheme_seq_id";
    public static String SCHEME_NAME = "scheme_name";
    public static String KEY_WORK_LIST_OPTIONAL_ACTION = "work_list_optional_action";
    public static String KEY_WORK_LIST_OPTIONAL = "work_list_optional";
    public static String KEY_INSPECTION_WORK_DETAILS = "inspection_work_details";
    public static String SCHEME_ID = "scheme_id";
    public static String WORK_ID = "work_id";
    public static String WORK_NAME = "work_name";
    public static String WORK_STATUS = "work_status";
    public static String CURRENT_STAGE = "current_stage";

    public static String HABITATION_NAME = "habitation_name";
    public static String TYPE_OF_PHOTO = "type_of_photo";
    public static String KEY_LATITUDE = "latitude";
    public static String KEY_LONGITUDE = "longitude";
    public static String KEY_IMAGE = "image";

    public static String KEY_INSPECTION_STATUS = "inspection_status";
    public static String FINANCIAL_YEAR = "fin_year";
    public static String STATUS_ID = "status_id";
    public static String STATUS = "status";

    public static String SQL_QUERY= "select distinct  a.shg_code,a.shg_member_code,a.work_code,a.shg_name,a.member_name,a.work_name,a.fin_year\n" +
            "                       from save_after_tree_image_table as a left join save_before_tree_image_table as b on a.shg_code=b.shg_code and a.shg_member_code=b.shg_member_code and a.work_code=b.work_code";


}
