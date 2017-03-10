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
package de.iteratec.iteraplan.presentation.rest.representation;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.restlet.resource.ResourceException;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticeam.iteraql2.IteraQl2Exception;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.CompiledQuery;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.RelationshipEndCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.UniversalTypeCompilationUnit;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.QueryResult;
import de.iteratec.iteraplan.elasticeam.iteraql2.result.UniversalTypeResult;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.operator.expand.ExpandedSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.filter.FilteredUniversalType;
import de.iteratec.iteraplan.elasticeam.operator.nullify.NullifiedUniversalType;
import de.iteratec.iteraplan.elasticeam.operator.objectify.ObjectifyingSubstantialType;
import de.iteratec.iteraplan.elasticeam.operator.power.PowerSubstantialType;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestApplication;
import de.iteratec.iteraplan.presentation.rest.resource.data.ADataResource;


/**
 * Common super type for representation handlers of the {@link ADataResource}.
 */
public abstract class ADataHandler implements RepresentationHandler {

  protected ElasticeamService  elasticeamService;

  /**
   * standard IteraQLQueryCompiler delegating to the IteraQL2Compiler.
   */
  private IteraQLQueryCompiler queryCompiler = new IteraQLQueryCompiler() {

                                               public CompiledQuery compile(Metamodel metamodel, String query) {
                                                 return IteraQl2Compiler.compile(metamodel, query);
                                               }
                                             };

  /**
   * interface allowing interchangable IteraQL query compilers to be used. This is important for unit testing, where the functionality of the IteraQL2Compiler is not subject to testing and thus setting up an elastic service shall be avoided. 
   */
  public interface IteraQLQueryCompiler {
    CompiledQuery compile(Metamodel metamodel, String query);
  }

  public void setElasticeamService(ElasticeamService elasticeamService) {
    this.elasticeamService = elasticeamService;
  }

  /**
   * @param arguments
   * @return
   *   A map queryString -> queryResult. If an iteraQl query was provided
   *   the map will contain only the one query. If the content was specified to be the entire model, it will contain all Substantial Types as key with the according objects as values. Otherwise, it will return an empty map.
   */
  protected Map<NamedExpression, QueryResult> evaluateQuery(Map<String, Object> arguments) {

    Map<NamedExpression, QueryResult> results = Maps.newHashMap();

    //TODO Double-check with Konstantin if the used method (iterate over all Substantial Types, generate an IteraQL query from its persistent name and merge all the results to a JSon object is correct, sufficient and stable regarding future changes. Also check about the difference between UniversalTypes and SubstantialTypes (e.g. this would list Interfaces)
    if (IteraplanRestApplication.CONTENT_ENTIRE_MODEL.equals(arguments.get(IteraplanRestApplication.KEY_RESPONSE_CONTENT))) {
      Metamodel metamodel = elasticeamService.getMetamodel();
      List<UniversalTypeExpression> types = metamodel.getUniversalTypes();

      for (UniversalTypeExpression type : types) {
        String queryString = type.getPersistentName();
        queryString += ";";
        CompiledQuery query = queryCompiler.compile(metamodel, queryString);

        if (!canDisplayQuery(query)) {
          throw new IteraQl2Exception(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, IteraplanErrorMessages.getErrorMessage(
              IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, new Object[] { "Synthetic types are not accessible via REST" }, Locale.ENGLISH));
        }

        results.putAll(doEvaluate(query));
      }
    }
    else {
      String queryString = (String) arguments.get(IteraplanRestApplication.KEY_RESPONSE_CONTENT);
      if (queryString != null) {
        Metamodel metamodel = elasticeamService.getMetamodel();
        CompiledQuery query = queryCompiler.compile(metamodel, queryString);

        if (!canDisplayQuery(query)) {
          throw new IteraQl2Exception(IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, IteraplanErrorMessages.getErrorMessage(
              IteraplanErrorMessages.SYNTAX_ERROR_IN_QUERY, new Object[] { "Synthetic types are not accessible via REST" }, Locale.ENGLISH));
        }

        Map<NamedExpression, QueryResult> evaluated = doEvaluate(query);
        if (Boolean.TRUE.equals(arguments.get(IteraplanRestApplication.KEY_SINGLETON)) && query instanceof UniversalTypeCompilationUnit
            && ((UniversalTypeResult) evaluated.get(evaluated.keySet().iterator().next())).getResults().size() != 1) {
          throw new ResourceException(404);
        }
        results.putAll(evaluated);

      }
    }
    return results;
  }

  private boolean canDisplayQuery(CompiledQuery query) {
    if (query instanceof RelationshipEndCompilationUnit) {
      RelationshipEndExpression resultType = ((RelationshipEndCompilationUnit) query).getCompilationResult();

      if (isSyntheticType(resultType.getType()) || isSyntheticType(resultType.getHolder())) {
        return false;
      }
    }
    else if (query instanceof UniversalTypeCompilationUnit) {
      UniversalTypeExpression resultType = ((UniversalTypeCompilationUnit) query).getCompilationResult();
      if (isSyntheticType(resultType)) {
        return false;
      }
    }
    return true;
  }

  private boolean isSyntheticType(UniversalTypeExpression type) {
    if (type instanceof NullifiedUniversalType) {
      return true;
    }
    if (type instanceof ObjectifyingSubstantialType) {
      return true;
    }
    if (type instanceof PowerSubstantialType) {
      return true;
    }
    if (type instanceof FilteredUniversalType) {
      return isSyntheticType(((FilteredUniversalType<?>) type).getBaseType());
    }
    if (type instanceof ExpandedSubstantialType) {
      return isSyntheticType(((ExpandedSubstantialType) type).getBaseType());
    }
    return false;
  }

  /**
   * @param query
   * @return a Map containing the query's type as key and the result as value.
   */
  private Map<NamedExpression, QueryResult> doEvaluate(CompiledQuery query) {
    Map<NamedExpression, QueryResult> results = Maps.newHashMap();

    if (UniversalTypeCompilationUnit.class.isInstance(query)) {
      results.put(((UniversalTypeCompilationUnit) query).getCompilationResult(), elasticeamService.executeQuery(query));
    }
    else {
      results.put(((RelationshipEndCompilationUnit) query).getCompilationResult(), elasticeamService.executeQuery(query));
    }
    return results;
  }

  protected void setQueryCompiler(IteraQLQueryCompiler queryCompiler) {
    this.queryCompiler = queryCompiler;
  }
}
