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

import java.lang.reflect.InvocationTargetException;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;


/**
 * Helper to handle {@link HbMappedClass}es and {@link HbMappedProperty}s
 */
public final class HbClassHelper {

  private static final Logger LOGGER = Logger.getIteraplanLogger(HbClassHelper.class);

  private HbClassHelper() {
    // do nothing
  }

  /**
   * Find {@link HbMappedClass} for a {@link UniversalTypeExpression}.
   * @return the {@link HbMappedClass}
   */
  public static HbMappedClass getHbClass(IteraplanMapping mapping, UniversalTypeExpression expression) {
    HbMappedClass result = null;
    if (expression instanceof SubstantialTypeExpression) {
      result = mapping.getSubstantialTypes().get(expression);
      if (result.hasReleaseClass()) {
        result = result.getReleaseClass();
      }
    }
    else {
      result = mapping.getRelationshipTypes().get(expression);
    }
    return result;
  }

  /**
   * Find {@link HbMappedProperty} property for a {@link PropertyExpression}.
   * @return the {@link HbMappedProperty}
   */
  public static HbMappedProperty findHbMappedProperty(HbMappedClass hbClass, PropertyExpression<?> expression) {
    String persistentName = expression.getPersistentName();

    HbMappedProperty hbProp = hbClass.getProperty(persistentName);
    if (hbProp == null && hbClass.isReleaseClass() && hbClass.getReleaseBase().getProperty(persistentName) != null) {
      hbProp = hbClass.getReleaseBase().getProperty(persistentName);
    }
    return hbProp;
  }

  /**
   * Returns the object the given {@link HbMappedProperty property} is owned by, which is either
   * the given instance or its base-class instance (i.e. InformationSystem for InformationSystemRelease).
   * @param hbClass
   *          {@link HbMappedClass} for the given instance 
   * @param prop
   *          {@link HbMappedProperty} for the property to set
   * @param instance
   *          The object the property is supposed to be set on
   * @return Either the given {@code instance} or its base-class instance (i.e. InformationSystem for InformationSystemRelease),
   *         whichever is applicable.
   */
  public static Object getOwningInstance(HbMappedClass hbClass, HbMappedProperty prop, Object instance) {
    Object result = instance;
    Class<?> propContainingClass = prop.getContainingClass().getMappedClass();
    if (propContainingClass != null && !propContainingClass.isInstance(result) && hbClass.isReleaseClass()) {
      try {
        result = hbClass.getReleaseBaseProperty().getGetMethod().invoke(result);
      } catch (IllegalArgumentException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (IllegalAccessException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      } catch (InvocationTargetException e) {
        LOGGER.error(e);
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, e);
      }
    }
    return result;
  }

}
