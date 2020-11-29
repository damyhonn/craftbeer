package com.beerhouse.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beerhouse.exception.BeerAlreadyExistsException;
import com.beerhouse.persistence.model.Beer;
import com.beerhouse.persistence.repository.BeerRepository;

@Service
public class CraftBeerServiceImpl implements CraftBeerService {

	@Autowired
	private BeerRepository beerRepository;
	
	@Override
	public List<Beer> getBeers() {
		return beerRepository.findAll();
	}

	@Override
	public Beer beerById(Long id) {
		return beerRepository.findById(id);
	}

	@Override
	public Beer addBeer(Beer beer) throws BeerAlreadyExistsException {
		if (beerRepository.exists(beer.getId())) {
			throw new BeerAlreadyExistsException("Cerveja já existe!");
		}
		return beerRepository.save(beer);
	}
	
	@Override
	public Beer putBeer(Beer beerBefore, Beer beerAfter) {
		beerRepository.delete(beerBefore);
		return beerRepository.save(beerAfter);
	}
	
	@Override
	public Beer patchBeer(Beer beer) {
		return beerRepository.save(beer);
	}

	@Override
	public Long removeBeerById(Long id) {
		beerRepository.delete(id);
		return id;
	}
}
