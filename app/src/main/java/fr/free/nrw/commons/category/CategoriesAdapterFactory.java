package fr.free.nrw.commons.category;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collections;
import java.util.List;

class CategoriesAdapterFactory {
    private final CategoriesRenderer.CategoryClickedListener listener;
    private final CategoriesRenderer.CategoryIconClickedListener categoryIconClickedListener;

    CategoriesAdapterFactory(CategoriesRenderer.CategoryClickedListener listener,CategoriesRenderer.CategoryIconClickedListener categoryIconClickedListener) {
        this.listener = listener;
        this.categoryIconClickedListener = categoryIconClickedListener;
    }

    public RVRendererAdapter<CategoryItem> create(List<CategoryItem> placeList) {
        RendererBuilder<CategoryItem> builder = new RendererBuilder<CategoryItem>()
                .bind(CategoryItem.class, new CategoriesRenderer(listener, categoryIconClickedListener));
        ListAdapteeCollection<CategoryItem> collection = new ListAdapteeCollection<>(
                placeList != null ? placeList : Collections.<CategoryItem>emptyList());
        return new RVRendererAdapter<>(builder, collection);
    }
}
