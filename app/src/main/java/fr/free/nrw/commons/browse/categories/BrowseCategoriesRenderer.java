package fr.free.nrw.commons.browse.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;

class BrowseCategoriesRenderer extends Renderer<BrowsedCategoryItem> {
    @BindView(R.id.tvCategoryName)
    TextView tvCategoryName;
    private final CategoryClickedListener listener;

    BrowseCategoriesRenderer(CategoryClickedListener listener) {
        this.listener = listener;
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.layout_browse_categories_item, viewGroup, false);
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void hookListeners(View view) {
        view.setOnClickListener(v -> {
            BrowsedCategoryItem item = getContent();
            if (listener != null) {
                listener.categoryClicked(item);
            }
        });
    }

    @Override
    public void render() {
        BrowsedCategoryItem item = getContent();
        tvCategoryName.setText(item.getName());
    }

    interface CategoryClickedListener {
        void categoryClicked(BrowsedCategoryItem item);
    }
}
