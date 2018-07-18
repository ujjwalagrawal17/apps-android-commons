package fr.free.nrw.commons.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;

class CategoriesRenderer extends Renderer<CategoryItem> {
//    @BindView(R.id.tvName) CheckedTextView checkedView;
    @BindView(R.id.tvName) TextView checkedView;
    @BindView(R.id.viewMoreIcon) ImageView viewMoreIcon;
    @BindView(R.id.categoryCheckbox) CheckBox categoryCheckbox;
    private final CategoryClickedListener listener;
    private final CategoryIconClickedListener categoryIconClickedListener;


    public CategoriesRenderer(CategoryClickedListener listener, CategoryIconClickedListener categoryIconClickedListener) {
        this.listener = listener;
        this.categoryIconClickedListener = categoryIconClickedListener;
    }

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.layout_categories_item, viewGroup, false);
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void hookListeners(View view) {
        view.setOnClickListener(v -> {
            CategoryItem item = getContent();
            checkedView.setText(item.getName());
            item.setSelected(!item.isSelected());
            categoryCheckbox.setChecked(item.isSelected());
            if (listener != null) {
                listener.categoryClicked(item);
            }
        });
        viewMoreIcon.setOnClickListener(v -> {
            CategoryItem item = getContent();
            if (categoryIconClickedListener != null) {
                categoryIconClickedListener.viewMoreIconClicked(item);
            }
        });
    }

    @Override
    public void render() {
        CategoryItem item = getContent();
        categoryCheckbox.setChecked(item.isSelected());
        checkedView.setText(item.getName());
    }

    interface CategoryClickedListener {
        void categoryClicked(CategoryItem item);
    }

    interface CategoryIconClickedListener {
        void viewMoreIconClicked(CategoryItem item);
    }

}
