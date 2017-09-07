package com.sap.olingo.jpa.metadata.core.edm.mapper.impl;

import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.Attribute;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;

import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmGeospatial;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAAttribute;
import com.sap.olingo.jpa.metadata.core.edm.mapper.exception.ODataJPAModelException;

/**
 * This class holds utility methods for type conversions between JPA Java types and OData Types.
 * 
 */
public final class JPATypeConvertor {

  private JPATypeConvertor() {}

  public static EdmPrimitiveTypeKind convertToEdmSimpleType(final Class<?> type) throws ODataJPAModelException {
    return convertToEdmSimpleType(type, null);
  }

  /**
   * This utility method converts a given jpa Type to equivalent EdmPrimitiveTypeKind for maintaining compatibility
   * between Java and OData Types.
   * 
   * @param jpaType
   * The JPA Type input.
   * @return The corresponding EdmPrimitiveTypeKind.
   * @throws ODataJPAModelException
   * @throws org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException
   * 
   * @see EdmPrimitiveTypeKind
   */

  public static EdmPrimitiveTypeKind convertToEdmSimpleType(final Class<?> jpaType,
      final Attribute<?, ?> currentAttribute) throws ODataJPAModelException {
    if (jpaType.equals(String.class) || jpaType.equals(Character.class) || jpaType.equals(char.class) || jpaType.equals(
        char[].class) || jpaType.equals(Character[].class)) {
      return EdmPrimitiveTypeKind.String;
    } else if (jpaType.equals(Long.class) || jpaType.equals(long.class)) {
      return EdmPrimitiveTypeKind.Int64;
    } else if (jpaType.equals(Short.class) || jpaType.equals(short.class)) {
      return EdmPrimitiveTypeKind.Int16;
    } else if (jpaType.equals(Integer.class) || jpaType.equals(int.class)) {
      return EdmPrimitiveTypeKind.Int32;
    } else if (jpaType.equals(Double.class) || jpaType.equals(double.class)) {
      return EdmPrimitiveTypeKind.Double;
    } else if (jpaType.equals(Float.class) || jpaType.equals(float.class)) {
      return EdmPrimitiveTypeKind.Single;
    } else if (jpaType.equals(BigDecimal.class)) {
      return EdmPrimitiveTypeKind.Decimal;
    } else if (jpaType.equals(byte[].class)) {
      return EdmPrimitiveTypeKind.Binary;
    } else if (jpaType.equals(Byte.class) || jpaType.equals(byte.class)) {
      return EdmPrimitiveTypeKind.SByte;
    } else if (jpaType.equals(Boolean.class) || jpaType.equals(boolean.class)) {
      return EdmPrimitiveTypeKind.Boolean;
    } else if (jpaType.equals(java.time.LocalTime.class) || jpaType.equals(java.sql.Time.class)) {
      return EdmPrimitiveTypeKind.TimeOfDay;
    } else if (jpaType.equals(java.time.Duration.class)) {
      return EdmPrimitiveTypeKind.Duration;
    } else if (jpaType.equals(java.time.LocalDate.class) || jpaType.equals(java.sql.Date.class)) {
      return EdmPrimitiveTypeKind.Date;
    } else if (jpaType.equals(Calendar.class) || jpaType.equals(Timestamp.class) || jpaType.equals(
        java.util.Date.class)) {
      if ((currentAttribute != null) && (determineTemporalType(currentAttribute) == TemporalType.TIME)) {
        return EdmPrimitiveTypeKind.TimeOfDay;
      } else if ((currentAttribute != null) && (determineTemporalType(currentAttribute) == TemporalType.DATE)) {
        return EdmPrimitiveTypeKind.Date;
      } else {
        return EdmPrimitiveTypeKind.DateTimeOffset;
      }
    } else if (jpaType.equals(UUID.class)) {
      return EdmPrimitiveTypeKind.Guid;
    } else if (jpaType.equals(Byte[].class)) {
      return EdmPrimitiveTypeKind.Binary;
    } else if (jpaType.equals(Blob.class) && isBlob(currentAttribute)) {
      return EdmPrimitiveTypeKind.Binary;
    } else if (jpaType.equals(Clob.class) && isBlob(currentAttribute)) {
      return EdmPrimitiveTypeKind.String;
    } else if (isGeography(currentAttribute)) {
      return convertGeography(jpaType, currentAttribute);
    } else if (isGeometry(currentAttribute)) {
      return convertGeometry(jpaType, currentAttribute);
    }
    if (currentAttribute != null)
      // Type (%1$s) of attribute (%2$s) is not supported. Mapping not possible
      throw new ODataJPAModelException(ODataJPAModelException.MessageKeys.TYPE_NOT_SUPPORTED,
          jpaType.getName(), currentAttribute.getName());
    else
      return null;
  }

  public static EdmPrimitiveTypeKind convertToEdmSimpleType(final JPAAttribute attribute)
      throws ODataJPAModelException {
    return convertToEdmSimpleType(attribute.getType(), null);
  }

  public static boolean isScalarType(final Class<?> type) {
    return (type == String.class ||
        type == Character.class ||
        type == Long.class ||
        type == Short.class ||
        type == Integer.class ||
        type == Double.class ||
        type == Float.class ||
        type == BigDecimal.class ||
        type == Byte.class ||
        type == Boolean.class ||
        type == java.time.LocalTime.class ||
        type == java.sql.Time.class ||
        type == java.time.Duration.class ||
        type == java.time.LocalDate.class ||
        type == java.sql.Date.class ||
        type == Calendar.class ||
        type == Timestamp.class ||
        type == java.util.Date.class ||
        type == UUID.class);
  }

  /**
   * For supported java types see {@link org.apache.olingo.commons.api.edm.EdmPrimitiveType}
   * @param type
   * @return
   */
  public static boolean isSupportedByOlingo(final Class<?> type) {

    return (type == Boolean.class ||
        type == Byte.class ||
        type == Byte[].class ||
        type == byte[].class ||
        type == Double.class ||
        type == Float.class ||
        type == Integer.class ||
        type == java.math.BigDecimal.class ||
        type == java.math.BigInteger.class ||
        type == java.sql.Time.class ||
        type == java.sql.Timestamp.class ||
        type == java.util.Calendar.class ||
        type == java.util.Date.class ||
        type == java.util.UUID.class ||
        type == Long.class ||
        type == Short.class ||
        type == String.class);
  }

  private static EdmPrimitiveTypeKind convertGeography(final Class<?> jpaType, final Attribute<?, ?> currentAttribute)
      throws ODataJPAModelException {
    if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.Point.class)) {
      return EdmPrimitiveTypeKind.GeographyPoint;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiPoint.class)) {
      return EdmPrimitiveTypeKind.GeographyMultiPoint;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.LineString.class)) {
      return EdmPrimitiveTypeKind.GeographyLineString;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiLineString.class)) {
      return EdmPrimitiveTypeKind.GeographyMultiLineString;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.Polygon.class)) {
      return EdmPrimitiveTypeKind.GeographyPolygon;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiPolygon.class)) {
      return EdmPrimitiveTypeKind.GeographyMultiPolygon;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.GeospatialCollection.class)) {
      return EdmPrimitiveTypeKind.GeographyCollection;
    }
    // Type (%1$s) of attribute (%2$s) is not supported. Mapping not possible
    throw new ODataJPAModelException(ODataJPAModelException.MessageKeys.TYPE_NOT_SUPPORTED,
        jpaType.getName(), currentAttribute.getName());
  }

  private static EdmPrimitiveTypeKind convertGeometry(final Class<?> jpaType, final Attribute<?, ?> currentAttribute)
      throws ODataJPAModelException {
    if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.Point.class)) {
      return EdmPrimitiveTypeKind.GeometryPoint;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiPoint.class)) {
      return EdmPrimitiveTypeKind.GeometryMultiPoint;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.LineString.class)) {
      return EdmPrimitiveTypeKind.GeometryLineString;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiLineString.class)) {
      return EdmPrimitiveTypeKind.GeometryMultiLineString;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.Polygon.class)) {
      return EdmPrimitiveTypeKind.GeometryPolygon;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.MultiPolygon.class)) {
      return EdmPrimitiveTypeKind.GeometryMultiPolygon;
    } else if (jpaType.equals(org.apache.olingo.commons.api.edm.geo.GeospatialCollection.class)) {
      return EdmPrimitiveTypeKind.GeometryCollection;
    }
    // Type (%1$s) of attribute (%2$s) is not supported. Mapping not possible
    throw new ODataJPAModelException(ODataJPAModelException.MessageKeys.TYPE_NOT_SUPPORTED,
        jpaType.getName(), currentAttribute.getName());
  }

  private static TemporalType determineTemporalType(final Attribute<?, ?> currentAttribute) {
    if (currentAttribute != null) {
      final AnnotatedElement annotatedElement = (AnnotatedElement) currentAttribute.getJavaMember();
      if (annotatedElement != null && annotatedElement.getAnnotation(Temporal.class) != null) {
        return annotatedElement.getAnnotation(Temporal.class).value();
      }
    }
    return null;

  }

  private static Dimension getDimension(final Attribute<?, ?> currentAttribute) {
    if (currentAttribute.getJavaMember() instanceof AnnotatedElement) {
      final AnnotatedElement annotatedElement = (AnnotatedElement) currentAttribute.getJavaMember();
      final EdmGeospatial spatialDetails = annotatedElement.getAnnotation(EdmGeospatial.class);
      if (spatialDetails != null)
        return spatialDetails.dimension();
    }
    return null;
  }

  private static boolean isBlob(final Attribute<?, ?> currentAttribute) {
    if (currentAttribute != null) {
      final AnnotatedElement annotatedElement = (AnnotatedElement) currentAttribute.getJavaMember();
      if (annotatedElement != null && annotatedElement.getAnnotation(Lob.class) != null) {
        return true;
      }
    }
    return false;
  }

  private static boolean isGeography(final Attribute<?, ?> currentAttribute) {
    return currentAttribute != null && getDimension(currentAttribute) == Dimension.GEOGRAPHY ? true : false;
  }

  private static boolean isGeometry(final Attribute<?, ?> currentAttribute) {
    return currentAttribute != null && getDimension(currentAttribute) == Dimension.GEOMETRY ? true : false;
  }
}
