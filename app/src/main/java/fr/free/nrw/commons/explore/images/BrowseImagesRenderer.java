package fr.free.nrw.commons.explore.images;

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
    @BindView(R.id.categoryImageTitle) TextView tvImageName;
    @BindView(R.id.categoryImageAuthor) TextView categoryImageAuthor;
    @BindView(R.id.categoryImageView) MediaWikiImageView browseImage;

    private final CategoryClickedListener listener;

    BrowseImagesRenderer(CategoryClickedListener listener) {
        this.listener = listener;
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.layout_category_images, viewGroup, false);
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
        Media media = new Media(item.getName());
        tvImageName.setText(item.getName());
        browseImage.setMedia(media);
        setAuthorView(media, categoryImageAuthor);
    }

    interface CategoryClickedListener {
        void categoryClicked(BrowsedImageItem item);
    }

    private void setAuthorView(Media item, TextView author) {
        if (item.getCreator() != null && !item.getCreator().equals("")) {
            String uploadedByTemplate = getContext().getString(R.string.image_uploaded_by);
            author.setText(String.format(uploadedByTemplate, item.getCreator()));
        } else {
            author.setVisibility(View.VISIBLE);
        }
    }
}
