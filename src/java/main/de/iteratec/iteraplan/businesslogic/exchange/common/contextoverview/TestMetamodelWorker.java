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

import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.TestMetamodelBuilder;


/**
 * This class instantiates a customized metamodel
 */
public class TestMetamodelWorker {

  private TestMetamodelBuilder builder;

  public TestMetamodelWorker() {
    builder = new TestMetamodelBuilder();
  }

  public PojoRMetamodel buildMetaModel() {
    
    builder.addStructuredType("BusinessMapping", "Business Mapping", "Fachliche Zuordnungen");
    builder.addStructuredType("BusinessUnit", "Business Unit", "Geschäftseinheit");
    builder.addStructuredType("Product", "Product", "Produkt");
    builder.addStructuredType("BusinessProcess", "Business Process", "Geschäftsprozess");
    builder.addStructuredType("Isr2BoAssociation", "Isr2BoAssociation", "Isr2BoAssociation");
    builder.addStructuredType("BusinessObject", "Business Object", "Geschäftsobjekt");
    builder.addStructuredType("BusinessFunction", "Business Function", "Fachliche Funktion");
    builder.addStructuredType("InformationSystem", "Information System", "Informationssystem");
    builder.addStructuredType("InformationFlow", "Information Flow", "Informationsfluss");
    builder.addStructuredType("InformationSystemInterface", "Interface","Schnittstelle");
    builder.addStructuredType("Project", "Project", "Projekt");
    builder.addStructuredType("TechnicalComponent", "Technical Component", "Technischer Baustein");
    builder.addStructuredType("InfrastructureElement", "Infrastructure Element", "Infrastrukturelement");
    builder.addStructuredType("InformationSystemDomain", "Information System Domain", "IS-Domäne");
    

    builder.addRelationship("InformationSystemDomain", "informationSystemReleases", "Affected Information Systems", "Betroffene Informationssysteme",
        0, 2147483647, false, "InformationSystem", "informationSystemDomains", "Belongs to the following Information System Domains",
        "Gehört zu den folgenden IS-Domänen", 0, 2147483647, false);

    builder.addRelationship("InfrastructureElement", "informationSystemReleases", "Affected Information Systems", "Betroffene Informationssysteme",
        0, 2147483647, false, "InformationSystem", "infrastructureElements", "Runs on the following Infrastructure Elements",
        "Wird auf den folgenden Infrastrukturelementen betrieben", 0, 2147483647, false);

    builder.addRelationship("TechnicalComponent", "informationSystemReleases", "Affected Information Systems", "Betroffene Informationssysteme", 0,
        2147483647, false, "InformationSystem", "technicalComponentReleases", "Is based on the following Technical Components",
        "Basiert auf den folgenden Technischen Bausteinen", 0, 2147483647, false);

    builder.addRelationship("Project", "informationSystemReleases", "Affected Information Systems", "Betroffene Informationssysteme", 0, 2147483647,
        false, "InformationSystem", "projects", "Projects", "Projekte", 0, 2147483647, false);

    builder.addRelationship("InformationSystemInterface", "informationFlows", "InformationFlows", "Informationsflüsse", 1, 2147483647, false,
        "InformationFlow", "informationSystemInterface", "Is transported by the following Interface",
        "Wird bei folgender Informationssystemschnittstelle transportiert", 0, 1, true);

    builder.addRelationship("InformationFlow", "informationSystemRelease1", "Information System Release 1", "Informationssystem Release 1", 0, 1,
        true, "InformationSystem", "informationFlows1", "Information Flows 1", "Informationsflüsse 1", 0, 2147483647, false);

    builder.addRelationship("InformationFlow", "informationSystemRelease2", "Information System Release 2", "Informationssystem Release 2", 0, 1,
        true, "InformationSystem", "informationFlows2", "Information Flows 2", "Informationsflüsse 2", 0, 2147483647, false);

    builder.addRelationship("BusinessFunction", "informationSystems", "Information Systems", "Informationssysteme", 0, 2147483647, false,
        "InformationSystem", "businessFunctions", "Supports the following Business Functions", "Unterstützt die folgenden Fachlichen Funktionen", 0,
        2147483647, false);

    builder.addRelationship("Isr2BoAssociation", "informationSystemRelease", "Information System Release", "Informationssystem Release", 1, 1, true,
        "InformationSystem", "businessObjectAssociations", "Business Object Associations", "Geschäftsobjektassoziationen", 0, 2147483647, false);

    builder.addRelationship("BusinessObject", "informationSystemReleaseAssociations", "Information System Release Associations", "IS-Assoziationen",
        0, 2147483647, false, "Isr2BoAssociation", "businessObject", "Business Object", "Geschäftsobjekt", 1, 1, true);

    builder.addRelationship("BusinessMapping", "informationSystemRelease", "Business Mapping", "Fachliche Zuordnung", 1, 1, true,
        "InformationSystem", "businessMappings", "Business Mappings", "Fachliche Zuordnungen", 0, 2147483647, false);

    builder.addRelationship("BusinessProcess", "businessMappings", "Business Mappings", "Fachliche Zuordnungen", 0, 2147483647, false,
        "BusinessMapping", "businessProcess", "Business Process", "Geschäftsprozess", 0, 1, true);

    builder.addRelationship("Product", "businessMappings", "Business Mappings", "Fachliche Zuordnungen", 0, 2147483647, false, "BusinessMapping",
        "product", "Product", "Produkt", 0, 1, true);

    builder.addRelationship("BusinessUnit", "businessMappings", "Business Mappings", "Fachliche Zuordnungen", 0, 2147483647, false,
        "BusinessMapping", "businessUnit", "Business Unit", "Geschäftseinheit", 0, 1, true);
    
    builder.addRelationship("InformationSystem", "baseComponents", "Uses the following Information Systems",
        "Verwendet die folgenden Infrastrukturelemente", 0, 2147483647, false, "InformationSystem", "parentComponents",
        "Used by the following Information Systems", "Wird von den folgenden Infrastrukturelementen verwendet", 0, 2147483647, false);

    return builder.getMetamodel();
  }
  


}
