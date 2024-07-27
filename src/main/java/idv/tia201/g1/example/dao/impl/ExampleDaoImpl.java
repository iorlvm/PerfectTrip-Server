package idv.tia201.g1.example.dao.impl;

import idv.tia201.g1.example.dao.ExampleDao;
import idv.tia201.g1.entity.ExampleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ExampleDaoImpl implements ExampleDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    @Transactional(readOnly = true)
    public ExampleEntity findById(Long id) {
        return entityManager.find(ExampleEntity.class, id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExampleEntity> findAll() {
        return entityManager.createQuery("from ExampleEntity", ExampleEntity.class).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExampleEntity findByPhone(String phone) {
        TypedQuery<ExampleEntity> query = entityManager.createQuery("from ExampleEntity where phone = :phone", ExampleEntity.class);
        query.setParameter("phone", phone);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean create(ExampleEntity user) {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        ExampleEntity entity = findById(id);
        if (entity == null) return false;
        entityManager.remove(entity);
        return true;
    }

    @Override
    @Transactional
    public boolean update(ExampleEntity exampleEntity) {
        try {
            entityManager.merge(exampleEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}