package ru.aviasales.template.filters;

import java.io.Serializable;

import ru.aviasales.expandedlistview.view.BaseCheckedText;

public class FilterCheckedAgency extends BaseCheckedText implements Serializable {
	private String id;

	public FilterCheckedAgency() {
		super();
	}

	public FilterCheckedAgency(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public FilterCheckedAgency(FilterCheckedAgency checkedAgency) {
		super(checkedAgency);
		id = checkedAgency.getId();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || ((Object) this).getClass() != o.getClass()) return false;

		FilterCheckedAgency agency = (FilterCheckedAgency) o;

		return !(id != null ? !id.equals(agency.id) : agency.id != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (checked != null ? checked.hashCode() : 0);
		return result;
	}
}