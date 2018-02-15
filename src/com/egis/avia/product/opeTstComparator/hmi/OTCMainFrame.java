package com.egis.avia.product.opeTstComparator.hmi;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.egis.avia.product.opeTstComparator.model.DumpsDirectoryContent;

/**
 * IFPS extractor main frame
 * 
 * @author Y. Merloz
 * 
 */
public class OTCMainFrame extends JFrame implements ActionListener {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private File opeDir;
	private File tstDir;

	// WIDGETS
	private JTextField opeDirTxt;
	private JTextField tstDirTxt;

	private JButton opeDirBt;
	private JButton tstDirBt;

	private JTable dumpsTable;

	private JButton compareButton;
	private JButton diffButton;

	private JFileChooser opeDirChooser = new JFileChooser();
	private JFileChooser tstDirChooser = new JFileChooser();

	private File workingDir = null;

	// Objects for creation of dumps list
	private Vector<Vector<String>> dumpsTableItems = new Vector<Vector<String>>();

	private DumpsDirectoryContent opeDumps = null;
	private DumpsDirectoryContent tstDumps = null;

	/** Constructor */
	public OTCMainFrame() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setTitle("OPE/TST comparator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initContents();
	}

	/**
	 * Initialize Content Pane with Tabs
	 */
	private void initContents() {
		createFormPart();
		createCenterPart();
		createButtonsPart();
		setFocusTraversalKeysEnabled(true);
		pack();

		// init the directories choosers
		opeDirChooser = new JFileChooser();
		opeDirChooser.setDialogTitle("Select OPE dumps directory");
		opeDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		tstDirChooser = new JFileChooser();
		tstDirChooser.setDialogTitle("Select TST dumps directory");
		tstDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	}

	private void createFormPart() {
		final JPanel formPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints gc = new GridBagConstraints();

		// OPE dir selection part
		final JLabel opeDirLb = new JLabel("OPE dumps directory: ");
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.EAST;
		gc.weightx = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(5, 5, 5, 5);
		formPanel.add(opeDirLb, gc);

		opeDirTxt = new JTextField();
		opeDirTxt.setText("");
		opeDirTxt.setEditable(false);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 6;
		gc.gridx = 1;
		gc.gridy = 0;
		formPanel.add(opeDirTxt, gc);

		opeDirBt = new JButton("Browse");
		opeDirBt.addActionListener(this);
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 1;
		gc.gridx = 2;
		gc.gridy = 0;
		formPanel.add(opeDirBt, gc);

		// TST dir selection part
		final JLabel tstDirLb = new JLabel("TST dumps directory: ");
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.EAST;
		gc.weightx = 1;
		gc.gridx = 0;
		gc.gridy = 1;
		gc.insets = new Insets(5, 5, 5, 5);
		formPanel.add(tstDirLb, gc);

		tstDirTxt = new JTextField();
		tstDirTxt.setText("");
		tstDirTxt.setEditable(false);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 6;
		gc.gridx = 1;
		gc.gridy = 1;
		formPanel.add(tstDirTxt, gc);

		tstDirBt = new JButton("Browse");
		tstDirBt.addActionListener(this);
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.weightx = 1;
		gc.gridx = 2;
		gc.gridy = 1;
		formPanel.add(tstDirBt, gc);

		add(formPanel, BorderLayout.NORTH);
	}

	// the center part contains the list of dumps
	private void createCenterPart() {
		final Vector<String> dumpsTableColumns = new Vector<String>();

		dumpsTableColumns.add("OPE");
		dumpsTableColumns.add("TST");
		dumpsTableColumns.add("different ?");

		dumpsTable = new JTable(dumpsTableItems, dumpsTableColumns) {
			private static final long serialVersionUID = 1L;

			// this is to inhibit the cell modification in the table
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		dumpsTable.setAutoCreateRowSorter(true);
		dumpsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dumpsTable.setDefaultRenderer(Object.class, new DumpsTableRenderer());
		dumpsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		;
		dumpsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		;
		dumpsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
		;

		JScrollPane scrollPane = new JScrollPane(dumpsTable);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		add(scrollPane, BorderLayout.CENTER);
	}

	/** Create the button Convert part */
	private void createButtonsPart() {

		final JPanel buttonsPanel = new JPanel(new GridBagLayout());
		compareButton = new JButton("Compare");
		compareButton.addActionListener(this);
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(5, 5, 5, 5);
		buttonsPanel.add(compareButton, gc);

		diffButton = new JButton("Show differences");
		diffButton.addActionListener(this);
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 1;
		gc.gridy = 0;
		gc.insets = new Insets(5, 5, 5, 5);
		buttonsPanel.add(diffButton, gc);

		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void openOpeDirChooser() {
		// Call the File chooser
		final int answer = opeDirChooser.showOpenDialog(this);

		if (answer == JFileChooser.APPROVE_OPTION) {
			opeDir = opeDirChooser.getSelectedFile();
			opeDirTxt.setText(opeDir.getName());
			workingDir = opeDirChooser.getSelectedFile().getParentFile();
			tstDirChooser.setCurrentDirectory(workingDir);
		}
	}

	private void openTstDirChooser() {
		// Call the File chooser
		final int answer = tstDirChooser.showOpenDialog(this);

		if (answer == JFileChooser.APPROVE_OPTION) {
			tstDir = tstDirChooser.getSelectedFile();
			tstDirTxt.setText(tstDir.getName());
			workingDir = tstDirChooser.getSelectedFile().getParentFile();
			opeDirChooser.setCurrentDirectory(workingDir);
		}
	}

	/**
	 * Display the MainFrame
	 */
	public void open() {
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {

		if (event.getSource() == opeDirBt) {
			openOpeDirChooser();
		} else if (event.getSource() == tstDirBt) {
			openTstDirChooser();
		} else if (event.getSource() == compareButton) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				compareOpeTst();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setCursor(Cursor.getDefaultCursor());
		} else if (event.getSource() == diffButton) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				String selectedCallsign = dumpsTable.getValueAt(
						dumpsTable.getSelectedRow(), 0).toString();
				launchDiff(opeDumps.getDumpFiles().get(selectedCallsign),
						tstDumps.getDumpFiles().get(selectedCallsign));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void launchDiff(File opeFile, File tstFile) throws IOException {
		String diffCmd = "D:\\Program Files\\WinMergePortable\\App\\WinMerge\\WinMergeU.exe";

		if (System.getProperty("os.name").equals("Linux")) {
			diffCmd = "/usr/bin/diffuse";
		}

		ProcessBuilder builder = new ProcessBuilder(diffCmd,
				opeFile.getParent() + File.separator + "ConvertedXmls"
						+ File.separator + opeFile.getName(),
				tstFile.getParent() + File.separator + "ConvertedXmls"
						+ File.separator + tstFile.getName());
		builder.redirectErrorStream();
		Process process = builder.start();

		// this reads from the subprocess's output stream
		BufferedReader subProcessInputReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		String line = null;
		while ((line = subProcessInputReader.readLine()) != null)
			System.out.println(line);

		subProcessInputReader.close();
	}

	private void compareOpeTst() throws TransformerException, IOException {
		opeDumps = new DumpsDirectoryContent(opeDirChooser.getSelectedFile());
		tstDumps = new DumpsDirectoryContent(tstDirChooser.getSelectedFile());

		// reset the content of the table
		dumpsTableItems.removeAllElements();

		// delete the directories (on both OPE and TST) containing the transformed files,
		// and recreate them before generating the files
		deleteDir(new File(opeDirChooser.getSelectedFile().getAbsolutePath()
				+ File.separator + "ConvertedXmls"));
		deleteDir(new File(tstDirChooser.getSelectedFile().getAbsolutePath()
				+ File.separator + "ConvertedXmls"));
		new File(opeDirChooser.getSelectedFile().getAbsolutePath()
				+ File.separator + "ConvertedXmls").mkdir();
		new File(tstDirChooser.getSelectedFile().getAbsolutePath()
				+ File.separator + "ConvertedXmls").mkdir();

		// fill the table with callsigns of OPE and TST dumps
		for (Entry<String, File> entry : opeDumps.getDumpFiles().entrySet()) {
			final Vector<String> line = new Vector<String>();
			line.add(entry.getKey());
			// convert the OPE file using the XSLT file
			TransformDump(entry.getValue());
			if (tstDumps.getDumpFiles().containsKey(entry.getKey())) {
				// if TST directory contains the same flight, add it in the TST column of the table
				line.add(entry.getKey());
				// convert the TST file using the XSLT file
				TransformDump(tstDumps.getDumpFiles().get(entry.getKey()));

				// after transformation, compare the OPE and the TST files,
				// and indicates if they differ in the 3rd column of the table
				final String transformedOpeDump = entry.getValue().getParent()
						+ File.separator + "ConvertedXmls" + File.separator
						+ entry.getValue().getName();
				final String transformedTstDump = tstDumps.getDumpFiles()
						.get(entry.getKey()).getParent()
						+ File.separator
						+ "ConvertedXmls"
						+ File.separator
						+ tstDumps.getDumpFiles().get(entry.getKey()).getName();
				if (areOpeTstDumpsDifferent(transformedOpeDump,
						transformedTstDump)) {
					line.add("Yes");
				} else {
					line.add("No");
				}

			} else {
				// Otherwise, add an empty cell in the TST column, and put "N/A" in the "different?" column
				line.add("");
				line.add("N/A");
			}
			dumpsTableItems.add(line);
		}

		// refresh the dumps table
		SwingUtilities.updateComponentTreeUI(dumpsTable);
	}

	// this method returns true if OPE and TST dump files have some differences
	private boolean areOpeTstDumpsDifferent(String opeDumpFilename,
			String tstDumpFilename) throws IOException {
		boolean result = false;
		String diffCmd = "FC";
		if (System.getProperty("os.name").equals("Linux")) {
			diffCmd = "diff";
		}

		ProcessBuilder builder = new ProcessBuilder(diffCmd, opeDumpFilename,
				tstDumpFilename);
		builder.redirectErrorStream();
		Process process = builder.start();

		// this reads from the subprocess's output stream
		BufferedReader subProcessInputReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		String line = null;
		while ((line = subProcessInputReader.readLine()) != null) {
			String diffMarker = "*****";
			if (System.getProperty("os.name").equals("Linux")) {
				diffMarker = "---";
			}
			if (line.contains(diffMarker)) {
				result = true;
				break;
			}
		}

		subProcessInputReader.close();

		return result;
	}

	private void TransformDump(File originalDump) throws FileNotFoundException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			//			transformer = tFactory
			//					.newTransformer(new StreamSource(
			//							this.getClass()
			//									.getResourceAsStream(
			//											"/ExternalFlightTranfo_for_OPE_TST_comparison.xsl")));
			transformer = tFactory
					.newTransformer(new StreamSource(new FileReader(
							"ExternalFlightTranfo_for_OPE_TST_comparison.xsl")));
			transformer.transform(
					new StreamSource(originalDump),
					new StreamResult(originalDump.getParent() + File.separator
							+ "ConvertedXmls" + File.separator
							+ originalDump.getName()));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					System.out.println("Unable to delete " + children[i]);
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

}
