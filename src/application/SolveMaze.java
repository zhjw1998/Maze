package application;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;


public class SolveMaze {
	public Stack<Point> pathStack = null;
	private boolean inBoard(int x,int y, int col, int row) {
   		if((x >= 1 && x <= col && y>=1 && y <= row) || ((x == 0 && y == 1) || (x == col + 1 && y == row))) 
   			return true;
   		else return false;
   	}
	private Point[] ArroundPointBreadthFirst(Lattice[][] mazeLattice, Point p, int colNumber, int rowNumber) {
		final int[] arroundPoint = { -1, 0, 1, 0, -1 };// 一个点周围四个点的坐标变化，顺序为左上右下
		Point[] point = { null, null, null, null };
		for (int i = 0; i < 4; ++i) {
			int x = p.getX() + arroundPoint[i];
			int y = p.getY() + arroundPoint[i + 1];
			if (inBoard(x, y, colNumber, rowNumber) && mazeLattice[y][x].isPassable()
					&& mazeLattice[y][x].getVisited() == null) {
				point[i] = new Point(x, y);
				mazeLattice[y][x].setVisited(p);
			}
		}
		return point;
	}

	public void solveMaze(Lattice[][] mazeLattice, Point entrance, Point exit, int colNumber, int rowNumber) {
		pathStack = new Stack<Point>();
		Point judge = new Point(0, 0);
		Deque<Point> pathDeque = new ArrayDeque<Point>();
		Point end = new Point(exit.getX() - 1, exit.getY());
		for (int i = 0; i < rowNumber + 2; ++i)
			for (int j = 0; j < colNumber + 2; ++j)
				mazeLattice[i][j].setVisited(null);
		mazeLattice[entrance.getY()][entrance.getX()].setVisited(judge);
		pathDeque.addLast(entrance);
		Point currentPoint = entrance;
		while (!currentPoint.equals(end)) {
			Point[] p = ArroundPointBreadthFirst(mazeLattice, currentPoint, colNumber, rowNumber);
			int count = 0;// 对能压入栈中的currentPoint点周围的四个点中的点进行计数
			for (int i = 0; i < p.length; ++i) {
				if (p[i] != null) {   
					pathDeque.addLast(p[i]);
					++count;
				}
			}
			if (count == 0) {
				pathDeque.removeLast();
				if (pathDeque.isEmpty())
					break;
				currentPoint = pathDeque.getLast();
			} else {
				pathDeque.addLast(currentPoint);
			}
		}
		mazeLattice[exit.getY()][exit.getX()].setVisited(end);
		for (currentPoint = exit; !currentPoint
				.equals(judge); currentPoint = mazeLattice[currentPoint.getY()][currentPoint.getX()].getVisited()) {
			pathStack.push(currentPoint);
		}
		//return pathStack;
	}
}
