package application;

import javafx.scene.control.Label;

public class MyTimer extends Label{	
	private long programStart = System.currentTimeMillis();//记录开始时间
	private long pauseStart = programStart;//记录暂停时间
	private long pauseCount = 0;//统计程序暂停的总时间

	public MyTimer() {
		programStart = 0;
		pauseStart = 0;//记录暂停时间
		pauseCount = 0;//统计程序暂停的总时间
	}
	public void setStartTime(long programStart) {
		this.programStart = programStart;
	}
	public void setPauseTime(long pauseStart) {
		this.pauseStart = pauseStart;
	}
	public void setPauseCount(long pauseCount) {
		this.pauseCount = pauseCount;
	}
	public long getProgramStart() {
		return this.programStart;
	}
	public long getPauseStart() {
		return this.pauseStart;
	}
	public long getPauseCount() {
		return this.pauseCount;
	}
}