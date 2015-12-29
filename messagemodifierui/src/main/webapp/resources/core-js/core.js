var phtool_Core = {	
		/**
		 * 
		 * execute the global function with name fname 
		 * @param fname
		 */
		 execfn : function(fname){
			var fn = window[fname];
			if(typeof fn === 'function') {
				fn();
			} 
		},
		/**
		 * return the global variable with name varname 
		 * @param varname
		 * @returns
		 */
		execvar:function(varname){
			return window[varname]; 
		}	

};


