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
package de.iteratec.iteraplan.elasticeam.model;

import java.math.BigInteger;
import java.util.Collection;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * Base interface for all Model related functionality
 */
public interface Model {

  /**
   * Creates an {@link InstanceExpression} instance of a {@link SubstantialTypeExpression}
   * 
   * @param type the {@link SubstantialTypeExpression}, representing the {@link InstanceExpression}'s type
   * @return the created {@link InstanceExpression} of the provided {@link SubstantialTypeExpression}
   */
  InstanceExpression create(SubstantialTypeExpression type);

  /**
   * Searches for all {@link InstanceExpression} for a certain {@link SubstantialTypeExpression}
   * 
   * @param type the {@link SubstantialTypeExpression} to search for
   * @return a {@link Collection} containing all {@link InstanceExpression}s of the {@link SubstantialTypeExpression} 
   */
  Collection<InstanceExpression> findAll(SubstantialTypeExpression type);

  /**
   * Searches for an {@link InstanceExpression}s of a certain {@link SubstantialTypeExpression} and checks its "name" property for equality to a {@link String} 
   * 
   * @param type the {@link SubstantialTypeExpression} to search for
   * @param name the name value to search for
   * @return an {@link InstanceExpression} or null, if the name condition doesn't match (throws {@link ModelException} when {@link SubstantialTypeExpression} is invalid
   */
  InstanceExpression findByName(SubstantialTypeExpression type, String name);

  /**
   * Searches for an {@link InstanceExpression} of a certain {@link SubstantialTypeExpression} and checks its "id" property for equality to an {@link BigInteger}
   * 
   * @param type the {@link SubstantialTypeExpression} to search for
   * @param id the id value to search for
   * @return an {@link InstanceExpression} or null, if the id condition doesn't match (throws {@link ModelException} when {@link SubstantialTypeExpression} is invalid
   */
  InstanceExpression findById(SubstantialTypeExpression type, BigInteger id);

  /**
   * Deletes an {@link InstanceExpression} from the {@link Model}
   * 
   * @param instance the {@link InstanceExpression} which will be deleted
   */
  void delete(InstanceExpression instance);

  /**
   * Links two {@link UniversalModelExpression}s, using a given {@link RelationshipEndExpression}
   * 
   * @param from the source {@link UniversalModelExpression}
   * @param via the {@link RelationshipEndExpression} linking the {@link UniversalModelExpression}s' {@link UniversalTypeExpression}'s 
   * @param to the target {@link UniversalModelExpression}
   */
  void link(UniversalModelExpression from, RelationshipEndExpression via, UniversalModelExpression to);

  /**
   * Returns a {@link BindingSet} for containing all {@link UniversalModelExpression}s that are linked via a certain {@link RelationshipEndExpression} 
   * 
   * @param via the {@link RelationshipEndExpression} to search for
   * @return a {@link BindingSet} with forward- and backward-refs
   */
  BindingSet findAll(RelationshipEndExpression via);

  /**
   * Creates a {@link LinkExpression} of a certain {@link RelationshipTypeExpression}
   * 
   * @param type the {@link RelationshipTypeExpression}
   * @return a {@link LinkExpression}
   */
  LinkExpression create(RelationshipTypeExpression type);

  /**
   * Searches for all {@link LinkExpression}s for a given {@link RelationshipTypeExpression}
   * 
   * @param type the {@link RelationshipTypeExpression} to search for
   * @return a {@link Collection} of {@link LinkExpression}s of the provided {@link RelationshipTypeExpression}
   */
  Collection<LinkExpression> findAll(RelationshipTypeExpression type);

  /**
   * Searches for a {@link LinkExpression} of a certain {@link RelationshipTypeExpression} and checks its "id" property for equality to an {@link BigInteger}
   * 
   * @param type the {@link RelationshipTypeExpression} to search for
   * @param id the id value to search for
   * @return an {@link LinkExpression} or null if the id value cannot be found (Throws {@link ModelException} if {@link RelationshipTypeExpression} is invalid)
   */
  LinkExpression findById(RelationshipTypeExpression type, BigInteger id);

  /**
   * Deletes a {@link LinkExpression}
   * 
   * @param link the {@link LinkExpression} to delete
   */
  void delete(LinkExpression link);

  /**
   * Returns the value of a {@link UniversalModelExpression}'s {@link PropertyExpression}
   * 
   * @param expression the {@link UniversalModelExpression} instance
   * @param property the {@link PropertyExpression}
   * @return the value of the {@link UniversalModelExpression} instances's {@link PropertyExpression}
   */
  Object getValue(UniversalModelExpression expression, PropertyExpression<?> property);

  /**
   * Sets the value for an {@link UniversalModelExpression}s {@link PropertyExpression}
   * 
   * @param expression the {@link UniversalModelExpression} instance
   * @param property the {@link PropertyExpression}
   * @param value the value which will be set
   */
  void setValue(UniversalModelExpression expression, PropertyExpression<?> property, Object value);

  /**
   * Getter, for an {@link UniversalModelExpression}'s {@link RelationshipEndExpression}
   * 
   * @param expression the {@link UniversalModelExpression} instance
   * @param relationshipEnd the {@link RelationshipEndExpression}
   * @return the value ("linked Instance") of the {@link RelationshipEndExpression} feature for the {@link UniversalModelExpression} instance
   */
  Object getValue(UniversalModelExpression expression, RelationshipEndExpression relationshipEnd);

  /**
   * Deletes the link between two {@link UniversalModelExpression}s that are currently linked via a certain {@link RelationshipEndExpression}
   * 
   * @param source the source {@link UniversalModelExpression} instance
   * @param relationshipEnd the {@link RelationshipEndExpression} that links the two {@link UniversalModelExpression}s
   * @param target the target {@link UniversalModelExpression} instance
   */
  void unlink(UniversalModelExpression source, RelationshipEndExpression relationshipEnd, UniversalModelExpression target);

  /**
   * Decides whether an {@link InstanceExpression} for a certain type ({@link UniversalTypeExpression}) can be created or not
   * 
   * @param universalType the type ({@link UniversalTypeExpression}) of the {@link InstanceExpression} to create
   * @return true, if an instance can be created, false otherwise
   */
  boolean canCreate(UniversalTypeExpression universalType);

  /**
   * Decides whether an {@link UniversalTypeExpression} or {@link InstanceExpression}s of the specific type can be deleted or not
   * 
   * @param typeExpression the type ({@link UniversalTypeExpression}) 
   * @return true, if an instance of the type (or the type itself) can be deleted, false otherwise
   */
  boolean canDelete(UniversalTypeExpression typeExpression);

  /**
   * Decides whether an {@link InstanceExpression}  can be deleted or not
   * 
   * @param instance the ({@link InstanceExpression}) to delete
   * @return true, if the {@link InstanceExpression} can be deleted, false otherwise
   */
  boolean canDelete(InstanceExpression instance);

  /**
   * Decides whether an {@link LinkExpression}  can be deleted or not
   * 
   * @param linkExpression the ({@link LinkExpression}) to delete
   * @return true, if the {@link LinkExpression} can be deleted, false otherwise
   */
  boolean canDelete(LinkExpression linkExpression);

  /**
   * Decides whether a {@link RelationshipEndExpression} can be edited/changed or not
   * 
   * @param relationshipEndExpression the {@link RelationshipEndExpression} to edit
   * @return true, if the {@link RelationshipEndExpression} can be edited, false otherwise
   */
  boolean canEdit(RelationshipEndExpression relationshipEndExpression);

  /**
   * Decides whether a {@link PropertyExpression}'s value can be edited for a certain {@link UniversalModelExpression} or not
   * 
   * @param expression the {@link UniversalModelExpression} instance
   * @param property the {@link PropertyExpression} to check
   * @return true, if the {@link PropertyExpression}'s value can be changed for the {@link UniversalModelExpression} instance, false otherwise
   */
  boolean canEdit(UniversalModelExpression expression, PropertyExpression<?> property);

  /**
   * Decides whether a connection between two {@link UniversalModelExpression}s, using a {@link RelationshipEndExpression} can be edited or not
   * 
   * @param from the source {@link UniversalModelExpression}
   * @param to the target (referenced) {@link UniversalModelExpression}
   * @param relationship the {@link RelationshipEndExpression} within the source's {@link UniversalTypeExpression}
   * @return true, if the {@link RelationshipEndExpression} can be edited, false otherwise
   */
  boolean canEdit(UniversalModelExpression from, UniversalModelExpression to, RelationshipExpression relationship);

  /**
   * Searches for all {@link UniversalModelExpression} for a certain {@link UniversalTypeExpression}.
   * 
   * @param type the {@link UniversalTypeExpression} to search for
   * @return a {@link Collection} containing all {@link UniversalModelExpression}s of the {@link UniversalTypeExpression} 
   */
  Collection<UniversalModelExpression> findAll(UniversalTypeExpression type);

  /**
   * Searches for a {@link UniversalModelExpression} of a certain {@link UniversalTypeExpression} and checks its "id" property for equality to an {@link BigInteger}
   * 
   * @param type the {@link UniversalTypeExpression} to search for
   * @param id the id value to search for
   * @return an {@link UniversalModelExpression} or null if the id value cannot be found (Throws {@link ModelException} if {@link UniversalTypeExpression} is invalid)
   */
  UniversalModelExpression findById(UniversalTypeExpression type, BigInteger id);

}
