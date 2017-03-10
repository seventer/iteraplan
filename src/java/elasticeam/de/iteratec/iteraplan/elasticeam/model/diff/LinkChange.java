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
package de.iteratec.iteraplan.elasticeam.model.diff;

import java.math.BigInteger;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Change representing an updated link between two {@link UniversalModelExpression}s
 */
public class LinkChange extends AbstractModelElementChange {

  public static final TypeOfModelElementChange HANDLED_TYPE = TypeOfModelElementChange.UPDATE_LINK;

  private final UniversalModelExpression       source;
  private final RelationshipEndExpression      relationshipEnd;
  private final UniversalModelExpression       oldTarget;
  private UniversalModelExpression             newTarget;

  /**
   * 
   * Default constructor.
   * @param currentModel
   * @param modifiedModel
   * @param metamodel
   * @param source
   * @param relationshipEnd the {@link RelationshipEndExpression} in the source's type
   * @param oldTarget linked {@link UniversalModelExpression} before change
   * @param newTarget linked {@link UniversalModelExpression} after change
   */
  protected LinkChange(Model currentModel, Model modifiedModel, Metamodel metamodel, UniversalModelExpression source,
      RelationshipEndExpression relationshipEnd, UniversalModelExpression oldTarget, UniversalModelExpression newTarget) {
    super(currentModel, modifiedModel, metamodel);
    this.source = source;
    this.relationshipEnd = relationshipEnd;
    this.oldTarget = oldTarget;
    this.newTarget = newTarget;
  }

  private UniversalModelExpression getValidNewTarget(UniversalModelExpression otherMetamodelNewTarget) {
    if (otherMetamodelNewTarget == null) {
      return null;
    }
    BigInteger newTargetId = (BigInteger) otherMetamodelNewTarget.getValue(UniversalTypeExpression.ID_PROPERTY);
    return getCurrentModel().findById(relationshipEnd.getType(), newTargetId);
  }

  /**{@inheritDoc}**/
  @Override
  public TypeOfModelElementChange getTypeOfModelDifference() {
    return HANDLED_TYPE;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isApplicable() {
    return (newTarget != null || relationshipEnd.getLowerBound() < 1 && getCurrentModel().canEdit(relationshipEnd));
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isActualChange() {
    boolean isChangeFromNull = oldTarget == null && newTarget != null;
    boolean isTargetChange = oldTarget != null && !oldTarget.equals(newTarget);

    return isChangeFromNull || isTargetChange && isApplicable();
  }

  /**{@inheritDoc}**/
  @Override
  public boolean apply() {
    if (isApplicable() && !isApplied()) {
      newTarget = getValidNewTarget(newTarget);
      if (oldTarget != null) {
        getCurrentModel().unlink(source, relationshipEnd, oldTarget);
      }
      if (newTarget != null) {
        getCurrentModel().link(source, relationshipEnd, newTarget);
      }
      setApplied(true);
      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot apply reference change");
  }

  @Override
  public String toString() {
    return super.toString() + ": linkage target from " + source + " via " + relationshipEnd + " changed from " + oldTarget + " to " + newTarget;
  }

  public UniversalModelExpression getSource() {
    return source;
  }

  public RelationshipEndExpression getRelationshipEnd() {
    return relationshipEnd;
  }

  public UniversalModelExpression getOldTarget() {
    return oldTarget;
  }

  public UniversalModelExpression getNewTarget() {
    return newTarget;
  }

}
