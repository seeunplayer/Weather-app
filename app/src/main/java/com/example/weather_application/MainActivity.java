package com.example.weather_application;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView cityTextView;
    TextView tempTextView;
    Button searchButton;
    EditText searchEditText;
    ImageView imageHere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.city);
        tempTextView = findViewById(R.id.degree);
        searchButton = findViewById(R.id.search);
        searchEditText = findViewById(R.id.sample_EditText);
        imageHere = findViewById(R.id.image_weather);


    }

    static class Weather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... address) {

            try{
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream streamIn = connection.getInputStream();
                InputStreamReader streamInReader = new InputStreamReader(streamIn);

                int data = streamInReader.read(); // no data-> EOF == -1
                StringBuilder weatherContent = new StringBuilder();

                while (data != -1){
                    char ch = (char) data;
                    weatherContent.append(ch);
                    data = streamInReader.read();
                }

                return weatherContent.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void searchWeather(View v) {
        String city = searchEditText.getText().toString().trim();

        if(city.isEmpty()){
            Toast.makeText(this, "Nothing entered here", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = "https://openweathermap.org/data/2.5/weather?q=" + city + "&appid=439d4b804bc8187953eb36d2a8c26a02";
        callWeatherData(content);

    }

    public void callWeatherData(String content){
        Weather weather = new Weather();

        try{
            String data = weather.execute(content).get();

            JSONObject jsonObject = new JSONObject(data);
            JSONObject mainObject = jsonObject.getJSONObject("main");

            String cityName = jsonObject.getString("name");
            String weatherData = jsonObject.getString("weather");
            String tempData = mainObject.getString("temp");
            String description = "";
            String iconData = "";

            JSONArray weatherArray = new JSONArray(weatherData);

            for(int i = 0; i < weatherArray.length(); i++){
                JSONObject object = weatherArray.getJSONObject(i);
                description = object.getString("description");
                iconData = object.getString("icon");
            }

            String retrieveIcon = "http://openweathermap.org/img/wn/" + iconData + "@2x.png";

            String details = description + " / " + tempData + '\u2103';
            cityTextView.setText(cityName);
            tempTextView.setText(details);

            Glide.with(this)
                    .load(retrieveIcon)
                    .centerCrop()
                    .into(imageHere);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}