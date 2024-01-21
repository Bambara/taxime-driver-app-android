package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletDataModel {
    @Expose
    @SerializedName("content")
    private Content content;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public class Content {
        @Expose
        @SerializedName("_id")
        private String _id;
        @Expose
        @SerializedName("driverId")
        private String driverId;
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
        @SerializedName("transactions")
        private List<Transactions> transactions;

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

        public List<Transactions> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transactions> transactions) {
            this.transactions = transactions;
        }
    }

    public class Transactions {
        @Expose
        @SerializedName("methode")
        private String methode;
        @Expose
        @SerializedName("transactionAmount")
        private double transactionAmount;

        public String getMethode() {
            return methode;
        }

        public void setMethode(String methode) {
            this.methode = methode;
        }

        public double getTransAmount() {
            return transactionAmount;
        }

        public void setTransAmount(double transAmount) {
            this.transactionAmount = transAmount;
        }
    }
}
