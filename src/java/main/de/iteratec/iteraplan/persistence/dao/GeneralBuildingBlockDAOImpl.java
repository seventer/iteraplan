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
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;


/**
 * Implementation of the DAO interface {@link GeneralBuildingBlockDAO}.
 */
public final class GeneralBuildingBlockDAOImpl extends GenericBaseDAO<BuildingBlock, Integer> implements GeneralBuildingBlockDAO {

  /** {@inheritDoc} */
  public BuildingBlock getBuildingBlock(final Integer id, final Class<? extends BuildingBlock> clazz) {
    if (id == null || clazz == null) {
      throw new IllegalArgumentException("All parameters are required and must not be null.");
    }

    return getHibernateTemplate().load(clazz, id);
  }

  /** {@inheritDoc} */
  public List<BuildingBlock> getBuildingBlocksByType(TypeOfBuildingBlock type) {
    if (type == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    return executeNamedQuery("getBuildingBlocksByType", "type", type);
  }

  /**
   * {@inheritDoc}
   * @deprecated do not use this method any more, because it a performance disaster....
   */
  public List<BuildingBlock> loadBuildingBlocks(final Collection<Integer> identifiers) {
    if (identifiers == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    if (identifiers.size() == 0) {
      return new ArrayList<BuildingBlock>();
    }

    List<BuildingBlock> list = new ArrayList<BuildingBlock>();
    list.addAll(loadBuildingBlocks(identifiers, ArchitecturalDomain.class));
    list.addAll(loadBuildingBlocks(identifiers, BusinessFunction.class));
    list.addAll(loadBuildingBlocks(identifiers, BusinessObject.class));
    list.addAll(loadBuildingBlocks(identifiers, BusinessProcess.class));
    list.addAll(loadBuildingBlocks(identifiers, TechnicalComponentRelease.class));
    list.addAll(loadBuildingBlocks(identifiers, InfrastructureElement.class));
    list.addAll(loadBuildingBlocks(identifiers, BusinessDomain.class));
    list.addAll(loadBuildingBlocks(identifiers, InformationSystemDomain.class));
    list.addAll(loadBuildingBlocks(identifiers, InformationSystemRelease.class));
    list.addAll(loadBuildingBlocks(identifiers, Project.class));
    list.addAll(loadBuildingBlocks(identifiers, BusinessUnit.class));
    list.addAll(loadBuildingBlocks(identifiers, Product.class));
    list.addAll(loadBuildingBlocks(identifiers, InformationSystemInterface.class));

    return list;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<BuildingBlock> loadBuildingBlocks(final Collection<Integer> identifiers, final Class<? extends BuildingBlock> clazz) {
    if (identifiers == null || clazz == null) {
      throw new IllegalArgumentException("All parameters are required and must not be null.");
    }

    if (identifiers.size() == 0) {
      return new ArrayList<BuildingBlock>(0);
    }

    HibernateCallback<List<?>> callback = new LoadBuildingBlocksCallback(clazz, identifiers);

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }

  private static final class LoadBuildingBlocksCallback implements HibernateCallback<List<?>> {
    private final Class<? extends BuildingBlock> clazz;
    private final Collection<Integer>            identifiers;

    public LoadBuildingBlocksCallback(Class<? extends BuildingBlock> clazz, Collection<Integer> identifiers) {
      this.clazz = clazz;
      this.identifiers = identifiers;
    }

    public List<?> doInHibernate(Session session) {
      Criteria c = session.createCriteria(clazz);
      c.add(CriteriaUtil.createInRestrictions("id", identifiers));
      c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

      return c.list();
    }
  }

}