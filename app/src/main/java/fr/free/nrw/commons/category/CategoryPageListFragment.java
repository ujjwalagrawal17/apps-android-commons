package fr.free.nrw.commons.category;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pedrogomez.renderers.RVRendererAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.explore.categories.SearchCategoriesAdapterFactory;
import fr.free.nrw.commons.mwapi.MediaWikiApi;
import fr.free.nrw.commons.utils.NetworkUtils;
import fr.free.nrw.commons.utils.ViewUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Displays the category search screen.
 */

public class CategoryPageListFragment extends CommonsDaggerSupportFragment {

    private static int TIMEOUT_SECONDS = 15;

    @BindView(R.id.imagesListBox)
    RecyclerView categoriesRecyclerView;
    @BindView(R.id.imageSearchInProgress)
    ProgressBar progressBar;
    @BindView(R.id.imagesNotFound)
    TextView categoriesNotFoundView;

    private String categoryName = null;
    @Inject MediaWikiApi mwApi;

    private RVRendererAdapter<String> categoriesAdapter;
    private List<String> subCategoryList = new ArrayList<>();

    private final SearchCategoriesAdapterFactory adapterFactory = new SearchCategoriesAdapterFactory(item -> {
        // Open SubCategory Details page
        Intent intent = new Intent(getContext(), CategoryDetailsActivity.class);
        intent.putExtra("categoryName", item);
        getContext().startActivity(intent);

    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_image, container, false);
        ButterKnife.bind(this, rootView);
        categoryName = getArguments().getString("categoryName");
        initSubCategoryList();
        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        else{
            categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        ArrayList<String> items = new ArrayList<>();
        categoriesAdapter = adapterFactory.create(items);
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        categoriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // check if end of recycler view is reached, if yes then add more results to existing results
                if (!recyclerView.canScrollVertically(1)) {
                    addCategoriesToList();
                }
            }
        });
        return rootView;
    }

    /**
     * Checks for internet connection and then initializes the recycler view with 25 categories of the searched query
     * Clearing categoryAdapter every time new keyword is searched so that user can see only new results
     */
    public void initSubCategoryList() {
        categoriesNotFoundView.setVisibility(GONE);
        if(!NetworkUtils.isInternetConnectionEstablished(getContext())) {
            handleNoInternet();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
//        subCategoryList.clear();
//        categoriesAdapter.clear();

        Observable.fromCallable(() -> mwApi.getPagesInCategory(categoryName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .subscribe(this::handleSuccess, this::handleError);
    }


    /**
     * Adds more results to existing search results
     */
    public void addCategoriesToList() {
        progressBar.setVisibility(View.VISIBLE);
        Observable.fromCallable(() -> mwApi.getPagesInCategory(categoryName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .subscribe(this::handlePaginationSuccess, this::handleError);
    }

    /**
     * Handles the success scenario
     * it initializes the recycler view by adding items to the adapter
     * @param subCategoryList
     */
    private void handlePaginationSuccess(List<String> subCategoryList) {
        this.subCategoryList.addAll(subCategoryList);
        progressBar.setVisibility(View.GONE);
        categoriesAdapter.addAll(subCategoryList);
        categoriesAdapter.notifyDataSetChanged();
    }



    /**
     * Handles the success scenario
     * it initializes the recycler view by adding items to the adapter
     * @param subCategoryList
     */
    private void handleSuccess(List<String> subCategoryList) {
        this.subCategoryList = subCategoryList;
        if(subCategoryList == null || subCategoryList.isEmpty()) {
            initErrorView();
        }
        else {
            progressBar.setVisibility(View.GONE);
            categoriesAdapter.addAll(subCategoryList);
            categoriesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Logs and handles API error scenario
     * @param throwable
     */
    private void handleError(Throwable throwable) {
        Timber.e(throwable, "Error occurred while loading queried pages");
//        initErrorView();
    }

    /**
     * Handles the UI updates for a error scenario
     */
    private void initErrorView() {
        progressBar.setVisibility(GONE);
        categoriesNotFoundView.setVisibility(VISIBLE);
        categoriesNotFoundView.setText(getString(R.string.no_pages_found));
    }

    /**
     * Handles the UI updates for no internet scenario
     */
    private void handleNoInternet() {
        progressBar.setVisibility(GONE);
        ViewUtil.showSnackbar(categoriesRecyclerView, R.string.no_internet);
    }
}
