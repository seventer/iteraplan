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
import java.util.Collection;

import org.eclipse.emf.compare.diff.metamodel.ReferenceChange;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.diff.LinkChange;


/**
 *
 */
public class EMFLinkChange extends LinkChange {

  private static final Logger                           LOGGER = Logger.getIteraplanLogger(EMFLinkChange.class);

  private final BiMap<Object, UniversalModelExpression> instanceMapping;
  private final ReferenceChange                         referenceChange;
  private final HbMappedClass                           hbClass;

  protected EMFLinkChange(IteraplanMapping mapping, Model model, BiMap<Object, UniversalModelExpression> instanceMapping, Model modifiedModel,
      UniversalModelExpression source, RelationshipEndExpression relationshipEnd, UniversalModelExpression oldTarget,
      UniversalModelExpression newTarget, ReferenceChange referenceChange) {
    super(model, modifiedModel, mapping.getMetamodel(), source, relationshipEnd, oldTarget, newTarget);
    this.instanceMapping = instanceMapping;
    //can also be null, in case of initialization for backlog changes;
    this.referenceChange = referenceChange;
    this.hbClass = HbClassHelper.getHbClass(mapping, relationshipEnd.getHolder());
  }

  public ReferenceChange getReferenceChange() {
    return referenceChange;
  }

  @Override
  public boolean apply() {
    if (super.apply()) {
      try {
        persist();
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
      return true;
    }
    throw new ModelException(ModelException.GENERAL_ERROR, "could not persist change " + this);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void persist() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Object source = instanceMapping.inverse().get(getSource());
    Object oldTarget = instanceMapping.inverse().get(getOldTarget());
    Object newTarget = instanceMapping.inverse().get(getNewTarget());

    HbMappedProperty property = hbClass.getProperty(getRelationshipEnd().getPersistentName());

    Object connected = property.getGetMethod().invoke(source);
    if (connected instanceof Collection) {
      Collection collection = (Collection) connected;
      if (collection != null) {
        if (oldTarget != null) {
          collection.remove(oldTarget);
        }
        if (newTarget != null) {
          collection.add(newTarget);
        }
      }
    }
    else {
      property.getSetMethod().invoke(source, newTarget);
    }
  }
}
