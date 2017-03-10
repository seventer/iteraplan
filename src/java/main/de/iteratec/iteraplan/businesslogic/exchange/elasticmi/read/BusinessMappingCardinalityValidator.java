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

import java.util.Collection;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;


/**
 * {@link ModelValidator} to check if BusinessMappings have any (= at least one) BusinessUnit, BusinessProcess or Product connected.
 */
/*package */class BusinessMappingCardinalityValidator extends SentientModelValidator implements ModelValidator {

  private static final Logger        LOGGER = Logger.getIteraplanLogger(BusinessMappingCardinalityValidator.class);

  private RStructuredTypeExpression  businessMapping;

  private RRelationshipEndExpression bm2bp;
  private RRelationshipEndExpression bm2bu;
  private RRelationshipEndExpression bm2prod;

  BusinessMappingCardinalityValidator(RMetamodel rMetamodel) {
    super(rMetamodel);
  }

  @Override
  public boolean validate(Model model, MessageListener messageListener) {
    if (isSentientMode()) {
      LOGGER.warn("Can't perform model validation! The metamodel is in invalid state for this validator.");
      return true;
    }

    boolean result = true;
    Collection<ObjectExpression> allBusinessMappings = businessMapping.apply(model).getMany();
    for (ObjectExpression bm : allBusinessMappings) {
      ElasticValue<ObjectExpression> connectedBp = bm2bp.apply(bm);
      ElasticValue<ObjectExpression> connectedBu = bm2bu.apply(bm);
      ElasticValue<ObjectExpression> connectedProd = bm2prod.apply(bm);

      if (connectedBp.isNone() && connectedBu.isNone() && connectedProd.isNone()) {

        messageListener.onMessage(new BusinessMappingCardinalityViolationMessage(businessMapping.getDomainIdentityValue(bm)));
        result = false;
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean canValidate(RMetamodel rMetamodel) {
    RStructuredTypeExpression bmType = rMetamodel.findStructuredTypeByPersistentName("BusinessMapping");
    if (bmType == null) {
      return false;
    }

    RRelationshipEndExpression toBp = bmType.findRelationshipEndByPersistentName("businessProcess");
    RRelationshipEndExpression toBu = bmType.findRelationshipEndByPersistentName("businessUnit");
    RRelationshipEndExpression toProd = bmType.findRelationshipEndByPersistentName("product");
    RRelationshipEndExpression toIs = bmType.findRelationshipEndByPersistentName("informationSystemRelease");
    return Iterables.all(Lists.newArrayList(toBp, toBu, toIs, toProd), Predicates.notNull());
  }

  /**{@inheritDoc}**/
  @Override
  protected void initInternal(RMetamodel rMetamodel) {
    businessMapping = rMetamodel.findStructuredTypeByPersistentName("BusinessMapping");

    bm2bp = businessMapping.findRelationshipEndByPersistentName("businessProcess");
    bm2bu = businessMapping.findRelationshipEndByPersistentName("businessUnit");
    bm2prod = businessMapping.findRelationshipEndByPersistentName("product");
  }
}