package cn.amazon.aws.rp.spapi.utils;

import cn.amazon.aws.rp.spapi.documents.CompressionAlgorithm;
import cn.amazon.aws.rp.spapi.documents.DownloadBundle;
import cn.amazon.aws.rp.spapi.documents.DownloadHelper;
import cn.amazon.aws.rp.spapi.documents.DownloadSpecification;
import cn.amazon.aws.rp.spapi.documents.exception.CryptoException;
import cn.amazon.aws.rp.spapi.documents.exception.HttpResponseException;
import cn.amazon.aws.rp.spapi.documents.exception.MissingCharsetException;
import cn.amazon.aws.rp.spapi.documents.impl.AESCryptoStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;



public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getEnv(String name){
        return System.getenv(name);
    }

    public static String getEnv(String name, String value) {
        String val = System.getenv(name);
        return val == null ? value : val;
    }

    public static <T> boolean isNullOrEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    final static DownloadHelper downloadHelper = new DownloadHelper.Builder().build();

    // The key, initializationVector, url, and compressionAlgorithm are obtained from the response to
    // the getReportDocument operation.
    public static void downloadAndDecrypt(String key, String initializationVector, String url, String compressionAlgorithm) {
        AESCryptoStreamFactory aesCryptoStreamFactory =
                new AESCryptoStreamFactory.Builder(key, initializationVector).build();

        DownloadSpecification downloadSpec = new DownloadSpecification.Builder(aesCryptoStreamFactory, url)
                .withCompressionAlgorithm(CompressionAlgorithm.fromEquivalent(compressionAlgorithm))
                .build();

        try (DownloadBundle downloadBundle = downloadHelper.download(downloadSpec)) {
            // This example assumes that the downloaded file has a charset in the content type, e.g.
            // text/plain; charset=UTF-8
            try (BufferedReader reader = downloadBundle.newBufferedReader()) {
                String line;
                do {
                    line = reader.readLine();
                    System.out.println(line);
                    // Process the decrypted line.
                } while (line != null);
            }
        }
        catch (CryptoException | HttpResponseException | IOException | MissingCharsetException  e) {
            logger.error("downloadAndDecrypt Error",e);
            throw new RuntimeException(e);
        }
    }
}
