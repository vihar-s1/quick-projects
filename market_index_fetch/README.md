# Market Index Fetcher

## <u>Modules used</u>

### nsetools
<ul>
    <li> The module implements various functionalities for India's National Stock Exchange (NSE)
</ul>

### forex_python
<ul>
    <li> The module is used to get real-time foreign exchange rates for various currencies.
    <li> The module is also contains assistance for converting x amount in one currency to another currency system.
    <li> One can also find currency code and currency symbol by knowing either one of them to find the other.
</ul>

## <u>Functions Implemented</u>

### getCurrentIndexValue(tickers)
<ul>
    <li>The <i>nsetools</i> module is used to implement <b> getCurrentIndexValue(tickers) </b> function where <u>tickers</u> is a list of tickers of the share whose price is to found. 
    <li>The fetched values are returned as <i>pandas Dataframe</i> object.
    The function fetches prices in INR as the stocks queried belong Indian Stock Market.
</ul>

### getCurrencyRate(currencies):
<ul>
    <li> The function simply returns a well-formatted mapping of the conversion rate to convert unit currency from currencies to INR currency system.
</ul>