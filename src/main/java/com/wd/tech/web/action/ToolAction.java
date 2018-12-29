package com.wd.tech.web.action;

import com.alibaba.fastjson.JSONObject;
import com.bw.pay.client.api.WeChatRpcService;
import com.bw.pay.client.pojo.ProductConstants;
import com.bw.pay.client.pojo.WeChatVo;
import com.wd.tech.rpc.api.ToolRpcService;
import com.wd.tech.rpc.pojo.AK;
import com.wd.tech.rpc.pojo.AppVersion;
import com.wd.tech.rpc.pojo.SysNotice;
import com.wd.tech.rpc.vo.CommodityListVo;
import com.wd.tech.rpc.vo.PrizeVo;
import com.wd.tech.rpc.vo.UserLotteryRecordVo;
import com.wd.tech.rpc.vo.WinningVo;
import com.wd.tech.web.util.BwJsonHelper;
import com.wd.tech.web.util.EncryptUtil;
import com.wd.tech.web.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xyj on 2018/9/26.
 */
@RestController
@RequestMapping("/tool")
public class ToolAction {

    private Logger logger = LoggerFactory.getLogger(ToolAction.class);

    @Resource
    private ToolRpcService techToolRpcService;

    @Resource
    private WeChatRpcService weChatRpcService;

    @RequestMapping(value = "/v1/findVipCommodityList",method = RequestMethod.GET,produces = "application/json")
    public String findVipCommodityList()
    {
        try {
            List<CommodityListVo> list = techToolRpcService.findVipCommodityList();
            return BwJsonHelper.returnJSON("0000","查询成功",list);
        } catch (Exception e) {
            logger.error("doTheTask={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  购买VIP
     * @param userId
     * @param commodityId
     * @return
     */
    @RequestMapping(value = "/verify/v1/buyVip",method = RequestMethod.POST,produces = "application/json")
    public String buyVip(
            @RequestHeader int userId,
            @RequestParam int commodityId,
            @RequestParam String sign
    )
    {
        try {

            //校验签名
            StringBuffer sb = new StringBuffer();
            sb.append(userId);
            sb.append(commodityId);
            sb.append("tech");
            String mySign = WebUtil.MD5(sb.toString());

            if(!mySign.equals(sign))
            {
                return BwJsonHelper.returnJSON("1001","签名不对");
            }

            String orderId = techToolRpcService.buyVip(userId,commodityId);
            return BwJsonHelper.returnJSON("0000","下单成功","orderId",orderId);
        } catch (Exception e) {
            logger.error("buyVip={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  支付
     * @param request
     * @param userId
     * @param orderId
     * @param payType
     * @return
     */
    @RequestMapping(value = "/verify/v1/pay",method = RequestMethod.POST,produces = "application/json")
    public String pay(
            HttpServletRequest request,
            @RequestHeader int userId,
            @RequestParam String orderId,
            @RequestParam int payType
    )
    {
        try {
            logger.info("pay：userId={},orderId={},payType",userId,orderId,payType);
            //获取用户真实IP
            String ip = WebUtil.getIpAddress(request);
            logger.info("获取用户真实IP为={}",ip);

            String result = techToolRpcService.pay(orderId,payType,ip);
            logger.info("返回客户端支付数据={}",result);

            return result;
        } catch (Exception e) {
            logger.error("pay={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  微信分享前置接口，获取分享所需参数
     * @param time
     * @param sign
     * @return
     */
    @RequestMapping(value = "/v1/wxShare",method = RequestMethod.POST,produces = "application/json")
    public String wxShare(
            @RequestHeader String time,
            @RequestHeader String sign)
    {
        logger.info("wxShare：time={},sign={}", time,sign);
        try {
            if(time == null || time.equals("") || sign == null || sign.equals(""))
            {
                return BwJsonHelper.returnJSON("1001", "非法请求");
            }

            String str = time+"wxShare"+"movie";
            String newSign = WebUtil.MD5(str);
            if(!newSign.equals(sign))
            {
                return BwJsonHelper.returnJSON("1001", "签名验证失败");
            }

            WeChatVo weChatInfo = weChatRpcService.getWeChatInfo(ProductConstants.PRODUCT_TECH);

            String appSecret = weChatInfo.getAppSecret();
            appSecret = EncryptUtil.encrypt(appSecret);
            return BwJsonHelper.returnJSON("0000", "分享成功","appId",weChatInfo.getAppId(),"appSecret",appSecret);
        } catch (Exception e) {
            logger.error("wxShare",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }

    }

    /**
     *  意见反馈
     * @param userId
     * @param content
     * @return
     */
    @RequestMapping(value = "/verify/v1/recordFeedBack",method = RequestMethod.POST,produces = "application/json")
    public String recordFeedBack(
                                @RequestHeader int userId,
                                 @RequestParam String content)
    {
        try {
            int num = techToolRpcService.recordFeedBack(userId, content);
            if(num == 0)
            {
                return BwJsonHelper.returnJSON("1001","反馈失败");
            }
            return BwJsonHelper.returnJSON("0000","反馈成功");
        } catch (Exception e) {
            logger.error("recordFeedBack：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }


    /**
     *  查询新版本
     * @param ak
     * @return
     */
    @RequestMapping(value = "/v1/findNewVersion",method = RequestMethod.GET,produces = "application/json")
    public String findNewVersion(
            @RequestHeader String ak)
    {
        try {
            if(ak == null || ak.equals(""))
            {
                return BwJsonHelper.returnJSON("1001","ak不能为空");
            }

            AppVersion appVersion = techToolRpcService.findNewAk();
            String ak2 = appVersion.getAk();

            AK newAk = new AK(ak2);
            AK oldAk = new AK(ak);

            if(oldAk.compare(newAk) == -1)
            {
                return BwJsonHelper.returnJSON("0000","查询成功","flag",1,"downloadUrl",appVersion.getDownloadUrl());
            }
            return BwJsonHelper.returnJSON("0000","查询成功","flag",2);
        } catch (Exception e) {
            logger.error("findNewVersion：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     *  查询所有奖品
     * @return
     */
    @RequestMapping(value = "/verify/v1/findAllPrize",method = RequestMethod.GET,produces = "application/json")
    public String findAllPrize(){
        try {
            List<PrizeVo> allPrize = techToolRpcService.findAllPrize();
            return BwJsonHelper.returnJSON("0000","查询成功",allPrize);
        } catch (Exception e) {
            logger.error("findAllPrize：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     *  抽奖
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/lottery",method = RequestMethod.POST,produces = "application/json")
    public String lottery(@RequestHeader int userId){
        try {
            WinningVo lottery = techToolRpcService.lottery(userId);
            return JSONObject.toJSONString(lottery);
        } catch (Exception e) {
            logger.error("lottery",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  查询用户抽奖记录
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findLotteryRecordList",method = RequestMethod.GET,produces = "application/json")
    public String findLotteryRecordList(@RequestHeader int userId, @RequestParam int page,@RequestParam int count){
        try {
            List<UserLotteryRecordVo> lotteryRecordList = techToolRpcService.findLotteryRecordList(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",lotteryRecordList);
        } catch (Exception e) {
            logger.error("findLotteryRecordList",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  领取奖品
     * @param recordId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/receivePrize",method = RequestMethod.PUT,produces = "application/json")
        public  String receivePrize(@RequestHeader int userId,@RequestParam int recordId){
        try {
            int i = techToolRpcService.receivePrize(recordId, userId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","领取成功");
            }
            return BwJsonHelper.returnJSON("1001","领取失败");
        } catch (Exception e) {
            logger.error("receivePrize：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     * 查询用户系统通知
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findSysNoticeList",method = RequestMethod.GET,produces = "application/json")
    public String findSysNoticeList(@RequestHeader int userId,@RequestParam int page,@RequestParam int count){
        try {
            List<SysNotice> sysNoticeList = techToolRpcService.findSysNoticeList(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",sysNoticeList);
        } catch (Exception e) {
            logger.error("findSysNoticeList：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

}
