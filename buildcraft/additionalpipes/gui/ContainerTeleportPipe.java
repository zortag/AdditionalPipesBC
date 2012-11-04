package buildcraft.additionalpipes.gui;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICrafting;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.transport.TileGenericPipe;

public class ContainerTeleportPipe extends Container {

	public int connectedPipes = 0;

	private int ticks = 0;
	private PipeTeleport pipe;
	private int freq;
	private boolean canReceive, isPublic;

	public ContainerTeleportPipe(TileGenericPipe tile) {
		pipe = (PipeTeleport) tile.pipe;
		canReceive = !pipe.logic.canReceive;
		isPublic = !pipe.logic.isPublic;
		freq = -1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void updateCraftingResults() {
		super.updateCraftingResults();
		int connectedPipesNew = connectedPipes;
		if(ticks % 40 == 0) { //reduce lag
			ticks = 0;
			AdditionalPipes.instance.logger.info("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, false).size();
			AdditionalPipes.instance.logger.info("New connected:" + connectedPipesNew);
		}
		ticks++;
		for (Object crafter : crafters) {
			if(freq != pipe.logic.freq) {
				((ICrafting) crafter).updateCraftingInventoryInfo(this, 0, pipe.logic.freq);
			}
			if(canReceive != pipe.logic.canReceive) {
				((ICrafting) crafter).updateCraftingInventoryInfo(this, 1, pipe.logic.canReceive ? 1 : 0);
			}
			if(connectedPipesNew != connectedPipes) {
				((ICrafting) crafter).updateCraftingInventoryInfo(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.logic.isPublic) {
				((ICrafting) crafter).updateCraftingInventoryInfo(this, 3, pipe.logic.isPublic ? 1 : 0);
			}
		}
		canReceive = pipe.logic.canReceive;
		freq = pipe.logic.freq;
		isPublic = pipe.logic.isPublic;
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.logic.freq = j;
			break;
		case 1:
			pipe.logic.canReceive = (j == 1);
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.logic.isPublic = (j == 1);
			break;
		}
	}

}