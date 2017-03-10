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
package de.iteratec.iteraplan.presentation.dialog.IteraQl;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeBean;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeDestination;
import de.iteratec.iteraplan.businesslogic.reports.interchange.InterchangeOrigin;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiService;
import de.iteratec.iteraplan.businesslogic.service.InitFormHelperService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RAtomicDataTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;
import de.iteratec.iteraplan.elasticmi.model.BindingSet;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.Either;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.presentation.GuiContext;
import de.iteratec.iteraplan.presentation.dialog.GuiController;
import de.iteratec.iteraplan.presentation.dialog.Interchange.InterchangeController;


@Controller
public class IteraQlController extends GuiController {

  private static final Logger                           LOGGER             = Logger.getIteraplanLogger(IteraQlController.class);

  private static final String                           DIALOG_MEMORY_NAME = "dialogMemory";

  private static final Map<String, TypeOfBuildingBlock> ute2bbTypeMap;

  static {
    ute2bbTypeMap = Maps.newHashMap();
    ute2bbTypeMap.put("ArchitecturalDomain", TypeOfBuildingBlock.ARCHITECTURALDOMAIN);
    ute2bbTypeMap.put("BusinessDomain", TypeOfBuildingBlock.BUSINESSDOMAIN);
    ute2bbTypeMap.put("BusinessFunction", TypeOfBuildingBlock.BUSINESSFUNCTION);
    ute2bbTypeMap.put("BusinessObject", TypeOfBuildingBlock.BUSINESSOBJECT);
    ute2bbTypeMap.put("BusinessProcess", TypeOfBuildingBlock.BUSINESSPROCESS);
    ute2bbTypeMap.put("BusinessUnit", TypeOfBuildingBlock.BUSINESSUNIT);
    ute2bbTypeMap.put("InformationSystemDomain", TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN);
    ute2bbTypeMap.put("InformationSystemInterface", TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE);
    ute2bbTypeMap.put("InformationSystem", TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    ute2bbTypeMap.put("InfrastructureElement", TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    ute2bbTypeMap.put("Product", TypeOfBuildingBlock.PRODUCT);
    ute2bbTypeMap.put("Project", TypeOfBuildingBlock.PROJECT);
    ute2bbTypeMap.put("TechnicalComponent", TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE);
  }

  @Autowired
  private final ElasticMiService                        elasticMiService;

  @Autowired
  private BuildingBlockServiceLocator                   buildingBlockServiceLocator;

  @Autowired
  private InterchangeController                         interchangeController;

  @Autowired
  private InitFormHelperService                         initFormHelperService;

  @Autowired
  public IteraQlController(ElasticMiService elasticMiService) {
    this.elasticMiService = elasticMiService;
  }

  @Override
  @RequestMapping
  public void init(ModelMap model, HttpSession session, HttpServletRequest request) {

    super.init(model, session, request);

    UserContext.getCurrentUserContext().getPerms().assureFunctionalPermission(TypeOfFunctionalPermission.ITERAQL);

    GuiContext context = GuiContext.getCurrentGuiContext();

    IteraQlDialogMemory dialogMemory;
    if (context.hasDialogMemory(getDialogName())) {
      // if the page was already accessed once, use the existing dialogMemory
      dialogMemory = (IteraQlDialogMemory) context.getDialogMemory(getDialogName());
    }
    else {
      // else create new one
      dialogMemory = new IteraQlDialogMemory();
    }

    model.addAttribute(DIALOG_MEMORY_NAME, dialogMemory);

    this.updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public void sendQuery(@RequestParam(value = "query", required = false)
  String query, @ModelAttribute(DIALOG_MEMORY_NAME)
  IteraQlDialogMemory dialogMemory, Model model, HttpSession session) {
    if (query == null || query.isEmpty()) {
      clear(dialogMemory, model, session);
    }
    else {
      try {
        dialogMemory.clearErrors();

        long initBegin = System.currentTimeMillis();
        IteraQlQuery compiledQuery = elasticMiService.compile(query);
        Either<ElasticValue<ObjectExpression>, BindingSet> queryResult = elasticMiService.executeQuery(compiledQuery);
        long initEnd = System.currentTimeMillis();

        TypeOfBuildingBlock type = null;
        if (compiledQuery.isLeft()) {
          RStructuredTypeExpression canonicBase = compiledQuery.getLeft().getCanonicBase();
          if (canonicBase != null) {
            type = ute2bbTypeMap.get(canonicBase.getPersistentName());
          }
        }

        if (type != null) {
          dialogMemory.setAvailableDestinations(InterchangeDestination.getSupportedDestinationStrings(type, InterchangeOrigin.ITERAQL));
          dialogMemory.setSelectedDestination("");

          ManageReportMemoryBean interchangeMemBean = initFormHelperService.getInitializedReportMemBean(type.getPluralValue());
          List<BuildingBlock> results = convertToBuildingBlocks(type, queryResult.getLeft().getMany());
          Collections.sort(results, new BuildingBlockComparator());
          interchangeMemBean.setResults(results);
          interchangeMemBean.setShowResults(Boolean.TRUE);
          interchangeMemBean.setCheckAllBox(Boolean.TRUE);
          interchangeMemBean.getQueryResult().setSelectedResultIds(GeneralHelper.createIdArrayFromIdEntities(results));

          model.addAttribute("memBean", interchangeMemBean);

          dialogMemory.getQueryResult().setSelectedResultIds(interchangeMemBean.getQueryResult().getSelectedResultIds());
        }
        else {
          IteraQlQueryResult iteraQlBean = null;
          if (compiledQuery.isLeft()) {
            iteraQlBean = createResultForSyntheticType(compiledQuery.getLeft(), queryResult.getLeft(), (initEnd - initBegin));
          }
          else if (compiledQuery.isRight()) {
            iteraQlBean = createResultForBindingSet(queryResult.getRight(), (initEnd - initBegin));
          }
          model.addAttribute("iteraQlBean", iteraQlBean);
        }

      } catch (IteraQl2Exception e) {
        dialogMemory.addError(IteraplanErrorMessages.getErrorMessage(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, new Object[] { e.getMessage() },
            UserContext.getCurrentLocale()));
      }

      model.addAttribute(DIALOG_MEMORY_NAME, dialogMemory);
      updateGuiContext(dialogMemory);
    }
  }

  @RequestMapping
  public void clear(@ModelAttribute(DIALOG_MEMORY_NAME)
  IteraQlDialogMemory dialogMemory, Model model, HttpSession session) {

    dialogMemory.setQuery("");
    dialogMemory.clearErrors();

    model.addAttribute(DIALOG_MEMORY_NAME, dialogMemory);
    updateGuiContext(dialogMemory);
  }

  @RequestMapping
  public String interchange(ModelMap model, @ModelAttribute(DIALOG_MEMORY_NAME)
  IteraQlDialogMemory dialogMemory, @ModelAttribute("memBean")
  ManageReportMemoryBean memBean, @RequestParam(value = "selectedDestination", required = true)
  String selectedDestination, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

    if (memBean.getQueryResult().getSelectedResultIds() == null || Arrays.asList(memBean.getQueryResult().getSelectedResultIds()).isEmpty()) {
      dialogMemory.addError(IteraplanErrorMessages.getErrorMessage(IteraplanErrorMessages.INTERCHANGE_NOTHING_SELECTED, null,
          UserContext.getCurrentLocale()));
      model.addAttribute(DIALOG_MEMORY_NAME, dialogMemory);

      model.addAttribute("memBean", null);
      updateGuiContext(dialogMemory);

      return "iteraql/sendQuery";
    }

    InterchangeBean bean = new InterchangeBean();
    bean.setInterchangeDestination(InterchangeDestination.getInterchangeResultByString(selectedDestination));
    bean.setTypeOfBuildingBlock(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock()));
    bean.setSelectedIds(memBean.getQueryResult().getSelectedResultIds());

    return interchangeController.interchange(model, bean, request);
  }

  private static IteraQlQueryResult createResultForSyntheticType(RStructuredTypeExpression rste, ElasticValue<ObjectExpression> elasticValueResult,
                                                                 long queryExecTime) {
    IteraQlQueryResult resultMemBean = new IteraQlQueryResult();
    resultMemBean.setExecTime(queryExecTime);

    List<IteraQlResultEntry> presentationEntries = Lists.newArrayList();
    RPropertyExpression idProperty = null;
    RPropertyExpression nameProperty = null;
    RPropertyExpression descriptionProperty = null;

    resultMemBean.setBindingSetResult(false);
    resultMemBean.setType1Name(rste.getName());
    resultMemBean.setType1Size(elasticValueResult.getSize());

    idProperty = rste.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_ID);
    nameProperty = rste.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    descriptionProperty = rste.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);

    for (ObjectExpression fromExpr : elasticValueResult.getMany()) {
      presentationEntries.add(new IteraQlResultEntry(getPropertyValue(fromExpr, idProperty), getPropertyValue(fromExpr, nameProperty),
          getPropertyValue(fromExpr, descriptionProperty), null, null, null));
    }

    resultMemBean.setResultEntries(presentationEntries);
    return resultMemBean;
  }

  private static IteraQlQueryResult createResultForBindingSet(BindingSet bindingSetResult, long queryExecTime) {
    IteraQlQueryResult resultMemBean = new IteraQlQueryResult();
    resultMemBean.setExecTime(queryExecTime);

    List<IteraQlResultEntry> presentationEntries = Lists.newArrayList();
    RPropertyExpression idProperty = null;
    RPropertyExpression nameProperty = null;
    RPropertyExpression descriptionProperty = null;

    resultMemBean.setBindingSetResult(true);
    resultMemBean.setType1Name(bindingSetResult.getFromType().getName());
    resultMemBean.setType2Name(bindingSetResult.getToType().getName());
    resultMemBean.setType1Size(bindingSetResult.getAllFromElements().getSize());
    resultMemBean.setType2Size(bindingSetResult.getAllToElements().getSize());

    idProperty = bindingSetResult.getFromType().findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_ID);
    nameProperty = bindingSetResult.getFromType().findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    descriptionProperty = bindingSetResult.getFromType().findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);

    RPropertyExpression idProperty2 = bindingSetResult.getToType().findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_ID);
    RPropertyExpression nameProperty2 = bindingSetResult.getToType().findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    RPropertyExpression descriptionProperty2 = bindingSetResult.getToType().findPropertyByPersistentName(
        ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);

    for (ObjectExpression fromExpr : bindingSetResult.getAllFromElements().getMany()) {
      for (ObjectExpression toExpr : bindingSetResult.apply(fromExpr).getMany()) {
        presentationEntries.add(new IteraQlResultEntry(getPropertyValue(fromExpr, idProperty), getPropertyValue(fromExpr, nameProperty),
            getPropertyValue(fromExpr, descriptionProperty), getPropertyValue(toExpr, idProperty2), getPropertyValue(toExpr, nameProperty2),
            getPropertyValue(toExpr, descriptionProperty2)));
      }
    }

    resultMemBean.setResultEntries(presentationEntries);
    return resultMemBean;
  }

  private List<BuildingBlock> convertToBuildingBlocks(TypeOfBuildingBlock bbType, Collection<ObjectExpression> objectExpressions) {
    List<BuildingBlock> candidates = buildingBlockServiceLocator.getService(bbType).loadElementList();
    List<BuildingBlock> results = Lists.newArrayList();
    Set<Integer> oeIds = getAllResultIds(objectExpressions);
    for (BuildingBlock bb : candidates) {
      if (oeIds.contains(bb.getId())) {
        results.add(bb);
      }
    }
    return results;
  }

  private static Set<Integer> getAllResultIds(Collection<ObjectExpression> objectExpressions) {
    Set<Integer> ids = Sets.newHashSet();
    for (ObjectExpression expression : objectExpressions) {
      BigInteger val = expression.getId();
      ids.add(Integer.valueOf(val.intValue()));
    }
    return ids;
  }

  private static String getPropertyValue(ObjectExpression onInstance, RPropertyExpression forProperty) {
    if (forProperty == null) {
      return null;
    }
    ElasticValue<ValueExpression> ev = forProperty.apply(onInstance);
    return ev.isNone() ? null : ev.getOne().getValue().toString();
  }

  @Override
  protected String getDialogName() {
    return Dialog.ITERAQL.getDialogName();
  }

  @RequestMapping
  public void metamodel(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    try {
      RMetamodel metamodel = elasticMiService.getRMetamodel();
      printMetamodel(metamodel, response.getWriter());
    } catch (IOException ignored) {
      System.out.println("Could not write to printwriter");
    }
  }

  @RequestMapping
  public void model(ModelMap model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
    try {
      de.iteratec.iteraplan.elasticmi.model.Model mod = elasticMiService.getModel();

      printModel(mod, elasticMiService.getRMetamodel(), response.getWriter());
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }

  private void printMetamodel(RMetamodel metamodel, PrintWriter writer) {
    printAtomicDataTypes(metamodel, writer);
    printEnumerations(metamodel, writer);
    printStructuredTypes(metamodel, writer);
    printRelationshipTypes(metamodel, writer);
  }

  private void printAtomicDataTypes(RMetamodel metamodel, PrintWriter writer) {
    for (RAtomicDataTypeExpression<?> type : metamodel.getAtomicDataTypes()) {
      writer.write("--" + type.getPersistentName() + "--(atomic data type)\n");
    }
  }

  private void printEnumerations(RMetamodel metamodel, PrintWriter writer) {
    for (RNominalEnumerationExpression ee : metamodel.getEnumerationTypes()) {
      writer.write("--" + ee.getPersistentName() + "--(enum)\n");
      for (REnumerationLiteralExpression el : ee.getLiterals()) {
        writer.write("\t" + el.getPersistentName() + "\n");
      }
    }
  }

  private void printStructuredTypes(RMetamodel metamodel, PrintWriter writer) {
    Iterable<RStructuredTypeExpression> structuredTypesClass = Iterables.filter(metamodel.getStructuredTypes(), new RStructuredTypeFilter(
        OriginalWType.CLASS));
    for (RStructuredTypeExpression st : structuredTypesClass) {
      writer.write("--" + st.getPersistentName() + "--(structured type (CLASS) '" + st.getAbbreviation() + "')\n");
      for (RPropertyExpression pe : st.getAllProperties()) {
        writer.write("\t" + pe.getPersistentName() + "[" + pe.getLowerBound() + ":" + pe.getUpperBound() + "]:");
        writer.write(pe.getPersistentName());
        writer.write("\n");
      }
      for (RRelationshipEndExpression re : st.getAllRelationshipEnds()) {
        writer.write("\t" + re.getPersistentName() + "[" + re.getLowerBound() + ":" + re.getUpperBound() + "]:");
        writer.write(re.getType().getPersistentName() + "\n");
      }
    }
  }

  private void printRelationshipTypes(RMetamodel metamodel, PrintWriter writer) {
    Iterable<RStructuredTypeExpression> structuredTypesRel = Iterables.filter(metamodel.getStructuredTypes(), new RStructuredTypeFilter(
        OriginalWType.RELATIONSHIP));
    for (RStructuredTypeExpression rste : structuredTypesRel) {
      writer.write("--" + rste.getPersistentName() + "--(RelationshipType)\n");
      for (RPropertyExpression pe : rste.getAllProperties()) {
        writer.write("\t" + pe.getPersistentName() + "[" + pe.getLowerBound() + ":" + pe.getUpperBound() + "]:");
        writer.write(pe.getPersistentName());
        writer.write("\n");
      }
      for (RRelationshipEndExpression ree : rste.getAllRelationshipEnds()) {
        writer.write("\t" + ree.getPersistentName() + "[" + ree.getLowerBound() + ":" + ree.getUpperBound() + "]\n");
      }
    }
  }

  private void printModel(de.iteratec.iteraplan.elasticmi.model.Model repo, RMetamodel metamodel, PrintWriter writer) {
    Iterable<RStructuredTypeExpression> structuredTypesClass = Iterables.filter(metamodel.getStructuredTypes(), new RStructuredTypeFilter(
        OriginalWType.CLASS));
    Iterable<RStructuredTypeExpression> structuredTypesRel = Iterables.filter(metamodel.getStructuredTypes(), new RStructuredTypeFilter(
        OriginalWType.RELATIONSHIP));
    List<RStructuredTypeExpression> types = Lists.newArrayList(Iterables.concat(structuredTypesClass, structuredTypesRel));
    for (RStructuredTypeExpression type : types) {
      writer.write(type.getPersistentName() + ":\n");
      for (ObjectExpression modelExpression : type.apply(repo).getMany()) {
        for (RPropertyExpression pe : type.getAllProperties()) {
          ElasticValue<ValueExpression> peValue = modelExpression.getValues(pe);
          if (peValue.isOne()) {
            writer.write("\t" + pe.getPersistentName() + ": " + modelExpression.getValues(pe).getOne() + "\n");
          }
          if (peValue.isMany()) {
            writer.write("\t" + pe.getPersistentName() + ": " + modelExpression.getValues(pe).getMany() + "\n");
          }
        }
        for (RRelationshipEndExpression ree : type.getAllRelationshipEnds()) {
          ElasticValue<ObjectExpression> reeValues = modelExpression.getConnecteds(ree);
          if (reeValues.isOne()) {
            writer.write("\t" + ree.getPersistentName() + ": " + modelExpression.getConnecteds(ree).getOne() + "\n");
          }
          if (reeValues.isMany()) {
            writer.write("\t" + ree.getPersistentName() + ": " + modelExpression.getConnecteds(ree).getMany() + "\n");
          }
        }
      }
    }
  }

  private static class RStructuredTypeFilter implements Predicate<RStructuredTypeExpression> {
    private OriginalWType originalWType;

    RStructuredTypeFilter(OriginalWType originalWType) {
      this.originalWType = originalWType;
    }

    /**{@inheritDoc}**/
    @Override
    public boolean apply(RStructuredTypeExpression input) {
      return originalWType == null ? true : originalWType.equals(input.getOriginalWType());
    }
  }
}
