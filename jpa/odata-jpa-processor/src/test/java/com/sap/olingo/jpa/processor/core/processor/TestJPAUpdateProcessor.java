package com.sap.olingo.jpa.processor.core.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.junit.Test;
import org.mockito.Matchers;

import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAEntityType;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAStructuredType;
import com.sap.olingo.jpa.metadata.core.edm.mapper.exception.ODataJPAModelException;
import com.sap.olingo.jpa.processor.core.api.JPAAbstractCUDRequestHandler;
import com.sap.olingo.jpa.processor.core.exception.ODataJPAProcessException;
import com.sap.olingo.jpa.processor.core.exception.ODataJPAProcessorException;
import com.sap.olingo.jpa.processor.core.modify.JPAUpdateResult;
import com.sap.olingo.jpa.processor.core.testmodel.Organization;

public class TestJPAUpdateProcessor extends TestJPAModifyProcessor {

  @Test
  public void testHockIsCalled() throws ODataJPAModelException, ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    RequestHandleSpy spy = new RequestHandleSpy();
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertTrue(spy.called);
  }

  @Test
  public void testHttpMethodProvided() throws ODataJPAModelException, ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);

    RequestHandleSpy spy = new RequestHandleSpy();
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(HttpMethod.PATCH, spy.method);
  }

  @Test
  public void testEntityTypeProvided() throws ODataJPAModelException, ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);

    RequestHandleSpy spy = new RequestHandleSpy();
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertTrue(spy.et instanceof JPAEntityType);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testJPAAttributes() throws ODataJPAModelException, ODataException, UnsupportedEncodingException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);

    RequestHandleSpy spy = new RequestHandleSpy();
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    InputStream is = new ByteArrayInputStream("{\"ID\" : \"35\", \"Country\" : \"USA\"}".getBytes("UTF-8"));
    when(request.getBody()).thenReturn(is);
    Map<String, Object> jpaAttributes = new HashMap<String, Object>();
    jpaAttributes.put("id", "35");
    jpaAttributes.put("country", "USA");
    when(helper.convertProperties(Matchers.any(OData.class), Matchers.any(JPAStructuredType.class), Matchers.any(
        List.class))).thenReturn(jpaAttributes);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(2, spy.jpaAttributes.size());
  }

  @Test
  public void testMinimalResponseUpdateStatusCode() throws ODataJPAProcessorException, SerializerException,
      ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);
    RequestHandleSpy spy = new RequestHandleSpy(new JPAUpdateResult(false, new Organization()));
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testMinimalResponseCreatedStatusCode() throws ODataJPAProcessorException, SerializerException,
      ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);
    RequestHandleSpy spy = new RequestHandleSpy(new JPAUpdateResult(true, new Organization()));
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testMinimalResponseUpdatePreferHeader() throws ODataJPAProcessorException, SerializerException,
      ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);
    RequestHandleSpy spy = new RequestHandleSpy(new JPAUpdateResult(false, new Organization()));
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(PREFERENCE_APPLIED, response.getHeader(HttpHeader.PREFERENCE_APPLIED));
  }

  @Test
  public void testMinimalResponseCreatedPreferHeader() throws ODataJPAProcessorException, SerializerException,
      ODataException {
    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareSimpleRequest();

    when(request.getMethod()).thenReturn(HttpMethod.PATCH);
    RequestHandleSpy spy = new RequestHandleSpy(new JPAUpdateResult(true, new Organization()));
    when(sessionContext.getCUDRequestHandler()).thenReturn(spy);

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(PREFERENCE_APPLIED, response.getHeader(HttpHeader.PREFERENCE_APPLIED));
  }

  @Test
  public void testRepresentationResponseUpdatedStatusCode() throws ODataJPAProcessorException, SerializerException,
      ODataException {

    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareRepresentationRequest(new RequestHandleSpy(new JPAUpdateResult(false,
        new Organization())));

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testRepresentationResponseCreatedStatusCode() throws ODataJPAProcessorException, SerializerException,
      ODataException {

    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareRepresentationRequest(new RequestHandleSpy(new JPAUpdateResult(true,
        new Organization())));

    processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testRepresentationResponseUpdatedErrorMissingEntity() throws ODataJPAProcessorException, ODataException {

    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareRepresentationRequest(new RequestHandleSpy(new JPAUpdateResult(false, null)));

    try {
      processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);
    } catch (ODataJPAProcessException e) {
      assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), e.getStatusCode());
      return;
    }
    fail();
  }

  @Test
  public void testRepresentationResponseCreatedErrorMissingEntity() throws ODataJPAProcessorException, ODataException {

    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareRepresentationRequest(new RequestHandleSpy(new JPAUpdateResult(true, null)));

    try {
      processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);
    } catch (ODataJPAProcessException e) {
      assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), e.getStatusCode());
      return;
    }
    fail();
  }

  @Test
  public void testResponseErrorIfNull() throws ODataJPAProcessorException, ODataException {

    ODataResponse response = new ODataResponse();
    ODataRequest request = prepareRepresentationRequest(new RequestHandleSpy(null));

    try {
      processor.updateEntity(request, response, ContentType.JSON, ContentType.JSON);
    } catch (ODataJPAProcessException e) {
      assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), e.getStatusCode());
      return;
    }
    fail();
  }

  class RequestHandleSpy extends JPAAbstractCUDRequestHandler {
    public JPAEntityType et;
    public Map<String, Object> jpaAttributes;
    public EntityManager em;
    public boolean called = false;
    public HttpMethod method;
    private final JPAUpdateResult change;

    RequestHandleSpy() {
      this(new JPAUpdateResult(true, new Organization()));
    }

    RequestHandleSpy(JPAUpdateResult typeOfChange) {
      this.change = typeOfChange;
    }

    @Override
    public JPAUpdateResult updateEntity(JPAEntityType et, Map<String, Object> jpaAttributes, Map<String, Object> keys,
        EntityManager em, HttpMethod method) throws ODataJPAProcessException {
      this.et = et;
      this.jpaAttributes = jpaAttributes;
      this.em = em;
      this.called = true;
      this.method = method;
      return change;
    }

  }
}
