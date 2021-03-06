/**
 *
 */
package org.upennapo.app.model;

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
import org.upennapo.app.R;
import org.upennapo.app.fragment.BrotherStatusFragment;
import org.upennapo.app.fragment.DirectoryFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * DataManager provides a means to download and parse information from the Google Sheets backend.
 *
 * @author Ronald Martin
 *         Using tutorial from http://www.vogella.com/tutorials/AndroidJSON/article.html
 */
public class DataManager {

    private static final String TAG = "DataManager";
    private static final int DATA_EXPIRATION_DAYS = 7;

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
                context.getSharedPreferences(context.getString(R.string.app_global_storage_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // Make sure data isn't stale.
        long currentTime = System.currentTimeMillis();
        long lastUpdated = prefs.getLong(BrotherStatusFragment.LAST_UPDATED, 0);

        boolean dataIsStale = isDataExpired(currentTime, lastUpdated);

        // If the status isn't cached or we are refreshing, download and parse the brother status
        // data, then save the corresponding JSON string in SharedPrefs.
        if ((!prefs.contains(BrotherStatusFragment.STORAGE_KEY) || forceDownload || dataIsStale)
                && isNetworkAvailable(context)) {
            String jsonString = downloadJsonData(urlString);
            user = findUserInArray(context, firstName, lastName, parseSpreadsheetJson(jsonString));

            if (user != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(BrotherStatusFragment.STORAGE_KEY, gson.toJson(user));
                editor.putLong(BrotherStatusFragment.LAST_UPDATED, currentTime);
                editor.apply();
            }
        } else {
            // Otherwise, retrieve the existing user JSON object and parse it.
            user = retrieveUser(prefs, gson);
        }

        return user;
    }

    /**
     * Check if data is stale based on timestamps. Data is stale if a week has passed since the last
     * update.
     *
     * @param currentTime The current Unix time in ms
     * @param lastUpdated The Unix time of the last update in ms
     * @return {@code true} if the a week has passed between the last update and the current time
     */
    private static boolean isDataExpired(long currentTime, long lastUpdated) {
        long delta = TimeUnit.MILLISECONDS.toDays(currentTime - lastUpdated);
        return delta >= DATA_EXPIRATION_DAYS;
    }

    /**
     * Iterate over the array of Users, returning the User with matching firstName and lastName and
     * saving the row number
     *
     * @param firstName of person to find
     * @param lastName  of person to find
     * @param users     array of Users in which to find this person
     * @return the User with name "firstName lastName" or null if no such person exists
     */
    private static User findUserInArray(Context context, String firstName, String lastName,
                                        User[] users) {
        for (int i = 0; i < users.length; ++i) {
            User user = users[i];
            if (user.Last_Name.equalsIgnoreCase(lastName) &&
                    (user.First_Name.equalsIgnoreCase(firstName))) {
                SharedPreferences.Editor editor =
                        context.getSharedPreferences(context.getString(R.string.app_global_storage_key),
                                Context.MODE_PRIVATE)
                                .edit();
                editor.putInt(BrotherStatusFragment.ROW_KEY, i);
                editor.apply();

                return user;
            }
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
                context.getSharedPreferences(context.getString(R.string.app_global_storage_key), Context.MODE_PRIVATE);

        // Attempt to retrieve the string from memory.
        String jsonString = prefs.getString(sheetKey, null);

        // Make sure data isn't stale.
        String lastUpdatedKey = sheetKey + DirectoryFragment.LAST_UPDATED;
        long currentTime = System.currentTimeMillis();
        long lastUpdated = prefs.getLong(lastUpdatedKey, 0);
        boolean dataIsStale = isDataExpired(currentTime, lastUpdated);

        if (jsonString == null || forceDownload || dataIsStale && isNetworkAvailable(context)) {
            jsonString = downloadJsonData(urlString);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(sheetKey, jsonString);
            editor.putLong(lastUpdatedKey, currentTime);
            editor.apply();
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
     * Get the URL for the webpage for a specific row on the Brother spreadsheet.
     *
     * @param context used to access string resources
     * @param sheetId gid for the sheet to get
     * @return the URL for the user's row page on sheet with sheetId
     */
    public static String userSpreadsheetRowUrl(Context context, int sheetId) {
        final String baseUrl = context.getString(R.string.spreadsheet_row_script),
                sheetKey = context.getString(R.string.spreadsheet_id);

        int rowNum =
                context.getSharedPreferences(context.getString(R.string.app_global_storage_key),
                        Context.MODE_PRIVATE)
                        .getInt(BrotherStatusFragment.ROW_KEY, 0);

        // On the membership spreadsheet, we need to go one extra row down.
        if (sheetId == context.getResources().getInteger(R.integer.spreadsheet_sheet_num_membership))
            ++rowNum;

        return spreadsheetRowUrl(baseUrl, sheetKey, rowNum, sheetId);
    }

    /**
     * Get the URL for the webpage for a specific row on a Google spreadsheet.
     *
     * @param rowNum which row to get (zero-indexed by first non-header row)
     * @param gid    id for the sheet to get
     * @return the URL for the page for rowNum on the spreadsheet with sheetKey and sheet gid
     */
    public static String spreadsheetRowUrl(final String baseUrl, final String sheetKey,
                                           final int rowNum, final int gid) {
        return String.format(baseUrl, rowNum, sheetKey, gid);
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
