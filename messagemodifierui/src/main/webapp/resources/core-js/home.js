function phtool_home(homeObject) {
	this.homeObject = homeObject;
	$("ul#mainNav").append(
			"<li><a id=\""+this.homeObject.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+this.homeObject.moduleName+ "').goNav('"+this.homeObject.targetId+"')\" title=\""+this.homeObject.description+"\">"+this.homeObject.title+"</a></li>"
	);
};

/**
 * allow navigation from one page to another
 */

phtool_home.prototype.goNav=function(id){
	this.goNav(id, false); 
}; 

/**
 * 
 * @param id
 */
phtool_home.prototype.goNav= function(id, override) {
			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide(); 
			 //HOME
			if (id == this.homeObject.targetId){
					$("#"+this.homeObject.moduleName + "_tab").removeClass('jslink').addClass('current jslink'); 
					$('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +"</ul>"); 
					$("#"+this.homeObject.targetId).show(); 
			} 
			phtool_getManager().saveState(id);
		
};

       

