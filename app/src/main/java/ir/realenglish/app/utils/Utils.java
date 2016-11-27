package ir.realenglish.app.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ir.realenglish.app.BuildConfig;
import ir.realenglish.app.app.MyApp;

/**
 * Created by ALI-PC on 2015-12-22.
 */
public class Utils {

    @SuppressLint("NewApi")
    public static String getRealPathFromURI(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String readFileFromAssets(Context context, String path) {
        try {
            InputStream file = context.getAssets().open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            String line, outPut = "";
            while ((line = reader.readLine()) != null) {
                outPut += line + "\n";
            }
            return outPut;
        } catch (Exception e) {
        }
        return null;
    }

    public static String getPrettyDate(String timestamp, boolean withYear) throws ParseException {
        String newFormat = withYear ? "MMM dd, yyyy" : "MMM dd";
        return formatDateTime(timestamp, "yyyy-mm-dd hh:mm:ss", newFormat);
    }

    public static String formatDateTime(String dateTime, String oldFormat, String newFormat) throws ParseException {
        return new SimpleDateFormat(newFormat).format(new SimpleDateFormat(oldFormat).parse(dateTime));
    }

    public static boolean assetExists(Context context, String path) {
        boolean flag = false;
        try {
            InputStream stream = context.getAssets().open(path);
            stream.close();
            flag = true;
        } catch (Exception e) {
            Log.w("IOUtilities", "assetExists failed: " + e.toString());
        }
        return flag;
    }

    public static int getCurrentVersionCode() {
        return BuildConfig.VERSION_CODE;
    }


    public static String generateRandomString(int length) {
        String included = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(included.charAt(rnd.nextInt(included.length())));
        return sb.toString();
    }


    public static boolean isURL(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) MyApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public static boolean isOnlineAdv(Context context) {
        if (isOnline())
            return true;
        DialogHelper.showConnectionFailed(context);
        return false;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static JSONObject Deserialize(String filePath) {
        FileInputStream fileInputStream = null;
        ObjectInputStream inputStream = null;
        JSONObject jsonObject = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            inputStream = new ObjectInputStream(fileInputStream);
            inputStream.readUTF();
            try {
                jsonObject = new JSONObject(inputStream.readUTF());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            inputStream.readUTF();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } finally {
            try {
                inputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static boolean isValidJSONObject(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static boolean isValidJSON(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public interface UnZipCallback {
        void onUnzipFinished();
    }


    public static void unzipAsync(final File zipFile, final File targetDirectory, final boolean mustDeleteZipFile, final UnZipCallback callback) {
        Thread thread = new Thread(new Runnable() {
            ZipInputStream zipInputStream;

            @Override
            public void run() {
                try {
                    zipInputStream = new ZipInputStream(
                            new BufferedInputStream(new FileInputStream(zipFile)));
                    ZipEntry zipEntry;
                    int count;
                    byte[] buffer = new byte[8192];
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        File file = new File(targetDirectory, zipEntry.getName());
                        File dir = zipEntry.isDirectory() ? file : file.getParentFile();
                        if (!dir.isDirectory() && !dir.mkdirs())
                            throw new FileNotFoundException("Failed to ensure directory: " +
                                    dir.getAbsolutePath());
                        if (zipEntry.isDirectory())
                            continue;
                        FileOutputStream fout = new FileOutputStream(file);
                        try {
                            while ((count = zipInputStream.read(buffer)) != -1)
                                fout.write(buffer, 0, count);
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        zipInputStream.close();
                        if (mustDeleteZipFile)
                            zipFile.delete();
                        callback.onUnzipFinished();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }
                } finally {
                    fout.flush();
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }


    public static void toast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApp.getInstance(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void log(final String message) {
        if (BuildConfig.DEBUG) {
            Log.d("myapp", message);
        }
    }

    public static String getFileExt(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static ArrayList<String> getFileNames(String dir) {//, String fileType) {
        ArrayList<String> fileNames = new ArrayList<>();
        File Dir = new File(dir);
        for (File file : Dir.listFiles()) {
            //  String fileExt;
            if (file.isFile()) {
                //   fileExt = getFileExt(file.getName());
                //   if (fileExt.equalsIgnoreCase(fileType)) {//|| fileExt.equalsIgnoreCase("mp4") || fileExt.equalsIgnoreCase("avi")){
                fileNames.add(file.getName());
                //   }
            }
        }
        return fileNames;
    }

    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static String getDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        return now;
    }

    public static boolean isPackageInstalled(String PackageName) {
        PackageManager manager = MyApp.getInstance().getPackageManager();
        boolean isAppInstalled = false;
        try {
            manager.getPackageInfo(PackageName, PackageManager.GET_ACTIVITIES);
            isAppInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.i("LOG", "package is not installed");
        }
        return isAppInstalled;
    }

    public static boolean isBazaarInstalled() {
        return isPackageInstalled("com.farsitel.bazaar");
    }

    public static String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

}
/*
   class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                user = new TopUser();
                user.username = "added username : " + i;
                userList.add(user);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            loading = false;
            //dialog.hide();
            progressBar.setVisibility(View.GONE);
        }
    }
 */