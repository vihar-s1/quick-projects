#!/usr/bin/env python

"""
Function implementations for getting currency rates for conversion to INR,
and another to fetch market indexes for various stocks from the NSE database.
"""

import pandas as pd
# Data Source
from nsetools import Nse
from forex_python.converter import CurrencyRates, CurrencyCodes, RatesNotAvailableError


def csv_stock_codes():
    '''Generates a csv file of all the stock codes and corresponding stock name.'''
    pd.Series(Nse().get_stock_codes()).to_csv('stock_codes.csv')


def get_currency_rate(currencies: list[str]):
    '''Converting currency in currencies to INR base'''
    currency_rates = CurrencyRates()  # get latest currency rates
    currency_codes = CurrencyCodes()  # mapping between the currency symbol and currency type

    rates = {}
    for currency in currencies:
        rates[f'{currency_codes.get_symbol(currency)}1 ({currency})'] = f"{currency_codes.get_symbol('INR')}{currency_rates.convert(currency, 'INR', 1): .3f} (INR)"

    return rates


def get_stock_quote(tickers: str | list[str]) -> pd.DataFrame:
    '''
    Fetches symbol, company mame, day-High,  day-Low, base price, change,
    percentage change, total Traded Volume, closing price, and last price for the given ticker
    in India's National Stock Market (NSE)
    '''
    nse = Nse()
    keys = [
        'symbol', 'companyName', 'dayHigh', 'dayLow',
        'basePrice', 'change', 'pChange', 'totalTradedVolume',
        'closePrice', 'lastPrice'
        ]

    quotes = {}

    if isinstance(tickers, str):
        tickers = [tickers]

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
        print(get_currency_rate(['GBP', 'EUR', 'USD']))
    except RatesNotAvailableError as eobj:
        print("RatesNotAvailableError Occured:", eobj)

    stock_quotes = get_stock_quote(['INFY', 'ADANIPOWER', 'NIFTY 50', 'SENSEX'])
    print(stock_quotes)
