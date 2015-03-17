package View;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import Controller.Main;
import Model.Database;
import Model.QuestionAnswer;

public class RFPView 
//				extends JFrame
{

	private JPanel contentPane;
	private JTextField searchTextField;
	private JTextArea answerTextArea;
	private Database my_database;
	private JFrame mainFrame;
	private Clipboard clipboard;
	private ImagePanel imagePanel;
	private JFileChooser myJFC;
	private boolean fileSelected;
	private JTextArea notesArea = new JTextArea();
	private ArrayList<QuestionAnswer> selectedQsList = new ArrayList<QuestionAnswer>();
	private JList<QuestionAnswer> selectedQAsList = new JList<QuestionAnswer>();
	

	public void initialize(JFrame my_mainFrame) {
		fileSelected = false;
		myJFC = new JFileChooser();
		mainFrame = my_mainFrame;
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		my_database = new Database();		
		
		/////////////////////////////////////////
		//
		//			MENU STUFF
		//
		/////////////////////////////////////////
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open...");
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save As...");
		mnFile.add(mntmSaveAs);

		JMenuItem mntmLogOut = new JMenuItem("Log out");
		mnFile.add(mntmLogOut);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("Help...");
		mnHelp.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		/////////////////////////////////////////////
		//
		//			APPLYING LAYOUT TO CONTENT PANE
		//
		/////////////////////////////////////////////
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{127, 0, 226, 66, 200, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.5, 0.0, 1.2, 0.0, 1.5, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		/////////////////////////////////////////////
		//
		//			QUESTION SEARCH AND LIST STUFF
		//
		/////////////////////////////////////////////
		searchTextField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		contentPane.add(searchTextField, gbc_textField);
		searchTextField.setColumns(10);

		final JButton btnSearch = new JButton("");
		btnSearch.setIcon(new ImageIcon(RFPView.class.getResource("/files/searchIcon.png")));
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 1;
		gbc_btnSearch.gridy = 0;
		contentPane.add(btnSearch, gbc_btnSearch);

		String[] my_list = my_database.getKeyPhrases().toArray(new String[my_database.getKeyPhrases().size()]);
		final JComboBox<String> comboBox = new JComboBox<String>(my_list);
		comboBox.setMaximumRowCount(30);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 2;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 1;
		contentPane.add(comboBox, gbc_comboBox);

		final JList<QuestionAnswer> list = new JList<QuestionAnswer>();
		list.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		list.setLayoutOrientation(JList.VERTICAL);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(400);
		list.setCellRenderer(new MyCellRenderer());


		////////////////////////////////////////////
		//
		//			TEXT AREA TO DISPLAY ANSWERS
		//
		////////////////////////////////////////////
		answerTextArea = new JTextArea();
		answerTextArea.setEditable(false);
		answerTextArea.setFocusable(true);
		answerTextArea.setRequestFocusEnabled(true);
		answerTextArea.setLineWrap(true);
		answerTextArea.setWrapStyleWord(true);
		answerTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		answerTextArea.setInheritsPopupMenu(true);
		answerTextArea.setOpaque(false);
		imagePanel = new ImagePanel();
		imagePanel.setLayout(new BorderLayout());
		imagePanel.setMinimumSize(new Dimension(100,0));
		imagePanel.add(answerTextArea, BorderLayout.CENTER);

		final JScrollPane answerScrollPane = new JScrollPane(imagePanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		splitPane.setLeftComponent(new JScrollPane(list));
		
		/*Playing with these two lines below, the scroll pane breaks the centering of the logo and makes
		 * the formatting a bit awkward where you can't read the answer very well because the scroll pane
		 * centers the contents so you can't see the first word. Bypassed by just adding the imagePanel directly*/
		
		//splitPane.setRightComponent(answerScrollPane);
		splitPane.setRightComponent(imagePanel);
		splitPane.setContinuousLayout(true);

		splitPane.setDividerLocation(0.45);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0 , 10, 10);
		gbc_splitPane.gridheight = 3;
		gbc_splitPane.gridwidth = 3;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 2;
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		contentPane.add(splitPane, gbc_splitPane);


		///////////////////////////////////////////////
		//
		//			NOTES AND SELECTED QUESTIONS
		//
		///////////////////////////////////////////////

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		//tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.weightx = 0.1;
		gbc_tabbedPane.gridheight = 4;
		gbc_tabbedPane.gridwidth = 1;
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 4;
		gbc_tabbedPane.gridy = 0;

		//final JTextArea notesArea = new JTextArea();
		notesArea.setFocusable(true);
		notesArea.setRequestFocusEnabled(true);
		notesArea.setLineWrap(true);
		notesArea.setWrapStyleWord(true);
		notesArea.setPreferredSize(new Dimension(10,10));
		tabbedPane.addTab("Notes", null, new JScrollPane(notesArea), null);
		
		//final ArrayList<QuestionAnswer> selectedQsList = new ArrayList<QuestionAnswer>(); //for storing selected questions to populate the list model
		//final JList<QuestionAnswer> selectedQAsList = new JList<QuestionAnswer>();
		selectedQAsList.setCellRenderer(new MyCellRenderer());
		
		selectedQAsList.setPreferredSize(new Dimension(10,10));
		JPanel thisPanel = new JPanel();
		thisPanel.setLayout(new BorderLayout());
		thisPanel.add(new JScrollPane(selectedQAsList), BorderLayout.CENTER);
		tabbedPane.addTab("Selected Q/A's", null, thisPanel, null);
		
		
		
		contentPane.add(tabbedPane, gbc_tabbedPane);


		///////////////////////////////////////////////////////////
		//
		//			ADDING QUESTIONS AND COPYING TO CLIPBOARD
		//
		///////////////////////////////////////////////////////////
		JButton btnAddQ = new JButton("");
		btnAddQ.setIcon(new ImageIcon(RFPView.class.getResource("/files/addQuestionIcon.png")));
		GridBagConstraints gbc_btnAddQ = new GridBagConstraints();
		gbc_btnAddQ.anchor = GridBagConstraints.SOUTH;	
		gbc_btnAddQ.insets = new Insets(0, 0, 5, 5);
		gbc_btnAddQ.gridx = 3;
		gbc_btnAddQ.gridy = 2;
		contentPane.add(btnAddQ, gbc_btnAddQ);

		JButton btnAddToClip = new JButton("");
		btnAddToClip.setIcon(new ImageIcon(RFPView.class.getResource("/files/copyToClipIcon.png")));
		GridBagConstraints gbc_btnAddToClip = new GridBagConstraints();
		gbc_btnAddToClip.anchor = GridBagConstraints.NORTH;
		gbc_btnAddToClip.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddToClip.gridx = 3;
		gbc_btnAddToClip.gridy = 3;
		contentPane.add(btnAddToClip, gbc_btnAddToClip);


		////////////////////////////////////////
		//
		//				POPUP MENU STUFF
		//
		////////////////////////////////////////
		//new popup menu
		final JPopupMenu popupMenu = new JPopupMenu();
		// menu items
		JMenuItem mntmSelectAll = new JMenuItem("Select all");
		JMenuItem mntmCopy = new JMenuItem("Copy");
		JMenuItem mntmPaste = new JMenuItem("Paste...");
		JMenuItem mntmUndo = new JMenuItem("Undo");
		//keyboard shortcuts
		mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		//populate menu
		popupMenu.add(mntmSelectAll);	
		popupMenu.add(mntmCopy);
		popupMenu.add(mntmPaste);
		popupMenu.add(mntmUndo);
		//add popup menu to proper components with helper method that adds mouse listeners respectively

		addPopup(answerTextArea, popupMenu);
		addPopup(notesArea, popupMenu);

		///////////////////////////////////////
		//
		//			FORMATTING THE FRAME
		//
		///////////////////////////////////////

		mainFrame.setContentPane(contentPane);
		mainFrame.setJMenuBar(menuBar);
		mainFrame.setResizable(true);
		mainFrame.setFocusTraversalPolicy(
				new FocusTraversalOnArray(new Component[]{searchTextField, btnSearch, comboBox, btnAddQ, btnAddToClip}));
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		



		/*******************************************************************************
		 * 
		 * 
		 * 							LISTENERS AND EVENTS
		 * 
		 * 
		 *******************************************************************************/

		//Menu listeners and actions
		mntmLogOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
				mainFrame = new JFrame();
				mainFrame.setResizable(false);
				mainFrame.setTitle("Global Business Logistics");
				mainFrame.setBounds(100, 100, 488, 329);
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.getContentPane().setLayout(null);
				mainFrame.setVisible(true);

				LoginView loginView = new LoginView();
				loginView.initializeLogin(mainFrame);
			}
		});
		
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
			
		});
		mntmSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAs();
			}
			
		});
		mntmOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				load();
			}
		});

		//RFP area listeners and actions

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<QuestionAnswer> searchResults = my_database.searchQuestionAnswers(searchTextField.getText());
				QuestionAnswer[] questionAnswerList = new QuestionAnswer[searchResults.size()];
				searchResults.toArray(questionAnswerList);
				list.setListData(questionAnswerList);
			}			
		});

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchTextField.setText((String) comboBox.getSelectedItem());
				ArrayList<QuestionAnswer> searchResults = my_database.searchQuestionAnswers(searchTextField.getText());
				QuestionAnswer[] questionAnswerList = new QuestionAnswer[searchResults.size()];
				searchResults.toArray(questionAnswerList);
				list.setListData(questionAnswerList);
			}			
		});


		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(!list.isSelectionEmpty()){
					selectedQAsList.clearSelection();
					answerTextArea.setText("");
					answerTextArea.setText(list.getSelectedValue().getAnswer());
					mainFrame.repaint();
				}
			}			
		});

		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				ArrayList<QuestionAnswer> searchResults = my_database.searchQuestionAnswers(searchTextField.getText());
				QuestionAnswer[] questionAnswerList = new QuestionAnswer[searchResults.size()];
				searchResults.toArray(questionAnswerList);
				list.setListData(questionAnswerList);
				list.setBorder(new EmptyBorder(4, 4, 4, 4));
				mainFrame.repaint();
			}			
		});

		searchTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					btnSearch.doClick();
					mainFrame.repaint();
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					searchTextField.selectAll();
					mainFrame.repaint();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				//do nothing cause we smart
			}			
		});

		searchTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				searchTextField.selectAll();
				mainFrame.repaint();
			}
		});

		btnAddQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedQsList.add(list.getSelectedValue());
				QuestionAnswer[] selectedTemp = new QuestionAnswer[selectedQsList.size()];
				selectedQsList.toArray(selectedTemp);
				selectedQAsList.setListData(selectedTemp);
				mainFrame.repaint();
			}
		});
		
		selectedQAsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(!selectedQAsList.isSelectionEmpty()){
					list.clearSelection();
					answerTextArea.setText("");
					answerTextArea.setText(selectedQAsList.getSelectedValue().getAnswer());
					mainFrame.repaint();
				}
			}			
		});

		btnAddToClip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

				if(answerTextArea.getSelectedText() == null) {
					answerTextArea.selectAll();
					clipboard.setContents(new StringSelection(answerTextArea.getSelectedText()), null);
				}
				else {
					clipboard.setContents(new StringSelection(answerTextArea.getSelectedText()), null);
				}
//				mainFrame.repaint();
			}
		});

		tabbedPane.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				tabbedPane.repaint();
				tabbedPane.revalidate();
				tabbedPane.validate();
			}
		});
		
		mntmSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(popupMenu.getInvoker().equals(answerTextArea)) {
					answerTextArea.requestFocus();
					answerTextArea.selectAll();
				}
				if(popupMenu.getInvoker().equals(notesArea)) {
					notesArea.requestFocus();
					notesArea.selectAll();
				}
				mainFrame.repaint();
			}
		});

		mntmCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(popupMenu.getInvoker().equals(answerTextArea)) {
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(new StringSelection(answerTextArea.getSelectedText()), null);
				}
				mainFrame.repaint();
			}
		});

		mntmPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(popupMenu.getInvoker().equals(notesArea)) {
					String result = "";
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable contents = clipboard.getContents(null);
					boolean hasTransferableText =
							(contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
					if (hasTransferableText) {
						try {
							result = (String)contents.getTransferData(DataFlavor.stringFlavor);
							notesArea.insert(result, notesArea.getCaretPosition());
						}
						catch (UnsupportedFlavorException | IOException e1){
							//do nothing cause we smart
						}
					}
				}
				mainFrame.repaint();
			}
		});

		mntmUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(notesArea.hasFocus()) {

				}
			}
		});
		
		//Help menu to show that user can email us for assistant
		mntmHelp.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent H) {				
				
				JOptionPane.showMessageDialog(null, 
						"For assistant: Please contact blueteamtcss360@gmail.com", 
						"Help",
						JOptionPane.PLAIN_MESSAGE);				
			}
		});
		
		//Show our site for about us and allow the user to click to open the site
		mntmAbout.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent A) {
				Object[] options = {"Open Webpage",
									"Cancel"};
				
				int n = JOptionPane.showOptionDialog(null, 
						"We are BlueTeam\n" 
						+ "For more information please visit our website\n" 
						+ "https://sites.google.com/site/blueteamtcss360/", 
						"About",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE,
						null,
						options,
						options[0]);	
				if (n == JOptionPane.YES_OPTION){
					try{
						Desktop desktop = java.awt.Desktop.getDesktop();
						URI oURL = new URI("https://sites.google.com/site/blueteamtcss360/");
						desktop.browse(oURL);
					
					}
					catch(Exception e){
						e.printStackTrace();
					}					
				}else{					
				}
			}
		});		
	}

	private void save() {
		if(fileSelected) {
			StringBuilder sb = new StringBuilder();
			try {
				PrintStream out = new PrintStream(myJFC.getSelectedFile());
				sb.append(selectedQsList.size());
				sb.append("\n");
				for(int i=0; i < selectedQsList.size(); i++) {
					sb.append(selectedQsList.get(i).getCategory());
					sb.append("\n=====\n");
					sb.append(selectedQsList.get(i).getKeyPhrases());
					sb.append("\n=====\n");
					sb.append(selectedQsList.get(i).getQuestion());
					sb.append("\n=====\n");
					sb.append(selectedQsList.get(i).getAnswer());
					sb.append("\n=====\n");
					sb.append(selectedQsList.get(i).getEditLvl());
					sb.append("\n");
				}
				sb.append(notesArea.getText());
				out.print(sb.toString());
			} catch (FileNotFoundException e) {
				saveAs();
			}
		} else {
			saveAs();
		}
	}
	
	private void saveAs() {
		int completed;
		do {
			completed = myJFC.showSaveDialog(null);
		} while (completed != JFileChooser.APPROVE_OPTION);
		fileSelected = true;
		save();
	}
	
	private void load() {
		int completed;
		do {
			completed = myJFC.showOpenDialog(null);
		} while (completed != JFileChooser.APPROVE_OPTION);
		try {
			Scanner in = new Scanner(myJFC.getSelectedFile());
			int count = in.nextInt();
			ArrayList<QuestionAnswer> QAlist = new ArrayList<QuestionAnswer>();
			for(int i = 0; i < count; i++) {
				String[] QA = {"", "", "", ""};
				String last = "";
				int edit;
				last = in.nextLine();
				while(!last.contains("=====")) {
					QA[0] += last + "\n";
					last = in.nextLine();
				}
				last = in.nextLine();
				while(!last.contains("=====")) {
					QA[1] += last+ "\n";
					last = in.nextLine();
				}
				last = in.nextLine();
				while(!last.contains("=====")) {
					QA[2] += last+ "\n";
					last = in.nextLine();
				}
				last = in.nextLine();
				while(!last.contains("=====")) {
					QA[3] += last+ "\n";
					last = in.nextLine();
				}
				edit = in.nextInt();
				QAlist.add(new QuestionAnswer(QA[0], QA[1], QA[2], QA[3], edit));
			}
			StringBuilder sb = new StringBuilder();
			while(in.hasNextLine()) {
				sb.append(in.nextLine());
			}
			selectedQsList = QAlist;
			QuestionAnswer[] selectedTemp = new QuestionAnswer[selectedQsList.size()];
			selectedQsList.toArray(selectedTemp);
			selectedQAsList.setListData(selectedTemp);
			notesArea.setText(sb.toString());
			mainFrame.repaint();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "invalid file");
			load();
		}
		fileSelected = true;
			
		
	}
	
	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
				mainFrame.repaint();
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
				mainFrame.repaint();
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
				mainFrame.repaint();
			}
		});
	}

	@SuppressWarnings("serial")
	private class ImagePanel extends JPanel{

		private BufferedImage image;
		public ImagePanel() {
			try {                
				image = ImageIO.read(Main.class.getResource("/files/smaller.png"));
			} catch (IOException ex) {
				//just toss it cause we smart
			}
		}

		@Override
		protected void paintComponent(Graphics g) {		
			
	        final Graphics2D g2d = (Graphics2D) g;
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                             RenderingHints.VALUE_ANTIALIAS_ON);
	        
			final AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
			g2d.setComposite(ac);
			super.paintComponent(g2d);
			g2d.drawImage(image, this.getWidth()/2 - image.getWidth()/2, this.getHeight()/2 - image.getHeight()/2, null);  
			final AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
			g2d.setComposite(ac2);
			mainFrame.repaint();
		}


	}
	
}
