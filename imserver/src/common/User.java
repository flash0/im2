package common;

import java.io.Serializable;

import utils.ShaUtils;

/**
 * 用户类
 * @author c
 *
 */
public class User implements Serializable{
	private String Name;
	private String PassWords;
	private String Type;	//注册或是登陆

	public User(){
		
	}
	
	public User(String Name,String PassWords,int Num){
		this.Name = Name;
		
		try {
			this.PassWords = ShaUtils.encryptSHA(PassWords + Num);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public User(String Name,String PassWords){
		this.Name = Name;
		this.PassWords = PassWords;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPassWords() {
		return PassWords;
	}

	public void setPassWords(String passWords) {
		PassWords = passWords;
	}
	
	
}
