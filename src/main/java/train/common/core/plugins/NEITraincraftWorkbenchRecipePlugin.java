package train.common.core.plugins;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import train.client.gui.GuiTrainCraftingBlock;
import train.common.inventory.TrainCraftingManager;
import train.common.recipes.ShapedTrainRecipes;

public class NEITraincraftWorkbenchRecipePlugin extends ShapedRecipeHandler {
	private Map<Integer, List<ShapedTrainRecipes>> recipeListWB = workbenchListCleaner(
			TrainCraftingManager.getInstance().getShapedRecipes());

	private CachedShapedRecipe getShape(ShapedTrainRecipes recipe) {
		CachedShapedRecipe shape = new CachedShapedRecipe(0, 0, null, recipe.getRecipeOutput());
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (recipe.recipeItems[y * 3 + x] == null) {
					continue;
				}
				PositionedStack stack = new PositionedStack(recipe.recipeItems[y * 3 + x], 25 + x * 18, 6 + y * 18);
				stack.setMaxSize(1);
				shape.ingredients.add(stack);
			}
		}
		shape.result.relx = 119;
		shape.result.rely = 25;
		return shape;
	}

	public class CachedShapedRecipe extends CachedRecipe {
		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;

		public CachedShapedRecipe(int width, int height, Object[] items, ItemStack out) {
			result = new PositionedStack(out, 119, 24);
			ingredients = new ArrayList<PositionedStack>();
			setIngredients(width, height, items);
		}

		public CachedShapedRecipe(ShapedRecipes recipe) {
			this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, recipe.getRecipeOutput());
		}

		/**
		 * @param width
		 * @param height
		 * @param items  an ItemStack[] or ItemStack[][]
		 */
		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (items[y * width + x] == null)
						continue;

					PositionedStack stack = new PositionedStack(items[y * width + x], 25 + x * 18, 6 + y * 18, false);
					stack.setMaxSize(1);
					ingredients.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		public PositionedStack getResult() {
			return result;
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();

			result.generatePermutations();
		}

		/**
		 * This will perform default cycling of ingredients, mulitItem capable
		 * 
		 * @return
		 */
		private int cycleTicks = 0;

		@Override
		public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients) {
			cycleTicks++;

			CachedRecipe recipe = !arecipes.isEmpty() ? arecipes.get(0) : null;

			if (recipe != null) {

				int id = getOutputID(recipe.getResult().item.getItem());
				List<ShapedTrainRecipes> recipes = recipeListWB.get(id);

				for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++) {

					Set<ItemStack> stacks = new HashSet<>();
					for(ShapedTrainRecipes otherRecipe:recipes) {
						PositionedStack incredient = getShape(otherRecipe).ingredients.get(itemIndex);
						if(incredient != null) {
							stacks.add(incredient.item);
						}
					}
					
					ArrayList<ItemStack> list = new ArrayList<>(stacks);
					if (list != null && list.size() > 1) {
						Random rand = new Random(cycle + System.currentTimeMillis());
						if (cycleTicks % 15 == 0) {
							int stackSize = ingredients.get(itemIndex).item.stackSize;
							ingredients.get(itemIndex).item = (ItemStack) list
									.get(Math.abs(rand.nextInt()) % list.size());
							ingredients.get(itemIndex).item.stackSize = stackSize;
						}
					} else {
						randomRenderPermutation(ingredients.get(itemIndex), cycle + itemIndex);
					}
				}
			}

			return ingredients;
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		if (result != null) {
			int id = getOutputID(result.getItem());
			List<ShapedTrainRecipes> results = recipeListWB.get(id);
			if (results != null) {
				for (ShapedTrainRecipes recipe : results) {
					this.arecipes.add(getShape(recipe));
					break;
				}
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiTrainCraftingBlock.class;
	}

	@Override
	public String getRecipeName() {
		return "Train Workbench";
	}

	@Override
	public String getGuiTexture() {
		return "tc:textures/gui/crafting_table.png";
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
		return false;
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "train workbench"));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (List<ShapedTrainRecipes> recipes : recipeListWB.values()) {
			for (ShapedTrainRecipes recipe : recipes) {
				for (ItemStack source : recipe.recipeItems) {
					if (NEIClientUtils.areStacksSameTypeCrafting(source, ingredient)) {
						this.arecipes.add(getShape(recipe));
						break;
					}
				}
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals("train workbench") && getClass() == NEITraincraftWorkbenchRecipePlugin.class) {
			for (List<ShapedTrainRecipes> recipe : recipeListWB.values()) {
				this.arecipes.add(getShape(recipe.get(0)));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	public static Map<Integer, List<ShapedTrainRecipes>> workbenchListCleaner(List recipeList) {
		Map<Integer, List<ShapedTrainRecipes>> sortedRecipes = new HashMap<>();

		for (int i = 0; i < recipeList.size(); i++) {
			if (recipeList.get(i) instanceof ShapedTrainRecipes) {
				ShapedTrainRecipes shapedRecipe = (ShapedTrainRecipes) recipeList.get(i);

				Item output = shapedRecipe.getRecipeOutput().getItem();
				int id = getOutputID(output);

				List<ShapedTrainRecipes> recipes = sortedRecipes.get(id);
				if (recipes == null) {
					recipes = new ArrayList<ShapedTrainRecipes>();
					sortedRecipes.put(id, recipes);
				}
				recipes.add(shapedRecipe);
			}
		}
		return sortedRecipes;
	}

	private static int getOutputID(Item pOutputItem) {
		return Item.getIdFromItem(pOutputItem);
	}

}