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

import java.util.Vector;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.data.Product;
import com.data.RecipesContent;

/**
 * show a recipe with the howto and the products needed
 */
public class Recipe extends Activity {
	private int idrecip;
	private RecipesContent recip;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_recip);
		// get shared data
		Bundle b=getIntent().getExtras();
		idrecip=b.getInt("idrecip",0);
		ShareData data=(ShareData)getApplication();
		recip=data.db.requestRecip(idrecip);
		// btnBack
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//btnAction
		Button btnAction=(Button)findViewById(R.id.action);
		btnAction.setVisibility(View.INVISIBLE);
		// get and display name of the recipe
		TextView nameTxt=(TextView)findViewById(R.id.title);
		nameTxt.setText(recip.getName());
		// get and display level of the recipe
		ImageView levelImg=(ImageView)findViewById(R.id.levelimg);
		setImg(levelImg,recip.getLevel());
		// get and display cost of the recipe
		ImageView costImg=(ImageView)findViewById(R.id.costimg);
		setImg(costImg,recip.getCost());
		// get and display all products needed by the recipe
		TextView ingredientTxt=(TextView)findViewById(R.id.ingredient);
		Vector<Product> prods=recip.getProdList();
		String ingredient=new String();
		int i=0;
		for(Product prod : prods) {
			ingredient+=prod.toString();
			if(i<prods.size()-1) {
				ingredient+="\n";
				i++;
			}
		}
		ingredientTxt.setText(ingredient);
		// get and display the howto
		TextView howtoTxt=(TextView)findViewById(R.id.howto);
		String howTo=recip.getHowto();
		Log.d("PickMeal",howTo);
		howTo.replace("\t","");
		howtoTxt.setText(howTo);
	}
	/**
	 * set image depending on the level or the cost
	 * 
	 * @param source where the image need to be set
	 * @param nb level or cost of the recipe
	 */
	public void setImg(ImageView source,int nb) {
		switch(nb) {
		case 1:
			source.setImageResource(R.drawable.rate1);
			break;
		case 2:
			source.setImageResource(R.drawable.rate2);
			break;
		case 3:
			source.setImageResource(R.drawable.rate3);
			break;
		case 4:
			source.setImageResource(R.drawable.rate4);
			break;
		case 5:
			source.setImageResource(R.drawable.rate5);
			break;
		default:
			source.setImageResource(R.drawable.rate1);
		}
	}
}