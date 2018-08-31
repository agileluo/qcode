package io.github.agileluo.qcode.web;


import com.alibaba.fastjson.JSON;
import io.github.agileluo.qcode.util.CheckUtils;
import io.github.agileluo.qcode.util.RedisComponent;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
        redisComponent.set("OpenId-" + session.getOpenid(), session);
        return session;
    }

    @Data
    static class WechatSession {
        private String openid;
        private String session_key;
    }
}
