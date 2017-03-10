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
package de.iteratec.iteraplan.model;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;


/**
 * An association class to link two building block classes with each other. In general, both ends may also be of
 * the same type, i.e. a self-referential association.
 * Association entities are building blocks themselves, so that they can carry attributes, have access permissions
 * defined etc.
 * <p>
 * The basic idea is that an association has a left end and a right end, which you can address consistently. For
 * concrete subclasses it is advised to use more specific names for both ends, though. Avoid exposing the left/right end
 * terminology to users.
 * <p>
 * <b>Note to implementers:</b> You cannot map this class in Hibernate directly, but you have to create a non-abstract
 * subclass. Hibernate cannot properly deal with the generics otherwise. See {@link Tcr2IeAssociation} for an example.
 * @param <L> The left-end side class of a concrete association.
 * @param <R> The right-end side class of a concrete association.
 */
public abstract class AbstractAssociation<L extends BuildingBlock, R extends BuildingBlock> extends BuildingBlock {

  private static final long serialVersionUID = 1L;

  private L                 leftEnd;
  private R                 rightEnd;

  /**
   * Concatenates the left and the right ends' identity strings
   */
  public String getIdentityString() {
    return leftEnd + " <-> " + rightEnd;
  }

  /**
   * Returns an empty string. Associations have no description.
   */
  @Override
  public String getDescription() {
    return "";
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    result = prime * result + ((leftEnd == null) ? 0 : leftEnd.hashCode());
    result = prime * result + ((rightEnd == null) ? 0 : rightEnd.hashCode());

    if (leftEnd == null && rightEnd == null) {
      result = super.hashCode();
    }

    return result;
  }

  /**
   * We consider two association objects equal if they have equal left ends and equal right ends.
   * The ID of the association itself is irrelevant.
   * <dd>This may have implication if different attributes are attached to these different objects.</dd>
   * @see Object#equals(Object)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractAssociation)) {
      return false;
    }
    AbstractAssociation other = (AbstractAssociation) obj;
    if (leftEnd == null) {
      if (other.leftEnd != null) {
        return false;
      }
    }
    else if (!leftEnd.equals(other.leftEnd)) {
      return false;
    }
    if (rightEnd == null) {
      if (other.rightEnd != null) {
        return false;
      }
    }
    else if (!rightEnd.equals(other.rightEnd)) {
      return false;
    }
    return true;
  }

  /**
   * Sets the left-end object on this association. The left-end object is not changed.
   */
  protected void setLeftEnd(L leftEnd) {
    this.leftEnd = leftEnd;
  }

  /**
   * Returns the left-end object from this association.
   */
  public L getLeftEnd() {
    return leftEnd;
  }

  /**
   * Sets the right-end object on this association. The right-end object is not changed.
   */
  protected void setRightEnd(R rightEnd) {
    this.rightEnd = rightEnd;
  }

  /**
   * Returns the right-end object from this association.
   */
  public R getRightEnd() {
    return rightEnd;
  }

  /**
   * This method can be called, if both ends of this association are set. On these two
   * ends this association object will be added.
   */
  public abstract void connect();

  /**
   * This is the complement method to {@link #connect()}. It will remove itself from the
   * two ends, which are connected.
   */
  public abstract void disconnect();

  protected <T extends BuildingBlock> void disconnectFromElementInCollection(BuildingBlock element, Collection<T> collection) {
    if (element != null) {
      Set<T> newCollection = Sets.newHashSet(collection);
      collection.clear();
      for (T bb : newCollection) {
        if (!getId().equals(bb.getId())) {
          collection.add(bb);
        }
      }
    }
  }

}
