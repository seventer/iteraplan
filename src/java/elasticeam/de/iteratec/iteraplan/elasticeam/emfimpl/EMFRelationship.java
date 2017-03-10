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
package de.iteratec.iteraplan.elasticeam.emfimpl;

import java.util.List;

import org.eclipse.emf.ecore.EReference;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


public class EMFRelationship extends EMFNamedExpression implements RelationshipExpression {

  private final String     persistentName;
  private final EReference eReference0;
  private final EReference eReference1;

  private final boolean    acyclic;

  /**
   * Default constructor.
   * @param metamodel
   */
  public EMFRelationship(String persistentName, EReference eReference0, EReference eReference1, EMFMetamodel metamodel, boolean acyclic) {
    super(metamodel);
    this.persistentName = persistentName;
    this.eReference0 = eReference0;
    this.eReference1 = eReference1;
    this.acyclic = acyclic;
  }

  /**{@inheritDoc}**/
  public final String getPersistentName() {
    return this.persistentName;
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds() {
    return Lists.newArrayList(getMetamodelImpl().encapsulate(this.eReference0), getMetamodelImpl().encapsulate(this.eReference1));
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByName(String name) {

    //TODO is this not for the persistent name?
    //Maybe we need to use 
    //getMetamodelImpl().encapsulate(eReferenceX.getEContainingClass()).findRelationshipEndByName(name).getRelationship().equals(this)?
    //to decide which end it is?...
    if (this.eReference0.getName().equals(name)) {
      return getMetamodelImpl().encapsulate(this.eReference0);
    }
    if (this.eReference1.getName().equals(name)) {
      return getMetamodelImpl().encapsulate(this.eReference1);
    }
    return null;
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByPersistentName(String name) {
    if (this.eReference0.getName().equals(name)) {
      return getMetamodelImpl().encapsulate(this.eReference0);
    }
    if (this.eReference1.getName().equals(name)) {
      return getMetamodelImpl().encapsulate(this.eReference1);
    }
    return null;
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression getEndLeadingTo(UniversalTypeExpression type) {
    for (RelationshipEndExpression relEnd : getRelationshipEnds()) {
      if (relEnd.getType().equals(type)) {
        return relEnd;
      }
    }
    return null;
  }

  /**{@inheritDoc}**/
  public final List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx) {
    return getRelationshipEnds();
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name) {
    return findRelationshipEndByName(name);
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression getOppositeEndFor(RelationshipEndExpression relationshipEnd) {
    if (((EMFRelationshipEnd) relationshipEnd).getWrapped().equals(eReference0)) {
      return getMetamodelImpl().encapsulate(eReference1);
    }
    else {
      return getMetamodelImpl().encapsulate(eReference0);
    }
  }

  /**{@inheritDoc}**/
  public final Class<? extends NamedExpression> getMetaType() {
    return RelationshipExpression.class;
  }

  /**{@inheritDoc}**/
  public boolean isAcyclic() {
    return acyclic;
  }
}
