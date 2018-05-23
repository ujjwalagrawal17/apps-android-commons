package fr.free.nrw.commons.explore;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.category.Category;
import fr.free.nrw.commons.explore.images.SearchImageFragment;
import fr.free.nrw.commons.explore.images.SearchImageItem;
import fr.free.nrw.commons.media.MediaDetailPagerFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Represents search screen of this app
 */

public class SearchActivity extends NavigationBaseActivity implements MediaDetailPagerFragment.MediaDetailProvider{

    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.searchBox) EditText etSearchKeyword;

    private SearchImageFragment searchImageFragment;
    private FragmentManager supportFragmentManager;
    private MediaDetailPagerFragment mediaDetails;
    SearchImageItem searchImageItem;

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
                .subscribe( filter -> {
                        //update image list
                            searchImageFragment.updateImageList(filter.toString());
                        }
                );
    }


    private void setBrowseImagesFragment() {
        searchImageFragment = new SearchImageFragment();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction .add(R.id.fragmentContainer, searchImageFragment).commit();
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
        Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this,getSupportFragmentManager().getBackStackEntryCount()+"",Toast.LENGTH_SHORT).show();
            toolbar.setVisibility(View.VISIBLE);
            setToolbarVisibility(false);
        }else {
            toolbar.setVisibility(View.GONE);
            setToolbarVisibility(true);
        }
        super.onBackPressed();
    }
}
