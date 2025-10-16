package com.xys.repository;

import com.xys.pojo.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB消息数据访问层
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    

    /**
     * 根据发送用户ID查询消息，并按发送时间降序排序
     * @param fromUserId 发送用户ID
     * @return 按时间降序排列的消息列表
     */
    List<Message> findByFromUserIdOrderBySendTimeDesc(Long fromUserId);
    
    /**
     * 根据场景查询消息
     * @param scene 场景名称
     * @return 该场景下的消息列表
     */
    @Query(value = "{scene: ?0}", fields = "{content: 1, fromUserId: 1, sendTime: 1, scene: 1, _id: 0}")
    List<Message> findByScene(String scene);
    

    /**
     * 查询两个用户之间的对话（双向）
     * @param userId1 第一个用户ID
     * @param userId2 第二个用户ID
     * @return 两个用户之间的对话列表，按发送时间降序排序
     */
    List<Message> findByFromUserIdAndToUserIdOrFromUserIdAndToUserIdOrderBySendTimeDesc(
            Long userId1, Long userId2, Long userId22, Long userId11);
    

    /**
     * 获取所有不重复的场景名称
     * @return 场景名称列表
     */
    @Query(value = "{}", fields = "{scene: 1, _id: 0}")
    List<String> findDistinctSceneBy();
    
    /**
     * 根据关键字模糊查询场景名称（支持部分文字匹配）
     * @param keyword 搜索关键字
     * @return 场景名称列表
     */
    @Query(value = "{scene: {$regex: ?0}}", fields = "{scene: 1, _id: 0}")
    List<String> findDistinctSceneBySceneContaining(String keyword);
}