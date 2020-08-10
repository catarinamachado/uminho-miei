# ARIMA Model
from statsmodels.tsa.arima.model import ARIMA
import numpy as np
import pandas as pd
import datetime

class ArimaModel:
    def __init__(self, order=(5,1,0)):
        self.order = order
    
    
    def predict_with_arima(self, dataset, code='PT', y='Total_Cases', days=5):
        tmp = dataset[[y+code]]
        history = [x for x in tmp.values]
        news = []
        data_mod = tmp.reset_index()
        data_mod.columns = ["Date",y+code]
        for i in range(days):
            model = ARIMA(history[i:], order=self.order,enforce_stationarity=False)
            model_fit = model.fit()
            output = model_fit.forecast()
            yhat = output[0]
            history.append(np.array([round(yhat)]))
            
            xn = datetime.datetime.strptime(data_mod.iloc[-1]['Date'], '%m/%d/%y') \
                + datetime.timedelta(days=i+1)
            news.append(
                pd.Series([xn.strftime("%m/%d/%y"), round(yhat)], index=data_mod.columns)
            )

        data_mod = pd.DataFrame(news)
        data_mod.set_index('Date', inplace=True, drop=True)
        data_mod.columns = ["ARIMA"+code]

        return data_mod