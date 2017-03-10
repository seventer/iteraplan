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
package de.iteratec.iteraplan.businesslogic.exchange.common.contextoverview;

import java.math.BigInteger;
import java.util.List;

import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * Class which uses different strategy implementations to load related building blocks of the central IS.
 * Also collects all necessary data for the creation of the SVG-Diagram and instantiates it.
 */
public class ContextOverviewCreator extends NameUriMapper {

  //private static final List<InfoContainer> CONTAINERS = Lists.newArrayList();
  private RMetamodel                       metamodel;
  private Model                            model;
  private ContextOverviewDiagram           diagram;
  private String                 serverURL;
  private String                 centralIsPngUrl;

  public ContextOverviewCreator(RMetamodel mm, Model m, String serverURL) {
    this.serverURL = serverURL;
    metamodel = mm;
    model = m;
    diagram = new ContextOverviewDiagram();
    centralIsPngUrl = serverURL + "/images/IS.png";
  }
  
  private void appendDataToDiagram(int slotId, String pngUrl, String messageKey, ContextOverviewDataStrategy strategy, int insId) {
    diagram.setMessageKey(slotId, messageKey);
    diagram.setPngURLs(slotId, pngUrl);
    diagram.setBuildingBlocks(slotId, cutOverflow(strategy.getBuildingBlocks(insId)));
  }

  // This method cuts of overflowing entries and writes the number of hidden entries in the 6th line 
  private List<NameUriPair> cutOverflow(List<NameUriPair> allPairs) {

    String xMore = " " + "global.xmore";
    int count = allPairs.size();
    if (count < 7) {
      return allPairs;
    }
    // else
    int overflow = count - 5;
    xMore = overflow + xMore;
    NameUriPair sixthEntrie = new NameUriPair(xMore, null);
    List<NameUriPair> relevantPairs = allPairs.subList(0, 5);
    relevantPairs.add(sixthEntrie);
    return allPairs;

  }

  //This method loads the information about related Building Blocks and the central IS and writes it into the Context Overview Diagram
  public ContextOverviewDiagram createContextOverviewDiagram(int insId) {


    appendDataToDiagram(0, serverURL + "/images/Organisation.png", "businessUnit.singular", new RelationTypeConnect("businessMappings",
        "businessUnit", metamodel, model,
        serverURL), insId);
    appendDataToDiagram(1, serverURL + "/images/Product.png", "global.product", new RelationTypeConnect("businessMappings", "product", metamodel,
        model, serverURL), insId);
    appendDataToDiagram(2, serverURL + "/images/Process.png", "businessProcess.singular", new RelationTypeConnect("businessMappings",
        "businessProcess", metamodel, model,
        serverURL), insId);
    appendDataToDiagram(3, serverURL + "/images/Object.png", "businessObject.singular", new RelationTypeConnect("businessObjectAssociations",
        "businessObject", metamodel,
        model, serverURL), insId);
    appendDataToDiagram(4, serverURL + "/images/Function.png", "global.business_function", new DirectConnect("businessFunctions", metamodel, model,
        serverURL),
        insId);
    appendDataToDiagram(5, serverURL + "/images/IS.png", "global.usedBy", new DirectConnect("parentComponents", metamodel, model, serverURL), insId);
    appendDataToDiagram(6, serverURL + "/images/IS.png", "global.uses", new DirectConnect("baseComponents", metamodel, model, serverURL), insId);
    appendDataToDiagram(7, serverURL + "/images/IS.png", "global.viainterfaceconnectedis", new DoubleRelationTypeConnect("informationSystemRelease1",
        "informationSystemRelease2", "InformationSystem", metamodel, model, serverURL), insId);
    appendDataToDiagram(8, serverURL + "/images/Interface.png", "interface.singular", new DoubleRelationTypeConnect("informationSystemInterface",
        "InformationSystemInterface", metamodel, model, serverURL), insId);
    appendDataToDiagram(9, serverURL + "/images/Project.png", "project.singular", new DirectConnect("projects", metamodel, model, serverURL), insId);
    appendDataToDiagram(10, serverURL + "/images/Technical.png", "technicalComponent.singular", new DirectConnect("technicalComponentReleases",
        metamodel, model, serverURL),
        insId);
    appendDataToDiagram(11, serverURL + "/images/Infrastructure.png", "infrastructureElement.singular", new DirectConnect("infrastructureElements",
        metamodel, model, serverURL),
        insId);
    appendDataToDiagram(12, serverURL + "/images/Domain.png", "informationSystemDomain.singular",
        new DirectConnect("informationSystemDomains", metamodel, model, serverURL), insId);

    diagram.setCentralIsPng(centralIsPngUrl);

    RStructuredTypeExpression Is = metamodel.findStructuredTypeByPersistentName("InformationSystem");
    ObjectExpression centralIs = model.findById(Is, BigInteger.valueOf(insId));
    RPropertyExpression IsNameProp = Is.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    String nameOfIs = IsNameProp.apply(centralIs).getOne().asString();
    NameUriPair isNameUriPair = extractPair(nameOfIs, centralIs, Is, serverURL);

    diagram.setCentralIsNameUriPair(isNameUriPair);

    return diagram;
  }

}
