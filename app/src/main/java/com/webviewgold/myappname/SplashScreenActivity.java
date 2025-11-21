package com.webviewgold.myappname;

import static com.webviewgold.myappname.Config.REMAIN_SPLASH_OPTION;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Config.blackStatusBarText) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (BuildConfig.IS_DEBUG_MODE) Log.d("TAG", "onCreate: RUN");
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorWhite));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));

        ConstraintLayout btnUnlock = findViewById(R.id.btnUnlock);
        btnUnlock.setOnClickListener(v -> showBiometricPrompt());
        if (Config.enableBioMetricAuth) {
            btnUnlock.setVisibility(View.VISIBLE);
            showBiometricPrompt();
        } else {
            btnUnlock.setVisibility(View.GONE);
            Handler handler = new Handler();
            if (REMAIN_SPLASH_OPTION){
                // There are another functionality to let the user wait on splash. So we have to navigate the user to the next screen.
                openNextActivity();
            } else {
                handler.postDelayed(this::openNextActivity, 1500);
            }
        }
    }

    private void showBiometricPrompt() {
        super.showBiometricPrompt(this::openNextActivity);
    }


    private void openNextActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        overridePendingTransition(0, 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (getIntent().getExtras() != null) {
            String url = getIntent().getExtras().getString("url");
            if (url != null) {
                if (!url.isEmpty()) {
                    intent.putExtra("ONESIGNAL_URL", url);
                }
            }
        }
        intent.putExtra(MainActivity.KEY_SKIP_BIOMETRIC, true);
        startActivity(intent);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            //This leads to potential problems with Android 14 Auto-Rotation Landscape Mode, so only run until Android 13
            this.overridePendingTransition(0, 0);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 30);
        }

    }
}
