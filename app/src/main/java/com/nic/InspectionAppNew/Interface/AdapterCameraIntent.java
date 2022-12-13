package com.nic.InspectionAppNew.Interface;

import android.graphics.Bitmap;

import com.nic.InspectionAppNew.model.ModelClass;

import java.util.ArrayList;

public interface AdapterCameraIntent {

    public void OnIntentListener(Bitmap data, String filePath);
//    public ArrayList<ModelClass> OnIntentListenerImageList();
    public void OnIntentListenerPermission(boolean flag);
    public void   OnIntentListenerDescription(String s);
}