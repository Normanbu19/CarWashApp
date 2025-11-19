package com.example.carwashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    EditText etEmail, etPassword;
    Button btnLogin, btnRecuperarContrasena, btnRegister;


    String URL_LOGIN = "http://18.191.153.112/api_carwash/usuarios/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRecuperarContrasena = findViewById(R.id.btnRecuperarContrasena);
        btnRegister = findViewById(R.id.btnRegister);


        btnLogin.setOnClickListener(v -> loginUsuario());


        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );


        btnRecuperarContrasena.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RecuperarContrasenaActivity.class))
        );
    }



    private void loginUsuario() {

        String correo = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();

        // 🔹 Validación: campos vacíos
        if (correo.isEmpty()) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }

        // 🔹 Validación: formato de correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus();
            return;
        }

        // 🔹 Petición a tu API
        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {

                            JSONObject data = json.getJSONObject("data");
                            int idUsuario = data.getInt("id");

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("id_usuario", idUsuario);
                            startActivity(intent);
                            finish();

                        } else {
                            // 🔹 Mensaje del servidor
                            Toast.makeText(this, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                    }
                },

                error -> {
                    // 🔹 No mostrar errores feos → mensaje limpio
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("correo", correo);
                params.put("contrasena", contrasena);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
