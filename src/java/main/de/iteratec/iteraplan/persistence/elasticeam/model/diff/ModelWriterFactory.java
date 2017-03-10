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
package de.iteratec.iteraplan.persistence.elasticeam.model.diff;

import java.util.Map;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.ModelWriter;


/**
 *  Factory for creating {@link ModelWriter} instances which can be used for comparison of two models; in addition, 
 *  the {@link ModelWriter}s can be used for applying changes to merge the two models
 */
public final class ModelWriterFactory {

  public static final ModelWriterFactory INSTANCE = new ModelWriterFactory();

  private ModelWriterFactory() {
    //hide constructor
  }

  /**
   * Used for comparing two models, its instances and their features
   * 
   * @param base
   *    The {@link Model} that is used for comparison
   * @param modified
   *    The {@link Model} holding the modifications (used for calculating differences)
   * @param metamodel
   *    The {@link Metamodel} for both models
   * @return
   *    A {@link ModelWriter} instance that calculates differences between the model-data and is able to write applicable changes to the base model, when when {@link ModelWriter#applyChanges(java.util.Set, java.util.Set)} is called 
   *    
   */
  public ModelWriter getWriter(Model base, Model modified, Metamodel metamodel) {
    return new EMFModelWriter(base, modified, metamodel);
  }

  /**
   * Used for comparing a model with the loaded iteraplan data;
   * Similar to {@link #getWriter(Model, Model, Metamodel)}, but when calling 
   * applyChanges on the returned {@link ModelWriter}, the changes will be written (persisted!) to the iteraplan database!
   * 
   * @param mapping
   *    The meta-information about the iteraplan landscape metamodel 
   * @param model
   *    The model representing the persisted data
   * @param instanceMapping
   *    The {@link Map} connecting the iteraplan objects to its Elasticeam representation ({@link UniversalModelExpression}s)
   * @param modified
   *    The in memory model, holding the modified instances
   * @return
   *    A {@link ModelWriter} instance that calculates differences between the model-data; persists any changes to the database, when {@link ModelWriter#applyChanges(java.util.Set, java.util.Set)} is called
   */
  public ModelWriter getPersister(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping, Model modified) {
    return new EMFModelWriter(mapping, model, instanceMapping, modified);
  }
}
