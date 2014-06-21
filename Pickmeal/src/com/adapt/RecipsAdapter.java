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
package com.adapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.data.RecipesContent;
import com.pickmeal.R;

/**
 * Adapter for the recipes listview
 */
public class RecipsAdapter extends ArrayAdapter<RecipesContent> {
	private LayoutInflater infl;
	private int textviewResourceId;
	public RecipsAdapter(Context context,int textViewResourceId) {
		super(context,textViewResourceId);
		infl=LayoutInflater.from(context);
		textviewResourceId=textViewResourceId;
	}
	public RecipsAdapter(Context context,int textViewResourceId,RecipesContent[] objects) {
		super(context,textViewResourceId,objects);
		infl=LayoutInflater.from(context);
		textviewResourceId=textViewResourceId;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		LinearLayout layout;
		if(convertView==null)
			layout=(LinearLayout)infl.inflate(textviewResourceId,parent,false);
		else
			layout=(LinearLayout)convertView;
		TextView name=(TextView)layout.findViewById(R.id.title);
		TextView lvlText=(TextView)layout.findViewById(R.id.leveltxt);
		ImageView lvlImg=(ImageView)layout.findViewById(R.id.levelimg);
		TextView costText=(TextView)layout.findViewById(R.id.couttxt);
		ImageView costImg=(ImageView)layout.findViewById(R.id.coutimg);
		name.setText(getItem(position).getName());
		lvlText.setText(R.string.lvltxt);
		lvlImg.setImageResource(getImgId(getItem(position).getLevel()));
		costText.setText(R.string.costtxt);
		costImg.setImageResource(getImgId(getItem(position).getCost()));
		return layout;
	}
	/**
	 * get the image id depending on the level or the cost of a recipe
	 * 
	 * @param level level or cost of the recipe
	 * @return image id
	 */
	private int getImgId(int level) {
		switch(level) {
		case 1:
			return R.drawable.rate1;
		case 2:
			return R.drawable.rate2;
		case 3:
			return R.drawable.rate3;
		case 4:
			return R.drawable.rate4;
		case 5:
			return R.drawable.rate5;
		default:
			return R.drawable.rate1;
		}
	}
}
