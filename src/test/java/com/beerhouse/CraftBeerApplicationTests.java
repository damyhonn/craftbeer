package com.beerhouse;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.junit4.SpringRunner;

import com.beerhouse.persistence.model.Beer;
import com.beerhouse.service.CraftBeerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackageClasses = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CraftBeerApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CraftBeerService service;
	
	private Beer beer;
	private Beer beer2;
	private Beer beer3;
	private Beer beer4;
	private Beer beer5;
	
	private long leftLimit = 100L;
    private long rightLimit = 200L;
	
	@Test
	public void contextLoads() {
	}

	@Before
	public void init() {
		initBeers();
	}
	
	@Test
	public void test1getBeersTest() throws JsonProcessingException, Exception {
		/* Busca sem cerveja adicionada */
		getBeer(null);
		List<Beer> beers = service.getBeers();
		Assertions.assertThat(beers.isEmpty());
		
		/* Adiciona cerveja 1 */
		postBeer(this.beer);
		
		/* Busca com cerveja adicionada */
		Beer beer = service.beerById(this.beer.getId());
		
		Assertions.assertThat(null != beer);
	}

	@Test
	public void test2addOneBeerTest() throws JsonProcessingException, Exception {
		/* Cerveja 1 já foi adicionada */
		
		List<Beer> beers = service.getBeers();
		
		Assertions.not(beers.isEmpty());
		
		/* Valida cerveja já adicionada */
		mockMvc.perform(post("/beers")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(this.beer)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void test3addTwoBeersTest() throws JsonProcessingException, Exception {
		/* Cerveja 1 já foi adicionada */
		
		List<Beer> beers = service.getBeers();
		
		Assertions.not(beers.isEmpty());
		Assertions.assertThat(beers.size() == 1);
		
		/* Adiciona cerveja 2 */
		postBeer(this.beer2);
		
		Assertions.assertThat(beers.size() == 2);
	}
	
	@Test
	public void test4findBeerByIdTest() throws JsonProcessingException, Exception {
		/* Cervejas 1 e 2 já foram adicionadas*/
		Beer beer = service.beerById(2L);
		
		Assertions.assertThat(null != beer);
		Assertions.assertThat(beer.getId() == this.beer.getId());
		
		/* Pesquisar pelo Service */
		mockMvc.perform(get("/beers/" + this.beer.getId())
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isOk());
		
		/* Pesquisar por uma ID inexistente */
		Long idAleatoria = new RandomDataGenerator().nextLong(this.leftLimit, this.rightLimit);
		
		beer = service.beerById(idAleatoria);
		Assertions.assertThat(null == beer);
	}
	
	@Test
	public void test5findBeerByInvalidIdTest() throws JsonProcessingException, Exception {
		/* Pesquisar pelo Service com ID inválida */
		mockMvc.perform(get("/beers/" + "idinvalida")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void test6putBeerTest() throws JsonProcessingException, Exception {
		putBeer(this.beer2.getId(), this.beer3);
		
		Assertions.assertThat(service.getBeers().size() == 2);
		Assertions.assertThat(service.beerById(this.beer3.getId()).equals(this.beer3));
		
		/* Put com ID inexistente */
		Long idAleatoria = new RandomDataGenerator().nextLong(this.leftLimit, this.rightLimit);
		mockMvc.perform(put("/beers/" + idAleatoria)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNotModified());
	}
	
	@Test
	public void test7putBeerInvalidIdTest() throws JsonProcessingException, Exception {
		mockMvc.perform(put("/beers/" + "idinvalido")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void test8patchBeerTest() throws JsonProcessingException, Exception {
		patchBeer(this.beer3.getId(), this.beer4);
		
		Assertions.assertThat(service.getBeers().size() == 2);
		Assertions.assertThat(service.beerById(this.beer4.getId()).equals(this.beer4));
		Assertions.assertThat(null == service.beerById(this.beer3.getId()));
		
		/* Patch com ID inexistente */
		Long idAleatoria = new RandomDataGenerator().nextLong(this.leftLimit, this.rightLimit);
		mockMvc.perform(patch("/beers/" + idAleatoria)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(this.beer)))
                .andExpect(status().isNotModified());

		this.beer4.setIngredients("Ingredientes alterados");
		patchBeer(this.beer4.getId(), this.beer4);
		Assertions.assertThat(service.beerById(this.beer4.getId()).getIngredients().equals("Ingredientes alterados"));
		
		/* Valida a tentativa de patch em cerveja inexistente na base de dados 
		 * (Nesse ponto a beer5 ainda não foi inserida na base)
		 */
		mockMvc.perform(patch("/beers/" + this.beer5.getId())
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(this.beer5)))
                .andExpect(status().isNotModified());
	}
	
	@Test
	public void test9patchBeerInvalidIdTest() throws JsonProcessingException, Exception {
		mockMvc.perform(patch("/beers/" + "idinvalido")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void test9adeleteBeerTest() throws JsonProcessingException, Exception {
		deleteBeer(this.beer4.getId());
		Assertions.assertThat(null == service.beerById(this.beer4.getId()));
	}
	
	@Test
	public void test9bdeleteBeerInvalidIdTest() throws JsonProcessingException, Exception {
		mockMvc.perform(delete("/beers/" + "idinvalido")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest());
		
		/* Pesquisar por uma ID inexistente */
		Long idAleatoria = new RandomDataGenerator().nextLong(this.leftLimit, this.rightLimit);
		
		mockMvc.perform(delete("/beers/" + idAleatoria)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(null)))
                .andExpect(status().isNotModified());
	}
	
	private void getBeer(Beer beer) throws JsonProcessingException, Exception {
		mockMvc.perform(get("/beers")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isOk());
	}
	
	private void postBeer(Beer beer) throws JsonProcessingException, Exception {
		mockMvc.perform(post("/beers")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated());
	}
	
	private void putBeer(Long id, Beer beer) throws JsonProcessingException, Exception {
		mockMvc.perform(put("/beers/" + id)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isOk());
	}
	
	private void patchBeer(Long id, Beer beer) throws JsonProcessingException, Exception {
		mockMvc.perform(patch("/beers/" + id)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isOk());
	}
	
	private void deleteBeer(Long id) throws JsonProcessingException, Exception {
		mockMvc.perform(delete("/beers/" + id)
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());
	}
	
	private void initBeers() {
		this.beer = new Beer();
		this.beer.setId(2L);
		this.beer.setName("Cavernosa");
		this.beer.setIngredients("Malte, Cevada e Lúpulo");
		this.beer.setAlcoholContent("5.5");
		this.beer.setPrice(15.5);
		this.beer.setCategory("IPA");
		
		this.beer2 = new Beer();
		this.beer2.setId(4L);
		this.beer2.setName("Estupenda");
		this.beer2.setIngredients("Malte, Beterraba, Cevada e Lúpulo");
		this.beer2.setAlcoholContent("6.5");
		this.beer2.setPrice(18.5);
		this.beer2.setCategory("SESSION APA");
		
		this.beer3 = new Beer();
		this.beer3.setId(5L);
		this.beer3.setName("Absurda");
		this.beer3.setIngredients("Trigo e Lúpulo");
		this.beer3.setAlcoholContent("4.5");
		this.beer3.setPrice(13.5);
		this.beer3.setCategory("Weiss");
		
		this.beer4 = new Beer();
		this.beer4.setId(6L);
		this.beer4.setName("Tenebrosa");
		this.beer4.setIngredients("Malte e Lúpulo");
		this.beer4.setAlcoholContent("8.5");
		this.beer4.setPrice(23.5);
		this.beer4.setCategory("Tripel");
		
		this.beer5 = new Beer();
		this.beer5.setId(7L);
		this.beer5.setName("HopMaster");
		this.beer5.setIngredients("Malte e Muito Lúpulo");
		this.beer5.setAlcoholContent("7.5");
		this.beer5.setPrice(21.5);
		this.beer5.setCategory("Quadrupel");
	}
}