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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.diff.model.Connect;
import de.iteratec.iteraplan.elasticmi.diff.model.CreateDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.DeleteDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.Disconnect;
import de.iteratec.iteraplan.elasticmi.diff.model.MergeStrategy;
import de.iteratec.iteraplan.elasticmi.diff.model.ModelDiff;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.SingleRelEndChangeFactory;
import de.iteratec.iteraplan.elasticmi.diff.model.SingleRelationshipEndDiff.OriginDiffType;
import de.iteratec.iteraplan.elasticmi.diff.model.UpdateDiff;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


public class SimpleModelDiff implements ModelDiff {

  final Multimap<RStructuredTypeExpression, CreateDiff> createDiffs = HashMultimap.create();
  final Multimap<RStructuredTypeExpression, UpdateDiff> updateDiffs = HashMultimap.create();
  final Multimap<RStructuredTypeExpression, DeleteDiff> deleteDiffs = HashMultimap.create();

  final SingleRelEndChangeFactory                       relEndChangeFactory;

  public SimpleModelDiff(SingleRelEndChangeFactory relEndChangeFactory) {
    this.relEndChangeFactory = relEndChangeFactory;
  }

  public void addUpdate(RStructuredTypeExpression type, ObjectExpression leftOE, ObjectExpression rightOE, AssignmentChanges connects,
                        AssignmentChanges disconnects, Set<PropertyChange> changes) {
    Set<Connect> conn = Sets.newHashSet();
    for (Map.Entry<RRelationshipEndExpression, Collection<ObjectExpression>> entry : connects) {
      RRelationshipEndExpression ree = entry.getKey();
      Collection<ObjectExpression> oes = entry.getValue();
      conn.add(new Connect(ree, relEndChangeFactory.singleConnects(rightOE, ree, oes, OriginDiffType.UPDATE)));
    }
    Set<Disconnect> disconn = Sets.newHashSet();
    for (Map.Entry<RRelationshipEndExpression, Collection<ObjectExpression>> entry : disconnects) {
      RRelationshipEndExpression ree = entry.getKey();
      Collection<ObjectExpression> oes = entry.getValue();
      disconn.add(new Disconnect(ree, relEndChangeFactory.singleDisconnects(rightOE, ree, oes, OriginDiffType.UPDATE)));
    }
    updateDiffs.put(type, new SimpleUpdateDiff(type, leftOE, rightOE, conn, disconn, changes));
  }

  public void addCreate(RStructuredTypeExpression type, ObjectExpression rightOE, Set<PropertyInit> propInits, AssignmentChanges connects) {
    Set<Connect> conn = Sets.newHashSet();
    for (Map.Entry<RRelationshipEndExpression, Collection<ObjectExpression>> entry : connects) {
      RRelationshipEndExpression ree = entry.getKey();
      Collection<ObjectExpression> oes = entry.getValue();
      conn.add(new Connect(ree, relEndChangeFactory.singleConnects(rightOE, ree, oes, OriginDiffType.CREATE)));
    }
    createDiffs.put(type, new SimpleCreateDiff(type, rightOE, propInits, conn));
  }

  public void addDelete(RStructuredTypeExpression type, ObjectExpression oe) {
    deleteDiffs.put(type, new SimpleDeleteDiff(type, oe));
  }

  @Override
  public Set<CreateDiff> getCreateDiffs() {
    return Sets.newHashSet(createDiffs.values());
  }

  @Override
  public Set<CreateDiff> getCreateDiffsForType(RStructuredTypeExpression structuredType) {
    return Sets.newHashSet(this.createDiffs.get(structuredType));
  }

  @Override
  public Set<UpdateDiff> getUpdateDiffs() {
    return Sets.newHashSet(updateDiffs.values());
  }

  @Override
  public Set<UpdateDiff> getUpdateDiffsForType(RStructuredTypeExpression structuredType) {
    return Sets.newHashSet(this.updateDiffs.get(structuredType));
  }

  @Override
  public Set<DeleteDiff> getDeleteDiffs() {
    return Sets.newHashSet(deleteDiffs.values());
  }

  @Override
  public Set<DeleteDiff> getDeleteDiffsForType(RStructuredTypeExpression structuredType) {
    return Sets.newHashSet(this.deleteDiffs.get(structuredType));
  }

  /**{@inheritDoc}**/
  @Override
  public void merge(MergeStrategy strategy) {
    // nop
  }

  public static class AssignmentChanges implements Iterable<Map.Entry<RRelationshipEndExpression, Collection<ObjectExpression>>> {
    private Multimap<RRelationshipEndExpression, ObjectExpression> oeChanges = HashMultimap.create();

    public static AssignmentChanges create() {
      return new AssignmentChanges();
    }

    public static AssignmentChanges create(RRelationshipEndExpression ree, ObjectExpression oe) {
      return new AssignmentChanges().add(ree, oe);
    }

    public static AssignmentChanges create(RRelationshipEndExpression ree, Iterable<ObjectExpression> oes) {
      return new AssignmentChanges().add(ree, oes);
    }

    private AssignmentChanges() {
      // make constructor inaccessible from outside
    }

    public AssignmentChanges add(RRelationshipEndExpression ree, ObjectExpression oe) {
      oeChanges.put(ree, oe);
      return this;
    }

    public AssignmentChanges add(RRelationshipEndExpression ree, Iterable<ObjectExpression> oes) {
      for (ObjectExpression oe : oes) {
        oeChanges.put(ree, oe);
      }
      return this;
    }

    /**{@inheritDoc}**/
    @Override
    public Iterator<Entry<RRelationshipEndExpression, Collection<ObjectExpression>>> iterator() {
      return oeChanges.asMap().entrySet().iterator();
    }
  }

}
