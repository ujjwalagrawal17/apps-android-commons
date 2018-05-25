package fr.free.nrw.commons.explore;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.explore.images.SearchHistoryFragment;
import fr.free.nrw.commons.explore.images.SearchImageFragment;
import fr.free.nrw.commons.explore.images.SearchImageItem;
import fr.free.nrw.commons.media.MediaDetailPagerFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import fr.free.nrw.commons.utils.ViewUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Represents search screen of this app
 */

public class SearchActivity extends NavigationBaseActivity implements MediaDetailPagerFragment.MediaDetailProvider{

    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.searchBox) EditText etSearchKeyword;

    private SearchImageFragment searchImageFragment;
    private SearchHistoryFragment searchHistoryFragment;
    private FragmentManager supportFragmentManager;
    private MediaDetailPagerFragment mediaDetails;
    SearchImageItem searchImageItem;
    private static final int PANEL_RECENT_SEARCHES = 0;
    private static final int PANEL_SEARCH_RESULTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v->onBackPressed());
        supportFragmentManager = getSupportFragmentManager();
        setBrowseImagesFragment();
        RxTextView.textChanges(etSearchKeyword)
                .takeUntil(RxView.detaches(etSearchKeyword))
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( query -> {
                        //update image list
                            if (!TextUtils.isEmpty(query)) {
//                                showPanel(PANEL_RECENT_SEARCHES);
                                searchImageFragment.updateImageList(query.toString());
                            }else {
//                                showPanel(PANEL_SEARCH_RESULTS);
                            }
                        }
                );
    }


    private void setBrowseImagesFragment() {
        searchImageFragment = new SearchImageFragment();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer, searchImageFragment).commit();
//        searchImageFragment = (SearchImageFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);
//        searchHistoryFragment = (SearchHistoryFragment) fragmentManager.findFragmentById(R.id.search_panel_recent);
    }

    @Override
    public Media getMediaAtPosition(int i) {
        if (searchImageItem.getName() == null) {
            // not yet ready to return data
            return null;
        } else {
            return new Media(searchImageItem.getName());
        }
    }

    @Override
    public int getTotalMediaCount() {
        return 1;
//        if (searchImageFragment.getAdapter() == null) {
//            return 0;
//        }
//        return searchImageFragment.getAdapter().getCount();
    }

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    public void onSearchImageClicked(SearchImageItem searchImageItem) {
        this.searchImageItem = searchImageItem;
        ViewUtil.hideKeyboard(this.findViewById(R.id.searchBox));
        toolbar.setVisibility(View.GONE);
        setToolbarVisibility(true);
        if (mediaDetails == null || !mediaDetails.isVisible()) {
            // set isFeaturedImage true for featured images, to include author field on media detail
            mediaDetails = new MediaDetailPagerFragment(false, true);
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, mediaDetails)
                    .addToBackStack(null)
                    .commit();
            supportFragmentManager.executePendingTransactions();
        }
        mediaDetails.showImage(0);
    }

    @Override
    protected void onResume() {
//        toolbar.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount()==1){
//            Toast.makeText(this,getSupportFragmentManager().getBackStackEntryCount()+"",Toast.LENGTH_SHORT).show();
            toolbar.setVisibility(View.VISIBLE);
            setToolbarVisibility(false);
        }else {
            toolbar.setVisibility(View.GONE);
            setToolbarVisibility(true);
        }
        super.onBackPressed();
    }

//    /**
//     * Show a particular panel, which can be one of:
//     * - PANEL_RECENT_SEARCHES
//     * - PANEL_SEARCH_RESULTS
//     * Automatically hides the previous panel.
//     *
//     * @param panel Which panel to show.
//     */
//    private void showPanel(int panel) {
//        switch (panel) {
//            case PANEL_RECENT_SEARCHES:
//                searchImageFragment.hide();
//                searchHistoryFragment.show();
//                break;
//            case PANEL_SEARCH_RESULTS:
//                searchHistoryFragment.hide();
//                searchImageFragment.show();
//                break;
//            default:
//                break;
//        }
//    }

    private int getActivePanel() {
        if (searchImageFragment.isShowing()) {
            return PANEL_SEARCH_RESULTS;
        } else {
            //otherwise, the recent searches must be showing:
            return PANEL_RECENT_SEARCHES;
        }
    }
}
