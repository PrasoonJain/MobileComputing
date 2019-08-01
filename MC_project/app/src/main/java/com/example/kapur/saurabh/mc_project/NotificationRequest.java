package com.example.kapur.saurabh.mc_project;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationRequest {


    public static void  Request(String topic, String title, String msg)
    {
        Gson gson = new Gson();

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        JSONObject item2 = new JSONObject();
        JSONObject item3 = new JSONObject();


        try {
            item.put("to", "/topics/"+topic);
            item.put("priority", "high");
            item2.put("title",title);
            item2.put("body",msg);
            item.put("notification", item2);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        array.put(item);
        String json = null;
        try {
            json = array.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("messss", json);
        String url = "https://fcm.googleapis.com/fcm/send";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        final RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "key=AAAALRD6z5c:APA91bF80zCCVHfbTNZQcztQ__JWxjp1jweTFXzIzKjENCENH1o0L1rFJzKlWF6r3xOT3fEXUdDVdnn6sekbQfkbNM3DsF3KdIuB59ThmoTNwHq6Kz-S9nuB1rkfelTAeAvTecegQmVj")
                .post(body)
                .build();
        Log.d("body", String.valueOf(request.toString()));


        Callback responseCallBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("response", response.toString());
                if (response.code() == 200)
                    response.close();
            }


        };
        okhttp3.Call call = client.newCall(request);
        call.enqueue(responseCallBack);

    }
}
