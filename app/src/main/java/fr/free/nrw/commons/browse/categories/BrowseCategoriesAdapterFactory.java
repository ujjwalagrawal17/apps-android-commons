package fr.free.nrw.commons.browse.categories;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collections;
import java.util.List;

import fr.free.nrw.commons.category.CategoriesRenderer;
import fr.free.nrw.commons.category.CategoryItem;

class BrowseCategoriesAdapterFactory {
    private final CategoriesRenderer.CategoryClickedListener listener;

    BrowseCategoriesAdapterFactory(CategoriesRenderer.CategoryClickedListener listener) {
        this.listener = listener;
    }

    public RVRendererAdapter<CategoryItem> create(List<CategoryItem> placeList) {
        RendererBuilder<CategoryItem> builder = new RendererBuilder<CategoryItem>()
                .bind(CategoryItem.class, new CategoriesRenderer(listener));
        ListAdapteeCollection<CategoryItem> collection = new ListAdapteeCollection<>(
                placeList != null ? placeList : Collections.<CategoryItem>emptyList());
        return new RVRendererAdapter<>(builder, collection);
    }
}
