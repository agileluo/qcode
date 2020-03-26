package io.github.agileluo.qcode.web;


import com.alibaba.fastjson.JSON;
import io.github.agileluo.qcode.mapper.ScanUserMapper;
import io.github.agileluo.qcode.model.ScanUser;
import io.github.agileluo.qcode.util.BizException;
import io.github.agileluo.qcode.util.CheckUtils;
import io.github.agileluo.qcode.util.RedisComponent;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WechatTokenController {
    private static final Logger log = LoggerFactory.getLogger(WechatTokenController.class);

    @Value("${wechat.url}")
    private String baseUrl;
    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.secret}")
    private String secret;

    @Autowired
    protected RedisComponent redisComponent;
    @Value("${wechat.session.timeout:1800}")
    protected Integer sessionTimeout;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ScanUserMapper userMapper;

    @RequestMapping("login")
    public WechatSession login(String code) {
        CheckUtils.checkEmpty("code", code);
        String loginCodeKey = "loginCode-" + code;
        WechatSession session = redisComponent.get(loginCodeKey, WechatSession.class);
        if (session == null) {
            String param = String.format("?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", appId, secret, code);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseUrl + param, String.class);
            String body = responseEntity.getBody();
            log.info(body);
            if (body != null && !body.contains("errcode")) {
                session = JSON.parseObject(body, WechatSession.class);
                redisComponent.setEx("loginCode-" + code, session, 1800);
            }
        }
        ScanUser query = new ScanUser();
        query.setOpenId(session.getOpenid());
        ScanUser dbUser = userMapper.selectOne(query);
        if(dbUser != null){
            session.setUserName(dbUser.getUserName());
        }
        redisComponent.set("OpenId-" + session.getOpenid(), session);
        return session;
    }
    @RequestMapping("bindUser")
    public ScanUser bindUser(@RequestBody ScanUser reqUser){
        log.info("bindUser: {}", JSON.toJSONString(reqUser));
        CheckUtils.checkEmpty(reqUser, "userName", "password", "openId");
        ScanUser query = new ScanUser();
        query.setUserName(reqUser.getUserName());
        ScanUser dbUser = userMapper.selectOne(query);
        if(dbUser == null){
            throw new BizException("用户不存在，请联系管理员！");
        }else if(!dbUser.getPassword().equals(reqUser.getPassword())){
            throw new BizException("密码错误！");
        }else if(StringUtils.isNotEmpty(dbUser.getOpenId()) && !dbUser.getOpenId().equals(reqUser.getOpenId())){
            throw new BizException("该用户绑定微信【" + dbUser.getWxName() + "】");
        }

        ScanUser updateUser = new ScanUser();
        updateUser.setId(dbUser.getId());
        updateUser.setOpenId(reqUser.getOpenId());
        updateUser.setWxName(reqUser.getWxName());
        userMapper.updateByPrimaryKeySelective(updateUser);
        dbUser.setPassword(null);
        return dbUser;
    }


    @Data
    static class WechatSession {
        private String openid;
        private String session_key;
        private String userName;
    }
}
