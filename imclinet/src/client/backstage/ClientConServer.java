package client.backstage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;

import javax.swing.JOptionPane;

import utils.DecryptionUtils;

import common.Message;
import common.MessageType;
import client.tools.ManageClientCollction;
import client.tools.ManageClientPersonCollection;
import client.view.ClientFrame;
import client.view.Client_Frame;

/**
 * 客户端继续与服务器端通信的后台线程类
 * @author Administrator
 *	Client_Continue_Connect_Server_Thread
 */
public class ClientConServer implements Runnable{
	private ClienManage cm;	//后台处理对象
	private ClientFrame client;//个人聊天界面对象
	private Client_Frame cf;//群聊界面对象
	

	public ClientConServer(ClienManage cm,Client_Frame cf){
		this.cm = cm;
		this.cf = cf;
	}
	
	@Override
	public void run(){
		// TODO Auto-generated method stub
		while(true){
			//不停的接收信息
			Message mess = cm.ReciveMessage();
			if(cm.IsConnect()){
				//根据不同的信息类型处理信息
				if(mess!=null){
					if(mess.getMessageType().equals(MessageType.Common_Message_ToAll)){
						/******************解密服务器转发过来的消息********************/
						try {
							mess = DecryptionUtils.decryptMessage(mess);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/******************解密服务器转发过来的消息********************/
						//根据获得者获得群聊窗口
						Client_Frame Client = ManageClientCollction.GetClient_Frame(mess.getGetter());
						//将信息显示在群聊窗口
						Client.showMessageToAll(mess);
					}else if(mess.getMessageType().equals(MessageType.Send_Online)){
						//如果提示更新在线用户
						//获得在线用户
						String string = mess.getContent();
						//根据获得者获得群聊窗口
						Client_Frame Client = ManageClientCollction.GetClient_Frame(mess.getGetter());
						//显示在线用户
						Client.SetOnLline(string);
					}else if(mess.getMessageType().equals(MessageType.System_Messages)){
						//如果提示是系统消息
						//根据获得者获得群聊窗口
						Client_Frame ClientFrame = ManageClientCollction.GetClient_Frame(mess.getGetter());
						//将信息显示在群聊窗口
						ClientFrame.ShowSystemMessage(mess);
					}else if(mess.getMessageType().equals(MessageType.Common_Message_ToPerson)){
						/******************解密服务器转发过来的消息********************/
						try {
							DecryptionUtils.decryptMessage(mess);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/******************解密服务器转发过来的消息********************/
						//如果提示发送给个人
						//根据字符串找到指定的个人聊天界面
						String str = mess.getGetter()+" "+mess.getSender();
						client = ManageClientPersonCollection.getClientPerson(str);
						//如果没找到就创建一个个人聊天界面
						if(client==null){
							client = new ClientFrame(mess.getGetter(), mess.getSender(), cm);
							//添加入管理个人聊天的界面集合
							ManageClientPersonCollection.addClientPersonCollection(str, client);
						}
						//显示信息
	
						client.ShowMessage(mess);
						
						
					}else if(mess.getMessageType().equals(MessageType.Send_FileToAll)){
						//发送给文件所有人的文件
						
						cm.ReciveFile(mess);
						
						/***************计算文件hash**************/
						String filehash = cm.filehash(mess.getContent());		
						Client_Frame Client = ManageClientCollction.GetClient_Frame(mess.getGetter());
						Client.ShowString(filehash);
						/***************计算文件hash**************/
					
					}else if(mess.getMessageType().equals(MessageType.Send_FileToPerson)){
						
						System.out.println("111111接收文件clientserver");
						//发送给文件个人的文件
						cm.ReciveFile(mess);
						//根据字符串找到指定的个人聊天界面
						String str = mess.getGetter()+" "+mess.getSender();
						client = ManageClientPersonCollection.getClientPerson(str);
						//如果没找到就创建一个个人聊天界面
						if(client==null){
							client = new ClientFrame(mess.getGetter(), mess.getSender(), cm);
							//添加入管理个人聊天的界面集合
							ManageClientPersonCollection.addClientPersonCollection(str, client);
						}
						
						String filehash = cm.filehash(mess.getContent());													
						client.ShowString(filehash);
						
					}else if(mess.getMessageType().equals(MessageType.Send_FileToAll_without_encry)){
						cm.ReciveFile2(mess);
					}}else if(mess.getMessageType().equals(MessageType.send_FileToPerson_without_encry)){
						cm.ReciveFile2(mess);
				}
			}else{
				//弹出对话框并关闭窗口
				if(cf!=null){
					String Name = cf.getName();
					cf.ShowMessageDialog("连接服务器中断");
					//关闭所有个人聊天窗口
					ClosePersonClient(Name);
					//关闭资源
					cm.CloseResource();
				}
				break;
			}
		}
	}
	
	
	/**
	 * 关闭其所有个人聊天窗口
	 * @param Name 其群聊窗口的名字
	 */
	public void ClosePersonClient(String Name){
		String[] strings;
		Set<String> set = ManageClientPersonCollection.getClientPersonSet();
		for(String s : set){
			strings = s.split(" ");
			if(strings[0].equals(Name)){
				ClientFrame cf = ManageClientPersonCollection.getClientPerson(s);
				//关闭个人聊天窗口
				cf.dispose();
				//移除个人聊天窗口
				ManageClientPersonCollection.removeClientPerson(s);
			}
		}
	}

}
