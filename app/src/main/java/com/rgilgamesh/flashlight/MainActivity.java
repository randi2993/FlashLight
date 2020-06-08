package com.rgilgamesh.flashlight;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    boolean flashStatus = false;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!validatePermission()){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        final Button btnPower = findViewById(R.id.btnOnOff);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.mouseclick);

        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.start();

                    if(flashStatus){
                        stopFlash();
                        btnPower.setBackground(getResources().getDrawable(R.drawable.poweroff));
                        flashStatus = false;
                    }else{
                        startFlash();
                        btnPower.setBackground(getResources().getDrawable(R.drawable.poweron));
                        flashStatus = true;
                    }
                }catch (Exception ex){
                    if(ex.getMessage().contains(getString(R.string.containCameraWord))){
                        camError();
                    }else{
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void startFlash(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Camera cam =  Camera.open();
            Camera.Parameters params = cam.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(params);
            cam.startPreview();
        }else {
            try {
                CameraManager camera = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                if(camera != null){
                    String cameraId = camera.getCameraIdList()[0];
                    camera.setTorchMode(cameraId, true);
                }
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void stopFlash(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            android.hardware.Camera cam =  android.hardware.Camera.open();
            android.hardware.Camera.Parameters params = cam.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(params);
            cam.stopPreview();
            cam.release();
            cam = null;
        }else {
            try {
                CameraManager camera = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                if(camera != null){
                    String cameraId = camera.getCameraIdList()[0];
                    camera.setTorchMode(cameraId, false);
                }
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validatePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void camError(){
        Intent intent = new Intent(this, CamErrorActivity.class);
        startActivity(intent);
    }
}
