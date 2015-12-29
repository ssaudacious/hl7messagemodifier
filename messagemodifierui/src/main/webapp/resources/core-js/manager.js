function phtool($jsonObject) {	
	this.$jsonObject = null;
};  

/**
 * 
 */
phtool.prototype.init = function($jsonObject){
	this.$jsonObject = $jsonObject ;
	if(!$jsonObject){
		alert("No config info found. Please add configuration info");
		return ;
	}
		
	//home
 	if($jsonObject.home){
		this.createhomeInstance($jsonObject.home);
	} 
 
	//context free
	var cfs = $jsonObject.cf;
	if(cfs){
		for(var index = 0; index < cfs.length; index++){
 			this.createcfInstance(cfs[index]);
		}
	} 
	
	//context based
	var cbs = $jsonObject.cb;
	if(cbs){
		for(var index = 0; index < cbs.length; index++){
 			this.createcbInstance(cbs[index]);
		}
	}
	
	//profile
	var profileViewer =$jsonObject.profileViewer;
 	if(profileViewer){
 		this.createProfileModuleInstance(profileViewer);
	} 
 
 	//vocab
 	if($jsonObject.vocab){
		this.createVocabModuleInstance($jsonObject.vocab);
	} 
 	
 	if($jsonObject.home){
 		this.getModule($jsonObject.home.moduleName).goNav($jsonObject.home.targetId,true);
 	} 
 	
 	//documentation
 	if($jsonObject.documentation){
		this.createdocInstance($jsonObject.documentation);
	} 
 	
 	if($jsonObject.setting){
		this.createSettingModuleInstance($jsonObject.setting);
	} 
 	

 	if($jsonObject.about){
		this.createAboutModuleInstance($jsonObject.about);
	} 
 
}; 


/**
 * create documentation module instance
 * @returns
 */
phtool.prototype.createdocInstance = function(moduleName){
	return  $('div#tool_data').data(moduleName,new phtool_doc(moduleName));
};

/**
 * 
 * @returns
 */
phtool.prototype.createVocabModuleInstance = function (jsonObject){
	return  $('div#tool_data').data(jsonObject.moduleName,new phtool_vocab(jsonObject));
};


/**
 * 
 * @returns
 */
phtool.prototype.createhomeInstance = function(homeObject){
	return  $('div#tool_data').data(homeObject.moduleName,new phtool_home(homeObject));
};




//***** Context free module creation call ***** /
/**
 * 
 * @param moduleName
 * @returns
 */
phtool.prototype.createcfInstance = function(cfObject){
	if(!cfObject.moduleName){
		alert("No name found for the context free module. Please provide the module name before continuing");
		return ;
	}
 	 $('div#tool_data').data(cfObject.moduleName,new phtool_cf(cfObject));
};

 


//***** Context based module creation call ***** /

/**
 * 
 * @param moduleName
 * @returns
 */
phtool.prototype.createcbInstance = function(cbObject){
	if(!cbObject.moduleName){
		alert("No name found for the context based module. Please provide the module name before continuing");
		return ;
	}
 	$('div#tool_data').data(cbObject.moduleName,new phtool_cb(cbObject));
};

 
//***** Profile module creation call ***** /

/**
 * 
 * @returns
 *//*
phtool.prototype.createProfileModuleInstance = function (moduleName){
	return  $('div#tool_data').data(moduleName,new phtool_profile(moduleName));
} */

phtool.prototype.createProfileModuleInstance = function (profileViewerObject){
	return  $('div#tool_data').data(profileViewerObject.moduleName,new phtool_profile(profileViewerObject));
}; 

phtool.prototype.createdocInstance = function (docObject){
	return  $('div#tool_data').data(docObject.moduleName,new phtool_doc(docObject));
};

phtool.prototype.createSettingModuleInstance = function (settingObject){
	return  $('div#tool_data').data(settingObject.moduleName,new phtool_setting(settingObject));
};
phtool.prototype.createAboutModuleInstance = function (aboutObject){
	return  $('div#tool_data').data(aboutObject.moduleName,new phtool_about(aboutObject));
};

phtool.prototype.createCopyrightModuleInstance = function (copyRightObject){
	return  $('div#tool_data').data(copyRightObject.moduleName,new phtool_copyright(copyRightObject));
};


 
/**
 * 
 */
phtool.prototype.getModule = function(moduleName){
	if(!moduleName){
		alert("No name found for module. Please provide the module name before continuing");
		return ;
	}
	return $('div#tool_data').data(moduleName);
};




/**
 * 
 * @returns
 */
function phtool_getManager(){
	return  $('div#tool_data').data('manager');	
}  

/**
 * 
 */
function phtool_createManager(jsonConfig){
		$('div#tool_data').data('manager',new phtool()); 
		phtool_getManager().init(jsonConfig);
}

/**
 * 
 */
phtool.prototype.back = function(){
	/* var State = History.getState(); // Note: We are using History.getState() instead of event.state
	 if(State){
		 		var id = State.data.id;
		 		if(id){
  	   			if(id.indexOf("cb") != -1){
		 			getcbInstance().goNav(id);
		 		}else if(id.indexOf("cf") != -1){
		 			getcfInstance().goNav(id);
		 		}else if(id.indexOf("profile") != -1){
		 			getProfileModuleInstance().goNav(id);		
		 		}else if(id.indexOf("vocab") != -1){
		 			getVocabModuleInstance().goNav(id);
		 		}else {
		 			getHomeInstance().goNav("home");
		 		} 
		 		}else{
		 			getHomeInstance().goNav("home");
		 		}
	}else{
		getHomeInstance().goNav("home");
	}*/
}; 

/**
 * used to save the current navigation tab
 * @param id
 */
phtool.prototype.saveState = function(id){
/*	if ( !History.enabled ) {
	        return false;
	}
	var tool_name = $("span#tool-name").val();
	
	History.pushState({id:id},tool_name + " Validation Suite", "?nav="+id);*/
}; 


/**
 * 
 * execute the global function with name fname 
 * @param fname
 */
phtool.prototype.execute = function(fname){
	var fn = window[fname];
	if(typeof fn === 'function') {
		fn();
	}else{
		return window[fname]; 
	} 
}; 


phtool.prototype.deleteDiv= function (id){
	$("div[id$='"+id+"']").remove();
};


 




