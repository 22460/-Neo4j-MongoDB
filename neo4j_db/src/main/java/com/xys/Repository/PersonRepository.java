package com.xys.Repository;

import com.xys.pojo.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    // 根据姓名查询
    Person findByName(String name);
    // 根据称号查询
    List<Person> findByTitle(String title);

}
