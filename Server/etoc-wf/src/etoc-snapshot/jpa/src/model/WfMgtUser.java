package model;

import java.io.Serializable;

/**
 * The persistent class for the wf_mgt_user database table.
 * 
 */
@Entity
@Table(name = "wf_mgt_user")
public class WfMgtUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private String nickname;

	private String password;

	private String username;

	public WfMgtUser() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}