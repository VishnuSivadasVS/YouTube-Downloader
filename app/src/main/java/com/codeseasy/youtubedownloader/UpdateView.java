package com.codeseasy.youtubedownloader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class UpdateView extends AppCompatActivity {
    private RequestQueue mQueue;
    TextView updateTextViewd,updateappv,updatecontact,updaterdate,devupdate,updatehead;
    String appname,version,releasedate,downloadurl,description;
    String updatenoteurl = "https://android.codeseasy.com/YouTube-Downloader/update-note.json";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_view);
        setTitle("Update Available");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        updateTextViewd=findViewById(R.id.updateviewdetails);

        updateappv=findViewById(R.id.updateappversion);
        updaterdate = findViewById(R.id.updatereleasedate);
        updatehead=findViewById(R.id.updateheading);

        devupdate = findViewById(R.id.developerupdate);
        devupdate.setMovementMethod(LinkMovementMethod.getInstance());
        updatecontact= findViewById(R.id.updatecontactus);
        updatecontact.setMovementMethod(LinkMovementMethod.getInstance());

        mQueue = Volley.newRequestQueue(this);
        jsonParse(updatenoteurl);
    }

    private void jsonParse(final String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("updatearray");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject videodetails = jsonArray.getJSONObject(i);

                                appname = videodetails.getString("appname");
                                version = videodetails.getString("version");
                                releasedate = videodetails.getString("releasedate");
                                downloadurl = videodetails.getString("downloadurl");
                                description = videodetails.getString("description");
                                updateTextViewd.setText(description);
                                updateappv.append(version);
                                updaterdate.append(releasedate);
                                updatehead.append(version+" is available");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void DownloadingFunction(String downloadurlDF){
        String WritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String ReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        } else {
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadurlDF));
            request.setTitle(appname);
            request.setDestinationInExternalPublicDir("/CodesEasy/", appname + ".apk");
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        }
    }

    public void buttondownloadupdate(View view) {
        if(downloadurl==null) {
            Toast.makeText(this,"Fetching Update...",Toast.LENGTH_LONG).show();
        }
        else {
            DownloadingFunction(downloadurl);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}

