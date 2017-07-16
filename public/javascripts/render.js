function drawMap() {

	var directionDisplay;
	var directionsService = new google.maps.DirectionsService();
	var map;

	var start = new google.maps.LatLng(-23.644359,-46.528339);
	var myOptions = {
	  mapTypeId: google.maps.MapTypeId.ROADMAP,
	  center: start
	}

	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

	const colors = ['red', 'blue', 'green', 'black', 'yellow', 'orange']

	function renderDirections(result) {
	    var randomColor = colors[Math.floor(Math.random()*colors.length)];
	    var polylineOptions = new 
	        google.maps.Polyline({ strokeColor: randomColor, strokeWeight: 3 }); 
	    var directionsRenderer = new google.maps.DirectionsRenderer({ polylineOptions: polylineOptions }); 
	    directionsRenderer.setMap(map); 
	    directionsRenderer.setDirections(result); 
	  }     

	function requestDirections(start, end) { 
	  directionsService.route({ 
	    origin: start, 
	    destination: end, 
	    travelMode: google.maps.DirectionsTravelMode.WALKING 
	  }, function(result) {
	  console.log(result) 
	    renderDirections(result); 
	  }); 
	} 

	requestDirections('-23.643108, -46.529357', 'rua acre, 85'); 
	requestDirections('-23.643108, -46.529357', 'avenida estados unidos, 100, santo andré');  
	requestDirections('-23.643108, -46.529357', 'rua aimbere, 85, santo andre'); 
}

drawMap()