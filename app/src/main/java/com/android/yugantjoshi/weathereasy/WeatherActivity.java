package com.android.yugantjoshi.weathereasy;

import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.yugantjoshi.weathereasy.models.WeatherData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{
    public static final String BASE_URL = "http://api.openweathermap.org";
    TextView city_text, conditions_text, humidity_text, pressure_text, lat_text, lon_text;
    Button weatherButton;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    Location lastLocation;
    String lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        city_text = (TextView) findViewById(R.id.city_text);
        conditions_text = (TextView) findViewById(R.id.conditions_text);
        humidity_text = (TextView) findViewById(R.id.humidity_text);
        pressure_text = (TextView) findViewById(R.id.pressure_text);
        weatherButton = (Button) findViewById(R.id.weather_button);
        lat_text = (TextView) findViewById(R.id.lat_text);
        lon_text = (TextView) findViewById(R.id.lon_text);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherUpdateCall();
            }
        });


    }

    synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void getWeatherUpdateCall()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //We can bring this all together by constructing a service leveraging
        // the MyApiEndpointInterface interface with the defined endpoints:
        WeatherServiceInterface weatherService = retrofit.create(WeatherServiceInterface.class);

        Call<WeatherData> weatherCall = weatherService.getCurrentWeather();

        //Execute the call
        weatherCall.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response)
            {
                String city, conditions, humidity, pressure;
                city = response.body().getName();
                conditions = response.body().getWeather().get(0).getDescription();
                humidity = response.body().getMain().getHumidity().toString();
                pressure = response.body().getMain().getPressure().toString();

                city_text.setText("City: "+city);
                conditions_text.setText("Conditions: "+conditions);
                humidity_text.setText("Humidity: "+humidity);
                pressure_text.setText("Pressure: "+pressure);

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            lat = String.valueOf(lastLocation.getLatitude());
            lon = String.valueOf(lastLocation.getLongitude());

            lat_text.setText("Latitude: "+lat);
            lon_text.setText("Longitude: "+lon);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onStart()
    {
        super.onStart();
        googleApiClient.connect();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        googleApiClient.disconnect();
    }

}
