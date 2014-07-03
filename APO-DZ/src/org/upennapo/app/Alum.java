package org.upennapo.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ronald Martin.
 */
public class Alum extends Brother implements Parcelable {

    public static final Parcelable.Creator<Alum> CREATOR
            = new Parcelable.Creator<Alum>() {
        @Override
        public Alum createFromParcel(Parcel in) {
            return new Alum(in);
        }

        @Override
        public Alum[] newArray(int size) {
            return new Alum[size];
        }
    };
    public String Graduation_Year;
    public String Location;

    private Alum(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(Graduation_Year);
        out.writeString(Location);
    }
}
