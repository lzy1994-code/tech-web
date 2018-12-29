package com.wd.tech.web.action;

import com.wd.tech.rpc.api.CommunityRpcService;
import com.wd.tech.rpc.api.UserRpcService;
import com.wd.tech.rpc.vo.*;
import com.wd.tech.web.util.BwJsonHelper;
import com.wd.tech.web.util.CustomConfig;
import com.wd.tech.web.util.UploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: tech-web
 * @description:
 * @author: Lzy
 * @create: 2018-09-11 16:38
 **/
@RestController
@RequestMapping("/community")
public class CommunityAction {

    private Logger logger = LoggerFactory.getLogger(CommunityAction.class);
    @Resource
    private CommunityRpcService communityRpcService;
    @Resource
    private CustomConfig customConfig;

    /**
     * 社区列表
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findCommunityList",method = RequestMethod.GET,produces = "application/json" )
    public String findCommunityList(@RequestHeader(value = "userId",required = false,defaultValue = "0") int userId, @RequestParam int page,@RequestParam int count){
        try {
            List<CommunityVo> communityList = communityRpcService.findCommunityList(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",communityList);
        } catch (Exception e) {
            logger.error("findCommunityList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 发布帖子
     * @param userId
     * @param content
     * @param files
     * @return
     */
    @RequestMapping(value = "/verify/v1/releasePost",method = RequestMethod.POST,produces = "application/json" )
    public String releasePost(@RequestHeader int userId,@RequestParam(value ="content" ,required = false) String content,@RequestParam(value = "file",required = false) MultipartFile[] files ){

        logger.info("releasePost：userId={},content={}",userId,content);

        if (content.equals("")||content==null){
            if (files.length==0){
                return BwJsonHelper.returnJSON("1001","发布失败","发布不能为空");
            }
        }
        String savePath = "";
        List<String> headPaths =new ArrayList<>();
        String visitPath = "";
        String headPath = "";
        if (files.length!=0){
            logger.info("releasePost：files.length={}",files.length);
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String fileName = file.getOriginalFilename();
                logger.info("releasePost：fileName={}",fileName);
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String newFileName = UploadUtil.getRandom()+df.format(new Date()) + "." + suffix;
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                String nowTime = sdf2.format(new Date());
                savePath = String.format(customConfig.getCommunityPath(),nowTime);
                headPath = String.format(customConfig.getCommunityVisit(),nowTime);
                savePath = savePath+newFileName;
                headPath = headPath+newFileName;
                headPaths.add(headPath);
                //上传图片
                try {
                    File f =  new File(savePath);
                    if (!f.exists()) {
                        f.getParentFile().mkdirs();
                    }
                    file.transferTo(f);
                } catch (IOException e) {
                    logger.error("releasePost",e);
                    return BwJsonHelper.returnJSON("1001","图片上传失败");
                }
            }
        }
        visitPath = String.join(",",headPaths);
        logger.info("releasePost：visitPath={}",visitPath);
        try {
           int  i = communityRpcService.releasePost(userId, content, visitPath);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","发布成功");
            }
            return BwJsonHelper.returnJSON("1001","发布失败");
        } catch (Exception e) {
            logger.error("releasePost={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 删除帖子
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/verify/v1/deletePost",method = RequestMethod.DELETE,produces = "application/json")
    public String deletePost(@RequestHeader int userId,@RequestParam String communityId){
        try {
            List<Integer> ids = Arrays.stream(communityId.split(",")).map(s->Integer.parseInt(s.trim())).collect(Collectors.toList());
            int i = communityRpcService.deletePost(userId,ids);
            if(i==2){
                return BwJsonHelper.returnJSON("1001","删除失败，只能删除自己的帖子");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000","删除成功");
            }
            return BwJsonHelper.returnJSON("1001","删除失败");
        } catch (Exception e) {
            logger.error("deletePost={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 点赞
     * @param userId
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/verify/v1/addCommunityGreat",method = RequestMethod.POST,produces = "application/json")
   public String addCommunityGreat(@RequestHeader int userId,@RequestParam int communityId){
       try {
           boolean b = communityRpcService.addCommunityGreat(userId, communityId);
           if (b){
               return BwJsonHelper.returnJSON("0000","点赞成功");
           }
           return BwJsonHelper.returnJSON("1001","点赞失败");
       } catch (Exception e) {
           logger.error("addCommunityGreat={}",e);
           return BwJsonHelper.returnJSON("1001","网络异常");
       }
   }

    /**
     * 取消点赞
     * @param userId
     * @param communityId
     * @return
     */
    @RequestMapping(value = "/verify/v1/cancelCommunityGreat",method = RequestMethod.DELETE,produces = "application/json")
    public String cancelCommunityGreat(@RequestHeader int userId,@RequestParam int communityId){
        try {
            boolean b = communityRpcService.cancelCommunityGreat(userId, communityId);
            if (b){
                return BwJsonHelper.returnJSON("0000","取消成功");
            }
            return BwJsonHelper.returnJSON("1001","取消失败");
        } catch (Exception e) {
            logger.error("cancelCommunityGreat={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     * 社区评论列表
     * @param communityId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findCommunityCommentList",method = RequestMethod.GET,produces = "application/json")
    public String findCommunityCommentList(@RequestHeader(value = "userId",required = false,defaultValue = "0") int userId,@RequestParam int communityId,@RequestParam int page,@RequestParam int count){
        try {
            List<String> communityCommentList = communityRpcService.findCommunityCommentList(communityId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",communityCommentList);
        } catch (Exception e) {
            logger.error("findCommunityCommentList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 社区评论
     * @param userId
     * @param communityId
     * @param content
     * @return
     */
    @RequestMapping(value = "/verify/v1/addCommunityComment",method = RequestMethod.POST,produces = "application/json")
    public String addCommunityComment(@RequestHeader int userId,@RequestParam int communityId,@RequestParam(value = "content") String content){
        try {
            if (content==null||content.equals("")){
                return  BwJsonHelper.returnJSON("1001","評論内容不能为空");
            }
            int i = communityRpcService.addCommunityComment(userId, communityId, content);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","評論成功");
            }
            return  BwJsonHelper.returnJSON("1001","評論失敗");
        } catch (Exception e) {
            logger.error("addCommunityComment={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     * 我的帖子
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findMyPostById",method = RequestMethod.GET,produces = "application/json")
    public String findMyPostById(@RequestHeader int userId, @RequestParam int page, @RequestParam int count){
        try {
                List<UserPostVo> myPostById = communityRpcService.findMyPostById(userId, page, count);
                return BwJsonHelper.returnJSON("0000","查詢成功",myPostById);
        } catch (Exception e) {
            logger.error("findMyPostById={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 查询用户帖子列表
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserPostById",method = RequestMethod.GET,produces = "application/json")
    public String findUserPostById(@RequestHeader int userId,@RequestParam int fromUid, @RequestParam int page, @RequestParam int count){
        try {
                List<CommunityUserListVo> userPostById = communityRpcService.findUserPostById(userId, fromUid, page, count);
                return BwJsonHelper.returnJSON("0000","查詢成功",userPostById);
        } catch (Exception e) {
            logger.error("findUserPostById={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     * 用户社区评论列表（区别于标签形式返回）
     * @param communityId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findCommunityUserCommentList",method = RequestMethod.GET,produces = "application/json")
    public String findCommunityUserCommentList(@RequestHeader(value = "userId",required = false,defaultValue = "0") int userId,@RequestParam int communityId,@RequestParam int page,@RequestParam int count){
        try {
            List<CommunityUserCommentVo> communityUserCommentList = communityRpcService.findCommunityUserCommentList(communityId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",communityUserCommentList);
        } catch (Exception e) {
            logger.error("findCommunityUserCommentList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
}
