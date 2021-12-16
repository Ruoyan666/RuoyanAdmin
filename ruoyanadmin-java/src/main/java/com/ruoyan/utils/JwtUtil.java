package com.ruoyan.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

/**
 * @Package: com.ruoyan.utils
 * @ClassName: JwtUtil
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/25 10:31
 * @Description: jwt工具类
 */
@Data
@ConfigurationProperties(prefix = "ruoyan.jwt")
@Component
public class JwtUtil
{
    /**
     * 过期时间
     */
    private long expire;

    /**
     * 密钥
     */
    private String secret;

    /**
     * jwt头部名称
     */
    private String header;


    /**
     * 生成jwt
     * @param username
     * @return jwtSecret
     */
    public String generateToken(String username)
    {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * expire);


        return Jwts.builder()
                //设置头部类型
                .setHeaderParam("typ","JWT")
                //设置应用主体
                .setSubject(username)
                //设置创建时间
                .setIssuedAt(nowDate)
                //设置过期时间
                .setExpiration(expireDate)
                //进行加密算法签名
                .signWith(SignatureAlgorithm.HS256,secret)
                //合成jwt
                .compact();
    }

    /**
     * 解析jwt
     * @param jwt
     * @return Claims
     */
    public Claims getClaimByToken(String jwt)
    {
        try
        {
            return Jwts.parser()
                    //设置密钥key
                    .setSigningKey(secret)
                    //解析jwt信息
                    .parseClaimsJws(jwt)
                    //拿到jwt中的body部分
                    .getBody();
        }
        catch (ExpiredJwtException e)
        {
            return null;
        }
    }


    //判定jwt是否过期
    public boolean isTokenExpired(Claims claims)
    {
        return claims.getExpiration().before(new Date());
    }
}
