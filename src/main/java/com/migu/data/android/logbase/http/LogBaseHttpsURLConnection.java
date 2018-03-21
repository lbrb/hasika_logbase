package com.migu.data.android.logbase.http;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by hasika on 2017/10/26.
 * https网络模块
 */

public class LogBaseHttpsURLConnection extends AbsLogBaseHttpConnection {

    @Override
    protected HttpURLConnection createUrlConnection(String url) {
        HttpsURLConnection connection = null;
        try {
            URL _url = new URL(url);
            connection = (HttpsURLConnection) _url.openConnection();
            connection.setSSLSocketFactory(getSsf());
            connection.setHostnameVerifier(getHv());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private SSLSocketFactory getSsf() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] tms = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    //客户端不做校验
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    if (chain == null) {
                        throw new IllegalArgumentException("checkServerTrusted is null");
                    }

                    if (chain.length != 2) {
                        throw new IllegalArgumentException("checkServerTrusted length is error");
                    }

                    try {
                        for (X509Certificate cert : chain) {
                            cert.checkValidity();
                        }
                    } catch (CertificateExpiredException e) {
                        e.printStackTrace();
                    } catch (CertificateNotYetValidException e) {
                        e.printStackTrace();
                    }

                    X509Certificate lastCert = chain[chain.length -1];

                    if (selfCheckCert(lastCert)) {
                        throw new CertificateException("root cert is invalid");
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tms, null);
        return sslContext.getSocketFactory();
    }

    private HostnameVerifier getHv() {

        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                return hostnameVerifier.verify("*.cmgame.com", session);
            }
        };
    }

    private boolean selfCheckCert(X509Certificate cert) {
        String name = cert.getIssuerDN().getName();
        String[] CAs =new String[]{"VeriSign", "GlobalSign", "DigiCert","GlobalSign","Google","CAcert","Comodo"};
        for (String str : CAs) {
            name = name.toLowerCase();
            str = str.toLowerCase();
            if (name.contains(str)) {
                return false;
            }
        }

        return true;
    }
}
