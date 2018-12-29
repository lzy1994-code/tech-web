package com.wd.tech.web.action;

import com.wd.tech.rpc.api.ChatRpcService;
import com.wd.tech.rpc.vo.*;
import com.wd.tech.web.util.BwJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: tech-web
 * @description:
 * @author: Lzy
 * @create: 2018-09-11 16:37
 **/
@RestController
@RequestMapping("/chat/verify")
public class ChatAction {

    private Logger logger = LoggerFactory.getLogger(ChatAction.class);

    @Resource
    private ChatRpcService chatRpcService;

    /**
     *  添加好友
     * @param userId
     * @param friendUid
     * @param remark
     * @return
     */
    @RequestMapping(value = "/v1/addFriend" ,method = RequestMethod.POST,produces = "application/json")
    public String addFriend(@RequestHeader int userId,@RequestParam int friendUid,@RequestParam(value = "remark",required = false) String remark){
        try {
            int i = chatRpcService.addFriend(userId, friendUid, remark);
            if (i==3)
            {
                return BwJsonHelper.returnJSON("1001","该用户不存在，无法添加好友");
            }
            else if (i==2)
            {
                return BwJsonHelper.returnJSON("1001","已是好友");
            }
            else if (i==0)
            {
                return BwJsonHelper.returnJSON("1001","添加好友失败");
            }
            else if (i==4)
            {
                return BwJsonHelper.returnJSON("1001","不能添加自己为好友");
            }
            else
            {
                return BwJsonHelper.returnJSON("0000","已发送加好友请求");
            }
        } catch (Exception e) {
            logger.error("addFriend={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  删除好友
     * @param userId
     * @param friendUid
     * @return
     */
    @RequestMapping(value = "/v1/deleteFriendRelation" ,method = RequestMethod.DELETE,produces = "application/json")
    public String deleteFriendRelation(@RequestHeader int userId, @RequestParam int friendUid){
        try {
            int i = chatRpcService.deleteFriendRelation(userId, friendUid);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","已解除好友关系");
            }
            return BwJsonHelper.returnJSON("1001","删除好友失败");
        } catch (Exception e) {
            logger.error("deleteFriendRelation={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  修改好友备注
     * @param userId
     * @param friendUid
     * @param remarkName
     * @return
     */
    @RequestMapping(value = "/v1/modifyFriendRemark" ,method = RequestMethod.PUT,produces = "application/json")
    public String modifyFriendRemark(@RequestHeader int userId,@RequestParam int friendUid,@RequestParam(value = "remarkName",required = false) String remarkName){
        try {
            int i = chatRpcService.modifyFriendRemark(userId, friendUid, remarkName);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","修改成功");
            }
            return BwJsonHelper.returnJSON("1001","修改失败");
        } catch (Exception e) {
            logger.error("modifyFriendRemark={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  检测是否为我的好友
     * @param userId
     * @param friendUid
     * @return
     */
    @RequestMapping(value = "/v1/checkMyFriend" ,method = RequestMethod.GET,produces = "application/json")
    public String checkMyFriend(@RequestHeader int userId,@RequestParam int friendUid){
        try {
            int i = chatRpcService.checkMyFriend(userId, friendUid);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","已是好友");
            }
            return BwJsonHelper.returnJSON("0000","对方不是你的好友");
        } catch (Exception e) {
            logger.error("modifyFriendRemark={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  创建自定义好友分组
     * @param userId
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/v1/addFriendGroup" ,method = RequestMethod.POST,produces = "application/json")
    public String addFriendGroup(@RequestHeader int userId,@RequestParam String groupName){
        try {
            int  i = chatRpcService.addFriendGroup(userId, groupName);
            if (i==2)
            {
                return BwJsonHelper.returnJSON("0000","分组已存在");
            }
            else if(i>0)
            {
                return BwJsonHelper.returnJSON("0000","创建分组成功","groupId",i);
            }
            else
            {
                return BwJsonHelper.returnJSON("1001","创建分组失败");
            }
        } catch (Exception e) {
            logger.error("addFriendGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询用户所有分组
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/findFriendGroupList" ,method = RequestMethod.GET,produces = "application/json")
    public String findFriendGroupList(@RequestHeader int userId){
        try {
            List<FriendGroupListVo> friendGroupList = chatRpcService.findFriendGroupList(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",friendGroupList);
        } catch (Exception e) {
            logger.error("findFriendGroupList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 修改好友分组名称
     * @param groupId
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/v1/modifyGroupName" ,method = RequestMethod.PUT,produces = "application/json")
    public String modifyGroupName(@RequestHeader int userId,@RequestParam int groupId,@RequestParam String groupName){
        try {
            int i = chatRpcService.modifyGroupName(groupId, groupName);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","修改成功");
            }
            return BwJsonHelper.returnJSON("1001","修改失败");
        } catch (Exception e) {
            logger.error("modifyGroupName={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  转移好友到其他分组
     * @param userId
     * @param friendUid
     * @param newGroupId
     * @return
     */
    @RequestMapping(value = "/v1/transferFriendGroup" ,method = RequestMethod.PUT,produces = "application/json")
    public String transferFriendGroup(@RequestHeader int userId,@RequestParam int friendUid,@RequestParam int newGroupId){
        try {
            int i = chatRpcService.transferFriendGroup(userId, friendUid, newGroupId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","转移成功");
            }
            return BwJsonHelper.returnJSON("1001","转移失败");
        } catch (Exception e) {
            logger.error("transferFriendGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  删除用户好友分组
     * @param userId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/deleteFriendGroup" ,method = RequestMethod.DELETE,produces = "application/json")
    public String deleteFriendGroup(@RequestHeader int userId,@RequestParam int groupId){
        try {
            int i = chatRpcService.deleteFriendGroup(userId, groupId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","删除分组成功");
            }
            else if(i==2)
            {
                return BwJsonHelper.returnJSON("1001","系统默认分组不能删除");
            }
            return BwJsonHelper.returnJSON("1001","删除分组失败");
        } catch (Exception e) {
            logger.error("deleteFriendGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询分组下的好友列表信息
     * @param userId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/findFriendListByGroupId" ,method = RequestMethod.GET,produces = "application/json")
    public String findFriendListByGroupId(@RequestHeader int userId,@RequestParam int groupId){
        try {
            List<FriendListVo> friendListByGroupId = chatRpcService.findFriendListByGroupId(userId, groupId);
            return BwJsonHelper.returnJSON("0000","查询成功",friendListByGroupId);
        } catch (Exception e) {
            logger.error("findFriendListByGroupId={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     * 查询用户的好友通知记录
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findFriendNoticePageList" ,method = RequestMethod.GET,produces = "application/json")
    public String findFriendNoticePageList(@RequestHeader int userId,@RequestParam int page,@RequestParam int count){
        try {
            List<FriendNoticeListVo> friendNoticePageList = chatRpcService.findFriendNoticePageList(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",friendNoticePageList);
        } catch (Exception e) {
            logger.error("findFriendNoticePageList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 审核好友申请
     * @param noticeId
     * @param flag
     * @return
     */
    @RequestMapping(value = "/v1/reviewFriendApply" ,method = RequestMethod.PUT,produces = "application/json")
    public String  reviewFriendApply(@RequestHeader int userId,@RequestParam int noticeId,@RequestParam int flag){
        try {
            int i = chatRpcService.reviewFriendApply(noticeId, flag);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","审核成功");
            }else if(i==2)
            {
                return BwJsonHelper.returnJSON("1001","审核失败,参数异常");
            }
            else if(i==3)
            {
                return BwJsonHelper.returnJSON("1001","审核失败,没有查询到相关申请");
            }
            else if(i==4)
            {
                return BwJsonHelper.returnJSON("1001","审核失败,该申请已处理");
            }
            else
            {
                return BwJsonHelper.returnJSON("1001","审核失败,请联系管理员");
            }
        } catch (Exception e) {
            logger.error("reviewFriendApply={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  发送消息
     * @param userId
     * @param receiveUid
     * @param content
     * @param chatImage
     * @return
     */
    @RequestMapping(value = "/v1/sendMessage" ,method = RequestMethod.POST,produces = "application/json")
    public String sendMessage(@RequestHeader int userId,@RequestParam int receiveUid,@RequestParam(value = "content",required = false) String content,@RequestParam(value = "chatImage",required = false) String chatImage){
        try {
            int  i = chatRpcService.sendMessage(userId, receiveUid, content, chatImage);
            if (i>0){
                return BwJsonHelper.returnJSON("0000","发送成功");
            }
            return BwJsonHelper.returnJSON("1001","发送失败");
        } catch (Exception e) {
            logger.error("sendMessage={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  查询好友聊天记录
     * @param userId
     * @param friendUid
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findChatRecordPageList" ,method = RequestMethod.GET,produces = "application/json")
    public String findChatRecordPageList(@RequestHeader int userId,@RequestParam int friendUid,@RequestParam int page,@RequestParam int count){
        try {
            List<ChatRecordListVo> chatRecordPageList = chatRpcService.findChatRecordPageList(userId, friendUid, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",chatRecordPageList);
        } catch (Exception e) {
            logger.error("findChatRecordPageList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }

    /**
     *  查询好友对话记录
     * @param userId
     * @param friendUid
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findDialogueRecordPageList" ,method = RequestMethod.GET,produces = "application/json")
    public  String findDialogueRecordPageList(@RequestHeader int userId,@RequestParam int friendUid,@RequestParam int page,@RequestParam int count){
        try {
            List<DialogueRecordListVo> dialogueRecordPageList = chatRpcService.findDialogueRecordPageList(userId, friendUid, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",dialogueRecordPageList);
        } catch (Exception e) {
            logger.error("findDialogueRecordPageList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  删除好友聊天记录
     * @param userId
     * @param friendUid
     * @return
     */
    @RequestMapping(value = "/v1/deleteChatRecord" ,method = RequestMethod.DELETE,produces = "application/json")
    public String deleteChatRecord(@RequestHeader int userId,@RequestParam int friendUid){
        try {
            int i = chatRpcService.deleteChatRecord(userId, friendUid);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","删除成功");
            }
            return BwJsonHelper.returnJSON("1001","删除失败");
        } catch (Exception e) {
            logger.error("deleteChatRecord={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }

    /**
     * 根据关键词搜索好友
     * @param userId
     * @param searchName
     * @return
     */
    @RequestMapping(value = "/v1/searchFriend" ,method = RequestMethod.GET,produces = "application/json")
    public  String searchFriend(@RequestHeader int userId,@RequestParam String searchName){
        try {
            List<FriendSearchVo> friendSearchList = chatRpcService.searchFriend(userId, searchName);
            return BwJsonHelper.returnJSON("0000","查询成功",friendSearchList);
        } catch (Exception e) {
            logger.error("searchFriend={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

}
