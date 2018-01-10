package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener {

  //  private GoogleMap mMap;
  private Button returnButton;
  private TextView statusText;
  private static String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("scrappr", ("jestesmy w scrapperze"));
        setContentView(R.layout.activity_update);
//        startActivity(new Intent(MainActivity.this, UserMainActivity.class));
        returnButton = findViewById(R.id.menu_return_button);
        statusText = findViewById(R.id.update_status_label);
        returnButton.setOnClickListener(this);

        new RetrieveFeedTask().execute("http://chmielowa-dolina.ontap.pl/","http://kij.ontap.pl/","http://piw-paw-lodz.ontap.pl/","http://piwoteka-narodowa.ontap.pl/","http://z-innej-beczki.ontap.pl/");
    }

    @Override
    public void onClick(View view) {
        if(status.equals("ok")){
            startActivity(new Intent(UpdateDataActivity.this, MainActivity.class));
        }

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

                return "bad IO";
            } catch (Exception e) {
                this.exception = e;
                return "very bad";
            }
            }


        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            Log.i("main", "znowu : "+ feed);
            if(feed.equals("ok")){
                status = "ok";
                statusText.setText(R.string.aktualizacjaOK);

            }else{
                status = "bad";
                statusText.setText(R.string.aktualizacja_blad);
            }
        }
    }
}
