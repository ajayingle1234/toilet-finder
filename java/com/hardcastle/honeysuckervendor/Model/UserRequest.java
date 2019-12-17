package com.hardcastle.honeysuckervendor.Model;

/**
 * Created by abhijeet on 10/16/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserRequest implements Parcelable
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
    private final static long serialVersionUID = 7857160450667410190L;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserRequest() {
    }

    /**
     *
     * @param mESSAGE
     * @param sTATUS
     * @param dATA
     */
    public UserRequest(int sTATUS, String mESSAGE, ArrayList<DATum> dATA) {
        super();
        this.sTATUS = sTATUS;
        this.mESSAGE = mESSAGE;
        this.dATA = dATA;
    }

    protected UserRequest(Parcel in) {
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

    public static final Creator<UserRequest> CREATOR = new Creator<UserRequest>() {
        @Override
        public UserRequest createFromParcel(Parcel in) {
            return new UserRequest(in);
        }

        @Override
        public UserRequest[] newArray(int size) {
            return new UserRequest[size];
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

        @SerializedName("INFO")
        @Expose
        private INFO iNFO;
        @SerializedName("SERVICE")
        @Expose
        private SERVICE sERVICE;
        private final static long serialVersionUID = 4226655270047955144L;

        /**
         * No args constructor for use in serialization
         *
         */
        public DATum() {
        }

        /**
         *
         * @param sERVICE
         * @param iNFO
         */
        public DATum(INFO iNFO, SERVICE sERVICE) {
            super();
            this.iNFO = iNFO;
            this.sERVICE = sERVICE;
        }

        protected DATum(Parcel in) {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
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

        public INFO getINFO() {
            return iNFO;
        }

        public void setINFO(INFO iNFO) {
            this.iNFO = iNFO;
        }

        public SERVICE getSERVICE() {
            return sERVICE;
        }

        public void setSERVICE(SERVICE sERVICE) {
            this.sERVICE = sERVICE;
        }


        public static class INFO implements Parcelable
        {

            @SerializedName("USER_ID")
            @Expose
            private String uSERID;
            @SerializedName("USER_NAME")
            @Expose
            private String uSERNAME;
            @SerializedName("MOBILE")
            @Expose
            private String mOBILE;
            @SerializedName("LATTITUDE")
            @Expose
            private String lATTITUDE;
            @SerializedName("LONGITUDE")
            @Expose
            private String lONGITUDE;
            @SerializedName("SERVICE_ADDRESS")
            @Expose
            private String sERVICEADDRESS;
            @SerializedName("SERVICE_DATE")
            @Expose
            private String sERVICEDATE;
            @SerializedName("SERVICE_REQUEST_ID")
            @Expose
            private String sERVICEREQUESTID;
            @SerializedName("SERVICE_TIME")
            @Expose
            private String sERVICETIME;
            @SerializedName("RATE")
            @Expose
            private String rATE;
            private final static long serialVersionUID = 8478849270170071519L;

            /**
             * No args constructor for use in serialization
             *
             */
            public INFO() {
            }

            /**
             *
             * @param rATE
             * @param uSERID
             * @param lATTITUDE
             * @param uSERNAME
             * @param lONGITUDE
             * @param mOBILE
             * @param sERVICEDATE
             * @param sERVICEADDRESS
             */
            public INFO(String uSERID, String uSERNAME, String mOBILE, String lATTITUDE, String lONGITUDE, String sERVICEREQUESTID, String sERVICEADDRESS, String sERVICEDATE, String sERVICETIME, String rATE) {
                super();
                this.uSERID = uSERID;
                this.uSERNAME = uSERNAME;
                this.mOBILE = mOBILE;
                this.lATTITUDE = lATTITUDE;
                this.lONGITUDE = lONGITUDE;
                this.sERVICEREQUESTID = sERVICEREQUESTID;
                this.sERVICEADDRESS = sERVICEADDRESS;
                this.sERVICEDATE = sERVICEDATE;
                this.sERVICETIME = sERVICETIME;
                this.rATE = rATE;
            }

            protected INFO(Parcel in) {
                uSERID = in.readString();
                uSERNAME = in.readString();
                mOBILE = in.readString();
                lATTITUDE = in.readString();
                lONGITUDE = in.readString();
                sERVICEREQUESTID = in.readString();
                sERVICEADDRESS = in.readString();
                sERVICEDATE = in.readString();
                sERVICETIME = in.readString();
                rATE = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(uSERID);
                dest.writeString(uSERNAME);
                dest.writeString(mOBILE);
                dest.writeString(lATTITUDE);
                dest.writeString(lONGITUDE);
                dest.writeString(sERVICEREQUESTID);
                dest.writeString(sERVICEADDRESS);
                dest.writeString(sERVICEDATE);
                dest.writeString(sERVICETIME);
                dest.writeString(rATE);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<INFO> CREATOR = new Creator<INFO>() {
                @Override
                public INFO createFromParcel(Parcel in) {
                    return new INFO(in);
                }

                @Override
                public INFO[] newArray(int size) {
                    return new INFO[size];
                }
            };

            public String getUSERID() {
                return uSERID;
            }

            public void setUSERID(String uSERID) {
                this.uSERID = uSERID;
            }

            public String getUSERNAME() {
                return uSERNAME;
            }

            public void setUSERNAME(String uSERNAME) {
                this.uSERNAME = uSERNAME;
            }

            public String getMOBILE() {
                return mOBILE;
            }

            public void setMOBILE(String mOBILE) {
                this.mOBILE = mOBILE;
            }

            public String getLATTITUDE() {
                return lATTITUDE;
            }

            public void setLATTITUDE(String lATTITUDE) {
                this.lATTITUDE = lATTITUDE;
            }

            public String getLONGITUDE() {
                return lONGITUDE;
            }

            public void setLONGITUDE(String lONGITUDE) {
                this.lONGITUDE = lONGITUDE;
            }

            public String getsERVICEADDRESS() {
                return sERVICEADDRESS;
            }

            public String getsERVICEREQUESTID() {
                return sERVICEREQUESTID;
            }

            public void setsERVICEREQUESTID(String sERVICEREQUESTID) {
                this.sERVICEREQUESTID = sERVICEREQUESTID;
            }

            public String getsERVICETIME() {
                return sERVICETIME;
            }

            public void setsERVICETIME(String sERVICETIME) {
                this.sERVICETIME = sERVICETIME;
            }

            public void setsERVICEADDRESS(String sERVICEADDRESS) {
                this.sERVICEADDRESS = sERVICEADDRESS;
            }

            public String getSERVICEDATE() {
                return sERVICEDATE;
            }

            public void setSERVICEDATE(String sERVICEDATE) {
                this.sERVICEDATE = sERVICEDATE;
            }

            public String getRATE() {
                return rATE;
            }

            public void setRATE(String rATE) {
                this.rATE = rATE;
            }

        }


        public static class SERVICE implements Parcelable
        {

            @SerializedName("SERVICE_NAME")
            @Expose
            private List<String> sERVICENAME = null;
            @SerializedName("PARM_NAME")
            @Expose
            private List<String> pARMNAME = null;
            @SerializedName("PARM_VALUE")
            @Expose
            private List<String> pARMVALUE = null;
            private final static long serialVersionUID = 223327686622855890L;

            /**
             * No args constructor for use in serialization
             *
             */
            public SERVICE() {
            }

            /**
             *
             * @param pARMNAME
             * @param pARMVALUE
             * @param sERVICENAME
             */
            public SERVICE(List<String> sERVICENAME, List<String> pARMNAME, List<String> pARMVALUE) {
                super();
                this.sERVICENAME = sERVICENAME;
                this.pARMNAME = pARMNAME;
                this.pARMVALUE = pARMVALUE;
            }

            protected SERVICE(Parcel in) {
                sERVICENAME = in.createStringArrayList();
                pARMNAME = in.createStringArrayList();
                pARMVALUE = in.createStringArrayList();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeStringList(sERVICENAME);
                dest.writeStringList(pARMNAME);
                dest.writeStringList(pARMVALUE);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<SERVICE> CREATOR = new Creator<SERVICE>() {
                @Override
                public SERVICE createFromParcel(Parcel in) {
                    return new SERVICE(in);
                }

                @Override
                public SERVICE[] newArray(int size) {
                    return new SERVICE[size];
                }
            };

            public List<String> getSERVICENAME() {
                return sERVICENAME;
            }

            public void setSERVICENAME(List<String> sERVICENAME) {
                this.sERVICENAME = sERVICENAME;
            }

            public List<String> getPARMNAME() {
                return pARMNAME;
            }

            public void setPARMNAME(List<String> pARMNAME) {
                this.pARMNAME = pARMNAME;
            }

            public List<String> getPARMVALUE() {
                return pARMVALUE;
            }

            public void setPARMVALUE(List<String> pARMVALUE) {
                this.pARMVALUE = pARMVALUE;
            }

        }

    }

}
