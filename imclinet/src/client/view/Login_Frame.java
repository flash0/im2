package client.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.ConnectException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import utils.EncryptionUtils;
import utils.IOUtils;
import utils.RSAUtils;
import common.Message;
import common.MessageType;
import common.User;
import client.backstage.ClienManage;
import client.backstage.ClientConServer;
import client.tools.ClientThreadCollection;
import client.tools.ManageClientCollction;
import client.tools.Tools;
/**
 * 登陆的界面
 * @author Administrator
 *
 */
public class Login_Frame extends JFrame implements Action {
	//定义需要的组件
	private JLabel jl1,jl2;	//标签
	private JTextField jtf;	//文本框
	private JPasswordField jpf;	//密码文本框
	private JButton jb1,jb2,jb3;	//按钮
	private JFrame jf;
	public Login_Frame(){
		super("登录界面");
		this.jf = this;
		this.setLayout(null);
		this.setSize(400,300);
		Container c = this.getContentPane();
		jl1 = new JLabel("用户名");
		jl1.setBounds(100, 60, 50, 20);
		c.add(jl1);
		
		jtf = new JTextField();
		//获得光标
		jtf.grabFocus();
		jtf.setBounds(160, 60, 120, 20);
		c.add(jtf);
		
		jl2 = new JLabel("密码");
		jl2.setBounds(100, 140, 50, 20);
		c.add(jl2);
		
		jpf = new JPasswordField();
		jpf.setBounds(160, 140, 120, 20);
		c.add(jpf);
		
		jb1 = new JButton("登录");
		jb1.setBounds(90, 210, 60, 20);
		c.add(jb1);
		
		this.getRootPane().setDefaultButton(jb1);
		 //登陆按扭添加事件监听
        jb1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String Name = jtf.getText();
				String PassWord = new String(jpf.getPassword());
				
				if(Name.equals("")){
					JOptionPane.showMessageDialog(jf, "用户名不能为空");
				}else if(PassWord.equals("")){
					JOptionPane.showMessageDialog(jf, "密码不能为空");
				}else{
					//User user = new User(Name, PassWord);
					
					ClienManage cm = new ClienManage();
					if(cm.IsConnect()){
						int Num = cm.getRadomNum();
						User user = new User(Name, PassWord,Num);
						
						if(!cm.Check_isLogin(user)){
							if(cm.Login()){
								JOptionPane.showMessageDialog(jf, "登陆成功");
								jf.dispose();
								initKey(user);
								Client_Frame client = new Client_Frame(user.getName(),cm);
								//获得聊天界面
								String name = user.getName();
								//添加入聊天界面集合
								ManageClientCollction.addClientCollction(name, client);
								ClientConServer clientConServer = new ClientConServer(cm,client);
								//将该线程对象加入线程序集合
								ClientThreadCollection.addClientThreadCollection(user.getName(), clientConServer);
								//启动客户端继续与服务器端通信的后台线程
								Thread t = new Thread(clientConServer);
								t.start();
							}else{
								JOptionPane.showMessageDialog(jf, "用户名或密码错误");
								return ;
							}
						}else{
							JOptionPane.showMessageDialog(jf, "用户已经登陆了");
							return ;
						}
					}else{
						JOptionPane.showMessageDialog(jf,"连接不到服务器");
					}
				}
			}
		});
		
		jb2 = new JButton("重置");
		jb2.setBounds(170, 210, 60, 20);
		c.add(jb2);
		//重置按扭注册事件监听
		jb2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				jtf.setText("");
				jpf.setText("");
				//获得光标
				jtf.grabFocus();
			}
		});
		jb3 = new JButton("注册");
		jb3.setBounds(250, 210, 60, 20);
		c.add(jb3);
	    //注册按扭添加事件监听
	    jb3.addActionListener(new ActionListener() {
				
	    	@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//启动注册界面
				Register_Frame register = new Register_Frame();
				jf.dispose();
			}
		});
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Tools.setFrameCenter(this);
		this.setVisible(true);
	}
	
	private void initKey(User user){
		/***************客户端生成密钥对并发送公钥给服务器****************/
		String publicKey;  //公钥
	    String privateKey; //私钥
		//生成密钥对
        Map<String, Object> keyMap;
		try {
			keyMap = RSAUtils.genKeyPair();
			  //得到公钥
            publicKey = RSAUtils.getPublicKey(keyMap);  
          //得到私钥
            privateKey = RSAUtils.getPrivateKey(keyMap);  
            
            //保存公钥和私钥
            IOUtils.SaveKeyFile(user.getName() + "_publicKey.key", publicKey);
            IOUtils.SaveKeyFile(user.getName() + "_privateKey.key", privateKey);
            //用服务器的公钥加密我们自己产生的公钥
            byte[] key = EncryptionUtils.encryptByPublicKey("publicKey.key", publicKey);    
            //发送公钥的内容给客户端
            Message mess = new Message(MessageType.Send_Public_Key,user.getName(),key);
            mess.setContent("");
            ClienManage clienManage = new ClienManage();
            clienManage.SendMessage(mess);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  

	/***************客户端生成密钥对并发送公钥给服务器****************/
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putValue(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
