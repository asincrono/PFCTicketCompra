package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Subcategory;

public interface SubcategorySelectionCallback {
    public void onSubcategorySelected(Subcategory subcategory, int position);
    public void onCancelSubcategorySelection();
}
