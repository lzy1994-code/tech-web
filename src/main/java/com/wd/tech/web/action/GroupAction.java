package com.wd.tech.web.action;

import com.wd.tech.rpc.api.GroupRpcService;
import com.wd.tech.rpc.vo.*;
import com.wd.tech.web.util.BwJsonHelper;
import com.wd.tech.web.util.CustomConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @program: tech-web
 * @description:
 * @author: Lzy
 * @create: 2018-09-11 16:37
 **/
@RestController
@RequestMapping("/group/verify")
public class GroupAction {

    private Logger logger = LoggerFactory.getLogger(GroupAction.class);
    @Resource
    private GroupRpcService groupRpcService;

    @Resource
    private CustomConfig customConfig;

    /**
     *  创建群组
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/createGroup",method = RequestMethod.POST ,produces = "application/json")
    public String createGroup(@RequestHeader int userId,@RequestParam String name,@RequestParam(value = "description",required = false) String description){
        try {
            if(name==null||name.equals("")){
                return BwJsonHelper.returnJSON("1001","请填写群名");
            }
            int group = groupRpcService.createGroup(name, description, userId);
            if(group == 0)
            {
                return BwJsonHelper.returnJSON("1001","创建失败");
            }
            else if (group==2){
                return BwJsonHelper.returnJSON("0000","群名称已存在");
            }
            else
            {
                return BwJsonHelper.returnJSON("0000","创建成功","groupId",group);
            }
        } catch (Exception e) {
            logger.error("createGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }
    /**
     *  修改群组名
     * @param groupId
     * @param groupName
     * @return
     */
    @RequestMapping(value = "/v1/modifyGroupName",method = RequestMethod.PUT ,produces = "application/json")
    public String modifyGroupName(@RequestHeader int userId,@RequestParam int groupId,@RequestParam String groupName){
        try {
            if (groupName == null || groupName.equals("")){
                return BwJsonHelper.returnJSON("1001","请输入新群名");
            }
            int i = groupRpcService.modifyGroupName(groupId, groupName);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","修改群名称成功");
            }
            return BwJsonHelper.returnJSON("1001","修改群名称失败");
        } catch (Exception e) {
            logger.error("modifyGroupName={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  修改群简介
     * @param groupId
     * @param description
     * @return
     */
    @RequestMapping(value = "/v1/modifyGroupDescription",method = RequestMethod.PUT ,produces = "application/json")
    public String modifyGroupDescription(@RequestHeader int userId,@RequestParam int groupId,@RequestParam String description){
        try {
            int i = groupRpcService.modifyGroupDescription(groupId, description);
            if (1==i){
                return BwJsonHelper.returnJSON("0000","修改群备注成功");
            }
            return BwJsonHelper.returnJSON("1001","修改群备注失败");
        } catch (Exception e) {
            logger.error("modifyGroupDescription={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 解散群组
     * @param groupId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/disbandGroup",method = RequestMethod.DELETE ,produces = "application/json")
    public String disbandGroup(@RequestHeader int userId,@RequestParam  int groupId){
        try {
            int  i = groupRpcService.disbandGroup(groupId, userId);
            if (i==2){
                return BwJsonHelper.returnJSON("0000","您无解散群组的权限");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000","解散群组成功");
            }
            return BwJsonHelper.returnJSON("1001","解散群组失败");
        } catch (Exception e) {
            logger.error("disbandGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询我创建的群组
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/findGroupsByUserId",method = RequestMethod.GET ,produces = "application/json")
    public String findGroupsByUserId(@RequestHeader int userId){
        try {
            List<GroupListVo> groupsByUserId = groupRpcService.findGroupsByUserId(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",groupsByUserId);
        } catch (Exception e) {
            logger.error("findGroupsByUserId={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  查询我所有加入的群组
     * @param userId
     * @return
     */
    @RequestMapping(value = "/v1/findUserJoinedGroup",method = RequestMethod.GET ,produces = "application/json")
    public String findUserJoinedGroup(@RequestHeader int userId){
        try {
            List<GroupListVo> userJoinedGroup = groupRpcService.findUserJoinedGroup(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",userJoinedGroup);
        } catch (Exception e) {
            logger.error("findUserJoinedGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询群组内所有用户信息
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/findGroupMemberList",method = RequestMethod.GET ,produces = "application/json")
    public String findGroupMemberList(@RequestHeader int userId,@RequestParam int groupId){
        try {
            List<GroupMemberListVo> groupMemberList = groupRpcService.findGroupMemberList(groupId);
            return BwJsonHelper.returnJSON("0000","查询成功",groupMemberList);
        } catch (Exception e) {
            logger.error("findGroupMemberList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  查询群组详细信息
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/findGroupInfo",method = RequestMethod.GET ,produces = "application/json")
    public String findGroupInfo(@RequestHeader int userId,@RequestParam int groupId){
        try {
            GroupInfoVo groupInfo = groupRpcService.findGroupInfo(groupId);
            return BwJsonHelper.returnJSON("0000","查询成功",groupInfo);
        } catch (Exception e) {
            logger.error("findGroupInfo={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  发送群信息
     * @param groupId
     * @param userId
     * @param content
     * @param chatImage
     * @return
     */
    @RequestMapping(value = "/v1/sendGroupMessage",method = RequestMethod.POST,produces = "application/json")
    public String sendGroupMessage(@RequestHeader int userId,@RequestParam  int groupId,@RequestParam(value = "content",required = false)  String content,@RequestParam(value = "chatImage",required = false) String chatImage){
        try {
            int i = groupRpcService.sendGroupMessage(groupId, userId, content, chatImage);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","发送成功");
            }
            return BwJsonHelper.returnJSON("1001","发送失败");
        } catch (Exception e) {
            logger.error("sendGroupMessage={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询群聊天内容
     * @param groupId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findGroupChatRecordPage",method = RequestMethod.GET,produces = "application/json")
    public String findGroupChatRecordPage(@RequestHeader int userId,@RequestParam int groupId,@RequestParam int page,@RequestParam int count){
        try {
            List<GroupChatRecordListVo> groupChatRecordPage = groupRpcService.findGroupChatRecordPage(groupId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",groupChatRecordPage);
        } catch (Exception e) {
            logger.error("findGroupChatRecordPage={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  移出群成员(管理员与群主才有的权限)
     * @param groupId
     * @param groupUserId
     * @return
     */
    @RequestMapping(value = "/v1/removeGroupMember",method = RequestMethod.DELETE,produces = "application/json")
    public String removeGroupMember(@RequestHeader int userId,@RequestParam int groupId,@RequestParam int groupUserId){
        try {
            int i = groupRpcService.removeGroupMember(groupId,userId, groupUserId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","移除成功");
            }
            else if(i==2)
            {
                return BwJsonHelper.returnJSON("1001","您没权限移除群成员");
            }
            return BwJsonHelper.returnJSON("1001","移除失败");
        } catch (Exception e) {
            logger.error("removeGroupMember={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  调整群成员角色(群主才有的权限)
     * @param groupId
     * @param groupUserId
     * @param role
     * @return
     */
    @RequestMapping(value = "/v1/modifyPermission",method = RequestMethod.PUT,produces = "application/json")
    public String modifyPermission(@RequestHeader int userId,@RequestParam  int groupId,@RequestParam int groupUserId,@RequestParam int role){

        try {
            int i = groupRpcService.modifyPermission(groupId, userId, role,groupUserId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","设置成功");
            }
            else if(i==2)
            {
                return BwJsonHelper.returnJSON("1001","您没权限");
            }
            return BwJsonHelper.returnJSON("1001","设置失败");
        } catch (Exception e) {
            logger.error("modifyPermission={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }


    /**
     *  判断用户是否已在群内
     * @param userId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/whetherInGroup",method = RequestMethod.GET,produces = "application/json")
    public String whetherInGroup(@RequestHeader int userId,@RequestParam int groupId){
        try {
            int  i = groupRpcService.whetherInGroup(userId, groupId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","已是该群成员");
            }
            if (i==0){
                return BwJsonHelper.returnJSON("0000","不是该群成员");
            }
            return BwJsonHelper.returnJSON("1001","网络异常");
        } catch (Exception e) {
            logger.error("whetherInGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  申请进群
     * @param groupId
     * @param userId
     * @param remark
     * @return
     */
    @RequestMapping(value = "/v1/applyAddGroup",method = RequestMethod.POST,produces = "application/json")
    public String applyAddGroup(@RequestHeader int userId,@RequestParam int groupId ,@RequestParam String remark){
        try {
            int i = groupRpcService.applyAddGroup(groupId, userId, remark);
            if (i==2){
                return BwJsonHelper.returnJSON("0000","您已在该群中");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000","已申请，等待管理员审核");
            }
            return BwJsonHelper.returnJSON("1001","加群失败");
        } catch (Exception e) {
            logger.error("applyAddGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  邀请加群
     * @param groupId
     * @param userId（邀请人）
     * @param receiverUid（被邀请人）
     * @return
     */
    @RequestMapping(value = "/v1/inviteAddGroup",method = RequestMethod.POST,produces = "application/json")
    public String inviteAddGroup(@RequestHeader  int userId,@RequestParam int groupId,@RequestParam int receiverUid){
        try {
            logger.info("inviteAddGroup, userId={},groupId={},receiverUid={}",userId,groupId,receiverUid);
            int i = groupRpcService.inviteAddGroup(groupId, userId, receiverUid);
            if (i==2){
                return BwJsonHelper.returnJSON("0000","该用户已在群中");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000","邀请成功，等待管理员与对方确认");
            }
            return BwJsonHelper.returnJSON("1001","邀请失败");
        } catch (Exception e) {
            logger.error("inviteAddGroup={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     *  查询群通知记录
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findGroupNoticePageList",method = RequestMethod.GET,produces = "application/json")
    public String findGroupNoticePageList(@RequestHeader int userId,@RequestParam int page,@RequestParam int count){
        try {
            List<GroupNoticeListVo> groupNoticePageList = groupRpcService.findGroupNoticePageList(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",groupNoticePageList);
        } catch (Exception e) {
            logger.error("findGroupNoticePageList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 审核群申请
     * @param noticeId
     * @param flag
     * @return
     */
    @RequestMapping(value = "/v1/reviewGroupApply",method = RequestMethod.PUT,produces = "application/json")
    public String reviewGroupApply(@RequestHeader int userId,@RequestParam int noticeId,@RequestParam int flag){
        try {
            int i = groupRpcService.reviewGroupApply(noticeId, flag);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","已审核");
            }
            return BwJsonHelper.returnJSON("1001","审核失败");
        } catch (Exception e) {
            logger.error("reviewGroupApply={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     * 上传群头像
     * @param userId
     * @param groupId
     * @param file
     * @return
     */
    @RequestMapping(value = "/v1/uploadGroupHeadPic",method = RequestMethod.POST,produces = "application/json")
    public String uploadGroupHeadPic(@RequestHeader int userId,@RequestParam int groupId,@RequestParam(value = "image") MultipartFile file){
        String savePath = "";
        String visitPath = "";
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            String nowTime = sdf2.format(new Date());
            savePath = String.format(customConfig.getGroupPath(), nowTime);
            visitPath = String.format(customConfig.getGroupVisit(), nowTime);
            String newFileName = df.format(new Date()) + "." + suffix;
            savePath = savePath + newFileName;
            visitPath = visitPath + newFileName;
            File f = new File(savePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
            }
            try {
                file.transferTo(f);
                int i = groupRpcService.modifyImage(visitPath,groupId);
                if ( i == 1){
                    return BwJsonHelper.returnJSON("0000", "上传成功",visitPath);
                }
                return BwJsonHelper.returnJSON("1001","上传失敗");
            } catch (IOException e) {
                logger.error("modifyEmail：{}", e);
                return BwJsonHelper.returnJSON("1001", "网络异常,请联系管理员");
            }
        }else {
            return BwJsonHelper.returnJSON("1001","上传失敗,图片为空");
        }
    }

    /**
     *  退群
     * @param userId
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/v1/retreat",method = RequestMethod.DELETE,produces = "application/json")
    public String retreat(@RequestHeader int userId,@RequestParam int groupId){
        try {
            int  i = groupRpcService.retreat(userId, groupId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","退群成功");
            }
            else if(i == 2)
            {
                return BwJsonHelper.returnJSON("1001","用户不在该群内");
            }
            else if(i == 3)
            {
                return BwJsonHelper.returnJSON("1001","你是群主啊！你想干嘛！皮！");
            }
            else
            {
                return BwJsonHelper.returnJSON("1001","退群失败");
            }
        } catch (Exception e) {
            logger.error("retreat",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }



}
