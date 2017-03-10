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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.ComparableListAdapter;
import de.iteratec.iteraplan.common.util.DefaultValueMap;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.fulltextsearch.BuildingBlockBridge;
import de.iteratec.iteraplan.model.interfaces.Entity;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * Base class for all model classes. This class manages the following aspects common to all model
 * classes:
 * <ul>
 * <li>assignment of attribute values</li>
 * <li>ownership by user entities</li>
 * <li>access to the type system</li>
 * <li>ids and optimistic locking</li>
 * <li>keeping track of the last change</li>
 * </ul>
 */
@javax.persistence.Entity
@Audited
public abstract class BuildingBlock implements Comparable<BuildingBlock>, Entity, Serializable {

  private static final long             serialVersionUID          = 3093927874801057265L;

  private static final Logger           LOGGER                    = Logger.getIteraplanLogger(BuildingBlock.class);

  /** {@link #getId()} */
  @Field(store = Store.YES, index = Index.UN_TOKENIZED)
  private Integer                       id;

  /** {@link #getOlVersion()} */
  private Integer                       olVersion;

  /** {@link #getBuildingBlockType()} */
  @IndexedEmbedded
  private BuildingBlockType             buildingBlockType;

  /** {@link #getAttributeValueAssignments()} */
  @IndexedEmbedded
  private Set<AttributeValueAssignment> attributeValueAssignments = new HashSet<AttributeValueAssignment>();

  /** {@link #getOwningUserEntities()} */
  private Set<UserEntity>               owningUserEntities        = new HashSet<UserEntity>();

  /** {@link #getLastModificationUser()} */
  private String                        lastModificationUser;

  /** {@link #getLastModificationTime()} */
  private Date                          lastModificationTime;

  /** {@link #getSubscribedUsers()} */
  private Set<User>                     subscribedUsers           = new HashSet<User>();
  /** The generic building block state. */
  private String                        state;
  private Set<Seal>                     seals                     = Sets.newTreeSet();

  public BuildingBlock() {
    // nothing to do
  }

  @Field(store = Store.YES, index = Index.UN_TOKENIZED)
  public String getAttributeStringForSearchIndexing() {
    StringBuilder sb = new StringBuilder();
    for (AttributeValueAssignment ava : getAttributeValueAssignments()) {
      AttributeValue av = ava.getAttributeValue();
      AttributeType at = av.getAbstractAttributeType();
      AttributeTypeGroup atg = at.getAttributeTypeGroup();

      sb.append(atg.getName()).append(": ");
      sb.append(at.getName()).append(": ");
      sb.append(av.getValueString()).append("^^^");
    }

    return sb.toString();
  }

  @Field(store = Store.YES, index = Index.UN_TOKENIZED)
  public String getActiveDataSource() {
    return UserContext.getActiveDatasource();
  }

  @DocumentId
  @FieldBridge(impl = BuildingBlockBridge.class)
  public String getDocumentId() {
    // if the id is null, the documentId is null, too
    if (id == null) {
      return null;
    }
    return UserContext.getActiveDatasource() + "_" + id;
  }

  /**
   * @return The unique identifier of the building block.
   */
  public Integer getId() {
    return id;
  }

  /**
   * @return The optimistic locking version.
   */
  public Integer getOlVersion() {
    return olVersion;
  }

  /**
   * @return The {@link BuildingBlockType} of this building block.
   */
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public BuildingBlockType getBuildingBlockType() {
    return buildingBlockType;
  }

  public abstract TypeOfBuildingBlock getTypeOfBuildingBlock();

  /**
   * @return The set of {@link AttributeValueAssignment}s assigned to this building block.
   */
  public Set<AttributeValueAssignment> getAttributeValueAssignments() {
    return this.attributeValueAssignments;
  }

  /**
   * @return The set of {@link UserEntity}s owning this building block.
   */
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public Set<UserEntity> getOwningUserEntities() {
    return owningUserEntities;
  }

  /**
   * @return The login name of the last user who modified this building block.
   */
  public String getLastModificationUser() {
    return this.lastModificationUser;
  }

  /**
   * @return The date and time of the last modification to this building block.
   */
  public Date getLastModificationTime() {
    return lastModificationTime;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public Set<User> getSubscribedUsers() {
    return subscribedUsers;
  }

  public void setSubscribedUsers(Set<User> subscribedUsers) {
    this.subscribedUsers = subscribedUsers;
  }

  /**
   * <p>
   * This method should not be called directly, instead use BuildingBlockFactory.createXXX().
   * Deprecated only to make this visible to the developer. It's okay to call it in tests when the
   * buildingBlockType should be set, but no Database should be used, or if it should be set
   * dynamically.
   * </p>
   * Sets this BuildingBlocks BuildingBlockType that was loaded from the database.
   * 
   * @param buildingBlockType
   */
  @Deprecated
  public void setBuildingBlockType(BuildingBlockType buildingBlockType) {
    if (buildingBlockType == null) { // NOPMD
      // TODO this should never happen, but happens when hibernate initializes an object
    }
    else if (buildingBlockType.getTypeOfBuildingBlock() == null) { // NOPMD
      // TODO this should never happen, but happens when hibernate initializes an object
    }
    else {
      // prevent setting a wrong BuildingBlockType
      if (!buildingBlockType.getTypeOfBuildingBlock().equals(getTypeOfBuildingBlock())) {
        LOGGER.error("Tried to initialize the buildingblock " + this.getClass().getName() + " with BuildingBlockType: "
            + buildingBlockType.getTypeOfBuildingBlock());
        throw new IllegalStateException("Buildingblocks must be initialized with their correct buildingBlockType!");
      }
    }
    this.buildingBlockType = buildingBlockType;
  }

  public void setAttributeValueAssignments(Set<AttributeValueAssignment> attributeValueAssignments) {
    this.attributeValueAssignments = attributeValueAssignments;
  }

  public void setOwningUserEntities(Set<UserEntity> owningUserEntities) {
    this.owningUserEntities = owningUserEntities;
  }

  public void setLastModificationUser(String user) {
    this.lastModificationUser = user;
  }

  public void setLastModificationTime(Date lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  /**
   * Adds the given {@link UserEntity} to this building block.
   * <p>
   * This method updates only this side of the association to the owning user entities. This is
   * sufficient if the Java object model must not be accurate, that is, if the building block is
   * immediately saved to the database and won't be needed further. In this case this method is
   * highly preferred over setting both sides of the association, because trying to set both sides
   * results in a database join over each and every subclass of building block. In some situations
   * this causes an error with MySQL.
   * 
   * @param userEntity
   *          The user entity to add.
   * @throws IllegalArgumentException
   *           If the passed-in user entity is {@code null}.
   */
  public void addOwningUserEntity(UserEntity userEntity) {

    if (userEntity == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    owningUserEntities.add(userEntity);
  }

  /**
   * Adds a set of {@link UserEntity}s to this building block. Updates only this side of the
   * association (see {@link #addOwningUserEntity(UserEntity)}) for an explanation.
   * 
   * @param userEntities
   *          The set of user entities to add.
   * @throws IllegalArgumentException
   *           If the passed-in set is {@code null}.
   */
  // TODO Exception by set where for example only one element is null ????
  public void addOwningUserEntities(Set<? extends UserEntity> userEntities) {

    if (userEntities == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    for (UserEntity userEntity : userEntities) {
      addOwningUserEntity(userEntity);
    }
  }

  /**
   * Removes all {@link AttributeValueAssignment}s from this building block. Updates both sides of
   * the association.
   */
  public void removeAttributeValueAssignments() {
    for (AttributeValueAssignment ava : attributeValueAssignments) {
      ava.setBuildingBlock(null);
    }
    this.attributeValueAssignments.clear();
  }

  public void removeAttributeValueAssignments(Set<AttributeValueAssignment> avas) {
    final Set<AttributeValueAssignment> toRemove = Sets.newHashSet(attributeValueAssignments);
    toRemove.retainAll(avas);

    for (AttributeValueAssignment ava : toRemove) {
      // In this case the one-sided removal of the reference between AVAs and their attribute values
      // as done in AttributeValueAssignment#removeReferences() is not enough, as it leads to an
      // ObjectDeletedException if the hibernate 2nd-level cache is not active. See ITERAPLAN-888.
      // This line is added here, because for other calls of ava.removeReferences the two-ways removal
      // can lead to problems, see ITERAPLAN-1457.
      ava.getAttributeValue().getAttributeValueAssignments().remove(ava);
      ava.removeReferences();
    }
  }

  public void removeAttributeValueAssignmentsForAttributeType(Integer atId) {
    Set<AttributeValueAssignment> set = this.getAttributeValueAssignments();
    for (Iterator<AttributeValueAssignment> it = set.iterator(); it.hasNext();) {
      AttributeValueAssignment assignment = it.next();

      if (assignment.getAttributeValue().getAbstractAttributeType().getId().equals(atId)) {
        assignment.setBuildingBlock(null);
        assignment.getAttributeValue().getAttributeValueAssignments().remove(assignment);
        it.remove();
      }
    }
    attributeValueAssignments = set;
  }

  /**
   * Removes the given {@link UserEntity} from this building block.
   * <p>
   * This method updates only this side of the association to the owning user entities. This is
   * sufficient if the Java object model must not be accurate, that is, if the building block is
   * immediately saved to the database and won't be needed further. In this case this method is
   * highly preferred over setting both sides of the association, because trying to set both sides
   * results in a database join over each and every subclass of building block. In some situations
   * this causes an error with MySQL.
   * 
   * @param userEntity
   *          The user entity to add.
   * @throws IllegalArgumentException
   *           If the passed-in user entity is {@code null}.
   */
  public void removeOwningUserEntity(UserEntity userEntity) {
    if (userEntity == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    owningUserEntities.remove(userEntity);
  }

  /**
   * Removes all owning {@link UserEntity}s from this building block. Updates only this side of the
   * association (see {@link #removeOwningUserEntity(UserEntity)}) for an explanation.
   */
  public void removeOwningUserEntities() {
    this.owningUserEntities.clear();
  }

  /**
   * Convenience method that returns a HashBucketMap with the AttributeType as key and a List of
   * sorted AttributeValue as values. The keys in the HashBucketMap are only the AttributeTypes
   * which have one or more values assigned for this instance!
   * 
   * @return (AttributeType, ArrayList(A))
   */
  public HashBucketMap<AttributeType, AttributeValue> getAttributeTypeToAttributeValues() {
    HashBucketMap<AttributeType, AttributeValue> map = new HashBucketMap<AttributeType, AttributeValue>();
    for (AttributeValueAssignment ava : this.getAttributeValueAssignments()) {
      map.add(ava.getAttributeValue().getAbstractAttributeType(), ava.getAttributeValue());
    }

    for (Entry<AttributeType, List<AttributeValue>> e : map.entrySet()) {
      e.getKey().getTypeOfAttribute().sort(e.getValue());
    }
    return map;
  }

  /**
   * Method used by nested bean property paths, that returns Map with the AttributeTypeId as key.
   * The keys are only the AttributeTypes which have one or more values assigned for this instance!
   * 
   * @return
   *    A java.util.Map of ComparableListAdapters (mapped by AttributeTypeId as String),
   *    which wrap java.util.List objects (containing AttributeValues).
   *    This allows lookup and sorting of multivalue attribute values. 
   */
  public Map<String, ComparableListAdapter<AttributeValue>> getAttributeTypeIdToValues() {
    Map<String, List<AttributeValue>> tmpMap = new HashMap<String, List<AttributeValue>>();
    for (AttributeValueAssignment ava : this.getAttributeValueAssignments()) {
      AttributeType at = ava.getAttributeValue().getAbstractAttributeType();
      String attributeTypeId = at.getId().toString();

      if (tmpMap.containsKey(attributeTypeId)) {
        List<AttributeValue> avList = tmpMap.get(attributeTypeId);
        avList.add(ava.getAttributeValue());
      }
      else {
        List<AttributeValue> avList = new LinkedList<AttributeValue>();
        avList.add(ava.getAttributeValue());
        tmpMap.put(attributeTypeId, avList);
      }
    }

    Map<String, ComparableListAdapter<AttributeValue>> map = new HashMap<String, ComparableListAdapter<AttributeValue>>();
    Set<Entry<String, List<AttributeValue>>> entrySet = tmpMap.entrySet();

    for (Entry<String, List<AttributeValue>> entry : entrySet) {
      map.put(entry.getKey(), new ComparableListAdapter<AttributeValue>(entry.getValue(), ComparableListAdapter.SORT_WRAPPED_LIST));
    }

    ComparableListAdapter<AttributeValue> defaultValue = new ComparableListAdapter<AttributeValue>(new ArrayList<AttributeValue>(0),
        ComparableListAdapter.NO_SORT_WRAPPED_LIST);

    return new DefaultValueMap<String, ComparableListAdapter<AttributeValue>>(map, defaultValue);
  }

  public String getAttributeValue(String attributeName, Locale locale) {
    for (AttributeValueAssignment ava : this.getAttributeValueAssignments()) {
      if (ava.getAttributeValue().getAbstractAttributeType().getName().equals(attributeName)) {
        return ava.getAttributeValue().getLocalizedValueString(locale);
      }
    }
    return "";
  }

  /**
   * Returns a sorted List of connected AttributeValues for the given AttributeType.
   * 
   * @param attributeType
   * @return List of AttributeValue.
   */
  public List<AttributeValue> getConnectedAttributeValues(AttributeType attributeType) {
    Preconditions.checkNotNull(attributeType);
    Preconditions.checkNotNull(attributeType.getTypeOfAttribute());

    // Extract the attribute values from the assignments
    List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
    for (AttributeValueAssignment a : attributeValueAssignments) {
      if (a.getAttributeValue().getAbstractAttributeType().getId().equals(attributeType.getId())) {
        attributeValues.add(a.getAttributeValue());
      }
    }

    // Filter and sort the values according to the attribute type
    attributeValues = attributeType.getTypeOfAttribute().filterAndSort(attributeValues);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(this.getClass().getName() + ".getConnectedAttributeValues(" + attributeType + ") returns: " + attributeValues);
    }
    return attributeValues;
  }

  /**
   * Returns an assignment object, if there is an {@link AttributeValue} for the attribute type with {@code atId}
   * assigned to this building block. If there is no such attribute value assigned, null is returned.
   * If there is more than one value assigned, always the first one will be returned.
   * 
   * @param atId
   *        The ID of an {@link AttributeType} to which an assigned value should belong.
   * @return An attribute value assignment that relates to the passed attribute type (ID), or null if none such.
   */
  public AttributeValueAssignment getAssignmentForId(Integer atId) {
    Set<AttributeValueAssignment> assignmentsForId = getAssignmentsForId(atId);

    if (assignmentsForId.isEmpty()) {
      return null;
    }
    else {
      return Iterables.get(assignmentsForId, 0);
    }
  }

  /**
   * Returns assignment objects for each {@link AttributeValue} for the attribute type with {@code atId} that is
   * assigned to this building block. If there is no such attribute value assigned, an empty set is returned.
   * 
   * @param atId
   *        The ID of an {@link AttributeType} to which the assigned values should belong.
   * @return Attribute value assignments that relate to the passed attribute type (ID). May be empty
   */
  public Set<AttributeValueAssignment> getAssignmentsForId(Integer atId) {
    if (atId == null) {
      return Collections.emptySet();
    }

    Set<AttributeValueAssignment> result = Sets.newHashSet();
    for (AttributeValueAssignment ava : attributeValueAssignments) {
      if (Objects.equal(ava.getAttributeValue().getAbstractAttributeType().getId(), atId)) {
        result.add(ava);
      }
    }

    return result;
  }

  /**
   * Returns the assignment object between {@code attributeValue} (belonging to attribute type with {@code atId})
   * and this building block. If that attribute value is not assigned, null is returned.
   * @param atId
   *        The ID of an {@link AttributeType} to which the assigned value belongs.
   * @param attributeValue
   *        The attribute value which should be check for being assigned to this building block
   * @return An attribute value assignment that links this building block to the passed attrbuteValue, or null if it is not assigned.
   */
  public AttributeValueAssignment getAssignmentForId(Integer atId, AttributeValue attributeValue) {
    if (atId == null) {
      return null;
    }

    for (AttributeValueAssignment ava : attributeValueAssignments) {
      boolean attributeTypesEquals = Objects.equal(ava.getAttributeValue().getAbstractAttributeType().getId(), atId);
      boolean attributeValueEquals = Objects.equal(ava.getAttributeValue().getId(), attributeValue.getId());

      if (attributeTypesEquals && attributeValueEquals) {
        return ava;
      }
    }

    return null;
  }

  public Set<Integer> getOwningUserEntityIds() {

    Set<Integer> ids = new HashSet<Integer>();
    for (UserEntity entity : owningUserEntities) {
      ids.add(entity.getId());
    }

    return ids;
  }

  /**
   * Validates the building block in its context. This method performs a validation of the building
   * block it is called upon in its current context. Subclasses of this class can implement various
   * domain-specific checks, this class itself does not validate anything.
   */
  public void validate() {
    LOGGER.debug("Validating building block ...");
  }

  /**
   * Implements the default behaviour for comparing building blocks by identity strings. The
   * comparison is case-insensitive.
   */
  public int compareTo(BuildingBlock o) {
    return getIdentityString().compareToIgnoreCase(o.getIdentityString());
  }

  @Override
  public String toString() {
    return getIdentityString();
  }

  public String getHierarchicalName() {
    return getIdentityString();
  }

  public String getNonHierarchicalName() {
    return getIdentityString();
  }

  /**
   * Returns the building block state.
   * 
   * <p>Implementation note: This method is made as private, to prevent
   * invalid values. Extending classes should add methods having {@link Enum}
   * classes for the possible state values.
   * 
   * @return the building block state
   */
  private String getState() {
    return state;
  }

  /**
   * Sets the building block state.
   * 
   * @param state the building block state value
   */
  private void setState(String state) {
    this.state = state;
  }

  /**
   * Returns the sorted collection of the seals, set for this information system. The
   * seals itself are immutable.
   * 
   * @return the seals
   */
  public Set<Seal> getSeals() {
    return seals;
  }

  public void setSeals(Set<Seal> seals) {
    this.seals = seals;
  }

  /**
   * Adds new seal. Updates both sides of the association.
   * 
   * @param seal the new seal to add
   */
  public void addSeal(Seal seal) {
    Preconditions.checkNotNull(seal);

    seal.setBb(this);
    getSeals().add(seal);
  }

  /**
   * Returns the last created {@link Seal} instance.
   * 
   * @return the last created {@link Seal} instance or {@code null}, if the
   *    seals collection is empty
   */
  public Seal getLastSeal() {
    if (!getSeals().isEmpty()) {
      return Iterables.getLast(getSeals());
    }

    return null;
  }

  /**
   * Returns the current Seal state
   * 
   * @return the current Seal state
   */
  public SealState getSealState() {
    final String stateValue = getState();
    SealState sealState = SealState.getByValue(stateValue);

    if (sealState == SealState.VALID && getLastSeal() != null) {
      Seal lastSeal = getLastSeal();
      int expirationInDays = IteraplanProperties.getIntProperty(IteraplanProperties.SEAL_EXPIRATION_DAYS);
      long lastSealCreationTime = lastSeal.getDate().getTime();
      boolean isOutdated = new DateTime().minusDays(expirationInDays).isAfter(lastSealCreationTime);

      if (isOutdated) {
        return SealState.OUTDATED;
      }
    }

    return sealState;
  }

  /**
   * Sets the current seal state
   * 
   * @param sealState the seal state
   */
  public void setSealState(SealState sealState) {
    Preconditions.checkNotNull(sealState);
    setState(sealState.toString());
  }

  /**
   * Sets the current seal state to {@link SealState#INVALID}, unless it is {@link SealState#NOT_AVAILABLE}.
   */
  public void breakSeal() {
    if (getSealState() != SealState.NOT_AVAILABLE) {
      setSealState(SealState.INVALID);
    }
  }

  /**
   * @return the description assigned to this building block, or an empty string, if this
   *         buildinglock has no description (like BusinessMappings).<br />
   *         Implementations may take special care to internationalize the description if necessary.
   */
  public abstract String getDescription();

  /**
   * This method is not compliant with equals().
   * If both id's are null, equals will return true, but the hashCode's are different.
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    if (id == null) {
      result = super.hashCode();
    }
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
   * This method is not compliant with hashCode().
   * If both id's are null, equals will return true, but the hashCode's are different.
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BuildingBlock)) {
      return false;
    }
    final BuildingBlock other = (BuildingBlock) obj;
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    }
    else if (!id.equals(other.getId())) {
      return false;
    }
    return true;
  }
}