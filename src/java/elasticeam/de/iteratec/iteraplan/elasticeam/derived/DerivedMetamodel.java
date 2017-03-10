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
package de.iteratec.iteraplan.elasticeam.derived;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


@SuppressWarnings("PMD.TooManyMethods")
final class DerivedMetamodel implements Metamodel {

  private Metamodel                                             wrapped;

  private Map<UniversalTypeExpression, UniversalTypeExpression> replacements;

  private List<SubstantialTypeExpression>                       aSubstantialTypes;
  private AddReplaceList<SubstantialTypeExpression>             substantialTypes  = new AddReplaceList<SubstantialTypeExpression>(getSTReplacer()) {

                                                                                    @Override
                                                                                    protected List<SubstantialTypeExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getSubstantialTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected SubstantialTypeExpression rawFindByPersistentname(String persistentName) {
                                                                                      return null; //TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected SubstantialTypeExpression rawFindByName(String name) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected List<? extends SubstantialTypeExpression> getAdditionalElements() {
                                                                                      return DerivedMetamodel.this.aSubstantialTypes;
                                                                                    }
                                                                                  };

  private List<RelationshipTypeExpression>                      aRelationshipTypes;
  private AddReplaceList<RelationshipTypeExpression>            relationshipTypes = new AddReplaceList<RelationshipTypeExpression>(getRTReplacer()) {
                                                                                    @Override
                                                                                    protected List<RelationshipTypeExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getRelationshipTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected RelationshipTypeExpression rawFindByPersistentname(String persistentName) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected RelationshipTypeExpression rawFindByName(String name) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected List<? extends RelationshipTypeExpression> getAdditionalElements() {
                                                                                      return DerivedMetamodel.this.aRelationshipTypes;
                                                                                    }
                                                                                  };
  private AddReplaceList<UniversalTypeExpression>               universalTypes    = new AddReplaceList<UniversalTypeExpression>(getUTReplacer()) {
                                                                                    @Override
                                                                                    protected List<UniversalTypeExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getUniversalTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected UniversalTypeExpression rawFindByPersistentname(String persistentName) {
                                                                                      return DerivedMetamodel.this.wrapped
                                                                                          .findUniversalTypeByPersistentName(persistentName);
                                                                                    }

                                                                                    @Override
                                                                                    protected UniversalTypeExpression rawFindByName(String name) {
                                                                                      return DerivedMetamodel.this.wrapped
                                                                                          .findUniversalTypeByName(name);
                                                                                    }

                                                                                    @SuppressWarnings("unchecked")
                                                                                    @Override
                                                                                    protected List<? extends UniversalTypeExpression> getAdditionalElements() {
                                                                                      return ListUtils.sum(DerivedMetamodel.this.aSubstantialTypes,
                                                                                          DerivedMetamodel.this.aRelationshipTypes);
                                                                                    }
                                                                                  };
  private List<EnumerationExpression>                           aEnumerations;
  private AddReplaceList<EnumerationExpression>                 enumerations      = new AddReplaceList<EnumerationExpression>() {

                                                                                    @Override
                                                                                    protected List<EnumerationExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getEnumerationTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected EnumerationExpression rawFindByPersistentname(String persistentName) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected EnumerationExpression rawFindByName(String name) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected List<? extends EnumerationExpression> getAdditionalElements() {
                                                                                      return DerivedMetamodel.this.aEnumerations;
                                                                                    }
                                                                                  };
  private AddReplaceList<DataTypeExpression>                    dataTypes         = new AddReplaceList<DataTypeExpression>() {
                                                                                    @Override
                                                                                    protected List<DataTypeExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getDataTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected DataTypeExpression rawFindByPersistentname(String persistentName) {
                                                                                      return DerivedMetamodel.this.wrapped
                                                                                          .findDataTypeByPersistentName(persistentName);
                                                                                    }

                                                                                    @Override
                                                                                    protected DataTypeExpression rawFindByName(String name) {
                                                                                      return DerivedMetamodel.this.wrapped.findDataTypeByName(name);
                                                                                    }

                                                                                    @Override
                                                                                    protected List<? extends DataTypeExpression> getAdditionalElements() {
                                                                                      return DerivedMetamodel.this.aEnumerations;
                                                                                    }
                                                                                  };
  private AddReplaceList<TypeExpression>                        types             = new AddReplaceList<TypeExpression>() {

                                                                                    @Override
                                                                                    protected List<TypeExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getTypes();
                                                                                    }

                                                                                    @Override
                                                                                    protected TypeExpression rawFindByPersistentname(String persistentName) {
                                                                                      return DerivedMetamodel.this.wrapped
                                                                                          .findTypeByPersistentName(persistentName);
                                                                                    }

                                                                                    @Override
                                                                                    protected TypeExpression rawFindByName(String name) {
                                                                                      return DerivedMetamodel.this.wrapped.findTypeByName(name);
                                                                                    }

                                                                                    @SuppressWarnings("unchecked")
                                                                                    @Override
                                                                                    protected List<? extends TypeExpression> getAdditionalElements() {
                                                                                      return ListUtils.sum(DerivedMetamodel.this.universalTypes
                                                                                          .getAdditionalElements(),
                                                                                          DerivedMetamodel.this.aEnumerations);
                                                                                    }
                                                                                  };
  private List<RelationshipExpression>                          aRelationships;
  private AddReplaceList<RelationshipExpression>                relationships     = new AddReplaceList<RelationshipExpression>(getREReplacer()) {

                                                                                    @Override
                                                                                    protected List<RelationshipExpression> rawGet() {
                                                                                      return DerivedMetamodel.this.wrapped.getRelationships();
                                                                                    }

                                                                                    @Override
                                                                                    protected RelationshipExpression rawFindByPersistentname(String persistentName) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected RelationshipExpression rawFindByName(String name) {
                                                                                      return null; // TODO
                                                                                    }

                                                                                    @Override
                                                                                    protected List<? extends RelationshipExpression> getAdditionalElements() {
                                                                                      return DerivedMetamodel.this.aRelationships;
                                                                                    }
                                                                                  };

  DerivedMetamodel(Metamodel wrapped) {
    this.wrapped = wrapped;
    this.aSubstantialTypes = Lists.newLinkedList();
    this.aRelationshipTypes = Lists.newLinkedList();
    this.aEnumerations = Lists.newLinkedList();
    this.aRelationships = Lists.newLinkedList();
    this.replacements = Maps.newHashMap();
  }

  void add(SubstantialTypeExpression type) {
    this.aSubstantialTypes.add(new DecoratedSubstantialType(type, getUTReplacer()));
  }

  void add(RelationshipTypeExpression type) {
    this.aRelationshipTypes.add(new DecoratedRelationshipType(type, getUTReplacer()));
  }

  void add(RelationshipExpression type) {
    this.aRelationships.add(new DecoratedRelationship(type, getUTReplacer()));
  }

  void add(EnumerationExpression type) {
    this.aEnumerations.add(type);
  }

  void add(SubstantialTypeExpression type, PropertyExpression<?> property) {
    demandGetDecorated(type).addFeature(property);
  }

  void add(SubstantialTypeExpression type, RelationshipEndExpression relationshipEnd) {
    demandGetDecorated(type).addFeature(relationshipEnd);
  }

  private DecoratedSubstantialType demandGetDecorated(SubstantialTypeExpression type) {
    if (!this.replacements.containsKey(type)) {
      this.replacements.put(type, new DecoratedSubstantialType(type, getUTReplacer()));
    }
    return (DecoratedSubstantialType) this.replacements.get(type);
  }

  private Function<RelationshipExpression, RelationshipExpression> getREReplacer() {
    return new Function<RelationshipExpression, RelationshipExpression>() {
      public RelationshipExpression apply(RelationshipExpression input) {
        return new DecoratedRelationship(input, getUTReplacer());
      }
    };
  }

  private Function<SubstantialTypeExpression, SubstantialTypeExpression> getSTReplacer() {
    return new Function<SubstantialTypeExpression, SubstantialTypeExpression>() {
      public SubstantialTypeExpression apply(SubstantialTypeExpression input) {
        if (DerivedMetamodel.this.replacements.containsKey(input)) {
          return (SubstantialTypeExpression) DerivedMetamodel.this.replacements.get(input);
        }
        else {
          return input;
        }
      }
    };
  }

  private Function<RelationshipTypeExpression, RelationshipTypeExpression> getRTReplacer() {
    return new Function<RelationshipTypeExpression, RelationshipTypeExpression>() {
      public RelationshipTypeExpression apply(RelationshipTypeExpression input) {
        if (DerivedMetamodel.this.replacements.containsKey(input)) {
          return (RelationshipTypeExpression) DerivedMetamodel.this.replacements.get(input);
        }
        else {
          return input;
        }
      }
    };
  }

  private Function<UniversalTypeExpression, UniversalTypeExpression> getUTReplacer() {
    return new Function<UniversalTypeExpression, UniversalTypeExpression>() {
      /**{@inheritDoc}**/
      public UniversalTypeExpression apply(UniversalTypeExpression input) {
        if (DerivedMetamodel.this.replacements.containsKey(input)) {
          return DerivedMetamodel.this.replacements.get(input);
        }
        else {
          return input;
        }
      }
    };
  }

  /**{@inheritDoc}**/
  public String getName() {
    return this.wrapped.getName();
  }

  /**{@inheritDoc}**/
  public List<TypeExpression> getTypes() {
    return this.types.get();
  }

  /**{@inheritDoc}**/
  public List<SubstantialTypeExpression> getSubstantialTypes() {
    return this.substantialTypes.get();
  }

  /**{@inheritDoc}**/
  public List<RelationshipTypeExpression> getRelationshipTypes() {
    return this.relationshipTypes.get();
  }

  /**{@inheritDoc}**/
  public List<EnumerationExpression> getEnumerationTypes() {
    return this.enumerations.get();
  }

  /**{@inheritDoc}**/
  public List<PrimitiveTypeExpression> getPrimitiveTypes() {
    return this.wrapped.getPrimitiveTypes();
  }

  /**{@inheritDoc}**/
  public List<DataTypeExpression> getDataTypes() {
    return this.dataTypes.get();
  }

  /**{@inheritDoc}**/
  public List<UniversalTypeExpression> getUniversalTypes() {
    return this.universalTypes.get();
  }

  /**{@inheritDoc}**/
  public List<RelationshipExpression> getRelationships() {
    return this.relationships.get();
  }

  /**{@inheritDoc}**/
  public TypeExpression findTypeByName(String name) {
    return this.types.findByName(name);
  }

  /**{@inheritDoc}**/
  public TypeExpression findTypeByPersistentName(String persistentName) {
    return this.types.findByPersistentName(persistentName);
  }

  /**{@inheritDoc}**/
  public DataTypeExpression findDataTypeByName(String name) {
    return this.dataTypes.findByName(name);
  }

  /**{@inheritDoc}**/
  public DataTypeExpression findDataTypeByPersistentName(String persistentName) {
    return this.dataTypes.findByPersistentName(persistentName);
  }

  /**{@inheritDoc}**/
  public UniversalTypeExpression findUniversalTypeByName(String name) {
    return this.universalTypes.findByName(name);
  }

  /**{@inheritDoc}**/
  public UniversalTypeExpression findUniversalTypeByPersistentName(String persistentName) {
    return this.universalTypes.findByPersistentName(persistentName);
  }

  /**{@inheritDoc}**/
  public List<TypeExpression> getTypes(ElasticeamContext ctx) {
    return getTypes();
  }

  /**{@inheritDoc}**/
  public List<SubstantialTypeExpression> getSubstantialTypes(ElasticeamContext ctx) {
    return getSubstantialTypes();
  }

  /**{@inheritDoc}**/
  public List<RelationshipTypeExpression> getRelationshipTypes(ElasticeamContext ctx) {
    return getRelationshipTypes();
  }

  /**{@inheritDoc}**/
  public List<EnumerationExpression> getEnumerationTypes(ElasticeamContext ctx) {
    return getEnumerationTypes();
  }

  /**{@inheritDoc}**/
  public List<PrimitiveTypeExpression> getPrimitiveTypes(ElasticeamContext ctx) {
    return getPrimitiveTypes();
  }

  /**{@inheritDoc}**/
  public List<DataTypeExpression> getDataTypes(ElasticeamContext ctx) {
    return getDataTypes();
  }

  /**{@inheritDoc}**/
  public List<UniversalTypeExpression> getUniversalTypes(ElasticeamContext ctx) {
    return getUniversalTypes();
  }

  /**{@inheritDoc}**/
  public TypeExpression findTypeByName(ElasticeamContext ctx, String name) {
    return findTypeByName(name);
  }

  /**{@inheritDoc}**/
  public List<RelationshipExpression> getRelationships(ElasticeamContext ctx) {
    return getRelationships();
  }
}