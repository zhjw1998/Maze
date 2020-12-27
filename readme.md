# 1.操作方法
	（1）开始：双击Maze.jar进入游戏界面，使用'W''、S'、'A'、'D'对小球进行上、下、左、右控制。
	（2）下拉框：三个下拉框分别选择迷宫的行、列和迷宫格子的宽度
	（3）'Refresh'按键：点击'Refresh'可以对迷宫进行刷新；
	（3）'Pause'按键：游戏过程中点击'Pause'暂停记时，此时将无法操控小球运动，同时按键更改为'Continue'，点击继续记时。
	（4）"Prompt'按键：点击'Prompt'生成从当前小球位置到终点的最短路径
	（5）'Quit'按键：点击'Quit'退出程序
	（6）Text区域：显示游戏时间和游戏步数
	（6）胜利：如果小球运行到终点，跳转到胜利界面，在胜利界面中按'Enter'键返回游戏界面。
# 2.数据结构说明
	row：迷宫的行数
	col：迷宫的列数
	width：迷宫格子宽度
	lattice[][]：迷宫，每一个元素类型是Lattice，代表每个迷宫小格子
	Lattice：迷宫小格类。两个元素：是否为墙、是否被访问过
	Point：点类。两个元素：x、y，代表点的坐标，主要用于运行小球的表示
	MyTimer：计时器类，三个元素：游戏开始时间、最近一次暂停时间、开始到现在总暂停时间，用于统计游戏时间
	CreateMaze：创建迷宫类
	SolveMaze：迷宫寻路类
# 3.算法说明
	（1）迷宫生成算法：随机普林算法(Randomized Prim's Algorithm)
		a. 让迷宫全是墙.
		b. 随机选一个单元格作为迷宫的通路，然后把它的邻墙放入列表
		c. 当列表里还有墙时
			(a) 从列表里随机选一个墙，如果这面墙分隔的两个单元格只有一个单元格被访问过
				(i)  那就从列表里移除这面墙，即把墙打通，让未访问的单元格成为迷宫的通路
				(ii) 把这个格子的墙加入列表
			(b) 如果墙两面的单元格都已经被访问过，那就从列表里移除这面墙

		ps：其他迷宫生成算法：DFS（深度优先搜索算法）、Recursive Division Algorithm（递归分割算法）

	（2）迷宫寻路算法：深度优先算法(DFS)
		a. 访问入口顶点v，并以此顶点为当前顶点
		b. 将当前顶点的未被访问的邻接点压入栈中
		c. 弹栈，将弹出的顶点作为当前顶点
		d. 若当前顶点没有未被访问的邻接点且栈不空，重复第3步，否则，重复第2步
		e. 重复第3、4步，直至搜索到出口顶点

		ps：其他迷宫寻路算法：BFS（广度优先搜索算法）
# 4.运行截图
	双击Maze.jar运行
	![Image text](https://github.com/zhjw1998/Maze/raw/master/prtsc/init.png)
	![Image text](https://github.com/zhjw1998/Maze/master/prtsc/playing.png)
	![Image text](https://github.com/zhjw1998/Maze/master/prtsc/prompt.png)
	![Image text](https://github.com/zhjw1998/Maze/master/prtsc/win.png)
	![Image text](https://github.com/zhjw1998/Maze/master/prtsc/after-settings.png)
	
