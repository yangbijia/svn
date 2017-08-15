package com.client;

import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class MyTableModel implements TableModel {

	public static String[][] value;
	public static Boolean[][] value1;
	Vector col;

	public MyTableModel(int row, int column) {
		value = new String[row][column];
		value1 = new Boolean[row][column];
		// value[0][0] = "�ļ�·��";
		// value[0][1] = "״̬";
		for (int i = 0; i < value.length - 1; i++) {

			for (int j = 0; j < value[0].length; j++) {
				value[i][j] = "";
			}
		}
		for (int i = 0; i < value1.length - 1; i++) {
			for (int j = 0; j < value1[0].length; j++) {
				value1[i][j] = false;
			}
		}
		col = new Vector();

		col.add("�ļ�·��");
		col.add("״̬");
		col.add("ѡ��");
		col.add("���");

	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getColumnClass(int c) {
		// TODO Auto-generated method stub
		// return String.class;
		return getValueAt(0, c).getClass();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return value[0].length;
	}

	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return (String) this.col.get(arg0);
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return value.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		if (column != 2)
			return value[row][column];
		else {
			// System.out.println("��ȡ��Ԫ��(" + row + "," + column + ")ֵΪ="+
			// value1[row][column]);
			return value1[row][column];
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		if (column != 2)
			return false;
		else
			return true;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValueAt(Object arg0, int row, int column) {
		// TODO Auto-generated method stub
		if (column != 2)
			value[row][column] = (String) arg0;
		else {
			value1[row][column] = (Boolean) arg0;
			// System.out.println("�õ�Ԫ��(" + row + "," + column + ")ֵΪ=" + arg0);
		}
		// value[row][column]=(boolean)arg0;
	}

}
