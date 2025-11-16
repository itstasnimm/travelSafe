// document.addEventListener("DOMContentLoaded", function () {
//     const trackId = window.trackId;
  
//     function sendLocation() {
//       if (!navigator.geolocation) return;
  
//       navigator.geolocation.getCurrentPosition(pos => {
//         const params = new URLSearchParams();
//         params.append("latitude", pos.coords.latitude);
//         params.append("longitude", pos.coords.longitude);
  
//         fetch(`/journey/track/${trackId}/update-location`, {
//           method: "POST",
//           headers: { "Content-Type": "application/x-www-form-urlencoded" },
//           body: params.toString()
//         }).catch(err => console.error("Failed to send location:", err));
//       });
//     }
  
   
//     sendLocation(); // immediately
//     setInterval(sendLocation, 300000); // every 3mins

//   });


document.addEventListener("DOMContentLoaded", function () {
  const trackId = window.trackId;

  function sendToServer(lat, lng) {
    const params = new URLSearchParams();
    params.append("latitude", lat);
    params.append("longitude", lng);

    fetch(`/journey/track/${trackId}/update-location`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params.toString()
    }).catch(err => console.error("Failed to send location:", err));
  }

  // REAL-TIME TRACKING
  navigator.geolocation.watchPosition(
    pos => {
      const lat = pos.coords.latitude;
      const lng = pos.coords.longitude;
      console.log("Live update:", lat, lng);
      sendToServer(lat, lng);
    },
    err => console.error(err),
    {
      enableHighAccuracy: true,
      maximumAge: 0,
      timeout: 10000
    }
  );
});


  
document.getElementById("findSafePlace").addEventListener("click", () => {
  fetch(`/journey/track/${trackId}/nearest-safehouse`)
    .then(res => res.json())
    .then(data => {
      console.log("Safe places response:", data);
      const resultsDiv = document.getElementById("safePlaceResults");
      resultsDiv.innerHTML = ""; // clear old results

      if (data.length === 0) {
        resultsDiv.textContent = "No safe places found nearby.";
        return;
      }

      data.forEach(place => {
        const p = document.createElement("p");
        p.textContent = `${place.pname} — ${place.pdistance} km away`;
        resultsDiv.appendChild(p);
      });
    })
    .catch(err => {
      console.error("Error fetching safe places:", err);
      document.getElementById("safePlaceResults").textContent =
        "Failed to fetch safe places ❌";
    });
});
