package fr.free.nrw.commons.browse;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.browse.images.BrowseImageFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
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

//        refreshView();
        setTabs();
    }

//    private void refreshView() {
//
//        progressBar.setVisibility(View.VISIBLE);
//        browseDisposable = Observable.fromCallable(() -> nearbyController.loadAttractionsFromLocation(curLatLang, this))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::populateTabs);
//    }

    public void setTabs() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        fragmentList.add(new BrowseImageFragment());
        titleList.add("Images");
        fragmentList.add(new BrowseImageFragment());
        titleList.add("Category");

        viewPagerAdapter.setTabData(fragmentList, titleList);
        viewPagerAdapter.notifyDataSetChanged();
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
