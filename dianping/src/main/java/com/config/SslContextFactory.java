package com.config;

import android.content.Context;

import com.danertu.dianping.R;
import com.danertu.tools.Logger;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 作者:  Viz
 * 日期:  2018/8/10 17:08
 * <p>
 * 包名：com.config
 * 文件名：SslContextFactory
 * 描述：TODO
 */
public class SslContextFactory {

    private static final String CLIENT_TRUST_PASSWORD = "214808370420393";//信任证书密码
    private static final String CLIENT_AGREEMENT = "SSL";//使用协议
    private static final String CLIENT_TRUST_MANAGER = "X509";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";
    SSLContext sslContext = null;

//    public SSLContext getSslSocket(Context context) {
//        try {
////取得SSL的SSLContext实例
//            sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
////取得TrustManagerFactory的X509密钥管理器实例
//            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
////取得BKS密库实例
//            KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
//            InputStream is = context.getResources().openRawResource(R.raw.apikey);
//            try {
//                tks.load(is, CLIENT_TRUST_PASSWORD.toCharArray());
//            } finally {
//                is.close();
//            }
////初始化密钥管理器
//            trustManager.init(tks);
////初始化SSLContext
//            sslContext.init(null, trustManager.getTrustManagers(), null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.e("SslContextFactory", e.getMessage());
//        }
//        return sslContext;
//    }
}
