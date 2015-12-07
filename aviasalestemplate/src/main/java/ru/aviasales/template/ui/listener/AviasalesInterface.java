package ru.aviasales.template.ui.listener;

import ru.aviasales.template.ui.model.SearchFormData;

public interface AviasalesInterface extends OnPlaceSelectedListener {

	SearchFormData getSearchFormData();

	void saveState();

}
