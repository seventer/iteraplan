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
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;


/**
 * Strategy for loading building blocks of direct with the selected IS connected structured types  
 */

public class DirectConnect extends NameUriMapper implements ContextOverviewDataStrategy {

  private String                    relEndPoint;
  private Model                     model;
  private RStructuredTypeExpression insType;
  private List<NameUriPair>         pairs;
  private String                    serverURL;

  public DirectConnect(String relEndPoint, RMetamodel mm, Model m, String serverURL) {
    this.relEndPoint = relEndPoint;
    RMetamodel metamodel = mm;
    model = m;
    insType = metamodel.findStructuredTypeByPersistentName("InformationSystem");
    pairs = new ArrayList<NameUriPair>();
    this.serverURL = serverURL;
  }

  @Override
  public List<NameUriPair> getBuildingBlocks(int insId) {
    BigInteger mainId = BigInteger.valueOf(insId);
    ObjectExpression mainIns = model.findById(insType, mainId);
    RRelationshipEndExpression endExpression = insType.findRelationshipEndByPersistentName(relEndPoint);
    RStructuredTypeExpression connectedType = endExpression.getType();
    Set<ObjectExpression> connectedOEs = (Set<ObjectExpression>) mainIns.getConnecteds(endExpression).getMany();

    final RPropertyExpression connectedTypeNameProp = connectedType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);

    Function<ObjectExpression, String> nameExtract = new Function<ObjectExpression, String>() {
      @Override
      public String apply(ObjectExpression input) {
        return connectedTypeNameProp.apply(input).getOne().asString();
      }
    };

    List<ObjectExpression> sortedOes = Ordering.natural().onResultOf(nameExtract).sortedCopy(connectedOEs);
    for(int i=0; i < sortedOes.size(); i++){
      ObjectExpression oe = sortedOes.get(i);
      String name = oe.getValues(connectedTypeNameProp).getOne().asString();
      pairs.add(extractPair(name, oe, connectedType, serverURL));
    }

    return pairs;

  }


}
