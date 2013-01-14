package de.dranke.aws.security;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class AwsSecurityPropertiesTest {

  @Test
  public void createFrom_resource_file_stream() throws IOException, InstantiationException {
    // given
    String workingProperties = "working.properties";

    // when
    AwsSecurityProperties properties = AwsSecurityProperties.createFrom(ClassLoader.getSystemResourceAsStream("working.properties"));

    // then
    assertThat(properties.getAwsAccessKey()).isNotEmpty();
    assertThat(properties.getAwsSecretKey()).isNotEmpty();
  }

  @Test
  public void createFrom_filename() throws IOException, InstantiationException {
    // given
    String workingProperties = "working.properties";

    // when
    AwsSecurityProperties properties = AwsSecurityProperties.createFrom(workingProperties);

    // then
    assertThat(properties.getAwsAccessKey()).isNotEmpty();
    assertThat(properties.getAwsSecretKey()).isNotEmpty();
  }

  @DataProvider
  public Object[][] invalidResourcePropertyFiles() {
    return new Object[][]{
        {"withoutAnyProperty.properties"},
        {"withoutAccessProperty.properties"},
        {"withoutSecretProperty.properties"}
    };
  }

  @Test(dataProvider = "invalidResourcePropertyFiles",expectedExceptions = InstantiationException.class)
  public void createFrom_throws_InstatiationException__when_any_property_is_missing(String resourceName) throws IOException, InstantiationException {
    // given

    // when
    AwsSecurityProperties.createFrom(ClassLoader.getSystemResourceAsStream(resourceName));

    // then throws exception
  }
}
