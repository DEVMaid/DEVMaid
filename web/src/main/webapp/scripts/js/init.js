/*
This script is being called by the inner html for initializing the terminal
It must be placed after the jquery.terminal*.js script

Author: Ken Wu

*/

function beautifyPrompt(userHomeDir, newCurrentPath) {
  var rPath = newCurrentPath.replace(userHomeDir, "~/");
  rPath = rPath.replace("//", "/");
  if(rPath.substring(rPath.length-1)=="/"){
    return rPath.substring(0, rPath.length-1);
  } else {
    return rPath;
  }
}

function init(rpcDest, userNameAtHost, initPrompt, userHomeDir) {
  jQuery(document).ready(function($) {
    var id = 1;
    $('body').terminal(function(command, term) {
                $.jrpc(rpcDest,
                       "terminal",
                       [$('#hidCurrentWorkingDir').val(), command],
                       function(data) {
                           //alert("data: " + data);
                           term.resume();
                           if (data.error && data.error.message) {
                               term.error(data.error.message);
                           } else {
                               if (typeof data.result == 'boolean') {
                                   term.echo(data.result ? 'success' : 'fail');
                               }
                               if(data.resultWorkingDir && data.resultWorkingDir.length>0) {
                                  //Update the prompt if there is any
                                  var bP = beautifyPrompt(userHomeDir, data.resultWorkingDir);
                                  term.set_prompt(userNameAtHost+":"+bP+"$ ");
                                  $('#hidCurrentWorkingDir').val(data.resultWorkingDir);
                               }
                               if(data.result && data.result.length > 0) {
                                   term.echo(data.result);
                               }
                           }
                       },
                       function(xhr, status, error) {
                           term.error('[AJAX] ' + status +
                                      ' - Server reponse is: \n' +
                                      xhr.responseText);
                           term.resume();
                       });
        
    }, {
        greetings: "Welcome to DEVMaid Web Terminal at " + userNameAtHost,
        prompt: initPrompt + "$ ", 
        onBlur: function() {
            // prevent loosing focus
            return false;
        }
    });
  });
	
}
