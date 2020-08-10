package main

import (
	"fmt"
	"github.com/tidwall/gjson"
	"io/ioutil"
	"net/http"
	"time"
)

type OverallData struct {
	Total    int64 `json:"total"`
	NewToday int64 `json:"newToday"`
	Cured    int64 `json:"cured"`
	Deaths   int64 `json:"deaths"`
}

type CountryData struct {
	Total    int64  `json:"total"`
	NewToday int64  `json:"newToday"`
	Cured    int64  `json:"cured"`
	Deaths   int64  `json:"deaths"`
	Critical int64  `json:"critical"`
	Active   int64  `json:"active"`
	Date     string `json:"date"`
}

type CountryHistory struct {
	NewCases   int64
	NewDeaths  int64
	Recoveries int64
	Deaths     int64
}

func getOverallData() (OverallData, error) {
	response, err := http.Get("https://thevirustracker.com/free-api?global=stats")

	if err != nil {
		fmt.Println("Request for overall data failed")
		return OverallData{}, err
	}

	data, _ := ioutil.ReadAll(response.Body)

	return OverallData{
		gjson.Get(string(data), "results.0.total_cases").Int(),
		gjson.Get(string(data), "results.0.total_new_cases_today").Int(),
		gjson.Get(string(data), "results.0.total_recovered").Int(),
		gjson.Get(string(data), "results.0.total_deaths").Int(),
	}, nil
}

func getCountryData(country string) (CountryData, error) {
	response, err := http.Get("https://thevirustracker.com/free-api?countryTotal=" + country)

	if err != nil {
		fmt.Println("Request for overall  country data failed")
		return CountryData{}, err
	}

	data, _ := ioutil.ReadAll(response.Body)

	return CountryData{
		gjson.Get(string(data), "countrydata.0.total_cases").Int(),
		gjson.Get(string(data), "countrydata.0.total_new_cases_today").Int(),
		gjson.Get(string(data), "countrydata.0.total_recovered").Int(),
		gjson.Get(string(data), "countrydata.0.total_deaths").Int(),
		gjson.Get(string(data), "countrydata.0.total_serious_cases").Int(),
		gjson.Get(string(data), "countrydata.0.total_active_cases").Int(),
		time.Now().String(),
	}, nil
}

func getCountryHistory(country string) interface{} {
	response, _ := http.Get("https://thevirustracker.com/free-api?countryTimeline=" + country)
	data, _ := ioutil.ReadAll(response.Body)
	return gjson.Get(string(data), "timelineitems").Value()
}
