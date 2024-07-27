package idv.tia201.g1.image.dao.impl;

import idv.tia201.g1.entity.Image;
import idv.tia201.g1.image.dao.ImageDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ImageDaoImpl implements ImageDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return entityManager.find(Image.class, id);
    }

    @Override
    @Transactional
    public Image save(Image image) {
        entityManager.persist(image);
        return image;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Image image = entityManager.find(Image.class, id);
        if (image != null) {
            entityManager.remove(image);
        }
    }
}
