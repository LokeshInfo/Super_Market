package com.ics.super_market;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    TextView app_name;
    ImageView imageView2;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    private AlertDialog dialog;

    private Session_management sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

       // app_name = (TextView)findViewById(R.id.app_name);
        imageView2 = (ImageView) findViewById(R.id.imageView2);



        sessionManagement = new Session_management(com.ics.super_market.SplashActivity.this);
        Session_management.setCHK_Version(SplashActivity.this,true);

        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(3 * 1000);

                    // After 5 seconds redirect to another intent
                    checkAppPermissions();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // start thread
        background.start();
    }

    public void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)&& ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)

            ) {
                go_next();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_SMS
                        },
                        MY_PERMISSIONS_REQUEST_WRITE_FIELS);
            }
        } else {
            go_next();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_FIELS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                go_next();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(com.ics.super_market.SplashActivity.this);
                builder.setMessage("App required some permission please enable it")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                openPermissionScreen();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                dialog = builder.show();
            }
            return;
        }
    }

    public void go_next() {

           if(sessionManagement.isLoggedIn()) {

            Intent startmain = new Intent(com.ics.super_market.SplashActivity.this, MainActivity.class);
            startActivity(startmain);
            finish();
        }
        else{
            Intent startmain = new Intent(com.ics.super_market.SplashActivity.this, MainActivity.class);
            startActivity(startmain);
            finish();
        }


       /* if(sessionManagement.isLoggedIn()) {

            sessionManagement.checkLogin();


        }else{
            Intent startmain = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(startmain);
        }

        finish();*/
    }

    public void openPermissionScreen() {

        // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", com.ics.super_market.SplashActivity.this.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
