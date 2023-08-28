package com.jmehta.shopme.admin.setting;

import java.util.List;

import com.jmehta.shopme.common.entity.setting.Setting;
import com.jmehta.shopme.common.entity.setting.SettingBag;

public class GeneralSettingBag extends SettingBag {

	public GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}
	
	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOL", value);
	}
	
	public void updateSitLogo(String value) {
		super.update("SITE_LOGO", value);
	}
	

}
