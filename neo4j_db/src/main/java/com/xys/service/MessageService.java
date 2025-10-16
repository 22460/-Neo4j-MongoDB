package com.xys.service;

import com.xys.pojo.Message;
import com.xys.pojo.MessageDTO;
import com.xys.pojo.Person;
import com.xys.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息服务层
 */
@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private PersonService personService;
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * 根据角色名称查询其在MongoDB中的发言
     * @param personName 角色名称
     * @return 发言DTO列表
     */
    public List<MessageDTO> getMessagesByPersonName(String personName) {
        // 从Neo4j查询角色信息
        Optional<Person> personOptional = Optional.ofNullable(personService.findByName(personName));
        
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            // 根据角色ID从MongoDB查询发言
            List<Message> messages = messageRepository.findByFromUserIdOrderBySendTimeDesc(person.getId());
            // 转换为DTO，只返回需要的字段
            return messages.stream()
                    .map(message -> new MessageDTO(personName, message.getContent(), message.getSendTime(), message.getScene()))
                    .collect(Collectors.toList());
        }
        
        return Collections.emptyList(); // 返回空列表
    }
    
    /**
     * 根据场景查询所有发言
     * @param scene 场景名称
     * @return 该场景下的所有发言DTO列表
     */
    public List<MessageDTO> getMessagesByScene(String scene) {
        List<Message> messages = messageRepository.findByScene(scene);
        // 转换为DTO，需要从Neo4j查询用户名称
        return messages.stream()
                .map(message -> {
                    Person person = personService.findById(message.getFromUserId());
                    String userName = person != null ? person.getName() : "未知用户";
                    return new MessageDTO(userName, message.getContent(), message.getSendTime(), message.getScene());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有发言
     * @return 所有发言DTO列表
     */
    public List<MessageDTO> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        // 转换为DTO，需要从Neo4j查询用户名称
        return messages.stream()
                .map(message -> {
                    Person person = personService.findById(message.getFromUserId());
                    String userName = person != null ? person.getName() : "未知用户";
                    return new MessageDTO(userName, message.getContent(), message.getSendTime(), message.getScene());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有不重复的场景名称
     * @return 场景名称字符串列表
     */
    public List<String> getAllScenes() {
        // 获取所有不重复的场景名称
        List<String> scenes = messageRepository.findDistinctSceneBy();
        // 过滤掉null值
        return scenes.stream()
                .filter(scene -> scene != null && !scene.trim().isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 根据关键字模糊查询场景名称
     * @param keyword 搜索关键字
     * @return 场景名称字符串列表
     */
    public List<String> searchScenes(String keyword) {
        logger.info("开始场景搜索，关键字: '{}'", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.info("搜索关键字为空，返回空列表");
            return Collections.emptyList();
        }
        
        // 使用关键字模糊查询场景名称（支持部分文字匹配）
        List<String> scenes = messageRepository.findDistinctSceneBySceneContaining(keyword);
        logger.info("搜索结果数量: {}", scenes.size());
        
        // 过滤掉null值和空字符串并返回
        return scenes.stream()
                .filter(scene -> scene != null && !scene.trim().isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 查询两个角色之间的对话内容
     * @param name1 第一个角色名称
     * @param name2 第二个角色名称
     * @return 两个角色之间的对话DTO列表，按发送时间降序排序
     */
    public List<MessageDTO> getDialogueBetweenPersons(String name1, String name2) {
        // 从Neo4j查询两个角色的信息
        Person person1 = personService.findByName(name1);
        Person person2 = personService.findByName(name2);
        
        if (person1 == null || person2 == null) {
            return Collections.emptyList(); // 如果任一角色不存在，返回空列表
        }
        
        // 查询两个角色之间的双向对话，并按发送时间降序排列
        List<Message> messages = messageRepository.findByFromUserIdAndToUserIdOrFromUserIdAndToUserIdOrderBySendTimeDesc(
                person1.getId(), person2.getId(), person2.getId(), person1.getId());
        
        // 转换为DTO，只返回需要的字段
        return messages.stream()
                .map(message -> {
                    // 根据发送者ID确定用户名
                    String userName;
                    if (message.getFromUserId().equals(person1.getId())) {
                        userName = name1;
                    } else if (message.getFromUserId().equals(person2.getId())) {
                        userName = name2;
                    } else {
                        userName = "未知用户";
                    }
                    return new MessageDTO(userName, message.getContent(), message.getSendTime(), message.getScene());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 根据多个角色名称查询他们的发言
     * @param names 角色名称列表
     * @return 这些角色的所有发言DTO列表，按发送时间降序排列
     */
    public List<MessageDTO> getMessagesByPersonNames(List<String> names) {
        logger.info("开始批量查询角色发言，角色名称列表: {}", names);
        
        if (names == null || names.isEmpty()) {
            logger.info("角色名称列表为空，返回空列表");
            return Collections.emptyList();
        }
        
        // 直接从MongoDB获取所有消息，避免通过Neo4j查询角色ID（解决中文编码问题）
        List<Message> allMessages = messageRepository.findAll();
        logger.info("从MongoDB获取到的总消息数量: {}", allMessages.size());
        
        // 添加调试日志，打印前5条消息的发送者名称（用于诊断中文编码问题）
        logger.info("数据库中前5条消息的发送者名称：");
        for (int i = 0; i < Math.min(allMessages.size(), 5); i++) {
            Message msg = allMessages.get(i);
            logger.info("消息 {} - 发送者名称: '{}', 长度: {}, 内容预览: {}", 
                    i+1, 
                    msg.getFromNickname(), 
                    msg.getFromNickname() != null ? msg.getFromNickname().length() : 0,
                    msg.getContent() != null && msg.getContent().length() > 30 ? 
                            msg.getContent().substring(0, 30) + "..." : msg.getContent());
        }
        
        List<MessageDTO> filteredMessages = new ArrayList<>();
        
        // 直接使用原始名称进行精确匹配（不转换大小写，确保中文匹配准确）
        for (Message message : allMessages) {
            // 检查消息的发送者名称
            if (message.getFromNickname() != null) {
                logger.debug("处理消息 - 发送者名称: {}, 名称长度: {}", 
                        message.getFromNickname(), message.getFromNickname().length());
                
                // 使用精确匹配确保中文角色名称能正确匹配
                if (names.contains(message.getFromNickname())) {
                    logger.info("精确匹配到消息 - 发送者名称: {}, 请求的角色列表: {}", 
                            message.getFromNickname(), names);
                    filteredMessages.add(new MessageDTO(
                            message.getFromNickname(),
                            message.getContent(),
                            message.getSendTime(),
                            message.getScene()
                    ));
                }
            }
        }
        
        // 如果精确匹配没有结果，尝试模糊匹配
        if (filteredMessages.isEmpty()) {
            logger.info("精确匹配未找到结果，尝试模糊匹配");
            for (Message message : allMessages) {
                if (message.getFromNickname() != null) {
                    String messageUserName = message.getFromNickname();
                    // 检查是否包含任何请求的角色名称
                    if (names.stream().anyMatch(name -> messageUserName.contains(name) || 
                            name.contains(messageUserName))) {
                        logger.info("模糊匹配到消息 - 发送者名称: {}, 内容预览: {}", 
                                messageUserName, 
                                message.getContent().length() > 50 ? 
                                        message.getContent().substring(0, 50) + "..." : 
                                        message.getContent());
                        filteredMessages.add(new MessageDTO(
                                messageUserName,
                                message.getContent(),
                                message.getSendTime(),
                                message.getScene()
                        ));
                    }
                }
            }
        }
        
        // 按发送时间降序排序
        filteredMessages.sort((m1, m2) -> {
            if (m1.getSendTime() == null || m2.getSendTime() == null) {
                return 0;
            }
            return m2.getSendTime().compareTo(m1.getSendTime());
        });
        
        logger.info("最终过滤后的消息数量: {}", filteredMessages.size());
        return filteredMessages;
    }
}