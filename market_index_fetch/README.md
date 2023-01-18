# Market Index Fetcher

## <u>Modules used</u>

### yfinance
<ul>
    <li> The module uses yahoo finance API to provide the functions to fetch the desired share's market price at a given time instance. 
</ul>

### forex_python
<ul>
    <li> The module is used to get real-time foreign exchange rates for various currencies.
    <li> The module is also contains assistance for converting x amount in one currency to another currency system.
    <li> One can also find currency code and currency symbol by knowing either one of them to find the other.
</ul>

## <u>Functions Implemented</u>

### getCurrencyIndexValue(tickers)
<ul>
    <li>The <i>yfinance</i> module is used to implement <b> getCurrentIndexValue(tickers) </b> function where <u>tickers</u> is a list of tickers of the share whose price is to found. 
    <li>The fetched values are returned as <i>pandas Dataframe</i> object which dumped into index.html file.
    The function also converts the fetched prices from USD to INR along the way using <i>forex_python</i> module.
</ul>

### getCurrencyRate(currencies):
<ul>
    <li> The function simply returns a well-formatted mapping of the conversion rate to convert unit currency from currencies to INR currency system.
</ul>