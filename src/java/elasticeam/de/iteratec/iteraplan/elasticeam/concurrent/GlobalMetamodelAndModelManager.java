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
package de.iteratec.iteraplan.elasticeam.concurrent;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.ElasticeamProfile;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;


/**
 * Responsible for loading and caching the elasticeam metamodel and model for each datasource
 */
@SuppressWarnings({ "PMD", "DoNotUseThreads" })
public class GlobalMetamodelAndModelManager extends Thread {

  private static final Logger                                                 LOGGER                      = Logger
                                                                                                              .getIteraplanLogger(GlobalMetamodelAndModelManager.class);

  /**
   * Default milliseconds between wake-ups to check whether a new update has come.
   */
  private static final long                                                   DEFAULT_CLOCK_TIMEOUT       = 5000;

  /**
   * Default milliseconds to wait when an update was received before checking again.
   */
  private static final long                                                   DEFAULT_UPDATE_TIMEOUT      = 20000;

  /**
   * Specifies the default time before a 'forced' load is triggered, if updates are submitted continuously for
   * approximately the given number of milliseconds. The value equals one hour.
   */
  private static final long                                                   DEFAULT_FORCED_LOAD_TIMEOUT = 3600000;

  private static final String                                                 CACHE_NAME                  = "elasticEamDataSourceCache";

  /**
   * Number of SECONDS(!!!) determining the lifetime for a cached element
   * (set to 10h, since this is the default lifetime of an iteraplan login)
   */
  private static final int                                                    CACHE_LIFETIME              = 36000;

  /**
   * Responsible for the execution of the load tasks.
   */
  private final ExecutorService                                               executor                    = Executors.newSingleThreadExecutor();

  /**
   * Timeouts between wake-ups of the manager to check whether some work is to be done.
   */
  private long                                                                clockTimeout                = DEFAULT_CLOCK_TIMEOUT;

  /**
   * Timeouts between wake-ups, once an update request was received, but not yet executed (to wait for further updates).
   */
  private long                                                                updateTimeout               = DEFAULT_UPDATE_TIMEOUT;

  private boolean                                                             stopManager                 = false;

  /**
   * Provides the calculated (see initialize()) approximate number of update timeouts to make before a load task is forcibly
   * submitted to the executor. This is used to guarantee that the metamodel and model are refreshed even if
   * updates are submitted continuously in intervals smaller that the specified updateTimeout over a long
   * period of time.
   */
  private int                                                                 maxUpdatesBeforeExecution   = 0;

  /**
   * An atomic object access which holds the current state of the metamodel and model manager. The state is updated only from
   * the manager itself, but can be accessed from external objects and other threads.
   */
  private AtomicObjectAccess<ManagerState>                                    managerState                = new AtomicObjectAccess<GlobalMetamodelAndModelManager.ManagerState>();
  private Map<String, AtomicObjectAccess<Future<MetamodelAndModelContainer>>> currentLoadTasks            = Maps.newHashMap();

  private ElasticeamProfile                                                   profile;

  private Ehcache                                                             metamodelAndModelContainers;

  private Map<String, AtomicObjectAccess<Date>>                               updateRequestLog            = Maps.newHashMap();
  private Map<String, AtomicObjectAccess<Integer>>                            updateCount                 = Maps.newHashMap();

  public GlobalMetamodelAndModelManager(ElasticeamProfile profile) {
    super("ElasticeamMetamodelAndModelManager");
    this.setDaemon(true);
    this.profile = profile;
    initialize();
  }

  @Override
  public void run() {
    managerState.set(ManagerState.RUNNING);
    while (!isStopManager()) {
      try {
        if (needToManageUpdate()) {
          manageUpdate();
        }
        try {
          LOGGER.debug("Performing CLOCK sleep: " + clockTimeout);
          managerState.set(ManagerState.CLOCK_SLEEP);
          Thread.sleep(clockTimeout);
          managerState.set(ManagerState.RUNNING);

        } catch (InterruptedException ex) {
          LOGGER.error("Interrupted while perfoming a CLOCK sleep.");
          throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "Interrupted while perfoming a CLOCK sleep.");
        }

        manageLoadResults();
      } catch (ElasticeamException ex) {
        managerState.set(ManagerState.ERROR);
        executor.shutdown();
        throw ex;
      }
    }

    managerState.set(ManagerState.TERMINATED);
  }

  /**
   * Notify {@link GlobalMetamodelAndModelManager} instance, that the selected dataSource needs (re)initialization 
   * 
   * @param dataSource
   *    the dataSource as specified by the current UserContext
   */
  public void doUpdate(String dataSource) {
    synchronized (this) {

      if (isRegistered(dataSource)) {
        this.updateRequestLog.get(dataSource).set(new Date());
      }
      else {
        initializeDataSource(dataSource);
        startLoadTask(dataSource);
      }
    }
  }

  /**
   * Get the {@link MetamodelAndModelContainer} for the specified dataSource
   * 
   * @param dataSource
   *    the key {@link String} referencing the datasource
   * @return
   *    the {@link MetamodelAndModelContainer} holding the data of the datasource as specified
   */
  public MetamodelAndModelContainer getMetamodelAndModel(String dataSource) {
    if (ManagerState.ERROR.equals(managerState.get()) || ManagerState.TERMINATED.equals(managerState.get())) {
      LOGGER
          .warn("The metamodel and model manager has either been shut down or has experienced an internal error. The obtained metamodel and model may not be consistent or up to date.");
    }
    if (metamodelAndModelContainers.getKeysWithExpiryCheck().contains(dataSource)) {
      MetamodelAndModelContainer result = (MetamodelAndModelContainer) metamodelAndModelContainers.get(dataSource).getObjectValue();
      LOGGER.debug("Retrieving metamodel and model: " + result);
      return result;
    }
    else if (currentLoadTasks.get(dataSource) == null || currentLoadTasks.get(dataSource).get() == null) {
      // element has been accessed, but no loadTask has been started yet => start now!
      if (!isRegistered(dataSource)) {
        initializeDataSource(dataSource);
      }
      startLoadTask(dataSource);
    }
    return null;
  }

  /**
   * For each of the handled datasources, check if a corresponding load task has finished;
   * if so, update the cached {@link MetamodelAndModelContainer}
   */
  private void manageLoadResults() {
    Set<String> dataSourceKeys = Sets.newHashSet(updateRequestLog.keySet());
    for (String dataSource : dataSourceKeys) {
      AtomicObjectAccess<Future<MetamodelAndModelContainer>> currentLoadTaskAccess = currentLoadTasks.get(dataSource);
      if (currentLoadTaskAccess != null) {
        Future<MetamodelAndModelContainer> currentLoadTask = currentLoadTaskAccess.get();
        if (currentLoadTask != null && currentLoadTask.isDone()) {
          MetamodelAndModelContainer newMetamodelAndModel = null;
          try {
            newMetamodelAndModel = currentLoadTask.get();

            LOGGER.debug("Replacing metamodel and model for datasource '{0}'.", dataSource);

            Element element = new Element(dataSource, newMetamodelAndModel);
            element.setTimeToIdle(CACHE_LIFETIME);
            metamodelAndModelContainers.put(element);

          } catch (ExecutionException e) {
            LOGGER.error("The execution of a load task has failed with an ExecutionException: \n " + e);
          } catch (InterruptedException e) {
            LOGGER.error("The execution of a load task has failed with an InterruptedException: \n " + e);
          }
          //remove current load task
          currentLoadTasks.get(dataSource).set(null);
          deinitializeExpiredDataSources();
        }
      }
    }
  }

  /**
   * @return true, if there has been a notification for at least one of the managed datasources
   */
  private boolean needToManageUpdate() {
    for (AtomicObjectAccess<Date> updateAccess : updateRequestLog.values()) {
      if (updateAccess.get() != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * For each of the managed data sources, check if the {@link MetamodelAndModelContainer} needs to be reloaded;
   * if so, create new load task and start its execution (only if no other task is currently busy with loading the same datasource)
   */
  private void manageUpdate() {
    Set<String> dataSourcesToUpdate = Sets.newHashSet();
    for (String dataSource : updateRequestLog.keySet()) {
      if (updateRequestLog.get(dataSource).get() != null) {
        Date now = new Date();
        int updates = updateCount.get(dataSource).get().intValue();
        if (((now.getTime() - updateRequestLog.get(dataSource).get().getTime()) >= updateTimeout) || maxUpdatesBeforeExecution < updates) {
          if (currentLoadTasks.get(dataSource).get() == null) {
            // actually executing load task now!
            startLoadTask(dataSource);
          }
          else {
            //already performing load task
            LOGGER.debug("Not starting new loader execution for datasource '{0}', since anotherone is already running", dataSource);
            dataSourcesToUpdate.add(dataSource);
          }
        }
        else {
          updates++;
          updateCount.get(dataSource).set(Integer.valueOf(updates));
        }
      }
    }
    for (String dataSource : dataSourcesToUpdate) {
      updateRequestLog.get(dataSource).set(new Date());
    }
  }

  private void startLoadTask(String dataSource) {
    updateCount.get(dataSource).set(Integer.valueOf(0));
    updateRequestLog.get(dataSource).set(null);
    ElasticeamLoadTask task = getLoadTaskInstance(dataSource);
    LOGGER.debug("Submitting new loader instance to the executor.");

    Future<MetamodelAndModelContainer> future = executor.submit(task);
    currentLoadTasks.get(dataSource).set(future);

    LOGGER.debug("Future " + future + " was set in the execution registry. Leaving startLoadTask().");
  }

  /**
   * Get new {@link ElasticeamLoadTask} instance
   * 
   * @return the new instance
   */
  private ElasticeamLoadTask getLoadTaskInstance(String dataSource) {
    String loaderThreadClass = (String) profile.getValue(ElasticeamProfile.ELASTICEAM_LOADER_TASK);
    if (loaderThreadClass == null) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "Loader task implementation not specified!");
    }
    Object task = instantiateClass(loaderThreadClass, new Class<?>[] { ElasticeamProfile.class, String.class }, new Object[] { profile, dataSource });
    if (!(task instanceof ElasticeamLoadTask)) {
      LOGGER.error("The provided metamodel and model loader task implementation is not an instance of IteraQlLoadTask. Check your configuration.");
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR,
          "The provided metamodel and model loader task implementation is not an instance of IteraQlLoadTask. Check your configuration.");
    }
    return (ElasticeamLoadTask) task;
  }

  private boolean isStopManager() {
    synchronized (this) {
      return this.stopManager;
    }
  }

  /**
   * Sets the stopManager flag. In the next cycle of the manager this is taken into
   * account and the manager is stopped.
   */
  public void setStopManager() {
    LOGGER.info("Setting the stop flag metamodel and model manager of iteraQl.");
    synchronized (this) {
      executor.shutdown();
      this.stopManager = true;
    }
  }

  /**
   * Retrieves custom timeouts for the clock and update waits from the iteraQl profile.
   * If none are provided, the default values are used. Note that the profile field must
   * be set before calling this method. Also estimates the maximum number of updates
   * before a loading execution is started, even if further updates are comming.
   */
  private void initialize() {
    //clock timeout
    long clockValue = getTimeout(profile.getValue(ElasticeamProfile.LOADER_TIMEOUT_CLOCK));
    if (!(clockValue < 0)) {
      this.clockTimeout = clockValue;
    }

    //update timeout
    long updateValue = getTimeout(profile.getValue(ElasticeamProfile.LOADER_TIMEOUT_UPDATE));
    if (!(updateValue < 0)) {
      this.updateTimeout = updateValue;
    }

    //update timeout
    long forceValue = getTimeout(profile.getValue(ElasticeamProfile.LOADER_TIMEOUT_FORCE));
    if (forceValue < 0) {
      forceValue = DEFAULT_FORCED_LOAD_TIMEOUT;
    }

    //estimate maximum number of updates before a load execution is
    //forcibly started no matter whether further updates are coming or not
    this.maxUpdatesBeforeExecution = (int) Math.ceil((double) forceValue / updateTimeout);

    // initialize cache
    CacheConfiguration cfg = new CacheConfiguration(CACHE_NAME, 1);
    this.metamodelAndModelContainers = new Cache(cfg);
    this.metamodelAndModelContainers.initialise();

    //set the initial state of the manager
    this.managerState.set(ManagerState.NEW);
  }

  /**
   * Add the dataSource specific keys where required
   * @param dataSource
   *    The {@link String} referencing the iteraplan DataSource
   */
  private void initializeDataSource(String dataSource) {
    updateRequestLog.put(dataSource, new AtomicObjectAccess<Date>());
    AtomicObjectAccess<Integer> zero = new AtomicObjectAccess<Integer>();
    zero.set(Integer.valueOf(0));
    updateCount.put(dataSource, zero);
    currentLoadTasks.put(dataSource, new AtomicObjectAccess<Future<MetamodelAndModelContainer>>());
    metamodelAndModelContainers.getCacheConfiguration().setMaxElementsInMemory(
        metamodelAndModelContainers.getCacheConfiguration().getMaxElementsInMemory() + 1);
  }

  /**
   * Remove keys, if the corresponding {@link MetamodelAndModelContainer} in the cache has expired
   */
  private void deinitializeExpiredDataSources() {
    List<?> inMemory = metamodelAndModelContainers.getKeysWithExpiryCheck();
    Set<String> dataSourcesToRemove = Sets.newHashSet();
    for (String dataSource : currentLoadTasks.keySet()) {
      if (!inMemory.contains(dataSource) && currentLoadTasks.containsKey(dataSource) && currentLoadTasks.get(dataSource).get() == null) {
        updateRequestLog.remove(dataSource);
        updateCount.remove(dataSource);
        dataSourcesToRemove.add(dataSource);
      }
    }
    for (String dataSource : dataSourcesToRemove) {
      currentLoadTasks.remove(dataSource);
    }
    metamodelAndModelContainers.getCacheConfiguration().setMaxElementsInMemory(inMemory.size());
  }

  /**
   * @return true, if the referenced DataSource has already been registered
   */
  private boolean isRegistered(String dataSource) {
    return currentLoadTasks.containsKey(dataSource);
  }

  private static long getTimeout(Object propertyValue) {
    try {
      if (!(propertyValue == null) && !((String) propertyValue).matches("//s*")) {
        long timeoutLong = Long.valueOf((String) propertyValue).longValue();
        if (timeoutLong > 0) {
          return timeoutLong;
        }
      }
    } catch (ClassCastException ce) {
      //Do nothing, will result in using the default setting.
    } catch (NumberFormatException ne) {
      //Do nothing, will result in using the default setting.
    }
    //No custom timeout was specified
    return -1;
  }

  /**
   * Creates an instance of a class.
   * @param className The name of the class, preferably with package.
   * @param classes An array of classes, which defiens the constructor of the class to be used for the instantiation.
   * @param arguments The actual arguments to be used for the instantiation of the object.
   * @return The instantiated object.
   */
  private static Object instantiateClass(String className, Class<?>[] classes, Object[] arguments) {

    Object result = null;

    try {

      Class<?> clazz = Class.forName(className);
      Constructor<?> constructor = clazz.getConstructor(classes);
      result = constructor.newInstance(arguments);

    } catch (Exception e) {
      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "Failed to instantiate class '" + className + "' due to the following: "
          + e.getMessage(), e);
    }

    return result;
  }

  /**
   * Defines the different states of the {@link GlobalMetamodelAndModelManager}.
   */
  public enum ManagerState {

    NEW("NEW"), CLOCK_SLEEP("CLOCK_SLEEP"), UPDATE_SLEEP("UPDATE_SLEEP"), RUNNING("RUNNING"), TERMINATED("TERMINATED"), ERROR("ERROR");

    private String status;

    private ManagerState(String staus) {
      this.status = staus;
    }

    public String getValue() {
      return this.status;
    }
  }
}
