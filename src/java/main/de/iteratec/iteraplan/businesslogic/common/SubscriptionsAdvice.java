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
package de.iteratec.iteraplan.businesslogic.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.MassUpdateServiceImpl;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.businesslogic.service.notifications.NotificationService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockUtil;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.Timeseries.TimeseriesEntry;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.MassUpdate.model.MassUpdateLine;
import de.iteratec.iteraplan.presentation.dialog.common.MemBean;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.email.AbstractModelBuilder;
import de.iteratec.iteraplan.presentation.email.EmailModel;
import de.iteratec.iteraplan.presentation.email.ModelBuilderFactory;


/**
 * The Advice for intercepting service methods and sending the notification emails about changed entities. If the
 * notifications are disabled, the emails will be not sent.
 * 
 * <p>The intercepted methods at the moment are:
 * <ul>
 * <li>see {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
 * <li>see {@link MassUpdateServiceImpl#updateLine(MassUpdateLine)}
 * </ul>
 * 
 */
public class SubscriptionsAdvice {

  private static final Logger                                    LOGGER             = Logger.getIteraplanLogger(SubscriptionsAdvice.class);

  private boolean                                                activated          = false;

  private NotificationService                                    notificationService;
  private BuildingBlockServiceLocator                            bbServiceLocator;

  private RoleService                                            roleService;

  private AttributeTypeService                                   attributeTypeService;
  private TimeseriesService                                      timeseriesService;

  private static final ImmutableMap<TypeOfBuildingBlock, String> BBT_TO_MESSAGE_KEY = initMessageKeys();

  private static final String                                    CREATED            = ".created";

  private static final String                                    UPDATED            = ".updated";

  private static final String                                    DELETED            = ".deleted";

  private static final String                                    SUBSCRIBED         = ".subscribed";

  private static final String                                    UNSUBSCRIBED       = ".unsubscribed";

  private static final String                                    RELEASE_NEW        = ".release.new";

  private static final String                                    RELEASE_COPY       = ".release.copy";

  private static final String                                    GENERIC            = "generic";

  private static final String                                    SUBSCRIBED_USERS   = "subscribedUsers";

  /**
   * Initializes the notification message keys.
   * 
   * @return the immutable map containing the {@link TypeOfBuildingBlock} associated with the message key.
   */
  private static ImmutableMap<TypeOfBuildingBlock, String> initMessageKeys() {
    Builder<TypeOfBuildingBlock, String> builder = ImmutableMap.builder();

    builder.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, "architectural.domain");
    builder.put(TypeOfBuildingBlock.BUSINESSDOMAIN, "business.domain");
    builder.put(TypeOfBuildingBlock.BUSINESSFUNCTION, "business.function");
    builder.put(TypeOfBuildingBlock.BUSINESSMAPPING, "business.mapping");
    builder.put(TypeOfBuildingBlock.BUSINESSOBJECT, "business.object");
    builder.put(TypeOfBuildingBlock.BUSINESSPROCESS, "business.process");
    builder.put(TypeOfBuildingBlock.BUSINESSUNIT, "business.unit");
    builder.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, "technical.component");
    builder.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, "infrastructure.element");
    builder.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, "interface");
    builder.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, "information.system.domain");
    builder.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "information.system");
    builder.put(TypeOfBuildingBlock.PROJECT, "project");
    builder.put(TypeOfBuildingBlock.PRODUCT, "product");

    return builder.build();
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @return the result of the intercepted method
   * @throws Throwable if any exceptions occurs executing method
   */
  public Object saveComponentModelAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      return pjp.proceed();
    }

    Object[] args = pjp.getArgs();
    BuildingBlock bb = getEntity(args[0]);

    if (bb == null || bb.getSubscribedUsers().isEmpty()) {
      return pjp.proceed();
    }

    BuildingBlock buildingBlockClone = cloneBb(bb);
    Map<String, List<TimeseriesEntry>> oldTimeseriesEntries = getTimeseriesEntriesForBb(buildingBlockClone);

    final Object retVal = pjp.proceed();

    if (buildingBlockClone != null) {
      BuildingBlock buildingBlock = load(buildingBlockClone);
      List<String> changedTimeseries = determineChangedTimeseries(oldTimeseriesEntries, buildingBlock);

      String messageKey = GENERIC + UPDATED;
      sendEmail(buildingBlockClone, buildingBlock, changedTimeseries, messageKey, buildingBlock.getSubscribedUsers());
    }

    return retVal;
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @return the result of the intercepted method
   * @throws Throwable if any exceptions occurs executing method
   */
  public Object saveNewComponentModelAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      return pjp.proceed();
    }

    Object[] args = pjp.getArgs();

    final Object retVal = pjp.proceed();

    if (retVal != null) {
      BuildingBlock buildingBlock = getEntity(args[0]);

      if (buildingBlock != null && BBT_TO_MESSAGE_KEY.containsKey(buildingBlock.getTypeOfBuildingBlock())) {
        buildingBlock = load(buildingBlock);
        List<String> changedTimeseries = determineChangedTimeseries(Maps.<String, List<TimeseriesEntry>> newHashMap(), buildingBlock);

        String messageKey = GENERIC + CREATED;
        sendEmail(null, buildingBlock, changedTimeseries, messageKey, buildingBlock.getBuildingBlockType().getSubscribedUsers());
      }
    }

    return retVal;
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @return the result of the intercepted method
   * @throws Throwable if any exceptions occurs executing method
   */
  public Object saveNewReleaseComponentModelAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      return pjp.proceed();
    }

    Object[] args = pjp.getArgs();

    final Object retVal = pjp.proceed();

    if (retVal != null) {
      BuildingBlock buildingBlock = getEntity(args[0]);

      if (buildingBlock != null && BBT_TO_MESSAGE_KEY.containsKey(buildingBlock.getTypeOfBuildingBlock())) {
        buildingBlock = load(buildingBlock);
        List<String> changedTimeseries = determineChangedTimeseries(Maps.<String, List<TimeseriesEntry>> newHashMap(), buildingBlock);

        String messageKey = GENERIC + RELEASE_NEW;
        sendEmail(null, buildingBlock, changedTimeseries, messageKey, buildingBlock.getBuildingBlockType().getSubscribedUsers());
      }
    }

    return retVal;
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @return the result of the intercepted method
   * @throws Throwable if any exceptions occurs executing method
   */
  public Object saveCopyReleaseComponentModelAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      return pjp.proceed();
    }

    Object[] args = pjp.getArgs();

    final Object retVal = pjp.proceed();

    if (retVal != null) {
      BuildingBlock buildingBlock = getEntity(args[0]);

      if (buildingBlock != null && BBT_TO_MESSAGE_KEY.containsKey(buildingBlock.getTypeOfBuildingBlock())) {
        buildingBlock = load(buildingBlock);
        List<String> changedTimeseries = determineChangedTimeseries(Maps.<String, List<TimeseriesEntry>> newHashMap(), buildingBlock);

        String messageKey = GENERIC + RELEASE_COPY;
        sendEmail(null, buildingBlock, changedTimeseries, messageKey, buildingBlock.getBuildingBlockType().getSubscribedUsers());
      }
    }

    return retVal;
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.businesslogic.service.BuildingBlockService#deleteEntity(de.iteratec.iteraplan.model.interfaces.Entity)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @throws Throwable if any exceptions occurs executing method
   */
  public void deleteEntityAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      pjp.proceed();
      return;
    }

    Object[] args = pjp.getArgs();
    BuildingBlock buildingBlock = load((BuildingBlock) args[0], SUBSCRIBED_USERS);

    pjp.proceed();

    if (BBT_TO_MESSAGE_KEY.containsKey(buildingBlock.getTypeOfBuildingBlock())) {
      Collection<User> users = new HashSet<User>(buildingBlock.getSubscribedUsers());
      users.addAll(buildingBlock.getBuildingBlockType().getSubscribedUsers());
      String messageKey = GENERIC + DELETED;
      sendEmail(null, buildingBlock, null, messageKey, users);
    }

  }

  /**
   * Intercepts the {@link MassUpdateServiceImpl#updateLine(MassUpdateLine)} method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @return the result of the intercepted method
   * @throws Throwable if any exceptions occurs executing method
   */
  @SuppressWarnings("unchecked")
  public Object massUpdateLineAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      return pjp.proceed();
    }

    Object[] args = pjp.getArgs();
    MassUpdateLine<BuildingBlock> line = (MassUpdateLine<BuildingBlock>) args[0];
    BuildingBlock bb = line.getBuildingBlockToUpdate();
    if (bb == null) {
      return pjp.proceed();
    }
    bb = load(bb, SUBSCRIBED_USERS);

    if (bb.getSubscribedUsers().isEmpty()) {
      return pjp.proceed();
    }

    BuildingBlock buildingBlockClone = cloneBb(bb);

    Object retVal = pjp.proceed();

    if (buildingBlockClone != null && bb.getId() != null) {
      BuildingBlock buildingBlock = load(buildingBlockClone, SUBSCRIBED_USERS);
      String messageKey = GENERIC + UPDATED;
      sendEmail(buildingBlockClone, buildingBlock, null, messageKey, buildingBlock.getSubscribedUsers());
    }

    return retVal;
  }

  /**
   * Intercepts the {@link de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService#saveComponentModel(MemBean, Integer, org.springframework.webflow.execution.RequestContext, org.springframework.webflow.execution.FlowExecutionContext)}
   * method and sends the notification email.
   * 
   * @param pjp the ProceedingJoinPoint, which exposes the proceed(..) method in order to support around advice in aspects
   * @throws Throwable if any exceptions occurs executing method
   */
  public void excelImportAdvice(ProceedingJoinPoint pjp) throws Throwable {
    if (!activated) {
      pjp.proceed();
      return;
    }

    Object[] args = pjp.getArgs();

    List<BuildingBlockHolder> elements = ((LandscapeData) args[0]).getBuildingBlocks();
    List<BuildingBlock> added = new ArrayList<BuildingBlock>();
    List<BuildingBlock> updated = new ArrayList<BuildingBlock>();

    for (BuildingBlockHolder holder : elements) {
      BuildingBlock buildingBlock = holder.getBuildingBlock();
      if (buildingBlock != null && BBT_TO_MESSAGE_KEY.containsKey(buildingBlock.getTypeOfBuildingBlock())) {
        BuildingBlock reloaded = bbServiceLocator.getService(buildingBlock.getTypeOfBuildingBlock()).loadObjectByIdIfExists(buildingBlock.getId());
        if (reloaded != null) {
          updated.add(holder.getClone());
        }
        else {
          added.add(buildingBlock);
        }
      }

    }

    pjp.proceed();

    for (BuildingBlock bb : added) {
      if (bb.getId() != null) {
        BuildingBlock buildingBlock = load(bb, SUBSCRIBED_USERS);
        String messageKey = GENERIC + CREATED;
        String messageKeySub = GENERIC + SUBSCRIBED;
        Collection<User> subscribedUsers = new HashSet<User>(buildingBlock.getSubscribedUsers());
        subscribedUsers.addAll(buildingBlock.getBuildingBlockType().getSubscribedUsers());
        sendEmail(null, buildingBlock, null, messageKeySub, buildingBlock.getSubscribedUsers());
        sendEmail(null, buildingBlock, null, messageKey, subscribedUsers);
      }
    }

    for (BuildingBlock bb : updated) {
      if (bb.getId() != null) {
        BuildingBlock buildingBlock = load(bb, SUBSCRIBED_USERS);
        String messageKey = GENERIC + UPDATED;
        String messageKeySub = GENERIC + SUBSCRIBED;
        String messageKeyUnsub = GENERIC + UNSUBSCRIBED;
        Collection<User> subscribedUsers = new HashSet<User>(buildingBlock.getSubscribedUsers());
        subscribedUsers.removeAll(bb.getSubscribedUsers());
        Collection<User> unsubscribedUsers = new HashSet<User>(bb.getSubscribedUsers());
        unsubscribedUsers.removeAll(buildingBlock.getSubscribedUsers());
        sendEmail(null, buildingBlock, null, messageKeySub, subscribedUsers);
        sendEmail(null, buildingBlock, null, messageKeyUnsub, unsubscribedUsers);
        sendEmail(bb, buildingBlock, null, messageKey, buildingBlock.getSubscribedUsers());
      }
    }
  }

  /**
   * Loads all timeseries related to the given building block and returns copies of their
   * entry lists.
   * @param bb
   *          The building block whose timeseries are to be loaded
   * @return Map with attribute name as key and a list of timeseries entries as value. The list is detached from the timeseries itself.
   */
  private Map<String, List<TimeseriesEntry>> getTimeseriesEntriesForBb(BuildingBlock bb) {
    Map<String, List<TimeseriesEntry>> timeseriesMap = Maps.newHashMap();
    for (AttributeType at : bb.getBuildingBlockType().getAttributeTypes()) {
      if (at instanceof TimeseriesType && ((TimeseriesType) at).isTimeseries()) {
        Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, at);
        if (timeseries != null) {
          timeseriesMap.put(at.getName(), timeseries.getEntries());
        }
        else {
          timeseriesMap.put(at.getName(), Lists.<TimeseriesEntry> newArrayList());
        }
      }
    }
    return timeseriesMap;
  }

  /**
   * Loads the current timeseries entries of the given building block and compares them to the given old timeseries entries. 
   * @param oldTimeseriesEntries
   *          The old timeseries entries to compare to
   * @param buildingBlock
   *          The Building block this is about
   * @return List of names of the timeseries attributes whose entries have changed
   */
  private List<String> determineChangedTimeseries(Map<String, List<TimeseriesEntry>> oldTimeseriesEntries, BuildingBlock buildingBlock) {
    Map<String, List<TimeseriesEntry>> newTimeseriesEntries = getTimeseriesEntriesForBb(buildingBlock);
    List<String> changedTimeseriesNames = Lists.newArrayList();
    for (Entry<String, List<TimeseriesEntry>> atEntry : oldTimeseriesEntries.entrySet()) {
      if (!atEntry.getValue().equals(newTimeseriesEntries.get(atEntry.getKey()))) {
        changedTimeseriesNames.add(atEntry.getKey());
      }
    }
    return changedTimeseriesNames;
  }

  /**
   * Loads the specified building block and associates it with the current Hibernate Session.
   * 
   * @param buildingBlock the building block to load
   * @param associations the lazy associations to be loaded
   * @return the loaded building block
   */
  private BuildingBlock load(BuildingBlock buildingBlock, String... associations) {
    BuildingBlockService<BuildingBlock, Integer> entityService = bbServiceLocator.getService(buildingBlock.getTypeOfBuildingBlock());
    return entityService.loadObjectById(buildingBlock.getId(), associations);
  }

  private void sendEmail(BuildingBlock buildingBlockClone, BuildingBlock buildingBlock, List<String> changedTimeseries, String messageKey,
                         Collection<User> users) {
    if (users == null || users.isEmpty()) {
      return;
    }
    AbstractModelBuilder modelBuilder = ModelBuilderFactory.createModelBuilder(buildingBlock, buildingBlockClone, changedTimeseries);
    Map<User, EmailModel> models = new HashMap<User, EmailModel>();
    for (User user : users) {
      EmailModel model = modelBuilder.createModel();
      filterModel(user, model);
      models.put(user, model);
    }
    notificationService.sendEmail(users, messageKey, models);
  }

  /**
   * Returns the {@link BuildingBlock} from the {@link BuildingBlockComponentModel}.
   * 
   * @param memBeanParam the memory bean to get the component model
   * @return the {@link BuildingBlock} or {@code null}, if the entity is not instance of {@link BuildingBlockComponentModel}
   */
  private BuildingBlock getEntity(Object memBeanParam) {
    MemBean<?, ?> membean = (MemBean<?, ?>) memBeanParam;
    ComponentModel<?> cm = membean.getComponentModel();

    if (cm instanceof BuildingBlockComponentModel) {
      BuildingBlockComponentModel<?> bbCm = (BuildingBlockComponentModel<?>) cm;
      return bbCm.getEntity();
    }

    return null;
  }

  /**
   * Clones the specified {@link BuildingBlock}.
   * 
   * @param bb the {@link BuildingBlock} to clone
   * @return the cloned building block
   */
  private BuildingBlock cloneBb(BuildingBlock bb) {
    BuildingBlock result = null;

    if (bb != null) {
      result = load(bb);
      try {
        result = BuildingBlockUtil.clone(result);
      } catch (Exception e) {
        LOGGER.debug("Building block cloning failed, returning null", e);
        result = null;
      }
    }

    return result;
  }

  public void setNotificationService(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  public void setRoleService(RoleService roleService) {
    this.roleService = roleService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    this.bbServiceLocator = bbServiceLocator;
  }

  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  private void filterModel(User user, EmailModel model) {
    Set<Integer> roles = new HashSet<Integer>();

    if (!model.getChanges().isEmpty()) {
      boolean isSupervisor = false;
      for (Role role : user.getRoles()) {
        role = roleService.loadObjectById(role.getId());
        if (role.getRoleName().equals("iteraplan_Supervisor")) {
          isSupervisor = true;
        }
        roles.add(role.getId());
      }

      if (!isSupervisor) {
        for (Iterator<EmailModel.Change> iterator = model.getChanges().iterator(); iterator.hasNext();) {
          EmailModel.Change change = iterator.next();
          if (change.getName().toLowerCase().startsWith("attribute")) {
            String attName = change.getName().split("\"")[1];
            AttributeType type = attributeTypeService.getAttributeTypeByName(attName);
            Set<Integer> neededRoleIds4read = type.getAttributeTypeGroup().getRoleIdsWithReadPermissionNotAggregated();
            Set<Integer> copy = new HashSet<Integer>(roles);
            copy.retainAll(neededRoleIds4read);
            if (!neededRoleIds4read.isEmpty() && copy.isEmpty()) {
              iterator.remove();
            }
          }
        }
      }
    }
  }

}
