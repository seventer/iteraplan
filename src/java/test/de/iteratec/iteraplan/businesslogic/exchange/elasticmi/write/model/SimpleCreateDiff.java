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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import java.math.BigInteger;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.diff.model.Connect;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ObjectDiffImpl;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


public class SimpleCreateDiff extends ObjectDiffImpl implements CreateDiff {

  private final ObjectExpression  oe;
  private final Set<PropertyInit> propInits;
  private final Set<Connect>      connects;

  protected SimpleCreateDiff(RStructuredTypeExpression type, ObjectExpression oe, Set<PropertyInit> propInits, Set<Connect> connects) {
    super(type);
    this.oe = oe;
    this.propInits = Sets.newHashSet(propInits);
    this.connects = Sets.newHashSet(connects);
  }

  /**{@inheritDoc}**/
  @Override
  public ObjectExpression getObjectExpression() {
    return this.oe;
  }

  /**{@inheritDoc}**/
  @Override
  public Set<Connect> getConnects() {
    return this.connects;
  }

  /**{@inheritDoc}**/
  @Override
  public Connect getConnect(RRelationshipEndExpression relationshipEnd) {
    for (Connect connect : this.connects) {
      if (connect.getRelEnd().getPersistentName().equals(relationshipEnd.getPersistentName())) {
        return connect;
      }
    }
    return Connect.emptyConnect(relationshipEnd);
  }

  /**{@inheritDoc}**/
  @Override
  public Set<PropertyInit> getPropertyInits() {
    return this.propInits;
  }

  /**{@inheritDoc}**/
  @Override
  public ObjectExpression applyCreate(Model model) {
    return getObjectExpression();
  }

  /**{@inheritDoc}**/
  @Override
  public void applyConnect(Connect arg0, Model arg1) {
    // nop
  }

  /**{@inheritDoc}**/
  @Override
  public BigInteger getIdAfterMerge() {
    return null;
  }

  /**{@inheritDoc}**/
  @Override
  public void setIdAfterMerge(BigInteger idAfterMerge) {
    // nop
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("SimpleCreateDiff(\n ");
    b.append("Type: ");
    b.append(getStructuredType());
    b.append("\n OE: ");
    b.append(getObjectExpression());
    b.append("\nPropertyInits:\n");
    for (PropertyInit init : propInits) {
      b.append(" - ");
      b.append(init);
      b.append("\n");
    }
    b.append("Connects:\n");
    for (Connect conn : getConnects()) {
      b.append(" - ");
      b.append(conn);
      b.append("\n");
    }
    b.append(")");
    return b.toString();
  }

}
