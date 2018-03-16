package fr.free.nrw.commons.browse;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.browse.categories.BrowseCategoryFragment;
import fr.free.nrw.commons.browse.images.BrowseImageFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Represents about screen of this app
 */

public class BrowseActivity extends NavigationBaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.searchBox)
    EditText filter;

    ViewPagerAdapter viewPagerAdapter;

    private Bundle bundle;
    private Disposable browseDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        ButterKnife.bind(this);
        bundle = new Bundle();
        initDrawer();
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setTabs();
    }


    public void setTabs() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        BrowseImageFragment browseImageFragment = new BrowseImageFragment();
        BrowseCategoryFragment browseCategoryFragment = new BrowseCategoryFragment();
        fragmentList.add(browseImageFragment);
        titleList.add("IMAGES");
        fragmentList.add(browseCategoryFragment);
        titleList.add("CATEGORIES");

        viewPagerAdapter.setTabData(fragmentList, titleList);
        viewPagerAdapter.notifyDataSetChanged();
        RxTextView.textChanges(filter)
                .takeUntil(RxView.detaches(filter))
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( filter -> {
                        browseImageFragment.updateCategoryList(filter.toString());
                        browseCategoryFragment.updateCategoryList(filter.toString());
                        }
                        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (browseDisposable != null) {
            browseDisposable.dispose();
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
