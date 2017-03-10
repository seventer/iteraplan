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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.iteratec.iteraplan.TestAsSuperUser;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Junit test class.
 * 
 * @author Tobias Dietl (iteratec GmbH)
 * @version $Id: $
 */
public class ExtensionNodeTest extends TestAsSuperUser {

  private static final String WRONG_QUERY_ERROR_MSG = "wrong HQL query";

  @Test
  public void testIsrTcr() {
    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);
    ExtensionNode exNode = new ExtensionNode(isrType, ex);
    TechnicalComponentRelease t1 = new TechnicalComponentRelease();
    t1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new TechnicalComponentRelease[] { t1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.InformationSystemRelease:isr0[Subcriteria(isr0.technicalComponentReleases:tcr1)][(tcr1.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

  @Test
  public void testIsrFromConnectionFromIsr() {
    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_FROMISRELEASEOVERFROMCONNECTION);
    ExtensionNode exNode = new ExtensionNode(isrType, ex);
    InformationSystemRelease i1 = new InformationSystemRelease();
    i1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new InformationSystemRelease[] { i1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.InformationSystemRelease:isr0[Subcriteria(isr0.interfacesReleaseA:isi1), Subcriteria(isi1.informationSystemReleaseB:isr2)][(isr2.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

  @Test
  public void testTcrUsedTcr() {
    TechnicalComponentReleaseTypeQu tcrType = TechnicalComponentReleaseTypeQu.getInstance();
    Extension ex = tcrType.getExtension(TechnicalComponentReleaseTypeQu.EXTENSION_BASECOMPONENTS);
    ExtensionNode exNode = new ExtensionNode(tcrType, ex);
    TechnicalComponentRelease t1 = new TechnicalComponentRelease();
    t1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new TechnicalComponentRelease[] { t1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.TechnicalComponentRelease:tcr0[Subcriteria(tcr0.baseComponents:tcr1)][(tcr1.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

  @Test
  public void testBusinessprocSuperBusinessproc() {
    BusinessProcessTypeQ bpType = BusinessProcessTypeQ.getInstance();
    Extension ex = bpType.getExtension(BusinessProcessTypeQ.EXTENSION_PARENT);
    ExtensionNode exNode = new ExtensionNode(bpType, ex);
    BusinessProcess bp1 = new BusinessProcess();
    bp1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new BusinessProcess[] { bp1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.BusinessProcess:bp0[Subcriteria(bp0.parent:bp1)][(bp1.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

  @Test
  public void testBusinessobjGeneralisationBusinessobj() {
    BusinessObjectTypeQu boType = BusinessObjectTypeQu.getInstance();
    Extension ex = boType.getExtension(BusinessObjectTypeQu.EXTENSION_GENERALISATION);
    ExtensionNode exNode = new ExtensionNode(boType, ex);
    BusinessObject bo1 = new BusinessObject();
    bo1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new BusinessObject[] { bo1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.BusinessObject:bo0[Subcriteria(bo0.generalisation:bo1)][(bo1.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

  @Test
  public void testIeTcr() {
    InfrastructureElementTypeQu ieTypeQu = InfrastructureElementTypeQu.getInstance();
    Extension ex = ieTypeQu.getExtension(InfrastructureElementTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);
    ExtensionNode exNode = new ExtensionNode(ieTypeQu, ex);
    TechnicalComponentRelease tcr1 = new TechnicalComponentRelease();
    tcr1.setId(Integer.valueOf(1));
    Set<BuildingBlock> bbs = new HashSet<BuildingBlock>(Arrays.asList(new TechnicalComponentRelease[] { tcr1 }));
    exNode.setLeafNodeBuildingBlocks(bbs);

    String expected = "DetachableCriteria(CriteriaImpl(de.iteratec.iteraplan.model.InfrastructureElement:ci0[Subcriteria(ci0.technicalComponentReleaseAssociations:tcr1), Subcriteria(tcr1.technicalComponentRelease:tcr2)][(tcr2.id in (1))]))";
    assertEquals(WRONG_QUERY_ERROR_MSG, expected, exNode.getCriteria().toString());
  }

}
