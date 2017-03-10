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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WClassExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WTypeGroup;
import de.iteratec.iteraplan.elasticmi.metamodel.write.impl.WMetamodelImpl;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;


/**
 * Data provider of {@link RMetamodel}s for model validator tests.
 */
abstract class DataProvider {
  private static final String pnISR  = "InformationSystem";
  private static final String pnISI  = "InformationSystemInterface";
  private static final String pnIF   = "InformationFlow";
  private static final String pnBO   = "BusinessObject";
  private static final String pnBP   = "BusinessProcess";
  private static final String pnBU   = "BusinessUnit";
  private static final String pnPROD = "Product";
  private static final String pnBM   = "BusinessMapping";

  public static RMetamodel getMetamodelWithBM() {
    WMetamodel metamodel = new WMetamodelImpl();

    WTypeGroup defaultTg = metamodel.createTypeGroup(AttributeTypeGroup.STANDARD_ATG_NAME);
    WClassExpression isr = metamodel.createClassExpression(pnISR, defaultTg);
    WClassExpression bp = metamodel.createClassExpression(pnBP, defaultTg);
    WClassExpression bu = metamodel.createClassExpression(pnBU, defaultTg);
    WClassExpression prod = metamodel.createClassExpression(pnPROD, defaultTg);

    WRelationshipTypeExpression bm = metamodel.createRelationshipType(pnBM, defaultTg);
    metamodel.createRelationship(bm, "informationSystemRelease", 0, RPropertyExpression.UNLIMITED, true, isr, "businessMappings", 1, 1, false);
    metamodel.createRelationship(bm, "businessProcess", 0, RPropertyExpression.UNLIMITED, true, bp, "businessMappings", 0, 1, false);
    metamodel.createRelationship(bm, "businessUnit", 0, RPropertyExpression.UNLIMITED, true, bu, "businessMappings", 0, 1, false);
    metamodel.createRelationship(bm, "product", 0, RPropertyExpression.UNLIMITED, true, prod, "businessMappings", 0, 1, false);

    return PojoRMetamodelCopyUtil.rMetamodelFor(metamodel);
  }

  public static RMetamodel getMetamodelForInterfaceInformationFlowConsistencyValidator() {
    WMetamodel metamodel = new WMetamodelImpl();

    WTypeGroup defaultTg = metamodel.createTypeGroup(AttributeTypeGroup.STANDARD_ATG_NAME);
    WClassExpression isi = metamodel.createClassExpression(pnISI, defaultTg);
    WClassExpression isr = metamodel.createClassExpression(pnISR, defaultTg);
    WClassExpression bo = metamodel.createClassExpression(pnBO, defaultTg);
    WRelationshipTypeExpression infoFlow = metamodel.createRelationshipType(pnIF, defaultTg);

    metamodel.createRelationship(infoFlow, "informationSystemInterface", 0, 1, true, isi, "informationFlows", 1, Integer.MAX_VALUE, false);
    metamodel.createRelationship(infoFlow, "informationSystemRelease1", 0, 1, true, isr, "informationFlows1", 1, Integer.MAX_VALUE, false);
    metamodel.createRelationship(infoFlow, "informationSystemRelease2", 0, 1, true, isr, "informationFlows2", 1, Integer.MAX_VALUE, false);
    metamodel.createRelationship(infoFlow, "businessObject", 0, 1, true, bo, "informationFlows", 1, Integer.MAX_VALUE, false);

    return PojoRMetamodelCopyUtil.rMetamodelFor(metamodel);
  }
}
