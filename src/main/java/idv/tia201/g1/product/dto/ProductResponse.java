package idv.tia201.g1.product.dto;

import java.math.BigDecimal;

public class ProductResponse {

        private Integer productId;
        private String productName;
        private BigDecimal roomPrice;

        // Constructor
        public ProductResponse(Integer productId, String productName, BigDecimal roomPrice) {
            this.productId = productId;
            this.productName = productName;
            this.roomPrice = roomPrice;
        }

        // Getters and Setters
        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getRoomPrice() {
            return roomPrice;
        }

        public void setRoomPrice(int roomPrice) {
            this.roomPrice = BigDecimal.valueOf(roomPrice);
        }
    }


