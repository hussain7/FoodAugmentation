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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.Editable;
import android.util.Log;
import com.data.Product;
import com.data.ProductBase;
import com.data.RecipesContent;
import com.exception.ProdListException;
import com.exception.RandomRecipsException;

public class RecipesDb extends DataOpenBaseHelper {
	/*
	 * catégorie: 1. viandes 2. poissons 3. légumes 4. fruits 5. boissons 6.
	 * laitages 7. bases 8. épices 9. confiserie
	 */
	private static final String DB_NAME="products";
	private static final String DB_PATH="/data/data/com.pickmeal/";
	private static final String NB_PATH="/data/data/com.pickmeal/nb_bl";
	private static final String BL_PATH="/data/data/com.pickmeal/blacklist";
	private final int BUFF_SIZE=8192;
	private static final int DB_VERSION=3;
	/**
	 * Update the database if needed and open it just after, kill the process if
	 * it can't write the database
	 * 
	 * @param context Android context
	 */
	public RecipesDb(Context context) {
		super(context,DB_NAME,DB_PATH,DB_VERSION);
		boolean dbstate=false;
		try {
			dbstate=updateDb();
		} catch(IOException e) {
			e.printStackTrace();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		openWrDb();
		if(dbstate)
			loadBlacklist();
	}
	/**
	 * get a list of recipes from the database according to the taste of the
	 * user
	 * 
	 * @return a list of recipes
	 */
	public RecipesContent[] requestTasteRecipes() {
		Log.i("PickMeal","requestTasteRecipes called");
		Cursor curs=null;
		RecipesContent[] recList=null;
		try {
			curs=db.rawQuery(
					"SELECT id,name,level,cost,howto FROM recipies WHERE id NOT IN (SELECT idRecip FROM recip_prod WHERE idProd IN (SELECT id_product FROM gout_user)	GROUP BY idRecip) ORDER BY name",
					null);
			recList=new RecipesContent[curs.getCount()];
			// get the recipes with the random list
			int count=curs.getCount();
			for(int i=0;i<count;i++) {
				curs.moveToPosition(i);
				recList[i]=new RecipesContent(curs.getInt(0),curs.getString(1),
						curs.getInt(2),curs.getInt(3),curs.getString(4));
				// request list of products corresponding with the recipe
				Product[] prods=requestProductsRecipList(recList[i].getId());
				for(int j=0;j<prods.length;j++) {
					recList[i].addProduct(prods[j]);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return recList;
	}
	/**
	 * get a random list of recipes
	 * 
	 * @param maxPrice price of one recipes
	 * @param difficulty maximum difficulty of recipes
	 * @param nbRecipes number of recipes to get
	 * @return a list of recipes
	 */
	public RecipesContent[] requestRecipes(int maxPrice,int difficulty,int nbRecipes)
			throws RandomRecipsException {
		Log.i("PickMeal","request called");
		Cursor curs=null;
		RecipesContent[] recList=null;
		Log.d("PickMeal","price max : "+maxPrice+"diff : "+difficulty);
		try {
			// request recipes without blacklisted products
			curs=db.rawQuery(
					"SELECT id,name,level,cost,howto FROM recipies WHERE id NOT IN (SELECT idRecip FROM recip_prod WHERE idProd IN (SELECT id_product FROM gout_user) GROUP BY idRecip) AND level <= "
							+difficulty+" AND cost <= "+maxPrice,null);
			// not enough recipes from db
			int count=curs.getCount();
			if(count<nbRecipes) {
				curs.close();
				throw new RandomRecipsException();
			}
			Vector<Integer> randList=new Vector<Integer>();
			// get a list of random numbers
			for(int i=0;i<nbRecipes;) {
				// create a random number between 0 and nbRecipes - 1
				Integer index=Integer.valueOf(((int)(Math.random()*1000000))%count);
				// check if the number is already in the list
				if(randList.indexOf(index)<0) {
					randList.add(index);
					i++;
				}
			}
			recList=new RecipesContent[nbRecipes];
			// get the recipes with the random list
			for(int i=0;i<nbRecipes;i++) {
				curs.moveToPosition(randList.elementAt(i));
				recList[i]=new RecipesContent(curs.getInt(0),curs.getString(1),
						curs.getInt(2),curs.getInt(3),curs.getString(4));
				// request list of products corresponding with the recipe
				Product[] prods=requestProductsRecipList(recList[i].getId());
				for(int j=0;j<prods.length;j++) {
					recList[i].addProduct(prods[j]);
				}
			}
		} catch(SQLiteException e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return recList;
	}
	/**
	 * request a list of recipes in the database
	 * 
	 * @return a string list of all the recipes
	 */
	public RecipesContent[] requestRecipesList() {
		String col[]= {"id","name","level","cost","howto"};
		Cursor curs=null;
		RecipesContent[] recList=null;
		try {
			curs=db.query("recipies",col,null,null,null,null,"name");
			int count=curs.getCount();
			recList=new RecipesContent[count];
			for(int i=0;i<count;i++) {
				curs.moveToPosition(i);
				recList[i]=new RecipesContent(curs.getInt(0),curs.getString(1),
						curs.getInt(2),curs.getInt(3),curs.getString(4));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return recList;
	}
	/**
	 * request a list of product of a type
	 * 
	 * @param value type of product
	 * @return list of product
	 */
	public ProductBase[] requestProductsList(int value) {
		String col[]= {"id,name"};
		Cursor curs=null;
		ProductBase[] res=null;
		try {
			if(value==0) {
				curs=db.query("products",col,null,null,null,null,"id");
			} else {
				curs=db.query("products",col,"category="+value,null,null,null,"id");
			}
			res=new ProductBase[curs.getCount()];
			for(int i=0;i<res.length;i++) {
				curs.moveToPosition(i);
				res[i]=new ProductBase(curs.getInt(0),curs.getString(1));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return res;
	}
	/**
	 * get a products lists of one recipe
	 * 
	 * @param id id of the recipe
	 * @return a list of products
	 */
	private Product[] requestProductsRecipList(int id) {
		Cursor curs=null;
		Product[] prods=null;
		try {
			curs=db.rawQuery(
					"SELECT id,name,quantity,category,type1,type2,type3,unity FROM products INNER JOIN recip_prod ON products.id=recip_prod.idProd WHERE recip_prod.idRecip="
							+id,null);
			int count=curs.getCount();
			prods=new Product[count];
			for(int i=0;i<count;i++) {
				curs.moveToPosition(i);
				prods[i]=new Product(curs.getInt(0),curs.getString(1),curs.getInt(2),
						curs.getInt(3),curs.getInt(4),curs.getInt(5),curs.getInt(6),
						curs.getString(7));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return prods;
	}
	/**
	 * request recipe's information based on the id of this recipes
	 * 
	 * @param id id of the recipe
	 * @return recipes content
	 */
	public RecipesContent requestRecip(int id) {
		Cursor curs=null;
		RecipesContent rec=null;
		try {
			curs=db.query("recipies",null,"id = "+id,null,null,null,null);
			curs.moveToFirst();
			rec=new RecipesContent(curs.getInt(0),curs.getString(1),curs.getInt(2),
					curs.getInt(3),curs.getString(4));
			Product[] prods=requestProductsRecipList(rec.getId());
			for(int j=0;j<prods.length;j++) {
				rec.addProduct(prods[j]);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return rec;
	}
	/**
	 * remove blacklisted products
	 * 
	 * @param type type of product to remove
	 */
	public void removeGout(int type) {
		db.delete("gout_user","id_product > "+type*1000+" AND id_product < "+(type+1)
				*1000,null);
	}
	/**
	 * insert a new blacklist product
	 * 
	 * @param idProd
	 */
	public void insertGout(int idProd) {
		Log.i("PickMeal","insertPref called");
		ContentValues values=new ContentValues();
		values.put("id_user",1);
		values.put("id_product",idProd);
		db.insert("gout_user",null,values);
	}
	/**
	 * remove all user's preferences
	 */
	public void removePref() {
		db.delete("pref_user",null,null);
	}
	/**
	 * insert user's preferences in the database
	 * 
	 * @param SB1 : level of cooking
	 * @param SB2 : cost of recipe
	 * @param TE : number of meal per week
	 */
	public void insertPref(int SB1,int SB2,int TE) {
		Log.i("PickMeal","insertPref called");
		ContentValues valuesPref=new ContentValues();
		valuesPref.put("id_user",1);
		valuesPref.put("progressSB1",SB1);
		valuesPref.put("progressSB2",SB2);
		valuesPref.put("valueTE",TE);
		db.insert("pref_user",null,valuesPref);
	}
	/**
	 * get the products blacklist, taste of the user
	 * 
	 * @return a list of blacklisted products
	 */
	public ProductBase[] getBlackList() {
		Cursor curs=null;
		ProductBase[] prods=null;
		try {
			curs=db.rawQuery(
					"SELECT id,name FROM products WHERE id IN (SELECT id_product FROM gout_user)",
					null);
			int count=curs.getCount();
			prods=new ProductBase[count];
			for(int i=0;i<count;i++) {
				curs.moveToPosition(i);
				prods[i]=new ProductBase(curs.getInt(0),curs.getString(1));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return prods;
	}
	/**
	 * push blacklist in the database, erase the blacklist contained in the
	 * database before pushing anything
	 * 
	 * @param prods list of blacklisted products
	 */
	private void pushBlacklist(ProductBase[] prods) {
		ContentValues val=new ContentValues();
		for(int i=0;i<prods.length;i++) {
			val.put("id_user",1);
			val.put("id_product",prods[i].getId());
			db.insert("gout_user",null,val);
			val.clear();
		}
	}
	/**
	 * save the blacklist into a file
	 */
	public void saveBlacklist() {
		ProductBase[] prods=getBlackList();
		try {
			DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(NB_PATH),BUFF_SIZE));
			dos.writeInt(prods.length);
			dos.close();
			ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(BL_PATH),BUFF_SIZE));
			for(int i=0;i<prods.length;i++) {
				out.writeObject(prods[i]);
			}
			out.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * load blacklist from a file
	 */
	public void loadBlacklist() {
		try {
			DataInputStream dis=new DataInputStream(new BufferedInputStream(
					new FileInputStream(NB_PATH),BUFF_SIZE));
			int nb_bl=dis.readInt();
			dis.close();
			if(nb_bl==0)
				return;
			ProductBase[] prods=new ProductBase[nb_bl];
			ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(BL_PATH),BUFF_SIZE));
			for(int i=0;i<nb_bl;i++) {
				prods[i]=(ProductBase)in.readObject();
			}
			in.close();
			pushBlacklist(prods);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * test if the recipe exists and return its id
	 * 
	 * @param editable
	 * @return id of the recipe
	 */
	public int requestProductsInfo(Editable editable) {
		int id;
		Cursor curs;
		try {
			curs=db.rawQuery("Select id FROM products WHERE name="+"\""+editable+"\"",
					null);
			curs.moveToFirst();
			id=curs.getInt(0);
			curs.close();
		} catch(Exception e) {
			e.printStackTrace();
			id=0;
		}
		return id;
	}
	public ProductBase requestProductInfo(Editable editable) throws Exception {
		Cursor curs;
		curs=db.rawQuery("SELECT id,name FROM products WHERE name="+"\""+editable+"\"",
				null);
		if(curs.getCount()<=0) 
			throw new Exception("request prod info : no product in the cursor");
		curs.moveToPosition(0);
		ProductBase prod=new ProductBase(curs.getInt(0),curs.getString(1));
		curs.close();
		return prod;
	}
	/**
	 * request a list of query which can be done with the product given as
	 * parameters
	 * 
	 * @param idProds list of products id
	 * @return id of a recipe
	 */
	public Vector<RecipesContent> requestFridgeRecipesList(Vector<Integer> idProds)
			throws ProdListException {
		// no product ids in the list
		if(idProds.size()==0) {
			throw new ProdListException();
		}
		Cursor curs=null;
		// base of the query
		String recipesReq="SELECT DISTINCT idRecip FROM recip_prod WHERE idProd IN (";
		int[] recips=null;
		Vector<RecipesContent> recipsList=new Vector<RecipesContent>();
		// add the id to the query
		for(int i=0;i<idProds.size()-1;i++) {
			recipesReq+=idProds.elementAt(i)+",";
		}
		// finalize the query with the last item
		recipesReq+=idProds.elementAt(idProds.size()-1)+")";
		try {
			curs=db.rawQuery(recipesReq,null);
			// store all the recipes id in a list
			recips=new int[curs.getCount()];
			for(int i=0;i<recips.length;i++) {
				curs.moveToPosition(i);
				recips[i]=curs.getInt(0);
			}
			curs.close();
			// get a list of product for each recipe and test if it is contained
			// in the product ids list given as parameter
			for(int i=0;i<recips.length;i++) {
				Vector<Integer> prodList=getListProdsIds(recips[i]);
				if(idProds.containsAll(prodList)) {
					recipsList.add(requestRecip(recips[i]));
				}
			}
		} catch(Exception e) {
			curs.close();
			e.printStackTrace();
		}
		return recipsList;
	}
	/**
	 * return the ids products list of a recipe
	 * 
	 * @param idRecip id of the recipe
	 * @return list of products ids
	 */
	private Vector<Integer> getListProdsIds(int idRecip) {
		String req="SELECT idProd FROM recip_prod WHERE idRecip=";
		req+=idRecip;
		Cursor curs=null;
		Vector<Integer> idProds=new Vector<Integer>();
		try {
			curs=db.rawQuery(req,null);
			int count=curs.getCount();
			for(int i=0;i<count;i++) {
				curs.moveToPosition(i);
				idProds.add(curs.getInt(0));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			curs.close();
		}
		return idProds;
	}
}
