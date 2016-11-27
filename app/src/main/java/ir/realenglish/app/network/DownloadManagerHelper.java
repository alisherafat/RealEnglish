package ir.realenglish.app.network;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

public class DownloadManagerHelper extends AsyncTask {
    private Activity activity;
    private String url;
    private String downloadPath;
    private String fileName;
    private String requestType;


    public DownloadManagerHelper(Activity _activity, String _url, String _downloadPath) {
        activity = _activity;
        downloadPath = _downloadPath;
        url = _url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (fileName == null)
            fileName = getFileName(url);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        downloadFile();
        return null;
    }

    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        if (downloadCompleteListener != null)
            downloadCompleteListener.onDownloadComplete();
    }

    private void downloadFile() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(notificationTitle);
        request.setDescription(notificationDescription);
        if (requestType != null && !requestType.equals(""))
            request.setMimeType(requestType);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            if (SHOW_NOTIFICATION_PROGRESS)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            else
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        }
        request.setDestinationInExternalPublicDir(downloadPath, fileName);

        final DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(downloadId);


        boolean downloading = true;
        while (downloading) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);

            Cursor cursor = manager.query(query);
            cursor.moveToFirst();
            int downloadedSize = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int totalSize = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false;
            }
            final int percent = (int) (100.0f * (float) downloadedSize / totalSize);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (downloadProgressListener != null)
                        downloadProgressListener.onDownloadProgressListener(percent);
                }
            });
            cursor.close();
        }
    }

    private DownloadProgressListener downloadProgressListener;
    private DownloadCompleteListener downloadCompleteListener;
    private boolean SHOW_NOTIFICATION_PROGRESS = false;
    private String notificationTitle = "";
    private String notificationDescription = "";

    public DownloadManagerHelper showNotificationProgress() {
        SHOW_NOTIFICATION_PROGRESS = true;
        return this;
    }

    public DownloadManagerHelper setDownloadFileName(String _fileName) {
        fileName = _fileName;
        return this;
    }

    public DownloadManagerHelper setNotificationTitle(String _notificationTitle) {
        notificationTitle = _notificationTitle;
        return this;
    }

    public DownloadManagerHelper setNotificationDescription(String _notificationDescription) {
        notificationDescription = _notificationDescription;
        return this;
    }

    public interface DownloadCompleteListener {
        void onDownloadComplete();
    }

    public interface DownloadProgressListener {
        void onDownloadProgressListener(int percent);
    }

    public DownloadManagerHelper setDownloadProgressListener(DownloadProgressListener progressListener) {
        downloadProgressListener = progressListener;
        return this;
    }

    public DownloadManagerHelper setDownloadCompleteListener(DownloadCompleteListener completeListener) {
        downloadCompleteListener = completeListener;
        return this;
    }

    public DownloadManagerHelper setRequestType(String _requestType) {
        requestType = _requestType;
        return this;
    }

    public void startDownload() {
        this.execute();
    }

    private String getFileName(String url) {
        return (url.substring(url.lastIndexOf('/') + 1, url.length()));
    }
}