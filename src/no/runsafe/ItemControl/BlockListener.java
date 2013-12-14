package no.runsafe.ItemControl;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.block.ICreatureSpawner;
import no.runsafe.framework.api.event.block.IBlockBreakEvent;
import no.runsafe.framework.api.event.block.IItemDispensed;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.minecraft.RunsafeEntityType;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Enchant;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.block.RunsafeBlockBreakEvent;
import no.runsafe.framework.minecraft.item.RunsafeItemStack;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

import java.util.logging.Level;

public class BlockListener implements IBlockBreakEvent, IItemDispensed
{
	public BlockListener(Globals globals, IScheduler scheduler, IConsole output)
	{
		this.console = output;
		this.globals = globals;
		this.scheduler = scheduler;
	}

	@Override
	public void OnBlockBreakEvent(RunsafeBlockBreakEvent event)
	{
		if (event.isCancelled())
			return;

		final RunsafeBlockBreakEvent blockBreakEvent = event;

		IPlayer thePlayer = event.getPlayer();
		RunsafeItemStack heldItem = thePlayer.getItemInHand();
		final IBlock theBlock = event.getBlock();

		if (Enchant.SilkTouch.isOn(heldItem) && this.globals.spawnerIsHarvestable(thePlayer.getWorld()))
		{
			try
			{
				ICreatureSpawner spawner = (ICreatureSpawner) theBlock;
				final RunsafeEntityType creature = spawner.getCreature();
				if (!globals.spawnerTypeValid(creature, thePlayer))
				{
					console.outputToConsole(
						String.format(
							"%s tried harvesting an invalid %s spawner!",
							thePlayer.getName(),
							creature.getName()
						),
						Level.WARNING
					);
					return;
				}
				scheduler.createSyncTimer(
					new Runnable()
					{
						@Override
						public void run()
						{
							if (blockBreakEvent.isCancelled())
								return;
							Item.Miscellaneous.MonsterEgg.Get(creature).Drop(theBlock.getLocation(), 1);
						}
					},
					10L
				);
				blockBreakEvent.setXP(0);
			}
			catch (Exception e)
			{
				console.logException(e);
			}
		}
	}

	@Override
	public boolean OnBlockDispense(IBlock block, RunsafeMeta itemStack)
	{
		IWorld blockWorld = block.getWorld();
		ILocation blockLocation = block.getLocation();
		if (this.globals.itemIsDisabled(block.getWorld(), itemStack.getItemId()))
		{
			blockWorld.createExplosion(blockLocation, 0, true);
			block.breakNaturally();
			return false;
		}
		return true;
	}

	private final IConsole console;
	private final Globals globals;
	private final IScheduler scheduler;
}
