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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.util.CriteriaUtil;
import de.iteratec.iteraplan.persistence.util.Pair;


/**
 * Implementation of the DAO interface {@link ConsistencyCheckDAO}.
 */
public class ConsistencyCheckDAOImpl extends HibernateDaoSupport implements ConsistencyCheckDAO {

  private static final String CURRENT_STATUS   = "current";
  private static final String INACTIVE_STATUS  = "inactive";
  private static final String PLANNED_STATUS   = "planned";
  private static final String UNDEFINED_STATUS = "undefined";

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<BuildingBlock> getBuildingBlocksWithAttributeValueAssigmentsOutOfRange(TypeOfBuildingBlock type) {
    if (type != null) {
      return getHibernateTemplate().findByNamedQueryAndNamedParam("getBuildingBlocksOfTypeWithAttributeValueAssigmentsOutOfRange", "type", type);
    }
    return getHibernateTemplate().findByNamedQuery("getBuildingBlocksWithAttributeValueAssigmentsOutOfRange");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemInterface> getConnectionsWithUnsynchronizedInformationSystemReleases() {
    return getHibernateTemplate().findByNamedQuery("getConnectionsWithUnsynchronizedInformationSystemReleases");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesActiveWithoutStatusCurrent() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getInformationSystemReleasesActiveWithoutStatusCurrent",
        new String[] { CURRENT_STATUS, "today" }, new Object[] { InformationSystemRelease.TypeOfStatus.CURRENT, new Date() });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesInactiveWithoutStatusInactive() {
    Date yesterday = new Date(Calendar.getInstance().getTimeInMillis() - Constants.DAY_AS_MILLISECONDS);

    return getHibernateTemplate().findByNamedQueryAndNamedParam("getInformationSystemReleasesInactiveWithoutStatusInactive",
        new String[] { INACTIVE_STATUS, "yesterday" }, new Object[] { InformationSystemRelease.TypeOfStatus.INACTIVE, yesterday });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesPlannedWithoutAssociatedProjects() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getInformationSystemReleasesPlannedWithoutAssociatedProjects", PLANNED_STATUS,
        InformationSystemRelease.TypeOfStatus.PLANNED);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesWithoutStatusPlannedButAssociatedToProjects() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getInformationSystemReleasesWithoutStatusPlannedButAssociatedToProjects",
        PLANNED_STATUS, InformationSystemRelease.TypeOfStatus.PLANNED);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesWithParents() {
    return getHibernateTemplate().findByNamedQuery("getInformationSystemReleasesWithParents");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getInformationSystemReleasesWithStatusCurrentOrInactiveButNotYetLaunched() {
    Date tomorrow = new Date(Calendar.getInstance().getTimeInMillis() + Constants.DAY_AS_MILLISECONDS);

    return getHibernateTemplate().findByNamedQueryAndNamedParam("getInformationSystemReleasesWithStatusCurrentOrInactiveButNotYetLaunched",
        new String[] { CURRENT_STATUS, INACTIVE_STATUS, "tomorrow" },
        new Object[] { InformationSystemRelease.TypeOfStatus.CURRENT, InformationSystemRelease.TypeOfStatus.INACTIVE, tomorrow });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> getNumberAttributeTypeAndValueForBuildingBlockID(Integer id) {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getNumberAttributeTypeAndValueForBuildingBlockID", "id", id);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemRelease> getReleasesWithStatusCurrentForInformationSystemID(Integer id) {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getReleasesWithStatusCurrentForInformationSystemID",
        new String[] { "id", CURRENT_STATUS }, new Object[] { id, InformationSystemRelease.TypeOfStatus.CURRENT });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Map<String, BuildingBlock>> getIsrConnectedToInfrastrElemViaTcrButNotDirectly() {

    HibernateCallback<List<Map<String, BuildingBlock>>> callback = new IsrConnectedToInfrastrElemViaTcrButNotDirectlyCallback(); // end of callback

    return getHibernateTemplate().executeFind(callback);

  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getReleasesWithStatusCurrentForTcID(Integer id) {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getReleasesWithStatusCurrentForTcID", new String[] { "id", CURRENT_STATUS },
        new Object[] { id, InformationSystemRelease.TypeOfStatus.CURRENT });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getTcReleasesActiveWithoutStatusCurrent() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getTcReleasesActiveWithoutStatusCurrent", new String[] { CURRENT_STATUS, "today" },
        new Object[] { TechnicalComponentRelease.TypeOfStatus.CURRENT, new Date() });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getTcReleasesInactiveWithoutStatusInactive() {
    Date yesterday = new Date(Calendar.getInstance().getTimeInMillis() - Constants.DAY_AS_MILLISECONDS);

    return getHibernateTemplate().findByNamedQueryAndNamedParam("getTcReleasesInactiveWithoutStatusInactive",
        new String[] { INACTIVE_STATUS, UNDEFINED_STATUS, "yesterday" },
        new Object[] { TechnicalComponentRelease.TypeOfStatus.INACTIVE, TechnicalComponentRelease.TypeOfStatus.UNDEFINED, yesterday });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getTcReleasesWithStatusCurrentOrInactiveButNotYetLaunched() {
    Date tomorrow = new Date(Calendar.getInstance().getTimeInMillis() + Constants.DAY_AS_MILLISECONDS);

    return getHibernateTemplate().findByNamedQueryAndNamedParam("getTcReleasesWithStatusCurrentOrInactiveButNotYetLaunched",
        new String[] { CURRENT_STATUS, INACTIVE_STATUS, "tomorrow" },
        new Object[] { TechnicalComponentRelease.TypeOfStatus.CURRENT, TechnicalComponentRelease.TypeOfStatus.INACTIVE, tomorrow });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<TechnicalComponentRelease> getTcReleasesWithStatusUndefined() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam("getTcReleasesWithStatusUndefined", UNDEFINED_STATUS,
        TechnicalComponentRelease.TypeOfStatus.UNDEFINED);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Map<String, TechnicalComponentRelease>> getTcReleasesUsingOtherTcrNotReleased() {
    HibernateCallback<List<Map<String, TechnicalComponentRelease>>> callback = new TcReleasesUsingOtherTcrNotReleasedCallback(); // end of callback

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> getUnsynchronizedProjectsWithInformationSystemReleases() {
    return getHibernateTemplate().findByNamedQuery("getUnsynchronizedProjectsWithInformationSystemReleases");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> getUnsynchronizedTcAndIsReleases() {
    return getHibernateTemplate().findByNamedQuery("getUnsynchronizedTcAndIsReleases");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemInterface> getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases() {
    return getHibernateTemplate().findByNamedQueryAndNamedParam(
        "getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases", new String[] { CURRENT_STATUS },
        new Object[] { InformationSystemRelease.TypeOfStatus.CURRENT });
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<InformationSystemInterface> getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases2() {
    return getHibernateTemplate().findByNamedQuery("getInformationSystemInterfacesConnectingCurrentAndNonCurrentInformationSystemReleases2");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirChildren() {
    return getHibernateTemplate().findByNamedQuery("getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirChildren");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Object[]> getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirSuccessors() {
    return getHibernateTemplate().findByNamedQuery("getTechnicalComponentReleasesSharingNoArchitecturalDomainWithTheirSuccessors");
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Map<String, BuildingBlock>> getIsiConnectedToInformationSystemViaBusinessObjectButNotDirectly() {
    HibernateCallback<List<Map<String, BuildingBlock>>> callback = new IsiConnectedToInformationSystemViaBusinessObjectButNotDirectlyCallback(); // end of callback

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<BuildingBlock> getBuildingBlocksRecentlyUpdated(final TypeOfBuildingBlock type, final int days, final boolean recentlyUpdated) {
    HibernateCallback<List<BuildingBlock>> callback = new BuildingBlocksRecentlyUpdatedCallback(type, days, recentlyUpdated); // end of callback class

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<BuildingBlock> getBuildingBlocksWithNoAssociations(final TypeOfBuildingBlock type) {
    HibernateCallback<List<BuildingBlock>> callback = new BuildingBlocksWithNoAssociationsCallback(type); // end of callback

    return getHibernateTemplate().executeFind(callback);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Pair<BusinessObject, InformationSystemRelease>> getBoUsedByInformationSystemButNotTransported() {
    HibernateCallback<List<Pair<BusinessObject, InformationSystemRelease>>> callback = new BoUsedByInformationSystemButNotTransportedCallback(); // end of callback

    return getHibernateTemplate().executeFind(callback);
  }

  private static final class IsrConnectedToInfrastrElemViaTcrButNotDirectlyCallback implements HibernateCallback<List<Map<String, BuildingBlock>>> {
    @SuppressWarnings("unchecked")
    public List<Map<String, BuildingBlock>> doInHibernate(Session session) {

      // cycle: via technical components and infrastructure elements back to information system
      String queryStr = "select isr as " + ISR_ALIAS;
      queryStr += ", tcr as " + TCR_ALIAS;
      queryStr += ", ie as " + INFRASTR_ELEM_ALIAS;
      queryStr += " from InformationSystemRelease isr " + "inner join isr.technicalComponentReleases tcr "
          + "inner join tcr.infrastructureElementAssociations ieassoc " + "inner join ieassoc.infrastructureElement ie ";
      queryStr += "where ie not in elements(isr.infrastructureElements) ";
      Query q = session.createQuery(queryStr);
      q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

      return q.list();
    }
  }

  private static final class TcReleasesUsingOtherTcrNotReleasedCallback implements HibernateCallback<List<Map<String, TechnicalComponentRelease>>> {
    @SuppressWarnings("unchecked")
    public List<Map<String, TechnicalComponentRelease>> doInHibernate(Session session) {

      Criteria c = session.createCriteria(TechnicalComponentRelease.class, TCR_ALIAS);
      c.createAlias("baseComponents", TCR_BASE_ALIAS);

      // base components which are not yet released
      c.add(Restrictions.ne(TCR_BASE_ALIAS + ".typeOfStatus", TypeOfStatus.CURRENT));

      c.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
      return c.list();
    }
  }

  private static final class IsiConnectedToInformationSystemViaBusinessObjectButNotDirectlyCallback implements
      HibernateCallback<List<Map<String, BuildingBlock>>> {
    @SuppressWarnings("unchecked")
    public List<Map<String, BuildingBlock>> doInHibernate(Session session) {
      Criteria c = session.createCriteria(InformationSystemInterface.class, ISI_ALIAS);
      c.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

      // cycle: via technical components and infrastructure elements back to information system
      // release

      String isrA = ISI_ALIAS + ".informationSystemReleaseA";
      String isrB = ISI_ALIAS + ".informationSystemReleaseB";
      c.createAlias(isrA, ISR_A_ALIAS);
      c.createAlias(isrB, ISR_B_ALIAS);

      String pathTransport = ISI_ALIAS + ".transports";
      c.createAlias(pathTransport, TRANS_ALIAS);

      String pathBO = TRANS_ALIAS + ".businessObject";
      c.createAlias(pathBO, BO_ALIAS);

      String pathIsrAssoc = BO_ALIAS + ".informationSystemReleaseAssociations";
      c.createAlias(pathIsrAssoc, ISR_ASSOC_ALIAS);

      String pathIsr = ISR_ASSOC_ALIAS + ".informationSystemRelease";
      c.createAlias(pathIsr, ISR_ALIAS);

      // get ids of all interfaces referenced by the information system releases A and B
      List<Map<String, BuildingBlock>> list = c.list();

      List<Integer> isrIds = new ArrayList<Integer>();

      for (Map<String, BuildingBlock> resultMap : list) {

        InformationSystemRelease isr = (InformationSystemRelease) resultMap.get(ISR_ALIAS);

        if (isr != null) {
          isrIds.add(isr.getId());
        }
      }

      // if lists is empty, remove interface with this id from result list
      if (isrIds.isEmpty()) {
        c.add(Restrictions.isNull(ISI_ALIAS + ".id"));
      }
      else {
        // neither information system A nor B are contained in list of information systems
        c.add(CriteriaUtil.createNotInRestrictions(ISR_A_ALIAS + ".id", isrIds));
        c.add(CriteriaUtil.createNotInRestrictions(ISR_B_ALIAS + ".id", isrIds));
      }

      return c.list();
    }
  }

  private static final class BuildingBlocksRecentlyUpdatedCallback implements HibernateCallback<List<BuildingBlock>> {
    private final TypeOfBuildingBlock type;
    private final int                 days;
    private final boolean             recentlyUpdated;

    public BuildingBlocksRecentlyUpdatedCallback(TypeOfBuildingBlock type, int days, boolean recentlyUpdated) {
      this.type = type;
      this.days = days;
      this.recentlyUpdated = recentlyUpdated;
    }

    public List<BuildingBlock> doInHibernate(Session session) {
      List<BuildingBlock> results = new ArrayList<BuildingBlock>();

      if (type != null) {
        results.addAll(getResultsForBuildingBlockType(session, type));
      }
      else { /* default: all building blocks */
        for (TypeOfBuildingBlock tob : TypeOfBuildingBlock.ALL) {
          results.addAll(getResultsForBuildingBlockType(session, tob));
        }
      }
      return results;
    }

    @SuppressWarnings("unchecked")
    private List<BuildingBlock> getResultsForBuildingBlockType(Session session, TypeOfBuildingBlock tob) {
      Class<? extends BuildingBlock> bbClass = tob.getAssociatedClass();
      Criteria c = session.createCriteria(bbClass, ConsistencyCheckDAO.BB_ALIAS);

      // subtract the amount 'days' (parameter) from now
      Date nDaysAgo = DateUtils.addDays(new Date(), -1 * days);

      // last modification has been recent enough
      String lastModProp = ConsistencyCheckDAO.BB_ALIAS + ".lastModificationTime";
      if (recentlyUpdated) { // all changes after nDaysAgo
        c.add(Restrictions.ge(lastModProp, nDaysAgo));
      }
      else { // all changes before nDaysAgo
        c.add(Restrictions.le(lastModProp, nDaysAgo));
      }

      return c.list();
    }
  }

  private static final class BuildingBlocksWithNoAssociationsCallback implements HibernateCallback<List<BuildingBlock>> {
    private final TypeOfBuildingBlock type;

    public BuildingBlocksWithNoAssociationsCallback(TypeOfBuildingBlock type) {
      this.type = type;
    }

    public List<BuildingBlock> doInHibernate(Session session) {

      List<BuildingBlock> results = new ArrayList<BuildingBlock>();

      // retrieve class of selected building block type and create criteria with it
      if (type != null) {
        results.addAll(CheckBuildingBlocksWithNoAssociationsHelper.getResultsForType(session, type));
      }
      else {
        // all building blocks
        for (TypeOfBuildingBlock tob : TypeOfBuildingBlock.ALL) {
          results.addAll(CheckBuildingBlocksWithNoAssociationsHelper.getResultsForType(session, tob));
        }
      }
      return results;
    }
  }

  private static final class BoUsedByInformationSystemButNotTransportedCallback implements
      HibernateCallback<List<Pair<BusinessObject, InformationSystemRelease>>> {
    @SuppressWarnings("unchecked")
    public List<Pair<BusinessObject, InformationSystemRelease>> doInHibernate(Session session) {
      // stores the results later
      List<Pair<BusinessObject, InformationSystemRelease>> resultList = new ArrayList<Pair<BusinessObject, InformationSystemRelease>>();

      // determine all information systems using the current BO
      Criteria c = session.createCriteria(BusinessObject.class, BO_ALIAS);
      c.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

      String pathIsrAssoc = BO_ALIAS + ".informationSystemReleaseAssociations";
      c.createAlias(pathIsrAssoc, ISR_ASSOC_ALIAS);
      
      String pathIsr = ISR_ASSOC_ALIAS + ".informationSystemRelease";
      c.createAlias(pathIsr, ISR_ALIAS);

      // get all information systems using the business object
      List<Map<String, BuildingBlock>> list = c.list();

      // store mappings between business objects and all their information systems in order to
      // interate over them later
      Map<BusinessObject, List<InformationSystemRelease>> businessObjectToInformationSystemMapping = new HashMap<BusinessObject, List<InformationSystemRelease>>();

      for (Map<String, BuildingBlock> resultMap : list) {

        BusinessObject bo = (BusinessObject) resultMap.get(BO_ALIAS);
        InformationSystemRelease isr = (InformationSystemRelease) resultMap.get(ISR_ALIAS);

        if (bo != null && isr != null) {

          // initialise
          if (businessObjectToInformationSystemMapping.get(bo) == null) {
            businessObjectToInformationSystemMapping.put(bo, new ArrayList<InformationSystemRelease>());
          }
          else {
            List<InformationSystemRelease> currentList = businessObjectToInformationSystemMapping.get(bo);
            // add isr to list of information systems, if it is not yet contained
            if (!currentList.contains(isr)) {
              List<InformationSystemRelease> newList = new ArrayList<InformationSystemRelease>(currentList);
              newList.add(isr);
              // update mapping of BO to this new list of information systems (with isr)
              businessObjectToInformationSystemMapping.put(bo, newList);
            }
          }
        } // end for

        // each pair BO - ISR
        for (Entry<BusinessObject, List<InformationSystemRelease>> entry : businessObjectToInformationSystemMapping.entrySet()) {
          BusinessObject businessObject = entry.getKey();

          // determine all interfaces which transport the business object
          List<InformationSystemInterface> interfacesTransportingBo = new ArrayList<InformationSystemInterface>();

          // get to carrying interfaces via transport
          for (Transport transport : businessObject.getTransports()) {
            InformationSystemInterface isi = transport.getInformationSystemInterface();
            // interface transports BO, add it to the list (if not contained already)
            if (!interfacesTransportingBo.contains(isi)) {
              interfacesTransportingBo.add(isi);
            }
          }

          // list of information systems using the business object
          for (InformationSystemRelease system : entry.getValue()) {
            // all interfaces of this particular information system
            List<InformationSystemInterface> interfacesOfSystem = new ArrayList<InformationSystemInterface>();
            interfacesOfSystem.addAll(system.getInterfacesReleaseA());
            interfacesOfSystem.addAll(system.getInterfacesReleaseB());

            boolean transportedByInterface = false;

            // go through the list of the system's interfaces
            for (InformationSystemInterface interfaceToTest : interfacesOfSystem) {
              // matching: one of the system's interfaces is also the one transporting BO
              if (interfacesTransportingBo.contains(interfaceToTest)) {
                transportedByInterface = true;
                break;
              }
            }

            if (!transportedByInterface) {
              // we want to collect all pairs of BO and systems where there is NO matching of
              // interfaces
              resultList.add(new Pair<BusinessObject, InformationSystemRelease>(businessObject, system));
            }
          } // for (information system)
        } // for (entry BO, list<ISR>)
      }
      return resultList;
    }
  }

}