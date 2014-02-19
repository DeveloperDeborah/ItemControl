package no.runsafe.ItemControl.trading.commands;

import no.runsafe.ItemControl.trading.TradingHandler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class CreateNamedTrader extends PlayerCommand
{
	public CreateNamedTrader(TradingHandler handler)
	{
		super("createnamed", "Create a trader", "runsafe.traders.create", new RequiredArgument("name"));
		this.handler = handler;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		// Spawn a merchant at the players location with a name!
		handler.createTrader(executor.getLocation(), parameters.get("name"));
		return "&eMerchant spawned!";
	}

	private final TradingHandler handler;
}
