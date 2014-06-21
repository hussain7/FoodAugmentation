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
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.data.RecipesContent;

public class AllRecip extends Activity {
	private ListView maListViewPerso;
	private RecipesContent[] recip;
	private HashMap<String,String> map;
	private Button toast;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customlist1);
		toast=(Button)findViewById(R.id.toast);
		toast.setText(R.string.toast_allrecipes);
		ShareData data=(ShareData)getApplication();
		recip=data.db.requestRecipesList();
		Button btnBack=(Button)findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		maListViewPerso=(ListView)findViewById(R.id.listviewperso);
		ArrayList<HashMap<String,String>> listItem=new ArrayList<HashMap<String,String>>();
		int length=recip.length;
		for(int i=0;i<length;i++) {
			map=new HashMap<String,String>();
			map.put("title",recip[i].getName());
			map.put("leveltxt","Niveau");
			setImg("levelimg",recip[i].getLevel());
			map.put("couttxt","Cout");
			setImg("coutimg",recip[i].getCost());
			listItem.add(map);
		}
		SimpleAdapter mSchedule=new SimpleAdapter(getBaseContext(),listItem,
				R.layout.item_recip,new String[] {"title","leveltxt","levelimg",
						"couttxt","coutimg"},new int[] {R.id.title,R.id.leveltxt,
						R.id.levelimg,R.id.couttxt,R.id.coutimg});
		maListViewPerso.setAdapter(mSchedule);
		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a,View v,int position,long id) {
				Bundle objetbunble=new Bundle();
				objetbunble.putInt("idrecip",recip[position].getId());
				Intent intent=new Intent(AllRecip.this,Recipe.class);
				intent.putExtras(objetbunble);
				startActivity(intent);
			}
		});
	}
	public void setImg(String source,int nb) {
		switch(nb) {
		case 1:
			map.put(source,String.valueOf(R.drawable.rate1));
			break;
		case 2:
			map.put(source,String.valueOf(R.drawable.rate2));
			break;
		case 3:
			map.put(source,String.valueOf(R.drawable.rate3));
			break;
		case 4:
			map.put(source,String.valueOf(R.drawable.rate4));
			break;
		case 5:
			map.put(source,String.valueOf(R.drawable.rate5));
			break;
		}
	}
}