package fr.free.nrw.commons.browse.categories;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collections;
import java.util.List;


class BrowseCategoriesAdapterFactory {
    private final BrowseCategoriesRenderer.CategoryClickedListener listener;

    BrowseCategoriesAdapterFactory(BrowseCategoriesRenderer.CategoryClickedListener listener) {
        this.listener = listener;
    }

    public RVRendererAdapter<BrowsedCategoryItem> create(List<BrowsedCategoryItem> placeList) {
        RendererBuilder<BrowsedCategoryItem> builder = new RendererBuilder<BrowsedCategoryItem>()
                .bind(BrowsedCategoryItem.class, new BrowseCategoriesRenderer(listener));
        ListAdapteeCollection<BrowsedCategoryItem> collection = new ListAdapteeCollection<>(
                placeList != null ? placeList : Collections.<BrowsedCategoryItem>emptyList());
        return new RVRendererAdapter<>(builder, collection);
    }
}
