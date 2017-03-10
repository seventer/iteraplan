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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.MapValueComparator;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.DashboardElementLists;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.SealState;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.persistence.dao.ArchitecturalDomainDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessDomainDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessFunctionDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessObjectDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessProcessDAO;
import de.iteratec.iteraplan.persistence.dao.BusinessUnitDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemDomainDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;
import de.iteratec.iteraplan.persistence.dao.InfrastructureElementDAO;
import de.iteratec.iteraplan.persistence.dao.ProductDAO;
import de.iteratec.iteraplan.persistence.dao.ProjectDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;


/**
 * A default {@link DashboardService} implementation for calculating various properties and 
 * attributes, required to show dashboard graphics.
 */
public class DashboardServiceImpl implements DashboardService {

  private BusinessDomainDAO             businessDomainDAO;
  private BusinessProcessDAO            businessProcessDAO;
  private BusinessFunctionDAO           businessFunctionDAO;
  private ProductDAO                    productDAO;
  private BusinessUnitDAO               businessUnitDAO;
  private BusinessObjectDAO             businessObjectDAO;
  private InformationSystemDomainDAO    informationSystemDomainDAO;
  private InformationSystemReleaseDAO   informationSystemReleaseDAO;
  private InformationSystemInterfaceDAO informationSystemInterfaceDAO;
  private ArchitecturalDomainDAO        architecturalDomainDAO;
  private TechnicalComponentReleaseDAO  technicalComponentReleaseDAO;
  private InfrastructureElementDAO      infrastructureElementDAO;
  private ProjectDAO                    projectDAO;
  private AttributeValueService         attributeValueService;
  private AttributeTypeService          attributeTypeService;

  /** {@inheritDoc} */
  public DashboardElementLists getElementLists() {
    DashboardElementLists elements = new DashboardElementLists();

    // instead of loading the whole lists with all elements, we might also switch to loading only the    
    // details needed, would be more resource efficient, but also not that flexible / more coding work
    elements.setBdList(businessDomainDAO.loadElementList(null));
    elements.setBpList(businessProcessDAO.loadElementList(null));
    elements.setBfList(businessFunctionDAO.loadElementList(null));
    elements.setProdList(productDAO.loadElementList(null));
    elements.setBuList(businessUnitDAO.loadElementList(null));
    elements.setBoList(businessObjectDAO.loadElementList(null));
    elements.setIsdList(informationSystemDomainDAO.loadElementList(null));
    elements.setIsrList(informationSystemReleaseDAO.loadElementList(null));
    elements.setIsiList(informationSystemInterfaceDAO.loadElementList(null));
    elements.setAdList(architecturalDomainDAO.loadElementList(null));
    elements.setTcrList(technicalComponentReleaseDAO.loadElementList(null));
    elements.setIeList(infrastructureElementDAO.loadElementList(null));
    elements.setProjList(projectDAO.loadElementList(null));

    return elements;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("boxing")
  public Map<String, Integer> getNumberOfElementsMap(DashboardElementLists elements) {
    // we use a linkedHashMap here, to ensure the order of elements is retained
    Map<String, Integer> result = new LinkedHashMap<String, Integer>();
    // get the number of elements for each BBT and put them in the map
    result.put(Constants.BB_BUSINESSDOMAIN_BASE, elements.getBdList().size());
    result.put(Constants.BB_BUSINESSPROCESS_BASE, elements.getBpList().size());
    result.put(Constants.BB_BUSINESSFUNCTION_BASE, elements.getBfList().size());
    result.put(Constants.BB_PRODUCT_BASE, elements.getProdList().size());
    result.put(Constants.BB_BUSINESSUNIT_BASE, elements.getBuList().size());
    result.put(Constants.BB_BUSINESSOBJECT_BASE, elements.getBoList().size());
    result.put(Constants.BB_INFORMATIONSYSTEMDOMAIN_BASE, elements.getIsdList().size());
    result.put(Constants.BB_INFORMATIONSYSTEMRELEASE_BASE, elements.getIsrList().size());
    result.put(Constants.BB_INFORMATIONSYSTEMINTERFACE_BASE, elements.getIsiList().size());
    result.put(Constants.BB_ARCHITECTURALDOMAIN_BASE, elements.getAdList().size());
    result.put(Constants.BB_TECHNICALCOMPONENTRELEASE_BASE, elements.getTcrList().size());
    result.put(Constants.BB_INFRASTRUCTUREELEMENT_BASE, elements.getIeList().size());
    result.put(Constants.BB_PROJECT_BASE, elements.getProjList().size());

    return result;
  }

  /** {@inheritDoc} */
  public Map<String, Integer> getIsrStatusMap(List<InformationSystemRelease> isrs) {
    Multiset<InformationSystemRelease.TypeOfStatus> multiset = EnumMultiset.create(InformationSystemRelease.TypeOfStatus.class);

    for (InformationSystemRelease tcr : isrs) {
      multiset.add(tcr.getTypeOfStatus());
    }

    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    for (InformationSystemRelease.TypeOfStatus status : InformationSystemRelease.TypeOfStatus.values()) {
      statusMap.put(status.toString(), Integer.valueOf(multiset.count(status)));
    }

    return statusMap;
  }

  /** {@inheritDoc} */
  public Map<String, Integer> getTechnicalComponentsStatusMap(List<TechnicalComponentRelease> tcrs) {
    Multiset<TechnicalComponentRelease.TypeOfStatus> multiset = EnumMultiset.create(TechnicalComponentRelease.TypeOfStatus.class);

    for (TechnicalComponentRelease tcr : tcrs) {
      multiset.add(tcr.getTypeOfStatus());
    }

    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    for (TechnicalComponentRelease.TypeOfStatus status : TechnicalComponentRelease.TypeOfStatus.values()) {
      statusMap.put(status.toString(), Integer.valueOf(multiset.count(status)));
    }

    return statusMap;
  }

  /** {@inheritDoc} */
  public Map<String, Integer> getIsrSealStateMap(List<InformationSystemRelease> isrs) {
    Multiset<SealState> multiset = EnumMultiset.create(SealState.class);

    for (InformationSystemRelease isr : isrs) {
      multiset.add(isr.getSealState());
    }

    Map<String, Integer> statusMap = Maps.newLinkedHashMap();
    for (SealState sealState : SealState.values()) {
      statusMap.put(sealState.toString(), Integer.valueOf(multiset.count(sealState)));
    }

    return statusMap;
  }

  /** {@inheritDoc} */
  public Map<InformationSystemRelease, Integer> getTopUsedIsr(List<InformationSystemRelease> isrs) {
    Map<InformationSystemRelease, Integer> isrMap = Maps.newHashMap();
    for (InformationSystemRelease isr : isrs) {
      int size = isr.getInterfacedInformationSystemReleases().size();
      isrMap.put(isr, Integer.valueOf(size));
    }

    @SuppressWarnings("unchecked")
    SortedMap<InformationSystemRelease, Integer> sortedMap = Maps.newTreeMap(new MapValueComparator<Integer>(isrMap));
    sortedMap.putAll(isrMap);

    return sortedMap;
  }

  /** {@inheritDoc} */
  public Map<TechnicalComponentRelease, Integer> getTopUsedTcr(List<TechnicalComponentRelease> tcrs) {
    Map<TechnicalComponentRelease, Integer> tcrMap = Maps.newHashMap();
    for (TechnicalComponentRelease tcr : tcrs) {
      int size = tcr.getInformationSystemReleases().size();
      tcrMap.put(tcr, Integer.valueOf(size));
    }

    @SuppressWarnings("unchecked")
    SortedMap<TechnicalComponentRelease, Integer> sortedMap = Maps.newTreeMap(new MapValueComparator<Integer>(tcrMap));
    sortedMap.putAll(tcrMap);

    return sortedMap;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "boxing", "unchecked" })
  public Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> getTopUsedTcrByAdMap(DashboardElementLists elements) {
    Map<ArchitecturalDomain, Map<TechnicalComponentRelease, Integer>> result = Maps.newLinkedHashMap();

    // first retrieve all technical components that are not assigned to any architectural domains
    Map<TechnicalComponentRelease, Integer> tcrUnassignedMap = Maps.newHashMap();
    for (TechnicalComponentRelease tcr : elements.getTcrList()) {
      if (tcr.getArchitecturalDomains().isEmpty()) {
        tcrUnassignedMap.put(tcr, tcr.getInformationSystemReleases().size());
      }
    }

    SortedMap<TechnicalComponentRelease, Integer> sortedUnassignedMap = Maps.newTreeMap(new MapValueComparator<Integer>(tcrUnassignedMap));
    sortedUnassignedMap.putAll(tcrUnassignedMap);
    // we'll show "manageDashboard.noADAssigned" for that
    result.put(null, sortedUnassignedMap);

    // second retrieve all technical components per architectural domain
    for (ArchitecturalDomain ad : elements.getAdList()) {
      // the abstract element can never contain any relations
      if (AbstractHierarchicalEntity.TOP_LEVEL_NAME.equals(ad.getName())) {
        continue;
      }
      Map<TechnicalComponentRelease, Integer> tcrMap = Maps.newHashMap();
      for (TechnicalComponentRelease tcr : ad.getTechnicalComponentReleases()) {
        tcrMap.put(tcr, tcr.getInformationSystemReleases().size());
      }

      // sort the technical components map by value (number of uses)
      SortedMap<TechnicalComponentRelease, Integer> sortedMap = Maps.newTreeMap(new MapValueComparator<Integer>(tcrMap));
      sortedMap.putAll(tcrMap);
      result.put(ad, sortedMap);
    }

    return result;
  }

  /** {@inheritDoc} */
  public List<BBAttribute> getSingleDimensionAttributes(List<BBAttribute> attributes) {
    List<BBAttribute> attrList = Lists.newLinkedList();
    for (BBAttribute attr : attributes) {
      if (!attr.isMultiValue()) {
        attrList.add(attr);
      }
    }

    return attrList;
  }

  public void setArchitecturalDomainDAO(ArchitecturalDomainDAO dao) {
    this.architecturalDomainDAO = dao;
  }

  public void setBusinessDomainDAO(BusinessDomainDAO dao) {
    this.businessDomainDAO = dao;
  }

  public void setInformationSystemReleaseDAO(InformationSystemReleaseDAO dao) {
    this.informationSystemReleaseDAO = dao;
  }

  public void setTechnicalComponentReleaseDAO(TechnicalComponentReleaseDAO dao) {
    this.technicalComponentReleaseDAO = dao;
  }

  public void setBusinessFunctionDAO(BusinessFunctionDAO businessFunctionDAO) {
    this.businessFunctionDAO = businessFunctionDAO;
  }

  public void setBusinessObjectDAO(BusinessObjectDAO businessObjectDAO) {
    this.businessObjectDAO = businessObjectDAO;
  }

  public void setBusinessProcessDAO(BusinessProcessDAO businessProcessDAO) {
    this.businessProcessDAO = businessProcessDAO;
  }

  public void setBusinessUnitDAO(BusinessUnitDAO businessUnitDAO) {
    this.businessUnitDAO = businessUnitDAO;
  }

  public void setInformationSystemDomainDAO(InformationSystemDomainDAO dao) {
    this.informationSystemDomainDAO = dao;
  }

  public void setInformationSystemInterfaceDAO(InformationSystemInterfaceDAO dao) {
    this.informationSystemInterfaceDAO = dao;
  }

  public void setInfrastructureElementDAO(InfrastructureElementDAO dao) {
    this.infrastructureElementDAO = dao;
  }

  public void setProductDAO(ProductDAO productDAO) {
    this.productDAO = productDAO;
  }

  public void setProjectDAO(ProjectDAO projectDAO) {
    this.projectDAO = projectDAO;
  }

  public void setAttributeValueService(AttributeValueService attributeValueService) {
    this.attributeValueService = attributeValueService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  /**
   *  Generic method for getting value map from any kind of building block. This method replaces the 13 old "duplicated" methods. 
   *  Returns a Map of <attributename,<attributeValue, count>> for a given list of {@link BBAttribute} and a list of any sub-type of {@link BuildingBlock}.
   */
  public Map<String, Map<String, List<Integer>>> getValueMap(List<BBAttribute> bbAttribute, List<? extends BuildingBlock> bbList) {
    final List<BBAttribute> singleDimensionAttributes = getSingleDimensionAttributes(bbAttribute, bbList);

    Map<String, Map<String, List<Integer>>> attributeValueMap = Maps.newLinkedHashMap();
    List<String> attributeNames = Lists.newLinkedList();
    for (BBAttribute attr : singleDimensionAttributes) {

      boolean hasReadPerm = determineReadPermission(attr);

      if (attr.getId() != null && !attr.getId().equals(BBAttribute.UNDEFINED_ID_VALUE) && hasReadPerm) {
        // handle number attributes separately
        if (attributeTypeService.isNumberAT(attr.getId())) {
          Map<String, List<Integer>> map = createAttributeRangeValuesMap(attr);
          attributeValueMap.put(attr.getName(), map);
        }
        else {
          attributeValueMap.put(attr.getName(), new HashMap<String, List<Integer>>());
        }
        attributeNames.add(attr.getName());
      }
    }

    for (String attributeName : attributeNames) {
      Map<String, List<Integer>> valueMap = calculateAttributeValuesAmount(bbList, attributeValueMap, attributeName);
      attributeValueMap.put(attributeName, valueMap);
    }

    return attributeValueMap;
  }

  private boolean determineReadPermission(BBAttribute attr) {
    AttributeType at = attributeTypeService.getAttributeTypeByName(attr.getName());
    boolean hasReadPerm = true;
    if (at != null
        && !UserContext.getCurrentPerms().userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ)) {
      hasReadPerm = false;
    }
    return hasReadPerm;
  }

  private Map<String, List<Integer>> calculateAttributeValuesAmount(List<? extends BuildingBlock> bbList,
                                                                    Map<String, Map<String, List<Integer>>> attributeValueMap, String attributeName) {
    Map<String, List<Integer>> valueMap = attributeValueMap.get(attributeName);

    for (BuildingBlock bb : bbList) {
      if ((bb instanceof TechnicalComponentRelease || bb instanceof InformationSystemRelease)
          && (Constants.ATTRIBUTE_TYPEOFSTATUS.equals(attributeName))) {
        calculateTypeOfStatusAmount(valueMap, bb);
      }
      else if (bb instanceof InformationSystemRelease && "seal".equals(attributeName)) {
        calculateSealStateAmount(valueMap, (InformationSystemRelease) bb);
      }
      else {
        calculateAttributesAmount(attributeName, valueMap, bb);
      }
    }
    // test manipulated!!!
    return valueMap;
  }

  private Map<String, List<Integer>> createAttributeRangeValuesMap(BBAttribute attr) {
    List<String> values = attributeValueService.getAllAVStrings(attr.getId());
    AttributeRangeAdapter adapter = new AttributeRangeAdapter(UserContext.getCurrentLocale());
    adapter.init(attributeTypeService.loadObjectById(attr.getId()), values);
    values = adapter.getValues();

    Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
    for (String value : values) {
      map.put(value, new ArrayList<Integer>());
    }
    return map;
  }

  private List<BBAttribute> getSingleDimensionAttributes(List<BBAttribute> bbAttribute, List<? extends BuildingBlock> bbList) {
    List<BBAttribute> singleDimensionAttributes = Lists.newLinkedList();
    if (bbList != null && bbList.size() > 0) {
      singleDimensionAttributes = getSingleDimensionAttributes(bbAttribute);
    }
    return singleDimensionAttributes;
  }

  private void calculateAttributesAmount(String attributeName, Map<String, List<Integer>> valueMap, BuildingBlock bb) {
    List<Integer> ids = Lists.newArrayList();
    for (AttributeValueAssignment ava : bb.getAttributeValueAssignments()) {
      if (ava.getAttributeValue().getAbstractAttributeType().getName().equals(attributeName)) {
        String attributeValue = null;
        if (attributeTypeService.isNumberAT(ava.getAttributeValue().getAbstractAttributeType().getId())) {
          List<String> values = attributeValueService.getAllAVStrings(ava.getAttributeValue().getAbstractAttributeType().getId());
          AttributeRangeAdapter adapter = new AttributeRangeAdapter(UserContext.getCurrentLocale());
          adapter.init(attributeTypeService.loadObjectById(ava.getAttributeValue().getAbstractAttributeType().getId()), values);
          attributeValue = adapter.getResultForValue(ava.getAttributeValue().getValueString());
        }
        else {
          attributeValue = ava.getAttributeValue().getValueString();
        }
        if (valueMap.get(attributeValue) != null) {
          ids = valueMap.get(attributeValue);
          ids.add(bb.getId());
        }
        else {
          ids.add(bb.getId());
        }
        valueMap.put(attributeValue, ids);
        return;
      }
    }

    // for some reason, the bblock.getAttributeValueAssignments()-method doesn't return the ava, 
    // which contains the specified attributeName, those have their attributeValue set as an empty string: "".
    // !some where in code, it was notified that attributeValue is not null in any cases.
    String noValue = MessageAccess.getString("attribute.novalue");
    if (valueMap.get(noValue) != null) {
      ids = valueMap.get(noValue);
      ids.add(bb.getId());
    }
    else if (!"-".equals(bb.getHierarchicalName())) {
      ids.add(bb.getId());
    }
    valueMap.put(noValue, ids);
  }

  private void calculateTypeOfStatusAmount(Map<String, List<Integer>> valueMap, BuildingBlock bb) {
    final String typeOfStatus = getTypeOfStatus(bb);
    List<Integer> ids = valueMap.get(typeOfStatus);
    if (ids == null) {
      ids = Lists.newArrayList();
    }

    ids.add(bb.getId());
    valueMap.put(typeOfStatus, ids);
  }

  private void calculateSealStateAmount(Map<String, List<Integer>> valueMap, InformationSystemRelease isr) {
    List<Integer> ids = Lists.newArrayList();
    final String sealState = isr.getSealState().toString();
    if (valueMap.get(sealState) != null) {
      ids = valueMap.get(sealState);
      ids.add(isr.getId());
    }
    else {
      ids.add(isr.getId());
    }
    valueMap.put(sealState, ids);
  }

  private String getTypeOfStatus(BuildingBlock bb) {
    String typeOfStatus = null;
    if (bb instanceof TechnicalComponentRelease) {
      typeOfStatus = ((TechnicalComponentRelease) bb).getTypeOfStatusAsString();
    }
    else {
      typeOfStatus = ((InformationSystemRelease) bb).getTypeOfStatusAsString();
    }

    return typeOfStatus;
  }
}
