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
package com.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.pickmeal.R;

/**
 * this class allow to open a database and maintain the database up to date
 */
public class DataOpenBaseHelper extends SQLiteOpenHelper {
	protected SQLiteDatabase db;
	private Context myContext;
	private String db_path;
	private String db_name;
	private int db_version;
	/**
	 * constructor for DatabaseHelper, call the constructor of SQLiteOpenHelper
	 * 
	 * @param context Android context
	 * @param dbName name of the database
	 * @param dbPath path to the directory of the database
	 * @param dbVersion version of the database
	 */
	public DataOpenBaseHelper(Context context,String dbName,String dbPath,int dbVersion) {
		super(context,dbName,null,1);
		db_name=dbName;
		db_path=dbPath;
		db_version=dbVersion;
		myContext=context;
	}
	/**
	 * use to open the database in read only mode
	 * 
	 * @return true if database opening succeed, false otherwise
	 */
	public boolean openDb() {
		Log.i("PickMeal","openDb called");
		try {
			db=SQLiteDatabase.openDatabase(db_path+db_name,null,
					SQLiteDatabase.OPEN_READONLY);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	/**
	 * open database in read write mode
	 */
	public void openWrDb() {
		Log.i("PickMeal","openWrDb called");
		db=SQLiteDatabase
				.openDatabase(db_path+db_name,null,SQLiteDatabase.OPEN_READWRITE);
	}
	/**
	 * update the database if needed, create a new one if it doesn't exist
	 * 
	 * @throws IOException throw if database file can't be opened
	 */
	public boolean updateDb() throws IOException {
		if(checkDb())
			return false;
		copyDb();
		openWrDb();
		db.setVersion(db_version);
		db.close();
		return true;
	}
	/**
	 * copy the database into a file
	 * 
	 * @throws IOException throw if database file can't be opened
	 */
	public void copyDb() throws IOException {
		BufferedInputStream in=new BufferedInputStream(myContext.getResources()
				.openRawResource(R.raw.recipes));
		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(db_path
				+db_name));
		byte buff[]=new byte[1024];
		int length;
		while((length=in.read(buff))>0) {
			out.write(buff,0,length);
		}
		out.flush();
		out.close();
		in.close();
	}
	/**
	 * check if the database exist and if it needs to be updated
	 * 
	 * @return true if the database is up to date, false otherwise
	 */
	public boolean checkDb() {
		boolean checkdb=openDb();
		if(!checkdb) {
			Log.i("PickMeal","db doesn't exist");
			return false;
		} else {
			int currentVersion=db.getVersion();
			if(db_version>currentVersion) {
				Log.i("PickMeal","db need update");
				db.close();
				return false;
			}
		}
		Log.i("PickMeal","db version is ok");
		db.close();
		return true;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("PickMeal","onCreate called");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
		Log.i("PickMeal","onUpgrade called");
	}
	/**
	 * destructor : close the database
	 */
	@Override
	protected void finalize() {
		if(db!=null)
			db.close();
	}
}
