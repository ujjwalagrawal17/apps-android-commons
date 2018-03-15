package fr.free.nrw.commons.browse.categories;

import android.os.Parcel;
import android.os.Parcelable;

public class BrowsedCategoryItem implements Parcelable {
    private final String name;
    private boolean selected;

    public static Creator<BrowsedCategoryItem> CREATOR = new Creator<BrowsedCategoryItem>() {
        @Override
        public BrowsedCategoryItem createFromParcel(Parcel parcel) {
            return new BrowsedCategoryItem(parcel);
        }

        @Override
        public BrowsedCategoryItem[] newArray(int i) {
            return new BrowsedCategoryItem[0];
        }
    };

    public BrowsedCategoryItem(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    private BrowsedCategoryItem(Parcel in) {
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

        BrowsedCategoryItem that = (BrowsedCategoryItem) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
