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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * Strategy for loading building blocks of structured types, which are connected with
 * the selected IS via one relationship.  
 */
public class RelationTypeConnect extends NameUriMapper implements ContextOverviewDataStrategy {

  private final String              relEndPoint1stHop;
  private final String              relEndPoint2ndHop;

  private Model                     model;
  private RStructuredTypeExpression insType;
  private List<NameUriPair>         pairs;
  private String                    serverURL;

  public RelationTypeConnect(String relEndPoint1stHop, String relEndPoint2ndHop, RMetamodel mm, Model m, String serverURL) {

    this.relEndPoint1stHop = relEndPoint1stHop;
    this.relEndPoint2ndHop = relEndPoint2ndHop;
    this.serverURL = serverURL;
    RMetamodel metamodel = mm;
    model = m;
    pairs = new ArrayList<NameUriPair>();
    insType = metamodel.findStructuredTypeByPersistentName("InformationSystem");

  }

  @Override
  public List<NameUriPair> getBuildingBlocks(int insId) {
    BigInteger mainId = BigInteger.valueOf(insId);
    ObjectExpression mainIns = model.findById(insType, mainId);
    RRelationshipEndExpression relEnd1stHop = insType.findRelationshipEndByPersistentName(relEndPoint1stHop);
    RStructuredTypeExpression relationshipType = relEnd1stHop.getType();
    Collection<ObjectExpression> relators = relEnd1stHop.apply(mainIns).getMany();
    RRelationshipEndExpression relEnd2ndHop = relationshipType.findRelationshipEndByPersistentName(relEndPoint2ndHop);
    RStructuredTypeExpression connectedType = relEnd2ndHop.getType();

    Set<ObjectExpression> connectedOEs = Sets.newHashSet();
    for (ObjectExpression relatorOE : relators) {
      connectedOEs.addAll(relEnd2ndHop.apply(relatorOE).getMany());
    }

    final RPropertyExpression connectedTypeNameProp = connectedType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);

    Function<ObjectExpression, String> nameExtract = new Function<ObjectExpression, String>() {
      @Override
      public String apply(ObjectExpression input) {
        return connectedTypeNameProp.apply(input).getOne().asString();
      }
    };

    List<ObjectExpression> sortedOes = Ordering.natural().onResultOf(nameExtract).sortedCopy(connectedOEs);
    for (int i = 0; i < sortedOes.size(); i++) {
      ObjectExpression oe = sortedOes.get(i);
      String name = oe.getValues(connectedTypeNameProp).getOne().asString();
      pairs.add(extractPair(name, oe, connectedType, serverURL));
    }

    return pairs;
  }


}
