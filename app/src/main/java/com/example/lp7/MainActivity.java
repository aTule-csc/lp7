package com.example.lp7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textWeather;
    private TextView textTemp;
    private ImageView imageView;
    protected OkHttpClient client = new OkHttpClient();
    String[] cities = { "N.Tagil, Russia", "Stockholm, Sweden",  "Tel Aviv-Yafo, Israel","Osaka, Japan","Milan, Italy"};
    String[] lat = { "57.913", "59.334", "32.109","34.672","45.464"};
    String[] lon = { "60.559", "18.063", "34.855","135.485","9.188"};
    String appid = "6248b3e3f9aaf2743fe49fdd284cf437";
    String lang = "en";
    String units= "metric";
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat[position] + "&lon=" + lon[position] + "&appid=" + appid + "&lang=" + lang + "&units=" + units;
                OkHTTPHandler handler = new OkHTTPHandler();
                handler.execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);
        textWeather =(TextView) findViewById(R.id.textWeather);
        textTemp=(TextView) findViewById(R.id.textTemp);
        imageView=(ImageView) findViewById(R.id.imageView);
    }
    public class OkHTTPHandler extends AsyncTask<Void,Void,ArrayList>{
        @Override
        protected ArrayList doInBackground(Void ... voids) {
            Request.Builder builder = new Request.Builder();
            Request request = builder.url(url)
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONObject object = new JSONObject(response.body().string());
                String info = object.getJSONArray("weather").getJSONObject(0).getString("description");
                String temp = object.getJSONObject("main").getString("temp");
                Double temp1 = Double.parseDouble(temp);
                temp = String.format("%.0f",temp1) +" C";
                String img = object.getJSONArray("weather").getJSONObject(0).getString("icon");
                URL img_url = new URL("https://openweathermap.org/img/wn/" + img + "@4x.png");
                InputStream inputStream = img_url.openStream();
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                ArrayList<Object> res = new ArrayList<>();
                res.add(info);
                res.add(temp);
                res.add(image);
                return res;
            } catch (IOException | JSONException e ) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(ArrayList o) {
            super.onPostExecute(o);
            textWeather.setText(o.get(0).toString());
            textTemp.setText(o.get(1).toString());
            imageView.setImageBitmap((Bitmap) o.get(2));
        }
    }
}