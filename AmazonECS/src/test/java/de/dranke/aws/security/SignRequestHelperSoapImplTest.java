package de.dranke.aws.security;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static de.dranke.aws.security.AwsSoapAction.ITEM_SEARCH;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SignRequestHelperSoapImplTest {

  Logger LOG = Logger.getLogger(SignRequestHelperSoapImplTest.class);
  private AwsSecurityProperties securityProperties;

  @BeforeClass
  public void init() throws Exception {
    securityProperties = AwsSecurityProperties.createFrom("AwsCredentials.properties");
  }

  @Test
  public void createNewSignature() throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
    // given
    SignRequestHelperSoapImpl signRequestHelper = new SignRequestHelperSoapImpl(securityProperties.getAwsSecretKey(), securityProperties.getAwsAccessKey());

    // when
    String signature = signRequestHelper.sign(ITEM_SEARCH);

    // then
    LOG.debug("Signed String: " + signature);
    LOG.debug(String.format("\n<aws:Timestamp>%s</aws:Timestamp>\n" +
        "<aws:Signature>%s</aws:Signature>",
        signRequestHelper.getGeneratedTimestamp(), signature));
    assertThat(signature).isNotEmpty();


  }
}
