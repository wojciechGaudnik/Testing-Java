import java.util.Objects;

public class Money implements Expression{

	protected final int amount;
	protected final String currency;

	public Money(int amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public String currency(){
		return currency;
	}

	@Override
	public Expression times(int multiplier) {
		return new Money(amount * multiplier, this.currency);
	}

	public static Money dollar(int amount) {
		return new Money(amount, "USD");
	}

	public static Money franc(int amount) {
		return new Money(amount, "CHF");
	}

	@Override
	public Expression plus(Expression addend) {
		return new Sum(this, addend);
	}

	@Override
	public Money reduce(Bank bank, String to){
		return new Money(amount / bank.rate(this.currency, to), to);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Money money = (Money) o;

		if (amount != money.amount) return false;
		return Objects.equals(currency, money.currency);
	}
}
