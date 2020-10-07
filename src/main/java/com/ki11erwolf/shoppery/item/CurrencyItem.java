package com.ki11erwolf.shoppery.item;

/**
 * Base implementation for currency type items that are used to
 * sell, buy, and trade.
 *
 * Implemented by (Coin and Note) Item classes to define themselves
 * as a type of currency with a numerical worth/price. Additionally,
 * this interface provides a method for inheriting classes to define
 * their value, along with some related utility methods.
 */
public interface CurrencyItem {

    /**
     * Gets the amount of cash this currency item is worth. The
     * returned value will be a constant
     *
     * <p>
     * Implementations should return a whole number with no values
     * proceeding the decimal point for whole currency that is not
     * cents. Cents and other factional currency should return a
     * decimal double, where the value is always equal to {@code 0}
     * with a decimal defining the number of cents. E.g:
     * {@code 5.0==$5, 100.0==$100, 0.50==50c, 0.05==5c}.
     *
     * <p>Currency items such as {@link NoteItem Notes} and {@link
     * CoinItem Coins} override this method to define themselves as
     * currency and to define their worth. Additional currency items
     * can be implemented with this.
     *
     * <p>{@link #isCashValueValid() Invalid} values should be
     * unexpected and ignored such that the item is considered to be
     * worthless currency. Use {@link #getSanitizedCashValue()} for
     * a validated cash value. The value is invalid if the number is
     * below {@code 0}. Zero ({@code 0.00}) values define the currency
     * as worthless. <b>Whole numbers above one (1) with decimals</b>
     * are not invalid, however they <b>should be avoided</b> at all
     * costs as they will <b>not be handled correctly</b>.
     *
     * @return a positive double indicating the constant worth of the
     * inheriting item class & type.
     */
    double getCashValue();

    /**
     * Check if the currency items worth is a valid whole number above
     * one (1), which indicates that it is a whole unit of currency
     * and not cents.
     *
     * @return {@code true} if the currency item is a whole unit of
     * currency.
     */
    default boolean isWholeCashValue() {
        return getCashValue() >= 1 && isCashValueValid(); }

    /**
     * Check if the currency items worth is a valid factional number
     * above zero (0) and below one (1) with a decimal value, which
     * indicates that it is a faction of one (1) unit of currency
     * called cents.
     *
     * @return {@code true} if the currency item is cents, or is a
     * fraction of one (1) currency unit.
     */
    default boolean isFractionalCashValue() {
        return getCashValue() < 1 && isCashValueValid();
    }

    /**
     * @return {@code true} if the {@link #getCashValue() cash value}
     * is not negative.
     */
    default boolean isCashValueValid() {
        return !(getCashValue() < 0);
    }

    /**
     * Identical to {@link #getCashValue()}, except in cases where
     * the value is invalid ({@link #isCashValueValid()}). In such
     * cases, a value of {@code 0} is returned instead.
     *
     * @return {@link #getCashValue()} or {@code 0} if the cash value
     * is invalid.
     */
    default double getSanitizedCashValue() {
        if(isCashValueValid())
            return getCashValue();
        else return 0;
    }

    /**
     * Converts this currency items cash value from
     * a double, storing either a whole or fractional
     * value into an Integer value worth the same
     * amount. <b>Does NOT differentiate between whole
     * or fractional currency! A value of {@code 5} can
     * be either five units of whole currency or five
     * cents.</b>
     *
     * @return the {@link #getCashValue() cash value}
     * as an Integer value defining either whole or
     * fractional worth.
     */
    default int getSimpleCashValue() {
        if(!isCashValueValid())
            return 0;

        double cashValue = getSanitizedCashValue();
        boolean whole = isWholeCashValue();
        boolean fraction = isFractionalCashValue();

        if(whole)
            return ((int) cashValue);
        else if(fraction)
            return (int)(cashValue * 100);
        else return 0;
    }
}
