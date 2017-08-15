package com.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputListener;
import javax.swing.table.TableCellRenderer;

public class Client extends JFrame {

	public static void main(String[] args) {
		Client client = new Client();
		client.initFrame();
	}

	Socket socket;
	JTextField ip, port, path;
	boolean flag = true;
	ClientThread ct;
	JTable table;
	MyTableModel model;
	ClientFileInfo cfi;
	protected JMenuItem menuItem1 = new JMenuItem("����");

	// protected JMenuItem menuItem2 = new JMenuItem();

	/**
	 * ��ʼ������
	 */
	public void initFrame() {
		setSize(500, 470);
		setTitle("---SVN---");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLayout(new FlowLayout(1, 20, 10));

		JLabel lb_ip = new JLabel(" IP��ַ ");
		JLabel lb_port = new JLabel(" �˿ں� ");
		ip = new JTextField(14);
		// ip.setText("172.22.65.2");
		ip.setText("192.168.0.115");
		port = new JTextField(10);
		port.setText("7979");
		JLabel lb_path = new JLabel(" ����·�� ");
		path = new JTextField(18);
		path.setText("C:\\client");
		// tf.setText(fileChooser.getSelectedFile().getPath().replaceAll("\\\\",
		// "\\\\\\\\"));
		JButton bt_load = new JButton("����");
		JButton bt_update = new JButton("����");
		JButton bt_summit = new JButton("�ύ");

		model = new MyTableModel(40, 3);

		table = new JTable(model) {
			// public Component getTableCellRenderComponent(JTable table,
			// Object value, boolean isSelected, boolean hasFocus,
			// int row, int column) {
			// return this;
			// }
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component component = super.prepareRenderer(renderer, row,
						column);
				JComponent jc = (JComponent) component;
				jc.setBackground(new Color(100, 223, 222));
				return component;
			}
		};
		// TableColumn tc = new TableColumn();
		// tc.setHeaderValue("ѡ��");
		// tc.setModelIndex(2);
		// table.addColumn(tc);
		table.setRowHeight(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		table.getColumnModel().getColumn(2).setPreferredWidth(30);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// MouseInputListener mouseInputListener =
		// getMouseInputListener(table);// �������Ҽ�ѡ����
		// ���ļ���
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu popupmenu = new JPopupMenu();
					popupmenu.add(menuItem1);
					popupmenu.show(e.getComponent(), e.getX(), e.getY());
				}
				// �õ�ѡ�е����е�����ֵ
				int r = table.getSelectedRow();
				int c = table.getSelectedColumn();
				if (c == 2) {
					// System.out.println("�õ�Ԫ��ֵΪ��");
					if ((boolean) table.getValueAt(r, c))
						table.setValueAt(false, r, c);
					else
						table.setValueAt(true, r, c);
				}
			}
		});
		JScrollPane jsp = new JScrollPane(table);
		jsp.setPreferredSize(new Dimension(450, 300));

		// ���أ����£��ύ��ť�ļ���
		ActionListener al = new ActionListener() {
			Load load = null;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// �����ļ���Ϣ
				if (e.getSource() == bt_load) {
					if (flag) {
						// Connect();
						try {
							socket = new Socket(getIP(), getPort());
							ct = new ClientThread(socket);
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null, "IP��˿ڴ��󡣡�");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null, "���ӷ�����ʧ�ܡ���");
						}
						flag = false;
					}
					try {
						ClientFileInfo.addFile(path.getText(), InetAddress
								.getLocalHost().getHostAddress());
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "���ʱ����ļ�ʧ�ܡ���");
					}
					load = new Load(socket, path.getText(), ct);
					try {
						ClientFileInfo.serialize();
						ClientFileInfo.deserialize();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Map map = ClientFileInfo.getFileInfo();
					Set set = map.keySet();
					Iterator it = set.iterator();
					int i = 0;
					while (it.hasNext()) {
						Object key = it.next();
						Object value = map.get(key);
						FileVersionInfo fvi = (FileVersionInfo) value;
						table.setValueAt(path.getText() + "\\" + key, i, 0);
						if (fvi.fileStatus == 0) {
							table.setValueAt("����", i, 1);
						} else if (fvi.fileStatus == 1) {
							table.setValueAt("һ��", i, 1);
						} else if (fvi.fileStatus == 2) {
							table.setValueAt("�ύ", i, 1);
						} else if (fvi.fileStatus == 3) {
							table.setValueAt("��ͻ", i, 1);
						}
						i++;
					}
				}
				// �����ļ�
				if (e.getSource() == bt_update) {
					if (!flag) {
						// Map map = ClientFileInfo.getFileInfo();
						int r = table.getSelectedRow();
						int c = table.getSelectedColumn();
						// �õ�ѡ�еĵ�Ԫ���ֵ������ж����ַ���
						Object values = table.getValueAt(r, 0);
						String info = values.toString();
						if (!"".equals(info)) {
							StringBuffer str = new StringBuffer(info);
							int index = str.indexOf("\\");
							while (index != -1) {
								str.substring(0, index);
								str.delete(0, index + 1);
								// System.out.println("str=" + str);
								index = str.indexOf("\\");
							}

							Update update = new Update(ct, socket,
									str.toString(), path.getText());
							update.Update();
							try {
								ClientFileInfo.serialize();
								ClientFileInfo.deserialize();
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog(null, "��δ���ء���");
						}
					}
				}
				// �ύ�ļ�
				if (e.getSource() == bt_summit) {
					if (!flag) {
						int r = table.getSelectedRow();
						int c = table.getSelectedColumn();
						// �õ�ѡ�еĵ�Ԫ���ֵ������ж����ַ���
						Object values = table.getValueAt(r, 0);
						String info = values.toString();
						if (!"".equals(info)) {
							StringBuffer str = new StringBuffer(info);
							int index = str.indexOf("\\");
							while (index != -1) {
								str.substring(0, index);
								str.delete(0, index + 1);
								// System.out.println("str=" + str);
								index = str.indexOf("\\");
							}
							// tf.setText(fileChooser.getSelectedFile().getPath()
							// .replaceAll("\\\\", "\\\\\\\\"));
							Submit submit = new Submit(ct, socket,
									str.toString(), path.getText().replaceAll(
											"\\\\", "\\\\\\\\")
											+ "\\" + str.toString());
							submit.Submit();
							// File file = new File("C:/ClientFileInfo.txt");
							try {
								ClientFileInfo.serialize();
								ClientFileInfo.deserialize();
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog(null, "��δ���ء���");
						}
					}
				}
				// ��ȡ�ļ�����
				if (e.getSource() == menuItem1) {
					int r = table.getSelectedRow();
					int c = table.getSelectedColumn();
					// �õ�ѡ�еĵ�Ԫ���ֵ������ж����ַ���
					Object values = table.getValueAt(r, c);
					String info = values.toString();
					if (!"".equals(info)) {
						StringBuffer str = new StringBuffer(info);
						int index = str.indexOf("\\");
						while (index != -1) {
							str.substring(0, index);
							str.delete(0, index + 1);
							// System.out.println("str=" + str);
							index = str.indexOf("\\");
						}
						Map map = ClientFileInfo.getFileInfo();
						Set set = map.keySet();
						Iterator it = set.iterator();
						while (it.hasNext()) {
							String key = (String) it.next();
							Object value = map.get(key);
							FileVersionInfo fvi = (FileVersionInfo) value;
							System.out.println("str=" + str + ",key=" + key);
							if (key.equals(str.toString())) {
								System.out.println("key��str���");
								String hisVersion = "";
								for (int k = 0; k < fvi.hisFileVersionInfo
										.size(); k++) {
									System.out.println("sssssssssss");
									if (k != fvi.hisFileVersionInfo.size() - 1)
										hisVersion += fvi.hisFileVersionInfo
												.get(k) + ",";
									else
										hisVersion += fvi.hisFileVersionInfo
												.get(k);
								}
								System.out.println("ssss�ļ�״̬");
								String status = "";
								if (fvi.fileStatus == 0) {
									status = "����";
								} else if (fvi.fileStatus == 1) {
									status = "һ��";
								} else if (fvi.fileStatus == 2) {
									status = "�ύ";
								} else if (fvi.fileStatus == 3) {
									status = "��ͻ";
								}
								System.out.println("----------");
								JOptionPane.showMessageDialog(null, "�ļ���" + key
										+ "\r\n��ǰ�汾��" + fvi.version
										+ "\r\n�޸��ˣ�" + fvi.modifyUser
										+ "\r\n��ʷ�汾��" + hisVersion
										+ "\r\n�ļ�״̬��" + status);
								break;
							}
						}
					} else
						JOptionPane.showMessageDialog(null, "����û���ļ�Ŷ����");
				}
			}
		};

		bt_load.addActionListener(al);
		bt_update.addActionListener(al);
		bt_summit.addActionListener(al);
		menuItem1.addActionListener(al);
		this.add(lb_ip);
		this.add(ip);
		this.add(lb_port);
		this.add(port);
		this.add(lb_path);
		this.add(path);
		this.add(bt_load);
		this.add(jsp);
		this.add(bt_update);
		this.add(bt_summit);

		this.setVisible(true);

		// ��ʼ���ļ���Ϣ
		setFileInfo();
	}

	private MouseInputListener getMouseInputListener(JTable table) {
		return new MouseInputListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				processEvent(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				processEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				processEvent(e);
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
				/* && !e.isControlDown() && !e.isShiftDown() */) {
					JPopupMenu popupmenu = new JPopupMenu();
					popupmenu.add(menuItem1);
					popupmenu.show(e.getComponent(), e.getX(), e.getY());
					// �Ҽ��˵���ʾ
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				processEvent(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				processEvent(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				processEvent(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				processEvent(e);
			}

			private void processEvent(MouseEvent e) {
				if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
					int modifiers = e.getModifiers();
					modifiers -= MouseEvent.BUTTON3_MASK;
					modifiers |= MouseEvent.BUTTON1_MASK;
					MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), modifiers, e.getX(), e.getY(),
							e.getClickCount(), false);
					table.dispatchEvent(ne);
				}
			}
		};
	}

	private void setFileInfo() {
		// System.out.println("InetAddress.getLocalHost().getHostAddress()="
		// + InetAddress.getLocalHost().getHostAddress());
		// ClientFileInfo.getFileInfo();
		try {
			File file = new File("C:/ClientFileInfo.txt");
			if (file.exists()) {
				try {
					ClientFileInfo.getFileInfo();
					ClientFileInfo.deserialize();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				ClientFileInfo.getFileInfo();
				ClientFileInfo.init(path.getText(), InetAddress.getLocalHost()
						.getHostAddress());
				ClientFileInfo.serialize();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String getIP() {
		String ip = this.ip.getText();
		return ip;
	}

	int getPort() {
		int port = Integer.parseInt(this.port.getText());
		return port;
	}

	public void Connect() {
		try {
			socket = new Socket(getIP(), getPort());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
