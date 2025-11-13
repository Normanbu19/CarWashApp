package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // 🔹 Enlazamos el botón “Agregar Vehículo”
        Button btnAgregarVehiculo = findViewById(R.id.btnAgregarVehiculo);

        btnAgregarVehiculo.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilUsuarioActivity.this, AgregarVehiculoActivity.class);
            startActivity(intent);
        });
    }
}
