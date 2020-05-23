package trade_store;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class TradeStore {
	
	/**
	 * Map of Trade Id and list of active trades for that trade id
	 **/
	private LinkedHashMap<String, LinkedList<Trade>> activeTrades = new LinkedHashMap<String, LinkedList<Trade>>();
	
	/**
	 * Map of Trade Id and list of expired trades for that trade id
	 **/
	private LinkedHashMap<String, LinkedList<Trade>> expiredTrades = new LinkedHashMap<String, LinkedList<Trade>>();

	
	public void addTrade(Trade trade) {
		if(trade == null) {
			throw new RuntimeException("Trade object is null, can't add trade into store.");
		}
		
		if(trade.isTradeMaturityDateBeforeCurrentDate()) {
			throw new RuntimeException("Trade maturity date has already passed.");
		}
		
		LinkedList<Trade> tradesById = activeTrades.get(trade.getTradeId());
		
		if(tradesById == null || tradesById.size() == 0) {
			tradesById = new LinkedList<Trade>();
			tradesById.add(trade);
			activeTrades.put(trade.getTradeId(), tradesById);
			return;
		}
		
		if(trade.getVersion() < tradesById.getFirst().getVersion()) {
			throw new RuntimeException("Trade is of lower version.");
		}
		
		for(int index=0; index < tradesById.size(); index++) {
			if(trade.getVersion() == tradesById.get(index).getVersion()) {
				tradesById.set(index, trade);
				return;
			}
		}
		
		tradesById.addFirst(trade);
	}
	
	/**
	 * Update expired flag to Y for trades for which maturity date has passed current date and move these trades from 
	 * the collection of active trades to expired trades.
	 */
	public void updateTradesToExpired() {
		Iterator<String> iterator = activeTrades.keySet().iterator();
		while(iterator.hasNext()) {
			String tradeId = iterator.next();
			if(this.activeTrades.get(tradeId).getFirst().isTradeMaturityDateBeforeCurrentDate()) {
				LinkedList<Trade> tradesById = this.activeTrades.get(tradeId);
				tradesById.forEach(trade -> trade.setExpired("Y"));
				if(expiredTrades.get(tradeId) == null) {
					expiredTrades.put(tradeId,tradesById);
				}else {
					expiredTrades.get(tradeId).addAll(0, tradesById);
				}
				iterator.remove();
			}
		}
	}
	
	public LinkedList<Trade> findTradesById(String tradeId){
		return activeTrades.get(tradeId) != null ? activeTrades.get(tradeId) : expiredTrades.get(tradeId);
	}

	@Override
	public String toString() {
		return "TradeStore [activeTrades=" + activeTrades + ", expiredTrades=" + expiredTrades + "]";
	}
	
	
}
