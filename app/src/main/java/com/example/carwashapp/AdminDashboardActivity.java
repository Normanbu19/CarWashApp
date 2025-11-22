package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnCotizaciones, btnVehiculos, btnUsuarios, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnCotizaciones = findViewById(R.id.btnCotizaciones);
        btnVehiculos = findViewById(R.id.btnVehiculos);
        btnUsuarios = findViewById(R.id.btnUsuarios);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion); // ← NUEVO

        btnCotizaciones.setOnClickListener(v ->
                startActivity(new Intent(this, AdminCotizacionesActivity.class))
        );

        btnVehiculos.setOnClickListener(v ->
                startActivity(new Intent(this, AdminVehiculosActivity.class))
        );

        btnUsuarios.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUsuariosActivity.class))
        );

        // ------------------------------------
        // 🔴 CERRAR SESIÓN
        // ------------------------------------
        btnCerrarSesion.setOnClickListener(v -> {
            Intent i = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish(); // cierra el panel admin
        });
    }
}
