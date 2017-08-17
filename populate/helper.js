window.test = null

function drawMap() {

   var directionsService = new google.maps.DirectionsService();

   function requestDirections(start, end, draggable = false) { 
    directionsService.route({ 
      origin: start, 
      destination: end, 
      travelMode: google.maps.DirectionsTravelMode.WALKING
    }, function(result) {
      const coords = result.routes[0].legs[0].steps.reduce(function(arr, step) {
      step.path.map(function(path) {
        arr.push({
          lat: path.lat(),
          lng: path.lng()
        })
      })

      return arr
    }, [])

     window.test = window.test || []

     window.test.push({
      user_id: "replacethis",
      coords: coords,
      time: new Date().getTime()
     })
    }); 
  } 

  //requestDirections('-23.643108, -46.529357', 'rua acre, 85'); 
  requestDirections('-23.643108, -46.529357', 'avenida estados unidos, 100, santo andré', true);  
  requestDirections('-23.643108, -46.529357', 'rua aimbere, 85, santo andre');
  requestDirections('-23.643108, -46.529357', 'rua japao, 100, santo andre');
  requestDirections('-23.643108, -46.529357', 'rua rio grande do norte, 580, santo andre');
  requestDirections('-23.643108, -46.529357', 'rua avare, 162, santo andre');
  
}

drawMap();
copy(window.test)