package com.healthmanage.model;

public class User extends Person {
	
	private int coin;
	private int remainTime;
	
	public User(String userId, String password, String name) {
		super(name, password, userId);
		this.coin = 0;
		this.remainTime = 0;
	}
	

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(int remainTime) {
		this.remainTime = remainTime;
	}
	

}
