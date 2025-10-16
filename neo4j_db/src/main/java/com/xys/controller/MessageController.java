package com.xys.controller;

import com.xys.pojo.MessageDTO;
import com.xys.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息管理", description = "MongoDB消息相关接口")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 根据角色名称查询发言
     * 实现：先从Neo4j查询角色ID，再从MongoDB查询对应发言
     * @param name 角色名称
     * @return 该角色的发言列表（只包含需要的信息）
     */
    @Operation(summary = "根据角色名称查询发言", description = "先从Neo4j查询角色信息，再从MongoDB查询对应发言，只返回关键信息")
    @GetMapping("/person/{name}")
    public List<MessageDTO> getMessagesByPersonName(
            @Parameter(description = "角色名称", required = true) @PathVariable String name) {
        return messageService.getMessagesByPersonName(name);
    }

    /**
     * 根据场景查询发言
     * @param scene 场景名称
     * @return 该场景下的所有发言（只包含需要的信息）
     */
    @Operation(summary = "根据场景查询发言", description = "查询指定场景下的所有发言，只返回关键信息")
    @GetMapping("/scene/{scene}")
    public List<MessageDTO> getMessagesByScene(
            @Parameter(description = "场景名称", required = true) @PathVariable String scene) {
        return messageService.getMessagesByScene(scene);
    }
    
    /**
     * 获取所有发言
     * @return 所有发言列表（只包含需要的信息）
     */
    @Operation(summary = "获取所有发言", description = "获取所有发言记录，只返回关键信息")
    @GetMapping("/all")
    public List<MessageDTO> getAllMessages() {
        return messageService.getAllMessages();
    }
    
    /**
     * 获取所有不重复的场景名称
     * @return 场景名称字符串列表
     */
    @Operation(summary = "获取所有场景", description = "获取所有不重复的场景名称字符串列表")
    @GetMapping("/scenes")
    public List<String> getAllScenes() {
        return messageService.getAllScenes();
    }
    
    /**
     * 根据关键字模糊查询场景名称
     * @param keyword 搜索关键字
     * @return 场景名称字符串列表
     */
    @Operation(summary = "模糊查询场景", description = "根据输入的关键字模糊查询匹配的场景名称字符串列表")
    @GetMapping("/scenes/search")
    public List<String> searchScenes(
            @Parameter(description = "搜索关键字", required = true) @RequestParam String keyword) {
        return messageService.searchScenes(keyword);
    }
    
    /**
     * 查询两个角色之间的对话内容
     * @param name1 第一个角色名称
     * @param name2 第二个角色名称
     * @return 两个角色之间的对话列表，按发送时间降序排列（只包含需要的信息）
     */
    @Operation(summary = "查询两个角色之间的对话", description = "查询指定的两个角色之间的所有对话内容，按时间降序排列，最新消息优先显示，只返回关键信息")
    @GetMapping("/dialogue/{name1}/{name2}")
    public List<MessageDTO> getDialogueBetweenPersons(
            @Parameter(description = "第一个角色名称", required = true) @PathVariable String name1,
            @Parameter(description = "第二个角色名称", required = true) @PathVariable String name2) {
        return messageService.getDialogueBetweenPersons(name1, name2);
    }
    

}