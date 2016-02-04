package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.SearchData;


public interface FiltersSet extends Serializable{
	List<Proposal> applyFilters(ru.aviasales.core.search.object.SearchData searchData);

	void setContext(Context context);

	BaseNumericFilter getPriceFilter();

	List<AirlinesFilter> getAirlinesFilters();

	List<AirportsFilter> getAirportsFilters();

	boolean isValid();

	boolean isActive();

	void clearFilters();

	void initMinAndMaxValues(Context context, SearchData searchData, List<Proposal> proposals);

	void mergeFiltersValues(FiltersSet filtersSet);

	FiltersSet getCopy(Context context);
}
