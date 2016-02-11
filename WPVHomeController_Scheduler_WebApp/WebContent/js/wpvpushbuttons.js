

function resetTimeButtons() {		
	$('a#btnTime1').toggleClass("active", false);
	$('a#btnTime5').toggleClass("active", false);
	$('a#btnTime10').toggleClass("active", false);
	$('a#btnTime15').toggleClass("active", false);
	$('a#btnTime20').toggleClass("active", false);
}

function resetZoneButtons() {		
	$('a#btnZone2').toggleClass("active", false);
	$('a#btnZone3').toggleClass("active", false);
	$('a#btnZone4').toggleClass("active", false);
	$('a#btnZone5').toggleClass("active", false);
	$('a#btnZone6').toggleClass("active", false);
}

$(document).ready(function(){
      
    $('a.btnTime').click(function() {
    	resetTimeButtons();
    	$(this).toggleClass("active");
    	document.getElementById("time").value = this.text;    	
      });
    
    $('a.btnZone').click(function() {
    	resetZoneButtons();
    	$(this).toggleClass("active");    	
    	document.getElementById("zone").value = this.text;
    	document.getElementById("valve").value = this.id.substring(7);
      });
    
    $('a#btnTime15').click();
    $('a#btnZone3').click();
});