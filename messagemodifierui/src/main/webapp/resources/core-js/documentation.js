
/**
 * 
 * @returns
 */
function phtool_doc(documentationObject){
	this.tab_loaded = false;
 	this.documentationObject = documentationObject;
 	$("ul#mainNav").append("<li><a id=\""+documentationObject.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+documentationObject.moduleName+ "').goNav('"+documentationObject.targetId+ "')\" title=\""+ documentationObject.description +"\">"+ documentationObject.title +"</a></li>");

}; 

 
/**
 * allow navigation from one page to another
 */
phtool_doc.prototype.goNav = function (id){
	this.goNav(id, false); 
};

/**
 * 
 * @param id
 */
phtool_doc.prototype.goNav = function (id, override) {
			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide(); 
			 
			 var docTagertId = this.documentationObject.targetId;
			 
 		     if (id == docTagertId){
			     	$('#'+ this.documentationObject.moduleName  +'_tab').removeClass('jslink').addClass('current jslink'); 	           
 		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\"></ul>"); 
 		           $('#'+this.documentationObject.targetId).show();
		     }   
            
		     phtool_getManager().saveState(id);
};
        

