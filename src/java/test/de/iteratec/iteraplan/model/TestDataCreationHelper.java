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
package de.iteratec.iteraplan.model;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


public final class TestDataCreationHelper {

  /** empty private constructor because class only provides static methods */
  private TestDataCreationHelper() {
    // hide constructor
  }

  /**
   * Creates and returns a {@link RuntimePeriod} object for testing purposes
   * @return {@link RuntimePeriod}
   * @throws ParseException
   */
  public static RuntimePeriod createTestRuntimePeriod() throws ParseException {
    String datePattern = "dd.MM.yyyy";
    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
    format.applyPattern(datePattern);
    Date start = format.parse("06.03.2005");
    Date end = format.parse("30.10.2015");
    RuntimePeriod rtp = new RuntimePeriod(start, end);

    return rtp;
  }

  /**
   * Creates and returns a {@link BuildingBlockType} object width a {@link TextAT} for testing purposes
   * @return {@link BuildingBlockType}
   */
  public static BuildingBlockType createTestBuildingBlockType() {
    HashSet<AttributeType> at = new HashSet<AttributeType>();
    TextAT tat = new TextAT();
    tat.setId(Integer.valueOf(1));
    at.add(tat);

    BuildingBlockType bbt = new BuildingBlockType();
    bbt.setId(Integer.valueOf(1));
    bbt.setAttributeTypes(at);

    return bbt;
  }

  /**
   * Creates and returns {@link AttributeValueAssignment}s for testing purposes
   * @return HashSet of {@link AttributeValueAssignment}s
   */
  public static Set<AttributeValueAssignment> createTestAttributeValueAssignments() {
    HashSet<AttributeValueAssignment> avas = new HashSet<AttributeValueAssignment>();

    AttributeValueAssignment ava1 = new AttributeValueAssignment();
    ava1.setId(Integer.valueOf(1));
    ava1.setOlVersion(Integer.valueOf(1));
    avas.add(ava1);
    AttributeValueAssignment ava2 = new AttributeValueAssignment();
    ava2.setId(Integer.valueOf(2));
    ava2.setOlVersion(Integer.valueOf(3));
    avas.add(ava2);

    return avas;
  }

  /**
   * Creates and returns several {@link UserEntity} objects for testing purposes
   * @return HashSet of {@link UserEntity} objects
   */
  public static Set<UserEntity> createTestUserEntities() {
    HashSet<UserEntity> ues = new HashSet<UserEntity>();
    UserGroup firstGroup = new UserGroup();
    firstGroup.setId(Integer.valueOf(1));

    UserGroup secGroup = new UserGroup();
    secGroup.setId(null);

    HashSet<UserEntity> dev = new HashSet<UserEntity>();

    User user1 = new User();
    user1.setId(Integer.valueOf(1));
    dev.add(user1);

    User user2 = new User();
    user2.setId(Integer.valueOf(2));
    dev.add(user2);

    firstGroup.setMembers(dev);

    ues.add(firstGroup);
    ues.add(secGroup);

    return ues;
  }

  /**
   * Creates and returns a {@link BusinessObject} object for testing purposes
   * @param id
   *          id of the {@link BusinessObject}
   * @param name
   *          name of the {@link BusinessObject}
   * @param desc
   *          the {@link BusinessObject}'s description
   * @return {@link BusinessObject}
   */
  public static BusinessObject createTestBusinessObject(int id, String name, String desc) {
    BusinessObject firstPr = new BusinessObject();
    firstPr.setId(Integer.valueOf(id));
    firstPr.setName(name);
    firstPr.setDescription(desc);
    return firstPr;
  }

  /**
   * Creates and returns a Set of {@link Transport}s between {@link BusinessObject}s for testing purposes
   * @param bos
   *          List of {@link BusinessObject}s
   * @return Set of {@link Transport}s
   */
  public static Set<Transport> createTestTransportsForBusinessObjects(List<BusinessObject> bos) {
    // Transport
    Set<Transport> ts = hashSet();
    // -
    Transport first = new Transport();
    first.setId(Integer.valueOf(85));
    first.setBusinessObject(bos.get(3));
    first.setDirection(Direction.NO_DIRECTION);

    // ->
    Transport sec = new Transport();
    sec.setId(Integer.valueOf(91));
    sec.setBusinessObject(bos.get(0));
    sec.setDirection(Direction.FIRST_TO_SECOND);

    // <-
    Transport third = new Transport();
    third.setId(Integer.valueOf(80));
    third.setBusinessObject(bos.get(1));
    third.setDirection(Direction.SECOND_TO_FIRST);
    // <->
    Transport fourth = new Transport();
    fourth.setId(Integer.valueOf(81));
    fourth.setBusinessObject(bos.get(2));
    fourth.setDirection(Direction.BOTH_DIRECTIONS);

    ts.add(first);
    ts.add(sec);
    ts.add(third);
    ts.add(fourth);

    return ts;
  }

  /**
   * Creates and returns several {@link BusinessObject}s for testing purposes
   * @return List of {@link BusinessObject}s
   */
  public static List<BusinessObject> createTestBusinessObjects() {
    // BusinessObject
    List<BusinessObject> bos = new ArrayList<BusinessObject>();
    bos.add(createTestBusinessObject(45, "first Product", "test first Product"));
    bos.add(createTestBusinessObject(50, "second Product", "test sec Product"));
    bos.add(createTestBusinessObject(55, "third Product", "test third Product"));
    bos.add(createTestBusinessObject(65, "fourth Product", "test fourth Product"));
    return bos;
  }

}
