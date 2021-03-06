package com.example.prusak.happyhourtrail.models;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Prusak on 2018-01-17.
 */

@IgnoreExtraProperties
public class Beer {

    public String uid;
    public String etykieta;
    public String browar;
    public String nazwa;
    public String gatunek;
    public String cena;
    @Exclude
    public Bitmap img;


    public Beer(String uid, String etykieta, String browar, String nazwa, String gatunek, String cena) {
        this.uid = uid;
        this.etykieta = etykieta;
        this.browar = browar;
        this.nazwa = nazwa;
        this.gatunek = gatunek;
        this.cena = cena;
    }
    public Beer(){

    }
    @Exclude
    @Override
    public String toString(){
        String data = nazwa;
        return data;



    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("etykieta", etykieta);
        result.put("browar", browar);
        result.put("nazwa", nazwa);
        result.put("gatunek", gatunek);
        result.put("cena", cena);

        return result;
    }
    @Exclude
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Beer other = (Beer) obj;
        if (nazwa == null) {
            if (other.nazwa != null)
                return false;
        }

        if(cena!=null && other.cena!=null){
            if(!cena.equals(other.cena)){
                return false;
            }
        }
        if(browar!=null && other.browar!=null){
            if(!browar.equals(other.browar)){
                return false;
            }
        }
        if(gatunek!=null && other.gatunek!=null){
            if(!gatunek.equals(other.gatunek)){
                return false;
            }
        }
        if(nazwa!=null && other.nazwa!=null){
            if(!nazwa.equals(other.nazwa)){
                return false;
            }
        }
        return true;
    }

}
