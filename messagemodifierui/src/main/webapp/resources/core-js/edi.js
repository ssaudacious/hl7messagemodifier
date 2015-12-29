/**
 * edi.js
 * 
 * Building upon and improving the CodeMirror 2 XML parser
 * @author: Harold AFFO
 * @date: 31 July, 2012
 */

CodeMirror.defineMode("edi", function(config, parserConfig) {
			var separators = parserConfig.separators;			
			return {
			    token: function(stream,state) {
			      var ch = stream.next();
			      if(stream.column() <= 2){
 			    	  return "edi-segment-name";
			      }else{
			    	 
			     	  if(separators != null){
  			     		  
			    		  if(ch == separators.field_separator){
			    			  return "edi-field-separator";
			    		  }
			    		  
			    		  if(ch == separators.component_separator){
			    			  return "edi-component-separator";
			    		  }
			    		  
			    		  if(ch == separators.subcomponent_separator){
			    			  return "edi-subcomponent-separator";
			    		  }
			    		  
			    		  if(ch == separators.continuation_separator){
			    			  return "edi-continuation-separator";
			    		  }
			    	  }else{
			    		  return "";
			    	  }
			      }
 			      } 
			 };
});

CodeMirror.defineMIME("text/edi", "edi");
