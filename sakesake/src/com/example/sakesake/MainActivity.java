package com.example.sakesake;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.example.sakesake.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

// 最寄り駅検索ボタン
class SearchNearestStationButtonActivity extends Activity implements android.view.View.OnClickListener
{
	private MainActivity mainWindow;
	
	public SearchNearestStationButtonActivity(MainActivity mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onClick(View v)
	{
		this.mainWindow.searchNearestStation();
	}
}

// 結果をクリア
class ClearResultButtonActivity extends Activity implements android.view.View.OnClickListener
{
	private MainActivity mainWindow;
	
	public ClearResultButtonActivity(MainActivity mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

	@Override
	public void onClick(View v)
	{
		this.mainWindow.clearResult();
	}
}


public class MainActivity extends FragmentActivity
{
    GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    public static MarkerOptions options;
    public ProgressDialog progressDialog;
    private Boolean isDisplayedResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(isDisplayedResult == true)
        {
        	return;
        }

        SearchNearestStationButtonActivity clickedSearch = new SearchNearestStationButtonActivity(this);           	 
        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(clickedSearch);

        ClearResultButtonActivity clickedClear = new ClearResultButtonActivity(this);           	 
        Button btnClear = (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(clickedClear);

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("検索中...");
		progressDialog.hide();
        
        markerPoints = new ArrayList<LatLng>();

        SupportMapFragment mapfragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        this.googleMap = mapfragment.getMap();

        //初期位置 皇居
        LatLng location = new LatLng(35.685175,139.752799); 
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11));
        this.googleMap.setMyLocationEnabled(true);

        this.googleMap.setOnMapClickListener(new OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
                markerPoints.add(point);

                options = new MarkerOptions();
                options.position(point);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                googleMap.addMarker(options);

            }
        } );
    }

    public void searchNearestStation()
    {
    	isDisplayedResult = true;
    	
    	progressDialog.show();
    	
    	LatLng center = getCenterLocation(this.markerPoints);
    	String url = "http://express.heartrails.com/api/json?method=getStations&x=" + center.longitude + "&y=" + center.latitude;
    	DownloadTask task = new DownloadTask();
    	task.execute(url);
    }
    
    public void clearResult()
    {
		markerPoints.clear();
		googleMap.clear();
    	isDisplayedResult = false;
    }

	// 緯度経度の平均値により中心位置(緯度経度位置)をもとめる
	private LatLng getCenterLocation(ArrayList<LatLng> markerPoints)
	{
		double lat_sum = 0.0;
		double lng_sum = 0.0;
		double lat_c = 0.0;
		double lng_c = 0.0;
		
		int point_num = markerPoints.size();

		for(LatLng l : markerPoints)
		{
			lat_sum += l.latitude;
			lng_sum += l.longitude;
		}

		lat_c = lat_sum / point_num;
		lng_c = lng_sum / point_num;
		LatLng center = new LatLng(lat_c, lng_c);
		return center;
	}
	
    private String downloadUrl(String strUrl) throws IOException
    {
    	String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            
            String line = "";
            while( ( line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        }
        catch(Exception e)
        {
            Log.d("sakesake", e.toString());
        }
        finally
        {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
    
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url)
        {
			String data = "";
			try
			{
				data = downloadUrl(url[0]);
			}
			catch(Exception e)
			{
				Log.d("sakesake", e.toString());
			}
			return data;
        }

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			Gson gson = new Gson();
			HeartRailsExpress obj = gson.fromJson(result, HeartRailsExpress.class);
		
			for(Station station : obj.response.station)
			{
				LatLng location = new LatLng(station.y, station.x);
			
				MarkerOptions options = new MarkerOptions();
				options.position(location);
				options.title(station.name);
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				googleMap.addMarker(options);
			}
	    	progressDialog.hide();
		}
    }
}
