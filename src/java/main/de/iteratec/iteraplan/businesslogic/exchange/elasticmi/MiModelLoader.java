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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.MetamodelExport;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticmi.exception.ElasticMiException;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.common.base.DateInterval;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WEnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WSortalTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.user.UserEntity;


public class MiModelLoader {

  private static final Logger LOGGER = Logger.getIteraplanLogger(MiModelLoader.class);

  /**
   * Loads the model from hibernate using the specified {@link RMetamodel} and {@link ElasticMiIteraplanMapping}. Afterwards, the Model is valid in terms of defining features. Lower bounds of features remain invalid if they are invalid within the hibernate model.  
   * 
   * @param metamodel the metamodel 
   * @param mapping the mapping, loaded by an {@link IteraplanMiLoadTask}
   * @param factory hibernate session factory
   * @return the loaded {@link Model}
   */
  public BiMap<Object, ObjectExpression> load(RMetamodel metamodel, ElasticMiIteraplanMapping mapping, Model inModel, SessionFactory factory) {
    return new LoadRun(metamodel, mapping, inModel, factory).load();
  }

  private static final class LoadRun {
    private static final int                                    ORACLE_MAX_LIST_SIZE = 995;

    private final RMetamodel                                    metamodel;
    private final ElasticMiIteraplanMapping                     mapping;
    private final Session                                       session;
    private BiMap<Object, ObjectExpression>                     instances;
    private BiMap<InformationSystemInterface, ObjectExpression> interfaceInstances   = HashBiMap.create();
    private Set<Integer>                                        iSIsWithTransport    = Sets.newHashSet(Integer.valueOf(-1));
    private final Model                                         model;

    public LoadRun(RMetamodel metamodel, ElasticMiIteraplanMapping mapping, Model inModel, SessionFactory factory) {
      this.mapping = mapping;
      this.metamodel = metamodel;
      session = factory.getCurrentSession();
      model = inModel;
      this.instances = HashBiMap.create();
    }

    public BiMap<Object, ObjectExpression> load() {
      LOGGER.info("Loading model.");
      long start = -System.currentTimeMillis();
      createObjectExpressions();
      createRelators();
      createInfoFlowsForInterfacesWithoutTransports();
      linkObjectExpressions();
      fixInterfaceNames();
      fixInformationFlowConnections();
      LOGGER.info("Model loaded in {0} ms.", (start + System.currentTimeMillis()));
      return instances;
    }

    private void createObjectExpressions() {
      for (Entry<WSortalTypeExpression, HbMappedClass> universalType2mappedClass : this.mapping.getSubstantialTypes().entrySet()) {
        LOGGER.info("Creating object expressions for type \"{0}\"", universalType2mappedClass.getKey().getPersistentName());
        HbMappedClass hbClass = universalType2mappedClass.getValue();
        WUniversalTypeExpression wute = universalType2mappedClass.getKey();
        RStructuredTypeExpression rste = metamodel.findStructuredTypeByPersistentName(wute.getPersistentName());
        if (hbClass.getMappedClass() != null) {
          if (hbClass.hasReleaseClass()) {
            hbClass = hbClass.getReleaseClass();
          }
          Criteria cQuery = session.createCriteria(hbClass.getMappedClass());
          List<?> dbInstances = cQuery.list();
          for (Object hibernateObject : dbInstances) {
            if (needsToBeProcessed(hibernateObject)) {
              LOGGER.debug("Loading \"{0}\"", hibernateObject);
              WPropertyExpression idProp = wute.findPropertyByPersistentName("id");
              ObjectExpression modelInstance;
              try {
                BigInteger id = BigInteger.valueOf(((Integer) mapping.resolveBuiltInProperty(wute, idProp).invoke(hibernateObject)).longValue());
                modelInstance = this.model.create(rste, id);
                this.instances.put(hibernateObject, modelInstance);
                loadProperties(hibernateObject, modelInstance, wute, hbClass);
              } catch (IllegalArgumentException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              } catch (IllegalAccessException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              } catch (InvocationTargetException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              }
            }
          }
        }
      }
    }

    private void createRelators() {
      for (Entry<WRelationshipTypeExpression, HbMappedClass> universalType2mappedClass : this.mapping.getRelationshipTypes().entrySet()) {
        LOGGER.info("Creating relators for type \"{0}\"", universalType2mappedClass.getKey().getPersistentName());
        HbMappedClass hbClass = universalType2mappedClass.getValue();
        WUniversalTypeExpression wute = universalType2mappedClass.getKey();
        RStructuredTypeExpression rste = metamodel.findStructuredTypeByPersistentName(wute.getPersistentName());
        if (hbClass.getMappedClass() != null) {
          boolean isTransport = Transport.class.equals(hbClass.getMappedClass());
          if (hbClass.hasReleaseClass()) {
            hbClass = hbClass.getReleaseClass();
          }
          Criteria cQuery = session.createCriteria(hbClass.getMappedClass());
          List<?> dbInstances = cQuery.list();
          for (Object hibernateObject : dbInstances) {
            if (needsToBeProcessed(hibernateObject)) {
              LOGGER.debug("Loading \"{0}\"", hibernateObject);
              if (isTransport) {
                Transport transport = (Transport) hibernateObject;
                if (transport.getInformationSystemInterface() != null) {
                  iSIsWithTransport.add(transport.getInformationSystemInterface().getId());
                }
              }
              WPropertyExpression idProp = wute.findPropertyByPersistentName("id");
              ObjectExpression modelInstance;
              try {
                BigInteger id = BigInteger.valueOf(((Integer) mapping.resolveBuiltInProperty(wute, idProp).invoke(hibernateObject)).longValue());
                modelInstance = this.model.create(rste, id);
                this.instances.put(hibernateObject, modelInstance);
                loadProperties(hibernateObject, modelInstance, wute, hbClass);
                for (RRelationshipEndExpression rree : rste.getAllRelationshipEnds()) {
                  loadConnections(hibernateObject, modelInstance, rree, hbClass);
                }
              } catch (IllegalArgumentException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              } catch (IllegalAccessException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              } catch (InvocationTargetException e) {
                throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load object expressions from database.");
              }
            }
          }
        }
      }
    }

    private void linkObjectExpressions() {
      LOGGER.info("Creating links");
      Collection<Relationship> processed = Sets.newHashSet();
      for (Entry<WSortalTypeExpression, HbMappedClass> universalType2mappedClass : this.mapping.getSubstantialTypes().entrySet()) {
        HbMappedClass hbClass = universalType2mappedClass.getValue();
        WUniversalTypeExpression wute = universalType2mappedClass.getKey();
        RStructuredTypeExpression rste = metamodel.findStructuredTypeByPersistentName(wute.getPersistentName());
        if (hbClass.getMappedClass() != null) {
          if (hbClass.hasReleaseClass()) {
            hbClass = hbClass.getReleaseClass();
          }
          Collection<ObjectExpression> instancesOfType = rste.apply(model).getMany();
          for (RRelationshipEndExpression rree : rste.getAllRelationshipEnds()) {
            Relationship rel = new Relationship(rree, rree.getOpposite());
            if (!processed.contains(rel)) {
              processed.add(rel);
              for (ObjectExpression oe : instancesOfType) {
                Object hibernateObject = instances.inverse().get(oe);
                if (hibernateObject != null) {
                  loadConnections(hibernateObject, oe, rree, hbClass);
                }
              }
            }
          }
        }
      }
    }

    private void fixInterfaceNames() {
      LOGGER.info("Fixing interface names.");
      WUniversalTypeExpression wType = mapping.getSortalTypeExpression(InformationSystemInterface.class.getSimpleName());
      RStructuredTypeExpression interfaceType = metamodel.findStructuredTypeByPersistentName(wType.getPersistentName());
      for (ObjectExpression interfaceExpression : interfaceType.apply(model).getMany()) {
        InformationSystemInterface informationSystemInterface = (InformationSystemInterface) instances.inverse().get(interfaceExpression);

        String newName = informationSystemInterface.getName();
        InformationSystemRelease releaseA = informationSystemInterface.getInformationSystemReleaseA();
        newName += "[";
        if (releaseA != null && releaseA.getName() != null) {
          newName += releaseA.getName();
        }
        newName += ",";
        InformationSystemRelease releaseB = informationSystemInterface.getInformationSystemReleaseB();
        if (releaseB != null && releaseB.getName() != null) {
          newName += releaseB.getName();
        }

        // to be able to match anonymous interfaces in the excel sheet the name is additionally
        // extended with the id of the interface
        newName += ",";
        newName += informationSystemInterface.getId();

        newName += "]";

        interfaceType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).set(ElasticValue.one(interfaceExpression),
            ElasticValue.one(ValueExpression.create(newName)));
      }
    }

    private void createInfoFlowsForInterfacesWithoutTransports() {
      LOGGER.info("Creating information flows for interfaces without transports.");
      WUniversalTypeExpression wType = this.mapping.getRelationshipTypeExpression(WMetamodelExport.INFORMATION_FLOW);
      RStructuredTypeExpression infoFlowType = metamodel.findStructuredTypeByPersistentName(wType.getPersistentName());
      RRelationshipEndExpression infoFlow2Is1 = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
      RRelationshipEndExpression infoFlow2Is2 = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
      RRelationshipEndExpression infoFlow2Isi = infoFlowType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI);

      //TODO select isi.* from isi where isi.id_bb not in (select transports.id_isi from transports); remove isiswithtransport field
      //      Criteria cQuery = session.createCriteria(InformationSystemInterface.class);

      Criterion crit = null;

      // workaround the restriction on list elements for queries against oracle database
      if (iSIsWithTransport.size() <= ORACLE_MAX_LIST_SIZE) {
        crit = Restrictions.in("id", iSIsWithTransport);
      }
      else {
        Iterator<Set<Integer>> it = splitByThousand(iSIsWithTransport).iterator();
        crit = Restrictions.or(Restrictions.in("id", it.next()), Restrictions.in("id", it.next())); // initialize first iteration over isIsWithTransport
        while (it.hasNext()) {
          crit = Restrictions.or(crit, Restrictions.in("id", it.next())); // add another or statement for each subset remaining in the subsets collection
        }
      }

      Criteria cQuery = session.createCriteria(InformationSystemInterface.class).add(Restrictions.not(crit));
      for (Object object : cQuery.list()) {
        InformationSystemInterface interfaceObject = (InformationSystemInterface) object;
        ObjectExpression interfaceInstance = this.instances.get(interfaceObject);
        ObjectExpression infoFlow = model.create(infoFlowType, BigInteger.valueOf(-1L * interfaceObject.getId().longValue()));
        ElasticValue<ObjectExpression> infoflowElasticValue = ElasticValue.one(infoFlow);
        infoFlow2Isi.connect(model, infoflowElasticValue, ElasticValue.one(interfaceInstance));

        InformationSystemRelease releaseA = interfaceObject.getInformationSystemReleaseA();
        InformationSystemRelease releaseB = interfaceObject.getInformationSystemReleaseB();
        if (releaseA != null) {
          infoFlow2Is1.connect(model, infoflowElasticValue, ElasticValue.one(instances.get(releaseA)));
        }
        if (releaseB != null) {
          infoFlow2Is2.connect(model, infoflowElasticValue, ElasticValue.one(instances.get(releaseB)));
        }
        interfaceInstances.put(interfaceObject, infoFlow);
      }
    }

    private void fixInformationFlowConnections() {
      LOGGER.info("Fixing information flow connections.");
      RStructuredTypeExpression informationFlowType = metamodel.findStructuredTypeByPersistentName(WMetamodelExport.INFORMATION_FLOW);
      RRelationshipEndExpression informationFlow2InformationSystem1 = informationFlowType
          .findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
      RRelationshipEndExpression informationFlow2InformationSystem2 = informationFlowType
          .findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
      RPropertyExpression isiID = informationFlowType.findPropertyByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI_ID);
      RPropertyExpression direction = informationFlowType.findPropertyByPersistentName("direction");
      RNominalEnumerationExpression directionEnum = (RNominalEnumerationExpression) direction.getType();

      for (ObjectExpression infoFlowInstance : informationFlowType.apply(model).getMany()) {
        InformationSystemInterface informationSystemInterface = null;

        Direction directionValue = null;
        if (interfaceInstances.containsValue(infoFlowInstance)) {
          informationSystemInterface = interfaceInstances.inverse().get(infoFlowInstance);
          directionValue = informationSystemInterface.getInterfaceDirection();
        }
        else if (instances.containsValue(infoFlowInstance)) {
          Transport transport = (Transport) instances.inverse().get(infoFlowInstance);
          informationSystemInterface = transport.getInformationSystemInterface();
          directionValue = transport.getDirection();
        }
        ElasticValue<ObjectExpression> infoFlowElasticValue = ElasticValue.one(infoFlowInstance);
        if (directionValue != null) {
          REnumerationLiteralExpression directionLiteral = directionEnum.findLiteralByPersistentName(((Enum<?>) directionValue).name());
          direction.set(infoFlowElasticValue, ElasticValue.one(ValueExpression.create(directionLiteral)));
        }

        if (informationSystemInterface != null) {
          isiID.set(infoFlowElasticValue,
              ElasticValue.one(ValueExpression.create(BigInteger.valueOf(informationSystemInterface.getId().longValue()))));
          ObjectExpression informationSystem1 = instances.get(informationSystemInterface.getInformationSystemReleaseA());
          ObjectExpression informationSystem2 = instances.get(informationSystemInterface.getInformationSystemReleaseB());

          if (informationSystem1 != null) {
            informationFlow2InformationSystem1.connect(model, infoFlowElasticValue, ElasticValue.one(informationSystem1));
          }
          if (informationSystem2 != null) {
            informationFlow2InformationSystem2.connect(model, infoFlowElasticValue, ElasticValue.one(informationSystem2));
          }
        }
      }
    }

    private void loadConnections(Object hibernateObject, ObjectExpression modelInstance, RRelationshipEndExpression rree, HbMappedClass hbClass) {
      LOGGER.debug("Loading connections for rel end \"{0}\" in OE \"{1}\"", rree.getPersistentName(), hibernateObject);
      HbMappedProperty hbRelation = hbClass.getProperty(rree.getPersistentName());
      Object correctHibernateObject = hibernateObject;
      if (hbRelation == null && hbClass.isReleaseClass() && hbClass.getReleaseBase().getProperty(rree.getPersistentName()) != null) {
        hbRelation = hbClass.getReleaseBase().getProperty(rree.getPersistentName());
        try {
          correctHibernateObject = hbClass.getReleaseBaseProperty().getGetMethod().invoke(correctHibernateObject);
        } catch (IllegalArgumentException e) {
          throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
        } catch (IllegalAccessException e) {
          throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
        } catch (InvocationTargetException e) {
          throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
        }
      }
      if (hbRelation != null && !hbRelation.isPrimitive() && !hbRelation.isEnum()) {
        Method getter = hbRelation.getGetMethod();
        if (getter != null) {
          try {
            Object value = getter.invoke(correctHibernateObject);
            Collection<ObjectExpression> values = Lists.newArrayList();
            if (value instanceof Collection) {
              for (Object singleValue : (Collection<?>) value) {
                if (instances.containsKey(singleValue)) {
                  values.add(instances.get(singleValue));
                }
              }
              rree.connect(model, ElasticValue.one(modelInstance), ElasticValue.many(values));
            }
            else {
              rree.connect(model, ElasticValue.one(modelInstance), ElasticValue.one(instances.get(value)));
            }
          } catch (IllegalArgumentException e) {
            throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
          } catch (IllegalAccessException e) {
            throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
          } catch (InvocationTargetException e) {
            throw new ElasticMiException(ElasticMiException.UNKNOWN_ERROR, "Could not load relations from database.");
          }
        }
      }
    }

    private void loadProperties(Object hibernateObject, ObjectExpression oe, WUniversalTypeExpression ute, HbMappedClass hbClass) {
      RStructuredTypeExpression ste = metamodel.findStructuredTypeByPersistentName(ute.getPersistentName());
      for (WPropertyExpression wprop : ute.getAllProperties()) {
        if (!ElasticMiConstants.PERSISTENT_NAME_ID.equals(wprop.getPersistentName())) {
          RPropertyExpression rprop = ste.findPropertyByPersistentName(wprop.getPersistentName());
          ElasticValue<ValueExpression> value = getValue(ute, hibernateObject, wprop, hbClass);
          if (ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_USER.equals(wprop.getPersistentName())) {
            if (value.isOne()) {
              oe.setLastModificationUser(value.getOne().asString());
            }
          }
          else if (ElasticMiConstants.PERSISTENT_NAME_LAST_MODIFICATION_TIME.equals(wprop.getPersistentName())) {
            if (value.isOne()) {
              oe.setLastModificationTime(value.getOne().asDate());
            }
          }
          else {
            oe.setValue(rprop, value);
          }
        }
      }
    }

    private ElasticValue<ValueExpression> getValue(WUniversalTypeExpression type, Object instance, WPropertyExpression property, HbMappedClass hbClass) {
      Method getter = this.mapping.resolveBuiltInProperty(type, property);
      if (getter != null) {
        try {
          Object object = instance;
          if (hbClass.isReleaseClass() && hbClass.getProperty(property.getPersistentName()) == null
              && hbClass.getReleaseBase().getProperty(property.getPersistentName()) != null) {
            //delegate method to superior instance (TCR => TC, ISR => IS)
            Method baseGetter = hbClass.getReleaseBaseProperty().getGetMethod();
            object = baseGetter.invoke(instance);
          }
          Object value = getter.invoke(object);

          if (hbClass.isReleaseClass() && "name".equals(property.getPersistentName())) {
            value = value == null ? "" : value;
            Method versionGetter = hbClass.getProperty(MetamodelExport.JOINED_RELEASE_ATTRIBUTE).getGetMethod();
            Object version = versionGetter.invoke(instance);
            version = version == null ? "" : version;
            value = GeneralHelper.makeReleaseName(value.toString(), version.toString());
          }
          if (value == null) {
            return ElasticValue.none();
          }
          return ElasticValue.one(convert(value, property));
        } catch (IllegalAccessException e) {
          return ElasticValue.none();
        } catch (InvocationTargetException e) {
          return ElasticValue.none();
        } catch (IllegalArgumentException e) {
          return ElasticValue.none();
        }
      }
      AttributeType persistedAT = this.mapping.resolveAdditionalProperty(property);
      if (persistedAT != null && instance instanceof BuildingBlock) {
        Collection<AttributeValueAssignment> avas = ((BuildingBlock) instance).getAssignmentsForId(persistedAT.getId());

        if (!avas.isEmpty() && persistedAT instanceof EnumAT) {
          AttributeValueAssignment[] avaArray = avas.toArray(new AttributeValueAssignment[avas.size()]);
          Comparator<AttributeValueAssignment> c = new Comparator<AttributeValueAssignment>() {
            public int compare(AttributeValueAssignment o1, AttributeValueAssignment o2) {
              return ((EnumAV) o1.getAttributeValue()).compareTo(o2.getAttributeValue());
            }
          };
          Arrays.sort(avaArray, c);
          avas = Lists.newArrayList(avaArray);
        }

        if (avas.size() > property.getUpperBound()) {
          //FIXME what to throw?
          throw new ElasticMiException(ElasticMiException.GENERAL_ERROR, new IllegalStateException("Cannot set " + avas.size()
              + " values on property with upper bound " + property.getUpperBound()));
        }

        List<ValueExpression> result = Lists.newArrayList();
        for (AttributeValueAssignment ava : avas) {
          result.add(convert(ava.getAttributeValue().getValue(), property));
        }
        return ElasticValue.many(result);
      }
      return ElasticValue.none();
    }

    private ValueExpression convert(Object value, WPropertyExpression targetProperty) {
      if (value instanceof UserEntity) {
        //necessary, since ResponsibilityAVs are of type UserEntity
        return ValueExpression.create(((UserEntity) value).getIdentityString());
      }
      else if (targetProperty.getType() instanceof WNominalEnumerationExpression) {
        RNominalEnumerationExpression enumm = (RNominalEnumerationExpression) metamodel.findValueTypeByPersistentName(targetProperty.getType()
            .getPersistentName());
        String name = null;
        if (value instanceof Enum) {
          Enum<?> e = (Enum<?>) value;
          name = e.name();
        }
        else {
          name = value.toString();
        }
        WEnumerationLiteralExpression literal = mapping.resolve(targetProperty, name);
        return ValueExpression.create(enumm.findLiteralByPersistentName(literal.getPersistentName()));
      }
      else {
        if (AtomicDataType.INTEGER.type().equals(targetProperty.getType()) && !BigInteger.class.isInstance(value)) {
          //convert to big integer
          if (Integer.class.isInstance(value)) {
            return ValueExpression.create(BigInteger.valueOf(((Integer) value).longValue()));
          }
          else if (Long.class.isInstance(value)) {
            return ValueExpression.create(BigInteger.valueOf(((Long) value).longValue()));
          }
          throw new ModelException(ModelException.UNSUPPORTED_OPERATION, "Unknown number format to convert to BigInteger: " + value.getClass());
        }
        else if (targetProperty.getType().equals(AtomicDataType.DECIMAL.type()) && !BigDecimal.class.isInstance(value)) {
          //convert to big decimal
          if (Double.class.isInstance(value)) {
            return ValueExpression.create(BigDecimal.valueOf(((Double) value).doubleValue()));
          }
          throw new ModelException(ModelException.UNSUPPORTED_OPERATION, "Unknown number format to convert to BigDecimal: " + value.getClass());
        }
        else {
          return fixDateValues(value);
        }
      }
    }

    private ValueExpression fixDateValues(Object value) {
      if (value instanceof Date) {
        return ValueExpression.create(new Date(((Date) value).getTime()));
      }
      if (value instanceof RuntimePeriod) {
        Date start = ((RuntimePeriod) value).getStart();
        Date end = ((RuntimePeriod) value).getEnd();
        Date startDate = start == null ? null : new Date(start.getTime());
        Date endDate = end == null ? null : new Date(end.getTime());
        return ValueExpression.create(new DateInterval(startDate, endDate));
      }
      return ValueExpression.create(value);
    }

    private boolean needsToBeProcessed(Object hibernateObject) {
      if (hibernateObject instanceof AbstractHierarchicalEntity<?>) {
        AbstractHierarchicalEntity<?> hierarchicEntity = (AbstractHierarchicalEntity<?>) hibernateObject;
        if (AbstractHierarchicalEntity.TOP_LEVEL_NAME.equals(hierarchicEntity.getName())) {
          return false;
        }
      }
      else if (hibernateObject instanceof InformationSystemInterface && (((InformationSystemInterface) hibernateObject).getName() == null)) {
        return false;
      }
      return true;
    }

    /**
     * Split the specified set into a collection of sets, each containing a maximum of 1000 elements as a workaround for oracle db's restriction to 1000 in a list
     * 
     * @param collection
     * @return a collection of sets, each containing a maximum of 1000 elements
     */
    private static Collection<Set<Integer>> splitByThousand(Set<Integer> collection) {
      Collection<Set<Integer>> result = Sets.newHashSet();

      Iterator<Integer> it = collection.iterator();
      while (it.hasNext()) {
        Set<Integer> subset = Sets.newHashSet();
        for (int i = 0; i <= ORACLE_MAX_LIST_SIZE; i++) {
          if (it.hasNext()) {
            subset.add(it.next());
          }
          else {
            break;
          }
        }
        result.add(subset);
      }

      return result;
    }
  }

  private static class Relationship {
    private static final int                 PRIME = 31;
    private final RRelationshipEndExpression end0;
    private final RRelationshipEndExpression end1;

    /**
     * Default constructor.
     */
    public Relationship(RRelationshipEndExpression end0, RRelationshipEndExpression end1) {
      this.end0 = end0;
      this.end1 = end1;
    }

    /**{@inheritDoc}**/
    @Override
    public int hashCode() {
      int result = 1;
      result = PRIME * result + end0.hashCode() + end1.hashCode();
      return result;
    }

    /**{@inheritDoc}**/
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Relationship other = (Relationship) obj;

      if (end0.equals(other.end0)) {
        return end1.equals(other.end1);
      }
      else if (end0.equals(other.end1)) {
        return end1.equals(other.end0);
      }
      return false;
    }
  }
}
