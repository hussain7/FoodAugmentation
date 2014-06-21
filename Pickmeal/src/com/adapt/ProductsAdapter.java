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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import com.data.ProductBase;

/**
 * Adapter for the recipes listview
 */
public class ProductsAdapter extends ArrayAdapter<ProductBase> {
	private Context context=null;
	private int textviewResourceId;
	public ProductsAdapter(Context context,int textViewResourceId) {
		super(context,textViewResourceId);
		this.context=context;
		textviewResourceId=textViewResourceId;
	}
	public ProductsAdapter(Context context,int textViewResourceId,ProductBase[] objects) {
		super(context,textViewResourceId,objects);
		this.context=context;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		CheckedTextView cTextView;
		cTextView=(CheckedTextView)View.inflate(context,textviewResourceId,null);
		cTextView.setText(getItem(position).getName());
		return cTextView;
	}
	@Override
	public int getPosition(ProductBase item) {
		int count=getCount();
		for(int i=0;i<count;i++) {
			if(item.getId()==getItem(i).getId())
				return i;
		}
		return -1;
	}
}
