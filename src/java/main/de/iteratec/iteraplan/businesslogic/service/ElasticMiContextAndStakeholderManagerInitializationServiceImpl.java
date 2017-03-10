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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.M3CRepository;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiFeatureGroupPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiFunctionalPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.StakeholderManager;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.Tcr2IeAssociation;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionAttrTypeGroup;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;


public class ElasticMiContextAndStakeholderManagerInitializationServiceImpl implements ElasticMiContextAndStakeholderManagerInitializationService {

  private final AttributeTypeGroupService attributeTypeGroupService;
  private final UserService               userService;
  private final RoleService               roleService;
  private final M3CRepository             m3cRepository;

  final private StakeholderManager        stakeholderManager;

  public ElasticMiContextAndStakeholderManagerInitializationServiceImpl(final AttributeTypeGroupService attributeTypeGroupService,
      final UserService userService, final RoleService roleService, final M3CRepository m3cRepository, final StakeholderManager stakeholderManager) {
    this.attributeTypeGroupService = attributeTypeGroupService;
    this.userService = userService;
    this.roleService = roleService;
    this.m3cRepository = m3cRepository;
    this.stakeholderManager = stakeholderManager;
  }

  /**{@inheritDoc}**/
  @Override
  public ElasticMiContext initializeMiContextAndStakeholderManager(String userLogin, String dataSource) {
    synchronized (stakeholderManager) {
      initializeStakeholderManager();

      de.iteratec.iteraplan.elasticmi.permission.User user = stakeholderManager.findUserByPersistentName(userLogin);

      ElasticMiContext elasticMiContext = new ElasticMiContext(user, dataSource, m3cRepository);
      ElasticMiContext.setCurrentContext(elasticMiContext);

      return elasticMiContext;
    }
  }

  private void initializeStakeholderManager() {
    clearStakeholderManager();

    createTypePermissions();
    createFeaturureGroupPermissions();
    Map<Role, de.iteratec.iteraplan.elasticmi.permission.Role> roleMapping = createRoles();
    createUsers(roleMapping);
  }

  private void createTypePermissions() {
    for (String bbtName : bbtPersistentNames().values()) {
      stakeholderManager.addTypePermission(new ElasticMiTypePermission(bbtName, ElasticMiAccessLevel.READ));
      stakeholderManager.addTypePermission(new ElasticMiTypePermission(bbtName, ElasticMiAccessLevel.CREATE));
      stakeholderManager.addTypePermission(new ElasticMiTypePermission(bbtName, ElasticMiAccessLevel.UPDATE));
      stakeholderManager.addTypePermission(new ElasticMiTypePermission(bbtName, ElasticMiAccessLevel.DELETE));
    }
  }

  private void createFeaturureGroupPermissions() {
    for (AttributeTypeGroup atg : attributeTypeGroupService.getAllAttributeTypeGroups()) {
      stakeholderManager.addFeatureGroupPermission(new ElasticMiFeatureGroupPermission(atg.getName(), ElasticMiAccessLevel.READ));
      stakeholderManager.addFeatureGroupPermission(new ElasticMiFeatureGroupPermission(atg.getName(), ElasticMiAccessLevel.UPDATE));
    }
  }

  private Map<Role, de.iteratec.iteraplan.elasticmi.permission.Role> createRoles() {
    Map<Role, de.iteratec.iteraplan.elasticmi.permission.Role> roles = Maps.newHashMap();
    Set<String> allAtgNames = allAtgNames();
    Set<String> explicitAtgNames = Sets.newHashSet();
    for (Role iteraplanRole : roleService.loadElementList()) {
      de.iteratec.iteraplan.elasticmi.permission.Role elasticRole = stakeholderManager.createRole(iteraplanRole.getRoleName());
      roles.put(iteraplanRole, elasticRole);
      grantBbtPerms(iteraplanRole, elasticRole);
      Set<String> newExplicitAtgNames = grantAtgPerms(iteraplanRole, elasticRole);
      explicitAtgNames = Sets.union(explicitAtgNames, newExplicitAtgNames);
    }
    for (String atgName : allAtgNames) {
      if (!explicitAtgNames.contains(atgName)) {
        ElasticMiFeatureGroupPermission updatePerm = stakeholderManager.findFeatureGroupPermissionByPersistentName(ElasticMiAccessLevel.UPDATE + ":"
            + atgName);
        for (de.iteratec.iteraplan.elasticmi.permission.Role role : stakeholderManager.getRoles()) {
          role.grantFeatureGroupPermission(updatePerm);
        }
      }
    }
    initSupervisorRole();
    return roles;
  }

  private void grantBbtPerms(Role iteraplanRole, de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    Map<String, Set<ElasticMiTypePermission>> miPermsByType = Maps.newHashMap();
    //set BBT perms for create, update, delete
    Set<Role2BbtPermission> iteraplanBbbtPermissions = aggregateRole2BbtPermissions(iteraplanRole);
    for (Role2BbtPermission iteraplanPermission : iteraplanBbbtPermissions) {
      EditPermissionType permType = iteraplanPermission.getType();
      TypeOfBuildingBlock typeOfBb = iteraplanPermission.getBbt().getTypeOfBuildingBlock();
      ElasticMiTypePermission typePerm = resolveTypePermission(permType, typeOfBb);
      addNullSafe(miPermsByType, typePerm);
    }
    //bbt read perms, correspond to functional perms in iteraplan
    for (PermissionFunctional perm : iteraplanRole.getPermissionsFunctionalAggregated()) {
      ElasticMiTypePermission elasticReadPerm = mapFunctionalPermToMiTypeReadPermission(perm.getTypeOfFunctionalPermission());
      if (elasticReadPerm != null) {
        addNullSafe(miPermsByType, elasticReadPerm);
      }
    }

    for (Entry<String, Set<ElasticMiTypePermission>> entry : miPermsByType.entrySet()) {
      ElasticMiTypePermission maximalFullPermission = determineMaximalCompleteTypePermission(entry);
      if (maximalFullPermission != null) {
        elasticRole.grantTypePermission(maximalFullPermission);
      }
    }

    //handle abstract associations
    handleAbstractAssociationPerms(elasticRole);
  }

  private static void addNullSafe(Map<String, Set<ElasticMiTypePermission>> destination, ElasticMiTypePermission perm) {
    if (destination.get(perm.getStructuredTypePersistentName()) == null) {
      destination.put(perm.getStructuredTypePersistentName(), Sets.<ElasticMiTypePermission> newHashSet());
    }
    destination.get(perm.getStructuredTypePersistentName()).add(perm);
  }

  private Set<String> grantAtgPerms(Role iteraplanRole, de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    Set<String> explicitAtgNames = Sets.newHashSet();
    Set<PermissionAttrTypeGroup> atgPerms = iteraplanRole.getPermissionsAttrTypeGroupAggregated();
    for (PermissionAttrTypeGroup atgPerm : atgPerms) {
      ElasticMiAccessLevel accessLevel = mapAtgPermissionType(atgPerm);
      ElasticMiFeatureGroupPermission fgPerm = stakeholderManager.findFeatureGroupPermissionByPersistentName(accessLevel.name() + ":"
          + atgPerm.getAttributeTypeGroupName());
      elasticRole.grantFeatureGroupPermission(fgPerm);
      explicitAtgNames.add(atgPerm.getAttributeTypeGroupName());
    }
    return explicitAtgNames;
  }

  private void createUsers(Map<Role, de.iteratec.iteraplan.elasticmi.permission.Role> roleMapping) {
    for (User iteraplanUser : userService.loadElementList()) {
      de.iteratec.iteraplan.elasticmi.permission.User elasticUser = stakeholderManager.createUser(iteraplanUser.getLoginName());
      elasticUser.setDefaultLocale(Locale.ENGLISH);
      elasticUser.setEmail(iteraplanUser.getEmail());
      elasticUser.setFirstName(iteraplanUser.getFirstName());
      elasticUser.setLastName(iteraplanUser.getLastName());

      Set<de.iteratec.iteraplan.elasticmi.permission.Role> elasticRoles = Sets.newHashSet();
      for (Role role : iteraplanUser.getRoles()) {
        if (roleMapping.get(role) != null) {
          elasticRoles.add(roleMapping.get(role));
        }
      }
      for (de.iteratec.iteraplan.elasticmi.permission.Role elasticRole : elasticRoles) {
        elasticUser.addRole(elasticRole);
      }
    }
  }

  private void clearStakeholderManager() {
    stakeholderManager.dropUsers();
    stakeholderManager.dropRoles();
    stakeholderManager.dropFunctionalPermissions();
    stakeholderManager.dropFeatureGroupPermissions();
    stakeholderManager.dropTypePermissions();
  }

  private void handleAbstractAssociationPerms(de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    handleIsr2BoAssociation(elasticRole);
    handleTcr2IeAssociation(elasticRole);
    handleTransports(elasticRole);
  }

  private void handleIsr2BoAssociation(de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    boolean boReadPerm = hasPermission(elasticRole, new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.BUSINESSOBJECT),
        ElasticMiAccessLevel.READ));
    boolean boUpdatePerm = hasPermission(elasticRole, new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.BUSINESSOBJECT),
        ElasticMiAccessLevel.UPDATE));
    boolean isrReadPerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), ElasticMiAccessLevel.READ));
    boolean isrUpdatePerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), ElasticMiAccessLevel.UPDATE));
    if (boUpdatePerm || isrUpdatePerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.DELETE.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.ISR2BOASSOCIATION)));
    }
    else if (boReadPerm || isrReadPerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.READ.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.ISR2BOASSOCIATION)));
    }
  }

  private void handleTcr2IeAssociation(de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    boolean tcrReadPerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), ElasticMiAccessLevel.READ));
    boolean tcrUpdatePerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), ElasticMiAccessLevel.UPDATE));
    boolean ieReadPerm = hasPermission(elasticRole, new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT),
        ElasticMiAccessLevel.READ));
    boolean ieUpdatePerm = hasPermission(elasticRole, new ElasticMiTypePermission(
        bbtPersistentNames().get(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT), ElasticMiAccessLevel.UPDATE));
    if (tcrUpdatePerm || ieUpdatePerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.DELETE.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.TCR2IEASSOCIATION)));
    }
    else if (tcrReadPerm || ieReadPerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.READ.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.TCR2IEASSOCIATION)));
    }
  }

  private void handleTransports(de.iteratec.iteraplan.elasticmi.permission.Role elasticRole) {
    boolean boReadPerm = hasPermission(elasticRole, new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.BUSINESSOBJECT),
        ElasticMiAccessLevel.READ));
    boolean boUpdatePerm = hasPermission(elasticRole, new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.BUSINESSOBJECT),
        ElasticMiAccessLevel.UPDATE));
    boolean intReadPerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), ElasticMiAccessLevel.READ));
    boolean intUpdatePerm = hasPermission(elasticRole,
        new ElasticMiTypePermission(bbtPersistentNames().get(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), ElasticMiAccessLevel.UPDATE));
    if (boUpdatePerm || intUpdatePerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.DELETE.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.TRANSPORT)));
    }
    else if (boReadPerm || intReadPerm) {
      elasticRole.grantTypePermission(stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.READ.name() + ":"
          + bbtPersistentNames().get(TypeOfBuildingBlock.TRANSPORT)));
    }
  }

  private static boolean hasPermission(de.iteratec.iteraplan.elasticmi.permission.Role elasticRole, ElasticMiPermission perm) {
    for (ElasticMiPermission permission : elasticRole.getAllPermissions()) {
      if (permission.implies(perm)) {
        return true;
      }
    }
    return false;
  }

  private ElasticMiTypePermission resolveTypePermission(EditPermissionType permissionType, TypeOfBuildingBlock typeOfBuildingBlock) {
    ElasticMiAccessLevel elasticAccessLevel = mapEditPermissionType(permissionType);
    String persistentName = bbtPersistentNames().get(typeOfBuildingBlock);
    return stakeholderManager.findTypePermissionByPersistentName(elasticAccessLevel.name() + ":" + persistentName);
  }

  private static ElasticMiAccessLevel mapEditPermissionType(EditPermissionType editPermissionType) {
    if (EditPermissionType.CREATE.equals(editPermissionType)) {
      return ElasticMiAccessLevel.CREATE;
    }
    else if (EditPermissionType.DELETE.equals(editPermissionType)) {
      return ElasticMiAccessLevel.DELETE;
    }
    else {
      return ElasticMiAccessLevel.UPDATE;
    }
  }

  private static ElasticMiAccessLevel mapAtgPermissionType(PermissionAttrTypeGroup atgPerm) {
    if (PermissionAttrTypeGroup.READ_KEY.equals(atgPerm.getPermissionKey())) {
      return ElasticMiAccessLevel.READ;
    }
    else if (PermissionAttrTypeGroup.READ_WRITE_KEY.endsWith(atgPerm.getPermissionKey())) {
      return ElasticMiAccessLevel.UPDATE;
    }
    return null;
  }

  private ElasticMiTypePermission mapFunctionalPermToMiTypeReadPermission(TypeOfFunctionalPermission perm) {
    String typeName = null;
    if (TypeOfFunctionalPermission.ARCHITECTURALDOMAIN.equals(perm)) {
      typeName = ArchitecturalDomain.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSDOMAIN.equals(perm)) {
      typeName = BusinessDomain.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSFUNCTION.equals(perm)) {
      typeName = BusinessFunction.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSMAPPING.equals(perm)) {
      typeName = BusinessMapping.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSOBJECT.equals(perm)) {
      typeName = BusinessObject.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSPROCESS.equals(perm)) {
      typeName = BusinessProcess.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.BUSINESSUNIT.equals(perm)) {
      typeName = BusinessUnit.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.INFORMATIONSYSTEMDOMAIN.equals(perm)) {
      typeName = InformationSystemDomain.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.INFORMATIONSYSTEMINTERFACE.equals(perm)) {
      typeName = InformationSystemInterface.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE.equals(perm)) {
      typeName = InformationSystem.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.INFRASTRUCTUREELEMENT.equals(perm)) {
      typeName = InfrastructureElement.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.PRODUCT.equals(perm)) {
      typeName = Product.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.PROJECT.equals(perm)) {
      typeName = Project.class.getSimpleName();
    }
    else if (TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES.equals(perm)) {
      typeName = TechnicalComponent.class.getSimpleName();
    }
    if (typeName != null) {
      return stakeholderManager.findTypePermissionByPersistentName(ElasticMiAccessLevel.READ + ":" + typeName);
    }
    return null;
  }

  private void initSupervisorRole() {
    de.iteratec.iteraplan.elasticmi.permission.Role supervisorRole = stakeholderManager.findRoleByPersistentName(Role.SUPERVISOR_ROLE_NAME);
    for (ElasticMiTypePermission perm : stakeholderManager.getTypePermissions()) {
      supervisorRole.grantTypePermission(perm);
    }
    for (ElasticMiFeatureGroupPermission perm : stakeholderManager.getFeatureGroupPermissions()) {
      supervisorRole.grantFeatureGroupPermission(perm);
    }
    for (ElasticMiFunctionalPermission perm : stakeholderManager.getFunctionalPermissions()) {
      supervisorRole.grantFunctionalPermission(perm);
    }
  }

  private Set<String> allAtgNames() {
    Set<String> names = Sets.newHashSet();
    for (AttributeTypeGroup atg : attributeTypeGroupService.getAllAttributeTypeGroups()) {
      names.add(atg.getName());
    }
    return names;
  }

  private static Map<TypeOfBuildingBlock, String> bbtPersistentNames() {
    Map<TypeOfBuildingBlock, String> map = Maps.newHashMap();

    map.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, ArchitecturalDomain.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSDOMAIN, BusinessDomain.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSFUNCTION, BusinessFunction.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSMAPPING, BusinessMapping.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSOBJECT, BusinessObject.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSPROCESS, BusinessProcess.class.getSimpleName());
    map.put(TypeOfBuildingBlock.BUSINESSUNIT, BusinessUnit.class.getSimpleName());
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEM, InformationSystem.class.getSimpleName());
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, InformationSystem.class.getSimpleName());
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, InformationSystemDomain.class.getSimpleName());
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, InformationSystemInterface.class.getSimpleName());
    map.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, InfrastructureElement.class.getSimpleName());
    map.put(TypeOfBuildingBlock.ISR2BOASSOCIATION, Isr2BoAssociation.class.getSimpleName());
    map.put(TypeOfBuildingBlock.PRODUCT, Product.class.getSimpleName());
    map.put(TypeOfBuildingBlock.PROJECT, Project.class.getSimpleName());
    map.put(TypeOfBuildingBlock.TCR2IEASSOCIATION, Tcr2IeAssociation.class.getSimpleName());
    map.put(TypeOfBuildingBlock.TECHNICALCOMPONENT, TechnicalComponent.class.getSimpleName());
    map.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, TechnicalComponent.class.getSimpleName());
    map.put(TypeOfBuildingBlock.TRANSPORT, "InformationFlow");

    return map;
  }

  private static Set<Role2BbtPermission> aggregateRole2BbtPermissions(Role role) {
    Set<Role2BbtPermission> allPerms = Sets.newHashSet(role.getPermissionsBbt());
    for (Role part : role.getConsistsOfRolesAggregated()) {
      allPerms.addAll(part.getPermissionsBbt());
    }
    return allPerms;
  }

  private static ElasticMiTypePermission determineMaximalCompleteTypePermission(Entry<String, Set<ElasticMiTypePermission>> perms) {
    boolean containsRead = false;
    boolean containsUpdate = false;
    boolean containsCreate = false;
    boolean containsDelete = false;

    for (ElasticMiTypePermission perm : perms.getValue()) {
      if (ElasticMiAccessLevel.READ.equals(perm.getAccessLevel())) {
        containsRead = true;
      }
      else if (ElasticMiAccessLevel.UPDATE.equals(perm.getAccessLevel())) {
        containsUpdate = true;
      }
      else if (ElasticMiAccessLevel.CREATE.equals(perm.getAccessLevel())) {
        containsCreate = true;
      }
      else {
        containsDelete = true;
      }
    }

    if (!containsRead) {
      return null;
    }
    else if (!containsUpdate) {
      return new ElasticMiTypePermission(perms.getKey(), ElasticMiAccessLevel.READ);
    }
    else if (!containsCreate) {
      return new ElasticMiTypePermission(perms.getKey(), ElasticMiAccessLevel.UPDATE);
    }
    else if (!containsDelete) {
      return new ElasticMiTypePermission(perms.getKey(), ElasticMiAccessLevel.CREATE);
    }
    else {
      return new ElasticMiTypePermission(perms.getKey(), ElasticMiAccessLevel.DELETE);
    }
  }
}
