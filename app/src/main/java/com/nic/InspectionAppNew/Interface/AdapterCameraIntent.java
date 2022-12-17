package com.nic.InspectionAppNew.Interface;

import android.graphics.Bitmap;

public interface AdapterCameraIntent {

    public void OnIntentListener(Bitmap data, String filePath, Double wayLatitude, Double wayLongitude);

}