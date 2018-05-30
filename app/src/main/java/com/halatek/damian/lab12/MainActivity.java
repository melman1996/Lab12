package com.halatek.damian.lab12;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.halatek.damian.lab12.Model.WeatherData;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private final static String KEY = "4985f781a282b9ed64debe4494e81dc0";
    private final static int PERMISSION_REQUEST_CODE = 1;

    private TextView mCity;
    private TextView mUpdated;
    private TextView mIcon;
    private TextView mTemperature;
    private TextView mDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchCurrentWeather(getCurrentLocation());
            }
        });

        mCity = findViewById(R.id.city);
        mUpdated = findViewById(R.id.updated);
        mIcon = findViewById(R.id.icon);
        mIcon.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/weather.ttf"));
        mTemperature = findViewById(R.id.temperature);
        mDetails = findViewById(R.id.details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Location getCurrentLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(checkPermission()){
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            requestPermission();
            if(checkPermission()){
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                return null;
            }
        }
    }

    private void fetchCurrentWeather(Location location){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s",location.getLatitude(), location.getLongitude(), KEY);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WeatherData data = gson.fromJson(response, WeatherData.class);
                        displayWeatherInfo(data);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(stringRequest);
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void displayWeatherInfo(WeatherData data){
        mCity.setText(data.getName().toUpperCase() + ", " + data.getSys().getCountry());
        mDetails.setText(data.getWeather()[0].getDescription().toUpperCase() + "\n" +
                        "Humidity: " + data.getMain().getHumidity() + "%\n" +
                        "Pressure: " + data.getMain().getPressure() + "hPa");
        mTemperature.setText(String.format("%.2f", data.getMain().getTemp()) + "℃");
        DateFormat df = DateFormat.getDateTimeInstance();
        String updatedOn = df.format(new Date(data.getDt() * 1000));
        mUpdated.setText("Last updated: " + updatedOn);

        setWeatherIcon(data.getWeather()[0].getId(),
                data.getSys().getSunrise() * 1000,
                data.getSys().getSunset() * 1000);

    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        mIcon.setText(icon);
    }
}