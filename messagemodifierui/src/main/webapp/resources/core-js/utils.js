var phtool_editorHelper = {	
		
		/**
		 * return the editor of the module moduleName
		 */
		getMessageEditor: function (moduleName){
			var editor = $('div[id$='+moduleName+'_editor_data]').data("message_editor_instance");
			if(!editor || editor.moduleName != moduleName){
					editor = new phtool_editor(moduleName);
			}
 			$('div[id$='+moduleName+'_editor_data]').data("message_editor_instance",editor);
 			return editor;
		},
		
/*		*//**
		 * create the editor itself 
		 *//*
		createEditor:function (moduleName){
			phtool_editorHelper.getMessageEditor(moduleName).createEditor();
		}, */
		
		
		/*rebuild : function(moduleName) {
			try {
				window.clearTimeout(phtool_editorHelper.getMessageEditor(moduleName).token);
  				var execute = function() {
 					phtool_editorHelper.execfn(moduleName + "_build_validation_and_tree");
				};
				phtool_editorHelper.getMessageEditor(moduleName).token = window.setTimeout(execute, 2000);
				
			} catch (e) {

			}
		}, 
		
		execfn : function(fname) {
			try {
				var fn = window[fname];
				if (typeof fn === 'function') {
					fn();
				}
			} catch (e) {

			}
		},
		
		parse : function(moduleName) {
			try {
				var msgEditor = phtool_editorHelper.getMessageEditor(moduleName);
				var content = msgEditor.getMessage();
				if ($.trim(content) != "") {
					var oldType = msgEditor.mode;
					var newType = this.getMode(content);
					if (newType != oldType) {
						this.parse(content);
					}
				}
			} catch (e) {

			}
		}*/
		
		
		
		 	
			
};
