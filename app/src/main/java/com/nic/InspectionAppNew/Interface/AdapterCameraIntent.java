package com.nic.InspectionAppNew.Interface;

import android.graphics.Bitmap;

public interface AdapterCameraIntent {

    public void OnIntentListener(Bitmap data, String filePath, Double wayLatitude, Double wayLongitude);
//    public ArrayList<ModelClass> OnIntentListenerImageList();
    public void OnIntentListenerPermission(boolean flag);
    public void   OnIntentListenerDescription(String s);
}