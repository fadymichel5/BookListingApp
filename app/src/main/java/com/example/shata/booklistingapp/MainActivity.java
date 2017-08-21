package com.example.shata.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {

    private final int EARTHQUAKE_LOADER_ID = 1;
    StringBuilder URL = new StringBuilder();
    String URL_String;
    String book_wanted;
    boolean restart = false;
    BookAdapter adapter;
    private String USGS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";


    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!isNetworkOnline()) {
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setVisibility(View.VISIBLE);
            TextView no_inter = (TextView) findViewById(R.id.no_internent);
            no_inter.setVisibility(View.GONE);

        }


        Button search_button = (Button) findViewById(R.id.search_button);


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isNetworkOnline()) {

                    LoaderManager loaderManager = getLoaderManager();




                    if(!restart)
                    {
                        EditText editText = (EditText) findViewById(R.id.edit_text);
                        book_wanted = editText.getText().toString();
                        URL.append(USGS_REQUEST_URL);
                        URL.append(book_wanted);
                        URL_String = URL.toString();


                        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
                        restart=true;

                    }

                    else
                    {
                        EditText editText = (EditText) findViewById(R.id.edit_text);
                        book_wanted = editText.getText().toString();
                        URL.append(USGS_REQUEST_URL);
                        URL.append(book_wanted);
                        URL_String = URL.toString();
                       

                        loaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);

                    }



                } else {
                    ListView listView = (ListView) findViewById(R.id.list_view);
                    listView.setEmptyView(findViewById(R.id.empty_list_item));
                    TextView no_inter = (TextView) findViewById(R.id.no_internent);
                    no_inter.setVisibility(View.VISIBLE);


                }


            }
        });


    }


    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getActiveNetworkInfo();
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){

            e.printStackTrace();
            return false;
        }
        return status;

    }


    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        // TODO: Create a new loader for the given URL
        return new BookLoader(this, URL_String);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {


        if (books == null || books.isEmpty())
        {
            Log.i("earthquake", "NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            TextView no_inter = (TextView) findViewById(R.id.no_internent);
            no_inter.setText("No Data found");
            no_inter.setVisibility(View.VISIBLE);
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setEmptyView(findViewById(R.id.empty_list_item));
            return;

        }
        updateUi(books);


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {



    }



    public void updateUi(ArrayList<Book> books) {

        if(books==null)
        {

            TextView no_inter = (TextView) findViewById(R.id.no_internent);
            no_inter.setText("No Data found");
            no_inter.setVisibility(View.VISIBLE);
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setEmptyView(findViewById(R.id.empty_list_item));

        }
        else {



            adapter = new BookAdapter(MainActivity.this, books);
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(adapter);

        }


    }

}
