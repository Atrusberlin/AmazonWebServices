package de.dranke.aws.security;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Envelope1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import static de.dranke.aws.security.AwsSoapAction.ITEM_SEARCH;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class AwsSecurityHandlerTest {

  private AwsSecurityProperties securityProperties;

  @BeforeClass
  public void init() throws Exception {
    securityProperties = AwsSecurityProperties.createFrom("AwsCredentials.properties");
  }

  @Test
  public void addSoapHeader_for_authentication() throws Exception {
    // givengit
    SOAPHandler handler = new AwsSecurityHandler(ITEM_SEARCH, securityProperties);
    SOAPMessageContext contextMock = mock(SOAPMessageContext.class);
    SOAPMessage messageMock = mock(SOAPMessage.class, RETURNS_DEEP_STUBS);

    SOAPPart1_1Impl enclosingDocument = new SOAPPart1_1Impl();
    SOAPEnvelope envelope = new Envelope1_1Impl((new SOAPDocumentImpl(enclosingDocument)), "soapenv");
//    SOAPEnvelope envelope = new Envelope1_2Impl(new SOAPDocumentImpl(enclosingDocument), "soapenv", false, true);

    given(contextMock.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)).willReturn(Boolean.TRUE);
    given(contextMock.getMessage()).willReturn(messageMock);
    given(messageMock.getSOAPPart()).willReturn(enclosingDocument);

    // when
    handler.handleMessage(contextMock);

    // then
    assertThat(envelope.getHeader()).isNotNull();
    assertThat(envelope.getHeader().getChildNodes().item(0).getLocalName()).isEqualTo("AWSAccessKeyId");
    assertThat(envelope.getHeader().getChildNodes().item(1).getLocalName()).isEqualTo("Timestamp");
    assertThat(envelope.getHeader().getChildNodes().item(2).getLocalName()).isEqualTo("Signature");
  }
}
