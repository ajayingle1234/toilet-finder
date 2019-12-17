package com.hardcastle.honeysuckervendor.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by abhijeet on 11/17/2017.
 */

public class AvailableDriverDetails implements Parcelable
{

    @SerializedName("STATUS")
    @Expose
    private int sTATUS;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("DATA")
    @Expose
    private ArrayList<DATum> dATA = null;
    private final static long serialVersionUID = 8937331996394585042L;

    /**
     * No args constructor for use in serialization
     *
     */
    public AvailableDriverDetails() {
    }

    /**
     *
     * @param mESSAGE
     * @param sTATUS
     * @param dATA
     */
    public AvailableDriverDetails(int sTATUS, String mESSAGE, ArrayList<DATum> dATA) {
        super();
        this.sTATUS = sTATUS;
        this.mESSAGE = mESSAGE;
        this.dATA = dATA;
    }

    protected AvailableDriverDetails(Parcel in) {
        sTATUS = in.readInt();
        mESSAGE = in.readString();
        dATA = in.createTypedArrayList(DATum.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sTATUS);
        dest.writeString(mESSAGE);
        dest.writeTypedList(dATA);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AvailableDriverDetails> CREATOR = new Creator<AvailableDriverDetails>() {
        @Override
        public AvailableDriverDetails createFromParcel(Parcel in) {
            return new AvailableDriverDetails(in);
        }

        @Override
        public AvailableDriverDetails[] newArray(int size) {
            return new AvailableDriverDetails[size];
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
        @SerializedName("DRIVER_NAME")
        @Expose
        private String dRIVERNAME;
        @SerializedName("MOBILE")
        @Expose
        private String mOBILE;
        @SerializedName("VEHICLE_NUMBER")
        @Expose
        private String vEHICLENUMBER;
        @SerializedName("LATITUDE")
        @Expose
        private String lATITUDE;
        @SerializedName("LONGITUDE")
        @Expose
        private String lONGITUDE;
        @SerializedName("WORK_STATUS")
        @Expose
        private String wORKSTATUS;
        @SerializedName("RATINGS")
        @Expose
        private String rATINGS;
        private final static long serialVersionUID = 6795837515454414008L;

        /**
         * No args constructor for use in serialization
         *
         */
        public DATum() {
        }

        /**
         *
         * @param dRIVERID
         * @param lATITUDE
         * @param wORKSTATUS
         * @param lONGITUDE
         * @param mOBILE
         * @param vEHICLENUMBER
         * @param dRIVERNAME
         * @param rATINGS
         */
        public DATum(String dRIVERID, String dRIVERNAME, String mOBILE, String vEHICLENUMBER, String lATITUDE, String lONGITUDE, String wORKSTATUS, String rATINGS) {
            super();
            this.dRIVERID = dRIVERID;
            this.dRIVERNAME = dRIVERNAME;
            this.mOBILE = mOBILE;
            this.vEHICLENUMBER = vEHICLENUMBER;
            this.lATITUDE = lATITUDE;
            this.lONGITUDE = lONGITUDE;
            this.wORKSTATUS = wORKSTATUS;
            this.rATINGS = rATINGS;
        }

        protected DATum(Parcel in) {
            dRIVERID = in.readString();
            dRIVERNAME = in.readString();
            mOBILE = in.readString();
            vEHICLENUMBER = in.readString();
            lATITUDE = in.readString();
            lONGITUDE = in.readString();
            wORKSTATUS = in.readString();
            rATINGS = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(dRIVERID);
            dest.writeString(dRIVERNAME);
            dest.writeString(mOBILE);
            dest.writeString(vEHICLENUMBER);
            dest.writeString(lATITUDE);
            dest.writeString(lONGITUDE);
            dest.writeString(wORKSTATUS);
            dest.writeString(rATINGS);
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

        public String getDRIVERID() {
            return dRIVERID;
        }

        public void setDRIVERID(String dRIVERID) {
            this.dRIVERID = dRIVERID;
        }

        public String getDRIVERNAME() {
            return dRIVERNAME;
        }

        public void setDRIVERNAME(String dRIVERNAME) {
            this.dRIVERNAME = dRIVERNAME;
        }

        public String getMOBILE() {
            return mOBILE;
        }

        public void setMOBILE(String mOBILE) {
            this.mOBILE = mOBILE;
        }

        public String getVEHICLENUMBER() {
            return vEHICLENUMBER;
        }

        public void setVEHICLENUMBER(String vEHICLENUMBER) {
            this.vEHICLENUMBER = vEHICLENUMBER;
        }

        public String getLATITUDE() {
            return lATITUDE;
        }

        public void setLATITUDE(String lATITUDE) {
            this.lATITUDE = lATITUDE;
        }

        public String getLONGITUDE() {
            return lONGITUDE;
        }

        public void setLONGITUDE(String lONGITUDE) {
            this.lONGITUDE = lONGITUDE;
        }

        public String getWORKSTATUS() {
            return wORKSTATUS;
        }

        public void setWORKSTATUS(String wORKSTATUS) {
            this.wORKSTATUS = wORKSTATUS;
        }

        public String getRATINGS() {
            return rATINGS;
        }

        public void setRATINGS(String rATINGS) {
            this.rATINGS = rATINGS;
        }

    }

}
