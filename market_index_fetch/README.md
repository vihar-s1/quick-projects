# ***Market Index Fetcher***

## **Modules used**

### nsetools

- The module implements various functionalities for India's National Stock Exchange (NSE)

### forex_python

- The module is used to get real-time foreign exchange rates for various currencies.
- The module is also contains assistance for converting x amount in one currency to another currency system.
- One can also find currency code and currency symbol by knowing either one of them to find the other.

## ***Functions Implemented***

### *1. getCurrentIndexValue(tickers):*

- The *nsetools* module is used to implement **getCurrentIndexValue(tickers)** function where *tickers* is a list of tickers of the share whose price is to found.
- The fetched values are returned as *pandas Dataframe* object.
- The function fetches prices in INR as the stocks queried belong Indian Stock Market.

### *2. getCurrencyRate(currencies):*

- The function simply returns a well-formatted mapping of the conversion rate to convert unit currency from currencies to INR currency system.
