#Java Wrapper for OANDA Rest API

A set of Java classes to represent OANDA Rest API as documented in [OANDA API]. The main class is `com.yanardi.oanda.rest.OANDARestAPI` which offers a set of methods to access the Rest API.

Currently the following endpoints are supported:

  - Accounts
  - Rates
  - Orders
  - Trades

In order to use the API, an access token is required (can be either fxTrade or fxTrade Practice access token). Please refer to the [OANDA API] Getting started on how to obtain the access token.
