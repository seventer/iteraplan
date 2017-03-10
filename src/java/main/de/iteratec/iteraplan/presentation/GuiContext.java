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
package de.iteratec.iteraplan.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.GuiTableState;
import de.iteratec.iteraplan.presentation.flow.FlowEntry;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * This class contains the current state of the user interface:
 * <ul>
 * <li>The currently active dialog.</li>
 * <li>Dialogs which are currently being edited.</li>
 * <li>The page position for each dialog.</li>
 * </ul>
 * Furthermore this class contains helper methods for rendering zebra-style tables.
 * <p>
 * Like the {@link de.iteratec.iteraplan.common.UserContext} class, this class is stored in the
 * {@link javax.servlet.http.HttpSession} and set into a session variable by the
 * {@link de.iteratec.iteraplan.presentation.ContextFilter} so that it may be used
 * by all classes. The instance of the current thread (request) may be retrieved by calling the
 * {@link #getCurrentGuiContext()} method.
 */
public final class GuiContext implements Serializable {

  /** Serialization version. */
  private static final long                              serialVersionUID       = -2783771609581763496L;
  /** CSS styles for creating alternating row colors in a table. */
  private static final String                            ROW_COLOR_DARK         = "rowColorDark";
  private static final String                            ROW_COLOR_BRIGHT       = "rowColorBright";

  /** GuiContext for the current Thread (i.e. Request) */
  private static final ThreadLocal<GuiContext>           CURRENT_GUICONTEXT     = new ThreadLocal<GuiContext>() {

                                                                                  @Override
                                                                                  protected GuiContext initialValue() {
                                                                                    return new GuiContext();
                                                                                  }
                                                                                };

  private static final Set<String>                       DIALOG_MENU_EADATA     = ImmutableSet.of(Dialog.OVERVIEW.getDialogName(),
                                                                                    Dialog.SEARCH.getDialogName(),
                                                                                    Dialog.BUSINESS_DOMAIN.getDialogName(),
                                                                                    Dialog.BUSINESS_PROCESS.getDialogName(),
                                                                                    Dialog.BUSINESS_UNIT.getDialogName(),
                                                                                    Dialog.PRODUCT.getDialogName(),
                                                                                    Dialog.BUSINESS_FUNCTION.getDialogName(),
                                                                                    Dialog.BUSINESS_OBJECT.getDialogName(),
                                                                                    Dialog.BUSINESS_MAPPING.getDialogName(),
                                                                                    Dialog.INFORMATION_SYSTEM_DOMAIN.getDialogName(),
                                                                                    Dialog.INFORMATION_SYSTEM.getDialogName(),
                                                                                    Dialog.INTERFACE.getDialogName(),
                                                                                    Dialog.ARCHITECHTURAL_DOMAIN.getDialogName(),
                                                                                    Dialog.TECHNICAL_COMPONENT.getDialogName(),
                                                                                    Dialog.INFRASTRUCTURE_ELEMENT.getDialogName(),
                                                                                    Dialog.PROJECT.getDialogName());

  private static final Set<String>                       DIALOG_MENU_REPORTS    = ImmutableSet.of(Dialog.TABULAR_REPORTING.getDialogName(),
                                                                                    Dialog.ITERAQL.getDialogName(),
                                                                                    Dialog.SUCCESSOR_REPORTS.getDialogName(),
                                                                                    Dialog.SUBSCRIPTION.getDialogName());

  private static final Set<String>                       DIALOG_MENU_VISUAL     = ImmutableSet.of(Dialog.DASHBOARD.getDialogName(),
                                                                                    Dialog.GRAPHICAL_REPORTING.getDialogName(),
                                                                                    Dialog.SAVED_QUERIES.getDialogName(),
                                                                                    Dialog.CUSTOM_DASHBOARD_INSTANCES_OVERVIEW.getDialogName(),
                                                                                    Dialog.CUSTOM_DASHBOARD_INSTANCE.getDialogName());

  private static final Set<String>                       DIALOG_MENU_MASS       = ImmutableSet
                                                                                    .of(Dialog.MASS_UPDATE.getDialogName(),
                                                                                        Dialog.XMISERIALIZATION.getDialogName(),
                                                                                        Dialog.XMIDESERIALIZATION.getDialogName(),
                                                                                        Dialog.EXCELIMPORT.getDialogName(),
                                                                                        Dialog.IMPORT.getDialogName());

  private static final Set<String>                       DIALOG_MENU_GOVERNANCE = ImmutableSet.of(Dialog.USER.getDialogName(),
                                                                                    Dialog.USER_GROUP.getDialogName(), Dialog.ROLE.getDialogName(),
                                                                                    Dialog.OBJECT_RELATED_PERMISSION.getDialogName(),
                                                                                    Dialog.SUPPORTING_QUERY.getDialogName(),
                                                                                    Dialog.CONSISTENCY_CHECK.getDialogName());

  private static final Set<String>                       DIALOG_MENU_ADMIN      = ImmutableSet.of(Dialog.CONFIGURATION.getDialogName(),
                                                                                    Dialog.ATTRIBUTE_TYPE_GROUP.getDialogName(),
                                                                                    Dialog.ATTRIBUTE_TYPE.getDialogName(),
                                                                                    Dialog.DATE_INTERVAL.getDialogName(),
                                                                                    Dialog.TEMPLATES.getDialogName());
  /** A property to hide the ContextActionMenu **/

  /** A Map containing all dialogs that are currently edited by the user. */
  private Map<String, Boolean>                           editedDialogs          = new HashMap<String, Boolean>();

  /** The currently active dialog. (May be a Dialog, or a Flow-Key) */
  private String                                         activeDialog           = null;

  /** The name of the currently active dialog. Must be a dialogName defined in {@link Dialog} */
  private String                                         activeDialogName       = null;

  /** Stores the current row color (CSS style) */
  private String                                         rowColor;

  /** Stores the current second level row color (CSS style) */
  private String                                         secondLevelRowColor;

  /** Stores the state, if each menu accordion is expanded or not
   *  In order: context actions, open elements, watched elements.
   *  Initialized in UserContextInitializationServiceImpl
   */
  private Boolean[]                                      expandedMenuStatus     = new Boolean[3];

  /** Maps dialogMemory to menu. Must be stored in session. */
  private final Map<String, DialogMemory>                dialogMemoryMap        = new HashMap<String, DialogMemory>();

  /** Dialog names map to all opened sessions of the corresponding flow. */
  private final Multimap<String, FlowEntry>              flowEntries            = ArrayListMultimap.create();

  /**
   * Maps selected tab status to every dialog page. First key is activeDialogName, second one
   * activeDialog
   */
  private Map<String, Map<String, String>>               selectedTabMap         = new HashMap<String, Map<String, String>>();

  private final Map<String, SetMultimap<String, String>> openedATGMap           = new HashMap<String, SetMultimap<String, String>>();

  private final Map<String, GuiTableState>               tableStateMap          = new HashMap<String, GuiTableState>();

  private static final ThreadLocal<HttpServletRequest>   CURRENT_REQUEST        = new ThreadLocal<HttpServletRequest>();

  private boolean                                        timeseriesEnabled      = IteraplanProperties.getProperties().propertyIsSetToTrue(
                                                                                    IteraplanProperties.TIMESERIES_ENABLED);

  /**
   * Constructor
   */
  public GuiContext() {
    super();
  }

  /**
   * Returns the GuiContext instance for the current thread.
   * 
   * @return The current GuiContext.
   */
  public static GuiContext getCurrentGuiContext() {
    return CURRENT_GUICONTEXT.get();
  }

  /**
   * Set the GuiContext instance for the current thread.
   * 
   * @param guiContext
   *          The GuiContext instance to set.
   */
  public static void setCurrentGuiContext(GuiContext guiContext) {
    CURRENT_GUICONTEXT.set(guiContext);
  }

  /**
   * Detaches/ removes the current GuiContext instance from this thread.
   * After invoking this method, {@link #getCurrentGuiContext()} will return null.
   */
  public static void detachCurrentGuiContext() {
    CURRENT_GUICONTEXT.remove();
  }

  /**
   * @return currentRequest the current HTTP Servlet Request, bound to this thread
   */
  public static HttpServletRequest getCurrentRequest() {
    return CURRENT_REQUEST.get();
  }

  public static void setCurrentRequest(HttpServletRequest currentRequest) {
    CURRENT_REQUEST.set(currentRequest);
  }

  /**
   * Detaches/ removes the current HTTP Servlet Request from this thread.
   * After invoking this method, {@link #getCurrentRequest()} will return null.
   */
  public static void detachCurrentRequest() {
    CURRENT_REQUEST.remove();
  }

  /**
   * Checks if at least one dialog is currently in edit mode.
   * 
   * @return true if at least one dialog is in edit mode.
   */
  public boolean isAnythingEdited() {
    for (Iterator<Boolean> iter = editedDialogs.values().iterator(); iter.hasNext();) {
      Boolean edited = iter.next();
      if ((edited != null) && (Boolean.TRUE.equals(edited))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a map which maps from the name of a dialog to a boolean value. The boolean value
   * indicates if the dialog is currently in edit mode.
   * 
   * @return Returns an edit-mode map for dialogs.
   */
  public Map<String, Boolean> getEditedDialogs() {
    return editedDialogs;
  }

  /**
   * Set an edit-mode map for dialogs.
   * 
   * @param editedDialogs
   *          The dialog mode map.
   * @see #getEditedDialogs()
   */
  public void setEditedDialogs(Map<String, Boolean> editedDialogs) {
    this.editedDialogs = editedDialogs;
    if (this.editedDialogs == null) {
      this.editedDialogs = new HashMap<String, Boolean>();
    }
  }

  /**
   * Returns the currently active dialog. This might be a dialogName from {@link Dialog}, or the
   * FlowId.
   * 
   * @return name of a dialog, or its flowId
   */
  public String getActiveDialog() {
    return activeDialog;
  }

  /**
   * Sets the currently active dialog. This might be a dialogName from {@link Dialog}, or the
   * FlowId.
   * 
   * @param activeDialog
   *          The name of a dialog.
   */
  public void setActiveDialog(String activeDialog) {
    this.activeDialog = activeDialog;
  }

  /**
   * Sets the currently active dialog name. Must be a dialogName defined in {@link Dialog}
   * 
   * @param activeDialogName
   */
  public void setActiveDialogName(String activeDialogName) {
    this.activeDialogName = activeDialogName;
  }

  /**
   * Returns the currently active dialog name. Must be a dialogName defined in {@link Dialog}
   * 
   * @return name of the dialog
   */
  public String getActiveDialogName() {
    return activeDialogName;
  }

  /**
   * @return timeseriesEnabled the timeseriesEnabled
   */
  public boolean isTimeseriesEnabled() {
    return timeseriesEnabled;
  }

  public void setTimeseriesEnabled(boolean timeseriesEnabled) {
    this.timeseriesEnabled = timeseriesEnabled;
  }

  /**
   * Returns alternating the value of {@link #ROW_COLOR_BRIGHT} and the value of
   * {@link #ROW_COLOR_DARK}. Used as CSS styles for rendering zebra-style tables.
   * 
   * @return either {@link #ROW_COLOR_BRIGHT} or {@link #ROW_COLOR_DARK}.
   */
  public String getRowColor() {
    if (ROW_COLOR_BRIGHT.equals(this.rowColor)) {
      this.rowColor = ROW_COLOR_DARK;
    }
    else {
      this.rowColor = ROW_COLOR_BRIGHT;
    }
    return this.rowColor;
  }

  /**
   * Set the current row color. Must be either {@link #ROW_COLOR_BRIGHT} or {@link #ROW_COLOR_DARK}.
   * This color determines which css style is returned first when calling {@link #getRowColor()}.
   * 
   * @param rowColor
   *          the current row color to set.
   */
  public void setRowColor(String rowColor) {
    this.rowColor = rowColor;
  }

  /**
   * Works the same as {@link #getRowColor()}, but works independently. Can be used for rendering a
   * further table.
   * 
   * @return Switches the current second level row color and returns it.
   * @see #getRowColor()
   */
  public String getSecondLevelRowColor() {
    if (ROW_COLOR_DARK.equals(secondLevelRowColor)) {
      secondLevelRowColor = ROW_COLOR_BRIGHT;
    }
    else {
      secondLevelRowColor = ROW_COLOR_DARK;
    }
    return secondLevelRowColor;
  }

  /**
   * Sets the second row color. Determines which color is returned first by
   * {@link #setSecondLevelRowColor(String)}.
   * 
   * @param rowColor
   *          The second row color to set.
   * @see #setRowColor(String)
   */
  public void setSecondLevelRowColor(String rowColor) {
    secondLevelRowColor = rowColor;
  }

  /**
   * Sets a dialogMemory for a corresponding dialog. An already existing Memory for this dialog is
   * overwritten.
   * 
   * @param dialog
   *          Key for a dialog.
   * @param dialogMemory
   *          the dialogMemory to add
   */
  public void setDialogMemory(String dialog, DialogMemory dialogMemory) {
    this.dialogMemoryMap.put(dialog, dialogMemory);
  }

  /**
   * Gets the dialogMemory for a dialog.
   * 
   * @param dialog
   *          Key for a dialog.
   * @return dialogMemory
   */
  public DialogMemory getDialogMemory(String dialog) {
    return this.dialogMemoryMap.get(dialog);
  }

  public boolean hasDialogMemory(String dialog) {
    return this.dialogMemoryMap.containsKey(dialog);
  }

  /**
   * Returns an Array with the status of each section of the menu, if expanded or not
   * 
   * @return an Array with the menu status of each section, if expanded or not
   */
  public Boolean[] getExpandedMenuStatus() {
    return expandedMenuStatus.clone();
  }

  /**
   * Sets an Array with the status of each section of the menu, if expanded or not
   */
  public void setExpandedMenuStatus(Boolean[] expandedMenuStatus) {
    this.expandedMenuStatus = expandedMenuStatus.clone();
  }

  /**
   * Store flow instance for a given flow key. First check, if the current flow is already stored
   * and only the step is different.
   * 
   * @param flowId
   *          Id of the flow definition
   * @param key
   *          Current key of the flow instance.
   * @param label
   *          Text to display in menu
   * @param entityId
   *          ID of the entity that is worked on in the current flow
   */
  public void addFlowEntry(String flowId, String key, String label, Integer entityId) {
    String dialogName = Dialog.getDialogNameForFlowId(flowId);

    removeFlowEntry(dialogName, key);
    flowEntries.put(dialogName, new FlowEntry(flowId, key, label, entityId));
    setActiveDialogName(dialogName);
    setActiveDialog(key);
  }

  /**
   * For each request the flow must be updated.
   * 
   * @param flowId
   *          Id of the flow definition
   * @param newFlowLabel
   *          The new label to set for this flow. If null, the label wont be changed.
   * @param newSessionKey
   *          Session key to update. May be {@code null} if the flow is still initializing, in which case nothing will be done.
   * @param edit
   *          Is flow in edit mode
   */
  public void updateFlowEntry(String flowId, String newFlowLabel, String newSessionKey, Boolean edit, Integer newEntityId) {
    String dialogName = Dialog.getDialogNameForFlowId(flowId);
    updateFlowEntryByDialogName(dialogName, newFlowLabel, newSessionKey, edit, newEntityId);
  }

  /**
   * For each request the flow must be updated.
   * 
   * @param dialogName
   *          Name of the dialog that the flow definition belongs to.
   * @param newFlowLabel
   *          The new label to set for this flow. If null, the label wont be changed.
   * @param newSessionKey
   *          Session key to update. May be {@code null} if the flow is still initializing, in which case nothing will be done.
   * @param edit
   *          Is flow in edit mode
   * @param newEntityId
   *          the new Id of the Entity that this flow represents, or null if no id exists or it
   *          wasn't changed
   */
  public void updateFlowEntryByDialogName(String dialogName, String newFlowLabel, String newSessionKey, Boolean edit, Integer newEntityId) {
    if (newSessionKey == null) {
      // we don't have a flow session ID yet, so just skip -- we'll come back later with a proper ID!
      return;
    }

    Collection<FlowEntry> activeFlowsForName = getAllFlowsForDialogName(dialogName);

    for (FlowEntry flow : activeFlowsForName) {
      if (flow.isStepOfSameFlow(newSessionKey)) {
        flow.setKey(newSessionKey);
        this.setActiveDialogName(dialogName);
        this.setActiveDialog(newSessionKey);
        if (null != edit) {
          flow.setEdit(edit.booleanValue());
        }
        if (null != newFlowLabel) {
          flow.setLabel(newFlowLabel);
        }
        if (null != newEntityId) {
          flow.setEntityId(newEntityId);
        }
        break;
      }
    }
  }

  /**
   * Remove flow instance for a given flow key.
   * 
   * @param flowId
   *          Id of the flow definition
   * @param removeFlowKey
   *          Current Session key to be removed
   */
  public void removeFlowEntry(String flowId, String removeFlowKey) {
    String dialogName = Dialog.getDialogNameForFlowId(flowId);
    removeFlowEntryByDialogName(dialogName, removeFlowKey);
  }

  /**
   * Remove flow instance for a given flow key.
   * 
   * @param dialogName
   *          Name of the dialog that the flow definition belongs to.
   * @param removeFlowKey
   *          Current Session key to be removed
   */
  public void removeFlowEntryByDialogName(String dialogName, String removeFlowKey) {
    Collection<FlowEntry> activeFlowsForName = getAllFlowsForDialogName(dialogName);
    if (activeFlowsForName.size() > 0) {

      for (FlowEntry flow : activeFlowsForName) {
        if (flow.isStepOfSameFlow(removeFlowKey)) {
          flowEntries.remove(dialogName, flow);
          break;
        }
      }
    }
  }

  /**
   * Returns all flow instances for a given flow key
   * 
   * @param dialogName
   *          Name of the dialog that the flow definition belongs to.
   * @return List of all current flow instances. If there is no entry a empty collection will be
   *         returned.
   */
  public Collection<FlowEntry> getAllFlowsForDialogName(String dialogName) {
    return flowEntries.get(dialogName);
  }

  /**
   * Returns all flow instance descriptors for a given <code>flowId</code> and (optionally)
   * <code>entityID</code>. A pair of these two values is assumed to identify a flow instance
   * uniquely. <code>entityID</code> may also be null.
   * 
   * @param flowId
   *          Flow ID, as used by Spring web flow
   * @param entityId
   *          ID of the entity (e.g. a building block) that is loaded in the desired flow. If set to
   *          null, all flow instances with the given flowId will be returned.
   * @return A list of flow instance descriptors that satisfy given constraints.
   */
  public List<FlowEntry> getAllFlowsWithIds(String flowId, Integer entityId) {
    ArrayList<FlowEntry> matchedFlows = new ArrayList<FlowEntry>();
    for (java.util.Map.Entry<String, FlowEntry> entry : flowEntries.entries()) {
      FlowEntry flowEntry = entry.getValue();
      boolean flowIdEquals = flowId.equals(flowEntry.getFlowId());
      boolean entityIdEquals = (entityId == null) || (entityId.equals(flowEntry.getEntityId()));

      if (flowIdEquals && entityIdEquals) {
        matchedFlows.add(flowEntry);
      }
    }

    return matchedFlows;
  }

  /**
   * Function is called by jsp to get all open session entries.
   * 
   * @return Map with first entry dialogName and second entry a list of open sessions.
   */
  public Map<String, Collection<FlowEntry>> getFlowEntries() {
    return this.flowEntries.asMap();
  }

  /**
   * Returns ALL open Flows (for all Dialogs) as a Collection of FlowEntry.
   * 
   * @return Collection<FlowEntry> containing all the Flow-Entries
   */
  public Collection<FlowEntry> getAllFlows() {
    return this.flowEntries.values();
  }

  public void resetAllDialogs() {
    dialogMemoryMap.clear();
    editedDialogs.clear();
    // this.flowEntries.clear()
  }

  public Map<String, Map<String, String>> getSelectedTabMap() {
    return selectedTabMap;
  }

  public void setSelectedTabMap(Map<String, Map<String, String>> selectedTabMap) {
    this.selectedTabMap = selectedTabMap;
  }

  public String getSelectedTab() {
    Map<String, String> map = getSelectedTabMap().get(getActiveDialogName());
    if (map != null) {
      return map.get(getDialogNameForTabIdentification());
    }
    return null;
  }

  public Map<String, SetMultimap<String, String>> getOpenedATGMap() {
    return openedATGMap;
  }

  public boolean isOpenedATG(String atgName) {
    if (openedATGMap.containsKey(getActiveDialogName()) && openedATGMap.get(getActiveDialogName()).containsKey(getDialogNameForTabIdentification())) {
      return openedATGMap.get(getActiveDialogName()).get(getDialogNameForTabIdentification()).contains(atgName);
    }
    return false;
  }

  /**
   * method determines the flow number part (e.g. e2) of the flow id (e.g. e2s5) important for
   * saving selected tabs to flow dialogs independent of the current status s in the specific flow
   * ID
   * <p>
   * Note: This method has a serious dependency on Spring's way of constructing flow identifier
   * parameters. Let's hope that the Spring guys will never change that.
   * 
   * @return flow number without status information, null if it's not a flow
   */
  public String getCurrentFlowNumber() {

    // only in a flow active dialog is not the dialog name but the flow id
    if (!this.activeDialog.equalsIgnoreCase(this.activeDialogName) && this.activeDialog.startsWith("e")) {
      String[] tmp = this.activeDialog.split("s");
      return tmp[0];
    }
    else {

      return null;
    }
  }

  /**
   * @return Flow Number without status information or if it isn't a flow then the usual active
   *         dialog name
   */
  public String getDialogNameForTabIdentification() {

    String dialogName = getCurrentFlowNumber();
    if (dialogName == null) {
      dialogName = this.activeDialog;
    }
    return dialogName;

  }

  /**
   * Gets Table state for this dialog.
   * 
   * @param dialogName
   *        Name of Dialog Memory
   * @return
   *        Table state for this dialog
   */
  public GuiTableState getTableState(String dialogName) {
    if (this.tableStateMap.containsKey(dialogName)) {
      return this.tableStateMap.get(dialogName);
    }
    else {
      return null;
    }
  }

  public void storeTableState(String dialogName, GuiTableState tableState) {
    this.tableStateMap.put(dialogName, tableState);
  }

  public Collection<String> getDialogsWithOpenElements() {
    Collection<String> result = new ArrayList<String>(DIALOG_MENU_EADATA);
    result.addAll(DIALOG_MENU_GOVERNANCE);
    result.addAll(DIALOG_MENU_ADMIN);
    return result;
  }

  /**
   * Returns true, if the active dialog is the home screen.
   * @return see method description.
   */
  public boolean isHomeDialogActive() {
    return getActiveDialogName().isEmpty();
  }

  /**
   * Returns true, if the active dialog one of ea data.
   * @return see method description.
   */
  public boolean isEadataDialogActive() {
    return DIALOG_MENU_EADATA.contains(getActiveDialogName());
  }

  /**
   * Returns true, if the active dialog one of reporting.
   * @return see method description.
   */
  public boolean isReportDialogActive() {
    return DIALOG_MENU_REPORTS.contains(getActiveDialogName());
  }

  /**
   * Returns true, if the active dialog one of visualization.
   * @return see method description.
   */
  public boolean isVisualDialogActive() {
    return DIALOG_MENU_VISUAL.contains(getActiveDialogName());
  }

  /**
   * Returns true, if there is at least one open element to display (which does not belong to a BusinessMapping-dialog).
   * @return see method description.
   */
  public boolean isListableOpenElements() {
    Collection<String> listableDialogs = getDialogsWithOpenElements();
    listableDialogs.remove("BusinessMapping");

    for (String dialog : listableDialogs) {
      if (!flowEntries.get(dialog).isEmpty()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns true, if the active dialog one of mass data.
   * @return see method description.
   */
  public boolean isMassDialogActive() {
    return DIALOG_MENU_MASS.contains(getActiveDialogName());
  }

  /**
   * Returns true, if the active dialog one of governance.
   * @return see method description.
   */
  public boolean isGovernanceDialogActive() {
    return DIALOG_MENU_GOVERNANCE.contains(getActiveDialogName());
  }

  /**
   * Returns true, if the active dialog one of administration.
   * @return see method description.
   */
  public boolean isAdminDialogActive() {
    return DIALOG_MENU_ADMIN.contains(getActiveDialogName());
  }

  public Collection<BuildingBlock> getSubscribedElements() {
    Collection<BuildingBlock> result = new ArrayList<BuildingBlock>();
    result.addAll(SpringServiceFactory.getBusinessDomainService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getBusinessProcessService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getBusinessUnitService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getProductService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getBusinessFunctionService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getBusinessObjectService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getInformationSystemDomainService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getInformationSystemReleaseService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getInformationSystemInterfaceService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getArchitecturalDomainService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getTechnicalComponentReleaseService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getInfrastructureElementService().getSubscribedElements());
    result.addAll(SpringServiceFactory.getProjectService().getSubscribedElements());
    return result;
  }
}
