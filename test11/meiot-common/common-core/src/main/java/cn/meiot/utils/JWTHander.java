package cn.meiot.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.*;

/**
 * token工具
 */
public class JWTHander {

    public String createToken() {
        try {
            String secret = "secret";//token ��Կ
            Algorithm algorithm = Algorithm.HMAC256("secret");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("alg", "HS256");
            map.put("typ", "JWT");

            Date nowDate = new Date();
            Date expireDate = getAfterDate(nowDate, 0, 0, 0, 2, 0, 0);
            String token = JWT.create()
                    .withHeader(map)
                    .withIssuer("SERVICE")
                    .withSubject("this is test token")
                    //.withNotBefore(new Date())
                    .withAudience("APP")
                    .withIssuedAt(nowDate)
                    .withExpiresAt(expireDate)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String createTokenWithClaim(Integer userId) {

        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            Map<String, Object> map = new HashMap<String, Object>();
            Date nowDate = new Date();
            Date expireDate = getAfterDate(nowDate, 0, 0, 0, 2, 0, 0);
            map.put("alg", "HS256");
            map.put("typ", "JWT");
            String token = JWT.create()
                    .withHeader(map)
                    .withClaim("id", userId)
                    .withIssuer("SERVICE")
                    .withSubject("this is  token")
                    //.withNotBefore(new Date())
                    .withAudience("APP")
                    .withIssuedAt(nowDate)
                    .withExpiresAt(expireDate)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     *   返回一定时间后的日期
     *  @param date 开始计时的时间
     *  @param year 增加的年
     *   @param month 增加的月
     * @param day 增加的日
     *  @param hour 增加的小时
     *  @param minute 增加的分钟
     *  @param second 增加的秒
     *  @return
     *         
     */
    public Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        if (year != 0) {
            cal.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (hour != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hour);
        }
        if (minute != 0) {
            cal.add(Calendar.MINUTE, minute);
        }
        if (second != 0) {
            cal.add(Calendar.SECOND, second);
        }
        return cal.getTime();
    }

    public void verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("SERVICE")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            String subject = jwt.getSubject();
            Map<String, Claim> claims = jwt.getClaims();
            Claim claim = claims.get("id");
            System.out.println(claim.asString());
            List<String> audience = jwt.getAudience();
            System.out.println(subject);
            System.out.println(audience.get(0));
        } catch (JWTVerificationException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JWTHander demo = new JWTHander();
        //String createToken = demo.createToken();
        String createTokenWithClaim = demo.createTokenWithClaim(1);
        System.out.println("返回结果");
        System.out.println(createTokenWithClaim);
        demo.verifyToken("123.123.123");

    }
}