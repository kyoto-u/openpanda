function selectionAuthor(id_, className, anchor){
	var x1, x2, y1, y2;
	
	var id = id_;

	//Variable indicates wether a mousedown event within your select happend or not
	var isSelect = false;
	var isActive = false;

	// Global mouse button variables
	var gMOUSEUP = false;
	var gMOUSEDOWN = false;
	
	var anchorJObj = $('#'+anchor);
	//look for image and set container width and height	
	/*if(anchorJObj.find('img').attr('src') != '/access/content')
	{
		anchorJObj.css('width', anchorJObj.find('img').width()+'px');
		anchorJObj.css('height', anchorJObj.find('img').height()+'px');
		anchorJObj.css('background-image', 'url("' + anchorJObj.find('img').attr('src') + '")');
	}*/
				
	var div = document.createElement('div');
	div.setAttribute('id', id);
	div.className = className.selectionClass;
	anchorJObj.append(div);
	
	var divJObj = $(div);
	//divJObj.draggable();
	
	var span = document.createElement('span');
	//div.setAttribute('id', id);
	span.className = className.textClass;
	divJObj.append(span);
	
	var spanObj = $(span);
	//spanObj.html(id);
	
	// Global Events if left mousebutton is pressed or nor (usability fix)
	$(document).mouseup(function() {
		gMOUSEUP = true;
		gMOUSEDOWN = false;
	});
	$(document).mousedown(function() {
		gMOUSEUP = false;
		gMOUSEDOWN = true;
	});
	$(window).mouseup(function() {
		gMOUSEUP = true;
		gMOUSEDOWN = false;
	});

	// select frame (playground :D)
	anchorJObj.mousedown(function(e) {
		if(isActive) 
		{
			isSelect = true;
			// store mouseX and mouseY
			//x1 = e.pageX;
			//y1 = e.pageY;
			x1 = e.pageX-anchorJObj.offset().left;
			y1 = e.pageY-anchorJObj.offset().top;
			x2 = x1;
			y2 = y1;
		}
	});

	// If select is true (mousedown on select frame) the mousemove 
	// event will draw the select div
	anchorJObj.mousemove(function(e) {
		if(isActive) 
		{
			if (isSelect) {
				// Store current mouseposition
				//x2 = e.pageX;
				//y2 = e.pageY;
				x2 = e.pageX-anchorJObj.offset().left;
				y2 = e.pageY-anchorJObj.offset().top;

				move();
			}
		}
	});
	// select complete, hide the select div (or fade it out)
	anchorJObj.mouseup(function() {
		if(isActive) 
		{
			if (typeof x1 != 'undefined' && typeof y1 != 'undefined' && x1 == x2 && y1 == y2) {
				// we just clicked and didn't do a mousemove
				var reset;
				x1 = x2 = y1 = y2 = reset;
				divJObj.hide();
			}
			isSelect = false;			
			//divJObj.hide();
		}
	});
	// Usability fix. If mouse leaves the select and enters the select frame again with mousedown
	anchorJObj.mouseenter(function() {
		if(isActive) 
			isSelect = gMOUSEDOWN;
	});
	// Usability fix. If mouse leaves the select and enters the select div again with mousedown
	divJObj.mouseenter(function() {
		if(isActive) 
			isSelect = gMOUSEDOWN;
	});
	// Set select to false, to prevent further select outside of your select frame
	anchorJObj.mouseleave(function() {
		if(isActive) 
			isSelect = false;
	});
	$('body').mouseleave(function(){
		if(isActive)
			isSelect = false;
		anchorJObj.mouseup();
	});
	$(document).mouseleave(function(){
		if(isActive)
			isSelect = false;
		anchorJObj.mouseup();
	});
	
	this.getId = function(){
		return id;
	}
	
	this.setActive = function(val){
		isActive = val;
		div.className = className.selectionClass+((val) ? '_selected' : '');
	}
	
	this.setText = function(text) {
		spanObj.html(text);
	}
	
	this.getCoords = function(){
		if(x1 === null || x1 === undefined || x2 === null || x2 === undefined || y1 === null || y1 === undefined || y2 === null || y2 === undefined) return null;
		var x1_ = (x1 < x2) ? x1 : x2;
		var x2_ = (x1 < x2) ? x2 : x1;
		var y1_ = (y1 < y2) ? y1 : y2;
		var y2_ = (y1 < y2) ? y2 : y1;
		return {x1: parseInt(x1_), y1: parseInt(y1_), x2: parseInt(x2_), y2: parseInt(y2_)};
	}	
	
	this.setCoords = function(coords){
		if(coords != null && coords.x1 != null && coords.y1 != null && coords.x2 != null && coords.y2 != null)
		{			
			x1 = coords.x1;
			x2 = coords.x2;
			y1 = coords.y1;
			y2 = coords.y2;
			
			move();
		}
	}
	
	this.remove = function()
	{
		divJObj.remove();
	}
	
	function move(){
		// Calculate the div select rectancle for positive and negative values				
		/*var TOP = (y1 < y2) ? y1 : y2;
		var LEFT = (x1 < x2) ? x1 : x2;
		var WIDTH = (x1 < x2) ? x2 - x1 : x1 - x2;
		var HEIGHT = (y1 < y2) ? y2 - y1 : y1 - y2;*/
	
		var TOP = parseInt(Math.max(0, (y1 < y2) ? y1 : y2));
		var LEFT = parseInt(Math.max(0, (x1 < x2) ? x1 : x2));
		var WIDTH = parseInt((x1 < x2) ? Math.min(anchorJObj.width()-x1, x2-x1) : Math.min(anchorJObj.width()-x2, x1-x2));
		var HEIGHT = parseInt((y1 < y2) ? Math.min(anchorJObj.height()-y1, y2-y1) : Math.min(anchorJObj.height()-y2, y1-y2));
			
		// Use CSS to place your select div
		divJObj.css({
			position: 'absolute',
			zIndex: 5000,
			left: LEFT,
			top: TOP,
			width: WIDTH,
			height: HEIGHT
		});
		divJObj.show();
	}
}
