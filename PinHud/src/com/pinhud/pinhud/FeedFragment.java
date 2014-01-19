package com.pinhud.pinhud;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedFragment extends Fragment {

	private GridLayout layout;
	private RelativeLayout newCard;
	private TextView title;
	private TextView repins;
	private ImageView pic;
	private View view;
	private JSONObject response;
	int arraySize;
	String[] desc;
	String[] urls;
	int[] numRepin;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.feed_layout, container,
				false);
		parseJson();
		layout = (GridLayout) view.findViewById(R.id.theLayout);

		for (int i = 0; i < 3; i++){
			try {
				createCard();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return view;
	}
	public void createCard() throws InterruptedException, ExecutionException{
		title = (TextView)this.getActivity().getLayoutInflater().inflate(R.layout.item_title, null);
		repins = (TextView)this.getActivity().getLayoutInflater().inflate(R.layout.item_descrip, null);
		pic = (ImageView)this.getActivity().getLayoutInflater().inflate(R.layout.item_image, null);
		newCard = (RelativeLayout) View.inflate(this.getActivity(), R.layout.main_list_card, null);
		title.setText("Best image ever!");
		repins.setText("Repins: 332");
		getImageTask image = new getImageTask();
		Bitmap bit = image.execute("http://media-cache-ak0.pinimg.com/1200x/43/0f/bd/430fbdb32d6f0c1379e81d72262ac02f.jpg").get();
		pic.setImageBitmap(Bitmap.createScaledBitmap(bit, 450, 600, false));
		newCard.addView(pic);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT);

		p.addRule(RelativeLayout.BELOW, R.id.item_image);
		title.setLayoutParams(p);
		newCard.addView(title);
		RelativeLayout.LayoutParams o = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT);

		o.addRule(RelativeLayout.BELOW, R.id.item_title);
		o.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		repins.setLayoutParams(o);
		newCard.addView(repins);
		layout.addView(newCard);
	}

	private class getImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			String url = params[0];
			Bitmap bitmap = null;
			try {
				  bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
				} catch (MalformedURLException e) {
				  e.printStackTrace();
				} catch (IOException e) {
				  e.printStackTrace();
				}
			
			return bitmap;
		}
	}
	
	private void parseJson(){

        MainActivity getter = new MainActivity();
        response = getter.getResponse();
        JSONArray results = null;
        //System.out.println(response.toString());
        try {
			results = (JSONArray) response.get("results");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        arraySize = results.length();
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
				desc[i] = (String) results.get(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
}

