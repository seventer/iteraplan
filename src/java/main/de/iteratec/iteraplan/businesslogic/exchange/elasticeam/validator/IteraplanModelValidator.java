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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidator;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;
import de.iteratec.iteraplan.model.BusinessMapping;


public class IteraplanModelValidator extends ModelValidator {

  private static final String           BM_ISR_REL_END_NAME  = "informationSystemRelease";
  private static final String           BM_BP_REL_END_NAME   = "businessProcess";
  private static final String           BM_BU_REL_END_NAME   = "businessUnit";
  private static final String           BM_PROD_REL_END_NAME = "product";

  private final UniversalTypeExpression businessMappingType;
  private final UniversalTypeExpression interfaceType;
  private final RelationshipEndExpression connectedInformationSystemRelease1;
  private final RelationshipEndExpression connectedInformationSystemRelease2;

  public IteraplanModelValidator(Metamodel metamodel) {
    super(metamodel);
    businessMappingType = metamodel.findUniversalTypeByPersistentName(BusinessMapping.class.getSimpleName());
    interfaceType = metamodel.findUniversalTypeByPersistentName("InformationSystemInterface");
    UniversalTypeExpression informationFlowType = metamodel.findUniversalTypeByPersistentName("InformationFlow");
    if(informationFlowType != null){
      connectedInformationSystemRelease1 = informationFlowType.findRelationshipEndByPersistentName("informationSystemRelease1");
      connectedInformationSystemRelease2 = informationFlowType.findRelationshipEndByPersistentName("informationSystemRelease2");
    }
    else{
      connectedInformationSystemRelease1 = null;
      connectedInformationSystemRelease2 = null;
    }
  }

  @Override
  public ModelValidatorResult validate(Model model) {
    ModelValidatorResult result = super.validate(model);

    checkBusinessMappingsUniqueness(model.findAll(businessMappingType), result);

    if (isCheckInterfaceConsistency()) {
      checkForInformationFlowInconsistencies(model.findAll(interfaceType), result);
    }

    return result;
  }

  /**
   * @param allInterfaces
   * @param result
   */
  private void checkForInformationFlowInconsistencies(Collection<UniversalModelExpression> allInterfaces, ModelValidatorResult result) {

    for(UniversalModelExpression interfaceModel : allInterfaces) {
      Collection<UniversalModelExpression> existingRelatedInfoFlows = interfaceModel.getConnecteds(interfaceType.findRelationshipEndByPersistentName("informationFlows"));
      if (!checkConnectedInformationSystems(existingRelatedInfoFlows)) {
        String interfaceName = (String) interfaceModel.getValue(MixinTypeNamed.NAME_PROPERTY);
        result.addViolation(new InterfaceInformationFlowConsistencyViolation(interfaceName));
      }
    }

  }

  /**
   * @param existingRelatedInfoFlows
   * @return true, if there are no inconsistencies
   */
  private boolean checkConnectedInformationSystems(Collection<UniversalModelExpression> existingRelatedInfoFlows) {
    if (existingRelatedInfoFlows == null || existingRelatedInfoFlows.size() <= 1) {
      return true;
    }
    UniversalModelExpression savedInformationSystemRelease1 = null;
    UniversalModelExpression savedInformationSystemRelease2 = null;
    UniversalModelExpression currentInformationSystemRelease1 = null;
    UniversalModelExpression currentInformationSystemRelease2 = null;
    boolean firstTime = true;
    for (UniversalModelExpression existingInfoFlow : existingRelatedInfoFlows) {
      if (firstTime) {
        savedInformationSystemRelease1 = existingInfoFlow.getConnected(connectedInformationSystemRelease1);
        savedInformationSystemRelease2 = existingInfoFlow.getConnected(connectedInformationSystemRelease2);
        firstTime = false;
        continue;
      }
      currentInformationSystemRelease1 = existingInfoFlow.getConnected(connectedInformationSystemRelease1);
      currentInformationSystemRelease2 = existingInfoFlow.getConnected(connectedInformationSystemRelease2);
      if (!savedInformationSystemRelease1.equals(currentInformationSystemRelease1)
          || !savedInformationSystemRelease2.equals(currentInformationSystemRelease2)) {
        return false;
      }
    }
    return true;
  }

  private void checkBusinessMappingsUniqueness(Collection<UniversalModelExpression> bmExpressions, ModelValidatorResult result) {
    Map<BusinessMappingKey, Set<UniversalModelExpression>> bmMap = Maps.newHashMap();
    for (UniversalModelExpression bmExpression : bmExpressions) {
      BusinessMappingKey key = new BusinessMappingKey(bmExpression, businessMappingType);
      if (!bmMap.containsKey(key)) {
        bmMap.put(key, new HashSet<UniversalModelExpression>());
      }
      bmMap.get(key).add(bmExpression);
    }

    for (Map.Entry<BusinessMappingKey, Set<UniversalModelExpression>> duplicates : bmMap.entrySet()) {
      Set<UniversalModelExpression> sameBMs = duplicates.getValue();
      if (sameBMs.size() > 1) {
        BusinessMappingKey bmKey = duplicates.getKey();
        result.addViolation(new DuplicateBusinessMappingsViolation(sameBMs, bmKey.getConcatenatedNames(), bmKey.isPartial()));
      }
    }
  }

  private static class BusinessMappingKey {
    private final String isrName;
    private final String bpName;
    private final String buName;
    private final String prodName;
    private boolean      partial;

    public BusinessMappingKey(UniversalModelExpression bmExpression, UniversalTypeExpression bmType) {
      isrName = getConnectedName(bmExpression, bmType, BM_ISR_REL_END_NAME);
      bpName = getConnectedName(bmExpression, bmType, BM_BP_REL_END_NAME);
      buName = getConnectedName(bmExpression, bmType, BM_BU_REL_END_NAME);
      prodName = getConnectedName(bmExpression, bmType, BM_PROD_REL_END_NAME);
    }

    private String getConnectedName(UniversalModelExpression bmExpression, UniversalTypeExpression bmType, String relEndName) {
      RelationshipEndExpression relEnd = bmType.findRelationshipEndByPersistentName(relEndName);
      if (relEnd != null) {
        UniversalModelExpression connected = bmExpression.getConnected(relEnd);
        if (connected != null) {
          return (String) connected.getValue(MixinTypeNamed.NAME_PROPERTY);
        }
        else {
          return null;
        }
      }
      else {
        partial = true;
        return "~~unknown~~";
      }
    }

    public String getIsrName() {
      return isrName;
    }

    public String getBpName() {
      return bpName;
    }

    public String getBuName() {
      return buName;
    }

    public String getProdName() {
      return prodName;
    }

    public String getConcatenatedNames() {
      List<String> names = Lists.newArrayList("IS: " + isrName, "BP: " + bpName, "BU: " + buName, "Prod: " + prodName);
      return "(" + Joiner.on(" / ").join(names) + ")";
    }

    public boolean isPartial() {
      return partial;
    }

    @Override
    public int hashCode() {
      HashCodeBuilder builder = new HashCodeBuilder();
      builder.append(this.isrName).append(this.bpName).append(this.buName).append(this.prodName);
      return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      else if (obj.getClass().equals(this.getClass())) {
        BusinessMappingKey other = (BusinessMappingKey) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this.isrName, other.getIsrName());
        builder.append(this.bpName, other.getBpName());
        builder.append(this.buName, other.getBuName());
        builder.append(this.prodName, other.getProdName());
        return builder.isEquals();
      }
      else {
        return false;
      }
    }
  }

}
