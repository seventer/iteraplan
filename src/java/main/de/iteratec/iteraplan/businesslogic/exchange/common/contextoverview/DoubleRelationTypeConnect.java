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
 * the selected IS via more than one relationship.  
 */
public class DoubleRelationTypeConnect extends NameUriMapper implements ContextOverviewDataStrategy {

  private String                    relEndPoint2ndHopA;
  private String                    relEndPoint2ndHopB;
  private String                    relatedConTypePoint;

  private RMetamodel                metamodel;
  private Model                     model;
  private RStructuredTypeExpression insType;
  private List<NameUriPair>         pairs;
  private String                    serverURL;

  public DoubleRelationTypeConnect(String relEndPoint2ndHopA, String relatedConTypePoint, RMetamodel mm, Model m, String serverURL) {
    this.relEndPoint2ndHopA = relEndPoint2ndHopA;
    this.relEndPoint2ndHopB = null;
    this.relatedConTypePoint = relatedConTypePoint;
    this.serverURL = serverURL;
    metamodel = mm;
    model = m;
    pairs = new ArrayList<NameUriPair>();
    insType = metamodel.findStructuredTypeByPersistentName("InformationSystem");

  }

  public DoubleRelationTypeConnect(String relEndPoint2ndHopA, String relEndPoint2ndHopB, String relatedConTypePoint, RMetamodel mm, Model m,
      String serverURL) {
    this.relEndPoint2ndHopA = relEndPoint2ndHopA;
    this.relEndPoint2ndHopB = relEndPoint2ndHopB;
    this.relatedConTypePoint = relatedConTypePoint;
    this.serverURL = serverURL;
    metamodel = mm;
    model = m;
    pairs = new ArrayList<NameUriPair>();
    insType = metamodel.findStructuredTypeByPersistentName("InformationSystem");

  }

  @Override
  public List<NameUriPair> getBuildingBlocks(int insId) {
    BigInteger mainId = BigInteger.valueOf(insId);
    ObjectExpression mainIns = model.findById(insType, mainId);
    RRelationshipEndExpression endExpression1 = insType.findRelationshipEndByPersistentName("informationFlows1");
    RRelationshipEndExpression endExpression2 = insType.findRelationshipEndByPersistentName("informationFlows2");
    RStructuredTypeExpression relationshipType = metamodel.findStructuredTypeByPersistentName("InformationFlow");
    Collection<ObjectExpression> relators = endExpression1.apply(mainIns).getMany();
    relators.addAll(endExpression2.apply(mainIns).getMany());
    RStructuredTypeExpression connectedType = metamodel.findStructuredTypeByPersistentName(relatedConTypePoint);
    RRelationshipEndExpression relEnd2ndHopA = relationshipType.findRelationshipEndByPersistentName(relEndPoint2ndHopA);
    RRelationshipEndExpression relEnd2ndHopB = null;

    Set<ObjectExpression> connectedOEs = Sets.newHashSet();
    for (ObjectExpression relatorOE : relators) {
      connectedOEs.addAll(relEnd2ndHopA.apply(relatorOE).getMany());
      if (null != relEndPoint2ndHopB) {
        relEnd2ndHopB = relationshipType.findRelationshipEndByPersistentName(relEndPoint2ndHopB);
        connectedOEs.addAll(relEnd2ndHopB.apply(relatorOE).getMany());

      }
    }
    connectedOEs.remove(mainIns);

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
