package com.egis.avia.product.opeTstComparator;

import javax.swing.UnsupportedLookAndFeelException;

import com.egis.avia.product.opeTstComparator.hmi.OTCMainFrame;

/**
 * 
 * @author Y. Merloz
 * 
 */
public class OpeTstComparatorMain { // NO_UCD

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		final OTCMainFrame mainFrame = new OTCMainFrame();
		mainFrame.open();
	}
}
