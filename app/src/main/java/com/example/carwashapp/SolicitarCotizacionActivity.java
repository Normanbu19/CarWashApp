package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class SolicitarCotizacionActivity extends AppCompatActivity {

    private RadioGroup grupoUbicacion;
    private RadioButton rbCentro, rbDomicilio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cotizacion);

        // 🔹 Referencias a los botones de ubicación
        grupoUbicacion = findViewById(R.id.grupoUbicacion);
        rbCentro = findViewById(R.id.rbCentro);
        rbDomicilio = findViewById(R.id.rbDomicilio);

        // 🔹 Escuchar el cambio de selección en el RadioGroup
        grupoUbicacion.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDomicilio) {
                // 🗺️ Si selecciona "A domicilio", abrir la pantalla del mapa
                Intent intent = new Intent(SolicitarCotizacionActivity.this, MapaUbicacionActivity.class);
                startActivity(intent);
            }
        });
    }
}
