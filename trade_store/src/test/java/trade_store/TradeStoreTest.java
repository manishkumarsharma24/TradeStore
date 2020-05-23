package trade_store;

import static org.junit.Assert.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import junit.framework.TestCase;

public class TradeStoreTest extends TestCase{
	
	@Test
	public void testAddTrade() {
		
		TradeStore tradeStore = new TradeStore();
		
		Trade trade1 = new Trade("T1", 1, "CP-1", "B1", getDateFromString("20/05/2021"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade1);
		
		Trade trade2 = new Trade("T2", 1, "CP-1", "B1", getDateFromString("20/05/2022"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade2);
		
		Trade trade3 = new Trade("T2", 2, "CP-2", "B1", getDateFromString("20/05/2022"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade3);
		
		assertEquals(trade1, tradeStore.findTradesById(trade1.getTradeId()).get(0));
		
		System.out.println("*************************************************testAddTrade***************************************************");
		
		System.out.println(tradeStore.toString());
		
		System.out.println("************************************************************ ***************************************************");


	}
	
	@Test(expected = RuntimeException.class)
	public void testLowerVersionIdException() {
		TradeStore tradeStore = new TradeStore();
		
		Trade trade1 = new Trade("T5", 2, "CP-2", "B1", getDateFromString("20/05/2022"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade1);
		
		Trade trade2 = new Trade("T5", 1, "CP-1", "B1", getDateFromString("20/05/2022"), getDateFromString("23/05/2020"));
		Exception exception = assertThrows(RuntimeException.class, ()-> tradeStore.addTrade(trade2));
		assertEquals("Trade is of lower version.", exception.getMessage());

		System.out.println("*************************************************testLowerVersionIdException***************************************************");
		
		System.out.println(tradeStore.toString());
		
		System.out.println("************************************************************ ***************************************************");
				
	}
	
	@Test(expected = RuntimeException.class)
	public void testPastMaturityDateException() {
		TradeStore tradeStore = new TradeStore();
		
		Trade trade1 = new Trade("T7", 2, "CP-2", "B1", getDateFromString("20/05/2019"), getDateFromString("23/05/2020"));
		
		Exception exception = assertThrows(RuntimeException.class, ()-> tradeStore.addTrade(trade1));
		assertEquals("Trade maturity date has already passed.", exception.getMessage());
		
		System.out.println("*************************************************testPastMaturityDateException***************************************************");
		
		System.out.println(tradeStore.toString());
		
		System.out.println("************************************************************ ***************************************************");
	}
	
	/**
	 * To test this Maturity Date validation need to be commented.
	 */
	@Test
	public void testExpiryFlag() {
		TradeStore tradeStore = new TradeStore();
		
		Trade trade1 = new Trade("T10", 2, "CP-2", "B1", getDateFromString("22/05/2020"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade1);
		
		Trade trade2 = new Trade("T11", 1, "CP-1", "B1", getDateFromString("20/05/2022"), getDateFromString("23/05/2020"));
		tradeStore.addTrade(trade2);
		
		tradeStore.updateTradesToExpired();
		
		assertEquals(tradeStore.findTradesById("T10").getFirst().getExpired(), "Y");
		assertEquals(tradeStore.findTradesById("T11").getFirst().getExpired(), "N");

		System.out.println("*************************************************testExpiryFlag***************************************************");
		
		System.out.println(tradeStore.toString());
		
		System.out.println("************************************************************ ***************************************************");	
	}
	
	private static Date getDateFromString(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		
		try {
    		date = formatter.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}

}
