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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.common.util.StringUtil;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * Base class for hierarchical entities.
 *
 * @param <T>
 *          Type parameter.
 */
@Entity
@Audited
public abstract class AbstractHierarchicalEntity<T extends HierarchicalEntity<T>> extends BuildingBlock implements HierarchicalEntity<T> {

  private static final long   serialVersionUID = -2122595039285381144L;

  private static final Logger LOGGER           = Logger.getIteraplanLogger(AbstractHierarchicalEntity.class);

  public static final String  TOP_LEVEL_NAME   = "-";

  /** {@link #getName()} */
  @Field(store = Store.YES)
  private String              name;

  /** {@link #getDescription()} */
  @Field(store = Store.YES)
  private String              description;

  private T                   parent           = null;
  private List<T>             children         = new ArrayList<T>();
  private Integer             position;

  public AbstractHierarchicalEntity() {
    super();
  }

  /**
   * {@inheritDoc}
   */
  public List<T> getChildrenAsList() {
    return getChildren();
  }

  /**
   * {@inheritDoc}
   */
  public Set<T> getChildrenAsSet() {
    return new HashSet<T>(getChildren());
  }

  public void getDescendants(T entity, Set<T> set) {

    for (T elem : entity.getChildrenAsSet()) {
      set.add(elem);
      getDescendants(elem, set);
    }
  }

  /**
   * Returns the description of this hierarchical entity. This method takes special measures if the
   * current hierarchical element is the virtual top-level element. In that case, the key to an
   * internationalized description provided for each concrete subclass is returned via the abstract
   * method {@link #getI18NKey()}. Together with the current locale stored in the user's context the
   * key is used to retrieve the internationalized description from the appropriate resource bundle.
   *
   * @return The description.
   */
  @Override
  public String getDescription() {
    if (isTopLevelElement()) {
      Locale locale = UserContext.getCurrentLocale();
      if (locale != null) {
        String desc = MessageAccess.getStringOrNull(getI18NKey(), locale);
        if (desc != null) {
          return desc;
        }
      }
      return getI18NKey();
    }
    return description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHierarchicalName() {
    return makeHierarchicalName();
  }

  public String getHierarchicalNameIfDifferent() {
    if (this.name.equals(this.getHierarchicalName())) {
      return "";
    }
    else {
      return this.getHierarchicalName();
    }
  }

  /**
   * Returns the localized string for the description of the hierarchy's top-level element.
   *
   * @return The key to the localized description of the top-level element.
   */
  public abstract String getI18NKey();

  /**
   * {@inheritDoc} The identity string returned for hierarchical entities equals their hierarchical
   * name.
   */
  public String getIdentityString() {
    return getHierarchicalName();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public int getLevel() {

    T entity = (T) this;
    int level = 0;
    while (entity.getParentElement() != null) {
      level++;
      entity = entity.getParentElement();
    }

    return level;
  }

  /**
   * @return Returns the name of this hierarchical entity.
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getNonHierarchicalName() {
    return name;
  }

  /** {@inheritDoc} */
  public boolean isTopLevelElement() {
    return TOP_LEVEL_NAME.equals(name);
  }

  /**
   * Assembles and returns the hierarchical name for the given HierarchicalEntity.
   *
   * @param entity
   *          The entity for which the hierarchical name should be created.
   * @param detailedName
   *          Iff true, the full hierarchical name is returned. Else, a short version is returned.
   * @return The hierarchical name where each part is separated by an appropriate separator symbol.
   */
  private String makeHierarchicalName() {
    if (getId() == null) {
      return "";
    }

    List<String> stringsToConcatenate = new ArrayList<String>();
    stringsToConcatenate.add(getNonHierarchicalName());

    HierarchicalEntity<?> p = getParentElement();
    while (p != null) {
      if (p.getParentElement() != null) {
        stringsToConcatenate.add(0, p.getNonHierarchicalName());
      }
      p = p.getParentElement();
    }

    return GeneralHelper.makeConcatenatedStringWithSeparator(stringsToConcatenate, HIERARCHICAL_NAME_SEPARATOR);
  }

  public void setDescription(String description) {
    // Ignore description for virtual elements as it is immutable
    if (!isTopLevelElement()) {
      this.description = StringUtil.removeIllegalXMLChars(description);
    }
  }

  public void setName(String name) {
    this.name = StringUtils.trim(StringUtil.removeIllegalXMLChars(name));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {
    super.validate();

    // Check for cycles in the parent/child hierarchy.
    if (HierarchyHelper.hasElementOfCycle(this)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE);
    }

    if ((name == null) || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }

    if ((description != null) && (description.length() > Constants.TEXT_LONG)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.TEXT_TOO_LONG);
    }

    // If this element has got no parent, it must be the top level element.
    if ((getParentElement() == null) && !isTopLevelElement()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.PARENT_ELEMENT_UNDEFINED);
    }
  }

  public int findChildPos(T c) {
    List<T> childrenList = getChildrenAsList();
    for (int result = 0; result < childrenList.size(); result++) {
      if (childrenList.get(result) != null && Objects.equal(childrenList.get(result).getId(), c.getId())) {
        return result;
      }
    }

    LOGGER.error("Object hierarchy inconsistent. Child {0} is not a child of Parent {1}! Can happen, if elements get deleted.", c, this);
    throw new IllegalStateException("Object hierarchy inconsistent. Child " + c + " is not a child of Parent " + this);
  }

  /**
   * Returns this element's position in the parents children list. Only used internally by Hibernate for List order persistence.
   * Do not call directly!
   */
  public Integer getPosition() {
    return position;
  }

  /**
   * Only used by Hibernate for List order persistence. Do not call directly!
   * @param pos
   */
  public void setPosition(Integer pos) {
    this.position = pos;
  }

  /**
   * {@inheritDoc}
   */
  public T getParentElement() {
    return parent;
  }

  public T getParent() {
    return parent;
  }

  @AuditMappedBy(mappedBy = "parent", positionMappedBy = "position")
  public List<T> getChildren() {
    return children;
  }

  public void setParent(T newParent) {
    LOGGER.debug("Setting Parent");

    LOGGER.debug("I am: {0}", this);
    
    T oldParent = this.parent;
    LOGGER.debug("old parent: {0}", oldParent);

    LOGGER.debug("new parent: {0}", newParent);

    this.parent = newParent;
    // perform cycle check to validate that the hierarchy is still in a consistent state
    // this is explicitly done _after_ the new parent relation has been set
    if (HierarchyHelper.hasElementOfCycle(this) || this.equals(newParent)) {
      LOGGER.error("Can't set parent of {0} to {1}", this, newParent);
      // reset to the old, valid parent
      this.parent = oldParent;
      throw new IteraplanBusinessException(IteraplanErrorMessages.ELEMENT_OF_HIERARCHY_CYCLE);
    }
  }

  /**
   * Adds a parent, or removes it if {@code parentToAdd} is null. Updates both sides of the association and
   * removes <code>this</code> from the old parent's children list.
   * <p>
   * Do not iterate over a child-list of an entity and call this method on the children. It will
   * result in a concurrent modification exception. Instead copy the children into a new list first
   * and iterate over the new list.
   * </p>
   * @param parentToAdd
   *          element to add as parent
   */
  @SuppressWarnings("unchecked")
  public void addParent(T parentToAdd) {

    LOGGER.debug("parentToAdd: {0}", parentToAdd);

    Preconditions.checkNotNull(parentToAdd);
    T oldParent = this.parent;
    setParent(parentToAdd);
    // if setParent works (doesn't throw an exception), update children-lists
    removeFromParent(oldParent);
    // it's save to cast here, there seems to be no better solution
    parentToAdd.getChildren().add((T) this);
  }

  /**
   * Removes the parent. Updates both sides of the association.
   */
  public void removeParent() {
    LOGGER.debug("called removeParent");
    LOGGER.debug("I am: {0}", this.getName());
    LOGGER.debug("Parent until now is: {0}", parent);

    if (parent != null) {

      parent.getChildren().remove(this);
      setParent(null);
    }
  }

  /**
   * Removes this from the parent's children list.
   */
  public void removeFromParent(T parentElement) {
    LOGGER.debug("removeFromParent: {0}", parentElement);
    LOGGER.debug("I am: {0}", getName());
    if (parentElement != null) {
      parentElement.getChildren().remove(this);
    }
  }

  public void setChildren(List<T> children) {
    this.children = children;
  }

  /**
   * Removes all Children
   */
  public void removeAllChildren() {
    List<T> newList = arrayList();
    this.children = newList;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof AbstractHierarchicalEntity<?>)) {
      return false;
    }
    final AbstractHierarchicalEntity<?> other = (AbstractHierarchicalEntity<?>) obj;
    if (getDescription() == null) {
      if (other.getDescription() != null) {
        return false;
      }
    }
    else if (!getDescription().equals(other.getDescription())) {
      return false;
    }
    if (getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    return true;
  }

  public void removeChildren(T root) {
    for (T child : Sets.newHashSet(getChildren())) {
      child.removeParent();
      child.addParent(root);
    }
    getChildren().clear();
  }

  @SuppressWarnings("unchecked")
  public void addChildren(Collection<T> childrenToAdd) {
    for (T child : childrenToAdd) {
      child.removeParent();
      child.addParent((T) this);
    }
  }

}