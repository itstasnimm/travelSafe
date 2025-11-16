// (trusted contact side)

document.addEventListener("DOMContentLoaded", function () {
  var trackId = window.trackId; // injected from Thymeleaf

  // read checkpoints from DOM
  var checkpointEls = document.querySelectorAll("li[data-id]");
  var checkpoints = Array.from(checkpointEls).map(el => ({
    id: el.dataset.id,
    lat: parseFloat(el.dataset.lat),
    lng: parseFloat(el.dataset.lng),
    el: el
  }));

  // create a "last known location" indicator
  var lastLocEl = document.createElement("p");
  lastLocEl.id = "lastLocation";
  lastLocEl.textContent = "Last known location: (fetching...)";
  document.body.appendChild(lastLocEl);

  function updateLocation() {
    fetch(`/journey/track/${trackId}/location`)
      .then(res => res.json())
      .then(loc => {
        var userLat = loc.lat;
        var userLng = loc.lng;
        var timestamp = loc.timestamp || new Date().toISOString();

        // update "last known location" text
        lastLocEl.textContent =
          `Last known location: ${userLat.toFixed(5)}, ${userLng.toFixed(5)} (at ${timestamp})`;

        // check checkpoints
        checkpoints.forEach(cp => {
          var statusEl = cp.el.querySelector(".status");
          if (statusEl.textContent.includes("Reached")) return; // skip if already reached

          var dist = distanceInMeters(userLat, userLng, cp.lat, cp.lng);
          if (dist < 900) {
            statusEl.textContent = "Reached ✅";
          }
        });
      })
      .catch(err => {
        console.error("Error fetching location:", err);
        lastLocEl.textContent = "Last known location: unavailable ❌";
      });
  }

  // run once immediately + then every 30s
  updateLocation();
  setInterval(updateLocation, 30000);
});

// Haversine distance function
function distanceInMeters(lat1, lon1, lat2, lon2) {
  function toRad(x) { return (x * Math.PI) / 180; }
  var R = 6371000; // meters
  var dLat = toRad(lat2 - lat1);
  var dLon = toRad(lon2 - lon1);
  var a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) ** 2;
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}
