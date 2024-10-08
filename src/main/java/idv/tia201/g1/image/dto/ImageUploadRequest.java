package idv.tia201.g1.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequest {
    private MultipartFile file;
    private String comment;
    private Boolean cacheEnabled;
    private Boolean resizeEnabled;
    private Integer width;
    private Integer height;


    public ImageUploadRequest(byte[] bytes, String originalFilename) {

    }
}
