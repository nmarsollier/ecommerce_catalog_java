package com.catalog.article;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    @Query("{" +
            "  $and: [" +
            "     { 'enabled': true, " +
            "        $or : [" +
            "               {'name' : {'$regex' : /.*?0.*/, $options: '1' }}, " +
            "               {'description' : {'$regex' : /.*?0.*/, $options: '1' }}" +
            "              ] " +
            "     }" +
            "  ]" +
            "}")
    List<Article> findWithCriteria(String descriptionRegEx);
}