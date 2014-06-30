/**
 *
 */
package org.upennapo.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ReadJSON provides a means to download and parse information from the Google Docs backend.
 *
 * @author Ronald Martin
 *         Using tutorial from http://www.vogella.com/tutorials/AndroidJSON/article.html
 */
public class ReadJSON {

    /**
     * Downloads brotherhood requirement data for the requested person.
     *
     * @param urlString from which to download JSON data
     * @param firstName of brother we are requesting
     * @param lastName  of brother we are requesting
     * @return User object representing the requested person's brotherhood status or null if the
     * person is not found.
     */
    public static User getBrotherData(String urlString, String firstName, String lastName,
                                      Context context, boolean forceDownload) {
        // If we cannot retrieve or find the requested person's data, return null.
        User user = null;
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // If the status isn't cached or we are refreshing, download and parse the brother status
        // data, then save the corresponding JSON string in SharedPrefs.
        if (!prefs.contains(BrotherStatusFragment.STORAGE_KEY) || forceDownload) {

            String jsonString = downloadJsonData(urlString);
            for (User brother : parseSpreadsheetJson(jsonString)) {
                if (brother.Last_Name.equalsIgnoreCase(lastName) &&
                        (brother.First_Name.equalsIgnoreCase(firstName))) {
                    user = brother;

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(BrotherStatusFragment.STORAGE_KEY, gson.toJson(user));
                    editor.apply();
                    break;
                }
            }

        } else {
            // Otherwise, retrieve the existing user JSON object and parse it.

            String userJson =
                    prefs.getString(BrotherStatusFragment.STORAGE_KEY, null);
            user = gson.fromJson(userJson, User.class);

        }

        return user;
    }

    /**
     * Parses a JSON string of brotherhood data into an array of User objects
     *
     * @param jsonData - String of JSON formatted brotherhood data
     * @return parsed User[] of brotherhood data for all brothers
     */
    private static User[] parseSpreadsheetJson(String jsonData) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject map = parser.parse(jsonData).getAsJsonObject();
        JsonArray brotherJsonArray = map.getAsJsonArray("records");

        return gson.fromJson(brotherJsonArray, User[].class);
    }

    /**
     * Retrieves and parses directory data from the specified source, returning it as an array of
     * Brother objects.
     *
     * @param urlString    from which to download JSON data
     * @param sheetKey     the sheet whose data to retrieve
     * @param context      the parent activity, used to access shared preferences
     * @param isRefreshing whether or not this is a network-refresh request or a standard retrieval
     * @return the requested directory data as an array of brothers
     */
    public static Brother[] getDirectoryData(String urlString, String sheetKey, Context context,
                                             boolean isRefreshing) {
        // If the JSON data is stored locally, pull it from preferences.
        // Otherwise, download it from the Internet and cache it.
        SharedPreferences prefs =
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

        String jsonString = isRefreshing ? downloadJsonData(urlString) :
                prefs.getString(sheetKey, downloadJsonData(urlString));

        if (!prefs.contains(sheetKey) || isRefreshing) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(sheetKey, jsonString);
            editor.apply();
        }

        return parseDirectoryJson(jsonString, sheetKey);
    }

    /**
     * Uses the GSON library to parse a raw JSON string as an array of Brother objects.
     *
     * @param jsonData - raw JSON-formatted String of objects to parse
     * @param sheetKey - spreadsheet key for information retrieval
     * @return the parsed array of Brother objects
     */
    private static Brother[] parseDirectoryJson(String jsonData, String sheetKey) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject map = parser.parse(jsonData).getAsJsonObject();
        JsonArray directoryJsonArray = map.getAsJsonArray(sheetKey);

        return gson.fromJson(directoryJsonArray, Brother[].class);
    }

    /**
     * Initiates an HTTP GET request at the specified URL to download a String of JSON data.
     *
     * @param urlString from which to download JSON
     * @return the JSON-formatted String from the URL at 'urlString'
     */
    private static String downloadJsonData(String urlString) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(urlString);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                reader.close();
                content.close();
            } else {
                Log.d("ReadJSON", "HttpResponse code error");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    /**
     * Checks if there is an internet connection.
     *
     * @param context to access ConnectivityManager
     * @return true iff the network is ready
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
