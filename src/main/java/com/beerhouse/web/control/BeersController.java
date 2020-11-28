package com.beerhouse.web.control;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.beerhouse.persistence.model.Beer;
import com.beerhouse.service.CraftBeerService;

@RestController
@RequestMapping(value="/")
public class BeersController {

	private static final Logger logger = LoggerFactory.getLogger(BeersController.class);
	
	@Autowired
	private CraftBeerService craftBeerService;
	
	@RequestMapping(value="/beers", method=RequestMethod.GET)
	private List<Beer> search() {
		List<Beer> beers = craftBeerService.getBeers();
		
		if (beers.size() > 0) {
			logger.info("Cerveja(s) retornada(s) ({}): {}", beers.size(), beers);
		} else {
			logger.info("Cervejas retornadas: 0");
		}
		return beers;
	}
	
	@RequestMapping(value="/beers", method = RequestMethod.POST)
	private ResponseEntity<String> add(@Valid @RequestBody Beer beer) throws Exception {
		Beer beerAdded = craftBeerService.addBeer(beer);
		
		if (null != beerAdded) {
			logger.info("Cerveja(s) adicionada(s): {}", beer);
		} else {
			logger.info("Não foi possível adicionar a cerveja.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
		       .contentType(MediaType.TEXT_PLAIN)
		       .body("Cerveja adicionada.");
		}		
		return ResponseEntity.status(HttpStatus.CREATED)
			       .contentType(MediaType.TEXT_PLAIN)
			       .body("Cerveja adicionada.");
	}
}
