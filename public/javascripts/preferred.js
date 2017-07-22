const map = startMap("map_canvas");
addAutocompleteToDestination("end");

let global_result;

// this function submits a preferred route for a given user
$("#preferred").click(function(){
  // POST global_result to the API
})

// starts a new map centered at UFABC LatLng
function startMap() {
  const start = new google.maps.LatLng(-23.644359,-46.528339);
  const myOptions = {
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    center: start,
    zoom: 16
  }
  return new google.maps.Map(document.getElementById("map_canvas"), myOptions)
}

// transform input from select to UFABC gate coordenate
const resolveInputToCoordenate = function (coordenate) {
  return {
    '1' : '-23.643108, -46.529357'
  }[coordenate]
}

// deal with changes on start and end input fields
const onChangeHandler = function() {
  // get start and end files
  const start = $("#start").val();
  const end = $("#end").val();
  // check if its correct
  if (start == 0 || end == "") {
    return;
  }
  // find start coordenates
  const start_coords = resolveInputToCoordenate(start);
  // find routes and render
  requestAndRenderDirection(start_coords, end)
}

$('#start').change(onChangeHandler)
$('#end').change(onChangeHandler)

// add autocomplete from google to an element id
function addAutocompleteToDestination(id_destination) {
  const defaultBounds = new google.maps.LatLngBounds(
    new google.maps.LatLng(-23.044359, -46.028339),
    new google.maps.LatLng(-24, -47))

  const options = { bounds: defaultBounds}
  const autocompleteOn = document.getElementById(id_destination)
  const autocomplete = new google.maps.places.Autocomplete(autocompleteOn, options)
}

// request and render the direction
// also adds a listener to track changes made by the user
// and save them in a global: global_result
// this is necessary because the user can drag routes
function requestAndRenderDirection(start, end) {
  const directionsService = new google.maps.DirectionsService;
  const directionsDisplay = new google.maps.DirectionsRenderer;

  directionsService.route({ 
    origin: start, 
    destination: end, 
    travelMode: google.maps.DirectionsTravelMode.WALKING
  }, function(result) {
    global_result = result

    const directionsRenderer = new google.maps.DirectionsRenderer({
      draggable: true,
      map: map,
      directions: result
    });

    directionsRenderer.addListener('directions_changed', function() {
      global_result = directionsRenderer.getDirections();
    })
  });
}