package com.jmehta.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.Country;
import com.jmehta.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {
	
	@Autowired
	private StateRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	
	@Test
	public void testCreateStatesInIndia() {
		
		Integer counryId = 1;
		Country country = entityManager.find(Country.class, counryId);
		
	//	State state = repo.save(new State("Gujarat", country));
	//	State state = repo.save(new State("Karnataka", country));
	//	State state = repo.save(new State("Punjab", country));	
	//	State state = repo.save(new State("Uttar Pradesh", country));
		
		State state = repo.save(new State("Bihar", country));
		
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
		
		
	}
	
	@Test
	public void testCreateStatesInUS() {
		
		Integer countryId = 4;
		Country country = entityManager.find(Country.class, countryId);
		
		State state = repo.save(new State("California", country));
//		State state = repo.save(new State("Texas", country));
//		State state = repo.save(new State("New York", country));	
//		State state = repo.save(new State("Washington", country));
		
	
		assertThat(state).isNotNull();
		assertThat(state.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListStatesByCountry() {
		
		Integer countryId = 4;
		
		Country country =  entityManager.find(Country.class, countryId);
		
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);
		
		listStates.forEach(System.out::println);
		
		assertThat(listStates.size()).isGreaterThan(0);
		
	}
	
	@Test
	public void testUpdateState() {
		
		Integer stateId = 2;
		String stateName = "Tamil Nadu";
		State state = repo.findById(stateId).get();
		
		state.setName(stateName);
		State updatedState = repo.save(state);
		
		assertThat(updatedState.getName()).isEqualTo(stateName);
		
	}
	
	@Test
	public void testGetState() {
		
		Integer id = 3;
		State state = repo.findById(id).get();
		
		assertThat(state).isNotNull();
	}
	
	@Test
	public void testDeleteState() {
		
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<State> findById = repo.findById(id);
		
		
		assertThat(findById.isEmpty());
	}

}
