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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.adapt.RecipsAdapter;
import com.data.RecipesContent;
import com.exception.RandomRecipsException;

/**
 * list all the meal contained in recipes list
 */
public class MealList extends Activity implements OnClickListener,
		DialogInterface.OnClickListener {
	private final String pathToNb="/data/data/com.pickmeal/nb_recips";
	private final String pathToData="/data/data/com.pickmeal/recips";
	private final int BUFF_SIZE=8192;
	private final int DIALOG_RMALL=0;
	private final int DIALOG_GENE=1;
	private ShareData data;
	private ListView mainListView;
	private RecipsAdapter adapt;
	private int idDiag;
	private Button toast;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_customlist2);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_meallist);
		// get shared data
		data=(ShareData)getApplication();
		// create dynamic adapter for the listview
		adapt=new RecipsAdapter(this,R.layout.item_recip);
		// create back button
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		// create Action Button
		Button btnAction=(Button)findViewById(R.id.action);
		btnAction.setText(R.string.nextmeal_btn);
		btnAction.setOnClickListener(this);
		// set the listview
		mainListView=(ListView)findViewById(R.id.list);
		mainListView.setCacheColorHint(0);
		adapt.setNotifyOnChange(true);
		mainListView.setAdapter(adapt);
		mainListView.setItemsCanFocus(false);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a,View v,int position,long id) {
				Bundle objetbunble=new Bundle();
				objetbunble.putInt("idrecip",data.recips.elementAt(position).getId());
				Intent intent=new Intent(MealList.this,Recipe.class);
				intent.putExtras(objetbunble);
				startActivity(intent);
			}
		});
		// register a context menu from the listview
		registerForContextMenu(mainListView);
		if(data.recips.isEmpty())
			// toast to warn the user that nb_meal is null
			if(data.nbRepas<=0)
				for(int i=0;i<2;i++)
					Toast.makeText(
							this,
							R.string.no_params,
							Toast.LENGTH_LONG).show();
			else
				generateNewList();
		else
			// restore all recipes
			for(RecipesContent recipe : data.recips) {
				adapt.add(recipe);
			}
	}
	public void generateNewList() {
		try {
			Vector<RecipesContent> tmpRec=data.recips;
			tmpRec.removeAllElements();
			// generate random recipes according to user's preference
			RecipesContent recs[];
			recs=data.db.requestRecipes(data.price,data.lvl,data.nbRepas);
			tmpRec.addAll(Arrays.asList(recs));
			adapt.clear();
			// add recipes to the adapter
			for(RecipesContent recipe : tmpRec) {
				adapt.add(recipe);
			}
			// notify the user that recipes generation was ok
			Toast.makeText(getApplicationContext(),R.string.recip_gene,Toast.LENGTH_SHORT)
					.show();
		} catch(RandomRecipsException e) {
			// notify user that there is not enough recipes with his
			// preference
			Toast.makeText(getApplicationContext(),
					R.string.notenough,
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.action:
			// remove an item from the listview and recipes list
			if(data.recips.size()<=0)
				return;
			data.recips.remove(0);
			RecipesContent temp=adapt.getItem(0);
			adapt.remove(temp);
			Toast.makeText(getApplicationContext(),R.string.nextmeal,Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.back:
			finish();
			break;
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		idDiag=id;
		switch(id) {
		case DIALOG_RMALL:
			builder.setMessage(R.string.dialog_removeall);
			break;
		case DIALOG_GENE:
			builder.setMessage(R.string.gene_newone);
			break;
		}
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.dialog_yes,this);
		builder.setNegativeButton(R.string.dialog_no,this);
		AlertDialog alert=builder.create();
		return alert;
	}
	@Override
	public void onClick(DialogInterface dialog,int which) {
		switch(idDiag) {
		case DIALOG_RMALL:
			switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				// erase all element in the recipes list
				if(data.recips.size()<=0)
					return;
				data.recips.removeAllElements();
				adapt.clear();
				Toast.makeText(getApplicationContext(),R.string.recip_erase,
						Toast.LENGTH_SHORT).show();
				break;
			}
			break;
		case DIALOG_GENE:
			// generate list of meal
			switch(which) {
			case DialogInterface.BUTTON_POSITIVE:
				generateNewList();
				break;
			}
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater infl=getMenuInflater();
		infl.inflate(R.menu.menu_meallist,menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.eraseall:
			showDialog(DIALOG_RMALL);
			return true;
		case R.id.generate:
			if(data.recips.size()<=0)
				generateNewList();
			else
				showDialog(DIALOG_GENE);
			return true;
		}
		return false;
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
		// create the menu with the item selected
		super.onCreateContextMenu(menu,v,menuInfo);
		AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(data.recips.elementAt(info.position).getName());
		menu.add(info.position,0,0,R.string.menu_delete);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// remove an item from the listview and recipes list
		data.recips.remove(item.getGroupId());
		RecipesContent temp=adapt.getItem(item.getGroupId());
		adapt.remove(temp);
		return super.onContextItemSelected(item);
	}
	/**
	 * save the recipes list into a file
	 */
	public void saveRecipsList() {
		try {
			DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(pathToNb),BUFF_SIZE));
			dos.writeInt(data.recips.size());
			dos.close();
			ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(pathToData),BUFF_SIZE));
			for(RecipesContent recipe : data.recips) {
				out.writeObject(recipe);
			}
			out.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onPause() {
		saveRecipsList();
		super.onPause();
	}
}
