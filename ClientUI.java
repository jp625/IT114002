import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.BorderLayout;

public class ClientUI extends JFrame{
	private static final long serialVersionUID = -6625037986217386003L;
	int count = 0;
	static ObjectOutputStream out;
	public JButton[][] board;
	private JPanel buttons;
	private JPanel chatPanel, chatArea, userInput;
	private JTextArea textArea, gameStats;
	private JTextField textField;
	private JButton Button;
	private ButtonListener Listener;
	private String player;
	private int[][] intBoard;
	private String name;
	private boolean YourTurn;
	public Payload p;
	Socket server;
	public ClientUI() {
		JFrame frame = new JFrame("Tic-Tac-Toe");
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		buttons = new JPanel();
		GridLayout grid = new GridLayout(3,3);
		buttons.setLayout(grid);
		board = new JButton[3][3];
		intBoard = new int[3][3];
		Listener = new ButtonListener();
		name = JOptionPane.showInputDialog("Username: ");
		player = JOptionPane.showInputDialog("Enter either  1 for Player 1 OR 2 for Player 2: ");
		while(!player.equals("1") && !player.equals("2")) {
			player = JOptionPane.showInputDialog("Enter either 1 for Player 1 OR 2 for Player 2: ");
		}
		if(player.equals(1) || player.equals(2)){
			for(int row = 0; row < 3; row++) {
				for(int col = 0; col < 3; col++) {
					board[row][col] = new JButton();
					board[row][col].setSize(100,100);
					board[row][col].setFont(new Font("Arial", Font.PLAIN, 80));
					buttons.add(board[row][col]);
					intBoard[row][col] = 0;
				}
			}
		} else {
			if(player.equals("1")) {
				YourTurn = true;
			} else {
				YourTurn = false;
			}
			for(int row = 0; row < 3; row++) {
				for(int col = 0; col < 3; col++) {
					board[row][col] = new JButton();
					board[row][col].setSize(100,100);
					board[row][col].addActionListener(Listener);
					board[row][col].setFont(new Font("Arial", Font.PLAIN, 70));
					board[row][col].setBackground(Color.white);
					buttons.add(board[row][col]);

					intBoard[row][col] = 0;
				}
			}
		}
		gameStats = new JTextArea();
		chatPanel = new JPanel();
		chatPanel.setPreferredSize(new Dimension(200,200));
		chatPanel.setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setText("");
		chatArea = new JPanel();
		chatArea.setLayout(new BorderLayout());
		chatArea.add(textArea, BorderLayout.CENTER);
		chatArea.setBorder(BorderFactory.createLineBorder(Color.gray));
		chatPanel.add(chatArea, BorderLayout.CENTER);
		userInput = new JPanel();
		textField = new JTextField();//start
		textField.setPreferredSize(new Dimension(100,30));
		Button = new JButton("Button");
		Button.setPreferredSize(new Dimension(100,30));
		chatPanel.add(userInput, BorderLayout.SOUTH);
		cp.add(chatPanel, BorderLayout.SOUTH);//end
		cp.add(buttons, BorderLayout.CENTER);
		cp.add(gameStats, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(600, 500);
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  	close();
				    System.exit(0);
				  }
		});
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == Button) {
				Payload p = new Payload();
				p.setPayloadType(PayloadType.MESSAGE);
				p.setMessage(textField.getText());
				try {
					out.writeObject(p);
				} catch(Exception e) {
					e.printStackTrace();
				}
				textField.setText("");
			} else {
				if(YourTurn) {
					for(int i = 0; i < 3; i++) {
						for(int j = 0; j < 3; j++) {
							if(event.getSource() == board[i][j]) {
								intBoard[i][j] = Integer.parseInt(player);
								if(player.equals("1")) {
									board[i][j].setText("X");
									board[i][j].setBackground(Color.yellow);
								} else {
									board[i][j].setText("O");
									board[i][j].setBackground(Color.orange);
								}
								board[i][j].setEnabled(false);
								p = new Payload();
								p.setPayloadType(PayloadType.XORO);
								p.setXorO(board);
								try {
									out.writeObject(p);
								} catch(Exception e) {
									e.printStackTrace();
								}
								Check t = new Check();
								if(t.isWin(intBoard, Integer.parseInt(player))) {
									JOptionPane.showMessageDialog(null, "You Won");
									Payload payload = new Payload();
									payload.setPayloadType(PayloadType.MESSAGE);
									payload.setMessage("Player " + player + " wins");
									try {
										out.writeObject(p);
									} catch(Exception e) {
										e.printStackTrace();
									}
									resetBoard();
								} else if(t.isTie(intBoard)) {
									JOptionPane.showMessageDialog(null, "It's a tie");
									Payload payload = new Payload();
									payload.setPayloadType(PayloadType.MESSAGE);
									payload.setMessage("It's A Tie");
									try {
										out.writeObject(p);
									} catch(Exception e) {
										e.printStackTrace();
									}
									resetBoard();
								}
							}
						}
					}
				}
			}
		}
	}
	public void connect(String address, int port) {
		try {
			server = new Socket(address, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void start() throws IOException {
		if(server == null) {
			return;
		}
		try(Scanner scan = new Scanner(System.in);
				ObjectInputStream in = new ObjectInputStream(server.getInputStream());) {
			out = new ObjectOutputStream(server.getOutputStream());
			while(!server.isClosed() && name != null && name.length() == 0);
			Payload p = new Payload();
			p.setPayloadType(PayloadType.CONNECT);
			p.setMessage(name);
			out.writeObject(p);
			//Thread to listen for keyboard input so main thread isn't blocked
			Thread inputThread = new Thread() {
				@Override
				public void run() {
					try {
						while(!server.isClosed()) {
							System.out.println("Waiting for input");
							String line = scan.nextLine();
							if(!"quit".equalsIgnoreCase(line) && line != null) {
								
							}
							else {
								textArea.append("Stopping input thread");
								Payload p = new Payload();
								p.setPayloadType(PayloadType.DISCONNECT);
								p.setMessage("Bye");
								out.writeObject(p);
								break;
							}
						}
					}
					catch(Exception e) {
						textArea.append("Client shutdown\n");
					}
					finally {
						close();
					}
				}
			};
			inputThread.start();//start the thread
			
			//Thread to listen for responses from server so it doesn't block main thread
			Thread fromServerThread = new Thread() {
				@Override
				public void run() {
					try {
						Payload fromServer;
						//while we're connected, listen for payloads from server
						while(!server.isClosed() && (fromServer = (Payload)in.readObject()) != null) {
							//System.out.println(fromServer);
							processPayload(fromServer);
						}
						textArea.append("Stopping server listen thread\n");
					}
					catch (Exception e) {
						if(!server.isClosed()) {
							e.printStackTrace();
							textArea.append("Server closed connection\n");
						}
						else {
							textArea.append("Connection closed\n");
						}
					}
				}
			};
			fromServerThread.start();//start the thread
			
			//Keep main thread alive until the socket is closed
			//initialize/do everything before this line
			while(!server.isClosed()) {
				Thread.sleep(50);
			}
			textArea.append("Exited loop");
			System.exit(0);//force close
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close();
		}
	}
	private void processPayload(Payload payload) {
		System.out.println(payload);
		switch(payload.getPayloadType()) {
		case CONNECT:
			textArea.append(payload.getMessage());
			break;
		case DISCONNECT:
			textArea.append(payload.getMessage());
			break;
		case MESSAGE:
			textArea.append(payload.getMessage());
			break;
		case XORO:
			updateBoard(payload.getXorO());
			break;
		default:
			textArea.append("Unhandled payload type: " + payload.getPayloadType().toString());
			break;
		}
	}
	private void close() {
		if(out != null) {
			Payload p = new Payload();
			p.setPayloadType(PayloadType.DISCONNECT);
			p.setMessage("Has Disconnected!");
			try {
				out.writeObject(p);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(server != null && !server.isClosed()) {
			try {
				server.close();
				System.out.println("Closed socket");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void updateBoard(JButton[][] board1) {
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				this.board[row][col].setText(board1[row][col].getText());
				this.board[row][col].setEnabled(board1[row][col].isEnabled());
				if(board1[row][col].getText().equals("X")) {
					this.intBoard[row][col] = 1;
					count++;
				} else if(board1[row][col].getText().equals("O")) {
					this.intBoard[row][col] = 2;
					count++;
				}
			}
		}
	}
	private void resetBoard() {//Always invoked after a game ends
		player = JOptionPane.showInputDialog("Enter either 1 for Player 1 OR 2 for Player 2: ");
		if(player.equals("1")) {
			YourTurn = true;
		} else {
			YourTurn = false;
		}
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				board[row][col].setText("");
				board[row][col].setEnabled(true);
				board[row][col].setBackground(Color.white);
				intBoard[row][col] = 0;
			}
		}
	}
	public static void main(String[] args) {
		ClientUI client = new ClientUI();
		client.connect("127.0.0.1", 3000);
		try {
			//if start is private, it's valid here since this main is part of the class
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}