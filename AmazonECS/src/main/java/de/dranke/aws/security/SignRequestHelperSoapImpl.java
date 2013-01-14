package de.dranke.aws.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

class SignRequestHelperSoapImpl extends SignRequestHelperAbstract {

  public SignRequestHelperSoapImpl(String awsSecretKey, String awsAccessKeyId) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
    super(awsSecretKey, awsAccessKeyId);
  }

  public String sign(AwsSoapAction action) {
    String toSign = action.getValue() + timestamp();
    return hmac(toSign);
  }

  public String getGeneratedTimestamp() {
    return lastTimeStamp;
  }

}
