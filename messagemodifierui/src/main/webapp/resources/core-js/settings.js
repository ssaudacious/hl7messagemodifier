/*
*//**
 * 
 * @returns
 *//*
function createSettingsModuleInstance(){
	return  $('div#tool_data').data('settingsModuleInstance',new phtool_setting());
};
*/
/**
 * 
 * @returns
 */
/*function getSettingsModuleInstance(){
	return $('div#tool_data').data('settingsModuleInstance');
};*/

/**
 * 
 * @returns
 */
function phtool_setting(jsonObject){
	this.tab_loaded= false;
  	this.jsonObject = jsonObject;
 	$("ul#mainNav").append("<li><a id=\""+jsonObject.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+jsonObject.moduleName+ "').goNav('"+jsonObject.targetId+ "')\" title=\""+ jsonObject.description +"\">"+ jsonObject.title +"</a></li>");

};

/* 
*//**
 * Allow navigation from one page to another
 *//*
phtool_setting.prototype.goNav=function (id){
	getSettingsModuleInstance().goNav(id, false); 
};*/


phtool_setting.prototype.goNav = function (id){
	this.goNav(id, false); 
};

/**
 * 
 * @param id
 */
phtool_setting.prototype.goNav=function (id, override) {
/*		var lastTab = $('input#current_tab').val();
		if(lastTab != id || override){
			 $("a[id$='_tab']").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide();
			 if (id == 'settings'){
		            $('#settings_tab').removeClass('jslink').addClass('current jslink'); 
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\"></ul>"); 
		            $('#settings').show(); 
		     } 
			 phtool_getManager().saveState(id);
		} 
		*/
		
		 $("a.module_tab").removeClass('current jslink').addClass('jslink');
		 $(".page").removeClass("hidden").hide(); 
		 var tagertId = this.jsonObject.targetId;
		 
		     if (id == tagertId){
		     	$('#'+ this.jsonObject.moduleName  +'_tab').removeClass('jslink').addClass('current jslink'); 	           
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\"></ul>"); 
		           $('#'+this.jsonObject.targetId).show();
	     }   
	     phtool_getManager().saveState(id);
	     
};
        

