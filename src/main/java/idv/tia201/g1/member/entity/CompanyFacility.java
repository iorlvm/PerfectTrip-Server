package idv.tia201.g1.member.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "company_facility")
public class CompanyFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_facility_id" )
    private int companyFacilityId;

    @Column(name = "company_id")
    private int companyId;

    @Column(name = "facility_id")
    private int facilityId;

}
