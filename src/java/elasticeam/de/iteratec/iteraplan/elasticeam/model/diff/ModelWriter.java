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
package de.iteratec.iteraplan.elasticeam.model.diff;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange.TypeOfModelElementChange;


/**
 * Calculate classified {@link AbstractModelElementChange}s describing the differences between two {@link Model}s
 * and can apply these changes to the base model.
 */
public abstract class ModelWriter {

  private static final Logger LOGGER = Logger.getIteraplanLogger(ModelWriter.class);

  private final Model         base;
  private final Model         modified;
  private final Metamodel     metamodel;

  protected ModelWriter(Model base, Model modified, Metamodel metamodel) {
    this.base = base;
    this.modified = modified;
    this.metamodel = metamodel;
    validateCompatibility();
  }

  protected Model getBaseModel() {
    return base;
  }

  protected Model getModifiedModel() {
    return modified;
  }

  protected Metamodel getMetamodel() {
    return metamodel;
  }

  /**
  * Compares the two models, and classifies each {@link AbstractModelElementChange} by its {@link TypeOfModelElementChange}
  * Should be called at the end of each constructor within the implementing subclasses!
  */
  protected abstract void compareAndClassifyChanges();

  /**
   * General check, if the model (and its metamodels) seem to be compatible at all;
   */
  protected abstract void validateCompatibility();

  /**
   * Calculate and classify ({@link TypeOfModelElementChange}) {@link AbstractModelElementChange}s between two {@link Model}s
   * 
   * @return the classified {@link AbstractModelElementChange} by their {@link TypeOfModelElementChange}
   */
  public abstract Map<TypeOfModelElementChange, List<AbstractModelElementChange>> getChanges();

  /**
   * Applies all changes that have been detected and classified by the comparator instance
   * 
   * @param ignoredTypes 
   *    A {@link Set} of {@link TypeOfModelElementChange}s that will be ignored when applying changes
   * @param ignoredChanges 
   *    A {@link Set} of {@link AbstractModelElementChange} instances that will be ignored when applying changes
   * @return 
   *    A {@link Map} containing all {@link AbstractModelElementChange}s that have been applied, grouped by their {@link TypeOfModelElementChange}
   */
  public Map<TypeOfModelElementChange, List<AbstractModelElementChange>> applyChanges(Set<TypeOfModelElementChange> ignoredTypes,
                                                                                      Set<AbstractModelElementChange> ignoredChanges) {
    LOGGER.info("Applying model changes...");
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> allChanges = getChanges();
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> applied = Maps.newHashMap();

    for (TypeOfModelElementChange type : TypeOfModelElementChange.ALL) {
      List<AbstractModelElementChange> list = Lists.newArrayList();
      if (!ignoredTypes.contains(type)) {
        list = applyChangesForType(type, allChanges, ignoredChanges);
      }
      applied.put(type, list);
    }
    LOGGER.info("All applicable model changes applied.");
    return applied;
  }

  /**
   * Applies all changes of the given type, which are not contained in {@code ignoredChanges}.
   * @param type
   *          {@link TypeOfModelElementChange} of the changes to apply.
   * @param allChanges
   *          Map with all changes
   * @param ignoredChanges
   *          Changes to ignore
   * @return List of changes which were applied.
   */
  private List<AbstractModelElementChange> applyChangesForType(TypeOfModelElementChange type,
                                                               Map<TypeOfModelElementChange, List<AbstractModelElementChange>> allChanges,
                                                               Set<AbstractModelElementChange> ignoredChanges) {
    LOGGER.info("Applying changes for type \"{0}\"...", type);
    List<AbstractModelElementChange> applied = Lists.newArrayList();
    for (AbstractModelElementChange change : allChanges.get(type)) {
      if (!ignoredChanges.contains(change)) {
        LOGGER.info("Applying change \"{0}\"...", change);
        if (change.apply()) {
          applied.add(change);
          LOGGER.info("Change \"{0}\" successfully applied.", change);
          if (change.hasBacklog()) {
            LOGGER.info("Adding backlog for change \"{0}\"...", change);
            for (AbstractModelElementChange backlogChange : change.getBacklog()) {
              allChanges.get(backlogChange.getTypeOfModelDifference()).add(backlogChange);
              LOGGER.info("Change \"{0}\" of type \"{1}\" added to backlog.", backlogChange, backlogChange.getTypeOfModelDifference());
            }
          }
        }
        else {
          throw new ModelException(ModelException.GENERAL_ERROR, "Could not apply change: " + change);
        }
      }
      else {
        LOGGER.info("Change \"{0}\" is being ignored.", change);
      }
    }
    LOGGER.info("{0} changes of type \"{1}\" applied.", Integer.valueOf(applied.size()), type);
    return applied;
  }

}
