from statsmodels.tsa.holtwinters import Holt
import numpy as np
import pandas as pd
import datetime

class ExponentialSmootheningModel:
       
    def __init__(self, level=0.5):
        self.level = level
       
    def predict_with_exp(self, dataset, code='PT', y='Total_Cases', days=5):
        tmp = dataset[[y+code]]
        history = [x for x in tmp.values]
        news = []
        data_mod = tmp.reset_index()
        data_mod.columns = ["Date",y+code]
        for i in range(days):
            model = Holt(history[i:])
            model_fit = model.fit(smoothing_level=self.level)
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
        data_mod.columns = ["EXPO"+code]

        return data_mod