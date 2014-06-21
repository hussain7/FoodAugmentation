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

/**
 * this class represent a product
 */
public class Product extends ProductBase implements Serializable {
	private static final long serialVersionUID=552375668006405520L;
	private int quantity;
	private int category;
	private int type1;
	private int type2;
	private int type3;
	private String unity;
	public Product(int id,String name,int quantity,int category,int type1,int type2,
			int type3,String unity) {
		super(id,name);
		this.quantity=quantity;
		this.category=category;
		this.type1=type1;
		this.type2=type2;
		this.type3=type3;
		this.unity=unity;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category=category;
	}
	public int getType1() {
		return type1;
	}
	public void setType1(int type1) {
		this.type1=type1;
	}
	public int getType2() {
		return type2;
	}
	public void setType2(int type2) {
		this.type2=type2;
	}
	public int getType3() {
		return type3;
	}
	public void setType3(int type3) {
		this.type3=type3;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity=quantity;
	}
	public String getUnity() {
		return unity;
	}
	public void setUnity(String unity) {
		this.unity=unity;
	}
	@Override
	public String toString() {
		return name+" : "+quantity+" "+unity;
	}
	public String compStr() {
		return name+":"+unity;
	}
}
