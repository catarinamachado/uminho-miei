import numpy as np
import pandas as pd
import datetime
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, LSTM, Dropout
from tensorflow.keras.callbacks import EarlyStopping


class LstmModel:
    
    def __init__(self, lag=5):
        self.model = None
        self.scaler = MinMaxScaler(feature_range=(0,1))
        self.lag = lag

    def _create_dataset(self, dataset, look_back=1):
        dataX, dataY = [], []
        for i in range(len(dataset) - look_back):
            a = dataset[i:(i + look_back), 0]
            dataX.append(a)
            dataY.append(dataset[i + look_back, 0])
        return np.array(dataX), np.array(dataY)

    def _transform_data(self,dataset, div=0):
        values = dataset.values.reshape(-1,1)
        values = values.astype('float32')
        scaled = self.scaler.fit_transform(values)

        train = scaled

        trainX, trainY = self._create_dataset(train, self.lag)
        trainX = np.reshape(trainX, (trainX.shape[0], self.lag, 1))
        
        return trainX, trainY
    
    def _initiate_model(self, trainX):
        model_ls = Sequential()
        model_ls.add(LSTM(128,return_sequences=True, input_shape=(trainX.shape[1],trainX.shape[2])))
        model_ls.add(Dropout(0.2))
        model_ls.add(LSTM(128, return_sequences=True))
        model_ls.add(Dropout(0.2))
        model_ls.add(LSTM(128))
        model_ls.add(Dropout(0.5))
        model_ls.add(Dense(1, activation='linear'))
        model_ls.compile(loss='mean_absolute_error', optimizer='sgd')
        self.model = model_ls

    
    def train(self, X, Y):
        es = EarlyStopping(monitor='loss', mode='min', verbose=1, patience=500)
        history = self.model.fit(X, Y, epochs=1000, batch_size=16, verbose=0, shuffle=False, callbacks=[es])
        #plt.plot(history.history['loss'], label='train')
        #plt.legend()
        #plt.savefig("test")


    def predict_with_lstm(self, dataset, code='PT', y='Total_Cases', days=5):
        trainX, trainY = self._transform_data(dataset[[y+code]])
        self._initiate_model(trainX)
        self.train(trainX,trainY)

        #self._stateful_model(trainX, trainY, y, code)
        
        tmp_data = dataset[[y+code]]
        tmp_data = tmp_data.reset_index()
        tmp_data.columns = ['Date', y+code]

        data = np.append(trainX[-1][1:],np.array(trainY[-1].reshape(1,-1)))
        results = []
        for i in range(days):
            prediction = self.model.predict(data.reshape(1,self.lag, 1))
            value = self.scaler.inverse_transform(prediction)[0][0]
            data = data[1:]
            data = np.append(data,np.array(prediction))
            
            xn = datetime.datetime.strptime(tmp_data.iloc[-1]['Date'], '%m/%d/%y') \
                + datetime.timedelta(days=i+1)
            results.append(
                pd.Series([xn.strftime("%m/%d/%y"), round(value)], index=["Date",y+code])
            )
        
        data_mod = pd.DataFrame(results)
        data_mod.set_index('Date', inplace=True, drop=True)
        data_mod.columns = ["LSTM"+code]
        
        return data_mod
    
    def predict_dataset(self, dataset_train, dataset_predict, y, code):
        trainX, trainY = self._transform_data(dataset_train)
        self._initiate_model(trainX)
        self.train(trainX,trainY)
        
        predX, _ = self._transform_data(dataset_predict)
        
        tmp_data = dataset_predict.reset_index()
        tmp_data.columns = ['Date',y+code]
        data = predX[16]
        results = []
        for i in range(tmp_data.shape[0]-21):
            prediction = self.model.predict(data.reshape(1,self.lag,1))
            value = self.scaler.inverse_transform(prediction)[0][0]
            data = data[1:]
            data = np.append(data,np.array(prediction))
            xn = datetime.datetime.strptime(tmp_data.iloc[19]['Date'], '%m/%d/%y') \
                + datetime.timedelta(days=i+1)
            results.append(
                pd.Series([xn.strftime("%m/%d/%y"), round(value)], index=["Date",y+code])
            )
        
        print(results)
        
        #tmp_data = tmp_data.append(results)
        #tmp_data.set_index('Date', inplace=True, drop=True)
        #tmp_data.columns = ["LSTM"+code]
        
        return tmp_data    
        
           
        
    def _stateful_model(self, trainX, trainY, y, code):
        model_ls = Sequential()
        model_ls.add(LSTM(128,return_sequences=True, batch_input_shape=(trainX.shape[0],trainX.shape[1],trainX.shape[2]), stateful=True))
        model_ls.add(Dropout(0.2))
        model_ls.add(LSTM(128, stateful=True, return_sequences=True))
        model_ls.add(Dropout(0.2))
        model_ls.add(LSTM(128, stateful=True))
        model_ls.add(Dropout(0.5))
        model_ls.add(Dense(1, activation='linear'))
        model_ls.compile(loss='mean_absolute_error', optimizer='adam', metrics=['accuracy'])
        
        for i in range(500):
            model_ls.fit(trainX, trainY, epochs=1, batch_size=trainX.shape[0], shuffle=False)
            model_ls.reset_states()
        
        model_ls2 = Sequential()
        model_ls2.add(LSTM(128,return_sequences=True, batch_input_shape=(1,trainX.shape[1],trainX.shape[2]), stateful=True))
        model_ls2.add(Dropout(0.2))
        model_ls2.add(LSTM(128, stateful=True, return_sequences=True))
        model_ls2.add(Dropout(0.2))
        model_ls2.add(LSTM(128, stateful=True))
        model_ls2.add(Dropout(0.5))
        model_ls2.add(Dense(1, activation='linear'))
        model_ls2.set_weights(model_ls.get_weights())
        model_ls2.compile(loss='mean_absolute_error', optimizer='adam', metrics=['accuracy'])
        self.model = model_ls2