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
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Change for adding a {@link RelationshipTypeExpression}
 */
public class LinkExpressionCreation extends ModelExpressionCreation {

  public static final TypeOfModelElementChange HANDLED_TYPE = TypeOfModelElementChange.ADD_RELATIONSHIP_OBJECT;

  private final LinkExpression                 sourceExpression;
  private final RelationshipTypeExpression     newExpressionsType;
  private LinkExpression                       newCreatedExpression;

  protected LinkExpressionCreation(Model currentModel, Model modifiedModel, Metamodel metamodel, LinkExpression newExpression,
      RelationshipTypeExpression newExpressionsType) {
    super(currentModel, modifiedModel, metamodel);
    this.sourceExpression = newExpression;
    this.newExpressionsType = newExpressionsType;
  }

  @Override
  public TypeOfModelElementChange getTypeOfModelDifference() {
    return HANDLED_TYPE;
  }

  @Override
  public boolean apply() {
    if (isApplicable() && !isApplied()) {
      newCreatedExpression = getCurrentModel().create((RelationshipTypeExpression) getUniversalTypeExpression());
      setProperties();
      linkToRelationshipEnds();
      setApplied(true);
      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "Cannot apply element creation: " + this);
  }

  private void linkToRelationshipEnds() {
    for (RelationshipEndExpression relationshipEnd : getUniversalTypeExpression().getRelationshipEnds()) {
      UniversalModelExpression newConnected = getSourceModelExpression().getConnected(relationshipEnd);
      if (newConnected != null) {
        BigInteger id = (BigInteger) newConnected.getValue(UniversalTypeExpression.ID_PROPERTY);

        UniversalModelExpression toConnectTo = getCurrentModel().findById(relationshipEnd.getType(), id);
        getNewCreatedModelExpression().connect(relationshipEnd, toConnectTo);
      }
    }
  }

  @Override
  public UniversalTypeExpression getUniversalTypeExpression() {
    return newExpressionsType;
  }

  @Override
  public UniversalModelExpression getSourceModelExpression() {
    return sourceExpression;
  }

  @Override
  public UniversalModelExpression getNewCreatedModelExpression() {
    return newCreatedExpression;
  }
}
