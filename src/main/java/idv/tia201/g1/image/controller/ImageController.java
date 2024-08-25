package idv.tia201.g1.image.controller;

import idv.tia201.g1.image.dto.ImageUploadRequest;
import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.image.entity.Image;
import idv.tia201.g1.image.service.ImageService;
import idv.tia201.g1.core.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static idv.tia201.g1.core.utils.Constants.ROLE_ADMIN;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long id) {
        Image image = imageService.findById(id);
        if (image != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(image.getMimetype()));
            return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public Result handleFileUpload(ImageUploadRequest imageUploadRequest) {
        try {
            // 將上傳的檔案處理成需要的格式
            Image image = imageService.upload(imageUploadRequest);

            String url = "image/" + imageService.save(image).getId();
            return Result.ok(url);
        }catch (IllegalArgumentException e) {
            // 接收imageService拋出的異常訊息
            return Result.fail("上傳失敗：" + e.getMessage());
        } catch (Exception e) {
            // TODO: 考慮是否要做log處理
            e.printStackTrace();
            return Result.fail("上傳失敗：系統錯誤");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        if (!ROLE_ADMIN.equals(UserHolder.getRole())) {
            new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        imageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
