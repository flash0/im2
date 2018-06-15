package client.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import utils.EncryptionUtils;
import utils.ShaUtils;

import common.Message;
import common.MessageType;
import client.backstage.ClienManage;
import client.tools.ManageClientPersonCollection;
import client.tools.Tools;

/**
 * 单人聊天的界面
 * @author Administrator
 *
 */
public class ClientFrame extends JFrame implements WindowListener{
	private static JTextArea jta1;	//文本区域
	private JTextArea jta2;
	private JButton jb1,jb2,jb3,jb4;	//按扭
	private JScrollPane jsp1,jsp2;	//滚动条
	private ClienManage cm;	//后台处理对象
	private JFileChooser jfc;	//文件选择器
	private String Sender;
	private String Getter;
	private JFrame jf;
	private static Message message = null;
	public ClientFrame(final String Sender,final String Getter,final ClienManage cm){
		super(Sender+"正在与"+Getter+"聊天中");
		this.jf = this;
		this.cm = cm;
		this.Sender = Sender;
		this.Getter = Getter;
		Container c = this.getContentPane();
		//设置大小
		this.setSize(500, 420);
		//设置空布局
		this.setLayout(null);
		
		jta1 = new JTextArea();
		//设置不可编辑
		jsp1 = new JScrollPane(jta1);
		jta1.setEditable(false);
		jsp1.setBounds(10, 10, 475, 210);
		c.add(jsp1);
		
		jta2 = new JTextArea();
		jsp2 = new JScrollPane(jta2);
		//获取光标
		jta2.grabFocus();
		jsp2.setBounds(10, 225, 475, 110);
		c.add(jsp2);
		
		
		jb1 = new JButton("发送");
		jb1.setBounds(265, 350, 60,20);
		//发送按扭注册事件监听
		this.getRootPane().setDefaultButton(jb1);
		jb1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String con = jta2.getText();
				@SuppressWarnings("deprecation")
				String Time = (new Date()).toLocaleString();
				Message mess = new Message();
				mess.setContent(con);
				mess.setTime(Time);
				mess.setSender(Sender);
				mess.setGetter(Getter);
				mess.setMessageType(MessageType.Common_Message_ToPerson);
				//jta1.append(Sender+"    "+Time+"\r\n"+con+"\r\n");	
				//发送消息
				cm.SendMessage(mess);
				
				//显示消息
				jta1.append(Sender+"    "+Time+"\r\n"+con+"\r\n");
			//	jta1.append(Sender+"    "+Time+"\r\n"+con+"\r\n"+"消息摘要值为："+mess.getSentContentMac()+"\r\n");
				jta2.setText("");
				//获取光标
				jta2.grabFocus();
			}
		});
		c.add(jb1);
		
		jb2 = new JButton("发送文件");
		jb2.setBounds(10, 350, 100, 20);
		//发送文件按扭注册事件监听
		jb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				jfc = new JFileChooser();
				jfc.showOpenDialog(jf);
				Message mess = new Message();
				
				mess.setSender(Sender);
				mess.setGetter(Getter);
				String FileName = jfc.getName(jfc.getSelectedFile());
				//设置文件名
				mess.setContent(FileName);
				int b = JOptionPane.showConfirmDialog(null, "是否选择加密","提示",JOptionPane.YES_NO_OPTION);
				if(b == JOptionPane.YES_OPTION || b== JOptionPane.NO_OPTION){
					mess.setMessageType(MessageType.Send_FileToPerson);
				if( jfc.getSelectedFile().toPath().toString()!=null){
					//发送消息类型
					/***************设置AES加密的KEY****************/
					String key = EncryptionUtils.encryptFileKey(mess);
					/***************设置AES加密的KEY****************/
					mess.setMessageType(MessageType.Send_FileToPerson);
					cm.SendMessage(mess);
					//获得路径
					String path = jfc.getSelectedFile().toPath().toString();
					
					//发送消息
					Message m = new Message();
					m.setMessageType(MessageType.Common_Message_ToPerson);
					m.setSender(Sender);
					m.setGetter(Getter);
					m.setTime(new Date().toLocaleString());
					String filehash = null;
					try {
						 filehash = ShaUtils.getFileSha512(jfc.getSelectedFile().toPath().toString());
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					m.setContent("我给你发送了文件名为："+FileName+" 的文件\r\n发送时文件hash为：" + filehash +"\r\n");
					//ShowString2(filehash);
					ShowMessage2(m);
					cm.SendMessage(m);
					
					//发送文件
					cm.SendFile(path,key);
				}
				}else{
				
					mess.setMessageType(MessageType.send_FileToPerson_without_encry);
					cm.SendMessage(mess);
					//获得路径
					String path = jfc.getSelectedFile().toPath().toString();
					Message m = new Message();
					m.setMessageType(MessageType.Common_Message_ToPerson);
					m.setSender(Sender);
					m.setGetter(Getter);
					m.setTime(new Date().toLocaleString());
					String filehash = null;
					try {
						 filehash = ShaUtils.getFileSha512(jfc.getSelectedFile().toPath().toString());
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					m.setContent("我给你发送了文件名为："+FileName+" 的文件\r\n发送时文件hash为：" + filehash +"\r\n");
					//ShowString2(filehash);
					ShowMessage2(m);
					cm.SendMessage(m);
					
					//发送文件
					cm.SendFile2(path);
					
				}	
				
			}
			
		});
		c.add(jb2);
		
		jb3 = new JButton("清空聊天记录");
		jb3.setBounds(120, 350, 120,20);
		//清空聊天记录按扭注册事件监听
		jb3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				jta1.setText("");
			}
		});
		c.add(jb3); 
		
		//注册窗口事件监听
		this.addWindowListener(this);
		//设置大小不可改变
		this.setResizable(false);
		//设置在屏幕中间
		Tools.setFrameCenter(this);
		this.setVisible(true);
	
	
	jb4 = new JButton("显示hash值");
	jb4.setBounds(350, 350, 120,20);
	
	//显示hash值
	jb4.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(message != null){
			jta1.append( "原消息hash：" + message.getSentContentMac()+"\r\n" + "计算消息hash" + message.getGetContentMac()+"\r\n"
					+ "原AES hash：" + message.getSentKeyMac()+"\r\n"+ "计算AES hash:"+message.getGetKeyMac()+"\r\n"+"\r\n");
			}
		}
	});
	c.add(jb4); 
	
	
}
	
	/***
	 * 显示信息在个人聊天界面
	 * @param mess
	 */
	public static void ShowMessage(Message mess){
		message = mess; 
		jta1.append(mess.getSender()+"    "+mess.getTime()+"\r\n"+mess.getContent()+"\r\n");
//		jta1.append(mess.getSender()+"    "+mess.getTime()+"\r\n"+mess.getContent()+"\r\n"
//				+ "原消息hash：" + mess.getSentContentMac()+"\r\n" + "计算消息hash" + mess.getGetContentMac()+"\r\n"
//				+ "原AES hash：" + mess.getSentKeyMac()+"\r\n"+ "计算AES hash:"+mess.getGetKeyMac()+"\r\n"+"\r\n");
	
	}
	
	//发送文件时将信息显示在聊天面板上
	public static void ShowMessage2(Message mess){
		jta1.append(mess.getSender()+"    "+mess.getTime()+"\r\n"+mess.getContent()+"\r\n");
	
	}
	
	
	public static void ShowString(String str){
		jta1.append("本地计算的文件hash："+str +"\r\n");
	
	}
	public static void ShowString2(String str){
		jta1.append("文件hash为："+str +"\r\n");
	
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		String str = Sender+" "+Getter;
		ManageClientPersonCollection.removeClientPerson(str);
	}


	@Override
	public void windowClosed(WindowEvent e) {
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	//判断
	
}
