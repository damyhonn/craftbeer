package com.beerhouse.web.control;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.beerhouse.exception.BeerAlreadyExistsException;
import com.beerhouse.exception.InvalidIdException;
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
	private ResponseEntity<String> add(@Valid @RequestBody Beer beer) {

		URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(beer.getId())
                .toUri();
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setLocation(location);
	    
		Beer beerAdded;

		try {
			beerAdded = craftBeerService.addBeer(beer);
			if (null != beerAdded) {
				logger.info("Cerveja(s) adicionada(s): {}", beer);
			} else {
				logger.info("Não foi possível adicionar a cerveja.");
				return new ResponseEntity<String>("Cerveja não adicionada.", responseHeaders, HttpStatus.NOT_MODIFIED);
			}		
		} catch (BeerAlreadyExistsException e) {
			logger.info("Cerveja já existe na base de dados.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja já existe na base de dados.");
		}

		return new ResponseEntity<String>("Cerveja adicionada.", responseHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value="/beers/{id}", method=RequestMethod.GET)
	private Beer searchById(@PathVariable String id) throws InvalidIdException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			logger.info("ID inválido.");
			throw new InvalidIdException("ID inválido.");
		}

		Beer beer =  craftBeerService.beerById(idLong);

		if (null != beer) {
			logger.info("Cerveja retornada: {}", beer);
		} else {
			logger.info("Não foi encontrada cerveja para o id informado.");
		}

		return beer;
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.PUT)
	private ResponseEntity<String> put(@PathVariable String id, @Valid @RequestBody Beer beer) throws InvalidIdException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			logger.info("ID inválido.");
			throw new InvalidIdException("ID inválido.");
		}

		Beer beerFound = craftBeerService.beerById(idLong);

		if (null != beerFound) {
			beerFound = craftBeerService.putBeer(beerFound, beer);
			logger.info("Cerveja atualizada: {}", beer);
		} else {
			logger.info("Cerveja com id={} não encontrada.", idLong);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não encontrada.");
		}		

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja atualizada.");
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.PATCH)
	private ResponseEntity<String> patch(@PathVariable String id, @Valid @RequestBody Beer beer) throws InvalidIdException {
	    
		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			logger.info("ID inválido.");
			throw new InvalidIdException("ID inválido.");
		}
		
		/*
		 * Verifica se o id da cerveja a ser atualizada já existe na base de dados
		 */
		if (null != craftBeerService.beerById(beer.getId())) {
			logger.info("ID já existe.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("ID já existe.");
		}
		
		/*
		 * Verifica se o id informado é de uma cerveja existente na base de dados
		 */
		Beer beerIdFound = craftBeerService.beerById(idLong);

		if (null != beerIdFound) {
			beerIdFound = craftBeerService.patchBeer(beer);
			logger.info("Cerveja atualizada: {}", beer);
		} else {
			logger.info("Cerveja não encontrada.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não encontrada.");
		}

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja atualizada.");
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.DELETE)
	private ResponseEntity<String> delete(@PathVariable String id) throws InvalidIdException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			logger.info("ID inválido.");
			throw new InvalidIdException("ID inválido.");
		}

		Long idDeleted = craftBeerService.removeBeerById(idLong);
		if (idDeleted.compareTo(0L) > 0) {
			logger.info("Cerveja removida: id={}", idDeleted);
		} else {
			logger.info("Não foi possível excluir a cerveja.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não adicionada.");
		}		

		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja removida.");
	}
}
