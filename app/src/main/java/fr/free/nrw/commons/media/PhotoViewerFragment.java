package fr.free.nrw.commons.media;

import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;

import fr.free.nrw.commons.LicenseList;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.MediaDataExtractor;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import timber.log.Timber;

public class PhotoViewerFragment extends CommonsDaggerSupportFragment {

    private MediaDetailPagerFragment.MediaDetailProvider detailProvider;
    private int index;

    public static PhotoViewerFragment forMedia(int index) {
        PhotoViewerFragment mf = new PhotoViewerFragment();

        Bundle state = new Bundle();
        state.putInt("index", index);
        state.putInt("listIndex", 0);
        state.putInt("listTop", 0);

        mf.setArguments(state);

        return mf;
    }

    @Inject
    Provider<MediaDataExtractor> mediaDataExtractorProvider;

    private DataSetObserver dataObserver;
    private AsyncTask<Void, Void, Boolean> detailFetchTask;
    private LicenseList licenseList;
    private GestureImageView photoView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", index);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        detailProvider = (MediaDetailPagerFragment.MediaDetailProvider) getActivity();
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index");
        } else {
            index = getArguments().getInt("index");
        }

        final View view = inflater.inflate(R.layout.fragment_media_viewer, container, false);
        photoView = (GestureImageView) view.findViewById(R.id.photo_view);
        licenseList = new LicenseList(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Media media = detailProvider.getMediaAtPosition(index);
        if (media == null) {
            // Ask the detail provider to ping us when we're ready
            Timber.d("MediaDetailFragment not yet ready to display details; registering observer");
            dataObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    if (!isAdded()) {
                        return;
                    }
                    Timber.d("MediaDetailFragment ready to display delayed details!");
                    detailProvider.unregisterDataSetObserver(dataObserver);
                    dataObserver = null;
                    displayMediaDetails(detailProvider.getMediaAtPosition(index));
                }
            };
            detailProvider.registerDataSetObserver(dataObserver);
        } else {
            Timber.d("MediaDetailFragment ready to display details");
            displayMediaDetails(media);
        }
    }

    private void displayMediaDetails(final Media media) {
        //Always load image from Internet to allow viewing the desc, license, and cats
        detailFetchTask = new AsyncTask<Void, Void, Boolean>() {
            private MediaDataExtractor extractor;

            @Override
            protected void onPreExecute() {
                extractor = mediaDataExtractorProvider.get();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    extractor.fetch(media.getFilename(), licenseList);
                    return Boolean.TRUE;
                } catch (IOException e) {
                    Timber.d(e);
                }
                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                detailFetchTask = null;
                if (!isAdded()) {
                    return;
                }

                if (success) {
                    extractor.fill(media);

//                    setTextFields(media);
                    openPhotoViewer(media.getImageUrl());
                } else {
                    Timber.d("Failed to load photo details.");
                }
            }
        };
        detailFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroyView() {
        if (detailFetchTask != null) {
            detailFetchTask.cancel(true);
            detailFetchTask = null;
        }
        if (dataObserver != null) {
            detailProvider.unregisterDataSetObserver(dataObserver);
            dataObserver = null;
        }
        super.onDestroyView();
    }

    private void openPhotoViewer(String imageUrl) {
//        Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();
        photoView.setImageURI(Uri.parse(imageUrl));
        imageView.getController().getSettings()
                .setMaxZoom(5f)
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance(10f, 10f)
                .setOverzoomFactor(3f)
                .setFitMethod(Settings.Fit.INSIDE)
                //.setFillViewport(true)
                //    .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);
//        if (context instanceof ImageViewerActivity) {
//            ((ImageViewerActivity) context).enableScroll(imageView);
//        }
//        imageView.setU
//        photoView.setImageResource(R.drawable.commons_logo_large);

    }

}
