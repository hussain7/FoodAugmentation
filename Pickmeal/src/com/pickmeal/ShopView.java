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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.data.Product;
import com.data.ShopList;

/**
 * activity to display the shop list
 */
public class ShopView extends ListActivity {
	private ListView mainListView=null;
	final String SETTING_TODOLIST="todolist";
	private ShareData data;
	private ShopList shop;
	private Button toast;
	private ArrayList<String> selectedItems=new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shoplist);
		// get shared data
		data=(ShareData)getApplication();
		// generate products list
		shop=new ShopList();
		if(data.recips==null)
			return;
		for(int i=0;i<data.recips.size();i++) {
			shop.addProdList(data.recips.elementAt(i).getProdList());
		}
		Vector<Product> prods=shop.getProdList();
		// create button back
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mainListView=getListView();
		mainListView.setCacheColorHint(0);
		mainListView.setAdapter(new ArrayAdapter<Product>(this,
				R.layout.list_custom_multiple_choice,prods));
		mainListView.setItemsCanFocus(false);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_shoplist);
		LoadSelections();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infl=getMenuInflater();
		infl.inflate(R.menu.prod_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.unselall:
			Toast.makeText(getApplicationContext(),R.string.rm_pref,Toast.LENGTH_SHORT)
					.show();
			ClearSelections();
			break;
		case R.id.selall:
			Select_all();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onPause() {
		SaveSelections();
		super.onPause();
	}
	/**
	 * clear selections in the list
	 */
	private void Select_all() {
		int count=mainListView.getAdapter().getCount();
		for(int i=0;i<count;i++) {
			mainListView.setItemChecked(i,true);
		}
		SaveSelections();
	}
	/**
	 * clear selections in the list
	 */
	private void ClearSelections() {
		int count=mainListView.getAdapter().getCount();
		for(int i=0;i<count;i++) {
			mainListView.setItemChecked(i,false);
		}
		SaveSelections();
	}
	/**
	 * load the selection preference
	 */
	private void LoadSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		if(settingsActivity.contains(SETTING_TODOLIST)) {
			String savedItems=settingsActivity.getString(SETTING_TODOLIST,"");
			selectedItems.addAll(Arrays.asList(savedItems.split(",")));
			ListAdapter adapt=mainListView.getAdapter();
			int count=adapt.getCount();
			for(int i=0;i<count;i++) {
				Product currentItem=(Product)adapt.getItem(i);
				for(String selectedItem : selectedItems) {
					if(selectedItem.compareTo(currentItem.toString())==0) {
						mainListView.setItemChecked(i,true);
					}
				}
			}
		}
	}
	/**
	 * save the selection preference
	 */
	private void SaveSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefEditor=settingsActivity.edit();
		String savedItems=getSavedItems();
		prefEditor.putString(SETTING_TODOLIST,savedItems);
		prefEditor.commit();
	}
	/**
	 * get all the selected items
	 * 
	 * @return a list of selected items
	 */
	private String getSavedItems() {
		if(mainListView==null)
			return "";
		String savedItems="";
		int count=mainListView.getAdapter().getCount();
		for(int i=0;i<count;i++) {
			if(mainListView.isItemChecked(i)) {
				if(savedItems.length()>0) {
					savedItems+=","+mainListView.getItemAtPosition(i);
				} else {
					savedItems+=mainListView.getItemAtPosition(i);
				}
			}
		}
		return savedItems;
	}
}
