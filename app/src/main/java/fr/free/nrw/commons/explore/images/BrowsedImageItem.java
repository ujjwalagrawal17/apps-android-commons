package fr.free.nrw.commons.explore.images;

import android.os.Parcel;
import android.os.Parcelable;

public class BrowsedImageItem implements Parcelable {
    private final String name;
    private boolean selected;

    public static Creator<BrowsedImageItem> CREATOR = new Creator<BrowsedImageItem>() {
        @Override
        public BrowsedImageItem createFromParcel(Parcel parcel) {
            return new BrowsedImageItem(parcel);
        }

        @Override
        public BrowsedImageItem[] newArray(int i) {
            return new BrowsedImageItem[0];
        }
    };

    public BrowsedImageItem(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    private BrowsedImageItem(Parcel in) {
        name = in.readString();
        selected = in.readInt() == 1;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeInt(selected ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BrowsedImageItem that = (BrowsedImageItem) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
