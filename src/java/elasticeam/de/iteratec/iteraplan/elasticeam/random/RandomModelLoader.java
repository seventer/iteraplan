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
package de.iteratec.iteraplan.elasticeam.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * Creates a random model.
 */
public class RandomModelLoader {

  private static final int DEFAULTSIZE = 10;

  public void load(Metamodel metamodel, Model model) {
    load(metamodel, model, DEFAULTSIZE);
  }

  public void load(Metamodel metamodel, Model model, int size) {
    new LoadRun(metamodel, model, size).run();
  }

  private static class LoadRun {
    private Metamodel    metamodel;
    private Model        model;
    private int          size;
    private SecureRandom rnd;
    private long         id;

    LoadRun(Metamodel metamodel, Model model, int size) {
      this.metamodel = metamodel;
      this.model = model;
      this.size = size;
      this.rnd = new SecureRandom();
      this.id = 0;
    }

    void run() {
      Multimap<UniversalTypeExpression, UniversalModelExpression> entities = LinkedListMultimap.create();
      for (UniversalTypeExpression universalType : this.metamodel.getUniversalTypes()) {
        for (int i = 0; i < this.size; i++) {
          UniversalModelExpression instance = universalType instanceof SubstantialTypeExpression ? this.model
              .create((SubstantialTypeExpression) universalType) : this.model.create((RelationshipTypeExpression) universalType);
          randomizeProperties(instance, universalType.getProperties());
          entities.put(universalType, instance);
          instance.setValue(UniversalTypeExpression.ID_PROPERTY, BigInteger.valueOf(this.id++));
        }
      }
      RelationshipEndFilter filter = new RelationshipEndFilter();
      for (Entry<UniversalTypeExpression, UniversalModelExpression> entity : entities.entries()) {
        randomizeRelationshipEnds(entity.getValue(), Collections2.filter(entity.getKey().getRelationshipEnds(), filter), entities);
      }

    }

    private void randomizeRelationshipEnds(UniversalModelExpression instance, Collection<RelationshipEndExpression> relationshipEnds,
                                           Multimap<UniversalTypeExpression, UniversalModelExpression> entities) {
      for (RelationshipEndExpression relationshipEnd : relationshipEnds) {
        randomizeRelationshipEnd(instance, relationshipEnd, entities);
      }
    }

    private void randomizeRelationshipEnd(UniversalModelExpression instance, RelationshipEndExpression relationshipEnd,
                                          Multimap<UniversalTypeExpression, UniversalModelExpression> entities) {
      if (relationshipEnd.getLowerBound() == 1 || rnd.nextBoolean()) {
        List<UniversalModelExpression> admissibleValues = new ArrayList<UniversalModelExpression>(entities.get(relationshipEnd.getType()));
        Collections.shuffle(admissibleValues, this.rnd);
        if (relationshipEnd.getUpperBound() == 1) {
          instance.connect(relationshipEnd, admissibleValues.get(0));
        }
        else {
          instance.connect(relationshipEnd, admissibleValues.subList(0, this.rnd.nextInt(this.size / 2) + 1));
        }
      }
    }

    private void randomizeProperties(UniversalModelExpression instance, List<PropertyExpression<?>> properties) {
      for (PropertyExpression<?> property : properties) {
        randomizeProperty(instance, property);
      }
    }

    private void randomizeProperty(UniversalModelExpression instance, PropertyExpression<?> property) {
      if (property.getLowerBound() == 1 || rnd.nextBoolean()) {
        if (property.getUpperBound() == 1) {
          instance.setValue(property, randomValue(property.getType()));
        }
        else {
          Set<Object> values = Sets.newHashSet();
          int card = rnd.nextInt(10);
          for (int i = 0; i < card; i++) {
            values.add(randomValue(property.getType()));
          }
          instance.setValue(property, values);
        }
      }
    }

    private Object randomValue(DataTypeExpression dataType) {
      if (dataType instanceof EnumerationExpression) {
        EnumerationExpression enType = (EnumerationExpression) dataType;
        return enType.getLiterals().get(this.rnd.nextInt(enType.getLiterals().size()));
      }
      else if (BuiltinPrimitiveType.BOOLEAN.equals(dataType)) {
        return Boolean.valueOf(rnd.nextBoolean());
      }
      else if (BuiltinPrimitiveType.DATE.equals(dataType)) {
        return new Date(rnd.nextLong());
      }
      else if (BuiltinPrimitiveType.DECIMAL.equals(dataType)) {
        return new BigDecimal(rnd.nextDouble() * 100000);
      }
      else if (BuiltinPrimitiveType.DURATION.equals(dataType)) {
        long startDate = rnd.nextLong();
        return new RuntimePeriod(new Date(startDate), new Date(startDate + rnd.nextInt(1000) * 10));
      }
      else if (BuiltinPrimitiveType.INTEGER.equals(dataType)) {
        return BigInteger.valueOf(rnd.nextLong());
      }
      else if (BuiltinPrimitiveType.STRING.equals(dataType)) {
        return RandomStringUtils.random(rnd.nextInt(20) + 5);
      }
      else {
        return null;
      }
    }
  }

  private static class RelationshipEndFilter implements Predicate<RelationshipEndExpression> {
    /**{@inheritDoc}**/
    public boolean apply(RelationshipEndExpression input) {
      return input.getHolder().compareTo(input.getType()) > 1
          || input.getPersistentName().compareTo(input.getRelationship().getOppositeEndFor(input).getPersistentName()) > 1;
    }

  }
}
