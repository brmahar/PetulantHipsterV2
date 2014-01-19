package com.pinhud.pinhud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
	
	private Button login;
	private String getUser;
	private String getName;
	private EditText username;
	private EditText name;
	private LoginFragment thisThing = this;
	private JSONObject request;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.login_layout, container,
                false);
        login = (Button) view.findViewById(R.id.login);
        username = (EditText)view.findViewById(R.id.email);
        name = (EditText)view.findViewById(R.id.password);
        
        login.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(view.getContext()).setTitle("Notice")
				.setMessage("Please use a Board named 'stuff-to-buy'")
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
						getUser = username.getText().toString();
						getName = name.getText().toString();
						savePreferences("stored", true);
						savePreferences("User", getUser);
						savePreferences("Name", getName);
						Fragment fragment = new FeedFragment();
						//asyncCheck(getUser);
					    FragmentTransaction transaction = thisThing.getFragmentManager().beginTransaction();
					    transaction.replace(R.id.fragment_container, fragment, "second");
					    transaction.addToBackStack(null);
					    transaction.commit();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						// do nothing
					}
				})
				.show();

			}

		});
        
        return view;
    }
	
	private void savePreferences(String key, String value){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		Editor edit = shared.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private void savePreferences(String key, boolean value){
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

		Editor edit = shared.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
//	
//	private class AuthRequestAsync extends AsyncTask<String, Void, Integer>{
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			String user = params[0];
//			int authCheck = 0;
//			// Creating HTTP client
//			HttpClient httpClient = new DefaultHttpClient();
//			// Creating HTTP Post TODO
//			HttpPost httpPost = new HttpPost("");
//			JSONObject passData = new JSONObject();
//			try {
//				passData.put("Username", user);
//			} catch (JSONException e1) {
//				e1.printStackTrace();
//			}
//
//			String message = passData.toString();
//			// Url Encoding the POST parameters
//			try {
//				httpPost.setEntity(new StringEntity(message, "UTF8"));
//				httpPost.setHeader("Content-type", "application/json");
//			} catch (UnsupportedEncodingException e) {
//				// writing error to Log
//				e.printStackTrace();
//			}
//
//			// Making HTTP Request
//			try {
//				HttpResponse response = httpClient.execute(httpPost);
//				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//				StringBuilder builder = new StringBuilder();
//				for (String line = null; (line = reader.readLine()) != null;) {
//					builder.append(line).append("\n");
//				}
//				//JSONTokener tokener = new JSONTokener(builder.toString());
//				//JSONArray finalResult = new JSONArray(tokener);
//				request = new JSONObject(builder.toString());
//				// writing response to log
//				Log.d("Http Response:", response.toString());
//			} catch (ClientProtocolException e) {
//				// writing exception to log
//				e.printStackTrace();
//			} catch (IOException e) {
//				// writing exception to log
//				e.printStackTrace();
//
//			} catch (JSONException e) {
//				authCheck = 1;
//				e.printStackTrace();
//			}
//			return authCheck;
//		}
//
//
//	}
//	
//	private void asyncCheck(String pUser) {
//		AuthRequestAsync theRun = new AuthRequestAsync();
//		int result = 0;
//		try {
//			result = theRun.execute(pUser).get();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(result == 0){
//			for(int i = 0; i < request.length(); i++){
//				
//			}
//		}
//		
//	}

}
