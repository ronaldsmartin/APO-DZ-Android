/**
 * 
 */
package org.upennapo.app;

/**
 * @author Ronald Martin
 *
 */
public class Brother {
	
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

	public String Email_Address;
	public String Phone_Number;
	
	public String Pledge_Class;
	public String Expected_Graduation_Year;
	
	public String School;
	public String Major;
	
	public String Birthday;
	
	@Override
	public String toString() {
		StringBuilder brotherBuilder = new StringBuilder();
		brotherBuilder.append("First name: ");
		brotherBuilder.append(First_Name);
		return brotherBuilder.toString();
	}
}
