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
package de.iteratec.iteraplan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


public final class MockTestDataFactory {

  private static final Logger        LOGGER   = Logger.getIteraplanLogger(MockTestDataFactory.class);

  private static MockTestDataFactory instance = new MockTestDataFactory();
  
  private Random random = new Random();

  /**  
   * Default-Konstruktor, der nicht auﬂerhalb dieser Klasse
   * aufgerufen werden kann
   */
  private MockTestDataFactory() {
    //Default-Konstruktor
  }

  public static MockTestDataFactory getInstance() {
    return instance;
  }

  public <T> T generateTestObject(Class<T> clazz) {
    T obj = null;
    try {
      obj = clazz.newInstance();

      for (Method m : clazz.getMethods()) {
        if (m.getName().startsWith("set") && !m.getName().endsWith("Id") && (m.getParameterTypes().length == 1)) {
          int rn = random.nextInt() * 10000;
          Class<?> param = m.getParameterTypes()[0];
          String nm = param.getSimpleName();
          if (("String").equals(nm)) {
            m.invoke(obj, new Object[] { clazz.getSimpleName() + "." + m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4) + "."
                + rn });
          }
          else if (("long").equals(nm) || ("int").equals(nm) || ("double").equals(nm)) {
            m.invoke(obj, new Object[] { Integer.valueOf(rn) });
          }
          else if (("Long").equals(nm)) {
            m.invoke(obj, new Object[] { Long.valueOf(rn) });
          }
          else if (("Integer").equals(nm)) {
            m.invoke(obj, new Object[] { Integer.valueOf(rn) });
          }
          else if (("boolean").equals(nm)) {
            m.invoke(obj, new Object[] { Boolean.TRUE });
          }
          else if (("Boolean").equals(nm)) {
            m.invoke(obj, new Object[] { Boolean.TRUE });
          }
          else if (("Locale").equals(nm)) {
            m.invoke(obj, new Object[] { Locale.GERMAN });
          }
          else if (("RuntimePeriod").equals(nm)) {
            m.invoke(obj, new Object[] { new RuntimePeriod(new Date(), new Date()) });
          }
        }
      }
    } catch (InvocationTargetException e) {
      LOGGER.error(e);
    } catch (IllegalArgumentException e) {
      LOGGER.error(e);
    } catch (IllegalAccessException e) {
      LOGGER.error(e);
    } catch (InstantiationException e) {
      LOGGER.error(e);
    }
    return obj;
  }

  public static BuildingBlockType getBuildingBlockType(TypeOfBuildingBlock typeOfBuildingBlock) {
    BuildingBlockType buildingBlockType = new BuildingBlockType(typeOfBuildingBlock);
    buildingBlockType.setId(Integer.valueOf(buildingBlockType.getTypeOfBuildingBlock().toString().hashCode()));
    return buildingBlockType;
  }

  @SuppressWarnings("deprecation")
  public InformationSystemRelease getIsrTestData() {
    InformationSystemRelease isr = generateTestObject(InformationSystemRelease.class);
    isr.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    return isr;
  }

  @SuppressWarnings("deprecation")
  public InformationSystem getInformationSystem() {
    InformationSystem is = generateTestObject(InformationSystem.class);
    is.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEM));
    return is;
  }

  @SuppressWarnings("deprecation")
  public Project getProjectTestData() {
    Project project = generateTestObject(Project.class);
    project.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.PROJECT));
    return project;
  }

  @SuppressWarnings("deprecation")
  public Product getProductTestData() {
    Product product = generateTestObject(Product.class);
    product.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.PRODUCT));
    return product;
  }

  @SuppressWarnings("deprecation")
  public ArchitecturalDomain getArchitecturalDomainTestData() {
    ArchitecturalDomain ad = generateTestObject(ArchitecturalDomain.class);
    ad.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    return ad;
  }

  @SuppressWarnings("deprecation")
  public InformationSystem getInformationSystemTestData() {
    InformationSystem is = generateTestObject(InformationSystem.class);
    is.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEM));
    return is;
  }

  @SuppressWarnings("deprecation")
  public InformationSystemRelease getInformationSystemReleaseTestData() {
    InformationSystemRelease isr = generateTestObject(InformationSystemRelease.class);
    isr.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    return isr;
  }

  @SuppressWarnings("deprecation")
  public TechnicalComponent getTechnicalComponentTestData() {
    TechnicalComponent tc = generateTestObject(TechnicalComponent.class);
    tc.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENT));
    return tc;
  }

  @SuppressWarnings("deprecation")
  public TechnicalComponentRelease getTechnicalComponentReleaseTestData() {
    TechnicalComponentRelease tcr = generateTestObject(TechnicalComponentRelease.class);
    tcr.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    return tcr;
  }

  @SuppressWarnings("deprecation")
  public InfrastructureElement getInfrastructureElementTestData() {
    InfrastructureElement ie = generateTestObject(InfrastructureElement.class);
    ie.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    return ie;
  }

  @SuppressWarnings("deprecation")
  public InformationSystemDomain getInformationSystemDomainTestData() {

    InformationSystemDomain isd = generateTestObject(InformationSystemDomain.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN);
    isd.setBuildingBlockType(type);
    return isd;
  }

  @SuppressWarnings("deprecation")
  public InformationSystemInterface getInformationSystemInterfaceTestData() {

    InformationSystemInterface isi = generateTestObject(InformationSystemInterface.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    isi.setBuildingBlockType(type);
    return isi;
  }

  @SuppressWarnings("deprecation")
  public BusinessUnit getBusinessUnitTestData() {

    BusinessUnit bu = generateTestObject(BusinessUnit.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.BUSINESSUNIT);
    bu.setBuildingBlockType(type);
    return bu;
  }

  @SuppressWarnings("deprecation")
  public BusinessProcess getBusinessProcessTestData() {

    BusinessProcess bp = generateTestObject(BusinessProcess.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS);
    bp.setBuildingBlockType(type);
    return bp;

  }

  @SuppressWarnings("deprecation")
  public BusinessObject getBusinessObjectTestData() {

    BusinessObject bp = generateTestObject(BusinessObject.class);
    bp.setBuildingBlockType(getBuildingBlockType(TypeOfBuildingBlock.BUSINESSOBJECT));
    bp.setId(Integer.valueOf(137));
    return bp;
  }

  @SuppressWarnings("deprecation")
  public BusinessFunction getBusinessFunctionTestData() {

    BusinessFunction bm = generateTestObject(BusinessFunction.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.BUSINESSFUNCTION);
    bm.setBuildingBlockType(type);
    return bm;
  }

  @SuppressWarnings("deprecation")
  public BusinessDomain getBusinessDomainTestData() {

    BusinessDomain bd = generateTestObject(BusinessDomain.class);
    BuildingBlockType type = getBuildingBlockType(TypeOfBuildingBlock.BUSINESSDOMAIN);
    bd.setBuildingBlockType(type);
    return bd;
  }

  public UserContext createUserContext() {
    User user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("system");

    Set<Role> roles = new HashSet<Role>();
    Role role = new Role();
    role.setRoleName(Role.SUPERVISOR_ROLE_NAME);
    roles.add(role);
    UserContext userContext = new UserContext(user.getLoginName(), roles, new Locale("de"), user);
    UserContext.setCurrentUserContext(userContext);
    return userContext;
  }

  public String getRandomString(int length) {
    return RandomStringUtils.random(length);
  }

}
