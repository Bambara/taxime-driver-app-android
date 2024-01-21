package com.ynr.taximedriver.model.driverCheckInformationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DriverCheckResponseModel {
    @Expose
    @SerializedName("content1")
    private List<DriverDispatch> driverDispatch;
    @Expose
    @SerializedName("content2")
    private Vehicle vehicle;
    @Expose
    @SerializedName("subCatData")
    private CategoryImage subCategoryImage;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<DriverDispatch> getDriverDispatch() {
        return driverDispatch;
    }

    public void setDriverDispatch(List<DriverDispatch> driverDispatch) {
        this.driverDispatch = driverDispatch;
    }

    public CategoryImage getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(CategoryImage subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
    }

    public class CategoryImage {
        @Expose
        @SerializedName("mapIconOntripSVG")
        private String mapIconOntripSVG;
        @Expose
        @SerializedName("mapIconOfflineSVG")
        private String mapIconOfflineSVG;
        @Expose
        @SerializedName("mapIconSVG")
        private String mapIconSVG;
        @Expose
        @SerializedName("subCategoryIconSelectedSVG")
        private String subCategoryIconSelectedSVG;
        @Expose
        @SerializedName("subCategoryIconSVG")
        private String subCategoryIconSVG;
        @Expose
        @SerializedName("mapIconOntrip")
        private String mapIconOntrip;
        @Expose
        @SerializedName("mapIconOffline")
        private String mapIconOffline;
        @Expose
        @SerializedName("mapIcon")
        private String mapIcon;
        @Expose
        @SerializedName("subCategoryIconSelected")
        private String subCategoryIconSelected;
        @Expose
        @SerializedName("subCategoryIcon")
        private String subCategoryIcon;

        public String getSubCategoryIcon() {
            return subCategoryIcon;
        }

        public void setSubCategoryIcon(String subCategoryIcon) {
            this.subCategoryIcon = subCategoryIcon;
        }

        public String getSubCategoryIconSelected() {
            return subCategoryIconSelected;
        }

        public void setSubCategoryIconSelected(String subCategoryIconSelected) {
            this.subCategoryIconSelected = subCategoryIconSelected;
        }

        public String getMapIcon() {
            return mapIcon;
        }

        public void setMapIcon(String mapIcon) {
            this.mapIcon = mapIcon;
        }

        public String getMapIconOffline() {
            return mapIconOffline;
        }

        public void setMapIconOffline(String mapIconOffline) {
            this.mapIconOffline = mapIconOffline;
        }

        public String getMapIconOntrip() {
            return mapIconOntrip;
        }

        public void setMapIconOntrip(String mapIconOntrip) {
            this.mapIconOntrip = mapIconOntrip;
        }

        public String getSubCategoryIconSVG() {
            return subCategoryIconSVG;
        }

        public void setSubCategoryIconSVG(String subCategoryIconSVG) {
            this.subCategoryIconSVG = subCategoryIconSVG;
        }

        public String getSubCategoryIconSelectedSVG() {
            return subCategoryIconSelectedSVG;
        }

        public void setSubCategoryIconSelectedSVG(String subCategoryIconSelectedSVG) {
            this.subCategoryIconSelectedSVG = subCategoryIconSelectedSVG;
        }

        public String getMapIconSVG() {
            return mapIconSVG;
        }

        public void setMapIconSVG(String mapIconSVG) {
            this.mapIconSVG = mapIconSVG;
        }

        public String getMapIconOfflineSVG() {
            return mapIconOfflineSVG;
        }

        public void setMapIconOfflineSVG(String mapIconOfflineSVG) {
            this.mapIconOfflineSVG = mapIconOfflineSVG;
        }

        public String getMapIconOntripSVG() {
            return mapIconOntripSVG;
        }

        public void setMapIconOntripSVG(String mapIconOntripSVG) {
            this.mapIconOntripSVG = mapIconOntripSVG;
        }
    }


    public class DriverDispatch {
        @Expose
        @SerializedName("isDispatchEnable")
        private boolean isDispatchEnable;
        @Expose
        @SerializedName("isApproved")
        private boolean isApproved;
        @Expose
        @SerializedName("isEnable")
        private boolean isEnable;
        @Expose
        @SerializedName("dispatcher")
        private List<Dispatcher> dispatcher;

        public boolean isDispatchEnable() {
            return isDispatchEnable;
        }

        public void setDispatchEnable(boolean dispatchEnable) {
            isDispatchEnable = dispatchEnable;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }


        public boolean isApproved() {
            return isApproved;
        }

        public void setApproved(boolean approved) {
            isApproved = approved;
        }

        public List<Dispatcher> getDispatcher() {
            return dispatcher;
        }

        public void setDispatcher(List<Dispatcher> dispatcher) {
            this.dispatcher = dispatcher;
        }

        public class Dispatcher {
            @Expose
            @SerializedName("_id")
            private String _id;
            @Expose
            @SerializedName("dispatcherId")
            private String dispatcherId;
            @Expose
            @SerializedName("type")
            private String type;
            @Expose
            @SerializedName("commission")
            private Commission commission;

            public Commission getCommission() {
                return commission;
            }

            public void setCommission(Commission commission) {
                this.commission = commission;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getDispatcherId() {
                return dispatcherId;
            }

            public void setDispatcherId(String dispatcherId) {
                this.dispatcherId = dispatcherId;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }


            public class Commission {
                @Expose
                @SerializedName("dispatchAdminCommission")
                private double dispatchAdminCommission;
                @Expose
                @SerializedName("dispatcherCommission")
                private double dispatcherCommission;

                public double getDispatcherCommission() {
                    return dispatcherCommission;
                }

                public void setDispatcherCommission(double dispatcherCommission) {
                    this.dispatcherCommission = dispatcherCommission;
                }

                public double getDispatchAdminCommission() {
                    return dispatchAdminCommission;
                }

                public void setDispatchAdminCommission(double dispatchAdminCommission) {
                    this.dispatchAdminCommission = dispatchAdminCommission;
                }
            }
        }
    }
    public class Vehicle {
        @Expose
        @SerializedName("isEnable")
        private boolean isEnable;

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }
    }
}
