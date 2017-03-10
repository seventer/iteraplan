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
package de.iteratec.iteraplan.persistence.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import net.sf.ehcache.loader.CacheLoader;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;


/**
 * Decorator for EHCache, to add to the cache keys information
 * about the datasource an object belongs to
 */
public class DatasourceCacheDecorator extends EhcacheDecoratorAdapter {

  /**
   * @param underlyingCache
   */
  public DatasourceCacheDecorator(Ehcache underlyingCache) {
    super(underlyingCache);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element get(Object key) throws IllegalStateException, CacheException {
    return underlyingCache.get(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element get(Serializable key) throws IllegalStateException, CacheException {
    return underlyingCache.get(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element getQuiet(Object key) throws IllegalStateException, CacheException {
    return underlyingCache.getQuiet(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element getQuiet(Serializable key) throws IllegalStateException, CacheException {
    return underlyingCache.getQuiet(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void put(Element element, boolean doNotNotifyCacheReplicators) throws IllegalArgumentException, IllegalStateException, CacheException {
    underlyingCache.put(createElementWithDecoratedCacheKey(element), doNotNotifyCacheReplicators);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void put(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
    underlyingCache.put(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void putQuiet(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
    underlyingCache.putQuiet(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void putWithWriter(Element element) throws IllegalArgumentException, IllegalStateException, CacheException {
    underlyingCache.putWithWriter(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
    return underlyingCache.remove(getDecoratedCacheKey(key), doNotNotifyCacheReplicators);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Object key) throws IllegalStateException {
    return underlyingCache.remove(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) throws IllegalStateException {
    return underlyingCache.remove(getDecoratedCacheKey(key), doNotNotifyCacheReplicators);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean remove(Serializable key) throws IllegalStateException {
    return underlyingCache.remove(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element getWithLoader(Object key, CacheLoader loader, Object loaderArgument) throws CacheException {
    return underlyingCache.getWithLoader(getDecoratedCacheKey(key), loader, loaderArgument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map getAllWithLoader(Collection keys, Object loaderArgument) throws CacheException {
    Collection<DatasourceCacheKeyDecorator> decoratedKeys = getDecoratedCacheKeyCollection(keys);
    return underlyingCache.getAllWithLoader(decoratedKeys, loaderArgument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void load(Object key) throws CacheException {
    underlyingCache.load(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadAll(Collection keys, Object argument) throws CacheException {
    Collection<DatasourceCacheKeyDecorator> decoratedKeys = getDecoratedCacheKeyCollection(keys);
    underlyingCache.loadAll(decoratedKeys, argument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isElementInMemory(Object key) {
    return underlyingCache.isElementInMemory(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isElementInMemory(Serializable key) {
    return underlyingCache.isElementInMemory(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isElementOnDisk(Object key) {
    return underlyingCache.isElementOnDisk(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isElementOnDisk(Serializable key) {
    return underlyingCache.isElementOnDisk(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExpired(Element element) throws IllegalStateException, NullPointerException {
    return underlyingCache.isExpired(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isKeyInCache(Object key) {
    return underlyingCache.isKeyInCache(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeQuiet(Object key) throws IllegalStateException {
    return underlyingCache.removeQuiet(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeQuiet(Serializable key) throws IllegalStateException {
    return underlyingCache.removeQuiet(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeWithWriter(Object key) throws IllegalStateException, CacheException {
    return underlyingCache.removeWithWriter(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element putIfAbsent(Element element) throws NullPointerException {
    return underlyingCache.putIfAbsent(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean removeElement(Element element) throws NullPointerException {
    return underlyingCache.removeElement(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean replace(Element old, Element element) throws NullPointerException, IllegalArgumentException {
    return underlyingCache.replace(createElementWithDecoratedCacheKey(old), createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element replace(Element element) throws NullPointerException {
    return underlyingCache.replace(createElementWithDecoratedCacheKey(element));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void acquireReadLockOnKey(Object key) {
    underlyingCache.acquireReadLockOnKey(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void acquireWriteLockOnKey(Object key) {
    underlyingCache.acquireWriteLockOnKey(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void releaseReadLockOnKey(Object key) {
    underlyingCache.releaseReadLockOnKey(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void releaseWriteLockOnKey(Object key) {
    underlyingCache.releaseWriteLockOnKey(getDecoratedCacheKey(key));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryReadLockOnKey(Object key, long timeout) throws InterruptedException {
    return underlyingCache.tryReadLockOnKey(getDecoratedCacheKey(key), timeout);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryWriteLockOnKey(Object key, long timeout) throws InterruptedException {
    return underlyingCache.tryWriteLockOnKey(getDecoratedCacheKey(key), timeout);
  }

  /**
   * Decorates each of the given collection's keys with the {@link DatasourceCacheKeyDecorator}
   * if it isn't already.
   * @param keys
   *          collection of keys to decorate
   * @return collection with decorated keys
   */
  private Collection<DatasourceCacheKeyDecorator> getDecoratedCacheKeyCollection(Collection keys) {
    List<DatasourceCacheKeyDecorator> decoratedKeys = CollectionUtils.arrayList();
    for (Object key : keys) {
      decoratedKeys.add(getDecoratedCacheKey(key));
    }
    return decoratedKeys;
  }

  /**
   * Decorates the given key with the {@link DatasourceCacheKeyDecorator} if it isn't already.
   * @param key
   *          the key to decorate
   * @return the decorated key
   */
  private DatasourceCacheKeyDecorator getDecoratedCacheKey(Object key) {
    if (key instanceof DatasourceCacheKeyDecorator) {
      return (DatasourceCacheKeyDecorator) key;
    }
    else {
      String datasource = Constants.MASTER_DATA_SOURCE;
      if (UserContext.getCurrentUserContext() != null) {
        datasource = UserContext.getCurrentUserContext().getDataSource();
      }
      return new DatasourceCacheKeyDecorator(key, datasource);
    }
  }

  /**
   * Creates a new {@link Element} with a key decorated by {@link DatasourceCacheKeyDecorator}
   * if it doesn't have one already.
   * @param element
   *          element whose key is to decorate
   * @return new element with decorated key, or simply the same element, if the key is already decorated
   */
  private Element createElementWithDecoratedCacheKey(Element element) {
    Object cacheKey = getDecoratedCacheKey(element.getKey());
    if (!cacheKey.equals(element.getKey())) {
      return new Element(cacheKey, element.getValue(), element.getVersion());
    }
    else {
      return element;
    }
  }

}
