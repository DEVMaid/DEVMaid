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

var all_my_commands;

function convertToArray(str, delimiter) {
  var strArray = str.split(delimiter);
  var resArray = [];
  for (i = 0; i < strArray.length; i++) { 
    resArray.push(strArray[i]);
  }
  return resArray;
}

function init(rpcDest, userNameAtHost, initPrompt, userHomeDir, initFilesDirsName) {
  jQuery(document).ready(function($) {
    var id = 1;
    $('body').terminal(function(command, term) {
                $.jrpc(rpcDest,
                       "terminal",
                       [$('#hidCurrentWorkingDir').val(), command],
                       function(data) {
                           term.resume();
                           //var cbP = beautifyPrompt(userHomeDir, $('#hidCurrentWorkingDir').val());
                           //term.echo(userNameAtHost+":"+cbP+"$ " + command);
                           if (data.errorMsg) {
                               term.error(data.errorMsg);
                           } else {
                               if (typeof data.result == 'boolean') {
                                   term.echo(data.result ? 'success' : 'fail');
                               }
                               if(data.result && data.result.length > 0) {
                                   term.echo(data.result);
                               }
                              if(data.resultWorkingDir && data.resultWorkingDir.length>0) {
                                  //Update the prompt if there is any
                                  var bP = beautifyPrompt(userHomeDir, data.resultWorkingDir);
                                  term.set_prompt(userNameAtHost+":"+bP+"$ ");
                                  $('#hidCurrentWorkingDir').val(data.resultWorkingDir);
                               }
                               if(data.filesDirsName) {
                                all_my_commands = convertToArray(data.filesDirsName, ",");
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
        tabcompletion : true,
        completion: function(terminal, command, callback) {
          all_my_commands = convertToArray(initFilesDirsName, ",");
          //alert("all_my_commands: "+all_my_commands);
          callback(all_my_commands);
        }, 
        prompt: initPrompt + "$ ", 
        onBlur: function() {
            // prevent loosing focus
            return false;
        }
    });
  });
	
}
