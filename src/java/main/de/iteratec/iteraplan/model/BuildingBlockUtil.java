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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.user.User;


@SuppressWarnings("deprecation")
public final class BuildingBlockUtil {

  private static final Cloner CLONER = new Cloner();

  private BuildingBlockUtil() {
    // nothing to be done
  }

  public static BusinessDomain clone(BusinessDomain oBD) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessDomain bd = new BusinessDomain();
    bd.setBuildingBlockType(oBD.getBuildingBlockType());
    bd.setName(oBD.getName());
    bd.setDescription(oBD.getDescription());
    bd.setParent(oBD.getParent());
    bd.setChildren(new ArrayList<BusinessDomain>(oBD.getChildren()));
    bd.setBusinessFunctions(new HashSet<BusinessFunction>(oBD.getBusinessFunctions()));
    bd.setBusinessProcesses(new HashSet<BusinessProcess>(oBD.getBusinessProcesses()));
    bd.setBusinessUnits(new HashSet<BusinessUnit>(oBD.getBusinessUnits()));
    bd.setBusinessObjects(new HashSet<BusinessObject>(oBD.getBusinessObjects()));
    bd.setProducts(new HashSet<Product>(oBD.getProducts()));
    bd.setSubscribedUsers(new HashSet<User>(oBD.getSubscribedUsers()));
    BuildingBlockUtil.clone(bd, oBD.getAttributeValueAssignments());
    bd.setId(oBD.getId());
    return bd;
  }

  public static BusinessFunction clone(BusinessFunction oBF) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessFunction bf = new BusinessFunction();
    bf.setBuildingBlockType(oBF.getBuildingBlockType());
    bf.setName(oBF.getName());
    bf.setDescription(oBF.getDescription());
    bf.setParent(oBF.getParent());
    bf.setChildren(new ArrayList<BusinessFunction>(oBF.getChildren()));
    bf.setBusinessDomains(new HashSet<BusinessDomain>(oBF.getBusinessDomains()));
    bf.setBusinessObjects(new HashSet<BusinessObject>(oBF.getBusinessObjects()));
    bf.setInformationSystems(new HashSet<InformationSystemRelease>(oBF.getInformationSystems()));
    bf.setSubscribedUsers(new HashSet<User>(oBF.getSubscribedUsers()));
    BuildingBlockUtil.clone(bf, oBF.getAttributeValueAssignments());
    bf.setId(oBF.getId());
    return bf;
  }

  public static BusinessObject clone(BusinessObject oBO) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessObject bo = new BusinessObject();
    bo.setBuildingBlockType(oBO.getBuildingBlockType());
    bo.setName(oBO.getName());
    bo.setDescription(oBO.getDescription());
    bo.setParent(oBO.getParent());
    bo.setChildren(new ArrayList<BusinessObject>(oBO.getChildren()));
    bo.setBusinessDomains(new HashSet<BusinessDomain>(oBO.getBusinessDomains()));
    bo.setBusinessFunctions(new HashSet<BusinessFunction>(oBO.getBusinessFunctions()));
    for (Isr2BoAssociation ass : oBO.getInformationSystemReleaseAssociations()) {
      Isr2BoAssociation newAss = new Isr2BoAssociation(ass.getInformationSystemRelease(), bo);
      bo.getInformationSystemReleaseAssociations().add(newAss);
    }
    bo.setTransports(new HashSet<Transport>(oBO.getTransports()));
    bo.setGeneralisation(oBO.getGeneralisation());
    bo.setSpecialisations(new HashSet<BusinessObject>(oBO.getSpecialisations()));
    bo.setSubscribedUsers(new HashSet<User>(oBO.getSubscribedUsers()));
    BuildingBlockUtil.clone(bo, oBO.getAttributeValueAssignments());
    bo.setId(oBO.getId());
    return bo;
  }

  public static BusinessProcess clone(BusinessProcess oBP) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessProcess bp = new BusinessProcess();
    bp.setBuildingBlockType(oBP.getBuildingBlockType());
    bp.setName(oBP.getName());
    bp.setDescription(oBP.getDescription());
    bp.setParent(oBP.getParent());
    bp.setChildren(new ArrayList<BusinessProcess>(oBP.getChildren()));
    bp.setBusinessDomains(new HashSet<BusinessDomain>(oBP.getBusinessDomains()));
    bp.setBusinessMappings(new HashSet<BusinessMapping>(oBP.getBusinessMappings()));
    bp.setSubscribedUsers(new HashSet<User>(oBP.getSubscribedUsers()));
    BuildingBlockUtil.clone(bp, oBP.getAttributeValueAssignments());
    bp.setId(oBP.getId());
    return bp;
  }

  public static BusinessMapping clone(BusinessMapping oBM) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessMapping bm = new BusinessMapping();
    bm.setBuildingBlockType(oBM.getBuildingBlockType());
    bm.setName(oBM.getName());
    bm.setDescription(oBM.getDescription());
    bm.setInformationSystemRelease(oBM.getInformationSystemRelease());
    bm.setBusinessProcess(oBM.getBusinessProcess());
    bm.setBusinessUnit(oBM.getBusinessUnit());
    bm.setProduct(oBM.getProduct());
    bm.setSubscribedUsers(new HashSet<User>(oBM.getSubscribedUsers()));
    BuildingBlockUtil.clone(bm, oBM.getAttributeValueAssignments());
    bm.setId(oBM.getId());
    return bm;
  }

  public static BusinessUnit clone(BusinessUnit oBU) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    BusinessUnit bu = new BusinessUnit();
    bu.setBuildingBlockType(oBU.getBuildingBlockType());
    bu.setName(oBU.getName());
    bu.setDescription(oBU.getDescription());
    bu.setParent(oBU.getParent());
    bu.setChildren(new ArrayList<BusinessUnit>(oBU.getChildren()));
    bu.setBusinessDomains(new HashSet<BusinessDomain>(oBU.getBusinessDomains()));
    bu.setBusinessMappings(new HashSet<BusinessMapping>(oBU.getBusinessMappings()));
    bu.setSubscribedUsers(new HashSet<User>(oBU.getSubscribedUsers()));
    BuildingBlockUtil.clone(bu, oBU.getAttributeValueAssignments());
    bu.setId(oBU.getId());
    return bu;
  }

  public static ArchitecturalDomain clone(ArchitecturalDomain oAD) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    ArchitecturalDomain ad = new ArchitecturalDomain();
    ad.setBuildingBlockType(oAD.getBuildingBlockType());
    ad.setName(oAD.getName());
    ad.setDescription(oAD.getDescription());
    ad.setParent(oAD.getParent());
    ad.setChildren(new ArrayList<ArchitecturalDomain>(oAD.getChildren()));
    ad.setTechnicalComponentReleases(new HashSet<TechnicalComponentRelease>(oAD.getTechnicalComponentReleases()));
    ad.setSubscribedUsers(new HashSet<User>(oAD.getSubscribedUsers()));
    BuildingBlockUtil.clone(ad, oAD.getAttributeValueAssignments());
    ad.setId(oAD.getId());
    return ad;
  }

  public static InformationSystemDomain clone(InformationSystemDomain oISD) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    InformationSystemDomain isd = new InformationSystemDomain();
    isd.setBuildingBlockType(oISD.getBuildingBlockType());
    isd.setName(oISD.getName());
    isd.setDescription(oISD.getDescription());
    isd.setParent(oISD.getParent());
    isd.setChildren(new ArrayList<InformationSystemDomain>(oISD.getChildren()));
    isd.setInformationSystemReleases(new HashSet<InformationSystemRelease>(oISD.getInformationSystemReleases()));
    isd.setSubscribedUsers(new HashSet<User>(oISD.getSubscribedUsers()));
    BuildingBlockUtil.clone(isd, oISD.getAttributeValueAssignments());
    isd.setId(oISD.getId());
    return isd;
  }

  public static InfrastructureElement clone(InfrastructureElement oIE) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    InfrastructureElement ie = new InfrastructureElement();
    ie.setBuildingBlockType(oIE.getBuildingBlockType());
    ie.setName(oIE.getName());
    ie.setDescription(oIE.getDescription());
    ie.setParent(oIE.getParent());
    ie.setChildren(Lists.newArrayList(oIE.getChildren()));
    ie.setBaseComponents(Sets.newHashSet(oIE.getBaseComponents()));
    ie.setParentComponents(Sets.newHashSet(oIE.getParentComponents()));
    ie.setInformationSystemReleases(Sets.newHashSet(oIE.getInformationSystemReleases()));
    for (Tcr2IeAssociation ass : oIE.getTechnicalComponentReleaseAssociations()) {
      Tcr2IeAssociation newAss = new Tcr2IeAssociation(ass.getTechnicalComponentRelease(), ie);
      ie.getTechnicalComponentReleaseAssociations().add(newAss);
    }
    ie.setSubscribedUsers(Sets.newHashSet(oIE.getSubscribedUsers()));
    BuildingBlockUtil.clone(ie, oIE.getAttributeValueAssignments());
    ie.setId(oIE.getId());
    return ie;
  }

  public static Product clone(Product oP) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    Product p = new Product();
    p.setBuildingBlockType(oP.getBuildingBlockType());
    p.setName(oP.getName());
    p.setDescription(oP.getDescription());
    p.setParent(oP.getParent());
    p.setChildren(new ArrayList<Product>(oP.getChildren()));
    p.setBusinessDomains(new HashSet<BusinessDomain>(oP.getBusinessDomains()));
    p.setBusinessMappings(new HashSet<BusinessMapping>(oP.getBusinessMappings()));
    p.setSubscribedUsers(new HashSet<User>(oP.getSubscribedUsers()));
    BuildingBlockUtil.clone(p, oP.getAttributeValueAssignments());
    p.setId(oP.getId());
    return p;
  }

  public static Project clone(Project oP) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    Project p = new Project();
    p.setBuildingBlockType(oP.getBuildingBlockType());
    p.setName(oP.getName());
    p.setDescription(oP.getDescription());
    p.setParent(oP.getParent());
    p.setChildren(new ArrayList<Project>(oP.getChildren()));
    p.setInformationSystemReleases(new HashSet<InformationSystemRelease>(oP.getInformationSystemReleases()));
    p.setRuntimePeriod(BuildingBlockUtil.clone(oP.getRuntimePeriod()));
    p.setSubscribedUsers(new HashSet<User>(oP.getSubscribedUsers()));
    BuildingBlockUtil.clone(p, oP.getAttributeValueAssignments());
    p.setId(oP.getId());
    return p;
  }

  public static InformationSystemRelease clone(InformationSystemRelease oIS) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    InformationSystemRelease isr = new InformationSystemRelease();
    isr.setBuildingBlockType(oIS.getBuildingBlockType());
    InformationSystem is = new InformationSystem();
    is.setBuildingBlockType(oIS.getInformationSystem().getBuildingBlockType());
    isr.setInformationSystem(is);

    is.setName(oIS.getInformationSystem().getName());
    isr.setDescription(oIS.getDescription());
    isr.setVersion(oIS.getVersion());
    isr.setTypeOfStatus(oIS.getTypeOfStatus());
    isr.setRuntimePeriod(BuildingBlockUtil.clone(oIS.getRuntimePeriod()));
    isr.setParent(oIS.getParent());
    isr.setChildren(new HashSet<InformationSystemRelease>(oIS.getChildren()));
    isr.setPredecessors(new HashSet<InformationSystemRelease>(oIS.getPredecessors()));
    isr.setSuccessors(new HashSet<InformationSystemRelease>(oIS.getSuccessors()));
    isr.setBaseComponents(new HashSet<InformationSystemRelease>(oIS.getBaseComponents()));
    isr.setParentComponents(new HashSet<InformationSystemRelease>(oIS.getParentComponents()));
    isr.setInformationSystemDomains(new HashSet<InformationSystemDomain>(oIS.getInformationSystemDomains()));
    isr.setTechnicalComponentReleases(new HashSet<TechnicalComponentRelease>(oIS.getTechnicalComponentReleases()));
    isr.setInfrastructureElements(new HashSet<InfrastructureElement>(oIS.getInfrastructureElements()));
    isr.setBusinessFunctions(new HashSet<BusinessFunction>(oIS.getBusinessFunctions()));
    isr.setProjects(new HashSet<Project>(oIS.getProjects()));
    for (Isr2BoAssociation ass : oIS.getBusinessObjectAssociations()) {
      Isr2BoAssociation newAss = new Isr2BoAssociation(isr, ass.getBusinessObject());
      isr.getBusinessObjectAssociations().add(newAss);
    }
    isr.setBusinessMappings(new HashSet<BusinessMapping>(oIS.getBusinessMappings()));
    isr.setInterfacesReleaseA(new HashSet<InformationSystemInterface>(oIS.getInterfacesReleaseA()));
    isr.setInterfacesReleaseB(new HashSet<InformationSystemInterface>(oIS.getInterfacesReleaseB()));
    isr.setSubscribedUsers(new HashSet<User>(oIS.getSubscribedUsers()));
    BuildingBlockUtil.clone(isr, oIS.getAttributeValueAssignments());
    isr.setId(oIS.getId());
    is.setId(oIS.getInformationSystem().getId());
    return isr;
  }

  public static InformationSystemInterface clone(InformationSystemInterface oISI) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    InformationSystemInterface isi = new InformationSystemInterface();
    isi.setBuildingBlockType(oISI.getBuildingBlockType());
    oISI.setReferenceRelease(oISI.getInformationSystemReleaseA());
    isi.setName(oISI.getName());
    isi.setDescription(oISI.getDescription());
    isi.setInformationSystemReleaseA(oISI.getInformationSystemReleaseA());
    isi.setInformationSystemReleaseB(oISI.getInformationSystemReleaseB());
    isi.setDirection(oISI.getDirection());
    isi.setTechnicalComponentReleases(new HashSet<TechnicalComponentRelease>(oISI.getTechnicalComponentReleases()));
    isi.setTransports(new HashSet<Transport>(oISI.getTransports()));
    isi.setSubscribedUsers(new HashSet<User>(oISI.getSubscribedUsers()));
    BuildingBlockUtil.clone(isi, oISI.getAttributeValueAssignments());
    isi.setId(oISI.getId());
    return isi;
  }

  public static TechnicalComponentRelease clone(TechnicalComponentRelease oTC) {
    // using primitive constructor instead of factory since necessary fields will be set as clones values from the original anyway
    TechnicalComponentRelease tcr = new TechnicalComponentRelease();
    tcr.setBuildingBlockType(oTC.getBuildingBlockType());
    TechnicalComponent tc = new TechnicalComponent();
    tc.setBuildingBlockType(oTC.getTechnicalComponent().getBuildingBlockType());
    tcr.setTechnicalComponent(tc);
    tc.setName(oTC.getTechnicalComponent().getName());
    tcr.setDescription(oTC.getDescription());
    tcr.setVersion(oTC.getVersion());
    tc.setAvailableForInterfaces(oTC.getTechnicalComponent().isAvailableForInterfaces());
    tcr.setTypeOfStatus(oTC.getTypeOfStatus());
    tcr.setRuntimePeriod(BuildingBlockUtil.clone(oTC.getRuntimePeriod()));
    tcr.setPredecessors(new HashSet<TechnicalComponentRelease>(oTC.getPredecessors()));
    tcr.setSuccessors(new HashSet<TechnicalComponentRelease>(oTC.getSuccessors()));
    tcr.setBaseComponents(new HashSet<TechnicalComponentRelease>(oTC.getBaseComponents()));
    tcr.setParentComponents(new HashSet<TechnicalComponentRelease>(oTC.getParentComponents()));
    tcr.setArchitecturalDomains(new HashSet<ArchitecturalDomain>(oTC.getArchitecturalDomains()));
    for (Tcr2IeAssociation ass : oTC.getInfrastructureElementAssociations()) {
      Tcr2IeAssociation newAss = new Tcr2IeAssociation(tcr, ass.getInfrastructureElement());
      tcr.getInfrastructureElementAssociations().add(newAss);
    }
    tcr.setInformationSystemReleases(new HashSet<InformationSystemRelease>(oTC.getInformationSystemReleases()));
    tcr.setInformationSystemInterfaces(new HashSet<InformationSystemInterface>(oTC.getInformationSystemInterfaces()));
    tcr.setSubscribedUsers(new HashSet<User>(oTC.getSubscribedUsers()));
    BuildingBlockUtil.clone(tcr, oTC.getAttributeValueAssignments());
    tcr.setId(oTC.getId());
    tc.setId(oTC.getTechnicalComponent().getId());
    return tcr;
  }

  public static BuildingBlock clone(BuildingBlock buildingBlock) {
    return CLONER.clone(buildingBlock);
  }

  private static void clone(BuildingBlock bb, Set<AttributeValueAssignment> attributeValueAssignements) {
    for (AttributeValueAssignment avs : attributeValueAssignements) {
      AttributeValueAssignment newAVS = new AttributeValueAssignment(bb, avs.getAttributeValue().getCopy());
      bb.getAttributeValueAssignments().add(newAVS);
    }
  }

  private static RuntimePeriod clone(RuntimePeriod oRP) {
    if (oRP == null) {
      return null;
    }
    Date start = null;
    Date end = null;
    if (oRP.getStart() != null) {
      start = new Date(oRP.getStart().getTime());
    }
    if (oRP.getEnd() != null) {
      end = new Date(oRP.getEnd().getTime());
    }
    return new RuntimePeriod(start, end);
  }
}
