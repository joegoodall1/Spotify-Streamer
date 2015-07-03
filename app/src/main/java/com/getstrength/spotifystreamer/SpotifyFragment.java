package com.getstrength.spotifystreamer;

/**
 * Created by Joe on 16/06/15.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyFragment extends Fragment {

    ArrayAdapter<String> mSpotifyAdapter;

    public SpotifyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            inflater.inflate(R.menu.menu_main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // perform query here
                    return performQuery(query);
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    return performQuery(query);
                }
            });
             super.onCreateOptionsMenu(menu, inflater);
        }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_search) {
            FetchSpotifyTask spotifyTask = new FetchSpotifyTask();
            spotifyTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   private boolean performQuery(String searchQuery) {
       SpotifyApi api = new SpotifyApi();

       SpotifyService spotify = api.getService();

       spotify.searchArtists(searchQuery, new Callback<ArtistsPager>() {

           @Override
           public void success(ArtistsPager artistsPager, Response response) {
               SpotifyFragment.this.onSpotifyResults(artistsPager, response);
           }

           @Override
           public void failure(RetrofitError error) {
               Log.d("fail", error.toString());

           }
       });
       return true;
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        return rootView;
    }

    private void onSpotifyResults(ArtistsPager artistsPager, Response response) {

        Log.d("success", artistsPager.toString());
        List<String> artistsName = new ArrayList<>();
        for(int i =0; i<artistsPager.artists.items.size(); i++) {
            Artist artist = artistsPager.artists.items.get(i);
            artistsName.add(artist.name);
        }

        mSpotifyAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item, // The name of the layout ID.
                        R.id.list_artists, // The ID of the textview to populate.
                        artistsName);

        // Get a reference to the ListView, and attach this adapter to it.
        final ListView listView = (ListView) getView().findViewById(R.id.listView);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(mSpotifyAdapter);
            }
        });
    }

    public class FetchSpotifyTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchSpotifyTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            if (result != null) {
                mSpotifyAdapter.clear();
                for(String dayForecastStr : result) {
                    mSpotifyAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}

