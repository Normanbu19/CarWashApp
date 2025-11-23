package com.example.carwashapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

    ListView list;
    ArrayList<String> datos = new ArrayList<>();

    String URL_LISTAR = "http://18.191.153.112/api_carwash/vehiculos/listar_admin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_template);

        // 🔥🔥🔥 ESTE ES EL TÍTULO COMO EN COTIZACIONES 🔥🔥🔥
        TextView txtTitulo = findViewById(R.id.txtTitulo);
        txtTitulo.setText("Vehículos Registrados");

        // 🔥🔥🔥 ICONO DINÁMICO 🔥🔥🔥
        ImageView iconMain = findViewById(R.id.iconMain);
        iconMain.setImageResource(R.drawable.ic_car); // Usa un icono que tengas

        list = findViewById(R.id.listGeneral);

        cargarVehiculos();
    }

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

                        list.setAdapter(new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, datos));

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                err -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }
}
