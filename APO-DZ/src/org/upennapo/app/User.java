package org.upennapo.app;

public class User {

	// Name and Status
	public String First_Name;
	public String Last_Name;
	public String Status;

	// High-Level Completion
	public boolean Complete;

	// Service Details
	public boolean Service;
	public float Service_Hours;
	public float Required_Service_Hours;
	public boolean Large_Group_Project;
	public boolean Publicity;
	public boolean Service_Hosting;

	// Fellowship Details
	public boolean Fellowship;
	public float Fellowship_Points;
	public float Required_Fellowship;
	public boolean Fellowship_Hosting;

	// Membership Details
	public boolean Membership;
	public float Membership_Points;
	public float Required_Membership_Points;
	public boolean Pledge_Comp;
	public boolean Brother_Comp;
	
	@Override
	public String toString() {
		StringBuilder userStringBuilder = new StringBuilder();
		userStringBuilder.append(this.First_Name);
		userStringBuilder.append(" ");
		userStringBuilder.append(this.Last_Name);
		userStringBuilder.append(":\n\t");
		
		userStringBuilder.append("Service Hours: ");
		userStringBuilder.append(this.Service_Hours);
		userStringBuilder.append('\n');
		
		userStringBuilder.append("\tMembership Points: ");
		userStringBuilder.append(this.Membership_Points);
		userStringBuilder.append('\n');
		
		userStringBuilder.append("\tFellowship Points: ");
		userStringBuilder.append(this.Fellowship_Points);
		
		return userStringBuilder.toString();
	}
}
