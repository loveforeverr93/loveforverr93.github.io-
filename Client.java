package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public  class Client extends JFrame implements ActionListener {
	
	private JButton send,clear,exit,login,logout;
	private JPanel p_login,p_chat;
	private JTextField nick,nick1,message;
	private JTextArea msg,online;
	
	private Socket client;
	private DataInputStream dis;
	private DataOutputStream dos;
	private DataStream dataStream;
	
	public Client() {
		super("ChatRoom: Client");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		setSize(600, 400);
		addItem();
		setVisible(true);
	}

	private void addItem() {
		// TODO Auto-generated method stub
		exit = new JButton("Thoát");
		exit.addActionListener(this);
		send = new JButton("Gửi");
		send.addActionListener(this);
		clear = new JButton("Xóa");
		clear.addActionListener(this);
		login = new JButton("Đăng Nhập");
		login.addActionListener(this);
		logout = new JButton("Thoát Tài Khoản");
		logout.addActionListener(this);
		
		p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());
		
		JPanel j1= new JPanel();
		j1.setLayout(new FlowLayout(FlowLayout.LEFT));
		nick = new JTextField(20);
		j1.add(nick);
		j1.add(exit);
		
		JPanel j2 = new JPanel();
		j2.setLayout(new BorderLayout());
		
		JPanel j22 = new JPanel();
		j22.setLayout(new FlowLayout(FlowLayout.CENTER));
		j22.add(new JLabel("Danh sách online"));
		j2.add(j22,BorderLayout.NORTH);
		
		online = new JTextArea(10,10);
		online.setEditable(false);
		j2.add(new JScrollPane(online),BorderLayout.CENTER);
		j2.add(new JLabel("     "),BorderLayout.SOUTH);
		j2.add(new JLabel("     "),BorderLayout.EAST);
		j2.add(new JLabel("     "),BorderLayout.WEST);
		
		msg = new JTextArea(10,20);
		msg.setEditable(false);
		
		JPanel j3 = new JPanel();
		j3.setLayout(new FlowLayout(FlowLayout.LEFT));
		j3.add(new JLabel("Tin nhắn"));
		message = new JTextField(30);
		j3.add(message);
		j3.add(send);
		j3.add(clear);
		
		p_chat.add(new JScrollPane(msg),BorderLayout.CENTER);
		p_chat.add(j1,BorderLayout.NORTH);
		p_chat.add(j2,BorderLayout.EAST);
		p_chat.add(j3,BorderLayout.SOUTH);
		p_chat.add(new JLabel("     "),BorderLayout.WEST);
		
		p_chat.setVisible(false);
		add(p_chat,BorderLayout.CENTER);
		
		p_login = new JPanel();
		p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
		p_login.add(new JLabel("Nick chát : "));
		nick1=new JTextField(20);
		p_login.add(nick1);
		p_login.add(login);
		p_login.add(logout);
		
		add(p_login,BorderLayout.NORTH);
	}
	
	public void go() {
		try {
			client = new Socket("",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());
		
			//client.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Lỗi kết nối, xem lại mạng hoặc room chưa mở.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		new Client().go();
	}
	
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		case 3:
			this.msg.append(msg2);
			break;
		case 4:
			this.online.setText(msg2);
			break;
		case 5:
			dataStream.stopThread();
			exit();
			break;
		default:
			break;
		}
	}
	
	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("Tôi : "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	
	private boolean checkLogin(String nick){
		if(nick.compareTo("")==0)
			return false;
		else if(nick.compareTo("0")==0){
			return false;
		}
		else{
			sendMSG(nick);
			int sst = Integer.parseInt(getMSG());
			if(sst==0)
				 return false;
			else return true;
		}
	}
	
	private void exit() {
		// TODO Auto-generated method stub
		try {
			sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==exit){
			dataStream.stopThread();
			exit();
		}
		else if(e.getSource()==clear){
			message.setText("");
		}
		else if(e.getSource()==send){
			checkSend(message.getText()+"\n");
			message.setText("");
		}
		else if(e.getSource()==login){
			if(checkLogin(nick1.getText())){
				p_chat.setVisible(true);
				p_login.setVisible(false);
				nick.setText(nick1.getText());
				nick.setEditable(false);
				this.setTitle(nick1.getText());
				msg.append("Đã đăng nhập thành công\n");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Đã tồn tại níck này trong room, bạn vui lòng nhập lại.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			exit();
		}
	}
}
