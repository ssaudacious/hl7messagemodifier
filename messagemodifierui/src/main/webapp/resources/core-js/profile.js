function phtool_profile(profileViewerObject){ 
	this.moduleName = profileViewerObject.moduleName;
	this.profileObject = profileViewerObject.profile;
	this.dataTypeObject = profileViewerObject.dataType; 
 	$("ul#mainNav").append("<li><a id=\""+ this.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+this.moduleName+ "').goNav('"+this.profileObject.targetId+ "')\" title=\"Profile Viewer\">Profile Viewer</a></li>");
};

/**
 * allow navigation from one page to another
 */
phtool_profile.prototype.goNav=function (id){
	this.goNav(id, false); 
}, 

/**
 * 
 * @param id
 */
phtool_profile.prototype.goNav=function (id, override) {
 			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide(); 
			 pTagertId = this.profileObject.targetId;
			//PROFILE
			if (id == pTagertId){
	            $('#'+ this.moduleName  +'_tab').removeClass('jslink').addClass('current jslink'); 	           
	            if(this.dataTypeObject){
	            	  $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
	   	                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('"+pTagertId+"')\" title=\""+this.profileObject.description+"\">"+this.profileObject.title+"</a></li>" +
	   	                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('"+ this.dataTypeObject.targetId +"')\" title=\""+ this.dataTypeObject.description +"\">"+this.dataTypeObject.title+"</a></li>"+
 	   	            "</ul>");
	            }else{
	                $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">"+
		            "</ul>");
	            }
	            $('#'+pTagertId).show();
			} 
			
			//DATA TYPES
			if (id == this.dataTypeObject.targetId){
	            $('#'+ this.moduleName +'_tab').removeClass('jslink').addClass('current jslink'); 	           
	            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\">" +
   	                    "<li><a class=\"jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('"+pTagertId+"')\" title=\""+this.profileObject.description+"\">"+this.profileObject.title+"</a></li>" +
   	                    "<li><a class=\"current jslink\" onclick=\"phtool_getManager().getModule('"+this.moduleName+"').goNav('"+ this.dataTypeObject.targetId +"')\" title=\""+ this.dataTypeObject.description +"\">"+this.dataTypeObject.title+"</a></li>"+
	   	            "</ul>");
 	           $('#'+this.dataTypeObject.targetId).show();
			}
			phtool_getManager().saveState(id);
	 
};