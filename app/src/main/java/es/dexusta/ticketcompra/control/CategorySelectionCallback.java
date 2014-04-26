package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Category;

public interface CategorySelectionCallback extends FragmentABCallback {
    public void onCategorySelected(Category category, int position);

    public CategoryAdapter getCategoryAdapter();

    public int getSelectedCategoryPosition();

    public void onCancelCategorySelection();
}
