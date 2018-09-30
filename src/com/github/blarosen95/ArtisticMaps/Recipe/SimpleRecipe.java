package com.github.blarosen95.ArtisticMaps.Recipe;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SimpleRecipe {

    public abstract Recipe toBukkitRecipe(ItemStack result);

    public abstract ItemStack[] getPreview();

    private final String name;

    SimpleRecipe(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }

    public static class Shaped extends SimpleRecipe {

        public Shaped(String name) {
            super(name);
        }

        private final HashMap<Character, Ingredient> items = new HashMap<>();
        private String[] shape;

        public void shape(String... rows) {
            if (rows.length != 3) throw new RecipeException("A recipe shape must have exactly 3 rows");
            for (String row : rows) if (row.length() != 3) throw new RecipeException("Recipe row length must be 3");
            shape = rows;
        }

        Shaped set(char key, Material material, int durability) {
            items.put(key, new Ingredient.WrappedMaterial(material, 1));
            return this;
        }

        public Shaped set(char key, Material material) {
            return set(key, material, -1);
        }

        public void set(char key, Ingredient ingredient) {
            items.put(key, ingredient);
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            NamespacedKey key = new NamespacedKey(ArtisticMaps.instance(), this.getName());
            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(shape);
            for (Character c : items.keySet()) {
                Ingredient item = items.get(c);
                recipe.setIngredient(c, item.getMaterial());
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = new ItemStack[9];
            int i = 0;
            for (String s : shape) {
                for (char c : s.toCharArray()) {
                    if (items.containsKey(c)) preview[i] = items.get(c).toItemStack();
                    i++;
                }
            }
            return preview;
        }
    }

    public static class Shapeless extends SimpleRecipe {

        public Shapeless(String name) {
            super(name);
        }

        private final ArrayList<Ingredient> items = new ArrayList<>();

        Shapeless add(Material material, int durability, int amount) {
            items.add(new Ingredient.WrappedMaterial(material, amount));
            return this;
        }

        public Shapeless add(Material material, int durability) {
            return add(material, durability, 1);
        }

        public Shapeless add(Material material) {
            return add(material, -1, 1);
        }

        public Shapeless add(Ingredient ingredient) {
            items.add(ingredient);
            return this;
        }

        @Override
        public Recipe toBukkitRecipe(ItemStack result) {
            NamespacedKey key = new NamespacedKey(ArtisticMaps.instance(), this.getName());
            ShapelessRecipe recipe = new ShapelessRecipe(key, result);
            for (Ingredient item : items) {
                recipe.addIngredient(item.getAmount(), item.getMaterial());
            }
            return recipe;
        }

        @Override
        public ItemStack[] getPreview() {
            ItemStack[] preview = new ItemStack[9];
            for (int i = 0; i < 9 && i < items.size(); i++) {
                Ingredient item = items.get(i);
                preview[i] = item.toItemStack();
            }
            return preview;
        }
    }

    private class RecipeException extends RuntimeException {
        public static final long serialVersionUID = 1L;

        private RecipeException(String message) {
            super(message);
        }
    }
}