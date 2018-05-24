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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.explore.SearchActivity;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Displays the image search screen.
 */

public class SearchImageFragment extends CommonsDaggerSupportFragment {

    public static final int SEARCH_CATS_LIMIT = 25;

    @BindView(R.id.imagesListBox)
    RecyclerView imagesList;
    @BindView(R.id.imageSearchInProgress)
    ProgressBar imageSearchInProgress;
    @BindView(R.id.imagesNotFound)
    TextView imagesNotFoundView;
    @Inject SearchImageDao searchImageDao;

    @Inject
    MediaWikiApi mwApi;
    @Inject @Named("default_preferences") SharedPreferences prefs;

    private RVRendererAdapter<SearchImageItem> imagesAdapter;
    private List<SearchImageItem> selectedImages = new ArrayList<>();

    private final SearchImagesAdapterFactory adapterFactory = new SearchImagesAdapterFactory(item -> {
        ((SearchActivity)getContext()).onSearchImageClicked(item);
//        imagesAdapter.getPosi
//        Toast.makeText(getContext(),"Add images to recently searched images db table and move to Media Details Fragment ",Toast.LENGTH_LONG).show();

    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_image, container, false);
        ButterKnife.bind(this, rootView);

        imagesList.setLayoutManager(new LinearLayoutManager(getContext()));

        ArrayList<SearchImageItem> items = new ArrayList<>();

        imagesAdapter = adapterFactory.create(items);
        imagesList.setAdapter(imagesAdapter);

        return rootView;
    }

    public void updateImageList(String filter) {
        Observable.fromIterable(selectedImages)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    imageSearchInProgress.setVisibility(View.VISIBLE);
                    imagesNotFoundView.setVisibility(View.GONE);
                    imagesAdapter.clear();
                })
                .observeOn(Schedulers.io())
                .concatWith(
                        searchImages(filter)
                )
                .distinct()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        s -> imagesAdapter.add(s),
                        Timber::e,
                        () -> {
                            imagesAdapter.notifyDataSetChanged();
                            imageSearchInProgress.setVisibility(View.GONE);

                            if (imagesAdapter.getItemCount() == selectedImages.size()) {
                                if (TextUtils.isEmpty(filter)) {

                                } else {
                                    imagesNotFoundView.setText(getString(R.string.images_not_found, filter));
                                    imagesNotFoundView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                );
    }

    private Observable<SearchImageItem> searchImages(String term) {
        //If user hasn't typed anything in yet, get search history
        if (TextUtils.isEmpty(term)) {
            return Observable.empty();
        }

        return mwApi.searchImages(term, SEARCH_CATS_LIMIT)
                .map(s -> new SearchImageItem(s,false));
    }

    @Override
    public void onResume() {
        Toast.makeText(getContext(),"qwertyui",Toast.LENGTH_SHORT).show();
        if (imagesAdapter!=null)imagesAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void updateCategoryCount(SearchImageItem item) {
        SearchedImage searchedImage = searchImageDao.find(item.getName());

        // Newly used category...
        if (searchedImage == null) {
            searchedImage = new SearchedImage(null, item.getName(), new Date(), 0);
        }

        searchedImage.incTimesSearched();
        searchImageDao.save(searchedImage);
    }
}
