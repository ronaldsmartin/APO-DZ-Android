/**
 *
 */
package org.upennapo.app.model;

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
    public String Graduation_Year;
    public String School;
    public String Major;
    public String Location;
    public String Birthday;

    public Brother() {
    }

    protected Brother(Parcel in) {
        // Parcelable tutorial due to: http://guides.thecodepath.com/android/Using-Parcelable
        this.Last_Name = in.readString();
        this.First_Name = in.readString();
        this.Preferred_Name = in.readString();

        this.Email_Address = in.readString();
        this.Phone_Number = in.readString();

        this.Pledge_Class = in.readString();
        this.Graduation_Year = in.readString();

        this.School = in.readString();
        this.Major = in.readString();

        this.Birthday = in.readString();
        this.Location = in.readString();
    }

    @Override
    public String toString() {
        final String firstName = Preferred_Name.length() == 0 ? First_Name : Preferred_Name;
        return firstName + " " + Last_Name;
    }

    /**
     * Provides a shortening of this person's School and Year. If multiple schools are found,
     * separate them by forward-slash. This abbreviation is specific to the University of Pennsylvania.
     * <p/>
     * e.g. (College of Arts & Sciences, 2014) -> CAS'14
     *
     * @return abbreviation of person's school info
     */
    public String gradAbbreviation() {
        // Make a school abbreviation.
        final String[] schools = this.School.split(", ");
        StringBuilder abbrevBuilder = new StringBuilder();
        for (int i = 0, length = schools.length; i < length; ++i) {
            final String school = schools[i];

            if ("College of Arts and Sciences".equals(school))
                abbrevBuilder.append("CAS");
            else if ("The Wharton School".equals(school))
                abbrevBuilder.append("W");
            else if ("School of Engineering and Applied Science".equals(school))
                abbrevBuilder.append("SEAS");
            else if ("School of Nursing".equals(school))
                abbrevBuilder.append("NURS");

            // Add the '/' separator if we haven't reached the end.
            if (i < length - 1) abbrevBuilder.append("/");
        }
        final String schoolAbbrev = abbrevBuilder.toString();

        // Make a year abbreviation.
        final String yearSuffix = this.Graduation_Year.length() == 4 ?
                "'" + this.Graduation_Year.substring(2) : this.Graduation_Year;

        return schoolAbbrev + yearSuffix;
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
        out.writeString(Graduation_Year);

        out.writeString(School);
        out.writeString(Major);

        out.writeString(Birthday);
        out.writeString(Location);
    }

    @Override
    public int compareTo(Brother brother) {
        return this.toString().compareTo(brother.toString());
    }
}
