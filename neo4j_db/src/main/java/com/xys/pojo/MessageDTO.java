package com.xys.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息传输对象，用于控制API返回的字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    
    @Schema(description = "发送用户名称")
    private String fromUserName;
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @Schema(description = "场景")
    private String scene;
}