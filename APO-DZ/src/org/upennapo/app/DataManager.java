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
 * DataManager provides a means to download and parse information from the Google Sheets backend.
 *
 * @author Ronald Martin
 *         Using tutorial from http://www.vogella.com/tutorials/AndroidJSON/article.html
 */
public class DataManager {

    private static final String TAG = "DataManager";

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
        User user;
        SharedPreferences prefs =
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // If the status isn't cached or we are refreshing, download and parse the brother status
        // data, then save the corresponding JSON string in SharedPrefs.
        if ((!prefs.contains(BrotherStatusFragment.STORAGE_KEY) || forceDownload)
                && isNetworkAvailable(context)) {
            String jsonString = downloadJsonData(urlString);
            user = findUserInArray(firstName, lastName, parseSpreadsheetJson(jsonString));

            if (user != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(BrotherStatusFragment.STORAGE_KEY, gson.toJson(user));
                editor.apply();
            }
        } else {
            // Otherwise, retrieve the existing user JSON object and parse it.
            user = retrieveUser(prefs, gson);
        }

        return user;
    }

    /**
     * Iterate over the array of Users, returning the User with matching firstName and lastName
     *
     * @param firstName of person to find
     * @param lastName  of person to find
     * @param users     array of Users in which to find this person
     * @return the User with name "firstName lastName" or null if no such person exists
     */
    private static User findUserInArray(String firstName, String lastName, User[] users) {
        for (User user : users) {
            if (user.Last_Name.equalsIgnoreCase(lastName) &&
                    (user.First_Name.equalsIgnoreCase(firstName)))
                return user;
        }
        return null;
    }

    /**
     * Attempt to find retrieve a JSON-formatted User person from SharedPreferences
     *
     * @param prefs in which to search for the person
     * @param gson  JSON parser for retrieved data
     * @return the requested User object or null if none exists
     */
    private static User retrieveUser(SharedPreferences prefs, Gson gson) {
        String userJson =
                prefs.getString(BrotherStatusFragment.STORAGE_KEY, null);
        return userJson == null ? null : gson.fromJson(userJson, User.class);
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
     * @param sheetKey      the sheet whose data to retrieve
     * @param context       the parent activity, used to access shared preferences
     * @param forceDownload whether or not to force a download from the internet
     * @return the requested directory data as an array of brothers
     */
    public static Brother[] getDirectoryData(String sheetKey, Context context,
                                             boolean forceDownload) {
        if (context == null) return null;

        final String url = context.getString(R.string.directory_script) + sheetKey;
        final String jsonString = loadJson(url, sheetKey, context, forceDownload);
        return parseDirectoryJson(jsonString, sheetKey);
    }

    /**
     * Retrieves directory data from the specified source, returning it as a String of JSON objects
     * and caching it.
     *
     * @param urlString     from which to download JSON data
     * @param sheetKey      the sheet whose data to retrieve
     * @param context       the parent activity, used to access shared preferences
     * @param forceDownload whether or not this is a network-refresh request or a standard retrieval
     * @return the requested directory data as a JSON-formatted String or null if we could not load
     */
    private static String loadJson(String urlString, String sheetKey, Context context,
                                   boolean forceDownload) {
        // If the JSON data is stored locally, pull it from preferences.
        // Otherwise, download it from the Internet and cache it.
        SharedPreferences prefs =
                context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);

        // Attempt to retrieve the string from memory.
        String jsonString = prefs.getString(sheetKey, null);

        if (jsonString == null || forceDownload) {
            if (isNetworkAvailable(context)) {
                jsonString = downloadJsonData(urlString);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(sheetKey, jsonString);
                editor.apply();
            }
        }

        return jsonString;
    }

    /**
     * Uses the GSON library to parse a raw JSON string as an array of Brother objects.
     *
     * @param jsonData - raw JSON-formatted String of objects to parse
     * @param sheetKey - spreadsheet key for information retrieval
     * @return the parsed array of Brother objects or null if any of the arguments is null
     */
    private static Brother[] parseDirectoryJson(String jsonData, String sheetKey) {
        if (jsonData == null || sheetKey == null) return null;

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
                Log.d(TAG, "HttpResponse code error");
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
