package com.dariawan.log4j.dao;
 
import java.text.MessageFormat;
import org.apache.log4j.Logger;
 
public class WorldDominationDao {
 
    private final Logger logger = Logger.getLogger(this.getClass());
 
    public void divide(int number1, int number2) {
        logger.debug("Run divide method");
 
        String message;
        try {
            int number = number1 / number2;
            if (logger.isDebugEnabled()) {
                message = MessageFormat.format("Info: {0} / {1} = {2}", number1, number2, number);
                logger.debug(message);
            }
        } catch (ArithmeticException e) {
            message = MessageFormat.format("Error: Cannot divide two number {0} / {1}", number1, number2);
            logger.error(message, e);
        }
    }
 
    public void conquer(int days) {
        logger.debug("Run conquer method");
 
        if (days < 0) {
            throw new IllegalArgumentException("Days must be greater than zero");
        } else {
            if (logger.isInfoEnabled()) {
                logger.debug(MessageFormat.format("Take over the world in {0} days", days));
            }
        }
    }
 
    public void destroy() {
        throw new IllegalStateException("Cannot run destroy method");
    }
}
