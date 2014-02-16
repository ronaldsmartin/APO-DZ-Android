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
public class Brother{
	
	// Keys
	public static final String LAST_NAME_KEY = "Last_Name";
	public static final String FIRST_NAME_KEY = "First_Name";
	public static final String PREFERRED_NAME_KEY = "Preferred_Name";
	public static final String PLEDGE_CLASS_KEY = "Pledge_Class";
	public static final String EMAIL_ADDRESS_KEY = "E-Mail_Address";
	public static final String PHONE_NUMBER_KEY = "Phone_Number";
	public static final String GRADUATION_YEAR_KEY = "Expected_Graduation_Year";
	public static final String SCHOOL_KEY = "School";
	public static final String MAJOR_KEY = "Major";
	public static final String BIRTHDAY_KEY = "Birthday";
	
	// Instance Variables
	public String lastName;
	public String firstName;

	public String emailAddress;
	public String phoneNumber;
	
	public String pledgeClass;
	public int graduationYear;
	
	public String school;
	public String major;
	
	public String birthday;



}
