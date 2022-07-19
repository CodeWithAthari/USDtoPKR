package com.qasimaliapps.usdtopkr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qasimaliapps.usdtopkr.databinding.ActivityMainBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String currentPrice;
    public static float todayPrice;
    public static final String api = "https://www.google.com/search?q=1$";
    ActivityMainBinding binding;
    Document document;
    int usd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            if (!isConnectedToInternet()) {
                Toast.makeText(this, "Make Sure You Are Connected to internet", Toast.LENGTH_SHORT).show();
                binding.button.setEnabled(false);
                binding.inputVal.setEnabled(false);
                binding.textView.setEnabled(false);
                binding.textView.setText("Turn on Internet");
                return;
            }
            InfoGetter infoGetter = new InfoGetter();
            infoGetter.execute();


            binding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (binding.inputVal.getText().toString().equals("") || binding.inputVal.getText().toString().equals("0")) {
                            Toast.makeText(MainActivity.this, "Enter Value", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        if (todayPrice == 50) {
                            SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                            todayPrice = sharedPreferences.getFloat("todayprice", todayPrice);
                            Toast.makeText(getApplicationContext(), "from Sharedd" + todayPrice, Toast.LENGTH_SHORT).show();

                        }
                        binding.textView.setText("Today Price is: " + todayPrice);

                        String strusd = binding.inputVal.getText().toString();

                        usd = Integer.parseInt(strusd);


                        float finalprice = usd * todayPrice;

                        binding.textView.setText("The Price is: " + finalprice);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class InfoGetter extends AsyncTask<Void, Void, Void> {
        ProgressDialog p;

        @Override
        protected void onPreExecute() {

            try {
                p = new ProgressDialog(MainActivity.this);
                p.setMessage("Wait while getting information...");
                p.setCancelable(false);
                p.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                document = Jsoup.connect(api)
                        .get();
                //   Log.d("Scrap",document.html());


                Elements element = document.getElementsByClass("DFlfde SwHCTb");
                currentPrice = element.text();
                Log.d("Scrap", element.text());


            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            try {
                p.dismiss();
                if (!Objects.equals(currentPrice, "50")) {
                    binding.button.setEnabled(true);
                    binding.textView.setText("Today Price is: " + currentPrice);

                } else {
                    binding.button.setEnabled(false);
                }
                if (todayPrice != 100) {
                    binding.button.setEnabled(true);
                    if (currentPrice != null) {
                        todayPrice = Float.parseFloat(currentPrice);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putFloat("todayprice", todayPrice);

                    // editor.apply();


                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


        }
    }

    public Boolean isConnectedToInternet() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnectedToInternet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isConnectedToInternet();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isConnectedToInternet();

    }
}