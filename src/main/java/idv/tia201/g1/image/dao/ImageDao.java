package idv.tia201.g1.image.dao;

import idv.tia201.g1.image.entity.Image;

public interface ImageDao  {
    Image findById(Long id);

    Image save(Image image);

    void deleteById(Long id);

}
