/*
 * This file is part of PickMeal.
 * 
 * PickMeal is Copyright (C) 2010, 2011 Clément BIZEAU, Pierre LAINE
 * 
 * PickMeal is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * PickMeal is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * PickMeal. If not, see <http://www.gnu.org/licenses/>.
 */
package com.pickmeal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * used to display button with different kind of food
 * 
 */
public class AllCategories extends Activity implements OnClickListener {
	private Button Viandes;
	private Button Poissons;
	private Button Legumes;
	private Button Fruits;
	private Button Boissons;
	private Button Laitages;
	private Button Autres;
	private Button Epices;
	private Button Bases;
	private Button toast;
	String tag;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// get button
		setContentView(R.layout.activity_allcategories);
		Viandes=(Button)findViewById(R.id.button1);
		Poissons=(Button)findViewById(R.id.button2);
		Legumes=(Button)findViewById(R.id.button3);
		Fruits=(Button)findViewById(R.id.button4);
		Boissons=(Button)findViewById(R.id.button5);
		Laitages=(Button)findViewById(R.id.button6);
		Autres=(Button)findViewById(R.id.button7);
		Epices=(Button)findViewById(R.id.button8);
		Bases=(Button)findViewById(R.id.button9);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_allcategories);
		// btnBack
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent home=new Intent(AllCategories.this,Pickmeal.class);
				startActivity(home);
			}
		});
		// set listener and id of all the button
		Viandes.setOnClickListener(this);
		Viandes.setId(1);
		Viandes.setTag(R.string.meat);
		Poissons.setOnClickListener(this);
		Poissons.setId(2);
		Poissons.setTag(R.string.fish);
		Legumes.setOnClickListener(this);
		Legumes.setId(3);
		Legumes.setTag(R.string.vegetables);
		Fruits.setOnClickListener(this);
		Fruits.setId(4);
		Fruits.setTag(R.string.fruits);
		Boissons.setOnClickListener(this);
		Boissons.setId(5);
		Boissons.setTag(R.string.drinks);
		Laitages.setOnClickListener(this);
		Laitages.setId(6);
		Laitages.setTag(R.string.milk);
		Autres.setOnClickListener(this);
		Autres.setId(7);
		Autres.setTag(R.string.others);
		Epices.setOnClickListener(this);
		Epices.setId(8);
		Epices.setTag(R.string.spices);
		Bases.setOnClickListener(this);
		Bases.setId(9);
		Bases.setTag(R.string.basis);
	}
	@Override
	public void onClick(View v) {
		int i=v.getId();
		int id=(Integer)v.getTag();
		String s=getString(id);
		Intent intent=new Intent(AllCategories.this,Category.class);
		Bundle b=new Bundle();
		b.putString("source",s);
		b.putInt("reqDB",i);
		intent.putExtras(b);
		startActivity(intent);
	}
}
