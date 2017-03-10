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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.InnerVBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.AVisualizationObject;


/**
 * Inner VBB for creating arbitrary visualization objects.
 * @param <T>
 */
public class CreateVisualizationObject<T extends AVisualizationObject> extends AbstractVBB implements InnerVBB<T> {
  private static final Logger       LOGGER    = Logger.getLogger(CreateVisualizationObject.class);

  private static int                idCounter = 0;
  private SubstantialTypeExpression avmClass;

  private Class<? extends T>        vObjectClass;

  /**
   * Default constructor.
   */
  public CreateVisualizationObject() {
    super();
  }

  protected SubstantialTypeExpression getAvmClass() {
    return avmClass;
  }

  /**{@inheritDoc}**/
  public T transform(UniversalModelExpression instance, Model model, ViewpointConfiguration config) {
    T result = null;
    try {
      result = this.vObjectClass.newInstance();
      result.setId(Integer.toString(idCounter++));
    } catch (InstantiationException e) {
      LOGGER.error("Could not instanciate AVisualizationObject", e);
    } catch (IllegalAccessException e) {
      LOGGER.error("Could not instanciate AVisualizationObject", e);
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    this.avmClass = viewmodel.createSubstantialType(prefix);
  }

  /**
   * @return the type of visualization object to create.
   */
  @VisualVariable
  public Class<? extends T> getVObjectClass() {
    return vObjectClass;
  }

  /**
   * Sets the type of visualization object to create.
   * @param vObjectClass the type of visualization object to create.
   */
  public final void setVObjectClass(Class<? extends T> vObjectClass) {
    this.vObjectClass = vObjectClass;
  }
}