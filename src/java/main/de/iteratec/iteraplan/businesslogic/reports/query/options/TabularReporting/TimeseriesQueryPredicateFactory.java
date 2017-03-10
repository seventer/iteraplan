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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.math.BigDecimal;
import java.util.Locale;

import com.google.common.base.Predicate;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.attribute.TypeOfAttribute;


/**
 *
 */
public final class TimeseriesQueryPredicateFactory {
  private static final Predicate<String> NO_VALUES_PREDICATE = new Predicate<String>() {
                                                               public boolean apply(String input) {
                                                                 return input == null;
                                                               }
                                                             };
  private static final Predicate<String> ANY_VALUE_PREDICATE = new Predicate<String>() {
                                                               public boolean apply(String input) {
                                                                 return input != null;
                                                               }
                                                             };

  private TimeseriesQueryPredicateFactory() {
    //static utility, do not instantiate
  }

  public static Predicate<String> createPredicate(TypeOfAttribute toa, final Integer operatorId, final String value) {
    if (Constants.OPERATION_NOENTRIES_ID.equals(operatorId)) {
      return NO_VALUES_PREDICATE;
    }
    else if (Constants.OPERATION_ANYENTRIES_ID.equals(operatorId)) {
      return ANY_VALUE_PREDICATE;
    }
    else {
      switch (toa) {
        case ENUM:
          return createEnumPredicate(operatorId, value);
        case NUMBER:
          return createNumberPredicate(operatorId, BigDecimalConverter.parse(value, UserContext.getCurrentLocale()));
        default:
          throw new IllegalArgumentException("could not create predicate on attribute type " + toa);
      }
    }
  }

  private static Predicate<String> createNumberPredicate(final Integer operatorId, final BigDecimal value) {
    if (operatorId.equals(Constants.OPERATION_GT_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && BigDecimalConverter.parse(input, Locale.ENGLISH).compareTo(value) > 0;
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_GEQ_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && BigDecimalConverter.parse(input, Locale.ENGLISH).compareTo(value) >= 0;
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_EQ_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && BigDecimalConverter.parse(input, Locale.ENGLISH).compareTo(value) == 0;
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_LEQ_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && BigDecimalConverter.parse(input, Locale.ENGLISH).compareTo(value) <= 0;
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_LT_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && BigDecimalConverter.parse(input, Locale.ENGLISH).compareTo(value) < 0;
        }
      };
    }

    throw new IllegalArgumentException("unknown operation id");
  }

  private static Predicate<String> createEnumPredicate(final Integer operatorId, final String value) {
    if (operatorId.equals(Constants.OPERATION_CONTAINS_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && input.contains(value);
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_CONTAINSNOT_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input == null || !input.contains(value);
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_STARTSWITH_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && input.startsWith(value);
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_ENDSWITH_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input != null && input.endsWith(value);
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_EQUALS_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return value.equals(input);
        }
      };
    }
    if (operatorId.equals(Constants.OPERATION_EQUALSNOT_ID)) {
      return new Predicate<String>() {
        public boolean apply(String input) {
          return input == null || !input.equals(value);
        }
      };
    }

    throw new IllegalArgumentException("unknown operation id");
  }
}
