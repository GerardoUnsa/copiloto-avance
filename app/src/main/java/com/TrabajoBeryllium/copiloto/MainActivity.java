 package com.TrabajoBeryllium.copiloto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.TrabajoBeryllium.copiloto.User.ApiInterface;
import com.TrabajoBeryllium.copiloto.User.Client;
import com.TrabajoBeryllium.copiloto.User.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

 public class MainActivity extends AppCompatActivity {

     private static final String TAG = "Response";
     private Button btnSendPostRequest;
     private Button btnNextActivity;

     // Guardar Sesion Login
     SharedPreferences preferences;
     SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gestion sesion
        inicializarElements();
        if (revisarSesion()){
            startActivity(new Intent(MainActivity.this, Speedometer.class));
        }

        setContentView(R.layout.activity_main);

        // API Requests
        btnSendPostRequest = findViewById(R.id.btnSendPostRequest);
        btnSendPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSendPostRequestClicked();
            }
        });

        // LOGIN
        btnNextActivity = (Button)findViewById(R.id.btnNextActivity);
        btnNextActivity.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, Speedometer.class));
            }
        });
    }

     // API Requests
     private void btnSendPostRequestClicked() {
        ApiInterface apiInterface = Client.getRetrofitInstance().create(ApiInterface.class);
        Call<User> call = apiInterface.getUserInformation("copilotoadmin", "copilotoadmin");
        call.enqueue(new Callback<User>(){
           @Override
           public void onResponse(Call<User> call, Response<User> response) {
               Log.e(TAG, "onResponse: "+response.code());
               Log.e(TAG, "onResponse token: "+response.body().getToken());
               Log.e(TAG, "onResponse user id: "+response.body().getUser_id());
               Log.e(TAG, "onResponse name: "+response.body().getUser_name());
               Log.e(TAG, "onResponse firstname: "+response.body().getUser_first_name());
               Log.e(TAG, "onResponse lastname: "+response.body().getUser_last_name());
               Log.e(TAG, "onResponse email: "+response.body().getUser_email());
               Log.e(TAG, "onResponse is staff: "+response.body().getUser_is_staff());
               Log.e(TAG, "onResponse is super: "+response.body().getUser_is_super());

               // Guardar sesion
               guardarSesion();
           }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage());
            }
        });
     }

     // Gestion Sesion
     private void guardarSesion() {
         editor.putBoolean("sesion", true);
         editor.apply();
     }

     private void inicializarElements(){
         preferences = this.getPreferences(Context.MODE_PRIVATE);
         editor = preferences.edit();
     }
     private boolean revisarSesion() {
        boolean sesion = this.preferences.getBoolean("sesion", false);
        return sesion;
     }
 }