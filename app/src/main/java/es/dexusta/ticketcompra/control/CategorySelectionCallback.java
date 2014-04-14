package es.dexusta.ticketcompra.control;

import es.dexusta.ticketcompra.model.Category;

public interface CategorySelectionCallback {
    public void onCategorySelected(Category category, int position);
    public void onCancelCategorySelection();
}
