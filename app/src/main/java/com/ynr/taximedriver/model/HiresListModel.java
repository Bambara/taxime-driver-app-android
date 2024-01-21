package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HiresListModel {
    @Expose
    @SerializedName("driverWallet")
    private List<DriverWallet> driverWallet;
    @Expose
    @SerializedName("totalTripsDone")
    private int totalTripsDone;
    @Expose
    @SerializedName("totalEarnings")
    private double totalEarnings;


    public List<DriverWallet> getDriverWallet() {
        return driverWallet;
    }

    public void setDriverWallet(List<DriverWallet> driverWallet) {
        this.driverWallet = driverWallet;
    }

    public int getTotalTripsDone() {
        return totalTripsDone;
    }

    public void setTotalTripsDone(int totalTripsDone) {
        this.totalTripsDone = totalTripsDone;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public class DriverWallet {
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("driverId")
        private String driverId;
        @Expose
        @SerializedName("promocode")
        private List<String> promoCode;
        @Expose
        @SerializedName("referral")
        private List<String> referral;
        @Expose
        @SerializedName("transactionHistory")
        private TransactionHistory transactionHistory;
        @Expose
        @SerializedName("adminEarnings")
        private double adminEarnings;
        @Expose
        @SerializedName("driverEarnings")
        private double driverEarnings;
        @Expose
        @SerializedName("totalWalletPoints")
        private double totalWalletPoints;
        @Expose
        @SerializedName("bonusAmount")
        private double bonusAmount;
        @Expose
        @SerializedName("__v")
        private int v;


        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public TransactionHistory getTransactionHistory() {
            return transactionHistory;
        }

        public void setTransactionHistory(TransactionHistory transactionHistory) {
            this.transactionHistory = transactionHistory;
        }

        public List<String> getPromoCode() {
            return promoCode;
        }

        public void setPromoCode(List<String> promoCode) {
            this.promoCode = promoCode;
        }

        public List<String> getReferral() {
            return referral;
        }

        public void setReferral(List<String> referral) {
            this.referral = referral;
        }

        public double getAdminEarnings() {
            return adminEarnings;
        }

        public void setAdminEarnings(double adminEarnings) {
            this.adminEarnings = adminEarnings;
        }

        public double getDriverEarnings() {
            return driverEarnings;
        }

        public void setDriverEarnings(double driverEarnings) {
            this.driverEarnings = driverEarnings;
        }

        public double getTotalWalletPoints() {
            return totalWalletPoints;
        }

        public void setTotalWalletPoints(double totalWalletPoints) {
            this.totalWalletPoints = totalWalletPoints;
        }

        public double getBonusAmount() {
            return bonusAmount;
        }

        public void setBonusAmount(double bonusAmount) {
            this.bonusAmount = bonusAmount;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public class TransactionHistory {
            @Expose
            @SerializedName("trip")
            private Trip trip;
            @Expose
            @SerializedName("_id")
            private String _id;
            @Expose
            @SerializedName("method")
            private String paymentMethod;
            @Expose
            @SerializedName("isCredited")
            private boolean isCredited;
            @Expose
            @SerializedName("isATrip")
            private boolean isATrip;
            @Expose
            @SerializedName("transactionType")
            private String transactionType;
            @Expose
            @SerializedName("transactionAmount")
            private double transactionAmount;
            @Expose
            @SerializedName("dateTime")
            private String dateTime;

            public Trip getTrip() {
                return trip;
            }

            public void setTrip(Trip trip) {
                this.trip = trip;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public String getPaymentMethod() {
                return paymentMethod;
            }

            public void setPaymentMethod(String paymentMethod) {
                this.paymentMethod = paymentMethod;
            }

            public boolean isCredited() {
                return isCredited;
            }

            public void setCredited(boolean credited) {
                isCredited = credited;
            }

            public boolean isATrip() {
                return isATrip;
            }

            public void setATrip(boolean ATrip) {
                isATrip = ATrip;
            }

            public String getTransactionType() {
                return transactionType;
            }

            public void setTransactionType(String transactionType) {
                this.transactionType = transactionType;
            }

            public double getTransactionAmount() {
                return transactionAmount;
            }

            public void setTransactionAmount(double transactionAmount) {
                this.transactionAmount = transactionAmount;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public class Trip {
                @Expose
                @SerializedName("pickupLocation")
                private Location pickupLocation;
                @Expose
                @SerializedName("destinations")
                private List<Locations> destinations;
                @Expose
                @SerializedName("totalTripValue")
                private double totalTripValue;
                @Expose
                @SerializedName("tripAdminCommission")
                private double tripAdminCommission;
                @Expose
                @SerializedName("tripEarning")
                private double tripEarning;
                @Expose
                @SerializedName("tripId")
                private String tripId;


                public Location getPickupLocation() {
                    return pickupLocation;
                }

                public void setPickupLocation(Location pickupLocation) {
                    this.pickupLocation = pickupLocation;
                }

                public List<Locations> getDestinations() {
                    return destinations;
                }

                public void setDestinations(List<Locations> destinations) {
                    this.destinations = destinations;
                }

                public double getTotalTripValue() {
                    return totalTripValue;
                }

                public void setTotalTripValue(double totalTripValue) {
                    this.totalTripValue = totalTripValue;
                }

                public double getTripAdminCommission() {
                    return tripAdminCommission;
                }

                public void setTripAdminCommission(double tripAdminCommission) {
                    this.tripAdminCommission = tripAdminCommission;
                }

                public double getTripEarning() {
                    return tripEarning;
                }

                public void setTripEarning(double tripEarning) {
                    this.tripEarning = tripEarning;
                }

                public String getTripId() {
                    return tripId;
                }

                public void setTripId(String tripId) {
                    this.tripId = tripId;
                }
            }
        }
    }
}
