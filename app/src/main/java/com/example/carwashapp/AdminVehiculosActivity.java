package com.example.carwashapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminVehiculosActivity extends AppCompatActivity {

    TextView txtTitulo;
    ImageView iconMain;
    ListView list;
    EditText edtBuscar;

    ArrayList<String> datos = new ArrayList<>();
    ArrayList<String> datosFiltrados = new ArrayList<>();
    ArrayAdapter<String> adaptador;

    String URL_LISTAR = "http://18.191.153.112/api_carwash/vehiculos/listar_admin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_template);

        txtTitulo = findViewById(R.id.txtTitulo);
        iconMain = findViewById(R.id.iconMain);
        list = findViewById(R.id.listGeneral);
        edtBuscar = findViewById(R.id.edtBuscar);

        // 🔥 Título e ícono dinámico
        txtTitulo.setText("Vehículos Registrados");
        iconMain.setImageResource(R.drawable.ic_car);

        cargarVehiculos();

        // 🔍 Filtro en tiempo real
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
    // LISTAR VEHÍCULOS
    // =====================================================
    void cargarVehiculos() {

        StringRequest req = new StringRequest(Request.Method.GET, URL_LISTAR,
                res -> {
                    try {

                        JSONObject json = new JSONObject(res);
                        if (!json.getBoolean("ok")) return;

                        JSONArray arr = json.getJSONArray("data");

                        datos.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            String marca = o.getString("marca");
                            String modelo = o.getString("modelo");
                            int anio = o.getInt("anio");
                            String placa = o.optString("placa", "N/A");
                            String aceite = o.optString("tipo_aceite", "N/A");

                            String nombre = o.optString("usuario_nombre");
                            String apellido = o.optString("usuario_apellido");

                            datos.add(
                                    "🚗 " + marca + " " + modelo + " (" + anio + ")\n" +
                                            "Placa: " + placa + "\n" +
                                            "Aceite: " + aceite + "\n" +
                                            "Usuario: " + nombre + " " + apellido
                            );
                        }

                        datosFiltrados.clear();
                        datosFiltrados.addAll(datos);

                        adaptador = new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, datosFiltrados);

                        list.setAdapter(adaptador);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                err -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
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

        if (adaptador != null) adaptador.notifyDataSetChanged();
    }
}
