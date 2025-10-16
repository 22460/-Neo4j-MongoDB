package com.xys.pojo;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import io.swagger.v3.oas.annotations.media.Schema;

@Node("person")
@Schema(description = "人物角色实体类，包含角色的基本信息")
public class Person {
    @Property
    @Schema(description = "角色所属阵营", example = "铁王座派")
    private String alliance;
    
    @Property
    @Schema(description = "角色姓名", required = true, example = "琼恩·雪诺")
    private String name;
    
    @Id
    @Property("ID") // 显式映射到Neo4j数据库中的ID字段
    @Schema(description = "角色唯一ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Property
    @Schema(description = "角色称号", example = "北境之王")
    private String title;
    
    @Property
    @Schema(description = "角色详细介绍", example = "琼恩·雪诺是临冬城公爵艾德·史塔克的私生子...")
    private String introduction;

    public String getAlliance() {
        return alliance;
    }

    public void setAlliance(String alliance) {
        this.alliance = alliance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Override
    public String toString() {
        return "Person{" +
                "alliance='" + alliance + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
