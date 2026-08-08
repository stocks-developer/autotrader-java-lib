/**
 *
 */
package com.dakshata.autotrader.api;

import java.util.Set;

import com.dakshata.constants.trading.*;
import com.dakshata.data.model.common.IOperationResponse;
import com.dakshata.trading.model.platform.PlatformHolding;
import com.dakshata.trading.model.platform.PlatformMargin;
import com.dakshata.trading.model.platform.PlatformOrder;
import com.dakshata.trading.model.platform.PlatformPosition;
import com.dakshata.trading.model.portfolio.Order;

/**
 * AutoTrader API instance.
 *
 * @author PRITESH
 *
 */
public interface IAutoTrader {

	String PRIMARY_SERVER_URL = "https://apix.stocksdeveloper.in";

	String BACKUP_SERVER_URL = "https://api.stocksdeveloper.in";

	String SERVER_URL = PRIMARY_SERVER_URL;

	/**
	 * Provides live pseudo accounts available under your user.
	 *
	 * @return live pseudo accounts
	 */
	IOperationResponse<Set<String>> fetchLivePseudoAccounts();

	/**
	 * Places an order. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-advanced-order/">api
	 * docs</a>.
	 *
	 * @param order order object
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeOrder(Order order);

	/**
	 * Places a regular order. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-regular-order/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount pseudo account
	 * @param exchange      exchange
	 * @param symbol        symbol
	 * @param tradeType     trade type
	 * @param orderType     order type
	 * @param productType   product type
	 * @param quantity      quantity
	 * @param price         price
	 * @param triggerPrice  trigger price
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeRegularOrder(final String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, ProductType productType, int quantity, float price,
			float triggerPrice);

	/**
	 * Places a bracket order. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-bracket-order/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount    pseudo account
	 * @param exchange         exchange
	 * @param symbol           symbol
	 * @param tradeType        trade type
	 * @param orderType        order type
	 * @param quantity         quantity
	 * @param price            price
	 * @param triggerPrice     trigger price
	 * @param target           target
	 * @param stoploss         stoploss
	 * @param trailingStoploss trailing stoploss
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeBracketOrder(String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, int quantity, float price, float triggerPrice, float target,
			float stoploss, float trailingStoploss);

	/**
	 * Places a cover order. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-cover-order/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount pseudo account
	 * @param exchange      exchange
	 * @param symbol        symbol
	 * @param tradeType     trade type
	 * @param orderType     order type
	 * @param quantity      quantity
	 * @param price         price
	 * @param triggerPrice  trigger price
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeCoverOrder(String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, int quantity, float price, float triggerPrice);

	/**
	 * Places an AutoTrader bracket order — our own bracket order, which works with
	 * every broker we support, including brokers that do not offer bracket orders of
	 * their own. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-autotrader-bracket-order/">api
	 * docs</a>.
	 *
	 * <p>
	 * The entry is sent to your broker as an ordinary intraday order and the target,
	 * stoploss and trailing stoploss are watched by AutoTrader. It does not give the
	 * extra intraday margin a broker's own bracket order sometimes gives, and the
	 * position is squared off automatically before the market closes.
	 * </p>
	 *
	 * <p>
	 * {@code target}, {@code stoploss} and {@code trailingStoploss} are distances in
	 * rupees from your entry price, exactly as they are for
	 * {@link #placeBracketOrder}. The {@code trailingStoploss} is the step the stop
	 * moves in each time the price advances by that much, so it needs a
	 * {@code stoploss} alongside it to say how far behind the price the stop sits.
	 * </p>
	 *
	 * @param pseudoAccount    pseudo account
	 * @param exchange         exchange
	 * @param symbol           symbol
	 * @param tradeType        trade type
	 * @param orderType        order type
	 * @param quantity         quantity
	 * @param price            price
	 * @param triggerPrice     trigger price (entry trigger, for a stoploss entry
	 *                         order; pass zero otherwise)
	 * @param target           target, in rupees away from your entry price
	 * @param stoploss         stoploss, in rupees away from your entry price
	 * @param trailingStoploss trailing stoploss step, in rupees (pass zero for a
	 *                         fixed stoploss)
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeAutoTraderBracketOrder(String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, int quantity, float price, float triggerPrice, float target,
			float stoploss, float trailingStoploss);

	/**
	 * Places an AutoTrader cover order — a stoploss without a target, which works
	 * with every broker we support. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-autotrader-cover-order/">api
	 * docs</a>.
	 *
	 * <p>
	 * Note the difference from {@link #placeCoverOrder}: a broker cover order
	 * carries its stop as an absolute price in {@code triggerPrice}, whereas an
	 * AutoTrader cover order carries it as a distance in rupees from your entry
	 * price in {@code stoploss}, exactly like the bracket form. There is no trigger
	 * price to pass.
	 * </p>
	 *
	 * @param pseudoAccount    pseudo account
	 * @param exchange         exchange
	 * @param symbol           symbol
	 * @param tradeType        trade type
	 * @param orderType        order type
	 * @param quantity         quantity
	 * @param price            price
	 * @param stoploss         stoploss, in rupees away from your entry price
	 * @param trailingStoploss trailing stoploss step, in rupees (pass zero for a
	 *                         fixed stoploss)
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeAutoTraderCoverOrder(String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, int quantity, float price, float stoploss,
			float trailingStoploss);

	/**
	 * Modifies the order as per the parameters passed.
	 *
	 * @param pseudoAccount pseudo account
	 * @param platformId    platform id (id given to order by trading platform)
	 * @param orderType     order type (pass null if you do not want to modify order
	 *                      type)
	 * @param quantity      quantity (pass zero if you do not want to modify
	 *                      quantity)
	 * @param price         price (pass zero if you do not want to modify price)
	 * @param triggerPrice  trigger price (pass zero if you do not want to modify
	 *                      trigger price)
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	IOperationResponse<Boolean> modifyOrderByPlatformId(final String pseudoAccount, final String platformId,
			final OrderType orderType, final Integer quantity, final Float price, final Float triggerPrice);

	/**
	 * Modifies the order, additionally setting the disclosed quantity percentage.
	 *
	 * @param disclosedQtyPct percent of order quantity to show in market depth
	 *                        (0 = no change; 10–100 = set new value; values
	 *                        below 10 are auto-adjusted to 10 to satisfy exchange
	 *                        rules). Applies only to LIMIT / STOP_LOSS orders.
	 */
	IOperationResponse<Boolean> modifyOrderByPlatformId(final String pseudoAccount, final String platformId,
			final OrderType orderType, final Integer quantity, final Float price, final Float triggerPrice,
			final Integer disclosedQtyPct);

	/**
	 * Cancels an order. For more information, please see
	 * <a href="https://stocksdeveloper.in/documentation/api/cancel-order/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount pseudo account
	 * @param platformId    platform id (id given to order by trading platform)
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	IOperationResponse<Boolean> cancelOrderByPlatformId(String pseudoAccount, String platformId);

	/**
	 * Cancels all open orders for the given account. For more information, please
	 * see
	 * <a href="https://stocksdeveloper.in/documentation/api/cancel-all-orders/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount pseudo account
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	IOperationResponse<Boolean> cancelAllOrders(String pseudoAccount);

	/**
	 * Used for exiting an open Bracket order or Cover order position. Cancels the
	 * child orders for the given parent order. For more information, please see
	 * <a href=
	 * "https://stocksdeveloper.in/documentation/api/cancel-child-orders/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount pseudo account
	 * @param platformId    platform id (id given to order by trading platform)
	 * @return <code>true</code> on success, <code>false</code> otherwise
	 */
	IOperationResponse<Boolean> cancelChildOrdersByPlatformId(String pseudoAccount, String platformId);

	/**
	 * Submits a square-off position request.
	 *
	 * @param pseudoAccount    pseudo account
	 * @param category         position category
	 * @param type             position type
	 * @param exchange         position exchange (broker independent exchange)
	 * @param symbol           position symbol (broker independent symbol)
	 * @param cancelOpenOrders cancel any open orders for this position
	 * @return true on successful acceptance of square-off request, false otherwise
	 */
	IOperationResponse<Boolean> squareOffPosition(final String pseudoAccount, final PositionCategory category,
			final PositionType type, final String exchange, final String symbol, boolean cancelOpenOrders);

	/**
	 * Submits a square-off portfolio request.
	 *
	 * @param pseudoAccount    pseudo account
	 * @param category         position category (DAY or NET portfolio to consider)
	 * @param cancelOpenOrders cancel all open orders for this portfolio
	 * @return true on successful acceptance of square-off request, false otherwise
	 */
	IOperationResponse<Boolean> squareOffPortfolio(final String pseudoAccount, final PositionCategory category,
			boolean cancelOpenOrders);

	/**
	 * This function executes a given command. The command can be operations like
	 * place, modify & cancel order etc. This is primarily used for AutoTrader
	 * desktop application which receives commands in csv format.
	 *
	 * @param command command in comma separated value (csv) format
	 * @return returns the result of the command
	 */
	IOperationResponse<? extends Object> execute(String command);

	/**
	 * Read trading platform orders from the trading account mapped to the given
	 * pseudo account.
	 *
	 * @param pseudoAccount pseudo account id
	 * @return orders trading platform orders
	 */
	IOperationResponse<Set<PlatformOrder>> readPlatformOrders(final String pseudoAccount);

	/**
	 * Read trading platform positions from the trading account mapped to the given
	 * pseudo account.
	 *
	 * @param pseudoAccount pseudo account id
	 * @return positions trading platform positions
	 */
	IOperationResponse<Set<PlatformPosition>> readPlatformPositions(final String pseudoAccount);

	/**
	 * Read trading platform margins from the trading account mapped to the given
	 * pseudo account.
	 *
	 * @param pseudoAccount pseudo account id
	 * @return margins trading platform margins
	 */
	IOperationResponse<Set<PlatformMargin>> readPlatformMargins(final String pseudoAccount);

	/**
	 * Read trading platform holdings from the trading account mapped to the given
	 * pseudo account.
	 *
	 * @param pseudoAccount pseudo account id
	 * @return holdings trading platform holdings
	 */
	IOperationResponse<Set<PlatformHolding>> readPlatformHoldings(final String pseudoAccount);

	/**
	 * Graceful shutdown. Call when your application is about to exit.
	 */
	void shutdown();

	/**
	 * Gets the latest version of at-desktop client.
	 *
	 * @return at-desktop latest version
	 */
	IOperationResponse<String> autoTraderDesktopVersion();

	/**
	 * Gets the minimum version required of at-desktop client.
	 *
	 * @return at-desktop minimum version
	 */
	IOperationResponse<String> autoTraderDesktopMinVersion();

}
