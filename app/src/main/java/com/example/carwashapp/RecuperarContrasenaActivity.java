package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecuperarContrasenaActivity extends AppCompatActivity {

    EditText edtCorreo;
    Button btnEnviar;

    // 👉 URL de tu servidor AWS
    String URL_SOLICITAR = "http://18.191.153.112/api_carwash/usuarios/solicitar_recuperacion.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        edtCorreo = findViewById(R.id.edtCorreo);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> enviarCodigo());
    }

    private void enviarCodigo() {

        String correo = edtCorreo.getText().toString().trim();

        if (correo.isEmpty()) {
            Toast.makeText(this, "Debes ingresar tu correo", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_SOLICITAR,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {

                            // 🔹 Obtener y mostrar el código (solo modo pruebas)
                            String codigo = json.getJSONObject("data").getString("codigo");
                            Toast.makeText(this,
                                    "Código recibido: " + codigo,
                                    Toast.LENGTH_LONG).show();

                            // 🔹 Pasar el correo a la siguiente Activity
                            Intent i = new Intent(this, ConfirmarCodigoActivity.class);
                            i.putExtra("correo", correo);
                            startActivity(i);

                        } else {
                            Toast.makeText(this,
                                    json.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Error procesando respuesta",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this,
                            "Error de conexión. Intenta más tarde",
                            Toast.LENGTH_LONG).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
