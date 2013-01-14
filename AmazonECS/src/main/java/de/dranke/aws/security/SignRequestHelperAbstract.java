package de.dranke.aws.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

abstract class SignRequestHelperAbstract {
  protected static final String UTF8_CHARSET = "UTF-8";
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

  private final String awsAccessKeyId;
  private final String awsSecretKey;

  private SecretKeySpec secretKeySpec = null;
  private Mac mac = null;

  String lastTimeStamp = null;

  public SignRequestHelperAbstract(String awsSecretKey, String awsAccessKeyId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    this.awsSecretKey = awsSecretKey;
    this.awsAccessKeyId = awsAccessKeyId;

    byte[] secretyKeyBytes = this.awsSecretKey.getBytes(UTF8_CHARSET);
    secretKeySpec =
        new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
    mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
    mac.init(secretKeySpec);
  }

  protected String getAwsAccessKeyId() {
    return awsAccessKeyId;
  }

  protected String hmac(String stringToSign) {
    String signature = null;
    byte[] data;
    byte[] rawHmac;
    try {
      data = stringToSign.getBytes(UTF8_CHARSET);
      rawHmac = mac.doFinal(data);
      Base64 encoder = new Base64();
      signature = new String(encoder.encode(rawHmac));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
    }
    return signature;
  }

  protected String timestamp() {
    String timestamp = null;
    Calendar cal = Calendar.getInstance();
    DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
    timestamp = dfm.format(cal.getTime());
    lastTimeStamp = timestamp;
    return timestamp;
  }


}