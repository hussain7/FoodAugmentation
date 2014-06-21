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

import java.util.Arrays;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.adapt.ProductsAdapter;
import com.data.ProductBase;
import com.data.RecipesContent;
import com.exception.ProdListException;
import com.exception.SaveItemsException;

/**
 * list all the meal contained in recipes list
 */
public class FridgeMode extends Activity implements OnClickListener,
		DialogInterface.OnClickListener {
	private ShareData data;
	private ListView mainListView;
	private ProductsAdapter adapt;
	private ProductBase[] recList;
	private ProductBase prod;
	private AutoCompleteTextView autoComplete;
	private Vector<RecipesContent> res;
	private static final String SAVE="list";
	private Button toast;
	private boolean check=false;
	private boolean checkEmpty=false;
	private int last_prodSize=0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fridgemode);
		data=(ShareData)getApplication();
		adapt=new ProductsAdapter(this,R.layout.list_custom);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_fridgemode);
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		Button btnAction=(Button)findViewById(R.id.action);
		btnAction.setOnClickListener(this);
		mainListView=(ListView)findViewById(R.id.list);
		mainListView.setCacheColorHint(0);
		adapt.setNotifyOnChange(true);
		mainListView.setAdapter(adapt);
		mainListView.setItemsCanFocus(false);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		registerForContextMenu(mainListView);
		data=(ShareData)getApplication();
		recList=data.db.requestProductsList(0);
		autoComplete=(AutoCompleteTextView)findViewById(R.id.autocompletion);
		Button btnOK=(Button)findViewById(R.id.btnOK);
		ArrayAdapter<ProductBase> adapter1=new ArrayAdapter<ProductBase>(this,
				android.R.layout.simple_dropdown_item_1line,recList);
		autoComplete.setAdapter(adapter1);
		btnOK.setOnClickListener(this);
		res=new Vector<RecipesContent>();
		LoadSelections();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.action:
			// find recipes corresponding to the products available
			res.removeAllElements();
			Vector<Integer> idProds=new Vector<Integer>();
			int count=adapt.getCount();
			for(int i=0;i<count;i++) {
				idProds.add(adapt.getItem(i).getId());
			}
			try {
				// request a list of recipe containing at least one products
				res=data.db.requestFridgeRecipesList(idProds);
				// no recipes found
				if(res.size()<=0) {
					int actualProdSize=idProds.size();
					if((check==false)||(last_prodSize!=actualProdSize)) {
						Toast.makeText(this,R.string.fridge_alert,Toast.LENGTH_LONG)
								.show();
						check=true;
						last_prodSize=actualProdSize;
					}
					break;
				} else
					check=false;
				// show a dialog with all the recipes
				removeDialog(0);
				showDialog(0);
			} catch(ProdListException e) {
				if(checkEmpty==false) {
					Toast.makeText(getApplicationContext(),R.string.prodlist_empty,
							Toast.LENGTH_SHORT).show();
					checkEmpty=true;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btnOK:
			try {
				// add a product in the list
				prod=data.db.requestProductInfo(autoComplete.getText());
				// product is not already on the list
				if(adapt.getPosition(prod)<0) {
					adapt.add(prod);
					autoComplete.setText("");
				} else {
					Toast.makeText(getApplicationContext(),R.string.prod_already_exist,
							Toast.LENGTH_SHORT).show();
				}
				SaveSelections();
			} catch(Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infl=getMenuInflater();
		infl.inflate(R.menu.menu_fridgemode,menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.fridge_erase:
			// clear the products list and save it
			adapt.clear();
			SaveSelections();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		// construct char sequence for the list
		int size=res.size();
		CharSequence items[]=new CharSequence[size];
		for(int i=0;i<size;i++) {
			items[i]=res.elementAt(i).toString();
		}
		// construct the dialog
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle(R.string.pick_meal_str);
		builder.setItems(items,this);
		AlertDialog alert=builder.create();
		return alert;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu,v,menuInfo);
		AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(mainListView.getItemAtPosition(info.position).toString());
		menu.add(info.position,0,0,R.string.menu_delete);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ProductBase temp=adapt.getItem(item.getGroupId());
		adapt.remove(temp);
		return super.onContextItemSelected(item);
	}
	@Override
	public void onClick(DialogInterface dialog,int which) {
		Bundle objetbunble=new Bundle();
		objetbunble.putInt("idrecip",res.elementAt(which).getId());
		Intent intent=new Intent(FridgeMode.this,Recipe.class);
		intent.putExtras(objetbunble);
		startActivity(intent);
	}
	/**
	 * load all the selection preferences
	 */
	private void LoadSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		if(!settingsActivity.contains(SAVE))
			return;
		String savedItems=settingsActivity.getString(SAVE,"");
		Vector<String> list=new Vector<String>();
		// split string with the token ','
		list.addAll(Arrays.asList(savedItems.split(",")));
		// get all items from strings
		for(String elem : list) {
			String[] str=elem.split(":");
			ProductBase prod=new ProductBase(Integer.valueOf(str[1]),str[0]);
			adapt.add(prod);
		}
	}
	/**
	 * save all the selection preferences
	 */
	private void SaveSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefEditor=settingsActivity.edit();
		try {
			String savedItems=getSavedItems();
			prefEditor.putString(SAVE,savedItems);
			prefEditor.commit();
		} catch(SaveItemsException e) {
			prefEditor.clear();
			prefEditor.commit();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * save items in the database and in a string
	 * 
	 * @return string with all items
	 */
	private String getSavedItems() throws SaveItemsException {
		String savedItems="";
		// nothing to save
		if(adapt.getCount()<=0)
			throw new SaveItemsException();
		// get all items and build the string
		int count=mainListView.getAdapter().getCount()-1;
		for(int i=0;i<count;i++) {
			ProductBase prod=adapt.getItem(i);
			savedItems+=prod.getName()+":"+prod.getId()+",";
		}
		// last item
		ProductBase prod=adapt.getItem(adapt.getCount()-1);
		savedItems+=prod.getName()+":"+prod.getId();
		return savedItems;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		check=false;
		checkEmpty=false;
	}
}
