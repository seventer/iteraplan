	var globalDiagramType="";
	var globalDiagramVariant="";
	
	function variables(args) {
		this.baseUrl = args.baseUrl;
		this.buildingBlockType = args.buildingBlockType;
		this.displayId = args.displayId;
		this.displayLoadingId = args.displayLoadingId;
		this.urlAttribute = args.urlAttribute;
		this.buildingBlockId = args.buildingBlockId;
		this.resultFormat = args.resultFormat;
		this.selectText = args.selectText;
		this.noContentMessage = args.noContentMessage;
		this.previewHeaderElement = args.previewHeaderElement;
		this.previewLabel = args.previewLabel;
	}

	function makeGraphicPrintable() {
		//	make fast export content printable after loading, by switching its CSS class from a temporary, dummy
		//	placeholder class (notLoaded) to the final CSS class which is printable
		$('#fastExportContentHolder').attr('class', 'fastExportContent');
	}

	// Called when the Object/Img loads: Show graphic/buttons, hide Loading
	function onGraphicLoaded() {
		showDownloadButton();
		$('#' + variables.displayLoadingId).css('visibility', 'hidden');
		
		// The following isn't done in IE6, because it results in half the page being blanked-out for some reason
		if (navigator.userAgent.indexOf("MSIE 6.0") === -1) {
			// Remove the Loading Graphic, so the Preview Gaphic's vertical location is not lowered by it 
			$('#' + variables.displayLoadingId).text('');
		}

		$('#' + variables.displayId).css('visibility', 'visible');
		makeGraphicPrintable();
	}
      
	function refreshHeader(key) {
		var previewHeader = $('#' + variables.previewHeaderElement);
		previewHeader.text(variables.previewLabel+key);
	}
	
	// construct url from parameters 
	function constructUrl(bbId, savedQueryType, variantParameter, format, outputMode) {
		return variables.baseUrl + "fastexport/generate.do?id=" + bbId + "&buildingBlockType=" + variables.buildingBlockType + "&savedQueryType=" + savedQueryType + variantParameter + "&resultFormat=" + format + "&outputMode=" + outputMode;
	}
       
	function constructUrlForConfigure() {
		switch (globalDiagramType) {
			case "InformationFlow":
				return variables.baseUrl + "show/graphicalreporting/informationflowdiagram?id=" + variables.buildingBlockId + "&_eventId=ConfigureGR&bbType=" + variables.buildingBlockType;
			case "Landscape":
				return variables.baseUrl + "show/graphicalreporting/landscapediagram?id=" + variables.buildingBlockId + "&_eventId=ConfigureGR&bbType=" + variables.buildingBlockType + "&diagramVariant=" + globalDiagramVariant;
			case "Masterplan":
				return variables.baseUrl + "show/graphicalreporting/masterplandiagram?id=" + variables.buildingBlockId + "&_eventId=ConfigureGR&bbType=" + variables.buildingBlockType + "&diagramVariant=" + globalDiagramVariant;
			default:
				return "#";
		}
	}

	// retrieves the value of the result format dropdown
	function getResultFormatOptionPreviewDownload() {
		return getResultFormatOption('resultFormatDropdown');
	}
       
	function getResultFormatOptionDirectDownload() {
		return getResultFormatOption('resultFormatDropdownDownload');
	}
       
	// retrieves the value of the result format dropdown
	function getResultFormatOption(dropdownId) {
		return $('#' + dropdownId).val();
	}    
         
	// for download of preview   
	function redirectToUrlFromDisplayedGraphic() {
		// element where graphic is displayed inline
		var graphic = $('#' + variables.displayId);
		   
		// attribute 'src' or 'data' where url is stored
		var fetchedUrl = graphic.attr(variables.urlAttribute);
		   
		// change to content type for download 
		//  NOTE: IE7 (6?): downloadUrl is relative in all browsers except for these 
		//   Filter "&outputMode=inline" rather than just "inline" to properly handle eg: https://inlineskater.com:8443/iteraplan
		var downloadUrl = fetchedUrl.replace("&outputMode=inline", "&outputMode=attachment");
		   
		// set result format to the value in the format dropdown 
		downloadUrl = downloadUrl.replace(/\&resultFormat=\w+\&/, "&resultFormat=" + getResultFormatOptionPreviewDownload() + "&");
		
		// finally set href to url
		changeLocation(downloadUrl);
	}
       
    function retrieveUrlFromDisplayedGraphic() {
    	// element where graphic is displayed inline
		var graphic = $('#' + variables.displayId);
			    	   
		if (graphic != null) {
			// 	attribute 'src' or 'data' where url is stored
			var fetchedUrl = graphic.attr(variables.urlAttribute);
			    	   
			// change to content type for download 
			//  NOTE: IE7 (6?): downloadUrl is relative in all browsers except for these 
			//   Filter "&outputMode=inline" rather than just "inline" to properly handle eg: https://inlineskater.com:8443/iteraplan
			var downloadUrl = fetchedUrl.replace("&outputMode=inline", "&outputMode=attachment");
    	
			    	   
			// set result format to the value in the format dropdown 
			downloadUrl = downloadUrl.replace(/\&resultFormat=\w+\&/, "&resultFormat=" + getResultFormatOptionPreviewDownload() + "&");
			    	   
			// retrieve prefix a la 'https://localhost:8443' to build complete URL
    	    prefix = window.location.protocol + "//" + window.location.host;
    	    
			// IE6/7 already have the full URL in downloadUrl at this point, so only prepend it if it's not in there 
			if (downloadUrl.indexOf(prefix) === -1) {
				downloadUrl = prefix + downloadUrl;
    	    }   
			return downloadUrl;
    	   
		} // no image where graphic is displayed available
		else { 
			return location.href;
		}
	}
	
	// toggle visibility of result format select
	function toggleResultFormatSpan(visibility) {
		$('#resultFormatSpan').css('visibility', visibility);
	}
	       
	function toggleResultFormatSpanDownload(visibility) {
		$('#resultFormatSpanDownload').css('visibility', visibility);
	}
	
	function showDownloadButton() {
		// allow downloading the selected graphic  	
		$('#downloadFastExport').css('visibility', 'visible');
		$('#fastExportButtonBookmark').css('visibility', 'visible');
		// allow configuring the selected graphic 
		if(globalDiagramType!='NeighborhoodDiagram'){
		$('#jumpToGraphicalReportStep2').css('visibility', 'visible');
		}
		toggleResultFormatSpanDownload('visible'); 	          
	}     
		         
	function hideDownloadButton() {
		// disallow downloading the selected graphic 
		$('#downloadFastExport').css('visibility', 'hidden');
		$('#fastExportButtonBookmark').css('visibility', 'hidden');
		// disallow configuring the selected graphic 
		$('#jumpToGraphicalReportStep2').css('visibility', 'hidden');
		toggleResultFormatSpanDownload('hidden'); 
	}
		         
	function hideResultFormats() {
		toggleResultFormatSpan("hidden"); 
	} 
	           
	function showResultFormats() {
		toggleResultFormatSpan("visible"); 
	}
	 
	// abstract handler for the onClick event on menu items, sets url of corresponding graphic
	function clickAction(savedQueryType, diagramVariant) {
		// Remember these for the Configure button later
		window.globalDiagramType=savedQueryType;
		window.globalDiagramVariant=diagramVariant; 
		         
		var variantParameter = "";
		
		// additional parameter for diagrams which have several variants   
		if (diagramVariant != null && diagramVariant != "") {
			variantParameter = "&diagramVariant=" + diagramVariant;  
		}
             
		var outputMode = "inline";

		// element where to display the diagram; one of image, object
		var graphic = $('#' + variables.displayId);
		// element where we show Loading, and potential error messages; always plain html
		var graphicLoading = $('#' + variables.displayLoadingId);
			      
		var format = variables.resultFormat;
		
		if (outputMode == "attachment") {
			format = getResultFormatOptionDirectDownload();
		}        
		  	       
		// construct url from parameters
		var urlWithParameters = constructUrl(variables.buildingBlockId, savedQueryType, variantParameter, format, outputMode); 
		
		// debugging:
		// alert ("urlAttribute " + variables.urlAttribute + "; urlWithParameters = " + urlWithParameters);
		
		if (outputMode == "attachment") {
			// download: change location to url
			changeLocation(urlWithParameters);
		}
		else {  			
		
			// dirty IE 7 hack to force it refreshing the image 
			//  Only needed since we do injection after the XHR. XHR has this built in
			if (graphic.prop('tagName') == "IMG" || graphic.prop('tagName') == "img") {
									
				// debugging:
				// alert("Achtung IE!");
				urlWithParameters = urlWithParameters + "&keepfresh=" + (new Date()).getTime() + "#";		        
			}		
			
			// XHR used to handle potential errors (such as when graphics are empty)
			// Note: Current implementation is not ideal: 
			//	1) We get the URLWithParameters, which is an SVG or PNG (if IE)
			//	2) If there are errors, we show an error message
			//	3) If no errors, we load the same URL once again, and inject it into our <object> for SVG or <img> for PNG
			
			var xhrArgs = {
				url : urlWithParameters,
				dataType : "text",
				cache: false,
				complete : function(jqXHR) {
				
					$("#graphSelectionBtn").attr("disabled", false);
					
					switch (jqXHR.status) {
					case 200:
                    	// Success... reload the whole thing again... could be better 
                    	// TODO: Inject the data retrieved into the svg/png 
						// set src attribute of image or data attribute of object to above url
						// graphic now starts loading again, but Loading will not be hidden / graphic will not be shown, until it's done loading (onGraphicLoaded()) 
						
						graphic.attr(variables.urlAttribute, urlWithParameters);
						
						// WebKit-based browsers (Safari/Chrome) don't seem to have their <object>.onLoad triggered 
						// So rather than showing the Loading gfx until it's actually done loading, we have to hide it here, at the half-way point
						
						if (navigator.userAgent.indexOf("WebKit") > -1) {
							onGraphicLoaded();
							makeGraphicPrintable();
						}
						break;
					                
					case 204:
					case 1223: // IE BugFix, see: http://forumsblogswikis.com/2008/07/02/ie7-xmlhttprequest-and-1223-status-code/ 
						// Error: No Content
						graphic.css('visibility', 'hidden');
						graphicLoading.html(variables.noContentMessage);
						graphicLoading.css('visibility', 'visible');
						makeGraphicPrintable();
                    	break;
                
					default:
						// Failure: Show failure message in Loading element (HTML possible) 
						graphic.css('visibility', 'hidden');
						graphicLoading.text("Error " + jqXHR.status);
						graphicLoading.css('visibility', 'visible');
						makeGraphicPrintable();
					}
				}
			};
			// Hide the graphic, disable graphic selection, and show loading 
			$("#graphSelectionBtn").attr("disabled", true);
			hideDownloadButton();
			
			//If variables.urlAttribute == src (IE) then an error occurs because a flow (/show) is wrongly requested
			if(variables.urlAttribute == "data") {
				graphic.attr(variables.urlAttribute, ''); // This is done so the Loading pic is always centered vertically
			}
			graphic.css('visibility', 'hidden');
			graphicLoading.html('<img border="0" src="' + variables.baseUrl + 'images/loading.gif" alt="Loading"/>');
			graphicLoading.css('visibility', 'visible');
			
			// Call the asynchronous xhrGet with above defined params
			var deferred = $.ajax(xhrArgs);			
		}	       		
	}
				
	// concrete handlers for the onClick event of specific menu items:
	function clickInformationFlow(key) {   
		clickAction("InformationFlow"); 
		refreshHeader(key);
	} 
	
	function clickBusinessLandscapeByBusinessFunctions(key) {
		clickAction("Landscape", "BusinessLandscapeByBusinessFunctions"); 
		refreshHeader(key);
	}
	       
	function clickBusinessLandscapeByProducts(key) {
		clickAction("Landscape", "BusinessLandscapeByProducts");
		refreshHeader(key);
	}		
			
	function clickBusinessLandscapeByBusinessUnits(key) {
		clickAction("Landscape", "BusinessLandscapeByBusinessUnits");
		refreshHeader(key);
	}
	       
	function clickBusinessLandscapeByBusinessProcesses(key) {
		clickAction("Landscape", "BusinessLandscapeByBusinessProcesses");
		refreshHeader(key);
	}
	       
	function clickBusinessLandscapeByProjects(key) {
		clickAction("Landscape", "BusinessLandscapeByProjects");
		refreshHeader(key);
	}
	
	function clickTechnicalLandscape(key) {
		clickAction("Landscape", "TechnicalLandscape");
		refreshHeader(key);
	}
	
	function clickMasterplanHierarchy(key) {
		clickAction("Masterplan", "Hierarchy");
		refreshHeader(key);
	}

	function clickMasterplanProjects(key) {
		clickAction("Masterplan", "Projects");
		refreshHeader(key);
	}

	function clickMasterplanTechnicalComponents(key) {
		clickAction("Masterplan", "TechnicalComponents"); 
		refreshHeader(key);
	}
	function clickNeighborhood(key) {
		clickAction("NeighborhoodDiagram"); 
		refreshHeader(key);
	}