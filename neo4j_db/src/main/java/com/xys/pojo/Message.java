package com.xys.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * MongoDB消息实体类，映射到messages集合
 */
@Data
@Document(collection = "messages")
public class Message {
    
    @Id
    @Schema(description = "消息ID")
    private String id;
    
    @Field("fromUserId")
    @Schema(description = "发送用户ID")
    private Long fromUserId;
    
    @Field("fromNickname")
    @Schema(description = "发送用户名称（昵称）")
    private String fromNickname;
    
    @Field("toUserId")
    @Schema(description = "接收用户ID")
    private Long toUserId;
    
    @Field("content")
    @Schema(description = "消息内容")
    private String content;
    
    @Field("sendTime")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @Field("readStatus")
    @Schema(description = "阅读状态")
    private Boolean readStatus;
    
    @Field("readAt")
    @Schema(description = "阅读时间")
    private LocalDateTime readAt;
    
    @Field("scene")
    @Schema(description = "场景")
    private String scene;
    
    @Field("isGroup")
    @Schema(description = "是否为群消息")
    private Boolean isGroup;
}