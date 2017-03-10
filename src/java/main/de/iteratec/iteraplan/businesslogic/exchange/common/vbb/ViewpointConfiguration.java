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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;


/**
 * Represents the configuration of a viewpoint.
 */
public class ViewpointConfiguration extends ViewpointConfigurationBase<NamedExpression> {

  private static final String EMPTYPACKAGEPREFIX = "";

  private final String        viewpointName;
  private EObject             visualVariables;
  private EClass              visualVariableClass;

  /**
   * @param viewpointDefinition is the viewpoint's name 
   */
  public ViewpointConfiguration(String viewpointDefinition) {
    super();
    this.viewpointName = viewpointDefinition;
  }

  /**
   * @param toClone the ViewpointConfiguration that should be cloned.
   */
  public ViewpointConfiguration(ViewpointConfiguration toClone) {
    super(toClone);
    this.viewpointName = toClone.viewpointName;
  }

  final void initVisualVariableClass(VBB vbb) {
    this.visualVariableClass = vbb.getEVisualVariableClass(VisualVariableHelper.createVisualVariableEPackage(viewpointName), EMPTYPACKAGEPREFIX);
  }

  /**
   * @return The EClass specifying the visual variables supplied by the viewpoint.
   */
  public EClass getVisualVariableDefinition() {
    return this.visualVariableClass;
  }

  private static void setVisualVariable(EObject eObject, int index, String[] names, String value) {
    if (value != null && !value.isEmpty()) {
      if (index == names.length - 1) {
        EAttribute attribute = (EAttribute) eObject.eClass().getEStructuralFeature(names[index]);
        if (attribute != null) {
          eObject.eSet(attribute, EcoreUtil.createFromString(attribute.getEAttributeType(), value));
        }
      }
      else {
        EReference reference = (EReference) eObject.eClass().getEStructuralFeature(names[index]);
        if (eObject.eGet(reference) == null) {
          eObject.eSet(reference, EcoreUtil.create(reference.getEReferenceType()));
        }
        setVisualVariable((EObject) eObject.eGet(reference), index + 1, names, value);
      }
    }
  }

  /**
   * @param possibleVisualVariables The key-value mappings that represent visual variables. The keys are hierarchic separated by a dot.
   */
  public void setVisualVariables(Map<String, String> possibleVisualVariables) {
    this.visualVariables = EcoreUtil.create(this.visualVariableClass);
    for (Entry<String, String> visualVariable : possibleVisualVariables.entrySet()) {
      setVisualVariable(this.visualVariables, 0, visualVariable.getKey().split("\\."), visualVariable.getValue());
    }
  }

  /**
   * @return An EObject holding the values of all visual variables. Is an instance of the EClass returned by {@link ViewpointConfiguration#getVisualVariableDefinition()}.
   */
  public EObject getVisualVariables() {
    return this.visualVariables;
  }

  /**
   * Maps a variable from the abstract viewmodel to an actual value from the information model.
   * @param variable a variable (type, attribute, relationship) from the abstract viewmodel
   * @param value the corresponding value (type, attribute, relationship) from the information model
   */
  public <S extends NamedExpression> void setMappingFor(S variable, S value) {
    this.getMapping().put(variable, value);
  }

  /**
   * @return the name of the configured viewpoint
   */
  public String getViewpointName() {
    return this.viewpointName;
  }

  /**
   * @return an empty Package reading for computing the abstract view model for a VBB.
   */
  public EditableMetamodel createEmptyAVM() {
    return new EMFMetamodel("AVM4" + getViewpointName(), true);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    List<Entry<NamedExpression, NamedExpression>> entries = new ArrayList<Entry<NamedExpression, NamedExpression>>(entrySet());
    Collections.sort(entries, new NamedExpressionComparator());

    sb.append(this.viewpointName);
    sb.append(": ");

    for (Entry<NamedExpression, NamedExpression> entry : entries) {
      sb.append(entry.getKey().getPersistentName());
      sb.append('=');
      sb.append(entry.getValue().getPersistentName());
      sb.append(", ");
    }
    return sb.toString();
  }

  private static final class NamedExpressionComparator implements Comparator<Entry<NamedExpression, NamedExpression>>, Serializable {
    private static final long serialVersionUID = -1336939294504157048L;

    public int compare(Entry<NamedExpression, NamedExpression> o1, Entry<NamedExpression, NamedExpression> o2) {
      return o1.getKey().getPersistentName().compareTo(o2.getKey().getPersistentName());
    }
  }
}