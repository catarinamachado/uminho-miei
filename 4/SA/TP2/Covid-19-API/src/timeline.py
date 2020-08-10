import requests
import sys
import csv

url = "https://thevirustracker.com/free-api?countryTimeline=" + sys.argv[1]

r = requests.get(url, headers={"User-Agent": "XY"})

data = r.json()
keys = data['timelineitems'][0].keys()
length = len(keys)
with open('covid.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            for key in keys:
                if length > 2:
                    row = []
                    row.append(data['timelineitems'][0][key]['total_cases'])
                    row.append(data['timelineitems'][0][key]['new_daily_cases'])
                    row.append(data['timelineitems'][0][key]['new_daily_deaths'])
                    row.append(data['timelineitems'][0][key]['total_deaths'])
                    row.append(data['timelineitems'][0][key]['total_recoveries'])
                    writer.writerow(row)
                    length-=1


