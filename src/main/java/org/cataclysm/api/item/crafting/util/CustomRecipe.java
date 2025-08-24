package org.cataclysm.api.item.crafting.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.cataclysm.Cataclysm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomRecipe {
    protected ShapedRecipe recipe;
    protected boolean haveAmount;
    protected @Getter String id;
    public static HashMap<ItemStack, HashMap<ItemStack, List<Integer>>> ingredientsWithAmount = new HashMap<>();
    public static List<ItemStack> ingredientListWithAmount = new ArrayList<>();

    //Temp hashmap
    protected HashMap<ItemStack, List<Integer>> materialsForIngredient;

    public CustomRecipe(String id, ItemStack item, boolean useMoreAmount) {
        this.haveAmount = useMoreAmount;

        // Si se usa mas cantidad, se crea un hashmap para guardar los materiales
        // y se crea una lista para explorar los items mas rapido.

        if(this.haveAmount){
            this.materialsForIngredient = new HashMap<>();
            ingredientListWithAmount.add(item);
        }

        this.id = id;
        this.recipe = new ShapedRecipe(new NamespacedKey(Cataclysm.getInstance(), id), item);
    }

    public CustomRecipe(String id, ItemStack item, int amount, boolean useMoreAmount){
        this.haveAmount = useMoreAmount;

        // Si se usa mas cantidad, se crea un hashmap para guardar los materiales
        // y se crea una lista para explorar los items mas rapido.

        if(this.haveAmount){
            this.materialsForIngredient = new HashMap<>();
            ingredientListWithAmount.add(item);
        }

        if(amount > 64) amount = 64; // No se puede poner mas de 64 items en un slot
        item.setAmount(amount);
        this.id = id;
        this.recipe = new ShapedRecipe(new NamespacedKey(Cataclysm.getInstance(), id), item);
    }

    public CustomRecipe(String id, Material material, int amount, boolean useMoreAmount){
        this.haveAmount = useMoreAmount;

        // Si se usa mas cantidad, se crea un hashmap para guardar los materiales
        // y se crea una lista para explorar los items mas rapido.

        if(this.haveAmount){
            this.materialsForIngredient = new HashMap<>();
            ingredientListWithAmount.add(new ItemStack(material, amount));
        }

        if(amount > 64) amount = 64; // No se puede poner mas de 64 items en un slot
        ItemStack item = new ItemStack(material, amount);
        this.id = id;
        this.recipe = new ShapedRecipe(new NamespacedKey(Cataclysm.getInstance(), id), item);
    }

    private static String check(String shape){
        // Esto es para que no haya problemas con los espacios en blanco
        // en la forma de la receta, ya que si no se pone esto, el juego
        // no reconoce la receta.
        switch(shape.length()){
            case 0 -> shape = "   ";
            case 1 -> shape = shape + "  ";
            case 2 -> shape = shape + " ";
            default -> {
                return shape;
            }
        }
        return shape;
    }

    public CustomRecipe setShape(String top, String mid, String bot) {
        this.recipe.shape(check(top), check(mid), check(bot));
        return this;
    }

    public CustomRecipe setIngredient(char key, ItemStack item) {
        if(this.haveAmount){
            this.setIngredient(key, item, 1);
            return this;
        }

        this.recipe.setIngredient(key, item);
        return this;
    }

    public CustomRecipe setIngredient(char key, ItemStack item, int amount) {
        if(!this.haveAmount){
            this.setIngredient(key, item);
            return this;
        }

        item.setAmount(amount);
        this.recipe.setIngredient(key, item);
        //get key in the shape
        // Obtener el slot en el que se encuentra el ingrediente
        List<Integer> cacheSlots = new ArrayList<>();
        for (int i = 0; i < this.recipe.getShape().length; i++) {
            String shape = this.recipe.getShape()[i];
            for (int j = 0; j < shape.length(); j++) {
                if (shape.charAt(j) == key) {
                    cacheSlots.add(i * 3 + j);
                }
            }
        }
        materialsForIngredient.put(item, cacheSlots);
        return this;
    }

    public CustomRecipe setIngredient(char key, Material item, int amount) {
        if (!this.haveAmount){
            ItemStack itemStack = new ItemStack(item, 1);
            this.recipe.setIngredient(key, itemStack);
            return this;
        }

        ItemStack itemStack = new ItemStack(item, amount);
        itemStack.setAmount(amount);
        this.recipe.setIngredient(key, itemStack);
        //get key in the shape
        // Obtener el slot en el que se encuentra el ingrediente
        List<Integer> cacheSlots = new ArrayList<>();
        for (int i = 0; i < this.recipe.getShape().length; i++) {
            String shape = this.recipe.getShape()[i];
            for (int j = 0; j < shape.length(); j++) {
                if (shape.charAt(j) == key) {
                    cacheSlots.add(i * 3 + j);
                }
            }
        }
        materialsForIngredient.put(itemStack, cacheSlots);
        return this;
    }

    public CustomRecipe setIngredient(char key, Material material) {
        return setIngredient(key, material, 1);
    }

    public void register() {
        // Add recipe to server
        Cataclysm.getInstance().getServer().addRecipe(this.recipe);
        // Add recipe to hashmap
        if(this.haveAmount){
            ingredientsWithAmount.put(this.recipe.getResult(), this.materialsForIngredient);
        }
    }

}
