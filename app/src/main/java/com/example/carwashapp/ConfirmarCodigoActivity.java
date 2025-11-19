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

public class ConfirmarCodigoActivity extends AppCompatActivity {

    EditText edtCorreo, edtCodigo;
    Button btnConfirmar;

    // 👉 SOLO verifica código, NO cambia contraseña
    String URL_CONFIRMAR = "http://18.191.153.112/api_carwash/usuarios/confirmar_recuperacion.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_codigo);

        edtCorreo = findViewById(R.id.edtCorreo);
        edtCodigo = findViewById(R.id.edtCodigo);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // Si viene el correo desde RecuperarContrasenaActivity, lo rellenamos
        String correoRecibido = getIntent().getStringExtra("correo");
        if (correoRecibido != null) {
            edtCorreo.setText(correoRecibido);
        }

        btnConfirmar.setOnClickListener(v -> verificarCodigo());
    }

    private void verificarCodigo() {
        String correo = edtCorreo.getText().toString().trim();
        String codigo = edtCodigo.getText().toString().trim();

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Debes ingresar el código", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_CONFIRMAR,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {
                            Toast.makeText(
                                    this,
                                    "Código verificado correctamente",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // ✅ AQUÍ SOLO NAVEGAS A CAMBIAR CONTRASEÑA
                            Intent i = new Intent(
                                    ConfirmarCodigoActivity.this,
                                    CambiarContrasenaActivity.class
                            );
                            i.putExtra("correo", correo);
                            i.putExtra("codigo", codigo);
                            startActivity(i);
                            finish();  // opcional, para no volver atrás a esta pantalla

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
                params.put("codigo", codigo);   // 👈 SOLO ESTO
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
