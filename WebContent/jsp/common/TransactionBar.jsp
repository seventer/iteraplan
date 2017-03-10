<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://tags.iteratec.de/iteratec-tags" prefix="itera"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<%-- Only set to true, if building blocks are affected by delete --%>
<tiles:useAttribute name="buildingBlockAffectedByDelete" ignore="true"/>

<%-- Only set to true, if the user has functional permissions and the instance/edit permissions should not be checked --%>
<tiles:useAttribute name="ignoreWritePermission" ignore="true" />

<%-- If set to true, a script is inserted to get a confirmation for deletion of an attribute type --%>
<tiles:useAttribute name="require_attribute_type_delete_confirmation" ignore="true" />

<%-- If set to true, the close button will be rendered (default: true) --%>
<tiles:useAttribute name="showCloseButton" ignore="true"/>

<%-- If set to true, the bookmarks and link button will be rendered (default: true) --%>
<tiles:useAttribute name="showBookmarkAndPrint" ignore="true"/>

<%-- If set to true, seals (for information systems) will be rendered (default: false) --%>
<tiles:useAttribute name="showSealButtons" ignore="true"/>

<%-- If set to true, a button for creating a new release is rendered (default: false) --%>
<tiles:useAttribute name="showNewReleaseButton" ignore="true" />

<%-- If set to true, a button for copying the currently shown instance is rendered (default: false) --%>
<tiles:useAttribute name="showBuildingBlockCopyButton" ignore="true"/>

<%-- If set to true, the close button will be rendered (default: true) --%>
<tiles:useAttribute name="showDeleteButton" ignore="true"/>

<%-- attributes for subscription (if not available, subscriptions will not be rendered) --%>
<tiles:useAttribute name="service_class" ignore="true"/>
<tiles:useAttribute name="subscribed_element" ignore="true"/>
<tiles:useAttribute name="element_id" ignore="true"/>

<tiles:useAttribute name="userHasFuncPermission" ignore="true" />

<%-- set default values --%>
<c:if test="${empty showCloseButton}">
	<c:set var="showCloseButton" value="true" />
</c:if>
<c:if test="${empty showBookmarkAndPrint}">
	<c:set var="showBookmarkAndPrint" value="true" />
</c:if>
<c:if test="${empty showSealButtons}">
	<c:set var="showSealButtons" value="false" />
</c:if>
<c:if test="${empty showNewReleaseButton}">
	<c:set var="showNewReleaseButton" value="false" />
</c:if>
<c:if test="${empty showBuildingBlockCopyButton}">
	<c:set var="showBuildingBlockCopyButton" value="false" />
</c:if>
<c:if test="${empty showDeleteButton}">
	<c:set var="showDeleteButton" value="true" />
</c:if>
<c:if test="${empty userHasFuncPermission}">
	<c:set var="userHasFuncPermission" value="true"/>
</c:if>

<%-- The virtual element is never deletable --%>
<c:if test="${virtualElementSelected}">
	<c:set var="deleteButtonEnabled" value="false" />
</c:if>

<%-- The virtual element can never be copied --%>
<c:if test="${not virtualElementSelected}">
	<c:set var="copyButtonEnabled" value="true" />
</c:if>

<%-- adds the shortcut keys to quickly perform actions such as new, create, save  --%>
<script type="text/javascript">
// <![CDATA[
    addTransactionBarShortcuts();
    addMiscShortcuts();
// ]]>
</script>

<%-- Warning the user about cascading deletes (valid only for hierarchical elements). --%>
<c:choose>
	<c:when
		test="${buildingBlockAffectedByDelete == 'true'}">
		<c:set var="delete_hook_script"
			value="confirmDeleteBuildingBlocks(function(){flowAction('delete')});" />
	</c:when>
	<c:otherwise>
		<c:set var="delete_hook_script"
			value="confirmDelete(function(){flowAction('delete')});" />
	</c:otherwise>
</c:choose>


<%-- Warning the user about attributes that are still used in BBs --%>
<c:if
	test="${not empty require_attribute_type_delete_confirmation && require_attribute_type_delete_confirmation == true}">
	<c:set var="delete_hook_script"
		value="confirmDeleteAttribute(listOfSubElementsChildren, function(){flowAction('delete');})" />
	<script type="text/javascript">
			var listOfSubElementsChildren = new Array();

			<c:forEach var="typeOfBuildingBlock" items="${memBean.componentModel.buildingBlockTypeModel.connectedElements}">
			  listOfSubElementsChildren.push('<fmt:message  key="${typeOfBuildingBlock.name}" />');
			</c:forEach>

		function confirmDeleteAttribute(list, callback){
		    if(list.length > 0) {
			   var buildingBlocks = list.join("<br />"+"- "); 
			   showConfirmDialog('<fmt:message key="global.confirmDelete"/>',
					   '<fmt:message key="attribute.delete1"/> <br />- ' + buildingBlocks + '<br /> <br />' + '<fmt:message key="attribute.delete2"/> ',
					   callback);
		    } else {
		    	callback();
		    }
		}
	</script>
</c:if>

<%-- Check for write permissions for the current element. The 'ignoreWritePermission' attribute
	 must ONLY be set in the surrounding jsp, if the current element has no InstancePermissions e.g. User-Management.
	 If the current element has Instance Permissions (every Building Block), the ignoreWritePermission attribute accessed from 
	 this page should not be set at all, so that the second check retrieves the permissions that are relevant for building 
	 blocks --%>
<c:choose>
	<c:when test="${ignoreWritePermission || userContext.perms.userIsAdministrator}">
		<c:set var="updatePermissionType" value="true" />
		<c:set var="createPermissionType" value="true" />
		<c:set var="deletePermissionType" value="true" />
		<c:set var="writePermissionInstance" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="updatePermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeUpdatePermission(${memBean.componentModel.managedType.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<c:set var="createPermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeCreatePermission(${memBean.componentModel.managedType.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<c:set var="deletePermissionType">
			<itera:write name="userContext"
				property="perms.userHasBbTypeDeletePermission(${memBean.componentModel.managedType.typeOfBuildingBlock.value})" escapeXml="false" />
		</c:set>
		<itera:checkBbInstancePermission2 name="memBean"
			property="componentModel.owningUserEntityModel.connectedElements"
			result="writePermissionInstance" userContext="userContext" />
		<c:set var="imminentLockOut" value="${memBean.componentModel.owningUserEntityModel.imminentLockOut}" />
	</c:otherwise>
</c:choose>

<%-- display helptext if no bbUpdatePermission was granted --%>
<c:set var="alertNoBbUpdatePermission" value="${!updatePermissionType && componentMode != 'CREATE'}" />

<%-- display helptext if no bbCreatePermission was granted --%>
<c:set var="alertNoBbCreatePermission" value="${!createPermissionType}" />

<%-- display helptext if no bbDeletePermission was granted --%>
<c:set var="alertNoBbDeletePermission" value="${!deletePermissionType  && componentMode != 'CREATE'}" />

<c:if test="${alertNoBbUpdatePermission || alertNoBbCreatePermission || alertNoBbDeletePermission}">
	<div class="alert">
		<a class="close" data-dismiss="alert">×</a>
		<c:if test="${alertNoBbUpdatePermission}">
			<p>
				<fmt:message key="messages.noBbUpdatePermission" />
			</p>
		</c:if>
		<c:if test="${alertNoBbCreatePermission}">
			<p>
				<fmt:message key="messages.noBbCreatePermission" />
			</p>
		</c:if>
		<c:if test="${alertNoBbDeletePermission}">
			<p>
				<fmt:message key="messages.noBbDeletePermission" />
			</p>
		</c:if>
	</div>
</c:if>

<%-- if no writePermissionInstance was granted, but at least one of the write PermissionTypes was granted, display more specific helptext --%>
<c:if test="${(updatePermissionType || createPermissionType || deletePermissionType) && !writePermissionInstance && !imminentLockOut}">
	<div class="alert">
		<a class="close" data-dismiss="alert">×</a>
		<fmt:message key="messages.noBbInstanceWritePermission" />
	</div>
</c:if>

<%-- grant final update-, create-, delete-permissions, if the corresponding PermissionType is granted and either writePermissionInstance is granted
 or lack of writePermissionInstance is caused by changes in this edit-session causing imminentLockOut --%>
<c:set var="updatePermission" value="${updatePermissionType && (writePermissionInstance || imminentLockOut)}" />
<c:set var="createPermission" value="${createPermissionType && (writePermissionInstance || imminentLockOut)}" />
<c:set var="deletePermission" value="${deletePermissionType && (writePermissionInstance || imminentLockOut)}" />

<c:set var="subscribePermission" value="${userContext.perms.userHasFuncPermSubscription}"/>
<c:set var="showSubscribersPermission" value="${userContext.perms.userHasFuncPermShowSubscribers}" scope="request" />
	

<%-- ############### TRANSACTION BAR ############### --%>

<div id="transactionbar" class="pull-right">
	<div class="btn-group">
		
		<%-- ### View-Mode ### --%>
		<c:if test="${componentMode == 'READ'}">
		
			<%-- EDIT --%>
			<c:if test="${updatePermission}">
				<a id="transactionEdit" class="btn" href="#"
					onclick="flowAction('edit');" >
					<i class="icon-pencil"></i>
					<fmt:message key="button.edit" />
				</a>
			</c:if>
			
			<%-- SEAL --%>
			<c:if test="${showSealButtons}">
				<c:set var="sealState" value="${memBean.componentModel.sealModel.currentAsString}" /> 
				<c:choose>
					<c:when test="${sealState == 'seal.valid'}">
						<a data-toggle="modal" class="btn" href="#SealContainer" >
							<i class="icon-check"></i>
							<fmt:message key="seal"/>&nbsp;<fmt:message key="seal.valid"/>
						</a>
					</c:when>
					<c:when test="${sealState == 'seal.outdated'}">
						<a data-toggle="modal" class="btn" href="#SealContainer" >
							<i class="icon-ban-circle"></i>
							<fmt:message key="seal"/>&nbsp;<fmt:message key="seal.outdated"/>
						</a>
					</c:when>
					<c:when test="${sealState == 'seal.invalid'}">
						<a data-toggle="modal" class="btn" href="#SealContainer" >
							<i class="icon-ban-circle"></i>
							<fmt:message key="seal"/>&nbsp;<fmt:message key="seal.invalid"/>
						</a>
					</c:when>
					<c:when test="${sealState == 'seal.notavailable'}">
						<a href="#" class="btn" >
							<i class="icon-ban-circle"></i>
							<fmt:message key="seal"/>&nbsp;<fmt:message key="seal.notavailable"/>
						</a>
					</c:when>
				</c:choose>		
			</c:if>
			
			<%-- dropdown menu --%>
			<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
				<i class="icon-cog"></i>
				<fmt:message key="menu.header.more" />&nbsp;<span class="caret"></span>
			</a>
			<ul class="dropdown-menu dropdown-right">
			
				<%-- Bookmark and Print --%>
			  	<c:if test="${showBookmarkAndPrint}">
					<tiles:insertTemplate template="/jsp/common/TransactionBarSmall.jsp" />
			  	</c:if>
			  	
			  	<%-- Refresh --%>
				<li>
					<a id="transactionRefresh" href="#"
						onclick="flowAction('refresh');" >
						<i class="icon-refresh"></i>
						<fmt:message key="button.refresh" />
					</a>
				</li>
				
			  	<%-- Seals --%>
				<c:if test="${showSealButtons}">
					<li class="divider"></li>
			   		<tiles:insertTemplate template="/jsp/InformationSystem/Seal.jsp">
			   			<tiles:putAttribute name="path_to_componentModel" value="componentModel.sealModel" />
			   		</tiles:insertTemplate>
			    </c:if>
			
				<%-- Subscriptions --%>
				<%-- FIXME (see Bug #ITERAPLAN-669): Subscribe shouldn't be dependent on the update permission, but it currently is, 
				     as AbstractBuildingBlockService.subscribe() performs a saveOrUpdate() on the element. This current workaround 
				     prevents the button to be shown to the user, but it should really be fixed in the business logic. --%>
				<c:if test="${not empty subscribed_element and ((subscribePermission) || showSubscribersPermission)}">
					<tiles:insertTemplate template="/javascript/SubscriptionFunctions.js-block.jsp">
						<tiles:putAttribute name="service_class" value="${service_class}" />
						<tiles:putAttribute name="subscribed_element" value="${subscribed_element}" />
						<tiles:putAttribute name="id" value="${element_id}" />
				  	</tiles:insertTemplate>

				  	<li class="divider"></li>
				  	<c:if test="${subscribePermission}">
						<li>
							<a href="javascript:doSubscribe();">
								<i class="icon-envelope"></i>
								<span class="subscribe_button"><fmt:message key="global.subscribe"/></span>
							</a>
						</li>
					</c:if>
					<c:if test="${showSubscribersPermission}">
						<li>
							<a id="subscribed_users_count" href="javascript:showSubscribedUsers();">
								<i class="icon-user"></i>
								<fmt:message key="global.subscribed.users.show" />&nbsp;<span class="subscribercount">(0)</span>  <%-- will be updated by JavaScript after page has loaded completely, so a static value is okay --%>
							</a>
						</li>
					</c:if>
				</c:if>
			  	
			  	<c:if test="${userHasFuncPermission}">
			  	
				  	<c:if test="${createPermission && (showNewReleaseButton || showBuildingBlockCopyButton) || showDeleteButton && deletePermission}">
						<li class="divider"></li>
				  	</c:if>
			  	
					<%-- NEW RELEASE --%>
					<c:if test="${showNewReleaseButton && createPermission}">
						<li>
							<a id="transactionNewRelease" href="#"
								onclick="flowActionWithID('createRel',${id});" >
								<i class="icon-play-circle"></i>
								<fmt:message key="button.newRelease" />
							</a>
						</li>
					</c:if>
					
					<%-- COPY --%>
					<c:if test="${showBuildingBlockCopyButton && createPermission}">
						<li>
							<a id="transactionCopyBuildingBlock" href="#"
								onclick="flowActionWithID('copyBB',${id});" >
								<i class="icon-share"></i>
								<fmt:message key="button.copy" />
							</a>
						</li>
					</c:if>
					
					<%-- DELETE --%>
					<c:if test="${showDeleteButton && deletePermission}">
						<li>
							<a id="transactionDelete" href="#"
								onclick="<c:out value="${delete_hook_script}" />" >
								<i class="icon-trash"></i>
								<fmt:message key="button.delete" />
							</a>
						</li>
					</c:if>
				</c:if>
			</ul>
			
			<%-- CLOSE --%>
			<c:if test="${showCloseButton}">
				<a id="transactionClose" class="btn" href="#"
					onclick="flowAction('close');" >
					<i class="icon-remove"></i>
					<fmt:message key="button.close" />
				</a>
		  	</c:if>
		</c:if>
		
		<%-- ### Edit-Mode ### --%>
		<c:if test="${componentMode == 'EDIT' || componentMode == 'CREATE'}">
		
			<%-- SAVE --%>
			<a id="transactionSave" class="btn btn-primary" href="#"
				onclick="flowAction('save');" >
				<fmt:message key="button.save" />
			</a>
			
			<%-- CANCEL --%>
			<a id="transactionCancel" class="btn" href="#"
				onclick="msgOkCancel(function(){flowAction('cancel');});" >
				<fmt:message key="button.cancel" />
			</a>
		</c:if>
		
	</div>
</div>