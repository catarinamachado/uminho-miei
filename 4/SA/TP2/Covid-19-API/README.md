# covid-19-API
COVID-19 API to fetch data and predictions done with ML

## Endpoints
### GO data API (localhost:8000)
* /overallData - to get overall data on every case reported
* /countryData?Country=??? - to get overall data for contry ??? where ??? is the country eg: PT
* /countryHistory?Country=??? -to get the timeline of cases for country ???

### Python predictions API (localhost:5000)
* /predictions
    | params:
    * country: list of country codes
    * days: days to predict
    * field: field to predict
    
    | returns: .csv


* /predictions_based_on
    | params:
    * base: base country to train the model
    * target: target country to predict
    * field: field to predict
    
    | returns: .csv

## RUN (in separate terminals if local dev)
``` make run ``` 

``` cd predictions/ && python api.py ```
 