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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;


/**
 * Abstract base class for both viewpoint configurations and configuration recommendations.
 *
 * @param <T> type of the elements mapped to elements of the abstract viewmodel.
 */
public abstract class ViewpointConfigurationBase<T> {
  private Map<NamedExpression, T> mapping;

  protected ViewpointConfigurationBase() {
    this.mapping = new HashMap<NamedExpression, T>();
  }

  protected <S extends ViewpointConfigurationBase<T>> ViewpointConfigurationBase(S toClone) {
    this();
    this.mapping.putAll(toClone.getMapping());
  }

  protected Map<NamedExpression, T> getMapping() {
    return mapping;
  }

  /**
   * Determines, whether a given variable from the abstract viewmodel is mapped or not. 
   * @param variable the variable from the abstract viewmodel.
   * @return true, when a mapping is configured for the variable; false, if not.
   */
  public <S extends NamedExpression> boolean hasValueFor(S variable) {
    return this.mapping.containsKey(variable) && this.mapping.get(variable) != null;
  }

  /**
   * Returns the mapping for a given variable from the abstract viewmodel.
   * @param variable the variable from the abstract viewmodel.
   * @return the mapping configured for the variable; may be null
   */
  public <S extends NamedExpression> T getMappingFor(S variable) {
    return this.mapping.get(variable);
  }

  /**
   * @return the set of all mapping currently defined in the configuration or recommendation
   */
  public Set<Entry<NamedExpression, T>> entrySet() {
    return Collections.unmodifiableSet(this.mapping.entrySet());
  }
}