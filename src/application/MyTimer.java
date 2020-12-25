package application;

import javafx.scene.control.Label;

public class MyTimer extends Label{	
	private long programStart = System.currentTimeMillis();//��¼��ʼʱ��
	private long pauseStart = programStart;//��¼��ͣʱ��
	private long pauseCount = 0;//ͳ�Ƴ�����ͣ����ʱ��

	public MyTimer() {
		programStart = 0;
		pauseStart = 0;//��¼��ͣʱ��
		pauseCount = 0;//ͳ�Ƴ�����ͣ����ʱ��
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