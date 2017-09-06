package com.example.chenqi.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int REQUEST_EXTERNAL_PERMISSION_RESULT=1;
    private ImageView mPhotoCaptureImageView;
    private String mImageFileLocation="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhotoCaptureImageView =(ImageView)findViewById(R.id.capturePhotoImageView);
    }

    //click
    public void takePhoto(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
            callCameraApp();
        }else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this,
                        "External storage permission required to save images",//when indicate
                        Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            REQUEST_EXTERNAL_PERMISSION_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_EXTERNAL_PERMISSION_RESULT){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                callCameraApp();
            }else{
                Toast.makeText(this,
                        ("External permission has not been granted,canot save images"),
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    public void callCameraApp(){
        Intent CallCameraApplicationIntent = new Intent();
        CallCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile =null;
        try{
            photoFile= createImageFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        CallCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(CallCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);

    }

    protected void onActivityResult(int requestCode, int resultcode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultcode == RESULT_OK) {
            //Toast.makeText(this, "Picture taken successfully", Toast.LENGTH_SHORT).show();
            //Bundle extras = data.getExtras();
            // Bitmap photoCaptureBitmap = (Bitmap) extras.get("data");
            // mPhotoCaptureImageView.setImageBitmap(photoCaptureBitmap);
            // use BitmaoFactory to creat a fullsize image
            //Bitmap photoCapturedBitmap= BitmapFactory.decodeFile(mImageFileLocation);
            //mPhotoCaptureImageView.setImageBitmap(photoCapturedBitmap);
            setReducedImageSize();
        }
    }

    //creat a file
    File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE"+timeStamp+"_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,"jpg",storageDirectory);
        mImageFileLocation = image.getAbsolutePath();
        return image;

    }

    void setReducedImageSize(){
        int targetImageViewWidth = mPhotoCaptureImageView.getWidth();
        int targeImageViewHeight = mPhotoCaptureImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation,bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth,cameraImageHeight/targeImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;//?

        Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation,bmOptions);
        mPhotoCaptureImageView.setImageBitmap(photoReducedSizeBitmap);


    }
}
