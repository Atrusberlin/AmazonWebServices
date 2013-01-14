package de.dranke.aws.security;

/**
* Created with IntelliJ IDEA.
* User: daniel
* Date: 23.12.12
* Time: 23:24
* To change this template use File | Settings | File Templates.
*/
public enum AwsSoapAction {
    ITEM_SEARCH("ItemSearch");

    private String value;

    AwsSoapAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
