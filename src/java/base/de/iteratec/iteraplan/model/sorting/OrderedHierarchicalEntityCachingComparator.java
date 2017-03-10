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
package de.iteratec.iteraplan.model.sorting;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;


/**
 * Implementation of the {@link java.util.Comparator} interface to sort hierarchical entities.
 * If two entities have the same parent, they will be sorted by their position.
 * The elements to compare also needs an unique id which is not null. The hierarchie needs also
 * a common root element, so {@link InformationSystemRelease} will not work.
 *
 * @author sip
 */
public class OrderedHierarchicalEntityCachingComparator<T extends HierarchicalEntity<T>> implements Comparator<T>, Serializable {

  private static final Logger           LOGGER           = Logger.getIteraplanLogger(OrderedHierarchicalEntityCachingComparator.class);
  private static final long             serialVersionUID = 720732131078307852L;

  private final Map<Integer, Integer[]> cache            = Maps.newHashMap();

  /**
   * {@inheritDoc}
   */
  public int compare(T entity1, T entity2) {
    Integer[] hierarchieArray1 = getHierarchieArray(entity1);
    Integer[] hierarchieArray2 = getHierarchieArray(entity2);
    int length = Math.min(hierarchieArray1.length, hierarchieArray2.length);
    for (int i = 0; i < length; i++) {
      int val = hierarchieArray1[i].intValue() - hierarchieArray2[i].intValue();
      if (val != 0) {
        return val;
      }
    }
    return hierarchieArray1.length - hierarchieArray2.length;
  }

  /**
   * Uses caching and dynamic programming to get an Array which represents the position in the hierarchie.
   * @param entity
   * @return hierarchie position as array
   */
  private Integer[] getHierarchieArray(T entity) {
    Integer id = entity.getId();
    if (id == null) {
      LOGGER.error("Error occured while sorting: id must not be null");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR);
    }

    if (cache.containsKey(id)) {
      return cache.get(id);
    }

    T parent = entity.getParentElement();
    Integer[] entityArray;
    if (parent != null) {
      Integer[] parentArray = getHierarchieArray(parent);
      int length = parentArray.length;
      entityArray = new Integer[length + 1];
      System.arraycopy(parentArray, 0, entityArray, 0, length);
      entityArray[length] = Integer.valueOf(parent.findChildPos(entity));
    }
    else {
      entityArray = new Integer[0];
    }

    cache.put(id, entityArray);
    return entityArray;
  }
}