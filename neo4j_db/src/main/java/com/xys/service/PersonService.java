package com.xys.service;

import com.xys.Repository.PersonRepository;
import com.xys.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final Neo4jClient neo4jClient;
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, Neo4jClient neo4jClient) {
        this.personRepository = personRepository;
        this.neo4jClient = neo4jClient;
    }
    
    public Person findByName(String name) {
        // 使用Neo4jClient实现更精确的名称查询，避免使用可能有问题的repository方法
        String query = "MATCH (p:person) WHERE p.name = $name RETURN p";
        return neo4jClient.query(query)
                .bind(name).to("name")
                .fetchAs(Person.class)
                .mappedBy((typeSystem, record) -> {
                    org.neo4j.driver.types.Node node = record.get("p").asNode();
                    Person person = new Person();
                    
                    // 注意：数据库中字段名为"ID"而不是"id"
                    if (node.containsKey("ID")) {
                        person.setId(node.get("ID").asLong());
                    }
                    
                    if (node.containsKey("name")) {
                        person.setName(node.get("name").asString());
                    }
                    
                    if (node.containsKey("alliance")) {
                        person.setAlliance(node.get("alliance").asString());
                    }
                    
                    if (node.containsKey("title")) {
                        person.setTitle(node.get("title").asString());
                    }
                    
                    if (node.containsKey("introduction")) {
                        person.setIntroduction(node.get("introduction").asString());
                    }
                    
                    return person;
                })
                .one() // 只返回一个结果
                .orElse(null); // 如果没有找到，返回null
    }
    
    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色对象
     */
    public Person findById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    /**
     * 根据阵营名称模糊查询人物
     * @param alliance 阵营名称
     * @return 人物列表
     */
    public List<Person> findByAlliance(String alliance) {
        String query = "MATCH (p:person) WHERE p.alliance CONTAINS $alliance RETURN p";
        return neo4jClient.query(query)
                .bind(alliance).to("alliance")
                .fetchAs(Person.class)
                .mappedBy((typeSystem, record) -> {
                    org.neo4j.driver.types.Node node = record.get("p").asNode();
                    Person person = new Person();
                    
                    // 注意：数据库中字段名为"ID"而不是"id"
                    if (node.containsKey("ID")) {
                        person.setId(node.get("ID").asLong());
                    }
                    
                    if (node.containsKey("name")) {
                        person.setName(node.get("name").asString());
                    }
                    
                    if (node.containsKey("alliance")) {
                        person.setAlliance(node.get("alliance").asString());
                    }
                    
                    if (node.containsKey("title")) {
                        person.setTitle(node.get("title").asString());
                    }
                    
                    if (node.containsKey("introduction")) {
                        person.setIntroduction(node.get("introduction").asString());
                    }
                    
                    return person;
                })
                .all()
                .stream()
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Person> findByTitle(String title) {
        return personRepository.findByTitle(title);
    }


    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public List<Map<String, Object>> getRelationship(String name1, String name2) {
        // 修改查询语句，分别查询两个方向的有向关系，并获取关系的方向信息
        String query = "MATCH (p1:person {name: $name1})-[r]->(p2:person {name: $name2}) RETURN type(r) as relationshipType, p1.name as startNode, p2.name as endNode, 'forward' as direction";

        return neo4jClient.query(query)
                .bind(name1).to("name1")
                .bind(name2).to("name2")
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("relationshipType", record.get("relationshipType").asString());
                    result.put("startNode", record.get("startNode").asString());
                    result.put("endNode", record.get("endNode").asString());
                    result.put("direction", record.get("direction").asString());
                    return result;
                })
                .all()
                .stream()
                .map(map -> (Map<String, Object>) map)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取数据库中所有对应关系
     * @return 所有角色关系列表
     */
    public List<Map<String, Object>> getAllRelationships() {
        String cypher = "MATCH (p1:person)-[r]->(p2:person) RETURN type(r) as relationshipType, p1.name as person1, p2.name as person2";
        
        return neo4jClient.query(cypher)
                .fetch()
                .all()
                .stream()
                .map(record -> {
                    Map<String, Object> relationship = new HashMap<>();
                    relationship.put("relationshipType", record.get("relationshipType"));
                    relationship.put("person1", record.get("person1"));
                    relationship.put("person2", record.get("person2"));
                    return relationship;
                })
                .collect(Collectors.toList());
    }
}