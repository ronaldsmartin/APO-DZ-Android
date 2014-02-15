package org.upennapo.app;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DirectoryFragment extends Fragment{
	
	private List<Brother> directoryList;
	
	public DirectoryFragment() {
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_directory, container, false);
		
		//AsyncBrotherLoader loader = new AsyncBrotherLoader();
		//loader.execute("URL");
		
		
		return view;
	}
	
	public class AsyncBrotherLoader extends AsyncTask<String, Void, List<Brother>> {

		
		
	    @Override
	    protected void onPostExecute(List<Brother> result) {            
	        super.onPostExecute(result);
	        directoryList = result;
	    }

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();    
	    }

	    @Override
	    protected List<Brother> doInBackground(String... params) {
	        List<Brother> result = new ArrayList<Brother>();

	        try {
	            URL u = new URL(params[0]);

	            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
	            conn.setRequestMethod("GET");

	            conn.connect();
	            InputStream is = conn.getInputStream();

	            // Read the stream
	            byte[] b = new byte[1024];
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();

	            while ( is.read(b) != -1)
	                baos.write(b);

	            String JSONResp = new String(baos.toByteArray());

	            JSONArray arr = new JSONArray(JSONResp);
	            for (int i=0; i < arr.length(); i++) {
	                result.add(convertBrother(arr.getJSONObject(i)));
	            }

	            return result;
	        } catch(Throwable t) {
	            t.printStackTrace();
	        }
	        return null;
	    }
	    
	    private Brother convertBrother(JSONObject obj) throws JSONException {
	    	Brother brother = new Brother();
	    	
	    	return brother;
	    }

	}
	
}

