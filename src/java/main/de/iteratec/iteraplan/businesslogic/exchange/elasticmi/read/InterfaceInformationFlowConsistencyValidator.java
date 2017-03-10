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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression.OriginalWType;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.elasticmi.validate.model.ModelValidator;


/**
 * {@link ModelValidator} to check that Interface's InformationFlows have the same two ISR connected (in the same order).
 */
/*package */class InterfaceInformationFlowConsistencyValidator extends SentientModelValidator implements ModelValidator {

  private static final Logger        LOGGER = Logger.getIteraplanLogger(InterfaceInformationFlowConsistencyValidator.class);

  private RStructuredTypeExpression  informationSystemInterface;
  private RStructuredTypeExpression  informationFlow;
  private RRelationshipEndExpression isiToIf;
  private RRelationshipEndExpression ifToIsr1;
  private RRelationshipEndExpression ifToIsr2;

  InterfaceInformationFlowConsistencyValidator(RMetamodel rMetamodel) {
    super(rMetamodel);
  }

  @Override
  public boolean validate(Model model, MessageListener messageListener) {
    if (isSentientMode()) {
      LOGGER.warn("Can't perform model validation! The metamodel is in invalid state for this validator.");
      return true;
    }

    boolean result = true;

    Collection<ObjectExpression> allISIs = informationSystemInterface.apply(model).getMany();
    for (ObjectExpression isi : allISIs) {

      ElasticValue<ObjectExpression> connectedInfoFlowEV = isiToIf.apply(isi);
      if (connectedInfoFlowEV.isMany()) {
        PeekingIterator<ObjectExpression> infoFlowsIterator = Iterators.peekingIterator(connectedInfoFlowEV.getMany().iterator());

        while (infoFlowsIterator.hasNext()) {
          ObjectExpression currentInfoFlow = infoFlowsIterator.next();

          ElasticValue<ObjectExpression> currentIsr1EV = ifToIsr1.apply(currentInfoFlow);
          ElasticValue<ObjectExpression> currentIsr2EV = ifToIsr2.apply(currentInfoFlow);
          // check if exactly two ISR are connected:
          if (!(currentIsr1EV.isOne() && currentIsr2EV.isOne())) {
            result = false;
            String nameOfInterface = getNameOrDefault(informationSystemInterface, isi, "NULL", ",");
            String nameOfInfoFlow = getNameOrDefault(informationFlow, currentInfoFlow, "NULL", ",");
            messageListener.onMessage(new InterfaceInformationFlowConsistencyViolationMessage(nameOfInterface, nameOfInfoFlow));
            continue;
          }

          if (!infoFlowsIterator.hasNext()) {
            break; // reached last pair
          }
          ObjectExpression nextInfoFlow = infoFlowsIterator.peek();
          ElasticValue<ObjectExpression> nextIsr1EV = ifToIsr1.apply(nextInfoFlow);
          ElasticValue<ObjectExpression> nextIsr2EV = ifToIsr2.apply(nextInfoFlow);
          if (!(nextIsr1EV.isOne() && nextIsr2EV.isOne())) {
            continue;
          }

          // check if connected ISR and/or their order are equal compared to the previous InformationFlow:
          ImmutableList<ObjectExpression> currentList = ImmutableList.of(currentIsr1EV.getOne(), currentIsr2EV.getOne());
          ImmutableList<ObjectExpression> nextList = ImmutableList.of(nextIsr1EV.getOne(), nextIsr2EV.getOne());

          if (!currentList.equals(nextList)) {
            result = false;
            String nameOfInterface = getNameOrDefault(informationSystemInterface, isi, "NULL", ", ");
            String nameOfInfoFlow1 = getNameOrDefault(informationFlow, currentInfoFlow, "NULL", ",");
            String nameOfInfoFlow2 = getNameOrDefault(informationFlow, nextInfoFlow, "NULL", ",");
            messageListener.onMessage(new InterfaceInformationFlowConsistencyViolationMessage(nameOfInterface, nameOfInfoFlow1, nameOfInfoFlow2));
          }
        }
      }
    }
    return result;
  }

  private static String getNameOrDefault(RStructuredTypeExpression type, ObjectExpression oe, String defaultValue, String separator) {
    if (type == null || oe == null) {
      return defaultValue;
    }

    if (type.getOriginalWType() == OriginalWType.RELATIONSHIP) {
      return type.getDomainIdentityValue(oe);
    }

    RPropertyExpression nameProp = type.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    if (nameProp == null) {
      return defaultValue;
    }

    ElasticValue<ValueExpression> nameEV = nameProp.apply(oe);
    if (nameEV.isNone()) {
      return defaultValue;
    }

    if (nameEV.isMany()) {
      Iterable<String> stringValues = Iterables.transform(nameEV.getMany(), new Function<ValueExpression, String>() {

        @Override
        public String apply(ValueExpression input) {
          return input.asString();
        }

      });
      Joiner.on(separator).join(stringValues);
    }

    return nameEV.getOne().asString();
  }

  /**{@inheritDoc}**/
  @Override
  protected boolean canValidate(RMetamodel rMetamodel) {
    RStructuredTypeExpression interfaceType = rMetamodel.findStructuredTypeByPersistentName("InformationSystemInterface");
    RStructuredTypeExpression infoFlowType = informationFlow = rMetamodel.findStructuredTypeByPersistentName("InformationFlow");
    if (interfaceType == null || infoFlowType == null) {
      LOGGER.warn("Can't build instance of this validator, because not all required types are available.");
      return false;
    }

    RRelationshipEndExpression toIf = interfaceType.findRelationshipEndByPersistentName("informationFlows");
    RRelationshipEndExpression toIsr1 = infoFlowType.findRelationshipEndByPersistentName("informationSystemRelease1");
    RRelationshipEndExpression toIsr2 = infoFlowType.findRelationshipEndByPersistentName("informationSystemRelease2");
    if (toIf == null || toIsr1 == null || toIsr2 == null) {
      LOGGER.warn("Can't build instance of this validator, because not all required relationship ends are available.");
      return false;
    }
    return true;
  }

  /**{@inheritDoc}**/
  @Override
  protected void initInternal(RMetamodel rMetamodel) {
    informationSystemInterface = rMetamodel.findStructuredTypeByPersistentName("InformationSystemInterface");
    informationFlow = rMetamodel.findStructuredTypeByPersistentName("InformationFlow");

    isiToIf = informationSystemInterface.findRelationshipEndByPersistentName("informationFlows");
    ifToIsr1 = informationFlow.findRelationshipEndByPersistentName("informationSystemRelease1");
    ifToIsr2 = informationFlow.findRelationshipEndByPersistentName("informationSystemRelease2");
  }
}