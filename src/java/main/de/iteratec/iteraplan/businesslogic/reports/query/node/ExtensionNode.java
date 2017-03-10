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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;


/**
 * Intermediary Node for all extension queries.
 */
public class ExtensionNode extends Node {

  /** The building block type of the result. */
  private Type<?>                      resultType;

  /** The extension to use for this node. */
  private Extension                    extension;

  /** Only one child is allowed for an ExtensionNode. */
  private Node                         child;

  private Set<? extends BuildingBlock> leafNodeBuildingBlocks;

  /**
   * Constructor.
   */
  public ExtensionNode(Type<?> resultType, Extension extension) {
    this.resultType = resultType;
    this.extension = extension;
  }

  public Type<?> getResultType() {
    return resultType;
  }

  public Extension getExtension() {
    return extension;
  }

  /**
   * Returns the distance between result type and leaf type.
   */
  public int getExtensionSize() {
    if (extension == null) {
      return 0;
    }

    return extension.getTypesWithJoinProperties().size();
  }

  /**
   * @return The type of the query leaf.
   */
  private Type<?> getLeafType() {
    if (getExtensionSize() > 0) {
      return getExtension().getRequestedType();
    }

    return getResultType();
  }

  public Node getChild() {
    return child;
  }

  public void setChild(Node child) {
    this.child = child;
  }

  public Set<? extends BuildingBlock> getLeafNodeBuildingBlocks() {
    return leafNodeBuildingBlocks;
  }

  public void setLeafNodeBuildingBlocks(Set<? extends BuildingBlock> leafNodeBuildingBlocks) {
    this.leafNodeBuildingBlocks = leafNodeBuildingBlocks;
  }

  /**
   * Returns the short name of the result type concatenated with a positional suffix.
   * 
   * @return The suffixed type name.
   */
  private String getResultTypeDBNameShortWithSuffix() {
    return resultType.getTypeNameDBShort() + "0";
  }

  /**
   * Returns the short name of an intermediary type concatenated with a positional suffix.
   * 
   * @param index
   *          The index of the intermediary type in the chain from result to leaf type.
   * @return The suffixed type name.
   */
  private String getIntermediaryTypeDBNameShortWithSuffix(int index) {
    int suffix = index + 1;
    return getExtension().getIntermediaryType(index).getTypeNameDBShort() + suffix;
  }

  /**
   * Returns the short name of the leaf type concatenated with a positional suffix.
   * 
   * @return The suffixed type name.
   */
  String getLeafTypeDBNameShortWithSuffix() {
    return getLeafType().getTypeNameDBShort() + getExtensionSize();
  }

  private Class<?> getCriteriaClass() {
    try {
      return Class.forName("de.iteratec.iteraplan.model." + getResultType().getTypeNameDB());
    } catch (ClassNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /**
   * Returns the {@link DetachedCriteria} instance with the restrictions, required to filter the data, 
   * loaded from Database. The extending classes must implement the {@link #getSelectCriteria(Class)}, 
   * {@link #getFromCriteria(DetachedCriteria)} and {@link #getWhereCriteria(DetachedCriteria)} methods, 
   * in order to create valid query.
   * 
   * @return the {@link DetachedCriteria} instance
   */
  public DetachedCriteria getCriteria() {
    DetachedCriteria criteria = getSelectCriteria(getCriteriaClass());
    criteria = getFromCriteria(criteria);
    criteria = getWhereCriteria(criteria);

    return criteria;
  }

  /**
   * Creates the newly created instance of the {@link DetachedCriteria} for the specified {@code resultTypeClass} and 
   * alias from {@link #getResultTypeDBNameShortWithSuffix()}.
   * 
   * @param resultTypeClass the class to be selected in a query
   * @return the newly created instance of the {@link DetachedCriteria}
   */
  private DetachedCriteria getSelectCriteria(Class<?> resultTypeClass) {
    return DetachedCriteria.forClass(resultTypeClass, getResultTypeDBNameShortWithSuffix() /* alias */);
  }

  /**
   * Adds the alias's to the specified {@code criteria} and specifies the join types, used for those 
   * alias. The alias can be later used in {@link #getWhereCriteria(DetachedCriteria)} method to filter
   * the data.
   *
   * @param criteria to be ammended
   * @return criteria to be executed within session scope
   */
  private DetachedCriteria getFromCriteria(DetachedCriteria criteria) {
    String previousTypeNameShort = getResultTypeDBNameShortWithSuffix();
    for (int i = 0; i < getExtensionSize(); i++) {
      String currentTypeNameShort = getIntermediaryTypeDBNameShortWithSuffix(i);
      String path = String.format("%s.%s", previousTypeNameShort, getExtension().getIntermediaryTypeJoinProperty(i));
      criteria.createAlias(path, currentTypeNameShort, Criteria.INNER_JOIN);
      previousTypeNameShort = currentTypeNameShort;
    }

    return criteria;
  }

  /**
   * Adds the restrictions to the specified {@code criteria} in order to filter the data.
   *
   * @param criteria the criteria instance
   * @return criteria to be executed within session scope
   */
  private DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    addIdRestriction(criteria);
    return criteria;
  }

  /**
   * Adds the restriction on the ids, taken from the {@link #getLeafNodeBuildingBlocks()}.
   * 
   * <p>If the element list is longer than 950 items, it is split up into two (or more) lists. This is
   * because Oracle refuses to check against lists with more than 1000 elements. In such a case, the
   * result will be <tt>prop.id in (<b>less than 1000 elements</b>) or prop.id in (<b>remaining elements</b>)</tt>.
   * Thus the list-items are split up into several in-clauses. This will make the query a bit slower.
   * 
   * @param criteria the criteria to add the restriction for
   */
  private void addIdRestriction(DetachedCriteria criteria) {
    Set<Integer> createIdSetFromIdEntities = GeneralHelper.createIdSetFromIdEntities(leafNodeBuildingBlocks);
    String propName = getLeafTypeDBNameShortWithSuffix() + ".id";

    criteria.add(CriteriaUtil.createInRestrictions(propName, createIdSetFromIdEntities));
  }

  public String toString() {
    return "Extension: " + getExtension();
  }
}
