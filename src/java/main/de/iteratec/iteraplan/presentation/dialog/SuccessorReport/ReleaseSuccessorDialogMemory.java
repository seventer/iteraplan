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
package de.iteratec.iteraplan.presentation.dialog.SuccessorReport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO;
import de.iteratec.iteraplan.presentation.memory.ExcelDialogMemory;


public class ReleaseSuccessorDialogMemory extends ExcelDialogMemory {

  private static final long                              serialVersionUID   = 8927349987897987L;

  /** The ID of the user-selected ISR. */
  private Integer                                        selectedIsrId;

  /** The ID of the user-selected TCR. */
  private Integer                                        selectedTcrId;

  /** The DTO containing ISR successor data for the HTML result format. */
  private ReleaseSuccessorDTO<InformationSystemRelease>  isrSuccessorDTO    = null;

  /** The DTO containing TCR successor data for the HTML result format. */
  private ReleaseSuccessorDTO<TechnicalComponentRelease> tcrSuccessorDTO    = null;

  public static final String                             CLICKED_BUTTON_ISR = "button.requestReport.ISR";
  public static final String                             CLICKED_BUTTON_TCR = "button.requestReport.TCR";
  private String                                         clickedButton;

  /**
   * Constructor.
   */
  public ReleaseSuccessorDialogMemory() {
    super();
    isrSuccessorDTO = new ReleaseSuccessorDTO<InformationSystemRelease>();
    tcrSuccessorDTO = new ReleaseSuccessorDTO<TechnicalComponentRelease>();
  }

  /**
   * Constructor.
   * 
   * @param isrDto
   *          The DTO for storing ISR successor query configuration and results
   * @param tcrDto
   *          The DTO for storing TCR successor query configuration and results
   */
  public ReleaseSuccessorDialogMemory(ReleaseSuccessorDTO<InformationSystemRelease> isrDto,
      ReleaseSuccessorDTO<TechnicalComponentRelease> tcrDto) {
    setIsrSuccessorDTO(isrDto);
    setTcrSuccessorDTO(tcrDto);
  }

  /**
   * @return Returns the isrSuccessorDTO.
   */
  public ReleaseSuccessorDTO<InformationSystemRelease> getIsrSuccessorDTO() {
    return isrSuccessorDTO;
  }

  /**
   * @param dto
   *          The isrSuccessorDTO to set.
   */
  public final void setIsrSuccessorDTO(ReleaseSuccessorDTO<InformationSystemRelease> dto) {
    this.isrSuccessorDTO = dto;
    if (dto != null && dto.getAvailableReleases() != null) {
      List<InformationSystemRelease> availableIsr = dto.getAvailableReleases();
      if (availableIsr.size() > 0) {
        InformationSystemRelease firstIsr = availableIsr.get(0);
        selectedIsrId = firstIsr.getId();
      }
    }
  }

  public ReleaseSuccessorDTO<TechnicalComponentRelease> getTcrSuccessorDTO() {
    return tcrSuccessorDTO;
  }

  public final void setTcrSuccessorDTO(ReleaseSuccessorDTO<TechnicalComponentRelease> dto) {
    this.tcrSuccessorDTO = dto;
    if (dto != null && dto.getAvailableReleases() != null) {
      List<TechnicalComponentRelease> availableTcr = dto.getAvailableReleases();
      if (availableTcr.size() > 0) {
        TechnicalComponentRelease firstTcr = availableTcr.get(0);
        selectedTcrId = firstTcr.getId();
      }
    }
  }

  /**
   * @return Returns the selectedIsrId.
   */
  public Integer getSelectedIsrId() {
    return selectedIsrId;
  }

  /**
   * @param selectedIsrId
   *          The selectedIsrId to set.
   */
  public void setSelectedIsrId(Integer selectedIsrId) {
    this.selectedIsrId = selectedIsrId;
  }

  public Integer getSelectedTcrId() {
    return selectedTcrId;
  }

  public void setSelectedTcrId(Integer selectedTcrId) {
    this.selectedTcrId = selectedTcrId;
  }

  /**
   * @return Returns a List with all result formats available for the Isr successor report page.
   */
  public List<ExportOption> getAvailableResultFormats() {
    List<ExportOption> results = new ArrayList<ExportOption>();
    results.add(new ExportOption(Constants.REPORTS_EXPORT_HTML, true));
    results.add(new ExportOption(Constants.REPORTS_EXPORT_EXCEL_2007, true));
    results.add(new ExportOption(Constants.REPORTS_EXPORT_EXCEL_2003, true));
    return results;
  }

  public String getClickedButton() {
    return clickedButton;
  }

  public void setClickedButton(String clickedButton) {
    this.clickedButton = clickedButton;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
      .append(selectedIsrId)
      .append(selectedTcrId)
      .append(clickedButton)
      .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ReleaseSuccessorDialogMemory other = (ReleaseSuccessorDialogMemory) obj;
    
    return new EqualsBuilder().appendSuper(super.equals(obj))
      .append(selectedIsrId, other.selectedIsrId)
      .append(selectedTcrId, other.selectedTcrId)
      .append(clickedButton, other.clickedButton)
      .isEquals();
  }

}
