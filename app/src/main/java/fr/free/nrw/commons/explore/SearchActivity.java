package fr.free.nrw.commons.explore;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.explore.images.SearchImageFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Represents search screen of this app
 */

public class SearchActivity extends NavigationBaseActivity implements FragmentManager.OnBackStackChangedListener {

    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.searchBox) EditText etSearchKeyword;

    private SearchImageFragment searchImageFragment;
    private FragmentManager supportFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(v->onBackPressed());
        supportFragmentManager = getSupportFragmentManager();
        setBrowseImagesFragment();
        supportFragmentManager.addOnBackStackChangedListener(this);
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
    public void onBackStackChanged() {

    }
}
