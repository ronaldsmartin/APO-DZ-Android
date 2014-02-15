package org.upennapo.app;

public class User {
	// Name and Status
	public String firstName;
	public String lastName;
	public String brotherStatus;

	// High-Level Completion
	public boolean doneWithRequirements;

	// Service Details
	public boolean doneWithService;
	public float serviceHours;
	public float reqServiceHours;
	public boolean mandatoryServiceDone;
	public boolean publicityDone;
	public boolean serviceHostingDone;

	// Fellowship Details
	public boolean doneWithFellowship;
	public float fellowshipPoints;
	public float reqFellowshipPoints;
	public boolean fellowshipHostingDone;

	// Membership Details
	public boolean doneWithMembership;
	public float membershipPoints;
	public float reqMembershipPoints;
	public boolean pledgeComponentDone;
	public boolean brotherComponentDone;
}
