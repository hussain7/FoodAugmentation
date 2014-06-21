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
 * A specific vector used for managing products
 * 
 * @param <E> : type of the vector
 */
public class ProdVector extends Vector<Product> implements Serializable {
	private static final long serialVersionUID=3883680117696702317L;
	public ProdVector() {
		super();
	}
	/**
	 * return the index of a product depending on
	 * 
	 * @param prod product to search in the vector
	 * @return index of the product
	 */
	public int indexofProd(Product prod) {
		int size=size();
		for(int i=0;i<size;i++) {
			if(elementAt(i).compStr().equalsIgnoreCase(prod.compStr()))
				return i;
		}
		return -1;
	}
}
