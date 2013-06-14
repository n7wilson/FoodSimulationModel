import java.math.BigDecimal;


public class Resources {
	public BigDecimal getMoney(double amount){
		BigDecimal amountBD = new BigDecimal(amount);
		amountBD.setScale(0, BigDecimal.ROUND_HALF_UP);
		return amountBD;
	}
}
