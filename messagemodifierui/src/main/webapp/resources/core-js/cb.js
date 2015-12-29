
/**
 * 
 * @returns
 */
function phtool_cb(cbObject) {
	if(!cbObject.moduleName){
		alert("No name found for the context based module. Please provide the module name before continuing");
		return ;
	}
	/** Message editor bean */
 	this.testcaseTabVisible = false;
	this.validationTabVisible = false;
	this.moduleName = cbObject.moduleName;
	$("ul#mainNav").append(
			"<li><a id=\""+ this.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+this.moduleName+ "').goNav('tc')\" title=\"Context-based Validation\">Context-based Validation</a></li>"
	);
} 
 
/**
 * 
 */
phtool_cb.prototype.loadTestCase =function () {
	this.validationTabVisible = false;
	this.goNav('validation');
	this.testcaseTabVisible = true;
};  

 
/**
 * allow navigation from one page to another
 */
phtool_cb.prototype.goNav =function (id){
	this.goNav(id, false); 
}; 

/**
 * 
 * @param id
 */
phtool_cb.prototype.goNav=function (id, override) {
 			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide();
 		     if (id == 'tc'){
		            $('#'+this.moduleName +'_tab').removeClass('jslink').addClass('current jslink');             
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
		                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('tc')\" title=\"Test Case\">Test Case</a></li>" +
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('validation')\" title=\"Validation\">Validation</a></li>"+
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('report')\" title=\"Report\">Report</a></li>"+
		            "</ul>");
		            $("#"+ this.moduleName +"_tc").show();
		            if(!this.testcaseTabVisible){
		            	phtool_getManager().execute(this.moduleName+"_load_testcase_tab");
		            	this.testcaseTabVisible = true;
		            }
		      }   
		        
		      if (id == 'validation'){
		            $('#'+this.moduleName +'_tab').removeClass('jslink').addClass('current jslink');             
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('tc')\" title=\"Test Case\">Test Case</a></li>" +
		                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('validation')\" title=\"Validation\">Validation</a></li>"+
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('report')\" title=\"Report\">Report</a></li>"+
		            "</ul>"); 
		            $("#"+ this.moduleName +"_validation").show(); 
		            if(!this.validationTabVisible){
		            	phtool_getManager().execute(this.moduleName+"_load_validation_tab");
		            	this.validationTabVisible = true;
		            }
		      } 
		        
		      if (id == "report"){
		            $('#'+this.moduleName +'_tab').removeClass('jslink').addClass('current jslink');             
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('tc')\" title=\"Test Case\">Test Case</a></li>" +
		                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('validation')\" title=\"Validation\">Validation</a></li>"+
		                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('report')\" title=\"Report\">Report</a></li>"+
		            "</ul>"); 
		            phtool_getManager().execute(this.moduleName+"_update_report");
		            $("#"+this.moduleName+"_report").show(); 
		     }
	 
		     phtool_getManager().saveState(id);
};

