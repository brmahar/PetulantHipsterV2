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


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.feed_layout, container,
				false);

		layout = (GridLayout) view.findViewById(R.id.theLayout);

		for (int i = 0; i < 3; i++){
			createCard();
		}


		return view;
	}
	public void createCard(){
		title = (TextView)this.getActivity().getLayoutInflater().inflate(R.layout.item_title, null);
		repins = (TextView)this.getActivity().getLayoutInflater().inflate(R.layout.item_descrip, null);
		newCard = (RelativeLayout) View.inflate(this.getActivity(), R.layout.main_list_card, null);
		title.setText("An Item");
		repins.setText("This is a fucking item. Deal with it.");
		newCard.addView(title);
		newCard.addView(repins);
		layout.addView(newCard);
	}
}
