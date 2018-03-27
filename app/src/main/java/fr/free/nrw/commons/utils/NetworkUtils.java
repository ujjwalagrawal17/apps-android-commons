package fr.free.nrw.commons.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NetworkUtils {

    private static Single<Boolean> isInternetConnectionEstablishedObservable(final Context context) {
        return Single.fromCallable(() -> isInternetConnectionEstablished(context));
    }

    public static boolean isInternetConnectionEstablished(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void checkConnection(WeakReference<Context> reference, final NetworkStateReceiverListener listener) {
        if (reference.get() == null || listener == null)
            return;

        isInternetConnectionEstablishedObservable(reference.get())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasConnection -> {

                    if (hasConnection) {
                        listener.networkAvailable();
                    } else {
                        listener.networkUnavailable();
                    }

                }, throwable -> {
                    throwable.printStackTrace();
                    Timber.e("Network Determination Error : %s", throwable.getMessage());
                });
    }

    public interface NetworkStateReceiverListener {

        void networkAvailable();

        void networkUnavailable();
    }
}
