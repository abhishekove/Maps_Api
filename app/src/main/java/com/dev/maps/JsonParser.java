package com.dev.maps;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private static final String TAG = "JsonParser";
    private HashMap<String,String> parseJsonObject(JSONObject object) throws JSONException {
        HashMap<String,String> dataList=new HashMap<>();
        Log.d(TAG, "parseJsonObject: here");

        String name=object.getString("name");
        String lat=object.getJSONObject("geometry")
                .getJSONObject("location").getString("lat");
        String lng=object.getJSONObject("geometry")
                .getJSONObject("location").getString("lng");
        dataList.put("name",name);
        dataList.put("lat",lat);
        dataList.put("lng",lng);

        return dataList;

    }
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        Log.d(TAG, "parseJsonArray: here");
        List<HashMap<String,String>> datalist=new ArrayList<>();
        for (int i=0;i<jsonArray.length();i++){
            try {
                HashMap<String,String> data=parseJsonObject((JSONObject)jsonArray.get(i));
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "parseJsonArray: "+e.getLocalizedMessage());
            }
        }
        return datalist;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray=null;
        Log.d(TAG, "parseResult: here");
        try {
            jsonArray=object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseResult: "+e.getLocalizedMessage());
        }
        return parseJsonArray(jsonArray);
    }
}
