package com.xys.controller;

import com.xys.pojo.Person;
import com.xys.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/persons") // 统一接口前缀
public class PersonController {

    // 注入服务层对象（实际项目中建议通过服务层调用Repository，而非直接调用）
    @Autowired
    private PersonService personService;

    /**
     * 根据姓名查询单个Person
     */
    @Operation(
        summary = "根据姓名查询角色",
        description = "通过角色姓名获取详细信息",
        tags = {"角色查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功找到角色", 
            content = @Content(schema = @Schema(implementation = Person.class))),
        @ApiResponse(responseCode = "404", description = "未找到指定角色")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<Person> getPersonByName(
            @Parameter(description = "角色姓名", required = true) 
            @PathVariable String name) {
        Person person = personService.findByName(name);
        if (person != null) {
            return ResponseEntity.ok(person); // 成功返回200 + 数据
        } else {
            return ResponseEntity.notFound().build(); // 未找到返回404
        }
    }


    /**
     * 根据阵营查询多个Person
     */
    @Operation(
            summary = "根据阵营查询角色",
            description = "通过阵营获取所有属于该阵营的角色列表",
            tags = {"角色查询"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取角色列表",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/alliance/{alliance}")
    public ResponseEntity<List<Person>> getPersonsByAlliance(
            @Parameter(description = "阵营名称", required = true)
            @PathVariable String alliance) {
        List<Person> persons = personService.findByAlliance(alliance);
        return ResponseEntity.ok(persons); // 无论是否为空，均返回200 + 集合（空集合也是有效响应）
    }

    /**
     * 根据称号查询多个Person
     */
    @Operation(
        summary = "根据称号查询角色",
        description = "通过称号获取所有拥有该称号的角色列表",
        tags = {"角色查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取角色列表", 
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Person>> getPersonsByTitle(
            @Parameter(description = "角色称号", required = true) 
            @PathVariable String title) {
        List<Person> persons = personService.findByTitle(title);
        return ResponseEntity.ok(persons);
    }



    /**
     * 查询所有角色
     */
    @Operation(
        summary = "获取所有角色",
        description = "获取数据库中所有角色的列表",
        tags = {"角色查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取所有角色列表", 
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    /**
     * 根据name 查询某个角色的介绍
     */
    @Operation(
        summary = "获取角色介绍",
        description = "通过角色姓名获取其详细介绍",
        tags = {"角色查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取角色介绍", 
            content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "未找到指定角色")
    })
    @GetMapping("/introduction/{name}")
    public ResponseEntity<String> getIntroduction(
            @Parameter(description = "角色姓名", required = true) 
            @PathVariable String name) {
        Person person = personService.findByName(name);
        if(person == null){
            return ResponseEntity.badRequest().body("Person not found: " + name);
        }else{
            return ResponseEntity.ok(person.getIntroduction());
        }
    }

    /**
     * 根据 name 查询两个角色之间的关系
     */
    @Operation(
        summary = "查询角色关系",
        description = "查询并获取两个角色之间的所有关系",
        tags = {"关系查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取角色关系", 
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/relationship/{name1}/{name2}")
    public ResponseEntity<Object> getRelationship(
            @Parameter(description = "第一个角色姓名", required = true) 
            @PathVariable String name1, 
            @Parameter(description = "第二个角色姓名", required = true) 
            @PathVariable String name2) {
        // 调用getRelationship方法查询两个角色之间的所有关系
        List<Map<String, Object>> relationships = personService.getRelationship(name1, name2);
        
        if(relationships == null || relationships.isEmpty()){
            return ResponseEntity.ok("No relationship found between " + name1 + " and " + name2);
        }else{
            // 将关系数据转换为明确表示从谁到谁的字符串列表
            List<String> relationshipDescriptions = new ArrayList<>();
            for (Map<String, Object> relationship : relationships) {
                String startNode = (String) relationship.get("startNode");
                String endNode = (String) relationship.get("endNode");
                String relationshipType = (String) relationship.get("relationshipType");
                
                // 根据数据库中的关系方向直接构建关系描述
                String description = startNode + " -> " + endNode + ": " + relationshipType;
                relationshipDescriptions.add(description);
            }
            
            // 返回格式化后的关系描述列表
            return ResponseEntity.ok(relationshipDescriptions);
        }
    }

    /**
     * 获取数据库中所有的对应关系
     */
    @Operation(
        summary = "获取所有关系",
        description = "查询数据库中所有角色之间的关系",
        tags = {"关系查询"}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取所有关系", 
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/allRelationships")
    public ResponseEntity<List<Map<String, Object>>> getAllRelationships() {
        List<Map<String, Object>> relationships = personService.getAllRelationships();
        return ResponseEntity.ok(relationships);
    }


}
