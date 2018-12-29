package com.wd.tech.web.action;

import com.wd.tech.rpc.api.InformationRpcService;
import com.wd.tech.rpc.pojo.InfoAdvertising;
import com.wd.tech.rpc.pojo.InfoComment;
import com.wd.tech.rpc.pojo.InformationPlate;
import com.wd.tech.rpc.vo.*;
import com.wd.tech.web.util.BwJsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * @program: tech-web
 * @description: 资讯Action
 * @author: Lzy
 * @create: 2018-09-05 13:52
 **/
@RestController
@RequestMapping("/information")
public class InformationAction {
    private Logger logger = LoggerFactory.getLogger(InformationAction.class);
    @Resource
    private InformationRpcService informationRpcService;

    /**
     * banner展示列表
     */
    @RequestMapping(value = "/v1/bannerShow",method = RequestMethod.GET, produces = "application/json")
    public String bannerShow(){
        try {
            List<BannersVo>  banners = informationRpcService.bannerShow();
            return BwJsonHelper.returnJSON("0000","查询成功",banners);
        } catch (Exception e) {
            logger.error("bannerShow={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 资讯推荐展示列表(包含单独板块列表展示)
     * @param userId
     * @param plateId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/infoRecommendList",method = RequestMethod.GET, produces = "application/json")
    public String infoRecommendList(@RequestHeader(value = "userId",defaultValue = "0") int userId, @RequestParam(name = "plateId",defaultValue = "0") int plateId, @RequestParam int page, @RequestParam int count){
        logger.info("userId={},plateId={},page={},count={}", userId, plateId, page, count);
        try {
            if (userId<0||plateId<0){
                return BwJsonHelper.returnJSON("1001","参数不合法");
            }
            List<InformationListVo> informationListVoList = informationRpcService.infoRecommendList(userId, plateId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",informationListVoList);
        } catch (Exception e) {
            logger.error("infoRecommendList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 资讯详情展示
     * @param userId
     * @param id
     * @return
     */
    @RequestMapping(value = "/v1/findInformationDetails",method = RequestMethod.GET,produces = "application/json")
    public String findInformationDetails(@RequestHeader(value = "userId",defaultValue = "0") int userId,@RequestParam int id){
        logger.info("userId={},id={}", userId, id);
        try {
            InformationVo informationDetails = informationRpcService.findInformationDetails(userId, id);
            return  BwJsonHelper.returnJSON("0000","查询成功",informationDetails);
        } catch (Exception e) {
            logger.error("findInformationDetails={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 所有板块查询
     * @return
     */
    @RequestMapping(value = "/v1/findAllInfoPlate",method = RequestMethod.GET,produces = "application/json")
    public String findAllInfoPlate(){
        try {
            List<InformationPlate> allInfoPlate = informationRpcService.findAllInfoPlate();
            return BwJsonHelper.returnJSON("0000","查询成功",allInfoPlate);
        } catch (Exception e) {
            logger.error("findAllInfoPlate={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }
    /**
     * 修改资讯分享数
     * @param infoId
     */
    @RequestMapping(value = "/verify/v1/updateInfoShareNum",method = RequestMethod.PUT,produces = "application/json")
    public String updateInfoShareNum(@RequestHeader(value = "userId") int userId,@RequestParam int infoId){
        try {
            boolean boo = informationRpcService.updateInfoShareNum(infoId);
            if (boo){
                return BwJsonHelper.returnJSON("0000","分享成功");
            }
            return BwJsonHelper.returnJSON("1001","分享失败");
        } catch (Exception e) {
            logger.error("updateInfoShareNum={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 资讯点赞
     * @param userId
     * @param infoId
     * @return
     */
    @RequestMapping(value = "/verify/v1/addGreatRecord",method = RequestMethod.POST ,produces = "application/json")
    public String addGreatRecord(@RequestHeader int userId,@RequestParam int infoId){
        try {
            boolean boo = informationRpcService.addGreatRecord(userId, infoId);
            if (boo){
                return BwJsonHelper.returnJSON("0000","点赞成功");
            }
            return BwJsonHelper.returnJSON("1001","点赞失败");
        } catch (Exception e) {
            logger.error("addGreatRecord={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }
    /**
     * 取消点赞
     * @param userId
     * @param infoId
     * @return
     */
    @RequestMapping(value = "/verify/v1/cancelGreat",method = RequestMethod.DELETE,produces = "application/json")
    public String cancelGreat(@RequestHeader int userId,@RequestParam int infoId){
        try {
            boolean boo = informationRpcService.cancelGreat(userId, infoId);
            if (boo){
                return BwJsonHelper.returnJSON("0000","取消点赞成功");
            }
            return  BwJsonHelper.returnJSON("1001","取消点赞失败");
        } catch (Exception e) {
            logger.error("cancelGreat={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 资讯用户评论
     * @param userId
     * @param content
     * @param infoId
     * @return
     */
    @RequestMapping(value = "/verify/v1/addInfoComment",method = RequestMethod.POST,produces = "application/json")
    public String addInfoComment(@RequestHeader int userId, @RequestParam String content,@RequestParam int infoId){
        try {
            if (content==null||content.equals("")){
                return BwJsonHelper.returnJSON("1001","评论内容不能为空");
            }
            InfoComment comment = new InfoComment();
            comment.setUserId(userId);
            comment.setContent(content);
            comment.setInfoId(infoId);
            boolean boo = informationRpcService.addInfoComment(comment);
            if (boo){
                return BwJsonHelper.returnJSON("0000","评论成功");
            }
            return BwJsonHelper.returnJSON("1001","评论失败");
        } catch (Exception e) {
            logger.error("addInfoComment={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 查询资讯评论列表
     * @param infoId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findAllInfoCommentList",method = RequestMethod.GET,produces = "application/json")
    public String findAllInfoCommentList(@RequestHeader(value = "userId",defaultValue = "0") int userId,@RequestParam int infoId,@RequestParam int page,@RequestParam int count){
        try {
            List<InfoCommentVo> allInfoComment = informationRpcService.findAllInfoComment(infoId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",allInfoComment);
        } catch (Exception e) {
            logger.error("findAllInfoCommentList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }
    /**
     * 根据标题模糊查询
     * @param title
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findInformationByTitle",method = RequestMethod.GET,produces = "application/json" )
    public String findInformationByTitle(@RequestParam String title,@RequestParam int page,@RequestParam int count){
        try {
            List<InformationVagueVo> informationByTitle = informationRpcService.findInformationByTitle(title, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",informationByTitle);
        } catch (Exception e) {
            logger.error("findInformationByTitle={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }
    /**
     * 资讯广告
     * @return
     */
    @RequestMapping(value = "/v1/findInfoAdvertising",method = RequestMethod.GET,produces = "application/json" )
    public String findInfoAdvertising(){
        try {
            InfoAdvertisingVo infoAdvertising = informationRpcService.findInfoAdvertising();
            return BwJsonHelper.returnJSON("0000","查询成功",infoAdvertising);
        } catch (Exception e) {
            logger.error("findInfoAdvertising={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }
    /**
     * 根据来源（作者）模糊查询
     * @param source
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/v1/findInformationBySource",method = RequestMethod.GET,produces = "application/json" )
    public String findInformationBySource(@RequestParam String source,@RequestParam int page,@RequestParam int count){
        try {
            List<InformationVagueVo> informationBySource = informationRpcService.findInformationBySource(source, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",informationBySource);
        } catch (Exception e) {
            logger.error("findInformationBySource={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }


}
