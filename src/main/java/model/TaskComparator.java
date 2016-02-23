package model;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

	@Override
	public int compare(Task o1, Task o2) {
		return o1.getNome().compareTo(o2.getNome());
	}

}
