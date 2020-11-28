package com.beerhouse.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beerhouse.persistence.model.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long> {
//	List<Beer> findBeers();
	Beer findById(Long id);
//	Long insertBeer(Beer beer);
	Long deleteBeerById(Long id);
}
