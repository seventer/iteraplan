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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;


/**
 * Basic interface for viewpoint building blocks (VBBs).
 */
public interface VBB {

  String ABSTRACT_VIEWMODEL_NS_PREFIX = "avm";
  String ABSTRACT_VIEWMODEL_NS_URI    = "uri-avm-ns";

  String EANNOTATION_SOURCE_OPTIONAL  = "uri-annotation-optional";

  /**
   * Enriches the supplied abstract viewmodel with the VBB-specific types, properties and relationships.
   * 
   * @param abstractViewModel The viewmodel to be enriched.
   * @param vpConfig The current configuration of the viewpoint, in which the VBB participates. Necessary to handle conditional elements in the abstract viewmodel.
   * @param prefix Name prefix designating the type to which the extension should be added.
   */
  void computeAbstractViewmodel(EditableMetamodel abstractViewModel, ViewpointConfiguration vpConfig, String prefix);

  /**
   * Determines the visual variables necessary for the VBB.
   * 
   * @param visualVariableEPackage The package of all visual variable classes.
   * @param prefix Prefix designating the name of the EClass containing all visual variables.
   * @return The EClass defining the visual variables.
   */
  EClass getEVisualVariableClass(EPackage visualVariableEPackage, String prefix);

  /**
   * Sets the VBB's visual variables to the specified values. The EObject must be an instance of the EClass specified in {@link VBB#getEVisualVariableClass(EPackage, String)}
   * @param visualVariables The values for the visual variables.
   */
  void setVisualVariables(EObject visualVariables);
}
