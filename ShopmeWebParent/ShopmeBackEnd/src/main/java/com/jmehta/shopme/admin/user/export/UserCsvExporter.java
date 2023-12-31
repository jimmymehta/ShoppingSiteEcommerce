package com.jmehta.shopme.admin.user.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.jmehta.shopme.admin.AbstractExporter;
import com.jmehta.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter {
	
	public void export(List<User> users, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "text/csv", "*.csv", "users");
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"User ID","E-mail","First Name","Last Name","Roles","Enabled"};
		String[] fieldMapping= {"id","email","firstName","lastName","roles","enabled"};
		
		csvWriter.writeHeader(csvHeader);
		
		for(User user: users) {
			csvWriter.write(user, fieldMapping);
		}
		
		csvWriter.close();
		
	}

}
