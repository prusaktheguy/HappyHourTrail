package com.example.prusak.happyhourtrail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.prusak.happyhourtrail.models.Beer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.prusak.happyhourtrail.Constants.onTapPubsNames;

/**
 * Created by Prusak on 2018-01-04.
 */

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener {

  //  private GoogleMap mMap;
  private Button returnButton;
  private TextView statusText;
  private static String status;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("scrappr", ("jestesmy w scrapperze"));
        setContentView(R.layout.activity_update);
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        startActivity(new Intent(MainActivity.this, UserMainActivity.class));
        returnButton = findViewById(R.id.menu_return_button);
        statusText = findViewById(R.id.update_status_label);
        status="";
        returnButton.setClickable(false);
        returnButton.setOnClickListener(this);

        new RetrieveFeedTask().execute(Constants.onTapUrls);
    }

    @Override
    public void onClick(View view) {
        if(!status.isEmpty()){
            startActivity(new Intent(UpdateDataActivity.this, MenuActivity.class));
        }

    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                int pubNumber=0;
            for (String url : urls){
                Log.i("podstrona on-tapa", (url));
                mDatabase.child("pubs").child(onTapPubsNames[pubNumber]).removeValue();

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
                        Log.i("jaki pub : ", (onTapPubsNames[pubNumber]));
                        Log.i("etykietka", (img));
                        Log.i("browarek", (brewery));
                        Log.i("piwerko", (name));
                        Log.i("gatuneczek", (kind));
                        Log.i("cenusia", (price));
                        //     Log.i("pitole", (what));

                        String key = mDatabase.child("pubs").child(onTapPubsNames[pubNumber]).push().getKey();
//                        String key = mDatabase.child("pubs").push().getKey();
                        Beer beer = new Beer(key, img, brewery, name, kind, price);
                        Map<String, Object> beerValues = beer.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/pubs/" + onTapPubsNames[pubNumber] + "/" + name, beerValues);

                        mDatabase.updateChildren(childUpdates);


                    }
                }
                pubNumber++;
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
                Log.i("main", "blad : "+ exception);

            }
            returnButton.setClickable(true);
        }
    }
}
