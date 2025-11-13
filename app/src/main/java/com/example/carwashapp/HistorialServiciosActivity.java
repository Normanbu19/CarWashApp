package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HistorialServiciosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_servicios);

        // 🔹 Referencia del botón
        Button btnHistorialVehiculos = findViewById(R.id.btnHistorialVehiculos);

        // 🔹 Evento para abrir la pantalla de historial de vehículos
        btnHistorialVehiculos.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialServiciosActivity.this, VehiculoActivity.class);
            startActivity(intent);
        });
    }
}
