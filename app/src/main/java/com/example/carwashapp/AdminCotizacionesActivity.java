package com.example.carwashapp;

import android.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Map;

public class AdminCotizacionesActivity extends AppCompatActivity {

    TextView txtTitulo;
    ImageView iconMain;
    ListView list;
    EditText edtBuscar;

    ArrayList<String> datos = new ArrayList<>();
    ArrayList<String> datosFiltrados = new ArrayList<>();
    ArrayAdapter<String> adaptador;

    ArrayList<Integer> ids = new ArrayList<>();

    String URL_LISTAR = "http://18.191.153.112/api_carwash/cotizaciones/listar.php";
    String URL_CONFIRMAR = "http://18.191.153.112/api_carwash/cotizaciones/confirmar.php";
    String URL_COMPLETAR = "http://18.191.153.112/api_carwash/cotizaciones/completar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_template);

        txtTitulo = findViewById(R.id.txtTitulo);
        iconMain = findViewById(R.id.iconMain);
        list = findViewById(R.id.listGeneral);
        edtBuscar = findViewById(R.id.edtBuscar);

        txtTitulo.setText("Gestión de Cotizaciones");
        iconMain.setImageResource(R.drawable.ic_cotizacion);

        cargar();

        // 👉 CLICK EN ITEM → MENÚ DE DECISIÓN
        list.setOnItemClickListener((adapterView, view, position, id) -> {
            int idCotizacion = ids.get(position);
            mostrarMenuAcciones(idCotizacion);
        });

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
    // LISTAR COTIZACIONES
    // =====================================================
    void cargar() {

        StringRequest req = new StringRequest(Request.Method.GET, URL_LISTAR,
                res -> {
                    try {

                        JSONObject json = new JSONObject(res);
                        if (!json.getBoolean("ok")) return;

                        JSONArray arr = json.getJSONArray("data");

                        datos.clear();
                        ids.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            ids.add(o.optInt("id"));

                            String nombre = o.optString("usuario_nombre", "Desconocido");
                            String apellido = o.optString("usuario_apellido", "");
                            String servicio = o.optString("servicio_nombre", "N/A");
                            String precio = o.optString("servicio_precio", "0");
                            String estado = o.optString("estado", "N/A");

                            datos.add(
                                    "Cotización #" + o.optInt("id") +
                                            "\nCliente: " + nombre + " " + apellido +
                                            "\nServicio: " + servicio + " (L" + precio + ")" +
                                            "\nEstado: " + estado
                            );
                        }

                        datosFiltrados.clear();
                        datosFiltrados.addAll(datos);

                        adaptador = new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, datosFiltrados);

                        list.setAdapter(adaptador);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    // =====================================================
    // MENÚ DE ACCIONES
    // =====================================================
    void mostrarMenuAcciones(int idCotizacion) {

        String[] opciones = {"Confirmar", "Marcar como Completada", "Cancelar"};

        new AlertDialog.Builder(this)
                .setTitle("Acción para Cotización #" + idCotizacion)
                .setItems(opciones, (dialog, which) -> {

                    if (which == 0) {
                        confirmarCotizacion(idCotizacion);
                    } else if (which == 1) {
                        completarCotizacion(idCotizacion);
                    }

                })
                .show();
    }

    // =====================================================
    // CONFIRMAR
    // =====================================================
    void confirmarCotizacion(int idCotizacion) {

        StringRequest req = new StringRequest(Request.Method.POST, URL_CONFIRMAR,
                res -> {
                    try {
                        JSONObject json = new JSONObject(res);

                        if (json.getBoolean("ok")) {
                            Toast.makeText(this, "Cotización confirmada", Toast.LENGTH_SHORT).show();
                            cargar();
                        } else {
                            Toast.makeText(this, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    }
                },
                err -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idCotizacion));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }

    // =====================================================
    // COMPLETAR
    // =====================================================
    void completarCotizacion(int idCotizacion) {

        StringRequest req = new StringRequest(Request.Method.POST, URL_COMPLETAR,
                res -> {
                    try {
                        JSONObject json = new JSONObject(res);

                        if (json.getBoolean("ok")) {
                            Toast.makeText(this, "Cotización completada", Toast.LENGTH_SHORT).show();
                            cargar();
                        } else {
                            Toast.makeText(this, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Error inesperado", Toast.LENGTH_SHORT).show();
                    }
                },
                err -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(idCotizacion));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
}
