import requests
import sys
import csv
import datetime
import pandas as pd
import seaborn as sb
import matplotlib.pyplot as plt
import numpy as np
import os

COLUMNS = ['Date','Total_Cases','New_Daily_Cases','New_Daily_Deaths','Total_Deaths','Total_Recoveries']

def generate_csv(code):
    url = "https://thevirustracker.com/free-api?countryTimeline=" + code

    r = requests.get(url, headers={"User-Agent": "XY"})

    data = r.json()
    keys = data['timelineitems'][0].keys()
    length = len(keys)
    name = "covid" + code + ".csv"
    with open(name, 'w', newline='') as file:
                writer = csv.writer(file)
                for key in keys:
                    if length > 1:
                        row = []
                        #x = datetime.datetime.strptime(key, '%m/%d/%y')
                        #xn = x - datetime.timedelta(days=1)
                        #date = xn.strftime("%m/%d/%y")
                        row.append(key)
                        row.append(data['timelineitems'][0][key]['total_cases'])
                        row.append(data['timelineitems'][0][key]['new_daily_cases'])
                        row.append(data['timelineitems'][0][key]['new_daily_deaths'])
                        row.append(data['timelineitems'][0][key]['total_deaths'])
                        row.append(data['timelineitems'][0][key]['total_recoveries'])
                        writer.writerow(row)
                        length-=1
    
    
def write_to_csv(dataset, name):
    dataset.to_csv(name + ".csv", index=True, header=True)                  

def create_dataset(siglas):
    result = pd.DataFrame()
    THIS_FOLDER = os.path.dirname(os.path.abspath(__file__))
    for i in siglas:
        generate_csv(i)
        my_file = os.path.join(THIS_FOLDER, 'covid'+i+'.csv')
        t_d = pd.read_csv(my_file)
        tmp = [j+i for j in COLUMNS[1:]]
        tmp.insert(0,'Date')
        t_d.columns = tmp
        t_d.set_index('Date', inplace=True, drop=True)
        result = pd.concat([result,t_d], axis=1, sort=True)
    result.fillna(0,inplace=True)
    return result