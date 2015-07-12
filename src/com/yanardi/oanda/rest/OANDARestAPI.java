package com.yanardi.oanda.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yanardi.oanda.data.Account;
import com.yanardi.oanda.data.AccountInfo;
import com.yanardi.oanda.data.Candle;
import com.yanardi.oanda.data.CandleFormat;
import com.yanardi.oanda.data.ClosedOrder;
import com.yanardi.oanda.data.ClosedTrade;
import com.yanardi.oanda.data.DayOfWeek;
import com.yanardi.oanda.data.Granularity;
import com.yanardi.oanda.data.Instrument;
import com.yanardi.oanda.data.InstrumentFields;
import com.yanardi.oanda.data.InstrumentHistory;
import com.yanardi.oanda.data.NewTrade;
import com.yanardi.oanda.data.Order;
import com.yanardi.oanda.data.OrderSide;
import com.yanardi.oanda.data.OrderType;
import com.yanardi.oanda.data.Price;
import com.yanardi.oanda.data.RestError;
import com.yanardi.oanda.data.Trade;

/**
 * A Java wrapper for OANDA Rest API
 *
 * @author Fredy Yanardi
 *
 */
public class OANDARestAPI {

    /* A set of helper classes */
    class Accounts {
        List<Account> accounts;
    }

    class Instruments {
        List<Instrument> instruments;
    }

    class Prices {
        List<Price> prices;
    }

    class Orders {
        List<Order> orders;
    }

    class Trades {
        List<Trade> trades;
    }

    public static final String FXPRACTICE_DOMAIN = "https://api-fxpractice.oanda.com";
    public static final String FXTRADE_DOMAIN = "https://api-fxtrade.oanda.com";
    public static final String RFC3339_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
    public static final SimpleDateFormat RFC3339_SDF = new SimpleDateFormat(RFC3339_DATE_FORMAT);

    private String domain = FXPRACTICE_DOMAIN;
    private String accessToken;
    private CloseableHttpClient httpClient;
    private Gson gson;
    private Escaper escaper;

    /**
     * Constructor
     *
     * @param accessToken OANDA API access token (retrievable from Account Management Portal)
     * @param accountType the OANDA account type, either fxTrade or fxTrade Practice
     */
    public OANDARestAPI(String accessToken, AccountType accountType) {
        this.accessToken = accessToken;
        domain = accountType == AccountType.FXTRADE ? FXTRADE_DOMAIN : FXPRACTICE_DOMAIN;

        httpClient = HttpClientBuilder.create().build();
        CandleTypeAdapterFactory candleTAF = new CandleTypeAdapterFactory();
        EnumTypeAdapterFactory enumTAF = new EnumTypeAdapterFactory();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(candleTAF)
                .registerTypeAdapterFactory(enumTAF)
                .enableComplexMapKeySerialization()
                .setDateFormat(RFC3339_DATE_FORMAT).create();
        escaper = UrlEscapers.urlFormParameterEscaper();
    }

    public List<Account> getAccounts() throws IOException, OANDARestException {
        Accounts accounts = requestGet("accounts", null, Accounts.class);
        return accounts != null ? accounts.accounts : null;
    }

    public AccountInfo getAccountInfo(String accountId) throws IOException, OANDARestException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");
        return requestGet("accounts/" + accountId, null, AccountInfo.class);
    }

    public List<Instrument> getInstruments(String accountId, List<InstrumentFields> fields, List<String> instrumentNames)
            throws IOException, OANDARestException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");

        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("accountId", accountId);
        if (fields != null && !fields.isEmpty()) {
            List<String> fieldsStr = new LinkedList<String>();
            for (InstrumentFields field : fields) {
                fieldsStr.add(enumConstant2LowerCamel(field));
            }
            parameters.put("fields", escapeStringList(fieldsStr));
        }
        if (instrumentNames != null) {
            parameters.put("instruments", escapeStringList(instrumentNames));
        }
        Instruments instruments = requestGet("instruments", parameters, Instruments.class);
        return instruments != null ? instruments.instruments : null;
    }

    public List<Price> getPrices(List<String> instrumentNames, Date since)
            throws IOException, OANDARestException {
        Preconditions.checkNotNull(instrumentNames, "Missing mandatory argument: instrumentNames");

        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("instruments", escapeStringList(instrumentNames));
        if (since != null) {
            parameters.put("since", escapeDate(since));
        }
        Prices prices = requestGet("prices", parameters, Prices.class);
        return prices != null ? prices.prices : null;
    }

    public List<Candle> getCandles(String instrument, Granularity granularity, Integer count,
            Date start, Date end, CandleFormat candleFormat, Boolean includeFirst, Byte dailyAlignment,
            TimeZone alignmentTimezone, DayOfWeek weeklyAlignment) throws OANDARestException, IOException {
        Preconditions.checkNotNull(instrument, "Missing mandatory argument: instrument");

        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("instrument", instrument);
        if (granularity != null) {
            parameters.put("granularity", granularity.toString());
        }
        if (start != null && end != null) {
            Preconditions.checkArgument(count == null, "Both start/end and count are specified");
            parameters.put("start", escapeDate(start));
            parameters.put("end", escapeDate(end));
        }
        else {
            Preconditions.checkNotNull(count, "start and/or end is not specified but count is missing");
            Preconditions.checkArgument(count >= 0 && count <= 5000,
                    "start and/or end is not specified but count is invalid: %s", count);
            parameters.put("count", Integer.toString(count));
        }
        if (candleFormat != null) {
            parameters.put("candleFormat", candleFormat.toString().toLowerCase());
        }
        if (includeFirst != null) {
            parameters.put("includeFirst", includeFirst.toString());
        }
        if (dailyAlignment != null) {
            parameters.put("dailyAlignment", dailyAlignment.toString());
        }
        if (alignmentTimezone != null) {
            parameters.put("alignmentTimezone", alignmentTimezone.toString());
        }
        if (weeklyAlignment != null) {
            parameters.put("weeklyAlignment", enumConstant2UpperCamel(weeklyAlignment));
        }
        InstrumentHistory instrumentHistory = requestGet("candles", parameters, InstrumentHistory.class);
        return instrumentHistory != null ? instrumentHistory.getCandles() : null;
    }

    public List<Order> getOrders(String accountId, Long maxId, Integer count, String instrument, List<Long> ids)
            throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");

        Map<String, String> parameters = new TreeMap<String, String>();
        if (maxId != null) {
            parameters.put("maxId", Long.toString(maxId));
        }
        if (count != null) {
            parameters.put("count", Integer.toString(count));
        }
        if (instrument != null) {
            parameters.put("instrument", instrument);
        }
        if (ids != null && !ids.isEmpty()) {
            String idsString = Joiner.on(",").join(ids);
            parameters.put("ids", escaper.escape(idsString));
        }
        Orders orders = requestGet("accounts/" + accountId + "/orders", parameters, Orders.class);
        return orders != null ? orders.orders : null;
    }

    public NewTrade newOrder(String accountId, String instrument, int units, OrderSide side, OrderType type,
            Date expiry, Double price, Double lowerBound, Double upperBound, Double stopLoss,
            Double takeProfit, Double trailingStop) throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");
        Preconditions.checkNotNull(instrument, "Missing mandatory argument: instrument");
        Preconditions.checkArgument(units > 0, "Units must be greater than 0");
        Preconditions.checkNotNull(side, "Missing mandatory argument: side");
        Preconditions.checkNotNull(type, "Missing mandatory argument: type");

        if (type == OrderType.LIMIT || type == OrderType.STOP || type == OrderType.MARKET_IF_TOUCHED) {
            Preconditions.checkNotNull(expiry, "expiry is required for order type " + type);
            Preconditions.checkNotNull(price, "price is required for order type " + type);
        }

        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("instrument", instrument);
        parameters.put("units", Integer.toString(units));
        parameters.put("side", enumConstant2LowerCamel(side));
        parameters.put("type", enumConstant2LowerCamel(type));
        if (expiry != null) {
            parameters.put("expiry", RFC3339_SDF.format(expiry));
        }
        if (price != null) {
            parameters.put("price", Double.toString(price));
        }
        if (lowerBound != null) {
            parameters.put("lowerBound", Double.toString(lowerBound));
        }
        if (upperBound != null) {
            parameters.put("upperBound", Double.toString(upperBound));
        }
        if (stopLoss != null) {
            parameters.put("stopLoss", Double.toString(stopLoss));
        }
        if (takeProfit != null) {
            parameters.put("takeProfit", Double.toString(takeProfit));
        }
        if (trailingStop != null) {
            parameters.put("trailingStop", Double.toString(trailingStop));
        }
        String endPoint = "accounts/" + accountId + "/orders";
        return requestPost(endPoint, parameters, NewTrade.class);
    }

    public Order getOrder(String accountId, long id) throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");
        Preconditions.checkArgument(id >= 0, "Invalid order id: " + id);
        String endPoint = "accounts/" + accountId + "/orders/" + Long.toString(id);
        return requestGet(endPoint, new TreeMap<String, String>(), Order.class);
    }

    public Order modifyOrder(String accountId, long id, Integer units, Double price, Date expiry,
            Double lowerBound, Double upperBound, Double stopLoss, Double takeProfit,
            Double trailingStop) throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");
        Preconditions.checkArgument(id >= 0, "Invalid order id: " + id);

        Map<String, String> parameters = new TreeMap<String, String>();
        if (units != null) {
            parameters.put("units", Integer.toString(units));
        }
        if (price != null) {
            parameters.put("price", Double.toString(price));
        }
        if (expiry != null) {
            parameters.put("expiry", RFC3339_SDF.format(expiry));
        }
        if (lowerBound != null) {
            parameters.put("lowerBound", Double.toString(lowerBound));
        }
        if (upperBound != null) {
            parameters.put("upperBound", Double.toString(upperBound));
        }
        if (stopLoss != null) {
            parameters.put("stopLoss", Double.toString(stopLoss));
        }
        if (takeProfit != null) {
            parameters.put("takeProfit", Double.toString(takeProfit));
        }
        if (trailingStop != null) {
            parameters.put("trailingStop", Double.toString(trailingStop));
        }
        String endPoint = "accounts/" + accountId + "/orders/" + Long.toString(id);
        return requestPatch(endPoint, parameters, Order.class);
    }

    public ClosedOrder closeOrder(String accountId, long id) throws OANDARestException, IOException {
        String endPoint = "accounts/" + accountId + "/orders/" + Long.toString(id);
        return requestDelete(endPoint, null, ClosedOrder.class);
    }

    public List<Trade> getTrades(String accountId, Long maxId, Integer count, String instrument, List<Long> ids)
            throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");

        Map<String, String> parameters = new TreeMap<String, String>();
        if (maxId != null) {
            parameters.put("maxId", Long.toString(maxId));
        }
        if (count != null) {
            parameters.put("count", Integer.toString(count));
        }
        if (instrument != null) {
            parameters.put("instrument", instrument);
        }
        if (ids != null) {
            String idsString = Joiner.on(",").join(ids);
            parameters.put("ids", escaper.escape(idsString));
        }
        Trades trades = requestGet("accounts/" + accountId + "/trades", parameters, Trades.class);
        return trades != null ? trades.trades : null;
    }

    public Trade getTrade(String accountId, long id) throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");

        return requestGet("accounts/" + accountId + "/trades/" + Long.toString(id), 
                new TreeMap<String, String>(), Trade.class);
    }

    public Trade modifyTrade(String accountId, long id, Double stopLoss, Double takeProfit, Double trailingStop)
            throws OANDARestException, IOException {
        Preconditions.checkNotNull(accountId, "Missing mandatory argument: accountId");

        Map<String, String> parameters = new TreeMap<String, String>();
        if (stopLoss != null) {
            parameters.put("stopLoss", Double.toString(stopLoss));
        }
        if (takeProfit != null) {
            parameters.put("takeProfit", Double.toString(takeProfit));
        }
        if (trailingStop != null) {
            parameters.put("trailingStop", Double.toString(trailingStop));
        }
        String endPoint = "accounts/" + accountId + "/trades/" + Long.toString(id);
        return requestPatch(endPoint, parameters, Trade.class);
    }

    public ClosedTrade closeTrade(String accountId, long id) throws OANDARestException, IOException {
        String endPoint = "accounts/" + accountId + "/trades/" + Long.toString(id);
        return requestDelete(endPoint, null, ClosedTrade.class);
    }

    protected <T> T requestGet(String endPoint, Map<String, String> parameters, Class<T> responseClass) 
            throws IOException, OANDARestException {
        StringBuilder uriString = new StringBuilder(domain);
        uriString.append("/v1/").append(endPoint);
        if (parameters != null && !parameters.isEmpty()) {
            uriString.append('?');
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (i++ > 0) uriString.append('&');
                uriString.append(entry.getKey());
                uriString.append('=');
                uriString.append(entry.getValue());
            }
        }

        HttpUriRequest httpGet = new HttpGet(uriString.toString());
        httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));

        try (CloseableHttpResponse resp = httpClient.execute(httpGet)) {
            HttpEntity entity = resp.getEntity();

            StringBuilder respString = null;
            if (entity != null) {
                InputStream stream = entity.getContent();
                respString = new StringBuilder();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        respString.append(line);
                    }
                }
            }
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                if (respString != null) {
                    return gson.fromJson(respString.toString(), responseClass);
                }
                else {
                    throw new IOException("Missing HTTP response body");
                }
            }
            else {
                if (respString != null) {
                    RestError restError = gson.fromJson(respString.toString(), RestError.class);
                    throw new OANDARestException(restError);
                }
                else {
                    throw new IOException("Missing HTTP response body (response code: " + statusCode + ")");
                }
            }
        }
    }

    protected <T> T requestPost(String endPoint, Map<String, String> data, Class<T> responseClass) 
            throws IOException, OANDARestException {
        StringBuilder uriString = new StringBuilder(domain);
        uriString.append("/v1/").append(endPoint);

        List <NameValuePair> nvps = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpPost httpPost = new HttpPost(uriString.toString());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));

        try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
            HttpEntity entity = resp.getEntity();

            StringBuilder respString = null;
            if (entity != null) {
                InputStream stream = entity.getContent();
                respString = new StringBuilder();
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        respString.append(line);
                    }
                }
            }
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_CREATED) {
                if (respString != null) {
                    return gson.fromJson(respString.toString(), responseClass);
                }
                else {
                    throw new IOException("Missing HTTP response body");
                }
            }
            else {
                if (respString != null) {
                    RestError restError = gson.fromJson(respString.toString(), RestError.class);
                    throw new OANDARestException(restError);
                }
                else {
                    throw new IOException("Missing HTTP response body (response code: " + statusCode + ")");
                }
            }
        }
    }

    protected <T> T requestPatch(String endPoint, Map<String, String> data, Class<T> responseClass) 
            throws IOException, OANDARestException {
        StringBuilder uriString = new StringBuilder(domain);
        uriString.append("/v1/").append(endPoint);

        List <NameValuePair> nvps = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpPatch httpPost = new HttpPatch(uriString.toString());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));

        try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
            HttpEntity entity = resp.getEntity();

            StringBuilder respString = null;
            if (entity != null) {
                InputStream stream = entity.getContent();
                respString = new StringBuilder();
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        respString.append(line);
                    }
                }
            }
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                if (respString != null) {
                    return gson.fromJson(respString.toString(), responseClass);
                }
                else {
                    throw new IOException("Missing HTTP response body");
                }
            }
            else {
                if (respString != null) {
                    RestError restError = gson.fromJson(respString.toString(), RestError.class);
                    throw new OANDARestException(restError);
                }
                else {
                    throw new IOException("Missing HTTP response body (response code: " + statusCode + ")");
                }
            }
        }
    }

    protected <T> T requestDelete(String endPoint, Map<String, String> data, Class<T> responseClass) 
            throws IOException, OANDARestException {
        StringBuilder uriString = new StringBuilder(domain);
        uriString.append("/v1/").append(endPoint);

        HttpDelete httpPost = new HttpDelete(uriString.toString());
        httpPost.setHeader(new BasicHeader("Authorization", "Bearer " + accessToken));

        try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
            HttpEntity entity = resp.getEntity();

            StringBuilder respString = null;
            if (entity != null) {
                InputStream stream = entity.getContent();
                respString = new StringBuilder();
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        respString.append(line);
                    }
                }
            }
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                if (respString != null) {
                    return gson.fromJson(respString.toString(), responseClass);
                }
                else {
                    throw new IOException("Missing HTTP response body");
                }
            }
            else {
                if (respString != null) {
                    RestError restError = gson.fromJson(respString.toString(), RestError.class);
                    throw new OANDARestException(restError);
                }
                else {
                    throw new IOException("Missing HTTP response body (response code: " + statusCode + ")");
                }
            }
        }
    }

    protected String enumConstant2UpperCamel(Enum<?> e) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, e.toString());
    }

    protected String enumConstant2LowerCamel(Enum<?> e) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.toString());
    }

    protected String escapeStringList(List<String> list) {
        return escaper.escape(Joiner.on(",").join(list));
    }

    protected String escapeDate(Date since) {
        return escaper.escape(RFC3339_SDF.format(since));
    }

}
