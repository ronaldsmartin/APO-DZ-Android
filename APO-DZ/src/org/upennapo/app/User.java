package org.upennapo.app;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    /**
     * Creator is used to de-marshal User from Parcel
     */
    public static final Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    // Name and Status
    public String First_Name;
    public String Last_Name;
    public String First_and_Last_Name;
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

    /**
     * No argument constructor for GSON (de-)serialization.
     */
    public User() {
    }

    /**
     * Private constructor used by Parcelable.Creator to deserialze a Parcelled User
     *
     * @param in - parcel to read from
     */
    private User(Parcel in) {
        this.First_Name = in.readString();
        this.Last_Name = in.readString();
        this.First_and_Last_Name = in.readString();
        this.Status = in.readString();

        boolean[] completion = new boolean[10];
        in.readBooleanArray(completion);
        this.Complete = completion[0];

        this.Service = completion[1];
        this.Large_Group_Project = completion[2];
        this.Publicity = completion[3];
        this.Service_Hosting = completion[4];

        this.Fellowship = completion[5];
        this.Fellowship_Hosting = completion[6];

        this.Membership = completion[7];
        this.Pledge_Comp = completion[8];
        this.Brother_Comp = completion[9];

        this.Service_Hours = in.readFloat();
        this.Required_Service_Hours = in.readFloat();

        this.Fellowship_Points = in.readFloat();
        this.Required_Fellowship = in.readFloat();

        this.Membership_Points = in.readFloat();
        this.Required_Membership_Points = in.readFloat();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.First_Name);
        out.writeString(this.Last_Name);
        out.writeString(this.First_and_Last_Name);
        out.writeString(this.Status);

        boolean[] completion =
                {Complete, Service, Large_Group_Project, Publicity, Service_Hosting,
                        Fellowship, Fellowship_Hosting,
                        Membership, Pledge_Comp, Brother_Comp};
        out.writeBooleanArray(completion);

        out.writeFloat(Service_Hours);
        out.writeFloat(Required_Service_Hours);

        out.writeFloat(Fellowship_Points);
        out.writeFloat(Required_Fellowship);

        out.writeFloat(Membership_Points);
        out.writeFloat(Required_Membership_Points);
    }
}
