package application;


public class Lattice {
	private boolean isPassable;//小球是否可以通过
	private Point visited;//是否遍历过
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
