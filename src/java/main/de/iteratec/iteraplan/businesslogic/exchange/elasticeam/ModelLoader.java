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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFInstanceStore;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationPropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedClass;
import de.iteratec.iteraplan.elasticeam.metamodel.loader.HbMappedProperty;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder;
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


/**
 * Provides loading functionality for a {@link EMFInstanceStore} instance, given an already loaded {@link IteraplanMapping} instance, conaining a metamodel
 */
public class ModelLoader {

  private static final Logger  LOGGER               = Logger.getIteraplanLogger(ModelLoader.class);

  private static final int     ORACLE_MAX_LIST_SIZE = 995;

  private final SessionFactory sessionFactory;

  /**
   * Default constructor.
   * @param sessionFactory
   */
  public ModelLoader(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Creates {@link UniversalModelExpression}s for each db instance that correspond to the {@link UniversalTypeExpression}s, as defined in the metamodel of the {@link IteraplanMapping} instance
   * 
   * @param model the {@link EMFInstanceStore} to be filled with {@link UniversalModelExpression}s
   * @param mappingInfo the {@link IteraplanMapping} instance, which provides the metamodel
   */
  public BiMap<Object, UniversalModelExpression> load(Model model, IteraplanMapping mappingInfo) {
    LoadRun loadRun = new LoadRun(model, mappingInfo);
    loadRun.run();
    return loadRun.instances;
  }

  /**
   * private class, holding all loading functionality
   */
  private class LoadRun {

    private Model                                                       model;
    private IteraplanMapping                                            mapping;
    private BiMap<Object, UniversalModelExpression>                     instances;
    private BiMap<InformationSystemInterface, UniversalModelExpression> doubleMappedInstances;

    private LinkedHashMap<String, BigInteger>                           logTimes;

    private Map<RelationshipExpression, RelationshipEndExpression>      firstEnds;
    private Set<Integer>                                                iSIsWithTransport;

    /**
     * 
     * Default constructor.
     * @param model the {@link Model} to be filled
     * @param mapping the {@link IteraplanMapping} holding the metamodel
     */
    LoadRun(Model model, IteraplanMapping mapping) {
      this.model = model;
      this.mapping = mapping;
      this.instances = HashBiMap.create();
    }

    /**
     * actually load data
     */
    void run() {
      logTimes = Maps.newLinkedHashMap();
      firstEnds = Maps.newHashMap();
      LOGGER.debug("\n\n\n\n\n ######### Starting MODEL LOAD TASK");
      long time = -System.currentTimeMillis();
      Session session = ModelLoader.this.sessionFactory.getCurrentSession();
      createInstanceExpressions(session);
      createLinkExpressions(session);
      linkInstanceExpressions();
      fixInterfaceNames();
      createInfoFlowsForInterfacesWithoutTransports(session);
      fixInformationFlowConnections();

      LOGGER.debug("###### Finished MODEL LOAD TASK in {0}ms\n\n\n\n", BigInteger.valueOf(System.currentTimeMillis() + time));

      LOGGER.debug("\n\n\n\n\n\n###########################################\n\nDetails\n");
      for (Entry<String, BigInteger> logInfo : logTimes.entrySet()) {
        LOGGER.debug(logInfo.getValue() + " => " + logInfo.getKey());
      }
    }

    /**
     * Link InformationFlow instances to Interface's InformationSystems
     */
    @SuppressWarnings("boxing")
    private void fixInformationFlowConnections() {
      LOGGER.debug("Fixing connections for InfoFlows");
      long time = -System.currentTimeMillis();
      RelationshipTypeExpression informationFlowType = mapping.getRelationshipTypeExpression(MetamodelExport.INFORMATION_FLOW);
      RelationshipEndExpression informationFlow2InformationSystem1 = informationFlowType
          .findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_IS1);
      RelationshipEndExpression informationFlow2InformationSystem2 = informationFlowType
          .findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_IS2);
      PropertyExpression<?> isiID = informationFlowType.findPropertyByPersistentName(MetamodelExport.INFORMATION_FLOW_ISI_ID);
      PropertyExpression<?> direction = informationFlowType.findPropertyByPersistentName("direction");
      EnumerationExpression directionEnum = (EnumerationExpression) direction.getType();

      PropertyExpression<?> id = informationFlowType.findPropertyByPersistentName(MetamodelExport.INFORMATION_FLOW_ID);
      boolean hasTransport = false;

      for (LinkExpression infoFlowInstance : model.findAll(informationFlowType)) {
        hasTransport = false;

        InformationSystemInterface informationSystemInterface = null;

        instances.inverse().get(infoFlowInstance);
        Direction directionValue = null;
        if (doubleMappedInstances.containsValue(infoFlowInstance)) {
          informationSystemInterface = doubleMappedInstances.inverse().get(infoFlowInstance);
          directionValue = informationSystemInterface.getInterfaceDirection();
        }
        else if (instances.containsValue(infoFlowInstance)) {
          Transport transport = (Transport) instances.inverse().get(infoFlowInstance);
          informationSystemInterface = transport.getInformationSystemInterface();
          directionValue = transport.getDirection();
          hasTransport = true;
        }
        else {
          LOGGER.error("Neither Transport nor Interface was found for " + infoFlowInstance);
        }
        if (directionValue != null) {
          EnumerationLiteralExpression directionLiteral = directionEnum.findLiteralByPersistentName(((Enum<?>) directionValue).name());
          infoFlowInstance.setValue(direction, directionLiteral);
        }

        if (informationSystemInterface != null) {
          infoFlowInstance.setValue(isiID, BigInteger.valueOf(informationSystemInterface.getId()));
          UniversalModelExpression informationSystem1 = instances.get(informationSystemInterface.getInformationSystemReleaseA());
          UniversalModelExpression informationSystem2 = instances.get(informationSystemInterface.getInformationSystemReleaseB());

          if (informationSystem1 != null) {
            infoFlowInstance.connect(informationFlow2InformationSystem1, informationSystem1);
          }
          if (informationSystem2 != null) {
            infoFlowInstance.connect(informationFlow2InformationSystem2, informationSystem2);
          }

          // for virtual information flows (that are the ones without a corresponding transport in the static model)
          // the negative id from the attached interface is used as id.
          if (!hasTransport) {
            infoFlowInstance.setValue(id, BigInteger.valueOf(informationSystemInterface.getId()).negate());
          }

        }
      }
      LOGGER.debug("Fixed connections of InfoFlows in {0}ms", BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Due to our model transformation concerning InformationFlows, Transport & Interfaces, we need to create LinkExpressions for all
     * Interfaces not having any Transports attached
     */
    private void createInfoFlowsForInterfacesWithoutTransports(Session session) {
      long time = -System.currentTimeMillis();
      int cnt = 0;
      doubleMappedInstances = HashBiMap.create();
      RelationshipTypeExpression infoFlowType = this.mapping.getRelationshipTypeExpression(MetamodelExport.INFORMATION_FLOW);
      RelationshipEndExpression infoFlow2Is1 = infoFlowType.findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_IS1);
      RelationshipEndExpression infoFlow2Is2 = infoFlowType.findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_IS2);
      RelationshipEndExpression infoFlow2Isi = infoFlowType.findRelationshipEndByPersistentName(MetamodelExport.INFORMATION_FLOW_ISI);

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
        cnt += 1;
        UniversalModelExpression interfaceInstance = this.instances.get(interfaceObject);
        LinkExpression infoFlow = model.create(infoFlowType);
        infoFlow.connect(infoFlow2Isi, interfaceInstance);
        InformationSystemRelease releaseA = interfaceObject.getInformationSystemReleaseA();
        InformationSystemRelease releaseB = interfaceObject.getInformationSystemReleaseB();
        if (releaseA != null) {
          infoFlow.connect(infoFlow2Is1, instances.get(releaseA));
        }
        if (releaseB != null) {
          infoFlow.connect(infoFlow2Is2, instances.get(releaseB));
        }
        doubleMappedInstances.put(interfaceObject, infoFlow);
      }
      LOGGER.debug("\n\nCreated {0} InfoFlows for Interfaces without BusinessObject in {1}ms", Integer.valueOf(cnt),
          BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Split the specified set into a collection of sets, each containing a maximum of 1000 elements as a workaround for oracle db's restriction to 1000 in a list
     * 
     * @param collection
     * @return a collection of sets, each containing a maximum of 1000 elements
     */
    private Collection<Set<Integer>> splitByThousand(Set<Integer> collection) {
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

    /**
     * create {@link InstanceExpression}s for all {@link SubstantialTypeExpression} "classes"
     * @param session
     */
    private void createInstanceExpressions(Session session) {
      LOGGER.debug("\n\n ######### Starting CREATION OF INSTANCE EXPRESSIONS");
      long time = -System.currentTimeMillis();
      int classesCnt = 0;
      int instanceCnt = 0;
      for (Entry<SubstantialTypeExpression, HbMappedClass> substantialType2mappedClass : this.mapping.getSubstantialTypes().entrySet()) {
        HbMappedClass hbClass = substantialType2mappedClass.getValue();
        SubstantialTypeExpression ste = substantialType2mappedClass.getKey();
        if (hbClass.getMappedClass() != null) {
          classesCnt += 1;
          if (hbClass.hasReleaseClass()) {
            hbClass = hbClass.getReleaseClass();
          }
          long tmp = -System.currentTimeMillis();
          int classCnt = 0;
          Criteria cQuery = session.createCriteria(hbClass.getMappedClass());
          List<?> dbInstances = cQuery.list();
          LOGGER.debug("Loaded {0} Instances for class {1} in {2}ms (from db)", Integer.valueOf(dbInstances.size()), hbClass.getMappedClass()
              .getSimpleName(), BigInteger.valueOf(System.currentTimeMillis() + tmp));
          for (Object hibernateObject : dbInstances) {
            if (needsToBeProcessed(hibernateObject)) {
              instanceCnt += 1;
              classCnt += 1;
              InstanceExpression modelInstance = this.model.create(ste);
              this.instances.put(hibernateObject, modelInstance);
              loadProperties(hibernateObject, modelInstance, ste, hbClass);
            }
          }
          LOGGER.debug("Created {0} instances for class {1} in {2}ms", Integer.valueOf(classCnt), hbClass.getMappedClass().getSimpleName(),
              BigInteger.valueOf(System.currentTimeMillis() + tmp));
        }
      }
      LOGGER.debug("Finished creation of {0} instances (for {1} classes) in {2}ms\n\n\n", Integer.valueOf(instanceCnt), Integer.valueOf(classesCnt),
          BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Add names of isr1 and isr2
     */
    private void fixInterfaceNames() {
      long time = -System.currentTimeMillis();
      int cnt = 0;
      SubstantialTypeExpression interfaceType = mapping.getSubstantialTypeExpression(InformationSystemInterface.class.getSimpleName());
      for (InstanceExpression interfaceExpression : model.findAll(interfaceType)) {
        cnt += 1;
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

        interfaceExpression.setValue(MixinTypeNamed.NAME_PROPERTY, newName);
      }
      LOGGER.debug("Fixed names of all {0} Interfaces in {1}ms", Integer.valueOf(cnt), BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * @param hibernateObject
     *      the persisted Object
     * @return true, if a corresponding UniversalModelExpression needs to be created
     */
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
     * Set all values for {@link PropertyExpression}s
     * 
     * @param hibernateObject the persistent {@link Object}
     * @param modelInstance the created {@link UniversalModelExpression}
     * @param universalType the {@link UniversalModelExpression}'s {@link UniversalTypeExpression}
     * @param hbClass the corresponding {@link HbMappedClass} ("source")
     */
    private void loadProperties(Object hibernateObject, UniversalModelExpression modelInstance, UniversalTypeExpression universalType,
                                HbMappedClass hbClass) {
      for (PropertyExpression<?> property : universalType.getProperties()) {
        long time = -System.currentTimeMillis();
        String key = universalType.getPersistentName() + "." + property.getPersistentName();
        if (!logTimes.containsKey(key)) {
          logTimes.put(key, BigInteger.ZERO);
        }
        Object value = getValue(universalType, hibernateObject, property, hbClass);
        if (value != null) {
          this.model.setValue(modelInstance, property, value);
        }
        logTimes.put(key, BigInteger.valueOf(logTimes.get(key).longValue() + System.currentTimeMillis() + time));
      }
    }

    /**
     * Establishes a connection between two {@link UniversalModelExpression}s among the corresponding {@link RelationshipEndExpression}s
     */
    private void linkInstanceExpressions() {
      long time = -System.currentTimeMillis();
      int cnt = 0;
      LOGGER.debug("\n\n\n    #### Started to LINK INSTANCE EXPRESSIONS ");

      for (Entry<SubstantialTypeExpression, HbMappedClass> substantialType2mappedClass : this.mapping.getSubstantialTypes().entrySet()) {
        long tmp = -System.currentTimeMillis();
        HbMappedClass hbClass = substantialType2mappedClass.getValue();
        SubstantialTypeExpression ste = substantialType2mappedClass.getKey();
        if (hbClass.getMappedClass() != null) {
          if (hbClass.hasReleaseClass()) {
            hbClass = hbClass.getReleaseClass();
          }
          for (UniversalModelExpression instanceExpression : model.findAll(ste)) {
            Object object = instances.inverse().get(instanceExpression);
            if (object != null) {
              cnt += 1;
              loadConnections(object, instanceExpression, ste, hbClass);
            }
          }
        }
        LOGGER.debug("Connected instances for {0} in {1}ms", ste.getPersistentName(), BigInteger.valueOf(System.currentTimeMillis() + tmp));
      }
      LOGGER.debug("Loaded Connections for {0} instances in {1}ms", Integer.valueOf(cnt), BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Link all connected {@link UniversalModelExpression}s for a given {@link UniversalModelExpression}
     * 
     * @param hibernateObject the persisted {@link Object} ("source"
     * @param modelInstance the {@link UniversalModelExpression}
     * @param type the {@link UniversalModelExpression}'s {@link UniversalTypeExpression}
     * @param hbClass the {@link UniversalTypeExpression}'s corresponding {@link HbMappedClass} ("source")
     */
    private void loadConnections(Object hibernateObject, UniversalModelExpression modelInstance, UniversalTypeExpression type, HbMappedClass hbClass) {
      for (RelationshipEndExpression relationshipEnd : type.getRelationshipEnds()) {
        RelationshipExpression relationship = relationshipEnd.getRelationship();
        if ((!firstEnds.containsKey(relationship) && relationshipEnd.getUpperBound() <= relationship.getOppositeEndFor(relationshipEnd)
            .getUpperBound()) || (firstEnds.containsKey(relationship) && firstEnds.get(relationship).equals(relationshipEnd))) {
          if (!firstEnds.containsKey(relationship)) {
            firstEnds.put(relationship, relationshipEnd);
          }
          String key = type.getPersistentName() + "." + relationshipEnd.getPersistentName();
          if (!logTimes.containsKey(key)) {
            logTimes.put(key, BigInteger.ZERO);
          }
          HbMappedProperty hbRelation = hbClass.getProperty(relationshipEnd.getPersistentName());
          Object correctHibernateObject = hibernateObject;
          if (hbRelation == null && hbClass.isReleaseClass() && hbClass.getReleaseBase().getProperty(relationshipEnd.getPersistentName()) != null) {
            hbRelation = hbClass.getReleaseBase().getProperty(relationshipEnd.getPersistentName());
            try {
              correctHibernateObject = hbClass.getReleaseBaseProperty().getGetMethod().invoke(correctHibernateObject);
            } catch (IllegalArgumentException e) {
              LOGGER.error(e);
            } catch (IllegalAccessException e) {
              LOGGER.error(e);
            } catch (InvocationTargetException e) {
              LOGGER.error(e);
            }
          }
          if (hbRelation != null && !hbRelation.isPrimitive() && !hbRelation.isEnum()) {
            Method getter = hbRelation.getGetMethod();
            if (getter != null) {
              try {
                Object value = getter.invoke(correctHibernateObject);
                if (value instanceof Collection) {
                  Collection<UniversalModelExpression> values = Lists.newArrayList();
                  for (Object singleValue : (Collection<?>) value) {
                    if (instances.containsKey(singleValue)) {
                      values.add(instances.get(singleValue));
                    }
                  }
                  if (!values.isEmpty()) {
                    modelInstance.connect(relationshipEnd, values);
                  }
                }
                else if (value != null) {
                  UniversalModelExpression iExpression = instances.get(value);
                  if (iExpression != null) {
                    modelInstance.connect(relationshipEnd, iExpression);
                  }
                }
              } catch (IllegalArgumentException e) {
                LOGGER.error(e);
              } catch (IllegalAccessException e) {
                LOGGER.error(e);
              } catch (InvocationTargetException e) {
                LOGGER.error(e);
              }
            }
          }

        }
      }
    }

    /**
     * Create {@link LinkExpression} for each {@link RelationshipTypeExpression} class
     * 
     * @param session
     */
    private void createLinkExpressions(Session session) {
      long time = -System.currentTimeMillis();
      int classesCnt = 0;
      LOGGER.debug("\n\n\n    ###### Creating LINK EXPRESSIONS");
      int instanceCnt = 0;
      for (Entry<RelationshipTypeExpression, HbMappedClass> entry : this.mapping.getRelationshipTypes().entrySet()) {
        RelationshipTypeExpression rte = entry.getKey();
        HbMappedClass hbClass = entry.getValue();
        if (hbClass.getMappedClass() != null) {
          if (Transport.class.equals(hbClass.getMappedClass())) {
            iSIsWithTransport = Sets.newHashSet(Integer.valueOf(-1));
          }
          int classCnt = 0;
          classesCnt += 1;
          long tmp = -System.currentTimeMillis();
          Criteria criteria = session.createCriteria(hbClass.getMappedClass());
          List<?> loadedInstances = criteria.list();
          LOGGER.debug("Loaded {0} {1} instances in {2}ms", Integer.valueOf(loadedInstances.size()), hbClass.getClassName(),
              BigInteger.valueOf(System.currentTimeMillis() + tmp));
          for (Object instance : loadedInstances) {
            instanceCnt += 1;
            classCnt += 1;
            if (instance instanceof Transport) {
              Transport transport = (Transport) instance;
              if (transport.getInformationSystemInterface() != null) {
                iSIsWithTransport.add(transport.getInformationSystemInterface().getId());
              }
            }
            LinkExpression linkExpression = this.model.create(rte);
            this.instances.put(instance, linkExpression);
            loadProperties(instance, linkExpression, rte, hbClass);
            loadConnections(instance, linkExpression, rte, hbClass);
          }
          LOGGER.debug("Created {0} {1}s in {2}ms", Integer.valueOf(classCnt), hbClass.getClassName(),
              BigInteger.valueOf(System.currentTimeMillis() + tmp));
        }
      }
      LOGGER.debug("Created {0} LinkExpressions for {1} RelationshipTypeExpressions in {2}ms", Integer.valueOf(instanceCnt),
          Integer.valueOf(classesCnt), BigInteger.valueOf(System.currentTimeMillis() + time));
    }

    /**
     * Get the {@link Object}'s value for a given {@link PropertyExpression}
     * 
     * @param instance the persisted {@link Object}
     * @param property the current {@link PropertyExpression}
     * @param hbClass the {@link HbMappedClass} for the instance's type
     * 
     * @return the persisted value(s) for the instances property that corresponds to the {@link PropertyExpression}
     */
    private Object getValue(UniversalTypeExpression type, Object instance, PropertyExpression<?> property, HbMappedClass hbClass) {
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

          if (hbClass.isReleaseClass() && MixinTypeNamed.NAME_PROPERTY.getPersistentName().equals(property.getPersistentName())) {
            value = value == null ? "" : value;
            Method versionGetter = hbClass.getProperty(MetamodelExport.JOINED_RELEASE_ATTRIBUTE).getGetMethod();
            Object version = versionGetter.invoke(instance);
            version = version == null ? "" : version;
            value = GeneralHelper.makeReleaseName(value.toString(), version.toString());
          }
          if (value == null) {
            return null;
          }
          return convert(value, property);
        } catch (IllegalAccessException e) {
          LOGGER.error("Error while accessing value + " + getter + "(" + getter + ") caused " + e);
          return null;
        } catch (InvocationTargetException e) {
          LOGGER.error("Error while accessing value + " + getter + "(" + getter + ") caused " + e);
          return null;
        } catch (IllegalArgumentException e) {
          LOGGER.error("Error while accessing value + " + getter + "(" + getter + ") caused " + e);
          return null;
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

        if (avas.size() == 1 && property.getUpperBound() == 1) {
          return convert(avas.iterator().next().getAttributeValue().getValue(), property);
        }
        else if (property.getUpperBound() > 1) {
          List<Object> result = Lists.newArrayList();
          for (AttributeValueAssignment ava : avas) {
            result.add(convert(ava.getAttributeValue().getValue(), property));
          }
          return result;
        }
      }
      return null;
    }

    /**
     * Converts a hibernate (object/) value to the value as required for setting the value of the given {@link PropertyExpression}
     * 
     * @param value the original value
     * @param targetProperty the current {@link PropertyExpression}
     * @return the value that can be used to set the {@link PropertyExpression}'s value
     */
    private Object convert(Object value, PropertyExpression<?> targetProperty) {
      if (value instanceof UserEntity) {
        //necessary, since RelationshipAVs are of type UserEntity
        return ((UserEntity) value).getIdentityString();
      }
      else if (targetProperty instanceof EnumerationPropertyExpression) {
        if (value instanceof Enum) {
          Enum<?> e = (Enum<?>) value;
          return this.mapping.resolve(targetProperty, e.name());
        }
        return this.mapping.resolve(targetProperty, value.toString());
      }
      else if (targetProperty instanceof PrimitivePropertyExpression) {
        if (BuiltinPrimitiveType.INTEGER.equals(targetProperty.getType()) && !BigInteger.class.isInstance(value)) {
          //convert to big integer
          if (Integer.class.isInstance(value)) {
            return BigInteger.valueOf(((Integer) value).longValue());
          }
          else if (Long.class.isInstance(value)) {
            return BigInteger.valueOf(((Long) value).longValue());
          }
          throw new ModelException(ModelException.UNSUPPORTED_OPERATION, "Unknown number format to convert to BigInteger: " + value.getClass());
        }
        else if (targetProperty.getType().equals(BuiltinPrimitiveType.DECIMAL) && !BigDecimal.class.isInstance(value)) {
          //convert to big decimal
          if (Double.class.isInstance(value)) {
            return BigDecimal.valueOf(((Double) value).doubleValue());
          }
          throw new ModelException(ModelException.UNSUPPORTED_OPERATION, "Unknown number format to convert to BigDecimal: " + value.getClass());
        }
        else {
          return fixDateValues(value);
        }
      }
      else {
        return fixDateValues(value);
      }
    }

    /**
     * Creates fresh Date objects from the given value for Date attributes and the Dates in {@link RuntimePeriod}s.
     * This keeps the {@link DiffBuilder} from wrongly detecting differences due to the loaded value being of
     * a from Date derived class like Timestamp.
     * @param value
     *          the property value which potentially needs fixing
     * @return A freshly created Date or RuntimePeriod object if the given value was of these types. The original value otherwise.
     */
    private Object fixDateValues(Object value) {
      if (value instanceof Date) {
        return new Date(((Date) value).getTime());
      }
      if (value instanceof RuntimePeriod) {
        Date start = ((RuntimePeriod) value).getStart();
        Date end = ((RuntimePeriod) value).getEnd();
        Date startDate = start == null ? null : new Date(start.getTime());
        Date endDate = end == null ? null : new Date(end.getTime());
        return new RuntimePeriod(startDate, endDate);
      }
      return value;
    }
  }
}
