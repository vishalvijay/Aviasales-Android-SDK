package ru.aviasales.template.ui.listener;

import ru.aviasales.template.ui.model.SearchFormData;

public interface AviasalesImpl extends OnPlaceSelectedListener {

	SearchFormData getSearchFormData();

	void saveState();

}
