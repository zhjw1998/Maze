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
    
    
    private int row;//行
    private int col;//列
    private int width;//格子宽度
    private Lattice[][] lattice;//迷宫格子
    private Point startPoint;//起点
    private Point endPoint;//终点
    private Point ball;//运动小球
    
    private MyTimer myTimer;// = new MyTimer();//计时器
    private CountingThread thread;//线程管理时间
    public static String winningTime;//胜利的时间
    public static int steps;//步数
    
    private int promptOldSteps;//记录按了prompt之前的步数
    private boolean btnPromptPressed;//记录是否刚刚按了该按键
    
    //监控面板上的四个按钮
    @FXML
    void on_btn_refresh_clicked(ActionEvent event) {
    	this.paneMaze.getChildren().clear();//清空迷宫显示面板
    	this.initTimeAndButton();//初始化时间和暂停按键
    	this.initTextAndControllers();//初始化变量
    }
    @FXML
    void on_btn_pause_clicked(ActionEvent event) {
    	//System.out.println(btnPause.getText());
    	//目前是暂停状态且游戏已经开始
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
    	//如果这个按钮刚刚就被点击了 啥都不做
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
				paintBallLine(p,nextPoint);//绘制线段
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
    //程序运行之后最先执行这一初始化部分
    @FXML
    void initialize() {
        this.addItemsToComboBox();//复选框增加信息
        this.initCombox();//复选框初值并初始化text区域
        this.autoAdjustSize();//绑定组件随窗口大小自动调整大小
        this.initTextAndControllers();//初始化控制变量信息
    }
    //下拉列表添加信息
    private void addItemsToComboBox() {
        for(int i=15;i<76;i+=2) {
        	setRow.getItems().addAll(Integer.toString(i));
        	setCol.getItems().addAll(Integer.toString(i));
        }
        for(int i=25;i>5;i-=3) {
        	setLatWid.getItems().addAll(Integer.toString(i));
        }
    }
    //初始化下拉列表和text区域
    private void initCombox() {
        setCol.setValue("15");
        setRow.setValue("15");
        setLatWid.setValue("25");
    }
    //初始化时间和暂停按键（refresh点击时调用）
    private void initTimeAndButton() {
    	thread.stopped = true;
    	if(btnPause.getText().equals("Continue")) {
    		myTimer.setPauseCount(myTimer.getPauseCount() + System.currentTimeMillis() - myTimer.getPauseStart());
    		btnPause.setText("Pause");
    	}
    }
    //初始化和重新启动都需要做的事情 初始化本类中对迷宫进行控制的变量
    private void initTextAndControllers() {
    	textTime.setText("00:00:00");
        textStepNumber.setText("0");
    	this.keyOfOrientationPressedListener();//监控四个按键
    	this.widthChangedListener();//监控width变化事件
    	//初始化变量
    	this.col = Integer.parseInt(setCol.getValue());
        this.row = Integer.parseInt(setRow.getValue());
        this.width = Integer.parseInt(setLatWid.getValue());
        this.promptOldSteps = 0;
        this.btnPromptPressed = false;
        steps = Integer.parseInt(textStepNumber.getText());
        this.myTimer = new MyTimer();
        this.thread = new CountingThread();
        this.thread.start();
    	//初始化迷宫变量
        this.lattice = new Lattice[this.row + 2][this.col + 2];
        for(int i=0;i<this.row+2;i++) {
        	for(int j=0;j<this.col+2;j++) {
        		this.lattice[i][j]=new Lattice();
        	}
        }
        //小球、起点和终点
        this.ball = new Point(0, 1);
        this.startPoint = new Point(0,1);
        this.endPoint = new Point(this.col+1,this.row);
        lattice[this.startPoint.getY()][this.startPoint.getX()].setPassable(true);;
        lattice[this.endPoint.getY()][this.endPoint.getX()].setPassable(true);;
        //创建迷宫、绘制图像
        this.createMaze();//调用创建迷宫算法
        this.paintMaze(); //绘制迷宫
        this.paintBall(0, 1);//绘制小球
    }
    //对上下左右键进行监听 分别以w a d s控制操作
    public void keyOfOrientationPressedListener() {
    	paneAll.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				//如果目前是暂停状态,禁止操作
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
					//获胜或者可以走通的路 就可以移动Ball
					//修改ball的位置、重新绘制ball
					if(isWin(x, y) || (inBoard(x, y) && lattice[y][x].isPassable())) {
						//如果是刚刚开始运行 则开始计时
						if(isballAtStartPoint()) {
							myTimer.setStartTime(System.currentTimeMillis());
							myTimer.setPauseTime(myTimer.getProgramStart());
							thread.stopped = false;
						}
						updateSteps();//更新走的步数
						paintBall(x,y);//绘制Ball
						if(isWin(x, y)) {
							thread.stopped = true;
							winningTime = textTime.getText();
							winTheGame();//界面跳转
							initCombox();//初始化面板内容
							initTextAndControllers();//初始化控制变量信息
						}
					}
				}
			}
		});
    }
    //width下拉列表改变监听事件
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
    //按钮随着界面自动调整大小
    private void autoAdjustSize() {
        hBoxSelection.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxBtn.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxCalculator.prefWidthProperty().bind(paneAll.widthProperty());
        hBoxMaze.prefWidthProperty().bind(paneAll.widthProperty());
    }
    //判断是否为起点
    private boolean isballAtStartPoint() {
    	return (ball.getX() == startPoint.getX() && ball.getY() == startPoint.getY());
    }
    //是否已经走出迷宫
   	private boolean isWin(int x,int y) {
   		if (this.endPoint.getX() == x && this.endPoint.getY() == y) {
   			return true;
   		}
   		return false;
   	}
   	//判断(x,y)是否在迷宫面板内
   	private boolean inBoard(int x,int y) {
   		if(x >= 1 && x <= this.col && y>=1 && y <= this.row) return true;
   		else return false;
   	}
    //更新步数统计信息
    private void updateSteps() {
    	if(this.btnPromptPressed) {
    		steps = this.promptOldSteps;
    		this.btnPromptPressed = false;
    	}
    	steps++;
    	this.textStepNumber.setText(Integer.toString(steps));
    }
    //获胜之后给出提示信息
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
    //创建迷宫
    private void createMaze() {
    	CreateMaze createMaze = new CreateMaze();
    	createMaze.createMaze(this.lattice, this.col, this.row);
    }
	// 绘制迷宫
	private void paintMaze() {
		//绘制迷宫主体
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
		//绘制起点和终点
		Rectangle r1=new Rectangle((this.startPoint.getX()+1)*width,(this.startPoint.getY()+1)*width,this.width,this.width);
		r1.setFill(Color.GREEN);
		paneMaze.getChildren().add(r1);
		Rectangle r2=new Rectangle((this.endPoint.getX()+1)*width,(this.endPoint.getY()+1)*width,this.width,this.width);
		r2.setFill(Color.GREEN);
		paneMaze.getChildren().add(r2);
		//绘制小球
		Circle c = new Circle((this.ball.getX()+1)*width+width/2,(this.ball.getY()+1)*width+width/2,this.width/2);
		c.setFill(Color.BROWN);
		paneMaze.getChildren().add(c);
	}
	//绘制小球
	private void paintBall(int newx,int newy) {
		//先将原来的ball的位置还原成矩形块
		Rectangle oldr=new Rectangle((this.ball.getX()+1)*width,(this.ball.getY()+1)*width,this.width,this.width);
		//如果是起点还原成原来的绿色 否则还原成白色通路的颜色
		if(isballAtStartPoint()) oldr.setFill(Color.GREEN);
		else oldr.setFill(Color.WHITE);
		paneMaze.getChildren().add(oldr);		
		//然后绘制新的ball格子
		Circle newc = new Circle((newx+1)*width+width/2,(newy+1)*width+width/2,this.width/2);
		newc.setFill(Color.BROWN);
		paneMaze.getChildren().add(newc);
		//更新位置信息
		this.ball.setX(newx);
		this.ball.setY(newy);
	}
	//点击prompt时迷宫解法路线中每一个小格的绘制
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
    //此类用来计时
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
	                sleep(1000);  // 1秒更新一次显示
	        	}
        	}catch (InterruptedException e) {
        		e.printStackTrace();  
                System.exit(1);
			}
        }     
        // 将毫秒数格式化  
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
