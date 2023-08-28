package com.jmehta.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.Role;
import com.jmehta.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	
	@Test
	public void testCreateNewUserWithOneRole() {
		
		Role roleAdmin = entityManager.find(Role.class, 1);
		
		User userJmehta = new User("xyz@gmail.com","jmehta","Jimmy","Mehta");
		
		userJmehta.addRole(roleAdmin);
		
		User savedUser = repo.save(userJmehta);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateNewUserWithTwoRole() {
		
		User userKunjan = new User("kunjan@gmail.com","kkapadia","Kunjan","Kapadia");
		Role roleEditor = new Role(3);
		Role roleAssistant1 = new Role(5);
		
		
		userKunjan.addRole(roleEditor);
		userKunjan.addRole(roleAssistant1);
		
		User savedUser = repo.save(userKunjan);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllUsers() {
		
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
		
	}
	
	@Test
	public void getUserById() {
		
		User userObj = repo.findById(1).get();
		assertThat(userObj).isNotNull();
		
	}
	
	@Test
	public void testUpdateeUserDetails() {
		User userJimmy = repo.findById(1).get();
		userJimmy.setEnabled(true);
		userJimmy.setEmail("updated@gmail.com");
		
		repo.save(userJimmy);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userKapad = repo.findById(4).get();
		
		Role roleEditor = new Role(3);
		Role roleSalesPerson = new Role(2);
		
		userKapad.getRoles().remove(roleEditor);
		userKapad.addRole(roleSalesPerson);
		
		repo.save(userKapad);
		
	}
	
	@Test
	public void testDeleteUser() {
		
		repo.deleteById(4);
		
		
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "updated@gmail.com";
		User byEmail = repo.getUserByEmail(email);
		
		assertThat(byEmail).isNotNull();
	}
	
	@Test
	public void testCountById() {
		
		Integer id = 7;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 1;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 5;
		repo.updateEnabledStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(4);
	}
	
	@Test
	public void testSearchUsers() {
		
		String keyword = "bruce";
		
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}

}
