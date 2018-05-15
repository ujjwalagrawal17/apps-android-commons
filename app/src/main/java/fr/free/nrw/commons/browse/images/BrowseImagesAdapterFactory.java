package fr.free.nrw.commons.browse.images;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collections;
import java.util.List;


class BrowseImagesAdapterFactory {
    private final BrowseImagesRenderer.CategoryClickedListener listener;

    BrowseImagesAdapterFactory(BrowseImagesRenderer.CategoryClickedListener listener) {
        this.listener = listener;
    }

    public RVRendererAdapter<BrowsedImageItem> create(List<BrowsedImageItem> placeList) {
        RendererBuilder<BrowsedImageItem> builder = new RendererBuilder<BrowsedImageItem>()
                .bind(BrowsedImageItem.class, new BrowseImagesRenderer(listener));
        ListAdapteeCollection<BrowsedImageItem> collection = new ListAdapteeCollection<>(
                placeList != null ? placeList : Collections.<BrowsedImageItem>emptyList());
        return new RVRendererAdapter<>(builder, collection);
    }
}
