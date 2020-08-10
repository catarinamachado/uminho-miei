package main

import (
	"encoding/json"
	"io"
	"io/ioutil"
	"net/http"
)

func getOverallDataHandler(w http.ResponseWriter, req *http.Request) {
	//Allow CORS here By * or specific origin
	w.Header().Set("Access-Control-Allow-Origin", "*")

	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
	var data OverallData
	data, error := getOverallData()
	if error != nil {
		http.Error(w, error.Error(), http.StatusNotFound)
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(data)
}

func getCountryOverallDataHandler(w http.ResponseWriter, req *http.Request) {
	//Allow CORS here By * or specific origin
	w.Header().Set("Access-Control-Allow-Origin", "*")

	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
	var country string
	param := req.URL.Query().Get("Country")

	if param == "" {
		http.Error(w, "", http.StatusBadRequest)
		return
	}
	country = param
	data, error := getCountryData(country)
	if error != nil {
		http.Error(w, error.Error(), http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(data)
}

func getCountryHistoryHandler(w http.ResponseWriter, req *http.Request) {
	//Allow CORS here By * or specific origin
	w.Header().Set("Access-Control-Allow-Origin", "*")

	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
	var country string
	param := req.URL.Query().Get("Country")

	if param == "" {
		http.Error(w, "", http.StatusBadRequest)
		return
	}
	country = param
	data := getCountryHistory(country)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(data)
}
func getCountries(w http.ResponseWriter, req *http.Request) {
	w.Header().Set("Access-Control-Allow-Origin", "*")
	w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
	data, _ := ioutil.ReadFile("./assets/countries.json")
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.WriteHeader(http.StatusOK)
	io.WriteString(w, string(data))
}
