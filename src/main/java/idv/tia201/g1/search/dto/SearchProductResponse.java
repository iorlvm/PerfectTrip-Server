package idv.tia201.g1.search.dto;

import idv.tia201.g1.product.entity.Facility;
import idv.tia201.g1.product.entity.ProductPhotos;
import lombok.Data;

import java.util.List;

@Data
public class SearchProductResponse {
    private Integer productId;
    private String productName;
    private Integer maxOccupancy;
    private Integer remainingRooms;
    private Integer price;
    private Integer days;
    private List<ProductPhotos> photos;
    private List<Facility> facilities;
    private boolean includesBreakfast;
    private boolean allowDateChanges;
    private boolean isRefundable;
    private boolean allowFreeCancellation;
}
