package com.beerhouse.web.resource;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.beerhouse.exceptionhandler.CraftBeerExceptionHandler;
import com.beerhouse.persistence.model.Beer;
import com.beerhouse.service.CraftBeerService;
import com.beerhouse.service.exception.CraftBeerException;

@RestController
@RequestMapping(value="/")
public class CraftBeerResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(CraftBeerResource.class);

	@Autowired
	private CraftBeerService craftBeerService;

	@RequestMapping(value="/beers", method=RequestMethod.GET)
	private List<Beer> search() {
		return craftBeerService.getBeers();
	}

	@RequestMapping(value="/beers", method = RequestMethod.POST)
	private ResponseEntity<String> add(@Valid @RequestBody Beer beer) {
		craftBeerService.addBeer(beer);
		return ResponseEntity.status(HttpStatus.CREATED).location(getLocation(beer)).body("Cerveja adicionada");
	}

	@RequestMapping(value="/beers/{id}", method=RequestMethod.GET)
	private Beer searchById(@PathVariable String id) throws CraftBeerException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			LOGGER.info("ID inválido.");
			throw new CraftBeerException("ID inválido.");
		}

		return craftBeerService.beerById(idLong);
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.PUT)
	private ResponseEntity<String> put(@PathVariable String id, @Valid @RequestBody Beer beer) throws CraftBeerException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			LOGGER.info("ID inválido.");
			throw new CraftBeerException("ID inválido.");
		}

		Beer beerFound = craftBeerService.beerById(idLong);

		if (null != beerFound) {
			beerFound = craftBeerService.putBeer(beerFound, beer);
			LOGGER.info("Cerveja atualizada: {}", beer);
		} else {
			LOGGER.info("Cerveja com id={} não encontrada.", idLong);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não encontrada.");
		}		

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja atualizada.");
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.PATCH)
	private ResponseEntity<String> patch(@PathVariable String id, @Valid @RequestBody Beer beer) throws CraftBeerException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			LOGGER.info("ID inválido.");
			throw new CraftBeerException("ID inválido.");
		}

		/*
		 * Verifica se o id da cerveja a ser atualizada é diferente da que foi
		 * passada como parâmetro e se já existe na base de dados outra cerveja com o mesmo id
		 */
		if (!idLong.equals(beer.getId())) {
			if (null != craftBeerService.beerById(beer.getId())) {
				LOGGER.info("ID já existe.");
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.contentType(MediaType.TEXT_PLAIN)
						.body("ID já existe.");
			}
			/* Caso a ID da cerveja não existe, ela substituirá a anterior (idLong) */
			craftBeerService.removeBeerById(idLong);
			craftBeerService.patchBeer(beer);
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja atualizada.");
		}

		/*
		 * Verifica se o id informado é de uma cerveja existente na base de dados
		 */
		Beer beerIdFound = craftBeerService.beerById(idLong);

		if (null != beerIdFound) {
			beerIdFound = craftBeerService.patchBeer(beer);
			LOGGER.info("Cerveja atualizada: {}", beer);
		} else {
			LOGGER.info("Cerveja não encontrada.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não encontrada.");
		}

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja atualizada.");
	}

	@RequestMapping(value="/beers/{id}", method = RequestMethod.DELETE)
	private ResponseEntity<String> delete(@PathVariable String id) throws CraftBeerException {

		Long idLong = new Long("0");

		try {
			idLong = Long.parseLong(id);
		} catch (NumberFormatException e) {
			LOGGER.info("ID inválido.");
			throw new CraftBeerException("ID inválido.");
		}
		
		/* Verifica se a ID a ser excluída existe na base de dados */
		if (null == craftBeerService.beerById(idLong)) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Não foi encontrada cerveja para o ID fornecido.");
		}

		Long idDeleted = craftBeerService.removeBeerById(idLong);
		if (idDeleted.compareTo(0L) > 0) {
			LOGGER.info("Cerveja removida: id={}", idDeleted);
		} else {
			LOGGER.info("Não foi possível excluir a cerveja.");
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Cerveja não removida.");
		}		

		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Cerveja removida.");
	}

	@ExceptionHandler({CraftBeerException.class})
	public ResponseEntity<Object> handleCraftBeerException(CraftBeerException ex) {

		final String mensagemUsuario = ex.getMessage();
		final String mensagemDev = ExceptionUtils.getRootCauseMessage(ex);

		final List<CraftBeerExceptionHandler.Erro> erros = Arrays.asList(new CraftBeerExceptionHandler.Erro(mensagemUsuario, mensagemDev));

		return ResponseEntity.badRequest().body(erros);
	}

	/*
	 * Retorna a URI location
	 */
	private URI getLocation(Beer beer) {
		return ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(beer.getId())
				.toUri();
	}
}
