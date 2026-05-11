package com.gamerstore.product_service.repositories;

import com.gamerstore.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
                SELECT p FROM Product p
                WHERE
                    LOWER(FUNCTION('unaccent', p.name)) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(FUNCTION('unaccent', p.description)) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(FUNCTION('unaccent', p.brand)) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(FUNCTION('unaccent', p.model)) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR LOWER(FUNCTION('unaccent', p.tags)) LIKE LOWER(CONCAT('%', :term, '%'))
                    OR CAST(p.price AS string) LIKE CONCAT('%', :term, '%')
            """)
    List<Product> searchGeneral(@Param("term") String term);
}
