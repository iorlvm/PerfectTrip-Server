package idv.tia201.g1.config;

import io.lettuce.core.ReadFrom;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class AppConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("idv.tia201.g1.entity", "idv.tia201.g1.*.entity");
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return factoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(JpaTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    // Redis配置
    @Bean
    LettuceClientConfigurationBuilderCustomizer clientConfigurationBuilderCustomizer () {
        // 讀寫分離配置 (優先讀取從節點)
        return clientConfigurationBuilder -> clientConfigurationBuilder.readFrom(ReadFrom.REPLICA_PREFERRED);
    }

    @Bean
    public RedisTemplate<String, byte[]> redisTemplateForImage(RedisConnectionFactory factory) {
        // for 圖片緩存使用的序列化器
        RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // Key 和 HashKey 使用 String 序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());

        // Value 和 HashValue 使用 byteArray 序列化
        redisTemplate.setValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setHashValueSerializer(RedisSerializer.byteArray());

        return redisTemplate;
    }
}
