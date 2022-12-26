package com.nic.InspectionAppNew.api;

import com.android.volley.VolleyError;
import com.nic.InspectionAppNew.model.ModelClass;

import java.util.ArrayList;

/**
 * Created by Kavitha M on oct 2022.
 */
public class Api {

    public interface Method {
        int GET = 0;
        int POST = 1;
    }

    public interface OnParseListener {
        void onParseComplete(int i);
    }

    public interface ServerResponseListener {
        void OnMyResponse(ServerResponse serverResponse);

        void OnError(VolleyError volleyError);
    }

    public interface schemeListener {
        void OnMyScheme(ArrayList<ModelClass> selectedList);
    }

}
