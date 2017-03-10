/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.presentation.dialog.common.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.AbstractAssociation;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AbstractAttributeTypeComponentModelPartBase;
import de.iteratec.iteraplan.presentation.dialog.common.model.attributes.AttributeTypeComponentModelPart;


/**
 * Component model for a N-association of a source type 'F' to a target type 'T' where the 
 * association is modeled as a set, i.e. with no inherent ordering. This Association is attributable.
 */
public abstract class ManyAssociationSetAttributableComponentModel<F extends BuildingBlock, T extends BuildingBlock, A extends AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>>
    extends ManyAssociationSetComponentModel<F, T> {

  private static final long                            serialVersionUID = -932350261467222928L;

  private static final Logger                          LOGGER           = Logger
                                                                            .getIteraplanLogger(ManyAssociationSetAttributableComponentModel.class);

  private List<AttributeType>                          attributeTypes   = null;

  private List<ElementWithAssociationAttributes<T, A>> rowsWithAssociationAttributes;

  /**
   * @param componentMode The component mode for this component.
   * @param htmlId The html id the JSP should use for this component. This facilitates 
   *               the writing of automated tests.
   * @param tableHeaderKey The I18N key the JSP should use as a heading for this component.
   * @param columnHeaderKeys The I18N keys the JSP should use as the headings for the columns
   *                         of the connected elements. The number of keys corresponds to the
   *                         number of columns.
   * @param connectedElementsFields The fields of the connected elements that can be displayed
   *                                by the JSP. The fields correspond to the columnHeaderKeys.
   *                                For example: If the first columnHeaderKey is 'global.name', the
   *                                first connectedElementsField could be 'name'.
   * @param availableElementsLabel The field of the available elements that should be used for
   *                               display. For example: 'name'.
   * @param dummyForPresentation An empty instance of the class on the other side of the association.
   *                             This dummy is added to the available list of elements to represent
   *                             no selected available element.
   */
  public ManyAssociationSetAttributableComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
      String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation) {
    super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation);
  }

  /**
   * @param componentMode The component mode for this component.
   * @param htmlId The html id the JSP should use for this component. This facilitates 
   *               the writing of automated tests.
   * @param tableHeaderKey The I18N key the JSP should use as a heading for this component.
   * @param columnHeaderKeys The I18N keys the JSP should use as the headings for the columns
   *                         of the connected elements. The number of keys corresponds to the
   *                         number of columns.
   * @param connectedElementsFields The fields of the connected elements that can be displayed
   *                                by the JSP. The fields correspond to the columnHeaderKeys.
   *                                For example: If the first columnHeaderKey is 'global.name', the
   *                                first connectedElementsField could be 'name'.
   * @param availableElementsLabel The field of the available elements that should be used for
   *                               display. For example: 'name'.
   * @param dummyForPresentation An empty instance of the class on the other side of the association.
   *                             This dummy is added to the available list of elements to represent
   *                             no selected available element.                    
   * @param lookupLablesMode This boolean list defines, which of the connectedElementsFields are
   *                         I18N keys that have to be resolved. For example, if the 
   *                         connectedElementsFields are: ['name', 'typeofstatus'] and the
   *                         lookupLablesMode are: [false, true], then the field value of name can be
   *                         displayed while the field value of typeofstatus is an I18N key that has
   *                         to be resolved. Set to null if not needed.
   * @param lookupAvailableLablesMode If true, then the availableElementsLabel points to a field which
   *                                  is an I18N key that has to be resolved. Set to false if not needed.
   * @param availableElementsPresentationGroupKeys A list of I18N keys. If this list is set, the method
   *                                               {{@link #getAvailableElements()} is expected to return
   *                                               a list of lists which correspond to the I18N keys. With
   *                                               this mechanism, the available elements can be grouped.
   *                                               Set to null if not needed. 
   */
  public ManyAssociationSetAttributableComponentModel(ComponentMode componentMode, String htmlId, String tableHeaderKey, String[] columnHeaderKeys,
      String[] connectedElementsFields, String availableElementsLabel, T dummyForPresentation, Boolean[] lookupLablesMode,
      Boolean lookupAvailableLablesMode, String[] availableElementsPresentationGroupKeys) {
    super(componentMode, htmlId, tableHeaderKey, columnHeaderKeys, connectedElementsFields, availableElementsLabel, dummyForPresentation,
        lookupLablesMode, lookupAvailableLablesMode, availableElementsPresentationGroupKeys);
  }

  /**
   * Returns the the {@link TypeOfBuildingBlock} for this association. e.g. {@link Isr2BoAssociation}
   * @return type of building block for this association.
   */
  protected abstract TypeOfBuildingBlock getAssociationType();
  
  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();

  /**
   * This method should be delegated to the getter method for this association.
   * @param entity The entity for which the getter should be called.
   * @return A list of Associations.
   */
  protected abstract Set<A> getAssociationsFrom(F entity);

  /**
   * This method should be delegated to the getter method from a association, to get the target.
   * @param association The association for which the getter should be called.
   * @return The entity from the target side.
   */
  protected abstract T getTargetFrom(A association);

  /**
   * Returns a new association.
   * @return A new association.
   */
  protected abstract A createNewAssociation();

  /**
   * Connects the association with the both sides.
   * @param association the association which has to be connected.
   * @param source the source side of the association.
   * @param target the target side of the association.
   */
  protected abstract void connectAssociation(A association, F source, T target);

  private void sortRows(List<ElementWithAssociationAttributes<T, A>> rows) {
    Comparator<T> comparator = comparatorForSorting();
    if (comparator == null) {
      Collections.sort(rows);
    }
    else {
      Collections.sort(rows, new Comparator<ElementWithAssociationAttributes<T, A>>() {
        Comparator<T> comp = comparatorForSorting();

        public int compare(ElementWithAssociationAttributes<T, A> o1, ElementWithAssociationAttributes<T, A> o2) {
          return comp.compare(o1.getEntity(), o2.getEntity());
        }
      });
    }
  }

  public void initializeFrom(F source) {
    super.initializeFrom(source);

    attributeTypes = Lists.newArrayList();
    List<AttributeType> types = SpringServiceFactory.getAttributeTypeService().getAttributeTypesForTypeOfBuildingBlock(getAssociationType(), false);
    for (AttributeType at : types) {
      AttributeTypeGroup atg = at.getAttributeTypeGroup();
      if (UserContext.getCurrentUserContext().getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ)) {
        attributeTypes.add(at);
      }
    }

    this.rowsWithAssociationAttributes = Lists.newArrayList();
    for (T entity : getConnectedElements(source)) {
      ElementWithAssociationAttributes<T, A> row = createElementWithAssociationAttributes(entity);
      this.rowsWithAssociationAttributes.add(row);
    }
    sortRows(this.rowsWithAssociationAttributes);

  }

  public void update() {
    super.update();

    for (ElementWithAssociationAttributes<T, A> row : rowsWithAssociationAttributes) {
      row.update();
    }

    if (rowsWithAssociationAttributes.size() > 0) {
      sortRows(rowsWithAssociationAttributes);
    }
  }

  protected void processElementIdToRemove() {
    Integer elementIdToRemove = getElementIdToRemove();
    if (elementIdToRemove != null && elementIdToRemove.intValue() > 0) {
      for (Iterator<ElementWithAssociationAttributes<T, A>> it = rowsWithAssociationAttributes.iterator(); it.hasNext();) {
        T connected = it.next().getEntity();
        Integer connectedId = connected.getId();
        if (elementIdToRemove.equals(connectedId)) {

          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing element with ID " + elementIdToRemove);
          }

          it.remove();
          getAvailableElements().add(connected);
          break;
        }
      }
    }
  }

  protected void processElementIdToAdd() {
    Integer elementIdToAdd = getElementIdToAdd();
    if (elementIdToAdd != null && elementIdToAdd.intValue() > 0) {
      for (Iterator<T> it = getAvailableElements().iterator(); it.hasNext();) {
        T available = it.next();
        Integer availableId = available.getId();
        if (elementIdToAdd.equals(availableId)) {

          LOGGER.debug("Adding element with ID {0}", elementIdToAdd);

          it.remove();
          rowsWithAssociationAttributes.add(createElementWithAssociationAttributes(available));
          break;
        }
      }
    }
  }

  protected void processElementIdsToRemove() {
    Integer[] elementIdsToRemove = getElementIdsToRemove();
    if (elementIdsToRemove != null && elementIdsToRemove.length > 0) {
      for (Iterator<ElementWithAssociationAttributes<T, A>> it = rowsWithAssociationAttributes.iterator(); it.hasNext();) {
        T connected = it.next().getEntity();
        Integer connectedId = connected.getId();
        for (Integer id : getElementIdsToRemove()) {
          if (id.equals(connectedId)) {

            LOGGER.debug("Removing element with ID {0}", id);

            it.remove();
            getAvailableElements().add(connected);
            break;
          }
        }
      }
    }

  }

  protected void processElementIdsToAdd() {
    Integer[] elementIdsToAdd = getElementIdsToAdd();
    if (elementIdsToAdd != null && elementIdsToAdd.length > 0) {
      for (Iterator<T> it = getAvailableElements().iterator(); it.hasNext();) {
        T available = it.next();
        Integer availableId = available.getId();
        for (Integer id : elementIdsToAdd) {
          if (id.equals(availableId)) {

            LOGGER.debug("Adding element with ID {0}", id);

            it.remove();
            rowsWithAssociationAttributes.add(createElementWithAssociationAttributes(available));
            break;
          }
        }
      }
    }

  }

  @SuppressWarnings("unchecked")
  public void configure(F target) {
    getAssociationsFrom(target).clear();

    BuildingBlockService<BuildingBlock, Integer> assocService = SpringServiceFactory.getBuildingBlockServiceLocator()
        .getService(getAssociationType());

    for (ElementWithAssociationAttributes<T, A> row : rowsWithAssociationAttributes) {
      T entity = row.getEntity();
      A association = row.getAssociation();
      BuildingBlockService<BuildingBlock, Integer> service = SpringServiceFactory.getBuildingBlockServiceLocator().getService(
          entity.getTypeOfBuildingBlock());
      entity = (T) service.loadObjectByIdIfExists(entity.getId());

      if (entity != null) {
        if (getComponentMode() == ComponentMode.CREATE) {
          association = createNewAssociation();
        }
        else if (association.getId() != null) {
          association = (A) assocService.loadObjectById(association.getId());
        }

        connectAssociation(association, target, entity);
        row.configure(association);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setConnectedElements(F target, Set<T> toConnect) {
    // There is another strategy in configure(). We do not need this method.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<T> getConnectedElements() {
    List<T> connectedElements = Lists.newArrayList();
    for (ElementWithAssociationAttributes<T, A> row : rowsWithAssociationAttributes) {
      connectedElements.add(row.getEntity());
    }
    return connectedElements;
  }

  /**
   * Returns a {@link AbstractAssociation} connecting the managed element with the entity as parameter.
   * This could be a new association if this relation is new, or the existing association object if
   * this relation is already persistent.
   * @param entity the entity for which the association object is needed.
   * @return an association object which connects the managed element with the entity as parameter.
   */
  protected A getOrCreateAssociation(T entity) {
    Set<A> associations = getAssociationsFrom(this.getManagedElement());
    for (A assoc : associations) {
      if (getTargetFrom(assoc).equals(entity)) {
        return assoc;
      }
    }
    return createNewAssociation();
  }

  public List<AttributeType> getAttributeTypes() {
    return attributeTypes;
  }

  public List<ElementWithAssociationAttributes<T, A>> getRowsWithAssociationAttributes() {
    return rowsWithAssociationAttributes;
  }

  /**
   * Creates a new {@link ElementWithAssociationAttributes} for the given entity. It will contain the entity itself,
   * the association object which connects the entity with the managed element and for every attribute which is available 
   * for these connection a {@link AttributeTypeComponentModelPart}.
   * @param entity the entity for wich the {@link ElementWithAssociationAttributes} should be created.
   * @return see method description
   */
  private ElementWithAssociationAttributes<T, A> createElementWithAssociationAttributes(T entity) {
    A association = getOrCreateAssociation(entity);
    List<AttributeType> ats = getAttributeTypes();
    ats = SpringServiceFactory.getAttributeTypeService().reload(ats);
    return new ElementWithAssociationAttributes<T, A>(entity, association, ats, getComponentMode());
  }

  /**
   * Contains an entity with his association object to the managed object and a list of {@link AttributeTypeComponentModelPart}
   * for each attribute on this association.
   */
  public static class ElementWithAssociationAttributes<T extends BuildingBlock, A extends AbstractAssociation<? extends BuildingBlock, ? extends BuildingBlock>>
      implements Serializable, Comparable<ElementWithAssociationAttributes<T, A>> {
    private static final long                     serialVersionUID = -2237551821000467738L;
    private final T                               entity;
    private final A                               association;
    private List<AttributeTypeComponentModelPart> atParts          = Lists.newArrayList();

    public ElementWithAssociationAttributes(T entity, A association, List<AttributeType> ats, ComponentMode componentMode) {
      this.entity = entity;
      this.association = association;
      Hibernate.initialize(association.getAttributeValueAssignments());
      for (AttributeType at : ats) {
        if (at != null) {
          List<AttributeValue> avs = Lists.newArrayList();
          Set<AttributeValueAssignment> avas = association.getAssignmentsForId(at.getId());
          for (AttributeValueAssignment ava : avas) {
            avs.add(ava.getAttributeValue());
          }
          atParts.add(AbstractAttributeTypeComponentModelPartBase.createAttributeTypeComponentModelPart(association, avs, at, componentMode));
        }
        else {
          atParts.add(null);
        }
      }
    }

    public A getAssociation() {
      return association;
    }

    public List<AttributeTypeComponentModelPart> getAtParts() {
      return atParts;
    }

    public T getEntity() {
      return entity;
    }

    public void update() {
      for (AttributeTypeComponentModelPart atPart : getAtParts()) {
        atPart.update();
      }
    }

    public void configure(A target) {
      for (AttributeTypeComponentModelPart atPart : getAtParts()) {
        atPart.configure(target);
      }
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(ElementWithAssociationAttributes<T, A> o) {
      return getEntity().compareTo(o.getEntity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int prime = 31;
      int result = 1;
      result = prime * result + ((entity == null) ? 0 : entity.hashCode());
      return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ElementWithAssociationAttributes<?, ?> other = (ElementWithAssociationAttributes<?, ?>) obj;
      if (entity == null) {
        if (other.entity != null) {
          return false;
        }
      }
      else if (!entity.equals(other.entity)) {
        return false;
      }
      return true;
    }

  }

}
