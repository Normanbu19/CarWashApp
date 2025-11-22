package com.example.carwashapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location; // ← IMPORT CORRECTO
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapaUbicacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISO_GPS = 200;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_ubicacion);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        pedirPermisoGPS();
    }

    private void pedirPermisoGPS() {

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_GPS
            );

        } else {
            cargarMapa();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISO_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarMapa();
            } else {
                Toast.makeText(this, "Se requiere permiso de GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarMapa() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        obtenerUbicacionActual();
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacionActual() {

        fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {

            double lat = 14.0723;   // Coordenadas por defecto
            double lng = -87.1921;

            if (loc != null) {
                lat = loc.getLatitude();
                lng = loc.getLongitude();
            }

            LatLng punto = new LatLng(lat, lng);

            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(punto).title("Tu ubicación"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 16f));

            obtenerDireccion(lat, lng);
        });
    }

    private void obtenerDireccion(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            List<Address> lista = geo.getFromLocation(lat, lng, 1);

            if (!lista.isEmpty()) {
                String dir = lista.get(0).getAddressLine(0);

                // GUARDAR PARA ENVIAR A LA COTIZACIÓN
                SolicitarCotizacionActivity.LAT = String.valueOf(lat);
                SolicitarCotizacionActivity.LNG = String.valueOf(lng);
                SolicitarCotizacionActivity.DIRECCION_TEXTUAL = dir;

                Toast.makeText(this, "Dirección obtenida", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show();
        }
    }
}
