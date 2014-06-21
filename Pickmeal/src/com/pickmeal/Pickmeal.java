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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import com.data.RecipesContent;
import com.database.RecipesDb;

public class Pickmeal extends Activity implements OnClickListener,
		SeekBar.OnSeekBarChangeListener,SlidingDrawer.OnDrawerCloseListener {
	private Button Repas7jButton;
	private Button CourseButton;
	private Button FrigoButton;
	private Button MyRecipButton;
	private Button AllRecipButton;
	private Button GoutButton;
	private Button RandomButton;
	private Button About;
	private SeekBar mSeekBar1;
	private SeekBar mSeekBar2;
	private TextView mProgressText1;
	private TextView mProgressText2;
	private EditText editText;
	private SlidingDrawer sDrawer;
	private final String pathToNb="/data/data/com.pickmeal/nb_recips";
	private final String pathToData="/data/data/com.pickmeal/recips";
	private final int BUFF_SIZE=8192;
	private final String SETTING_TODOLIST="todolist";
	private ArrayList<String> selectedItems=new ArrayList<String>();
	private ShareData data;
	private Dialog dialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		// Instantiate all the objects needed
		data=(ShareData)getApplication();
		if(data.db==null)
			data.db=new RecipesDb(this);
		if(data.recips==null)
			data.recips=new Vector<RecipesContent>();
		// get all buttons
		Repas7jButton=(Button)findViewById(R.id.button1);
		CourseButton=(Button)findViewById(R.id.button2);
		FrigoButton=(Button)findViewById(R.id.button3);
		RandomButton=(Button)findViewById(R.id.button4);
		MyRecipButton=(Button)findViewById(R.id.button5);
		AllRecipButton=(Button)findViewById(R.id.button6);
		GoutButton=(Button)findViewById(R.id.button7);
		About=(Button)findViewById(R.id.about);
		// set all listeners
		Repas7jButton.setOnClickListener(this);
		CourseButton.setOnClickListener(this);
		FrigoButton.setOnClickListener(this);
		RandomButton.setOnClickListener(this);
		MyRecipButton.setOnClickListener(this);
		AllRecipButton.setOnClickListener(this);
		GoutButton.setOnClickListener(this);
		About.setOnClickListener(this);
		// sliding drawer
		sDrawer=(SlidingDrawer)findViewById(R.id.drawer);
		sDrawer.setOnDrawerCloseListener(this);
		mSeekBar1=(SeekBar)findViewById(R.id.SeekBar1);
		mSeekBar2=(SeekBar)findViewById(R.id.SeekBar2);
		mSeekBar1.setOnSeekBarChangeListener(this);
		mSeekBar2.setOnSeekBarChangeListener(this);
		mProgressText1=(TextView)findViewById(R.id.progress1);
		mProgressText2=(TextView)findViewById(R.id.progress2);
		editText=(EditText)findViewById(R.id.entry);
		editText.setMaxLines(1);
		// load preferences
		LoadSelections();
		// load data
		loadRecipsList();
		dialog=new Dialog(Pickmeal.this);
		dialog.setContentView(R.layout.about);
		dialog.setCancelable(true);
		dialog.setTitle("A propos de PickMeal");
		ImageView img=(ImageView)dialog.findViewById(R.id.ImageView01);
		img.setImageResource(R.drawable.appname);
		Button button=(Button)dialog.findViewById(R.id.Button01);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		
	}
	@Override
	public void onProgressChanged(SeekBar seekBar,int progress,boolean fromTouch) {
		if(seekBar.getId()==R.id.SeekBar1) {
			switch(progress) {
			case 0:
				mProgressText1.setText(R.string.lvl0);
				break;
			case 1:
				mProgressText1.setText(R.string.lvl1);
				break;
			case 2:
				mProgressText1.setText(R.string.lvl2);
				break;
			case 3:
				mProgressText1.setText(R.string.lvl3);
				break;
			case 4:
				mProgressText1.setText(R.string.lvl4);
				break;
			}
		} else {
			switch(progress) {
			case 0:
				mProgressText2.setText(R.string.money0);
				break;
			case 1:
				mProgressText2.setText(R.string.money1);
				break;
			case 2:
				mProgressText2.setText(R.string.money2);
				break;
			case 3:
				mProgressText2.setText(R.string.money3);
				break;
			case 4:
				mProgressText2.setText(R.string.money4);
				break;
			}
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	if(sDrawer.isOpened() == true){
	    		sDrawer.animateClose();	
	    	}else{
	    	    return super.onKeyDown(keyCode, event);
	    	}
	    }return false;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button1:
			Intent intent1=new Intent(Pickmeal.this,MealList.class);
			startActivity(intent1);
			break;
		case R.id.button2:
			Intent intent2=new Intent(Pickmeal.this,ShopView.class);
			startActivity(intent2);
			break;
		case R.id.button3:
			Intent intent3=new Intent(Pickmeal.this,FridgeMode.class);
			startActivity(intent3);
			break;
		case R.id.button4:
			Intent intent4=new Intent(Pickmeal.this,RandomRecip.class);
			startActivity(intent4);
			break;
		case R.id.button5:
			Intent intent5=new Intent(Pickmeal.this,MyRecip.class);
			startActivity(intent5);
			break;
		case R.id.button6:
			Intent intent6=new Intent(Pickmeal.this,AllRecip.class);
			startActivity(intent6);
			break;
		case R.id.button7:
			Intent intent8=new Intent(Pickmeal.this,AllCategories.class);
			startActivity(intent8);
			break;
		case R.id.about:
			dialog.show();
			break;
		}
	}
	@Override
	protected void onPause() {
		SaveSelections();
		data.db.saveBlacklist();
		super.onPause();
	}
	/**
	 * save selections preference
	 */
	private void SaveSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefEditor=settingsActivity.edit();
		String savedItems=getSavedItems();
		prefEditor.putString(SETTING_TODOLIST,savedItems);
		prefEditor.commit();
	}
	/**
	 * get all the general preference
	 * 
	 * @return a list of preference
	 */
	private String getSavedItems() {
		String savedItems="";
		savedItems+=mSeekBar1.getProgress();
		savedItems+=","+mSeekBar2.getProgress();
		try {
			data.nbRepas=Integer.parseInt(editText.getText().toString());
			savedItems+=","+editText.getText();
		} catch (Exception e) {
			Toast.makeText(
					this,
					R.string.only_number,
					Toast.LENGTH_LONG).show();
			editText.setText("0");
			savedItems+=",0";
		}
		data.lvl=mSeekBar1.getProgress();
		data.price=mSeekBar2.getProgress();
		return savedItems;
	}
	/**
	 * load the general preference
	 */
	private void LoadSelections() {
		SharedPreferences settingsActivity=getPreferences(MODE_PRIVATE);
		if(settingsActivity.contains(SETTING_TODOLIST)) {
			String savedItems=settingsActivity.getString(SETTING_TODOLIST,"");
			selectedItems.addAll(Arrays.asList(savedItems.split(",")));
			data.lvl=Integer.parseInt(selectedItems.get(0));
			data.price=Integer.parseInt(selectedItems.get(1));
			data.nbRepas=Integer.parseInt(selectedItems.get(2));
			mSeekBar1.setProgress(data.lvl);
			mSeekBar2.setProgress(data.price);
			editText.setText(selectedItems.get(2));
		}
	}
	/**
	 * load the meal list
	 */
	public void loadRecipsList() {
		try {
			DataInputStream din=new DataInputStream(new BufferedInputStream(
					new FileInputStream(pathToNb),BUFF_SIZE));
			int nbRecips=din.readInt();
			din.close();
			data.recips.removeAllElements();
			ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(pathToData),BUFF_SIZE));
			for(int i=0;i<nbRecips;i++) {
				RecipesContent temp=(RecipesContent)in.readObject();
				data.recips.add(temp);
			}
			in.close();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(StreamCorruptedException e) {
			e.printStackTrace();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onDrawerClosed() {
		SaveSelections();
	}
}