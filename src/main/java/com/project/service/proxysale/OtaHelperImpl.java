package com.project.service.proxysale;

import com.project.common.Constants;
import com.project.entity.proxysale.PriceStrategy;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 *         2015-11-11 11:07
 */
@Component
public class OtaHelperImpl implements OtaHelper {
    @Override
    public Short convertStrategy(String strategyStr) {
        Short strategy;
        if(Constants.PRICE_STRATEGY_BASE.equals(strategyStr)){
            strategy = PriceStrategy.STRATEGY_BASE_PRICE;
        }else if(Constants.PRICE_STRATEGY_SALE.equals(strategyStr)){
            strategy = PriceStrategy.STRATEGY_SALE_PRICE;
        }else if(Constants.PRICE_STRATEGY_SALE_BASE.equals(strategyStr)){
            strategy = PriceStrategy.STRATEGY_SALE_BASE_PRICE;
        }else{
            strategy = null;
        }
        return strategy;
    }
}
