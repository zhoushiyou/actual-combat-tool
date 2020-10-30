package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName AlipayConfig
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/22
 * @Version V1.0
 **/
public class AlipayConfig {


    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766862";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCiPiuwJutXQYpv8xt72QAY6/mKDCVu5Zabrane4LELg0hzZdVCo/3D5QXYSWQ+6tRxAZF4eqcO88mh4T6SYaBvnnUkjc7Q971cMsW9yb7SE7IqqUsffcS7lwvL5uyHYxGY4TTO/qWxq+V0V+qV7TJ/P2WRAhdfs49bLnFr/CbnWaiuWcdkFl+Ds/RQYFptyqwh3z9XY46jrhwEY6pUkrDSh+nCzkusbvuWXUu69VRtormydScvyulpsZLRQZa/x+IivkRavQpRGRIG56FGC+0B+pTyX1NwSDoeb0d+jotgczX1xJndez7nkKO/UVm+NMsJ3ypko/POjY9LxXXnF/BfAgMBAAECggEAXLdCqCvVmcuunUNElYXw9E0GEcnXKoSxiM25odCt3HJt2OR89tRYlLYwnOV0EDCKrvnCdCNDjNvsgkICTAzWbnMTZoxTgD/TSvkI3+iGz+7Jhm+wqpJo9vreluukh8opqEAquY/0iJA/VDlZqcwfa3CDTlbvnJU0e1sTHi6+3L4K8pzF2cz/raJi7+fIQ21wRPaymLt2YzSmYXpTqRVL/TXysviyFnJSaDx0KFI35Y9aowDksm4MYZgQCOzyyyxeI4P7/esBcLvsr2eZMxS/13Y5qF866l3AO9S5QQdsQ6NfDEIOP4cJlQp44k5cJ2qwObqUA3Ckg8Os3GFc3Uc/wQKBgQDUWzQM/1bKgYTmjkC+Bc5TommWnGVByyKt86fZopEe1p5cIYdJA6vDwZKQWZUOYG08AEdMzaLBByPE1NR32l1zHxeNopDZBWB8HFl3Wg3p+anD3HyFybu+4iRJ/txDcou4YnMdBeFg80a/fuKUpjA1AeLY6ZxEtzKSl35esqCllwKBgQDDllPnrlwoXLKsMAS0ouU3euaOS4Hf0QFC/Nn3HUnMsmCZdm/oAcjDOpe8r/6UlBArFKrAd9juA1HjTkfNHczMzAYVt/zzV2L4Gs+5lX9KZI1l8W72KPbEnBdxm1yOVywSiQ7BwnwHmFaXopEtHeB/XLLB3k2YzhQV/q+++5E0eQKBgQC1XqgYKcVQ9Eo0OG8nXQIaAG86SZ1aEksuUxqbEPPtaNScT66iN9ZuPVgWncxnGQhy5LIA8Oj/AEAWjshhDUofVwDg4WXhVWxQ1E29WFbl7WyHcjuQcPo/UBo6cMhjeuPGuZ8jvLAABAf66j5PQ5mMzG2ZYf0r22QAPA+chegbRQKBgQC7yTq4B91t10qpLdVmxbvEH9j7KB4DEX3fnjt8Gn+/7Ef5MBDTtdrXLwt+249Xgi1f35DuCGOcak2WK5OeqYz32K/C7IsN7IoWYk+tBI9Zkt970GnUKJJ9RMzxg9RCygmDplonyJ4jAK2rGye+JKBmSE9M+i6QltQMJiMBADJzKQKBgQC+y/BlzDtH+R7A7mVzsHrH/9SZPhhlPrOTNumPWz92SdgZze0eCQuiSzODde2U8bhYW7BAqvPogYkMOviUqRJCXuzanFnapHFF8SR9OIAOt2Y0/0D3jMZd+4bmHMc047fyMMN2EgqeDWkXO8W4D7DZhx1FGkiSlk1+3Hv55znbeg==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjodxk7JO5Or00OREqspQ2IL8sIKnjULugWzt41VzME7dg2+d/YO34kkqFAyy7i6GCNCvumi5UixpXRUQKCP2w0sJ2arvUDCBig4SVGiazvSkgHyLmYzk/1qc8bMtRu8ShKMUKk80I5iNNoxmne0Setg/Kj2f5FFsHLnI0r2xypVhwKKzVp12TY0pdAZcJvgntHxj9KdAEsdA0bmt6h32MiIgylFJ+5KJcvV6U/E1NWA4DghlWN655mOoYiG6QNjaZFyRW5iRTU7m6iJfwGcQhH1sefbxfd4HMx32KeH7vGTjEw5Q3tIv5VWXF2rV7H1dnVfWEQLNo8f/YYn9SelE8wIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnURL";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "D:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
