package com.example.carwashapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private static final int REQ_CAMARA = 100;
    private static final int REQ_PERMISO_CAMARA = 200;

    private EditText txtNombre, txtPais, txtCorreo;
    private ImageView imgPerfil;
    private ImageButton btnCambiarFoto;
    private Button btnGuardar, btnAgregarVehiculo;

    private Bitmap fotoBitmap = null;
    private int ID_USUARIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        txtNombre = findViewById(R.id.edtNombreCompleto);
        txtPais = findViewById(R.id.edtPais);
        txtCorreo = findViewById(R.id.edtCorreo);
        imgPerfil = findViewById(R.id.imgPerfil);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnAgregarVehiculo = findViewById(R.id.btnAgregarVehiculo);

        txtCorreo.setEnabled(false);

        ID_USUARIO = getIntent().getIntExtra("id_usuario", -1);

        if (ID_USUARIO == -1) {
            Toast.makeText(this, "Error al recibir usuario", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarPerfil();

        btnCambiarFoto.setOnClickListener(v -> pedirPermisoCamara());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnAgregarVehiculo.setOnClickListener(v ->
                startActivity(new Intent(this, AgregarVehiculoActivity.class))
        );
    }

    private void pedirPermisoCamara() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_PERMISO_CAMARA);
        } else {
            abrirCamara();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirCamara() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, REQ_CAMARA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CAMARA && resultCode == Activity.RESULT_OK) {
            fotoBitmap = (Bitmap) data.getExtras().get("data");
            imgPerfil.setImageBitmap(fotoBitmap);
            imgPerfil.setVisibility(ImageView.VISIBLE);
        }
    }


    private void cargarPerfil() {

        String url = "http://18.191.153.112/api_carwash/usuarios/listar.php?id=" + ID_USUARIO;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                resp -> {
                    try {
                        JSONObject json = new JSONObject(resp);
                        JSONObject data = json.getJSONObject("data");

                        String nombre = data.optString("nombre", "");
                        String apellido = data.optString("apellido", "");

                        txtNombre.setText(nombre + " " + apellido);

                        txtPais.setText(data.optString("pais", ""));
                        txtCorreo.setText(data.optString("correo", ""));

                        String foto = data.optString("foto_perfil", "");

                        if (!foto.equals("") && !foto.equals("null")) {
                            new Thread(() -> {
                                try {
                                    Bitmap bmp = android.graphics.BitmapFactory.decodeStream(
                                            new java.net.URL(
                                                    "http://18.191.153.112/uploads/" + foto
                                            ).openStream()
                                    );
                                    runOnUiThread(() -> {
                                        imgPerfil.setImageBitmap(bmp);
                                        imgPerfil.setVisibility(ImageView.VISIBLE);
                                    });
                                } catch (Exception ignored) {}
                            }).start();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error cargando perfil", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        MySingleton.getInstance(this).addToRequestQueue(req);
    }


    private void guardarCambios() {

        String url = "http://18.191.153.112/api_carwash/usuarios/editar.php";

        VolleyMultipartRequest req = new VolleyMultipartRequest(
                Request.Method.POST,
                url,
                resp -> {
                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> p = new HashMap<>();

                String full = txtNombre.getText().toString().trim();
                String[] partes = full.split(" ", 2);

                String nombreSolo = partes[0];
                String apellidoSolo = (partes.length > 1) ? partes[1] : "";

                p.put("id", String.valueOf(ID_USUARIO));
                p.put("nombre", nombreSolo);
                p.put("apellido", apellidoSolo);
                p.put("pais", txtPais.getText().toString());

                return p;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> img = new HashMap<>();
                if (fotoBitmap != null) {
                    img.put("foto", new DataPart(
                            "perfil.jpg",
                            ImageHelper.getBytes(fotoBitmap),
                            "image/jpeg"
                    ));
                }
                return img;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(req);
    }
}
