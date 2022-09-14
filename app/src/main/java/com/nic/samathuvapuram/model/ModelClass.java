package com.nic.samathuvapuram.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class ModelClass {

    private String distictCode;
    private String districtName;
    private String blockCode;
    private String HabCode;
    private String Description;
    private String Latitude;
    private String Longtitude;
    private String image_path;
    private int image_serial_number;
    private String PvCode;
    private String PvName;
    private String blockName;
    private String HabitationName;
    private Bitmap Image;
    private int menu_id;
    private String menu_name;
    private String menu_access_control;
    private int current_usage_id;
    private String current_usage;
    private String is_beneficiary_detail_required;
    private int photo_type_id;
    private int form_id;
    private String photo_type_name;
    private int min_no_of_photos;
    private int max_no_of_photos;
    private int community_category_id;
    private String community_category;
    private String estimate_type_name;
    private String category;
    private String has_sub_type;
    private String existing_condition_required;
    private String photos_required;
    private String scheme_name;
    private int estimate_type_id;
    private int scheme_group_id;
    private int scheme_id;
    private int scheme_disp_order;
    private int work_group_id;
    private int work_type_id;
    private String work_name;
    private int samathuvapuram_id;
    private String des;
    private String name;
    private String condition_of_infra;
    private int condition_of_infra_id;
    private int condition_of_house_id;
    private String condition_of_house;

    private int localbody_area_type;
    private int localbody_type_id;
    private int dcode;
    private int bcode;
    private int tpcode;
    private int no_of_houses_constructed;
    private String muncode;
    private String corpcode;
    private String year_of_construction;
    private String dname;
    private String bname;
    private String townpanchayat_name;
    private String municipality_name;
    private String corporation_name;
    private String name_of_the_beneficiary;
    private String gender;
    private String house_sanctioned_order_no;
    private String estimate_cost_required;
    private String type;
    private int repair_infra_estimate_id;


    public String getType() {
        return type;
    }

    public ModelClass setType(String type) {
        this.type = type;
        return this;
    }

    public int getRepair_infra_estimate_id() {
        return repair_infra_estimate_id;
    }

    public ModelClass setRepair_infra_estimate_id(int repair_infra_estimate_id) {
        this.repair_infra_estimate_id = repair_infra_estimate_id;
        return this;
    }

    public String getName_of_the_beneficiary() {
        return name_of_the_beneficiary;
    }

    public ModelClass setName_of_the_beneficiary(String name_of_the_beneficiary) {
        this.name_of_the_beneficiary = name_of_the_beneficiary;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public ModelClass setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getHouse_sanctioned_order_no() {
        return house_sanctioned_order_no;
    }

    public ModelClass setHouse_sanctioned_order_no(String house_sanctioned_order_no) {
        this.house_sanctioned_order_no = house_sanctioned_order_no;
        return this;
    }

    public String getEstimate_cost_required() {
        return estimate_cost_required;
    }

    public ModelClass setEstimate_cost_required(String estimate_cost_required) {
        this.estimate_cost_required = estimate_cost_required;
        return this;
    }

    public String getLongtitude() {
        return Longtitude;
    }

    public ModelClass setLongtitude(String longtitude) {
        Longtitude = longtitude;
        return this;
    }

    public String getImage_path() {
        return image_path;
    }

    public ModelClass setImage_path(String image_path) {
        this.image_path = image_path;
        return this;
    }

    public int getImage_serial_number() {
        return image_serial_number;
    }

    public ModelClass setImage_serial_number(int image_serial_number) {
        this.image_serial_number = image_serial_number;
        return this;
    }

    public int getLocalbody_area_type() {
        return localbody_area_type;
    }

    public ModelClass setLocalbody_area_type(int localbody_area_type) {
        this.localbody_area_type = localbody_area_type;
        return this;
    }

    public int getLocalbody_type_id() {
        return localbody_type_id;
    }

    public ModelClass setLocalbody_type_id(int localbody_type_id) {
        this.localbody_type_id = localbody_type_id;
        return this;
    }

    public int getDcode() {
        return dcode;
    }

    public ModelClass setDcode(int dcode) {
        this.dcode = dcode;
        return this;
    }

    public int getBcode() {
        return bcode;
    }

    public ModelClass setBcode(int bcode) {
        this.bcode = bcode;
        return this;
    }

    public int getTpcode() {
        return tpcode;
    }

    public ModelClass setTpcode(int tpcode) {
        this.tpcode = tpcode;
        return this;
    }

    public int getNo_of_houses_constructed() {
        return no_of_houses_constructed;
    }

    public ModelClass setNo_of_houses_constructed(int no_of_houses_constructed) {
        this.no_of_houses_constructed = no_of_houses_constructed;
        return this;
    }


    public String getMuncode() {
        return muncode;
    }

    public ModelClass setMuncode(String muncode) {
        this.muncode = muncode;
        return this;
    }

    public String getCorpcode() {
        return corpcode;
    }

    public ModelClass setCorpcode(String corpcode) {
        this.corpcode = corpcode;
        return this;
    }

    public String getYear_of_construction() {
        return year_of_construction;
    }

    public ModelClass setYear_of_construction(String year_of_construction) {
        this.year_of_construction = year_of_construction;
        return this;
    }

    public String getDname() {
        return dname;
    }

    public ModelClass setDname(String dname) {
        this.dname = dname;
        return this;
    }

    public String getBname() {
        return bname;
    }

    public ModelClass setBname(String bname) {
        this.bname = bname;
        return this;
    }


    public String getTownpanchayat_name() {
        return townpanchayat_name;
    }

    public ModelClass setTownpanchayat_name(String townpanchayat_name) {
        this.townpanchayat_name = townpanchayat_name;
        return this;
    }

    public String getMunicipality_name() {
        return municipality_name;
    }

    public ModelClass setMunicipality_name(String municipality_name) {
        this.municipality_name = municipality_name;
        return this;
    }

    public String getCorporation_name() {
        return corporation_name;
    }

    public ModelClass setCorporation_name(String corporation_name) {
        this.corporation_name = corporation_name;
        return this;
    }

    public String getCondition_of_infra() {
        return condition_of_infra;
    }

    public ModelClass setCondition_of_infra(String condition_of_infra) {
        this.condition_of_infra = condition_of_infra;
        return this;
    }

    public int getCondition_of_infra_id() {
        return condition_of_infra_id;
    }

    public ModelClass setCondition_of_infra_id(int condition_of_infra_id) {
        this.condition_of_infra_id = condition_of_infra_id;
        return this;
    }

    public int getCondition_of_house_id() {
        return condition_of_house_id;
    }

    public ModelClass setCondition_of_house_id(int condition_of_house_id) {
        this.condition_of_house_id = condition_of_house_id;
        return this;
    }

    public String getCondition_of_house() {
        return condition_of_house;
    }

    public ModelClass setCondition_of_house(String condition_of_house) {
        this.condition_of_house = condition_of_house;
        return this;
    }

    public String getDes() {
        return des;
    }

    public ModelClass setDes(String des) {
        this.des = des;
        return this;
    }

    public int getSamathuvapuram_id() {
        return samathuvapuram_id;
    }

    public ModelClass setSamathuvapuram_id(int samathuvapuram_id) {
        this.samathuvapuram_id = samathuvapuram_id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ModelClass setName(String name) {
        this.name = name;
        return this;
    }

    public int getWork_group_id() {
        return work_group_id;
    }

    public ModelClass setWork_group_id(int work_group_id) {
        this.work_group_id = work_group_id;
        return this;
    }

    public int getWork_type_id() {
        return work_type_id;
    }

    public ModelClass setWork_type_id(int work_type_id) {
        this.work_type_id = work_type_id;
        return this;
    }

    public String getWork_name() {
        return work_name;
    }

    public ModelClass setWork_name(String work_name) {
        this.work_name = work_name;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ModelClass setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getHas_sub_type() {
        return has_sub_type;
    }

    public ModelClass setHas_sub_type(String has_sub_type) {
        this.has_sub_type = has_sub_type;
        return this;
    }

    public String getExisting_condition_required() {
        return existing_condition_required;
    }

    public ModelClass setExisting_condition_required(String existing_condition_required) {
        this.existing_condition_required = existing_condition_required;
        return this;
    }

    public String getPhotos_required() {
        return photos_required;
    }

    public ModelClass setPhotos_required(String photos_required) {
        this.photos_required = photos_required;
        return this;
    }

    public String getScheme_name() {
        return scheme_name;
    }

    public ModelClass setScheme_name(String scheme_name) {
        this.scheme_name = scheme_name;
        return this;
    }

    public int getScheme_group_id() {
        return scheme_group_id;
    }

    public ModelClass setScheme_group_id(int scheme_group_id) {
        this.scheme_group_id = scheme_group_id;
        return this;
    }

    public int getScheme_id() {
        return scheme_id;
    }

    public ModelClass setScheme_id(int scheme_id) {
        this.scheme_id = scheme_id;
        return this;
    }

    public int getScheme_disp_order() {
        return scheme_disp_order;
    }

    public ModelClass setScheme_disp_order(int scheme_disp_order) {
        this.scheme_disp_order = scheme_disp_order;
        return this;
    }

    public String getEstimate_type_name() {
        return estimate_type_name;
    }

    public ModelClass setEstimate_type_name(String estimate_type_name) {
        this.estimate_type_name = estimate_type_name;
        return this;
    }

    public int getEstimate_type_id() {
        return estimate_type_id;
    }

    public ModelClass setEstimate_type_id(int estimate_type_id) {
        this.estimate_type_id = estimate_type_id;
        return this;
    }

    public int getCommunity_category_id() {
        return community_category_id;
    }

    public ModelClass setCommunity_category_id(int community_category_id) {
        this.community_category_id = community_category_id;
        return this;
    }

    public String getCommunity_category() {
        return community_category;
    }

    public ModelClass setCommunity_category(String community_category) {
        this.community_category = community_category;
        return this;
    }

    public int getPhoto_type_id() {
        return photo_type_id;
    }

    public ModelClass setPhoto_type_id(int photo_type_id) {
        this.photo_type_id = photo_type_id;
        return this;
    }

    public int getForm_id() {
        return form_id;
    }

    public ModelClass setForm_id(int form_id) {
        this.form_id = form_id;
        return this;
    }

    public String getPhoto_type_name() {
        return photo_type_name;
    }

    public ModelClass setPhoto_type_name(String photo_type_name) {
        this.photo_type_name = photo_type_name;
        return this;
    }

    public int getMin_no_of_photos() {
        return min_no_of_photos;
    }

    public ModelClass setMin_no_of_photos(int min_no_of_photos) {
        this.min_no_of_photos = min_no_of_photos;
        return this;
    }

    public int getMax_no_of_photos() {
        return max_no_of_photos;
    }

    public ModelClass setMax_no_of_photos(int max_no_of_photos) {
        this.max_no_of_photos = max_no_of_photos;
        return this;
    }

    public int getCurrent_usage_id() {
        return current_usage_id;
    }

    public ModelClass setCurrent_usage_id(int current_usage_id) {
        this.current_usage_id = current_usage_id;
        return this;
    }

    public String getCurrent_usage() {
        return current_usage;
    }

    public ModelClass setCurrent_usage(String current_usage) {
        this.current_usage = current_usage;
        return this;
    }

    public String getIs_beneficiary_detail_required() {
        return is_beneficiary_detail_required;
    }

    public ModelClass setIs_beneficiary_detail_required(String is_beneficiary_detail_required) {
        this.is_beneficiary_detail_required = is_beneficiary_detail_required;
        return this;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public ModelClass setMenu_id(int menu_id) {
        this.menu_id = menu_id;
        return this;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public ModelClass setMenu_name(String menu_name) {
        this.menu_name = menu_name;
        return this;
    }

    public String getMenu_access_control() {
        return menu_access_control;
    }

    public ModelClass setMenu_access_control(String menu_access_control) {
        this.menu_access_control = menu_access_control;
        return this;
    }

    public Bitmap getImage() {
        return Image;
    }

    public ModelClass setImage(Bitmap image) {
        Image = image;
        return this;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public ModelClass setHabitationName(String habitationName) {
        HabitationName = habitationName;
        return this;
    }

    public String getDistictCode() {
        return distictCode;
    }

    public ModelClass setDistictCode(String distictCode) {
        this.distictCode = distictCode;
        return this;
    }

    public String getDistrictName() {
        return districtName;
    }

    public ModelClass setDistrictName(String districtName) {
        this.districtName = districtName;
        return this;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public ModelClass setBlockCode(String blockCode) {
        this.blockCode = blockCode;
        return this;
    }

    public String getHabCode() {
        return HabCode;
    }

    public ModelClass setHabCode(String habCode) {
        HabCode = habCode;
        return this;
    }

    public String getDescription() {
        return Description;
    }

    public ModelClass setDescription(String description) {
        Description = description;
        return this;
    }

    public String getLatitude() {
        return Latitude;
    }

    public ModelClass setLatitude(String latitude) {
        Latitude = latitude;
        return this;
    }

    public String getPvCode() {
        return PvCode;
    }

    public ModelClass setPvCode(String pvCode) {
        PvCode = pvCode;
        return this;
    }

    public String getPvName() {
        return PvName;
    }

    public ModelClass setPvName(String pvName) {
        PvName = pvName;
        return this;
    }

    public String getBlockName() {
        return blockName;
    }

    public ModelClass setBlockName(String blockName) {
        this.blockName = blockName;
        return this;
    }


    /////House List
    private int current_beneficiary_id;
    private int house_serial_number;
    private String is_house_owned_by_sanctioned_beneficiary;
    private String current_house_usage;
    private String current_name_of_the_beneficiary;
    private String current_gender;
    private String current_community_category_id;

    public int getCurrent_beneficiary_id() {
        return current_beneficiary_id;
    }

    public void setCurrent_beneficiary_id(int current_beneficiary_id) {
        this.current_beneficiary_id = current_beneficiary_id;
    }

    public int getHouse_serial_number() {
        return house_serial_number;
    }

    public void setHouse_serial_number(int house_serial_number) {
        this.house_serial_number = house_serial_number;
    }

    public String getIs_house_owned_by_sanctioned_beneficiary() {
        return is_house_owned_by_sanctioned_beneficiary;
    }

    public void setIs_house_owned_by_sanctioned_beneficiary(String is_house_owned_by_sanctioned_beneficiary) {
        this.is_house_owned_by_sanctioned_beneficiary = is_house_owned_by_sanctioned_beneficiary;
    }

    public String getCurrent_house_usage() {
        return current_house_usage;
    }

    public void setCurrent_house_usage(String current_house_usage) {
        this.current_house_usage = current_house_usage;
    }

    public String getCurrent_name_of_the_beneficiary() {
        return current_name_of_the_beneficiary;
    }

    public void setCurrent_name_of_the_beneficiary(String current_name_of_the_beneficiary) {
        this.current_name_of_the_beneficiary = current_name_of_the_beneficiary;
    }

    public String getCurrent_gender() {
        return current_gender;
    }

    public void setCurrent_gender(String current_gender) {
        this.current_gender = current_gender;
    }

    public String getCurrent_community_category_id() {
        return current_community_category_id;
    }

    public void setCurrent_community_category_id(String current_community_category_id) {
        this.current_community_category_id = current_community_category_id;
    }
}