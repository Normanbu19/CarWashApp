package com.example.carwashapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RegisterActivity extends AppCompatActivity {

    private ImageView imgLogoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Vincular el logo
        imgLogoRegister = findViewById(R.id.imgLogoRegister);

        // Cargar y ejecutar la animación (misma que en el login)
        Animation animLogo = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        imgLogoRegister.startAnimation(animLogo);
    }
}