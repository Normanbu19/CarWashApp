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

    EditText etNombre, etApellido, etCorreo, etPassword, etConfirmPassword, etPais;
    Button btnRegistrar;

    String URL_REGISTRAR = "http://18.191.153.112/api_carwash/usuarios/registrar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPais = findViewById(R.id.etPais);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();
        String pais = etPais.getText().toString().trim();

        // ============================================================
        // VALIDACIONES - CAMPOS VACÍOS
        // ============================================================
        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() ||
                pass.isEmpty() || confirm.isEmpty() || pais.isEmpty()) {
            mostrar("Todos los campos son obligatorios");
            return;
        }

        // ============================================================
        // VALIDACIÓN: NOMBRE SOLO LETRAS
        // ============================================================
        if (!nombre.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            etNombre.setError("El nombre no debe contener números");
            etNombre.requestFocus();
            return;
        }

        // ============================================================
        // VALIDACIÓN: APELLIDO SOLO LETRAS
        // ============================================================
        if (!apellido.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            etApellido.setError("El apellido no debe contener números");
            etApellido.requestFocus();
            return;
        }

        // ============================================================
        // VALIDACIÓN: PAÍS SOLO LETRAS
        // ============================================================
        if (!pais.matches("[a-zA-ZÁÉÍÓÚáéíóúñÑ ]+")) {
            etPais.setError("El país no debe contener números");
            etPais.requestFocus();
            return;
        }

        // ============================================================
        // VALIDACIÓN: CORREO FORMATO CORRECTO
        // ============================================================
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            etCorreo.requestFocus();
            return;
        }

        // ============================================================
        // VALIDACIÓN: CONTRASEÑA MÍNIMA
        // ============================================================
        if (pass.length() < 6) {
            mostrar("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // ============================================================
        // VALIDACIÓN: CONTRASEÑAS IGUALES
        // ============================================================
        if (!pass.equals(confirm)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        // ============================================================
        // PETICIÓN VOLLEY
        // ============================================================
        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_REGISTRAR,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (json.getBoolean("ok")) {

                            mostrar("Registro exitoso");
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {

                            String msg = json.getString("msg");

                            // Validación directa desde la API
                            if (msg.toLowerCase().contains("correo")) {
                                etCorreo.setError("El correo ya está registrado");
                                etCorreo.requestFocus();
                            }

                            mostrar(msg);
                        }

                    } catch (Exception e) {
                        mostrar("Error al procesar la respuesta del servidor");
                    }
                },
                error -> mostrar("Ocurrió un error al conectar con el servidor")
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("correo", correo);
                params.put("contrasena", pass);
                params.put("pais", pais);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void mostrar(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
