package application;


public class Lattice {
	private boolean isPassable;//С���Ƿ����ͨ��
	private Point visited;//�Ƿ������
	public Lattice() {
		setPassable(false);
		setVisited(null);
	}
	public boolean isPassable() {
		return isPassable;
	}
	public void setPassable(boolean isPassable) {
		this.isPassable = isPassable;
	}
	public Point getVisited() {
		return visited;
	}
	public void setVisited(Point visited) {
		this.visited = visited;
	}
}
