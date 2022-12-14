package com.nic.InspectionAppNew.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Kavitha M on 19-09-2022.
 */

public class ModelClass implements Serializable {

    private String stateCode;
    private String stateName;
    private String districtCode;
    private String districtName;
    private String blockCode;
    private String HabCode;
    private String Description;
    private String Latitude;
    private String Longtitude;
    private String image_path;
    private int img_id;
    private int image_serial_number;
    private String PvCode;
    private String PvName;
    private String blockName;
    private String HabitationName;
    private Bitmap Image;
    private int work_id;
    private String work_name;
    private int other_work_category_id;
    private String other_work_category_name;
    private String work_status;
    private int work_status_id;
    private String work_type;
    private int photo_type_id;
    private String photo_type_name;
    private int min_no_of_photos;
    private int max_no_of_photos;
    private String financialYear;
    private String VillageListDistrictCode;
    private String VillageListBlockCode;
    private String VillageListPvCode;
    private String VillageListPvName;
    private String schemeSequentialID;
    private String schemeName;
    private boolean schemeCheck;
    private String scheme_group_id;
    private String work_type_id;
    private String as_value;
    private String ts_value;
    private String current_stage_of_work;
    private String stage_name;
    private String is_high_value;
    private String work_group_id;
    private String other_work_detail;
    private int save_other_work_details_primary_id;
    private int save_work_details_primary_id;
    private int save_work_details_image_primary_id;
    private String work_description;
    private String inspection_id;
    private String inspectedDate;
    private String action_taken_id;
    private String action_taken_date;
    private String action_status;
    private String reported_by;


    private String as_date;
    private String ts_date;
    private String work_order_date;
    private String work_type_name;
    private String other_work_inspection_id;
    private String gender_code;
    private String gender_name_en;
    private String gender_name_ta;

    private String localbody_name;
    private String localbody_code;

    private String desig_code;
    private String desig_name;
    private String inspection_by_officer;
    private String inspection_by_officer_designation;

    String work_stage_code;
    String work_stage_order;
    String work_stage_name;
    String flag;
    int total_cout;
    int satisfied_count;
    int unsatisfied_count;
    int needimprovement_count;


    public String getReported_by() {
        return reported_by;
    }

    public void setReported_by(String reported_by) {
        this.reported_by = reported_by;
    }

    public String getAction_status() {
        return action_status;
    }

    public void setAction_status(String action_status) {
        this.action_status = action_status;
    }

    public String getAction_taken_date() {
        return action_taken_date;
    }

    public void setAction_taken_date(String action_taken_date) {
        this.action_taken_date = action_taken_date;
    }

    public String getAction_taken_id() {
        return action_taken_id;
    }

    public void setAction_taken_id(String action_taken_id) {
        this.action_taken_id = action_taken_id;
    }

    public String getInspection_by_officer_designation() {
        return inspection_by_officer_designation;
    }

    public void setInspection_by_officer_designation(String inspection_by_officer_designation) {
        this.inspection_by_officer_designation = inspection_by_officer_designation;
    }

    public String getInspection_by_officer() {
        return inspection_by_officer;
    }

    public void setInspection_by_officer(String inspection_by_officer) {
        this.inspection_by_officer = inspection_by_officer;
    }

    public int getTotal_cout() {
        return total_cout;
    }

    public void setTotal_cout(int total_cout) {
        this.total_cout = total_cout;
    }

    public int getSatisfied_count() {
        return satisfied_count;
    }

    public void setSatisfied_count(int satisfied_count) {
        this.satisfied_count = satisfied_count;
    }

    public int getUnsatisfied_count() {
        return unsatisfied_count;
    }

    public void setUnsatisfied_count(int unsatisfied_count) {
        this.unsatisfied_count = unsatisfied_count;
    }

    public int getNeedimprovement_count() {
        return needimprovement_count;
    }

    public void setNeedimprovement_count(int needimprovement_count) {
        this.needimprovement_count = needimprovement_count;
    }

    public String getAs_date() {
        return as_date;
    }

    public void setAs_date(String as_date) {
        this.as_date = as_date;
    }

    public String getTs_date() {
        return ts_date;
    }

    public void setTs_date(String ts_date) {
        this.ts_date = ts_date;
    }

    public String getWork_order_date() {
        return work_order_date;
    }

    public void setWork_order_date(String work_order_date) {
        this.work_order_date = work_order_date;
    }

    public String getWork_type_name() {
        return work_type_name;
    }

    public void setWork_type_name(String work_type_name) {
        this.work_type_name = work_type_name;
    }
    public String getStage_name() {
        return stage_name;
    }

    public void setStage_name(String stage_name) {
        this.stage_name = stage_name;
    }

    public int getOther_work_category_id() {
        return other_work_category_id;
    }

    public void setOther_work_category_id(int other_work_category_id) {
        this.other_work_category_id = other_work_category_id;
    }

    public String getOther_work_category_name() {
        return other_work_category_name;
    }

    public void setOther_work_category_name(String other_work_category_name) {
        this.other_work_category_name = other_work_category_name;
    }

    public boolean isSchemeCheck() {
        return schemeCheck;
    }

    public String getOther_work_detail() {
        return other_work_detail;
    }

    public void setOther_work_detail(String other_work_detail) {
        this.other_work_detail = other_work_detail;
    }

    public int getSave_other_work_details_primary_id() {
        return save_other_work_details_primary_id;
    }

    public void setSave_other_work_details_primary_id(int save_other_work_details_primary_id) {
        this.save_other_work_details_primary_id = save_other_work_details_primary_id;
    }

    public boolean getSchemeCheck() {
        return schemeCheck;
    }

    public void setSchemeCheck(boolean schemeCheck) {
        this.schemeCheck = schemeCheck;
    }

    public String getInspection_id() {
        return inspection_id;
    }

    public void setInspection_id(String inspection_id) {
        this.inspection_id = inspection_id;
    }

    public String getInspectedDate() {
        return inspectedDate;
    }

    public void setInspectedDate(String inspectedDate) {
        this.inspectedDate = inspectedDate;
    }

    public String getWork_description() {
        return work_description;
    }

    public void setWork_description(String work_description) {
        this.work_description = work_description;
    }

    public int getSave_work_details_primary_id() {
        return save_work_details_primary_id;
    }

    public void setSave_work_details_primary_id(int save_work_details_primary_id) {
        this.save_work_details_primary_id = save_work_details_primary_id;
    }

    public int getSave_work_details_image_primary_id() {
        return save_work_details_image_primary_id;
    }

    public void setSave_work_details_image_primary_id(int save_work_details_image_primary_id) {
        this.save_work_details_image_primary_id = save_work_details_image_primary_id;
    }

    public String getWork_group_id() {
        return work_group_id;
    }

    public void setWork_group_id(String work_group_id) {
        this.work_group_id = work_group_id;
    }

    public String getScheme_group_id() {
        return scheme_group_id;
    }

    public void setScheme_group_id(String scheme_group_id) {
        this.scheme_group_id = scheme_group_id;
    }

    public String getWork_type_id() {
        return work_type_id;
    }

    public void setWork_type_id(String work_type_id) {
        this.work_type_id = work_type_id;
    }

    public String getAs_value() {
        return as_value;
    }

    public void setAs_value(String as_value) {
        this.as_value = as_value;
    }

    public String getTs_value() {
        return ts_value;
    }

    public void setTs_value(String ts_value) {
        this.ts_value = ts_value;
    }

    public String getCurrent_stage_of_work() {
        return current_stage_of_work;
    }

    public void setCurrent_stage_of_work(String current_stage_of_work) {
        this.current_stage_of_work = current_stage_of_work;
    }

    public String getIs_high_value() {
        return is_high_value;
    }

    public void setIs_high_value(String is_high_value) {
        this.is_high_value = is_high_value;
    }

    public int getWork_status_id() {
        return work_status_id;
    }

    public void setWork_status_id(int work_status_id) {
        this.work_status_id = work_status_id;
    }

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getSchemeSequentialID() {
        return schemeSequentialID;
    }

    public void setSchemeSequentialID(String schemeSequentialID) {
        this.schemeSequentialID = schemeSequentialID;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getVillageListDistrictCode() {
        return VillageListDistrictCode;
    }

    public void setVillageListDistrictCode(String villageListDistrictCode) {
        VillageListDistrictCode = villageListDistrictCode;
    }

    public String getVillageListBlockCode() {
        return VillageListBlockCode;
    }

    public void setVillageListBlockCode(String villageListBlockCode) {
        VillageListBlockCode = villageListBlockCode;
    }

    public String getVillageListPvCode() {
        return VillageListPvCode;
    }

    public void setVillageListPvCode(String villageListPvCode) {
        VillageListPvCode = villageListPvCode;
    }

    public String getVillageListPvName() {
        return VillageListPvName;
    }

    public void setVillageListPvName(String villageListPvName) {
        VillageListPvName = villageListPvName;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getWork_type() {
        return work_type;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getHabCode() {
        return HabCode;
    }

    public void setHabCode(String habCode) {
        HabCode = habCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(String longtitude) {
        Longtitude = longtitude;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public int getImage_serial_number() {
        return image_serial_number;
    }

    public void setImage_serial_number(int image_serial_number) {
        this.image_serial_number = image_serial_number;
    }

    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        PvCode = pvCode;
    }

    public String getPvName() {
        return PvName;
    }

    public void setPvName(String pvName) {
        PvName = pvName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public void setHabitationName(String habitationName) {
        HabitationName = habitationName;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public int getWork_id() {
        return work_id;
    }

    public void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    public String getWork_name() {
        return work_name;
    }

    public void setWork_name(String work_name) {
        this.work_name = work_name;
    }

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public int getPhoto_type_id() {
        return photo_type_id;
    }

    public void setPhoto_type_id(int photo_type_id) {
        this.photo_type_id = photo_type_id;
    }

    public String getPhoto_type_name() {
        return photo_type_name;
    }

    public void setPhoto_type_name(String photo_type_name) {
        this.photo_type_name = photo_type_name;
    }

    public int getMin_no_of_photos() {
        return min_no_of_photos;
    }

    public void setMin_no_of_photos(int min_no_of_photos) {
        this.min_no_of_photos = min_no_of_photos;
    }

    public int getMax_no_of_photos() {
        return max_no_of_photos;
    }

    public void setMax_no_of_photos(int max_no_of_photos) {
        this.max_no_of_photos = max_no_of_photos;
    }


    public String getOther_work_inspection_id() {
        return other_work_inspection_id;
    }

    public void setOther_work_inspection_id(String other_work_inspection_id) {
        this.other_work_inspection_id = other_work_inspection_id;
    }

    public String getDesig_code() {
        return desig_code;
    }

    public void setDesig_code(String desig_code) {
        this.desig_code = desig_code;
    }

    public String getDesig_name() {
        return desig_name;
    }

    public void setDesig_name(String desig_name) {
        this.desig_name = desig_name;
    }

    public String getLocalbody_name() {
        return localbody_name;
    }

    public void setLocalbody_name(String localbody_name) {
        this.localbody_name = localbody_name;
    }

    public String getLocalbody_code() {
        return localbody_code;
    }

    public void setLocalbody_code(String localbody_code) {
        this.localbody_code = localbody_code;
    }

    public String getGender_code() {
        return gender_code;
    }

    public void setGender_code(String gender_code) {
        this.gender_code = gender_code;
    }

    public String getGender_name_en() {
        return gender_name_en;
    }

    public void setGender_name_en(String gender_name_en) {
        this.gender_name_en = gender_name_en;
    }

    public String getGender_name_ta() {
        return gender_name_ta;
    }

    public void setGender_name_ta(String gender_name_ta) {
        this.gender_name_ta = gender_name_ta;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getWork_stage_code() {
        return work_stage_code;
    }

    public void setWork_stage_code(String work_stage_code) {
        this.work_stage_code = work_stage_code;
    }

    public String getWork_stage_order() {
        return work_stage_order;
    }

    public void setWork_stage_order(String work_stage_order) {
        this.work_stage_order = work_stage_order;
    }

    public String getWork_stage_name() {
        return work_stage_name;
    }

    public void setWork_stage_name(String work_stage_name) {
        this.work_stage_name = work_stage_name;
    }
}