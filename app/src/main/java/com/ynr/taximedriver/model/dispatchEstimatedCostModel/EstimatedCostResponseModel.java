package com.ynr.taximedriver.model.dispatchEstimatedCostModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EstimatedCostResponseModel {
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
        @SerializedName("estimatedCost")
        private double estimatedCost;

        public double getEstimatedCost() {
            return estimatedCost;
        }

        public void setEstimatedCost(double estimatedCost) {
            this.estimatedCost = estimatedCost;
        }
    }
}
