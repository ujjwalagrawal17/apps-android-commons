package fr.free.nrw.commons.explore.images;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;


public class SearchImageDao {

    private final Provider<ContentProviderClient> clientProvider;

    @Inject
    public SearchImageDao(@Named("searched_image") Provider<ContentProviderClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    public void save(SearchedImage searchedImage) {
        ContentProviderClient db = clientProvider.get();
        try {
            if (searchedImage.getContentUri() == null) {
                searchedImage.setContentUri(db.insert(SearchImageContentProvider.BASE_URI, toContentValues(searchedImage)));
            } else {
                db.update(searchedImage.getContentUri(), toContentValues(searchedImage), null, null);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            db.release();
        }
    }

    /**
     * Find persisted images in database, based on its name.
     *
     * @param name Images's name
     * @return image from database, or null if not found
     */
    @Nullable
    SearchedImage find(String name) {
        Cursor cursor = null;
        ContentProviderClient db = clientProvider.get();
        try {
            cursor = db.query(
                    SearchImageContentProvider.BASE_URI,
                    Table.ALL_FIELDS,
                    Table.COLUMN_NAME + "=?",
                    new String[]{name},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        } catch (RemoteException e) {
            // This feels lazy, but to hell with checked exceptions. :)
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.release();
        }
        return null;
    }

    /**
     * Retrieve recently-searched images, ordered by descending date.
     *
     * @return a list containing recently searched images
     */
    @NonNull
    List<String> recentSearchedImages(int limit) {
        List<String> items = new ArrayList<>();
        Cursor cursor = null;
        ContentProviderClient db = clientProvider.get();
        try {
            cursor = db.query(
                    SearchImageContentProvider.BASE_URI,
                    Table.ALL_FIELDS,
                    null,
                    new String[]{},
                    Table.COLUMN_LAST_SEARCHED + " DESC");
            // fixme add a limit on the original query instead of falling out of the loop?
            while (cursor != null && cursor.moveToNext()
                    && cursor.getPosition() < limit) {
                items.add(fromCursor(cursor).getName());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.release();
        }
        return items;
    }

    @NonNull
    SearchedImage fromCursor(Cursor cursor) {
        // Hardcoding column positions!
        return new SearchedImage(
                SearchImageContentProvider.uriForId(cursor.getInt(cursor.getColumnIndex(Table.COLUMN_ID))),
                cursor.getString(cursor.getColumnIndex(Table.COLUMN_NAME)),
                new Date(cursor.getLong(cursor.getColumnIndex(Table.COLUMN_LAST_SEARCHED))),
                cursor.getInt(cursor.getColumnIndex(Table.COLUMN_TIMES_SEARCHED))
        );
    }

    private ContentValues toContentValues(SearchedImage searchedImage) {
        ContentValues cv = new ContentValues();
        cv.put(SearchImageDao.Table.COLUMN_NAME, searchedImage.getName());
        cv.put(SearchImageDao.Table.COLUMN_LAST_SEARCHED, searchedImage.getLastSearched().getTime());
        cv.put(SearchImageDao.Table.COLUMN_TIMES_SEARCHED, searchedImage.getTimesSearched());
        return cv;
    }

    public static class Table {
        public static final String TABLE_NAME = "searchimages";

        public static final String COLUMN_ID = "_id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_LAST_SEARCHED = "last_searched";
        static final String COLUMN_TIMES_SEARCHED = "times_searched";

        // NOTE! KEEP IN SAME ORDER AS THEY ARE DEFINED UP THERE. HELPS HARD CODE COLUMN INDICES.
        public static final String[] ALL_FIELDS = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_LAST_SEARCHED,
                COLUMN_TIMES_SEARCHED
        };

        static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME + " STRING,"
                + COLUMN_LAST_SEARCHED + " INTEGER,"
                + COLUMN_TIMES_SEARCHED + " INTEGER"
                + ");";

        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_STATEMENT);
        }

        public static void onDelete(SQLiteDatabase db) {
            db.execSQL(DROP_TABLE_STATEMENT);
            onCreate(db);
        }

        public static void onUpdate(SQLiteDatabase db, int from, int to) {
            if (from == to) {
                return;
            }
            if (from < 6) {
                // doesn't exist yet
                from++;
                onUpdate(db, from, to);
                return;
            }
            if (from == 6) {
                // table added in version 5
                onCreate(db);
                from++;
                onUpdate(db, from, to);
                return;
            }
            if (from == 7) {
                from++;
                onUpdate(db, from, to);
                return;
            }
        }
    }
}
