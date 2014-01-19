package com.pinhud.pinhud;

import org.json.JSONObject;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private JSONObject jResponse;
	int arraySize;
	String[] desc;
	String[] urls;
	int[] numRepin;

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


				final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				getUser = username.getText().toString();
				getName = name.getText().toString();
				savePreferences("stored", true);
				savePreferences("User", getUser);
				savePreferences("Name", getName);
				
				((MainActivity)getActivity()).generateJson(getUser, getName);
				Fragment fragment = new FeedFragment();
				//asyncCheck(getUser);
				FragmentTransaction transaction = thisThing.getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, fragment, "second");
				transaction.addToBackStack(null);
				transaction.commit();


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


}
