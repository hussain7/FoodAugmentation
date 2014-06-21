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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.adapt.RecipsAdapter;
import com.data.RecipesContent;

public class MyRecip extends Activity implements OnClickListener {
	private ShareData data;
	private ListView mainListView;
	private RecipsAdapter adapt;
	private RecipesContent[] recList;
	private Button toast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_customlist1);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_myrecipes);
		data=(ShareData)getApplication();
		recList=data.db.requestTasteRecipes();
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		mainListView=(ListView)findViewById(R.id.listviewperso);
		adapt=new RecipsAdapter(this,R.layout.item_recip,recList);
		mainListView.setAdapter(adapt);
		mainListView.setCacheColorHint(0);
		mainListView.setItemsCanFocus(false);
		mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		registerForContextMenu(mainListView);
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a,View v,int position,long id) {
				Bundle objetbunble=new Bundle();
				objetbunble.putInt("idrecip",recList[position].getId());
				Intent intent=new Intent(MyRecip.this,Recipe.class);
				intent.putExtras(objetbunble);
				startActivity(intent);
			}
		});
	}
	@Override
	public void onClick(View v) {
		finish();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu,v,menuInfo);
		AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle(recList[info.position].getName());
		menu.add(info.position,0,0,R.string.addtomlist);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos=item.getGroupId();
		data.recips.add(recList[pos]);
		return super.onContextItemSelected(item);
	}
}