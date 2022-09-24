package com.nic.InspectionAppNew.model;

import android.graphics.Bitmap;

/**
 * Created by Kavitha M on 19-09-2022.
 */

public class ModelClass {

    private String stateCode;
    private String stateName;
    private String distictCode;
    private String districtName;
    private String blockCode;
    private String HabCode;
    private String Description;
    private String Latitude;
    private String Longtitude;
    private String image_path;
    private String image;
    private int img_id;
    private int image_serial_number;
    private String PvCode;
    private String PvName;
    private String blockName;
    private String HabitationName;
    private Bitmap Image;
    private int work_id;
    private String work_name;
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
    private String scheme_group_id;
    private String work_type_id;
    private String as_value;
    private String ts_value;
    private String current_stage_of_work;
    private String is_high_value;
    private String work_group_id;
    private int save_work_details_primary_id;
    private int save_work_details_image_primary_id;
    private String work_description;
    private String inspection_id;
    private String inspectedDate;

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

    public void setImage(String image) {
        this.image = image;
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

    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
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
}