import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tqs.IStockmarketService;
import tqs.Stock;
import tqs.StocksPortfolio;

import java.util.List;

public class _Junit_StocksPortfolioTest {

    @Mock
    private IStockmarketService stockmarketServiceMock;

    private StocksPortfolio portfolio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolio = new StocksPortfolio(stockmarketServiceMock);
    }

    @Test
    void testTotalValueWithExtraMockedStocks() {
        when(stockmarketServiceMock.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketServiceMock.lookUpPrice("GOOGL")).thenReturn(2800.0);
        when(stockmarketServiceMock.lookUpPrice("TSLA")).thenReturn(900.0);   // Not used in test
        when(stockmarketServiceMock.lookUpPrice("AMZN")).thenReturn(3300.0);  // Not used in test
        when(stockmarketServiceMock.lookUpPrice("MSFT")).thenReturn(310.0);   // Not used in test

        portfolio.addStock(new Stock("AAPL", 2));
        portfolio.addStock(new Stock("GOOGL", 1));
        portfolio.addStock(new Stock("TSLA", 1));  // Not used in test
        portfolio.addStock(new Stock("AMZN", 1));  // Not used in test
        portfolio.addStock(new Stock("MSFT", 1));  // Not used in test

        List<Stock> mostValuableStocks = portfolio.mostValuableStocks(2);

        assertEquals(2, mostValuableStocks.size());
        assertEquals("GOOGL", mostValuableStocks.get(0).getLabel());
        assertEquals("AAPL", mostValuableStocks.get(1).getLabel());

        double expectedTotal = (2 * 150.0) + (1 * 2800.0); // 300 + 2800 = 3100
        assertEquals(expectedTotal, portfolio.totalValue());

        verify(stockmarketServiceMock, times(1)).lookUpPrice("AAPL");
        verify(stockmarketServiceMock, times(1)).lookUpPrice("GOOGL");

        verify(stockmarketServiceMock, never()).lookUpPrice("TSLA");
        verify(stockmarketServiceMock, never()).lookUpPrice("AMZN");
        verify(stockmarketServiceMock, never()).lookUpPrice("MSFT");
    }
}
