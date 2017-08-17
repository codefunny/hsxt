/* 
 * 修正 万华成
 * 2016-02-04  解决电商合并单文件上传问题
 *
 */
jQuery.extend({
	handleError: function( s, xhr, status, e )      {  
        // If a local callback was specified, fire it  
                if ( s.error ) {  
                    s.error.call( s.context || s, xhr, status, e );  
                }  
  
                // Fire the global callback  
                if ( s.global ) {  
                    (s.context ? jQuery(s.context) : jQuery.event).trigger( "ajaxError", [xhr, s, e] );  
                }  
    },

    createUploadIframe: function(id, uri)  
    {  
    
        var frameId = 'jUploadFrame' + id;  
          
        if(window.ActiveXObject) {  
           if(!$.support.leadingWhitespace)  
            {  
              
                var io = document.createElement('<iframe id="' + frameId + '" name="' + frameId + '" />');  
                if(typeof uri== 'boolean'){  
                    io.src = 'javascript:false';  
                }  
                else if(typeof uri== 'string'){  
                    io.src = uri;  
                }  
            }else{
            	 io = document.createElement('iframe');  
                 io.id = frameId;  
                 io.name = frameId;  
            }  
        }  
        else {  
            var io = document.createElement('iframe');  
            io.id = frameId;  
            io.name = frameId;  
        }  
        io.style.position = 'absolute';  
        io.style.top = '-1000px';  
        io.style.left = '-1000px';  
  
        document.body.appendChild(io);  
  
        return io;        
    },
    createUploadForm: function(id, fileElementId, data)
	{
		//create form	
		var formId = 'jUploadForm' + id;
		var fileId = 'jUploadFile' + id;
		var form = jQuery('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');	
		if(data)
		{
			for(var i in data)
			{
				jQuery('<input type="hidden" name="' + i + '" value="' + data[i] + '" />').appendTo(form);
			}			
		}
		
		if (typeof fileElementId ==='object' && fileElementId.length > 0 ){
			
			
		} else {
			
			
		}
		
		if(typeof(fileElementId) == 'string'){
            fileElementId = [fileElementId];
        }
         for (var f = 0 ; f < fileElementId.length ;f++){
        	var oldElement = jQuery("input[name="+fileElementId[f]+"]");            
            	oldElement.each(function() {
	                var newElement = jQuery($(this)).clone();
	                jQuery(oldElement).attr('id', fileId);
	                jQuery(oldElement).before(newElement);
	                jQuery(oldElement).appendTo(form);
            	});   
        }
	 
		//set attributes
		jQuery(form).css('position', 'absolute');
		jQuery(form).css('top', '-1200px');
		jQuery(form).css('left', '-1200px');
		jQuery(form).appendTo('body');		
		return form;
    },

    ajaxFileUpload: function(s) {
        // TODO introduce global settings, allowing the client to modify them for all requests, not only timeout		
        s = jQuery.extend({}, jQuery.ajaxSettings, s);
        var id = new Date().getTime()        
		var form = jQuery.createUploadForm(id, s.fileElementId, (typeof(s.data)=='undefined'?false:s.data));
		var io = jQuery.createUploadIframe(id, s.secureuri);
		var frameId = 'jUploadFrame' + id;
		var formId = 'jUploadForm' + id;		
        // Watch for a new set of requests
        if ( s.global && ! jQuery.active++ )
		{
			jQuery.event.trigger( "ajaxStart" );
		}            
        var requestDone = false;
        // Create the request object
        var xml = {}   
        if ( s.global )
            jQuery.event.trigger("ajaxSend", [xml, s]);
        // Wait for a response to come back
        var uploadCallback = function(isTimeout)
		{			
			var io = document.getElementById(frameId);
            try 
			{				
				if(io.contentWindow)
				{
					 xml.responseText = io.contentWindow.document.body?io.contentWindow.document.body.innerHTML:null;
                	 xml.responseXML = io.contentWindow.document.XMLDocument?io.contentWindow.document.XMLDocument:io.contentWindow.document;
					 
				}else if(io.contentDocument)
				{
					 xml.responseText = io.contentDocument.document.body?io.contentDocument.document.body.innerHTML:null;
                	xml.responseXML = io.contentDocument.document.XMLDocument?io.contentDocument.document.XMLDocument:io.contentDocument.document;
				}						
            }catch(e)
			{
				jQuery.handleError(s, xml, null, e);
			}
            if ( xml || isTimeout == "timeout") 
			{				
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if ( status != "error" )
					{
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = jQuery.uploadHttpData( xml, s.dataType );    
                        // If a local callback was specified, fire it and pass it the data
                        if ( s.success )
                            s.success( data, status );
    
                        // Fire the global callback
                        if( s.global )
                            jQuery.event.trigger( "ajaxSuccess", [xml, s] );
                    } else
                        jQuery.handleError(s, xml, status);
                } catch(e) 
				{
                    status = "error";
                    jQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if( s.global )
                    jQuery.event.trigger( "ajaxComplete", [xml, s] );

                // Handle the global AJAX counter
                if ( s.global && ! --jQuery.active )
                    jQuery.event.trigger( "ajaxStop" );

                // Process result
                if ( s.complete )
                    s.complete(xml, status);

                jQuery(io).unbind()

                setTimeout(function()
									{	try 
										{
											jQuery(io).remove();
											jQuery(form).remove();	
											
										} catch(e) 
										{
											jQuery.handleError(s, xml, null, e);
										}									

									}, 100)

                xml = null

            }
        }
        // Timeout checker
        if ( s.timeout > 0 ) 
		{
            setTimeout(function(){
                // Check to see if the request is still happening
                if( !requestDone ) uploadCallback( "timeout" );
            }, s.timeout);
        }
        try 
		{

			var form = jQuery('#' + formId);
			jQuery(form).attr('action', s.url);
			jQuery(form).attr('method', 'POST');
			jQuery(form).attr('target', frameId);
            if(form.encoding)
			{
				jQuery(form).attr('encoding', 'multipart/form-data');      			
            }
            else
			{	
				jQuery(form).attr('enctype', 'multipart/form-data');			
            }			
            jQuery(form).submit();

        } catch(e) 
		{			
            jQuery.handleError(s, xml, null, e);
        }
		
		jQuery('#' + frameId).load(uploadCallback	);
        return {abort: function () {}};	

    },

    uploadHttpData: function( r, type ) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if ( type == "script" )
            jQuery.globalEval( data );
        // Get the JavaScript object, if JSON is used.
        if ( type == "json" ){
        	 //creat by  2015/3/31  
        	 //ajaxfileupload会把服务器响应的Text内容加上<pre></pre>前后缀,会导致json转换失败而报服务器端错误
        	 //修改处理掉<pre></pre>  begin  
        	 data=data.replace(/<pre.*?>/g,'');
        	 data=data.replace('<pre>','');
        	 data=data.replace('</pre>','');
        	 data=data.replace(/<PRE.*?>/g,'');
        	 data=data.replace('<PRE>','');
        	 data=data.replace('</PRE>','');
        	//修改处理掉<pre></pre>  end  
        	 
        	 eval( "data = " + data );
        }
        // evaluate scripts within html
        if ( type == "html" )
            jQuery("<div>").html(data).evalScripts();

        return data;
    }
})
