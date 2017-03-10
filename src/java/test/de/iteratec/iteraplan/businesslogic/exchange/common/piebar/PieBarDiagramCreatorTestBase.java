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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.persistence.dao.BusinessFunctionDAO;


public class PieBarDiagramCreatorTestBase extends BaseTransactionalTestSupport {

  private final List<BusinessObject>   boList    = CollectionUtils.arrayList();
  private final List<BusinessFunction> bfList    = CollectionUtils.arrayList();
  private NumberAT                     numberAT;
  private EnumAT                       enumAT;
  private final List<AttributeValue>   enumAVs   = CollectionUtils.arrayList();
  private final List<AttributeValue>   numberAVs = CollectionUtils.arrayList();

  private PieBarDiagramOptionsBean     pieBarOptions;

  @Autowired
  private InitFormHelperService        initFormHelperService;
  @Autowired
  private BusinessFunctionDAO          businessFunctionDAO;
  @Autowired
  private TestDataHelper2 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    
    createBuildingBlocks();
    createAttributes();
    setAssociations();
    assignAttributeValues();

    pieBarOptions = new PieBarDiagramOptionsBean();
    pieBarOptions.setSelectedBbType("businessObject.plural");

    initPieBarOptions();

    commit();
  }

  public InitFormHelperService getInitFormHelperService() {
    return initFormHelperService;
  }

  protected void initPieBarOptions() {
    ManageReportMemoryBean tempBean = initFormHelperService.getInitializedReportMemBean(pieBarOptions.getSelectedBbType());
    tempBean.setGraphicalOptions(pieBarOptions);
    initFormHelperService.initializePieBarOptions(tempBean);

    //needs to be set explicitly since the tempBean doesn't have a valid resultList of building blocks
    pieBarOptions.setMaxNumberOfAssociatedEntities(determineMaxNumberOfAssociatedEntities());
  }

  private Map<String, Integer> determineMaxNumberOfAssociatedEntities() {
    List<TypeOfBuildingBlock> tobbList = TypeOfBuildingBlock.BUSINESSOBJECT.getConnectedTypesOfBuildingBlocks();
    List<String> availableAssociations = CollectionUtils.arrayList();
    for (TypeOfBuildingBlock tobb : tobbList) {
      if (TypeOfBuildingBlock.BUSINESSOBJECT.equals(tobb)) {
        availableAssociations.addAll(tobb.getSelfReferencesPropertyKeys());
      }
      else {
        availableAssociations.add(tobb.getValue());
      }
    }
    pieBarOptions.setAvailableAssociations(availableAssociations);

    Map<String, Integer> maxNumberOfAssociatedEntities = CollectionUtils.hashMap();

    for (String association : pieBarOptions.getAllAvailableAssociations()) {
      if (TypeOfBuildingBlock.BUSINESSFUNCTION.getValue().equals(association)) {
        // current maximum number of associated business functions, see setAssociations() 
        maxNumberOfAssociatedEntities.put(association, Integer.valueOf(3));
      }
      else {
        maxNumberOfAssociatedEntities.put(association, Integer.valueOf(0));
      }
    }
    return maxNumberOfAssociatedEntities;
  }

  private void createBuildingBlocks() {
    boList.clear();
    for (int i = 1; i <= 5; i++) {
      boList.add(testDataHelper.createBusinessObject("BO" + i, "BO " + i + " description"));
    }

    BusinessFunction rootBF = businessFunctionDAO.getFirstElement();
    bfList.clear();
    for (int i = 1; i <= 5; i++) {
      BusinessFunction bf = testDataHelper.createBusinessFunction("BF" + i, "BF " + i + " description");
      bf.addParent(rootBF);
      bfList.add(bf);
    }
  }

  private void createAttributes() {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    numberAT = testDataHelper.createNumberAttributeType("NumberAT", "Number AT description", atg);
    numberAT.setBuildingBlockTypes(CollectionUtils.hashSet(boList.get(0).getBuildingBlockType()));
    enumAT = testDataHelper.createEnumAttributeType("EnumAT", "Enum AT description", Boolean.TRUE, atg);
    enumAT.setBuildingBlockTypes(CollectionUtils.hashSet(boList.get(0).getBuildingBlockType()));

    for (int i = 1; i <= 5; i++) {
      enumAVs.add(testDataHelper.createEnumAV("av" + i, "enum AV description " + i, enumAT));
    }

    for (int i = 1; i <= 10; i++) {
      numberAVs.add(testDataHelper.createNumberAV(new BigDecimal(i * i), numberAT));
    }
  }

  private void setAssociations() {
    boList.get(0).addBusinessFunctions(CollectionUtils.hashSet(bfList.get(0)));
    boList.get(1).addBusinessFunctions(CollectionUtils.hashSet(bfList.get(1), bfList.get(3), bfList.get(4)));
    boList.get(3).addBusinessFunctions(CollectionUtils.hashSet(bfList.get(2), bfList.get(4)));
    boList.get(4).addBusinessFunctions(CollectionUtils.hashSet(bfList.get(1)));
  }

  private void assignAttributeValues() {
    testDataHelper.createAVA(boList.get(0), numberAVs.get(0));
    testDataHelper.createAVA(boList.get(0), enumAVs.get(0));
    testDataHelper.createAVA(boList.get(0), enumAVs.get(1));

    testDataHelper.createAVA(boList.get(1), numberAVs.get(9));
    testDataHelper.createAVA(boList.get(1), enumAVs.get(3));

    testDataHelper.createAVA(boList.get(2), numberAVs.get(4));
    testDataHelper.createAVA(boList.get(2), enumAVs.get(0));
    testDataHelper.createAVA(boList.get(2), enumAVs.get(1));
    testDataHelper.createAVA(boList.get(2), enumAVs.get(3));
    testDataHelper.createAVA(boList.get(2), enumAVs.get(4));

    testDataHelper.createAVA(boList.get(3), numberAVs.get(5));

    testDataHelper.createAVA(boList.get(4), numberAVs.get(7));
    testDataHelper.createAVA(boList.get(4), enumAVs.get(2));
    testDataHelper.createAVA(boList.get(4), enumAVs.get(4));
  }

  protected List<BuildingBlock> getBoList() {
    List<BuildingBlock> resultList = CollectionUtils.arrayList();
    for (BuildingBlock bb : boList) {
      resultList.add(bb);
    }
    return resultList;
  }

  protected List<BusinessFunction> getBfList() {
    return bfList;
  }

  protected NumberAT getNumberAT() {
    return numberAT;
  }

  protected EnumAT getEnumAT() {
    return enumAT;
  }

  protected List<AttributeValue> getEnumAVs() {
    return enumAVs;
  }

  protected List<AttributeValue> getNumberAVs() {
    return numberAVs;
  }

  public PieBarDiagramOptionsBean getPieBarOptions() {
    return pieBarOptions;
  }

  protected void refresh() {
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(getPieBarOptions().getSelectedBbType());
    getInitFormHelperService().refreshGraphicalExportColorOptionsForPieBar(getPieBarOptions().getDiagramValuesType(), getPieBarOptions(),
        getPieBarOptions().getColorOptionsBean(), tobb);
  }

}
