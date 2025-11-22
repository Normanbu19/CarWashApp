package com.example.carwashapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SolicitarCotizacionActivity extends AppCompatActivity {

    private Spinner spinnerVehiculo, spinnerServicio;

    private ArrayList<String> listaVehiculos = new ArrayList<>();
    private ArrayList<Integer> idsVehiculos = new ArrayList<>();

    private RadioGroup grupoUbicacion;
    private RadioButton rbCentro, rbDomicilio;

    private EditText txtFecha, txtHora;
    private TextView txtResumen;
    private Button btnEnviarCotizacion;

    private int ID_USUARIO = 1;

    public static String LAT = "0";
    public static String LNG = "0";
    public static String DIRECCION_TEXTUAL = "N/A";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cotizacion);

        spinnerVehiculo = findViewById(R.id.spinnerVehiculo);
        spinnerServicio = findViewById(R.id.spinnerServicio);
        txtFecha = findViewById(R.id.txtFecha);
        txtHora = findViewById(R.id.txtHora);
        grupoUbicacion = findViewById(R.id.grupoUbicacion);
        rbCentro = findViewById(R.id.rbCentro);
        rbDomicilio = findViewById(R.id.rbDomicilio);
        txtResumen = findViewById(R.id.txtResumen);
        btnEnviarCotizacion = findViewById(R.id.btnEnviarCotizacion);

        cargarVehiculos();
        cargarServicios();

        txtFecha.setOnClickListener(v -> abrirCalendario());
        txtHora.setOnClickListener(v -> abrirReloj());

        grupoUbicacion.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDomicilio) {
                Intent intent = new Intent(SolicitarCotizacionActivity.this, MapaUbicacionActivity.class);
                startActivity(intent);
            }
        });

        btnEnviarCotizacion.setOnClickListener(v -> validarAntesDeEnviar());
    }


    private void cargarVehiculos() {

        String url = "http://18.191.153.112/api_carwash/vehiculos/listar.php?id_usuario=" + ID_USUARIO;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        JSONObject json = new JSONObject(response);

                        if (!json.getBoolean("ok")) {
                            Toast.makeText(this, "Error API: " + json.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray array = json.getJSONArray("data");

                        listaVehiculos.clear();
                        idsVehiculos.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);

                            int id = o.getInt("id");
                            String marca = o.getString("marca");
                            String modelo = o.getString("modelo");
                            int anio = o.getInt("anio");

                            listaVehiculos.add(marca + " " + modelo + " (" + anio + ")");
                            idsVehiculos.add(id);
                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaVehiculos);

                        spinnerVehiculo.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando vehículos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error cargando vehículos", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void cargarServicios() {

        ArrayList<String> lista = new ArrayList<>();

        lista.add("Lavado general - L100 (L150 domicilio)");
        lista.add("Lavado completo - L150 (L200 domicilio)");
        lista.add("Cambio de aceite (solo en centro)");
        lista.add("Lavado de motor L400 (solo en centro)");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lista);

        spinnerServicio.setAdapter(adapter);
    }


    private void abrirCalendario() {

        Calendar c = Calendar.getInstance();
        int año = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // FORMATO CORRECTO PARA MYSQL → YYYY-MM-DD
                    String fechaFormateada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    txtFecha.setText(fechaFormateada);
                },
                año, mes, dia
        );

        dp.show();
    }


    private void abrirReloj() {

        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog tp = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) ->
                        txtHora.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hora, minuto, true
        );

        tp.show();
    }


    private void validarAntesDeEnviar() {

        if (txtFecha.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (txtHora.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!rbCentro.isChecked() && !rbDomicilio.isChecked()) {
            Toast.makeText(this, "Selecciona ubicación del servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        String servicio = spinnerServicio.getSelectedItem().toString();
        boolean esDomicilio = rbDomicilio.isChecked();

        if ((servicio.contains("aceite") || servicio.contains("motor")) && esDomicilio) {
            Toast.makeText(this, "Este servicio solo se realiza en el centro.", Toast.LENGTH_LONG).show();
            return;
        }

        actualizarResumen();
        enviarCotizacion();
    }


    private void actualizarResumen() {

        String resumen =
                "Vehículo: " + listaVehiculos.get(spinnerVehiculo.getSelectedItemPosition()) + "\n" +
                        "Servicio: " + spinnerServicio.getSelectedItem().toString() + "\n" +
                        "Fecha: " + txtFecha.getText().toString() + "\n" +
                        "Hora: " + txtHora.getText().toString() + "\n" +
                        "Ubicación: " + (rbCentro.isChecked() ? "Centro" : "Domicilio") + "\n" +
                        "Dirección: " + DIRECCION_TEXTUAL;

        txtResumen.setText(resumen);
    }


    private void enviarCotizacion() {

        String url = "http://18.191.153.112/api_carwash/cotizaciones/crear.php";

        int idVehiculo = idsVehiculos.get(spinnerVehiculo.getSelectedItemPosition());
        int idServicio = spinnerServicio.getSelectedItemPosition() + 1;

        String ubicacionFinal = rbCentro.isChecked()
                ? "Centro de servicio"
                : DIRECCION_TEXTUAL;

        JSONObject params = new JSONObject();
        try {
            params.put("id_usuario", ID_USUARIO);
            params.put("id_vehiculo", idVehiculo);
            params.put("id_servicio", idServicio);
            params.put("fecha", txtFecha.getText().toString());
            params.put("hora", txtHora.getText().toString());
            params.put("ubicacion", ubicacionFinal);
            params.put("ubicacion_tipo", rbCentro.isChecked() ? "centro" : "domicilio");
            params.put("lat", LAT.replace(",", "."));
            params.put("lng", LNG.replace(",", "."));
        } catch (Exception e) {
            Toast.makeText(this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    Toast.makeText(this, "Cotización enviada correctamente", Toast.LENGTH_LONG).show();
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Toast.makeText(this, "Error server: " + body, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                15000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 🟢 AHORA SÍ USAMOS LA MISMA COLA CORRECTA
        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}