package br.uff.ic.lek.pathfinder;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;


public class HierarchicalTiledNode extends TiledNode<HierarchicalTiledNode> {
	public final int index;

	public HierarchicalTiledNode (int x, int y, int type, int index, int connectionCapacity) {
		super(x, y, type, new Array<Connection<HierarchicalTiledNode>>(connectionCapacity));
		this.index = index;
	}

	@Override
	public int getIndex () {
		return index;
	}

	public HierarchicalTiledNode getLowerLevelNode() {
		return null;
	}

}
