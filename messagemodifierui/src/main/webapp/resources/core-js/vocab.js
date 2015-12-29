
/**
 * 
 * @returns
 */
function phtool_vocab(vocabObject){
	this.jsonObject = vocabObject; 
	this.tab_loaded = false;
	$("ul#mainNav").append("<li><a id=\""+ this.jsonObject.moduleName + "_tab\" class=\"jslink module_tab\" onclick=\"phtool_getManager().getModule('"+ this.jsonObject.moduleName+ "').goNav('"+ this.jsonObject.targetId+"')\" title=\""+this.jsonObject.description+"\">"+this.jsonObject.title+"</a></li>");
}; 


 
/**
 * allow navigation from one page to another
 */
phtool_vocab.prototype.goNav=function (id){
	this.goNav(id, false); 
};


/**
 * 
 * @param id
 */
phtool_vocab.prototype.goNav=function (id, override) {
			 $("a.module_tab").removeClass('current jslink').addClass('jslink');
			 $(".page").removeClass("hidden").hide(); 
 
			 if (id == this.jsonObject.targetId){
		            $("#"+this.jsonObject.moduleName +"_tab").removeClass('jslink').addClass('current jslink'); 
		            $('#subNavBar').html("<ul id=\"mainSubNav\" class=\"clearfix\"></ul>");  
		            $("#"+this.jsonObject.targetId).show();
		            if(!this.tab_loaded){
		                loadTableTab();
		                this.tab_loaded = true;
		            } 
		     } 
			 phtool_getManager().saveState(id);
};
        

