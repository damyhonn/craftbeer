package com.beerhouse.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="beer")
public class Beer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1693493411333577756L;

	@Id
	private Long id;
	
	private String name;
	
	private String ingredients;
	
	@Column(name="alcohol_content")
	private String alcoholContent;
	
	private Double price;
	
	private String category;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getAlcoholContent() {
		return alcoholContent;
	}

	public void setAlcoholContent(String alcoholContent) {
		this.alcoholContent = alcoholContent;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Beer [name=" + name + ", alcoholContent=" + alcoholContent + ", category=" + category + "]";
	}
	
	
		
}
