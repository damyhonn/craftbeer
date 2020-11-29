package com.beerhouse.service;

import java.util.List;

import com.beerhouse.exception.BeerAlreadyExistsException;
import com.beerhouse.persistence.model.Beer;

public interface CraftBeerService {
	List<Beer> getBeers();
	Beer beerById(Long id);
	Beer addBeer(Beer beer) throws BeerAlreadyExistsException;
	Beer putBeer(Beer beerBefore, Beer beerAfter);
	Beer patchBeer(Beer beer);
	Long removeBeerById(Long id);
}
