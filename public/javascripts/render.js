window.fbAsyncInit = function() {
  FB.init({
    appId      : '109836813005590',
    cookie     : true,
    xfbml      : true,
    version    : 'v2.8'
  });
  FB.AppEvents.logPageView();   
};

(function(d, s, id){
 var js, fjs = d.getElementsByTagName(s)[0];
 if (d.getElementById(id)) {return;}
 js = d.createElement(s); js.id = id;
 js.src = "//connect.facebook.net/en_US/sdk.js";
 fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

function checkLoginState() {
  FB.getLoginStatus(function(response) {
    if(response.status === 'connected') {
      fetch("/api/facebook",
      {
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        body: {access_token: response.authResponse.accessToken}
      })
      .then(res => res.json().then(body => console.log(parseJwt(body.token))))
      .then(console.log)
    }
  });
}

const parseJwt = function (token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace('-', '+').replace('_', '/');
  return JSON.parse(window.atob(base64));
};

const serialize = function(obj) {
  let str = [];
  for(let p in obj)
    if (obj.hasOwnProperty(p)) {
      if (obj[p] instanceof Array) {
        str.push(encodeURIComponent(p) + "=" + obj[p].join(","))
      } else {
        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
      }
    }
    return str.join("&");
}


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

   function renderDirections(result, draggable) {
     var randomColor = colors[Math.floor(Math.random()*colors.length)];
     var polylineOptions = new 
     google.maps.Polyline({ strokeColor: randomColor, strokeWeight: 3 }); 
     var directionsRenderer = new google.maps.DirectionsRenderer(
     { 
      //polylineOptions: polylineOptions,
      draggable: draggable 
      }); 

     if(draggable) {
      directionsRenderer.addListener('directions_changed', function() {
        console.log(directionsRenderer.getDirections())
      })
     }
     directionsRenderer.setMap(map); 
     directionsRenderer.setDirections(result); 
   }     

   function requestDirections(start, end, draggable = false) { 
    directionsService.route({ 
      origin: start, 
      destination: end, 
      travelMode: google.maps.DirectionsTravelMode.WALKING
    }, function(result) {
      renderDirections(result, draggable); 
    }); 
  } 

  //requestDirections('-23.643108, -46.529357', 'rua acre, 85'); 
  requestDirections('-23.643108, -46.529357', 'avenida estados unidos, 100, santo andré', true);  
  //requestDirections('-23.643108, -46.529357', 'rua aimbere, 85, santo andre'); 
}

drawMap()