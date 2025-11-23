package com.example.carwashapp;

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

    private EditText etMarca, etModelo, etPlaca, etAnio, etTipoAceite;
    private Button btnGuardar;

    private static final String URL_INSERTAR =
            "http://18.191.153.112/api_carwash/vehiculos/insertar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_vehiculo);

        etMarca = findViewById(R.id.edtMarca);
        etModelo = findViewById(R.id.edtModelo);
        etPlaca = findViewById(R.id.edtPlaca);
        etAnio = findViewById(R.id.edtAnio);
        etTipoAceite = findViewById(R.id.edtTipoAceite);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {

        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();
        String placa = etPlaca.getText().toString().trim();
        String anio = etAnio.getText().toString().trim();
        String tipoAceite = etTipoAceite.getText().toString().trim();

        if (TextUtils.isEmpty(marca)) { etMarca.setError("Ingresa la marca"); return; }
        if (TextUtils.isEmpty(modelo)) { etModelo.setError("Ingresa el modelo"); return; }
        if (TextUtils.isEmpty(anio)) { etAnio.setError("Ingresa el año"); return; }
        if (TextUtils.isEmpty(tipoAceite)) { etTipoAceite.setError("Ingresa el tipo de aceite"); return; }

        int idUsuario = getUserId();
        if (idUsuario == -1) {
            Toast.makeText(this, "Error: sesión no válida", Toast.LENGTH_LONG).show();
            return;
        }

        enviarAlServidor(idUsuario, marca, modelo, placa, anio, tipoAceite);
    }

    private void enviarAlServidor(int idUsuario, String marca, String modelo,
                                  String placa, String anio, String tipoAceite) {

        StringRequest req = new StringRequest(Request.Method.POST, URL_INSERTAR,
                resp -> {
                    try {
                        JSONObject json = new JSONObject(resp);

                        if (json.getBoolean("ok")) {
                            Toast.makeText(this, "Vehículo guardado", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("msg"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Respuesta inesperada: " + resp, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id_usuario", String.valueOf(idUsuario));
                p.put("marca", marca);
                p.put("modelo", modelo);
                p.put("anio", anio);
                p.put("tipo_aceite", tipoAceite);
                if (!TextUtils.isEmpty(placa)) p.put("placa", placa);
                return p;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(req);
    }

    // ✅ PARA TOMAR SIEMPRE EL MISMO ID DEL LOGIN
    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        return prefs.getInt("id_usuario", -1);
    }
}
