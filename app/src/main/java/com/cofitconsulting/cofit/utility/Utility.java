package com.cofitconsulting.cofit.utility;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Utility {

    public Utility() {

    }

    public ArrayList findUnaskedPermissions(ArrayList<String> wanted, Context context){
        ArrayList<String> result = new ArrayList<>();
        for(String perm : wanted) //per ogni permesso cercato
        {
            //se il permesso NON è stato dato allora lo dobbiamo richiedere
            if(!(context.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED))
            {
                result.add(perm);
            }
        }
        return result;
    }

    public String getFileExtension(Uri uri, Context context){
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public void downloadFiles(Context context, String fileName, String fileExtension, String destinatonDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinatonDirectory, fileName + "." + fileExtension);

        downloadManager.enqueue(request);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }


    public void adapterSpinner(Context context, Spinner spinner)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Fattura cliente");
        arrayList.add("Contanti");
        arrayList.add("Assegno");
        arrayList.add("Acconto");
        arrayList.add("Altri crediti");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }



}
