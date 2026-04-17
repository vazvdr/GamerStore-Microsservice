package com.gamerstore.product_service.repositories.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.gamerstore.product_service.entity.elastic.ProductDocument;

@Repository
public interface ProductElasticRepository extends ElasticsearchRepository<ProductDocument, Long> {

    List<ProductDocument> findByNameContainingOrDescriptionContainingOrBrandContainingOrModelContainingOrTagsContaining(
        String name,
        String description,
        String brand,
        String model,
        String tags
    );
}