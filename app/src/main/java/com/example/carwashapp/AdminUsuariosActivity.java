package com.example.carwashapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminUsuariosActivity extends AppCompatActivity {

    TextView txtTitulo;
    ImageView iconMain;
    ListView list;
    EditText edtBuscar;

    ArrayList<String> datos = new ArrayList<>();
    ArrayList<String> datosFiltrados = new ArrayList<>();
    ArrayAdapter<String> adaptador;

    ArrayList<Integer> ids = new ArrayList<>();

    String URL = "http://18.191.153.112/api_carwash/usuarios/listar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_template);

        txtTitulo = findViewById(R.id.txtTitulo);
        iconMain = findViewById(R.id.iconMain);
        list = findViewById(R.id.listGeneral);
        edtBuscar = findViewById(R.id.edtBuscar);

        txtTitulo.setText("Usuarios Registrados");
        iconMain.setImageResource(R.drawable.ic_user);

        cargar();

        // 🔍 BÚSQUEDA EN TIEMPO REAL
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }
        });
    }

    // =====================================================
    // LISTAR USUARIOS
    // =====================================================
    void cargar() {
        StringRequest req = new StringRequest(Request.Method.GET, URL,
                res -> {
                    try {
                        JSONObject json = new JSONObject(res);
                        if (!json.getBoolean("ok")) return;

                        JSONArray arr = json.getJSONArray("data");

                        datos.clear();
                        ids.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            ids.add(o.getInt("id"));

                            datos.add(
                                    "Usuario #" + o.getInt("id") +
                                            "\nNombre: " + o.getString("nombre") +
                                            " " + o.getString("apellido") +
                                            "\nCorreo: " + o.getString("correo")
                            );
                        }

                        datosFiltrados.clear();
                        datosFiltrados.addAll(datos);

                        adaptador = new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, datosFiltrados);

                        list.setAdapter(adaptador);

                    } catch (Exception ignored) {}
                },
                err -> {}
        );

        Volley.newRequestQueue(this).add(req);
    }

    // =====================================================
    // 🔍 FILTRO
    // =====================================================
    void filtrar(String texto) {
        datosFiltrados.clear();

        for (String item : datos) {
            if (item.toLowerCase().contains(texto.toLowerCase())) {
                datosFiltrados.add(item);
            }
        }

        adaptador.notifyDataSetChanged();
    }
}
