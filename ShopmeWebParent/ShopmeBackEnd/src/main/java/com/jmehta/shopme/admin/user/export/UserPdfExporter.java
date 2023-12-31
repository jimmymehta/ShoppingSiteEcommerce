package com.jmehta.shopme.admin.user.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.jmehta.shopme.admin.AbstractExporter;
import com.jmehta.shopme.common.entity.User;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class UserPdfExporter extends AbstractExporter{

	public void export(List<User> listOfUsers, HttpServletResponse response) throws IOException {
		
		super.setResponseHeader(response, "application/pdf", ".pdf", "users");
		
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());
		
		document.open();
		
		Font font = new Font(Font.HELVETICA, 18, Font.BOLDITALIC, Color.BLUE);
		
		Paragraph para = new Paragraph("List of users", font);
		para.setAlignment(Paragraph.ALIGN_CENTER);
		
		document.add(para);
		
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		
		writeTableHeader(table);
		writeTableData(table, listOfUsers);
		
		document.add(table);
		document.close();
		
	}

	private void writeTableHeader(PdfPTable table) {
		
		PdfPCell cell = new PdfPCell();
		
		cell.setBackgroundColor(Color.blue);
		cell.setPadding(5);
		
		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);
		
		cell.setPhrase(new Phrase("User ID", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("E-mail", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("First Name", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Last Name", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Roles", font));
		table.addCell(cell);
		
		cell.setPhrase(new Phrase("Enabled", font));
		table.addCell(cell);
		
	}
	
	private void writeTableData(PdfPTable table, List<User> listUsers) {
		for(User user: listUsers) {
			table.addCell(String.valueOf(user.getId()));
			table.addCell(user.getEmail());
			table.addCell(user.getFirstName());
			table.addCell(user.getLastName());
			table.addCell(user.getRoles().toString());
			table.addCell(String.valueOf(user.isEnabled()));
			
		}
		
		
	}

}
