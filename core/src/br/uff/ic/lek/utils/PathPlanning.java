package br.uff.ic.lek.utils;

import br.uff.ic.lek.game.World;
import br.uff.ic.lek.pathfinder.FlatTiledGraph;
import br.uff.ic.lek.pathfinder.FlatTiledNode;
import br.uff.ic.lek.pathfinder.TiledManhattanDistance;
import br.uff.ic.lek.pathfinder.TiledRaycastCollisionDetector;
import br.uff.ic.lek.pathfinder.TiledSmoothableGraphPath;

import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;


public class PathPlanning {
	public World world;

	public FlatTiledGraph worldMap;
	public TiledSmoothableGraphPath<FlatTiledNode> path;
	public TiledManhattanDistance<FlatTiledNode> heuristic;
	public IndexedAStarPathFinder<FlatTiledNode> pathFinder;
	public PathSmoother<FlatTiledNode, Vector2> pathSmoother;

	public ShapeRenderer renderer;
	public Vector3 tmpUnprojection = new Vector3();

	public float tileWidth = 32;
	public float tileHeight = 32;
	public int lastScreenX;
	public int lastScreenY;
	public int lastEndTileX;
	public int lastEndTileY;
	public int startTileX;
	public int startTileY;
	public boolean smooth = false;

	private int[][] pathMap;

	public PathPlanning(World world) {
		this.world = world;
	}

	public void create() {
			lastEndTileX = 4;
			lastEndTileY = 4;
			lastScreenX = lastEndTileX * (int) tileWidth;
			lastScreenY = lastEndTileY * (int) tileWidth;
			startTileX = 1;
			startTileY = 1;

			worldMap = new FlatTiledGraph(World.xTiles, World.yTiles);
			tileWidth = (float) world.map.getProperties().get("tilewidth", Integer.class);
			tileHeight = (float) world.map.getProperties().get("tileheight", Integer.class);
			pathMap = new int[World.xTiles][World.yTiles];

			int maiorValorEncontrado = 0, min = 1, max = 100 * World.xTiles * World.yTiles;
			int avatarX = 0;
			int avatarY = 0;

			for (int i = 0; i < World.xTiles; i++) {
				for (int j = 0; j < World.yTiles; j++) {
					TiledMapTileLayer cur = (TiledMapTileLayer) world.map.getLayers().get("path01");
					if (cur.getCell(i, j) != null) {
						cur.setVisible(true);
						pathMap[i][j] = 1;
						int num = RandomNumber.random(min, max);

						if (num > maiorValorEncontrado) {
							maiorValorEncontrado = num;
							avatarX = i;
							avatarY = j;
							System.out.println("maior valor até agora=" + maiorValorEncontrado + " posX=" + avatarX + " posY=" + avatarY);
						}
					} else {
						pathMap[i][j] = 0;
					}
				}
			}

			worldMap.init(pathMap);


			avatarX = 0;
			avatarY = 0;
			maiorValorEncontrado = 0;
			for (int i = 0; i < World.xTiles; i++) {
				for (int j = 0; j < World.yTiles; j++) {
					TiledMapTileLayer cur = (TiledMapTileLayer) world.map.getLayers().get("cavernas");
					if (cur.getCell(i, j) != null) {
						cur.setVisible(true);
						int num = RandomNumber.random(min, max);
						if (num > maiorValorEncontrado) {
							maiorValorEncontrado = num;
							avatarX = i-1;
							avatarY = j-1;
							System.out.println("maior valor de caverna até agora=" + maiorValorEncontrado + " posX=" + avatarX + " posY=" + avatarY);
						}
					}
				}
			}


			World.avatarStartTileX = avatarX;
			World.avatarStartTileY = avatarY;

			path = new TiledSmoothableGraphPath<>();
			heuristic = new TiledManhattanDistance<>();
			pathFinder = new IndexedAStarPathFinder<>(worldMap, true);
			pathSmoother = new PathSmoother<>(new TiledRaycastCollisionDetector<>(worldMap));
			renderer = new ShapeRenderer();
			smooth = true;
			worldMap.diagonal = true;
	}

	public boolean getClosestTileWithID(TileCell1 closest) {
		if (closest.u < 0 || closest.u >= World.xTiles) return false;
		if (closest.v < 0 || closest.v >= World.yTiles) return false;
		if (pathMap[closest.u][closest.v] == closest.id) return true;
			
		int passo = 1;
		while (passo <= World.xTiles || passo <= World.yTiles) {
			for (int v = closest.v - passo; v <= closest.v + passo; v++) {
				if (v < 0 || v >= World.yTiles) continue;
				int u = closest.u - passo;
				if (u < 0) break;
				if (pathMap[u][v] == closest.id) {
					closest.u = u;
					closest.v = v;
					return true;
				}
			}
			for (int v = closest.v - passo; v <= closest.v + passo; v++) {
				if (v < 0 || v >= World.yTiles) continue;
				int u = closest.u + passo;
				if (u >= World.xTiles) break;
				if (pathMap[u][v] == closest.id) {
					closest.u = u;
					closest.v = v;
					return true;
				}
			}
			for (int u = closest.u - passo; u <= closest.u + passo; u++) {
				if (u < 0 || u >= World.xTiles) continue;
				int v = closest.v - passo;
				if (v < 0) break;
				if (pathMap[u][v] == closest.id) {
					closest.u = u;
					closest.v = v;
					return true;
				}
			}
			for (int u = closest.u - passo; u <= closest.u + passo; u++) {
				if (u < 0 || u >= World.xTiles) continue;
				int v = closest.v + passo;
				if (v >= World.yTiles) break;
				if (pathMap[u][v] == closest.id) {
					closest.u = u;
					closest.v = v;
					return true;
				}
			}
			passo++;
		}
		return false;
	}

	public void render(float delta) {
		renderer.setProjectionMatrix(getCamera().combined);
		renderer.begin(ShapeType.Filled);
		for (int x = 0; x < FlatTiledGraph.sizeX; x++) {
			for (int y = 0; y < FlatTiledGraph.sizeY; y++) {
				switch (worldMap.getNode(x, y).type) {
					case FlatTiledNode.TILE_FLOOR:
						renderer.setColor(Color.WHITE);
						break;
					case FlatTiledNode.TILE_WALL:
						renderer.setColor(Color.GRAY);
						break;
					default:
						renderer.setColor(Color.BLACK);
						break;
				}
			}
		}

		renderer.setColor(Color.MAGENTA);
		int nodeCount = path.getCount();
		for (int i = 0; i < nodeCount; i++) {
			FlatTiledNode node = path.nodes.get(i);
			System.out.println("Valor de X "+node.x);
			System.out.println("Valor de Y "+node.y);
			System.out.println("Valor de XWidth "+tileWidth);
			renderer.rect(node.x * tileWidth, node.y * tileWidth, tileWidth/2, tileHeight/2);
		}

		if (smooth) {
			renderer.end();
			renderer.begin(ShapeType.Line);
			float hw = tileWidth / 2f;
			if (nodeCount > 0) {
				FlatTiledNode prevNode = path.nodes.get(0);
				for (int i = 1; i < nodeCount; i++) {
					FlatTiledNode node = path.nodes.get(i);
					renderer.line(node.x * tileWidth + hw, node.y * tileWidth + hw, prevNode.x * tileWidth + hw, prevNode.y * tileWidth + hw);
					prevNode = node;
				}
			}
		}

		renderer.end();
	}

	public Vector3 getTarget(){
		if(path.getCount()==0)return null;
		return new Vector3(path.nodes.get(path.getCount()-1).x*32,path.nodes.get(path.getCount()-1).y*32,0);
	}
	public void pop(){
		path.nodes.pop();
	}
	public void dispose() {
		renderer.dispose();
		worldMap = null;
		path = null;
		heuristic = null;
		pathFinder = null;
		pathSmoother = null;
	}

	public Camera getCamera() {
		return world.getCamera();
	}

	private void updatePath(boolean forceUpdate) {
		Vector3 vec = new Vector3(lastScreenX, lastScreenY, 0);

		getCamera().update();
		getCamera().unproject(vec);

		int tileX = (int) (vec.x / tileWidth);
		int tileY = (int) (vec.y / tileWidth);

		if (tileX < 0 || tileX >= World.xTiles || tileY < 0 || tileY >= World.yTiles) return;
		if (!(forceUpdate || tileX != lastEndTileX || tileY != lastEndTileY)) return;

		FlatTiledNode startNode = worldMap.getNode(startTileX, startTileY);
		FlatTiledNode endNode = worldMap.getNode(tileX, tileY);

		if (!(forceUpdate || endNode.type == FlatTiledNode.TILE_FLOOR)) return;

		if (endNode.type == FlatTiledNode.TILE_FLOOR) {
			lastEndTileX = tileX;
			lastEndTileY = tileY;
		} else {
			endNode = worldMap.getNode(lastEndTileX, lastEndTileY);
		}

		path.clear();
		worldMap.startNode = startNode;
		long startTime = nanoTime();
		pathFinder.searchNodePath(startNode, endNode, heuristic, path);

		if (pathFinder.metrics != null) {
			float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
			System.out.println("----------------- Indexed A* Path Finder Metrics -----------------");
			System.out.println("Visited nodes................... = " + pathFinder.metrics.visitedNodes);
			System.out.println("Open list additions............. = " + pathFinder.metrics.openListAdditions);
			System.out.println("Open list peak.................. = " + pathFinder.metrics.openListPeak);
			System.out.println("Path finding elapsed time (ms).. = " + elapsed);
		}

		if (smooth) {
			startTime = nanoTime();
			pathSmoother.smoothPath(path);
			
			if (pathFinder.metrics != null) {
				float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
				System.out.println("Path smoothing elapsed time (ms) = " + elapsed);
			}
		}
	}

	private long nanoTime() {
		return pathFinder.metrics == null ? 0 : TimeUtils.nanoTime();
	}

	public boolean targetChanged(int screenX, int screenY) {
		lastScreenX = screenX;
		lastScreenY = screenY;

		updatePath(true);

		startTileX = lastEndTileX;
		startTileY = lastEndTileY;

		return true;
	}
}

class TileCell1 {
	public int id;
	public int u;
	public int v;

	public TileCell1(int u, int v, int id) {
		this.u = u;
		this.v = v;
		this.id = id;
	}
}