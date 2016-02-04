package ru.aviasales.expandedlistview.view;

import java.io.Serializable;
import java.util.Comparator;

public class BaseCheckedText implements Serializable {
	protected String name;
	protected Boolean checked = true;

	public BaseCheckedText() {
		//не удОлять
	}

	public BaseCheckedText(String name) {
		this.name = name;
	}


	public BaseCheckedText(BaseCheckedText baseCheckedText) {
		name = baseCheckedText.getName();
		checked = baseCheckedText.isChecked();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseCheckedText that = (BaseCheckedText) o;

		return !(name != null ? !name.equals(that.name) : that.name != null)
				&& !(checked != null ? !checked.equals(that.checked) : that.checked != null);

	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (checked != null ? checked.hashCode() : 0);
		return result;
	}

	public final static Comparator<BaseCheckedText> nameComparator = new Comparator<BaseCheckedText>() {
		@Override
		public int compare(BaseCheckedText lhs, BaseCheckedText rhs) {
			return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
		}
	};
}
