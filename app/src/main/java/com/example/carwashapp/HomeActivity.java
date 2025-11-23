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
        // OBTENER ID DESDE SHAREDPREFERENCES
        // ==========================
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        ID_USUARIO = prefs.getInt("id_usuario", -1);

        if (ID_USUARIO == -1) {
            // Sesión inválida → volver al login
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
        // ACCIONES
        // ==========================

        // Solicitar Cotización
        btnSolicitarCotizacion.setOnClickListener(v ->
                startActivity(new Intent(this, SolicitarCotizacionActivity.class))
        );

        // Historial
        btnHistorialServicios.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialServiciosActivity.class))
        );

        // Calificación
        btnCalificacionServicio.setOnClickListener(v ->
                startActivity(new Intent(this, CalificacionServicioActivity.class))
        );

        // Cerrar sesión
        btnHome.setOnClickListener(v -> {
            prefs.edit().clear().apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // PERFIL → MANDAMOS EL ID DEL USUARIO
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilUsuarioActivity.class);
            intent.putExtra("id_usuario", ID_USUARIO);
            startActivity(intent);
        });
    }
}
