package com.example.carwashapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SolicitarCotizacionActivity extends AppCompatActivity {

    private RadioGroup grupoUbicacion;
    private RadioButton rbCentro, rbDomicilio;
    private EditText txtFecha, txtHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cotizacion);

        // 🔹 Referencias a los botones de ubicación
        grupoUbicacion = findViewById(R.id.grupoUbicacion);
        rbCentro = findViewById(R.id.rbCentro);
        rbDomicilio = findViewById(R.id.rbDomicilio);

        // 🔹 Referencias a la fecha y hora
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);

        // 📅 ABRIR CALENDARIO AL TOCAR FECHA
        txtFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int año = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH);
            int dia = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) ->
                            txtFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                    año, mes, dia
            );

            datePicker.show();
        });

        // ⏰ ABRIR RELOJ AL TOCAR HORA
        txtHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hora = c.get(Calendar.HOUR_OF_DAY);
            int minuto = c.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) ->
                            txtHora.setText(String.format("%02d:%02d", hourOfDay, minute)),
                    hora, minuto, true
            );

            timePicker.show();
        });

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
