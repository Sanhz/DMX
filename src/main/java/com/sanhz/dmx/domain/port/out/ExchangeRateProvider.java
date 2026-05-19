package com.sanhz.dmx.domain.port.out;

import com.sanhz.dmx.domain.port.out.ExchangeRates;

/**
 * Port that provides exchange rates and the date of the rates.
 */
public interface ExchangeRateProvider {

    ExchangeRates getRates();

}
