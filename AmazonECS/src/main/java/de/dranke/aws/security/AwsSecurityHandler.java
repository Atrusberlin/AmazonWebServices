package de.dranke.aws.security;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;

public class AwsSecurityHandler implements SOAPHandler<SOAPMessageContext> {

  private final SignRequestHelperSoapImpl signRequestHelperSoap;
  private final AwsSoapAction action;

  public AwsSecurityHandler(AwsSoapAction action, AwsSecurityProperties securityProperties) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
    this.action = action;
    this.signRequestHelperSoap = new SignRequestHelperSoapImpl(securityProperties.getAwsSecretKey(), securityProperties.getAwsAccessKey());
  }

  @Override
  public Set<QName> getHeaders() {
    return new TreeSet();
  }

  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    SOAPMessage soapMessage = context.getMessage();

    if (outboundProperty.booleanValue()) {
      String signature = signRequestHelperSoap.sign(action);
      try {
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPFactory factory = SOAPFactory.newInstance();
        String prefix = "aws";
        String namespace = "http://security.amazonaws.com/doc/2007-01-01/";

        SOAPHeader header = soapEnvelope.addHeader();
        header.addNamespaceDeclaration(prefix, namespace);


        SOAPElement keyElement = factory.createElement("AWSAccessKeyId", prefix, namespace);
        keyElement.addTextNode(signRequestHelperSoap.getAwsAccessKeyId());
        header.addChildElement(keyElement);

        SOAPElement timestampElement = factory.createElement("Timestamp", prefix, namespace);
        timestampElement.addTextNode(signRequestHelperSoap.lastTimeStamp);
        header.addChildElement(timestampElement);

        SOAPElement signatureElement = factory.createElement("Signature", prefix, namespace);
        signatureElement.addTextNode(signature);
        header.addChildElement(signatureElement);

        // other way to configure SOAP Header

        /*Name akidName = soapEnvelope.createName("AWSAccessKeyId", prefix, namespace);
        Name tsName = soapEnvelope.createName("Timestamp", prefix, namespace);
        Name sigName = soapEnvelope.createName("Signature", prefix, namespace);

        SOAPHeaderElement akidElement = header.addHeaderElement(akidName);
        SOAPHeaderElement tsElement = header.addHeaderElement(tsName);
        SOAPHeaderElement sigElement = header.addHeaderElement(sigName);

        akidElement.addTextNode(SignRequestHelperSoapImpl.awsAccessKeyId);
        tsElement.addTextNode(signRequestHelperSoap.getGeneratedTimestamp());
        sigElement.addTextNode(signature);*/

        return true;

      } catch (SOAPException e) {
        System.out.println("Exception in handler: " + e);
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean handleFault(SOAPMessageContext context) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public void close(MessageContext context) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
