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

    if (!trackId) {
        console.error("trackId is undefined! Check Thymeleaf injection.");
        return;
    }

    function sendLocation() {
        if (!navigator.geolocation) {
            console.error("Geolocation not supported by this browser.");
            return;
        }

        navigator.geolocation.getCurrentPosition(
            pos => {
                const latitude = pos.coords.latitude;
                const longitude = pos.coords.longitude;

                console.log(`Sending location -> lat: ${latitude}, lng: ${longitude}`);

                const params = new URLSearchParams();
                params.append("latitude", latitude);
                params.append("longitude", longitude);

                fetch(`/journey/track/${trackId}/update-location`, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: params.toString()
                })
                .then(res => res.text())
                .then(text => console.log(`Server response: ${text}`))
                .catch(err => console.error("Failed to send location:", err));
            },
            err => {
                console.error("Error getting geolocation:", err);
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    }

    // Run immediately
    sendLocation();
    // Repeat every 3 minutes
    setInterval(sendLocation, 300000);
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
