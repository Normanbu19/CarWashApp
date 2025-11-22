package com.example.carwashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CambiarContrasenaActivity extends AppCompatActivity {

    EditText edtNuevaContrasena;
    Button btnCambiar;

    String URL_CAMBIAR = "http://18.191.153.112/api_carwash/usuarios/cambiar_contrasena.php";

    String correo, codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        edtNuevaContrasena = findViewById(R.id.edtNuevaContrasena);
        btnCambiar = findViewById(R.id.btnCambiarContrasena);

        //  vienen desde ConfirmarCodigoActivity
        correo = getIntent().getStringExtra("correo");
        codigo = getIntent().getStringExtra("codigo");

        btnCambiar.setOnClickListener(v -> cambiarContrasena());
    }

    private void cambiarContrasena() {
        String nueva = edtNuevaContrasena.getText().toString().trim();

        if (nueva.isEmpty()) {
            Toast.makeText(this, "Ingresa la nueva contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_CAMBIAR,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {
                            Toast.makeText(
                                    this,
                                    "Contraseña actualizada",
                                    Toast.LENGTH_SHORT
                            ).show();
                            // volver al login
                            finish();
                        } else {
                            Toast.makeText(
                                    this,
                                    json.getString("msg"),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(
                                this,
                                "Error procesando respuesta",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },
                error -> Toast.makeText(
                        this,
                        "Error de conexión",
                        Toast.LENGTH_LONG
                ).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("codigo", codigo);
                params.put("nueva_contrasena", nueva);   //  AQUÍ SÍ VA
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
