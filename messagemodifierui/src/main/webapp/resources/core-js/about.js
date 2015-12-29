/**
 * 
 * @returns
 */
function phtool_about(jsonObject){
	this.tab_loaded= false;
  	this.jsonObject = jsonObject;
 	$("ul#mainNav").append("<li><a id=\""+jsonObject.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+jsonObject.moduleName+ "').goNav('"+jsonObject.targetId+ "')\" title=\""+ jsonObject.description +"\">"+ jsonObject.title +"</a></li>");

};

 

phtool_about.prototype.goNav = function (id){
	this.goNav(id, false); 
};

/**
 * 
 * @param id
 */
phtool_about.prototype.goNav=function (id, override) {
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
        

