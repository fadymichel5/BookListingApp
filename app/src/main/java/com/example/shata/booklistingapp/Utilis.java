/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.shata.booklistingapp;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Utility class with methods to help perform the HTTP request and
 * parse the response.
 */
public final class Utilis extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utilis.class.getSimpleName();

    /**
     * Query the USGS dataset and return an {@link Book} object to represent a single earthquake.
     */
    public  static ArrayList<Book> fetchBookData (String requestUrl) {
        // Create URL object
        Log.i("Requested URL", requestUrl);
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
            Log.i("jsonResponse", jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Book> books = extractFeatureFromJson(jsonResponse);

        if(books==null)
            Log.i("earthquake", "NuLLLLLLLLLLLLL hna kmann");

        // Return the {@link Event}
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private  static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private  static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input BookJSON string.
     */
    private   static ArrayList<Book> extractFeatureFromJson(String BookJSON)  {


        Log.i("earthquake", BookJSON);


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(BookJSON)) {

            return null;
        }

        try {

            JSONObject baseJsonResponse = new JSONObject(BookJSON);
            int totalItems = baseJsonResponse.getInt("totalItems");

            if (totalItems==0)
            {

                return null;
            }


            JSONArray featureArray = baseJsonResponse.getJSONArray("items");
            ArrayList<Book> books = new ArrayList<>();

            // If there are results in the features array

            int i = 0;
            int z = featureArray.length();
            while (i < z && featureArray.length() != 0) {


                // Extract out the first feature (which is an earthquake)
                JSONObject firstFeature = featureArray.getJSONObject(i);


                // Extract out the title, number of people, and perceived strength values
                JSONObject volume_info = firstFeature.getJSONObject("volumeInfo");

                String title = volume_info.getString("title");
                StringBuilder authors = new StringBuilder();
                String authors_string=new String();

                if(volume_info.has("authors"))
                {

                    JSONArray authorArray = volume_info.getJSONArray("authors");

                    int x = authorArray.length();


                    if (x == 0)
                        authors.append("No Authors");
                    else {
                        authors.append(authorArray.getString(0));
                        x--;
                        while (x != 0) {
                            authors.append(" and ");
                            authors.append(authorArray.getString(x));
                            x--;

                        }
                    }
                     authors_string = authors.toString();

                }
                else{

                    authors.append("Authors N/A");
                    authors_string = authors.toString();

                }

                // Create a new {@link Event} object
                books.add(new Book(title, authors_string));

                i++;
            }


            return books;


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        return null;
    }
}
