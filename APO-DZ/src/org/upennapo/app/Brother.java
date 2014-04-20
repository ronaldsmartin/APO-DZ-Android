/**
 * 
 */
package org.upennapo.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Ronald Martin
 *
 */
public class Brother implements Parcelable {
	
	// Keys
	public static final String LAST_NAME_KEY = "Last_Name";
	public static final String FIRST_NAME_KEY = "First_Name";
	public static final String PREFERRED_NAME_KEY = "Preferred_Name";
	public static final String PLEDGE_CLASS_KEY = "Pledge_Class";
	public static final String EMAIL_ADDRESS_KEY = "Email_Address";
	public static final String PHONE_NUMBER_KEY = "Phone_Number";
	public static final String GRADUATION_YEAR_KEY = "Expected_Graduation_Year";
	public static final String SCHOOL_KEY = "School";
	public static final String MAJOR_KEY = "Major";
	public static final String BIRTHDAY_KEY = "Birthday";
	
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
		StringBuilder brotherBuilder = new StringBuilder();
		brotherBuilder.append(First_Name);
		brotherBuilder.append(" ");
		brotherBuilder.append(Last_Name);
		return brotherBuilder.toString();
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
}
