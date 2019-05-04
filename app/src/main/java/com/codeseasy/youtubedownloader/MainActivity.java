package com.codeseasy.youtubedownloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String youTubeURL = null;
    TextView textView;
    String WritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String ReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        startService(intent);

        textView = findViewById(R.id.createdby);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        }
        editText = findViewById(R.id.youtubevideourlenter);

    }

    public void openFolder()
    {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

    }

    public void YouTubeVideoDownloadF(int iTag){

        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        } else {
                YTDownload(iTag);
        }
    }

    public void YTDownload(final int itag) {
        String VideoURLDownload = youTubeURL;
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("Download URL: ", downloadURL);

                    if (downloadURL != null) {
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
                        request.setTitle(videoTitle);
                        request.setDestinationInExternalPublicDir("/Downloads/YouTube-Downloader/", videoTitle + ".mp4");
                        if (downloadManager != null) {
                            downloadManager.enqueue(request);
                        }
                    }
                } else Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        };
        youTubeUriExtractor.execute(VideoURLDownload);
    }

    public void viewdownloadsbtn(View view) {
        openFolder();
    }

    public void ytvdownload(View view) {
        youTubeURL = editText.getText().toString();
        if (youTubeURL.contains("http"))
        YouTubeVideoDownloadF(18);
        else Toast.makeText(this,"Enter URL First",Toast.LENGTH_LONG).show();
    }

    public void ytvdownloadhd(View view) {
        youTubeURL = editText.getText().toString();
        if (youTubeURL.contains("http"))
        YouTubeVideoDownloadF(22);
        else Toast.makeText(this,"Enter URL First",Toast.LENGTH_LONG).show();
    }
}
