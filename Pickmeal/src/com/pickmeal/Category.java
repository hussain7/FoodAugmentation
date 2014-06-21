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
import android.app.ListActivity;
import android.content.Intent;
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
import android.widget.ListView;
import com.data.ProductBase;

/**
 * use to define user taste
 */
public class Category extends ListActivity {
	private ProductBase recList[];
	private ListView mainListView=null;
	private ArrayList<String> selectedItems=new ArrayList<String>();
	private int reqDB;
	private ShareData data;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_category);
		// get shared data
		Bundle b=getIntent().getExtras();
		reqDB=b.getInt("reqDB",0);
		data=(ShareData)getApplication();
		recList=data.db.requestProductsList(reqDB);
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent back=new Intent(Category.this,AllCategories.class);
				startActivity(back);
			}
		});
		mainListView=getListView();
		mainListView.setCacheColorHint(0);
		mainListView.setAdapter(new ArrayAdapter<ProductBase>(this,
				R.layout.list_custom_multiple_choice,recList));
		mainListView.setItemsCanFocus(false);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		LoadSelections();
	}
	@Override
	protected void onPause() {
		SaveSelections();
		data.db.saveBlacklist();
		super.onPause();
	}
	@Override
	protected void onResume() {
		LoadSelections();
		super.onResume();
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
		case R.id.selall:
			// check all object in the list
			for(int i=0;i<mainListView.getCount();i++) {
				mainListView.setItemChecked(i,true);
			}
			return true;
		case R.id.unselall:
			// uncheck all object in the list
			int count=mainListView.getCount();
			for(int i=0;i<count;i++) {
				mainListView.setItemChecked(i,false);
			}
			return true;
		}
		return false;
	}
	/**
	 * load all the selection preferences
	 */
	private void LoadSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		if(settingsActivity.contains(String.valueOf(reqDB))) {
			String savedItems=settingsActivity.getString(String.valueOf(reqDB),"");
			selectedItems.addAll(Arrays.asList(savedItems.split(",")));
			int count=mainListView.getAdapter().getCount();
			for(int i=0;i<count;i++) {
				ProductBase currentItem=(ProductBase)mainListView.getAdapter().getItem(i);
				for(String selectedItem : selectedItems) {
					if(selectedItem.compareTo(currentItem.toString())==0) {
						mainListView.setItemChecked(i,true);
					}
				}
			}
		}
	}
	/**
	 * save all the selection preferences
	 */
	private void SaveSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefEditor=settingsActivity.edit();
		String savedItems=SaveItems();
		prefEditor.remove(String.valueOf(reqDB));
		prefEditor.putString(String.valueOf(reqDB),savedItems);
		prefEditor.commit();
	}
	/**
	 * save items in the database and in a string
	 * 
	 * @return string with all items
	 */
	private String SaveItems() {
		String savedItems="";
		int count=mainListView.getAdapter().getCount();
		data.db.removeGout(reqDB);
		for(int i=0;i<count;i++) {
			if(mainListView.isItemChecked(i)) {
				data.db.insertGout(recList[i].getId());
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
