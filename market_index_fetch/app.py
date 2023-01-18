#!/usr/bin/env python

import pandas as pd
# Data Source
import yfinance as yf
from forex_python.converter import CurrencyRates, CurrencyCodes

def getCurrencyRate(currencies: list[str]): 
    '''Obtaining the currency rates for conversion in INR from each currency in \'currencies\''''
    cr = CurrencyRates() # get latest currency rates
    cc = CurrencyCodes() # mapping between the currency symbol and currency type
    
    rates = {}
    for currency in currencies:
        rates[ f'{cc.get_symbol(currency)}1 ({currency})' ] = "{}{rate:.3f} (INR)".format(cc.get_symbol('INR'), rate=cr.convert(currency, 'INR', 1))
    
    return rates


def getCurrentIndexValue(tickers: list[str]) -> pd.DataFrame:
    '''Fetches 1hr prior or the closing index values of the tickers given along with the volume of trade of that ticker/share'''
    data = yf.download(tickers=tickers, period='1hr')
    
    # to convert incoming rates in USD to INR
    usd_to_inr = CurrencyRates().get_rate('USD', 'INR')
    
    # data --> pandas dictionary --> keys: Open, High, Low, Close, Adj Close, Volume
    
    for entry in data:
        data[entry] *= usd_to_inr
             
    data.to_html('./index.html') # dumps data in a table in index.html
    return data['Close'], data['Volume']  # only return closing value and the volume of the shares


if __name__ == "__main__":
    print(getCurrencyRate(['GBP','EUR','USD']))
    closing_data, volume_data = getCurrentIndexValue(['AMZN', 'UBER', 'GOOGL'])
    
    print(closing_data.to_json(), "\n", volume_data.to_json())
    
    # print(data['Open']['NFTY']['Rate'], type(data['Open']['NFTY']))
    