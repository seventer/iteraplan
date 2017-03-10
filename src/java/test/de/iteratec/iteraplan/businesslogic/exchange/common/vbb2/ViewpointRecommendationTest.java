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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.FlatVariableRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.PrimaryRecommendation;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.Priority;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.ViewpointRecommendation.StructuredVariableRecommendation;
import de.iteratec.iteraplan.presentation.ajax.VbbConfigurationService.Tag;


public class ViewpointRecommendationTest {

  @Test
  public void testViewpointRecommendation() {
    //setup
    ViewpointRecommendation rec = ViewpointRecommendation.create();
    FlatVariableRecommendation relationVar = rec.addFlat("relation");
    relationVar.append("rel1", "rel1loc", Priority.HIGH);
    relationVar.append("rel2", "rel2loc", Priority.HIGH);
    relationVar.append("rel3", "rel3loc", Priority.HIGH);
    relationVar.append("rel4", "rel4loc", Priority.LOW);

    StructuredVariableRecommendation type1 = rec.addStructured("type1");
    PrimaryRecommendation primary1 = type1.append("t1.1", "t1.1loc", Priority.HIGH);
    primary1.append("t1.1obj1", "t1.1obj1loc");
    primary1.append("t1.1obj2", "t1.1obj2loc");
    type1.append("t1.2", "t1.2loc", Priority.LOW);

    StructuredVariableRecommendation type2 = rec.addStructured("type2");
    type2.append("t2.1", "t2.1loc", Priority.HIGH);
    type2.append("t2.2", "t2.2loc", Priority.HIGH);

    //object under test
    Map<String, Collection<Tag>> vpRecMap = rec.toRecommendationMap();

    //assertions
    checkAssertions(vpRecMap);
  }

  private void checkAssertions(Map<String, Collection<Tag>> vpRecMap) {
    assertNotNull(vpRecMap);

    //relation
    Collection<Tag> relationRecs = vpRecMap.get("relation");
    assertNotNull(relationRecs);
    assertEquals(4, relationRecs.size());

    for (Tag relTag : relationRecs) {
      assertEquals(0, relTag.getChildren().size());
    }

    Tag rel1Tag = retrieveTag("rel1", relationRecs);
    assertNotNull(rel1Tag);
    assertEquals("rel1loc", rel1Tag.getName());
    assertEquals(0, rel1Tag.getPrio());

    Tag rel2Tag = retrieveTag("rel2", relationRecs);
    assertNotNull(rel2Tag);
    assertEquals("rel2loc", rel2Tag.getName());
    assertEquals(0, rel2Tag.getPrio());

    Tag rel3tag = retrieveTag("rel3", relationRecs);
    assertNotNull(rel3tag);
    assertEquals("rel3loc", rel3tag.getName());
    assertEquals(0, rel3tag.getPrio());

    Tag rel4tag = retrieveTag("rel4", relationRecs);
    assertNotNull(rel4tag);
    assertEquals("rel4loc", rel4tag.getName());
    assertEquals(-1, rel4tag.getPrio());

    //type1
    Collection<Tag> type1rec = vpRecMap.get("type1");
    assertNotNull(type1rec);
    assertEquals(2, type1rec.size());

    Tag type11tag = retrieveTag("t1.1", type1rec);
    assertNotNull(type11tag);
    assertEquals("t1.1loc", type11tag.getName());
    assertEquals(0, type11tag.getPrio());
    assertEquals(2, type11tag.getChildren().size());

    Tag type11c1tag = retrieveTag("t1.1obj1", type11tag.getChildren());
    assertNotNull(type11c1tag);
    assertEquals("t1.1obj1loc", type11c1tag.getName());
    assertEquals(0, type11c1tag.getChildren().size());
    assertEquals(-1, type11c1tag.getPrio());

    Tag type11c2tag = retrieveTag("t1.1obj2", type11tag.getChildren());
    assertNotNull(type11c2tag);
    assertEquals("t1.1obj2loc", type11c2tag.getName());
    assertEquals(0, type11c2tag.getChildren().size());
    assertEquals(-1, type11c2tag.getPrio());

    Tag type12tag = retrieveTag("t1.2", type1rec);
    assertNotNull(type12tag);
    assertEquals("t1.2loc", type12tag.getName());
    assertEquals(-1, type12tag.getPrio());
    assertEquals(0, type12tag.getChildren().size());

    //type2
    Collection<Tag> type2rec = vpRecMap.get("type2");
    assertNotNull(type2rec);
    assertEquals(2, type2rec.size());

    Tag type21tag = retrieveTag("t2.1", type2rec);
    assertNotNull(type21tag);
    assertEquals("t2.1loc", type21tag.getName());
    assertEquals(0, type21tag.getPrio());
    assertEquals(0, type21tag.getChildren().size());

    Tag type22tag = retrieveTag("t2.2", type2rec);
    assertNotNull(type22tag);
    assertEquals("t2.2loc", type22tag.getName());
    assertEquals(0, type22tag.getPrio());
    assertEquals(0, type22tag.getChildren().size());
  }

  private Tag retrieveTag(String id, Collection<Tag> fromCollection) {
    for (Tag tag : fromCollection) {
      if (id.equals(tag.getId())) {
        return tag;
      }
    }
    return null;
  }

}
