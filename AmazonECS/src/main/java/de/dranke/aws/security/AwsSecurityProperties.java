package de.dranke.aws.security;

import java.io.*;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AwsSecurityProperties {

  public static final String AWS_ACCESS_KEY_PROP = "accessKey";
  public static final String AWS_SECRET_KEY_PROP = "secretKey";
  private String awsAccessKey;
  private String awsSecretKey;

  public static AwsSecurityProperties createFrom(String file) throws IOException, InstantiationException {
    BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
    return createFrom(stream);
  }

  public static AwsSecurityProperties createFrom(InputStream propertiesFileStream) throws IOException, InstantiationException {
    AwsSecurityProperties properties = new AwsSecurityProperties();
    init(propertiesFileStream, properties);
    validate(properties);
    return properties;
  }

  private static void init(InputStream stream, AwsSecurityProperties awsSecurityProperties) throws IOException {
    Properties properties = new Properties();
    properties.load(stream);
    awsSecurityProperties.awsAccessKey = properties.getProperty(AWS_ACCESS_KEY_PROP);
    awsSecurityProperties.awsSecretKey = properties.getProperty(AWS_SECRET_KEY_PROP);
    stream.close();
  }

  private static void validate(AwsSecurityProperties properties) throws InstantiationException {
    StringBuilder errorMessages = new StringBuilder();
    if (isEmpty(properties.getAwsAccessKey())) {
      errorMessages.append(createErrorMessage(AWS_ACCESS_KEY_PROP));
    }
    if (isEmpty(properties.getAwsSecretKey())) {
      errorMessages.append(createErrorMessage(AWS_SECRET_KEY_PROP));
    }

    if (errorMessages.length() > 0) {
      throw new InstantiationException(errorMessages.toString());
    }
  }

  private static String createErrorMessage(String propertyName) {
    return String.format("Property '%s' was not found!", propertyName);
  }

  public String getAwsAccessKey() {
    return awsAccessKey;
  }

  public String getAwsSecretKey() {
    return awsSecretKey;
  }
}
