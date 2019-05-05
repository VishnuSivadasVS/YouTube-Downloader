# YouTube Downloader
YouTube Downloader, an open source Android Application that allows you to download videos from YouTube.

> [Download APK](codeseasy.com/download/youtube-downloader/)

**Download Function, Download URL is fetched.**

``` 
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
```

**onClick for a Button View (Download in Normal Quality)**

```
    public void ytvdownload(View view) {
        youTubeURL = editText.getText().toString();
        if (youTubeURL.contains("http"))
        YouTubeVideoDownloadF(18);
        else Toast.makeText(this,"Enter URL First",Toast.LENGTH_LONG).show();
    }
```

**onClick for a Button View (Download in HD Quality)**
_This option could crash sometimes_
```
    public void ytvdownloadhd(View view) {
        youTubeURL = editText.getText().toString();
        if (youTubeURL.contains("http"))
        YouTubeVideoDownloadF(22);
        else Toast.makeText(this,"Enter URL First",Toast.LENGTH_LONG).show();
    }
```
