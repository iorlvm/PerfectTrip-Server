package idv.tia201.g1.faq.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

//import @Data就不用打getter/setter~~
@Data
@Entity
@Table(name = "qa_table")
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qa_id")
    private Integer qaId;

    @Column(name = "qa_type", nullable = false, length = 255)
    private String qaType;

    @Column(name = "qa_title", nullable = false, length = 255)
    private String qaTitle;

    @Column(name = "qa_content", columnDefinition = "TEXT")
    private String qaContent;

    @Column(name = "create_date", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createDate;
}
