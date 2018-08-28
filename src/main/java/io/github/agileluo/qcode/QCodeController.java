package io.github.agileluo.qcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("qCode")
public class QCodeController {
    private static final String Code = "qCode";
    @Autowired
    private RedisTemplate<String, String>  redisTemplate;

    @RequestMapping("save")
    public void save(@RequestBody String body){
        redisTemplate.opsForList().leftPush(Code, body);
    }

    @RequestMapping("list")
    public List<String> list(Integer size){
        return redisTemplate.opsForList().range(Code, 0, size != null ? size : 500);
    }
}
