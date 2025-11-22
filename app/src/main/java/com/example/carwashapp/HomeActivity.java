package com.example.carwashapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnSolicitarCotizacion, btnHistorialServicios, btnCalificacionServicio;
    private ImageButton btnHome, btnPerfil;

    private int ID_USUARIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ==========================
        // RECIBIR EL ID
        // ==========================
        ID_USUARIO = getIntent().getIntExtra("id_usuario", -1);

        if (ID_USUARIO == -1) {
            // Si algo sale mal, cerramos sesión sin mostrar Toast
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // ==========================
        // INICIALIZAR BOTONES
        // ==========================
        btnSolicitarCotizacion = findViewById(R.id.btnSolicitarCotizacion);
        btnHistorialServicios = findViewById(R.id.btnHistorialServicios);
        btnCalificacionServicio = findViewById(R.id.btnCalificacionServicio);

        btnHome = findViewById(R.id.btnHome);
        btnPerfil = findViewById(R.id.btnPerfil);

        // ==========================
        // FUNCIONES DE BOTONES
        // ==========================

        btnSolicitarCotizacion.setOnClickListener(v ->
                startActivity(new Intent(this, SolicitarCotizacionActivity.class))
        );

        btnHistorialServicios.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialServiciosActivity.class))
        );

        btnCalificacionServicio.setOnClickListener(v ->
                startActivity(new Intent(this, CalificacionServicioActivity.class))
        );

        // Cerrar sesión
        btnHome.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // IR A PERFIL (MANDAMOS EL ID)
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilUsuarioActivity.class);
            intent.putExtra("id_usuario", ID_USUARIO);
            startActivity(intent);
        });
    }
}
