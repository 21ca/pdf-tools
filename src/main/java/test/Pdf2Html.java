package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class Pdf2Html {

	public static void main(String[] args) throws Exception {
		String outputFolder = "jmeter\\";
		
		PDDocument doc = PDDocument.load(new File("D:\\Books\\Testing\\jmeter.pdf"));
		int pages = doc.getNumberOfPages();

		PDFRenderer pdfRenderer = new PDFRenderer(doc);
		new File(outputFolder).mkdir();
		for (int i = 0; i < pages; i++) {
			BufferedImage img = pdfRenderer.renderImageWithDPI(i, 180, ImageType.GRAY);
			ImageIO.write(img, "png", new File(outputFolder + i + ".png"));
			
			String previous = (i == 0 ? "" : "<a href='" + (i - 1)+ ".html'>Previous</a>  ");
			String next = (i == pages - 1 ? "" : "<a href='" + (i + 1)+ ".html'>Next</a>");
			String nav = "<h2>" + previous + next + "</h2>";
			String html = nav + "<img width=100% src='" + i + ".png'>" + nav;
			FileWriter fw = new FileWriter(outputFolder + i + ".html");
			IOUtils.write(html, fw);
			fw.close();
			System.out.println(i);
		}
		doc.close();

	}

}
