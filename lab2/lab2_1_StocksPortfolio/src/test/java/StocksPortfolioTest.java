import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

public class StocksPortfolioTest {

    @Mock
    private IStockmarketService stockmarketServiceMock;

    private StocksPortfolio portfolio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolio = new StocksPortfolio(stockmarketServiceMock);
    }

    @Test
    void testAddStock() {
        // Arrange: Define stock prices, including extra stocks not used in the test
        when(stockmarketServiceMock.lookUpPrice("AAPL")).thenReturn(150.0);
        // Act: Add stocks
        portfolio.addStock(new Stock("AAPL", 3));

        // Assert: Use Hamcrest assertions
        assertEquals(1, portfolio.getStocks().size());
        assertEquals("AAPL", portfolio.getStocks().get(0).getLabel());

    }

    @Test
    void testTotalValueWithHamcrest() {
        // Arrange: Define stock prices, including extra stocks not used in the test
        when(stockmarketServiceMock.lookUpPrice("AAPL")).thenReturn(150.0);
        when(stockmarketServiceMock.lookUpPrice("GOOGL")).thenReturn(2800.0);
        when(stockmarketServiceMock.lookUpPrice("TSLA")).thenReturn(900.0);
        when(stockmarketServiceMock.lookUpPrice("AMZN")).thenReturn(3300.0);
        when(stockmarketServiceMock.lookUpPrice("MSFT")).thenReturn(310.0);

        // Act: Add stocks
        portfolio.addStock(new Stock("AAPL", 3));
        portfolio.addStock(new Stock("GOOGL", 2));
        portfolio.addStock(new Stock("TSLA", 1));
        portfolio.addStock(new Stock("AMZN", 1));
        portfolio.addStock(new Stock("MSFT", 1));

        // Expected correct total value
        double expectedTotal = (3 * 150.0) + (2 * 2800.0) + (1 * 900.0) + (1 * 3300.0) + (1 * 310.0);
        assertThat("Total portfolio value should be correct", portfolio.totalValue(), equalTo(expectedTotal));


        // Verify that stock prices were looked up
        verify(stockmarketServiceMock, times(1)).lookUpPrice("AAPL");
        verify(stockmarketServiceMock, times(1)).lookUpPrice("GOOGL");
        verify(stockmarketServiceMock, times(1)).lookUpPrice("TSLA");
        verify(stockmarketServiceMock, times(1)).lookUpPrice("AMZN");
        verify(stockmarketServiceMock, times(1)).lookUpPrice("MSFT");

    }

    @Test
    void testMostValuableStocks(){

        portfolio.addStock(new Stock("AAPL", 3));
        portfolio.addStock(new Stock("AMZN", 1));

        List<Stock> mostValuableStocks = portfolio.mostValuableStocks(2);
        assertEquals(2, mostValuableStocks.size());
        assertEquals("AAPL", mostValuableStocks.get(0).getLabel());
        assertEquals("AMZN", mostValuableStocks.get(1).getLabel());
    }
}
