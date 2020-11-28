package com.beerhouse.service;

import java.util.List;

import com.beerhouse.persistence.model.Beer;

public interface CraftBeerService {
	List<Beer> getBeers();
	Beer beerById(Long id);
	Beer addBeer(Beer beer) throws Exception ;
	Long removeBeerById(Long id);
}
