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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Predicate;

import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * A Binding Set is a set of tuples (x, y), where each tuple represents a relationship between the elements x and y.
 */
public class BindingSet {

  private UniversalTypeExpression                                      fromType;
  private UniversalTypeExpression                                      toType;

  private Map<UniversalModelExpression, Set<UniversalModelExpression>> forwardBindings;
  private Map<UniversalModelExpression, Set<UniversalModelExpression>> backwardBindings;

  public BindingSet() {
    forwardBindings = new HashMap<UniversalModelExpression, Set<UniversalModelExpression>>();
    backwardBindings = new HashMap<UniversalModelExpression, Set<UniversalModelExpression>>();
  }

  /**
   * Intersects this binding set with another one as follows: A binding <b>(x,y)</b> is left in this
   * binding set after the intersection if and only if the binding exists in both the original version
   * of this binding set and the other set, which is provided as argument.
   * 
   * @param anotherSet
   *    The binding set with which this binding set is intersected.
   */
  public void intersectWith(BindingSet anotherSet) {

    List<UniversalModelExpression> toRemoveStart = new ArrayList<UniversalModelExpression>();
    List<UniversalModelExpression> toRemoveEnd = new ArrayList<UniversalModelExpression>();

    for (UniversalModelExpression fromEl : forwardBindings.keySet()) {
      for (UniversalModelExpression toEl : forwardBindings.get(fromEl)) {
        if (!anotherSet.containsBinding(fromEl, toEl)) {
          toRemoveStart.add(fromEl);
          toRemoveEnd.add(toEl);
        }
      }
    }

    for (int i = 0; i < toRemoveStart.size(); i++) {
      this.removeBinding(toRemoveStart.get(i), toRemoveEnd.get(i));
    }
  }

  /**
   * Adds all bindings from the other set to this binding set.
   * 
   * @param anotherSet
   *          The set with the bindings to be added.
   */
  public void mergeWith(BindingSet anotherSet) {
    for (UniversalModelExpression fromElement : anotherSet.getAllFromElements()) {
      for (UniversalModelExpression toElement : anotherSet.getToBindings(fromElement)) {
        this.addBinding(fromElement, toElement);
      }
    }
  }

  public boolean isEmpty() {
    if (forwardBindings.keySet().size() == 0) {
      return true;
    }
    return false;
  }

  /**
   * Creates a clone of this binding set.
   * @return
   *    The cloned binding set.
   */
  public BindingSet cloneBindingSet() {

    BindingSet cloned = new BindingSet();
    cloned.setFromType(fromType);
    cloned.setToType(toType);

    for (Map.Entry<UniversalModelExpression, Set<UniversalModelExpression>> entry : forwardBindings.entrySet()) {
      for (UniversalModelExpression toBinding : entry.getValue()) {
        cloned.addBinding(entry.getKey(), toBinding);
      }
    }
    return cloned;
  }

  public BindingSet filterBindingSet(Predicate<UniversalModelExpression> end0Filter, Predicate<UniversalModelExpression> end1Filter) {
    BindingSet result = new BindingSet();
    result.setFromType(fromType);
    result.setToType(toType);
    for (Map.Entry<UniversalModelExpression, Set<UniversalModelExpression>> entry : forwardBindings.entrySet()) {
      for (UniversalModelExpression toBinding : entry.getValue()) {
        if (end0Filter.apply(entry.getKey()) && end1Filter.apply(toBinding)) {
          result.addBinding(entry.getKey(), toBinding);
        }
      }
    }
    return result;
  }

  /**
   * Adds a binding between the two elements to the binding set.
   * 
   * @param fromElement
   *          The first component of the binding tuple.
   * @param toElement
   *          The second component of the binding tuple.
   */
  public void addBinding(UniversalModelExpression fromElement, UniversalModelExpression toElement) {

    // Add the forward binding
    if (forwardBindings.get(fromElement) == null) {
      Set<UniversalModelExpression> bList = new HashSet<UniversalModelExpression>();
      bList.add(toElement);
      forwardBindings.put(fromElement, bList);
    }
    else {
      forwardBindings.get(fromElement).add(toElement);
    }

    // Add the backward binding
    if (backwardBindings.get(toElement) == null) {
      Set<UniversalModelExpression> bList = new HashSet<UniversalModelExpression>();
      bList.add(fromElement);
      backwardBindings.put(toElement, bList);
    }
    else {
      backwardBindings.get(toElement).add(fromElement);
    }
  }

  /**
   * Retrieves all second components of the binding tuples with the given element as a first
   * component.
   * 
   * @param fromElement
   *          The element that is the first component of the tuples.
   * @return The set of second components of tuples, where the given element is the first component.
   */
  public Set<UniversalModelExpression> getToBindings(UniversalModelExpression fromElement) {
    return forwardBindings.get(fromElement);
  }

  /**
   * Retrieves the first components of all tuples where the second component is the given binding.
   * 
   * @param toElement
   *          The second component to be used as a filter.
   * @return The set of elements that are the first components of the corresponding bindings.
   */
  public Set<UniversalModelExpression> getFromBindings(UniversalModelExpression toElement) {
    return backwardBindings.get(toElement);
  }

  /**
   * Represents the projection of the first component of the binding set.
   * 
   * @return The first components of all bindings in the binding set.
   */
  public Set<UniversalModelExpression> getAllFromElements() {
    return forwardBindings.keySet();
  }

  /**
   * Represents the projection of the second component of the binding set.
   * 
   * @return The second components of all tuples in the binding set.
   */
  public Set<UniversalModelExpression> getAllToElements() {
    return backwardBindings.keySet();
  }

  /**
   * Removes a binding between two elements from the binding set.
   * 
   * @param fromElement
   *    The first component of the binding to be removed.
   * @param toElement
   *    The second component of the binding to be removed.
   */
  public void removeBinding(UniversalModelExpression fromElement, UniversalModelExpression toElement) {

    if (forwardBindings.get(fromElement) != null) {
      forwardBindings.get(fromElement).remove(toElement);
      if (forwardBindings.get(fromElement).size() == 0) {
        forwardBindings.remove(fromElement);
      }
    }
    if (backwardBindings.get(toElement) != null) {
      backwardBindings.get(toElement).remove(fromElement);
      if (backwardBindings.get(toElement).size() == 0) {
        backwardBindings.remove(toElement);
      }
    }
  }

  /**
   * Removes all bindings that have the given element as a first component of their tuple.
   * 
   * @param fromElement
   *          The element that is the first component of the bindings.
   */
  public void removeAllBindingsFromElement(UniversalModelExpression fromElement) {
    for (UniversalModelExpression element : forwardBindings.get(fromElement)) {
      backwardBindings.get(element).remove(fromElement);
      if (backwardBindings.get(element).size() == 0) {
        backwardBindings.remove(element);
      }
    }
    forwardBindings.remove(fromElement);
  }

  /**
   * Removes all bindings that have the given element as a second component of their tuple.
   * 
   * @param toElement
   *          The element that is the second component of the bindings.
   */
  public void removeAllBindingsToElement(UniversalModelExpression toElement) {
    for (UniversalModelExpression element : backwardBindings.get(toElement)) {
      forwardBindings.get(element).remove(toElement);
      if (forwardBindings.get(element).size() == 0) {
        forwardBindings.remove(element);
      }
    }
    backwardBindings.remove(toElement);
  }

  /**
   * @param fromElement
   *    The first component of the binding (tuple).
   * @param toElement
   *    The second component of the binding (tuple).
   * @return
   *    <b>true</b> if and only if a binding between the two provided elements exists in the binding set.
   */
  public boolean containsBinding(UniversalModelExpression fromElement, UniversalModelExpression toElement) {

    if (forwardBindings.get(fromElement) != null && forwardBindings.get(fromElement).contains(toElement)) {
      return true;
    }
    return false;
  }

  public UniversalTypeExpression getFromType() {
    return fromType;
  }

  public void setFromType(UniversalTypeExpression fromType) {
    this.fromType = fromType;
  }

  public UniversalTypeExpression getToType() {
    return toType;
  }

  public void setToType(UniversalTypeExpression toType) {
    this.toType = toType;
  }

  @SuppressWarnings({ "PMD", "ConsecutiveLiteralAppends" })
  public String toString() {
    StringBuffer buffer = new StringBuffer(25);
    buffer.append("Bindings:  { \n");
    int numberOfBindings = 0;
    for (Entry<UniversalModelExpression, Set<UniversalModelExpression>> entry : forwardBindings.entrySet()) {
      for (UniversalModelExpression toElememnt : entry.getValue()) {
        buffer.append(" (" + entry.getKey() + " ," + toElememnt + "), \n");
        numberOfBindings++;
      }
    }
    buffer.append('}');

    StringBuffer result = new StringBuffer(100);
    result.append("From: ");
    result.append(fromType);
    result.append("\n To:");
    result.append(toType);
    result.append("\n Unique first components: ");
    result.append(forwardBindings.keySet().size());
    result.append("\n Unique second components: ");
    result.append(backwardBindings.keySet().size());
    result.append("\n Total bindings: ");
    result.append(numberOfBindings);
    result.append('\n');

    result.append(buffer.toString());
    return result.toString();
  }

  /**
   * Joins the two binding sets as follows: For every tuple <b>(x,y)</b> in the first set and every tuple
   * <b>(y,z)</b> in the second set, the result binding set obtains a tuple <b>(x,z)</b>.
   * 
   * @param firstSet
   * @param secondSet
   * @return A binding set that 'extents' the two binding sets.
   */
  public static BindingSet join(BindingSet firstSet, BindingSet secondSet) {

    BindingSet result = new BindingSet();
    for (UniversalModelExpression toElement : firstSet.getAllToElements()) {
      if (!UniversalModelExpression.class.isInstance(toElement)) {
        throw new ModelException(ElasticeamException.GENERAL_ERROR,
            "A binding set which is internal to the evaluation has a derived type as its second component.");
      }
      if (secondSet.getAllFromElements().contains(toElement)) {
        for (UniversalModelExpression fromElement : firstSet.getFromBindings(toElement)) {
          for (UniversalModelExpression newToElement : secondSet.getToBindings(toElement)) {
            result.addBinding(fromElement, newToElement);
          }
        }
      }
    }
    result.setFromType(firstSet.getFromType());
    result.setToType(secondSet.getToType());
    return result;
  }

  /**
   * Creates a new binding set as follows: For each element <b>x</b> of the second component of the source binding set,
   * a binding <b>(x,x)</b> is introduced to the new binding set.
   * 
   * @param source
   *    The bindings set to use.
   * @return
   *    The new binding set, filled as described above.
   */
  public static BindingSet forkBySecondComponent(BindingSet source) {

    BindingSet result = new BindingSet();
    result.setFromType(source.getToType());
    result.setToType(source.getToType());

    for (UniversalModelExpression instance : source.getAllToElements()) {
      if (!UniversalModelExpression.class.isInstance(instance)) {
        throw new ModelException(ModelException.GENERAL_ERROR,
            "A binding set which is internal to the evaluation has a derived type as its second component.");
      }
      result.addBinding(instance, instance);
    }

    return result;
  }

}
