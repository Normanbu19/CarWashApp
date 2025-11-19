package com.example.carwashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etCorreo, etPassword, etConfirmPassword;
    Button btnRegistrar;
    String URL_REGISTRAR = "http://18.191.153.112/api_carwash/usuarios/registrar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }


    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();



        //VALIDACIONES

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            mostrar("Todos los campos son obligatorios");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            mostrar("Correo no válido");
            return;
        }

        if (pass.length() < 6) {
            mostrar("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!pass.equals(confirm)) {
            mostrar("Las contraseñas no coinciden");
            return;
        }



        // PETICIÓN VOLLEY
        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTRAR,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {
                            mostrar("Registro exitoso");

                            // Enviar al login
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {
                            mostrar(json.getString("msg"));
                        }

                    } catch (Exception e) {
                        mostrar("Error al procesar la respuesta del servidor");
                    }
                },

                error -> {
                    mostrar("Ocurrió un error al conectar con el servidor");
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("correo", correo);
                params.put("contrasena", pass);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void mostrar(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
