package com.example.carwashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRecuperarContrasena, btnRegister;

    String URL_LOGIN = "http://18.191.153.112/api_carwash/usuarios/login.php";
    String URL_ADMIN = "http://18.191.153.112/api_carwash/administradores/validar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRecuperarContrasena = findViewById(R.id.btnRecuperarContrasena);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> login());

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        btnRecuperarContrasena.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RecuperarContrasenaActivity.class))
        );
    }

    // =====================================================
    // LOGIN USUARIO NORMAL
    // =====================================================
    private void login() {

        String correo = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();

        if (!validar(correo, contrasena)) return;

        StringRequest reqUser = new StringRequest(Request.Method.POST, URL_LOGIN,
                res -> {
                    try {

                        JSONObject json = new JSONObject(res);

                        if (json.getBoolean("ok")) {

                            int idUsuario = json.getJSONObject("data").getInt("id");

                            // GUARDAR EN SHAREDPREFERENCES
                            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                            prefs.edit()
                                    .putInt("id_usuario", idUsuario)
                                    .apply();

                            // IR A HOME
                            Intent i = new Intent(this, HomeActivity.class);
                            startActivity(i);
                            finish();
                            return;
                        }

                        // Intentar admin
                        validarAdmin(correo, contrasena);

                    } catch (Exception e) {
                        validarAdmin(correo, contrasena);
                    }
                },
                err -> validarAdmin(correo, contrasena)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("correo", correo);
                p.put("contrasena", contrasena);
                return p;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(reqUser);
    }

    // =====================================================
    // LOGIN ADMIN
    // =====================================================
    private void validarAdmin(String correo, String contrasena) {

        StringRequest reqAdmin = new StringRequest(Request.Method.POST, URL_ADMIN,
                res -> {
                    try {
                        JSONObject json = new JSONObject(res);

                        if (json.getBoolean("ok")) {

                            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                            prefs.edit()
                                    .putInt("id_admin", json.getJSONObject("data").getInt("id"))
                                    .putString("rol_admin", json.getJSONObject("data").getString("rol"))
                                    .apply();

                            Intent i = new Intent(this, AdminDashboardActivity.class);
                            startActivity(i);
                            finish();
                            return;
                        }

                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al validar administrador", Toast.LENGTH_LONG).show();
                    }
                },
                err -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("correo", correo);
                p.put("contrasena", contrasena);
                return p;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(reqAdmin);
    }

    // =====================================================
    // VALIDACIÓN
    // =====================================================
    private boolean validar(String correo, String contrasena) {

        if (correo.isEmpty()) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus();
            return false;
        }

        if (contrasena.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
