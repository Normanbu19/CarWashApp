package com.example.carwashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialServiciosActivity extends AppCompatActivity {

    private ListView listHistorial;
    private ArrayList<String> listaCompleta = new ArrayList<>();
    private ArrayList<JSONObject> registros = new ArrayList<>();

    private Button btnFiltroAceite, btnFiltroLavados, btnHistorialVehiculos;

    private int ID_USUARIO = 1; // Se debe recibir con intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_servicios);

        listHistorial = findViewById(R.id.listHistorial);
        btnFiltroAceite = findViewById(R.id.btnFiltroAceite);
        btnFiltroLavados = findViewById(R.id.btnFiltroLavados);
        btnHistorialVehiculos = findViewById(R.id.btnHistorialVehiculos);

        cargarHistorial();

        btnFiltroAceite.setOnClickListener(v -> filtrarAceite());
        btnFiltroLavados.setOnClickListener(v -> filtrarLavados());

        btnHistorialVehiculos.setOnClickListener(v ->
                startActivity(new Intent(HistorialServiciosActivity.this, VehiculoActivity.class))
        );
    }

    private void cargarHistorial() {

        String url = "http://18.191.153.112/api_carwash/historial/listar.php?id_usuario=" + ID_USUARIO;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                resp -> {
                    try {

                        JSONObject json = new JSONObject(resp);

                        if (!json.getBoolean("ok")) return;

                        JSONArray data = json.getJSONArray("data");

                        listaCompleta.clear();
                        registros.clear();

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject h = data.getJSONObject(i);
                            registros.add(h);

                            String item =
                                    "🔧 Servicio: " + h.getString("servicio") + "\n" +
                                            "Vehículo: " + h.getString("marca") + " " + h.getString("modelo") + "\n" +
                                            "Fecha: " + h.getString("fecha_realizada");

                            listaCompleta.add(item);
                        }

                        actualizarLista(listaCompleta);

                    } catch (Exception e) {
                        Toast.makeText(this, "Error al procesar historial", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error conectando al servidor", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }

    private void actualizarLista(ArrayList<String> lista) {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);

        listHistorial.setAdapter(adapter);
    }

    private void filtrarAceite() {

        ArrayList<String> filtrada = new ArrayList<>();

        for (JSONObject h : registros) {
            try {

                if (h.getString("servicio").toLowerCase().contains("aceite")) {

                    filtrada.add(
                            "🛢 Cambio de aceite\n" +
                                    "Vehículo: " + h.getString("marca") + " " + h.getString("modelo") + "\n" +
                                    "Fecha: " + h.getString("fecha_realizada")
                    );
                }

            } catch (Exception ignored) {}
        }

        if (filtrada.isEmpty()) filtrada.add("No hay servicios de aceite.");

        actualizarLista(filtrada);
    }

    private void filtrarLavados() {

        ArrayList<String> filtrada = new ArrayList<>();

        for (JSONObject h : registros) {
            try {

                if (h.getString("servicio").toLowerCase().contains("lavado")) {

                    filtrada.add(
                            "🚗 Lavado\n" +
                                    "Vehículo: " + h.getString("marca") + " " + h.getString("modelo") + "\n" +
                                    "Fecha: " + h.getString("fecha_realizada")
                    );
                }

            } catch (Exception ignored) {}
        }

        if (filtrada.isEmpty()) filtrada.add("No hay lavados realizados.");

        actualizarLista(filtrada);
    }
}
