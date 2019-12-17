package com.hardcastle.honeysuckervendor.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VendorDetails implements Parcelable {

    @SerializedName("STATUS")
    @Expose private int sTATUS;

    @SerializedName("MESSAGE")
    @Expose private String mESSAGE;

    @SerializedName("DATA")
    @Expose private ArrayList<DATum> dATA = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public VendorDetails() {
    }

    /**
     *
     * @param mESSAGE
     * @param sTATUS
     * @param dATA
     */
    public VendorDetails(int sTATUS, String mESSAGE, ArrayList<DATum> dATA) {
        super();
        this.sTATUS = sTATUS;
        this.mESSAGE = mESSAGE;
        this.dATA = dATA;
    }

    protected VendorDetails(Parcel in) {
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

    public static final Creator<VendorDetails> CREATOR = new Creator<VendorDetails>() {
        @Override
        public VendorDetails createFromParcel(Parcel in) {
            return new VendorDetails(in);
        }

        @Override
        public VendorDetails[] newArray(int size) {
            return new VendorDetails[size];
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


    public static class DATum implements Parcelable{

        @SerializedName("NAME")
        @Expose
        public String nAME;
        @SerializedName("EMAIL")
        @Expose
        public String eMAIL;
        @SerializedName("MOBILE")
        @Expose
        public String mOBILE;
        @SerializedName("ID")
        @Expose
        public String iD;
        @SerializedName("ENVIRONMENT_CREDITS")
        @Expose
        private String eNVIRONMENTCREDITS;
        @SerializedName("ADDRESS")
        @Expose
        private String aDDRESS;
        @SerializedName("LATITUDE")
        @Expose
        private String lATITUDE;
        @SerializedName("LONGITUDE")
        @Expose
        private String lONGITUDE;

        /**
         * No args constructor for use in serialization
         *
         */
        public DATum() {
        }

        /**
         *
         * @param nAME
         * @param mOBILE
         * @param eMAIL
         * @param iD
         */
        public DATum(String nAME, String eMAIL, String mOBILE, String iD, String eNVIRONMENTCREDITS, String aDDRESS, String lATITUDE, String lONGITUDE) {
            super();
            this.nAME = nAME;
            this.eMAIL = eMAIL;
            this.mOBILE = mOBILE;
            this.iD = iD;
            this.eNVIRONMENTCREDITS = eNVIRONMENTCREDITS;
            this.aDDRESS = aDDRESS;
            this.lATITUDE = lATITUDE;
            this.lONGITUDE = lONGITUDE;
        }

        protected DATum(Parcel in) {
            nAME = in.readString();
            eMAIL = in.readString();
            mOBILE = in.readString();
            iD = in.readString();
            eNVIRONMENTCREDITS = in.readString();
            aDDRESS = in.readString();
            lATITUDE = in.readString();
            lONGITUDE = in.readString();
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

        public String getNAME() {
            return nAME;
        }

        public void setNAME(String nAME) {
            this.nAME = nAME;
        }

        public String getEMAIL() {
            return eMAIL;
        }

        public void setEMAIL(String eMAIL) {
            this.eMAIL = eMAIL;
        }

        public String getMOBILE() {
            return mOBILE;
        }

        public void setMOBILE(String mOBILE) {
            this.mOBILE = mOBILE;
        }

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public String getENVIRONMENTCREDITS() {
            return eNVIRONMENTCREDITS;
        }

        public void setENVIRONMENTCREDITS(String eNVIRONMENTCREDITS) {
            this.eNVIRONMENTCREDITS = eNVIRONMENTCREDITS;
        }

        public String getADDRESS() {
            return aDDRESS;
        }

        public void setADDRESS(String aDDRESS) {
            this.aDDRESS = aDDRESS;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(nAME);
            parcel.writeString(eMAIL);
            parcel.writeString(mOBILE);
            parcel.writeString(iD);
            parcel.writeString(eNVIRONMENTCREDITS);
            parcel.writeString(aDDRESS);
            parcel.writeString(lATITUDE);
            parcel.writeString(lONGITUDE);
        }
    }

}