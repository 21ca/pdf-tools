package test;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDFScaleTest {

	public static void main(String[] args) throws IOException {
		String file = "D:\\Books\\JavaScript\\js\\1.pdf";
	
		PDDocument doc = PDDocument.load(new File(file));
		for (int i = 0; i< doc.getNumberOfPages(); i++) {
			PDPage page = doc.getPage(i);
			page.setMediaBox(new PDRectangle(50, 60, page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 100));
		}
//		doc.removePage(239);
//		doc.removePage(215);
//		doc.removePage(175);
//		doc.removePage(107);
//		doc.removePage(77);

		doc.save(new File(file + ".a.pdf"));
	}

}
