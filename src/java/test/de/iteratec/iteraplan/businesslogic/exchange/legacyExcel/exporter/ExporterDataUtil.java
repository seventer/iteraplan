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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelConstants;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.user.User;


/**
 * This class serves as a data helper for the excel sheet tests of {@link ExcelSheetTestBase} 
 * and its subclasses 
 */
public final class ExporterDataUtil {
  private static ExporterDataUtil instance = new ExporterDataUtil();

  private ExporterDataUtil() {
    //default constructor
  }

  public static ExporterDataUtil getInstance() {
    return instance;
  }

  /**
   * Creates a set of {@link BusinessProcess}es with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<BusinessProcess> createBPs(int initId, String... names) {
    Set<BusinessProcess> processSet = new HashSet<BusinessProcess>();
    int aInitId = initId;
    for (String name : names) {
      BusinessProcess process = new BusinessProcess();
      process.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      process.setName(name);
      processSet.add(process);
      aInitId++;
    }
    return processSet;
  }

  /**
   * Creates a set of {@link BusinessObject}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<BusinessObject> createBOs(int initId, String... names) {
    Set<BusinessObject> entities = new HashSet<BusinessObject>();
    int aInitId = initId;
    for (String name : names) {
      BusinessObject entity = new BusinessObject();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link BusinessFunction}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<BusinessFunction> createBFs(int initId, String... names) {
    Set<BusinessFunction> entities = new HashSet<BusinessFunction>();
    int aInitId = initId;
    for (String name : names) {
      BusinessFunction entity = new BusinessFunction();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link Product}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<Product> createProducts(int initId, String... names) {
    Set<Product> entities = new HashSet<Product>();
    int aInitId = initId;
    for (String name : names) {
      Product entity = new Product();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link BusinessDomain}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<BusinessDomain> createBDs(int initId, String... names) {
    Set<BusinessDomain> entities = new HashSet<BusinessDomain>();
    int aInitId = initId;
    for (String name : names) {
      BusinessDomain entity = new BusinessDomain();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link BusinessUnit}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<BusinessUnit> createBUs(int initId, String... names) {
    Set<BusinessUnit> entities = new HashSet<BusinessUnit>();
    int aInitId = initId;
    for (String name : names) {
      BusinessUnit entity = new BusinessUnit();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link InformationSystemRelease}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param isName
   *          the name for the corresponding {@link InformationSystem}
   * @param versions
   *          versions to be set
   * @return the set with all required entities
   */
  protected Set<InformationSystemRelease> createISRs(int initId, String isName, int... versions) {
    Set<InformationSystemRelease> entities = new HashSet<InformationSystemRelease>();
    int aInitId = initId;
    InformationSystem is = new InformationSystem();
    is.setName(isName);
    for (int version : versions) {
      InformationSystemRelease entity = new InformationSystemRelease();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setVersion(String.valueOf(version));
      entity.setInformationSystem(is);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link TechnicalComponentRelease}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param tcName
   *          the name for the corresponding {@link TechnicalComponent}
   * @param versions
   *          versions to be set
   * @return the set with all required entities
   */
  protected Set<TechnicalComponentRelease> createTCRs(int initId, String tcName, int... versions) {
    Set<TechnicalComponentRelease> entities = new HashSet<TechnicalComponentRelease>();
    int aInitId = initId;
    TechnicalComponent tc = new TechnicalComponent();
    tc.setName(tcName);
    for (int version : versions) {
      TechnicalComponentRelease entity = new TechnicalComponentRelease();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setVersion(String.valueOf(version));
      entity.setTechnicalComponent(tc);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link InformationSystemDomain}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<InformationSystemDomain> createISDs(int initId, String... names) {
    Set<InformationSystemDomain> entities = new HashSet<InformationSystemDomain>();
    int aInitId = initId;
    for (String name : names) {
      InformationSystemDomain entity = new InformationSystemDomain();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link InfrastructureElement}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<InfrastructureElement> createIEs(int initId, String... names) {
    Set<InfrastructureElement> entities = new HashSet<InfrastructureElement>();
    int aInitId = initId;
    for (String name : names) {
      InfrastructureElement entity = new InfrastructureElement();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link Project}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<Project> createProjects(int initId, String... names) {
    Set<Project> entities = new HashSet<Project>();
    int aInitId = initId;
    for (String name : names) {
      Project entity = new Project();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      entities.add(entity);
      aInitId++;
    }
    return entities;
  }

  /**
   * Creates a set of {@link ArchitecturalDomain}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<ArchitecturalDomain> createADs(int initId, String... names) {
    Set<ArchitecturalDomain> result = new HashSet<ArchitecturalDomain>();
    int aInitId = initId;
    for (String name : names) {
      ArchitecturalDomain entity = new ArchitecturalDomain();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setName(name);
      result.add(entity);
      aInitId++;
    }
    return result;
  }

  /**
   * Creates a set of {@link User}s with the given data.
   * 
   * @param initId
   *          the initial id
   * @param names
   *          array with names
   * @return the set with all required entities
   */
  protected Set<User> createUsers(int initId, String... names) {
    Set<User> result = new HashSet<User>();
    int aInitId = initId;
    for (String name : names) {
      User entity = new User();
      entity.setId(Integer.valueOf(aInitId)); // required because of hashCode()
      entity.setLoginName(name);
      result.add(entity);
      aInitId++;
    }
    return result;
  }

  protected List<String> initCommonExpectedHeaders(Locale locale, String sheetKey) {
    List<String> expContent = new ArrayList<String>();
    expContent.add(MessageAccess.getStringOrNull("global.id", locale));
    expContent.add(MessageAccess.getStringOrNull(sheetKey, locale));
    StringBuffer sheetKeyExt = new StringBuffer();
    sheetKeyExt.append(MessageAccess.getStringOrNull(sheetKey, locale));
    sheetKeyExt.append(' ');
    sheetKeyExt.append(MessageAccess.getStringOrNull("global.hierarchical", locale));
    expContent.add(sheetKeyExt.toString());
    expContent.add(MessageAccess.getStringOrNull("global.description", locale));
    expContent.add(MessageAccess.getStringOrNull("global.lastModificationUser", locale));
    expContent.add(MessageAccess.getStringOrNull("global.lastModificationTime", locale));
    expContent.add(MessageAccess.getStringOrNull("global.subscribed.users", locale));

    return expContent;
  }

  @SuppressWarnings("boxing")
  protected InformationSystemRelease initISR(String name, Date lastModDate, int i) {
    Set<InformationSystemRelease> isrs = this.createISRs(i + 1, name + " - InformationSystemRelease", 2, 3, 4);
    List<InformationSystemRelease> sortedISRs = ExcelHelper.sortEntities(isrs, new HierarchicalEntityCachingComparator<InformationSystemRelease>());
    Iterator<InformationSystemRelease> iter = sortedISRs.iterator();
    InformationSystemRelease entity = iter.next();
    entity.addPredecessor(iter.next());
    entity.addSuccessor(iter.next());
    entity.setDescription("testDescription");
    entity.setLastModificationUser("testUser");
    entity.setLastModificationTime(lastModDate);
    entity.setTypeOfStatus(TypeOfStatus.CURRENT);
    entity.setRuntimePeriod(new RuntimePeriod(lastModDate, lastModDate));

    int j = i + 1;
    // initialize business mapping and all belonging entities
    BusinessMapping bm = new BusinessMapping();
    bm.setId(j);
    bm.setBusinessProcess(this.createBPs(i + 1, name + " - BusinessProcess").iterator().next());
    bm.setProduct(this.createProducts(i + 1, name + " - Product").iterator().next());
    bm.setBusinessUnit(this.createBUs(i + 1, name + " - BusinessUnit").iterator().next());
    entity.addBusinessMapping(bm);

    entity.setBusinessObjects(this.createBOs(i + 1, name + " - BusinessObject"));
    entity.setInformationSystemDomains(this.createISDs(i + 1, name + " - InformationSystemDomain"));

    // initialize information system interface
    InformationSystemInterface isi = new InformationSystemInterface();
    isi.setId(1);
    isi.setInformationSystemReleaseA(entity);
    //     predecessor is taken as element B just for test purposes
    isi.setInformationSystemReleaseB(entity.getPredecessors().iterator().next());
    Set<InformationSystemInterface> isis = new HashSet<InformationSystemInterface>();
    isis.add(isi);
    entity.setInterfacesReleaseA(isis);
    entity.setBusinessFunctions(this.createBFs(i + 1, name + " - BusinessFunction"));
    entity.setTechnicalComponentReleases(this.createTCRs(i + 1, name + " - TechnicalComponentRelease", 3));
    entity.setInfrastructureElements(this.createIEs(i + 1, name + " - InfrastructureElement"));
    entity.setProjects(this.createProjects(i + 1, name + " - Project"));

    //  it does not matter which base components are used for the test
    entity.setBaseComponents(this.createISRs(i + 100, "baseISR", 2, 3, 4));

    InformationSystem is = new InformationSystem();
    is.setId(6);
    is.setName(name);
    is.setReleases(isrs);

    return entity;
  }

  protected List<String> initExpectedISRHeaders(Locale locale) {
    List<String> expContent = this.initCommonExpectedHeaders(locale, ExcelReportTestBase.ISR_SHEET_KEY);

    expContent.add(expContent.size() - 4, MessageAccess.getStringOrNull(Constants.ASSOC_PREDECESSORS, locale));
    expContent.add(expContent.size() - 4, MessageAccess.getStringOrNull(Constants.ASSOC_SUCCESSORS, locale));
    expContent.add(expContent.size() - 4, MessageAccess.getStringOrNull(Constants.ASSOC_USES, locale));
    expContent.add(expContent.size() - 4, MessageAccess.getStringOrNull(Constants.ASSOC_USEDBY, locale));
    expContent.add(expContent.size() - 3, MessageAccess.getStringOrNull(Constants.TIMESPAN_PRODUCTIVE_FROM, locale));
    expContent.add(expContent.size() - 3, MessageAccess.getStringOrNull(Constants.TIMESPAN_PRODUCTIVE_TO, locale));
    expContent.add(expContent.size() - 3, MessageAccess.getStringOrNull(Constants.ATTRIBUTE_TYPEOFSTATUS, locale));

    expContent.add(MessageAccess.getStringOrNull("seal.state", locale));
    expContent.add(MessageAccess.getStringOrNull("seal.verification.date", locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_BUSINESSFUNCTION_PLURAL, locale));

    String bm = MessageAccess.getStringOrNull(Constants.BB_BUSINESSMAPPING_PLURAL, locale);
    String process = MessageAccess.getStringOrNull(Constants.BB_BUSINESSPROCESS, locale);
    String bu = MessageAccess.getStringOrNull(Constants.BB_BUSINESSUNIT, locale);
    String product = MessageAccess.getStringOrNull(Constants.BB_PRODUCT, locale);
    String businessMappingHeaderNameExtended = bm + "(" + process + " / " + product + " / " + bu + ")";
    expContent.add(businessMappingHeaderNameExtended);

    expContent.add(MessageAccess.getStringOrNull(Constants.BB_BUSINESSOBJECT_PLURAL, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL, locale));
    expContent.add(MessageAccess.getStringOrNull("reporting.excel.header.informationSystemRelease.interfacesTo", locale));
    expContent.add(MessageAccess.getStringOrNull("technicalRealisation", locale));
    expContent.add(MessageAccess.getStringOrNull("deploymentInfrastructure", locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_PROJECT_PLURAL, locale));

    return expContent;
  }

  protected List<String> initExpectedTCRHeaders(Locale locale) {
    List<String> expContent = new ArrayList<String>();

    expContent.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_ID, locale));

    expContent.add(MessageAccess.getStringOrNull("technicalRealisation", locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ASSOC_PREDECESSORS, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ASSOC_SUCCESSORS, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ASSOC_USES, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ASSOC_USEDBY, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_DESCRIPTION, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.TIMESPAN_PRODUCTIVE_FROM, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.TIMESPAN_PRODUCTIVE_TO, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_TYPEOFSTATUS, locale));
    expContent.add(MessageAccess.getStringOrNull("technicalComponentRelease.availableForInterfaces", locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_LAST_USER, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.SUBSCRIBED_USERS, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_ARCHITECTURALDOMAIN_PLURAL, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL, locale));
    expContent.add(MessageAccess.getStringOrNull(ExcelConstants.HEADER_TECHNICALCOMPONENTRELEASE_SHEET_INTERFACES_COLUMN, locale));
    expContent.add(MessageAccess.getStringOrNull(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL, locale));

    return expContent;
  }
}
