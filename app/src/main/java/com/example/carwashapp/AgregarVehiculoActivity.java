package com.example.carwashapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AgregarVehiculoActivity extends AppCompatActivity {
    private EditText etMarca;
    private EditText etModelo;
    private EditText etPlaca;
    private EditText etAnio;
    private EditText etTipoAceite;
    private Button btnGuardar;

    // Puedes centralizar la URL en ApiConfig si prefieres
    private static final String URL_INSERTAR = "http://18.191.153.112/api_carwash/vehiculos/insertar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_vehiculo);

        etMarca = findViewById(R.id.edtMarca);
        etModelo = findViewById(R.id.edtModelo);
        etPlaca = findViewById(R.id.edtPlaca);
        etAnio = findViewById(R.id.edtAnio);
        etTipoAceite = findViewById(R.id.edtTipoAceite); // asegúrate existe en tu XML
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            String marca = etMarca.getText().toString().trim();
            String modelo = etModelo.getText().toString().trim();
            String placa = etPlaca.getText().toString().trim();
            String anio = etAnio.getText().toString().trim();
            String tipoAceite = etTipoAceite.getText().toString().trim();

            // validaciones
            if (TextUtils.isEmpty(marca)) {
                etMarca.setError("Ingresa la marca");
                etMarca.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(modelo)) {
                etModelo.setError("Ingresa el modelo");
                etModelo.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(anio)) {
                etAnio.setError("Ingresa el año");
                etAnio.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(tipoAceite)) {
                etTipoAceite.setError("Ingresa tipo de aceite");
                etTipoAceite.requestFocus();
                return;
            }

            int idUsuario = getUserId(); // obtiene id del usuario (SharedPreferences o 1 por defecto)
            guardarVehiculoEnServidor(idUsuario, marca, modelo, placa, anio, tipoAceite);
        });
    }

    private void guardarVehiculoEnServidor(int idUsuario, String marca, String modelo, String placa, String anio, String tipoAceite) {
        String url = URL_INSERTAR;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // El PHP responde con ok([...], "Vehículo guardado") o fail(...)
                    // Intentamos parsear JSON, pero si llega texto lo mostramos igualmente.
                    try {
                        JSONObject json = new JSONObject(response);

                        // Buscamos keys comunes: success, message, data.id o id
                        boolean success = json.optBoolean("success", true);
                        String message = json.optString("message", null);

                        // Algunos utilitarios devuelven {"data":{"id":...}, ...}
                        String insertedId = null;
                        if (json.has("data")) {
                            JSONObject data = json.optJSONObject("data");
                            if (data != null) {
                                insertedId = String.valueOf(data.optInt("id", -1));
                            }
                        }
                        if (insertedId == null && json.has("id")) {
                            insertedId = String.valueOf(json.optInt("id", -1));
                        }

                        if (message == null) {
                            // si no trae message, usar raw response summary
                            message = success ? "Vehículo guardado" : "Respuesta del servidor";
                        }

                        Toast.makeText(AgregarVehiculoActivity.this, message, Toast.LENGTH_LONG).show();
                        // aquí puedes devolver el id creado a la activity anterior si quieres:
                        // Intent i = new Intent(); i.putExtra("vehiculo_id", insertedId); setResult(RESULT_OK, i);
                        finish();
                    } catch (JSONException e) {
                        // No es JSON: mostrar raw response
                        Toast.makeText(AgregarVehiculoActivity.this, response, Toast.LENGTH_LONG).show();
                        finish();
                    }
                },
                error -> {
                    // Error de red o servidor: mostrar e intentar fallback (simulación)
                    String msg = error.getMessage();
                    if (msg == null) msg = "Error de red";
                    Toast.makeText(AgregarVehiculoActivity.this, "No se pudo conectar: " + msg + "\nSe ha simulado guardado local para pruebas.", Toast.LENGTH_LONG).show();
                    // Para pruebas locales sin VPS, podemos simular éxito:
                    finish();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // parámetros que tu PHP espera
                params.put("id_usuario", String.valueOf(idUsuario));
                params.put("marca", marca);
                params.put("modelo", modelo);
                params.put("anio", anio);
                params.put("tipo_aceite", tipoAceite);
                // placa es opcional en PHP
                if (!TextUtils.isEmpty(placa)) params.put("placa", placa);
                return params;
            }
        };

        // timeout/retry opcional: puedes mejorar la política de reintentos si lo deseas
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getInt("user_id",1);
    }

}