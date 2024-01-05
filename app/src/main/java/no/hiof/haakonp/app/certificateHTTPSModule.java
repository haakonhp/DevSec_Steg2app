package no.hiof.haakonp.app;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class certificateHTTPSModule {
    public static HttpsURLConnection setupHTTPSConnection(String urlString, Context ViewContext, String trustedPort)
    {
        try
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = new BufferedInputStream(ViewContext.getAssets().open("datasikkerhet.crt"));
            Certificate localCertificate = cf.generateCertificate(caInput);
            System.out.println("localCertificate=" + ((X509Certificate) localCertificate).getSubjectDN());

            // Lager et keystore som inneholder vår inkluderte serfikat.
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("localCertificate", localCertificate);

            // Lager en trustManager som skal lagre keyStoren (med vårt serikat.)
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Vi får en SSL kontenkt der vi kan feste trustManageren.
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            // Lager til slutt en URL som benytter denne nye konteksten.
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            // Hostnavn må manuelt verifiseres fra Android API nivå 28+.
            urlConnection.setHostnameVerifier((hostname, session) -> {
                if (hostname.equalsIgnoreCase(trustedPort)) {
                    return true;
                } else {
                    return false;
                }
            });
            return urlConnection;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Feil under SSL tilkobling: " + ex);
            return null;
        }
    }
}
