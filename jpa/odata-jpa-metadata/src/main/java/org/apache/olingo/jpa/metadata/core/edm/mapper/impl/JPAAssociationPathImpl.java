package org.apache.olingo.jpa.metadata.core.edm.mapper.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.olingo.jpa.metadata.core.edm.mapper.api.JPAAssociationAttribute;
import org.apache.olingo.jpa.metadata.core.edm.mapper.api.JPAAttribute;
import org.apache.olingo.jpa.metadata.core.edm.mapper.api.JPAElement;
import org.apache.olingo.jpa.metadata.core.edm.mapper.api.JPAOnConditionItem;
import org.apache.olingo.jpa.metadata.core.edm.mapper.api.JPAStructuredType;
import org.apache.olingo.jpa.metadata.core.edm.mapper.exception.ODataJPAModelException;

class JPAAssociationPathImpl implements JPAAssociationPath {
  final private String alias;
  final private List<JPAElement> pathElements;
  private IntermediateStructuredType sourceType;
  private IntermediateStructuredType targetType;
  private List<IntermediateJoinColumn> joinColumns;
  private final PersistentAttributeType cardinality;

  JPAAssociationPathImpl(String externalName, IntermediateStructuredType sourceType,
      IntermediateStructuredType targetType, List<IntermediateJoinColumn> joinColumns,
      PersistentAttributeType jpaAttributeType) {
    alias = externalName;
    this.sourceType = sourceType;
    this.targetType = targetType;
    this.joinColumns = joinColumns;
    this.pathElements = null;
    this.cardinality = jpaAttributeType;
  }

  JPAAssociationPathImpl(JPAEdmNameBuilder namebuilder, JPAAssociationPath associationPath,
      IntermediateStructuredType source, List<IntermediateJoinColumn> joinColumns, JPAAttribute attribute) {

    List<JPAElement> pathElementsBuffer = new ArrayList<JPAElement>();
    pathElementsBuffer.add(attribute);
    pathElementsBuffer.addAll(associationPath.getPath());

    alias = namebuilder.buildNaviPropertyBindingName(associationPath, attribute);
    this.sourceType = source;
    this.targetType = (IntermediateStructuredType) associationPath.getTargetType();
    if (joinColumns.isEmpty())
      this.joinColumns = ((JPAAssociationPathImpl) associationPath).getJoinColumns();
    else
      this.joinColumns = joinColumns;
    this.pathElements = Collections.unmodifiableList(pathElementsBuffer);
    this.cardinality = ((JPAAssociationPathImpl) associationPath).getCardinality();
  }

  JPAAssociationPathImpl(IntermediateNavigationProperty association,
      IntermediateStructuredType source) throws ODataJPAModelException {
    List<JPAElement> pathElementsBuffer = new ArrayList<JPAElement>();
    pathElementsBuffer.add(association);

    alias = association.getExternalName();
    this.sourceType = source;
    this.targetType = (IntermediateStructuredType) association.getTargetEntity();
    this.joinColumns = association.getJoinColumns();
    this.pathElements = Collections.unmodifiableList(pathElementsBuffer);
    this.cardinality = association.getJoinCardinality();
  }

  private List<IntermediateJoinColumn> getJoinColumns() {
    return joinColumns;
  }

  private PersistentAttributeType getCardinality() {
    return cardinality;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getAlias()
   */
  @Override
  public String getAlias() {
    return alias;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getJoinColumnsList()
   */
  @Override
  public List<JPAOnConditionItem> getJoinColumnsList() throws ODataJPAModelException {
    List<JPAOnConditionItem> joinColumns = new ArrayList<JPAOnConditionItem>();
    for (IntermediateJoinColumn column : this.joinColumns) {
      // ManyToOne
      if (cardinality == PersistentAttributeType.MANY_TO_ONE
          || cardinality == PersistentAttributeType.MANY_TO_MANY)
        joinColumns.add(new JPAOnConditionItemImpl(
            sourceType.getAttributeByDBField(column.getName()),
            targetType.getAttributeByDBField(column.getReferencedColumnName())));
      else
        joinColumns.add(new JPAOnConditionItemImpl(
            sourceType.getAttributeByDBField(column.getReferencedColumnName()),
            targetType.getAttributeByDBField(column.getName())));
    }
    return joinColumns;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getLeaf()
   */
  @Override
  public JPAAssociationAttribute getLeaf() {
    return (JPAAssociationAttribute) pathElements.get(pathElements.size() - 1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getPath()
   */
  @Override
  public List<JPAElement> getPath() {
    return pathElements;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getTargetType()
   */
  @Override
  public JPAStructuredType getTargetType() {
    return targetType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.olingo.jpa.metadata.core.edm.mapper.impl.JPAAssociationPath#getSourceType()
   */
  @Override
  public JPAStructuredType getSourceType() {
    return sourceType;
  }
}
