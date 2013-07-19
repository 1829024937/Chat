import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * �������������
 * 
 * @author ben
 * 
 */
public class Chat extends JFrame implements Runnable, ActionListener,
		ItemListener {
	private static final long serialVersionUID = 1L;

	/**
	 * ���������ı���
	 */
	JTextArea areaContent = new JTextArea();

	JTextField fieldSelfIP = new JTextField("");

	JTextField fieldOtherIP = new JTextField("");

	JTextField fieldSelfName = new JTextField("");

	JTextField fieldOtherName = new JTextField("");

	JPasswordField fieldSelfPassword = new JPasswordField("");

	JPasswordField fieldOtherPassword = new JPasswordField("");

	JTextField fieldSentence = new JTextField();

	JButton buttonSend = new JButton("����");

	JButton buttonExport = new JButton("���������¼...");

	JButton buttonSendFile = new JButton("�����ļ�");

	Thread rec2010;

	Thread rec2011;

	Thread rec2012;

	Thread sendTread;

	JCheckBox checkEncry = new JCheckBox("�շ�����");

	private static final String M_seg = "\r\t\n";

	private String tfilePath = null;

	private String tfileName = null;

	/**
	 * format������������ָ����ʱ���ʽ
	 */
	SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * ��ǰ�շ��Ƿ���Ҫ���ܼ���
	 */
	boolean isEncry = false;

	public Chat() {
		try {
			InetAddress address;
			address = InetAddress.getLocalHost();
			fieldSelfIP.setText(address.getHostAddress());
		} catch (Exception e) {
			return;
		}

		JPanel south = new JPanel();

		south.setLayout(new BorderLayout(5, 15));

		JPanel southOfSouth = new JPanel();

		JPanel centerOfSouth = new JPanel();

		centerOfSouth.setLayout(new GridLayout(3, 2, 15, 15));

		southOfSouth.setLayout(new GridLayout(1, 1));

		JPanel[] p = new JPanel[7];
		for (int i = 0; i < 7; i++) {
			p[i] = new JPanel();
			p[i].setLayout(new BorderLayout());
		}
		p[0].add(BorderLayout.WEST, new JLabel("����IP: "));
		p[0].add(BorderLayout.CENTER, fieldSelfIP);
		p[1].add(BorderLayout.WEST, new JLabel("�Է�IP: "));
		p[1].add(BorderLayout.CENTER, fieldOtherIP);
		p[2].add(BorderLayout.WEST, new JLabel("��������: "));
		p[2].add(BorderLayout.CENTER, fieldSelfName);
		p[3].add(BorderLayout.WEST, new JLabel("�Է�����:"));
		p[3].add(BorderLayout.CENTER, fieldOtherName);
		// p[4].add(BorderLayout.WEST, new JLabel("��������: "));
		// p[4].add(BorderLayout.CENTER, fieldSelfPassword);
		p[4].add(checkEncry);
		p[5].add(buttonSendFile);
		// p[5].add(BorderLayout.WEST, new JLabel("�Է�����: "));
		// p[5].add(BorderLayout.CENTER, fieldOtherPassword);
		p[6].add(BorderLayout.CENTER, fieldSentence);
		p[6].add(BorderLayout.EAST, buttonSend);
		p[6].add(BorderLayout.WEST, buttonExport);
		for (int i = 0; i < 6; i++) {
			centerOfSouth.add(p[i]);
		}

		southOfSouth.add(p[6]);

		south.add(centerOfSouth, BorderLayout.CENTER);

		south.add(southOfSouth, BorderLayout.SOUTH);

		Container con = this.getContentPane();

		con.add(south, BorderLayout.SOUTH);

		con.add(new JScrollPane(areaContent));

		areaContent.setEditable(false); // ������ʾ������Ϣ����ֻ��

		fieldSelfIP.setEditable(false); // ���ñ���IPֻ��

		buttonSend.addActionListener(this);

		buttonExport.addActionListener(this);

		buttonSendFile.addActionListener(this);

		checkEncry.addItemListener(this);
		checkEncry.setSelected(false);

		buttonSend.registerKeyboardAction(this,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
				JComponent.WHEN_IN_FOCUSED_WINDOW); // ��ӦEnter��
		this.setTitle("���������졪��UDP�˿�2010��2012��2013,TCP�˿�2011");

		/**
		 * ʹ��������ʱ����Ļ������ʾ
		 */
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int width = 550;
		final int height = 650;
		final int left = (screen.width - width) / 2;
		final int top = (screen.height - height) / 2;
		this.setLocation(left, top);
		this.setSize(width, height);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rec2010 = new Thread(this);
		rec2010.setName("2010");
		rec2011 = new Thread(this);
		rec2011.setName("2011");
		rec2012 = new Thread(this);
		rec2012.setName("2012");
		sendTread = new Thread(this);
		sendTread.setName("send");
		setVisible(true);
	}

	/**
	 * ���ӷ�������
	 * 
	 * @param port
	 *            Ҫ���ӵķ�����SocketServer�˿�
	 */
	public Socket connectSocketServer(String IP, int port) {
		try {
			Socket s = new Socket(InetAddress.getByName(IP), port);
			return s;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ��Է����ʹ����ļ�����
	 * 
	 * @param aimIP
	 * @param filePath
	 */
	public void request() {
		DatagramSocket socketSend;
		try {
			socketSend = new DatagramSocket();
			byte[] buf = this.tfileName.getBytes();
			InetAddress otherAddress = InetAddress.getByName(this.fieldOtherIP
					.getText());
			DatagramPacket packet = new DatagramPacket(buf, buf.length,
					otherAddress, 2012);
			socketSend.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean tryToSend() {
		while (true) {
			try {
				DatagramSocket socketRecieve = new DatagramSocket(2013);
				while (true) {
					byte[] buf = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socketRecieve.receive(packet);
					InetAddress address = packet.getAddress();
					String srcIP = address.getHostAddress();
					if (!srcIP.equals(this.fieldOtherIP.getText())) {
						continue;
					}
					int length = packet.getLength();
					String message = new String(buf, 0, length);

					String[] word = message.split(M_seg);
					if (word == null || word.length != 2) {
						continue;
					}
					if (!word[1].trim().equals(this.tfileName)) {
						continue;
					}
					if (word[0].equals("accept")) {
						if (sendFile()) {
							JOptionPane.showMessageDialog(this, "�ļ����ͳɹ�!");
							return true;
						} else {
							JOptionPane.showMessageDialog(this, "���͹��̳����ļ�����ʧ��!");
							return false;
						}
					} else {
						 JOptionPane.showMessageDialog(this, "�Է��ܾ����գ��ļ�����ʧ��!");
						return false;
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	// �����ļ�
	public boolean sendFile() {
		Socket s = connectSocketServer(this.fieldOtherIP.getText(), 2011);
		byte[] b = new byte[1024];
		File f = new File(this.tfilePath);
		try {
			// ���������
			DataOutputStream dout = new DataOutputStream(
					new BufferedOutputStream(s.getOutputStream()));
			// �ļ�������
			FileInputStream fr = new FileInputStream(f);
			int n = fr.read(b);
			while (n != -1) {
				// ��������д������
				dout.write(b, 0, n);
				dout.flush();
				// �ٴζ�ȡn�ֽ�
				n = fr.read(b);
			}

			// �ر���
			fr.close();
			dout.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void listen2010() {
		try {
			DatagramSocket socketRecieve = new DatagramSocket(2010);
			while (true) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socketRecieve.receive(packet);
				InetAddress address;
				address = packet.getAddress();
				String currentOtherIP = address.getHostAddress();
				int length = packet.getLength();

				/**
				 * ����
				 */
				if (isEncry) {
					buf = Chat.setAndGetEncryption(buf);
				}

				String message = new String(buf, 0, length);
				String strOtherIP = fieldOtherIP.getText().trim();
				if (currentOtherIP.trim().equals("")) {
					currentOtherIP = "???";
				}
				if (!strOtherIP.equals("") && !currentOtherIP.equals(strOtherIP)) {
					continue;
				}
				

				String strOtherName = fieldOtherName.getText().trim();
				if (strOtherName.length() == 0) {
					strOtherName = new String("???");
				}
				String strSentence = new String(message);

				this.printlnRecMessage(currentOtherIP, strOtherName, strSentence);
			}
		} catch (SocketException e) {
		} catch (Exception e) {
		}
	}

	/*
	 * �ӶԷ���ȡ�ļ�
	 */
	public boolean listen2011() {
		try {
			byte[] b = new byte[1024];
			ServerSocket ss = new ServerSocket(2011);
			while (true) {
				Socket s = ss.accept();
				// ������������s.getInputStream();
				InputStream in = s.getInputStream();
				DataInputStream din = new DataInputStream(
						new BufferedInputStream(in));

				/**
				 * ����Ҫ������ļ�
				 */
				String filePath = new String("");
				try {
					JFileChooser fileChooser = new JFileChooser(".");
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int n = fileChooser.showSaveDialog(this);
					if (n == JFileChooser.APPROVE_OPTION) {
						filePath = fileChooser.getSelectedFile().getPath();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}

				File f = new File(filePath);
				RandomAccessFile fw = new RandomAccessFile(f, "rw");

				int num = din.read(b);
				while (num != -1) {
					// ���ļ���д��0~num���ֽ�
					fw.write(b, 0, num);
					// ����num���ֽ��ٴ�д���ļ�
					fw.skipBytes(num);
					// ��ȡnum���ֽ�
					num = din.read(b);
				}
				// �ر����룬�����
				din.close();
				fw.close();
				s.close();
				JOptionPane.showMessageDialog(this, "�ļ����ճɹ�!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��Ӧ�Է��Ĵ����ļ�����
	 */
	public void response(boolean accept, String fileName, String aimIP) {
		DatagramSocket socketSend;
		try {
			socketSend = new DatagramSocket();
			String message = new String("");
			if (accept) {
				message += "accept" + M_seg + fileName;
			} else {
				message += "refuse" + M_seg + fileName;
			}

			byte[] buf = message.getBytes();
			InetAddress otherAddress = InetAddress.getByName(aimIP);
			DatagramPacket packet = new DatagramPacket(buf, buf.length,
					otherAddress, 2013);
			socketSend.send(packet);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void listen2012() {
		String srcIP = null;
		try {
			DatagramSocket socketRecieve = new DatagramSocket(2012);
			while (true) {
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socketRecieve.receive(packet);
				InetAddress address = packet.getAddress();
				srcIP = address.getHostAddress();

				int length = packet.getLength();
				String message = new String(buf, 0, length);

				int result = JOptionPane.showConfirmDialog(this, "�����" + srcIP
						+ "�������������ļ�" + message + ",�Ƿ���ܣ�");

				if (result == JOptionPane.YES_OPTION) {
					response(true, message, srcIP);
					// String fileName = new String(message);
					// if (listen2011()) {
					// JOptionPane.showMessageDialog(this, "�ļ����ճɹ�!");
					// } else {
					// JOptionPane.showMessageDialog(this, "�ļ����ճɹ�!");
					// }
				} else {
					response(false, message, srcIP);
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		String thread = Thread.currentThread().getName();
		if (thread.equals("2010")) {
			listen2010();
		} else if (thread.equals("2012")) {
			listen2012();
		} else if (thread.equals("2011")) {
			listen2011();
		} else if (thread.equals("send")) {
			tryToSend();
		}
	}

	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();

		/**
		 * �����ļ�
		 */
		if (obj == this.buttonSendFile) {
			JFileChooser chooser = new JFileChooser(".");
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.tfilePath = chooser.getSelectedFile().getAbsolutePath();
				this.tfileName = (new File(this.tfilePath)).getName();
				request();
				// this.sendTread.notify();
				// this.notifyAll();
				// new SendFile(this, fieldOtherIP.getText(), filename).send();
				// if (sf.send()) {
				// JOptionPane.showMessageDialog(this, "�ļ����ͳɹ�!");
				// } else {
				// JOptionPane.showMessageDialog(this, "�ļ�����ʧ��!");
				// }
			} else {
			}
			return;
		}

		/**
		 * ����
		 */
		if (obj == buttonSend) {
			try {
				DatagramSocket socketSend = new DatagramSocket();

				String strOtherIP = fieldOtherIP.getText().trim();
				if (strOtherIP.length() == 0) {
					String temp = this.fieldSelfIP.getText();
					int index = temp.lastIndexOf(".");
					strOtherIP = temp.substring(0, index);
					strOtherIP += ".255";
					JOptionPane.showMessageDialog(this, "�Է���IPΪ��,�����ù㲥��ʽ������Ϣ, ���͵�ַ:" + strOtherIP);
				}

				String strOtherName = fieldOtherName.getText().trim();
				if (strOtherName.length() == 0) {
					strOtherName = new String("???");
					// JOptionPane.showMessageDialog(this, "�Է������ֲ���Ϊ��");
					// return;
				}

				String strSentence = fieldSentence.getText();
				if (strSentence.length() == 0) {
					JOptionPane.showMessageDialog(this, "Ҫ���͵����ݲ���Ϊ��");
					return;
				}

				this.printlnSendMessage(strOtherIP, strOtherName, strSentence);

				byte[] buf = strSentence.getBytes();

				/**
				 * ����
				 */
				if (isEncry) {
					buf = Chat.setAndGetEncryption(buf);
				}

				InetAddress otherAddress;

				otherAddress = InetAddress.getByName(strOtherIP);

				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						otherAddress, 2010);
				socketSend.send(packet);
				fieldSentence.setText("");
			} catch (IOException e) {
			}
			return;
		}

		/**
		 * ���������¼
		 */
		if (obj == buttonExport) {
			FileDialog fileDialog = new FileDialog(this, "���������¼�Ի���",
					FileDialog.SAVE);
			fileDialog.setVisible(true);
			String filePath = fileDialog.getDirectory() + fileDialog.getFile(); // �õ��ļ�·��
			if (fileDialog.getFile() == null) {
				// JOptionPane.showMessageDialog(this, "δָ���ļ�����", "��ʾ",
				// JOptionPane.PLAIN_MESSAGE);
				return;
			}
			File f = new File(filePath.trim());
			try {
				FileWriter fw = new FileWriter(f);
				String text = areaContent.getText();
				fw.write(text);
				fw.flush();
				fw.close();
			} catch (IOException ioe) {
				return;
			}
			return;
		}
	}

	private void printlnSendMessage(String strOtherIP, String strOtherName,
			String strSentence) {

		String nowtimes = form.format(new Date());
		String show = nowtimes;
		show += "���Ҷ� ";
		show += strOtherIP;
		show += " �� ";
		show += strOtherName;
		show += " ˵�� ";
		show += strSentence + "\r\n";
		areaContent.append(show);
		areaContent.setCaretPosition(areaContent.getText().length());
		insertDB(nowtimes, this.fieldSelfIP.getText(), strOtherIP, strSentence);
	}

	private void printlnRecMessage(String srcIP, String strOtherName,
			String strSentence) {
		String nowtimes = form.format(new Date());
		String show = nowtimes;
		show += "�� ";
		show += srcIP;
		show += " �� ";
		show += strOtherName;
		show += " ����˵��";
		show += strSentence;
		show += "\r\n";
		areaContent.append(show);
		areaContent.setCaretPosition(areaContent.getText().length());
		insertDB(nowtimes, srcIP, this.fieldSelfIP.getText(), strSentence);
	}

	/**
	 * ���ܽ��ܺ���
	 */
	public static byte[] setAndGetEncryption(byte[] buf) {
		int len = buf.length;
		for (int i = 0; i < len; i++) {
			buf[i] = (byte) (255 - buf[i]);
		}
		return buf;
	}

	/**
	 * ����������������
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Chat c = new Chat();
		c.rec2010.start();
		c.rec2011.start();
		c.rec2012.start();
		c.sendTread.start();
	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		// try {
		// new Socket(InetAddress.getByName(""), 2011);
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		if (checkEncry.isSelected()) {
			this.isEncry = true;
		} else {
			this.isEncry = false;
		}
	}
	public void insertDB(String time, String srcIP, String aimIP,
			String content) {
		Connection con = null;
		PreparedStatement ps = null;
		String sentence = "insert into chat([time],[srcIP],[aimIP],[content]) values(?,?,?,?)";
		try {
			con = getConnection();
			ps = con.prepareStatement(sentence);
			ps.setString(1, time);
			ps.setString(2, srcIP);
			ps.setString(3, aimIP);
			ps.setString(4, content);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public Connection getConnection() throws Exception {
		Connection con = null;
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		con = DriverManager.getConnection("jdbc:odbc:XYchatDB");
		return con;
	}
}
