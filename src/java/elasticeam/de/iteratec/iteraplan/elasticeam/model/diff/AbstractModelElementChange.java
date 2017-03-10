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
import java.util.Queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;


/**
 * Abstract base class for Model changes
 */
public abstract class AbstractModelElementChange {

  private final Model                             currentModel;
  private final Model                             modifiedModel;
  private final Metamodel                         metamodel;
  private boolean                                 applied;
  private final Queue<AbstractModelElementChange> backlog;

  public enum TypeOfModelElementChange {
    TECHNICAL("technical", 0), ADD_INSTANCE("addInstance", 1), ADD_RELATIONSHIP_OBJECT("addRelationshipObject", 2), UPDATE_PROPERTY_VALUE(
        "updatePropertyValue", 3), UPDATE_LINK("updateLink", 4), REMOVE_INSTANCE("removeInstance", 5), UNKNOWN("unknown", 6);

    private String                                     typeOfDiff;
    private int                                        position;

    public static final List<TypeOfModelElementChange> ALL = ImmutableList.of(TECHNICAL, ADD_INSTANCE, ADD_RELATIONSHIP_OBJECT,
                                                               UPDATE_PROPERTY_VALUE, UPDATE_LINK, REMOVE_INSTANCE, UNKNOWN);

    private TypeOfModelElementChange(String typeOfDiff, int position) {
      this.typeOfDiff = typeOfDiff;
      this.position = position;
    }

    @Override
    public String toString() {
      return position + ". " + typeOfDiff;
    }
  }

  public abstract TypeOfModelElementChange getTypeOfModelDifference();

  public abstract boolean isApplicable();

  public abstract boolean isActualChange();

  public abstract boolean apply();

  /**
   * 
   * Default constructor.
   * @param currentModel
   * @param modifiedModel
   * @param metamodel
   */
  protected AbstractModelElementChange(Model currentModel, Model modifiedModel, Metamodel metamodel) {
    this.currentModel = currentModel;
    this.modifiedModel = modifiedModel;
    this.metamodel = metamodel;
    this.applied = false;
    this.backlog = Queues.newConcurrentLinkedQueue();
  }

  public Model getCurrentModel() {
    return currentModel;
  }

  public Model getModifiedModel() {
    return modifiedModel;
  }

  public Metamodel getMetamodel() {
    return metamodel;
  }

  public boolean isApplied() {
    return applied;
  }

  public boolean hasBacklog() {
    return !backlog.isEmpty();
  }

  public Queue<AbstractModelElementChange> getBacklog() {
    return backlog;
  }

  protected void setApplied(boolean applied) {
    this.applied = applied;
  }

  @Override
  public String toString() {
    return getTypeOfModelDifference().typeOfDiff;
  }

}
