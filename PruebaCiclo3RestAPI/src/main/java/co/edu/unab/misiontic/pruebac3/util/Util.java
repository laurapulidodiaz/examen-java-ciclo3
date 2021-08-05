/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unab.misiontic.pruebac3.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import java.util.Date;

/**
 *
 * @author Dm
 */
public class Util {

    public static Gson getGson() {
        // Trick to get the DefaultDateTypeAdatpter instance
        // Create a first instance a Gson
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        // Get the date adapter
        TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);

        // Ensure the DateTypeAdapter is null safe
        TypeAdapter<Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();

        // Build the definitive safe Gson instance
        return new GsonBuilder().serializeNulls()
                .registerTypeAdapter(Date.class, safeDateTypeAdapter)
                .create();
    }
}
