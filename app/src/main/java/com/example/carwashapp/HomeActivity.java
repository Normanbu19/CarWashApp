package com.example.carwashapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private Button btnFull, btnExpress, btnAmorol, btnFechaHora;
    private boolean isFullSelected = false;
    private boolean isExpressSelected = false;
    private boolean isAmorolSelected = false; // Nuevo estado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Botón volver al login (casita)
        ImageButton btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Botones de servicios
        btnFull = findViewById(R.id.btnFull);
        btnExpress = findViewById(R.id.btnExpress);
        btnAmorol = findViewById(R.id.btnAmorol);
        btnFechaHora = findViewById(R.id.btnFechaHora);

        // Selección de servicios
        btnFull.setOnClickListener(v -> toggleServicio("full"));
        btnExpress.setOnClickListener(v -> toggleServicio("express"));

        // Selección de Pasta Cerámica Extra (toggle independiente)
        btnAmorol.setOnClickListener(v -> toggleAmorol());

        // Selección de día y hora
        btnFechaHora.setOnClickListener(v -> mostrarSelectorFechaHora());
    }

    private void toggleServicio(String tipo) {
        if (tipo.equals("full")) {
            if (isFullSelected) {
                // Deseleccionar si ya estaba seleccionado
                btnFull.setBackgroundColor(getResources().getColor(R.color.azul_claro));
                isFullSelected = false;
            } else {
                // Seleccionar este y desactivar el otro
                btnFull.setBackgroundColor(getResources().getColor(R.color.azul_oscuro));
                btnExpress.setBackgroundColor(getResources().getColor(R.color.azul_claro));
                isFullSelected = true;
                isExpressSelected = false;
            }
        } else if (tipo.equals("express")) {
            if (isExpressSelected) {
                btnExpress.setBackgroundColor(getResources().getColor(R.color.azul_claro));
                isExpressSelected = false;
            } else {
                btnExpress.setBackgroundColor(getResources().getColor(R.color.azul_oscuro));
                btnFull.setBackgroundColor(getResources().getColor(R.color.azul_claro));
                isExpressSelected = true;
                isFullSelected = false;
            }
        }
    }

    // ====== NUEVO: toggle del botón de pasta cerámica ======
    private void toggleAmorol() {
        isAmorolSelected = !isAmorolSelected; // alternar estado

        btnAmorol.setSelected(isAmorolSelected); // esto activa el selector XML

        if (isAmorolSelected) {
            // Si está seleccionado, puedes cambiar color de texto o mostrar aviso
            btnAmorol.setTextColor(getResources().getColor(android.R.color.white));
            Toast.makeText(this, "Pasta cerámica extra agregada", Toast.LENGTH_SHORT).show();
        } else {
            btnAmorol.setTextColor(getResources().getColor(android.R.color.white));
            Toast.makeText(this, "Pasta cerámica extra quitada", Toast.LENGTH_SHORT).show();
        }
    }

    // ====== Selector de fecha y hora ======
    private void mostrarSelectorFechaHora() {
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mostrarSelectorHora(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setTitle("Selecciona la fecha");
        datePicker.show();
    }

    private void mostrarSelectorHora(Calendar calendar) {
        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    SimpleDateFormat formatoFecha = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
                    SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm a", new Locale("es", "ES"));

                    String fechaFormateada = formatoFecha.format(calendar.getTime());
                    String horaFormateada = formatoHora.format(calendar.getTime());

                    Toast.makeText(
                            this,
                            "Has seleccionado:\n" + fechaFormateada + "\nHora: " + horaFormateada,
                            Toast.LENGTH_LONG
                    ).show();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        timePicker.setTitle("Selecciona la hora");
        timePicker.show();
    }
}