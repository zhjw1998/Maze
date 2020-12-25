package application;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CreateMaze {
	// 验证横纵坐标是否超界
	private boolean isOutofBorder(int x, int y, int colNumber, int rowNumber) {
		if ((x == 0 && y == 1) || (x == colNumber + 1 && y == rowNumber))
			return false;
		else
			return (x > colNumber || y > rowNumber || x < 1 || y < 1) ? true : false;
	}
	// 将点p的邻墙放入列表中
	private void pushArroundWallToList(Lattice[][] lattice, Point p, List<Point> list, int colNumber, int rowNumber) {
		final int[] arroundWall = { -1, 0, 1, 0, -1 };// 一个点周围四个墙的坐标变化，顺序为左上右下
		for (int i = 0; i < 4;) {
			int x = p.getX() + arroundWall[i];
			int y = p.getY() + arroundWall[++i];
			if (!isOutofBorder(x, y, colNumber, rowNumber) && !lattice[y][x].isPassable()) {
				list.add(new Point(x, y));
			}
		}
	}
	// 找到墙wall未被访问过的点，如果没有返回Null
	private Point findPoint(Point wall, Lattice[][] lattice) {
		final int[] arroundWall = { -1, 0, 1, 0, -1 };// 顺序为左上右下
		Point p = null;
		for (int i = (wall.getY() + 1) % 2; i < 2; i += 2) {
			boolean add = lattice[wall.getY() + arroundWall[i + 1]][wall.getX() + arroundWall[i]].isPassable(),
					sub = lattice[wall.getY() - arroundWall[i + 1]][wall.getX() - arroundWall[i]].isPassable();
			if (add && !sub) {
				p = new Point(wall.getX() - arroundWall[i], wall.getY() - arroundWall[i + 1]);
				break;
			}
			if (!add && sub) {
				p = new Point(wall.getX() + arroundWall[i], wall.getY() + arroundWall[i + 1]);
				break;
			}
		}
		return p;
	}
	
	public void createMaze(Lattice[][] lattice, int colNumber, int rowNumber) {
		Random rand = new Random();
		Point currentPoint = new Point(2 * rand.nextInt(colNumber / 2) + 1, 2 * rand.nextInt(rowNumber / 2) + 1);
		lattice[currentPoint.getY()][currentPoint.getX()].setPassable(true);
		List<Point> listWall = new LinkedList<Point>();
		pushArroundWallToList(lattice, currentPoint, listWall, colNumber, rowNumber);
		while (!listWall.isEmpty()) {
			int k = rand.nextInt(listWall.size());
			Point wall = listWall.remove(k);
			currentPoint = findPoint(wall, lattice);
			if (currentPoint != null) {
				lattice[wall.getY()][wall.getX()].setPassable(true);
				lattice[currentPoint.getY()][currentPoint.getX()].setPassable(true);
				pushArroundWallToList(lattice, currentPoint, listWall, colNumber, rowNumber);
			}
		}
	}
}
