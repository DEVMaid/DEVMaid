/*
This script is being called by the inner html for initializing the terminal
It must be placed after the jquery.terminal*.js script
*/

function init(rpcDest) {
  jQuery(document).ready(function($) {
    var id = 1;
    $('body').terminal(function(command, term) {
                $.jrpc(rpcDest,
                       "terminal",
                       [command],
                       function(data) {
                           //alert("data: " + data);
                           term.resume();
                           if (data.error && data.error.message) {
                               term.error(data.error.message);
                           } else {
                               if (typeof data.result == 'boolean') {
                                   term.echo(data.result ? 'success' : 'fail');
                               } else {
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
        greetings: "multiply terminals demo use help to see available commands",
        onBlur: function() {
            // prevent loosing focus
            return false;
        }
    });
  });
	
}
