package io.github.agileluo.qcode.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ResultList {
	private List<Object> list = new ArrayList<>();

	public void add(Object o) {
		if (o == null) {
			this.list.add(o);
		} else if (o instanceof Collection) {
			addAll((Collection<?>) o);
		} else if (o.getClass() == ResultList.class) {
			addAll(((ResultList) o).getResult());
		} else {
			list.add(o);
		}
	}

	public void check(String field) {
		for (Object o : list) {
			if (CheckUtils.isEmpty(o)) {
				throw new ParamEmptyException(field);
			}
		}
	}

	public void addAll(Collection<?> list) {
		this.list.addAll(list);
	}

	public List<Object> getResult() {
		return this.list;
	}
}
