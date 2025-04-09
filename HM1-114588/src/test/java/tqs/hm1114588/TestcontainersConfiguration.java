package tqs.hm1114588;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import tqs.hm1114588.repository.DishRepository;
import tqs.hm1114588.repository.MenuRepository;
import tqs.hm1114588.repository.ReservationRepository;
import tqs.hm1114588.repository.RestaurantRepository;
import tqs.hm1114588.repository.UserRepository;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @MockitoBean 
    private DishRepository dishRepository;
    
    @MockitoBean 
    private RestaurantRepository restaurantRepository;
    
    @MockitoBean 
    private MenuRepository menuRepository;
    
    @MockitoBean 
    private ReservationRepository reservationRepository;
    
    @MockitoBean 
    private UserRepository userRepository;
    
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> testRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        
        // Mock value operations
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        
        // Mock hash operations
        HashOperations<String, Object, Object> hashOps = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        
        // Mock list operations
        ListOperations<String, Object> listOps = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOps);
        
        // Mock set operations
        SetOperations<String, Object> setOps = mock(SetOperations.class);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        
        return redisTemplate;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("tqs.hm1114588.model");
        factory.setDataSource(dataSource());
        return factory;
    }

}
