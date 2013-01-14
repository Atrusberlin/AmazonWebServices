#!/bin/sh
wsimport -verbose -s src/main/java-generated/ -p com.amazon.ecs.client.jax http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl -b aws_ecs_jaxb_config.xml