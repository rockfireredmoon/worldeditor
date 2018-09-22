function we_resize(el, maxWidth, maxHeight) {

    var ratio = 0;  // Used for aspect ratio
    var width = el.width();    // Current image width
    var height = el.height();  // Current image height

    // Check if the current width is larger than the max
    if(width > maxWidth){
        ratio = maxWidth / width;   // get ratio for scaling image
        el.css("width", maxWidth); // Set new width
        el.css("height", height * ratio);  // Scale height based on ratio
        height = height * ratio;    // Reset height to match scaled image
        width = width * ratio;    // Reset width to match scaled image
    }

    // Check if current height is larger than max
    if(height > maxHeight){
        ratio = maxHeight / height; // get ratio for scaling image
        el.css("height", maxHeight);   // Set new height
        el.css("width", width * ratio);    // Scale width based on ratio
        width = width * ratio;    // Reset width to match scaled image
    }
}
//keep in mind, and i will explain, some of these "moving-parts" or not needed,
//but are added to show you the "ease" of jquery and help you see the solution

//This global function is designed simply to allow the creation of new
//checkboxes as you specified, however, if you won't be making check boxes at
//end user time, then i suggest simply moving it to within the .each statement
//found later on.
//Also, this could easily be written as a jQuery plugin so that you could make
//a "chainable" one-line call to change checkboxes to this but let's get to the
//nitty gritty first
function createCheckBox(ele, i) {
	// First I simply create the new ID here, of course you can do this inline, but
	// this gives us a bottleneck for possible errors
	var newID = "cbx-"+i;
	// below we use the param "ele" wich will be a jQuery Element object like
	// $("#eleID")
	// This gives us the "chainability" we want so we don't need to waste time
	// writing more lines to recall our element
	// You will also notice, the first thing i do is asign the "attribute" ID
	ele.attr({ "id": newID  })
	    // Here we see "chainability at work, by not closing the last line, we can
		// move right on to the next bit of code to apply to our element
	    // In this case, I'm changing a "property", keep in mind this is kinda new
		// to jQuery,
	    // In older versions, you would have used .attr but now jQuery distinguishes
		// between "attributes" and "properties" on elements (note we are using
		// "edge", aka. the latest jQuery version
	    .prop({ "type": "checkbox" })
	    // .after allows us to add an element after, but maintain our chainability
		// so that we can continue to work on the input
	    // here of course, I create a NEW label and then immidiatly add its "for"
		// attribute to relate to our input ID
	    .after($("<label />").attr({ for: newID  }))
	    // I should note, by changing your CSS and/or changing input to <button>,
		// you can ELIMINATE the previous step all together
	    // Now that the new label is added, lets set our input to be a button,
	    .button({ text: false }) // of course, icon only
	    // finally, let's add that click function and move on!
	    // again, notice jQuery's chainability allows us no need to recall our
		// element
	    .click(function(e) {
	        // FYI, there are about a dozen ways to achieve this, but for now, I'll
			// stick with your example as it's not far from correct
	        var toConsole = $(this).button("option", {
	            icons: {
	                primary: $(this)[0].checked ? "ui-icon-check" : ""
	            }
	        });
	        console.log(toConsole, toConsole[0].checked);
	    });
	// Finally, for sake of consoling this new button creation and showing you how
	// it works, I'll return our ORIGINAL (yet now changed) element
	return ele;
}

$(document).ready(function() {
	$('img.thumbnail').each(function() {
		var el = $(this);
		var bigSrc = el.attr('src');
	   $(this).qtip({
	      content: '<img src="' + bigSrc + '" alt=""/>', // Set the
															// height/width!!!
															// This can cause
															// positioning
															// problems if not
															// set
	      position: {
	    	  my: 'center left',
	    	  at: 'center right',
	    	  target: el,
	    	  adjust: {
	    		  method: 'shift'
	    	  }
	      }
	   });
	});	
    $('img.avatar').each(function() {
    	we_resize($(this), 64, 64);
    });
    

//    //  This .each call upon the inputs containing the class I asiged them in the html,
//    //  Allows an easy way to edit each input and maintain a counter variable
//    //  Thus the "i" parameter
//    //  You could also use your ORIGINAL HTML, just change $(".inp-checkbox") to $("input:[type='checkbox']") or even $("input:checkbox")
//    $("input[type='checkbox']").each(function(i) {
//        // as previously noted, we asign this function to a variable in order to get the return and console log it for your future vision!
//        var newCheckBox = createCheckBox($(this), i);
//        console.log(newCheckBox);
//    });

});

