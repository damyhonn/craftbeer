package com.beerhouse.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beerhouse.persistence.model.Beer;
import com.beerhouse.persistence.repository.BeerRepository;
import com.beerhouse.service.exception.CraftBeerException;

@Service
public class CraftBeerServiceImpl implements CraftBeerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CraftBeerServiceImpl.class);
	
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
	public Beer addBeer(Beer beer) throws CraftBeerException {
		if (beerRepository.exists(beer.getId())) {
			LOGGER.info("Cerveja já existe.");
			throw new CraftBeerException("Cerveja já existe!");
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
