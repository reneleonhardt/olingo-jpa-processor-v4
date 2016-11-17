package com.sap.olingo.jpa.processor.core.processor;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.OData;

import com.sap.olingo.jpa.processor.core.api.JPAODataRequestContextAccess;
import com.sap.olingo.jpa.processor.core.api.JPAODataSessionContextAccess;

abstract class JPAAbstractGetRequestProcessor extends JPAAbstractRequestProcessor implements JPARequestProcessor {

  public JPAAbstractGetRequestProcessor(OData odata, JPAODataSessionContextAccess context,
      JPAODataRequestContextAccess requestContext) throws ODataException {
    super(odata, context, requestContext);
  }

}
