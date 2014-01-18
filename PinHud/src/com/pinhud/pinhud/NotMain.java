//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.UnknownHostException;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.parse.Parse;
//import com.parse.ParseObject;
//import com.parse.ParseUser;
//
//public class Print extends Activity {
//
//	TextView name, category;
//	String id;
//	String title;
//	LinearLayout pLayout;
//	Handler handler = new Handler();
//	String response;
//	Socket socket = null;
//	DataOutputStream dataOutputStream = null;
//	DataInputStream dataInputStream = null;
//	String temp, users, size;
//	SharedPreferences prefs;
//	boolean go = true;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.print);
//		Parse.initialize(getApplicationContext(), "CrYOY2CDijkZo4kz9aWo9wCrfQWjQhww4O3PbHXK", "7cb3TIGp39gw3T9oM2X9aW128bQbVNvrEg2mXL3M");
//		prefs = getSharedPreferences("com.austinn.hamster", Context.MODE_PRIVATE);
//
//		id = getIntent().getExtras().getString("ID");
//		title = getIntent().getExtras().getString("Title");
//
//		pLayout = (LinearLayout)findViewById(R.id.pLayout);
//		category = (TextView)findViewById(R.id.category);
//		name = (TextView)findViewById(R.id.name);
//
//		temp = MainActivity.client.likesByThing(id);
//		users = MainActivity.client.user("me");
//
//		category.setText(getJSONInfo(MainActivity.client.categoryByThing(id),"name"));
//		name.setText(title);
//
//		populate(id); //populate 
//
//	} //end onCreate
//
//	private String getJSONInfo(String json, String key) {
//		String value = null;
//		try {
//			JSONArray jsonArray = new JSONArray(json);
//			for (int i = 0; i < jsonArray.length(); i++) {
//				final org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
//				value = jsonObject.getString(key);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return value;
//	}
//
//	private Bitmap downloadImage(String url) {
//		Bitmap bitmap = null;
//		InputStream stream = null;
//		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//		bmOptions.inSampleSize = 1;
//
//		try {
//			stream = getHttpConnection(url);
//			bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
//			stream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return bitmap;
//	}
//
//	private InputStream getHttpConnection(String urlString) throws IOException {
//		InputStream stream = null;
//		URL url = new URL(urlString);
//		URLConnection connection = url.openConnection();
//		try {
//			HttpURLConnection httpConnection = (HttpURLConnection) connection;
//			httpConnection.setRequestMethod("GET");
//			httpConnection.connect();
//			if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//				stream = httpConnection.getInputStream();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return stream;
//	}
//
//	/**
//	 * SENDS COMMAND TO SERVER
//	 */
//	protected void sendCommand(String command) {
//		try {
//
//			String port = (prefs.getString("port_number", ""));
//			int port_number=0;
//			if(port.equals("")) {
//				port_number=0;
//			} else {
//				port_number = Integer.parseInt(port);
//			}
//
//			socket = new Socket(prefs.getString("ip_address", ""), port_number); 
//
//			dataOutputStream = new DataOutputStream(socket.getOutputStream());
//			dataInputStream = new DataInputStream(socket.getInputStream());
//			dataOutputStream.writeChars(command);
//			Log.i("command  --------------------------", command);
//
//
//		} catch (UnknownHostException e) {
//
//		} catch (IOException e) {
//			go = false;
//			Print.this.runOnUiThread(new Runnable() {
//
//				public void run() {
//					Toast.makeText(Print.this, "Please Configure Your Server Settings!", Toast.LENGTH_LONG).show();
//
//				}
//			});
//
//		}
//		finally{
//			if (socket != null){
//				try {
//					socket.close();
//				} catch (IOException e) {
//				}
//			}
//			if (dataOutputStream != null){
//				try {
//					dataOutputStream.close();
//				} catch (IOException e) {
//					//Toast.makeText(this, "IO Exception", Toast.LENGTH_SHORT).show();
//					e.printStackTrace();
//				}
//			}
//			if (dataInputStream != null){
//				try {
//					dataInputStream.close();
//				} catch (IOException e) {
//					//Toast.makeText(this, "IO Exception", Toast.LENGTH_SHORT).show();
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
////	public void populate(String ref) {
////
////		try{
////			size = MainActivity.client.filesByThing(ref); //get JSONArray of all files
////			JSONArray jsonArray = new JSONArray(size);  //create the array
////
////			Log.i("SIZE", jsonArray.length()+"");
////
////			//for all the files
////			for(int i = 0; i < jsonArray.length(); i++) {
////				final org.json.JSONObject jsonObject = jsonArray.getJSONObject(i); //get the file
////
////				//create linear layout
////				LinearLayout card = new LinearLayout(Print.this);
////				card.setOrientation(LinearLayout.HORIZONTAL);
////
////				//create image
////				ImageView preview = new ImageView(Print.this);
////				final Bitmap bmp = downloadImage(jsonObject.getString("thumbnail"));
////				preview.setImageBitmap(bmp);
////
////				//create print button
////				final Button print = new Button(Print.this);
////				print.setText("Print " + jsonObject.getString("name"));
////				print.setTag(jsonObject.getString("id"));
////
////				/**
////				 * Click print
////				 * sends command to server
////				 * adds object to your history
////				 */
////				print.setOnClickListener(new OnClickListener() {
////					@Override
////					public void onClick(View v) {
////
////						new Thread() {
////							public void run() {
////								try {
////									sendCommand(jsonObject.getString("url"));
////									
////								} catch (JSONException e) {
////									e.printStackTrace();
////								}
////							}
////						}.start();
////
////
////						Toast.makeText(Print.this, "Now Printing " + title, Toast.LENGTH_SHORT).show();
////						Intent intent = new Intent(Print.this, Browse.class);
////						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////						startActivity(intent);
////						finish();
////
////					}
////				}); //end 
////
////				if(jsonObject.getString("name").endsWith(".stl")) {
////					//add image and button to linear layout
////					card.addView(preview);
////					card.addView(print);
////
////					//add linear layout to pLayout
////					pLayout.addView(card);
////				}
////			}
////		} catch(JSONException e1) {
////			Toast.makeText(getApplicationContext(), "No files found", Toast.LENGTH_SHORT).show();
////			Log.w("ERROR", e1.getMessage().toString());
////		}
////	}
//}