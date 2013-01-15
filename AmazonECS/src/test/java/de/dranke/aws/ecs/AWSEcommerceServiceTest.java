package de.dranke.aws.ecs;

import com.amazon.ecs.client.jax.*;
import com.sun.deploy.util.StringUtils;
import de.dranke.aws.security.AwsSecurityHandler;
import de.dranke.aws.security.AwsSecurityProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.TransformerUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.dranke.aws.security.AwsSoapAction.ITEM_SEARCH;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;

/**
 * User: Daniel
 * Date: 16.08.12
 */
public class AWSEcommerceServiceTest {

  private static final Logger LOG = Logger.getLogger(AWSEcommerceServiceTest.class);
  private AWSECommerceService service;
  private AWSECommerceServicePortType port;

  private AwsSecurityProperties securityProperties;

  @BeforeClass
  public void init() throws Exception {
    securityProperties = AwsSecurityProperties.createFrom("AwsCredentials.properties");
  }

  @BeforeMethod
  protected void setUp() throws Exception {
    service = new AWSECommerceService();
    service.setHandlerResolver(new HandlerResolver() {
      @Override
      public List<Handler> getHandlerChain(PortInfo portInfo) {
        ArrayList<Handler> handler = new ArrayList<>();
        try {
          handler.add(new AwsSecurityHandler(ITEM_SEARCH, securityProperties));
          return handler;
        } catch (InvalidKeyException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
      }
    });
    port = service.getAWSECommerceServicePortDE();
  }

  @Test
  public void searchItems_liefert_Ergebnisse_mit_setShared() {
    // given
    ItemSearchRequest itemRequest = new ItemSearchRequest();
    itemRequest.setSearchIndex("Books");
    itemRequest.setKeywords("Asimov");

    ItemSearch itemElement = new ItemSearch();
    itemElement.setAssociateTag("dranke");
    itemElement.getRequest().add(itemRequest);


    // when
    ItemSearchResponse response = port.itemSearch(itemElement);

    // then
    assertThat(response.getItems()).isNotEmpty();

    for (Items item : response.getItems()) {
      LOG.debug(String.format("Items gefunden: %d", item.getTotalResults()));
      int itemCount = 0;
      for (Item resultItem : item.getItem()) {
        LOG.debug(String.format("Item content %d: %s", ++itemCount, itemContent(resultItem)));

      }
    }
  }

  private String itemContent(Item resultItem) {
    StringBuilder builder = new StringBuilder();
    append("ASIN", resultItem.getASIN(), builder);
//    append(resultItem.getDetailPageURL(), "DetailPage", builder);
    if (resultItem.getOfferSummary() != null) {
      append("TotalCollectible", resultItem.getOfferSummary().getTotalCollectible(), builder);
    }
    append("ItemAttributes", EMPTY, builder);
    append(resultItem.getItemAttributes(), builder);

    return builder.toString();
  }

  private void append(ItemAttributes itemAttributes, StringBuilder builder) {
    builder.append("\n\t\t\t" + "Title : " + itemAttributes.getTitle());
    builder.append("\n\t\t\t" + "Author : " + StringUtils.join(itemAttributes.getAuthor(), ", "));
    builder.append("\n\t\t\t" + "Categories : " + StringUtils.join(itemAttributes.getCategory(), ", "));
    Collection transformCollection = CollectionUtils.transformedCollection(new ArrayList(), new CreatorTransformer());
    transformCollection.addAll(itemAttributes.getCreator());
    builder.append("\n\t\t\t" + "Creator : " + StringUtils.join(transformCollection, ", "));
    builder.append("\n\t\t\t" + "ISBN : " + itemAttributes.getISBN());

    if (itemAttributes.getListPrice() != null) {
      builder.append("\n\t\t\t" + "List price : " + itemAttributes.getListPrice().getFormattedPrice());
    }
  }

  private void append(String field, String value, StringBuilder builder) {
    builder.append("\n\t\t" + field + " : " + value);
  }

  class CreatorTransformer implements Transformer {

    @Override
    public Object transform(Object object) {
      if (object instanceof ItemAttributes.Creator) {
        ItemAttributes.Creator creator = (ItemAttributes.Creator) object;
        return creator.getValue() + " (Role: " + creator.getRole() + ")";
      }
      return null;
    }
  }

}
