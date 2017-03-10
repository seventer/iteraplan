/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfigurationBase;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.recommendation.ViewpointConfigurationRecommendation.PrioritizedNamedElement;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;


/**
 * A recommendation for a viewpoint configuration in which the variables of an abstract viewmodel are
 * bound to candidates from an information model.
 */
public class ViewpointConfigurationRecommendation extends ViewpointConfigurationBase<Set<PrioritizedNamedElement>> {

  /**
   * Default constructor.
   * @param config the partial binding on which this configuration builds. may be an empty binding but not null
   */
  public ViewpointConfigurationRecommendation(ViewpointConfigurationBase<NamedExpression> config) {
    super();
    for (Entry<NamedExpression, NamedExpression> configEntry : config.entrySet()) {
      addRecommendation(configEntry.getKey(), configEntry.getValue(), 0);
    }
  }

  /**
   * Adds a binding candidate with a given priority for a given variable. 
   * @param avmElement the variable from the abstract viewmodel
   * @param recommendedElement the binding candidate for the variable
   * @param priority the priority of the recommendation
   */
  public final <S extends NamedExpression> void addRecommendation(S avmElement, S recommendedElement, int priority) {
    if (getMappingFor(avmElement) == null) {
      this.getMapping().put(avmElement, new HashSet<PrioritizedNamedElement>());
    }
    if (recommendedElement != null) {
      getMappingFor(avmElement).add(new PrioritizedNamedElement(recommendedElement, priority));
    }
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Entry<NamedExpression, Set<PrioritizedNamedElement>> entry : entrySet()) {
      sb.append(entry.getKey().getPersistentName());
      sb.append(" - ");
      for (PrioritizedNamedElement nl : entry.getValue()) {
        sb.append(nl.getNamedElement().getPersistentName());
        sb.append('(');
        sb.append(nl.getPriority());
        sb.append("),");
      }
      sb.append('\n');
    }
    return sb.toString();
  }

  /**
   * Represents a binding candidate for a variable with a priority from 0 to -Integer.MaxValue.
   */
  public static class PrioritizedNamedElement {
    private NamedExpression namedElement;
    private int             priority;

    /**
     * Default constructor, setting priority to 0.
     * @param namedElement the binding candidate
     */
    public PrioritizedNamedElement(NamedExpression namedElement) {
      this(namedElement, 0);
    }

    /**
     * Default constructor.
     * @param namedElement the binding candidate
     * @param prio the priority of the binding candidate
     */
    public PrioritizedNamedElement(NamedExpression namedElement, int prio) {
      this.namedElement = namedElement;
      this.priority = prio;
    }

    /**
     * @return the binding candidate
     */
    public NamedExpression getNamedElement() {
      return namedElement;
    }

    /**
     * @return the priority of the binding candidate
     */
    public int getPriority() {
      return priority;
    }

    /**{@inheritDoc}**/
    @Override
    public int hashCode() {
      return this.namedElement.hashCode();
    }

    /**{@inheritDoc}**/
    @Override
    public boolean equals(Object obj) {
      return obj instanceof PrioritizedNamedElement && ((PrioritizedNamedElement) obj).namedElement.equals(this.namedElement);
    }
  }
}
