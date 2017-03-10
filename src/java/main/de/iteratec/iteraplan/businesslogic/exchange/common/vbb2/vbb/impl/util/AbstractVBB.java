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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl.util;

import static org.eclipse.emf.ecore.EcoreFactory.eINSTANCE;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.VBB;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;


/**
 * Abstract base class for VBBs of any type. Provides basic mechanisms for handling children-VBBs in
 * constructing the abstract viewmodel.
 */
public abstract class AbstractVBB implements VBB {

  private Map<String, VBB> children;
  private EClass           visualVariables;
  private String           baseUrl;

  /**
   * Default constructor.
   */
  protected AbstractVBB() {
    this.children = Maps.newHashMap();
  }

  /** {@inheritDoc}. */
  public void computeAbstractViewmodel(EditableMetamodel abstractViewModel, ViewpointConfiguration vpConfig, String prefix) {
    for (Entry<String, VBB> child : this.children.entrySet()) {
      child.getValue().computeAbstractViewmodel(abstractViewModel, vpConfig, (prefix.isEmpty() ? prefix : prefix + '.') + child.getKey());
    }
    computeMyAbstractViewmodel(abstractViewModel, vpConfig, prefix);
  }

  protected void putChild(String key, VBB child) {
    this.children.put(key, child);
  }

  protected abstract void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix);

  /** {@inheritDoc}. */
  public void setVisualVariables(EObject visualVariables) {
    if (this.visualVariables != null && this.visualVariables.isInstance(visualVariables)) {
      for (EReference ref2Child : this.visualVariables.getEAllReferences()) {
        if (this.children.containsKey(ref2Child.getName())) {
          this.children.get(ref2Child.getName()).setVisualVariables((EObject) visualVariables.eGet(ref2Child));
        }
      }
      BeanMap bm = new BeanMap(this);
      for (EAttribute att : this.visualVariables.getEAllAttributes()) {
        VisualVariableHelper.setVisualVariableValue(bm, visualVariables, att);
      }
    }
  }

  /** {@inheritDoc}. */
  public EClass getEVisualVariableClass(EPackage visualVariableEPackage, String prefix) {
    this.visualVariables = EcoreFactory.eINSTANCE.createEClass();
    this.visualVariables.setName(prefix + this.getClass().getSimpleName());
    if (!visualVariableEPackage.getEClassifiers().contains(this.visualVariables)) {
      visualVariableEPackage.getEClassifiers().add(this.visualVariables);
    }
    VisualVariableHelper.addAllVisualVariables(getClass(), this.visualVariables);
    for (Entry<String, VBB> child : this.children.entrySet()) {
      EReference ref2Child = eINSTANCE.createEReference();
      ref2Child.setLowerBound(1);
      ref2Child.setUpperBound(1);
      ref2Child.setName(child.getKey());
      ref2Child.setContainment(true);
      String newPrefix = prefix.isEmpty() ? child.getKey() : prefix + '.' + child.getKey();
      ref2Child.setEType(child.getValue().getEVisualVariableClass(visualVariableEPackage, newPrefix));
      this.visualVariables.getEStructuralFeatures().add(ref2Child);
    }

    return this.visualVariables;
  }

  protected String createGeneratedInformationString() {
    Locale locale = UserContext.getCurrentLocale();
    String date = DateUtils.formatAsStringToLong(new Date(), locale);
    String appName = MessageAccess.getStringOrNull("global.applicationname", locale);
    String buildId = IteraplanProperties.getProperties().getBuildId();
    return "Generated " + date + " by " + appName + " " + buildId;
  }

  /**
   * @return the baseUrl used for creating links at symbols in the resulting visualization
   */
  @VisualVariable
  public final String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Sets the baseUrl used for creating links at symbols in the resulting visualization.
   * @param baseUrl the baseUrl for links.
   */
  public final void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
}
