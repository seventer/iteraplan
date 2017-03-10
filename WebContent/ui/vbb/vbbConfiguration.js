function Configuration(vbbName, submitButton) {
	this.substantialTypes = [];
	this.features = [];
	this.filters = [];
	this.vbbName = vbbName;
	this.submitButton = submitButton;
	
	// Only the selectedValueHolder should be bound to the backing bean.
	// Its value will be set from the select list via javascript.
	function _addFeature(select, parentHiddens, selectedValueHolder, optional) {
		var feature = {};
		feature.select = select;
		feature.parentHiddens = parentHiddens;
		feature.id = select.attr('id');
		feature.configuration = this;
		feature.optional = (typeof optional != 'undefined') ? optional : false; // default value "false"
		feature.selectedValueHolder = selectedValueHolder;
		feature.select.change(function () {
			feature.selectedValueHolder.val(select.val());
		});
		this.features.push(feature);
		return feature;
	}
	this.addFeature = _addFeature;
	
	//select, parentHidden, selectedValueHolder, optional, staticRadio, attributeRadio,
	//staticContent, attributeContent, decorationModeHidden, continuousContent, discreteContent,
	//minValueHidden, maxValueHidden, discreteColorMappingHidden
	function _addColorFeature(params) {
		var feature = this.addFeature(params.select, [params.parentHidden], params.selectedValueHolder, params.optional);
		feature.isColorFeature = true;
		feature.params = params;
		feature.attributeContent = params.attributeContent;
		feature.staticContent = params.staticContent;
		
		feature.select.change(function(){
			return _readColorMappingRecommendation(feature, feature.params.select.val(), true);
		});
		
		params.staticRadio.click(function(){
			params.staticContent.show();
			params.attributeContent.hide();
			params.selectedValueHolder.val("");
		});
		params.attributeRadio.click(function(){
			_readColorMappingRecommendation(feature, params.select.val(), false);
			params.selectedValueHolder.val(params.select.val());
			params.staticContent.hide();
			params.attributeContent.show();
		});
	}
	this.addColorFeature = _addColorFeature;
	
	function _readColorMappingRecommendation(feature, selectedValue, resetValues) {
		feature.selectedValueHolder.val(selectedValue);
		VbbConfigurationService.recommendColorMapping(feature.params.parentHidden.val(), feature.selectedValueHolder.val(), function(data) {
			feature.params.decorationModeHidden.val(data['decorationMode']);
			if ('continuous' == data['decorationMode']) {
				feature.params.minValueHidden.val(data['minValue']);
				feature.params.maxValueHidden.val(data['maxValue']);
				feature.params.continuousContent.show();
				feature.params.discreteContent.hide();
			}
			if ('discrete' == data['decorationMode']) {
				//TODO we still overwrite all values here=>parse from hidden field controlled with a falg
				var availableColors = jQuery.parseJSON(data['availableColors']);
				var orderedLiterals = jQuery.parseJSON(data['orderedLiterals']);
				var selectedColors = jQuery.parseJSON(data['selectedColors']);
				
				if (resetValues == false && feature.params.discreteColorMappingHidden.val()) {
					selectedColors = jQuery.parseJSON(feature.params.discreteColorMappingHidden.val());
				} else {
					feature.params.discreteColorMappingHidden.val(data['selectedColors']);
				}
				 _refreshDiscreteColoringOptions(feature.params.discreteContent, availableColors, orderedLiterals, selectedColors, feature.params.discreteColorMappingHidden);
	
				feature.params.continuousContent.hide();
				feature.params.discreteContent.show();
			}
			if (undefined == data['decorationMode'] || '' == data['decorationMode']) {
				feature.params.staticContent.show();
				feature.params.attributeContent.hide();
				feature.params.selectedValueHolder.val("");
			}
		});
	}
	
	function _refreshDiscreteColoringOptions(container, availableColors, orderedLiterals, selectedColors, discreteColorMappingHidden) {
		container.find("*").unbind();
		container.empty();
		var discreteColorContent = '';
		$.each(orderedLiterals, function(index, literal) {
			discreteColorContent = discreteColorContent + '<div  class="control-group"><label class="control-label"> </label><div class="controls">';
			discreteColorContent = discreteColorContent +  _createColorSelect(availableColors, selectedColors[literal], literal);
			discreteColorContent = discreteColorContent + '<span class="help-inline">';
			discreteColorContent = discreteColorContent + literal;
			discreteColorContent = discreteColorContent + '</span></div></div>';
		});
		container.append(discreteColorContent);
		container.find("select").each(function(){
			$(this).click(function(){
				var colorMapping = jQuery.parseJSON(discreteColorMappingHidden.val());
				var literalName = $(this).data('name');
				var o2 = this.options[this.options.selectedIndex];
				if (o2) {
					this.style.backgroundColor = o2.style.backgroundColor;
					colorMapping[literalName] =  o2.value;
				} 
				var colorMappingSerialization = JSON.stringify(colorMapping);
				discreteColorMappingHidden.val(colorMappingSerialization);
			});
		});
	}
	
	function _createColorSelect(availableColors, selectedColor, literalName) {
		var result = '<select data-name="'+literalName+'" style="width:60px;background-color:#' +selectedColor + '">';
		$.each(availableColors, function(index, color) { 
			if (color.toLowerCase() == selectedColor.toLowerCase()) {
				result = result + '<option selected="selected" value="' + color + '" style="background-color:#' + color + '">&nbsp;</option>';
			} else {
				result = result + '<option value="' + color + '" style="background-color:#' + color + '">&nbsp;</option>';
			}
		});
		result = result + '</select>';
		return result;
	}
	
	function _addSubstantialType(cloud, drop, hidden, optional) {
		var substantialType = {};
		substantialType.id = hidden.attr('id').replace(/\W/g,"__");
		substantialType.cloud = cloud;
		substantialType.drop = drop;
		substantialType.hidden = hidden;
		substantialType.configuration = this;
		substantialType.optional = (typeof optional != 'undefined') ? optional : false; // default value "false"
		this.substantialTypes.push(substantialType);
	};
	this.addSubstantialType = _addSubstantialType;
	
	function _addFilter(button, propertyDropDown, propertyDropDownSelected, operatorDropDown, operatorDropDownSelected, valueDropDown, valueField, hidden, hintDiv, showOrphansCheckbox) {
		var filter = {};
		filter.button = button;
		filter.hintDiv = hintDiv;
		filter.hidden = hidden;
		filter.propertyDropDown = propertyDropDown;
		filter.propertyDropDownSelected = propertyDropDownSelected;
		filter.operatorDropDown = operatorDropDown;
		filter.operatorDropDownSelected = operatorDropDownSelected;
		filter.valueDropDown = valueDropDown;
		filter.valueDropDown.hide();
		filter.valueField = valueField;
		filter.showOrphansCheckbox = showOrphansCheckbox;
		this.filters.push(filter);
		filter.hidden.change(function() {
			filter.propertyDropDown.empty();
		});
		
		filter.propertyDropDown.change(function() {
			filter.propertyDropDownSelected.val(filter.propertyDropDown.val());
			filter.operatorDropDown.empty();
			filter.valueDropDown.empty();
			MetamodelExplorerService.getComparisonOperators(filter.hidden.val(), filter.propertyDropDown.val(), function(data2) {
				$.each(data2, function(index2, value2) {
					var option = $('<option></option>').val(value2.persistentName).html(value2.localName);
					if (value2.persistentName == filter.operatorDropDownSelected.val()) {
						option.attr('selected','selected');
					}
					filter.operatorDropDown.append(option);
				});
				filter.operatorDropDownSelected.val(filter.operatorDropDown.val());
			});
			MetamodelExplorerService.getLiterals(filter.hidden.val(), filter.propertyDropDown.val(), function(data2) {
				if (data2.length==0) {
					filter.valueDropDown.hide();
					filter.valueField.val('');
					filter.valueField.show();
				} else {
					filter.valueDropDown.empty();
					filter.valueDropDown.show();					
					filter.valueField.hide();
					$.each(data2, function(index2, value2) {
						var option = $('<option></option>').val(value2.persistentName).html(value2.localName);
						if (value2.persistentName == filter.valueField.val()) {
							option.attr('selected','selected');
						}
						filter.valueDropDown.append(option);
					});
					filter.valueField.val(filter.valueDropDown.val());
				}
			});
		});

		filter.operatorDropDown.change(function() {
			filter.operatorDropDownSelected.val(filter.operatorDropDown.val());
		});

		filter.valueDropDown.change(function() {
			filter.valueField.val(filter.valueDropDown.val());
		});
	}
	this.addFilter = _addFilter;
	
	this.init = function() {
		// Establishes the parent-child Relationships between the features and the substantialTypes
		var substantialTypeHelper = {};
		$.each(this.substantialTypes, function(index, substantialType) {
			substantialTypeHelper[substantialType.hidden.attr("name")] = substantialType;
		});
		
		$.each(this.features, function(index, feature) {
			feature.parents = [];
			$.each(feature.parentHiddens, function(parentHiddenIndex, parentHidden) {
				feature.parents.push(substantialTypeHelper[parentHidden.attr("name")]);
			});
		});
		
		$.each(this.substantialTypes, function(index, substantialType) {
			substantialType.cloudLoadIndicator = $('<img id="' + substantialType.id +'LoadIndicator" src="../../images/loading.gif" alt="loading..."/>');
			substantialType.cloud.append(substantialType.cloudLoadIndicator);
			_createCloudAndDropList(substantialType);
		});
		
		this.readRecommendation();
	};
	
	function _readRecommendation() {
		var parameters = {};
		var complete = true;
		$.each(this.substantialTypes, function(index, substantialType) {
			$('.' + substantialType.id + 'Draggable').remove();
			substantialType.cloudLoadIndicator.show();
			complete &= substantialType.optional || (substantialType.hidden.val() != '');
			parameters[substantialType.id] = substantialType.hidden.val();
		});
		$.each(this.features, function(index, feature) {
			var enabled = true;
			$.each(feature.parents, function(parentIndex, parent) {
				enabled &= (parent.hidden.val() !== ''); 
			});
			if (enabled) {
				feature.select.removeAttr('disabled');
				parameters[feature.id] = feature.select.val();
			} else {
				feature.select.attr('disabled', 'disabled');
				feature.selectedValueHolder.val(null);
			}
		});
		if (complete) {
			submitButton.removeAttr('disabled');
		} else {
			submitButton.attr('disabled', 'disabled');
		}
		var substantialTypes = this.substantialTypes;
		var features = this.features;
		var filters = this.filters;
		VbbConfigurationService.recommend(parameters, vbbName, function(data) {
			var candidate; // Handle undefined substantialType/feature-mappings (Can happen when substType/feature is optional)
			$.each(substantialTypes, function(index, substantialType) {
				substantialType.cloudLoadIndicator.hide();
				candidate = (typeof data[substantialType.id] === "undefined") ? [] : data[substantialType.id];
				_addSubstantialTypeCandidates(substantialType, candidate);
				_initCloudAndDropList(substantialType);
			});
			
			$.each(features, function(index, feature) {
				if (feature.select.attr('disabled') === 'disabled') {
					feature.select.empty();
				} else {
					candidate = (typeof data[feature.id] === "undefined") ? [] : data[feature.id];
					_addFeatureCandidates(feature, candidate);
				}
			});
			
			$.each(filters, function(index, filter) {
				if (filter.hidden.val() == '' || filter.hidden.val().indexOf(".objectify(") != -1) {
					filter.button.attr('disabled', 'disabled');
					if (filter.showOrphansCheckbox != undefined) {
						filter.showOrphansCheckbox.attr('disabled', 'disabled');
					}
					filter.hintDiv.hide();
				} else {
					filter.button.removeAttr('disabled');
					if (filter.showOrphansCheckbox != undefined) {
						filter.showOrphansCheckbox.removeAttr('disabled');
					}
					filter.hintDiv.show();
					MetamodelExplorerService.getProperties(filter.hidden.val(), function(data2) {
						filter.propertyDropDown.empty();
			    		$.each(data2, function(index,candidate2) {
			    			var option = $('<option></option>').val(candidate2.persistentName).html(candidate2.localName);
			    			if (candidate2.persistentName == filter.propertyDropDownSelected.val()) {
			    				option.attr('selected', 'selected');
			    			}
			    			filter.propertyDropDown.append(option);
			    		});
					});			
				}
			});
		});
	}
	this.readRecommendation = _readRecommendation;
	
	function _addSwitch(hidden1, hidden2, trigger) {
		var config = this;
		trigger.click(function() {
			var new1 = hidden2.val();
			var new2 = hidden1.val();
			
			hidden1.val(new1);
			hidden2.val(new2);

			flowAction('switchTypes');
		});
	}
	this.addSwitch = _addSwitch;
	
	function _createCloudAndDropList(substantialType) {
		substantialType.cloudList = $('<ul class="' + substantialType.id + 'TagList tagList"></ul>');
		substantialType.cloud.append(substantialType.cloudList);
		substantialType.dropList = $('<ul class="' + substantialType.id + 'DropList dropList"></ul>');
		substantialType.drop.append(substantialType.dropList);
	}
	
	function _initCloudAndDropList(substantialType) {
		substantialType.cloud.droppable({
			accept: '.' + substantialType.id +'Draggable',
			activeClass: 'hover',
			hoverClass: 'active',
			drop: function(event, ui) {
				if ($('.' + substantialType.id + 'DropList>.' + substantialType.id + 'Draggable').size() !== 1) {
					if (substantialType.hidden.val() !== '') {
						substantialType.hidden.val('');
						flowAction('reset' + substantialType.id.charAt(0).toUpperCase() + substantialType.id.slice(1));
					}
				}
			}
		});
		substantialType.drop.droppable({
			accept: '.' + substantialType.id + 'Draggable',
			activeClass: 'hover',
			hoverClass: 'active',
			drop: function(event, ui) {
				if (substantialType.hidden.val().indexOf('[') >= 0) {
					
				} else {
					if (substantialType.hidden.val() != ui.draggable.attr('id').split(':').pop()) {
						substantialType.hidden.val(ui.draggable.attr('id').split(':').pop());
						substantialType.configuration.readRecommendation();
					}
				}
			}
		});
	}

	function _addSubstantialTypeCandidates(substantialType, candidates) {
		$.each(candidates, function(index, candidate) {
			_addSubstantialTypeCandidate(substantialType, candidate, null);
			$.each(candidate.children, function(childIndex, childCandidate) {
				_addSubstantialTypeCandidate(substantialType, childCandidate, candidate);
			});
		});
		$('li.' + substantialType.id + 'Draggable').draggable({
			revert: 'valid', 
			helper: 'clone',
		});
		$('li.' + substantialType.id + 'Draggable span.symbol').click(function () {
			$('li.' + substantialType.id + 'Draggable[id^="' + $(this).parent().attr("id") + ':"]').toggle();
			$(this).text($(this).text()=="+" ? "-" : "+");
			$(this).parent().show();
		});
	}
	
	function _addSubstantialTypeCandidate(substantialType, candidateValue, parentValue) {
		isChildCandidate = (parentValue != null);
		var html = '<li id="' + (isChildCandidate ? ':' + parentValue.id :'') + ':' + candidateValue.id + '" class="' + substantialType.id + 'Draggable draggable' + (isChildCandidate ? ' childDraggable' : '');
		html += (candidateValue.root ? ' rootDraggable' : '') + '" style="font-size:' + ((5 + candidateValue.prio) * 20) + '%">';
		html += '<span class="symbol">' + (candidateValue.children.length > 1 ? '+' : '') + '</span> <span class="text">' + candidateValue.name + '</span></li>';
		if (candidateValue.id == substantialType.hidden.val().split('[')[0]) {
			substantialType.dropList.append(html);
		} else {
			substantialType.cloudList.append(html);
		}
	}
	
	function _addFeatureCandidates(feature, candidates) {
		feature.select.empty();
		var firstId = null;
		// add an empty candidate if feature is optional
		if (feature.optional == true && (feature.isColorFeature == false || feature.isColorFeature == undefined)) {
			candidates.unshift({id:"",name:""});
		}

		// add an option to the select-field for each of the candidates
		$.each(candidates, function(index, candidate) {
			var selected = '';
			if (candidate.id == feature.selectedValueHolder.val()) {
				selected=' selected="selected"';
				firstId = candidate.id;
			}				
			if (firstId == null) {
				firstId = candidate.id;
			}
			var html = '<option class="listItem" value="' + candidate.id + '"'+selected+'>' + candidate.name + '</option>';
			feature.select.append(html);
		});
		if (feature.isColorFeature == false || feature.isColorFeature == undefined) {
			feature.selectedValueHolder.val(firstId);
		}
		if (feature.isColorFeature == true) {
			//show (if there is at least one matching attribute) or
			//hide (if not matcihg attributes) the radiobutton and content for attribute coloring
			if (candidates.length == 0) {
				feature.params.attributeColoringContainer.hide();
			} else {
				feature.params.attributeColoringContainer.show();
			}
			//if a decoration mode has been set from the options bean
			//read recommendation with settings from the page
			if (feature.params.decorationModeHidden.val()) {
				feature.params.staticContent.hide();
				feature.params.attributeContent.show();
				_readColorMappingRecommendation(feature, feature.selectedValueHolder.val(), false);
			} else {
				feature.params.staticContent.show();
				feature.params.attributeContent.hide();
			}
		}
	}
}