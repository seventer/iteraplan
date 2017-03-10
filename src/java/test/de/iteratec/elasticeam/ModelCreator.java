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
package de.iteratec.elasticeam;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.iteratec.iteraplan.elasticeam.emfimpl.EMFPrimitiveProperty;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;


public final class ModelCreator {

  public static BigDecimal[] costValues = new BigDecimal[] { BigDecimal.valueOf(2.5), BigDecimal.valueOf(3.5), BigDecimal.valueOf(4.5),
      BigDecimal.valueOf(5.5), BigDecimal.valueOf(6.5), BigDecimal.valueOf(7.5), BigDecimal.valueOf(8.5), BigDecimal.valueOf(9.5),
      BigDecimal.valueOf(10.5), BigDecimal.valueOf(11.5) };

  public static String[]     startDates = new String[] { "2012-02-01", "2012-02-02", "2012-02-03", "2012-02-04", "2012-02-05", "2012-02-06",
      "2012-02-07", "2012-02-08", "2012-02-09", "2012-02-10" };

  private ModelCreator() {
    //Nothing to do
  }

  public static Model createModel(Metamodel metamodel) {
    Model model = ModelFactory.INSTANCE.createModel(metamodel);

    for (String stName : MetamodelCreator.getSubstantialTypeNames()) {
      for (int i = 1; i <= 10; i++) {
        SubstantialTypeExpression type = (SubstantialTypeExpression) metamodel.findTypeByPersistentName(stName);
        InstanceExpression expr = model.create(type);
        model.setValue(expr, MixinTypeNamed.NAME_PROPERTY, stName.toLowerCase() + i);
        model.setValue(expr, MixinTypeNamed.DESCRIPTION_PROPERTY, stName.toLowerCase() + i + "descr");
        model.setValue(expr, UniversalTypeExpression.ID_PROPERTY, BigInteger.valueOf(IdGenerator.getNextId().longValue()));

        // set the costs property
        PropertyExpression<?> costs = type.findPropertyByName("costs");
        model.setValue(expr, costs, costValues[i - 1]);

        // set responsibility
        PropertyExpression<?> resp = type.findPropertyByName("responsibility");
        EnumerationExpression enumE = (EnumerationExpression) metamodel.findTypeByName("responsibilityE");
        List<EnumerationLiteralExpression> literals = new ArrayList<EnumerationLiteralExpression>();
        literals.add(enumE.getLiterals().get(i % 3));
        literals.add(enumE.getLiterals().get((i + 1) % 3));
        model.setValue(expr, resp, literals);

        // set is-specific attributes
        if ("IS".equals(stName)) {
          // version
          EMFPrimitiveProperty version = (EMFPrimitiveProperty) type.findPropertyByName("version");
          model.setValue(expr, version, BigInteger.valueOf(i));

          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

          // start date
          PropertyExpression<?> startDate = type.findPropertyByName("startDate");
          try {
            model.setValue(expr, startDate, format.parse(startDates[i - 1]));
          } catch (ParseException e) {
            throw new ModelException(ModelException.GENERAL_ERROR, "Failed to create dates");
          }

          // health
          PropertyExpression<?> health = type.findPropertyByName("health");
          EnumerationExpression enum2E = (EnumerationExpression) metamodel.findTypeByName("healthE");
          EnumerationLiteralExpression literal = enum2E.getLiterals().get(i % 3);
          model.setValue(expr, health, literal);
        }
      }
    }

    createHierarchies(metamodel, model);
    createBMs(metamodel, model);

    return model;
  }

  private static void createHierarchies(Metamodel metamodel, Model model) {
    // create hierarchies (same structure for the instances all three
    // substantial types)
    for (SubstantialTypeExpression expr : metamodel.getSubstantialTypes()) {
      InstanceExpression inst1 = model.findByName(expr, expr.getName().toLowerCase() + 1);
      InstanceExpression inst2 = model.findByName(expr, expr.getName().toLowerCase() + 2);
      InstanceExpression inst3 = model.findByName(expr, expr.getName().toLowerCase() + 3);
      InstanceExpression inst4 = model.findByName(expr, expr.getName().toLowerCase() + 4);
      InstanceExpression inst5 = model.findByName(expr, expr.getName().toLowerCase() + 5);
      InstanceExpression inst6 = model.findByName(expr, expr.getName().toLowerCase() + 6);
      InstanceExpression inst7 = model.findByName(expr, expr.getName().toLowerCase() + 7);
      InstanceExpression inst8 = model.findByName(expr, expr.getName().toLowerCase() + 8);
      InstanceExpression inst9 = model.findByName(expr, expr.getName().toLowerCase() + 9);

      // inst1 is parent of inst2 and inst3
      model.link(inst1, expr.findRelationshipEndByName("children"), inst2);
      model.link(inst1, expr.findRelationshipEndByName("children"), inst3);

      // inst2 is parent of inst4 and inst5
      model.link(inst2, expr.findRelationshipEndByName("children"), inst4);
      model.link(inst2, expr.findRelationshipEndByName("children"), inst5);

      // inst3 is parent of inst6
      model.link(inst3, expr.findRelationshipEndByName("children"), inst6);

      // inst7 is parent of inst8
      model.link(inst7, expr.findRelationshipEndByName("children"), inst8);

      // inst8 is parent of inst9
      model.link(inst8, expr.findRelationshipEndByName("children"), inst9);
    }
  }

  private static void createBMs(Metamodel metamodel, Model model) {

    // add bm instances
    // prepare types
    SubstantialTypeExpression isType = (SubstantialTypeExpression) metamodel.findTypeByName("IS");
    SubstantialTypeExpression bpType = (SubstantialTypeExpression) metamodel.findTypeByName("BP");
    SubstantialTypeExpression buType = (SubstantialTypeExpression) metamodel.findTypeByName("BU");

    // prepare instances
    InstanceExpression is1 = model.findByName(isType, "is1");
    InstanceExpression is2 = model.findByName(isType, "is2");
    InstanceExpression is3 = model.findByName(isType, "is3");

    InstanceExpression bp1 = model.findByName(bpType, "bp1");
    InstanceExpression bp2 = model.findByName(bpType, "bp2");

    InstanceExpression bu1 = model.findByName(buType, "bu1");
    InstanceExpression bu2 = model.findByName(buType, "bu2");
    InstanceExpression bu3 = model.findByName(buType, "bu3");
    InstanceExpression bu4 = model.findByName(buType, "bu4");

    // (is1, bp1, bu1)
    createBM(metamodel, model, is1, isType, bp1, bpType, bu1, buType, "bm1");

    // (is1, bp1, bu2)
    createBM(metamodel, model, is1, isType, bp1, bpType, bu2, buType, "bm2");

    // (is1, bp2, bu2)
    createBM(metamodel, model, is1, isType, bp2, bpType, bu2, buType, "bm3");

    // (is2, bp1, bu3)
    createBM(metamodel, model, is2, isType, bp1, bpType, bu3, buType, "bm4");

    // (is3, bp2, bu4)
    createBM(metamodel, model, is3, isType, bp2, bpType, bu4, buType, "bm5");
  }

  private static void createBM(Metamodel metamodel, Model model, InstanceExpression is, SubstantialTypeExpression isType, InstanceExpression bp,
                               SubstantialTypeExpression bpType, InstanceExpression bu, SubstantialTypeExpression buType, String bmName) {
    RelationshipTypeExpression bmType = (RelationshipTypeExpression) metamodel.findTypeByName("BM");
    LinkExpression bm = model.create(bmType);
    model.link(is, isType.findRelationshipEndByName("business mapping"), bm);
    model.link(bp, bpType.findRelationshipEndByName("business mapping"), bm);
    model.link(bu, buType.findRelationshipEndByName("business mapping"), bm);

    // set id and version properties
    model.setValue(bm, UniversalTypeExpression.ID_PROPERTY, BigInteger.valueOf(IdGenerator.getNextId().longValue()));
    PropertyExpression<?> name = bmType.findPropertyByName("name");
    model.setValue(bm, name, bmName);
    PropertyExpression<?> resp = bmType.findPropertyByName("responsibility");
    model.setValue(bm, resp, ((EnumerationPropertyExpression) resp).getType().getLiterals());
    PropertyExpression<?> version = bmType.findPropertyByName("version");
    model.setValue(bm, version, BigInteger.valueOf(IdGenerator.getNextId().longValue()));
  }
}
