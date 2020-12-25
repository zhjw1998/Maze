package controller;

import java.io.IOException;

import application.CreateMaze;
import application.Lattice;
import application.MyTimer;
import application.Point;
import application.SolveMaze;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MazeUIController {  
    @FXML
    private Pane paneAll;
    @FXML
    private HBox hBoxSelection;
    @FXML
    private HBox hBoxBtn;
    @FXML
    private HBox hBoxCalculator;
    @FXML
    private HBox hBoxMaze;
    @FXML
    private Button btnQuit;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnPrompt;
    @FXML
    private Button btnPause;
    @FXML
    private TextField textStepNumber;
    @FXML
    private TextField textTime;
    @FXML
    private ComboBox<String> setRow;
    @FXML
    private ComboBox<String> setCol;
    @FXML
    private ComboBox<String> setLatWid;
    @FXML
    private Pane paneMaze; 
    
    
    private int row;//��
    private int col;//��
    private int width;//���ӿ��
    private Lattice[][] lattice;//�Թ�����
    private Point startPoint;//���
    private Point endPoint;//�յ�
    private Point ball;//�˶�С��
    
    private MyTimer myTimer;// = new MyTimer();//��ʱ��
    private CountingThread thread;//�̹߳���ʱ��
    public static String winningTime;//ʤ����ʱ��
    public static int steps;//����
    
    private int promptOldSteps;//��¼����prompt֮ǰ�Ĳ���
    private boolean btnPromptPressed;//��¼�Ƿ�ոհ��˸ð���
    
    //�������ϵ��ĸ���ť
    @FXML
    void on_btn_refresh_clicked(ActionEvent event) {
    	this.paneMaze.getChildren().clear();//����Թ���ʾ���
    	this.initTimeAndButton();//��ʼ��ʱ�����ͣ����
    	this.initTextAndControllers();//��ʼ������
    }
    @FXML
    void on_btn_pause_clicked(ActionEvent event) {
    	//System.out.println(btnPause.getText());
    	//Ŀǰ����ͣ״̬����Ϸ�Ѿ���ʼ
    	if(btnPause.getText().equals("Pause")) {
    		if(!isballAtStartPoint()) {
	    		myTimer.setPauseTime(System.currentTimeMillis());
	    		thread.stopped = true;
	    		btnPause.setText("Continue");
    		}
    	}
    	else {
    		myTimer.setPauseCount(myTimer.getPauseCount() + System.currentTimeMillis() - myTimer.getPauseStart());
    		thread.stopped = false;
    		btnPause.setText("Pause");
    	}
    }
    @FXML
    void on_btn_prompt_clicked(ActionEvent event) {
    	//��������ť�ովͱ������ ɶ������
    	if(!this.btnPromptPressed) {
	    	this.promptOldSteps = steps;
	    	this.btnPromptPressed = true;
	    	SolveMaze solveMaze = new SolveMaze();
	    	solveMaze.solveMaze(this.lattice, new Point(ball.getX(), ball.getY()), this.endPoint, this.col, this.row);
	    	while (!solveMaze.pathStack.isEmpty()) {
				Point p = solveMaze.pathStack.pop();
				Point nextPoint = null;
				if(solveMaze.pathStack.isEmpty()) nextPoint = null;
				else nextPoint = solveMaze.pathStack.lastElement();
				paintBallLine(p,nextPoint);//�����߶�
				++steps;
			}
	    	steps--;
	    	textStepNumber.setText(Integer.toString(steps));
    	}
    }
    @FXML
    void on_btn_quit_clicked(ActionEvent event) {
    	Stage stage = (Stage)btnQuit.getScene().getWindow();
	    stage.close();
    }
    //��������֮������ִ����һ��ʼ������
    @FXML
    void initialize() {
        this.addItemsToComboBox();//��ѡ��������Ϣ
        this.initCombox();//��ѡ���ֵ����ʼ��text����
        this.autoAdjustSize();//������洰�ڴ�С�Զ�������С
        this.initTextAndControllers();//��ʼ�����Ʊ�����Ϣ
    }
    //�����б������Ϣ
    private void addItemsToComboBox() {
        for(int i=15;i<76;i+=2) {
        	setRow.getItems().addAll(Integer.toString(i));
        	setCol.getItems().addAll(Integer.toString(i));
        }
        for(int i=25;i>5;i-=3) {
        	setLatWid.getItems().addAll(Integer.toString(i));
        }
    }
    //��ʼ�������б��text����
    private void initCombox() {
        setCol.setValue("15");
        setRow.setValue("15");
        setLatWid.setValue("25");
    }
    //��ʼ��ʱ�����ͣ������refresh���ʱ���ã�
    private void initTimeAndButton() {
    	thread.stopped = true;
    	if(btnPause.getText().equals("Continue")) {
    		myTimer.setPauseCount(myTimer.getPauseCount() + System.currentTimeMillis() - myTimer.getPauseStart());
    		btnPause.setText("Pause");
    	}
    }
    //��ʼ����������������Ҫ�������� ��ʼ�������ж��Թ����п��Ƶı���
    private void initTextAndControllers() {
    	textTime.setText("00:00:00");
        textStepNumber.setText("0");
    	this.keyOfOrientationPressedListener();//����ĸ�����
    	this.widthChangedListener();//���width�仯�¼�
    	//��ʼ������
    	this.col = Integer.parseInt(setCol.getValue());
        this.row = Integer.parseInt(setRow.getValue());
        this.width = Integer.parseInt(setLatWid.getValue());
        this.promptOldSteps = 0;
        this.btnPromptPressed = false;
        steps = Integer.parseInt(textStepNumber.getText());
        this.myTimer = new MyTimer();
        this.thread = new CountingThread();
        this.thread.start();
    	//��ʼ���Թ�����
        this.lattice = new Lattice[this.row + 2][this.col + 2];
        for(int i=0;i<this.row+2;i++) {
        	for(int j=0;j<this.col+2;j++) {
        		this.lattice[i][j]=new Lattice();
        	}
        }
        //С�������յ�
        this.ball = new Point(0, 1);
        this.startPoint = new Point(0,1);
        this.endPoint = new Point(this.col+1,this.row);
        lattice[this.startPoint.getY()][this.startPoint.getX()].setPassable(true);;
        lattice[this.endPoint.getY()][this.endPoint.getX()].setPassable(true);;
        //�����Թ�������ͼ��
        this.createMaze();//���ô����Թ��㷨
        this.paintMaze(); //�����Թ�
        this.paintBall(0, 1);//����С��
    }
    //���������Ҽ����м��� �ֱ���w a d s���Ʋ���
    public void keyOfOrientationPressedListener() {
    	paneAll.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				//���Ŀǰ����ͣ״̬,��ֹ����
				if(btnPause.getText().equals("Pause") ) {
					int x = ball.getX();
					int y = ball.getY();
					switch(event.getCode()) {
						case W:
							//System.out.println("UP");
							y--;
							break;
						case S:
							//System.out.println("DOWN");
							y++;
							break;
						case A:
							//System.out.println("LEFT");
							x--;
							break;
						case D:
							//System.out.println("RIGHT");
							x++;
							break;
						default:
							//System.out.println( "NOT");
							return;
					}
					//��ʤ���߿�����ͨ��· �Ϳ����ƶ�Ball
					//�޸�ball��λ�á����»���ball
					if(isWin(x, y) || (inBoard(x, y) && lattice[y][x].isPassable())) {
						//����Ǹոտ�ʼ���� ��ʼ��ʱ
						if(isballAtStartPoint()) {
							myTimer.setStartTime(System.currentTimeMillis());
							myTimer.setPauseTime(myTimer.getProgramStart());
							thread.stopped = false;
						}
						updateSteps();//�����ߵĲ���
						paintBall(x,y);//����Ball
						if(isWin(x, y)) {
							thread.stopped = true;
							winningTime = textTime.getText();
							winTheGame();//������ת
							initCombox();//��ʼ���������
							initTextAndControllers();//��ʼ�����Ʊ�����Ϣ
						}
					}
				}
			}
		});
    }
    //width�����б�ı�����¼�
    private void widthChangedListener() {
    	this.setLatWid.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				//System.out.print("oldvalue="+oldValue+" newvalue="+newValue);
				width = Integer.parseInt(setLatWid.getValue());
				paneMaze.getChildren().clear();
				paintMaze();        
		        paintBall(ball.getX(),ball.getY());
			}

		});
    }
    //��ť���Ž����Զ�������С
    private void autoAdjustSize() {
        hBoxSelection.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxBtn.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxCalculator.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxMaze.prefWidthProperty().bind(paneAll.widthProperty());
    }
    //�ж��Ƿ�Ϊ���
    private boolean isballAtStartPoint() {
    	return (ball.getX() == startPoint.getX() && ball.getY() == startPoint.getY());
    }
    //�Ƿ��Ѿ��߳��Թ�
   	private boolean isWin(int x,int y) {
   		if (this.endPoint.getX() == x && this.endPoint.getY() == y) {
   			return true;
   		}
   		return false;
   	}
   	//�ж�(x,y)�Ƿ����Թ������
   	private boolean inBoard(int x,int y) {
   		if(x >= 1 && x <= this.col && y>=1 && y <= this.row) return true;
   		else return false;
   	}
    //���²���ͳ����Ϣ
    private void updateSteps() {
    	if(this.btnPromptPressed) {
    		steps = this.promptOldSteps;
    		this.btnPromptPressed = false;
    	}
    	steps++;
    	this.textStepNumber.setText(Integer.toString(steps));
    }
    //��ʤ֮�������ʾ��Ϣ
	private void winTheGame() {
		Scene scene = null;
		try {
			scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("view/WinTheGameUI.fxml")));
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.print("winthegame error");
		}
		scene.getStylesheets().add(getClass().getClassLoader().getResource("application/application.css").toExternalForm());
        Stage stage = ((Stage)paneAll.getScene().getWindow());
        stage.setTitle("VICTORY");
        stage.setScene(scene);
        stage.show();
    }
    //�����Թ�
    private void createMaze() {
    	CreateMaze createMaze = new CreateMaze();
    	createMaze.createMaze(this.lattice, this.col, this.row);
    }
	// �����Թ�
	private void paintMaze() {
		//�����Թ�����
		for(int i=0;i<this.row+2;i++) {
			for(int j=0;j<this.col+2;j++) {
				Rectangle r=new Rectangle((j+1)*this.width,(i+1)*this.width,this.width,this.width);
				if(lattice[i][j].isPassable()) {
					r.setFill(Color.WHITE);
				}else {
					r.setFill(Color.BLACK);
				}
				paneMaze.getChildren().add(r);
			}
		}
		//���������յ�
		Rectangle r1=new Rectangle((this.startPoint.getX()+1)*width,(this.startPoint.getY()+1)*width,this.width,this.width);
		r1.setFill(Color.GREEN);
		paneMaze.getChildren().add(r1);
		Rectangle r2=new Rectangle((this.endPoint.getX()+1)*width,(this.endPoint.getY()+1)*width,this.width,this.width);
		r2.setFill(Color.GREEN);
		paneMaze.getChildren().add(r2);
		//����С��
		Circle c = new Circle((this.ball.getX()+1)*width+width/2,(this.ball.getY()+1)*width+width/2,this.width/2);
		c.setFill(Color.BROWN);
		paneMaze.getChildren().add(c);
	}
	//����С��
	private void paintBall(int newx,int newy) {
		//�Ƚ�ԭ����ball��λ�û�ԭ�ɾ��ο�
		Rectangle oldr=new Rectangle((this.ball.getX()+1)*width,(this.ball.getY()+1)*width,this.width,this.width);
		//�������㻹ԭ��ԭ������ɫ ����ԭ�ɰ�ɫͨ·����ɫ
		if(isballAtStartPoint()) oldr.setFill(Color.GREEN);
		else oldr.setFill(Color.WHITE);
		paneMaze.getChildren().add(oldr);		
		//Ȼ������µ�ball����
		Circle newc = new Circle((newx+1)*width+width/2,(newy+1)*width+width/2,this.width/2);
		newc.setFill(Color.BROWN);
		paneMaze.getChildren().add(newc);
		//����λ����Ϣ
		this.ball.setX(newx);
		this.ball.setY(newy);
	}
	//���promptʱ�Թ��ⷨ·����ÿһ��С��Ļ���
	private void paintBallLine(Point presentPoint, Point nextPoint) {
		double startX,startY,endX,endY;
		startX = (presentPoint.getX()+1)*width+width/2;
		startY = (presentPoint.getY()+1)*width+width/2;
		if(nextPoint != null) {
			endX = (nextPoint.getX()+1)*width+width/2;
			endY = (nextPoint.getY()+1)*width+width/2;
		}
		else {
			endX = startX;
			endY = startY;
		}
		Line line = new Line(startX, startY, endX, endY);
		line.setStroke(Color.DARKGREEN);
		line.setStrokeWidth(width/3);
		paneMaze.getChildren().add(line);
	}
    //����������ʱ
    private class CountingThread extends Thread{  
    	boolean stopped;
        private CountingThread() {  
        	stopped = true;
            setDaemon(true);
        }
        @Override  
        public void run() {
        	try {
	        	while(true) {
	        		if (!stopped) {
	                    long elapsed = System.currentTimeMillis() - myTimer.getProgramStart() - myTimer.getPauseCount(); 
	                    textTime.setText(format(elapsed));  
	                }
	                sleep(1000);  // 1�����һ����ʾ
	        	}
        	}catch (InterruptedException e) {
        		e.printStackTrace();  
                System.exit(1);
			}
        }     
        // ����������ʽ��  
        private String format(long elapsed) {
            int hour, minute, second;  
            elapsed = elapsed / 1000;  
            second = (int) (elapsed % 60);  
            elapsed = elapsed / 60;  
            minute = (int) (elapsed % 60);  
            elapsed = elapsed / 60;  
            hour = (int) (elapsed % 60);  
            return String.format("%02d:%02d:%02d", hour, minute, second);  
        }  
    } 
}
