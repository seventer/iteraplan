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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.QueryTreeGenerator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.ExtensionNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.NotAssociatedLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.TextAT;


public class QueryTreeGeneratorTest extends BaseTransactionalTestSupport {

  private Locale                                 locale                          = Locale.GERMAN;
  @Autowired
  private AttributeTypeService                   attributeTypeService;
  @Autowired
  private TestDataHelper2                        testDataHelper;

  private final InformationSystemReleaseTypeQu   ipureleaseQueryType             = InformationSystemReleaseTypeQu.getInstance();
  private final ProjectQueryType                 projectQueryType                = ProjectQueryType.getInstance();
  private final ArchitecturalDomainTypeQu        adType                          = ArchitecturalDomainTypeQu.getInstance();
  private final InformationSystemInterfaceTypeQu connectionQueryType             = InformationSystemInterfaceTypeQu.getInstance();
  private QueryTreeGenerator                     qtg                             = null;

  private static final String                    BB_ATT_NAME_1                   = "dummyname1";
  private static final String                    BB_ATT_NAME_2                   = "dummyname2";
  private static final String                    BB_ATT_NAME_3                   = "dummyname3";
  private static final String                    STANDARD_START_DATE_2005        = "5.5.2005";
  private static final String                    STANDARD_START_DATE_2006        = "5.5.2006";

  private static final String                    ROOT_NODE_CHILDREN_NR_ERROR_MSG = "Root node does not have right nr. of children";

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    qtg = new QueryTreeGenerator(locale, attributeTypeService);
    createDummyAttributes();
    commit();
    beginTransaction();
  }

  @Test
  public void testGenerateComplexTree() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    DynamicQueryFormData<Project> form1 = new DynamicQueryFormData<Project>(getAvailableProjectAttributes(), ProjectQueryType.getInstance(), locale);
    form1.setExtension(ipureleaseQueryType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS));
    DynamicQueryFormData<ArchitecturalDomain> form2 = new DynamicQueryFormData<ArchitecturalDomain>(getArchitecturalDomainAttributes(),
        ArchitecturalDomainTypeQu.getInstance(), locale);
    form2.setExtension(ipureleaseQueryType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT));

    QUserInput input0 = form0.getQueryUserInput();
    QTimespanData timespanQueryData = input0.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("03.03.2003");
    timespanQueryData.setEndDateAsString("");

    QPart qpart = input0.getQueryFirstLevels().get(0).getQuerySecondLevels().get(0);
    // this should not show up in the query tree because no attribute has been chosen.
    initQPart(qpart, 0, Constants.OPERATION_CONTAINS_ID, "", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    QUserInput input1 = form1.getQueryUserInput();
    timespanQueryData = input1.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("04.04.2004");
    timespanQueryData.setEndDateAsString("04.04.2005");

    qpart = getQPart(input1, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "ittask1", null, Boolean.TRUE, getAvailableProjectAttributes());

    form1.expandSecondLevel(0);
    qpart = getQPart(input1, 0, 1);
    initQPart(qpart, 2, Constants.OPERATION_CONTAINS_ID, null, "ittask2", Boolean.FALSE, getAvailableProjectAttributes());

    QUserInput input2 = form2.getQueryUserInput();
    qpart = getQPart(input2, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "A-Dom1", null, Boolean.TRUE, getArchitecturalDomainAttributes());

    form2.expandFirstLevel();
    qpart = getQPart(input2, 1, 0);
    // this should not show up in the query tree because no attribute has been chosen.
    initQPart(qpart, 0, Constants.OPERATION_CONTAINS_ID, null, "A-Dom9", Boolean.FALSE, getArchitecturalDomainAttributes());

    form2.expandSecondLevel(1);
    qpart = getQPart(input2, 1, 1);
    initQPart(qpart, 2, Constants.OPERATION_CONTAINS_ID, null, "A-Dom2", Boolean.FALSE, getArchitecturalDomainAttributes());

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    forms.add(form1);
    forms.add(form2);
    Node n = qtg.generateQueryTree(forms);

    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 4, ch.size());

    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    pln = (PropertyLeafNode) ch.get(1);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate("03.03.2003", Locale.GERMAN), pln);

    ExtensionNode en = (ExtensionNode) ch.get(2);
    assertEquals(projectQueryType.getTypeNameDB(), en.getExtension().getRequestedType().getTypeNameDB());
    on = (OperationNode) en.getChild();
    assertEquals(on.getChildren().size(), 3);
    pln = (PropertyLeafNode) on.getChildren().get(0);
    doPropertyLeafTest(projectQueryType.getTypeNameDB(), ProjectQueryType.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate("04.04.2004", Locale.GERMAN), pln);

    pln = (PropertyLeafNode) on.getChildren().get(1);
    doPropertyLeafTest(projectQueryType.getTypeNameDB(), ProjectQueryType.PROPERTY_ENDDATE, Comparator.GEQ,
        DateUtils.parseAsDate("04.04.2005", Locale.GERMAN), pln);

    on = (OperationNode) on.getChildren().get(2);
    List<Node> ch2 = on.getChildren();
    assertEquals(2, ch2.size());

    pln = (PropertyLeafNode) ch2.get(0);
    doPropertyLeafTest(projectQueryType.getTypeNameDB(), ProjectQueryType.PROPERTY_NAME, Comparator.LIKE, "*ittask1*", pln);

    AttributeLeafNode aln = (AttributeLeafNode) ch2.get(1);
    doAttributeLeafTest(projectQueryType.getTypeNameDB(), 3, Comparator.LIKE, "*ittask2*", aln);

    en = (ExtensionNode) ch.get(3);
    assertEquals(adType.getTypeNameDB(), en.getExtension().getRequestedType().getTypeNameDB());
    on = (OperationNode) en.getChild();
    List<Node> ch3 = on.getChildren();
    assertEquals(2, ch3.size());

    pln = (PropertyLeafNode) ch3.get(0);
    doPropertyLeafTest(adType.getTypeNameDB(), ArchitecturalDomainTypeQu.PROPERTY_NAME, Comparator.LIKE, "*A-Dom1*", pln);

    aln = (AttributeLeafNode) ch3.get(1);
    doAttributeLeafTest(adType.getTypeNameDB(), 5, Comparator.LIKE, "*A-Dom2*", aln);
  }

  @Test
  public void testGenerateSimpleTree() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    QUserInput input = form0.getQueryUserInput();

    IQStatusData statusQueryData = input.getStatusQueryData();
    statusQueryData.setStatus(InformationSystemRelease.TypeOfStatus.PLANNED.toString(), Boolean.TRUE);

    QTimespanData timespanQueryData = input.getTimespanQueryData();
    timespanQueryData.setStartDateAsString(STANDARD_START_DATE_2005);
    timespanQueryData.setEndDateAsString(STANDARD_START_DATE_2006);

    QPart qpart = getQPart(input, 0, 0);
    // this should not show up in the query tree because no attribute has been chosen.
    initQPart(qpart, 0, Constants.OPERATION_CONTAINS_ID, "testPattern1", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    form0.expandSecondLevel(0);
    qpart = getQPart(input, 0, 1);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "testPattern2", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    form0.expandFirstLevel();
    qpart = getQPart(input, 1, 0);
    initQPart(qpart, 2, Constants.OPERATION_EQUALSNOT_ID, null, "testPattern3", Boolean.FALSE, getAvailableIpureleaseAttributes());

    form0.expandSecondLevel(1);
    qpart = getQPart(input, 1, 1);
    initQPart(qpart, 3, Constants.OPERATION_EQUALSNOT_ID, null, "testPattern4", Boolean.FALSE, getAvailableIpureleaseAttributes());

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    Node n = qtg.generateQueryTree(forms);

    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 5, ch.size());

    on = (OperationNode) ch.get(0);
    List<Node> ch1 = on.getChildren();
    assertEquals(2, ch1.size());

    PropertyLeafNode pln = (PropertyLeafNode) ch1.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    pln = (PropertyLeafNode) ch1.get(1);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.PLANNED, pln);

    pln = (PropertyLeafNode) ch.get(1);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2005, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch.get(2);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2006, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch.get(3);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "*testPattern2*", pln);

    on = (OperationNode) ch.get(4);
    List<Node> ch3 = on.getChildren();
    assertEquals(2, ch3.size());

    AttributeLeafNode aln = (AttributeLeafNode) ch3.get(0);
    doAttributeLeafTest(ipureleaseQueryType, 1, Comparator.NOT_LIKE, "testPattern3", aln);

    aln = (AttributeLeafNode) ch3.get(1);
    doAttributeLeafTest(ipureleaseQueryType, 2, Comparator.NOT_LIKE, "testPattern4", aln);
  }

  @Test
  public void testGenerateConnectionTree() {
    DynamicQueryFormData<InformationSystemInterface> form0 = new DynamicQueryFormData<InformationSystemInterface>(getAvailableConnectionAttributes(),
        InformationSystemInterfaceTypeQu.getInstance(), locale);
    DynamicQueryFormData<InformationSystemRelease> form1 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    form1.setExtension(connectionQueryType.getExtensionsForPresentation().get(InformationSystemInterfaceTypeQu.PRESENTATION_EXTENSION_ISR));

    QUserInput input0 = form0.getQueryUserInput();
    QPart qpart = input0.getQueryFirstLevels().get(0).getQuerySecondLevels().get(0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "conndesc1", null, Boolean.TRUE, getAvailableConnectionAttributes());

    QUserInput input1 = form1.getQueryUserInput();

    IQStatusData statusQueryData = input1.getStatusQueryData();
    statusQueryData.setStatus(InformationSystemRelease.TypeOfStatus.CURRENT.toString(), Boolean.FALSE);
    statusQueryData.setStatus(InformationSystemRelease.TypeOfStatus.PLANNED.toString(), Boolean.TRUE);

    QTimespanData timespanQueryData = input1.getTimespanQueryData();
    timespanQueryData.setStartDateAsString(STANDARD_START_DATE_2005);
    timespanQueryData.setEndDateAsString(STANDARD_START_DATE_2006);

    qpart = getQPart(input1, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_EQUALS_ID, "ipurelease1", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    forms.add(form1);
    Node n = qtg.generateQueryTree(forms);

    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(2, ch.size());

    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(connectionQueryType, InformationSystemInterfaceTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE, "*conndesc1*", pln);

    on = (OperationNode) ch.get(1);
    List<Node> ch2 = on.getChildren();
    assertEquals(2, ch2.size());

    ExtensionNode en = (ExtensionNode) ch2.get(0);
    assertEquals(ipureleaseQueryType.getTypeNameDB(), en.getExtension().getRequestedType().getTypeNameDB());
    assertEquals(InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_A, en.getExtension().getTypesWithJoinProperties().get(0)
        .getAssociationName());
    on = (OperationNode) en.getChild();
    List<Node> ch3 = on.getChildren();
    assertEquals(4, ch3.size());

    pln = (PropertyLeafNode) ch3.get(0);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE, TypeOfStatus.PLANNED, pln);

    pln = (PropertyLeafNode) ch3.get(1);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2005, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch3.get(2);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2006, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch3.get(3);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "ipurelease1", pln);

    en = (ExtensionNode) ch2.get(1);
    assertEquals(ipureleaseQueryType.getTypeNameDB(), en.getExtension().getRequestedType().getTypeNameDB());
    assertEquals(InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_B, en.getExtension().getTypesWithJoinProperties().get(0)
        .getAssociationName());
    on = (OperationNode) en.getChild();
    List<Node> ch6 = on.getChildren();
    assertEquals(4, ch6.size());

    pln = (PropertyLeafNode) ch6.get(0);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE, TypeOfStatus.PLANNED, pln);

    pln = (PropertyLeafNode) ch6.get(1);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2005, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch6.get(2);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_ENDDATE, Comparator.GEQ,
        DateUtils.parseAsDate(STANDARD_START_DATE_2006, Locale.GERMAN), pln);

    pln = (PropertyLeafNode) ch6.get(3);
    doPropertyLeafTest(ipureleaseQueryType, InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE, "ipurelease1", pln);
  }

  @Test
  public void testTreeWithNotAssociatedCombinedExtension() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    DynamicQueryFormData<InformationSystemInterface> form1 = new DynamicQueryFormData<InformationSystemInterface>(null,
        InformationSystemInterfaceTypeQu.getInstance(), locale);
    form1.setExtension(ipureleaseQueryType.getExtensionsForPresentation().get(InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_INTERFACES));
    DynamicQueryFormData<ArchitecturalDomain> form2 = new DynamicQueryFormData<ArchitecturalDomain>(getArchitecturalDomainAttributes(),
        ArchitecturalDomainTypeQu.getInstance(), locale);
    form2.setExtension(ipureleaseQueryType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT));

    QUserInput input0 = form0.getQueryUserInput();
    QTimespanData timespanQueryData = input0.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("");
    timespanQueryData.setEndDateAsString("");

    input0.setQueryFirstLevels(new ArrayList<QFirstLevel>());
    form1.getQueryUserInput().setNoAssignements(Boolean.TRUE);

    QUserInput input2 = form2.getQueryUserInput();
    QPart qpart = getQPart(input2, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "A-Dom1", null, Boolean.TRUE, getArchitecturalDomainAttributes());

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    forms.add(form1);
    forms.add(form2);
    Node n = qtg.generateQueryTree(forms);

    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 3, ch.size());

    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    on = (OperationNode) ch.get(1);
    List<Node> ch2 = on.getChildren();
    assertEquals(2, ch2.size());

    NotAssociatedLeafNode naln = (NotAssociatedLeafNode) ch2.get(0);
    assertEquals(naln.getExtension().getName(), InformationSystemReleaseTypeQu.EXTENSION_INTERFACES_B);
    assertEquals(ipureleaseQueryType.getTypeNameDB(), pln.getResultType().getTypeNameDB());
    assertNull(naln.getPattern());
    assertEquals(naln.getResultType().getTypeNameDB(), ipureleaseQueryType.getTypeNameDB());

    naln = (NotAssociatedLeafNode) ch2.get(1);
    assertEquals(naln.getExtension().getName(), InformationSystemReleaseTypeQu.EXTENSION_INTERFACES_A);
    assertEquals(ipureleaseQueryType.getTypeNameDB(), pln.getResultType().getTypeNameDB());
    assertNull(naln.getPattern());
    assertEquals(naln.getResultType().getTypeNameDB(), ipureleaseQueryType.getTypeNameDB());

    ExtensionNode en = (ExtensionNode) ch.get(2);
    assertEquals(adType.getTypeNameDB(), en.getExtension().getRequestedType().getTypeNameDB());

    pln = (PropertyLeafNode) en.getChild();
    doPropertyLeafTest(adType.getTypeNameDB(), ArchitecturalDomainTypeQu.PROPERTY_NAME, Comparator.LIKE, "*A-Dom1*", pln);
  }

  @Test
  public void testTreeWithTwoCombinedExtensions() {
    Node n = generateNodeWithTwoCombinedExtensions();

    // root-node (and)
    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 4, ch.size());

    // status (1st child of root-node)
    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    // end date (2nd child of root-node)
    pln = (PropertyLeafNode) ch.get(1);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_STARTDATE, Comparator.LEQ,
        DateUtils.parseAsDate("01.01.2006", Locale.GERMAN), pln);

    // or-node (3rd child of root-node)
    on = (OperationNode) ch.get(2);
    List<Node> ch2 = on.getChildren();
    assertEquals(2, ch2.size());

    // extension node (1st child of or-node)
    ExtensionNode en = (ExtensionNode) ch2.get(0);
    assertEquals(InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_B, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());

    // and-node (1st child of 1st extension node)
    on = (OperationNode) en.getChild();
    List<Node> ch3 = on.getChildren();
    doOperationNodeTest(on, Operation.AND.toString(), 2);

    for (int i = 0; i < 2; i++) {
      // description ("i"st child of and-node)
      pln = (PropertyLeafNode) ch3.get(i);
      doPropertyLeafTest(connectionQueryType.getTypeNameDB(), InformationSystemInterfaceTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE,
          "*connectionDescription" + (i + 1) + "*", pln);
    }

    // extension node (2nd child of or-node)
    en = (ExtensionNode) ch2.get(1);
    assertEquals(InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_A, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());

    // and-node (1st child of 2nd extension node)
    on = (OperationNode) en.getChild();
    List<Node> ch6 = on.getChildren();
    doOperationNodeTest(on, Operation.AND.toString(), 2);

    for (int i = 0; i < 2; i++) {
      // description ("i"st child of and-node)
      pln = (PropertyLeafNode) ch6.get(i);
      doPropertyLeafTest(connectionQueryType.getTypeNameDB(), InformationSystemInterfaceTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE,
          "*connectionDescription" + (i + 1) + "*", pln);
    }

    // or-node (4th child of root-node)
    on = (OperationNode) ch.get(3);
    List<Node> ch9 = on.getChildren();
    doOperationNodeTest(on, Operation.OR.toString(), 2);

    // extension node (1st child of or-node)
    en = (ExtensionNode) ch9.get(0);
    doExtensionNodeTest(en, 2, InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_A,
        InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_B);

    // and-node (1st child of 1st extension node)
    on = (OperationNode) en.getChild();
    List<Node> ch11 = on.getChildren();
    doOperationNodeTest(on, Operation.AND.toString(), 2);

    // status (first child of and-node)
    pln = (PropertyLeafNode) ch11.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    // or-node (second child of and-node)
    on = (OperationNode) ch11.get(1);
    testLastOrNode(on, en, ch9);
  }

  @Test
  public void testInformationSystemInterfaceExtensionWithDirectionAttribute() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    QUserInput input = form0.getQueryUserInput();

    IQStatusData statusQueryData = input.getStatusQueryData();
    statusQueryData.setStatus(InformationSystemRelease.TypeOfStatus.CURRENT.toString(), Boolean.TRUE);

    QTimespanData timespanQueryData = input.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("");
    timespanQueryData.setEndDateAsString("");

    DynamicQueryFormData<InformationSystemInterface> form1 = new DynamicQueryFormData<InformationSystemInterface>(getAvailableConnectionAttributes(),
        InformationSystemInterfaceTypeQu.getInstance(), locale);
    form1.setExtension(ipureleaseQueryType.getExtensionsForPresentation().get(InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_INTERFACES));

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    forms.add(form1);

    QUserInput input1 = form1.getQueryUserInput();
    QPart qpart = getQPart(input1, 0, 0);

    testInformationSystemInterfaceExtensionWithDirectionAttributeUnidirectional(qpart, forms, Direction.FIRST_TO_SECOND.toString());
    testInformationSystemInterfaceExtensionWithDirectionAttributeUnidirectional(qpart, forms, Direction.SECOND_TO_FIRST.toString());
    testInformationSystemInterfaceExtensionWithDirectionAttributeBidirectional(qpart, forms, Direction.NO_DIRECTION.toString());
    testInformationSystemInterfaceExtensionWithDirectionAttributeBidirectional(qpart, forms, Direction.BOTH_DIRECTIONS.toString());
  }

  @Test
  public void testInformationSystemInterfaceExtentionWithoutFilter() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    QUserInput input = form0.getQueryUserInput();

    IQStatusData statusQueryData = input.getStatusQueryData();
    statusQueryData.setStatus(InformationSystemRelease.TypeOfStatus.CURRENT.toString(), Boolean.TRUE);

    QTimespanData timespanQueryData = input.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("");
    timespanQueryData.setEndDateAsString("");

    DynamicQueryFormData<InformationSystemInterface> form1 = new DynamicQueryFormData<InformationSystemInterface>(getAvailableConnectionAttributes(),
        InformationSystemInterfaceTypeQu.getInstance(), locale);
    form1.setExtension(ipureleaseQueryType.getExtensionsForPresentation().get(InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_INTERFACES));

    ArrayList<DynamicQueryFormData<?>> forms = Lists.newArrayList();
    forms.add(form0);
    forms.add(form1);

    //test would fail here with ClassCastException because of ITERAPLAN-1174
    qtg.generateQueryTree(forms);
  }

  private void testInformationSystemInterfaceExtensionWithDirectionAttributeUnidirectional(QPart qpart, List<DynamicQueryFormData<?>> forms,
                                                                                           String direction) {
    initQPart(qpart, 3, Constants.OPERATION_EQUALS_ID, null, direction, Boolean.FALSE, getAvailableConnectionAttributes());

    Node n = qtg.generateQueryTree(forms);

    // root-node (and)
    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 2, ch.size());

    // status (1st child of root-node)
    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    // extension node (1st child of or-node)
    ExtensionNode en = (ExtensionNode) ch.get(1);
    assertEquals(InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_A, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());

    // and-node (child of extension node)
    pln = (PropertyLeafNode) en.getChild();
    doPropertyLeafTest(connectionQueryType.getTypeNameDB(), InformationSystemInterfaceTypeQu.DIRECTION, Comparator.LIKE, direction, pln);
  }

  private void testInformationSystemInterfaceExtensionWithDirectionAttributeBidirectional(QPart qpart, List<DynamicQueryFormData<?>> forms,
                                                                                          String direction) {
    initQPart(qpart, 3, Constants.OPERATION_EQUALS_ID, null, direction, Boolean.FALSE, getAvailableConnectionAttributes());

    Node n = qtg.generateQueryTree(forms);

    // root-node (and)
    OperationNode on = (OperationNode) n;
    List<Node> ch = on.getChildren();
    assertEquals(ROOT_NODE_CHILDREN_NR_ERROR_MSG, 2, ch.size());

    // status (1st child of root-node)
    PropertyLeafNode pln = (PropertyLeafNode) ch.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    // or-node (2nd child of root-node)
    on = (OperationNode) ch.get(1);
    List<Node> ch2 = on.getChildren();
    assertEquals(2, ch2.size());

    // extension node (1st child of or-node)
    ExtensionNode en = (ExtensionNode) ch2.get(0);
    assertEquals(InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_B, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());

    // and-node (child of extension node)
    pln = (PropertyLeafNode) en.getChild();
    doPropertyLeafTest(connectionQueryType.getTypeNameDB(), InformationSystemInterfaceTypeQu.DIRECTION, Comparator.LIKE, direction, pln);

    // extension node (2nd child of or-node)
    en = (ExtensionNode) ch2.get(1);
    assertEquals(InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_A, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());

    // and-node (child of extension node)
    pln = (PropertyLeafNode) en.getChild();
    doPropertyLeafTest(connectionQueryType.getTypeNameDB(), InformationSystemInterfaceTypeQu.DIRECTION, Comparator.LIKE, direction, pln);
  }

  private List<BBAttribute> getAvailableConnectionAttributes() {
    List<BBAttribute> availableAttributes = new ArrayList<BBAttribute>();
    availableAttributes.add(new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));
    availableAttributes.add(new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_TYPE, BB_ATT_NAME_1,
        InformationSystemInterfaceTypeQu.PROPERTY_DESCRIPTION));
    availableAttributes.add(new BBAttribute(Integer.valueOf(2), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_3, null));
    availableAttributes.add(new BBAttribute(Integer.valueOf(3), BBAttribute.FIXED_ATTRIBUTE_ENUM, "dummyname4",
        InformationSystemInterfaceTypeQu.DIRECTION));
    return availableAttributes;
  }

  private List<BBAttribute> getAvailableIpureleaseAttributes() {
    List<BBAttribute> availableAttributes = new ArrayList<BBAttribute>();
    availableAttributes.add(new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));
    availableAttributes.add(new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_TYPE, BB_ATT_NAME_1, InformationSystemReleaseTypeQu.PROPERTY_NAME));
    availableAttributes.add(new BBAttribute(Integer.valueOf(1), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_2, null));
    availableAttributes.add(new BBAttribute(Integer.valueOf(2), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_3, null));
    return availableAttributes;
  }

  private List<BBAttribute> getAvailableProjectAttributes() {
    List<BBAttribute> availableAttributes = new ArrayList<BBAttribute>();
    availableAttributes.add(new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));
    availableAttributes.add(new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_TYPE, BB_ATT_NAME_1, ProjectQueryType.PROPERTY_NAME));
    availableAttributes.add(new BBAttribute(Integer.valueOf(3), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_2, null));
    availableAttributes.add(new BBAttribute(Integer.valueOf(4), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_3, null));
    return availableAttributes;
  }

  private List<BBAttribute> getArchitecturalDomainAttributes() {
    List<BBAttribute> availableAttributes = new ArrayList<BBAttribute>();
    availableAttributes.add(new BBAttribute(null, BBAttribute.BLANK_ATTRIBUTE_TYPE, Constants.ATTRIBUTE_BLANK, null));
    availableAttributes.add(new BBAttribute(null, BBAttribute.FIXED_ATTRIBUTE_TYPE, BB_ATT_NAME_1, ArchitecturalDomainTypeQu.PROPERTY_NAME));
    availableAttributes.add(new BBAttribute(Integer.valueOf(5), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_2, null));
    availableAttributes.add(new BBAttribute(Integer.valueOf(6), BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, BB_ATT_NAME_3, null));
    return availableAttributes;
  }

  private void createDummyAttributes() {

    AttributeTypeGroup atg = new AttributeTypeGroup();
    atg.setName("group");
    atg.setToplevelATG(Boolean.FALSE);
    atg.setPosition(Integer.valueOf(0));
    getHibernateTemplate().save(atg);
    for (int i = 0; i < 10; i++) {
      TextAT attr = new TextAT();
      attr.setName(String.valueOf(i));
      attr.setAttributeTypeGroupTwoWay(atg);

      getHibernateTemplate().save(attr);
    }
  }

  private void initQPart(QPart qpart, int attributeNr, Integer operationId, String freeTextCriteria, String existingCriteria, Boolean freeTextChosen,
                         List<BBAttribute> attributes) {
    qpart.setChosenAttributeStringId(attributes.get(attributeNr).getStringId());
    qpart.setChosenOperationId(operationId);
    qpart.setFreeTextCriteria(freeTextCriteria);
    qpart.setExistingCriteria(existingCriteria);
    qpart.setFreeTextCriteriaSelected(freeTextChosen);
  }

  private QPart getQPart(QUserInput input, int firstLevelindex, int secondLevelindex) {
    return input.getQueryFirstLevels().get(firstLevelindex).getQuerySecondLevels().get(secondLevelindex);
  }

  private void doPropertyLeafTest(Object expect1, String expect2, Comparator expect3, Object expect4, PropertyLeafNode pln) {
    if (expect1 instanceof String) {
      assertEquals(expect1, pln.getResultType().getTypeNameDB());
    }
    else if (expect1 instanceof Type<?>) {
      assertEquals(expect1, pln.getResultType());
    }
    assertEquals(expect2, pln.getPropertyName());
    assertEquals(expect3, pln.getComparator());
    assertEquals(expect4, pln.getPattern());
    assertNull(pln.getExtension());
  }

  private void doAttributeLeafTest(Object expect1, int expect2, Comparator expect3, Object expect4, AttributeLeafNode aln) {
    if (expect1 instanceof String) {
      assertEquals(expect1, aln.getResultType().getTypeNameDB());
    }
    else if (expect1 instanceof Type<?>) {
      assertEquals(expect1, aln.getResultType());
    }
    assertEquals(expect2, aln.getAttributeId());
    assertEquals(expect3, aln.getComparator());
    assertEquals(expect4, aln.getPattern());
    assertNull(aln.getExtension());
  }

  private void doOperationNodeTest(OperationNode on, String operation, int expect) {
    assertEquals(operation, on.getOperation().toString());
    assertEquals(on.getChildren().size(), expect);
  }

  private void doExtensionNodeTest(ExtensionNode en, int expect1, String expect2, String expect3) {
    assertEquals(expect1, en.getExtension().getTypesWithJoinProperties().size());
    assertEquals(expect2, en.getExtension().getTypesWithJoinProperties().get(0).getAssociationName());
    assertEquals(expect3, en.getExtension().getTypesWithJoinProperties().get(1).getAssociationName());
  }

  private Node generateNodeWithTwoCombinedExtensions() {
    DynamicQueryFormData<InformationSystemRelease> form0 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    DynamicQueryFormData<InformationSystemInterface> form1 = new DynamicQueryFormData<InformationSystemInterface>(getAvailableConnectionAttributes(),
        InformationSystemInterfaceTypeQu.getInstance(), locale);
    DynamicQueryFormData<InformationSystemRelease> form2 = new DynamicQueryFormData<InformationSystemRelease>(getAvailableIpureleaseAttributes(),
        InformationSystemReleaseTypeQu.getInstance(), locale);
    form1.setExtension(ipureleaseQueryType.getExtensionsForPresentation().get(InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_INTERFACES));
    form2.setExtension(ipureleaseQueryType.getExtensionsForPresentation().get(
        InformationSystemReleaseTypeQu.PRESENTATION_EXTENSION_IPURELEASEOVERCONNECTION));

    QUserInput input0 = form0.getQueryUserInput();
    QTimespanData timespanQueryData = input0.getTimespanQueryData();
    timespanQueryData.setStartDateAsString("01.01.2006");
    timespanQueryData.setEndDateAsString("");
    input0.setQueryFirstLevels(new ArrayList<QFirstLevel>()); // ?

    QUserInput input1 = form1.getQueryUserInput();
    QPart qpart = getQPart(input1, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "connectionDescription1", null, Boolean.TRUE, getAvailableConnectionAttributes());

    form1.expandFirstLevel();
    qpart = getQPart(input1, 1, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "connectionDescription2", null, Boolean.TRUE, getAvailableConnectionAttributes());

    QUserInput input2 = form2.getQueryUserInput();
    // input2.getStatusQueryData().setStatus(TypeOfStatus.CURRENT.toString(), Boolean.FALSE);
    input2.getTimespanQueryData().setStartDateAsString("");
    input2.getTimespanQueryData().setEndDateAsString("");

    qpart = getQPart(input2, 0, 0);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "connectedIpureleaseName1", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    form2.expandSecondLevel(0);
    qpart = getQPart(input2, 0, 1);
    initQPart(qpart, 1, Constants.OPERATION_CONTAINS_ID, "connectedIpureleaseName2", null, Boolean.TRUE, getAvailableIpureleaseAttributes());

    List<DynamicQueryFormData<?>> forms = new ArrayList<DynamicQueryFormData<?>>();
    forms.add(form0);
    forms.add(form1);
    forms.add(form2);
    return qtg.generateQueryTree(forms);
  }

  private void testLastOrNode(OperationNode onInput, ExtensionNode enInput, List<Node> ch9) {
    OperationNode on = onInput;
    ExtensionNode en = enInput;
    PropertyLeafNode pln;
    List<Node> ch12 = on.getChildren();
    doOperationNodeTest(on, Operation.OR.toString(), 2);

    for (int i = 0; i < 2; i++) {
      // name ("i"st child of or-node)
      pln = (PropertyLeafNode) ch12.get(i);
      doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE,
          "*connectedIpureleaseName" + (i + 1) + "*", pln);
    }

    // extension node (2nd child of or-node)
    en = (ExtensionNode) ch9.get(1);
    doExtensionNodeTest(en, 2, InformationSystemReleaseTypeQu.ASSOCIATION_INTERFACES_B,
        InformationSystemInterfaceTypeQu.ASSOCIATION_INFORMATIONSYSTEMRELEASE_A);

    // and-node (1st child of 1st extension node)
    on = (OperationNode) en.getChild();
    List<Node> ch13 = on.getChildren();
    doOperationNodeTest(on, Operation.AND.toString(), 2);

    // status (first child of and-node)
    pln = (PropertyLeafNode) ch13.get(0);
    doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TypeOfStatus.CURRENT, pln);

    // or-node (second child of and-node)
    on = (OperationNode) ch13.get(1);
    List<Node> ch14 = on.getChildren();
    doOperationNodeTest(on, Operation.OR.toString(), 2);

    for (int i = 0; i < 2; i++) {
      // name (1st child of or-node)
      pln = (PropertyLeafNode) ch14.get(i);
      doPropertyLeafTest(ipureleaseQueryType.getTypeNameDB(), InformationSystemReleaseTypeQu.PROPERTY_NAME, Comparator.LIKE,
          "*connectedIpureleaseName" + (i + 1) + "*", pln);
    }
  }
}
