package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Prusak on 2018-01-04.
 */

public class UpdateDataActivity  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        startActivity(new Intent(MainActivity.this, UserMainActivity.class));
        new RetrieveFeedTask().execute("http://chmielowa-dolina.ontap.pl/","http://kij.ontap.pl/","http://piw-paw-lodz.ontap.pl/","http://piwoteka-narodowa.ontap.pl/","http://z-innej-beczki.ontap.pl/");



        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
            for (String url : urls){
                Log.i("podstrona on-tapa", (url));

                Document doc = Jsoup.connect(url).get();
                Elements els = doc.getElementsByClass("panel panel-default");
                for (Element container : els) {
                    //  String what = container.text();
                    if (!container.getElementsByClass("panel-body cml_semi").isEmpty() &&
                            !container.getElementsByClass("panel-body cml_semi").get(0).getElementsByClass("cml_shadow").get(0).select("span").first().text().contains("N/A") &&
                            !container.getElementsByClass("panel-body cml_semi").get(0).attr("style").isEmpty()
                            ) {
                        Element beerContainer = container.getElementsByClass("panel-body cml_semi").get(0);
                        String price = container.getElementsByClass("col-xs-5 white").text();

                        String imagStyle = beerContainer.attr("style"); // You can put those two lines into one
                        String imgBeta = imagStyle.split("\\(")[1]; // TODO: Insert a check if a value is set
                        String img = imgBeta.split("\\)")[0];
                        Elements beerNameAndBrewery = beerContainer.getElementsByClass("cml_shadow");


                        String brewery = beerNameAndBrewery.get(0).getElementsByClass("brewery").text();
                        String nameHtml = beerNameAndBrewery.get(0).select("span").first().html();
                        String[] textSplitResult = nameHtml.split("<br>");
                        String name = textSplitResult[1].split("<")[0];
                        String kind = beerNameAndBrewery.get(1).select("b").first().text();
                        Log.i("etykietka", (img));
                        Log.i("browarek", (brewery));
                        Log.i("piwerko", (name));
                        Log.i("gatuneczek", (kind));
                        Log.i("cenusia", (price));
                        //     Log.i("pitole", (what));

//                    for (Element help : beerNameAndBrewery){
//                        Log.i("main", (help.toString()));
//
//                    }

//                    log("%s\n\t%s",
//                            headline.attr("title"), headline.absUrl("href"));
//                    Log.i("main", (beerContainer.toString()));
                    }
                }


//                Log.i("main", (doc.title()));
            }
                return "ok";
            } catch (IOException e) {
                this.exception = e;

                return null;
            }
            }


        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            Log.i("main", "znowu : "+ feed);
        }
    }
}
