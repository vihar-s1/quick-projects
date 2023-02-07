#!/usr/bin/env python

import pandas as pd
# Data Source
from nsetools import Nse
from forex_python.converter import CurrencyRates, CurrencyCodes, RatesNotAvailableError


def csv_stock_codes():
    '''Generates a csv file comprising of all the stock codes and corresponding stock name.'''
    pd.Series(Nse().get_stock_codes()).to_csv('stock_codes.csv')

def getCurrencyRate(currencies: list[str]): 
    '''Obtaining the currency rates for conversion in INR from each currency in \'currencies\''''
    cr = CurrencyRates() # get latest currency rates
    cc = CurrencyCodes() # mapping between the currency symbol and currency type
    
    rates = {}
    for currency in currencies:
        rates[ f'{cc.get_symbol(currency)}1 ({currency})' ] = "{}{rate:.3f} (INR)".format(cc.get_symbol('INR'), rate=cr.convert(currency, 'INR', 1))
    
    return rates


def getStockQuote(tickers: list[str]) -> pd.DataFrame:
    '''
    Fetches symbol, company mame, day-High,  day-Low, base price, change, 
    percentage change, total Traded Volume, closing price, and last price for the given ticker
    in India's National Stock Market (NSE)
    '''
    nse = Nse()
    keys = ['symbol', 'companyName', 'dayHigh', 'dayLow','basePrice', 'change', 'pChange', 'totalTradedVolume', 'closePrice', 'lastPrice']
    
    quotes = {}
    
    if type(tickers) is str:
        quote = nse.get_quote(tickers)
        req_data = {}
        for key in keys:
            req_data[key] = quote[key]
        quotes[tickers] = req_data
    else:
        for ticker in tickers:
            # get_index_quote
            quote = nse.get_quote(ticker)
            
            if quote is None:
                quotes[ticker] = None
                continue
            
            req_data = {}
            for key in keys:
                req_data[key] = quote[key]
                
            quotes[ticker] = req_data
            
    return pd.DataFrame(quotes)  


if __name__ == "__main__":
    try:
        print(getCurrencyRate(['GBP','EUR','USD']))
    except RatesNotAvailableError as e:
        print("RatesNotAvailableError Occured:", e)
    
    quotes = getStockQuote(['INFY', 'ADANIPOWER'])
    print(quotes)