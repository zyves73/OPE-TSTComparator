package com.egis.avia.product.opeTstComparator.hmi;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DumpsTableRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component component = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		// Color the cell in red if it is empty
		Object o = table.getValueAt(row, column);
		if (o != null && component instanceof JLabel) {
			JLabel label = (JLabel) component;
			if (label.getText().equals("")) {
				component.setBackground(Color.RED);
			} else if (label.getText().equals("Yes")) {
				component.setBackground(Color.ORANGE);
			} else {
				if (!isSelected) {
					component.setBackground(Color.WHITE);
				}
			}
		}
		return component;
	}
}
