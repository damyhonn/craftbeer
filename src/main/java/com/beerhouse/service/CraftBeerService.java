package com.beerhouse.service;

import java.util.List;

import com.beerhouse.persistence.model.Beer;
import com.beerhouse.service.exception.CraftBeerException;

public interface CraftBeerService {
	List<Beer> getBeers();
	Beer beerById(Long id);
	Beer addBeer(Beer beer) throws CraftBeerException;
	Beer putBeer(Beer beerBefore, Beer beerAfter);
	Beer patchBeer(Beer beer);
	Long removeBeerById(Long id);
}
