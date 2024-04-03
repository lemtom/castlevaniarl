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
		/*
		 * Debug.say("forcing "+what+" to front");
		 * Debug.say("forcing "+what.getClass()+" to front");
		 * Debug.say("forcing "+objClass.getClass()+" to front");
		 * Debug.say(what.getClass().equals(objClass.getClass())+"? ");
		 * Debug.say(list.get(0).getClass()+"  0? ");
		 * Debug.say(list.get(1).getClass()+" 1? ");
		 * Debug.say(list.get(2).getClass()+" 2? ");
		 */
		// RUN TO THE LAST OCURRENCE OF OBJCLASS
		for (int i = list.size() - 1; i >= 0; i--)
			// if (!
			// list.get(i).getClass().toString().equals(objClass.getClass().toString())){
			if (list.get(i).getClass().equals(objClass.getClass())) {
				if (i < list.size() - 1) {
					list.add(i + 1, what);
					// Debug.say("inserted at " +(i+1));
				} else
					list.add(what);
				return;
			}

		// Debug.say("in the end, it doesnt even matter");
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