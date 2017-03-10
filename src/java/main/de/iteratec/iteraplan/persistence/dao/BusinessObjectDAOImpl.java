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
package de.iteratec.iteraplan.persistence.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.model.BusinessObject;


/**
 * Implementation of the DAO interface {@link BusinessObjectDAO}.
 */
public class BusinessObjectDAOImpl extends GenericHierarchicalDAO<BusinessObject, Integer> implements BusinessObjectDAO {

  /** {@inheritDoc} */
  public List<BusinessObject> getBusinessObjectsWithoutGeneralisation() {
    return executeNamedQuery("getBusinessObjectsWithoutGeneralisation");
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(BusinessObject entity) {
    super.onBeforeDelete(entity);

    // Find all descendants of the given entity.
    Set<BusinessObject> descendants = new HashSet<BusinessObject>(0);
    entity.getDescendants(entity, descendants);

    // Find all the descendant's specialisations (if there are descendants). Note: This has to
    // happen prior to the call of the superclass's onBeforeDelete()-method in order to avoid a
    // DataIntegrityViolationException (= entity references a not-null or transient value).
    List<BusinessObject> specialisations = new ArrayList<BusinessObject>(0);
    if (descendants.size() > 0) {
      specialisations = executeNamedQuery("getSpecialisationsForSetOfBusinessObjects", "set", descendants);
    }

    // In case there are no descendants and the entity has specialisations, add them to the
    // set of specialisations. This is necessary in order to remove the reference between
    // the specialisations and the entity to delete (generalisation). Otherwise the foreign-
    // key constraint would be violated.
    if (entity.getSpecialisations().size() > 0) {
      specialisations.addAll(entity.getSpecialisations());
    }

    // Remove the parent/child references between each descendant.
    for (BusinessObject bo : descendants) {
      bo.removeParent();
      bo.getChildren().clear();
    }

    // Create a new set comprised of the entity's descendants as well as their specialisations
    // in order to consistently remove the generalisation/specialisation references between each
    // element in the set. Failing to do so may result in a ConstraintViolationException in the
    // following situation: Element A is tried to be deleted. Because the 'generalisation' end
    // of the generalisation/specialisation association is the owner of the association, calls
    // like 'ElementA.getSpecialisations().clear' do not update the association. Thus, references
    // to ElementA from its specialisations won't be removed. It is therefore not enough to merely
    // remove generalisation/specialisation references between the entity's descendants (as done
    // with the parent/child association). Their specialisatioins must be taken into account as
    // well.
    Set<BusinessObject> set = new HashSet<BusinessObject>();
    set.addAll(descendants);
    set.addAll(specialisations);

    // Remove the generalisation/specialisation references between each element in the set.
    for (BusinessObject bo : set) {
      bo.setGeneralisation(null); // Updating the owner side of the association is sufficient.
    }

    // Manually delete the entity's descendants and manually remove all inverse associations.
    for (BusinessObject bo : descendants) {
      getHibernateTemplate().delete(bo);
    }

    // Remove the parent and clear the list of children.
    entity.removeParent();
    entity.getChildren().clear();

    // Remove the generalisation (the specialisations have been removed above).
    entity.removeGeneralisation();
  }

}