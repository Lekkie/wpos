package com.avantir.wpos.network;

import android.content.Context;
import com.avantir.wpos.utils.ResourceUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by lekanomotayo on 12/02/2018.
 */
public class SSLContextBuild {

    public SSLContextBuild() {
    }

    public static SSLContext getSSLContext(Context mContext, String cer) {
        SSLContext sslContext = null;
        Security.addProvider(new BouncyCastleProvider());

        try {
            KeyStore ksKeys = KeyStore.getInstance("BKS");
            ksKeys.load((InputStream)null, (char[])null);
            InputStream e = mContext.getAssets().open(cer);
            String[] cerStrings = cer.split(".");
            InputStream rawStream = null;
            if(cerStrings != null && cerStrings.length > 1) {
                rawStream = mContext.getResources().openRawResource(ResourceUtil.getIdByName(mContext, "raw", cerStrings[0]));
            }

            PEMReader cacertfile = null;
            if(rawStream != null) {
                cacertfile = new PEMReader(new InputStreamReader(rawStream));
            } else {
                if(e == null) {
                    //LogUtil.si(SSLSocketConnection.class, "Unable to get SSLcontext,The certificate is not prevented.");
                    return null;
                }

                cacertfile = new PEMReader(new InputStreamReader(e));
            }

            X509Certificate cacert = (X509Certificate)cacertfile.readObject();
            cacertfile.close();
            KeyStore.TrustedCertificateEntry trustedEntry = new KeyStore.TrustedCertificateEntry(cacert);
            ksKeys.setEntry("ca_root", trustedEntry, (KeyStore.ProtectionParameter)null);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ksKeys);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    if(chain == null) {
                        throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                    } else if(chain.length <= 0) {
                        throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                    } else if(authType == null || !authType.equalsIgnoreCase("RSA")) {
                        throw new CertificateException("checkServerTrusted: AuthType is not RSA");
                    }
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (KeyStoreException var11) {
            var11.printStackTrace();
        } catch (NoSuchAlgorithmException var12) {
            var12.printStackTrace();
        } catch (CertificateException var13) {
            var13.printStackTrace();
        } catch (IOException var14) {
            var14.printStackTrace();
        } catch (KeyManagementException var15) {
            var15.printStackTrace();
        }

        return sslContext;
    }

    public static SSLContext getTLSContext(Context mContext, String cer) throws Exception {
        BufferedInputStream trustCertIS = null;
        TrustManager[] tm = null;
        trustCertIS = new BufferedInputStream(mContext.getAssets().open(cer));
        KeyStore ts = KeyStore.getInstance("BKS");
        ts.load(trustCertIS, (char[])null);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(ts);
        tm = tmf.getTrustManagers();
        SSLContext sslCtx = SSLContext.getInstance("TLSv1.2");
        sslCtx.init((KeyManager[])null, tm, (SecureRandom)null);
        return sslCtx;
    }

    public static SSLContext trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        SSLContextBuild.miTM tm = new SSLContextBuild.miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init((KeyManager[])null, trustAllCerts, (SecureRandom)null);
        return sc;
    }

    static class miTM implements TrustManager, X509TrustManager {
        miTM() {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }

}
