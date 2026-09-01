/**
 *
 */
package com.dakshata.autotrader.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dakshata.constants.trading.*;
import com.dakshata.data.model.autotrader.web.AdjustHoldingsRequest;
import com.dakshata.data.model.autotrader.web.AdjustHoldingsResponse;
import com.dakshata.data.model.autotrader.service.AccountValidationPublic;
import com.dakshata.data.model.autotrader.service.TradingAccountPublic;
import com.dakshata.data.model.common.IOperationResponse;
import com.dakshata.trading.model.basket.BasketExecutionResult;
import com.dakshata.trading.model.basket.BasketPlacementRequest;
import com.dakshata.trading.model.platform.PlatformHolding;
import com.dakshata.trading.model.platform.PlatformMargin;
import com.dakshata.trading.model.platform.PlatformOrder;
import com.dakshata.trading.model.platform.PlatformPosition;
import com.dakshata.trading.model.instrument.AccountContractSizing;
import com.dakshata.trading.model.portfolio.IOrder;
import com.dakshata.trading.model.tv.order.TvOrder;
import com.dakshata.trading.model.tv.position.TvPosSqOff;

/**
 * Responsible for trading activities.
 *
 * @author PRITESH
 *
 */
public interface ITradingService {

	/**
	 * Provides live pseudo accounts available under your user.
	 *
	 * @return live pseudo accounts
	 */
	IOperationResponse<Set<String>> fetchLivePseudoAccounts();

	/**
	 * Provides every trading account under your user, with its broker, platform, nickname and
	 * licence details. Never returns credentials or any other sensitive field. For more
	 * information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/fetch-all-trading-accounts/">api docs</a>.
	 *
	 * @return the trading accounts under your user
	 */
	IOperationResponse<Set<TradingAccountPublic>> fetchAllTradingAccounts();

	/**
	 * Adds a new broker trading account. The keys your account needs depend on the broker and
	 * platform, so see <a href=
	 * "https://stocksdeveloper.in/documentation/api/create-or-update-trading-account/">api docs</a>
	 * for the common and broker specific fields.
	 *
	 * @param account the account fields, as documented for your broker
	 * @return the id of the trading account that was created
	 */
	IOperationResponse<Long> createTradingAccount(Map<String, String> account);

	/**
	 * Updates an existing broker trading account. Takes the same fields as
	 * {@link #createTradingAccount(Map)}. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/create-or-update-trading-account/">api docs</a>.
	 *
	 * @param account the account fields, as documented for your broker
	 * @return the id of the trading account that was updated
	 */
	IOperationResponse<Long> updateTradingAccount(Map<String, String> account);

	/**
	 * Checks whether a set of broker credentials is valid, without saving an account. Useful before
	 * calling {@link #createTradingAccount(Map)}. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/validate-trading-account-credentials/">api
	 * docs</a>.
	 *
	 * @param account the account fields, as documented for your broker
	 * @return true when the credentials are valid
	 */
	IOperationResponse<Boolean> validateCredentials(Map<String, String> account);

	/**
	 * Checks whether an account you have already saved can still log in to the broker. Useful every
	 * morning before the market opens, to find accounts with expired credentials early. For more
	 * information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/validate-trading-account-credentials/">api
	 * docs</a>.
	 *
	 * @param tradingAccountId the trading account id
	 * @return true when the account can log in
	 */
	IOperationResponse<Boolean> validateAccount(Long tradingAccountId);

	/**
	 * Checks every trading account under your user in one call, and reports each one separately.
	 * The pre-market check {@link #validateAccount(Long)} does, for all accounts at once. For more
	 * information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/validate-trading-account-credentials/">api
	 * docs</a>.
	 *
	 * @return one result per trading account
	 */
	IOperationResponse<List<AccountValidationPublic>> validateAllAccounts();

	/**
	 * Places an order. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-advanced-order/">api
	 * docs</a>.
	 *
	 * @param order order object
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeOrder(IOrder order);

	IOperationResponse<String> placeOrder(String apiKey, IOrder order);

	/**
	 * Places an order received from Trading View. This method is not supposed to be
	 * used by outside world.
	 *
	 * @param apiKey api key
	 * @param order  order object
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<Boolean> placeTvOrder(String apiKey, TvOrder order);

	/**
	 * Places one multi-leg option structure across every chosen account, in a single call.
	 *
	 * <p>
	 * The fan-out, the hedge ordering and the exchange freeze slicing all happen server-side. A
	 * caller looping over legs itself could not guarantee hedge order under a slow response, and
	 * each leg it sent would be an independent submission with nothing tying them together in the
	 * record.
	 * </p>
	 *
	 * <p>
	 * A {@code status=false} response whose message explains the refusal means <b>nothing was
	 * placed</b> — the basket was rejected before the first order went out. Any other failure
	 * means orders may already be live, so the outcome must be reviewed rather than retried.
	 * </p>
	 *
	 * @param basket the resolved legs, the accounts, the product type and the lot multiplier
	 * @return the execution id, its roll-up status and one result per account
	 */
	IOperationResponse<BasketExecutionResult> placeBasket(String apiKey, BasketPlacementRequest basket);

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
	 * every broker we support. For more information, please see <a href=
	 * "https://stocksdeveloper.in/documentation/api/place-autotrader-bracket-order/">api
	 * docs</a>.
	 *
	 * @param pseudoAccount    pseudo account
	 * @param exchange         exchange
	 * @param symbol           symbol
	 * @param tradeType        trade type
	 * @param orderType        order type
	 * @param quantity         quantity
	 * @param price            price
	 * @param triggerPrice     trigger price (entry trigger; pass zero otherwise)
	 * @param target           target, in rupees away from your entry price
	 * @param stoploss         stoploss, in rupees away from your entry price
	 * @param trailingStoploss trailing stoploss step, in rupees
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
	 * @param pseudoAccount    pseudo account
	 * @param exchange         exchange
	 * @param symbol           symbol
	 * @param tradeType        trade type
	 * @param orderType        order type
	 * @param quantity         quantity
	 * @param price            price
	 * @param stoploss         stoploss, in rupees away from your entry price
	 * @param trailingStoploss trailing stoploss step, in rupees
	 * @return the order id given by your stock broker
	 */
	IOperationResponse<String> placeAutoTraderCoverOrder(String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, int quantity, float price, float stoploss,
			float trailingStoploss);

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

	IOperationResponse<Boolean> cancelAllOrders(String apiKey, String pseudoAccount);

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

	IOperationResponse<Boolean> cancelOrderByPlatformId(String apiKey, String pseudoAccount, String platformId);

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

	IOperationResponse<Boolean> cancelChildOrdersByPlatformId(String apiKey, String pseudoAccount, String platformId);

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

	IOperationResponse<Boolean> modifyOrderByPlatformId(final String apiKey, final String pseudoAccount,
			final String platformId, final OrderType orderType, final Integer quantity, final Float price,
			final Float triggerPrice);

	/**
	 * Modifies the order, additionally setting the disclosed quantity percentage.
	 *
	 * @param disclosedQtyPct percent of order quantity to show in market depth
	 *                        (0 = no change to existing disclosed quantity;
	 *                        10–100 = set new disclosed quantity; values below 10
	 *                        are auto-adjusted to 10 to satisfy exchange rules).
	 *                        Applies only when the resolved order type is LIMIT
	 *                        or STOP_LOSS; ignored for MARKET / SL_MARKET.
	 */
	IOperationResponse<Boolean> modifyOrderByPlatformId(final String apiKey, final String pseudoAccount,
			final String platformId, final OrderType orderType, final Integer quantity, final Float price,
			final Float triggerPrice, final Integer disclosedQtyPct);

	/**
	 * Submits a square-off position request.
	 *
	 * @param pseudoAccount pseudo account
	 * @param category      position category
	 * @param type          position type
	 * @param exchange      position exchange (broker independent exchange)
	 * @param symbol        position symbol (broker independent symbol)
	 * @return true on successful acceptance of square-off request, false otherwise
	 */
	IOperationResponse<Boolean> squareOffPosition(final String pseudoAccount, final PositionCategory category,
			final PositionType type, final String exchange, final String symbol, final boolean cancelOpenOrders);

	IOperationResponse<Boolean> squareOffPosition(final String apiKey, final String pseudoAccount,
			final PositionCategory category, final PositionType type, final String exchange, final String symbol,
			final boolean cancelOpenOrders);

	IOperationResponse<Boolean> squareOffTvPosition(String apiKey, TvPosSqOff input);

	/**
	 * Submits a square-off portfolio request.
	 *
	 * @param pseudoAccount pseudo account
	 * @param category      position category (DAY or NET portfolio to consider)
	 * @return true on successful acceptance of square-off request, false otherwise
	 */
	IOperationResponse<Boolean> squareOffPortfolio(final String pseudoAccount, final PositionCategory category,
			final boolean cancelOpenOrders);

	IOperationResponse<Boolean> squareOffPortfolio(final String apiKey, final String pseudoAccount,
			final PositionCategory category, final boolean cancelOpenOrders);

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

	IOperationResponse<Set<PlatformOrder>> readPlatformOrders(String apiKey, final String pseudoAccount);

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
	 * Used for selling or adding to holdings.
	 *
	 * @param apiKey  api key
	 * @param request sell holdings request
	 * @return order ids
	 */
	IOperationResponse<List<AdjustHoldingsResponse>> adjustHoldings(final String apiKey,
			final AdjustHoldingsRequest request);

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

	/**
	 * This is for internal use. Resolves the sizing numbers for one contract <b>as they apply to
	 * one account</b>: the lot size that account's broker declares, and the underlying's exchange
	 * freeze cap.
	 * <p>
	 * The lot size is a per-broker fact, not a per-contract one. On MCX GOLDM, Angel declares 100
	 * units per lot and Zerodha declares 1, and each is describing its own API correctly. Resolving
	 * it from the single-row-per-symbol independent instrument instead yields whichever broker's
	 * instrument download happened to run last, which can change with nothing else changing.
	 * <p>
	 * Resolution only - the caller still decides the quantity and still slices it.
	 *
	 * @param apiKey        the caller's API key
	 * @param pseudoAccount the account the order would be placed into
	 * @param exchange      independent exchange name, e.g. {@code MCX}
	 * @param symbol        independent (normalised) symbol, e.g. {@code GOLDM_04-SEP-2026_FUT}
	 */
	IOperationResponse<AccountContractSizing> resolveContractSizing(String apiKey, String pseudoAccount,
			String exchange, String symbol);

	/**
	 * This is for internal use. It is used by master-child order copying process.
	 */
	IOperationResponse<String> placeOrderMCA(String apiKey, String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, ProductType productType, int quantity, float price,
			float triggerPrice, Validity validity, Boolean amo, String publisherId, String commandId);

	/**
	 * This is for internal use. It is used by the signal service to place an
	 * AutoTrader bracket or cover order from a TradingView alert.
	 * <p>
	 * Same as the {@code traceId} variant below, plus the order variety and the
	 * three level fields. {@code target} / {@code stoploss} /
	 * {@code trailingStoploss} are distances in rupees from the fill price and are
	 * meaningful only for {@link Variety#AT_BO} / {@link Variety#AT_CO}; pass zero
	 * for a {@link Variety#REGULAR} order.
	 */
	IOperationResponse<String> placeOrderMCA(String apiKey, Variety variety, String pseudoAccount, String exchange,
			String symbol, TradeType tradeType, OrderType orderType, ProductType productType, int quantity, float price,
			float triggerPrice, float target, float stoploss, float trailingStoploss, Validity validity, Boolean amo,
			String publisherId, String commandId, String traceId);

	/**
	 * This is for internal use. It is used by master-child order copying process.
	 * <p>
	 * Variant that also carries a {@code traceId} so end-to-end timing of the
	 * master&rarr;child copy unit can be correlated across services.
	 */
	IOperationResponse<String> placeOrderMCA(String apiKey, String pseudoAccount, String exchange, String symbol,
			TradeType tradeType, OrderType orderType, ProductType productType, int quantity, float price,
			float triggerPrice, Validity validity, Boolean amo, String publisherId, String commandId, String traceId);

	/**
	 * This is for internal use. It is used by master-child order copying process.
	 */
	IOperationResponse<Boolean> cancelOrderMCA(String apiKey, String pseudoAccount, String platformId,
			String commandId);

	/**
	 * This is for internal use. It is used by master-child order copying process.
	 */
	IOperationResponse<Boolean> modifyOrderMCA(final String apiKey, final String pseudoAccount, final String platformId,
			final OrderType orderType, final Integer quantity, final Float price, final Float triggerPrice,
			String commandId);

}
