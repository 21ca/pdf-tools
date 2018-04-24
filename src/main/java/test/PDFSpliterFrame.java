package test;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFSpliterFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -5084055481088442254L;

	private JTextField fileInput;
	private JTextField pageInput;
	private JList<Integer> pdfPageList;
	private JImagePanel pdfView;
	private PDDocument doc;
	
	public static class JImagePanel extends JPanel implements MouseMotionListener, MouseListener {

		private static final long serialVersionUID = -4864800644674837029L;
		private Image img;
		private JScrollPane parent;
		
		public JImagePanel(Image img, JScrollPane parent) {
			super();
			this.parent = parent;
			setImage(img);
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			this.addMouseMotionListener(this);
			this.addMouseListener(this);
		}

		public void setImage(Image img) {
			this.img = img;
			repaint();		
			doLayout();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null) {
				Image tmpImage = img.getScaledInstance(parent.getWidth() - 20, -1, Image.SCALE_SMOOTH);
				Dimension size = new Dimension(tmpImage.getWidth(null), tmpImage.getHeight(null));
				setPreferredSize(size);
				setSize(size);
				g.drawImage(tmpImage, 0, 0, null);
			} else {
				g.drawString("Welcome", 50, 20);
			}
		}

		int lastY = 0;
		@Override
		public void mouseDragged(MouseEvent e) {
			drag(lastY, e.getY());
		}

		private void drag(int p1, int p2) {
			Point p = parent.getViewport().getViewPosition();
			double y = p.getY() + p1 - p2;
			p.setLocation(p.getX(), y >= 0 ? y : 0);
			parent.getViewport().setViewPosition(p);
		}


		@Override
		public void mousePressed(MouseEvent e) {
			lastY = e.getY();
		}

		public void mouseMoved(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}		
	}
	
	public PDFSpliterFrame() {
		setSize(960, 720);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("PDF Spliter");
		setLayout(new BorderLayout(5, 5));
		//Top
		JPanel topBar = new JPanel(new FlowLayout());
		fileInput = addSourceFolderInput(topBar);
		addPageInput(topBar);
		addSplitButton(topBar);
		add(topBar, BorderLayout.NORTH);
		
		//Left
		JPanel leftBar = new JPanel();
		leftBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		leftBar.setLayout(new BorderLayout(0, 0));
		leftBar.setPreferredSize(new Dimension(100, 200));
		add(leftBar, BorderLayout.WEST);
		addPdfPagetList(leftBar);
		
		//Center
		JPanel centerBar = new JPanel();
		centerBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		centerBar.setLayout(new BorderLayout(0, 0));
		add(centerBar, BorderLayout.CENTER);
		addPdfView(centerBar);
		
		setVisible(true);
//		openPdf("D:\\Books\\Design\\a.pdf");
		doLayout();
	}
	
	private void addPdfView(JPanel p) {
		JScrollPane scrollPane = new JScrollPane();
		pdfView = new JImagePanel(null, scrollPane);
		scrollPane.setViewportView(pdfView);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(400, 480));
		p.add(scrollPane);
	}

	private void addPdfPagetList(JPanel p) {
		pdfPageList = new JList<Integer>();
		pdfPageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane pane = new JScrollPane(pdfPageList);
		p.add(pane);
		
		pdfPageList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				displayPage(pdfPageList.getSelectedIndex());
			}
		});
	}

	int currentPage = -1;
	private void displayPage(int page) {
		try {
			if (page < 0 || currentPage == page) return;
			System.out.println("Display: " + page);
			currentPage = page;
			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			BufferedImage img = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			pdfView.setImage(img);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e);
		}
	}

	private void addPageInput(JPanel p) {
		JLabel label = new JLabel("Pages:");
		p.add(label);

		pageInput = new JTextField(6);
		pageInput.setText("16-275");
		p.add(pageInput);
	}

	private void addSplitButton(JPanel p) {
		JButton previewButton = new JButton("Preview");
//		p.add(previewButton);
		previewButton.addActionListener(this);

		JButton splitButton = new JButton("Split");
		p.add(splitButton);
		splitButton.addActionListener(this);
	}

	private JTextField addSourceFolderInput(JPanel p) {
		JLabel label = new JLabel("Source:");
		p.add(label);

		final JTextField input = new JTextField(30);
		input.setText("D:\\Books\\Design\\a.pdf");
		p.add(input);

		JButton button = new JButton("...");
		p.add(button);
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY | JFileChooser.OPEN_DIALOG);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Document", "pdf"));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = fileChooser.showOpenDialog(PDFSpliterFrame.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					input.setText(fileChooser.getSelectedFile().getAbsolutePath());
					openPdf(input.getText());
				}
			}
		});

		return input;
	}

	public static void main(String[] args) throws Exception {
		new PDFSpliterFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (((JButton) e.getSource()).getText().equals("Split")) {
			split();
		} else if (((JButton) e.getSource()).getText().equals("Preview")) {
			preview();
		}
	}

	private void preview() {
		try {
			String pages = pageInput.getText();

			int firstPage = Integer.MAX_VALUE;
			int lastPage = -1;
			for (String page : pages.split(";|,")) {
				page = page.trim();
				if (page.indexOf('-') > 0) {
					int start = Integer.parseInt(page.split("-")[0]) - 1;
					int end = Integer.parseInt(page.split("-")[1]) - 1;
					if (firstPage > start) {
						firstPage = start;
					}
					if (lastPage < end) {
						lastPage = end;
					}
				} else {
					int p = Integer.parseInt(page) - 1;
					if (firstPage > p) {
						firstPage = p;
					}
					if (lastPage < p) {
						lastPage = p;
					}
				}
			}
			PDFRenderer pdfRenderer = new PDFRenderer(doc);
			BufferedImage firstImage = pdfRenderer.renderImageWithDPI(firstPage, 300, ImageType.RGB);
			BufferedImage lastImage = pdfRenderer.renderImageWithDPI(lastPage, 300, ImageType.RGB);

			showImage(firstImage, "First Page");
			showImage(lastImage, "Last Page");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex);
		}
	}
	

	private void openPdf(String file) {
		DefaultListModel<Integer> pdfPageListModel = new DefaultListModel<Integer>();
		try {
			if (doc != null) {
				doc.close();
				currentPage = -1;
			}
			doc = PDDocument.load(new File(file));
			for (int i = 0; i < doc.getNumberOfPages(); i++) {
				pdfPageListModel.addElement(i + 1);
			}
			displayPage(0);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e);
		}
		pdfPageList.setModel(pdfPageListModel);
	}

	private void showImage(BufferedImage img, String title) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(720, 960);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		f.setContentPane(contentPane);
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane,BorderLayout.CENTER);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scrollPane.setViewportView(new JImagePanel(img, scrollPane));
		scrollPane.setViewportView(new JLabel(
				new ImageIcon(img.getScaledInstance(600, 800, Image.SCALE_SMOOTH))));

		f.setVisible(true);
	}

	private void split() {
		try {
			String file = fileInput.getText();
			String pages = pageInput.getText();

			PDDocument newDoc = new PDDocument();
			for (String page : pages.split(";|,")) {
				page = page.trim();
				if (page.indexOf('-') > 0) {
					int start = Integer.parseInt(page.split("-")[0]) - 1;
					int end = Integer.parseInt(page.split("-")[1]);
					for (int i = start; i < end; i++) {
						newDoc.addPage(doc.getPage(i));
					}
				} else {
					newDoc.addPage(doc.getPage(Integer.parseInt(page) - 1));
				}
			}

			newDoc.save(new File(file + ".new.pdf"));
			newDoc.close();
			JOptionPane.showMessageDialog(this, "Split to " + file + ".new.pdf");
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex);
		}
	}

}
