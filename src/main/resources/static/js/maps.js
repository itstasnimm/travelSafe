
function useCurrentLocationToField(fieldId) {
  if (!navigator.geolocation) {
    alert("Geolocation not supported by your browser.");
    return;
  }
  navigator.geolocation.getCurrentPosition(
    position => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      document.getElementById(fieldId).value = lat + "," + lng;
    },
    error => {
      console.error("Error getting location:", error);
      alert("Unable to fetch current location.");
    }
  );
}

function appendCurrentLocationToCheckpoints() {
  if (!navigator.geolocation) {
    alert("Geolocation not supported by your browser.");
    return;
  }
  navigator.geolocation.getCurrentPosition(
    position => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      const input = document.getElementById("checkpoints");
      input.value += (input.value ? ", " : "") + lat + "," + lng;
    },
    error => {
      console.error("Error getting location:", error);
      alert("Unable to fetch current location.");
    }
  );
}
