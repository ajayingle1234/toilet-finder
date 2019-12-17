package com.hardcastle.honeysuckervendor.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by abhijeet on 10/30/2017.
 */

public class DriverProfile implements Parcelable{

    @SerializedName("STATUS")
    @Expose
    private int sTATUS;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private ArrayList<DATum> dATA = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DriverProfile() {
    }

    /**
     *
     * @param mESSAGE
     * @param sTATUS
     * @param dATA
     */
    public DriverProfile(int sTATUS, String mESSAGE, ArrayList<DATum> dATA) {
        super();
        this.sTATUS = sTATUS;
        this.mESSAGE = mESSAGE;
        this.dATA = dATA;
    }

    protected DriverProfile(Parcel in) {
        sTATUS = in.readInt();
        mESSAGE = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sTATUS);
        dest.writeString(mESSAGE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DriverProfile> CREATOR = new Creator<DriverProfile>() {
        @Override
        public DriverProfile createFromParcel(Parcel in) {
            return new DriverProfile(in);
        }

        @Override
        public DriverProfile[] newArray(int size) {
            return new DriverProfile[size];
        }
    };

    public int getSTATUS() {
        return sTATUS;
    }

    public void setSTATUS(int sTATUS) {
        this.sTATUS = sTATUS;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public ArrayList<DATum> getDATA() {
        return dATA;
    }

    public void setDATA(ArrayList<DATum> dATA) {
        this.dATA = dATA;
    }


    public static class DATum implements Parcelable
    {

        @SerializedName("DRIVER_ID")
        @Expose
        private String dRIVERID;
        @SerializedName("DRIVER_PHOTO")
        @Expose
        private String dRIVERPHOTO;
        @SerializedName("DRIVER_NAME")
        @Expose
        private String dRIVERNAME;
        @SerializedName("MOBILE")
        @Expose
        private String mOBILE;
        @SerializedName("EMAIL_ID")
        @Expose
        private String eMAILID;
        @SerializedName("VEHICLE_NUMBER")
        @Expose
        private String vEHICLE_NUMBER;
        @SerializedName("DRIVER_LICENCE_NUMBER")
        @Expose
        private String dRIVER_LICENCE_NUMBER;
        @SerializedName("VEHICLE_TANK_CAPACITY")
        @Expose
        private String vEHICLE_TANK_CAPACITY;
        @SerializedName("ADDRESS")
        @Expose
        private String aDDRESS;

        /**
         * No args constructor for use in serialization
         *
         */
        public DATum() {
        }

        public DATum(String dRIVERID, String dRIVERPHOTO, String dRIVERNAME, String mOBILE, String eMAILID, String vEHICLE_NUMBER, String dRIVER_LICENCE_NUMBER, String vEHICLE_TANK_CAPACITY, String aDDRESS) {
            this.dRIVERID = dRIVERID;
            this.dRIVERPHOTO = dRIVERPHOTO;
            this.dRIVERNAME = dRIVERNAME;
            this.mOBILE = mOBILE;
            this.eMAILID = eMAILID;
            this.vEHICLE_NUMBER = vEHICLE_NUMBER;
            this.dRIVER_LICENCE_NUMBER = dRIVER_LICENCE_NUMBER;
            this.vEHICLE_TANK_CAPACITY = vEHICLE_TANK_CAPACITY;
            this.aDDRESS = aDDRESS;
        }

        protected DATum(Parcel in) {
            dRIVERID = in.readString();
            dRIVERPHOTO = in.readString();
            dRIVERNAME = in.readString();
            mOBILE = in.readString();
            eMAILID = in.readString();
            vEHICLE_NUMBER = in.readString();
            dRIVER_LICENCE_NUMBER = in.readString();
            vEHICLE_TANK_CAPACITY = in.readString();
            aDDRESS = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(dRIVERID);
            dest.writeString(dRIVERPHOTO);
            dest.writeString(dRIVERNAME);
            dest.writeString(mOBILE);
            dest.writeString(eMAILID);
            dest.writeString(vEHICLE_NUMBER);
            dest.writeString(dRIVER_LICENCE_NUMBER);
            dest.writeString(vEHICLE_TANK_CAPACITY);
            dest.writeString(aDDRESS);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DATum> CREATOR = new Creator<DATum>() {
            @Override
            public DATum createFromParcel(Parcel in) {
                return new DATum(in);
            }

            @Override
            public DATum[] newArray(int size) {
                return new DATum[size];
            }
        };

        public String getdRIVERID() {
            return dRIVERID;
        }

        public void setdRIVERID(String dRIVERID) {
            this.dRIVERID = dRIVERID;
        }

        public String getdRIVERPHOTO() {
            return dRIVERPHOTO;
        }

        public void setdRIVERPHOTO(String dRIVERPHOTO) {
            this.dRIVERPHOTO = dRIVERPHOTO;
        }

        public String getdRIVERNAME() {
            return dRIVERNAME;
        }

        public void setdRIVERNAME(String dRIVERNAME) {
            this.dRIVERNAME = dRIVERNAME;
        }

        public String getmOBILE() {
            return mOBILE;
        }

        public void setmOBILE(String mOBILE) {
            this.mOBILE = mOBILE;
        }

        public String geteMAILID() {
            return eMAILID;
        }

        public void seteMAILID(String eMAILID) {
            this.eMAILID = eMAILID;
        }

        public String getvEHICLE_NUMBER() {
            return vEHICLE_NUMBER;
        }

        public void setvEHICLE_NUMBER(String vEHICLE_NUMBER) {
            this.vEHICLE_NUMBER = vEHICLE_NUMBER;
        }

        public String getdRIVER_LICENCE_NUMBER() {
            return dRIVER_LICENCE_NUMBER;
        }

        public void setdRIVER_LICENCE_NUMBER(String dRIVER_LICENCE_NUMBER) {
            this.dRIVER_LICENCE_NUMBER = dRIVER_LICENCE_NUMBER;
        }

        public String getvEHICLE_TANK_CAPACITY() {
            return vEHICLE_TANK_CAPACITY;
        }

        public void setvEHICLE_TANK_CAPACITY(String vEHICLE_TANK_CAPACITY) {
            this.vEHICLE_TANK_CAPACITY = vEHICLE_TANK_CAPACITY;
        }

        public String getaDDRESS() {
            return aDDRESS;
        }

        public void setaDDRESS(String aDDRESS) {
            this.aDDRESS = aDDRESS;
        }
    }

}
