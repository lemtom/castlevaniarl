package sz.fov;

public interface FOVMap {
	boolean blockLOS(int x, int y);
	void setSeen(int x, int y);
}
