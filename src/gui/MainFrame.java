package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.SocketDataTableModel;
import service.PortscannerListener;
import service.PortscannerStatus;
import service.Portscanner;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.BorderLayout;

public class MainFrame extends JFrame implements ActionListener, PortscannerListener {

	private JPanel contentPane;
	private JTextField hostTextField;
	private JTextField fromTextField;
	private JTextField toTextField;
	private JTable resultsTable;
	private JButton scanButton;
	private JButton stopButton;
	private JButton clearButton;

	private SocketDataTableModel socketDataTableModel;

	private Portscanner portscanner;
	private JTextField timeoutTextField;
	private JLabel lblThreads;
	private JTextField threadsTextField;
	private JLabel statusLabel;
	private JScrollPane scrollPane;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainFrame() {

		socketDataTableModel = new SocketDataTableModel();

		setTitle("Portscanner");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 540);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblIphost = new JLabel("IP/Host:");
		lblIphost.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIphost.setBounds(6, 20, 75, 16);
		contentPane.add(lblIphost);

		hostTextField = new JTextField();
		hostTextField.setBounds(80, 15, 240, 26);
		contentPane.add(hostTextField);
		hostTextField.setColumns(10);

		JLabel lblFrom = new JLabel("From:");
		lblFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFrom.setBounds(6, 50, 75, 16);
		contentPane.add(lblFrom);

		fromTextField = new JTextField();
		fromTextField.setBounds(80, 45, 90, 26);
		contentPane.add(fromTextField);
		fromTextField.setColumns(10);

		JLabel lblTo = new JLabel("to:");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setBounds(204, 50, 24, 16);
		contentPane.add(lblTo);

		toTextField = new JTextField();
		toTextField.setBounds(230, 45, 90, 26);
		contentPane.add(toTextField);
		toTextField.setColumns(10);

		scanButton = new JButton("Scan");
		scanButton.setBounds(332, 15, 117, 29);
		contentPane.add(scanButton);
		scanButton.addActionListener(this);

		stopButton = new JButton("Stop");
		stopButton.setBounds(332, 45, 117, 29);
		contentPane.add(stopButton);
		stopButton.addActionListener(this);

		clearButton = new JButton("Clear");
		clearButton.setBounds(330, 480, 117, 29);
		contentPane.add(clearButton);
		clearButton.addActionListener(this);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 116, 433, 352);
		contentPane.add(scrollPane);

		resultsTable = new JTable();
		scrollPane.setViewportView(resultsTable);
		resultsTable.setBorder(UIManager.getBorder("CheckBox.border"));
		resultsTable.setModel(socketDataTableModel);

		JLabel lblDelayms = new JLabel("Timeout:");
		lblDelayms.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDelayms.setBounds(172, 80, 56, 16);
		contentPane.add(lblDelayms);

		timeoutTextField = new JTextField();
		timeoutTextField.setColumns(10);
		timeoutTextField.setBounds(230, 75, 90, 26);
		contentPane.add(timeoutTextField);

		lblThreads = new JLabel("Threads:");
		lblThreads.setHorizontalAlignment(SwingConstants.RIGHT);
		lblThreads.setBounds(6, 80, 75, 16);
		contentPane.add(lblThreads);

		threadsTextField = new JTextField();
		threadsTextField.setColumns(10);
		threadsTextField.setBounds(80, 75, 90, 26);
		contentPane.add(threadsTextField);

		hostTextField.setText("google.es");
		fromTextField.setText("80");
		toTextField.setText("500");
		threadsTextField.setText("100");
		timeoutTextField.setText("1000");

		statusLabel = new JLabel("");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusLabel.setBounds(10, 485, 314, 16);
		contentPane.add(statusLabel);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if (obj instanceof JButton) {
			JButton button = (JButton) obj;

			if (button == scanButton) {
				scanButtonPressed();
			} else if (button == clearButton) {
				clearButtonPressed();
			} else if (button == stopButton) {
				stopButtonPressed();
			}
		}
	}

	private void scanButtonPressed() {
		
		try {		
			
			String ipHost = hostTextField.getText();
			int startPort = Integer.parseInt(fromTextField.getText());
			int endPort = Integer.parseInt(toTextField.getText());

			int maxThreads = Integer.parseInt(threadsTextField.getText());
			int timeout = Integer.parseInt(timeoutTextField.getText());

			portscanner = new Portscanner(ipHost,startPort,endPort,maxThreads,timeout);	
			portscanner.setListener(this);
			portscanner.start();

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
	}


	private void clearButtonPressed() {
		socketDataTableModel.clear();
		socketDataTableModel.fireTableDataChanged();
	}

	private void stopButtonPressed() {
		portscanner.stop();
		statusLabel.setText("");
	}

	@Override
	public void openSocket(String remote, int port) {
		socketDataTableModel.addOpenSocket(remote, port);
		socketDataTableModel.fireTableDataChanged();
	}
	
	@Override
	public void update(PortscannerStatus status, int currentPort, int startPort, int endPort, int openSockets) {
		
		System.out.println("Status: " + status + " current: " + currentPort + " start:" + startPort + " endPort: " + endPort + " openSocket: " + openSockets);
		
		boolean isRunning = status == PortscannerStatus.RUNNING;
		
		scanButton.setEnabled(!isRunning);
		stopButton.setEnabled(isRunning);
		
		hostTextField.setEnabled(!isRunning);
		fromTextField.setEnabled(!isRunning);
		toTextField.setEnabled(!isRunning);
		
		threadsTextField.setEnabled(!isRunning);
		timeoutTextField.setEnabled(!isRunning);
		
		if (isRunning) {
			
			float totalPorts = (float) endPort - (float) startPort;
			float percent = ((float) currentPort - (float) startPort) * 100 / totalPorts;
			
			statusLabel.setText("Looking at port " + currentPort + "... (" + String.format("%.0f",percent) + "%)");
		} else {
			statusLabel.setText("");
		}
	}
}
