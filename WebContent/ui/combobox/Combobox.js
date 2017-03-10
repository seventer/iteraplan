var cache = {};
(function($) {
	$.widget("ui.combobox", $.ui.autocomplete, {
		options : {
			minLength : 2,
			baseUrl : "/iteraplan/",
			showInactiveStatus : true,
			buttonTitle : "Show All Items",
			logging : false
		},
		
		_create : function() {
			this.options.sort = this.options.sort || this.element.attr("sort");
			this.options.bbtype = this.options.bbtype || this.element.attr("bbtype");
			if (this.options.bbtype) {
				this.options.bbtype = this.options.bbtype.replace(/release/i, '');
			}

			if (this.element.is("SELECT")) {
				this._selectInit();
				return;
			}

			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": init autocomplete");
			$.ui.autocomplete.prototype._create.call(this);

			this.element.addClass("ui-widget ui-widget-content ui-corner-left").tooltip({
				tooltipClass : "ui-state-highlight"
			});
			this._createButton();
		},

		_createButton : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": CreateButton");
			var self = this;

			this.button = $("<button type='button'>&nbsp;</button>").attr("tabIndex", -1).attr("title", "Show All Items").insertAfter(this.element)
					.button({
						icons : {
							primary : "ui-icon-triangle-1-s"
						},
						text : false
					}).removeClass("ui-corner-all").addClass("ui-corner-right ui-button-icon").click(function(event) {
						self._showFullMenu();
					});
		},

		_showFullMenu : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": Show Full Menu");
			input = this.element;
			var data = input.data("combobox");

			// close if already visible
			if (input.combobox("widget").is(":visible")) {
				input.combobox("close");
				this._selectOptionOrRemoveIfInvalid();
				return;
			}

			// when user clicks the show all button, we display the cached full
			// menu
			var openList = function() {
				// if (data.options.logging) console.log("open list view");
				/*
				 * input/select that are initially hidden (display=none, i.e.
				 * second level menus), will not have position cordinates until
				 * they are visible.
				 */
				input.combobox("widget").css("display", "block").position($.extend({
					of : input
				}, data.options.position));
				input.focus();
				data._trigger("open");
			};

			clearTimeout(data.closing);
			if (!input.isFullMenu) {
				var isFullMenuRendered = data.menuAll;
				data._swapMenu();
				input.isFullMenu = true;
				if (!isFullMenuRendered) {
					if (this.options.sourceArray) {
						this._renderFullMenu(this.options.sourceArray);
						openList();
					} else {
						this._loadOptions(function() {
							data._createSourceArray();
							data._renderFullMenu(data.options.sourceArray);
							openList();
						});
					}
				} else {
					openList();
				}
			} else {
				openList();
			}
		},

		// initialize the full list of items, this menu will be reused whenever
		// the user clicks the show all button
		_renderFullMenu : function(source) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": RenderFullMenu");
			var self = this, input = this.element, ul = input.data("combobox").menu.element, lis = [];
			source = this._normalize(source);
			input.data("combobox").menuAll = input.data("combobox").menu.element.clone(true).appendTo("body");
			for ( var i = 0; i < source.length; i++) {
				lis[i] = "<li class=\"ui-menu-item\" role=\"menuitem\"><a class=\"ui-corner-all\" tabindex=\"-1\">" + source[i].label + "</a></li>";
			}
			ul.append(lis.join(""));
			this._resizeMenu();
			// setup the rest of the data, and event stuff
			setTimeout(function() {
				self._setupMenuItem.call(self, ul.children("li"), source);
			}, 0);
			input.isFullMenu = true;
		},

		// incrementally setup the menu items, so the browser can remains
		// responsive when processing thousands of items
		_setupMenuItem : function(items, source) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": SetupMenuItem");
			var self = this, itemsChunk = items.splice(0, 500), sourceChunk = source.splice(0, 500);
			for ( var i = 0; i < itemsChunk.length; i++) {
				$(itemsChunk[i]).data("item.autocomplete", sourceChunk[i]).mouseenter(function(event) {
					self.menu.activate(event, $(this));
				}).mouseleave(function() {
					self.menu.deactivate();
				});
			}
			if (items.length > 0) {
				setTimeout(function() {
					self._setupMenuItem.call(self, items, source);
				}, 0);
			}
		},

		/* overwrite. make the matching string bold */
		_renderItem : function(ul, item) {
			var label = item.label.replace(new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + $.ui.autocomplete.escapeRegex(this.term)
					+ ")(?![^<>]*>)(?![^&;]+;)", "gi"), "<strong>$1</strong>");
			return $("<li></li>").data("item.autocomplete", item).append("<a>" + label + "</a>").appendTo(ul);
		},

		/* overwrite. to cleanup additional stuff that was added */
		destroy : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": Destroy");
			if (this.element.is("SELECT")) {
				this.input.remove();
				this.element.removeData().show();
				return;
			}
			// super()
			$.ui.autocomplete.prototype.destroy.call(this);
			// clean up new stuff
			this.element.removeClass("ui-widget ui-widget-content ui-corner-left");
			this.button.remove();
		},

		/* overwrite. to swap out and preserve the full menu */
		search : function(value, event) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": Search");
			var input = this.element;
			if (input.isFullMenu) {
				this._swapMenu();
				input.isFullMenu = false;
			}
			// super()
			$.ui.autocomplete.prototype.search.call(this, value, event);
		},

		_change : function(event) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _change");
			this._selectOptionOrRemoveIfInvalid();
			// super()
			$.ui.autocomplete.prototype._change.call(this, event);
		},

		_selectOptionOrRemoveIfInvalid : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _selectOptionOrRemoveIfInvalid");
			if (!this.selectedItem) {
				var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex(this.element.val()) + "$", "i"), match = $.grep(
						this.options.sourceArray, function(value) {
							return matcher.test(value.label);
						});
				if (match.length) {
					var item = $(match[0].option);
					if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": Setting (", item.val(), "-", item.text(), ") as selected.");
					this.options.selectElement.val(item.val());
				} else {
					if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": remove invalid value, as it didn't match anything");
					this.element.val("");
					this.options.selectElement.val("-1");
					this.options.selectElement.val("-1"); // 2nd call needed, since otherwise it doesn't work most of the time in IE9
				}
			} else {
				if (this.options.logging) console.log("nothing to do.");
			}
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": current selected element: ", this.options.selectElement.val());
		},

		_swapMenu : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _swapMenu");
			var input = this.element, data = input.data("combobox"), tmp = data.menuAll;

			data.menuAll = data.menu.element.hide();
			if (tmp) {
				data.menu.element = tmp;
			} else {
				data.menu.element.empty();
			}
		},

		_loadOptions : function(callback) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _loadOptions");
			if (!this.options.loaded) {
				if (this.options.bbtype in cache) {
					this._addOptionsEntries(cache[this.options.bbtype]);
					callback();
				} else {
					var self = this;
					this._fetch(function(values) {
						cache[self.options.bbtype] = values;
						self._addOptionsEntries(values);
						callback();
					});
					}
			} else {
				if (this.options.selectElement.children("option").first().val() == '') {
					this.options.selectElement.children("option").first().val('-1').html('&nbsp;');
				}
				callback();
			}
		},

		_fetch : function(processValues) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _fetch");
			$.ajax({
				url : this.options.baseUrl + this.options.bbtype + "/list.do",
				data : {
					showInactive : this.options.showInactiveStatus,
					escapeHtml: true
				},
				dataType : "json",
				success : function(data) {
					var values = $.map(data.items, function(item) {
						return {
							label : item.name,
							value : item.id
						};
					});
					processValues(values);
				}
			});
		},

		_addOptionsEntries : function(values) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": _addOptionsEntries");
			if (this.options.sort == 'desc') {
				values = values.slice().reverse();
				if (values.length > 1) {
					if (values[values.length - 1].label == '-') {
						values.unshift(values.pop());
					}
				}
			}
			var htmlString = "";
			if (this.options.selectElement.attr("req") != 'true') {
				htmlString += '<option value="-1">&nbsp;</option>';
			}
			$.each(values, function(_, el) {
				htmlString += '<option value="' + el.value + '">' + el.label + '</option>';
			});
			this.options.selectElement.html(htmlString);
			this.options.loaded = true;
		},

		_selectInit : function() {
			var self = this;
			
			if (this.options.logging) console.log(this.element.attr('id'), ": _selectInit");
			var select = this.element.hide(), selected = select.children(":selected"), value = selected.val() ? selected.text() : "";
			if (select.attr("dynLoad") == 'true') {
				this.options.loaded = false;
			} else {
				this.options.loaded = true;
			}
			this.options.selectElement = select;
			this.options.source = this._source;
			var userSelectCallback = this.options.select;
			var userSelectedCallback = this.options.selected;
			this.options.select = function(event, ui) {
				var item = $(ui.item.option);
				if (self.options.logging) console.log(self.options.selectElement.attr('id'), ": Setting (", item.val(), "-", item.text(), ") as selected.");
				self.options.selectElement.val(item.val());
				self.options.selectElement.val(item.val()); // 2nd call needed, since otherwise it doesn't work most of the time in IE9
				
				self.input.val(item.text());
				event.preventDefault();
					
				if (userSelectCallback)
					userSelectCallback(event, ui);
				// compatibility with jQuery UI's combobox.
				if (userSelectedCallback)
					userSelectedCallback(event, ui);
				if (self.options.logging) console.log(self.options.selectElement.attr('id'), ": current selected element: ", self.options.selectElement.val());
			};
			this.input = $("<input>").insertAfter(select).val(value).combobox(this.options);
		},

		_createSourceArray : function() {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": CreateSourceArray");
			var optElements = this.options.selectElement.children();
			this.options.sourceArray = [];
			for ( var k = 0; k < optElements.length; k++) {
				this.options.sourceArray[k] = {
					label : optElements[k].innerHTML,
					option : optElements[k]
				};
			}
		},

		_source : function(request, response) {
			if (this.options.logging) console.log(this.options.selectElement.attr('id'), ": Source");
			var self = this;
			if (this.options.sourceArray) {
				response($.ui.autocomplete.filter(this.options.sourceArray, request.term));
			} else {
				this._loadOptions(function() {
					self._createSourceArray();
					response($.ui.autocomplete.filter(self.options.sourceArray, request.term));
				});
			}
		}
	});
})(jQuery);