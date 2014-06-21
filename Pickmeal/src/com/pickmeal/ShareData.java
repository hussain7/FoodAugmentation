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

import java.util.Vector;
import android.app.Application;
import com.data.RecipesContent;
import com.database.RecipesDb;

/**
 * contain all the data shared by all activity
 */
public class ShareData extends Application {
	private static final long serialVersionUID=-601093828022444376L;
	public RecipesDb db;
	public Vector<RecipesContent> recips;
	public int nbRepas,lvl,price;
	public ShareData() {
		super();
		db=null;
		recips=null;
	}
}
