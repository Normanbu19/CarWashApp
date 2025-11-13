package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    // Botones del menú principal
    private Button btnSolicitarCotizacion, btnHistorialServicios, btnCalificacionServicio;

    // Botones de navegación inferiores
    private ImageButton btnHome, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔹 Inicializar los botones del menú
        btnSolicitarCotizacion = findViewById(R.id.btnSolicitarCotizacion);
        btnHistorialServicios = findViewById(R.id.btnHistorialServicios);
        btnCalificacionServicio = findViewById(R.id.btnCalificacionServicio);

        // 🔹 Inicializar los botones inferiores
        btnHome = findViewById(R.id.btnHome);
        btnPerfil = findViewById(R.id.btnPerfil);

        // ========= Eventos de botones principales =========

        // 🧾 Solicitar Cotización
        btnSolicitarCotizacion.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SolicitarCotizacionActivity.class);
            startActivity(intent);
        });

        // 🕘 Historial de Servicios
        btnHistorialServicios.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistorialServiciosActivity.class);
            startActivity(intent);
        });

        // ⭐ Calificación del Servicio
        btnCalificacionServicio.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalificacionServicioActivity.class);
            startActivity(intent);
        });

        // ========= Barra inferior =========

        // 🏠 Botón Home (refresca esta misma pantalla)
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // 👤 Ir al perfil del usuario
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PerfilUsuarioActivity.class);
            startActivity(intent);
        });
    }
}
