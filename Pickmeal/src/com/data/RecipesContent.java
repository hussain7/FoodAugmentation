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
 * this class represent a recipes
 */
public class RecipesContent extends ProdList implements Serializable {
	private static final long serialVersionUID=-2355659560241484370L;
	private int id;
	private String name;
	private int level;
	private int cost;
	private String howto;
	/**
	 * constructor
	 * 
	 * @param id
	 * @param name
	 * @param level
	 * @param cost
	 * @param howto
	 */
	public RecipesContent(int id,String name,int level,int cost,String howto) {
		super();
		this.id=id;
		this.name=name;
		this.level=level;
		this.cost=cost;
		this.howto=howto;
	}
	/**
	 * add a product to the list
	 * 
	 * @param prod a product
	 */
	public void addProduct(Product prod) {
		if(prod!=null)
			prodList.add(prod);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name=name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id=id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level=level;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost=cost;
	}
	public String getHowto() {
		return howto;
	}
	public void setHowto(String howto) {
		this.howto=howto;
	}
	@Override
	public String toString() {
		return name;
	}
}
