package com.egis.avia.product.opeTstComparator.model;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

public class DumpsDirectoryContent {

	private HashMap<String, File> dumpFiles;

	/**
	 * @param dumpFiles
	 */
	public DumpsDirectoryContent(File directory) {
		dumpFiles = new HashMap<String, File>();

		File[] xmlFiles = directory.listFiles(xmlFileFilter);
		for (File xmlFile : xmlFiles) {
			String callsign = xmlFile.getName().split("_")[0];
			dumpFiles.put(callsign, xmlFile);
		}
	}

	private static FilenameFilter xmlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".xml");
		}
	};

	public HashMap<String, File> getDumpFiles() {
		return dumpFiles;
	}
}
