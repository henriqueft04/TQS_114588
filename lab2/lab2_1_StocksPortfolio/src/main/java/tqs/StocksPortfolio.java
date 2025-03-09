package tqs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StocksPortfolio {
    private IStockmarketService stockmarketService;
    private List<Stock> stocks;

    public StocksPortfolio(IStockmarketService stockmarketService) {
        this.stockmarketService = stockmarketService;
        this.stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public double totalValue() {
        return stocks.stream()
                .mapToDouble(stock -> stockmarketService.lookUpPrice(stock.getLabel()) * stock.getQuantity())
                .sum();
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    /**
     * Returns the top N most valuable stocks in the portfolio.
     *
     * @param topN the number of most valuable stocks to return
     * @return a list with the topN most valuable stocks in the portfolio
     */
    public List<Stock> mostValuableStocks(int topN) {
        return stocks.stream()
                .sorted(Comparator.comparingDouble((Stock stock) ->
                                stockmarketService.lookUpPrice(stock.getLabel()) * stock.getQuantity())
                        .reversed())  // Sort in descending order of value
                .limit(topN)  // Keep only top N
                .collect(Collectors.toList());
    }
}
