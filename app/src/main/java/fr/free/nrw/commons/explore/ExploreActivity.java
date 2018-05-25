package fr.free.nrw.commons.explore;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import butterknife.ButterKnife;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.category.CategoryImagesListFragment;
import fr.free.nrw.commons.media.MediaDetailPagerFragment;
import fr.free.nrw.commons.theme.NavigationBaseActivity;

/**
 * This activity displays pictures of a particular category
 * Its generic and simply takes the name of category name in its start intent to load all images in
 * a particular category. This activity is currently being used to display a list of featured images,
 * which is nothing but another category on wikimedia commons.
 */

public class ExploreActivity extends NavigationBaseActivity
        implements MediaDetailPagerFragment.MediaDetailProvider,
                    AdapterView.OnItemClickListener{


//    private static final String FEATURED_IMAGES_CATEGORY = "Category:Featured_pictures_on_Wikimedia_Commons";
    private static final String FEATURED_IMAGES_CATEGORY = "Category:Pictures_of_the_day_(2018)";
    private FragmentManager supportFragmentManager;
    private CategoryImagesListFragment categoryImagesListFragment;
    private MediaDetailPagerFragment mediaDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        ButterKnife.bind(this);

        // Activity can call methods in the fragment by acquiring a
        // reference to the Fragment from FragmentManager, using findFragmentById()
        supportFragmentManager = getSupportFragmentManager();
        setCategoryImagesFragment();
        initDrawer();
        setTitle(R.string.title_activity_explore);
    }

    /**
     * Gets the categoryName from the intent and initializes the fragment for showing images of that category
     */
    private void setCategoryImagesFragment() {
        categoryImagesListFragment = new CategoryImagesListFragment();
        Bundle arguments = new Bundle();
        arguments.putString("categoryName", FEATURED_IMAGES_CATEGORY);
        categoryImagesListFragment.setArguments(arguments);
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer, categoryImagesListFragment).commit();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
        mediaDetails.showImage(i);
    }

    @Override
    public Media getMediaAtPosition(int i) {
        if (categoryImagesListFragment.getAdapter() == null) {
            // not yet ready to return data
            return null;
        } else {
            return (Media) categoryImagesListFragment.getAdapter().getItem(i);
        }
    }

    @Override
    public int getTotalMediaCount() {
        if (categoryImagesListFragment.getAdapter() == null) {
            return 0;
        }
        return categoryImagesListFragment.getAdapter().getCount();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                NavigationBaseActivity.startActivityWithFlags(this, SearchActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
