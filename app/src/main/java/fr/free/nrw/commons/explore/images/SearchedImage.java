package fr.free.nrw.commons.explore.images;

import android.net.Uri;

import java.util.Date;

/**
 * Represents a recently searched image
 */
public class SearchedImage {
    private Uri contentUri;
    private String name;
    private Date lastSearched;
    private int timesSearched;

    public SearchedImage() {
    }

    public SearchedImage(Uri contentUri, String name, Date lastSearched, int timesSearched) {
        this.contentUri = contentUri;
        this.name = name;
        this.lastSearched = lastSearched;
        this.timesSearched = timesSearched;
    }

    /**
     * Gets name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Modifies name
     *
     * @param name Image name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets last searched date
     *
     * @return Last searched date
     */
    public Date getLastSearched() {
        // warning: Date objects are mutable.
        return (Date) lastSearched.clone();
    }

    /**
     * Generates new last searched date
     */
    private void touch() {
        lastSearched = new Date();
    }

    /**
     * Gets no. of times the images is searched
     *
     * @return no. of times searched
     */
    public int getTimesSearched() {
        return timesSearched;
    }

    /**
     * Increments timesSearched by 1 and sets last searched date as now.
     */
    public void incTimesSearched() {
        timesSearched++;
        touch();
    }

    /**
     * Gets the content URI for this image
     *
     * @return content URI
     */
    public Uri getContentUri() {
        return contentUri;
    }

    /**
     * Modifies the content URI - marking this image as already saved in the database
     *
     * @param contentUri the content URI
     */
    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

}