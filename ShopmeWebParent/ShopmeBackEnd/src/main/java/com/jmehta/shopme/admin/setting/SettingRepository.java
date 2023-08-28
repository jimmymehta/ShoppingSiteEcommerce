package com.jmehta.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jmehta.shopme.common.entity.setting.Setting;
import com.jmehta.shopme.common.entity.setting.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);
}
