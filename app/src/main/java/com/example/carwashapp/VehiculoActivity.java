package com.example.carwashapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VehiculoActivity extends AppCompatActivity {

    private ListView listVehiculos;
    private ArrayList<String> listaVehiculos = new ArrayList<>();

    private int ID_USUARIO = 1; // cámbialo si usas SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        listVehiculos = findViewById(R.id.listVehiculos);

        cargarVehiculos();
    }

    private void cargarVehiculos() {

        String url = "http://18.191.153.112/api_carwash/vehiculos/listar.php?id_usuario=" + ID_USUARIO;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (!json.getBoolean("ok")) {
                            Toast.makeText(this, "Error API: " + json.getString("msg"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray data = json.getJSONArray("data");
                        listaVehiculos.clear();

                        if (data.length() == 0) {
                            listaVehiculos.add("No hay vehículos registrados.");
                        } else {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject v = data.getJSONObject(i);

                                String marca = v.getString("marca");
                                String modelo = v.getString("modelo");
                                int anio = v.getInt("anio");
                                String placa = v.getString("placa");

                                String item =
                                        "🚗 " + marca + " " + modelo + " (" + anio + ")\n" +
                                                "Placa: " + placa;

                                listaVehiculos.add(item);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                listaVehiculos
                        );

                        listVehiculos.setAdapter(adapter);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error conectando al servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
