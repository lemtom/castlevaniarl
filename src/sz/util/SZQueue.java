package sz.util;

import java.util.ArrayList;
import java.util.List;

public class SZQueue implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<Object> list;

	public SZQueue() {
		list = new ArrayList<>(40);
	}

	public boolean contains(Object what) {
		return list.contains(what);
	}

	public SZQueue(int ini) {
		list = new ArrayList<>(ini);
	}

	public List<Object> getArrayList() {
		return list;
	}

	public Object unqueue() {
		if (!list.isEmpty()) {
			Object x = list.get(0);
			list.remove(0);
			return x;
		} else
			return null;
	}

	public void enqueue(PriorityEnqueable what) {
		list.add(list.size(), what);
	}

	public void forceToFront(Object what) {
		list.add(0, what);
	}

	public void forceToFront(Object what, Object objClass) {
		// RUN TO THE LAST OCURRENCE OF OBJCLASS
		for (int i = list.size() - 1; i >= 0; i--)
			if (list.get(i).getClass().equals(objClass.getClass())) {
				if (i < list.size() - 1) {
					list.add(i + 1, what);
				} else
					list.add(what);
				return;
			}
		list.add(what);
	}

	public void remove(Object what) {
		list.remove(what);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void removeAll() {
		list.clear();
	}
}