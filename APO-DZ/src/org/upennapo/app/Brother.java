/**
 *
 */
package org.upennapo.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Brother object represents an entry for a person in the directory.
 *
 * @author Ronald Martin
 */
public class Brother implements Parcelable, Comparable<Brother> {

    public static final Parcelable.Creator<Brother> CREATOR
            = new Parcelable.Creator<Brother>() {
        @Override
        public Brother createFromParcel(Parcel in) {
            return new Brother(in);
        }

        @Override
        public Brother[] newArray(int size) {
            return new Brother[size];
        }
    };
    // Instance Variables
    public String Last_Name;
    public String First_Name;
    public String Preferred_Name;
    public String Email_Address;
    public String Phone_Number;
    public String Pledge_Class;
    public String Expected_Graduation_Year;
    public String School;
    public String Major;
    public String Birthday;

    private Brother(Parcel in) {
        // Parcelable tutorial due to: http://guides.thecodepath.com/android/Using-Parcelable
        this.Last_Name = in.readString();
        this.First_Name = in.readString();
        this.Preferred_Name = in.readString();

        this.Email_Address = in.readString();
        this.Phone_Number = in.readString();

        this.Pledge_Class = in.readString();
        this.Expected_Graduation_Year = in.readString();

        this.School = in.readString();
        this.Major = in.readString();

        this.Birthday = in.readString();
    }

    @Override
    public String toString() {
        final String firstName = Preferred_Name.length() == 0 ? First_Name : Preferred_Name;
        return firstName + " " + Last_Name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Last_Name);
        out.writeString(First_Name);
        out.writeString(Preferred_Name);

        out.writeString(Email_Address);
        out.writeString(Phone_Number);

        out.writeString(Pledge_Class);
        out.writeString(Expected_Graduation_Year);

        out.writeString(School);
        out.writeString(Major);

        out.writeString(Birthday);
    }

    @Override
    public int compareTo(Brother brother) {
        return this.toString().compareTo(brother.toString());
    }
}
