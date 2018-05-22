package fr.free.nrw.commons.explore.images;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pedrogomez.renderers.RVRendererAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import fr.free.nrw.commons.utils.StringSortingUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Displays the image search screen.
 */

public class SearchImageFragment extends CommonsDaggerSupportFragment {

    public static final int SEARCH_CATS_LIMIT = 25;

    @BindView(R.id.categoriesListBox)
    RecyclerView categoriesList;
    @BindView(R.id.categoriesSearchInProgress)
    ProgressBar categoriesSearchInProgress;
    @BindView(R.id.categoriesNotFound)
    TextView categoriesNotFoundView;

    @Inject
    MediaWikiApi mwApi;
    @Inject @Named("default_preferences") SharedPreferences prefs;

    private RVRendererAdapter<SearchImageItem> categoriesAdapter;
    private List<SearchImageItem> selectedCategories = new ArrayList<>();

    private final SearchImagesAdapterFactory adapterFactory = new SearchImagesAdapterFactory(item -> {

        Toast.makeText(getContext(),"Add images to recently searched images db table and move to Media Details Fragment ",Toast.LENGTH_LONG).show();

    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_image, container, false);
        ButterKnife.bind(this, rootView);

        categoriesList.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<SearchImageItem> items = new ArrayList<>();

        categoriesAdapter = adapterFactory.create(items);
        categoriesList.setAdapter(categoriesAdapter);

        return rootView;
    }

    public void updateImageList(String filter) {
        Observable.fromIterable(selectedCategories)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    categoriesSearchInProgress.setVisibility(View.VISIBLE);
                    categoriesNotFoundView.setVisibility(View.GONE);
                    categoriesAdapter.clear();
                })
                .observeOn(Schedulers.io())
                .concatWith(
                        searchCategories(filter)
                )
                .distinct()
                .sorted(sortBySimilarity(filter))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> categoriesAdapter.add(s),
                        Timber::e,
                        () -> {
                            categoriesAdapter.notifyDataSetChanged();
                            categoriesSearchInProgress.setVisibility(View.GONE);

                            if (categoriesAdapter.getItemCount() == selectedCategories.size()) {
                                if (TextUtils.isEmpty(filter)) {

                                } else {
                                    categoriesNotFoundView.setText(getString(R.string.images_not_found, filter));
                                    categoriesNotFoundView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
    }

    private Comparator<SearchImageItem> sortBySimilarity(final String filter) {
        Comparator<String> stringSimilarityComparator = StringSortingUtils.sortBySimilarity(filter);
        return (firstItem, secondItem) -> stringSimilarityComparator
                .compare(firstItem.getName(), secondItem.getName());
    }

    private Observable<SearchImageItem> searchCategories(String term) {
        //If user hasn't typed anything in yet, get GPS and recent items
        if (TextUtils.isEmpty(term)) {
            return Observable.empty();
        }

        return mwApi.searchImages(term, SEARCH_CATS_LIMIT)
                .map(s -> new SearchImageItem(s,false));
    }

}
