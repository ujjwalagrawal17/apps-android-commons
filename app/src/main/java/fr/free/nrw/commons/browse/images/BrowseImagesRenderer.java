package fr.free.nrw.commons.browse.images;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.MediaWikiImageView;
import fr.free.nrw.commons.R;

class BrowseImagesRenderer extends Renderer<BrowsedImageItem> {
    @BindView(R.id.tvImageName)
    TextView tvImageName;
    @BindView(R.id.browseImage)
    MediaWikiImageView browseImage;

    private final CategoryClickedListener listener;

    BrowseImagesRenderer(CategoryClickedListener listener) {
        this.listener = listener;
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.layout_browse_images_item, viewGroup, false);
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void hookListeners(View view) {
        view.setOnClickListener(v -> {
            BrowsedImageItem item = getContent();
            if (listener != null) {
                listener.categoryClicked(item);
            }
        });
    }

    @Override
    public void render() {
        BrowsedImageItem item = getContent();
        tvImageName.setText(item.getName());
        browseImage.setMedia(new Media(item.getName()));
    }

    interface CategoryClickedListener {
        void categoryClicked(BrowsedImageItem item);
    }
}
