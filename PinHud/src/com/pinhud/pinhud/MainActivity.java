package com.pinhud.pinhud;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	int mCurCheckPosition = 0;
	private NfcAdapter mNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	private String storeResult;
	private JSONObject request;
	private JSONObject jResponse;
	private String storage;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	String first;
	String second;
	boolean go = true;
	Socket socket = null;
	int arraySize;
	String[] desc;
	String[] urls;
	int[] numRepin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		request = new JSONObject();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter == null){
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (!mNfcAdapter.isEnabled()){
			new AlertDialog.Builder(this).setTitle("NFC Disabled")
			.setMessage("Please enable your NFC to use this app.")
			.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					// do nothing
				}
			})
			.show();

		}else{
			SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
			String pUser = shared.getString("User", "");
			String storeName = shared.getString("StoreName", "");
			String storeBoard = shared.getString("StoreBoard", "");
			String board = shared.getString("Name", "");

			System.out.println(storeName);
			boolean check = shared.getBoolean("stored", false);
			if(storeName != null && storeBoard != null && check){
				FeedFragment fFrag = new FeedFragment();
				fFrag.setArguments(getIntent().getExtras());
				try {
					request.put("user_pinterest_name", pUser);
					//request.put("StoreName", storeName);
					//request.put("StoreBoard",storeBoard);
					request.put("user_board_name", board);
					request.put("company_pinterest_name", storeName);
					request.put("company_board_name", storeBoard);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SharedPreferences remove = PreferenceManager.getDefaultSharedPreferences(this);
				populate("test");
				parseJson();
				Intent i = new Intent();
				i.putExtra("descriptor", desc);
				i.putExtra("urls", urls);
				i.putExtra("repins", numRepin);
				Editor edit = remove.edit();
				edit.putString("Store", "");
				edit.commit();
				getFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, fFrag).commit();
				return;
			}
			
		}
		handleFragments(getIntent());
	}

	private void handleFragments(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
		//loadSavedPreferences();

	}

	/**
	 * Background task for reading the data. Do not block the UI thread while reading.
	 *
	 * @author Ralf Wondratschek
	 *
	 */
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}
			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}
			return null;
		}
		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			byte[] payload = record.getPayload();
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			int languageCodeLength = payload[0] & 0063;

			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				int middle = result.indexOf(",");
				first = result.substring(0, middle);
				second = result.substring(middle+2);
				savePreferences(first, second);
				loadSavedPreferences();
				//savePreferences(result);
				//System.out.println("Read content: " + result);
				//mTextView.setText("Read content: " + result);
			}
		}
	}

	private void savePreferences(String store, String storeB){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

		Editor edit = shared.edit();
		edit.clear();
		edit.putString("StoreName", store);
		edit.putString("StoreBoard", storeB);
		edit.commit();
	}

	private void loadSavedPreferences(){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		String pUser = shared.getString("User", "");
		String board = shared.getString("Name", "");

		boolean check = shared.getBoolean("stored", false);
		if(check){
			//asyncCheck(pUser);
			FeedFragment fFrag = new FeedFragment();
			fFrag.setArguments(getIntent().getExtras());
			try {
				request.put("user_pinterest_name", pUser);
				//request.put("StoreName", storeName);
				//request.put("StoreBoard",storeBoard);
				request.put("user_board_name", board);
				request.put("company_pinterest_name", first);
				request.put("company_board_name", second);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SharedPreferences remove = PreferenceManager.getDefaultSharedPreferences(this);
			populate("test");
			parseJson();
			Intent i = new Intent();
			i.putExtra("descriptor", desc);
			i.putExtra("urls", urls);
			i.putExtra("repins", numRepin);
			Editor edit = remove.edit();
			edit.putString("Store", "");
			edit.commit();
			getFragmentManager().beginTransaction()
			.add(R.id.fragment_container, fFrag).commit();
		}else{
			try {
				request.put("user_pinterest_name", pUser);
				//request.put("StoreName", storeName);
				//request.put("StoreBoard",storeBoard);
				request.put("user_board_name", board);
				request.put("company_pinterest_name", first);
				request.put("company_board_name", second);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			populate("test");
//			parseJson();
			LoginFragment lFrag = new LoginFragment();
			//lFrag.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction()
			.add(R.id.fragment_container, lFrag).commit();
		}
	}
	private Bitmap downloadImage(String url) {
		Bitmap bitmap = null;
		InputStream stream = null;
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;

		try {
			stream = getHttpConnection(url);
			bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	private InputStream getHttpConnection(String urlString) throws IOException {
		InputStream stream = null;
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		try {
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			httpConnection.setRequestMethod("GET");
			httpConnection.connect();
			if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				stream = httpConnection.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stream;
	}

	private class Send extends AsyncTask<String, Void, Integer> {


		@Override
		protected Integer doInBackground(String... params) {
			//String command = params[0];
			String user = params[0];
			Integer authCheck = 0;
			// Creating HTTP client
			HttpClient httpClient = new DefaultHttpClient();
			// Creating HTTP Post
			HttpPost httpPost = new HttpPost("http://kensmc.no-ip.org:4999/api");
			//auth.put("logonPassword", pass);

			String message = request.toString();
			// Url Encoding the POST parameters
			try {
				httpPost.setEntity(new StringEntity(message, "UTF8"));
				httpPost.setHeader("Content-type", "application/json");
			} catch (UnsupportedEncodingException e) {
				// writing error to Log
				e.printStackTrace();
			}

			// Making HTTP Request
			try {
				HttpResponse response = httpClient.execute(httpPost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
//				JSONTokener tokener = new JSONTokener(builder.toString());
//				JSONArray finalResult = new JSONArray("description");
				
				jResponse = new JSONObject(builder.toString());
				// writing response to log
				Log.d("Http Response:", response.toString());
			} catch (ClientProtocolException e) {
				// writing exception to log
				System.out.println("Test");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("TEST@");
				// writing exception to log
				e.printStackTrace();

			} catch (JSONException e) {
				System.out.println("kl;asdjg");
				authCheck = 1;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return authCheck;
		}

	}

	public void populate(String ref) {
		Send sender = new Send();
		JSONArray jsonArray = new JSONArray();  //create the array

		//Log.i("", request.toString()+"");


		int result = 0;
		try {
			result = sender.execute(request.toString()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(result == 0){
			System.out.println(result);
			//Object description = jResponse.get("description");
			//System.out.println(jResponse.toString());
		}
	}

	public JSONObject getResponse(){
		return jResponse;
	}

	protected void parseJson(){
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) jResponse.get("results");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        JSONArray results = null;
        JSONArray results1 = null;
        JSONArray results2 = null;
        System.out.println(jResponse.toString());
        try {
			results = obj.getJSONArray("image_large_urls");
			results1 = obj.getJSONArray("descriptions");
			results2 = obj.getJSONArray("repins");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        arraySize = results.length();
        System.out.println(arraySize);
        desc = new String[results.length()];
        urls = new String[results.length()];
        numRepin = new int[results.length()];
        try {
			System.out.println(results.get(0));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        for(int i = 0; i < results.length(); i++){
        	try {
				urls[i] = (String) results.get(i);
				numRepin[i] =  (Integer) results2.get(i);
				desc[i] = (String) results1.get(i) ;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
	}
	
	public void generateJson(String uName, String bName){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		String pUser = shared.getString("User", "");
		String board = shared.getString("Name", "");
		try {
			request.put("user_pinterest_name", pUser);
			//request.put("StoreName", storeName);
			//request.put("StoreBoard",storeBoard);
			request.put("user_board_name", board);
			request.put("company_pinterest_name", uName);
			request.put("company_board_name", bName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		populate("test");
		parseJson();
		Intent i = new Intent();
		i.putExtra("descriptor", desc);
		i.putExtra("urls", urls);
		i.putExtra("repins", numRepin);
	}

}
