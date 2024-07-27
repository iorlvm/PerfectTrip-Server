package idv.tia201.g1.example.dao;

import idv.tia201.g1.entity.ExampleEntity;

import java.util.List;

public interface ExampleDao {
    ExampleEntity findById(Long id);

    List<ExampleEntity> findAll();

    ExampleEntity findByPhone(String phone);

    public boolean create(ExampleEntity exampleEntity);

    boolean deleteById(Long id);

    boolean update(ExampleEntity exampleEntity);
}
