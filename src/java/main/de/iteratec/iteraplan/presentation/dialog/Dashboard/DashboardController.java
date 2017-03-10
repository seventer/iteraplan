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
package de.iteratec.iteraplan.presentation.dialog.Dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeBean;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeDestination;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.DashboardService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.DashboardElementLists;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.dialog.Interchange.InterchangeController;


@Controller
public class DashboardController extends GuiController {

  private static final String                   BBT            = "bbt";
  private static final String                   AT             = "at";
  private static final String                   AV             = "av";
  private static final String                   DESTINATION    = "selectedDestination";

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private QueryService     queryService;

  @Autowired
  private InterchangeController       interchangeController;

  @Autowired
  private BuildingBlockServiceLocator  bbServiceLocator;

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {
    super.init(model, session, request);

    GuiContext context = GuiContext.getCurrentGuiContext();

    DashboardDialogMemory dashboardDialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      dashboardDialogMemory = (DashboardDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      dashboardDialogMemory = new DashboardDialogMemory();
    }

    DashboardElementLists elements = dashboardService.getElementLists();
    dashboardDialogMemory.setBbMap(dashboardService.getNumberOfElementsMap(elements));
    dashboardDialogMemory.setIsrStatusMap(dashboardService.getIsrStatusMap(elements.getIsrList()));
    dashboardDialogMemory.setIsrSealStateMap(dashboardService.getIsrSealStateMap(elements.getIsrList()));
    dashboardDialogMemory.setTcrStatusMap(dashboardService.getTechnicalComponentsStatusMap(elements.getTcrList()));
    dashboardDialogMemory.setTopUsedTcrMap(dashboardService.getTopUsedTcr(elements.getTcrList()));
    dashboardDialogMemory.setTopUsedTcrByAdMap(dashboardService.getTopUsedTcrByAdMap(elements));
    dashboardDialogMemory.setTopUsedIsrMap(dashboardService.getTopUsedIsr(elements.getIsrList()));

    addDashboardAttributes(model, elements);

    model.addAttribute("dialogMemory", dashboardDialogMemory);
    //set dialogue memory to model and update GUI context
    updateGuiContext(dashboardDialogMemory);
  }

  @SuppressWarnings("cast")
  @RequestMapping
  public String interchange(ModelMap model,
                            @RequestParam(value = BBT, required = true) String bbt,
                            @RequestParam(value = AT, required = true) String at,
                            @RequestParam(value = AV, required = true) String av,
                            @RequestParam(value = DESTINATION, required = true) String selectedDestination,
                            HttpServletRequest request, HttpServletResponse response) {

    TypeOfBuildingBlock typeOfBB = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbt);
    List<BBAttribute> bbAttributesForGraphicalExport = queryService.getBBAttributesForGraphicalExport(typeOfBB);

    List<? extends BuildingBlock> bbBlist = bbServiceLocator.getService(typeOfBB).loadElementList();
    Map<String, Map<String,List<Integer>>> valueMap = dashboardService.getValueMap(bbAttributesForGraphicalExport, bbBlist);
    Map<String, List<Integer>> valueToIdsMap = valueMap.get(at);

    InterchangeBean bean = new InterchangeBean();
    bean.setInterchangeDestination(InterchangeDestination.getInterchangeResultByString(selectedDestination));
    bean.setTypeOfBuildingBlock(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbt));
    Integer[] ids = new Integer[valueToIdsMap.get(av).size()];
    ids = (Integer[])valueToIdsMap.get(av).toArray(ids);
    bean.setSelectedIds(ids);

    if(StringUtils.equals(at, "global.type_of_status")) {
      bean.setIsStatusSelected(Boolean.TRUE);
      bean.setSelectedStatusValue(av);
    }
    else if (StringUtils.equals(at, "seal")) {
      bean.setIsSealSelected(Boolean.TRUE);
      bean.setSelectedSealValue(av);
    }

    return interchangeController.interchange(model, bean, request);
  }

  public void addDashboardAttributes(ModelMap model, DashboardElementLists elements) {
    model.addAttribute("businessDomain.", getValueMap(elements.getBdList().get(0).getBuildingBlockType(), elements.getBdList()));
    model.addAttribute("businessFunction.", getValueMap(elements.getBfList().get(0).getBuildingBlockType(), elements.getBfList()));
    model.addAttribute("architecturalDomain.", getValueMap(elements.getAdList().get(0).getBuildingBlockType(), elements.getAdList()));
    model.addAttribute("businessObject.", getValueMap(elements.getBoList().get(0).getBuildingBlockType(), elements.getBoList()));
    model.addAttribute("businessProcess.", getValueMap(elements.getBpList().get(0).getBuildingBlockType(), elements.getBpList()));
    model.addAttribute("businessUnit.", getValueMap(elements.getBuList().get(0).getBuildingBlockType(), elements.getBuList()));
    model.addAttribute("infrastructureElement.", getValueMap(elements.getIeList().get(0).getBuildingBlockType(), elements.getIeList()));
    model.addAttribute("informationSystemDomain.", getValueMap(elements.getIsdList().get(0).getBuildingBlockType(), elements.getIsdList()));

    // we have to handle the case when the list of non-hierarchical elements (interfaces) is empty
    if (elements.getIsiList().isEmpty()) {
      List<InformationSystemInterface> isiList = Lists.newArrayList();
      InformationSystemInterface isiFirst = new InformationSystemInterface();
      isiList.add(isiFirst);
      model.addAttribute("interface.", getValueMap(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), isiList));
    }
    else {
      if (!elements.getIsrList().isEmpty()) {
        model.addAttribute("interface.", getValueMap(elements.getIsiList().get(0).getBuildingBlockType(), elements.getIsiList()));
      }
    }

    // we have to handle the case when the list of non-hierarchical elements (information systems) is empty
    if (elements.getIsrList().isEmpty()) {
      List<InformationSystemRelease> isrList = Lists.newArrayList();
      InformationSystemRelease isrFirst = new InformationSystemRelease();
      isrList.add(isrFirst);
      model.addAttribute("informationSystemRelease.", getValueMap(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), isrList));

      List<InformationSystemInterface> isiList = new ArrayList<InformationSystemInterface>();
      InformationSystemInterface isiFirst = new InformationSystemInterface();
      isiList.add(isiFirst);
      model.addAttribute("interface.", getValueMap(new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), isiList));
    }
    else {
      List<InformationSystemRelease> isrList = elements.getIsrList();
      BuildingBlockType buildingBlockType = isrList.get(0).getBuildingBlockType();
      TypeOfBuildingBlock typeOfBB = buildingBlockType.getTypeOfBuildingBlock();
      List<BBAttribute> bbAttributesForGraphicalExport = queryService.getBBAttributesForGraphicalExport(typeOfBB);
      Map<String, Map<String, List<Integer>>> valueMap = dashboardService.getValueMap(bbAttributesForGraphicalExport, isrList);

      model.addAttribute("informationSystemRelease.", valueMap);
    }

    model.addAttribute("product.", getValueMap(elements.getProdList().get(0).getBuildingBlockType(), elements.getProdList()));
    model.addAttribute("project.", getValueMap(elements.getProjList().get(0).getBuildingBlockType(), elements.getProjList()));

    // we have to handle the case when the list of non-hierarchical elements (technnical component releases) is empty
    if (elements.getTcrList().isEmpty()) {
      List<TechnicalComponentRelease> tcrList = Lists.newArrayList();
      TechnicalComponentRelease tcrFirst = new TechnicalComponentRelease();
      tcrList.add(tcrFirst);
      model.addAttribute("technicalComponentRelease.", getValueMap(new BuildingBlockType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), tcrList));
    }
    else {
      model.addAttribute("technicalComponentRelease.", getValueMap(elements.getTcrList().get(0).getBuildingBlockType(), elements.getTcrList()));
    }
  }

  private Map<String, Map<String, List<Integer>>> getValueMap(BuildingBlockType bbt, List<? extends BuildingBlock> bbBlist) {
    TypeOfBuildingBlock typeOfBB = bbt.getTypeOfBuildingBlock();
    List<BBAttribute> bbAttributesForGraphicalExport = queryService.getBBAttributesForGraphicalExport(typeOfBB);
    return dashboardService.getValueMap(bbAttributesForGraphicalExport, bbBlist);
  }

  @Override
  protected String getDialogName() {
    return Dialog.DASHBOARD.getDialogName();
  }

}
