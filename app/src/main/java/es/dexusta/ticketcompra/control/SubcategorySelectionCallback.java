package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Subcategory;

public interface SubcategorySelectionCallback extends FragmentCallback {
    public void onSubcategorySelected(Subcategory subcategory, int position);

    public SubcategoryAdapter getSubcategoryAdapter();

    public int getSelectedSubcategoryPostion();

    public void onCancelSubcategorySelection();
}
