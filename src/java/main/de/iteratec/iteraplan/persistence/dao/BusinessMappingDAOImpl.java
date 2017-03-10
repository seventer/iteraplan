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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BusinessMapping;


/**
 * Implementation of the DAO interface {@link BusinessMappingDAO}.
 */
public class BusinessMappingDAOImpl extends GenericBaseDAO<BusinessMapping, Integer> implements BusinessMappingDAO {

  private static final Logger LOGGER = Logger.getIteraplanLogger(BusinessMappingDAOImpl.class);

  /**
   * @param connectionProperty
   *          from which side are the business mappings identified
   * @param id
   *          the Building block ID
   */
  private List<BusinessMapping> getBusinessMappingsConnectedTo(final String connectionProperty, final Integer id) {
    Criterion c = Restrictions.eq(connectionProperty + ".id", id);
    return getBusinessMappings(c);
  }

  private List<BusinessMapping> getBusinessMappings(final Criterion restrictions) {
    HibernateCallback<List<BusinessMapping>> callback = new BusinessMappingsCallback(restrictions);
    return getHibernateTemplate().execute(callback);
  }

  public List<BusinessMapping> getBusinessMappingsConnectedToISR(final Integer id) {
    return getBusinessMappingsConnectedTo("informationSystemRelease", id);
  }

  public List<BusinessMapping> getBusinessMappingsConnectedToBU(final Integer id) {
    return getBusinessMappingsConnectedTo("businessUnit", id);
  }

  public List<BusinessMapping> getBusinessMappingsConnectedToBP(final Integer id) {
    return getBusinessMappingsConnectedTo("businessProcess", id);
  }

  public List<BusinessMapping> getBusinessMappingsConnectedToProduct(final Integer id) {
    return getBusinessMappingsConnectedTo("product", id);
  }

  public BusinessMapping getBusinessMappingConnectedToProductAndBUAndBPAndISR(final Integer prodId, final Integer buId, final Integer bpId,
                                                                              final Integer isrId) {
    Criterion c = Restrictions.and(
        Restrictions.eq("product.id", prodId),
        Restrictions.and(Restrictions.eq("businessUnit.id", buId),
            Restrictions.and(Restrictions.eq("businessProcess.id", bpId), Restrictions.eq("informationSystemRelease.id", isrId))));
    List<BusinessMapping> mappings = getBusinessMappings(c);
    if (mappings.size() > 1) {
      LOGGER.error("Found more than one assigned business mapping with product id '" + prodId + "' and business unit id '" + buId
          + "' and business process id '" + bpId + "' and information system release id '" + isrId
          + "'. A maximum of one business mapping is logically allowed.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    
    return mappings.isEmpty() ? null : mappings.get(0);
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  /** {@inheritDoc} */
  public int deleteOrphanedBusinessMappings() {
    Criterion isrNull = Restrictions.isNull("informationSystemRelease.id");
    Criterion buNull = Restrictions.isNull("businessUnit.id");
    Criterion bpNull = Restrictions.isNull("businessProcess.id");
    Criterion prodNull = Restrictions.isNull("product.id");
    Junction invalidBusinessMappings = Restrictions.disjunction().add(isrNull).add(buNull).add(bpNull).add(prodNull);

    List<BusinessMapping> orphanedMappings = getBusinessMappings(invalidBusinessMappings);
    for (BusinessMapping orphan : orphanedMappings) {
      delete(orphan);
    }

    return orphanedMappings.size();
  }

  private static final class BusinessMappingsCallback implements HibernateCallback<List<BusinessMapping>> {
    private final Criterion restrictions;

    public BusinessMappingsCallback(Criterion restrictions) {
      this.restrictions = restrictions;
    }

    @SuppressWarnings("unchecked")
    public List<BusinessMapping> doInHibernate(Session session) {
      Criteria c = session.createCriteria(BusinessMapping.class);
      c.add(restrictions);
      return c.list();
    }
  }

}