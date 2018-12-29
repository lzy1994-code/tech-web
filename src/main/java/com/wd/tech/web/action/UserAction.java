package com.wd.tech.web.action;

import com.wd.tech.rpc.api.GroupRpcService;
import com.wd.tech.rpc.api.UserRpcService;
import com.wd.tech.rpc.pojo.UserInfoCollection;
import com.wd.tech.rpc.pojo.UserIntegral;
import com.wd.tech.rpc.vo.*;
import com.wd.tech.web.util.BwJsonHelper;
import com.wd.tech.web.util.CheckUtil;
import com.wd.tech.web.util.CustomConfig;
import com.wd.tech.web.util.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by xyj on 2018/8/22.
 */
@RestController
@RequestMapping("/user")
public class UserAction {

    private Logger logger = LoggerFactory.getLogger(UserAction.class);
    @Resource
    private UserRpcService userRpcService;
    @Resource
    private CustomConfig customConfig;
    @Resource
    private GroupRpcService groupRpcService;
    /**
     *  注册用户
     * @param phone
     * @param pwd
     * @param nickName
     * @return
     */
    @RequestMapping(value = "/v1/register",method = RequestMethod.POST,produces = "application/json")
    public String register(@RequestParam String phone,@RequestParam String pwd,@RequestParam String nickName){
        try {
            logger.info("phone={},pwd={},nickName={}",phone,pwd,nickName);

            int maxNum = userRpcService.findMaxNum();
            //TODO 控制总注册人数，特殊处理一下
            if(maxNum >= 55)
            {
                return BwJsonHelper.returnJSON("1001","因环信注册用户已达上限,如需注册新帐号请联系徐老师");
            }

            if(Pattern.matches(CheckUtil.REGEX_MOBILE,phone)==false){
                return BwJsonHelper.returnJSON("1001","请正确填写手机号");
            }
            if (pwd == null ||pwd.equals("")){
                return BwJsonHelper.returnJSON("1002","密码不能为空");
            }
            if (nickName == null || nickName.equals("")){
                return BwJsonHelper.returnJSON("1003","昵称不能为空");
            }

            int register = userRpcService.register(phone, pwd, nickName);
            switch (register){
                case 1:
                    return BwJsonHelper.returnJSON("0000","注册成功");
                case 2:
                    return BwJsonHelper.returnJSON("1001","注册失败，用户昵称已存在");
                case 3:
                    return BwJsonHelper.returnJSON("1001","该手机号已注册，不能重复注册");
                case 4:
                    return BwJsonHelper.returnJSON("1001","密码加解密处理异常");
                default:
                    return BwJsonHelper.returnJSON("1001","注册失败");
            }
        } catch (Exception e) {
            logger.error("register={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  校验手机号是否可用
     * @param phone
     * @return
     */
    @RequestMapping(value = "/v1/checkPhone",method = RequestMethod.POST,produces = "application/json")
    public String checkPhone(@RequestParam String phone)
    {
        try {
            logger.info("phone={}",phone);

            if(Pattern.matches(CheckUtil.REGEX_MOBILE,phone)==false){
                return BwJsonHelper.returnJSON("1001","请正确填写手机号");
            }

            if(userRpcService.checkPhone(phone) > 0)
            {
                return BwJsonHelper.returnJSON("1001","该手机号已注册");
            }

            return BwJsonHelper.returnJSON("0000","手机号可用");
        } catch (Exception e) {
            logger.error("checkPhone",e);
            return BwJsonHelper.returnJSON("1001","网络异常");
        }
    }

    /**
     *  用户登录
     * @param phone
     * @param pwd
     * @return
     */
    @RequestMapping(value = "/v1/login",method = RequestMethod.POST,produces = "application/json")
    public String login(@RequestParam String phone,@RequestParam String pwd){
        try {
            logger.info("phone={},pwd={}",phone,pwd);
            if(phone == null || phone.equals(""))
            {
                return BwJsonHelper.returnJSON("1001","请输入手机号");
            }
            if(pwd == null || pwd.equals(""))
            {
                return BwJsonHelper.returnJSON("1001","请输入密码");
            }
            UserLoginVo user = userRpcService.login(phone, pwd);
            if (user == null){
                return BwJsonHelper.returnJSON("1001","登陆失败,手机号或密码错误");
            }
            return BwJsonHelper.returnJSON("0000","登录成功",user);
        } catch (Exception e) {
            logger.error("login：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  完善用户信息
     * @param userId
     * @param user
     * @return
     */
    @RequestMapping(value = "/verify/v1/perfectUserInfo",method = RequestMethod.POST,produces = "application/json")
    public String perfectUserInfo(@RequestHeader int userId,UserInfoVo user){
        try {
            logger.info("userId={},user={}",userId,user);
            if(user.getNickName() == null || user.getNickName().equals(""))
            {
                return BwJsonHelper.returnJSON("1001","用户昵称不能为空");
            }
            if(user.getEmail().equals("") || user.getEmail() == null)
            {
                return BwJsonHelper.returnJSON("1001","邮箱不能为空");
            }
            if(user.getSex() !=1 && user.getSex() != 2)
            {
                return BwJsonHelper.returnJSON("1001","请选择正确的性别");
            }

            if (!Pattern.matches(CheckUtil.REGEX_EMAIL, user.getEmail())){
                return BwJsonHelper.returnJSON("1001","请输入正确的邮箱");
            }
            int i = userRpcService.perfectUserInfo(userId,user.getNickName(), user.getSex(), user.getSignature(), user.getBirthday(), user.getEmail());
            if (i==1){
                return BwJsonHelper.returnJSON("0000","完善成功");
            }
            return BwJsonHelper.returnJSON("1001","完善失败");
        } catch (Exception e) {
            logger.error("perfectUserInfo：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     *  根据用户ID查询用户详细信息(我的资料)
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/getUserInfoByUserId",method = RequestMethod.GET,produces = "application/json")
    public String getUserInfoByUserId(@RequestHeader int userId){
        try {
            UserInfoShowVo userInfoByUserId = userRpcService.getUserInfoByUserId(userId);
            if (userInfoByUserId == null){
                return BwJsonHelper.returnJSON("1001","查询失败");
            }
            return BwJsonHelper.returnJSON("0000","查询成功",userInfoByUserId);
        } catch (Exception e) {
            logger.error("getUserInfoByUserId：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     * 查询好友信息
     * @param friend
     * @return
     */
    @RequestMapping(value = "/verify/v1/queryFriendInformation",method = RequestMethod.GET,produces = "application/json")
    public String queryFriendInformation(@RequestParam int friend){
        try {
            UserInfoShowVo userInfoByUserId = userRpcService.getUserInfoByUserId(friend);
            if (userInfoByUserId == null){
                return BwJsonHelper.returnJSON("1001","查询失败,没有查到此用户");
            }
            List<GroupListVo> groupsByUserId = groupRpcService.findGroupsByUserId(friend);
            userInfoByUserId.setMyGroupList(groupsByUserId);
            return BwJsonHelper.returnJSON("0000","查询成功",userInfoByUserId);
        } catch (Exception e) {
            logger.error("getUserInfoByUserId：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  修改用户昵称
     * @param userId
     * @param nickName
     * @return
     */
    @RequestMapping(value = "/verify/v1/modifyNickName",method = RequestMethod.PUT,produces = "application/json")
    public String modifyNickName(@RequestHeader int userId,@RequestParam String nickName){
        try {
            if (nickName == null ||nickName.equals(""))
            {
                return BwJsonHelper.returnJSON("1001","用户昵称不能为空");
            }
            int i = userRpcService.modifyNickName(userId, nickName);
            if (1==i){
                return BwJsonHelper.returnJSON("0000","修改成功");
            }
            return BwJsonHelper.returnJSON("1001","修改失敗");
        } catch (Exception e) {
            logger.error("modifyNickName：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     *  修改用户签名
     * @param userId
     * @param signature
     * @return
     */
    @RequestMapping(value = "/verify/v1/modifySignature",method = RequestMethod.PUT,produces = "application/json")
    public String modifySignature(@RequestHeader int userId,@RequestParam String signature){
        try {
            if (signature.length() > 30){
                return BwJsonHelper.returnJSON("1001", "簽名過長");
            }
            int i = userRpcService.modifySignature(userId, signature);
            if (i==1){
                return BwJsonHelper.returnJSON("0000", "修改成功");
            }
            return BwJsonHelper.returnJSON("1001", "修改失敗");
        } catch (Exception e) {
            logger.error("modifySignature：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }


    /**
     *  修改头像地址
     * @param userId
     * @param
     * @return
     */
    @RequestMapping(value = "/verify/v1/modifyHeadPic",method = RequestMethod.POST,produces = "application/json")
    public String modifyHeadPic(@RequestHeader int userId,@RequestParam(value = "image") MultipartFile file) {
        String savePath = "";
        String headPath = "";
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

            String nowTime = sdf2.format(new Date());
            savePath = String.format(customConfig.getHeadPath(), nowTime);
            headPath = String.format(customConfig.getHeadVisit(), nowTime);

            String newFileName = df.format(new Date()) + "." + suffix;
            savePath = savePath + newFileName;
            headPath = headPath + newFileName;

            File f = new File(savePath);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
            }
            try {
                file.transferTo(f);
                int i = userRpcService.modifyHeadPic(userId, headPath);
                if ( i == 1){
                    return BwJsonHelper.returnJSON("0000", "上传成功",headPath);
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
     *  修改邮箱
     * @param userId
     * @param email
     * @return
     */
    @RequestMapping(value = "/verify/v1/modifyEmail",method = RequestMethod.PUT,produces = "application/json")
    public String modifyEmail(@RequestHeader int userId,@RequestParam String email){
        try {
            if(email.equals("") || email == null)
            {
                return BwJsonHelper.returnJSON("1001","邮箱不能为空");
            }
            if (!Pattern.matches(CheckUtil.REGEX_EMAIL, email)){
                return BwJsonHelper.returnJSON("1001","请输入正确的邮箱");
            }
            int i = userRpcService.modifyEmail(userId, email);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","修改成功");
            }
            return BwJsonHelper.returnJSON("1001","修改失敗");
        } catch (Exception e) {
            logger.error("modifyEmail：{}", e);
            return BwJsonHelper.returnJSON("1001", "网络异常,请联系管理员");
        }
    }
    /**
     *  修改用户密码
     * @param userId
     * @param newPwd
     * @param oldPwd
     * @return
     */
    @RequestMapping(value = "/verify/v1/modifyUserPwd",method = RequestMethod.PUT,produces = "application/json")
    public String modifyUserPwd(@RequestHeader int userId,@RequestParam String newPwd,@RequestParam String oldPwd){
        try {
            if (newPwd.equals("")||newPwd==null){
                return BwJsonHelper.returnJSON("1001","新密码不能为空");
            }
            int i = userRpcService.modifyUserPwd(userId, newPwd, oldPwd);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","修改成功");
            }
            return BwJsonHelper.returnJSON("1001","修改失敗");
        } catch (Exception e) {
            logger.error("modifyUserPwd：{}", e);
            return BwJsonHelper.returnJSON("1001", "网络异常,请联系管理员");
        }
    }

    /**
     *  根据环信userNames批量查询会话列表需要的信息
     * @param userNames
     * @return
     */
    @RequestMapping(value = "/verify/v1/findConversationList",method = RequestMethod.GET,produces = "application/json")
    public String findConversationList(@RequestParam String userNames){
        try {
            if(null == userNames  || "".equals(userNames))
            {
                return BwJsonHelper.returnJSON("1001","参数不能为空");
            }

            List<ConversationListVo> conversationList = userRpcService.findConversationList(userNames);
            return BwJsonHelper.returnJSON("0000","查询成功",conversationList);
        } catch (Exception e) {
            logger.error("findConversationList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     * 查询用户积分
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserIntegral",method = RequestMethod.GET,produces = "application/json")
    public String findUserIntegral(@RequestHeader int userId){
        try {
            UserIntegral userIntegral = userRpcService.findUserIntegral(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",userIntegral);
        } catch (Exception e) {
            logger.error("findUserIntegral={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     * 查询用户积分明细
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserIntegralRecord",method = RequestMethod.GET,produces = "application/json")
    public String findUserIntegralRecord(@RequestHeader int userId,@RequestParam int page, @RequestParam int count){
        try {
            List<UserIntegralRecordVo> userIntegralRecord = userRpcService.findUserIntegralRecord(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",userIntegralRecord);
        } catch (Exception e) {
            logger.error("findUserIntegralRecord={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     * 用户收藏列表
     * @param userId
     * @param page
     * @param count
     * @return
     */
    @RequestMapping(value = "/verify/v1/findAllInfoCollection",method = RequestMethod.GET,produces = "application/json")
    public String findAllInfoCollection(@RequestHeader int userId,@RequestParam int page,@RequestParam int count){
        try {
            List<UserInfoCollection> allInfoCollection = userRpcService.findAllInfoCollection(userId, page, count);
            return BwJsonHelper.returnJSON("0000","查询成功",allInfoCollection);
        } catch (Exception e) {
            logger.error("findAllInfoCollection={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }

    }

    /**
     *  微信登录
     * @param ak
     * @param code
     * @return
     */
    @RequestMapping(value = "/v1/weChatLogin",method = RequestMethod.POST,produces = "application/json")
    public String weChatLogin(
            @RequestHeader String ak,
            @RequestParam String code)
    {
        try {
            logger.info("weChatLogin：code={}",code);
            UserLoginVo userLogin = userRpcService.wxBindingLogin(code);
            if(userLogin == null)
            {
                logger.info("weChatLogin登陆失败");
                return BwJsonHelper.returnJSON("1001","微信登陆失败");
            }
            return BwJsonHelper.returnJSON("0000","登陆成功",userLogin);
        } catch (Exception e) {
            logger.error("weChatLogin={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  绑定微信帐号
     * @param userId
     * @param code
     * @return
     */
    @RequestMapping(value = "/verify/v1/bindWeChat",method = RequestMethod.POST,produces = "application/json")
    public String bindWeChat(
            @RequestHeader int userId,
            @RequestParam String code) {
        try {
            logger.info("bindWeChat：userId={},code={}", userId, code);
            int num = userRpcService.bindWeChat(userId, code);
            if (num == 2) {
                logger.info("bindWeChat：已绑定微信账号");
                return BwJsonHelper.returnJSON("1001", "已绑定微信账号");
            } else if (num == 1) {
                return BwJsonHelper.returnJSON("0000", "绑定成功");
            }
            return BwJsonHelper.returnJSON("1001", "绑定失败");
        } catch (Exception e) {
            logger.error("bindWeChat={}", e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     * 添加收藏
     * @param userId
     * @param infoId
     * @return
     */
    @RequestMapping(value = "/verify/v1/addCollection",method = RequestMethod.POST,produces = "application/json")
    public String addCollection(@RequestHeader int userId,@RequestParam int infoId){

        try {
            int i = userRpcService.addCollection(userId, infoId);
            if (i==2){
                return BwJsonHelper.returnJSON("0000" ,"已收藏，不能重复收藏");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000" ,"收藏成功");
            }
            return BwJsonHelper.returnJSON("1001","收藏失败");
        } catch (Exception e) {
            logger.error("addCollection={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }

    }

    /**
     * 取消收藏(可批量操作)
     * @param userId
     * @param infoId
     * @return
     */
    @RequestMapping(value = "/verify/v1/cancelCollection",method = RequestMethod.DELETE,produces = "application/json")
    public String cancelCollection(@RequestHeader int userId,@RequestParam String infoId){
        try {
            List<Integer> infoIds = Arrays.stream(infoId.split(",")).map(s->Integer.parseInt(s.trim())).collect(Collectors.toList());
            int i = userRpcService.cancelCollection(userId, infoIds);
            if (i==1){
                return BwJsonHelper.returnJSON("0000" ,"取消成功");
            }
            return BwJsonHelper.returnJSON("1001","取消失败");
        } catch (Exception e) {
            logger.error("cancelCollection={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  判断是否绑定微信
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/whetherToBindWeChat",method = RequestMethod.GET,produces = "application/json")
    public String whetherToBindWeChat(
            @RequestHeader int userId)
    {
        try {
            logger.info("whetherToBindWeChat：userId={}",userId);
            int num = userRpcService.whetherToBindWeChat(userId);
            return BwJsonHelper.returnJSON("0000","查询成功","bindStatus",num);
        } catch (Exception e) {
            logger.error("whetherToBindWeChat={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  查询用户任务列表
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserTaskList",method = RequestMethod.GET,produces = "application/json")
    public String findUserTaskList(
            @RequestHeader int userId) {
        try {
            logger.info("findUserTaskList：userId={}", userId);
            List<UserTaskListVo> userTaskList = userRpcService.findUserTaskList(userId);
            return BwJsonHelper.returnJSON("0000", "查询成功", userTaskList);
        } catch (Exception e) {
            logger.error("findUserTaskList={}", e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     * 用户关注列表
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/findFollowUserList",method = RequestMethod.GET,produces = "application/json")
    public String  findFollowUserList(@RequestHeader int userId,@RequestParam int page,@RequestParam int count){
        try {
            List<FocusUserVo> followUserList = userRpcService.findFollowUserList(userId,page,count);
            return BwJsonHelper.returnJSON("0000","查询成功",followUserList);
        } catch (Exception e) {
            logger.error("findFollowUserList={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     * 关注用户
     * @param userId
     * @param focusId
     * @return
     */
    @RequestMapping(value = "/verify/v1/addFollow",method = RequestMethod.POST,produces = "application/json")
    public String addFollow(@RequestHeader int userId,@RequestParam int focusId){
        try {
            int i = userRpcService.addFollow(userId, focusId);
            if (i==2){
                return BwJsonHelper.returnJSON("0000","已关注用户，不能重复关注");
            }
            if (i==1){
                return BwJsonHelper.returnJSON("0000","关注成功");
            }
            return BwJsonHelper.returnJSON("1001","关注失败");
        } catch (Exception e) {
            logger.error("addFollow={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  做任务
     * @param userId
     * @param taskId
     * @return
     */
    @RequestMapping(value = "/verify/v1/doTheTask",method = RequestMethod.POST,produces = "application/json")
    public String doTheTask(
            @RequestHeader int userId,
            @RequestParam int taskId)
    {
        try {
            logger.info("doTheTask：userId={},taskId={}", userId, taskId);
            int status = userRpcService.doTheTask(taskId, userId);
            if (status == 1) {
                return BwJsonHelper.returnJSON("0000", "做任务成功");
            } else {
                return BwJsonHelper.returnJSON("1001", "做任务失败");
            }
        } catch (Exception e) {
            logger.error("doTheTask={}", e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }
    /**
     * 取消关注
     * @param userId
     * @param focusId
     * @return
     */
    @RequestMapping(value = "/verify/v1/cancelFollow",method = RequestMethod.DELETE,produces = "application/json")
    public String cancelFollow(@RequestHeader int userId,@RequestParam int focusId){
        try {
            int i = userRpcService.cancelFollow(userId, focusId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","取消成功");
            }
            return BwJsonHelper.returnJSON("1001","取消失败");
        } catch (Exception e) {
            logger.error("cancelFollow={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  签到
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/userSign",method = RequestMethod.POST,produces = "application/json")
    public String userSign(@RequestHeader int userId){
        try {
            int i = userRpcService.userSign(userId);
            if (i==1){
                return BwJsonHelper.returnJSON("0000","签到成功");
            }
            return BwJsonHelper.returnJSON("1001","签到失败");
        } catch (Exception e) {
            logger.error("userSign={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  查询当天签到状态
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserSignStatus",method = RequestMethod.GET,produces = "application/json")
    public String findUserSignStatus(@RequestHeader int userId){
        try {
            int userSignStatus = userRpcService.findUserSignStatus(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",userSignStatus);
        } catch (Exception e) {
            logger.error("findUserSignStatus={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  查询用户连续签到天数
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/findContinuousSignDays",method = RequestMethod.GET,produces = "application/json")
    public String findContinuousSignDays(@RequestHeader int userId){
        try {
            int continuousSignDays = userRpcService.findContinuousSignDays(userId);
            return BwJsonHelper.returnJSON("0000","查询成功",continuousSignDays);
        } catch (Exception e) {
            logger.error("findContinuousSignDays={}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  绑定faceId
     * @param userId
     * @return
     */
    @RequestMapping(value = "/verify/v1/bindingFaceId",method = RequestMethod.PUT,produces = "application/json")
    public String bindingFaceId(@RequestHeader int userId){
        try {
            String faceId = userRpcService.bindingFaceId(userId);
            if (faceId != null){
                return BwJsonHelper.returnJSON("0000","绑定成功","faceId",faceId);
            }
            return BwJsonHelper.returnJSON("1001","绑定失败");
        } catch (Exception e) {
            logger.error("bindingFaceId：{}",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  face登录
     * @param faceId
     * @return
     */
    @RequestMapping(value = "/v1/faceLogin",method = RequestMethod.POST,produces = "application/json")
    public String faceLogin(@RequestParam String faceId){
        try {
            logger.info("faceId={}",faceId);
            if(faceId == null || faceId.equals(""))
            {
                return BwJsonHelper.returnJSON("1001","faceId不能为空");
            }

            UserLoginVo user = userRpcService.faceLogin(faceId);
            if (user == null){
                return BwJsonHelper.returnJSON("1001","登陆失败");
            }
            return BwJsonHelper.returnJSON("0000","登录成功",user);
        } catch (Exception e) {
            logger.error("faceLogin",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }

    /**
     *  根据手机号查询用户信息
     * @param phone
     * @return
     */
    @RequestMapping(value = "/verify/v1/findUserByPhone",method = RequestMethod.GET,produces = "application/json")
    public String findUserByPhone(@RequestParam String phone){
        try {
            UserInfoShowVo userInfo = userRpcService.findUserByPhone(phone);
            if(userInfo == null)
            {
                return BwJsonHelper.returnJSON("1001","没有查询到相关用户");
            }
            return BwJsonHelper.returnJSON("0000","查询成功",userInfo);
        } catch (Exception e) {
            logger.error("findUserByPhone",e);
            return BwJsonHelper.returnJSON("1001","网络异常,请联系管理员");
        }
    }


}