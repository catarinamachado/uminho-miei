package main

import (
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/countryHistory", getCountryHistoryHandler)
	http.HandleFunc("/countryData", getCountryOverallDataHandler)
	http.HandleFunc("/overallData", getOverallDataHandler)
	http.HandleFunc("/countries", getCountries)

	// start the server on port 8000
	log.Fatal(http.ListenAndServe(":8000", nil))
}
