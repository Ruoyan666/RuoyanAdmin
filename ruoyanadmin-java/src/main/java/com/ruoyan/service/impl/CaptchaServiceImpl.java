package com.ruoyan.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.ruoyan.commom.lang.Const;
import com.ruoyan.commom.lang.Result;
import com.ruoyan.service.CaptchaService;
import com.ruoyan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Package: com.ruoyan.service.impl
 * @ClassName: CaptchaServiceImpl
 * @Author: ruoyan1998
 * @CreateTime: 2021/12/13 8:33
 * @Description:
 */
@Service
public class CaptchaServiceImpl implements CaptchaService
{
    @Autowired
    Producer producer;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Result getCaptcha() throws IOException
    {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();

        // 测试数据
//        key = "aaaaa";
//        code = "11111";

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";

        String base64Img = str + encoder.encode(outputStream.toByteArray());

        //将验证码存入redis中，并设置有效时间为2分钟
        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);

        return Result.success(
                MapUtil.builder()
                        .put("token", key)
                        .put("captchaImage", base64Img)
                        .build()

        );
    }
}
