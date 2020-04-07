public class Sum implements Expression{
	final Expression augment;
	final Expression addment;

	public Sum(Expression augment, Expression addmend) {
		this.augment = augment;
		this.addment = addmend;
	}

	@Override
	public Money reduce(Bank bank, String to) {
		int amount = augment.reduce(bank, to).amount + addment.reduce(bank, to).amount;
		return new Money(amount, to);
	}

	@Override
	public Expression plus(Expression addend) {
		return new Sum(this, addment);
	}

	@Override
	public Expression times(int multiplier) {
		return new Sum(augment.times(multiplier), addment.times(multiplier));
	}
}
