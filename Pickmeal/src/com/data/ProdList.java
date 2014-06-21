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
package com.data;

import java.io.Serializable;
import java.util.Vector;

/**
 * contain all the function to manager a list of product
 */
public abstract class ProdList implements Serializable {
	private static final long serialVersionUID=2224679338325582692L;
	protected ProdVector prodList;
	/**
	 * allocate the vector of products
	 */
	public ProdList() {
		prodList=new ProdVector();
	}
	/**
	 * get the products list
	 * 
	 * @return list of products
	 */
	public Vector<Product> getProdList() {
		return prodList;
	}
}
