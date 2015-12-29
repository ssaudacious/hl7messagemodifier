/**
 * 
 * @param moduleName
 * @returns
 */
function phtool_cf(cfObject) {
	if(!cfObject.moduleName){
		alert("No name found for the context free module. Please provide the module name before continuing");
		return ;
	}
 	this.validationVisible = false;
	this.moduleName = cfObject.moduleName;
	$("ul#mainNav").append(
			"<li><a id=\""+ this.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+this.moduleName+ "').goNav('validation')\" title=\"Context-free Validation\">Context-free Validation</a></li>"
	);
 }; 
 



/**
 * allow navigation from one page to another
 */
phtool_cf.prototype.goNav= function (){
	this.goNav(id, false); 	
};
	


/**
 * 
 * @param id
 */
phtool_cf.prototype.goNav=function (id, override) {
			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide(); 
 			if (id == "validation"){
					$('#'+this.moduleName +'_tab').removeClass('jslink').addClass('current jslink'); 
					 $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
 			                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('validation')\" title=\"Validation\">Validation</a></li>"+
			                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('report')\" title=\"Report\">Report</a></li>"+
			            "</ul>");
					$("#"+ this.moduleName +"_validation").show();
					if(!this.validationVisible){
 						phtool_getManager().execute(this.moduleName+"_load_validation_tab");
						this.validationVisible = true;
					}
			} 
			
			if (id == 'report'){
				$('#'+this.moduleName +'_tab').removeClass('jslink').addClass('current jslink'); 
				 $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
			                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('validation')\" title=\"Validation\">Validation</a></li>"+
		                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('report')\" title=\"Report\">Report</a></li>"+
		            "</ul>");
				 	phtool_getManager().execute(this.moduleName+"_update_report");
		            $("#"+this.moduleName +"_report").show(); 
			}
	 		phtool_getManager().saveState(id);
};

        

