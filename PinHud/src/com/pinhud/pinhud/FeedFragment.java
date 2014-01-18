package com.pinhud.pinhud;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FeedFragment extends Fragment {

		private GridLayout layout;
		private RelativeLayout newCard;
		private RelativeLayout newerCard;
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        View view = inflater.inflate(R.layout.feed_layout, container,
	                false);
	        
	        layout = (GridLayout) view.findViewById(R.id.theLayout);
	        for (int i = 0; i < 3; i++){
	        	createCard();
	        }
	        
			
	        return view;
	    }
		public void createCard(){
			newCard = (RelativeLayout) View.inflate(this.getActivity(), R.layout.main_list_card, null);
	        newerCard = (RelativeLayout) View.inflate(this.getActivity(), R.layout.main_list_card, null);
			layout.addView(newCard);
	        layout.addView(newerCard);
		}
}
