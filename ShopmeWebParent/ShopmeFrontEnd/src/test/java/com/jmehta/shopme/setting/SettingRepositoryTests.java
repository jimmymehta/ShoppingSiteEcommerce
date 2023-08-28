package com.jmehta.shopme.setting;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.jmehta.shopme.common.entity.setting.Setting;
import com.jmehta.shopme.common.entity.setting.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {
	
	@Autowired
	private SettingRepository repo;
	
	@Test
	public void testFindByTwoCategories() {
		
		List<Setting> findByTwoCategories = repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
		
		findByTwoCategories.forEach(System.out::println);
		
	}

}
