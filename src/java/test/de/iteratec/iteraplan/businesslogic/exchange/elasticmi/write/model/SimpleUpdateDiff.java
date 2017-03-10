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

import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.diff.model.Connect;
import de.iteratec.iteraplan.elasticmi.diff.model.Disconnect;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.ObjectDiffImpl;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


public class SimpleUpdateDiff extends ObjectDiffImpl implements UpdateDiff {

  private final ObjectExpression    left;
  private final ObjectExpression    right;
  private final Set<Connect>        connects;
  private final Set<Disconnect>     disconencts;
  private final Set<PropertyChange> changes;

  protected SimpleUpdateDiff(RStructuredTypeExpression type, ObjectExpression leftOE, ObjectExpression rightOE, Set<Connect> connects,
      Set<Disconnect> disconnects, Set<PropertyChange> changes) {
    super(type);
    this.left = leftOE;
    this.right = rightOE;
    this.changes = Sets.newHashSet(changes);
    this.connects = Sets.newHashSet(connects);
    this.disconencts = Sets.newHashSet(disconnects);
  }

  /**{@inheritDoc}**/
  @Override
  public ObjectExpression getLeftObjectExpression() {
    return this.left;
  }

  /**{@inheritDoc}**/
  @Override
  public ObjectExpression getRightObjectExpression() {
    return this.right;
  }

  /**{@inheritDoc}**/
  @Override
  public Set<Connect> getConnects() {
    return this.connects;
  }

  /**{@inheritDoc}**/
  @Override
  public Connect getConnect(RRelationshipEndExpression relationshipEnd) {
    for (Connect conn : this.connects) {
      if (conn.getRelEnd().getPersistentName().equals(relationshipEnd.getPersistentName())) {
        return conn;
      }
    }
    return Connect.emptyConnect(relationshipEnd);
  }

  /**{@inheritDoc}**/
  @Override
  public Set<Disconnect> getDisconnects() {
    return this.disconencts;
  }

  /**{@inheritDoc}**/
  @Override
  public Disconnect getDisconnect(RRelationshipEndExpression relationshipEnd) {
    for (Disconnect disconnect : this.disconencts) {
      if (disconnect.getRelEnd().getPersistentName().equals(relationshipEnd.getPersistentName())) {
        return disconnect;
      }
    }
    return Disconnect.emptyDisconnect(relationshipEnd);
  }

  /**{@inheritDoc}**/
  @Override
  public Set<PropertyChange> getPropertyChanges() {
    return this.changes;
  }

  /**{@inheritDoc}**/
  @Override
  public boolean isEmpty() {
    return this.connects.isEmpty() && this.disconencts.isEmpty() && this.changes.isEmpty();
  }

  /**{@inheritDoc}**/
  @Override
  public void applyConnect(Connect connect, Model model) {
    // nop
  }

  /**{@inheritDoc}**/
  @Override
  public void applyDisconnect(Disconnect disconnect, Model model) {
    // nop
  }

  /**{@inheritDoc}**/
  @Override
  public void applyPropertyChange(PropertyChange change, Model model) {
    // nop
  }

  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("SimpleUpdateDiff(\n ");
    b.append("Type: ");
    b.append(getStructuredType());
    b.append("\n Left OE: ");
    b.append(getLeftObjectExpression());
    b.append("\n Right OE: ");
    b.append(getRightObjectExpression());
    b.append("\nPropertyInits:\n");
    for (PropertyChange change : changes) {
      b.append(" - ");
      b.append(change);
      b.append("\n");
    }
    b.append("Connects:\n");
    for (Connect conn : getConnects()) {
      b.append(" - ");
      b.append(conn);
      b.append("\n");
    }
    b.append("Disconnects:\n");
    for (Disconnect dconn : getDisconnects()) {
      b.append(" - ");
      b.append(dconn);
      b.append("\n");
    }
    b.append(")");
    return b.toString();
  }
}
