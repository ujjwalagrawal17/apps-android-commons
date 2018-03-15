package fr.free.nrw.commons.browse.images;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.nearby.Place;
import fr.free.nrw.commons.utils.UriDeserializer;
import timber.log.Timber;

public class BrowseImageFragment extends Fragment {
    private static final Type LIST_TYPE = new TypeToken<List<Place>>() {
    }.getType();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriDeserializer())
            .create();

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_image, container, false);
        ButterKnife.bind(this,view);
//        recyclerView = (RecyclerView) view.findViewById(R.id.listView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Check that this is the first time view is created,
        // to avoid double list when screen orientation changed
        List<Place> placeList = Collections.emptyList();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String gsonPlaceList = bundle.getString("PlaceList", "[]");
            placeList = gson.fromJson(gsonPlaceList, LIST_TYPE);

//            String gsonLatLng = bundle.getString("CurLatLng");
//            LatLng curLatLng = gson.fromJson(gsonLatLng, CUR_LAT_LNG_TYPE);

//            placeList = NearbyController.loadAttractionsFromLocationToPlaces(curLatLng, placeList);
        }

//        recyclerView.setAdapter(adapterFactory.create(placeList));
    }
}
