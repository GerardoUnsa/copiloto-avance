package com.TrabajoBeryllium.copiloto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class Speedometer extends AppCompatActivity implements LocationListener {

    //-------------------- VARIABLES --------------------//
    private boolean sw_metric;
    TextView tv_speed;

    //-------------------- CONSTRUCTOR --------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);

        // Speedometer
        tv_speed = findViewById(R.id.tv_speed);
        sw_metric = false;

        // Si la version del build es igual o mayor a la del que esta en el aplicativo
        // Y si el usuario aun no dio los permisos de acceso a ubicacion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos con el identificador 1000 (1000 identifica que permiso habilitar, en este caso location)
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            // Si la version del build es correcta y se dieron los permisos anteriormente
            doStuff();
        }
        Speedometer.this.updateSpeed(null);
    }

    //-------------------- LOCATION LISTENER and SPEEDOMETER --------------------//

    // Solicitar Permisos de Ubicacion (Solo se ejecuta una vez)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) { // Si el identificador es el de location
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Si el usuario le da permisos al aplicativo
                doStuff();
            } else { // Si el usuario no le da permisos al aplicativo
                Toast.makeText(this, "Activar Permisos de Ubicacion", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Verificar si la ubicacion esta activado
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS DESHABILITADO", Toast.LENGTH_SHORT).show();
        } else if (locationManager != null) { // Si el gps esta habilitado
            sw_metric = true;
            Toast.makeText(this, "GPS HABILITADO - CARGANDO GPS", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("On location Change", "1");
        LocationListener.super.onStatusChanged(provider, status, extras);
    }
    @Override // Se ejecuta despues de habilitar el gps
    public void onProviderEnabled(@NonNull String provider) {
        Log.e("On location Change", "2");
        doStuff();
        LocationListener.super.onProviderEnabled(provider);
    }
    @Override // Se ejecuta despues de desabilitar el gps
    public void onProviderDisabled(@NonNull String provider) {
        Log.e("On location Change", "3");
        Toast.makeText(this, "GPS DESHABILITADO 2", Toast.LENGTH_SHORT).show();
        LocationListener.super.onProviderDisabled(provider);
    }
    @Override // Se ejecuta cada vez que la ubicacion cambia
    public void onLocationChanged(@NonNull Location location) {
        Log.e("On location Change", "4");
        if(location != null) {
            CLocation myLocation = new CLocation(location);
            this.updateSpeed(myLocation);
        }
    }

    private boolean allowSpeedometer() {
        Log.v("AllowSpeedMeter", "->" + sw_metric);
        return sw_metric;
    }

    private void updateSpeed(CLocation location) {
        float nCurrentSpeed = 0;

        if(location != null) { // Location diferente a null
            location.setAllowCalculation(this.allowSpeedometer());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%2.1f", nCurrentSpeed); // Dale formato sin decimales
        String strCurrentSpeed = fmt.toString(); // Cast a string
        strCurrentSpeed = strCurrentSpeed.replace(" ","0");

        tv_speed.setText(strCurrentSpeed + "km/h");
        Log.e("Velocidad: ", strCurrentSpeed);
    }
}