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

import java.util.Set;

import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;


/**
 * This class is used to instantiate a customized model
 */

public class TestModelBuilder {

  private RMetamodel metamodel;
  private Model          model;

  private static final String IS   = "InformationSystem";
  private static final String IE   = "Infrastructureelement";
  private static final String TC   = "TechnicalComponent";
  private static final String ISI  = "InformationSystemInterface";
  private static final String IF   = "InformationFlow";
  private static final String ISD  = "InformationSystemDomain";
  private static final String IBA  = "Isr2BoAssociation";
  private static final String BO   = "BusinessObject";
  private static final String BM   = "BusinessMapping";
  private static final String BP   = "BusinessProcess";
  private static final String BU   = "BusinessUnit";
  private static final String PROD = "Product";
  private static final String PROJ = "Project";
  private static final String BF   = "BusinessFunction";

  private ObjectExpression is1;
  private ObjectExpression    is2;
  private ObjectExpression    is3;
  private ObjectExpression    is4;
  private ObjectExpression ie1;
  private ObjectExpression tc1;
  private ObjectExpression isi1;
  private ObjectExpression if1;
  private ObjectExpression    if2;
  private ObjectExpression isd1;
  private ObjectExpression iba1;
  private ObjectExpression bo1;
  private ObjectExpression bm1;
  private ObjectExpression    bm2;
  private ObjectExpression bp1;
  private ObjectExpression bu1;
  private ObjectExpression    bu2;
  private ObjectExpression prod1;
  private ObjectExpression proj1;
  private ObjectExpression bf1;
  private ObjectExpression    bf2;
  private ObjectExpression    bf3;
  private ObjectExpression    bf4;
  private ObjectExpression    bf5;
  private ObjectExpression    bf6;
  private ObjectExpression    bf7;

  public TestModelBuilder(RMetamodel metamodel) {
    this.metamodel = metamodel;
    model = new ModelImpl();
  }

  public Model getModel() {
    buildTestBuildingBlocks();
    buildRelationships();
    return model;
  }

  public ObjectExpression getInstanceOf(String nameOfType) {
    RStructuredTypeExpression type = metamodel.findStructuredTypeByPersistentName(nameOfType);
    return type.create(model);
  }

  public ObjectExpression createWithName(String typeName, String instanceName) {
    ObjectExpression oe = this.getInstanceOf(typeName);
    oe.setValue(metamodel.findStructuredTypeByPersistentName(typeName).findPropertyByPersistentName("name"),
        ElasticValue.one(ValueExpression.create(instanceName)));
    return oe;
  }

  public void buildTestBuildingBlocks() {

    is1 = this.createWithName(IS, "CRM-System");
    is2 = this.createWithName(IS, "DWH");
    is3 = this.createWithName(IS, "BI-Frontend");
    is4 = this.createWithName(IS, "Oracle-DB");
    ie1 = this.createWithName(IE, "IBM Host 1");
    tc1 = this.createWithName(TC, "COBOL");
    isi1 = this.createWithName(ISI, "Depo Funds 1");
    if1 = this.createWithName(IF, "Flow 1");
    if2 = this.createWithName(IF, "Flow 2");
    isd1 = this.createWithName(ISD, "Core Apps Domain");
    iba1 = this.createWithName(IBA, "IB-Association 1");
    bo1 = this.createWithName(BO, "Accounting Entry");
    bm1 = this.createWithName(BM, "Mapping 1");
    bm2 = this.createWithName(BM, "Mapping 2");
    bp1 = this.createWithName(BP, "Process 1");
    bu1 = this.createWithName(BU, "Accounting Unit");
    bu2 = this.createWithName(BU, "Sales Unit");
    prod1 = this.createWithName(PROD, "Product 1");
    proj1 = this.createWithName(PROJ, "Project 1");
    bf1 = this.createWithName(BF, "Business Function 1");
    bf2 = this.createWithName(BF, "Business Function 2");
    bf3 = this.createWithName(BF, "Business Function 3");
    bf4 = this.createWithName(BF, "Business Function 4");
    bf5 = this.createWithName(BF, "Business Function 5");
    bf6 = this.createWithName(BF, "Business Function 6");
    bf7 = this.createWithName(BF, "Business Function 7");

  }

  public void relateWithEachOther(RRelationshipEndExpression end, Set<ObjectExpression> oes1, Set<ObjectExpression> oes2) {

    ElasticValue<ObjectExpression> ev1 = ElasticValue.many(oes1);
    ElasticValue<ObjectExpression> ev2 = ElasticValue.many(oes2);
    end.connect(model, ev1, ev2);

  }

  public void relateWithEachOther(RRelationshipEndExpression end, ObjectExpression oe1, Set<ObjectExpression> oes2) {

    ElasticValue<ObjectExpression> ev1 = ElasticValue.one(oe1);
    ElasticValue<ObjectExpression> ev2 = ElasticValue.many(oes2);
    end.connect(model, ev1, ev2);

  }

  public void relateWithEachOther(RRelationshipEndExpression end, Set<ObjectExpression> oes1, ObjectExpression oe2) {

    ElasticValue<ObjectExpression> ev1 = ElasticValue.many(oes1);
    ElasticValue<ObjectExpression> ev2 = ElasticValue.one(oe2);
    end.connect(model, ev1, ev2);

  }

  public void relateWithEachOther(RRelationshipEndExpression end, ObjectExpression oe1, ObjectExpression oe2) {

    ElasticValue<ObjectExpression> ev1 = ElasticValue.one(oe1);
    ElasticValue<ObjectExpression> ev2 = ElasticValue.one(oe2);
    end.connect(model, ev1, ev2);

  }

  public void buildRelationships() {
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("infrastructureElements"), is1, ie1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("baseComponents"), is1, is2);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("parentComponents"), is1, is3);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("informationFlows1"), is1, if2);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("informationFlows2"), is4, if2);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("informationSystemDomains"), is1,
        isd1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("technicalComponentReleases"), is1,
        tc1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("projects"), is1, proj1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("informationFlows1"), is1, if1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("informationFlows2"), is1, if1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(ISI).findRelationshipEndByPersistentName("informationFlows"), isi1, if1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf2);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf3);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf4);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf5);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf6);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessFunctions"), is1, bf7);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessObjectAssociations"), is1,
        iba1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IBA).findRelationshipEndByPersistentName("businessObject"), iba1, bo1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessMappings"), is1, bm1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(BP).findRelationshipEndByPersistentName("businessMappings"), bp1, bm1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(BU).findRelationshipEndByPersistentName("businessMappings"), bu1, bm1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(PROD).findRelationshipEndByPersistentName("businessMappings"), prod1, bm1);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(IS).findRelationshipEndByPersistentName("businessMappings"), is1, bm2);
    this.relateWithEachOther(metamodel.findStructuredTypeByPersistentName(BU).findRelationshipEndByPersistentName("businessMappings"), bu2, bm2);

  }

}

