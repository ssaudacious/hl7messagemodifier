/**
 * 
 * 
 * 
 */

String.prototype.startsWith = function(str) {
	return (this.match("^" + str) == str);
};

/**
 * 
 */
function phtool_editor(moduleName) {
	try {
		/**
		 * mode of the editor. example: text, xml
		 */
		this.environment = new Array();
		this.moduleName = moduleName;
		this.token = '';
		this.mozilla = false;
		this.active_testcase_tab = 0;
		this.interval = 0;
		this.editor = window[moduleName + '_editor'].instance;
		this.layout_displayed = false;
		this.mode = null;
		this.selected = false;
 	} catch (e) {

	}
}

phtool_editor.prototype.setSelected = function(selected) {
	try {
		this.selected = selected;
	} catch (e) {
 	}
};

phtool_editor.prototype.selectText = function(start, end) {
	try {
		this.getEditor().setSelection(start, end);
		this.setSelected(true);
	} catch (e) {
 	}
};

phtool_editor.prototype.isSelected = function() {
	try {
		return this.selected;
	} catch (e) {
 		return false;
	}

};

/**
 * call onclick event fired on the editor
 * 
 * @param moduleName
 */
phtool_editor.prototype.onclick = function() {
	try {
		var moduleName = this.moduleName;
		var editor = this.getEditor();
		var cursor = editor.getCursor(true);
		var line = $("input[id$='" + moduleName + "_cursor_line']").val() - 1;
		var pos = $("input[id$='" + moduleName + "_cursor_index']").val();
		var start = $("input[id$='" + moduleName + "_cursor_start_index']")
				.val();
		var end = $("input[id$='" + moduleName + "_cursor_end_index']").val();
		if (this.isSelected() && (line == cursor.line && start <= cursor.ch && cursor.ch <= end)) {
			this.selectText({
				line : cursor.line,
				ch : cursor.ch
			}, {
				line : cursor.line,
				ch : cursor.ch
			});
			this.setSelected(false);
		} else {
			this.openElement();
		}
	} catch (e) {
 	}
};

/**
 * 
 */
phtool_editor.prototype.getMessage = function() {
	try {
		return this.getTextArea().val();
	} catch (e) {

	}
};

/**
 * 
 */
phtool_editor.prototype.getTextArea = function() {
	try {
		return $("textarea[id$='" + this.moduleName + "_textarea']");
	} catch (e) {

	}
};

phtool_editor.prototype.getMode = function(message) {
	try {
		if ($.trim(message) == "") {
			return "default";
		}
		if ($.trim(message).startsWith("<")) {
			return "xml";
		} else {
			return "edi";
		}
	} catch (e) {

	}
};

/**
 * 
 * @returns
 */
phtool_editor.prototype.getSeparatorArray = function() {
	try {
		return $("input[id$='" + this.moduleName + "_message_separators']")
				.val().split(",");
	} catch (e) {
		// console.log('Error when loading the separators' + e.message);
		return new Array();
	}
};

/**
 * 
 */
phtool_editor.prototype.removeEnvironment = function() {
	try {
		this.environment = null;
	} catch (e) {

	}
};

/**
 * 
 */
phtool_editor.prototype.openElement = function() {
	try {
		var functionName = this.moduleName + "_open_text_element";
		var fn = window[functionName];
		if (typeof fn === 'function') {
			fn();
		}
	} catch (e) {

	}
};

/**
 * 
 */
phtool_editor.prototype.getEDISeparatorArrayWithType = function() {
	try {
		var separatorArray = this.getSeparatorArray();
		var separatorArrayWithType = new Array(4);
		separatorArrayWithType['field_separator'] = separatorArray[0];
		separatorArrayWithType['component_separator'] = separatorArray[1];
		separatorArrayWithType['subcomponent_separator'] = separatorArray[2];
		separatorArrayWithType['continuation_separator'] = separatorArray[3];
		return separatorArrayWithType;
	} catch (e) {
		return new Array();
	}
};

/**
 * 
 */
phtool_editor.prototype.parse = function(content) {
	try {
		var mode = this.getMode(content);
		this.mode = mode;
		var editor = this.getEditor();
		if (mode == "edi" || mode == "xml") {
			if (mode == "edi") {
				var separatorArrayWithType = this
						.getEDISeparatorArrayWithType();
				editor.setOption("mode", {
					name : mode,
					separators : separatorArrayWithType
				});
			} else if (mode == "xml") {
				editor.setOption("mode", {
					name : mode
				});
				editor.setOption("extraKeys", {
					"'>'" : function(cm) {
						cm.closeTag(cm, '>');
					},
					"'/'" : function(cm) {
						cm.closeTag(cm, '/');
					}
				});
			}
			editor.setOption("theme", "elegant");
		}

	} catch (e) {

	}
};


/**
 * set onclick event handler
 */
phtool_editor.prototype.setOnclickHandler = function() {
	var othis = this;
	var $container = $("[id$='" + this.moduleName + "_editor_container']");
	$container.die('click');
	$container.click(function() {
		othis.onclick();
	});
};

/**
 * 
 * execute the global function with name fname
 * 
 * @param fname
 */
phtool_editor.prototype.execfn = function(fname) {
	try {
		var fn = window[fname];
		if (typeof fn === 'function') {
			fn();
		}
	} catch (e) {

	}
};

/**
 * return the global variable with name varname
 * 
 * @param varname
 * @returns
 */
phtool_editor.prototype.execvar = function(varname) {
	return window[varname];
};

/**
 * rebuild the components tree, validation and editor
 * 
 * @param moduleName
 */
phtool_editor.prototype.rebuild = function() {
	try {
		this.stopAutomaticRefresh();
 		var that = this;
		var execute = function() {
			var msg = that.getMessage();
			if (that.getMode(msg) == 'xml') {
				that.execfn(that.moduleName + "_build_all");
			} else {
				that.execfn(that.moduleName + "_build_validation_and_tree");
			}
		};
/*		this.token = window.setTimeout(execute, 2000);
*/		this.token = window.setTimeout(execute,$("#refresh_frequency").val());
		
	} catch (e) {
	}
};

phtool_editor.prototype.parseEditor = function() {
	try {
		var content = this.getMessage();
		if ($.trim(content) != "") {
			this.parse(content);
		}
	} catch (e) {

	}
};

/**
 * 
 * @param moduleName
 * @returns
 */
phtool_editor.prototype.getEditor = function() {
	return this.editor = window[this.moduleName + '_editor'].instance;
};

/**
 * 
 * @param moduleName
 * @param tohighlight
 */
phtool_editor.prototype.setCursorPosition = function(
		tohighlight) {
	try {
		var editor = this.getEditor();
		if (editor != null) {
			var moduleName = this.moduleName;
			var cursor = editor.getCursor(true);
			var cursorPosition = cursor.ch;
			var segment = editor.getLine(cursor.line);
			var characters = segment.split('');
			var startIndex = cursorPosition - 1;
			var endIndex = cursorPosition;
			var separatorArray = this.getSeparatorArray();
			if (separatorArray.lenght == 0) {
				return;
			}
			// get the beginning of the element
			while (startIndex > 0) {
				if ($.inArray(characters[startIndex], separatorArray) != -1) {
					break;
				} else {
					startIndex--;
				}
			}

			// get the end of the element
			while (endIndex < characters.length) {
				if ($.inArray(characters[endIndex], separatorArray) != -1) {
					break;
				} else {
					endIndex++;
				}
			}

			$("input[id$='" + moduleName + "_cursor_line']").val(
					cursor.line + 1);
			$("input[id$='" + moduleName + "_cursor_segment']").val(segment);
			$("input[id$='" + moduleName + "_cursor_index']").val(
					cursorPosition);
			$("input[id$='" + moduleName + "_cursor_start_index']").val(
					startIndex + 1);
			$("input[id$='" + moduleName + "_cursor_end_index']").val(endIndex);

			if (tohighlight) {
				this.highlight(startIndex + 1, endIndex);
			}
		}
	} catch (e) {

	}
};

/**
 * select the selected node in the textarea
 */
phtool_editor.prototype.selectElement = function() {
	try {
		var startIndex = parseInt($(
				"input[id$='" + this.moduleName + "_cursor_start_index']")
				.val());
		var endIndex = parseInt($(
				"input[id$='" + this.moduleName + "_cursor_end_index']").val());
		this.highlight(startIndex, endIndex);
	} catch (e) {

	}
};

/**
 * 
 * @param textarea
 * @param startIndex
 * @param endIndex
 */
phtool_editor.prototype.highlight = function(startIndex,
		endIndex) {
	try {
		if (startIndex != null && endIndex != null && startIndex >= 0
				&& endIndex >= 0) {
			var moduleName = this.moduleName;
			var line = parseInt($("input[id$='" + moduleName + "_cursor_line']")
					.val()) - 1;
			var start = {
				line : line,
				ch : startIndex
			};
			var end = {
				line : line,
				ch : endIndex
			};
			this.selectText(start, end);
		}
	} catch (e) {

	}
};

/**
 * pretty print xml content
 */
phtool_editor.prototype.formatContent = function() {
	try {
		var msg = this.getMessage();
		if (this.getMode(msg) == 'xml') {
 			var pretty = vkbeautify.xml(msg);
			this.getTextArea().val(pretty);
		}
	} catch (e) {

	}
}; 

phtool_editor.prototype.stopAutomaticRefresh=function(){
	window.clearTimeout(this.token);
};


 
