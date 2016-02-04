package ru.aviasales.template.filters;

import java.util.List;

import ru.aviasales.expandedlistview.view.BaseCheckedText;

public abstract class BaseListFilter {

	public void mergeFilter(BaseListFilter newFilter) {
		if (newFilter.isActive()) {
			for (BaseCheckedText oldCheckedText : getCheckedTextList()) {
				for (BaseCheckedText newCheckedText : newFilter.getCheckedTextList()) {
					if (oldCheckedText.getName().equals(newCheckedText.getName())) {
						oldCheckedText.setChecked(newCheckedText.isChecked());
					}
				}
			}
		}
	}

	public boolean isActive() {
		for (BaseCheckedText checkedText : getCheckedTextList()) {
			if (!checkedText.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid() {
		return !getCheckedTextList().isEmpty();
	}

	public void clearFilter() {
		for (BaseCheckedText checkedText : getCheckedTextList()) {
			checkedText.setChecked(true);
		}
	}

	public abstract List<? extends BaseCheckedText> getCheckedTextList();
}
