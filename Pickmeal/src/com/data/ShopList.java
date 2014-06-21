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

import java.util.Vector;

/**
 * represent the shopping list
 */
public class ShopList extends ProdList {
	private static final long serialVersionUID=826883032543538086L;
	public ShopList() {
		super();
	}
	/**
	 * add a products list to the shopping list. Test if a product already exist
	 * in the list, in this case just update the number of this product
	 * 
	 * @param list : list of products
	 */
	public void addProdList(Vector<Product> list) {
		int size=list.size();
		Product prod;
		for(int i=0;i<size;i++) {
			// get product from the list
			prod=list.elementAt(i);
			// find if the product already exist
			int index=prodList.indexofProd(prod);
			if(index>=0) {
				// if it already exist, add quantity to existing product
				prodList.elementAt(index).setQuantity(
						prod.getQuantity()+prodList.elementAt(index).getQuantity());
				// remove the product from the source list
				list.remove(prod);
				// don't forget to decrement the size of the list
				size--;
			}
		}
		// add all remaining products
		prodList.addAll(list);
	}
}
