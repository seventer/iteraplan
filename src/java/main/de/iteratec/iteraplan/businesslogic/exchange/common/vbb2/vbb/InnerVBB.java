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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.AVisualizationObject;


/**
 * Interface for a VBB that cannot act as base VBB for a viewpoint.
 *
 * @param <T> Type specifying the result of the transformation.
 */
public interface InnerVBB<T extends AVisualizationObject> extends VBB {

  /**
   * Transforms an input object from a resource and applies the supplied viewpoint configuration.
   * @param instance The input object to be transformed.
   * @param model The resource containing the input object. Needed, if attribute values are determined or traversals over relationships are performed.
   * @param config Configuration for the VBB and all its children-VBBs.
   * @return The visual result object.
   */
  T transform(UniversalModelExpression instance, Model model, ViewpointConfiguration config);
}
