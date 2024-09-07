package idv.tia201.g1.product.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "facility")
@Data
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Integer facilityId; // 設施ID

    @Column(name = "facility_name")
    private String facilityName; // 設施名稱

    @Column(name = "facility_icon")
    private String facilityIcon; // 設施icon
}